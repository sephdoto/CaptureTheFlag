package org.ctf.client.controller;

import org.ctf.client.data.dto.GameSessionRequest;
import org.ctf.client.data.dto.GameSessionResponse;
import org.ctf.client.data.dto.GiveupRequest;
import org.ctf.client.data.dto.JoinGameRequest;
import org.ctf.client.data.dto.JoinGameResponse;
import org.ctf.client.data.dto.MoveRequest;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.data.exceptions.Accepted;
import org.ctf.shared.state.data.exceptions.ForbiddenMove;
import org.ctf.shared.state.data.exceptions.GameOver;
import org.ctf.shared.state.data.exceptions.InvalidMove;
import org.ctf.shared.state.data.exceptions.NoMoreTeamSlots;
import org.ctf.shared.state.data.exceptions.SessionNotFound;
import org.ctf.shared.state.data.exceptions.URLError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * A Spring Boot Controller based Communication Layer which can be used to make calls to and get
 * data from the REST Api Server Takes the IP and Remote port to connect to from the constants file
 *
 * @author rsyed
 */
@Service
public class CaptureTheFlagClient {

  private RestClient restClient;

  /** No param constructor to init the RestClient with a baseURL */
  @Autowired
  public CaptureTheFlagClient() {
    this.restClient =
        RestClient.builder()
            .baseUrl(
                "http://"
                    + Constants.remoteIP
                    + ":"
                    + Constants.remotePort
                    + Constants.remoteBinder)
            .build();
  }

  /**
   * Requests the server specified in the URL parameter to create a GameSession using the map in the
   * MapTemplate parameter. Returns the server reponse as well as HTTP status codes thrown as
   * exceptions
   *
   * @param GameSessionRequest
   * @returns GameSessionResponse
   * @throws UnknownError (500)
   * @throws URLError (404)
   * @throws Accepted (200)
   */
  public GameSessionResponse createGameSession(GameSessionRequest gameSessionRequest) {
    return restClient
        .post()
        .uri("gamesession")
        .contentType(MediaType.APPLICATION_JSON)
        .body(gameSessionRequest)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        // .onStatus(status -> status.value() == 404, (request, response) -> {
        //      throw new URLError("URL Error")
        // })
        .body(GameSessionResponse.class);
  }

  /**
   * Makes a request to the server, specified in the URL, to join the game using the specified
   * teamName. Returns the server reponse as well as HTTP status codes thrown as exceptions
   *
   * @param sessionId
   * @param JoinGameRequest
   * @return JoinGameResponse
   * @throws SessionNotFound (404)
   * @throws NoMoreTeamSlots (429)
   * @throws UnknownError (500)
   * @throws Accepted (200)
   * @throws URLError (404)
   */
  public JoinGameResponse joinGameSession(
      String sessionId, JoinGameRequest jd) { // Make Request Object in Client
    return restClient
        .post()
        .uri("gamesession/{sessionId}/join", sessionId)
        .contentType(MediaType.APPLICATION_JSON)
        .body(jd)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .body(JoinGameResponse.class);
  }

  /**
   * Method makes a move request to the API using the input parameters. Returns the server reponse
   * as well as HTTP status codes thrown as exceptions
   *
   * @param sessionId
   * @param moveRequest
   * @throws Accepted (200)
   * @throws ForbiddenMove (403)
   * @throws SessionNotFound (404)
   * @throws InvalidMove (409)
   * @throws GameOver (410)
   * @throws UnknownError (500)
   * @throws URLError (404)
   */
  public void move(String sessionId, MoveRequest moveRequest) { // Make Request Object in Client
    restClient
        .post()
        .uri("gamesession/{sessionId}/move", sessionId)
        .contentType(MediaType.APPLICATION_JSON)
        .body(moveRequest)
        .retrieve();
  }

  /**
   * Used to send a give up request to the server specified in the URL param. Other params are the
   * data needed to make a complete request. Returns the server reponse as well as HTTP status codes
   * thrown as exceptions.
   *
   * @param sessionId
   * @param giveupRequest
   * @throws Accepted (200)
   * @throws SessionNotFound (404)
   * @throws ForbiddenMove (403)
   * @throws GameOver (410)
   * @throws UnknownError (500)
   * @throws URLError (404)
   */
  public void giveUp(
      String sessionId, GiveupRequest giveupRequest) { // Make Request Object in Client
    restClient
        .post()
        .uri("gamesession/{sessionId}/giveup", sessionId)
        .contentType(MediaType.APPLICATION_JSON)
        .body(giveupRequest)
        .retrieve()
        .toBodilessEntity();
  }

  /**
   * Requests the server to return the current state of the session, session and server are
   * specified in the input param URL. Returns the server reponse as well as HTTP status codes
   * thrown as exceptions Functions as a refresh command for the URL specified session.
   *
   * @param sessionId
   * @return GameSessionResponse
   * @throws Accepted (200)
   * @throws SessionNotFound (404)
   * @throws UnknownError (500)
   * @throws URLError (404)
   */
  public GameSessionResponse getSession(String sessionId) {
    return restClient
        .get()
        .uri("gamesession/{sessionId}", sessionId)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .body(GameSessionResponse.class);
  }

  /**
   * Gets the GameState from the API. On faliure throws Exceptions
   *
   * @param SessionId
   * @return GameState
   * @throws Accepted
   * @throws SessionNotFound
   * @throws UnknownError
   * @throws URLError (404)
   */
  public GameState getState(String sessionId) {
    return restClient
        .get()
        .uri("gamesession/{sessionId}/state", sessionId)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .body(GameState.class);
  }

  /**
   * Requests the server to delete the current session On faliure throws Exceptions
   *
   * @param sessionId
   * @throws Accepted (200)
   * @throws SessionNotFound (404)
   * @throws UnknownError (500)
   * @throws URLError (404)
   */
  public void deleteSession(String sessionId) {
    restClient.delete().uri("gamesession/{sessionId}", sessionId).retrieve().toBodilessEntity();
  }
}
