package org.ctf.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unimannheim.swt.pse.ctf.CtfApplication;
import de.unimannheim.swt.pse.ctf.game.exceptions.InvalidMove;
import java.io.IOException;
import org.ctf.client.service.CommLayer;
import org.ctf.shared.state.Move;
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
  static JavaClient javaClient2;
  final MapTemplate template = createGameTemplate();

  @BeforeAll
  static void setup() {
    String[] args = new String[] {};
    CtfApplication.main(args);
    javaClient = new JavaClient("localhost", "9999");
  }

  @BeforeEach
  void setupBeforeEach() {
    javaClient = new JavaClient("localhost", "9999");
    javaClient2 = new JavaClient("localhost", "9999");
  }

  @Test
  void testCreateGame() {
    Throwable throwable =
        assertThrows(
            Accepted.class,
            () -> {
              javaClient.createGame(template);
            });
    assertEquals(Accepted.class, throwable.getClass());

    assertNotNull(javaClient.getCurrentGameSessionID());
  }

  @Test
  void testDeleteSession() {
    Throwable throwable1 =
        assertThrows(
            Accepted.class,
            () -> {
              javaClient.createGame(template);
            });
    assertEquals(Accepted.class, throwable1.getClass());
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
    Throwable throwable =
        assertThrows(
            Accepted.class,
            () -> {
              javaClient.createGame(template);
            });
    assertEquals(Accepted.class, throwable.getClass());
    assertNotNull(javaClient.getCurrentGameSessionID());
  }

  @Test
  void testGetCurrentServer() {
    Throwable throwable =
        assertThrows(
            Accepted.class,
            () -> {
              javaClient.createGame(template);
            });
    assertEquals(Accepted.class, throwable.getClass());
    assertNotNull(javaClient.getCurrentServer());
  }

  @Test
  void testGetCurrentSession() {
    Throwable throwable =
        assertThrows(
            Accepted.class,
            () -> {
              javaClient.createGame(template);
            });
    assertEquals(Accepted.class, throwable.getClass());
    assertNotNull(javaClient.getCurrentSession());
  }

  @Test
  void testGetCurrentState() {
    Throwable throwable =
        assertThrows(
            Accepted.class,
            () -> {
              javaClient.createGame(template);
            });
    assertEquals(Accepted.class, throwable.getClass());
    assertNotNull(javaClient.getCurrentState());
  }

  @Test
  void testGetCurrentTeamTurn() {
    Throwable throwable =
        assertThrows(
            Accepted.class,
            () -> {
              javaClient.createGame(template);
            });
    assertEquals(Accepted.class, throwable.getClass());
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "9999", javaClient.getCurrentGameSessionID(), "Team2");
    assertNotNull(javaClient.getCurrentTeamTurn());
  }

  @Test
  void testGetEndDate() {
    try {
      javaClient.createGame(template);
    } catch (Exception e) {
      System.out.println("Accepted");
    } finally {
      javaClient.joinGame("Team1");
      javaClient2.joinExistingGame(
          "localhost", "8888", javaClient.getCurrentGameSessionID(), "Team2");
    }
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
    Throwable throwable =
        assertThrows(
            Accepted.class,
            () -> {
              javaClient.createGame(template);
            });
    assertEquals(Accepted.class, throwable.getClass());
    javaClient.getStateFromServer();
    assertNotNull(javaClient.getGrid());
  }

  // TODO FIX THIS BUG WITH THE LASTMOVE BEING NULL. This shit is ServerSided
  // @Test
  void testGetLastMove() {
    Throwable throwable =
        assertThrows(
            Accepted.class,
            () -> {
              javaClient.createGame(template);
            });
    assertEquals(Accepted.class, throwable.getClass());
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "9999", javaClient.getCurrentGameSessionID(), "Team2");
    Move move = new Move();
    if (javaClient.getCurrentTeamTurn() == 1) {
      try {
        move.setPieceId("p:1_1");
        move.setNewPosition(new int[] {1, 1});
        javaClient.makeMove(move);
      } catch (Exception e) {
        System.out.println("Made move");
      }
    } else {
      try {
        move.setPieceId("p:0_1");
        move.setNewPosition(new int[] {9, 1});
        javaClient2.makeMove(move);
      } catch (Exception e) {
        System.out.println("Made move");
      }
    }
    javaClient.getStateFromServer();
    javaClient2.getStateFromServer();
    assertNotNull(javaClient.getLastMove());
  }

  @Test
  void testGetSessionFromServer() {
    Throwable throwable =
        assertThrows(
            Accepted.class,
            () -> {
              javaClient.createGame(template);
            });
    assertEquals(Accepted.class, throwable.getClass());
    Throwable throwable1 =
        assertThrows(
            Accepted.class,
            () -> {
              javaClient.getSessionFromServer();
            });
    assertEquals(Accepted.class, throwable1.getClass());
  }

  @Test
  void testGetStartDate() {
    Throwable throwable =
        assertThrows(
            Accepted.class,
            () -> {
              javaClient.createGame(template);
            });
    assertEquals(Accepted.class, throwable.getClass());
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
    assertNotNull(javaClient.startDate);
  }

  @Test
  void testGetStateFromServer() {
    Throwable throwable =
        assertThrows(
            Accepted.class,
            () -> {
              javaClient.createGame(template);
            });
    assertEquals(Accepted.class, throwable.getClass());
    Throwable throwable1 =
        assertThrows(
            Accepted.class,
            () -> {
              javaClient.getSessionFromServer();
            });
    assertEquals(Accepted.class, throwable1.getClass());
  }

  @Test
  void testGetTeamColor() {
    Throwable throwable =
        assertThrows(
            Accepted.class,
            () -> {
              javaClient.createGame(template);
            });
    assertEquals(Accepted.class, throwable.getClass());
    javaClient.joinGame("Team1");
    assertNotNull(javaClient.getTeamColor());
  }

  @Test
  void testGetTeamID() {
    Throwable throwable =
        assertThrows(
            Accepted.class,
            () -> {
              javaClient.createGame(template);
            });
    assertEquals(Accepted.class, throwable.getClass());
    javaClient.joinGame("Team1");
    assertNotNull(javaClient.getTeamID());
  }

  @Test
  void testGetTeamSecret() {
    Throwable throwable =
        assertThrows(
            Accepted.class,
            () -> {
              javaClient.createGame(template);
            });
    assertEquals(Accepted.class, throwable.getClass());
    javaClient.joinGame("Team1");
    assertNotNull(javaClient.getTeamSecret());
  }

  @Test
  void testGetTeams() {
    Throwable throwable =
        assertThrows(
            Accepted.class,
            () -> {
              javaClient.createGame(template);
            });
    assertEquals(Accepted.class, throwable.getClass());
    javaClient.getStateFromServer();
    assertNotNull(javaClient.getTeams());
  }

  @Test
  void testGetWinners() {
    Throwable throwable =
        assertThrows(
            Accepted.class,
            () -> {
              javaClient.createGame(template);
            });
    assertEquals(Accepted.class, throwable.getClass());
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "9999", javaClient.getCurrentGameSessionID(), "Team2");
    if (javaClient.getCurrentTeamTurn() == 0) {
      Throwable throwable2 =
          assertThrows(
              Accepted.class,
              () -> {
                javaClient.giveUp();
              });
      assertEquals(Accepted.class, throwable2.getClass());
    } else {
      Throwable throwable3 =
          assertThrows(
              Accepted.class,
              () -> {
                javaClient2.giveUp();
              });
      assertEquals(Accepted.class, throwable3.getClass());
    }
    Throwable throwable4 =
        assertThrows(
            Accepted.class,
            () -> {
              javaClient.getSessionFromServer();
            });
    assertEquals(Accepted.class, throwable4.getClass());
    assertNotNull(javaClient.getWinners());
  }

  @Test
  void testGiveUp() {
    Throwable throwable =
        assertThrows(
            Accepted.class,
            () -> {
              javaClient.createGame(template);
            });
    assertEquals(Accepted.class, throwable.getClass());
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "9999", javaClient.getCurrentGameSessionID(), "Team2");
    if (javaClient.getCurrentTeamTurn() == 0) {
      Throwable throwable2 =
          assertThrows(
              Accepted.class,
              () -> {
                javaClient.giveUp();
              });
      assertEquals(Accepted.class, throwable2.getClass());
    } else {
      Throwable throwable3 =
          assertThrows(
              Accepted.class,
              () -> {
                javaClient2.giveUp();
              });
      assertEquals(Accepted.class, throwable3.getClass());
    }
  }

  @Test
  void testIsGameOver() {
    Throwable throwable =
        assertThrows(
            Accepted.class,
            () -> {
              javaClient.createGame(template);
            });
    assertEquals(Accepted.class, throwable.getClass());
    javaClient.joinGame("Team1");

    try {
      javaClient.getSessionFromServer();
      javaClient.getStateFromServer();
    } catch (Exception e) {

    }
    assertFalse(javaClient.isGameOver());
  }

  @Test
  void testJoinExistingGame() {
    Throwable throwable =
        assertThrows(
            Accepted.class,
            () -> {
              javaClient.createGame(template);
            });
    assertEquals(Accepted.class, throwable.getClass());
    javaClient2.joinExistingGame(
        "localhost", "9999", javaClient.getCurrentGameSessionID(), "Team2");
    assertNotNull(javaClient2.getTeamSecret());
  }

  @Test
  void testJoinGame() {
    Throwable throwable =
        assertThrows(
            Accepted.class,
            () -> {
              javaClient.createGame(template);
            });
    assertEquals(Accepted.class, throwable.getClass());
    javaClient.joinGame("Team1");
    assertNotNull(javaClient.getTeamSecret());
  }

  @Test
  void testMakeMove() {
    Throwable throwable =
        assertThrows(
            Accepted.class,
            () -> {
              javaClient.createGame(template);
            });
    assertEquals(Accepted.class, throwable.getClass());
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "9999", javaClient.getCurrentGameSessionID(), "Team2");
    Move move = new Move();
    move.setPieceId("p:1_1");
    move.setNewPosition(new int[] {1, 1});
    try {
      javaClient.makeMove(move);
    } catch (Exception ex) {
      assert ((ex instanceof Accepted) || (ex instanceof InvalidMove));
    }
  }

  @Test
  void testSetServer() {
    Throwable throwable =
        assertThrows(
            Accepted.class,
            () -> {
              javaClient.setServer("localhost", "9999");
              javaClient.createGame(template);
            });
    assertEquals(Accepted.class, throwable.getClass());
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
