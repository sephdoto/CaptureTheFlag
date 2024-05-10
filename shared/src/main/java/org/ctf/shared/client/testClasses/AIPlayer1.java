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
            .aiPlayerSelector(AI.MCTS, null)
            .enableSaveGame(true)
            .gameData("2f2d9642-972a-4c28-8552-1bb25033df32", "Team 1")
            .build();
    //javaClient1.startAIGameController();
  }
}
