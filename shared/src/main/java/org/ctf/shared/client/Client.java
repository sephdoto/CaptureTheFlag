package org.ctf.shared.client;

import com.google.gson.Gson;
import java.time.Clock;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.ctf.shared.client.lib.GameClientInterface;
import org.ctf.shared.client.lib.ServerChecker;
import org.ctf.shared.client.lib.ServerDetails;
import org.ctf.shared.client.service.CommLayer;
import org.ctf.shared.client.service.CommLayerInterface;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Team;
import org.ctf.shared.state.data.exceptions.Accepted;
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
  public volatile GameState currentState;
  public String[][] grid;
  public int currentTeamTurn;
  public Move lastMove;
  public Team[] teams;
  public Clock currentTime;

  public Gson gson; // Gson object for conversions incase needed
  // Two CommLayers Available CommLayer and RestClientLayer
  public CommLayerInterface comm; // Layer instance which is used for communication

  // Block for Server Info
  public String currentServer; // Creates URL with Session ID for use later
  public String shortURL;
  public ServerDetails serverInfo;

  // Block for session info
  public GameSession currentSession;
  public String currentGameSessionID;
  public GameSessionResponse gameResponse;
  public String[] winners;
  public int turnTimeLimit;

  // Block for Team Data
  public String teamSecret;
  public String requestedTeamName; // Team name we request from the Server
  public String teamID; // TeamID we get from the server for current team recognition
  public String teamNumber; // Is set when the server tells you what number it assigned you
  public String teamColor;

  // Block for alt game mode data
  public Date startDate;
  public Date endDate;
  public int moveTimeLeft;
  public int gameTimeLeft;
  public int lastTeamTurn;
  public long refreshTime = 300L;

  // Block for booleans
  public boolean gameOver;
  public boolean gameStarted;
  protected boolean turnSupportFlag; // is enabled when the team ID recieved in response is an INT
  public boolean moveTimeLimitedGameTrigger = false;
  public boolean timeLimitedGameTrigger = false;

  // Services
  ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
  ExecutorService executor = Executors.newFixedThreadPool(3);

  /**
   * Constructor to set the IP and port on object creation
   *
   * @param comm Sets the comm layer the client is going to use
   * @param IP the IP to connect to Exp "localhost" or "192.xxx.xxx.xxx"
   * @param port the port the server is at Exp 9999 / 8080 /
   * @param enableLogging tells the client to start logging the moves for later analysis by an AI
   * @author rsyed
   */
  Client(CommLayerInterface comm, String IP, String port, Boolean enableLogging) {
    this.gson = new Gson();
    this.currentState = new GameState();
    this.currentSession = new GameSession();
    this.comm = comm;
    setServer(IP, port);
  }

  // **************************************************
  // Start of CRUD Call Methods
  // **************************************************
  /**
   * Method to set the server which this this object communicates with and save info into a
   * ServerDeatils object. Its automatically called from the Constructor.
   *
   * @param IP
   * @param port
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
   * @param map
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
   * @param teamName
   * @throws SessionNotFound
   * @throws NoMoreTeamSlots
   * @throws UnknownError
   * @author rsyed
   */
  @Override
  public void joinGame(String teamName) {
    this.requestedTeamName = teamName;
    joinGameParser(joinGameCaller(teamName));
  }

  /**
   * Method makes a move in the game
   *
   * @param Move
   * @throws SessionNotFound (404)
   * @throws ForbiddenMove (403)
   * @throws InvalidMove (409)
   * @throws GameOver (410)
   * @throws UnknownError (500)
   * @author rsyed
   */
  @Override
  public void makeMove(Move move) {
    makeMoverCaller(teamID, teamSecret, move);
  }

  /**
   * Requests a refresh of the GameState from the server. Parses the data and makes it available for
   * consumption Throws exceptions listed incase of acceptance or errors. Functions as a REFRESH
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
    // System.out.println(requestedTeamName + " wants to give up");
    comm.giveUp(currentServer, requestedTeamName, teamSecret);
  }

  /**
   * Changes the sessionID which this client object is pointing to. Functions as a join game command
   *
   * @param IP
   * @param port
   * @param gameSessionID
   * @param teamName
   * @throws SessionNotFound
   * @throws NoMoreTeamSlots
   * @throws UnknownError
   * @author rsyed
   */
  public void joinExistingGame(String IP, String port, String gameSessionID, String teamName) {
    this.currentServer = "http://" + IP + ":" + port + "/api/gamesession";
    this.currentServer = shortURL + "/" + gameSessionID;
    joinGame(teamName);
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
  public GameSessionResponse createGameCaller(MapTemplate map) {
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
   * individual variables for easier consumption by the UI
   *
   * @param {@link GameSessionResponse} with feteched Data from server
   * @author rsyed
   */
  public void gameSessionResponseParser(GameSessionResponse gameSessionResponse) {
    this.currentSession = gson.fromJson(gson.toJson(gameSessionResponse), GameSession.class);
    this.currentGameSessionID = gameSessionResponse.getId();
    this.currentServer = shortURL + "/" + currentGameSessionID;
    try {
      this.startDate = gameSessionResponse.getGameStarted();
    } catch (NullPointerException e) {
      System.out.println("Game hasnt started yet");
    }
    try {
      this.endDate = gameSessionResponse.getGameEnded();
    } catch (NullPointerException e) {
      System.out.println("Game hasnt ended yet");
    }
    try {
      this.gameOver = gameSessionResponse.isGameOver();
    } catch (NullPointerException e) {
      System.out.println("Game hasnt ended yet");
    }
    try {
      this.winners = gameSessionResponse.getWinner();
    } catch (NullPointerException e) {
      System.out.println("There are no winners");
    }
    try {
      this.currentGameSessionID = gameSessionResponse.getId();
    } catch (NullPointerException e) {
      System.out.println("No Game ID is set yet");
    }
    try {
      this.turnTimeLimit = gameSessionResponse.getRemainingGameTimeInSeconds();
      if (turnTimeLimit > 0) {
        this.timeLimitedGameTrigger = true;
      }

    } catch (NullPointerException e) {
      System.out.println("There is no Turn Time Limit");
    }
    try {
      this.moveTimeLeft = gameSessionResponse.getRemainingMoveTimeInSeconds();
      if (moveTimeLeft > 0) {
        this.moveTimeLimitedGameTrigger = true;
      }
    } catch (NullPointerException e) {
      System.out.println("There is no Turn Time Limit");
    }
  }

  /**
   * Called from the joinGame function. Recieves the teamName wished for by the team as a param and
   * returns the response from the server as a {@link JoinGameResponse}
   *
   * @param teamName with feteched Data from server
   * @return {@link JoinGameResponse} with returned data from the server
   * @throws SessionNotFound
   * @throws NoMoreTeamSlots
   * @throws UnknownError
   * @author rsyed
   */
  public JoinGameResponse joinGameCaller(String teamName) {
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
   * @author rsyed
   */
  public void joinGameParser(JoinGameResponse joinGameResponse) {
    this.teamID = joinGameResponse.getTeamId();
    this.teamSecret = joinGameResponse.getTeamSecret();
    try {
      this.teamColor = joinGameResponse.getTeamColor();
    } catch (NullPointerException e) {
      System.out.println("No Team color has been set");
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
  public void makeMoverCaller(String teamID, String teamSecret, Move move) {
    try {
      MoveRequest moveReq = new MoveRequest();
      moveReq.setTeamId(teamID);
      moveReq.setTeamSecret(teamSecret);
      moveReq.setPieceId(move.getPieceId());
      moveReq.setNewPosition(move.getNewPosition());
      comm.makeMove(currentServer, moveReq);
    } catch (SessionNotFound e) {
      throw new SessionNotFound();
    } catch (ForbiddenMove e) {
      throw new ForbiddenMove();
    } catch (InvalidMove e) {
      throw new InvalidMove();
    } catch (GameOver e) {
      throw new GameOver();
    } catch (UnknownError e) {
      throw new UnknownError();
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
  public void gameStateHelper() {
    GameState gameState = new GameState();
    try {
      gameState = comm.getCurrentGameState(currentServer);
    } catch (SessionNotFound e) {
      throw new SessionNotFound("Session isnt available for this request");
    } catch (UnknownError e) {
      throw new UnknownError("Server Error or Setting error");
    }
    this.grid = gameState.getGrid();
    this.currentTeamTurn = gameState.getCurrentTeam();
    this.lastMove = gameState.getLastMove();
    updateLastTeam();
    this.teams = gameState.getTeams();
    this.currentState = gameState;
  }

  /**
   * Called from the getGameSession method. Requests the server specific/set in the Client object to
   * send the current {@link GameSessionResponse}.
   *
   * @throws SessionNotFound
   * @throws UnknownError
   * @author rsyed
   */
  public GameSessionResponse gameSessionCaller() {
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
   * Refreshes {@link GameState} from the server and then checks if the GameStates currentTeam
   * matches the one from any {@link TeamID} in the {@link Team} Array
   *
   * @throws NumberFormatException if the server does not support proper ID return
   * @return true if its your turn, false if its not
   * @author rsyed
   */
  protected boolean isItMyTurn() {
    getStateFromServer();
    int index = 0;
    for (int i = 0; i < this.currentState.getTeams().length; i++) {
      if (!this.currentState.getTeams()[i].getId().equals("" + i)) {
        index = i;
        this.currentState.getTeams()[i].setId("" + i);
      }
    }
    return this.currentState.getCurrentTeam() == index;
  }

  /**
   * Checks if server is active through a dummy gameTemplate
   *
   * @return true if server is active and ready to make sessions, false if not
   * @author rsyed
   */
  protected boolean isServerActive() {
    return new ServerChecker().isServerActive(this.serverInfo.getHost(), this.serverInfo.getPort());
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
  public void updateLastTeam() {
    this.lastTeamTurn =
        (lastMove != null)
            ? Integer.parseInt(lastMove.getPieceId().split(":")[1].split("_")[0])
            : -1;
  }

  /**
   * Method which returns which team made the last move -1 if no last move. 0 to n otherwise
   *
   * @author rsyed
   */
  public int getLastTeamTurn() {
    return lastTeamTurn;
  }

  // **************************************************
  // End of Client Funtional Methods
  // **************************************************

  // **************************************************
  // Start of Alt Game Data Getters
  // **************************************************

  public int getRemainingMoveTimeInSeconds() {
    return this.moveTimeLeft;
  }

  public int getRemainingGameTimeInSeconds() {
    return this.gameTimeLeft;
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
  public void startGameController() {
    gameStartWatcher();
  }

  /**
   * A Watcher thread which calls GameSessionResponse periodically and hands over functionality to
   * GameStartedThread when it has and then terminates itself.
   *
   * @author rsyed
   */
  public void gameStartWatcher() {
    Thread watcherThread =
        new Thread(
            () -> {
              boolean running = true;
              while (running) {
                try {
                  Long sleep = 1000L;
                  Thread.sleep(sleep);
                  this.getSessionFromServer(); // Gets Session from server
                  if (getStartDate() != null) {
                    gameStartedThread();
                    running = false;
                  }
                  Thread.sleep(sleep);
                } catch (InterruptedException e) {
                  throw new Error("Something went wrong in the Client Thread");
                }
              }
            });
    watcherThread.start();
  }

  /**
   * Thead which handles client logic for when game has started. Just pulls data periodically set by
   * the refesh time var
   *
   * @author rsyed
   */
  public void gameStartedThread() {
    Thread gameThread =
        new Thread(
            () -> {
              boolean running = true;
              while (running) {
                try {
                  pullData();
                  Thread.sleep(this.refreshTime);
                } catch (InterruptedException e) {
                  throw new Error("Something went wrong in the Client Thread");
                }
              }
            });
    gameThread.start();
  }

  /**
   * Setter to change Refresh time period dynamically if desired
   *
   * @author rsyed
   */
  public void setRefreshTime(long refreshtime){
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

  public boolean isGameOver() {
    return gameOver;
  }

  public ServerDetails getServerDetails() {
    return this.serverInfo;
  }
  // **************************************************
  // End of Getter Block
  // **************************************************
}
