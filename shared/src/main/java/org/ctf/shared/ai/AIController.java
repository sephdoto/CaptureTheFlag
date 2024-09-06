package org.ctf.shared.ai;

import org.ctf.shared.ai.GameUtilities.InvalidShapeException;
import org.ctf.shared.ai.GameUtilities.NoMovesLeftException;
import org.ctf.shared.ai.random.RandomAI;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.AI;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;

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
  
  private boolean backgroundCalc;
  private boolean bctLock;
  private BackgroundCalculatorThread bct;
  
  private boolean alreadyshutdown = false;
  
  
  /**
   * The controller assumes moves use the row,column or [y,x] coordinates.
   * 
   * @param gameState
   * @param ai
   * @param config
   * @param thinkingTime
   * @param backgroundCalc
   */
  public AIController(GameState gameState, AI ai, AIConfig config, int thinkingTime, boolean backgroundCalc) {
    this.backgroundCalc = backgroundCalc;
    this.bctLock = false;
    setActive(false);
    setThinkingTime(thinkingTime);
    setThinkingTime(Constants.forceAiThinkingTime);
    this.setAi(config == null ? AI.RANDOM : ai);
    this.normalizedGameState = new GameStateNormalizer(gameState, true);
    this.setConfig(config);
    if(config != null) initMCTS();
    
    if (gameState.getCurrentTeam() < 0)
      return;
    setActive(true);
  }

  /**
   * Initializes the according MCTS.
   * It creates a new MCTS instance with the Controllers config and normalized GameState.
   */
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
          default: return;
      }
      System.out.println(ai + " re-initiated");
    } catch (Exception e ) {
      System.err.println("error initializing MCTS");
      e.printStackTrace();
    }
  }

  /**
   * Tries to update with Move to build upon an older Search Tree.
   * If it fails, it updates the GameState.
   * 
   * @param gameState GameState to update with
   * @param move Move to update with
   * @return true if the update was successful
   */
  public synchronized boolean update(GameState gameState, Move move) {
    if(!isNewGameState(gameState))
      return false;
    
    interruptBct();
    
    if (gameState.getCurrentTeam() < 0) {
      shutDown();
      return false;
    } else {
      this.setActive(true);
    }
    
    boolean update = false;
    try {
      update = update(move);
    } catch (Exception e) {e.printStackTrace();}
    
    if(!update) {
      if(ai != AI.RANDOM)
        System.out.println("couldnt update " + ai + " with Move, trying GameState");
      update = update(gameState);
    }
    if(!update) {
      System.out.println("couldnt update " + ai + " with GameState");
    }
    
    startBct();
    return update;
  }
  
  /**
   * Update the Controller with a new GameState.
   * 
   * @param gameState
   */
  public boolean update(GameState gameState) {
    if(gameState == null) return false;
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
      
      Move normove = this.normalizedGameState.normalizedMove(move);

      MonteCarloTreeNode[] children = getMcts().getRoot().getChildren();
      for(int i=0; i<children.length; i++) {
        if(children[i] == null) continue;
        Move childMove = children[i].getGameState().getLastMove();
        if(GameUtilities.moveEquals(childMove, normove)) {
          getMcts().setRoot(children[i]);
          getMcts().getRoot().getParent().getChildren()[i] = null;
          getMcts().getRoot().setParent(null);
          
          //(Re)set counter
          getMcts().setExpansionCounter(getMcts().getRoot().getNK());
          getMcts().setHeuristicCounter(0);
          getMcts().setSimulationCounter(0);
          
          // TODO start of todo block
          this.normalizedGameState.getNormalizedGameState().setLastMove(normove);
          this.normalizedGameState.getNormalizedGameState().setCurrentTeam(GameUtilities.getTeamIndex(this.normalizedGameState.getNormalizedGameState(), normove.getTeamId()));
          GameUtilities.toNextTeam(this.normalizedGameState.getNormalizedGameState());
          this.normalizedGameState.getOriginalGameState().setLastMove(move);
          this.normalizedGameState.getOriginalGameState().setCurrentTeam(GameUtilities.getTeamIndex(this.normalizedGameState.getOriginalGameState(), move.getTeamId()));
          GameUtilities.toNextTeam(this.normalizedGameState.getOriginalGameState());
          // TODO end of todo block.
          // might update the complete gameState later, but would take much longer.
          
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

  /**
   * Shuts down this AI Controller.
   * To start it again, a new one must be created.
   * Interrupts the background calculator Thread, the MCTS calculation and sets MCTS to null.
   * Calls the Garbage Collector.
   */
  public void shutDown() {
    if(!alreadyshutdown) {
      alreadyshutdown = true;
      System.out.println(this.ai +"-AI shut down. Collecting Garbage ...");
      this.setActive(false);
      interruptBct();
      if(this.mcts != null) {
        this.mcts.shutdown();
        this.mcts = null;
      }
      //TODO
      System.gc();
    }
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
      bctLock = true; interruptBct();
      move = getMcts().getMove(thinkingTime);
      bctLock = false; startBct();
//      System.out.println(getMcts().printResults(move));
//      getMcts().getRoot().printGrid();
    } else {
      move = RandomAI.pickMoveComplex(getNormalizedGameState().getNormalizedGameState(), new ReferenceMove(null, new int[] { 0, 0 })).toMove();
      try {
        Thread.sleep(Constants.randomAiSleepTimeMS);      
      } catch (InterruptedException e) {}
    }
    move.setTeamId(move.getPieceId().split(":")[1].split("_")[0]);
    return move == null ? null : getNormalizedGameState().unnormalizeMove(move);
  }
  
  /**
   * Compares the current and new GameStates last moves.
   * Assumes the newState is not normalized.
   * If they are equal, the current team gets compared, if they are equal the GameState is not new.
   * This must happen that way, as a team can giveUp, the last Move stays the same, but the current Team changes.
   *
   * @author sistumpf
   * @return true if newState is a new GameState
   * @return false if the game has not started yet or newState is not new
   */
  protected boolean isNewGameState(GameState newState) {
    if(newState.getCurrentTeam() == -1 && normalizedGameState.getOriginalGameState().getCurrentTeam() == -1)
      return false;
    
    if(GameUtilities.moveEquals(newState.getLastMove(), normalizedGameState.getOriginalGameState().getLastMove())) {
      return newState.getCurrentTeam() != normalizedGameState.getOriginalGameState().getCurrentTeam();
    }
    
    return true;
  }

  /**
   * Initialized a new BackgroundCalculatorThread if none is active, then starts it.
   * Only starts if the current AI is not dead, is allowed to calculate in the background,
   * Constants.FULL_AI_POWER allows it, and it has not been locked by getNextMove().
   */
  public void startBct() {
    if(bct == null && this.isActive() && backgroundCalc && Constants.FULL_AI_POWER && !bctLock) {
      bct = new BackgroundCalculatorThread(mcts);
      bct.start();
    }
  }
  
  /**
   * Interrupts the BackgroundCalculatorThread, then stops this Thread till the other one has been interrupted.
   */
  public void interruptBct() {
    if(bct != null) {
      bct.interrupt();
      while(bct != null && bct.getMcts() != null)
        sleep(1);
      bct = null;
    }
  }
  
  /**
   * A shorter way to call Thread.sleep, contains the Error handling
   * 
   * @param ms milliseconds to be asleep for
   */
  private void sleep(int ms) {
    try {
      Thread.sleep(1);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
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
   * Reduces thinkingTime by 10% (unless its Experimental, then 30%) and sets it as the attribute.
   * 
   * @param thinkingTime in s
   */
  public void setThinkingTime(int thinkingTime) {
    if(thinkingTime < 0) return;
    thinkingTime = thinkingTime == 0 ? 500 : thinkingTime * 1000;
    
    int percentage = this.ai == AI.EXPERIMENTAL ? 70 : 90;
    thinkingTime = (int)Math.round((thinkingTime / 100.) * percentage);
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
