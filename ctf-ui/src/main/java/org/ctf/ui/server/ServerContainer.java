package org.ctf.ui.server;

import de.unimannheim.swt.pse.ctf.CtfApplication;

import java.io.IOException;

import org.apache.catalina.core.ApplicationContext;
import org.ctf.shared.client.lib.ServerChecker;
import org.ctf.shared.client.lib.ServerDetails;
import org.ctf.shared.client.lib.ServerManager;
import org.ctf.shared.client.service.RestClientLayer;
import org.ctf.shared.state.data.map.MapTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ServerContainer implements ServerInterface {
  SpringApplication application;
  ConfigurableApplicationContext  appContext;
  ServerManager seManager;

  @Override
  public boolean startServer(String port) {
    appContext = SpringApplication.run(CtfApplication.class, "--server.port=" + port);
    if(appContext.isRunning()){
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

  private MapTemplate createGameTemplate() {
    ObjectMapper objectMapper = new ObjectMapper();
    MapTemplate mapTemplate = null;
    try {
      mapTemplate =
          objectMapper.readValue(
              getClass().getResourceAsStream("/maptemplates/10x10_2teams_example.json"),
              MapTemplate.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return mapTemplate;
  }
}
