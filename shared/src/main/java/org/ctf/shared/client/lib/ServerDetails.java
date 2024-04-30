package org.ctf.shared.client.lib;

/**
 * Simplistic Data Object to hold the ip and port of servers
 *
 * @author rsyed
 */
public class ServerDetails {
  private String host;
  private String port;

  /**
   * Default constructor to assign the object a Host ip and port
   *
   * @param host an IP
   * @param port a Port
   * @author rsyed
   */
  public ServerDetails(String host, String port) {
    this.host = host;
    this.port = port;
  }

  /**
   * Checks if the object is holding "localhost" as its IP
   *
   * @author rsyed
   */
  public boolean isLocalhost() {
    if (host.equals("localhost")) {
      return true;
    } else {
      return false;
    }
  }

  // Getters and setters
  public String getHost() {
    return host;
  }

  public String getPort() {
    return port;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public void setPort(String port) {
    this.port = port;
  }
}
