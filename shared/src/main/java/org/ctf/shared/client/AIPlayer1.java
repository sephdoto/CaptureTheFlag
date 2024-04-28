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
                .gameData("73ca3b4b-b83d-48b2-8139-c707e639dda3", "Team 1")
                .build();
              //  javaClient2.pullData();
            
            //  System.out.println(gson.toJson(javaClient2.getCurrentSession()));
             // System.out.println(gson.toJson(javaClient2.getCurrentState()));
             
              javaClient1.joinExistingGame("localhost", "8888", "bea5bd99-2591-436f-a118-a5c1b8a0217e", "Team 1");
              javaClient1.pullData();
              javaClient1.startGameController();
      }
}
