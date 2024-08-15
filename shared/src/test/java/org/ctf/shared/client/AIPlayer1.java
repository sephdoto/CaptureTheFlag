package org.ctf.shared.client;

import org.ctf.shared.ai.AIConfig;
import org.ctf.shared.constants.Enums.AI;
import com.google.gson.Gson;

public class AIPlayer1 {
  @SuppressWarnings("unused")
  public static void main(String[] args) {
    String AICONFIG =
        """
        {
          "pieceMultiplier": 2,
          "directionMultiplier": 1,
          "shapeReachMultiplier": 1,
          "C": 1.3591409142295225,
          "distanceBaseMultiplier": 7,
          "attackPowerMultiplier": 2,
          "MAX_STEPS": 100,
          "numThreads": 4,
          "flagMultiplier": 1000
        }
        """;
    Gson gson = new Gson();
    AIConfig aiconfig = gson.fromJson(AICONFIG, AIConfig.class);

    AIClient javaClient1 =
        AIClientStepBuilder.newBuilder()
            .enableRestLayer(false)
            .onLocalHost()
            .onPort("8888")
            .aiPlayerSelector(AI.IMPROVED, aiconfig)
            .enableSaveGame(true)
            .gameData("433c5a17-480e-4e6a-ab9c-31419ff369d4", "Team AI")
            .build();
  }
}
