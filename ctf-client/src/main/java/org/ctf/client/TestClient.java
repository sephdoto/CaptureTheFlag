package org.ctf.client;

import java.util.Date;

import org.ctf.client.layer.CommLayer;
import org.ctf.client.state.GameState;
import org.ctf.client.state.Move;
import org.ctf.client.state.data.exceptions.Accepted;
import org.ctf.client.state.data.exceptions.ForbiddenMove;
import org.ctf.client.state.data.exceptions.GameOver;
import org.ctf.client.state.data.exceptions.InvalidMove;
import org.ctf.client.state.data.exceptions.NoMoreTeamSlots;
import org.ctf.client.state.data.exceptions.SessionNotFound;
import org.ctf.client.state.data.exceptions.URLError;
import org.ctf.client.state.data.map.MapTemplate;
import org.ctf.client.state.data.wrappers.GameSessionResponse;
import org.ctf.client.state.data.wrappers.JoinGameResponse;

import com.google.gson.Gson;

/**
 * Base Client file which is going to use the Translation Layer to talk to the game server
 *
 * <p>Also saves data critical for the GameState
 *
 * @author rsyed
 */
public class TestClient {

  public GameState currentState; // Main DataStore
  // tracks grid, teams, current team(move), last move,

  private Gson gson; // Gson object for conversions
  private CommLayer comm; // Layer instance which is used for communication

  // Block to store Data from Game Session
  public String gameSessionID; // Sets the Session ID for the Layer
  private String urlWithID; // Creates URL with Session ID for use later

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
  public String[] winner;

  /** Constructor which inits some objects on creation */
  public TestClient() {
    this.gson = new Gson(); // creates a gson Object on creation to conserve memory
    this.currentState = new GameState();
  }

  /**
   * Connect method to establish connection to the server
   *
   * @param URL "http://localhost:8080"
   * @param Map
   * @throws URLError 404
   * @throws UnknownError 500
   */
  public void connect(String URL, MapTemplate Map) {
    this.comm = new CommLayer();
    URL = URL + "/api/gamesession";
    GameSessionResponse response = new GameSessionResponse();

    try {
      response = comm.createGameSession(URL, Map);
    } catch (UnknownError e) {
      System.out.println("Something wong");
    } catch (URLError e) {
      System.out.println("Bruh check the URL");
    } catch (Accepted e) {
      System.out.println("We Gucci");
    }

    this.gameSessionID = response.getId(); // Saves SessionID
    this.urlWithID = URL + "/" + gameSessionID; // Creates URL with Session ID for use later

    //TIME LEFT IN YOUR TURN IS CALCULATED CLIENT SIDE
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
    JoinGameResponse response = new JoinGameResponse();

    try {
      response = comm.joinGame(urlWithID, teamName);
    } catch (SessionNotFound e) {
      System.out.println("SessionID is wrong / Server is not there");
    } catch (NoMoreTeamSlots e) {
      System.out.println("Slots are full!");
    } catch (UnknownError e) {
      System.out.println("Something wong");
    }

    // Sets Data for the Layer from the response
    this.teamSecret = response.getTeamSecret();
    this.teamID = response.getTeamId();
    this.teamColor = response.getTeamColor();

    // Additional Tags for ALT game Modes

    updateState();
  }

  /**
   * Method makes a move in the game
   *
   * @param teamID
   * @param secret
   * @param Move
   * @throws Accepted (200)
   * @throws SessionNotFound (404)
   * @throws ForbiddenMove (403)
   * @throws InvalidMove (409)
   * @throws GameOver (410)
   * @throws UnknownError (500)
   */
  public void makeMove(String teamID, String secret, Move move) {

    try {
      comm.makeMove(urlWithID, teamID, teamSecret, move);
    } catch (Accepted e) {
      System.out.println("We Gucci");
      updateState();
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

  public void giveUp() {
    comm.giveUp(urlWithID, teamID, teamSecret);
    updateState();
    refreshSession();
  }

  // syncs session state with server
  public void updateState() {
    this.currentState = comm.getCurrentGameState(urlWithID);
  }

  // Refreshes the session
  public void refreshSession() {
    GameSessionResponse gsr = new GameSessionResponse();
    try {
        gsr = comm.getCurrentSessionState(urlWithID);
    } catch (Accepted  e) {
        this.gameOver = gsr.isGameOver();
        if (gsr.getGameStarted() != null) {
          this.startDate = gsr.getGameStarted();
        }

        if (gsr.getGameEnded() != null) {
          this.endDate = gsr.getGameEnded();
        }

        if (gsr.getWinner() != null) {
          this.winner = gsr.getWinner();
        }
    } catch (SessionNotFound  e) {
        throw new SessionNotFound();
    } catch (UnknownError  e) {
        throw new UnknownError();
    }
  }

  public void changeSession() {
    
  }

  public void deleteSession() {
    comm.deleteCurrentSession(urlWithID);
  }

  // Methods for Testing Returns
  public String getSessionID() {
    return this.gameSessionID;
  }

  public String getSecretID() {
    return this.teamSecret;
  }

  public GameState getState() {
    return this.currentState;
  }
}
