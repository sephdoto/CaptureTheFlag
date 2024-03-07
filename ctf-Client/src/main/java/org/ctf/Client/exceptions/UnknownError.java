package org.ctf.client.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Represents a special exception (Unknown error occurred)
 * that is marked with a HTTP status if thrown.
 * @author rsyed
 */
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason="Unknown error occurred")
public class UnknownError extends RuntimeException {
    
}
