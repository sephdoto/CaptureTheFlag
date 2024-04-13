package org.ctf.shared.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.ctf.shared.ai.AI_Controller;
import org.ctf.shared.ai.AI_Tools.InvalidShapeException;
import org.ctf.shared.ai.AI_Tools.NoMovesLeftException;
import org.ctf.shared.client.service.CommLayer;
import org.ctf.shared.constants.Constants.AI;
import org.ctf.shared.constants.Constants.Port;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Team;
import org.ctf.shared.state.data.exceptions.InvalidMove;
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
    //copierCheck();
    // arrayTest();
    getStateTests();
    //AIVSHUMAN();
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
            .onPort("8888")
            .HumanPlayer()
            .build();
    Client javaClient2 =
        ClientStepBuilder.newBuilder()
            .enableRestLayer(true)
            .onLocalHost()
            .onPort("8888")
            .HumanPlayer()
            .build();
    javaClient.createGame(template);
    javaClient.joinGame("0");
    javaClient2.joinExistingGame("localhost", "8888", javaClient.getCurrentGameSessionID(), "1");
    javaClient.getStateFromServer();
    javaClient2.getStateFromServer();
  /*   Move move = new Move();
    if (javaClient.getCurrentTeamTurn() == 1) {
      try {
        move.setPieceId("p:1_2");
        move.setNewPosition(new int[] {9, 8});
        javaClient.makeMove(move);
      } catch (Exception e) {
        System.out.println("Made move");
      }
    } else {
      try {
        move.setPieceId("p:0_2");
        move.setNewPosition(new int[] {0, 1});
        javaClient2.makeMove(move);
      } catch (Exception e) {
        System.out.println("Made move");
      }
    } */
    javaClient.getStateFromServer();
    javaClient2.getStateFromServer();
    System.out.println(gson.toJson(javaClient.getCurrentState()));
    AI_Controller Controller = new AI_Controller(javaClient.getCurrentState(), AI.MCTS);
    AI_Controller Controller2 = new AI_Controller(javaClient2.getCurrentState(), AI.RANDOM);
    for (int i = 0; i<90;i++){
      try {
        javaClient.makeMove(Controller.getNextMove());
        javaClient.getStateFromServer();
        javaClient2.getStateFromServer();
        Controller.update(javaClient.getCurrentState());
        Controller2.update(javaClient2.getCurrentState());
        javaClient2.makeMove(Controller2.getNextMove());
        javaClient.getStateFromServer();
        javaClient2.getStateFromServer();
        Controller.update(javaClient.getCurrentState());
        Controller2.update(javaClient2.getCurrentState());
      } catch (NoMovesLeftException | InvalidShapeException | InvalidMove e) {
        javaClient.getStateFromServer();
        System.out.println(gson.toJson(javaClient.getCurrentState()));
        e.printStackTrace();
      }
    }
  }

  public static void AIVSHUMAN() {
    
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
            .onPort("8888")
            .AIPlayerSelector(AI.RANDOM)
            .build();
    Client javaClient2 =
        ClientStepBuilder.newBuilder()
            .enableRestLayer(true)
            .onLocalHost()
            .onPort("8888")
            .HumanPlayer()
            .build();
    javaClient.createGame(template);
    javaClient.joinGame("0");
    javaClient2.joinExistingGame("localhost", "8888", javaClient.getCurrentGameSessionID(), "1");
    javaClient.getStateFromServer();
    javaClient2.getStateFromServer();
  /*   Move move = new Move();
    if (javaClient.getCurrentTeamTurn() == 1) {
      try {
        move.setPieceId("p:1_2");
        move.setNewPosition(new int[] {9, 8});
        javaClient.makeMove(move);
      } catch (Exception e) {
        System.out.println("Made move");
      }
    } else {
      try {
        move.setPieceId("p:0_2");
        move.setNewPosition(new int[] {0, 1});
        javaClient2.makeMove(move);
      } catch (Exception e) {
        System.out.println("Made move");
      }
    } */
    javaClient.getStateFromServer();
    javaClient2.getStateFromServer();
    System.out.println(gson.toJson(javaClient.getCurrentState()));
    AI_Controller Controller2 = new AI_Controller(javaClient2.getCurrentState(), AI.RANDOM);
    for (int i = 0; i<90;i++){
      try {
        javaClient.getStateFromServer();
        javaClient2.getStateFromServer();

        Controller2.update(javaClient2.getCurrentState());
        javaClient2.makeMove(Controller2.getNextMove());
        javaClient.getStateFromServer();
        javaClient2.getStateFromServer();
        Controller2.update(javaClient2.getCurrentState());
      } catch (NoMovesLeftException | InvalidShapeException | InvalidMove e) {
        javaClient.getStateFromServer();
        System.out.println(gson.toJson(javaClient.getCurrentState()));
        e.printStackTrace();
      }
    }
  }

}
