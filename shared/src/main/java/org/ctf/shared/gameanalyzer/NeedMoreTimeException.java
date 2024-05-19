package org.ctf.shared.gameanalyzer;

/**
 * Gets thrown when a NullPointerException is thrown, because not enough MCTS nodes could be expanded.
 * 
 * @author Simon Stumpf
 */
public class NeedMoreTimeException extends NullPointerException {
  private static final long serialVersionUID = 2186132386372176637L;
  int time;
  

  public NeedMoreTimeException() {
    super();
    time = -1;
  }
  
  /**
   * If time gets set the localized message will contain the thinking time.
   * 
   * @param time
   */
  public void mentionTime(int time) {
    this.time = time;
  }
  
  @Override
  public String getLocalizedMessage() {
    return (time >= 0 ? "" + time : "the given") + " ms were not enough time to expand all nodes. Please allocate more time.";
  }
}
