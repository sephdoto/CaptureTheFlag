package org.ctf.shared.client;

import com.google.gson.Gson;
import org.ctf.shared.ai.AI_Controller;
import org.ctf.shared.ai.AI_Tools.InvalidShapeException;
import org.ctf.shared.ai.AI_Tools.NoMovesLeftException;
import org.ctf.shared.constants.Constants.AI;
import org.ctf.shared.state.data.exceptions.GameOver;
import org.ctf.shared.state.data.map.MapTemplate;

// Makes upto 500 moves
public class MCTSSimulation {
  public static void main(String[] args) {
    String jsonPayload =
        """
        {
            "gridSize": [20, 20],
            "teams": 2,
            "flags": 1,
            "blocks": 50,
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
            "placement": "defensive",
            "totalTimeLimitInSeconds": 50,
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
    /* Client javaClient3 =
        ClientStepBuilder.newBuilder()
            .enableRestLayer(true)
            .onLocalHost()
            .onPort("8888")
            .HumanPlayer()
            .build(); */
    javaClient.createGame(template);
    javaClient.joinGame("Team 1");
    javaClient2.joinExistingGame(
        "localhost", "8888", javaClient.getCurrentGameSessionID(), "Team 2");
    //javaClient3.joinExistingGame(
    //    "localhost", "8888", javaClient.getCurrentGameSessionID(), "Team 3");
    javaClient.getStateFromServer();
    javaClient2.getStateFromServer();
   // javaClient3.getStateFromServer();
    // System.out.println(gson.toJson(javaClient.getCurrentState()));
    AI_Controller Controller = new AI_Controller(javaClient.getCurrentState(), AI.MCTS);
    AI_Controller Controller2 = new AI_Controller(javaClient2.getCurrentState(), AI.MCTS);
   // AI_Controller Controller3 = new AI_Controller(javaClient3.getCurrentState(), AI.MCTS);
    for (int i = 0; i < 500; i++) {
      try {
        if (javaClient.isItMyTurn()) {
          System.out.println("it was Teams turn " + javaClient.getLastTeamTurn());
          javaClient.makeMove(Controller.getNextMove());
          System.out.println("client 0 made a move");
         
        } else if (javaClient2.isItMyTurn()) {
          System.out.println("it was Teams turn " + javaClient2.getLastTeamTurn());
          javaClient2.makeMove(Controller2.getNextMove());
          System.out.println("client 1 made a move");
          
       // } else if (javaClient3.isItMyTurn()) {
       //   javaClient3.makeMove(Controller2.getNextMove());
       //   System.out.println("client 2 made a move");
      //    System.out.println("it was Teams turn " + javaClient3.getLastTeamTurn());
        }
        javaClient.getStateFromServer();
        Controller.update(javaClient.getCurrentState());
        javaClient2.getStateFromServer();
        Controller2.update(javaClient2.getCurrentState());
       // javaClient3.getStateFromServer();
      //  Controller3.update(javaClient3.getCurrentState());
        if (javaClient.getCurrentTeamTurn() == -1) {
          javaClient.getStateFromServer();
          System.out.println(gson.toJson(javaClient.getCurrentState()));
          javaClient.getSessionFromServer();
          System.out.println(gson.toJson(javaClient.getCurrentSession()));
          javaClient2.getStateFromServer();
          System.out.println(gson.toJson(javaClient2.getCurrentState()));
          javaClient2.getSessionFromServer();
          System.out.println(gson.toJson(javaClient2.getCurrentSession()));
          System.out.println(gson.toJson(javaClient.getWinners()));
          System.out.println(gson.toJson(javaClient2.getWinners()));
          break;
        }
        // System.out.println(gson.toJson(javaClient.getGrid()));
      } catch (NoMovesLeftException e) {
        e.printStackTrace();
      } catch (InvalidShapeException e) {
        e.printStackTrace();
      } catch (NullPointerException e) {
        e.printStackTrace();
        javaClient.getStateFromServer();
        System.out.println(gson.toJson(javaClient.getCurrentState()));
        javaClient.getSessionFromServer();
        System.out.println(gson.toJson(javaClient.getCurrentSession()));
        break;
      } catch (GameOver e) {
        javaClient.getStateFromServer();
        System.out.println(gson.toJson(javaClient.getCurrentState()));
        javaClient.getSessionFromServer();
        System.out.println(gson.toJson(javaClient.getCurrentSession()));
        break;
      }
    }
  }
}
