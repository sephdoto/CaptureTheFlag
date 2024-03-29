package org.ctf.controller;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

import org.ctf.model.GameSessionRequest;
import org.ctf.model.GameSessionResponse;
import org.ctf.model.GiveUpRequest;
import org.ctf.model.JoinGameRequest;
import org.ctf.model.JoinGameResponse;
import org.ctf.model.MoveRequest;
import org.ctf.shared.state.GameState;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;


// CURRENT INTERFACE TO USE
@HttpExchange(url = "/api/gamesession", accept = "application/json")
public interface cfpServiceClient {

  @PostExchange()
  GameSessionResponse createGameSession(
      @RequestBody GameSessionRequest gameSessionRequest);

  @PostExchange("/{sessionId}/join")
  JoinGameResponse joinGameSession(
      @PathVariable String sessionId, @RequestBody JoinGameRequest jd);

  @PostExchange("/{sessionId}/move")
  void move(@PathVariable String sessionId, @RequestBody MoveRequest moveRequest);

  @PostExchange("/{sessionId}/giveUp")
  void giveUp(@PathVariable String sessionId, @RequestBody GiveUpRequest giveupRequest);

  @GetExchange("/{sessionId}")
  GameSessionResponse getSession(@PathVariable String sessionId);

  @GetExchange("/{sessionId}/state")
  GameState getState(@PathVariable String sessionId);

  @DeleteExchange("/{sessionId}")
  void deleteSession(@PathVariable String sessionId);
}
