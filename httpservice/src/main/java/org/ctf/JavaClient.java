package org.ctf;

import com.google.gson.Gson;
import java.util.Date;
import org.ctf.controller.HTTPClientController;
import org.ctf.model.GameSession;
import org.ctf.model.GameSessionRequest;
import org.ctf.model.GameSessionResponse;
import org.ctf.model.GiveUpRequest;
import org.ctf.model.JoinGameRequest;
import org.ctf.model.JoinGameResponse;
import org.ctf.model.MoveRequest;
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

/**
 * Base Client file which is going to use the Translation Layer to talk to the game server Has
 * support for multiple gameSessions and flexible server polling
 *
 * @author rsyed
 */
public class JavaClient {

  // Main DataStore block
  private volatile GameState currentState;
  private String[][] grid;
  private int currentTeamTurn;
  private Move lastMove;
  private Team[] teams;

  private Gson gson; // Gson object for conversions incase needed
  private HTTPClientController comm; // Layer instance which is used for communication

  // Block for Server Info
  private String currentServer; // Creates URL with Session ID for use later
  private String shortURL;

  // Block for session info
  private GameSession currentSession;
  private String currentGameSessionID;
  private GameSessionResponse gameResponse;
  private String[] winners;

  // Block for Team Data
  private String teamSecret;
  public String teamID;
  public String teamColor;

  // Block for alt game mode data
  public Date startDate;
  public Date endDate;
  public Date moveTimeLeft;

  // Block for booleans
  public boolean gameOver;

  /** Constructor which inits some objects on creation */
  public JavaClient(HTTPClientController controller) {
    this.gson = new Gson(); // creates a gson Object on creation to conserve memory
    this.currentState = new GameState();
    this.currentSession = new GameSession();
    this.comm = controller;
  }

  /**
   * Requests the server specified in the current object to create a GameSession using the map in
   * the MapTemplate parameter. Throws exceptions on acception and incase errors occour
   *
   * @param map
   * @throws UnknownError (500)
   * @throws URLError (404)
   * @throws Accepted (200)
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
  public void joinGame(String teamName) {
    joinGameParser(joinGameCaller(teamName));
  }

  /**
   * Method makes a move in the game
   *
   * @param Move
   * @throws Accepted (200)
   * @throws SessionNotFound (404)
   * @throws ForbiddenMove (403)
   * @throws InvalidMove (409)
   * @throws GameOver (410)
   * @throws UnknownError (500)
   */
  public void makeMove(Move move) {
    makeMoverCaller(currentServer, teamID, teamSecret, move);
    /* getStateFromServer();
    getSessionFromServer(); */
  }

  /**
   * Requests a refresh of the GameState from the server. Parses the data and makes it available for
   * consumption Throws exceptions listed incase of acceptance or errors. Functions as a REFRESH
   * COMMAND for GAMESTATE
   *
   * @throws Accepted
   * @throws SessionNotFound
   * @throws UnknownError
   * @throws URLError (404)
   */
  public void getStateFromServer() {
    gameStateHelper();
  }

  /**
   * Requests the server to return the current state of the session and parses it Throws exceptions
   * depending on what happens. Functions as a REFRESH command for SESSION INFO
   *
   * @throws Accepted (200)
   * @throws SessionNotFound (404)
   * @throws UnknownError (500)
   * @throws URLError (404)
   */
  // Refreshes the session
  public void getSessionFromServer() {
    gameSessionResponseParser(gameSessionCaller());
  }

  /**
   * Requests the server to delete the current session. Returns the server reponse which are HTTP
   * status codes thrown as exceptions.
   *
   * @throws Accepted (200)
   * @throws SessionNotFound (404)
   * @throws UnknownError (500)
   * @throws URLError (404)
   */
  public void deleteSession() {
    comm.deleteSession(currentServer);
  }

  /**
   * Used to send a give up request to the current session. Throws exceptions depending on errors
   *
   * @throws Accepted (200)
   * @throws SessionNotFound (404)
   * @throws ForbiddenMove (403)
   * @throws GameOver (410)
   * @throws UnknownError (500)
   * @throws URLError (404)
   */
  public void giveUp() {
    comm.giveUp(currentServer,  new GiveUpRequest(teamID, teamSecret));
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

  // HELPER METHODS
  /*
   * @param map
   * @throws UnknownError (500)
   * @throws URLError (404)
   */
  private GameSessionResponse createGameCaller(MapTemplate map) {
    try {
      return comm.createGameSession(new GameSessionRequest(map));
    } catch (URLError e) {
      System.out.println("Something wrong with the server. Try to fix using setServer");
    } catch (UnknownError e) {
      System.out.println("Something is wrong with the server");
    }
  }

  private void gameSessionResponseParser(GameSessionResponse gameSessionResponse) {
    this.currentGameSessionID = gameSessionResponse.id();
    this.currentServer = shortURL + "/" + currentGameSessionID;
    try {
      this.startDate = gameSessionResponse.gameStarted();
    } catch (NullPointerException e) {
      System.out.println("Game hasnt started yet");
    }
    try {
      this.endDate = gameSessionResponse.gameEnded();
    } catch (NullPointerException e) {
      System.out.println("Game hasnt ended yet");
    }
    try {
      this.gameOver = gameSessionResponse.gameOver();
    } catch (NullPointerException e) {
      System.out.println("Game hasnt ended yet");
    }
    try {
      this.winners = gameSessionResponse.winner();
    } catch (NullPointerException e) {
      System.out.println("There are no winners");
    }
    throw new Accepted();
  }

 /**
   * Method joins the requested game session
   *
   * @param teamName
   * @throws SessionNotFound
   * @throws NoMoreTeamSlots
   * @throws UnknownError
   */
  private JoinGameResponse joinGameCaller(String teamName) {
    try {
      return comm.joinGameSession(getCurrentGameSessionID(),new JoinGameRequest(teamName));
    } catch (SessionNotFound e) {
      System.out.println("SessionID is wrong / Server is not there");
    } catch (NoMoreTeamSlots e) {
      System.out.println("Slots are full!");
    } catch (UnknownError e) {
      System.out.println("Something wong");
    }
  }

  private void joinGameParser(JoinGameResponse joinGameResponse) {
    this.teamID = joinGameResponse.teamId();
    this.teamSecret = joinGameResponse.teamSecret();
    try {
      this.teamColor = joinGameResponse.teamColor();
    } catch (NullPointerException e) {
      System.out.println("No Team color has been set");
    }
  }

  private void makeMoverCaller(String currentServer, String teamID, String teamSecret, Move move) {
    try {
      comm.move(currentServer, new MoveRequest(teamID, teamSecret, move.getPieceId(), move.getNewPosition()));
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
    GameState gameState = comm.getState(currentServer);

    this.grid = gameState.getGrid();
    this.currentTeamTurn = gameState.getCurrentTeam();
    this.lastMove = gameState.getLastMove();
    this.teams = gameState.getTeams();
  }

  private GameSessionResponse gameSessionCaller() {
    GameSessionResponse gameSessionResponse;
    try {
      gameSessionResponse = comm.getSession(currentServer);
    } catch (SessionNotFound e) {
      throw new SessionNotFound();
    } catch (UnknownError e) {
      throw new UnknownError();
    }
    return gameSessionResponse;
  }

  // Getter Block
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
}
