package org.ctf.client.controller;

import org.ctf.client.state.data.wrappers.GameSessionResponse;
import org.ctf.client.state.data.wrappers.JoinGameResponse;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.data.map.MapTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import reactor.core.publisher.Mono;

/**
 * V2 of Communication Service which uses Reactive calls to get Data from the RESTApi
 * the game server
 * 
 * @author rsyed
 * @return Layer Object
 */
@Service
public class ClientController {

  /*   private final WebClient webClient;

    public ClientController(final WebClient webClient){    //Assigns a WebClient to the class // WEBCLIENT HAS TO BE CREATED ELSEWHERE AND THEN PASSED ONTO THIS CLASS
        this.webClient = webClient;
    } */
    
    //@Override
   /*  public Mono<GameSessionResponse> createGameSession(String URL, MapTemplate map) {
    return webClient
        .post()
        .uri("/api/gameSession")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(map)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError, response -> new URLError("Check URL:"))
        .onStatus(HttpStatus::is5xxServerError, response -> new URLError("Check URL:"))
        .bodyToMono(GameSessionResponse.class);
    }

    public Mono<JoinGameResponse> joinGame(String sessionId, String teamName) {
        final ParameterizedTypeReference<JoinGameResponse> joinGameResponse =
            new ParameterizedTypeReference<JoinGameResponse>() {
            };
        
        return webClient.get()
        .uri("/api/gameSession/"+sessionId+"/join")
        .retrieve()
        .bodyToMono(joinGameResponse);
    } */

    public void makeMove(String URL, String teamID, String teamSecret, Move move) {

    }

    public void giveUp(String URL, String teamID, String teamSecret) {
    
    }

    public Mono<GameSessionResponse> getCurrentSessionState(String URL) {

        return null;
    }

    public void deleteCurrentSession(String URL) {

    }

    public Mono<GameState> getCurrentGameState(String URL) {
        return null;
    }


}
