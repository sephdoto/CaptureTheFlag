package org.ctf.shared.client.testClasses;

import com.google.gson.Gson;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.ctf.shared.ai.AIController;
import org.ctf.shared.ai.GameUtilities.InvalidShapeException;
import org.ctf.shared.ai.GameUtilities.NoMovesLeftException;
import org.ctf.shared.client.AIClient;
import org.ctf.shared.client.AIClientStepBuilder;
import org.ctf.shared.client.lib.ServerDetails;
import org.ctf.shared.client.lib.ServerManager;
import org.ctf.shared.client.service.CommLayer;
import org.ctf.shared.constants.Enums.AI;
import org.ctf.shared.state.data.map.MapTemplate;

public class ScheduledTasks {
  static String GameID;

  public static void main(String[] args) {
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
    Gson gson = new Gson();

    Runnable CreateGame =
        () -> {
          try {
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
                    "moveTimeLimitInSeconds": 5
                  }
                """;
            MapTemplate mapTemplate = gson.fromJson(jsonPayload, MapTemplate.class);
            ServerManager server =
                new ServerManager(
                    new CommLayer(), new ServerDetails("localhost", "8888"), mapTemplate);
            server.createGame();
            GameID = server.getGameSessionID();
            System.out.println(GameID);
          } catch (Exception e) {
            e.printStackTrace();
          }
        };

    AIClient client1 =
        AIClientStepBuilder.newBuilder()
            .enableRestLayer(false)
            .onLocalHost()
            .onPort("8888")
            .AIPlayerSelector(AI.MCTS)
            .enableSaveGame(false)
            .gameData(GameID, "Team 1")
            .build();
    AIClient client2 =
        AIClientStepBuilder.newBuilder()
            .enableRestLayer(false)
            .onLocalHost()
            .onPort("8888")
            .AIPlayerSelector(AI.MCTS)
            .enableSaveGame(false)
            .gameData(GameID, "Team 2")
            .build();
    Runnable refreshTask =
        () -> {
          try {
            client1.getSessionFromServer();
            client1.getStateFromServer();
            client2.getSessionFromServer();
            client2.getStateFromServer();
            // System.out.println(gson.toJson(client1.getCurrentState()));
            TimeUnit.MILLISECONDS.sleep(300);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        };
    Runnable joinTask =
        () -> {
          // client1.getSessionFromServer();
          client1.joinExistingGame("localhost", "8888", GameID, "Team 1");
        };

    Runnable joinTask2 =
        () -> {
          // client1.getSessionFromServer();
          client2.joinExistingGame("localhost", "8888", GameID, "Team 2");
        };

    // AIController Controller1 = new AIController(client1.getCurrentState(), AI.RANDOM);
    //  AIController Controller2 = new AIController(client2.getCurrentState(), AI.MCTS);
    Runnable playTask =
        () -> {
          try {
            System.out.println("running playtask 1");
            int thinkingTime = 10;
            if (client1.moveTimeLimitedGameTrigger) {
              thinkingTime = client1.getRemainingMoveTimeInSeconds() - 1;
              System.out.println("We had " +thinkingTime + " to think");
            }
            AIController controller = new AIController(client1.getCurrentState(), client1.selectedAI, thinkingTime);
            client1.pullData();
            controller.update(client1.getCurrentState());
            /*       client2.pullData();
            Controller2.update(client2.getCurrentState()); */
            if (client1.isItMyTurn()) {
              client1.makeMove(controller.getNextMove());
            }
            /* else if (client2.isItMyTurn()) {
              client2.makeMove(Controller2.getNextMove());
            } */
            client1.pullData();
            controller.update(client1.getCurrentState());
            /*   client2.pullData();
            Controller2.update(client2.getCurrentState()); */
            TimeUnit.MILLISECONDS.sleep(1500);
          } catch (InterruptedException | NoMovesLeftException | InvalidShapeException e) {
            e.printStackTrace();
          }
        };

    Runnable playTask2 =
        () -> {
          try {
            System.out.println("running playtask 2");
            int thinkingTime = 10;
            if (client2.moveTimeLimitedGameTrigger) {
              thinkingTime = client1.getRemainingMoveTimeInSeconds() - 1;
              System.out.println("We had " + thinkingTime + " to think");
            }
            AIController controller2 = new AIController(client2.getCurrentState(), client2.selectedAI, thinkingTime);
            client2.pullData();
            controller2.update(client2.getCurrentState());
            if (client2.isItMyTurn()) {
              client2.makeMove(controller2.getNextMove());
            }
            client2.pullData();
            controller2.update(client2.getCurrentState());
            TimeUnit.MILLISECONDS.sleep(1500);
          } catch (InterruptedException | NoMovesLeftException | InvalidShapeException e) {
            e.printStackTrace();
          }
        };

    Runnable printGson =
        () -> {
          try {
            System.out.println(gson.toJson(client1.getCurrentState()));
            TimeUnit.MILLISECONDS.sleep(300);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        };

    scheduler.schedule(CreateGame, 1, TimeUnit.SECONDS);
    scheduler.schedule(joinTask, 3, TimeUnit.SECONDS);
    scheduler.schedule(joinTask2, 5, TimeUnit.SECONDS);
    scheduler.schedule(refreshTask, 8, TimeUnit.SECONDS);
    // scheduler.scheduleWithFixedDelay(refreshTask, 15, 2, TimeUnit.SECONDS);
    scheduler.scheduleWithFixedDelay(playTask, 9, 3, TimeUnit.SECONDS);
    scheduler.scheduleWithFixedDelay(playTask2, 9, 3, TimeUnit.SECONDS);
    // scheduler.scheduleWithFixedDelay(printGson, 9, 5, TimeUnit.SECONDS);
    // scheduler.scheduleWithFixedDelay(playTask2, 11, 2, TimeUnit.SECONDS);
    /* try {

      Callable<Integer> task2 =
          new Callable<Integer>() {
            public Integer call() {
              try {
                Thread.sleep(5000);
              } catch (InterruptedException ex) {
                ex.printStackTrace();
              }

              return 1000000;
            }
          };

      Callable<Integer> task =
          new Callable<Integer>() {
            public Integer call() {
              if (true) {
                Future<Integer> result2 = scheduler.schedule(task2, 5, TimeUnit.SECONDS);
                try {
                  System.out.println(result2.get() + "of sresult 2");
                } catch (InterruptedException | ExecutionException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }
              }
              try {
                Thread.sleep(5000);
              } catch (InterruptedException ex) {
                ex.printStackTrace();
              }

              return 1000000;
            }
          };

      int delay = 5;

      Future<Integer> result = scheduler.schedule(task, delay, TimeUnit.SECONDS);

      try {

        Integer value = result.get();

        System.out.println("value = " + value);

      } catch (InterruptedException | ExecutionException ex) {
        ex.printStackTrace();
      }

      scheduler.shutdown();
    } catch (Exception e) {
      // TODO: handle exception
    } */
  }
}
