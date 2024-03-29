package org.ctf.ai.mcts2;

import static org.junit.jupiter.api.Assertions.*;
import org.ctf.ai.TestValues;
import org.ctf.shared.state.GameState;
import org.junit.jupiter.api.Test;

class GridTest {

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
        if(grid.getPosition(x, y) != null)
          assertNotEquals(grid.getPosition(x, y), clone.getPosition(x, y));
      }
    }
  }

}
