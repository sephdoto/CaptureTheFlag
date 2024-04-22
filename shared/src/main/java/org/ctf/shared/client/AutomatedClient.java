package org.ctf.shared.client;

import ch.qos.logback.core.util.Duration;
import com.google.gson.Gson;
import java.time.Clock;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.ctf.shared.ai.AI_Controller;
import org.ctf.shared.client.lib.Analyzer;
import org.ctf.shared.client.lib.GameClientInterface;
import org.ctf.shared.client.lib.ServerChecker;
import org.ctf.shared.client.service.CommLayerInterface;
import org.ctf.shared.constants.Constants.AI;
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
import org.ctf.shared.state.data.map.MapTemplate;
import org.ctf.shared.state.dto.GameSession;
import org.ctf.shared.state.dto.GameSessionRequest;
import org.ctf.shared.state.dto.GameSessionResponse;
import org.ctf.shared.state.dto.JoinGameResponse;
import org.ctf.shared.state.dto.MoveRequest;

public class AutomatedClient implements GameClientInterface {
  // Main DataStore block
  private volatile GameState currentState;
  private volatile String[][] grid;
  private volatile int currentTeamTurn;
  private volatile Move lastMove;
  private volatile Team[] teams;
  private volatile Clock currentTime;

  private Gson gson; // Gson object for conversions incase needed
  // Two CommLayers Available CommLayer and RestClientLayer
  private CommLayerInterface comm; // Layer instance which is used for communication

  // Block for Server Info
  private String currentServer; // Creates URL with Session ID for use later
  private String shortURL;
  private String port;
  private String baseURL;

  // Block for session info
  private volatile GameSession currentSession;
  private String currentGameSessionID;
  private volatile GameSessionResponse gameResponse;
  private String[] winners;

  // Block for Team Data
  private String teamSecret;
  public String teamName; // Team name we request from the Server
  private String teamID; // TeamID we get from the server for current team recognition
  public String teamColor;

  // Block for alt game mode data
  public Date startDate;
  public Date endDate;
  public int moveTimeLeft;
  private Duration turnTime;
  private Clock gameShouldEndBy;
  public int timeLeftInTheGame;
  private Duration timeLimDuration;
  private int lastTeamTurn;
  private long refreshTime = 300L;

  // Block for booleans
  public boolean gameOver;
  public boolean gameStarted;
  protected boolean turnSupportFlag; // is enabled when the team ID recieved in response is an INT

  public AI selectedPlayer;
  public boolean enableSaveGame;
  public volatile Analyzer analyzer;
  public AI_Controller Controller;
  ExecutorService executor = Executors.newFixedThreadPool(10);

  /**
   * Constructor to set the IP and port on object creation
   *
   * @param selector boolean to enable RESTClient
   * @param IP the IP to connect to Exp "localhost" or "192.xxx.xxx.xxx"
   * @param port the port the server is at Exp 9999 / 8080 /
   */
  AutomatedClient(CommLayerInterface comm, String IP, String port) {
    this.baseURL = IP;
    this.port = port;
    this.gson = new Gson();
    this.currentState = new GameState();
    this.currentSession = new GameSession();
    this.comm = comm;
    setServer(IP, port);
  }

  /**
   * Method to set the server which this this object communicates with
   *
   * @param IP
   * @param port
   */
  public void setServer(String IP, String port) {
    this.currentServer = "http://" + IP + ":" + port + "/api/gamesession";
    this.shortURL = "http://" + IP + ":" + port + "/api/gamesession";
  }

