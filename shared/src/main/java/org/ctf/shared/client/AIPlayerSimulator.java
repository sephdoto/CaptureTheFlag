package org.ctf.shared.client;

import org.ctf.shared.constants.Constants.AI;

public class AIPlayerSimulator {
  public static void main(String[] args) {
    startPlayer();
  }

  public static void startPlayer() {
    Client javaClient2 =
        ClientStepBuilder.newBuilder()
            .enableRestLayer(false)
            .onLocalHost()
            .onPort("8888")
            .AIPlayerSelector(AI.MCTS_IMPROVED)
            .build();
            String gameSessionID = "5a37d163-7933-40c4-83f1-fed58d825395";
            javaClient2.joinExistingGame("localhost", "8888", gameSessionID, "1");
            System.out.println("joined");
            ((AIClient) javaClient2).run();
  }
}
