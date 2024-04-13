package org.ctf.shared.client.service;

import java.net.URISyntaxException;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.data.exceptions.ForbiddenMove;
import org.ctf.shared.state.data.exceptions.GameOver;
import org.ctf.shared.state.data.exceptions.InvalidMove;
import org.ctf.shared.state.data.exceptions.NoMoreTeamSlots;
import org.ctf.shared.state.data.exceptions.SessionNotFound;
import org.ctf.shared.state.data.exceptions.URLError;
import org.ctf.shared.state.dto.GameSessionRequest;
import org.ctf.shared.state.dto.GameSessionResponse;
import org.ctf.shared.state.dto.GiveupRequest;
import org.ctf.shared.state.dto.JoinGameRequest;
import org.ctf.shared.state.dto.JoinGameResponse;
import org.ctf.shared.state.dto.MoveRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

/**
 * A RestClient based Communication Layer which can be used to make calls to and get data from the
 * REST Api Server. Translates returned HTTP codes into exceptions.
 *
 * @author rsyed
 * @return Layer Object which performs the role of a Service
 */
public class RestClientLayer implements CommLayerInterface {
  final RestClient restClient;

  public RestClientLayer() {
    restClient = RestClient.create();
  }

  /**
   * Requests the server specified in the URL parameter to create a GameSession using the map in the
   * MapTemplate parameter. Returns the server reponse as well as HTTP status codes thrown as
   * exceptions Example URL "http://localhost:9999/api/gamesession"
   *
   * @param URL
   * @param map
   * @returns GameSessionResponse
   * @throws UnknownError (500)
   * @throws URLError (404)
   */
  @Override
  public GameSessionResponse createGameSession(String URL, GameSessionRequest gsr) {
    ResponseEntity<GameSessionResponse> result;
    try {
      result = restClient.post().uri(URL).body(gsr).retrieve().toEntity(GameSessionResponse.class);
    } catch (NullPointerException e) {
      throw new URLError("Check URL");
    }
    int code = result.getStatusCode().value();
    if (code != 200) {
      if (code == 404) {
        throw new UnknownError();
      } else if (code == 500) {
        throw new URLError("URL Error");
      }
    }
    return result.getBody();
  }

  /**
   * Makes a request to the server, specified in the URL, to join the game using the specified
   * teamName. Returns the server reponse as well as HTTP status codes thrown as exceptions
   *
   * @param URL "http://localhost:9999/api/gamesession/{sessionID}"
   * @param teamName
   * @return JoinGameResponse
   * @throws URISyntaxException
   * @throws SessionNotFound (404)
   * @throws NoMoreTeamSlots (429)
   * @throws UnknownError (500)
   * @throws URLError (404)
   */
  @Override
  public JoinGameResponse joinGame(String URL, String teamName) {
    JoinGameRequest joinGameRequest = new JoinGameRequest();
    joinGameRequest.setTeamId(teamName);
    ResponseEntity<JoinGameResponse> result;
    try {
      result =
          restClient
              .post()
              .uri(URL + "/join")
              .body(joinGameRequest)
              .retrieve()
              .toEntity(JoinGameResponse.class);
    } catch (NullPointerException e) {
      throw new URLError("Check URL");
    }
    int code = result.getStatusCode().value();
    if (code != 200) {
      if (code == 404) {
        throw new SessionNotFound();
      } else if (code == 429) {
        throw new NoMoreTeamSlots();
      } else if (code == 500) {
        throw new UnknownError();
      }
    }
    return result.getBody();
  }

