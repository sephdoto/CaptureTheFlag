package org.ctf.shared.client;

import com.google.gson.Gson;
import org.ctf.shared.constants.Constants.AI;
import org.ctf.shared.state.data.map.MapTemplate;

public class AIPlayerSimulator {

  public static String GameID;

  public static void main(String[] args) {
    startPlayer1();
    startPlayer2();
  }

  public static void setGameID(String id) {
    GameID = id;
  }

  public static String getGameID(){
    return GameID;
  }

  public static void startPlayer1() {
    String jsonPayload =
        """
        {
            "gridSize": [20, 20],
            "teams": 2,
            "flags": 1,
            "blocks": 5,
            "pieces": [
              {
                "type": "Pawn",
                "attackPower": 1,
                "count": 10,
                "movement": {
                  "directions": {
                    "left": 0,
                    "right": 0,
                    "up": 1,
                    "down": 0,
                    "upLeft": 1,
                    "upRight": 1,
                    "downLeft": 0,
                    "downRight": 0
                  }
                }
              },
              {
                "type": "Rook",
                "attackPower": 5,
                "count": 2,
                "movement": {
                  "directions": {
                    "left": 2,
                    "right": 2,
                    "up": 2,
                    "down": 2,
                    "upLeft": 0,
                    "upRight": 0,
                    "downLeft": 0,
                    "downRight": 0
                  }
                }
              },
              {
                "type": "Knight",
                "attackPower": 3,
                "count": 2,
                "movement": {
                  "shape": {
                    "type": "lshape"
                  }
                }
              },
              {
                "type": "Bishop",
                "attackPower": 3,
                "count": 2,
                "movement": {
                  "directions": {
                    "left": 0,
                    "right": 0,
                    "up": 0,
                    "down": 0,
                    "upLeft": 2,
                    "upRight": 2,
                    "downLeft": 2,
                    "downRight": 2
                  }
                }
              },
              {
                "type": "Queen",
                "attackPower": 5,
                "count": 1,
                "movement": {
                  "directions": {
                    "left": 2,
                    "right": 2,
                    "up": 2,
                    "down": 2,
                    "upLeft": 2,
                    "upRight": 2,
                    "downLeft": 2,
                    "downRight": 2
                  }
                }
              },
              {
                "type": "King",
                "attackPower": 1,
                "count": 1,
                "movement": {
                  "directions": {
                    "left": 1,
                    "right": 1,
                    "up": 1,
                    "down": 1,
                    "upLeft": 1,
                    "upRight": 1,
                    "downLeft": 1,
                    "downRight": 1
                  }
                }
              }
            ],
            "placement": "symmetrical",
            "totalTimeLimitInSeconds": -1,
            "moveTimeLimitInSeconds": -1
          }
        """;

    Gson gson = new Gson();
    MapTemplate mapTemplate = gson.fromJson(jsonPayload, MapTemplate.class);
    AIClient javaClient1 =
        AIClientStepBuilder.newBuilder()
            .enableRestLayer(false)
            .onLocalHost()
            .onPort("8888")
            .AIPlayerSelector(AI.MCTS)
            .enableSaveGame(false)
            .createGameMode(mapTemplate, "Seph1")
            .build();

    // javaClient1.startAutomation();
    javaClient1.createGame(mapTemplate);
    javaClient1.joinGame("Seph1");
    //System.out.println("Session ID " + javaClient1.getSessionIDfromAI());

    setGameID(javaClient1.getSessionIDfromAI());
    System.out.println(getGameID() + " Is set");
  }

  public static void startPlayer2() {
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    AIClient javaClient2 =
        AIClientStepBuilder.newBuilder()
            .enableRestLayer(false)
            .onLocalHost()
            .onPort("8888")
            .AIPlayerSelector(AI.MCTS)
            .enableSaveGame(false)
            .joinerGameMode(getGameID(), "Seph2")
            .build();

    javaClient2.startAutomation();
  }
}
