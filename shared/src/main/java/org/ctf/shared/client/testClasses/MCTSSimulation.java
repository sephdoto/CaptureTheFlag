package org.ctf.shared.client.testClasses;

import com.google.gson.Gson;
import org.ctf.shared.ai.AIController;
import org.ctf.shared.ai.GameUtilities.InvalidShapeException;
import org.ctf.shared.ai.GameUtilities.NoMovesLeftException;
import org.ctf.shared.client.Client;
import org.ctf.shared.client.ClientStepBuilder;
import org.ctf.shared.client.lib.ServerDetails;
import org.ctf.shared.client.lib.ServerManager;
import org.ctf.shared.client.service.CommLayer;
import org.ctf.shared.constants.Enums.AI;
import org.ctf.shared.gameanalyzer.GameSaveHandler;
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
            "totalTimeLimitInSeconds": 15,
            "moveTimeLimitInSeconds": -1
          }
        """;
        Gson gson = new Gson();
        MapTemplate template = gson.fromJson(jsonPayload, MapTemplate.class);
        ServerManager manager = new ServerManager(new CommLayer(), new ServerDetails("localhost", "8888") , template);
        manager.createGame();
        System.out.println(manager.getGameSessionID());
   /*  Gson gson = new Gson();
    MapTemplate template = gson.fromJson(jsonPayload, MapTemplate.class);
    Client javaClient =
        ClientStepBuilder.newBuilder()
            .enableRestLayer(false)
            .onLocalHost()
            .onPort("8888")
            .enableSaveGame(false)
            .build();

    Client javaClient2 =
        ClientStepBuilder.newBuilder()
            .enableRestLayer(false)
            .onLocalHost()
            .onPort("8888")
            .enableSav eGame(false)
            .build();*/
    /*        AIClient javaClient3 =
    AIClientStepBuilder.newBuilder()
        .enableRestLayer(false)
        .onLocalHost()
        .onPort("8888")
        .AIPlayerSelector(AI.MCTS)
        .enableSaveGame(false)
        .createGameMode(null, "Seph3")
        .build();
        AIClient javaClient4 =
        AIClientStepBuilder.newBuilder()
            .enableRestLayer(false)
            .onLocalHost()
            .onPort("8888")
            .AIPlayerSelector(AI.MCTS)
            .enableSaveGame(false)
            .createGameMode(null, "Seph4")
            .build();
            AIClient javaClient5 =
            AIClientStepBuilder.newBuilder()
                .enableRestLayer(false)
                .onLocalHost()
                .onPort("8888")
                .AIPlayerSelector(AI.MCTS)
                .enableSaveGame(false)
                .createGameMode(null, "Seph5")
                .build(); */
 /*    javaClient.createGame(template);
    javaClient.joinGame("Team 1");
    javaClient2.joinExistingGame(
        "localhost", "8888", javaClient.getCurrentGameSessionID(), "Team 2"); */
    /* javaClient3.joinExistingGame(
        "localhost", "8888", javaClient.getCurrentGameSessionID(), "Team 3");
    javaClient4.joinExistingGame(
        "localhost", "8888", javaClient.getCurrentGameSessionID(), "Team 4");
    javaClient5.joinExistingGame(
        "localhost", "8888", javaClient.getCurrentGameSessionID(), "Team 5"); */
    GameSaveHandler newAna = new GameSaveHandler();
    /*  javaClient.getStateFromServer();
    javaClient2.getStateFromServer();
    javaClient3.getStateFromServer();
    javaClient4.getStateFromServer();
    javaClient5.getStateFromServer();
    javaClient.getSessionFromServer();
    javaClient2.getSessionFromServer();*/
/*     javaClient.pullData();
    javaClient2.pullData();
    /*    javaClient3.getSessionFromServer();
    javaClient4.getSessionFromServer();
    javaClient5.getSessionFromServer(); */
 /*    System.out.println(gson.toJson(javaClient.getCurrentState()));
    System.out.println(gson.toJson(javaClient.getCurrentSession()));
    newAna.addGameState(javaClient.getCurrentState());
    AIController Controller = new AIController(javaClient.getCurrentState(), AI.MCTS,0);
    AIController Controller2 = new AIController(javaClient2.getCurrentState(), AI.MCTS,0); */
    /*  AIController Controller3 = new AIController(javaClient3.getCurrentState(), AI.MCTS);
    AIController Controller4 = new AIController(javaClient4.getCurrentState(), AI.MCTS);
    AIController Controller5 = new AIController(javaClient5.getCurrentState(), AI.MCTS); */
  /*   for (int i = 0; i < 50 && !javaClient.isGameOver(); i++) {
      javaClient.pullData();
      javaClient2.pullData();
      Controller.update(javaClient.getCurrentState());
      Controller2.update(javaClient2.getCurrentState());
      try {
        if (javaClient.isItMyTurn()) {
          System.out.println("it was Teams turn " + javaClient.getLastTeamTurn());
          javaClient.makeMove(Controller.getNextMove());
          System.out.println("client 1 made a move");
        } else if (javaClient2.isItMyTurn()) {
          System.out.println("it was Teams turn " + javaClient2.getLastTeamTurn());
          javaClient2.makeMove(Controller2.getNextMove());
          System.out.println("client 2 made a move");
        } else {
          System.out.println("nobodys turn??");
        } */
        /*  else if (javaClient3.isItMyTurn()) {
            javaClient3.makeMove(Controller3.getNextMove());
            System.out.println("client 3 made a move");
            System.out.println("it was Teams turn " + javaClient3.getLastTeamTurn());
          } else if (javaClient4.isItMyTurn()) {
            javaClient4.makeMove(Controller4.getNextMove());
            System.out.println("client 4 made a move");
            System.out.println("it was Teams turn " + javaClient3.getLastTeamTurn());
          } else if (javaClient5.isItMyTurn()) {
            javaClient5.makeMove(Controller5.getNextMove());
            System.out.println("client 5 made a move");
            System.out.println("it was Teams turn " + javaClient3.getLastTeamTurn());
          } */
        /*  javaClient.getStateFromServer();
        newAna.addMove(javaClient.getLastMove());
        Controller.update(javaClient.getCurrentState());
        javaClient2.getStateFromServer();
        Controller2.update(javaClient2.getCurrentState()); */
        /* javaClient3.getStateFromServer();
        Controller3.update(javaClient3.getCurrentState());
        javaClient4.getStateFromServer();
        Controller4.update(javaClient4.getCurrentState());
        javaClient5.getStateFromServer();
        Controller5.update(javaClient5.getCurrentState()); */
     /*    if (javaClient.gameOver) {
          System.out.println(newAna.writeOut());
          break;
        }
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
          newAna.writeOut();
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
  } */
}
}
