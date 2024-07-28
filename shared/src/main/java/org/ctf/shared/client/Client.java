package org.ctf.shared.client;

import com.google.gson.Gson;
import java.time.Clock;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.ctf.shared.ai.AIController;
import org.ctf.shared.ai.GameStateNormalizer;
import org.ctf.shared.ai.GameUtilities;
import org.ctf.shared.client.lib.GameClientInterface;
import org.ctf.shared.client.lib.ServerChecker;
import org.ctf.shared.client.lib.ServerDetails;
import org.ctf.shared.client.service.CommLayer;
import org.ctf.shared.client.service.CommLayerInterface;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.gameanalyzer.GameSaveHandler;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Piece;
import org.ctf.shared.state.Team;
import org.ctf.shared.state.data.exceptions.ForbiddenMove;
import org.ctf.shared.state.data.exceptions.GameOver;
import org.ctf.shared.state.data.exceptions.InvalidMove;
import org.ctf.shared.state.data.exceptions.NoMoreTeamSlots;
import org.ctf.shared.state.data.exceptions.SessionNotFound;
import org.ctf.shared.state.data.exceptions.URLError;
import org.ctf.shared.state.data.exceptions.UnknownError;
import org.ctf.shared.state.data.map.MapTemplate;
import org.ctf.shared.state.dto.GameSession;
import org.ctf.shared.state.dto.GameSessionRequest;
import org.ctf.shared.state.dto.GameSessionResponse;
import org.ctf.shared.state.dto.JoinGameResponse;
import org.ctf.shared.state.dto.MoveRequest;

/**
 * Base Client file which is going to use the Translation Layer to talk to the game server Has
 * support for multiple gameSessions and flexible server polling Uses a StepBuilder Class to Create
 * Objects
 *
 * @author rsyed
 */
public class Client implements GameClientInterface {

  // Main DataStore block
  protected GameStateNormalizer normalizer;
  protected volatile GameState currentState;
  protected volatile GameState lastState;
  protected String[][] grid;
  protected int currentTeamTurn;
  protected Move lastMove;
  protected Team[] teams;
  protected Clock currentTime;
  
  protected Gson gson; // Gson object for conversions incase needed
  // Two CommLayers Available CommLayer and RestClientLayer
  protected CommLayerInterface comm; // Layer instance which is used for communication
  protected GameSaveHandler analyzer;

  // Block for Server Info
  protected String currentServer; // Creates URL with Session ID for use later
  protected String shortURL;
  protected ServerDetails serverInfo;

  // Block for session info
  protected GameSession currentSession;
  protected String currentGameSessionID;
  protected GameSessionResponse gameResponse;
  protected String[] winners;
  protected int turnTimeLimit;
  protected String couldJoin;

  // Block for Team Data
  protected String teamSecret;
  protected String requestedTeamName; // Team name we request from the Server
  protected String teamID; // TeamID we get from the server for current team recognition
  protected String teamNumber; // Is set when the server tells you what number it assigned you
  protected String teamColor;
  protected int myTeam = -1; // index of the team this client represents in teams array
  protected String gameIDtoJoin;
  protected String[] allTeamNames;

  // Block for alt game mode data
  protected Date startDate;
  protected Date endDate;
  protected volatile int moveTimeLeft;
  protected int gameTimeLeft;
  protected int lastTeamTurn;
  protected long refreshTime = 10L;

  // Block for booleans
  protected boolean gameOver;
  protected boolean gameStarted;
  protected boolean enableLogging;
  protected boolean enableQueue = false;
  protected boolean moveTimeLimitedGameTrigger = false;
  protected boolean timeLimitedGameTrigger = false;
  protected boolean isAlive;

  // Services
  ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
  Logger logger = Logger.getLogger(getClass().getName());
  // Queue for storing different game states
  ConcurrentLinkedQueue<GameState> fifoQueue = new ConcurrentLinkedQueue<>();
  Thread watcherThread;

