package org.ctf.shared.state.data.exceptions;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

/**
 * Class to handle API Errors thrown by Restful API Uses a BUILDER class to create and use Objects
 *
 * @author rsyed
 */
@Deprecated
public class ApiError extends RuntimeException {

  private static final long serialVersionUID = -3558730812414252748L;
  private HttpStatus currentStatus; // required
  private LocalDateTime timeStamp;
  private String message; // optional
  private String debugMessage; // optional

  /**
   * Private constructor which gets the time as soon as an error is thrown
   *
   * @param builder gets an object of its builder class
   */
  private ApiError(ApiErrorBuilder builder) {
    this.timeStamp = builder.timeStamp;
    this.currentStatus = builder.currentStatus;
    this.message = builder.message;
    this.debugMessage = builder.debugMessage;
  }

  /** Public getter Methods for immutability and reading data */
  public HttpStatus getCurrentStatus() {
    return currentStatus;
  }

  public LocalDateTime getTimeStamp() {
    return timeStamp;
  }

  public String getMessage() {
    return message;
  }

  public String getDebugMessage() {
    return debugMessage;
  }

  /**
   * Uses a BUILDER class to create and use Objects
   *
   * @author rsyed
   */
  public static class ApiErrorBuilder extends Throwable {

    private static final long serialVersionUID = -7575971413881915287L;
    // required parameters
    private HttpStatus currentStatus;
    private LocalDateTime timeStamp;

    // optional parameters
    private String message;
    private String debugMessage;

    public ApiErrorBuilder(int status) {
      timeStamp = LocalDateTime.now();
      this.currentStatus = HttpStatus.valueOf(status);
    }

    public ApiErrorBuilder message(String message) {
      this.message = message;
      return this;
    }

    public ApiErrorBuilder debugMessage(Throwable ex) {
      this.message = "Unexpected error";
      this.debugMessage = ex.getLocalizedMessage();
      return this;
    }

    public ApiError build() {
      ApiError error = new ApiError(this);
      return error;
    }
  }
}
