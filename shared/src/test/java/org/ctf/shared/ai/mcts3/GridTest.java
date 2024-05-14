package org.ctf.shared.ai.mcts3;

import static org.junit.jupiter.api.Assertions.*;
import org.ctf.shared.ai.ReferenceMove;
import org.ctf.shared.ai.TestValues;
import org.ctf.shared.state.GameState;
import org.junit.jupiter.api.Test;

/**
 * @author sistumpf
 */
class GridTest {
  @Test
  void testEquals() {
    TreeNode node = new TreeNode(null, new ReferenceGameState(TestValues.getTestState()), null, new ReferenceMove(null, new int[2]));
    TreeNode clone = node.clone(node.getGameState().clone());
    assertTrue(node.getGameState().getGrid().equals(node.getGameState().getGrid()));
    assertTrue(node.getGameState().getGrid().equals(clone.getGameState().getGrid()));
  }
  
  @Test
  void testInitAndClone() {
    GameState testState = TestValues.getTestState();
    Grid grid = new Grid(testState);
    
    for(int y=0; y<testState.getGrid().length; y++) {
      for(int x=0; x<testState.getGrid()[y].length; x++) {
        GridObjectContainer goc = grid.getPosition(x, y);
        assertEquals(testState.getGrid()[y][x], goc == null ? "" : goc.toString());
      }
    }
    
    Grid clone = grid.clone();
    
    assertNotEquals(grid, clone);
    
    for(int y=0; y<grid.getGrid().length; y++) {
      for(int x=0; x<grid.getGrid()[y].length; x++) {
        if(grid.getPosition(x, y) != null) {
          assertNotEquals(grid.getPosition(x, y), clone.getPosition(x, y));
          if(grid.getPosition(x, y).getObject() == GridObjects.piece)
            assertNotEquals(grid.getPosition(x, y).getPiece().getPosition(), clone.getPosition(x, y).getPiece().getPosition());
        }
      }
    }
  }
}
