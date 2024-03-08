package org.ctf.tl.exceptions;

/**
 * Represents a special exception 404 Not Found (URL Error)
 * @author rsyed
 */

public class URLError extends RuntimeException {
    public URLError(String message){
        super(message);
    }

    public URLError(int message){
        super(Integer.toString(message));
    }
}