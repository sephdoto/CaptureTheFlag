package org.ctf.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.ctf.client.controller.HTTPServicer;
import org.ctf.client.controller.cfpClientController;
import org.ctf.client.controller.ctfHTTPClient;
import org.ctf.client.service.CommLayer;
import org.ctf.client.state.data.wrappers.GameSessionRequest;
import org.ctf.client.state.data.wrappers.GameSessionResponse;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.data.map.MapTemplate;

/** Tests for the layer and the responses it gives out. */
public class ServerCommandTests {

  /* Notes while testing
   * on successful request. Returns gamesessionID, gameover flag and winners;
   * if flags for ALTERNATE game modes are set in the map, then also returns data
   * on fail/malformed request: returns GameOver AND 500. The swagger UI is wrong
   * malformed always returns gameOver true which DOES NOT depend on the isGameOver method in the game engine. The return is being calculated elsewhere. +
   * which is a VERY weird behaviour
   *
   */

  public static void main(String[] args) {

    // Uncomment to do invidivual tests
   // testConnection();
    //testStart();
    String move = "p:1_n";
    String[] split = move.split("[:._n]");
    for(String a: split){
      System.out.println(a);
    }
    int moveTeam = Integer.parseInt( "p:2_n".split(":")[1].split("_")[0]);
    System.out.println(moveTeam);

    // testConnectionTimedGameMode();
    // testMalformedConnection();
    // testConnectionTimedMoveMode();

    // join();
    // joinNDelete();
  }

  public static void testStart() {
    String jsonPayload =
        """
        {
            "gridSize": [10, 10],
            "teams": 2,
            "flags": 1,
            "blocks": 0,
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
            "moveTimeLimitInSeconds": 3
          }
        """;

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        MapTemplate test = gson.fromJson(jsonPayload, MapTemplate.class);
        GameSessionRequest request = new GameSessionRequest();
        request.setTemplate(test);

    TestClient client = new TestClient();
    client.connect("localhost", "8888", test);
    //System.out.println(client.getSessionID());

    client.joinGame("team1");
    //System.out.println(client.getSecretID());
    //client.joinGame("team2");
    //System.out.println(client.getSecretID());
    // client.joinGame("team3");
    // System.out.println(client.getSecretID());
    client.refreshSession();
    GameState gs = client.getState();
    System.out.println(gson.toJson(gs));
  }

  public static void testConnection() {
    String jsonPayload =
        """
        {
            "gridSize": [10, 10],
            "teams": 2,
            "flags": 1,
            "blocks": 0,
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
            "moveTimeLimitInSeconds": 3
          }
        """;

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        MapTemplate test = gson.fromJson(jsonPayload, MapTemplate.class);
        GameSessionRequest request = new GameSessionRequest();
        request.setTemplate(test);

    DataHandler client = new TestClient();
    client.connect("localhost", "9999", test);
    //System.out.println(client.getSessionID());

    client.joinGame("team1");
    //System.out.println(client.getSecretID());
    client.joinGame("team2");
    //System.out.println(client.getSecretID());
    // client.joinGame("team3");
    // System.out.println(client.getSecretID());
    client.refreshSession();
    //GameState  gs = client.getState();
   // System.out.println(gson.toJson(gs));
    
    ServerCommandTests st = new ServerCommandTests();

    Thread t =
        new Thread() {
          public void run() {
            long start1 = System.currentTimeMillis();
            for (int i = 0; i < 10000; i++) {
              //st.setState(client.getState());
              // System.out.println(gson.toJson(gs));
            }
            long end1 = System.currentTimeMillis();
            System.out.println("T1 Elapsed Time in ms: " + (end1 - start1));
          }
        };
    t.start();

    Thread t2 =
        new Thread() {
          public void run() {
            long start2 = System.currentTimeMillis();
            for (int i = 0; i < 10000; i++) {
              //st.setState(client.getState());
              // System.out.println(gson.toJson(gs));
            }
            long end2 = System.currentTimeMillis();
            System.out.println("T2 Elapsed Time in ms: " + (end2 - start2));
          }
        };
    t2.start();

    Thread t3 =
        new Thread() {
          public void run() {
            long start1 = System.currentTimeMillis();
            for (int i = 0; i < 10000; i++) {
             // st.setState(client.getState());
              // System.out.println(gson.toJson(gs));
            }
            long end1 = System.currentTimeMillis();
            System.out.println("T3 Elapsed Time in ms: " + (end1 - start1));
          }
        };
    t3.start();

    Thread t4 =
        new Thread() {
          public void run() {
            long start1 = System.currentTimeMillis();
            for (int i = 0; i < 10000; i++) {
              //st.setState(client.getState());
              // System.out.println(gson.toJson(gs));
            }
            long end1 = System.currentTimeMillis();
            System.out.println("T4 Elapsed Time in ms: " + (end1 - start1));
          }
        };
    t4.start();

    Thread t5 =
        new Thread() {
          public void run() {
            long start1 = System.currentTimeMillis();
            try {
              sleep(10000);
            } catch (InterruptedException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            long end1 = System.currentTimeMillis();
            System.out.println("T4 Elapsed Time in ms: " + (end1 - start1));
          }
        };
    t5.start();

    // long end1 = System.currentTimeMillis();
    // System.out.println("Elapsed Time in ms: "+ (end1-start1));

    // System.out.println(client.gameOver);

  }
}
