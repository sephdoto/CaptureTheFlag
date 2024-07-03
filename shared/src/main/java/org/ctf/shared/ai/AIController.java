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
  protected int thinkingTime;
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
    setThinkingTime(thinkingTime < 0 ? 5000 : thinkingTime == 0 ? 700 : thinkingTime * 1000);
//    this.setThinkingTime(100);
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
    try {
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
    } catch (Exception e ) {
      System.out.println("error in init mcts");
      e.printStackTrace();
    }
  }

  /**
   * Tries to update with Move to build upon an older Search Tree.
   * If it fails, it updates the GameState.
   * 
   * !! temporarily set to only update with gameState, cant find the error !! TODO
   * 
   * @param gameState GameState to update with
   * @param move Move to update with
   * @return true if the update was successful
   */
  public boolean update(GameState gameState, Move move) {
    /*boolean update = false;
    try {
      update = update(move);
    } catch (Exception e) {e.printStackTrace();}
    if(!update)
      update = update(gameState);
    System.out.println(update);*/
    return update(gameState);
  }
  
  /**
   * Update the Controller with a new GameState.
   * 
   * @param gameState
   */
  public boolean update(GameState gameState) {
    if(gameState == null) return false;
    
    if(GameUtilities.moveEquals(gameState.getLastMove(), this.normalizedGameState.getOriginalGameState().getLastMove()))
      if (gameState.getCurrentTeam() == this.normalizedGameState.getOriginalGameState().getCurrentTeam())
        return false;
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
   * @param move a move that updates the gameState, if its different than the last one
   */
  public boolean update(Move move) {
    try {
    if(this.ai == AI.HUMAN || this.ai == AI.RANDOM) return false;
    if(GameUtilities.moveEquals(this.normalizedGameState.getOriginalGameState().getLastMove(), move)) return false;
    Move normove = this.normalizedGameState.normalizedMove(move);
//    this.normalizedGameState.getNormalizedGameState().setLastMove(normove);
    GameUtilities.toNextTeam(this.normalizedGameState.getNormalizedGameState());
//    this.normalizedGameState.getOriginalGameState().setLastMove(move);
    GameUtilities.toNextTeam(this.normalizedGameState.getOriginalGameState());
    
    MonteCarloTreeNode[] children = getMcts().getRoot().getChildren();
    for(int i=0; i<children.length; i++) {
      if(children[i] == null) continue;
      Move childMove = children[i].getGameState().getLastMove();
      if(GameUtilities.moveEquals(childMove, normove)) {
        getMcts().setRoot(children[i]);
        getMcts().getRoot().getParent().getChildren()[i] = null;
        getMcts().getRoot().setParent(null);
        System.gc();
        return true;
      }
    }
    return false;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  public void shutDown() {
    System.out.println(this.ai +"-AI shut down");
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
      move = getMcts().getMove(thinkingTime);
//      System.out.println(getMcts().printResults(move));
//      getMcts().getRoot().printGrid();
    } else {
      move = RandomAI.pickMoveComplex(getNormalizedGameState().getNormalizedGameState(), new ReferenceMove(null, new int[] { 0, 0 })).toMove();
    }
    return move == null ? null : getNormalizedGameState().unnormalizeMove(move);
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
   * Reduces thinkingTime by 10% and sets it as the attribute.
   * 
   * @param thinkingTime in millis
   */
  public void setThinkingTime(int thinkingTime) {
    thinkingTime = (int)Math.round((thinkingTime / 100.) * 90);
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
  
  public void setNormalizedGameState(GameStateNormalizer normalizedGameState) {
    this.normalizedGameState = normalizedGameState;
  }

  public MonteCarloTreeSearch getMcts() {
    return mcts;
  }

  public void setMcts(MonteCarloTreeSearch mcts) {
    this.mcts = mcts;
  }
}
