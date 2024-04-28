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
                .gameData("73ca3b4b-b83d-48b2-8139-c707e639dda3", "Team 2")
                .build();
              //  javaClient2.pullData();
            
            //  System.out.println(gson.toJson(javaClient2.getCurrentSession()));
             // System.out.println(gson.toJson(javaClient2.getCurrentState()));

              javaClient2.joinExistingGame("localhost", "8888", "bea5bd99-2591-436f-a118-a5c1b8a0217e", "Team 2");
              javaClient2.pullData();
              javaClient2.startGameController();
      }
}