  /**
   * Method makes a move request to the API using the input parameters. Returns the server reponse
   * as well as HTTP status codes thrown as exceptions
   *
   * @param URL "http://localhost:9999/api/gamesession/{sessionID}"
   * @param teamID
   * @param teamSecret
   * @param move
   * @throws ForbiddenMove (403)
   * @throws SessionNotFound (404)
   * @throws InvalidMove (409)
   * @throws GameOver (410)
   * @throws UnknownError (500)
   * @throws URLError (404)
   */
  @Override
  public void makeMove(String URL, MoveRequest moveReq) {
    ResponseEntity<Void> result;
    try {
      result = restClient.post().uri(URL + "/move").body(moveReq).retrieve().toBodilessEntity();
    } catch (NullPointerException e) {
      throw new URLError("Check URL");
    }
    int code = result.getStatusCode().value();
    if (code != 200) {
      if (code == 403) {
        throw new ForbiddenMove();
      } else if (code == 404) {
        throw new SessionNotFound();
      } else if (code == 409) {
        throw new InvalidMove();
      } else if (code == 410) {
        throw new GameOver();
      } else if (code == 500) {
        throw new UnknownError();
      }
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
   * @throws ForbiddenMove (403)
   * @throws SessionNotFound (404)
   * @throws GameOver (410)
   * @throws UnknownError (500)
   * @throws URLError (404)
   */
  @Override
  public void giveUp(String URL, String teamID, String teamSecret) {
    GiveupRequest giveUpRequest = new GiveupRequest();
    giveUpRequest.setTeamId(teamID);
    giveUpRequest.setTeamSecret(teamSecret);
    ResponseEntity<Void> result;
    try {
      result =
          restClient.post().uri(URL + "/giveup").body(giveUpRequest).retrieve().toBodilessEntity();
    } catch (NullPointerException e) {
      throw new URLError("Check URL");
    }
    int code = result.getStatusCode().value();
    if (code != 200) {
      if (code == 403) {
        throw new ForbiddenMove();
      } else if (code == 404) {
        throw new SessionNotFound();
      } else if (code == 410) {
        throw new GameOver();
      } else if (code == 500) {
        throw new UnknownError();
      }
    }
  }

  /**
   * Requests the server to return the current state of the session, session and server are
   * specified in the input param URL. Returns the server reponse as well as HTTP status codes
   * thrown as exceptions Functions as a refresh command for the URL specified session.
   *
   * @param URL
   * @return GameSessionResponse
   * @throws SessionNotFound (404)
   * @throws UnknownError (500)
   * @throws URLError (404)
   */
  @Override
  public GameSessionResponse getCurrentSessionState(String URL) {
    ResponseEntity<GameSessionResponse> result;
    try {
      result = restClient.get().uri(URL).retrieve().toEntity(GameSessionResponse.class);
    } catch (NullPointerException e) {
      throw new URLError("Check URL");
    }
    int code = result.getStatusCode().value();
    if (code != 200) {
      if (code == 404) {
        throw new SessionNotFound();
      } else if (code == 500) {
        throw new UnknownError();
      }
    }
    return result.getBody();
  }

  /**
   * Requests the server to delete the current session which is specified in the URL. Returns the
   * server reponse which are HTTP status codes thrown as exceptions.
   *
   * @param URL
   * @throws SessionNotFound (404)
   * @throws UnknownError (500)
   * @throws URLError (404)
   */
  @Override
  public void deleteCurrentSession(String URL) {
    ResponseEntity<Void> result;
    try {
      result = restClient.delete().uri(URL).retrieve().toBodilessEntity();
    } catch (NullPointerException e) {
      throw new URLError("Check URL");
    }

    int code = result.getStatusCode().value();
    if (code != 200) {
      if (code == 404) {
        throw new SessionNotFound();
      } else if (code == 500) {
        throw new UnknownError();
      }
    }
  }

  /**
   * Requests the session, specified in the URL, to return its GameState. Returns the GameState
   * object as well as the HTTP status codes which are thrown as exceptions
   *
   * @param URL
   * @return GameState
   * @throws SessionNotFound
   * @throws UnknownError
   * @throws URLError (404)
   */
  @Override
  public GameState getCurrentGameState(String URL) {
    ResponseEntity<GameState> result;
    try {
      result = restClient.get().uri(URL + "/state").retrieve().toEntity(GameState.class);
    } catch (NullPointerException e) {
      throw new URLError("Check URL");
    }
    int code = result.getStatusCode().value();
    if (code != 200) {
      if (code == 404) {
        throw new SessionNotFound();
      } else if (code == 500) {
        throw new UnknownError();
      }
    }
    return result.getBody();
  }
}
