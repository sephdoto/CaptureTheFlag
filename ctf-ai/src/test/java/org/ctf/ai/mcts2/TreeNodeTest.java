package org.ctf.ai.mcts2;

import static org.junit.jupiter.api.Assertions.*;
import org.ctf.ai.TestValues;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Piece;
import org.junit.jupiter.api.Test;

class TreeNodeTest {

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
    
    MCTS mcts = new MCTS(node);
    for(; mcts.isTerminal(node) == -1; ) {
      mcts.oneMove(node, node, true);
      node.printGrids();
    }
  }
}
