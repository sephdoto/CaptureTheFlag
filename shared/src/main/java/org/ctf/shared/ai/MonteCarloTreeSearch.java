package org.ctf.shared.ai;

import java.util.concurrent.atomic.AtomicInteger;
import org.ctf.shared.state.Move;

/**
 * All useful information that should be possible to extract from a MCTS is given here.
 * All MCTS provide the information the same way, making it easy to work with.
 * 
 * @author sistumpf
 */
public interface MonteCarloTreeSearch {
  /**
   * Starts a Monte Carlo Tree Search from a given state of the game,
   * if the given time runs out the best calculated move is returned.
   * 
   * @param time in milliseconds the algorithm is allowed to take
   * @return the algorithms choice for the best move
   */
  public Move getMove(int milis);
  
  /**
   * Returns the root of the current MCTS instance
   * 
   * @return the current root
   */
  public MonteCarloTreeNode getRoot();
  
  /**
   * Sets the root of this MCTS instance
   *
   * @param MonteCarloTreeNode new root node
   */
  public void setRoot(MonteCarloTreeNode root);
  
  /**
   * Returns the expansion counter.
   * The expansion counter increments with every expanded node in the Search Tree.
   * Nodes in simulations are not included.
   * 
   * @return the current expansion counter
   */
  public AtomicInteger getExpansionCounter();
  
  /**
   * Sets the current expansion counter to a given int value.
   * 
   * @param expansionCounter new expansion counter value
   */
  public void setExpansionCounter(int expansionCounter);
  
  /**
   * Returns the simulation counter.
   * The simulation counter increments with every complete simulation,
   * meaning a simulation which ends in a clear winner.
   * 
   * @return the current simulation counter
   */
  public AtomicInteger getSimulationCounter();
  
  /**
   * Sets the current simulation counter to a given int value.
   * 
   * @param simulationCounter new simulation counter value
   */
  public void setSimulationCounter(int simulationCounter);
  
  /**
   * Returns the heuristic counter.
   * The heuristic counter increments with every incomplete simulation,
   * meaning a simulation that reached its maximum depth without a clear winner,
   * as the heuristic gets applied.
   * 
   * @return the current heuristic counter
   */
  public AtomicInteger getHeuristicCounter();
  
  /**
   * Sets the current heuristic counter to a given int value.
   * 
   * @param simulationCounter new heurstic counter value
   */
  public void setHeuristicCounter(int heuristicCounter);
  
  /**
   * Returns a String that contains all important simulation results.
   * Its main use is debugging.
   * 
   * @param move that got made
   * @return a String containing important information about the search tree.
   */
  public String printResults(Move move);
}
