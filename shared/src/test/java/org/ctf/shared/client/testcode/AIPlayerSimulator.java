package org.ctf.shared.client.testcode;

import org.ctf.shared.client.lib.ServerDetails;
import org.ctf.shared.client.lib.ServerManager;
import org.ctf.shared.client.service.CommLayer;
import org.ctf.shared.state.data.map.MapTemplate;
import com.google.gson.Gson;

public class AIPlayerSimulator {

  public static String GameID;

  public static void main(String[] args) {
    startPlayer1();
    startPlayer2();
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
    ServerManager server = new ServerManager(new CommLayer(), new ServerDetails("localhost", "8888") , mapTemplate);
    server.createGame();
    GameID = server.getGameSessionID();
    System.out.println(GameID + " this is the session ID");
    System.out.println(server.isServerActive() + " session is active");
    System.out.println(server.isSessionActive() + " session is active");
   
/*     AIClient javaClient1 =
        AIClientStepBuilder.newBuilder()
            .enableRestLayer(false)
            .onLocalHost()
            .onPort("8888")
            .AIPlayerSelector(AI.MCTS)
            .enableSaveGame(false)
            .gameData(server.getGameSessionID(), "Seph1")
            .build();

            javaClient1.joinExistingGame("localhost", "8888", server.getGameSessionID(), "seph 1");
            javaClient1.startGameController(); */

             try {
              Thread.sleep(15000);
              System.out.println(server.isServerActive());
              System.out.println(server.getCurrentNumberofTeams());
            } catch (InterruptedException e) {
              e.printStackTrace();
            }  

      
  }

  public static void startPlayer2() {
    
  
  }
}
