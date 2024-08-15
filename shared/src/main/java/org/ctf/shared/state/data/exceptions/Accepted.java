package org.ctf.shared.state.data.exceptions;

/**
 * Represents a special exception (Accepted/OK) that is marked with a HTTP status if thrown.
 *
 * @author rsyed
 */
public class Accepted extends RuntimeException {
  private static final long serialVersionUID = 5027270045199320945L;

  /**
   * Extension to the Exception to carry a message forward for the UI
   *
   * @author rsyed
   */
  public Accepted(String message) {
    super(message);
  }

  /**
   * Default Constructor
   *
   * @author rsyed
   */
  public Accepted() {
    super();
  }

  /**
   * Extension to the Exception to carry an int forward for the UI
   *
   * @author rsyed
   */
  public Accepted(int message) {
    super(Integer.toString(message));
  }
}