  /**
   * Package constructor to set the IP and port on object creation. Cannot be called externally
   *
   * @param comm Sets the comm layer the client is going to use
   * @param IP the IP to connect to Exp "localhost" or "192.xxx.xxx.xxx"
   * @param port the port the server is at Exp 9999 / 8080 /
   * @param enableLogging tells the client to start logging the moves for later analysis by an AI
   * @author rsyed
   */
  Client(CommLayerInterface comm, String IP, String port, boolean enableLogging) {
    this.currentTeamTurn = -1;
    this.couldJoin = "unjoined";
    this.gson = new Gson();
    this.currentState = new GameState();
    this.currentSession = new GameSession();
    this.comm = comm;
    this.enableLogging = enableLogging;
    if (enableLogging) {
      analyzer = new GameSaveHandler();
    }
    setServer(IP, port);
    allTeamNames = null;
  }

  /**
   * Overloaded constructor used when auto join functionality is wished for. Joins a game 2 seconds
   * after object creation, starts watching for game start after 3
   *
   * @param gameID Sets the comm layer the client is going to use
   * @param teamName the IP to connect to Exp "localhost" or "192.xxx.xxx.xxx"
   * @author rsyed
   */
  Client(
      CommLayerInterface comm,
      String IP,
      String port,
      boolean enableLogging,
      String gameID,
      String teamName) {
    this(comm, IP, port, enableLogging);
    this.gameIDtoJoin = gameID;
    this.requestedTeamName = teamName;
    scheduler.schedule(joinTask, 100, TimeUnit.MILLISECONDS);
    scheduler.schedule(startWatcher, 100, TimeUnit.MILLISECONDS);
  }

  /**
   * Runnable task which joins a session. The data is read from the current object. Is scheduled
   * with the overloaded constructor by default
   *
   * @throws NoMoreTeamSlots when the server returns a no more team slots exception
   * @author rsyed
   */
  Runnable joinTask =
      () -> {
        try {
          joinExistingGame(
              serverInfo.getHost(), serverInfo.getPort(), gameIDtoJoin, requestedTeamName);
        } catch (NoMoreTeamSlots e) {
          throw new NoMoreTeamSlots();
        }
      };

  /**
   * Runnable lamba task which inits a watcher thread. The thread watches the Game Session for data
   * which indicates that a game has started.
   *
   * @author rsyed
   */
  Runnable startWatcher = Client.this::startGameController;

  // **************************************************
  // Start of CRUD Call Methods
  // **************************************************
  /**
   * Method to set the server which this this object communicates with and save info into a
   * ServerDeatils object. Its automatically called from the Constructor.
   *
   * @param IP of the server this object should point to
   * @param port of the server this object should point to
   * @author rsyed
   */
  public void setServer(String IP, String port) {
    this.currentServer = "http://" + IP + ":" + port + "/api/gamesession";
    this.shortURL = "http://" + IP + ":" + port + "/api/gamesession";
    this.serverInfo = new ServerDetails(IP, port);
  }

  /**
   * Requests the server specified in the current object to create a GameSession using the map in
   * the MapTemplate parameter. Throws exceptions on acception and incase errors occour
   *
   * @param map the map template which is sent to the server as to create a session with
   * @throws UnknownError (500)
   * @throws URLError (404)
   * @author rsyed
   */
  public void createGame(MapTemplate map) {
    gameSessionResponseParser(createGameCaller(map));
  }

  /**
   * Method joins the requested game session
   *
   * @param teamName the team name you want to join the session with. The method saves the requested
   *     name incase its needed later
   * @throws SessionNotFound
   * @throws NoMoreTeamSlots
   * @throws UnknownError
   * @author rsyed
   */
  @Override
  public void joinGame(String teamName) {
    this.requestedTeamName = teamName;
    joinGameParser(joinGameCaller(teamName));
    this.isAlive = true;
  }

  /**
   * Method makes a move in the game
   *
   * @param Move Move object which represents a move a client/player wants to make
   * @throws SessionNotFound (404)
   * @throws ForbiddenMove (403)
   * @throws InvalidMove (409)
   * @throws GameOver (410)
   * @throws UnknownError (500)
   * @author rsyed
   */
  @Override
  public void makeMove(Move move) {
    if(this.isAlive)
      makeMoverCaller(teamID, teamSecret, move);
  }

