package org.ctf.ai;

import java.util.Arrays;
import org.ctf.ai.AI_Tools.InvalidShapeException;
import org.ctf.ai.AI_Tools.NoMovesLeftException;
import org.ctf.ai.random.RandomAI;
import org.ctf.shared.constants.Constants.AI;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Piece;
import org.ctf.shared.state.Team;
import org.ctf.shared.state.data.map.MapTemplate;
import org.ctf.shared.tools.JSON_Tools;
import org.ctf.shared.tools.JSON_Tools.MapNotFoundException;

/**
 * This class requests a GameState from the server, uses one of the implemented AIs
 * to generate the next move and finally returns said move.
 * @author sistumpf 
 */
public class AI_Controller {
  AI ai;
  GameState gameState;
  
  public AI_Controller(GameState gameState, AI ai) {
    this.ai = ai;
    this.gameState = gameState;
  }
  
  public void update(GameState gameState) {
    this.gameState = gameState;
  }
  
  public Move getNextMove()
      throws NoMovesLeftException, InvalidShapeException {
    int milis = 1000;
    switch (this.ai) {
      case RANDOM:
        return RandomAI.pickMoveComplex(gameState);
      case MCTS:
        org.ctf.ai.mcts.TreeNode root = new org.ctf.ai.mcts.TreeNode(null, gameState, null);
        org.ctf.ai.mcts.MCTS mcts = new org.ctf.ai.mcts.MCTS(root); 
        Move move = mcts.getMove(milis, AI_Constants.C);
        mcts.root.printGrid();
        System.out.println(mcts.printResults(move));
        return move;
      case MCTS_IMPROVED:
        org.ctf.ai.mcts2.TreeNode root2 = new org.ctf.ai.mcts2.TreeNode(null, gameState, null);
        org.ctf.ai.mcts2.MCTS mcts2 = new org.ctf.ai.mcts2.MCTS(root2);
        Move move2 = mcts2.getMove(milis, AI_Constants.C);
        mcts2.root.printGrids();
        System.out.println(mcts2.printResults(move2));
        return move2;  
      default:
        return RandomAI.pickMoveComplex(gameState);
    }
  }
}
