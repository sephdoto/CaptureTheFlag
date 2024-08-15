package org.ctf.shared.state.data.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Represents a special exception (game is over)
 * that is marked with a HTTP status if thrown.
 * @author Marcus Kessel
 */
@ResponseStatus(value = HttpStatus.GONE, reason="Game is over")
public class GameOver extends RuntimeException {

  private static final long serialVersionUID = 3743679284247002915L;
    
}