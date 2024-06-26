package org.ctf.ui.server;

/**
 * Defines what functionality the server class has to provide
 *
 * @author Raffay Syed
 */
public interface ServerInterface {

  /**
   * Starts a Server instance and checks if it is functional.
   *
   * @param port the port you want to start a server at
   * @return true if server is active and ready to make sessions, false if something went wrong
   * @author rsyed
   */
  public boolean startServer(String port);

  public int checkStatus();

  public boolean restartServer();

  public boolean stopServer();

  public boolean killServer();
}
