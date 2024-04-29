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
            .gameData("d0acf1ce-d41a-4b61-8939-dc505cc236ee", "Team 1")
            .build();
    javaClient1.startAIGameController();
  }
}