  /**
   * Requests a refresh of the GameState from the server. Parses the data and makes it available for
   * consumption. Throws exceptions listed incase of acceptance or errors. Functions as a REFRESH
   * COMMAND for GAMESTATE
   *
   * @throws SessionNotFound
   * @throws UnknownError
   * @throws URLError (404)
   * @author rsyed
   */
  @Override
  public void getStateFromServer() {
    gameStateHelper();
  }

  /**
   * Requests the server to return the current state of the session and parses it Throws exceptions
   * depending on what happens. Functions as a REFRESH command for SESSION INFO
   *
   * @throws SessionNotFound (404)
   * @throws UnknownError (500)
   * @throws URLError (404)
   * @author rsyed
   */
  @Override
  public void getSessionFromServer() {
    gameSessionResponseParser(gameSessionCaller());
  }

  /**
   * Requests the server to delete the current session. Returns the server reponse which are HTTP
   * status codes thrown as exceptions.
   *
   * @throws SessionNotFound (404)
   * @throws UnknownError (500)
   * @throws URLError (404)
   * @author rsyed
   */
  @Override
  public void deleteSession() {
    comm.deleteCurrentSession(currentServer);
  }

  /**
   * Used to send a give up request to the current session. Throws exceptions depending on errors
   *
   * @throws SessionNotFound (404)
   * @throws ForbiddenMove (403)
   * @throws GameOver (410)
   * @throws UnknownError (500)
   * @throws URLError (404)
   * @author rsyed
   */
  @Override
  public void giveUp() {
    //  logger.info(requestedTeamName + " wants to give up");
    comm.giveUp(currentServer, requestedTeamName, teamSecret);
  }

  /**
   * Joins an existing game session
   *
   * @param IP which the game server is located at
   * @param port the server port
   * @param gameSessionID the Game Session ID which you want to join
   * @param teamName The team name you wish to join the session with
   * @throws SessionNotFound
   * @throws NoMoreTeamSlots
   * @throws UnknownError
   * @author rsyed
   */
  public void joinExistingGame(String IP, String port, String gameSessionID, String teamName) {
    this.currentServer = "http://" + IP + ":" + port + "/api/gamesession";
    this.currentServer = shortURL + "/" + gameSessionID;
    try {
      joinGame(teamName);
      this.couldJoin = "joined";
      getStateFromServer();
    } catch (Exception e) {
      this.couldJoin = "declined";
    }
  }

  // **************************************************
  // End of CRUD Call Methods
  // **************************************************

  // **************************************************
  // Start of CRUD Call Parsers
  // **************************************************

  /**
   * Called from createGame function. Makes a {@link GameSessionRequest} and sends it over the
   * {@link Comm} layer to the server. Recieves the response {@link GameSessionResponse} and returns
   * it
   *
   * @param {@link MapTemplate} used for the Request
   * @return {@link GameSessionResponse} with returned data from the server
   * @author rsyed
   */
  protected GameSessionResponse createGameCaller(MapTemplate map) {
    gameResponse = new GameSessionResponse();
    GameSessionRequest gsr = new GameSessionRequest();
    gsr.setTemplate(map);
    try {
      this.gameResponse = comm.createGameSession(currentServer, gsr);
    } catch (URLError e) {
      throw new URLError("Something wrong with the server. Try to fix using setServer");
    } catch (UnknownError e) {
      throw new UnknownError();
    }
    return gameResponse;
  }

