package org.ctf.shared.ai;

import java.util.Arrays;
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
  private MonteCarloTreeSearch mcts;
  
  
  /**
   * The controller assumes moves use the row,column or [y,x] coordinates.
   * 
   * @param gameState
   * @param ai
   * @param config
   * @param thinkingTime
   */
  public AIController(GameState gameState, AI ai, AIConfig config, int thinkingTime) {
    setActive(false);
    this.setThinkingTime(thinkingTime <= 0 ? 100 : thinkingTime * 1000);
    this.setAi(config == null ? AI.RANDOM : ai);
    this.normalizedGameState = new GameStateNormalizer(gameState, true);
    this.setConfig(config);
    if(config != null) initMCTS();
    
    if (gameState.getCurrentTeam() < 0)
      return;
    setActive(true);
  }
  
  @SuppressWarnings("incomplete-switch")
  protected void initMCTS() {
    switch(ai) {
      case MCTS:
        org.ctf.shared.ai.mcts.TreeNode root = new org.ctf.shared.ai.mcts.TreeNode(
            null, normalizedGameState.getNormalizedGameState(), null, new ReferenceMove(null, new int[] { 0, 0 }));
        setMcts(new org.ctf.shared.ai.mcts.MCTS(root, getConfig()));
        break;
      case IMPROVED:
        org.ctf.shared.ai.mcts3.TreeNode root3 = new org.ctf.shared.ai.mcts3.TreeNode(null,
            new org.ctf.shared.ai.mcts3.ReferenceGameState(
                normalizedGameState.getNormalizedGameState()), null, new ReferenceMove(null, new int[2]));
        setMcts(new org.ctf.shared.ai.mcts3.MCTS(root3, getConfig()));
        break;
      case EXPERIMENTAL:
        org.ctf.shared.ai.mcts2.TreeNode root2 = new org.ctf.shared.ai.mcts2.TreeNode(null, 
            normalizedGameState.getNormalizedGameState(), null);
        setMcts(new org.ctf.shared.ai.mcts2.MCTS(root2, getConfig()));
        break;
    }
  }

  /**
   * Update the Controller with a new GameState.
   * 
   * @param gameState
   */
  public boolean update(GameState gameState) {
    if (gameState.getCurrentTeam() < 0) {
      this.setActive(false);
      shutDown();
      return false;
    } else {
      this.setActive(true);
    }
    this.normalizedGameState = new GameStateNormalizer(gameState, true);
    initMCTS();
    return true;
  }
  
  /**
   * Update the Controller with a new Move.
   * Only works with an MCTS AI.
   * It is assumed that the move is unnormalized.
   * 
   * @param gameState
   */
  public boolean update(Move move) {
    if(this.ai == AI.HUMAN || this.ai == AI.RANDOM) return false;
    move = this.normalizedGameState.normalizedMove(move);
    
    MonteCarloTreeNode[] children = getMcts().getRoot().getChildren();
    for(int i=0; i<children.length; i++) {
      Move childMove = children[i].getGameState().getLastMove();
      if(moveEquals(childMove, move)) {
        getMcts().setRoot(children[i]);
        getMcts().getRoot().setParent(null);
        return true;
      }
    }
    return false;
  }

  public void shutDown() {
    System.out.println("AI shut down");
    throw new GameOver();
  }

  /**
   * Calculates the next move with the chose AI
   * 
   * @return the next best move made from the chosen AI
   * @throws NoMovesLeftException
   * @throws InvalidShapeException
   */
  public Move getNextMove() throws NoMovesLeftException, InvalidShapeException {
    if (!this.isActive())
      return null;

    Move move;
    
    if(getAi() == AI.MCTS || getAi() == AI.IMPROVED || getAi() == AI.EXPERIMENTAL) {
      move = getMcts().getMove(thinkingTime, getConfig().C);
    } else {
      move = RandomAI.pickMoveComplex(getNormalizedGameState().getNormalizedGameState(), new ReferenceMove(null, new int[] { 0, 0 })).toMove();
    }
    return getNormalizedGameState().unnormalizeMove(move);
  }
  
  /**
   * Depending on the contained piece and its new position, two moves are checked for equality.
   * 
   * @param move1
   * @param move2
   * @return true if move1 and move2 are equal
   */
  public boolean moveEquals(Move move1, Move move2) {
    if(move1.getPieceId().equals(move2.getPieceId()))
      if(Arrays.equals(move1.getNewPosition(), move2.getNewPosition()))
        return true;
    return false;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  /**
   * @return millis the AI got to make a move
   */
  public int getThinkingTime() {
    return thinkingTime;
  }

  /**
   * @param thinkingTime in millis
   */
  public void setThinkingTime(int thinkingTime) {
    this.thinkingTime = thinkingTime;
  }

  public AI getAi() {
    return ai;
  }

  public void setAi(AI ai) {
    this.ai = ai;
  }

  public AIConfig getConfig() {
    return config;
  }

  public void setConfig(AIConfig config) {
    this.config = config;
  }

  public GameStateNormalizer getNormalizedGameState() {
    return normalizedGameState;
  }

  public MonteCarloTreeSearch getMcts() {
    return mcts;
  }

  public void setMcts(MonteCarloTreeSearch mcts) {
    this.mcts = mcts;
  }
}
