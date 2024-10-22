package org.ctf.shared.integration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.IOException;
import org.ctf.shared.ai.AIConfig;
import org.ctf.shared.ai.AIController;
import org.ctf.shared.ai.GameUtilities.InvalidShapeException;
import org.ctf.shared.ai.GameUtilities.NoMovesLeftException;
import org.ctf.shared.client.Client;
import org.ctf.shared.client.ClientStepBuilder;
import org.ctf.shared.client.service.RestClientLayer;
import org.ctf.shared.constants.Enums.AI;
import org.ctf.shared.state.data.exceptions.Accepted;
import org.ctf.shared.state.data.exceptions.SessionNotFound;
import org.ctf.shared.state.data.exceptions.URLError;
import org.ctf.shared.state.data.map.MapTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.unimannheim.swt.pse.ctf.CtfApplication;

/**
 * Tests to test the communication capabilities and Exception catching of the CommLayer service
 * file. Auto starts a server and runs tests
 *
 * @author rsyed
 */
public class RestClientTests {

  static RestClientLayer comm = new RestClientLayer();
  static Client javaClient;
  static Client javaClient2;
  final MapTemplate template = createGameTemplate();

  @BeforeAll
  static void setup() {
    String[] args = new String[] {"--server.port=9999"};
    CtfApplication.main(args);
    javaClient =
        ClientStepBuilder.newBuilder()
            .enableRestLayer(true)
            .onLocalHost()
            .onPort("9999")
            .enableSaveGame(false)
            .disableAutoJoin()
            .build();
  }

  @BeforeEach
  void setupBeforeEach() {
    javaClient =
        ClientStepBuilder.newBuilder()
            .enableRestLayer(true)
            .onLocalHost()
            .onPort("9999")
            .enableSaveGame(false)
            .disableAutoJoin()
            .build();
    javaClient2 =
        ClientStepBuilder.newBuilder()
            .enableRestLayer(true)
            .onLocalHost()
            .onPort("9999")
            .enableSaveGame(false)
            .disableAutoJoin()
            .build();
  }

  @Test
  void testCreateGame() {
    try {
      javaClient.createGame(template);
    } catch (Exception e) {
      assertNotEquals(SessionNotFound.class, e.getClass());
      assertNotEquals(UnknownError.class, e.getClass());
      assertNotEquals(URLError.class, e.getClass());
    }
    assertNotNull(javaClient.getCurrentGameSessionID());
  }

  @Test
  void testDeleteSession() {
    javaClient.createGame(template);
    try {
      javaClient.deleteSession();
    } catch (Exception e) {
      assertNotEquals(SessionNotFound.class, e.getClass());
      assertNotEquals(UnknownError.class, e.getClass());
      assertNotEquals(URLError.class, e.getClass());
    }
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
    javaClient2.joinExistingGame(
        "localhost", "9999", javaClient.getCurrentGameSessionID(), "Team2");
    assertNotNull(javaClient.getCurrentTeamTurn());
  }

