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
  public Move getMove(int milis);
  public MonteCarloTreeNode getRoot();
  public void setRoot(MonteCarloTreeNode root);
  public AtomicInteger getExpansionCounter();
  public void setExpansionCounter(int expansionCounter);
}
