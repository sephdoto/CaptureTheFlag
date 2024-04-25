package org.ctf.shared.client;

import com.google.gson.Gson;
import java.time.Clock;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.ctf.shared.client.lib.GameClientInterface;
import org.ctf.shared.client.lib.ServerChecker;
import org.ctf.shared.client.lib.ServerDetails;
import org.ctf.shared.client.service.CommLayerInterface;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Team;
import org.ctf.shared.state.data.exceptions.Accepted;
import org.ctf.shared.state.data.exceptions.ForbiddenMove;
import org.ctf.shared.state.data.exceptions.GameOver;
import org.ctf.shared.state.data.exceptions.InvalidMove;
import org.ctf.shared.state.data.exceptions.NoMoreTeamSlots;
import org.ctf.shared.state.data.exceptions.NoProperSupport;
import org.ctf.shared.state.data.exceptions.SessionNotFound;
import org.ctf.shared.state.data.exceptions.URLError;
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
  public boolean gameTimeLimitedToken;
  public boolean moveTimeLimtedToken;

  // Block for Team Data
  public String teamSecret;
  public String teamName; // Team name we request from the Server
  public String teamID; // TeamID we get from the server for current team recognition
  public String teamNumber; // Is set when the server tells you what number it assigned you
  public String teamColor;

  // Block for alt game mode data
  public Date startDate;
  public Date endDate;
  public int moveTimeLeft;
  public Duration turnTime;
  public Clock gameShouldEndBy;
  public int timeLeftInTheGame;
  public Duration timeLimDuration;
  public int lastTeamTurn;
  public long refreshTime = 300L;

  // Block for booleans
  public boolean gameOver;
  public boolean gameStarted;
  protected boolean turnSupportFlag; // is enabled when the team ID recieved in response is an INT
  public boolean moveTimeLimitedGameTrigger = false;
  public boolean timeLimitedGameTrigger = false;
  public Clock turnEndsBy;

  //Services
  ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
  ExecutorService executor = Executors.newFixedThreadPool(3);

  /**
   * Constructor to set the IP and port on object creation
   *
   * @param comm Sets the comm layer the client is going to use
   * @param IP the IP to connect to Exp "localhost" or "192.xxx.xxx.xxx"
   * @param port the port the server is at Exp 9999 / 8080 /
   * @param enableLogging tells the client to start logging the moves for later analysis by an AI
   */
  Client(CommLayerInterface comm, String IP, String port, Boolean enableLogging) {
    this.gson = new Gson();
    this.currentState = new GameState();
    this.currentSession = new GameSession();
    this.comm = comm;
    gameTimeLimitedToken = true;
    setServer(IP, port);
  }

  // **************************************************
  // Start of CRUD Call Methods
  // **************************************************
  /**
   * Method to set the server which this this object communicates with
   *
   * @param IP
   * @param port
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
   */
  @Override
  public void joinGame(String teamName) {
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
   */
  @Override
  public void makeMove(Move move) {
    makeMoverCaller(currentServer, teamID, teamSecret, move);
  }

  /**
   * Requests a refresh of the GameState from the server. Parses the data and makes it available for
   * consumption Throws exceptions listed incase of acceptance or errors. Functions as a REFRESH
   * COMMAND for GAMESTATE
   *
   * @throws SessionNotFound
   * @throws UnknownError
   * @throws URLError (404)
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
   */
  // Refreshes the session
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
   */
  @Override
  public void giveUp() {
    comm.giveUp(currentServer, teamID, teamSecret);
  }

  /**
   * Changes the sessionID which this client object is pointing to. Functions as a join game command
   *
   * @param IP
   * @param port
   * @param gameSessionID
   * @param teamName
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

  public GameSessionResponse createGameCaller(MapTemplate map) {
    gameResponse = new GameSessionResponse();
    GameSessionRequest gsr = new GameSessionRequest();
    gsr.setTemplate(map);
    try {
      this.gameResponse = comm.createGameSession(currentServer, gsr);
    } catch (URLError e) {
      System.out.println("Something wrong with the server. Try to fix using setServer");
    } catch (UnknownError e) {
      System.out.println("Something is wrong with the server");
    }
    return gameResponse;
  }

  public void gameSessionResponseParser(GameSessionResponse gameSessionResponse) {
    this.currentSession = gson.fromJson(gson.toJson(gameSessionResponse), GameSession.class);
    this.currentGameSessionID = gameSessionResponse.getId();
    this.currentServer = shortURL + "/" + currentGameSessionID;
    try {
      this.startDate = gameSessionResponse.getGameStarted();
      checkForTimeLimitedGame();
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
      this.turnTimeLimit = gameSessionResponse.getTurnTimeLimit();
    } catch (NullPointerException e) {
      System.out.println("There is no Turn Time Limit");
    }
  }

  public JoinGameResponse joinGameCaller(String teamName) {
    JoinGameResponse response = new JoinGameResponse();
    this.teamName = teamName;
    try {
      response = comm.joinGame(currentServer, teamName);
    } catch (SessionNotFound e) {
      System.out.println("SessionID is wrong / Server is not there");
    } catch (NoMoreTeamSlots e) {
      System.out.println("Slots are full!");
    } catch (UnknownError e) {
      System.out.println("Something wong");
    }
    return response;
  }

  public void joinGameParser(JoinGameResponse joinGameResponse) {
    checkProperResponse(joinGameResponse);
    this.teamID = joinGameResponse.getTeamId(); // This is the INT Parseable ID from the server
    this.teamSecret = joinGameResponse.getTeamSecret();
    try {
      this.teamColor = joinGameResponse.getTeamColor();
    } catch (NullPointerException e) {
      System.out.println("No Team color has been set");
    }
  }

  public void makeMoverCaller(String currentServer, String teamID, String teamSecret, Move move) {
    try {
      MoveRequest moveReq = new MoveRequest();
      moveReq.setTeamId(teamID);
      moveReq.setTeamSecret(teamSecret);
      moveReq.setPieceId(move.getPieceId());
      moveReq.setNewPosition(move.getNewPosition());
      comm.makeMove(currentServer, moveReq);
    } catch (Accepted e) {
      throw new Accepted();
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

  public void gameStateHelper() {
    GameState gameState = comm.getCurrentGameState(currentServer);
    this.grid = gameState.getGrid();
    this.currentTeamTurn = gameState.getCurrentTeam();
    this.lastMove = gameState.getLastMove();
    updateLastTeam();
    this.teams = gameState.getTeams();
    this.currentState = gameState;
  }

  public GameSessionResponse gameSessionCaller() {
    GameSessionResponse gameSessionResponse = new GameSessionResponse();
    try {
      gameSessionResponse = comm.getCurrentSessionState(currentServer);
    } catch (SessionNotFound e) {
      throw new SessionNotFound();
    } catch (UnknownError e) {
      throw new UnknownError();
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
   * Refreshes Game State from the server and then checks if the GameStatesCurrent team matches the
   * one from
   *
   * @throws NumberFormatException if the server does not support proper ID return
   * @return true if its your turn, false if its not
   */
  protected boolean isItMyTurn() {
    getStateFromServer();
    try {
      if (Integer.parseInt(this.teamID) == currentTeamTurn) {
        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      throw new NoProperSupport();
    }
  }

  /**
   * Checks if server is active through a dummy gameTemplate
   *
   * @return true if server is active and ready to make sessions, false if not
   */
  protected boolean isServerActive() {
    return new ServerChecker().isServerActive(this.serverInfo.getHost(), this.serverInfo.getPort());
  }

  /**
   * Method which can be called anywhere to pull both DTO objects from the server.
   *
   * @author rsyed
   */
  public void pullData() {
    this.getSessionFromServer();
    this.getStateFromServer();
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
   * Method Force starts the controller and automation
   *
   * @author rsyed
   */
  public void forcestartGameController() {
    startGameController();
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
  // Start of CRUD Helper Methods
  // **************************************************

  /**
   * Method is called while parsing JoinGameResponse to set variables incase the server
   * implementation supports the right implementation of the joinServerResponse. Modifies
   * turnSupportFlag to true incase it is supported. False if not
   *
   * @param joinGameResponse the object from which the data is read
   */
  public void checkProperResponse(JoinGameResponse joinGameResponse) {
    try {
      int teamNumber = Integer.parseInt(joinGameResponse.getTeamId());
      this.turnSupportFlag = true;
    } catch (Exception e) {
      this.turnSupportFlag = false;
    }
  }

  // **************************************************
  // End of CRUD Helper Methods
  // **************************************************

  // **************************************************
  // Start of Alt Game Logic
  // **************************************************

  /**
   * Init for TimeLimited Game Method is called while parsing JoinGameResponse to check if the game
   * is a TimeLimitedGame. Takes a token and consumes it so the check is only done ONCE when the
   * game has started
   */
  public void checkForTimeLimitedGame() {
    if (this.startDate != null && gameTimeLimitedToken) {
      if (this.startDate != null && this.endDate != null && !isGameOver()) {
        this.timeLimitedGameTrigger = true;
        // Sets the Duration
        this.timeLimDuration =
            Duration.ofSeconds(
                TimeUnit.SECONDS.convert(
                    Math.abs(endDate.getTime() - startDate.getTime()), TimeUnit.MILLISECONDS));

        this.gameTimeLimitedToken = false;
      }
      this.gameTimeLimitedToken = false;
    }
  }

  /**
   * Init for MoveTimeLimitedGame Method is called while parsing JoinGameResponse to check if the
   * game is a MoveTimeLimitedGame Takes a token and consumes it so the check is only done ONCE when
   * the game has started
   */
  public void checkForMoveTimeGame() {
    if (this.startDate != null && this.turnTimeLimit > 0 && moveTimeLimtedToken) {
      moveTimeLimitedGameTrigger = true;
      this.turnTime = Duration.ofSeconds(turnTimeLimit);
      this.moveTimeLimtedToken = false;
    }
    this.moveTimeLimtedToken = false;
  }

  // TODO Implement this Method
  public int getRemainingMoveTimeInSeconds() {

    return 0; // Dummy Return
  }

  /**
   * Checks how much time is left for the game
   *
   * @author rsyed
   * @return -1 if no total game time limit set, 0 if over, > 0 if seconds remain
   */
  public int getRemainingGameTimeInSeconds() {
    if (!this.timeLimitedGameTrigger) {
      return -1;
    }
    if (isGameOver()) {
      return 0;
    } else {
      return Math.toIntExact(
          Duration.between(currentTime.instant(), endDate.toInstant()).getSeconds());
    }
  }

  /**
   * Main CONTROLLER for Client. Call when either a game is created or a game is joined
   *
   * @author rsyed
   */
  public void startGameController() {
    gameStartWatcher();
  }

  /**
   * A Watcher. Calls GameSessionResponse periodically and hands over functionality to
   * GameStartedThread when it does and kills itself.
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
   * Thead which handles client logic for when game has started
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
                  // TODO Additional Logic once Professor updates the server
                  Thread.sleep(this.refreshTime);
                } catch (InterruptedException e) {
                  throw new Error("Something went wrong in the Client Thread");
                }
              }
            });
    gameThread.start();
  }

  public void setWhenGameShouldEnd() {
    this.gameShouldEndBy =
        Clock.offset(
            Clock.fixed(getStartDate().toInstant(), ZoneId.systemDefault()), timeLimDuration);
  }

  public void increaseTurnTimer() {
    this.turnEndsBy =
        Clock.fixed(Clock.offset(currentTime, turnTime).instant(), ZoneId.systemDefault());
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
