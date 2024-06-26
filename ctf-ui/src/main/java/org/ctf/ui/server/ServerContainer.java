package org.ctf.ui.server;

import de.unimannheim.swt.pse.ctf.CtfApplication;
import org.ctf.shared.client.lib.ServerChecker;
import org.ctf.shared.client.lib.ServerDetails;
import org.ctf.shared.client.lib.ServerManager;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Implementation of the ServerInterface
 *
 * @author rsyed
 */
public class ServerContainer implements ServerInterface {
  ConfigurableApplicationContext appContext;
  ServerManager seManager;

  /**
   * Starts a Server instance and checks if it is functional.
   *
   * @param port the port you want to start a server at
   * @return true if server is active and ready to make sessions, false if something went wrong
   * @throws PortInUseException if the selected port is already in use
   */
  @Override
  public boolean startServer(String port) {

    try {
      appContext = SpringApplication.run(CtfApplication.class, "--server.port=" + port);
    } catch (BeanCreationException ex) {
      throw new PortInUseException();
    }
    if (appContext.isRunning()) {
      return new ServerChecker().isServerActive(new ServerDetails("localhost", port));
    }
    return false;
  }

  @Override
  public int checkStatus() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'checkStatus'");
  }

  @Override
  public boolean restartServer() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'restartServer'");
  }

  @Override
  public boolean stopServer() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'stopServer'");
  }

  @Override
  public boolean killServer() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'killServer'");
  }
}
