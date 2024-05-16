package de.unimannheim.swt.pse.ctf.game.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unimannheim.swt.pse.ctf.CtfApplication;
import java.io.IOException;

import org.ctf.shared.client.service.RestClientLayer;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.data.exceptions.Accepted;
import org.ctf.shared.state.data.exceptions.InvalidMove;
import org.ctf.shared.state.data.map.MapTemplate;
import org.ctf.shared.state.dto.GameSessionRequest;
import org.ctf.shared.state.dto.GameSessionResponse;
import org.ctf.shared.state.dto.JoinGameResponse;
import org.ctf.shared.state.dto.MoveRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Tests to test the communication capabilities and Exception catching of the CommLayer service
 * file. Auto starts a server and runs tests
 *
 * @author rsyed
 */
public class RestClientTest {

  static RestClientLayer comm = new RestClientLayer();

  @BeforeAll
  static void setup() {
    String[] args = new String[] {"--server.port=9997"};
    CtfApplication.main(args);
  }

  @Test
  void testCreateGameSession() {
    MapTemplate template = createGameTemplate();
    GameSessionRequest gSessionRequest = new GameSessionRequest();
    gSessionRequest.setTemplate(template);
    GameSessionResponse gameSessionResponse =
        comm.createGameSession("http://localhost:9997/api/gamesession", gSessionRequest);
    assertNotNull(gameSessionResponse.getId());
  }

  @Test
  void testGetCurrentGameState() {
    MapTemplate template = createGameTemplate();
    GameSessionRequest gSessionRequest = new GameSessionRequest();
    gSessionRequest.setTemplate(template);
    GameSessionResponse gameSessionResponse =
        comm.createGameSession("http://localhost:9997/api/gamesession", gSessionRequest);
    String idURL = "http://localhost:9997/api/gamesession/" + gameSessionResponse.getId();
    JoinGameResponse jsResponse = comm.joinGame(idURL, "TestTeam1");
    JoinGameResponse jsResponse2 = comm.joinGame(idURL, "TestTeam2");
    assertNotNull(comm.getCurrentGameState(idURL));
  }

  @Test
  void testGetCurrentSessionState() {
    MapTemplate template = createGameTemplate();
    GameSessionRequest gSessionRequest = new GameSessionRequest();
    gSessionRequest.setTemplate(template);
    GameSessionResponse gameSessionResponse =
        comm.createGameSession("http://localhost:9997/api/gamesession", gSessionRequest);
    String idURL = "http://localhost:9997/api/gamesession/" + gameSessionResponse.getId();

    assertNotNull(comm.getCurrentSessionState(idURL));
  }

  @Test
  void testJoinGame() {
    MapTemplate template = createGameTemplate();
    GameSessionRequest gSessionRequest = new GameSessionRequest();
    gSessionRequest.setTemplate(template);

    GameSessionResponse gameSessionResponse =
        comm.createGameSession("http://localhost:9997/api/gamesession", gSessionRequest);
    String idURL = "http://localhost:9997/api/gamesession/" + gameSessionResponse.getId();
    JoinGameResponse jsResponse = comm.joinGame(idURL, "TestTeam1");
    assertNotNull(jsResponse.getTeamSecret());
  }

  @Test
  void testMakeMove() {
    MapTemplate template = createGameTemplate();
    GameSessionRequest gSessionRequest = new GameSessionRequest();
    gSessionRequest.setTemplate(template);

    GameSessionResponse gameSessionResponse =
        comm.createGameSession("http://localhost:9997/api/gamesession", gSessionRequest);
    JoinGameResponse jsResponse =
        comm.joinGame(
            "http://localhost:9997/api/gamesession/" + gameSessionResponse.getId(), "TestTeam1");
    assertNotNull(jsResponse.getTeamSecret());
    JoinGameResponse jsResponse2 =
        comm.joinGame(
            "http://localhost:9997/api/gamesession/" + gameSessionResponse.getId(), "TestTeam2");
    MoveRequest movRe = new MoveRequest();
    movRe.setNewPosition(new int[] {1, 1});
    movRe.setPieceId("p:1_1");
    movRe.setTeamId(jsResponse.getTeamId());
    movRe.setTeamSecret(jsResponse.getTeamSecret());
    try {
      comm.makeMove("http://localhost:9999/api/gamesession/" + gameSessionResponse.getId(), movRe);
    } catch (Exception ex) {
      assert (!(ex instanceof Accepted) || (ex instanceof InvalidMove));
    }
  }

  @Test
  void testGiveUp() {
    MapTemplate template = createGameTemplate();
    GameSessionRequest gSessionRequest = new GameSessionRequest();
    gSessionRequest.setTemplate(template);

    GameSessionResponse gameSessionResponse =
        comm.createGameSession("http://localhost:9997/api/gamesession", gSessionRequest);
    String idURL = "http://localhost:9997/api/gamesession/" + gameSessionResponse.getId();
    JoinGameResponse jsResponse = comm.joinGame(idURL, "TestTeam1");

    JoinGameResponse jsResponse2 = comm.joinGame(idURL, "TestTeam2");
    GameState gameState = new GameState();

    gameState = comm.getCurrentGameState(idURL);
    try {
      if (gameState.getCurrentTeam() == 0) {
        comm.giveUp(idURL, jsResponse.getTeamId(), jsResponse.getTeamSecret());
      }
    } catch (Exception ex) {
      assert (!(ex instanceof Accepted));
    }
    try {
      if (gameState.getCurrentTeam() == 1) {
        comm.giveUp(idURL, jsResponse2.getTeamId(), jsResponse2.getTeamSecret());
      }
    } catch (Exception ex) {
      assert (!(ex instanceof Accepted));
    }
  }

  @Test
  void testDeleteCurrentSession() {
    MapTemplate template = createGameTemplate();
    GameSessionRequest gSessionRequest = new GameSessionRequest();
    gSessionRequest.setTemplate(template);

    GameSessionResponse gameSessionResponse =
        comm.createGameSession("http://localhost:9997/api/gamesession", gSessionRequest);
    try {
      comm.deleteCurrentSession(
          "http://localhost:9997/api/gamesession/" + gameSessionResponse.getId());
    } catch (Exception e) {
      fail();
    }
  }

  private MapTemplate createGameTemplate() {
    ObjectMapper objectMapper = new ObjectMapper();
    MapTemplate mapTemplate = null;
    try {
      mapTemplate =
          objectMapper.readValue(
              getClass().getResourceAsStream("/maptemplates/10x10_2teams_example.json"),
              MapTemplate.class);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return mapTemplate;
  }
}
