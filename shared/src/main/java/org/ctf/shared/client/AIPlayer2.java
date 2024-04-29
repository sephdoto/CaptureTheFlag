package org.ctf.shared.client;

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
            .gameData("35feb993-5340-4339-8b61-c70b3e105382", "Team 2")
            .build();
    javaClient2.startAIGameController();
  }
}