  /**
   * Called from createGameCaller function. Recieves the response {@link GameSessionResponse} as a
   * param and converts it into a {@link GameSession} Object as well as parse the data into
   * individual variables for easier consumption by the UI. Method is protected and synchronized so
   * that the data being extracted doesnt run into any mutex issues.
   *
   * @param {@link GameSessionResponse} with feteched Data from server
   * @author rsyed
   */
  protected synchronized void gameSessionResponseParser(GameSessionResponse gameSessionResponse) {
    this.currentSession = gson.fromJson(gson.toJson(gameSessionResponse), GameSession.class);
    this.currentGameSessionID = gameSessionResponse.getId();
    this.currentServer = shortURL + "/" + currentGameSessionID;
    try {
      this.startDate = gameSessionResponse.getGameStarted();
      this.gameStarted = true;
    } catch (NullPointerException e) {
      logger.info("Game hasnt started yet");
    }
    try {
      this.endDate = gameSessionResponse.getGameEnded();
    } catch (NullPointerException e) {
      logger.info("Game hasnt ended yet");
    }
    try {
      this.gameOver = gameSessionResponse.isGameOver();
    } catch (NullPointerException e) {
      logger.info("Game hasnt ended yet");
    }
    try {
      this.winners = gameSessionResponse.getWinner();
    } catch (NullPointerException e) {
      logger.info("There are no winners");
    }
    try {
      this.currentGameSessionID = gameSessionResponse.getId();
    } catch (NullPointerException e) {
      logger.info("No Game ID is set yet");
    }
    try {
      this.turnTimeLimit = gameSessionResponse.getRemainingGameTimeInSeconds();
      this.gameTimeLeft = gameSessionResponse.getRemainingGameTimeInSeconds();
      if (turnTimeLimit > 0) {
        this.timeLimitedGameTrigger = true;
      }
    } catch (NullPointerException e) {
      logger.info("There is no Turn Time Limit");
    }
    try {
      this.moveTimeLeft = gameSessionResponse.getRemainingMoveTimeInSeconds();
      if (moveTimeLeft > 0) {
        /*if(this instanceof AIClient) {
          tdt = new TimeDecreaseThread();
          tdt.start();
        }*/
        this.moveTimeLimitedGameTrigger = true;
      }
    } catch (NullPointerException e) {
      logger.info("There is no Turn Time Limit");
    }
  }

//  TimeDecreaseThread tdt;

  /**
   * Decreases the moveTimeLeft, in case the updated Server time cannot be used.
   * Should be used by AIClients, they stop updating from Server if they are calculating.
   * TODO
   * @author sistumpf
   */
  /*private class TimeDecreaseThread extends Thread {
    private boolean running = true;

    @Override
    public void run() {  
      if(tdt != null)
        tdt.interrupt();
      if(myTeam == comm.getCurrentGameState(currentServer).getCurrentTeam())
        tdt = this;
      while(moveTimeLeft > 0 && running) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        --moveTimeLeft;
      }
      tdt = null;
    }

    @Override
    public void interrupt() {
      running = false;
    }
  }*/

  
  /**
   * Called from the joinGame function. Recieves the teamName wished for by the team as a param and
   * returns the response from the server as a {@link JoinGameResponse}.
   *
   * @param teamName with feteched Data from server
   * @return {@link JoinGameResponse} with returned data from the server
   * @throws SessionNotFound
   * @throws NoMoreTeamSlots
   * @throws UnknownError
   * @author rsyed
   */
  protected JoinGameResponse joinGameCaller(String teamName) {
    JoinGameResponse response = new JoinGameResponse();
    try {
      response = comm.joinGame(currentServer, teamName);
    } catch (SessionNotFound e) {
      throw new SessionNotFound("SessionID is wrong / Server is not there");
    } catch (NoMoreTeamSlots e) {
      throw new NoMoreTeamSlots("Slots are full!");
    } catch (UnknownError e) {
      throw new UnknownError("Something wrong with the server/URL");
    }
    return response;
  }

  /**
   * Called from the joinGame function. Recieves the {@link JoinGameResponse} as a param and parses
   * the data from the object into individual variables for easier consumption by the UI
   *
   * @param {@link GameSessionResponse} with feteched Data from server
   * @throws UnknownError with message indicating that no team color is set by the server
   * @author rsyed
   */
  protected void joinGameParser(JoinGameResponse joinGameResponse) {
    this.teamID = joinGameResponse.getTeamId();
    this.teamSecret = joinGameResponse.getTeamSecret();
    try {
      this.teamColor = joinGameResponse.getTeamColor();
    } catch (NullPointerException e) {
      logger.info("No Team color has been set");
      throw new UnknownError("No Team color has been set by the server");
    }
  }

