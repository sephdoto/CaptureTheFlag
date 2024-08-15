package org.ctf.shared.state.data.exceptions;

/**
 * Represents a special exception 404 Not Found (URL Error)
 * @author rsyed
 */

public class URLError extends RuntimeException {
    private static final long serialVersionUID = -6615649417005142789L;

    public URLError(String message){
        super(message);
    }

    public URLError(int message){
        super(Integer.toString(message));
    }
}