package org.ctf.shared.client.testcode;

import org.ctf.shared.client.AIClient;
import org.ctf.shared.client.AIClientStepBuilder;
import org.ctf.shared.constants.Enums.AI;

public class AIPlayer2 {
  @SuppressWarnings("unused")
  public static void main(String[] args) {

    AIClient javaClient2 =
        AIClientStepBuilder.newBuilder()
            .enableRestLayer(false)
            .onLocalHost()
            .onPort("8888")
            .aiPlayerSelector(AI.MCTS, null)
            .enableSaveGame(false)
            .gameData("4223fe13-f5c6-4f04-b4b4-ae8057571a74", "Team 2")
            .build();
    //javaClient2.startAIGameController();
  }
}
