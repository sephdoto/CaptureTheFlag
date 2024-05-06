package org.ctf.shared.ai;

import org.ctf.shared.ai.AI_Tools.InvalidShapeException;
import org.ctf.shared.ai.AI_Tools.NoMovesLeftException;
import org.ctf.shared.ai.random.RandomAI;
import org.ctf.shared.constants.Enums.AI;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.data.exceptions.GameOver;

/**
 * This class requests a GameState from the server, uses one of the implemented AIs to generate the
 * next move and finally returns said move.
 *
 * @author sistumpf
 */
public class AI_Controller {
  AI_Config config;
  AI ai;
  GameState gameState;
  boolean active;
  int thinkingTime;

  public AI_Controller(GameState gameState, AI ai, int thinkingTime, AI_Config config) {
    this.ai = ai;
    this.thinkingTime = thinkingTime * 1000;
    if (gameState.getCurrentTeam() < 0) return;
    this.gameState = gameState;
    this.config = config;
    //    normaliseGameState();
    this.active = true;
  }

  public void update(GameState gameState) {
    this.gameState = gameState;
    if (gameState.getCurrentTeam() < 0) {
      this.active = false;
      shutDown();
    } else {
      this.active = true;
    }
    //    normaliseGameState();
  }

  public void shutDown() {
    System.out.println("AI Shit down");
    throw new GameOver();
  }

  public Move getNextMove() throws NoMovesLeftException, InvalidShapeException {
    if (!this.active) return null;
    int milis = thinkingTime;
    if(thinkingTime == 0){
      milis = 500;
    } else {
      milis = thinkingTime;
    }

    switch (this.ai) {
      case RANDOM:
        return RandomAI.pickMoveComplex(gameState, new ReferenceMove(null, new int[] {0, 0}))
            .toMove();
      case MCTS:
        org.ctf.shared.ai.mcts.TreeNode root =
        new org.ctf.shared.ai.mcts.TreeNode(
            null, gameState, null, new ReferenceMove(null, new int[] {0, 0}));
        org.ctf.shared.ai.mcts.MCTS mcts = new org.ctf.shared.ai.mcts.MCTS(root,config);
        Move move = mcts.getMove(milis,config.C);
        mcts.root.printGrid();
        System.out.println(mcts.printResults(move));
        return move;
      case IMPROVED:
        org.ctf.shared.ai.mcts3.TreeNode root3 =
        new org.ctf.shared.ai.mcts3.TreeNode(null, new org.ctf.shared.ai.mcts3.ReferenceGameState(gameState), null, new ReferenceMove(null, new int[2]));
        org.ctf.shared.ai.mcts3.MCTS mcts3 = new org.ctf.shared.ai.mcts3.MCTS(root3,config);
        Move move3 = mcts3.getMove(milis,config.C);
        mcts3.root.printGrid();
        System.out.println(mcts3.printResults(move3));
      case EXPERIMENTAL:
        org.ctf.shared.ai.mcts2.TreeNode root2 =
        new org.ctf.shared.ai.mcts2.TreeNode(null, gameState, null);
        org.ctf.shared.ai.mcts2.MCTS mcts2 = new org.ctf.shared.ai.mcts2.MCTS(root2,config);
        Move move2 = mcts2.getMove(milis,config.C);
        mcts2.root.printGrids();
        System.out.println(mcts2.printResults(move2));
        return move2;
      default:
        return RandomAI.pickMoveComplex(gameState, new ReferenceMove(null, new int[] {0, 0}))
            .toMove();
    }
  }

  private void normaliseGameState() {
    for (int i = 0; i < this.gameState.getTeams().length; i++)
      this.gameState.getTeams()[i].setId("" + i);
  }
}
