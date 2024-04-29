package org.ctf.shared.ai;
import org.ctf.shared.ai.mcts.TreeNode;
import java.util.Arrays;
import org.ctf.shared.ai.AI_Tools.InvalidShapeException;
import org.ctf.shared.ai.AI_Tools.NoMovesLeftException;
import org.ctf.shared.ai.random.RandomAI;
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
  boolean active;

  public AI_Controller(GameState gameState, AI ai) {
    this.ai = ai;
    if(gameState.getCurrentTeam() < 0)
      return;
    this.gameState = gameState;
    //    normaliseGameState();
    this.active = true;
  }

  public void update(GameState gameState) {
    this.gameState = gameState;
    if(gameState.getCurrentTeam() < 0) {
      this.active = false;
      shutDown();
    } else {
      this.active = true;
    }
    //    normaliseGameState();
  }

  public void shutDown() {
    System.out.println("AI Shit down");
  }

  public Move getNextMove()
      throws NoMovesLeftException, InvalidShapeException {
    if(!this.active)
      return null;

    int milis = 6000;
    switch (this.ai) {
      case RANDOM:
        return RandomAI.pickMoveComplex(gameState, new ReferenceMove(null, new int[] {0,0})).toMove();
      case MCTS:
        org.ctf.shared.ai.mcts.TreeNode root = new org.ctf.shared.ai.mcts.TreeNode(null, gameState, null, new ReferenceMove(null, new int[] {0,0}));
        org.ctf.shared.ai.mcts.MCTS mcts = new org.ctf.shared.ai.mcts.MCTS(root); 
        Move move = mcts.getMove(milis, AI_Constants.C);
        mcts.root.printGrid();
        System.out.println(mcts.printResults(move));
        return move;
      case MCTS_IMPROVED:
        org.ctf.shared.ai.mcts2.TreeNode root2 = new org.ctf.shared.ai.mcts2.TreeNode(null, gameState, null);
        org.ctf.shared.ai.mcts2.MCTS mcts2 = new org.ctf.shared.ai.mcts2.MCTS(root2);
        Move move2 = mcts2.getMove(milis, AI_Constants.C);
        mcts2.root.printGrids();
        System.out.println(mcts2.printResults(move2));
        return move2;  
      default:
        return RandomAI.pickMoveComplex(gameState, new ReferenceMove(null, new int[] {0,0})).toMove();
    }
  }

  private void normaliseGameState() {
    for(int i=0; i<this.gameState.getTeams().length; i++)
      this.gameState.getTeams()[i].setId(""+i);
  }
}
