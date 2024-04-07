package org.ctf.ai.mcts;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import org.ctf.ai.TestValues;
import org.ctf.ai.AI_Tools;
import org.ctf.shared.state.GameState;
import org.junit.jupiter.api.Test;

/**
 * @author sistumpf
 */
class TreeNodeTest {

  @Test
  void testGetNK() {
    TreeNode parent = new TreeNode(null, TestValues.getTestState(), new int[] {90,10});
    TreeNode child = new TreeNode(parent, TestValues.getTestState(), new int[] {0,10});
    child.gameState.setCurrentTeam(0);
    
    assertEquals(100, parent.getNK());
    assertEquals(10, child.getNK());
  }

  @Test
  void testGetV() {
    TreeNode parent = new TreeNode(null, TestValues.getTestState(), new int[] {90,10});
    TreeNode child = new TreeNode(parent, TestValues.getTestState(), new int[] {0,10});
    child.gameState.setCurrentTeam(0);
    
    assertEquals((float)90/100., Math.round(parent.getV()*100)/100.);
    assertEquals(1, child.getV());
    
    child.gameState.setCurrentTeam(1);
    assertEquals(0, child.getV());
  }

  @Test
  void testGetUCT() {
    TreeNode parent = new TreeNode(null, TestValues.getTestState(), new int[] {3,3});
    parent.gameState.setCurrentTeam(0);
    TreeNode child1 = parent.clone(AI_Tools.toNextTeam(parent.copyGameState()));
    child1.wins = new int[] {2,1};
    TreeNode child2 = parent.clone(AI_Tools.toNextTeam(parent.copyGameState()));
    child2.wins = new int[] {1,2};
    
    assertEquals(3, child1.getNK());
    assertEquals(Math.round(2/3.*100)/100., Math.round(child1.getV()*100)/100.);
    
    assertEquals(Math.round(1.44f*100)/100., Math.round(child1.getUCT(1)*100)/100.);
    double c = child2.getUCT(1);
    assertEquals(Math.round(1.11f*100)/100., Math.round(c*100)/100.);
  }

  @Test
  void testClone() {
    TreeNode parent = new TreeNode(null, TestValues.getTestState(), null);
    
    TreeNode clone = parent.clone(parent.copyGameState());
    
    assertNotEquals(clone.wins, parent.wins);
    assertNotEquals(clone.gameState, parent.gameState);
    assertNotEquals(clone.possibleMoves, parent.possibleMoves);
  }

  @Test
  void testCopyGameState() {
    TreeNode parent = new TreeNode(null, TestValues.getTestState(), null);
    GameState copy = parent.copyGameState();
    
    assertNotEquals(parent.gameState, copy);
    assertArrayEquals(parent.gameState.getTeams()[0].getPieces()[0].getPosition(), copy.getTeams()[0].getPieces()[0].getPosition());
    parent.gameState.getTeams()[0].getPieces()[0].setPosition(new int[] {100,100});
    assertFalse(Arrays.equals(parent.gameState.getTeams()[0].getPieces()[0].getPosition(), copy.getTeams()[0].getPieces()[0].getPosition()));
  }

  @Test
  void testCompareTo() {
    TreeNode parent = new TreeNode(null, TestValues.getTestState(), new int[] {3,3});
    TreeNode child1 = parent.clone(AI_Tools.toNextTeam(parent.copyGameState()));
    
    assertTrue(parent.compareTo(child1) == 0);
  
    child1.wins = new int[] {3,4};
    assertTrue(parent.compareTo(child1) > 0);
  
    child1.wins = new int[] {5,3};
    assertTrue(parent.compareTo(child1) < 0);
    
    child1.wins = new int[] {3,3};
    assertTrue(parent.compareTo(child1) == 0);
  }

}
