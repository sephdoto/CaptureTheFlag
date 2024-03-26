package org.ctf.client;

import com.google.gson.Gson;
import java.util.Date;
import org.ctf.client.service.CommLayer;
import org.ctf.client.state.data.wrappers.GameSessionResponse;
import org.ctf.client.state.data.wrappers.JoinGameResponse;
import org.ctf.client.tools.GameSession;
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
public class JavaClient implements GameClientInterface {

  // Main DataStore block
  private volatile GameState currentState;
  private String[][] grid;
  private int currentTeamTurn;
  private Move lastMove;
  private Team[] teams;

  private Gson gson; // Gson object for conversions incase needed
  private CommLayer comm; // Layer instance which is used for communication

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
  public JavaClient() {
    this.gson = new Gson(); // creates a gson Object on creation to conserve memory
    this.currentState = new GameState();
    this.currentSession = new GameSession();
    this.comm = new CommLayer();
  }

  public void setServer(String IP, String port) {
    this.currentServer = "http://" + IP + ":" + port + "/api/gamesession";
    this.shortURL = "http://" + IP + ":" + port + "/api/gamesession";
  }

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
    getStateFromServer();
    getSessionFromServer();
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
  @Override
  public void makeMove(Move move) {
    makeMoverCaller(currentServer, teamID, teamSecret, move);
    getStateFromServer();
    getSessionFromServer();
  }

  // Refreshes the WHOLE STATE
  @Override
  public void getStateFromServer() {
    gameStateParser(gameStateCaller());
  }

    /**
   * Requests the server to return the current state of the session and parses it
   * Throws exceptions depending on what happens.
   * Functions as a refresh command for current session
   *
   * @param URL
   * @return GameSessionResponse
   * @throws Accepted (200)
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
   * Requests the server to delete the current session. Returns the
   * server reponse which are HTTP status codes thrown as exceptions.
   *
   * @throws Accepted (200)
   * @throws SessionNotFound (404)
   * @throws UnknownError (500)
   * @throws URLError (404)
   */
  @Override
  public void deleteSession() {
    comm.deleteCurrentSession(currentServer);
    getStateFromServer();
    getSessionFromServer();
  }

   /**
   * Used to send a give up request to the current session.
   * Throws exceptions depending on errors
   *
   * @throws Accepted (200)
   * @throws SessionNotFound (404)
   * @throws ForbiddenMove (403)
   * @throws GameOver (410)
   * @throws UnknownError (500)
   * @throws URLError (404)
   */
  @Override
  public void giveUp() {
    comm.giveUp(currentServer, teamID, teamSecret);
    getStateFromServer();
    getSessionFromServer();
  }

/**
   * Changes the sessionID which this client object is pointing to. Functions as a join game command
   * 
   *  @param IP
   *  @param port
   *  @param gameSessionID
   *  @param teamName
   */
  public void joinExistingGame(String IP, String port, String gameSessionID, String teamName) {
    this.currentServer = "http://" + IP + ":" + port + "/api/gamesession";
    this.currentServer = shortURL + "/" + gameSessionID;
    joinGame(teamName);
    getStateFromServer();
    getSessionFromServer();
  }

  // HELPER METHODS

  private GameSessionResponse createGameCaller(MapTemplate map) {
    gameResponse = new GameSessionResponse();
    try {
      gameResponse = comm.createGameSession(currentServer, map);
    } catch (URLError e) {
      System.out.println("Something wrong with the server. Try to fix using setServer");
    } catch (UnknownError e) {
      System.out.println("Something is wrong with the server");
    }

    return gameResponse;
  }

  private void gameSessionResponseParser(GameSessionResponse gameSessionResponse) {
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
  }

  private JoinGameResponse joinGameCaller(String teamName) {
    JoinGameResponse response = new JoinGameResponse();

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

    // TODO
    // You get a confirmation of the Session ID which you joined the team with
    // Maybe use this for a check if you joined the right session
    joinGameResponse.getGameSessionId();
    // END
    this.teamID = joinGameResponse.getTeamId();
    this.teamSecret = joinGameResponse.getTeamSecret();

    try {
      this.teamColor = joinGameResponse.getTeamColor();
    } catch (NullPointerException e) {
      System.out.println("No Team color has been set");
    }
  }

  private void makeMoverCaller(String currentServer, String teamID, String teamSecret, Move move) {
    try {
      comm.makeMove(currentServer, teamID, teamSecret, move);
    } catch (Accepted e) {
      System.out.println("We Gucci");
    } catch (SessionNotFound e) {
      System.out.println("SessionID is wrong / Server is not there");
    } catch (ForbiddenMove e) {
      System.out.println("Not turn/secret is borked");
    } catch (InvalidMove e) {
      System.out.println("Canne make this move mate");
    } catch (GameOver e) {
      System.out.println("Games Ova");
    } catch (UnknownError e) {
      System.out.println("Something wong");
    }
  }

  private GameState gameStateCaller() {
    return comm.getCurrentGameState(currentServer + "/" + currentGameSessionID + "/");
  }

  private void gameStateParser(GameState gameState) {
    this.grid = gameState.getGrid();
    this.currentTeamTurn = gameState.getCurrentTeam();
    this.lastMove = gameState.getLastMove();
    this.teams = gameState.getTeams();
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

  public String[] getWinner() {
    return winners;
  }
}
