package org.ctf.shared.client;

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
            .gameData("35feb993-5340-4339-8b61-c70b3e105382", "Team 1")
            .build();
    javaClient1.startAIGameController();
  }
}
