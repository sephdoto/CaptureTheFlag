package org.ctf.ai.mcts;

import static org.junit.jupiter.api.Assertions.*;
import org.ctf.ai.TestValues;
import org.junit.jupiter.api.Test;

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
    
    assertEquals((float)10/100., Math.round(parent.getV()*100)/100.);
    assertEquals(0, child.getV());
    
    child.gameState.setCurrentTeam(1);
    assertEquals(1, child.getV());
  }

  @Test
  void testGetUCT() {
    TreeNode parent = new TreeNode(null, TestValues.getTestState(), new int[] {3,3});
    TreeNode child1 = parent.clone(parent.toNextTeam(parent.copyGameState()));
    child1.wins = new int[] {2,1};
    TreeNode child2 = parent.clone(parent.toNextTeam(parent.copyGameState()));
    child2.wins = new int[] {1,2};
    
    assertEquals(3, child1.getNK());
    assertEquals(Math.round(2/3.*100)/100., Math.round(child1.getV()*100)/100.);
    
    assertEquals(Math.round(1.44f*100)/100., Math.round(child1.getUCT(1)*100)/100.);
    double c = child2.getUCT(1);
    assertEquals(Math.round(1.11f*100)/100., Math.round(c*100)/100.);
  }

  @Test
  void testCloneGameState() {
    fail("Not yet implemented");
  }

  @Test
  void testCopyGameState() {
    fail("Not yet implemented");
  }

  @Test
  void testToNextTeam() {
    fail("Not yet implemented");
  }

  @Test
  void testCompareTo() {
    fail("Not yet implemented");
  }

}
