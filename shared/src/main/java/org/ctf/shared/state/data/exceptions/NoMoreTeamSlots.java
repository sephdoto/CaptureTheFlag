package org.ctf.shared.state.data.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Represents a special exception (no more team slots available)
 * that is marked with a HTTP status if thrown.
 * @author Marcus Kessel
 */
@ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS, reason="No team slots free")
public class NoMoreTeamSlots extends RuntimeException {}
