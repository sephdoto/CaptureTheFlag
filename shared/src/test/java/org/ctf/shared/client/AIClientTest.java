package org.ctf.shared.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unimannheim.swt.pse.ctf.CtfApplication;
import java.io.IOException;

import org.ctf.shared.ai.AIConfig;
import org.ctf.shared.client.lib.ServerDetails;
import org.ctf.shared.client.lib.ServerManager;
import org.ctf.shared.client.service.CommLayer;
import org.ctf.shared.constants.Enums;
import org.ctf.shared.constants.Enums.AI;
import org.ctf.shared.constants.Enums.Port;
import org.ctf.shared.state.data.map.MapTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AIClientTest {

  static CommLayer comm = new CommLayer();
  final MapTemplate template = createGameTemplate();
  ServerManager server = new ServerManager(comm, new ServerDetails("localhost", "9992"), template);

  @BeforeAll
  static void setup() {
    String[] args = new String[] {"--server.port=9992"};
    CtfApplication.main(args);
  }

  @Test
  void AIClientInit() {
    AIClient aiClient1 =
        AIClientStepBuilder.newBuilder()
            .enableRestLayer(false)
            .onLocalHost()
            .onPort("9992")
            .aiPlayerSelector(AI.MCTS, new AIConfig())
            .enableSaveGame(false)
            .gameData(server.getGameSessionID(), "Team1")
            .build();

    AIClient aiClient2 =
        AIClientStepBuilder.newBuilder()
            .enableRestLayer(true)
            .onRemoteHost("127.0.0.1")
            .onPort(Port.AICLIENTTEST)
            .aiPlayerSelector(AI.MCTS, new AIConfig())
            .enableSaveGame(true)
            .gameData(server.getGameSessionID(), "Team2")
            .build();

            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                // TODO: handle exception
            }
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
