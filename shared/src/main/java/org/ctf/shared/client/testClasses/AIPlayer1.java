package org.ctf.shared.client.testClasses;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.ctf.shared.ai.AIConfig;
import org.ctf.shared.client.AIClient;
import org.ctf.shared.client.AIClientStepBuilder;
import org.ctf.shared.constants.Enums.AI;

public class AIPlayer1 {
  public static void main(String[] args) {
     ScheduledExecutorService aiClientScheduler = Executors.newScheduledThreadPool(2);

 
    AIClient javaClient1 =
        AIClientStepBuilder.newBuilder()
            .enableRestLayer(false)
            .onLocalHost()
            .onPort("8888")
            .aiPlayerSelector(AI.MCTS, null)
            .enableSaveGame(true)
            .gameData("4223fe13-f5c6-4f04-b4b4-ae8057571a74", "Team 1")
            .build();
    //javaClient1.startAIGameController();

    Runnable checktime =
    () -> System.out.println(javaClient1.getRemainingGameTimeInSeconds());

    aiClientScheduler.scheduleAtFixedRate(checktime, 0, 1,TimeUnit.SECONDS);
    
  /*   for(int i = 0;i<15;i++){
      System.out.println(javaClient1.getRemainingGameTimeInSeconds());
      Thread.
    } */
   

  }
}
