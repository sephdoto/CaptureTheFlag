package org.ctf.client.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.ctf.client.CaptureTheFlagClient;
import org.ctf.client.state.data.wrappers.GameSessionRequest;
import org.ctf.client.state.data.wrappers.GameSessionResponse;
import org.ctf.client.state.data.wrappers.GiveupRequest;
import org.ctf.client.state.data.wrappers.JoinGameRequest;
import org.ctf.client.state.data.wrappers.JoinGameResponse;
import org.ctf.client.state.data.wrappers.MoveRequest;
import org.ctf.shared.state.GameState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api-client")
public class cfpClientController {

  @Autowired private CaptureTheFlagClient cfpClient;

  @Operation(summary = "Creates a new game session on the server with the given Map packaged as a GameSessionRequest")
  @PostMapping("/gamesession/")
  public GameSessionResponse createGameSession(@RequestBody GameSessionRequest gameSessionRequest) {
    return cfpClient.createGameSession(gameSessionRequest);
  }

  @Operation(summary = "Joins the game at the specific sessionId with a teamId packaged in a JoinGameRequest object")
  @PostMapping("/gamesession/{sessionId}")
  public JoinGameResponse joinGameSession(
    @PathVariable String sessionId, @RequestBody JoinGameRequest jd) { // Make Request Object in Client
    return cfpClient.joinGameSession(sessionId, jd);
  }

  @Operation(summary = "makes a move in the session with the data packaged in a MoveRequest object")
  @PostMapping("/gamesession/{sessionId}")
  public void move(
    @PathVariable String sessionId, @RequestBody MoveRequest moveRequest) { // Make Request Object in Client
    cfpClient.move(sessionId, moveRequest);
  }

  @Operation(summary = "gives up in the current session specific by the sessionId with the data packaged in a giveupRequest object")
  @PostMapping("/gamesession/{sessionId}")
  public void giveUp(
    @PathVariable String sessionId, @RequestBody GiveupRequest giveupRequest) { // Make Request Object in Client
    cfpClient.giveUp(sessionId, giveupRequest);
  }

  @Operation(summary = "Gets the GameSession from the Server")
  @GetMapping("/gamesession/{sessionId}")
  public GameSessionResponse getSession(@PathVariable String sessionId) {
    return cfpClient.getSession(sessionId);
  }

  @Operation(summary = "gets the current GameState of the specified sessionId")
  @GetMapping("/gamesession/{sessionId}")
  public GameState getState(@PathVariable String sessionId) {
    return cfpClient.getState(sessionId);
  }

  @Operation(summary = "deletes the session specified by the sessionId")
  @DeleteMapping("/gamesession/{sessionId}")
  public void deleteSession(@PathVariable String sessionId) {
    cfpClient.deleteSession(sessionId);
  }
}
