package org.ctf.client.state.data.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Represents a special exception (Game session not found)
 * that is marked with a HTTP status if thrown.
 * @author rsyed
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason="Game session not found")
public class SessionNotFound extends RuntimeException {

}
