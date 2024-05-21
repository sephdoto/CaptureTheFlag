package org.ctf.shared.ai;

import static org.junit.jupiter.api.Assertions.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import org.ctf.shared.ai.GameUtilities.InvalidShapeException;
import org.ctf.shared.ai.GameUtilities.NoMovesLeftException;
import org.ctf.shared.ai.mcts.MCTS;
import org.ctf.shared.ai.mcts.TreeNode;
import org.ctf.shared.ai.random.RandomAI;
import org.ctf.shared.client.Client;
import org.ctf.shared.client.ClientStepBuilder;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.AI;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.data.exceptions.GameOver;
import org.ctf.shared.tools.JsonTools;
import org.ctf.shared.tools.JsonTools.MapNotFoundException;
import org.junit.jupiter.api.Test;

/**
 * AIController is responsible for managing the AIs.
 * It takes an AI and a initial GameState as input, then returns a move made by the chosen AI.
 * The Controller gets updated with either a GameState or a Move.
 * Updating with a move is more efficient, as the Controller can build the search tree in the background.
 * This might be changed in the future. TODO
 * 
 * @author sistumpf
 */
class AIControllerTest {

  @Test
  void testInit() {
    new AIController(TestValues.getTestState(), AI.RANDOM, new AIConfig(), 0);
  }

  @Test
  void testGetMove() throws MapNotFoundException {
    for(AI ai : AI.values()) {
      if(ai == AI.HUMAN) continue;
      //    AI ai = AI.IMPROVED; {
      AIController aic = new AIController(JsonTools.readGameState("test"), ai, new AIConfig(), 0);
      for(int i=0; i<3; i++) {
        try {
          Move move = aic.getNextMove();
          /*if(ai != AI.RANDOM)
            aic.getMcts().getRoot().printGrid();
          System.out.println(aic.update(move) + " updated with " + move.getPieceId() + " to " + move.getNewPosition()[0] + "," + move.getNewPosition()[1]);*/
        } catch (NoMovesLeftException | InvalidShapeException e) {
          fail("Fehler bei getMove");
        }
      }
    }
  }

  @Test
  void testUpdateGameState() throws NoMovesLeftException, InvalidShapeException {
    GameState gsOld = TestValues.getTestState();
    AIController aic = new AIController(gsOld, AI.RANDOM, new AIConfig(), 1);
    Move move = aic.getNextMove();
    MCTS mcts = new MCTS(new TreeNode(null, gsOld, new int[2], new ReferenceMove(null, new int[2])), new AIConfig());
    GameState gs = mcts.getRoot().copyGameState();
    mcts.alterGameState(gs, new ReferenceMove(gs, move));

    aic.update(gs);

    assertTrue(gs == (aic.getNormalizedGameState().getOriginalGameState()));
    assertFalse(gs == gsOld);
  }

  @Test
  void testUpdateMove() throws NoMovesLeftException, InvalidShapeException {
    GameState gsOld = new GameStateNormalizer(TestValues.getTestState(), true).getNormalizedGameState();
    AIController aic = new AIController(gsOld, AI.IMPROVED, new AIConfig(), 0);

    Move move = aic.getNextMove();
    assertTrue(gridEquals(aic.getMcts().getRoot().getGameState().getGrid(), gsOld.getGrid()));

    assertTrue(aic.update(move));

    assertFalse(gridEquals(aic.getMcts().getRoot().getGameState().getGrid(), gsOld.getGrid()));
  }

  //  @Test
  void testRamDrop() throws NoMovesLeftException, InvalidShapeException {
    AIController aic = new AIController(TestValues.getTestState(), AI.IMPROVED, new AIConfig(), 1);
    for(int i=0; i<100; i++) {
      Move move = aic.getNextMove();
      move = RandomAI.pickMoveComplex(aic.getMcts().getRoot().getGameState(), new ReferenceMove(null, new int[2])).toMove();
      aic.update(move);
    }
  }


  @SuppressWarnings("deprecation")
  @Test
  /**
   * Integration testing.
   * Starting a Server, creating 2 human clients, creating 2 AIControllers.
   * Controllers play against each other.
   * 
   * @throws MapNotFoundException
   * @throws NoMovesLeftException
   * @throws InvalidShapeException
   * @throws IOException
   * @throws InterruptedException
   */
  void clientIntegrationTesting() throws MapNotFoundException, NoMovesLeftException, InvalidShapeException, IOException, InterruptedException {
    Process process = startServer();
    Thread.sleep(7000);
    Client client1 = ClientStepBuilder.newBuilder()
        .enableRestLayer(false)
        .onRemoteHost("127.0.0.1")
        .onPort("9999")
        .enableSaveGame(false)
        .disableAutoJoin()
        .build();
    Client client2 = ClientStepBuilder.newBuilder()
        .enableRestLayer(false)
        .onRemoteHost("127.0.0.1")
        .onPort("9999")
        .enableSaveGame(false)
        .disableAutoJoin()
        .build();
    client1.createGame(JsonTools.readMapTemplate("Default"));
    client1.joinGame("testClient1");
    client2.joinExistingGame("127.0.0.1", "9999", client1.getCurrentGameSessionID(), "testClient2");
    Thread.sleep(5000);
    try {
      client1.pullData();
      client2.pullData();
      AIController aic = new AIController(client1.getCurrentState(), AI.IMPROVED, new AIConfig(), 1);
      AIController aic2 = new AIController(client2.getCurrentState(), AI.IMPROVED, new AIConfig(), 1);
      aic2.getNextMove();
      for(int i=0; i<9; i++) {
        Move move = aic.getNextMove();
        System.out.println(aic.getMcts().printResults(move));
        client1.makeMove(move);
        Thread.sleep(50);
        client1.pullData();
        client2.pullData();
        Thread.sleep(50);
        aic.update(client1.getCurrentState(), client1.getLastMove());
        aic2.update(client2.getCurrentState(), client2.getLastMove());
        move = aic2.getNextMove();
        System.out.println(aic2.getMcts().printResults(move));
        client2.makeMove(move);
        Thread.sleep(50);
        client1.pullData();
        client2.pullData();
        Thread.sleep(50);
        aic.update(client1.getCurrentState(), client1.getLastMove());
        aic2.update(client2.getCurrentState(), client2.getLastMove());
      }
    } catch(GameOver e) {
      //nice
    }catch(Exception e) {
      e.printStackTrace();
      process.destroyForcibly();
    } 
    process.destroyForcibly();
  }

  Process startServer() throws IOException {
    ProcessBuilder processBuilder =
        new ProcessBuilder(
            "java", "-jar", Constants.toUIResources + "server.jar", "--server.port=9999");
    Process process = processBuilder.start();
    new Thread(
        () -> {
          try (BufferedReader reader =
              new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
              System.out.println(line);
            }

          } catch (IOException e) {
            e.printStackTrace();
          }
        })
    .start();
    return process;
  }


  /**
   * Writing a whole GameState.equals would be a bit much, comparing the grids should be fine.
   * @return true if the Grids are equal
   */
  boolean gridEquals(String[][] grid1, String[][] grid2) {
    try {
      for(int y=0; y<grid1.length; y++)
        for(int x=0; x<grid1[y].length; x++)
          if(!grid1[y][x].equals(grid2[y][x]))
            return false;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
}
