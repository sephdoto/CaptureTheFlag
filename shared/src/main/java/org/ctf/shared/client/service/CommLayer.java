package org.ctf.shared.client.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.blackbird.BlackbirdModule;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.data.exceptions.ForbiddenMove;
import org.ctf.shared.state.data.exceptions.GameOver;
import org.ctf.shared.state.data.exceptions.InvalidMove;
import org.ctf.shared.state.data.exceptions.NoMoreTeamSlots;
import org.ctf.shared.state.data.exceptions.SessionNotFound;
import org.ctf.shared.state.data.exceptions.URLError;
import org.ctf.shared.state.data.exceptions.UnknownError;
import org.ctf.shared.state.dto.GameSessionRequest;
import org.ctf.shared.state.dto.GameSessionResponse;
import org.ctf.shared.state.dto.GiveupRequest;
import org.ctf.shared.state.dto.JoinGameRequest;
import org.ctf.shared.state.dto.JoinGameResponse;
import org.ctf.shared.state.dto.MoveRequest;

/**
 * A lightweight Java based Communication Layer which can be used to make calls to and get data from
 * the REST Api Server. Translates returned HTTP codes into exceptions.
 *
 * @author rsyed
 * @return Layer Object which performs the role of a Service
 */
public class CommLayer implements CommLayerInterface {

  private ObjectMapper mapper;
  private ExecutorService executor;
  private HttpClient client;
  private HttpRequest request;
  private HttpResponse<String> ret;

  /**
   * Class constructor. Uses a SingleThreadExecutor to insure that the commands are done
   * sequentially to minimize chances of data inconsistency.
   *
   * @author rsyed
   */
  public CommLayer() {
    mapper = new ObjectMapper();
    mapper.registerModule(new BlackbirdModule());
    executor = Executors.newSingleThreadExecutor();
    client =
        HttpClient.newBuilder()
            .followRedirects(Redirect.ALWAYS)
            .connectTimeout(Duration.ofSeconds(5))
            .executor(executor)
            .build();
  }

