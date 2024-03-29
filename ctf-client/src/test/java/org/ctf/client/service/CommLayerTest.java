package org.ctf.client.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unimannheim.swt.pse.ctf.CtfApplication;
import de.unimannheim.swt.pse.ctf.game.exceptions.InvalidMove;

import java.io.IOException;
import org.ctf.client.data.dto.GameSessionResponse;
import org.ctf.client.data.dto.JoinGameResponse;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.data.exceptions.Accepted;
import org.ctf.shared.state.data.map.MapTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
/**
 * Tests to test the communication capabilities and Exception catching of the CommLayer service file. Auto starts a server and runs tests
 *
 * @author rsyed
 */
public class CommLayerTest {
  static CommLayer comm = new CommLayer();

  @BeforeAll
  static void setup() {
    String[] args = new String[] {};
    CtfApplication.main(args);
  }

  @Test
  void testCreateGameSession() {
    MapTemplate template = createGameTemplate();
    GameSessionResponse gameSessionResponse =
        comm.createGameSession("http://localhost:9999/api/gamesession", template);
    assertNotNull(gameSessionResponse.getId());
  }

  @Test
  void testGetCurrentGameState() {
    MapTemplate template = createGameTemplate();
    GameSessionResponse gameSessionResponse =
        comm.createGameSession("http://localhost:9999/api/gamesession", template);
    String idURL = "http://localhost:9999/api/gamesession/" + gameSessionResponse.getId();
    JoinGameResponse jsResponse = comm.joinGame(idURL, "TestTeam1");
    JoinGameResponse jsResponse2 = comm.joinGame(idURL, "TestTeam2");
    assertNotNull(comm.getCurrentGameState(idURL));
  }

  @Test
  void testGetCurrentSessionState() {
    MapTemplate template = createGameTemplate();
    GameSessionResponse gameSessionResponse =
        comm.createGameSession("http://localhost:9999/api/gamesession", template);
    String idURL = "http://localhost:9999/api/gamesession/" + gameSessionResponse.getId();
    
    assertNotNull(comm.getCurrentSessionState(idURL));
  }

  @Test
  void testJoinGame() {
    MapTemplate template = createGameTemplate();

    GameSessionResponse gameSessionResponse =
        comm.createGameSession("http://localhost:9999/api/gamesession", template);
    String idURL = "http://localhost:9999/api/gamesession/" + gameSessionResponse.getId();
    JoinGameResponse jsResponse = comm.joinGame(idURL, "TestTeam1");
    assertNotNull(jsResponse.getTeamSecret());
  }

  @Test
  void testMakeMove() {
    MapTemplate template = createGameTemplate();

    GameSessionResponse gameSessionResponse =
        comm.createGameSession("http://localhost:9999/api/gamesession", template);
    JoinGameResponse jsResponse =
        comm.joinGame(
            "http://localhost:9999/api/gamesession/" + gameSessionResponse.getId(), "TestTeam1");
    assertNotNull(jsResponse.getTeamSecret());
    JoinGameResponse jsResponse2 =
        comm.joinGame(
            "http://localhost:9999/api/gamesession/" + gameSessionResponse.getId(), "TestTeam2");
    Move move = new Move();
    move.setPieceId("p:1_1");
    move.setNewPosition(new int[] {1, 1});
      try {
        comm.makeMove(
          "http://localhost:9999/api/gamesession/" + gameSessionResponse.getId(),
          jsResponse.getTeamId(),
          jsResponse.getTeamSecret(),
          move);
    }
    catch (Exception ex) {
        assert((ex instanceof Accepted) || (ex instanceof InvalidMove));
    }
  }

  @Test
  void testGiveUp() {
    MapTemplate template = createGameTemplate();

    GameSessionResponse gameSessionResponse =
        comm.createGameSession("http://localhost:9999/api/gamesession", template);
    String idURL = "http://localhost:9999/api/gamesession/" + gameSessionResponse.getId();
    JoinGameResponse jsResponse = comm.joinGame(idURL, "TestTeam1");

    JoinGameResponse jsResponse2 = comm.joinGame(idURL, "TestTeam2");
    GameState gameState = new GameState();
    try {
      gameState = comm.getCurrentGameState(idURL);
    } catch (Exception e) {
      System.out.println("Request Accepted");
    }

    if (gameState.getCurrentTeam() == 0) {
      Throwable throwable =
          assertThrows(
              Accepted.class,
              () -> {
                comm.giveUp(idURL, jsResponse.getTeamId(), jsResponse.getTeamSecret());
              });
      assertEquals(Accepted.class, throwable.getClass());
    }
    if (gameState.getCurrentTeam() == 1) {
      Throwable throwable2 =
          assertThrows(
              Accepted.class,
              () -> {
                comm.giveUp(idURL, jsResponse2.getTeamId(), jsResponse2.getTeamSecret());
              });
      assertEquals(Accepted.class, throwable2.getClass());
    }
  }

  @Test
  void testDeleteCurrentSession() {
    MapTemplate template = createGameTemplate();
    GameSessionResponse gameSessionResponse =
        comm.createGameSession("http://localhost:9999/api/gamesession", template);
          Throwable throwable2 =
        assertThrows(
            Accepted.class,
            () -> {
              comm.deleteCurrentSession("http://localhost:9999/api/gamesession/" + gameSessionResponse.getId());
            });
    assertEquals(Accepted.class, throwable2.getClass());
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
