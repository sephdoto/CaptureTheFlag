package org.ctf.shared.state.data.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Represents a special exception (Game session not found) that is marked with a HTTP status if
 * thrown.
 *
 * @author rsyed
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Game session not found")
public class SessionNotFound extends RuntimeException {
  private static final long serialVersionUID = 915909089290168756L;

  /**
   * Extension to the Exception to carry a message forward for the UI
   *
   * @author rsyed
   */
  public SessionNotFound(String message) {
    super(message);
  }

  /**
   * Default Constructor
   *
   * @author rsyed
   */
  public SessionNotFound() {
    super();
  }
}
