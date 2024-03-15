package org.ctf.shared.state.data.exceptions;

/**
 * Represents a special exception (Accepted/OK)
 * that is marked with a HTTP status if thrown.
 * @author rsyed
 */

public class Accepted extends RuntimeException {
    public Accepted(String message){
        super(message);
    }

    public Accepted(int message){
        super(Integer.toString(message));
    }
}