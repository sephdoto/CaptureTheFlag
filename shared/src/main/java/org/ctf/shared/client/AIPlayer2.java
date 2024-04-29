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
            .gameData("d0acf1ce-d41a-4b61-8939-dc505cc236ee", "Team 2")
            .build();
    javaClient2.startAIGameController();
  }
}
