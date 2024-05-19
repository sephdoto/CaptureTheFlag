package de.unimannheim.swt.pse.ctf.game.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unimannheim.swt.pse.ctf.CtfApplication;
import java.io.IOException;

import org.ctf.shared.client.lib.ServerDetails;
import org.ctf.shared.client.lib.ServerManager;
import org.ctf.shared.client.service.CommLayer;
import org.ctf.shared.state.data.map.MapTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ServerManagerTest {
  static CommLayer comm = new CommLayer();

  @BeforeAll
  static void setup() {
    String[] args = new String[] {"--server.port=9995"};
    CtfApplication.main(args);
  }

  @Test
  void testCreateGame() {
    MapTemplate template = createGameTemplate();
    ServerManager manager =
        new ServerManager(comm, new ServerDetails("localhost", "9995"), template);
    try {
      manager.createGame();
    } catch (Exception e) {
      fail();
    }
    assertNotNull(manager.gameSessionID);
  }

  @Test
  void testDeleteGame() {
    MapTemplate template = createGameTemplate();
    ServerDetails serverDetails = new ServerDetails("localhost", "localhost");
    serverDetails.setHost("localhost");
    serverDetails.setPort("9995");
    ServerManager manager =
        new ServerManager(comm, serverDetails, template);
    try {
      manager.createGame();
    } catch (Exception e) {
      fail();
    }
    assertNotNull(manager.gameSessionID);
    try {
      manager.deleteGame();
    } catch (Exception e) {
      fail();
    }
    assertFalse(manager.isSessionActive());
  }

  @Test
  void testGetCurrentNumberofTeams() {
    MapTemplate template = createGameTemplate();
    ServerManager manager =
        new ServerManager(comm, new ServerDetails("localhost", "9995"), template);
    try {
      manager.createGame();
    } catch (Exception e) {
      fail();
    }
    assertNotNull(manager.gameSessionID);
    try {
      manager.getCurrentNumberofTeams();
    } catch (Exception e) {
      fail();
    }
    assertEquals(manager.getCurrentNumberofTeams(), 0);
    comm.joinGame(
        "http://"
            + manager.serverDetails.getHost()
            + ":"
            + manager.serverDetails.getPort()
            + "/api/gamesession/"
            + manager.getGameSessionID(),
        "TestTeam");
    assertEquals(manager.getCurrentNumberofTeams(), 1);
  }

  @Test
  void testGetGameSessionID() {
    MapTemplate template = createGameTemplate();
    ServerManager manager =
        new ServerManager(comm, new ServerDetails("localhost", "9995"), template);
    assertTrue(manager.serverDetails.isLocalhost());    
    try {
      manager.createGame();
    } catch (Exception e) {
      fail();
    }
    ServerManager manager2 =
    new ServerManager(comm, new ServerDetails("localhost", "9995"), manager.getGameSessionID());
    manager2.getGameSessionID();
    assertNotNull(manager2.getGameSessionID());
    assertTrue(manager2.isSessionActive());
  }

  @Test
  void deleteGameFalseTest() {
    MapTemplate template = createGameTemplate();
    ServerManager manager =
        new ServerManager(comm, new ServerDetails("localhost", "9995"), template);
    try {
      manager.createGame();
    } catch (Exception e) {
      fail();
    }
    manager.gameSessionID = "Empty";
    assertFalse(manager.deleteGame());
  }

  @Test
  void testServerNotActive() {
    MapTemplate template = createGameTemplate();
    ServerManager manager =
        new ServerManager(comm, new ServerDetails("localhost", "9994"), template);
    try {
      manager.createGame();
    } catch (Exception e) {
    }
    assertEquals(manager.getCurrentNumberofTeams(), 0);
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
