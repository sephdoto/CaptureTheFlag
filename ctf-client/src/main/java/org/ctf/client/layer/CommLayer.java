package org.ctf.client.layer;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import org.ctf.client.state.GameState;
import org.ctf.client.state.Move;
import org.ctf.client.state.data.exceptions.Accepted;
import org.ctf.client.state.data.exceptions.ForbiddenMove;
import org.ctf.client.state.data.exceptions.GameOver;
import org.ctf.client.state.data.exceptions.InvalidMove;
import org.ctf.client.state.data.exceptions.NoMoreTeamSlots;
import org.ctf.client.state.data.exceptions.SessionNotFound;
import org.ctf.client.state.data.exceptions.URLError;
import org.ctf.client.state.data.exceptions.UnknownError;
import org.ctf.client.state.data.map.MapTemplate;
import org.ctf.client.state.data.wrappers.GameSessionRequest;
import org.ctf.client.state.data.wrappers.GameSessionResponse;
import org.ctf.client.state.data.wrappers.GiveupRequest;
import org.ctf.client.state.data.wrappers.JoinGameRequest;
import org.ctf.client.state.data.wrappers.JoinGameResponse;
import org.ctf.client.state.data.wrappers.MoveRequest;

/**
 * Layer file which is going to contain commands sent to the Rest API running on the game server
 *
 * @author rsyed
 * @return Layer Object
 */
public class CommLayer implements CommLayerInterface {

  private Gson gson; // Gson object to convert classes to Json

  /**
   * Creates a Layer Object which can then be used to communicate with the Server The URL and the
   * port the layer binds to are given on object creation. Example URL http://localhost:8080
   *
   * @param url
   */
  public CommLayer() {
    gson = new Gson();
  }

  /**
   * Requests the server specified in the URL parameter to create a GameSession using the map in the
   * MapTemplate parameter. Returns the server reponse as well as HTTP status codes thrown as
   * exceptions
   *
   * @param URL
   * @param map
   * @returns GameSessionResponse
   * @throws UnknownError (500)
   * @throws URLError (404)
   * @throws Accepted (200)
   */
  @Override
  public GameSessionResponse createGameSession(String URL, MapTemplate map) {
    GameSessionRequest gsr = new GameSessionRequest();
    gsr.setTemplate(map);

    String jsonPayload = gson.toJson(gsr);

    // Performs the POST request
    HttpResponse<String> serverResponse;
    try {
      serverResponse = POSTRequest(URL, jsonPayload);
    } catch (URLError e) {
      throw new URLError("Check input URL");
    }

    // Parses Server Response to expected class
    GameSessionResponse gameSessionResponse =
        gson.fromJson(serverResponse.body(), GameSessionResponse.class);

    // Saves the code of the server response
    int returnedCode = serverResponse.statusCode();

    if (returnedCode == 500) {
      throw new UnknownError();
    } else if (returnedCode == 404) {
      throw new URLError("URL Error");
    }
    return gameSessionResponse;
  }

  /**
   * Makes a request to the server, specified in the URL, to join the game using the specified
   * teamName. Returns the server reponse as well as HTTP status codes thrown as exceptions
   *
   * @param URL
   * @param teamName
   * @return JoinGameResponse
   * @throws SessionNotFound (404)
   * @throws NoMoreTeamSlots (429)
   * @throws UnknownError (500)
   * @throws Accepted (200)
   * @throws URLError (404)
   */
  @Override
  public JoinGameResponse joinGame(String URL, String teamName) {

    JoinGameRequest joinGameRequest = new JoinGameRequest();
    joinGameRequest.setTeamId(teamName);

    HttpResponse<String> postResponse;
    try {
      postResponse = POSTRequest(URL + "/join", gson.toJson(joinGameRequest));
    } catch (URLError e) {
      throw new URLError("Check URL");
    }

    JoinGameResponse joinGameResponse = gson.fromJson(postResponse.body(), JoinGameResponse.class);

    int returnedCode = postResponse.statusCode();

    if (returnedCode == 404) {
      throw new SessionNotFound();
    } else if (returnedCode == 429) {
      throw new NoMoreTeamSlots();
    } else if (returnedCode == 500) {
      throw new UnknownError();
    }

    return joinGameResponse;
  }