  /**
   * Called from the makeMove function. Recieves the teamID, teamSecret and Move to create a {@link
   * MoveRequest} and send it over to the server through {@link CommLayer}
   *
   * @param teamID Team Name for the request. Read from the Client
   * @param teamSecret Team Secret for the Request. Read from the Client
   * @param move Move requested. Needs to be given by the UI
   * @throws SessionNotFound
   * @throws ForbiddenMove
   * @throws InvalidMove
   * @throws GameOver
   * @throws UnknownError
   * @author rsyed
   */
  protected void makeMoverCaller(String teamID, String teamSecret, Move move) {
    move = normalizer.unnormalizeMove(move);
    move.setTeamId(teamID);
    if (move != null) {
      try {
        MoveRequest moveReq = new MoveRequest();
        moveReq.setTeamId(teamID);
        moveReq.setTeamSecret(teamSecret);
        moveReq.setPieceId(move.getPieceId());
        moveReq.setNewPosition(move.getNewPosition());
        comm.makeMove(currentServer, moveReq);
//        System.out.println("dop");
      } catch (SessionNotFound e) {
        System.out.println("SessionNotFound");
        throw new SessionNotFound();
      } catch (ForbiddenMove e) {
        System.out.println("ForbiddenMove");
        throw new ForbiddenMove();
      } catch (InvalidMove e) {
        System.out.println(move.getPieceId() + ", team: " + move.getTeamId() + " [" +
            move.getNewPosition()[0] + "-" + move.getNewPosition()[1] + "], I am " + this.requestedTeamName);
        final String pieceId = move.getPieceId();
        System.out.println("InvalidMove");
        throw new InvalidMove();
      } catch (GameOver e) {
        System.out.println("GameOver");
        throw new GameOver();
      } catch (UnknownError e) {
        System.out.println("UnknownError");
        throw new UnknownError();
      }
    }
  }

  /**
   * Called from the getGameState method. Requests the server specific/set in the Client object to
   * send the current {@link GameState}. Also parses the response and saves data to local variables
   * for easier consumption by the UI
   *
   * @param teamID Team Name for the request. Read from the Client
   * @param teamSecret Team Secret for the Request. Read from the Client
   * @param move Move requested. Needs to be given by the UI
   * @throws SessionNotFound
   * @throws UnknownError
   * @author rsyed
   */
  protected synchronized void gameStateHelper() {
    GameState gameState = new GameState();
    try {
      gameState = comm.getCurrentGameState(currentServer);
      initMyTeam(gameState);
      if (gameState.getTeams()[myTeam] == null) this.isAlive = false;
    } catch (SessionNotFound e) {
      throw new SessionNotFound("Session isnt available for this request");
    } catch (UnknownError e) {
      throw new UnknownError("Server Error or Setting error");
    }
    if (isNewGameState(gameState)) {
      gameState = normalizeGameState(gameState);
      this.currentTeamTurn = gameState.getCurrentTeam();
      this.lastState = currentState;
      this.currentState = gameState;
      this.teams = gameState.getTeams();
      this.lastMove = gameState.getLastMove();
      updateLastTeam();
      this.grid = gameState.getGrid();

      if (enableQueue) {
        this.fifoQueue.offer(gameState);
      }

      if (enableLogging) {
        if(!isMoveEmpty(gameState.getLastMove())) {
          /*System.out.println(" " + gameState.getLastMove().getPieceId() 
              + " : " + gameState.getLastMove().getNewPosition()[0] + "," 
              + gameState.getLastMove().getNewPosition()[1] + " :: " 
              + GameUtilities.howManyTeams(gameState));*/
        
          this.analyzer.addMove(gameState.getLastMove(), GameUtilities.teamsGaveUp(lastState, currentState));
        }
      }
    }
  }

  /**
   * Checks if a Move is a valid Move or just some kind of empty uninitialized move.
   * Should be used before adding a Move to the analyzer.
   * 
   * @author sistumpf
   * @param move the Move to check
   * @return true if the Move is an initial Move and should not be treated as a Move
   */
  protected boolean isMoveEmpty(Move move){
    if(move == null)
      return true;
    else if(move.getNewPosition() == null)
      return true;
    else if(move.getPieceId() == null)
      return true;
    else if(move.getTeamId() == null)
      return true;
    else if(move.getPieceId().equals(""))
      return true;
    else if(move.getTeamId().equals(""))
      return true;
    return false;
  }
  
