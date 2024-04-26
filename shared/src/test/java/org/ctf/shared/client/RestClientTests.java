package org.ctf.shared.client;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import de.unimannheim.swt.pse.ctf.CtfApplication;
import java.io.IOException;
import org.ctf.shared.ai.AI_Controller;
import org.ctf.shared.ai.AI_Tools.InvalidShapeException;
import org.ctf.shared.ai.AI_Tools.NoMovesLeftException;
import org.ctf.shared.client.service.RestClientLayer;
import org.ctf.shared.constants.Constants.AI;
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
public class RestClientTests {

  static RestClientLayer comm = new RestClientLayer();
  static Client javaClient;
  static Client javaClient2;
  final MapTemplate template = createGameTemplate();

  @BeforeAll
  static void setup() {
    String[] args = new String[] {};
    CtfApplication.main(args);
    javaClient =
        ClientStepBuilder.newBuilder()
            .enableRestLayer(true)
            .onLocalHost()
            .onPort("8888")
            .enableSaveGame(false)
            .build();
  }

  @BeforeEach
  void setupBeforeEach() {
    javaClient =
        ClientStepBuilder.newBuilder()
            .enableRestLayer(true)
            .onLocalHost()
            .onPort("8888")
            .enableSaveGame(false)
            .build();
    javaClient2 =
        ClientStepBuilder.newBuilder()
            .enableRestLayer(true)
            .onLocalHost()
            .onPort("8888")
            .enableSaveGame(false)
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
        "localhost", "8080", javaClient.getCurrentGameSessionID(), "Team2");
    javaClient.pullData();
    javaClient2.pullData();
    assertNotNull(javaClient.getEndDate());
  }

  @Test
  void testGetGrid() {
    javaClient.createGame(template);
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "8080", javaClient.getCurrentGameSessionID(), "Team2");
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
        "localhost", "8080", javaClient.getCurrentGameSessionID(), "Team2");
    javaClient.pullData();
    javaClient2.pullData();
    AI_Controller Controller = new AI_Controller(javaClient.getCurrentState(), AI.MCTS);
    AI_Controller Controller2 = new AI_Controller(javaClient2.getCurrentState(), AI.MCTS);
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
        "localhost", "8080", javaClient.getCurrentGameSessionID(), "Team2");
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
        "localhost", "8080", javaClient.getCurrentGameSessionID(), "Team2");
    try {
      javaClient.getSessionFromServer();
      javaClient.getStateFromServer();
      javaClient2.getSessionFromServer();
      javaClient2.getStateFromServer();
    } catch (Exception e) {
      System.out.println("Updated");
    }
    assertNotNull(javaClient.startDate);
  }

  @Test
  void testGetStateFromServer() {
    javaClient.createGame(template);
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "8080", javaClient.getCurrentGameSessionID(), "Team2");
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
        "localhost", "8080", javaClient.getCurrentGameSessionID(), "Team2");
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
        "localhost", "8080", javaClient.getCurrentGameSessionID(), "Team2");
    javaClient.getStateFromServer();
    assertNotNull(javaClient.getTeams());
  }

  @Test
  void testGetWinners() {
    javaClient.createGame(template);
    template.setTotalTimeLimitInSeconds(1);
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "8080", javaClient.getCurrentGameSessionID(), "Team2");
    javaClient.pullData();
    javaClient2.pullData();
    assertNotNull(javaClient.getWinners());
  }

  @Test
  void testGiveUp() {
    javaClient.createGame(template);
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "8888", javaClient.getCurrentGameSessionID(), "Team2");
    javaClient.pullData();
    javaClient2.pullData();
    Gson gson = new Gson();
    System.out.println(gson.toJson(javaClient.getCurrentState()));
    try {
      if (javaClient2.isItMyTurn()) {
        javaClient2.giveUp();
      } else {
        javaClient.giveUp();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } 
      javaClient.pullData();
      javaClient2.pullData();

  
      System.out.println(gson.toJson(javaClient.getCurrentState()));
  }   


  @Test
  void testIsGameOver() {
    javaClient.createGame(template);
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "8080", javaClient.getCurrentGameSessionID(), "Team2");
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
        "localhost", "8080", javaClient.getCurrentGameSessionID(), "Team2");
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
      javaClient.setServer("localhost", "8080");
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
