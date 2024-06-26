package org.ctf.ui.server;

import de.unimannheim.swt.pse.ctf.CtfApplication;
import org.ctf.shared.client.lib.ServerChecker;
import org.ctf.shared.client.lib.ServerDetails;
import org.ctf.shared.client.lib.ServerManager;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class ServerContainer implements ServerInterface {
  SpringApplication application;
  ConfigurableApplicationContext appContext;
  ServerManager seManager;

  @Override
  public boolean startServer(String port) {
    appContext = SpringApplication.run(CtfApplication.class, "--server.port=" + port);
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
