package org.ctf.ui.server;

import org.ctf.shared.client.lib.ServerChecker;
import org.ctf.shared.client.lib.ServerDetails;
import org.ctf.shared.client.lib.ServerManager;
import org.ctf.shared.constants.Constants;
import org.ctf.ui.data.SceneHandler;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ConfigurableApplicationContext;
import de.unimannheim.swt.pse.ctf.CtfApplication;

/**
 * Implementation of the ServerInterface
 *
 * @author rsyed
 */
public class ServerContainer implements ServerInterface {
  ConfigurableApplicationContext appContext;
  ServerManager seManager;
  String port;

  /**
   * Starts a Server instance and checks if it is functional.
   *
   * @param port the port you want to start a server at
   * @return true if server is active and ready to make sessions, false if something went wrong
   * @throws PortInUseException if the selected port is already in use
   */
  @Override
  public boolean startServer(String port) {
    this.port = port;
    try {
      appContext = SpringApplication.run(CtfApplication.class, "--server.port=" + port);
      SceneHandler.setTitle("CFP 14" + " Local Server is active @ " + port);
    } catch (BeanCreationException | ApplicationContextException ex) {
      SceneHandler.setTitle("CFP 14" + " Local Server is already active @ " + port);
      throw new PortInUseException(port);
    }
    if (appContext.isRunning()) {
      if(checkStatus()) {
        Constants.userSelectedLocalServerPort = port;
      } else {
        return false;
      }
    }
    return false;
  }

  @Override
  public boolean checkStatus() {
    return new ServerChecker().isServerActive(new ServerDetails("localhost", port));
  }

  @Override
  public boolean restartServer() {
    if (appContext != null) {
      appContext.refresh();
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean stopServer() {
    if (appContext != null && checkStatus()) {
      appContext.close();
      return true;
    } else {
      return false;
    }
  }
}