  @Test
  void testGetEndDate() {
    javaClient.createGame(template);
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "9999", javaClient.getCurrentGameSessionID(), "Team2");
    javaClient.pullData();
    javaClient2.pullData();
    assertNotNull(javaClient.getEndDate());
  }

  @Test
  void testGetGrid() {
    javaClient.createGame(template);
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "9999", javaClient.getCurrentGameSessionID(), "Team2");
    javaClient.getStateFromServer();
    javaClient2.getStateFromServer();
    assertNotNull(javaClient.getGrid());
  }

  // Tests MakeMove and getLastMove
  @Test
  void testGetLastMove() {
    javaClient.createGame(template);
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "9999", javaClient.getCurrentGameSessionID(), "Team2");
    javaClient.pullData();
    javaClient2.pullData();
    AIController Controller =
        new AIController(javaClient.getCurrentState(), AI.MCTS, new AIConfig(), 0, false);
    AIController Controller2 =
        new AIController(javaClient2.getCurrentState(), AI.MCTS, new AIConfig(), 0, false);
    try {
      if (javaClient.isItMyTurn()) {

        javaClient.makeMove(Controller.getNextMove());
      } else {
        javaClient2.makeMove(Controller2.getNextMove());
      }

    } catch (NoMovesLeftException | InvalidShapeException e) {
      fail();
    }
    javaClient.getStateFromServer();
    javaClient2.getStateFromServer();
    assertNotNull(javaClient.getLastMove());
    assertNotNull(javaClient2.getLastMove());
  }

  @Test
  void testGetSessionFromServer() {
    javaClient.createGame(template);
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "9999", javaClient.getCurrentGameSessionID(), "Team2");
    try {
      javaClient.getSessionFromServer();
      javaClient2.getSessionFromServer();
    } catch (Exception e) {
      assertNotEquals(SessionNotFound.class, e.getClass());
      assertNotEquals(UnknownError.class, e.getClass());
      assertNotEquals(URLError.class, e.getClass());
    }
  }

  @Test
  void testGetStartDate() {
    javaClient.createGame(template);
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "9999", javaClient.getCurrentGameSessionID(), "Team2");
    try {
      javaClient.getSessionFromServer();
      javaClient.getStateFromServer();
      javaClient2.getSessionFromServer();
      javaClient2.getStateFromServer();
    } catch (Exception e) {
      System.out.println("Updated");
    }
    assertNotNull(javaClient.getStartDate());
  }

  @Test
  void testGetStateFromServer() {
    javaClient.createGame(template);
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "9999", javaClient.getCurrentGameSessionID(), "Team2");
    try {
      javaClient.getSessionFromServer();
      javaClient2.getSessionFromServer();
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void testGetTeamColor() {
    javaClient.createGame(template);
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "9999", javaClient.getCurrentGameSessionID(), "Team2");
    assertNotNull(javaClient.getTeamColor());
  }

  @Test
  void testGetTeamID() {
    javaClient.createGame(template);
    javaClient.joinGame("Team1");
    assertNotNull(javaClient.getTeamID());
  }

  @Test
  void testGetTeamSecret() {
    javaClient.createGame(template);
    javaClient.joinGame("Team1");
    assertNotNull(javaClient.getTeamSecret());
  }

  @Test
  void testGetTeams() {
    try {
      javaClient.createGame(template);
      javaClient.joinGame("Team1");
      javaClient2.joinExistingGame(
          "localhost", "9999", javaClient.getCurrentGameSessionID(), "Team2");
      javaClient.getStateFromServer();

    } catch (Accepted expected) {
    }
    assertNotNull(javaClient.getTeams());
  }

  @Test
  void testGetWinners() {
    javaClient.createGame(template);
    template.setTotalTimeLimitInSeconds(1);
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "9999", javaClient.getCurrentGameSessionID(), "Team2");
    javaClient.pullData();
    javaClient2.pullData();
    assertNotNull(javaClient.getWinners());
  }

  @Test
  void testGiveUp() {
    javaClient.createGame(template);
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "9999", javaClient.getCurrentGameSessionID(), "Team2");
    javaClient.pullData();
    javaClient2.pullData();
    try {
      if (javaClient.isItMyTurn()) {
        javaClient.giveUp();
      }
      if (javaClient2.isItMyTurn()) {
        javaClient2.giveUp();
      }
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  void testIsGameOver() {
    javaClient.createGame(template);
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "9999", javaClient.getCurrentGameSessionID(), "Team2");
    try {
      javaClient.getSessionFromServer();
      javaClient.getStateFromServer();
    } catch (Exception e) {
      fail();
    }
    assertFalse(javaClient.isGameOver());
  }

  @Test
  void testJoinExistingGame() {
    javaClient.createGame(template);
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "9999", javaClient.getCurrentGameSessionID(), "Team2");
    assertNotNull(javaClient2.getTeamSecret());
  }

  @Test
  void testJoinGame() {
    javaClient.createGame(template);
    javaClient.joinGame("Team1");
    assertNotNull(javaClient.getTeamSecret());
  }

  @Test
  void testSetServer() {
    try {
      javaClient.setServer("localhost", "9999");
    } catch (Exception ex) {
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
      e.printStackTrace();
    }
    mapTemplate.setTotalTimeLimitInSeconds(10);
    return mapTemplate;
  }
}
