package org.ctf.shared.state.data.exceptions;

public class NoProperSupport extends RuntimeException {
  /**
   * Represents a special exception used when checking weather or not the server return a String encoded INT
   * as team turn indicator
   *
   * @author rsyed
   */
  public NoProperSupport(String message) {
    super(message);
  }

  public NoProperSupport(int message) {
    super(Integer.toString(message));
  }

  public NoProperSupport() {
    super();
  }
}
