package org.ctf.shared.ai;

import org.ctf.shared.ai.GameUtilities.InvalidShapeException;
import org.ctf.shared.ai.GameUtilities.NoMovesLeftException;
import org.ctf.shared.ai.random.RandomAI;
import org.ctf.shared.constants.Enums.AI;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.data.exceptions.GameOver;

/**
 * This class requests a GameState from the server, uses one of the implemented
 * AIs to generate the
 * next move and finally returns said move.
 *
 * @author sistumpf
 */
public class AIController {
  private AIConfig config;
  private AI ai;
  private boolean active;
  private int thinkingTime;
  private GameStateNormalizer normalizedGameState;
  
  /**
   * The controller assumes moves use the row,column or [y,x] coordinates.
   * 
   * @param gameState
   * @param ai
   * @param config
   * @param thinkingTime
   */
  public AIController(GameState gameState, AI ai, AIConfig config, int thinkingTime) {
    this.ai = config == null ? AI.RANDOM : ai;
    this.thinkingTime = thinkingTime * 1000;
    if (gameState.getCurrentTeam() < 0)
      return;
    this.normalizedGameState = new GameStateNormalizer(gameState, true);
    this.config = config;
    // normaliseGameState();
    this.active = true;
  }

  public void update(GameState gameState) {
    this.normalizedGameState = new GameStateNormalizer(gameState, true);
    if (gameState.getCurrentTeam() < 0) {
      this.active = false;
      shutDown();
    } else {
      this.active = true;
    }
  }

  public void shutDown() {
    System.out.println("AI shut down");
    throw new GameOver();
  }

  public Move getNextMove() throws NoMovesLeftException, InvalidShapeException {
    if (!this.active)
      return null;
    int milis = thinkingTime;
    if (thinkingTime == 0) {
      milis = 500;
    } else {
      milis = thinkingTime;
    }

    switch (this.ai) {
      case RANDOM:
        Move moveR = RandomAI.pickMoveComplex(normalizedGameState.getNormalizedGameState(), new ReferenceMove(null, new int[] { 0, 0 })).toMove();
        return normalizedGameState.unnormalizeMove(moveR);
      case MCTS:
        org.ctf.shared.ai.mcts.TreeNode root = new org.ctf.shared.ai.mcts.TreeNode(
            null, normalizedGameState.getNormalizedGameState(), null, new ReferenceMove(null, new int[] { 0, 0 }));
        org.ctf.shared.ai.mcts.MCTS mcts = new org.ctf.shared.ai.mcts.MCTS(root, config);
        Move move = mcts.getMove(milis, config.C);
        mcts.root.printGrid();
        System.out.println(mcts.printResults(move));
        return normalizedGameState.unnormalizeMove(move);
      case IMPROVED:
        org.ctf.shared.ai.mcts3.TreeNode root3 = new org.ctf.shared.ai.mcts3.TreeNode(null,
            new org.ctf.shared.ai.mcts3.ReferenceGameState(normalizedGameState.getNormalizedGameState()), null, new ReferenceMove(null, new int[2]));
        org.ctf.shared.ai.mcts3.MCTS mcts3 = new org.ctf.shared.ai.mcts3.MCTS(root3, config);
        Move move3 = mcts3.getMove(milis, config.C);
        mcts3.root.printGrid();
        System.out.println(mcts3.printResults(move3));
        return normalizedGameState.unnormalizeMove(move3);
      case EXPERIMENTAL:
        org.ctf.shared.ai.mcts2.TreeNode root2 = new org.ctf.shared.ai.mcts2.TreeNode(null, normalizedGameState.getNormalizedGameState(), null);
        org.ctf.shared.ai.mcts2.MCTS mcts2 = new org.ctf.shared.ai.mcts2.MCTS(root2, config);
        Move move2 = mcts2.getMove(milis, config.C);
        mcts2.root.printGrids();
        System.out.println(mcts2.printResults(move2));
        return normalizedGameState.unnormalizeMove(move2);
      default:
        Move moveR2 = RandomAI.pickMoveComplex(normalizedGameState.getNormalizedGameState(), new ReferenceMove(null, new int[] { 0, 0 })).toMove();
        return normalizedGameState.unnormalizeMove(moveR2);
    }
  }
}
