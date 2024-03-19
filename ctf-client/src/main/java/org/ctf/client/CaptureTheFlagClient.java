package org.ctf.client;

import org.ctf.client.state.data.wrappers.GameSessionRequest;
import org.ctf.client.state.data.wrappers.GameSessionResponse;
import org.ctf.client.state.data.wrappers.GiveupRequest;
import org.ctf.client.state.data.wrappers.JoinGameRequest;
import org.ctf.client.state.data.wrappers.JoinGameResponse;
import org.ctf.client.state.data.wrappers.MoveRequest;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.state.GameState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class CaptureTheFlagClient {

  private RestClient restClient;

  @Autowired
  public CaptureTheFlagClient() {
    this.restClient =
        RestClient.builder()
            .baseUrl("http://" + Constants.remoteIP + ":" + Constants.remotePort + "/api/")
            .build();
  }

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

  public void move(String sessionId, MoveRequest moveRequest) { // Make Request Object in Client
    restClient
        .post()
        .uri("gamesession/{sessionId}/move", sessionId)
        .contentType(MediaType.APPLICATION_JSON)
        .body(moveRequest)
        .retrieve();
  }

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

  public GameSessionResponse getSession(String sessionId) {
    return restClient
        .get()
        .uri("gamesession/{sessionId}", sessionId)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .body(GameSessionResponse.class);
  }

  public GameState getState(String sessionId) {
    return restClient
        .get()
        .uri("gamesession/{sessionId}/state", sessionId)
        .accept(MediaType.APPLICATION_JSON)
        .retrieve()
        .body(GameState.class);
  }

  public void deleteSession(String sessionId) {
    restClient.delete().uri("gamesession/{sessionId}", sessionId).retrieve().toBodilessEntity();
  }
}
