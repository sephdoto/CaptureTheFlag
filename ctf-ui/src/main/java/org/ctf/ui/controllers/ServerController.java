package org.ctf.ui.controllers;

import de.unimannheim.swt.pse.ctf.CtfApplication;
import org.ctf.shared.client.lib.ServerChecker;
import org.ctf.shared.client.lib.ServerDetails;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * This class responsible for handling a server. Has the capability to start and close a server
 *
 * @author rsyed
 */
public class ServerController {

  ConfigurableApplicationContext ctx;

  /**
   * Stars the a server at the specified port
   *
   * @param port The port the server should use to start
   * @return true if server starts properly, false otherwise
   * @author rsyed
   */
  public boolean startServer(String port) {
    String[] args = new String[] {"--server.port=" + port};
    try {
      ctx = new SpringApplication(CtfApplication.class).run(args);
    } catch (Exception e) {
      return false;
    }
    boolean success = new ServerChecker().isServerActive(new ServerDetails("localhost", port));
    if (success) {
      // Constants.localServerPort = port;
    }
    return success;
  }

  /**
   * Closes the server this instance of Server Controller is pointing to
   *
   * @return 0 if server closes properly
   * @author rsyed
   */
  public int closeServer() {
    System.exit(SpringApplication.exit(ctx, () -> 0));
    if (ctx == null) {
      return 0;
    } else {
      return -1;
    }

    // Alt Method
    // return SpringApplication.exit(ctx, () -> 0);
  }

  // Uncomment the code below to test
  public static void main(String[] args) {
    ServerController sc = new ServerController();
    System.out.println(sc.startServer("8888"));
    System.out.println(sc.closeServer());
  }
}
