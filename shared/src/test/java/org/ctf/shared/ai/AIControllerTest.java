package org.ctf.shared.ai;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import org.ctf.shared.ai.GameUtilities.InvalidShapeException;
import org.ctf.shared.ai.GameUtilities.NoMovesLeftException;
import org.ctf.shared.ai.mcts.MCTS;
import org.ctf.shared.ai.mcts.TreeNode;
import org.ctf.shared.ai.random.RandomAI;
import org.ctf.shared.constants.Enums.AI;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
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
  void testGetMove() {
    for(AI ai : AI.values()) {
      AIController aic = new AIController(TestValues.getTestState(), ai, new AIConfig(), 0);
      for(int i=0; i<6; i++) {
        try {
          Move move = aic.getNextMove();
          aic.update(move);
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
