package org.ctf.shared.client.testClasses;

import org.ctf.shared.client.AIClient;
import org.ctf.shared.client.AIClientStepBuilder;
import org.ctf.shared.constants.Constants.AI;

public class AIPlayer1 {
  public static void main(String[] args) {

    AIClient javaClient1 =
        AIClientStepBuilder.newBuilder()
            .enableRestLayer(false)
            .onLocalHost()
            .onPort("8888")
            .AIPlayerSelector(AI.MCTS)
            .enableSaveGame(false)
            .gameData("49b5f29b-5cc3-4257-9290-a5f783ca25ba", "Team 1")
            .build();
    javaClient1.startAIGameController();
  }
}
