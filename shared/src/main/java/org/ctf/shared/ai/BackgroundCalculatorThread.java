package org.ctf.shared.ai;

import org.ctf.shared.constants.Constants;

/**
 * A Thread for continuously enlarging an MCTS Tree, until it gets interrupted.
 * 
 * @author sistumpf
 */
public class BackgroundCalculatorThread extends Thread{
  private MonteCarloTreeSearch mcts;
  private boolean interrupted;
  private double initialC;
  
  /**
   * Initialize a new Thread with an MCTS instance, to call getMove() later
   * 
   * @param mcts a MonteCarloTreeSearch instance
   */
  public BackgroundCalculatorThread(MonteCarloTreeSearch mcts) {
    interrupted = false;
    this.mcts = mcts;
    if(mcts != null)
      this.initialC = mcts.getConfig().C;
  }
  
  @Override
  public void run() {
    if(mcts != null) {
//      mcts.getConfig().C = 1000;
      
      while(!interrupted && Constants.FULL_AI_POWER) {
        mcts.getMove(10);
        //      System.out.println("BackgroundCalculator" + BackgroundCalculatorThread.this + " " +mcts.getExpansionCounter());
      }
      //    System.err.println("BackgroundCalculator" + BackgroundCalculatorThread.this + " stopped");
      mcts = null;
    }
  }
  
  @Override
  public void interrupt() {
    if(mcts != null)
      mcts.getConfig().C = initialC;
    this.interrupted = true;
//    System.err.println("BackgroundCalculator" + BackgroundCalculatorThread.this + " interrupted");
  }
  
  /**
   * Returns the saved MCTS instance, can be used to check if the Thread is still active,
   * as the MCTS instance will be null if the Thread gets interrupted.
   * 
   * @return the current MCTS instance
   */
  public MonteCarloTreeSearch getMcts() {
    return this.mcts;
  }
}
