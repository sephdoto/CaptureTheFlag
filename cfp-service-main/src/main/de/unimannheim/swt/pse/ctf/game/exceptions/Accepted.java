package de.unimannheim.swt.pse.ctf.game.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Represents a special exception (Accepted/OK)
 * that is marked with a HTTP status if thrown.
 * @author rsyed
 */
@ResponseStatus(value = HttpStatus.OK, reason="OK")
public class Accepted extends RuntimeException {

}
