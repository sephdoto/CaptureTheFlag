package org.ctf.ui.server;

/**
 * Exception to let the System know that the port is already in use
 *
 * @author rsyed
 * @author sistumpf
 */
public class PortInUseException extends RuntimeException {
  private static final long serialVersionUID = 3501018038690722735L;
  String port;
  
  public PortInUseException(String port) {
    this.port = port;
  }
  
  @Override
  public String getLocalizedMessage(){
    return "Port " + port + " is already in use.";
  }
}
