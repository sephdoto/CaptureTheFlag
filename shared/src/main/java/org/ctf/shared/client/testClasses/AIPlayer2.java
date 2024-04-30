package org.ctf.shared.client.testClasses;

import org.ctf.shared.client.AIClient;
import org.ctf.shared.client.AIClientStepBuilder;
import org.ctf.shared.constants.Constants.AI;

public class AIPlayer2 {
  public static void main(String[] args) {

    AIClient javaClient2 =
        AIClientStepBuilder.newBuilder()
            .enableRestLayer(false)
            .onLocalHost()
            .onPort("8888")
            .AIPlayerSelector(AI.MCTS)
            .enableSaveGame(false)
            .gameData("a37b8816-0da7-4d94-9772-97dd449fc973", "Team 2")
            .build();
    javaClient2.startAIGameController();
  }
}
