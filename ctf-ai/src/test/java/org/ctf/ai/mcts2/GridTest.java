package org.ctf.ai.mcts2;

import static org.junit.jupiter.api.Assertions.*;
import org.ctf.ai.TestValues;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Piece;
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
  
  @Test
  void testInitPieceVision() {
    GameState testState = TestValues.getTestState();
    TreeNode node = new TreeNode(null, testState, null);
    Grid grid = node.grid;
    for(Piece key : node.possibleMoves.keySet())
      for(int[] pos : node.possibleMoves.get(key))
        assertNotNull(grid.pieceVisionGrid[pos[0]][pos[1]]);
    
    for(int i=0; i<node.grid.getGrid().length; i++) {
      for(int j=0; j<node.grid.getGrid()[0].length; j++) {
        if(node.grid.getPieceVisionGrid()[i][j] == null)
          System.out.print(". ");
        else if(node.grid.getPieceVisionGrid()[i][j].getPieces().stream().anyMatch(p -> p.getId().equals("p:0_6")))
          System.out.print("X ");
        else
          System.out.print(node.grid.getPieceVisionGrid()[i][j].getPieces().size() + " ");
      }
      System.out.print("\t");
      for(int j=0; j<node.grid.getGrid()[0].length; j++) {
        if(node.grid.getGrid()[i][j] == null)
          System.out.print(". ");
        else
          System.out.print(node.grid.getGrid()[i][j].getObject().ordinal() + " ");
      }
     System.out.println(); 
    }
    
    
    Grid clone = grid.clone();
    for(Piece key : node.possibleMoves.keySet())
      for(int[] pos : node.possibleMoves.get(key)) {
        assertNotNull(clone.pieceVisionGrid[pos[0]][pos[1]]);
        assertFalse(clone.pieceVisionGrid[pos[0]][pos[1]].equals(grid.pieceVisionGrid[pos[1]][pos[0]]));
      }
  }

}
