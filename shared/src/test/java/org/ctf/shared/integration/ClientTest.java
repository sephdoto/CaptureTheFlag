package org.ctf.shared.integration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unimannheim.swt.pse.ctf.CtfApplication;
import java.io.IOException;
import org.ctf.shared.ai.AIConfig;
import org.ctf.shared.ai.AIController;
import org.ctf.shared.ai.GameUtilities.InvalidShapeException;
import org.ctf.shared.ai.GameUtilities.NoMovesLeftException;
import org.ctf.shared.client.Client;
import org.ctf.shared.client.ClientStepBuilder;
import org.ctf.shared.client.lib.ServerDetails;
import org.ctf.shared.client.lib.ServerManager;
import org.ctf.shared.client.service.CommLayer;
import org.ctf.shared.constants.Enums.AI;
import org.ctf.shared.state.data.exceptions.SessionNotFound;
import org.ctf.shared.state.data.exceptions.URLError;
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
class ClientTest {

  static CommLayer comm = new CommLayer();
  static Client javaClient;
  static Client javaClient2;
  final MapTemplate template = createGameTemplate();
  ServerManager server = new ServerManager(comm, new ServerDetails("localhost", "9998"), template);

  @BeforeAll
  static void setup() {
    String[] args = new String[] {"--server.port=9998"};
    CtfApplication.main(args);
    javaClient =
        ClientStepBuilder.newBuilder()
            .enableRestLayer(false)
            .onLocalHost()
            .onPort("9998")
            .enableSaveGame(false)
            .disableAutoJoin()
            .build();
  }

  @BeforeEach
  void setupBeforeEach() {
    javaClient =
        ClientStepBuilder.newBuilder()
            .enableRestLayer(false)
            .onLocalHost()
            .onPort("9998")
            .enableSaveGame(false)
            .disableAutoJoin()
            .build();
    javaClient2 =
        ClientStepBuilder.newBuilder()
            .enableRestLayer(false)
            .onLocalHost()
            .onPort("9998")
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
        "localhost", "9998", javaClient.getCurrentGameSessionID(), "Team2");
    assertNotNull(javaClient.getCurrentTeamTurn());
  }

  @Test
  void testGetEndDate() {
    template.setTotalTimeLimitInSeconds(12);
    javaClient.createGame(template);
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "9998", javaClient.getCurrentGameSessionID(), "Team2");
    try {
      if (javaClient.getCurrentTeamTurn() == 1) {
        javaClient.giveUp();
      } else {
        javaClient2.giveUp();
      }
    } catch (Exception e) {
      System.out.println("Accepted");
    }
    try {
      javaClient.getSessionFromServer();
    } catch (Exception e) {
      System.out.println("Accepted");
    }
    assertNotNull(javaClient.getEndDate());
  }

  @Test
  void testGetGrid() {
    javaClient.createGame(template);
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "9998", javaClient.getCurrentGameSessionID(), "Team2");
    javaClient.getStateFromServer();
    javaClient2.getStateFromServer();
    assertNotNull(javaClient.getGrid());
  }

  @Test
  void testGetLastMove() {
    javaClient.createGame(template);
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "9998", javaClient.getCurrentGameSessionID(), "Team2");
    javaClient.pullData();
    javaClient2.pullData();
    AIController Controller =
        new AIController(javaClient.getCurrentState(), AI.MCTS, new AIConfig(), 0);
    AIController Controller2 =
        new AIController(javaClient2.getCurrentState(), AI.MCTS, new AIConfig(), 0);
    try {
      if (javaClient.isItMyTurn()) {
        javaClient.makeMove(Controller.getNextMove());
      } else {
        javaClient2.makeMove(Controller2.getNextMove());
      }
      javaClient.pullData();
      javaClient2.pullData();
    } catch (NoMovesLeftException | InvalidShapeException e) {
      fail();
    }
    assertNotNull(javaClient.getLastMove());
    assertNotNull(javaClient2.getLastMove());
  }

  @Test
  void testGetSessionFromServer() {
    javaClient.createGame(template);
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "9998", javaClient.getCurrentGameSessionID(), "Team2");
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
        "localhost", "9998", javaClient.getCurrentGameSessionID(), "Team2");
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
        "localhost", "9998", javaClient.getCurrentGameSessionID(), "Team2");
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
        "localhost", "9998", javaClient.getCurrentGameSessionID(), "Team2");
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
    javaClient.createGame(template);
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "9998", javaClient.getCurrentGameSessionID(), "Team2");
    javaClient.getStateFromServer();
    assertNotNull(javaClient.getTeams());
  }

  @Test
  void testGetWinners() {
    javaClient.createGame(template);
    template.setTotalTimeLimitInSeconds(1);
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "9998", javaClient.getCurrentGameSessionID(), "Team2");
    javaClient.pullData();
    javaClient2.pullData();
    assertNotNull(javaClient.getWinners());
  }

  @Test
  void testGiveUp() {
    javaClient.createGame(template);
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "9998", javaClient.getCurrentGameSessionID(), "Team2");
    if (javaClient.isItMyTurn()) {
      javaClient.giveUp();
    }
  }

  @Test
  void testIsGameOver() {
    javaClient.createGame(template);
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "9998", javaClient.getCurrentGameSessionID(), "Team2");
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
        "localhost", "9998", javaClient.getCurrentGameSessionID(), "Team2");
    assertNotNull(javaClient2.getTeamSecret());
  }

  @Test
  void testJoinGame() {
    javaClient.createGame(template);
    javaClient.joinGame("Team1");
    assertNotNull(javaClient.getTeamSecret());
  }

  @Test
  void testMakeMove() {
    javaClient.createGame(template);
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "9998", javaClient.getCurrentGameSessionID(), "Team2");
    javaClient.pullData();
    javaClient2.pullData();
    AIController Controller =
        new AIController(javaClient.getCurrentState(), AI.MCTS, new AIConfig(), 0);
    AIController Controller2 =
        new AIController(javaClient2.getCurrentState(), AI.MCTS, new AIConfig(), 0);
    try {
      if (javaClient.isItMyTurn()) {

        javaClient.makeMove(Controller.getNextMove());
      } else {
        javaClient2.makeMove(Controller2.getNextMove());
      }

    } catch (Exception e) {
      fail();
    }
    javaClient.getStateFromServer();
    javaClient2.getStateFromServer();
    assertNotNull(javaClient.getLastMove());
    assertNotNull(javaClient2.getLastMove());
  }

  @Test
  void testSetServer() {
    try {
      javaClient.setServer("localhost", "9998");
      javaClient.createGame(template);
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

    return mapTemplate;
  }
}
