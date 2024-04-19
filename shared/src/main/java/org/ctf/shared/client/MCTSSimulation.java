package org.ctf.shared.client;

import org.ctf.shared.ai.AI_Controller;
import org.ctf.shared.ai.AI_Tools.InvalidShapeException;
import org.ctf.shared.ai.AI_Tools.NoMovesLeftException;
import org.ctf.shared.constants.Constants.AI;
import org.ctf.shared.state.data.map.MapTemplate;

import com.google.gson.Gson;
//Makes upto 500 moves
public class MCTSSimulation {
    public static void main(String[] args) {
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
    
    Gson gson = new Gson();
    MapTemplate template = gson.fromJson(jsonPayload, MapTemplate.class);
        Client javaClient =
            ClientStepBuilder.newBuilder()
                .enableRestLayer(false)
                .onLocalHost()
                .onPort("8888")
                .HumanPlayer()
                .build();
        Client javaClient2 =
            ClientStepBuilder.newBuilder()
                .enableRestLayer(false)
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
        }  */
        javaClient.getStateFromServer();
        javaClient2.getStateFromServer();
        //System.out.println(gson.toJson(javaClient.getCurrentState()));
        AI_Controller Controller = new AI_Controller(javaClient.getCurrentState(), AI.MCTS);
        AI_Controller Controller2 = new AI_Controller(javaClient2.getCurrentState(), AI.MCTS);
        for (int i = 0; i < 500; i++) {
          try {
            if(javaClient.getCurrentTeamTurn() == 0){
              javaClient.makeMove(Controller.getNextMove());
              System.out.println("client 0 made a move");
            } else if (javaClient.getCurrentTeamTurn() == 1){
              javaClient2.makeMove(Controller2.getNextMove());
              System.out.println("client 1 made a move");
            }
            javaClient.getStateFromServer();
            Controller.update(javaClient.getCurrentState());
            javaClient2.getStateFromServer();
            Controller2.update(javaClient2.getCurrentState());
            //System.out.println(gson.toJson(javaClient.getGrid()));
          } catch (NoMovesLeftException e) {
            e.printStackTrace();
          } catch (InvalidShapeException e) {
            e.printStackTrace();
          }
          
        }
    }
}
