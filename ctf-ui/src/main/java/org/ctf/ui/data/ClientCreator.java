package org.ctf.ui.data;

import org.ctf.shared.ai.AIConfig;
import org.ctf.shared.client.AIClient;
import org.ctf.shared.client.AIClientStepBuilder;
import org.ctf.shared.client.Client;
import org.ctf.shared.client.ClientStepBuilder;
import org.ctf.shared.constants.Enums.AI;
import org.ctf.ui.hostGame.CreateGameController;

/**
 * Creates Clients with the ServerManager from CreateGameController.
 * 
 * @author sistumpf
 */
public class ClientCreator {
  /**
   * Creates a Human-Client, adds it to the local client list, if it is a main client, sets it as such, then returns the Client.
   * 
   * @author sistumpf
   * @param isMain true if this is the main client. If true, it gets set as such and it will record the game
   * @param teamName the clients team name
   * @param enableRestLayer true to enable the rest layer
   * @param ip server ip
   * @param port server port
   * @param sessionID server session ID
   * @return human client
   */
  public static Client createHumanClient(boolean isMain, String teamName, boolean enableRestLayer, String ip, String port, String sessionID) {
    CreateGameController.setSessionID(sessionID);
    Client client = 
        ClientStepBuilder
        .newBuilder()
        .enableRestLayer(enableRestLayer)
        .onRemoteHost(ip)
        .onPort(port)
        .enableSaveGame(isMain)
        .enableAutoJoin(sessionID, teamName)
        .build();
    if (isMain) {
      ClientStorage.setMainClient(client);
      client.enableGameStateQueue(true);
    }
    ClientStorage.addLocalHumanClient(client);
    return client;
  }
  
  /**
   * Creates an AI-Client, adds it to the local client list, if it is a main client, sets it as such, then returns the Client.
   * 
   * @author sistumpf
   * @param isMain true if this is the main client. If true, it gets set as such and it will record the game
   * @param teamName the clients team name
   * @param enableRestLayer true to enable the rest layer
   * @param ip server ip
   * @param port server port
   * @param sessionID server session ID
   * @param aitype what kind of AI it is (RANDOM, MCTS, IMPROVED, EXPERIMENTAL)
   * @param config a valid AI config or null to use a default config
   */
  public static AIClient createAiClient(boolean isMain, String teamName, boolean enableRestLayer, String ip, String port, String sessionID, AI aitype, AIConfig config) {
    CreateGameController.setSessionID(sessionID);
    AIClient aiClient = 
        AIClientStepBuilder
        .newBuilder()
        .enableRestLayer(enableRestLayer)
        .onRemoteHost(ip)
        .onPort(port)
        .aiPlayerSelector(aitype, config)
        .enableSaveGame(isMain)
        .gameData(sessionID, teamName)
        .build();
    if (isMain) { 
      ClientStorage.setMainClient(aiClient);
      aiClient.enableGameStateQueue(true);
    }
    ClientStorage.addLocalAIClient(aiClient);
    return aiClient;
  }
}
