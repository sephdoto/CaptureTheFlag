package org.ctf.client.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.ctf.client.CaptureTheFlagClient;
import org.ctf.client.state.data.wrappers.GameSessionRequest;
import org.ctf.client.state.data.wrappers.GameSessionResponse;
import org.ctf.client.state.data.wrappers.GiveupRequest;
import org.ctf.client.state.data.wrappers.JoinGameRequest;
import org.ctf.client.state.data.wrappers.JoinGameResponse;
import org.ctf.client.state.data.wrappers.MoveRequest;
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

@RestController
@RequestMapping("/api-client")
public class cfpClientController {

  private static final Logger LOG = LoggerFactory.getLogger(cfpClientController.class);

  @Autowired private CaptureTheFlagClient cfpClient = new CaptureTheFlagClient();

  /**
   * Creates a new game session on the server with the given Map packaged as a GameSessionRequest
   *
   * @param request {@link GameSessionRequest}
   * @return unique session ID created
   */
  @Operation(
      summary =
          "Creates a new game session on the server with the given Map packaged as a"
              + " GameSessionRequest")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Game session created"),
        @ApiResponse(responseCode = "500", description = "Unknown error occurred")
      })
  @PostMapping("/gamesession/")
  public GameSessionResponse createGameSession(@RequestBody GameSessionRequest gameSessionRequest) {
    LOG.info("Performed a createGameSession request");
    return cfpClient.createGameSession(gameSessionRequest);
  }

  @Operation(
      summary =
          "Joins the game at the specific sessionId with a teamId packaged in a JoinGameRequest"
              + " object")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Team joined"),
        @ApiResponse(responseCode = "404", description = "Game session not found"),
        @ApiResponse(responseCode = "429", description = "No more team slots available"),
        @ApiResponse(responseCode = "500", description = "Unknown error occurred")
      })
  @PostMapping("/gamesession/{sessionId}/join")
  public JoinGameResponse joinGameSession(
      @PathVariable String sessionId,
      @RequestBody JoinGameRequest jd) { // Make Request Object in Client
    LOG.info("Performed a joinGameSession request");
    return cfpClient.joinGameSession(sessionId, jd);
  }

  @Operation(summary = "makes a move in the session with the data packaged in a MoveRequest object")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Valid move"),
        @ApiResponse(responseCode = "404", description = "Game session not found"),
        @ApiResponse(
            responseCode = "403",
            description = "Move is forbidden for given team (anti-cheat)"),
        @ApiResponse(responseCode = "409", description = "Invalid move"),
        @ApiResponse(responseCode = "410", description = "Game is over"),
        @ApiResponse(responseCode = "500", description = "Unknown error occurred")
      })
  @PostMapping("/gamesession/{sessionId}/move")
  public void move(
      @PathVariable String sessionId,
      @RequestBody MoveRequest moveRequest) { // Make Request Object in Client
    LOG.info("Performed a move request");
    cfpClient.move(sessionId, moveRequest);
  }

  @Operation(
      summary =
          "gives up in the current session specific by the sessionId with the data packaged in a"
              + " giveupRequest object")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Request completed"),
        @ApiResponse(responseCode = "404", description = "Game session not found"),
        @ApiResponse(
            responseCode = "403",
            description = "Give up is forbidden for given team (anti-cheat)"),
        @ApiResponse(responseCode = "410", description = "Game is over"),
        @ApiResponse(responseCode = "500", description = "Unknown error occurred")
      })
  @PostMapping("/gamesession/{sessionId}/giveUp")
  public void giveUp(
      @PathVariable String sessionId,
      @RequestBody GiveupRequest giveupRequest) { // Make Request Object in Client
    LOG.info("Performed a giveUp request");
    cfpClient.giveUp(sessionId, giveupRequest);
  }

  @Operation(summary = "Gets the GameSession from the Server")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Game session response returned"),
        @ApiResponse(responseCode = "404", description = "Game session not found"),
        @ApiResponse(responseCode = "500", description = "Unknown error occurred")
      })
  @GetMapping("/gamesession/{sessionId}")
  public GameSessionResponse getSession(@PathVariable String sessionId) {
    LOG.info("Performed a getSession request");
    return cfpClient.getSession(sessionId);
  }

  @Operation(summary = "gets the current GameState of the specified sessionId")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Game state returned"),
        @ApiResponse(responseCode = "404", description = "Game session not found"),
        @ApiResponse(responseCode = "500", description = "Unknown error occurred")
      })
  @GetMapping("/gamesession/{sessionId}/state")
  public GameState getState(@PathVariable String sessionId) {
    LOG.info("Performed getState request");
    return cfpClient.getState(sessionId);
  }

  @Operation(summary = "deletes the session specified by the sessionId")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Game session removed"),
        @ApiResponse(responseCode = "404", description = "Game session not found"),
        @ApiResponse(responseCode = "500", description = "Unknown error occurred")
      })
  @DeleteMapping("/gamesession/{sessionId}")
  public void deleteSession(@PathVariable String sessionId) {
    LOG.info("Performed a deleteSession request");
    cfpClient.deleteSession(sessionId);
  }
}
