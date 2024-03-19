package org.ctf.client.controller;

import org.ctf.client.state.data.wrappers.GameSessionRequest;
import org.ctf.client.state.data.wrappers.GameSessionResponse;
import org.ctf.client.state.data.wrappers.GiveupRequest;
import org.ctf.client.state.data.wrappers.JoinGameRequest;
import org.ctf.client.state.data.wrappers.JoinGameResponse;
import org.ctf.client.state.data.wrappers.MoveRequest;
import org.ctf.shared.state.GameState;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

public interface ctfHTTPClient {

  @PostExchange("/api/gamesession")
  GameSessionResponse createGameSession(GameSessionRequest gameSessionRequest);

  @PostExchange("/gamesession/{sessionId}/join")
  JoinGameResponse joinGameSession(String sessionId, JoinGameRequest jd);

  @PostExchange("/gamesession/{sessionId}/move")
  void move(String sessionId, MoveRequest moveRequest);

  @PostExchange("/gamesession/{sessionId}/giveUp")
  void giveUp(
      @PathVariable String sessionId,
      @RequestBody GiveupRequest giveupRequest);

  @GetExchange("/gamesession/{sessionId}")
  GameSessionResponse getSession(@PathVariable String sessionId);

  @GetExchange("/gamesession/{sessionId}/state")
  GameState getState(@PathVariable String sessionId);

  @DeleteExchange("/gamesession/{sessionId}")
  void deleteSession(@PathVariable String sessionId);
    
}