  /**
   * Requests the server specified in the URL parameter to create a GameSession using the map in the
   * MapTemplate parameter. Returns the server reponse as well as HTTP status codes thrown as
   * exceptions Example URL "http://localhost:9999/api/gamesession"
   *
   * @param URL
   * @param map
   * @returns GameSessionResponse
   * @throws URLError (404)
   * @throws UnknownError (500)
   * @author rsyed
   */
  @Override
  public GameSessionResponse createGameSession(String URL, GameSessionRequest gsr) {
    final HttpRequest request;
    HttpResponse<String> response;
    try {
      request =
          HttpRequest.newBuilder()
              .uri(new URI(URL))
              .header("Content-Type", "application/json")
              .POST(BodyPublishers.ofString(mapper.writeValueAsString(gsr)))
              .build();
      response = client.send(request, BodyHandlers.ofString());
    } catch (URISyntaxException | IOException | InterruptedException | NullPointerException e) {
      throw new URLError("Check URL");
    }
    if (response.statusCode() != 200) {
      if (response.statusCode() == 404) {
        throw new UnknownError();
      } else if (response.statusCode() == 500) {
        throw new URLError("URL Error");
      }
    }
    try {
      return mapper.readValue(response.body(), GameSessionResponse.class);
    } catch (JsonProcessingException e) {
      throw new UnknownError("Jackson Errored out");
    }
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
   * @author rsyed
   */
  @Override
  public JoinGameResponse joinGame(String URL, String teamName) {
    final HttpRequest request;
    HttpResponse<String> response;
    JoinGameRequest joinGameRequest = new JoinGameRequest();
    joinGameRequest.setTeamId(teamName);
    try {
      request =
          HttpRequest.newBuilder()
              .uri(new URI(URL + "/join"))
              .header("Content-Type", "application/json")
              .POST(BodyPublishers.ofString(mapper.writeValueAsString(joinGameRequest)))
              .build();
      response = client.send(request, BodyHandlers.ofString());
    } catch (URISyntaxException | IOException | InterruptedException | NullPointerException e) {
      throw new URLError("Check URL");
    }
    if (response.statusCode() != 200) {
      if (response.statusCode() == 404) {
        throw new SessionNotFound();
      } else if (response.statusCode() == 429) {
        throw new NoMoreTeamSlots();
      } else if (response.statusCode() == 500) {
        throw new UnknownError();
      }
    }

    try {
      return mapper.readValue(response.body(), JoinGameResponse.class);
    } catch (JsonProcessingException e) {
      throw new UnknownError("Jackson Errored out");
    }
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
   * @author rsyed
   */
  @Override
  public void makeMove(String URL, MoveRequest moveReq) {
    final HttpRequest request;
    HttpResponse<String> response;

    try {
      request =
          HttpRequest.newBuilder()
              .uri(new URI(URL + "/move"))
              .header("Content-Type", "application/json")
              .POST(BodyPublishers.ofString(mapper.writeValueAsString(moveReq)))
              .build();
      response = client.send(request, BodyHandlers.ofString());
    } catch (URISyntaxException | IOException | InterruptedException | NullPointerException e) {
      throw new URLError("Check URL");
    }
    if (response.statusCode() != 200) {
      if (response.statusCode() == 403) {
        throw new ForbiddenMove();
      } else if (response.statusCode() == 404) {
        throw new SessionNotFound();
      } else if (response.statusCode() == 409) {
        throw new InvalidMove();
      } else if (response.statusCode() == 410) {
        throw new GameOver();
      } else if (response.statusCode() == 500) {
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
   * @author rsyed
   */
  @Override
  public void giveUp(String URL, String teamID, String teamSecret) {
    final HttpRequest request;
    HttpResponse<String> response;
    GiveupRequest giveUpRequest = new GiveupRequest();
    giveUpRequest.setTeamId(teamID);
    giveUpRequest.setTeamSecret(teamSecret);
    try {
      request =
          HttpRequest.newBuilder()
              .uri(new URI(URL + "/giveup"))
              .header("Content-Type", "application/json")
              .POST(BodyPublishers.ofString(mapper.writeValueAsString(giveUpRequest)))
              .build();
      response = client.send(request, BodyHandlers.ofString());
    } catch (URISyntaxException | IOException | InterruptedException | NullPointerException e) {
      throw new URLError("Check URL");
    }
    if (response.statusCode() != 200) {
      if (response.statusCode() == 403) {
        throw new ForbiddenMove();
      } else if (response.statusCode() == 404) {
        throw new SessionNotFound();
      } else if (response.statusCode() == 410) {
        throw new GameOver();
      } else if (response.statusCode() == 500) {
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
   * @author rsyed
   */
  @Override
  public GameSessionResponse getCurrentSessionState(String URL) {
    final HttpRequest request;
    HttpResponse<String> response;
    try {
      request =
          HttpRequest.newBuilder()
              .uri(new URI(URL))
              .header("Content-Type", "application/json")
              .GET()
              .build();
      response = client.send(request, BodyHandlers.ofString());
    } catch (URISyntaxException | IOException | InterruptedException | NullPointerException e) {
      throw new URLError("Check URL");
    }

    if (response.statusCode() != 200) {
      if (response.statusCode() == 404) {
        throw new SessionNotFound();
      } else if (response.statusCode() == 500) {
        throw new UnknownError();
      }
    }
    try {
      return mapper.readValue(response.body(), GameSessionResponse.class);
    } catch (JsonProcessingException e) {
      throw new UnknownError("Jackson Errored out");
    }
  }

  /**
   * Requests the server to delete the current session which is specified in the URL. Returns the
   * server reponse which are HTTP status codes thrown as exceptions.
   *
   * @param URL
   * @throws SessionNotFound (404)
   * @throws UnknownError (500)
   * @throws URLError (404)
   * @author rsyed
   */
  @Override
  public void deleteCurrentSession(String URL) {
    final HttpRequest request;
    HttpResponse<String> response;
    try {
      request = HttpRequest.newBuilder().uri(new URI(URL)).DELETE().build();
      response = client.send(request, BodyHandlers.ofString());
    } catch (URISyntaxException | IOException | InterruptedException | NullPointerException e) {
      throw new URLError("Check URL");
    }

    if (response.statusCode() != 200) {
      if (response.statusCode() == 404) {
        throw new SessionNotFound();
      } else if (response.statusCode() == 500) {
        throw new UnknownError();
      }
    }
  }

  /**
   * Requests the server to delete the current session which is specified in the URL. Returns the
   * server reponse which are HTTP status codes thrown as exceptions.
   *
   * @param URL
   * @throws SessionNotFound (404)
   * @throws UnknownError (500)
   * @throws URLError (404)
   * @author rsyed
   */
  @Override
  public GameState getCurrentGameState(String URL) {
    final HttpRequest request;
    HttpResponse<String> response;
    try {
      request =
          HttpRequest.newBuilder()
              .uri(new URI(URL + "/state"))
              .header("Content-Type", "application/json")
              .GET()
              .build();
      response = client.send(request, BodyHandlers.ofString());
    } catch (URISyntaxException | IOException | InterruptedException | NullPointerException e) {
      throw new URLError("Check URL");
    }

    if (response.statusCode() != 200) {
      if (response.statusCode() == 404) {
        throw new SessionNotFound();
      } else if (response.statusCode() == 500) {
        throw new UnknownError();
      }
    }

    try {
      return mapper.readValue(response.body(), GameState.class);
    } catch (JsonProcessingException e) {
      throw new UnknownError("Jackson Errored out");
    }
  }

  /*
   * Helper method to perform a POST HTTP Request
   * @param URL
   * @param jsonPayload String Representation of a json
   * @return HttpResponse<String>
   * @throws URLError
   */
  @Deprecated
  private GameSessionResponse createGameSessionRequester(String URL, String jsonPayload) {
    HttpResponse<String> response;
    try {
      final HttpRequest request =
          HttpRequest.newBuilder()
              .uri(new URI(URL))
              .header("Content-Type", "application/json")
              .POST(BodyPublishers.ofString(jsonPayload))
              .build();
      response = client.send(request, BodyHandlers.ofString());
    } catch (URISyntaxException | IOException | InterruptedException | NullPointerException e) {
      throw new URLError("Check URL");
    }
    if (response.statusCode() != 200) {
      if (response.statusCode() == 500) {
        throw new UnknownError();
      } else if (response.statusCode() == 404) {
        throw new URLError("URL Error");
      }
    }
    try {
      return mapper.readValue(response.body(), GameSessionResponse.class);
    } catch (JsonProcessingException e) {
      throw new UnknownError("Jackson Errored out");
    }
  }

  /**
   * Helper method which takes a URL and performs a GET HTTP request and returns the response
   *
   * @param URL
   * @return HttpResponse<String>
   * @throws URLError
   */
  @Deprecated
  private HttpResponse<String> GETRequest(String URL)
      throws URISyntaxException, IOException, InterruptedException {
    try {
      request =
          HttpRequest.newBuilder()
              .uri(new URI(URL))
              .header("Content-Type", "application/json")
              .GET()
              .build();
      ret = client.send(request, BodyHandlers.ofString());
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
  @Deprecated
  private HttpResponse<String> DELETERequest(String URL)
      throws URISyntaxException, IOException, InterruptedException {
    try {
      request = HttpRequest.newBuilder().uri(new URI(URL)).DELETE().build();
      ret = client.send(request, BodyHandlers.ofString());
    } catch (URISyntaxException | IOException | InterruptedException | NullPointerException e) {
      throw new URLError("Check URL");
    }
    return ret;
  }
}
