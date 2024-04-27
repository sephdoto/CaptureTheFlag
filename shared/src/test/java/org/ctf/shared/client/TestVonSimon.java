package org.ctf.shared.client;

import org.ctf.shared.ai.TestValues;
import com.google.gson.Gson;

public class TestVonSimon {
  public static void main(String[] args) {
    Client javaClient =
        ClientStepBuilder.newBuilder()
            .enableRestLayer(true)
            .onLocalHost()
            .onPort("8888")
            .enableSaveGame(false)
            .build();    
    Client javaClient2 =
        ClientStepBuilder.newBuilder()
            .enableRestLayer(true)
            .onLocalHost()
            .onPort("8888")
            .enableSaveGame(false)
            .build();   
    
    
    javaClient.createGame(TestValues.getTestTemplate());
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "8888", javaClient.getCurrentGameSessionID(), "Team2");
    javaClient.pullData();
    javaClient2.pullData();
    Gson gson = new Gson();
   // System.out.println(gson.toJson(javaClient.getCurrentState()));
    try {
      if (javaClient2.isItMyTurn()) {
        javaClient2.giveUp();
      } else {
        javaClient.giveUp();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } 
      javaClient.pullData();
      javaClient2.pullData();

  }
}
