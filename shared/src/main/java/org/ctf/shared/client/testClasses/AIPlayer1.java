package org.ctf.shared.client.testClasses;

import org.ctf.shared.ai.AIConfig;
import org.ctf.shared.client.AIClient;
import org.ctf.shared.client.AIClientStepBuilder;
import org.ctf.shared.constants.Enums.AI;

public class AIPlayer1 {
  public static void main(String[] args) {

    AIClient javaClient1 =
        AIClientStepBuilder.newBuilder()
            .enableRestLayer(false)
            .onLocalHost()
            .onPort("8888")
            .aiPlayerSelector(AI.MCTS, new AIConfig())
            .enableSaveGame(true)
            .gameData("6235df97-1e0f-4c29-af6a-7239e63ab1ab", "Team 1")
            .build();
    //javaClient1.startAIGameController();
  }
}