  /**
   * Method makes a move request to the API using the input parameters. Returns the server reponse
   * as well as HTTP status codes thrown as exceptions
   *
   * @param URL
   * @param teamID
   * @param teamSecret
   * @param move
   * @throws Accepted (200)
   * @throws ForbiddenMove (403)
   * @throws SessionNotFound (404)
   * @throws InvalidMove (409)
   * @throws GameOver (410)
   * @throws UnknownError (500)
   * @throws URLError (404)
   */
  @Override
  public void makeMove(String URL, String teamID, String teamSecret, Move move) {
    MoveRequest moveReq = new MoveRequest();
    moveReq.setTeamId(teamID);
    moveReq.setTeamSecret(teamSecret);
    moveReq.setPieceId(move.getPieceId());
    moveReq.setNewPosition(move.getNewPosition());

    HttpResponse<String> postResponse;
    try {
      postResponse = POSTRequest(URL + "/move", gson.toJson(moveReq));
    } catch (URLError e) {
      throw new URLError("Check URL");
    }

    int returnedCode = postResponse.statusCode();

    if (returnedCode == 200) {
      throw new Accepted(200);
    } else if (returnedCode == 403) {
      throw new ForbiddenMove();
    } else if (returnedCode == 404) {
      throw new SessionNotFound();
    } else if (returnedCode == 409) {
      throw new InvalidMove();
    } else if (returnedCode == 410) {
      throw new GameOver();
    } else if (returnedCode == 500) {
      throw new UnknownError();
    }
  }

  /**
   * Used to send a give up request to the server specified in the URL param. Other params are the
   * data needed to make a complete request. Returns the server reponse as well as HTTP status codes
   * thrown as exceptions.
   *
   * @param URL
   * @param teamID
   * @param teamSecret
   * @throws Accepted (200)
   * @throws SessionNotFound (404)
   * @throws ForbiddenMove (403)
   * @throws GameOver (410)
   * @throws UnknownError (500)
   * @throws URLError (404)
   */
  @Override
  public void giveUp(String URL, String teamID, String teamSecret) {

    GiveupRequest giveUpRequest = new GiveupRequest();
    giveUpRequest.setTeamId(teamID);
    giveUpRequest.setTeamSecret(teamSecret);

    HttpResponse<String> postResponse;
    try {
      postResponse = POSTRequest(URL + "/giveup", gson.toJson(giveUpRequest));
    } catch (URLError e) {
      throw new URLError("Check URL");
    }

    int returnedCode = postResponse.statusCode();
    if (returnedCode == 200) {
      throw new Accepted(200);
    } else if (returnedCode == 403) {
      throw new ForbiddenMove();
    } else if (returnedCode == 404) {
      throw new SessionNotFound();
    } else if (returnedCode == 410) {
      throw new GameOver();
    } else {
      throw new UnknownError();
    }
  }

  /**
   * Requests the server to return the current state of the session, session and server are
   * specified in the input param URL. Returns the server reponse as well as HTTP status codes
   * thrown as exceptions Functions as a refresh command for the URL specified session.
   *
   * @param URL
   * @return GameSessionResponse
   * @throws Accepted (200)
   * @throws SessionNotFound (404)
   * @throws UnknownError (500)
   * @throws URLError (404)
   */
  @Override
  public GameSessionResponse getCurrentSessionState(String URL) {

    HttpResponse<String> getResponse;
    try {
      getResponse = GETRequest(URL);
    } catch (URLError e) {
      throw new URLError("Check URL");
    }

    GameSessionResponse gameSessionResponse =
        gson.fromJson(getResponse.body(), GameSessionResponse.class);

    int returnedCode = getResponse.statusCode();

    if (returnedCode == 404) {
      throw new SessionNotFound();
    } else if (returnedCode == 500) {
      throw new UnknownError();
    }

    return gameSessionResponse;
  }

