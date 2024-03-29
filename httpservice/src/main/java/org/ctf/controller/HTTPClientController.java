package org.ctf.controller;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.ctf.model.GameSessionResponse;
import org.ctf.model.GiveUpRequest;
import org.ctf.model.JoinGameRequest;
import org.ctf.model.JoinGameResponse;
import org.ctf.model.MoveRequest;
import org.ctf.shared.state.GameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// MAIN FILE WHICH GETS DATA N SHIT...THIS IS THE CLIENT
@RestController
@RequestMapping("/api-client")
public class HTTPClientController {
  private static final Logger LOG = LoggerFactory.getLogger(cfpServiceClient.class);
  @Autowired
  public final cfpServiceClient cfpClient;

  public HTTPClientController(cfpServiceClient cfpClient) {
    this.cfpClient = cfpClient;
  }

  @PostMapping("/gamesession/")
  public GameSessionResponse createGameSession(
      @RequestBody org.ctf.model.GameSessionRequest gameSessionRequest) {
    return cfpClient.createGameSession(gameSessionRequest);
  }

  public JoinGameResponse joinGameSession(
      @PathVariable String sessionId,
      @RequestBody JoinGameRequest jd) { // Make Request Object in Client
    LOG.info("Performed a joinGameSession request");
    return cfpClient.joinGameSession(sessionId, jd);
  }

  @PostMapping("/gamesession/{sessionId}/move")
  public void move(
      @PathVariable String sessionId,
      @RequestBody MoveRequest moveRequest) { // Make Request Object in Client
    LOG.info("Performed a move request");
    cfpClient.move(sessionId, moveRequest);
  }

  @PostMapping("/gamesession/{sessionId}/giveUp")
  public void giveUp(
      @PathVariable String sessionId,
      @RequestBody GiveUpRequest giveupRequest) { // Make Request Object in Client
    LOG.info("Performed a giveUp request");
    cfpClient.giveUp(sessionId, giveupRequest);
  }

  @GetMapping("/gamesession/{sessionId}")
  public GameSessionResponse getSession(@PathVariable String sessionId) {
    LOG.info("Performed a getSession request");
    return cfpClient.getSession(sessionId);
  }

  @GetMapping("/gamesession/{sessionId}/state")
  public GameState getState(@PathVariable String sessionId) {
    LOG.info("Performed getState request");
    return cfpClient.getState(sessionId);
  }

  @DeleteMapping("/gamesession/{sessionId}")
  public void deleteSession(@PathVariable String sessionId) {
    LOG.info("Performed a deleteSession request");
    cfpClient.deleteSession(sessionId);
  }
}
