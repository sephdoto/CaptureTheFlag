package org.ctf.shared.state.data.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Represents a special exception (game session not found)
 * that is marked with a HTTP status if thrown.
 * @author Marcus Kessel
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason="Game session not found")
public class GameSessionNotFound extends RuntimeException {

  private static final long serialVersionUID = -4418570489882759357L;}
