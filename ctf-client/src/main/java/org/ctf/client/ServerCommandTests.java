package org.ctf.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.ctf.client.service.CommLayer;
import org.ctf.shared.constants.Constants.Port;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Team;
import org.ctf.shared.state.data.map.MapTemplate;
import org.ctf.shared.state.dto.GameSessionRequest;

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
    // testStart();
    // joinTest();
    copierCheck();
    // arrayTest();
    // getStateTests();
    // testConnectionTimedGameMode();
    // testMalformedConnection();
    // testConnectionTimedMoveMode();

    // join();
    // joinNDelete();
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

    /*  JavaClient client = new JavaClient();
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

     ServerCommandTests st = new ServerCommandTests(); */

    Thread t =
        new Thread() {
          public void run() {
            long start1 = System.currentTimeMillis();
            for (int i = 0; i < 10000; i++) {
              // st.setState(client.getState());
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
              // st.setState(client.getState());
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
              // st.setState(client.getState());
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

  public static void joinTest() {

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
            "moveTimeLimitInSeconds": -1
          }
        """;

    Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
    Gson gson = new Gson();
    MapTemplate template = gson.fromJson(jsonPayload, MapTemplate.class);
    CommLayer comm = new CommLayer();
    Client javaClient =
        ClientStepBuilder.newBuilder()
            .enableRestLayer(true)
            .onLocalHost()
            .onPort(Port.DEFAULTPORT)
            .HumanPlayer()
            .build();
    Client javaClient2 =
        ClientStepBuilder.newBuilder()
            .enableRestLayer(true)
            .onLocalHost()
            .onPort(Port.DEFAULTPORT)
            .HumanPlayer()
            .build();
    javaClient.createGame(template);
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "9999", javaClient.getCurrentGameSessionID(), "Team2");
    javaClient.getStateFromServer();
    javaClient2.getStateFromServer();
    Move move = new Move();
    move.setPieceId("p:0_2");
    move.setNewPosition(new int[] {0, 1});
    // javaClient2.makeMove(move);
    /*       move.setPieceId("p:1_2");
    move.setNewPosition(new int[] {9, 8});
    javaClient.makeMove(move); */

    javaClient.getStateFromServer();
    javaClient2.getStateFromServer();
    System.out.println(gson.toJson(javaClient.getGrid()));
    System.out.println(gson.toJson(javaClient.getLastMove()));
    System.out.println(gson.toJson(javaClient2.getLastMove()));
    /*    for(int i = 0; i < 100; i++){
      System.out.println( (int) (Math.random() * 2) );
    } */
  }

  public static void getStateTests() {

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
            "moveTimeLimitInSeconds": -1
          }
        """;

    Gson gson = new Gson();
    MapTemplate template = gson.fromJson(jsonPayload, MapTemplate.class);
    Client javaClient =
        ClientStepBuilder.newBuilder()
            .enableRestLayer(true)
            .onLocalHost()
            .onPort(Port.DEFAULTPORT)
            .HumanPlayer()
            .build();
    Client javaClient2 =
        ClientStepBuilder.newBuilder()
            .enableRestLayer(true)
            .onLocalHost()
            .onPort(Port.DEFAULTPORT)
            .HumanPlayer()
            .build();
    javaClient.createGame(template);
    javaClient.joinGame("Se[j1]");
    javaClient2.joinExistingGame("localhost", "8888", javaClient.getCurrentGameSessionID(), "nasd");
    javaClient.getStateFromServer();
  }

  public static void copierCheck() {
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
            "moveTimeLimitInSeconds": -1
          }
        """;

    Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
    Gson gson = new Gson();
    MapTemplate template = gson.fromJson(jsonPayload, MapTemplate.class);
    CommLayer comm = new CommLayer();
    Client javaClient =
        ClientStepBuilder.newBuilder()
            .enableRestLayer(true)
            .onLocalHost()
            .onPort(Port.DEFAULTPORT)
            .HumanPlayer()
            .build();
    Client javaClient2 =
        ClientStepBuilder.newBuilder()
            .enableRestLayer(true)
            .onLocalHost()
            .onPort(Port.DEFAULTPORT)
            .HumanPlayer()
            .build();

    javaClient.createGame(template);
    javaClient.joinGame("Team1");
    javaClient2.joinExistingGame(
        "localhost", "8888", javaClient.getCurrentGameSessionID(), "Team2");
    javaClient.getStateFromServer();
    javaClient2.getStateFromServer();
    Move move = new Move();
    move.setPieceId("p:0_2");
    move.setNewPosition(new int[] {0, 1});

    javaClient.getStateFromServer();
    javaClient.getSessionFromServer();
    javaClient2.getSessionFromServer();
    javaClient2.getStateFromServer();

    System.out.println(javaClient.getStartDate());
    System.out.println(javaClient2.getCurrentSession().getGameStarted());

    /*     GameState state1 = javaClient.getCurrentState();
      System.out.println("GSON " + gson.toJson(state1));

      ObjectMapper mapper = new ObjectMapper();
    GameState re = new GameState();
    try {
    	String asJson = mapper.writeValueAsString(state1);
        System.out.println("As JSON " + asJson);
    } catch (JsonGenerationException e) {
    	System.out.println("Error in deepCopyGameState JSON Method");
    } catch (JsonMappingException e) {
    	System.out.println("Error in deepCopyGameState JSON Method");
    } catch (IOException e) {
    	System.out.println("Error in deepCopyGameState JSON Method");
    }*/
  }

  public static void arrayTest() {
    Team[] teams = new Team[3];
    for (int i = 0; i < teams.length; i++) {
      teams[i] = new Team();
      teams[i].setId(Integer.toString(i));
    }

    for (Team t : teams) {
      System.out.println(t.getId());
    }
  }
}