  /**
   * Requests the server specified in the current object to create a GameSession using the map in
   * the MapTemplate parameter. Throws exceptions on acception and incase errors occour
   *
   * @param map
   * @throws UnknownError (500)
   * @throws URLError (404)
   */
  @Override
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
    setServer(IP, port);
    this.currentGameSessionID = gameSessionID;
    joinGame(teamName);
  }

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
      return false;
    }
  }

  /**
   * Checks if server is active through a dummy gameTemplate
   *
   * @param ip IP of the server
   * @param port port of the server
   * @return true if server is active and ready to make sessions, false if not
   */
  protected boolean isServerActive(String ip, String port) {
    return new ServerChecker().isServerActive(ip, port);
  }

  // HELPER METHODS

  private GameSessionResponse createGameCaller(MapTemplate map) {
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
    //// Block for parsing and setting alt game mode flags here
    //
    // initAltGameModeLogic(map);
    //
    // End of Block
    return gameResponse;
  }

  private void gameSessionResponseParser(GameSessionResponse gameSessionResponse) {
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
  }

  private JoinGameResponse joinGameCaller(String teamName) {
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

  private void joinGameParser(JoinGameResponse joinGameResponse) {
    try {
      int temp = Integer.parseInt(joinGameResponse.getTeamId());
      this.turnSupportFlag = true;
    } catch (Exception e) {
      this.turnSupportFlag = false;
    }
    this.teamID = joinGameResponse.getTeamId(); // This is the INT Parseable ID from the server
    this.teamSecret = joinGameResponse.getTeamSecret();
    try {
      this.teamColor = joinGameResponse.getTeamColor();
    } catch (NullPointerException e) {
      System.out.println("No Team color has been set");
    }
  }

  private void makeMoverCaller(String currentServer, String teamID, String teamSecret, Move move) {
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

  private void gameStateHelper() {
    GameState gameState = comm.getCurrentGameState(currentServer);
    this.grid = gameState.getGrid();
    this.currentTeamTurn = gameState.getCurrentTeam();
    this.lastMove = gameState.getLastMove();
    updateLastTeam();
    this.teams = gameState.getTeams();
    this.currentState = gameState;
  }

  private GameSessionResponse gameSessionCaller() {
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

  /**
   * Method which sets an int to keep track of which teams turn it was before (Derived from Last
   * Move). Sets the lastTeamTurn int to -1 if no last move, 0 to n Otherwise
   *
   * @author rsyed
   */
  private void updateLastTeam() {
    this.lastTeamTurn =
        (lastMove != null)
            ? Integer.parseInt(lastMove.getPieceId().split(":")[1].split("_")[0])
            : -1;
  }

  /**
   * Method is used to create a Executor Task which checks if the server the client is set to is
   * active. And then creates a game and join it automatically
   *
   * @param template The map which is going to be used
   * @author rsyed
   */
  public void AutomatedCreateAndJoinTask(MapTemplate template, String teamName) {
    Thread gameSaverThread =
        new Thread(
            () -> {
              boolean running = true;
              while (running) {
                try {
                  if (isServerActive("localhost", "8888")) {
                    createGame(template);
                    joinGame(teamName);
                    Thread.sleep(200);
                    if(this.currentGameSessionID != null){
                      playTheGame(AI.MCTS);
                    }
                    //running = false;
                  }
                } catch (Exception e) {
                  throw new Error("Method is borked");
                }
              }
            });
    gameSaverThread.start();
  }

  /**
   * Method is used to create a Executor Task which checks if the server the client is set to is
   * active. And then creates a game and join it automatically
   *
   * @param template The map which is going to be used
   * @author rsyed
   */
  public void playTheGame(AI selected) {
    Thread player =
        new Thread(
            () -> {
              boolean running = true;
              while (running) {
                try {
                 getSessionFromServer();
                 getStateFromServer(); 
                 Controller = new AI_Controller(getCurrentState(), selected);
                 if(isItMyTurn()){
                  makeMove(Controller.getNextMove());
                  getSessionFromServer();
                  getStateFromServer(); 
                  Controller.update(currentState);
                 }   
                 
                 Thread.sleep(1000);
                  
                  
                } catch (Exception e) {
                  throw new Error("Method is borked");
                }
              }
            });
            player.start();
  }

  private GameState getCurrentState() {
    return this.currentState;
  }

  public String getSessionID(){
    return this.currentGameSessionID;
  }
}