  /**
   * Called from the getGameSession method. Requests the server specific/set in the Client object to
   * send the current {@link GameSessionResponse}.
   *
   * @throws SessionNotFound
   * @throws UnknownError
   * @author rsyed
   */
  protected GameSessionResponse gameSessionCaller() {
    GameSessionResponse gameSessionResponse = new GameSessionResponse();
    try {
      gameSessionResponse = comm.getCurrentSessionState(currentServer);
    } catch (SessionNotFound e) {
      throw new SessionNotFound("Session isnt available for this request");
    } catch (UnknownError e) {
      throw new UnknownError("Server Error or Setting error");
    }
    return gameSessionResponse;
  }

  // **************************************************
  // End of CRUD Call Parsers
  // **************************************************

  // **************************************************
  // Start of Client Functional Methods
  // **************************************************

  /**
   * Checks if the {@link GameState}s currentTeam matches this.{@link myTeam}
   *
   * @return true if its your turn, false if its not
   * @author rsyed, sistumpf
   */
  public boolean isItMyTurn() {
    if (this.myTeam >= 0) return this.myTeam == this.currentState.getCurrentTeam();
    return false;
  }

  /**
   * Initializes {@link allTeamNames} if not done yet, then normalizes the team IDs. The Server
   * replaces the teamIDs, so here they get reset to ints 0-n, representing their placement in the
   * array. If the team name matches this clients name {@link myTeam} is set to the corresponding
   * ID.
   *
   * @param gameState the gameState to get normalized
   * @author rsyed, sistumpf
   */
  protected synchronized GameState normalizeGameState(GameState gameState) {
    if (allTeamNames == null && gameState.getCurrentTeam() >= 0) 
      initAllTeamNames(gameState);      
    if(this.normalizer == null)
      this.normalizer = new GameStateNormalizer(gameState, true, true);
    else
      normalizer.update(gameState);
    
    return normalizer.getNormalizedGameState();
  }

  /**
   * Initializes {@link allTeamNames} so the ui can easily access them.
   *
   * @author sistumpf
   * @param gameState the gameState from which the teamNames get pulled
   */
  private synchronized void initAllTeamNames(GameState gameState) {
    allTeamNames = new String[gameState.getTeams().length];

    for (int teamID = 0; teamID < gameState.getTeams().length; teamID++) {
      String name = gameState.getTeams()[teamID].getId();
      allTeamNames[teamID] = name;
    }
  }
  
  /**
   * Looks for the clients team index in the gameState and saves it.
   * 
   * @param gameState the GameState to look for the team
   */
  protected synchronized void initMyTeam(GameState gameState) {
    for (int teamID = 0; teamID < gameState.getTeams().length; teamID++) {
      if(gameState.getTeams()[teamID] == null) continue;
      String name = gameState.getTeams()[teamID].getId();
      if (this.requestedTeamName.equals(name)) {
        this.myTeam = teamID;
        break;
      }
    }
  }

  /**
   * Checks if server is active through a dummy gameTemplate. Calling this too often might lead to
   * server overload if badly progarammed
   *
   * @return true if server is active and ready to make sessions, false if not
   * @author rsyed
   */
  public boolean isServerActive() {
    return new ServerChecker().isServerActive(serverInfo);
  }

  /**
   * Method which can be called anywhere to pull both DTO objects from the server. Preferred way to
   * pull data from the Server
   *
   * @author rsyed
   */
  public void pullData() {
    getSessionFromServer();
    getStateFromServer();
  }

  /**
   * Helper method which sets an int to keep track of which teams turn it was before (Derived from
   * Last Move) Sets the lastTeamTurn int -1 if no last move, 0 to n Otherwise
   *
   * @author rsyed
   */
  protected void updateLastTeam() {
    this.lastTeamTurn =
        (lastMove != null)
            ? Integer.parseInt(lastMove.getPieceId().split(":")[1].split("_")[0])
            : -1;
  }

