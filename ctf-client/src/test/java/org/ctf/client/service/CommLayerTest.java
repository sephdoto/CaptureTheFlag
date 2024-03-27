package org.ctf.client.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.ctf.client.data.dto.GameSessionResponse;
import org.ctf.client.data.dto.JoinGameResponse;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.data.exceptions.Accepted;
import org.ctf.shared.state.data.map.MapTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class CommLayerTest {
  static CommLayer comm;

  @BeforeAll
  static void setup() {
    comm = new CommLayer();
  }

  @Test
  void testCreateGameSession() throws IOException {
    MapTemplate template = createGameTemplate();
    GameSessionResponse gameSessionResponse =
        comm.createGameSession("http://localhost:8888/api/gamesession", template);
    assertNotNull(gameSessionResponse.getId());
  }

  @Test
  void testGetCurrentGameState() {}

  @Test
  void testGetCurrentSessionState() {}

  @Test
  void testGiveUp() {
    MapTemplate template = createGameTemplate();

    GameSessionResponse gameSessionResponse =
        comm.createGameSession("http://localhost:8888/api/gamesession", template);
    JoinGameResponse jsResponse =
        comm.joinGame(
            "http://localhost:8888/api/gamesession/" + gameSessionResponse.getId(), "TestTeam1");
    assertNotNull(jsResponse.getTeamSecret());
    JoinGameResponse jsResponse2 =
        comm.joinGame(
            "http://localhost:8888/api/gamesession/" + gameSessionResponse.getId(), "TestTeam2");
    GameState gameState = comm.getCurrentGameState("http://localhost:8888/api/gamesession/" + gameSessionResponse.getId());
    System.out.println(gameState.getCurrentTeam());
    if(gameState.getCurrentTeam() == 1){
        Throwable throwable =
        assertThrows(
            Accepted.class,
            () -> {
                comm.giveUp("http://localhost:8888/api/gamesession/" + gameSessionResponse.getId() , jsResponse.getTeamId(), jsResponse.getTeamSecret());
            });
         assertEquals(Accepted.class, throwable.getClass());   
    } else {
        Throwable throwable =
        assertThrows(
            Accepted.class,
            () -> {
                comm.giveUp("http://localhost:8888/api/gamesession/" + gameSessionResponse.getId() , jsResponse2.getTeamId(), jsResponse2.getTeamSecret());
            });
         assertEquals(Accepted.class, throwable.getClass());   
    }
           
  }

  @Test
  void testJoinGame() {
    MapTemplate template = createGameTemplate();

    GameSessionResponse gameSessionResponse =
        comm.createGameSession("http://localhost:8888/api/gamesession", template);
    JoinGameResponse jsResponse =
        comm.joinGame(
            "http://localhost:8888/api/gamesession/" + gameSessionResponse.getId(), "TestTeam1");
    assertNotNull(jsResponse.getTeamSecret());
  }

  @Test
  void testMakeMove() {
    MapTemplate template = createGameTemplate();

    GameSessionResponse gameSessionResponse =
        comm.createGameSession("http://localhost:8888/api/gamesession", template);
    JoinGameResponse jsResponse =
        comm.joinGame(
            "http://localhost:8888/api/gamesession/" + gameSessionResponse.getId(), "TestTeam1");
    assertNotNull(jsResponse.getTeamSecret());
    JoinGameResponse jsResponse2 =
        comm.joinGame(
            "http://localhost:8888/api/gamesession/" + gameSessionResponse.getId(), "TestTeam2");
    Move move = new Move();
    move.setPieceId("p:1_1");
    move.setNewPosition(new int[] {1, 1});

    Throwable throwable =
        assertThrows(
            Accepted.class,
            () -> {
              comm.makeMove(
                  "http://localhost:8888/api/gamesession/" + gameSessionResponse.getId(),
                  jsResponse.getTeamId(),
                  jsResponse.getTeamSecret(),
                  move);
            });
         assertEquals(Accepted.class, throwable.getClass());   
  }

  @Test
  void testDeleteCurrentSession() {
    MapTemplate template = createGameTemplate();
    GameSessionResponse gameSessionResponse =
        comm.createGameSession("http://localhost:8888/api/gamesession", template);
    comm.deleteCurrentSession(
        "http://localhost:8888/api/gamesession/" + gameSessionResponse.getId());
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
