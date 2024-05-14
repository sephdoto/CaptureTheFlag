package org.ctf.shared.ai.mcts2;

import org.ctf.shared.ai.AIConfig;
//import static org.junit.jupiter.api.Assertions.*;
import org.ctf.shared.ai.TestValues;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Piece;
import org.junit.jupiter.api.Test;

/**
 * @author sistumpf
 */
class TreeNodeTest {

  @Test
  /**
   * Test if the updated grid stays correct after simulating it
   */
  void testUpdating() {
    TreeNode node = new TreeNode(null, TestValues.getTestState(), null);
    MCTS mcts = new MCTS(node, new AIConfig());
    for(int i=0; i<50 && mcts.isTerminal(node.getGameState()) == -1; i++){
      mcts.oneMove(node, node, true);
      mcts.removeTeamCheck(node.getGameState());
//      node.printGrids();
//      System.out.println(node.gameState.getTeams()[0].getPieces().length + "<- 0, 1 ->" + node.gameState.getTeams()[1].getPieces().length);
    }
    TreeNode copy = node.clone(node.getGameState().clone());
    copy.initPossibleMovesAndChildren();
    
  }
  
  @Test
  void testCaptureUpdate() {
    GameState gameState = TestValues.getEmptyTestState();
    gameState = TestValues.getEmptyTestState();
    gameState.getTeams()[0].setBase(new int[] {0,0});
    gameState.getTeams()[0].setFlags(1);
    gameState.getTeams()[0].setId("0");
    Piece[] pieces0 = new Piece[2];
    pieces0[0] = new Piece();
    pieces0[0].setDescription(TestValues.getTestTemplate().getPieces()[2]);
    pieces0[0].setId("p:0_1");
    pieces0[0].setPosition(new int[] {0,5});
    pieces0[0].setTeamId("0");
    pieces0[1] = new Piece();
    pieces0[1].setDescription(TestValues.getTestTemplate().getPieces()[2]);
    pieces0[1].setId("p:0_2");
    pieces0[1].setPosition(new int[] {0,9});
    pieces0[1].setTeamId("0");
    gameState.getTeams()[0].setPieces(pieces0);
    gameState.getTeams()[1].setBase(new int[] {9,9});
    gameState.getTeams()[1].setFlags(1);
    gameState.getTeams()[1].setId("1");
    Piece[] pieces1 = new Piece[2];
    pieces1[0] = new Piece();
    pieces1[0].setDescription(TestValues.getTestTemplate().getPieces()[1]);
    pieces1[0].getDescription().setAttackPower(3);
    pieces1[0].setId("p:1_1");
    pieces1[0].setPosition(new int[] {9,5});
    pieces1[0].setTeamId("1");
    pieces1[1] = new Piece();
    pieces1[1].setDescription(TestValues.getTestTemplate().getPieces()[1]);
    pieces1[1].getDescription().setAttackPower(3);
    pieces1[1].setId("p:1_2");
    pieces1[1].setPosition(new int[] {9,0});
    pieces1[1].setTeamId("1");

    gameState.getTeams()[1].setPieces(pieces1);
    gameState.getGrid()[0][5] = pieces0[0].getId();
    gameState.getGrid()[9][5] = pieces1[0].getId();
    gameState.getGrid()[0][9] = pieces0[1].getId();
    gameState.getGrid()[9][0] = pieces1[1].getId();
    for(int i=0; i<10; i++) {
      gameState.getGrid()[i][4] = "b";
      gameState.getGrid()[i][6] = "b";
    }
    
    TreeNode node = new TreeNode(null, gameState, null);
    
    MCTS mcts = new MCTS(node, new AIConfig());
    for(; mcts.isTerminal(node.getGameState()) == -1; ) {
      mcts.oneMove(node, node, true);
//      node.printGrids();
    }
  }
  
  @Test
  void testUpdatePossibleMovesAndChildren() {
    GameState gameState = TestValues.getEmptyTestState();
    gameState = TestValues.getEmptyTestState();
    gameState.getTeams()[0].setBase(new int[] {0,0});
    gameState.getTeams()[0].setFlags(1);
    gameState.getTeams()[0].setId("0");
    Piece[] pieces0 = new Piece[1];
    pieces0[0] = new Piece();
    pieces0[0].setDescription(TestValues.getTestTemplate().getPieces()[2]);
    pieces0[0].setId("p:0_1");
    pieces0[0].setPosition(new int[] {0,5});
    pieces0[0].setTeamId("0");
    gameState.getTeams()[0].setPieces(pieces0);
    gameState.getTeams()[1].setBase(new int[] {9,9});
    gameState.getTeams()[1].setFlags(1);
    gameState.getTeams()[1].setId("1");
    Piece[] pieces1 = new Piece[1];
    pieces1[0] = new Piece();
    pieces1[0].setDescription(TestValues.getTestTemplate().getPieces()[1]);
    pieces1[0].getDescription().setAttackPower(3);
    pieces1[0].setId("p:1_1");
    pieces1[0].setPosition(new int[] {9,5});
    pieces1[0].setTeamId("1");
    gameState.getTeams()[1].setPieces(pieces1);
    gameState.getGrid()[0][5] = pieces0[0].getId();
    gameState.getGrid()[9][5] = pieces1[0].getId();
    for(int i=0; i<10; i++) {
      gameState.getGrid()[i][4] = "b";
      gameState.getGrid()[i][6] = "b";
    }
    TreeNode node = new TreeNode(null, gameState, null);
    
    MCTS mcts = new MCTS(node, new AIConfig());
    for(; mcts.isTerminal(node.getGameState()) == -1; ) {
      mcts.oneMove(node, node, true);
//      node.printGrids();
    }
  }
}