  /**
   * Compares the current and new GameStates last moves.
   * If they are equal, the current team gets compared, if they are equal the GameState is not new.
   * This must happen that way, as a team can giveUp, the last Move stays the same, but the current Team changes.
   *
   * @author sistumpf
   * @return true if newState is a new GameState
   * @return false if the game has not started yet or newState is not new
   */
  protected boolean isNewGameState(GameState newState) {
    if(newState.getCurrentTeam() == -1 && this.currentTeamTurn == -1)
      return false;
    if(this.normalizer == null) {
      return true;
    }
    
    if(GameUtilities.moveEquals(newState.getLastMove(), normalizer.unnormalizeMove(currentState.getLastMove()))) {
      return newState.getCurrentTeam() != currentState.getCurrentTeam();
    }
    
    return true;
  }
  
  /**
   * Method which returns which team made the last move -1 if no last move. 0 to n otherwise
   *
   * @author rsyed
   */
  public int getLastTeamTurn() {
    return lastTeamTurn;
  }

  /**
   * Method which returns how many teams have joined the session at present
   *
   * @author rsyed
   */
  public int getCurrentNumberofTeams() {
    getStateFromServer();
    int counter = 0;
    for (int i = 0; i < getCurrentState().getTeams().length; i++) {
      if (getCurrentState().getTeams()[i] != null) {
        counter++;
      }
    }
    return counter;
  }
  
  /**
   * Kills all Client processes, shutting it down.
   * 
   * @author sistumpf
   */
  public void shutdown() {
    try {
      this.watcherThread.interrupt();
    } catch(Exception e) {
//      e.printStackTrace();
    }
    this.scheduler.shutdown();
    this.gameOver = true;
    this.isAlive = false;
  }

  // **************************************************
  // End of Client Funtional Methods
  // **************************************************

  // **************************************************
  // Start of Alt Game Data Getters
  // **************************************************
  /**
   * Getter which returns how much time is left in the move
   *
   * @return int denoting the time left in seconds
   * @author rsyed
   */
  public int getRemainingMoveTimeInSeconds() {
    return this.moveTimeLeft;
  }

  /**
   * Getter which returns how much time is left in the game
   *
   * @return int denoting the time left in seconds
   * @author rsyed
   */
  public int getRemainingGameTimeInSeconds() {
    return this.turnTimeLimit;
  }

  // **************************************************
  // End of Alt Game Data Getters
  // **************************************************

  /**
   * Main CONTROLLER for Client. Call when either a game is created or a game is joined Starts the
   * automatic refreshing of data
   *
   * @author rsyed
   */
  protected void startGameController() {
    gameStartWatcher();
  }

  /**
   * Starts a scheduled Thread just to update the Move time.
   * Only does this if the Client is an instance of AIClient,
   * as the AIClient does not update if it is currently calculating its move.
   * 
   * @author sistumpf
   */
  protected void startMoveTimeThread() {
    if(this instanceof AIClient && !isGameOver()) {
      scheduler.schedule(new Runnable() {
        @Override
        public void run() {
          moveTimeLeft = comm.getCurrentSessionState(currentServer).getRemainingMoveTimeInSeconds();
          startMoveTimeThread();
        }
      }
      , Constants.UIupdateTime, TimeUnit.MILLISECONDS);
    }
  }
  
  /**
   * A Watcher thread which calls GameSessionResponse periodically and hands over functionality to
   * GameStartedThread when it detects that the same has started. Terminates itself right after to
   * free resources.
   *
   * @author rsyed, sistumpf
   */
  protected void gameStartWatcher() {
    watcherThread = new Thread() {
      boolean active = true;
      
      public void interrupt() {
        this.active = false;
      }
      
      public void run() {
        while (active) {
          try {
            Long sleep = 100L;
            Client.this.getSessionFromServer(); // Gets Session from server
            if (getStartDate() != null) {
              if (enableLogging) {
                getStateFromServer();
                analyzer.addGameState(getCurrentState());
              }
              gameStartedThread();
              active = false;
            }
            Thread.sleep(sleep);
          } catch (InterruptedException e) {
            throw new Error("Something went wrong in the Client Thread");
          } catch(SessionNotFound e) {
            active = false;
            Client.this.shutdown();
          }
        }
      }};
      watcherThread.start();
  }

