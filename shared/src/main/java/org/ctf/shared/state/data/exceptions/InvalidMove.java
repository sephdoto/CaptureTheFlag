package org.ctf.shared.state.data.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Represents a special exception (move request is invalid)
 * that is marked with a HTTP status if thrown.
 * @author Marcus Kessel
 */
@ResponseStatus(value = HttpStatus.CONFLICT, reason="Move is invalid")
public class InvalidMove extends RuntimeException {

  private static final long serialVersionUID = -75324629303478793L;}
