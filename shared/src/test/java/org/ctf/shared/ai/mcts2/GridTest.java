package org.ctf.shared.ai.mcts2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.ctf.shared.ai.TestValues;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Piece;
import org.junit.jupiter.api.Test;

/**
 * @author sistumpf
 */
class GridTest {
  @Test
  void testEquals() {
    TreeNode node = new TreeNode(null, TestValues.getTestState(), null);
    TreeNode clone = node.clone(node.getReferenceGameState().clone());
    assertTrue(node.getReferenceGameState().getGrid().equals(node.getReferenceGameState().getGrid()));
    assertTrue(node.getReferenceGameState().getGrid().equals(clone.getReferenceGameState().getGrid()));
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
        if(grid.getPosition(x, y) != null)
          assertNotEquals(grid.getPosition(x, y), clone.getPosition(x, y));
      }
    }
  }
  
  @Test
  void testInitPieceVision() {
    GameState testState = TestValues.getTestState();
    TreeNode node = new TreeNode(null, testState, null);
    Grid grid = node.getReferenceGameState().getGrid();
    for(Piece key : node.getPossibleMoves().keySet())
      for(int[] pos : node.getPossibleMoves().get(key))
        assertNotNull(grid.pieceVisionGrid[pos[0]][pos[1]]);
    
    for(int i=0; i<node.getReferenceGameState().getGrid().getGrid().length; i++) {
      for(int j=0; j<node.getReferenceGameState().getGrid().getGrid()[0].length; j++) {
        if(node.getReferenceGameState().getGrid().getPieceVisionGrid()[i][j] == null)
          System.out.print(". ");
        else if(node.getReferenceGameState().getGrid().getPieceVisionGrid()[i][j].getPieces().stream().anyMatch(p -> p.getId().equals("p:0_6")))
          System.out.print("X ");
        else
          System.out.print(node.getReferenceGameState().getGrid().getPieceVisionGrid()[i][j].getPieces().size() + " ");
      }
      System.out.print("\t");
      for(int j=0; j<node.getReferenceGameState().getGrid().getGrid()[0].length; j++) {
        if(node.getReferenceGameState().getGrid().getGrid()[i][j] == null)
          System.out.print(". ");
        else
          System.out.print(node.getReferenceGameState().getGrid().getGrid()[i][j].getObject().ordinal() + " ");
      }
     System.out.println(); 
    }
    
    
    //Das Grid wird nicht mehr komplett geclont, PieceVisions sollen in den Nodes initialisiert werden?
    /*
    Grid clone = grid.clone();
    
    for(Piece key : node.possibleMoves.keySet())
      for(int[] pos : node.possibleMoves.get(key)) {
        assertNotNull(clone.pieceVisionGrid[pos[0]][pos[1]]);
        assertFalse(clone.pieceVisionGrid[pos[0]][pos[1]].equals(grid.pieceVisionGrid[pos[1]][pos[0]]));
      }*/
  }

}