  /**
   * Main thread which handles client logic for when game has started. Just pulls data periodically
   * set by the refesh time var. Also handles the logger incase logging is wished for by the
   * player/ui
   *
   * @author rsyed
   */
  protected void gameStartedThread() {
    Thread gameThread =
        new Thread(
            () -> {
              while (!isGameOver()) {
                try {
                  pullData();
                  if (enableLogging) {
                    if(!isMoveEmpty(getCurrentState().getLastMove()))
                      analyzer.addMove(getCurrentState().getLastMove(), GameUtilities.teamsGaveUp(lastState, currentState));
                  }
                  if (isGameOver()) {
                    scheduler.shutdown();
                  }
                  Thread.sleep(this.refreshTime);
                } catch (InterruptedException e) {
                  throw new Error("Something went wrong in the Client Thread");
                }
              }

              analyzer.writeOut();
            });
    gameThread.start();
  }

  /**
   * Setter to change the refreshing period dynamically if desired
   *
   * @author rsyed
   */
  public void setRefreshTime(long refreshtime) {
    this.refreshTime = refreshtime;
  }

  // **************************************************
  // End of Alt Game Logic
  // **************************************************

  // **************************************************
  // Getter Block
  // **************************************************

  public GameSessionResponse getLastGameSessionResponse() {
    return this.gameResponse;
  }

  public GameState getCurrentState() {
    return currentState;
  }

  public String getCurrentServer() {
    return currentServer;
  }

  public String[][] getGrid() {
    return grid;
  }

  public int getCurrentTeamTurn() {
    return currentTeamTurn;
  }

  public Move getLastMove() {
    return lastMove;
  }

  public Team[] getTeams() {
    return teams;
  }

  public GameSession getCurrentSession() {
    return currentSession;
  }

  public String getCurrentGameSessionID() {
    return currentGameSessionID;
  }

  public String[] getWinners() {
    getSessionFromServer();
    return winners;
  }

  public String getTeamSecret() {
    return teamSecret;
  }

  public String getTeamID() {
    return teamID;
  }

  public String getTeamColor() {
    return teamColor;
  }

  public Date getStartDate() {
    return startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  /**
   * Getter which returns true if and only if the game is over on serverside
   *
   * @author rsyed
   */
  public boolean isGameOver() {
    try {
      return gameOver;
    } catch (Exception e) {
      return false;
    }
  }

  public ServerDetails getServerDetails() {
    return this.serverInfo;
  }

  /**
   * Getter which returns if the game has started. Aka a Start Date is set
   *
   * @author rsyed
   */
  public boolean isGameStarted() {
    return startDate != null;
  }

  public String[] getAllTeamNames() {
    return allTeamNames;
  }

  public ScheduledExecutorService getScheduler() {
    return this.scheduler;
  }

  public boolean isGameTimeLimited() {
    return this.timeLimitedGameTrigger;
  }

  public boolean isGameMoveTimeLimited() {
    return this.moveTimeLimitedGameTrigger;
  }

  /**
   * Method to interact with the queue storing different game states for the UI. Queue is thread
   * safe.
   *
   * @author rsyed
   * @return The game state in the FIFO queue
   */
  public GameState getQueuedGameState() {
    return this.fifoQueue.poll();
  }
  
  /**
   * Returns the number of GameStates in the queue.
   * 
   * @author sistumpf
   * @return number of GameStates in the queue.
   */
  public int queuedGameStates() {
    return this.fifoQueue.size();
  }

  /**
   * Enables the queue generation logic
   *
   * @param selector true to enable the game state queue for the UI, ,false to disable
   * @author rsyed
   * @return The game state in the FIFO queue
   */
  public void enableGameStateQueue(boolean selector) {
    this.enableQueue = selector;
  }

  public GameSaveHandler getGameSaveHandler() {
    return this.analyzer;
  }
  // **************************************************
  // End of Getter Block
  // **************************************************

  public boolean isAlive() {
    return isAlive;
  }

  public String couldJoin() {
    return couldJoin;
  }

}