  /**
   * Requests the server to delete the current session which is specified in the URL. Returns the
   * server reponse which are HTTP status codes thrown as exceptions.
   *
   * @param URL
   * @throws Accepted (200)
   * @throws SessionNotFound (404)
   * @throws UnknownError (500)
   * @throws URLError (404)
   */
  @Override
  public void deleteCurrentSession(String URL) {

    HttpResponse<String> deleteResponse;
    try {
      deleteResponse = DELETERequest(URL);
    } catch (URLError e) {
      throw new URLError("Check URL");
    }

    int returnedCode = deleteResponse.statusCode();

    if (returnedCode == 404) {
      throw new SessionNotFound();
    } else if (returnedCode == 500) {
      throw new UnknownError();
    }
  }

  /**
   * Requests the session, specified in the URL, to return its GameState. Returns the GameState
   * object as well as the HTTP status codes which are thrown as exceptions
   *
   * @param URL
   * @return GameState
   * @throws Accepted
   * @throws SessionNotFound
   * @throws UnknownError
   * @throws URLError (404)
   */
  @Override
  public GameState getCurrentGameState(String URL) {
    HttpResponse<String> getResponse;
    try {
      getResponse = GETRequest(URL + "/state");
    } catch (URLError e) {
      throw new URLError("Check URL");
    }

    GameState returnedState = gson.fromJson(getResponse.body(), GameState.class);

    int returnedCode = getResponse.statusCode();

    if (returnedCode == 404) {
      throw new SessionNotFound();
    } else if (returnedCode == 500) {
      throw new UnknownError();
    }
    return returnedState;
  }

  /**
   * Helper method to perform a POST HTTP Request
   *
   * @param URL
   * @param jsonPayload String Representation of a json
   * @return HttpResponse<String>
   * @throws URLError
   */
  private HttpResponse<String> POSTRequest(String URL, String jsonPayload) {

    HttpResponse<String> ret = null;
    try {
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(new URI(URL))
              .header("Content-Type", "application/json")
              .POST(BodyPublishers.ofString(jsonPayload))
              .build();
      ret = HttpClient.newHttpClient().send(request, BodyHandlers.ofString());
    } catch (URISyntaxException | IOException | InterruptedException | NullPointerException e) {
      throw new URLError("Check URL");
    }
    return ret;
  }

  /**
   * Helper method which takes a URL and performs a GET HTTP request and returns the response
   *
   * @param URL
   * @return HttpResponse<String>
   * @throws URLError
   */
  private HttpResponse<String> GETRequest(String URL) {
    HttpResponse<String> ret = null;
    try {
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(new URI(URL))
              .header("Content-Type", "application/json")
              .GET()
              .build();
      ret = HttpClient.newHttpClient().send(request, BodyHandlers.ofString());
    } catch (URISyntaxException | IOException | InterruptedException | NullPointerException e) {
      throw new URLError("Check URL");
    }
    return ret;
  }

  /**
   * Helper method which takes a URL and performs a DELETE HTTP request and returns the response
   *
   * @param URL
   * @return HttpResponse<String>
   * @throws URLError
   */
  private HttpResponse<String> DELETERequest(String URL) {
    HttpResponse<String> ret = null;
    try {
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(new URI(URL))
              .header("Content-Type", "application/json")
              .DELETE()
              .build();
      ret = HttpClient.newHttpClient().send(request, BodyHandlers.ofString());
    } catch (URISyntaxException | IOException | InterruptedException | NullPointerException e) {
      throw new URLError("Check URL");
    }
    return ret;
  }
}