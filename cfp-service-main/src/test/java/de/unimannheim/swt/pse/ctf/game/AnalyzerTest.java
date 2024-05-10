package de.unimannheim.swt.pse.ctf.game;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import de.unimannheim.swt.pse.ctf.CtfApplication;
import java.io.File;
import java.io.IOException;
import org.ctf.shared.ai.AIConfig;
import org.ctf.shared.ai.AIController;
import org.ctf.shared.ai.GameUtilities.InvalidShapeException;
import org.ctf.shared.ai.GameUtilities.NoMovesLeftException;
import org.ctf.shared.client.Client;
import org.ctf.shared.client.ClientStepBuilder;
import org.ctf.shared.client.lib.Analyzer;
import org.ctf.shared.client.lib.SavedGame;
import org.ctf.shared.client.lib.ServerDetails;
import org.ctf.shared.client.lib.ServerManager;
import org.ctf.shared.client.service.RestClientLayer;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.AI;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.data.map.MapTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AnalyzerTest {

  static RestClientLayer comm = new RestClientLayer();

  @BeforeAll
  static void setup() {
    String[] args = new String[] {"--server.port=9994"};
    CtfApplication.main(args);
  }

  @Test
  void testAddGameState() {
    Analyzer analyzer = new Analyzer();
    MapTemplate template = createGameTemplate();
    ServerManager manager =
        new ServerManager(comm, new ServerDetails("localhost", "9994"), template);
    manager.createGame();
    Client p1 =
        ClientStepBuilder.newBuilder()
            .enableRestLayer(false)
            .onLocalHost()
            .onPort("9994")
            .enableSaveGame(false)
            .enableAutoJoin(manager.getGameSessionID(), "p1")
            .build();
    Client p2 =
        ClientStepBuilder.newBuilder()
            .enableRestLayer(false)
            .onLocalHost()
            .onPort("9994")
            .enableSaveGame(false)
            .enableAutoJoin(manager.getGameSessionID(), "p2")
            .build();
    p1.joinExistingGame(
        manager.serverDetails.getHost(),
        manager.serverDetails.getPort(),
        manager.getGameSessionID(),
        "p1");
    p2.joinExistingGame(
        manager.serverDetails.getHost(),
        manager.serverDetails.getPort(),
        manager.getGameSessionID(),
        "p2");
    p1.pullData();
    try {
      analyzer.addGameState(p1.getCurrentState());
    } catch (Exception e) {
      fail();
    }
    Gson gson = new Gson();
    System.out.println(gson.toJson(analyzer.getSavedGame().getInitialState()));
    assertNotNull(analyzer.getSavedGame().getInitialState());
    assertEquals(analyzer.getSavedGame().getInitialState().getTeams().length, 2);
    manager.deleteGame();
  }

  @Test
  void testAddMove() {
    Analyzer analyzer = new Analyzer();
    MapTemplate template = createGameTemplate();
    ServerManager manager =
        new ServerManager(comm, new ServerDetails("localhost", "9994"), template);
    manager.createGame();
    Client p1 =
        ClientStepBuilder.newBuilder()
            .enableRestLayer(false)
            .onLocalHost()
            .onPort("9994")
            .enableSaveGame(false)
            .disableAutoJoin()
            .build();
    Client p2 =
        ClientStepBuilder.newBuilder()
            .enableRestLayer(false)
            .onLocalHost()
            .onPort("9994")
            .enableSaveGame(false)
            .disableAutoJoin()
            .build();
    p1.joinExistingGame(
        manager.serverDetails.getHost(),
        manager.serverDetails.getPort(),
        manager.getGameSessionID(),
        "p1");
    p2.joinExistingGame(
        manager.serverDetails.getHost(),
        manager.serverDetails.getPort(),
        manager.getGameSessionID(),
        "p2");
    p1.pullData();
    p2.pullData();
    AIController controller1 = new AIController(p1.getCurrentState(), AI.RANDOM, new AIConfig(), 0);
    AIController controller2 = new AIController(p2.getCurrentState(), AI.RANDOM, new AIConfig(), 0);
    analyzer.addGameState(p1.getCurrentState());
    for (int i = 0; i < 5; i++) {
      try {
        if (p1.isItMyTurn()) {
          p1.makeMove(controller1.getNextMove());
        } else {
          p2.makeMove(controller2.getNextMove());
        }
      } catch (NoMovesLeftException | InvalidShapeException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      p1.pullData();
      p2.pullData();
      analyzer.addMove(p1.getCurrentState().getLastMove());
      controller1.update(p1.getCurrentState());
      controller2.update(p2.getCurrentState());
    }
    Gson gson = new Gson();
    System.out.println(gson.toJson(analyzer.savedGame.getMoves().get("1")));
    System.out.println(gson.toJson(analyzer.savedGame.getMoves().get("2")));
    System.out.println(gson.toJson(analyzer.savedGame.getMoves().get("3")));
    System.out.println(gson.toJson(analyzer.savedGame.getMoves().get("4")));
    System.out.println(gson.toJson(analyzer.savedGame.getMoves().get("5")));
  }

  @Test
  void testGetSavedGame() {}

  @Test
  void testReadFile() {
    Analyzer analyzer = new Analyzer();
    boolean b = analyzer.readFile("analyzerTestDataFile");
    assertTrue(b);
    SavedGame gameData = analyzer.getSavedGame();
    Gson gson = new Gson();

    Move move1 = new Move();
    move1.setPieceId("p:0_16");
    move1.setTeamId("p1");
    move1.setNewPosition(new int[] {6, 9});
    assertTrue(
        move1.getPieceId().toString().equals(gameData.getMoves().get("1").getPieceId().toString()));
    assertTrue(
        move1.getTeamId().toString().equals(gameData.getMoves().get("1").getTeamId().toString()));
    assertArrayEquals(move1.getNewPosition(), gameData.getMoves().get("1").getNewPosition());
    // System.out.println(gson.toJson(move1.getNewPosition()) +
    // gson.toJson(gameData.getMoves().get("1").getNewPosition()));
  }

  @Test
  void testWriteOut() {
    Analyzer analyzer = new Analyzer();
    MapTemplate template = createGameTemplate();
    ServerManager manager =
        new ServerManager(comm, new ServerDetails("localhost", "9994"), template);
    manager.createGame();
    Client p1 =
        ClientStepBuilder.newBuilder()
            .enableRestLayer(false)
            .onLocalHost()
            .onPort("9994")
            .enableSaveGame(false)
            .disableAutoJoin()
            .build();
    Client p2 =
        ClientStepBuilder.newBuilder()
            .enableRestLayer(false)
            .onLocalHost()
            .onPort("9994")
            .enableSaveGame(false)
            .disableAutoJoin()
            .build();
    p1.joinExistingGame(
        manager.serverDetails.getHost(),
        manager.serverDetails.getPort(),
        manager.getGameSessionID(),
        "p1");
    p2.joinExistingGame(
        manager.serverDetails.getHost(),
        manager.serverDetails.getPort(),
        manager.getGameSessionID(),
        "p2");
    p1.pullData();
    p2.pullData();
    AIController controller1 = new AIController(p1.getCurrentState(), AI.RANDOM,new AIConfig(), 0);
    AIController controller2 = new AIController(p2.getCurrentState(), AI.RANDOM,new AIConfig(), 0);
    analyzer.addGameState(p1.getCurrentState());
    for (int i = 0; i < 5; i++) {
      try {
        if (p1.isItMyTurn()) {
          p1.makeMove(controller1.getNextMove());
        } else {
          p2.makeMove(controller2.getNextMove());
        }
      } catch (NoMovesLeftException | InvalidShapeException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      p1.pullData();
      p2.pullData();
      analyzer.addMove(p1.getCurrentState().getLastMove());
      controller1.update(p1.getCurrentState());
      controller2.update(p2.getCurrentState());
    }
    assertTrue(analyzer.writeOut());
    File myObj = new File(Constants.saveGameFolder + analyzer.lastFileName + ".savedgame");
    myObj.delete();
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
