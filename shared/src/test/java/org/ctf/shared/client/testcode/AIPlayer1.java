package org.ctf.shared.client.testcode;

import java.util.Random;

public class AIPlayer1 {
  public static void main(String[] args) {
/*      ScheduledExecutorService aiClientScheduler = Executors.newScheduledThreadPool(2);

 
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
 
    aiClientScheduler.scheduleAtFixedRate(checktime, 0, 1,TimeUnit.SECONDS);*/
     Random r = new Random();
     for(int i = 0;i<15;i++){
      int rand = r.nextInt(5);
      System.out.println(rand);
    } 
   

  }
}
