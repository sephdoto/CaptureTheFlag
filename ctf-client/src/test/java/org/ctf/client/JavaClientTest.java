package org.ctf.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unimannheim.swt.pse.ctf.CtfApplication;
import java.io.IOException;
import org.ctf.client.service.CommLayer;
import org.ctf.shared.state.data.exceptions.Accepted;
import org.ctf.shared.state.data.map.MapTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests to test the communication capabilities and Exception catching of the CommLayer service
 * file. Auto starts a server and runs tests
 *
 * @author rsyed
 */
public class JavaClientTest {

  static CommLayer comm = new CommLayer();
  static JavaClient javaClient;
  final MapTemplate template = createGameTemplate();
  ;

  @BeforeAll
  static void setup() {
    String[] args = new String[] {};
    CtfApplication.main(args);
    javaClient = new JavaClient("localhost", "9999");
  }

  @BeforeEach
  void setupBeforeEach() {
    javaClient = new JavaClient("localhost", "9999");
  }

  @Test
  void testCreateGame() {
    javaClient.createGame(template);
    assertNotNull(javaClient.getCurrentGameSessionID());
  }

  @Test
  void testDeleteSession() {
    javaClient.createGame(template);
    Throwable throwable =
        assertThrows(
            Accepted.class,
            () -> {
              javaClient.deleteSession();
            });
    assertEquals(Accepted.class, throwable.getClass());
  }

  @Test
  void testGetCurrentGameSessionID() {
    javaClient.createGame(template);
    assertNotNull(javaClient.getCurrentGameSessionID());
  }

  @Test
  void testGetCurrentServer() {
    javaClient.createGame(template);
    assertNotNull(javaClient.getCurrentServer());
  }

  @Test
  void testGetCurrentSession() {
    javaClient.createGame(template);
    assertNotNull(javaClient.getCurrentSession());
  }

  @Test
  void testGetCurrentState() {
    javaClient.createGame(template);
    assertNotNull(javaClient.getCurrentState());
  }

  @Test
  void testGetCurrentTeamTurn() {
    javaClient.createGame(template);
    javaClient.joinGame("Team1");
    comm.joinGame(
        "http://localhost:9999/api/gamesession/" + javaClient.getCurrentGameSessionID(),
        "TestTeam2");
    assertNotNull(javaClient.getCurrentTeamTurn());
  }

  @Test
  void testGetEndDate() {

  }

  @Test
  void testGetGrid() {
    javaClient.createGame(template);
    javaClient.getStateFromServer();
    assertNotNull(javaClient.getGrid());
}

  @Test
  void testGetLastMove() {}

  @Test
  void testGetSessionFromServer() {}

  @Test
  void testGetStartDate() {}

  @Test
  void testGetStateFromServer() {}

  @Test
  void testGetTeamColor() {}

  @Test
  void testGetTeamID() {}

  @Test
  void testGetTeamSecret() {}

  @Test
  void testGetTeams() {}

  @Test
  void testGetWinner() {}

  @Test
  void testGetWinners() {}

  @Test
  void testGiveUp() {}

  @Test
  void testIsGameOver() {}

  @Test
  void testJoinExistingGame() {}

  @Test
  void testJoinGame() {}

  @Test
  void testMakeMove() {}

  @Test
  void testSetServer() {}

  private MapTemplate createGameTemplate() {
    ObjectMapper objectMapper = new ObjectMapper();
    MapTemplate mapTemplate = null;
    try {
      mapTemplate =
          objectMapper.readValue(
              getClass().getResourceAsStream("/maptemplates/10x10_2teams_example.json"),
              MapTemplate.class);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return mapTemplate;
  }
}
