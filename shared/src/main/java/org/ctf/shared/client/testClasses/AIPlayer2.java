package org.ctf.shared.client.testClasses;

import org.ctf.shared.client.AIClient;
import org.ctf.shared.client.AIClientStepBuilder;
import org.ctf.shared.constants.Enums.AI;

public class AIPlayer2 {
  public static void main(String[] args) {

    AIClient javaClient2 =
        AIClientStepBuilder.newBuilder()
            .enableRestLayer(false)
            .onLocalHost()
            .onPort("8888")
            .AIPlayerSelector(AI.MCTS)
            .enableSaveGame(true)
            .gameData("84af3ba7-af34-4ac0-9247-a3615e378a84", "Team 2")
            .build();
    javaClient2.startAIGameController();
  }
}
