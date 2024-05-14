package org.ctf.shared.ai;

import org.ctf.shared.state.GameState;

/**
 * All useful information that should be possible to extract from a TreeNode is given here.
 * All TreeNodes provide the information the same way, making it easy to work with.
 * 
 * @author sistumpf
 */
public interface MonteCarloTreeNode {
  public MonteCarloTreeNode[] getChildren();
  public double getV();
  public GameState getGameState();
  public void setParent(MonteCarloTreeNode node);
}
