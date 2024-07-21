package org.ctf.shared.client.testcode;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
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
import org.ctf.shared.gameanalyzer.SavedGame;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.data.exceptions.InvalidMove;
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
    testConnection();
    // testStart();
    // joinTest();
    // copierCheck();
    // arrayTest();
    // getStateTests();
    // AIVSHUMAN();
    // testConnectionTimedGameMode();
    // testMalformedConnection();
    // testConnectionTimedMoveMode();
    // TimeTests();
    // joinTest();
    // TimeTest(); //Tests for Date Time Conversions
    // join();
    // joinNDelete()
    // tests();
  }

  public static void TimeTest() {
    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy/HH/mm/ss", Locale.ENGLISH);
    Date startDate = new Date();
    Date endDate = new Date();
    try {
      endDate = sdf.parse("04/25/2024/12/40/00");
    } catch (ParseException e) {
      e.printStackTrace();
    }
    Duration timeLimDuration =
        Duration.ofSeconds(
            TimeUnit.SECONDS.convert(
                Math.abs(endDate.getTime() - startDate.getTime()), TimeUnit.MILLISECONDS));
    System.out.println(timeLimDuration.toMinutes());
  }

  public static void tests() {
    String gsString =
        """
  {"grid":[["b","","","","","","","","","b"],["","","","","p:0_11","p:0_12","p:0_13","","",""],["","p:0_10","p:0_8","p:0_6","p:0_4","b:0","p:0_5","p:0_7","p:0_9","b"],["","","","","p:0_3","p:0_1","p:0_2","b","","b"],["","","","p:0_18","p:0_17","p:0_16","p:0_15","p:0_14","",""],["","","","p:1_16","p:1_17","p:1_18","","","",""],["","","","","p:1_2","p:1_1","p:1_3","","",""],["p:1_12","p:1_11","p:1_9","p:1_7","p:1_5","b:1","p:1_4","p:1_6","p:1_8","p:1_10"],["","","","","p:1_15","p:1_14","p:1_13","","",""],["","","","","","","","","",""]],"teams":[{"id":"0","color":"0x52217eff","base":[2,5],"flags":1,"pieces":[{"id":"p:0_1","teamId":"0","description":{"type":"Pawn","attackPower":1,"count":10,"movement":{"directions":{"left":0,"right":0,"up":1,"down":0,"upLeft":1,"upRight":1,"downLeft":0,"downRight":0}}},"position":[3,5]},{"id":"p:0_2","teamId":"0","description":{"type":"Pawn","attackPower":1,"count":10,"movement":{"directions":{"left":0,"right":0,"up":1,"down":0,"upLeft":1,"upRight":1,"downLeft":0,"downRight":0}}},"position":[3,6]},{"id":"p:0_3","teamId":"0","description":{"type":"Pawn","attackPower":1,"count":10,"movement":{"directions":{"left":0,"right":0,"up":1,"down":0,"upLeft":1,"upRight":1,"downLeft":0,"downRight":0}}},"position":[3,4]},{"id":"p:0_4","teamId":"0","description":{"type":"Pawn","attackPower":1,"count":10,"movement":{"directions":{"left":0,"right":0,"up":1,"down":0,"upLeft":1,"upRight":1,"downLeft":0,"downRight":0}}},"position":[2,4]},{"id":"p:0_5","teamId":"0","description":{"type":"Pawn","attackPower":1,"count":10,"movement":{"directions":{"left":0,"right":0,"up":1,"down":0,"upLeft":1,"upRight":1,"downLeft":0,"downRight":0}}},"position":[2,6]},{"id":"p:0_6","teamId":"0","description":{"type":"Pawn","attackPower":1,"count":10,"movement":{"directions":{"left":0,"right":0,"up":1,"down":0,"upLeft":1,"upRight":1,"downLeft":0,"downRight":0}}},"position":[2,3]},{"id":"p:0_7","teamId":"0","description":{"type":"Pawn","attackPower":1,"count":10,"movement":{"directions":{"left":0,"right":0,"up":1,"down":0,"upLeft":1,"upRight":1,"downLeft":0,"downRight":0}}},"position":[2,7]},{"id":"p:0_8","teamId":"0","description":{"type":"Pawn","attackPower":1,"count":10,"movement":{"directions":{"left":0,"right":0,"up":1,"down":0,"upLeft":1,"upRight":1,"downLeft":0,"downRight":0}}},"position":[2,2]},{"id":"p:0_9","teamId":"0","description":{"type":"Pawn","attackPower":1,"count":10,"movement":{"directions":{"left":0,"right":0,"up":1,"down":0,"upLeft":1,"upRight":1,"downLeft":0,"downRight":0}}},"position":[2,8]},{"id":"p:0_10","teamId":"0","description":{"type":"Pawn","attackPower":1,"count":10,"movement":{"directions":{"left":0,"right":0,"up":1,"down":0,"upLeft":1,"upRight":1,"downLeft":0,"downRight":0}}},"position":[2,1]},{"id":"p:0_11","teamId":"0","description":{"type":"Rook","attackPower":5,"count":2,"movement":{"directions":{"left":2,"right":2,"up":2,"down":2,"upLeft":0,"upRight":0,"downLeft":0,"downRight":0}}},"position":[1,4]},{"id":"p:0_12","teamId":"0","description":{"type":"Rook","attackPower":5,"count":2,"movement":{"directions":{"left":2,"right":2,"up":2,"down":2,"upLeft":0,"upRight":0,"downLeft":0,"downRight":0}}},"position":[1,5]},{"id":"p:0_13","teamId":"0","description":{"type":"Knight","attackPower":3,"count":2,"movement":{"shape":{"type":"lshape"}}},"position":[1,6]},{"id":"p:0_14","teamId":"0","description":{"type":"Knight","attackPower":3,"count":2,"movement":{"shape":{"type":"lshape"}}},"position":[4,7]},{"id":"p:0_15","teamId":"0","description":{"type":"Bishop","attackPower":3,"count":2,"movement":{"directions":{"left":0,"right":0,"up":0,"down":0,"upLeft":2,"upRight":2,"downLeft":2,"downRight":2}}},"position":[4,6]},{"id":"p:0_16","teamId":"0","description":{"type":"Bishop","attackPower":3,"count":2,"movement":{"directions":{"left":0,"right":0,"up":0,"down":0,"upLeft":2,"upRight":2,"downLeft":2,"downRight":2}}},"position":[4,5]},{"id":"p:0_17","teamId":"0","description":{"type":"Queen","attackPower":5,"count":1,"movement":{"directions":{"left":2,"right":2,"up":2,"down":2,"upLeft":2,"upRight":2,"downLeft":2,"downRight":2}}},"position":[4,4]},{"id":"p:0_18","teamId":"0","description":{"type":"King","attackPower":1,"count":1,"movement":{"directions":{"left":1,"right":1,"up":1,"down":1,"upLeft":1,"upRight":1,"downLeft":1,"downRight":1}}},"position":[4,3]}]},{"id":"1","color":"0xa19c8aff","base":[7,5],"flags":1,"pieces":[{"id":"p:1_1","teamId":"1","description":{"type":"Pawn","attackPower":1,"count":10,"movement":{"directions":{"left":0,"right":0,"up":1,"down":0,"upLeft":1,"upRight":1,"downLeft":0,"downRight":0}}},"position":[6,5]},{"id":"p:1_2","teamId":"1","description":{"type":"Pawn","attackPower":1,"count":10,"movement":{"directions":{"left":0,"right":0,"up":1,"down":0,"upLeft":1,"upRight":1,"downLeft":0,"downRight":0}}},"position":[6,4]},{"id":"p:1_3","teamId":"1","description":{"type":"Pawn","attackPower":1,"count":10,"movement":{"directions":{"left":0,"right":0,"up":1,"down":0,"upLeft":1,"upRight":1,"downLeft":0,"downRight":0}}},"position":[6,6]},{"id":"p:1_4","teamId":"1","description":{"type":"Pawn","attackPower":1,"count":10,"movement":{"directions":{"left":0,"right":0,"up":1,"down":0,"upLeft":1,"upRight":1,"downLeft":0,"downRight":0}}},"position":[7,6]},{"id":"p:1_5","teamId":"1","description":{"type":"Pawn","attackPower":1,"count":10,"movement":{"directions":{"left":0,"right":0,"up":1,"down":0,"upLeft":1,"upRight":1,"downLeft":0,"downRight":0}}},"position":[7,4]},{"id":"p:1_6","teamId":"1","description":{"type":"Pawn","attackPower":1,"count":10,"movement":{"directions":{"left":0,"right":0,"up":1,"down":0,"upLeft":1,"upRight":1,"downLeft":0,"downRight":0}}},"position":[7,7]},{"id":"p:1_7","teamId":"1","description":{"type":"Pawn","attackPower":1,"count":10,"movement":{"directions":{"left":0,"right":0,"up":1,"down":0,"upLeft":1,"upRight":1,"downLeft":0,"downRight":0}}},"position":[7,3]},{"id":"p:1_8","teamId":"1","description":{"type":"Pawn","attackPower":1,"count":10,"movement":{"directions":{"left":0,"right":0,"up":1,"down":0,"upLeft":1,"upRight":1,"downLeft":0,"downRight":0}}},"position":[7,8]},{"id":"p:1_9","teamId":"1","description":{"type":"Pawn","attackPower":1,"count":10,"movement":{"directions":{"left":0,"right":0,"up":1,"down":0,"upLeft":1,"upRight":1,"downLeft":0,"downRight":0}}},"position":[7,2]},{"id":"p:1_10","teamId":"1","description":{"type":"Pawn","attackPower":1,"count":10,"movement":{"directions":{"left":0,"right":0,"up":1,"down":0,"upLeft":1,"upRight":1,"downLeft":0,"downRight":0}}},"position":[7,9]},{"id":"p:1_11","teamId":"1","description":{"type":"Rook","attackPower":5,"count":2,"movement":{"directions":{"left":2,"right":2,"up":2,"down":2,"upLeft":0,"upRight":0,"downLeft":0,"downRight":0}}},"position":[7,1]},{"id":"p:1_12","teamId":"1","description":{"type":"Rook","attackPower":5,"count":2,"movement":{"directions":{"left":2,"right":2,"up":2,"down":2,"upLeft":0,"upRight":0,"downLeft":0,"downRight":0}}},"position":[7,0]},{"id":"p:1_13","teamId":"1","description":{"type":"Knight","attackPower":3,"count":2,"movement":{"shape":{"type":"lshape"}}},"position":[8,6]},{"id":"p:1_14","teamId":"1","description":{"type":"Knight","attackPower":3,"count":2,"movement":{"shape":{"type":"lshape"}}},"position":[8,5]},{"id":"p:1_15","teamId":"1","description":{"type":"Bishop","attackPower":3,"count":2,"movement":{"directions":{"left":0,"right":0,"up":0,"down":0,"upLeft":2,"upRight":2,"downLeft":2,"downRight":2}}},"position":[8,4]},{"id":"p:1_16","teamId":"1","description":{"type":"Bishop","attackPower":3,"count":2,"movement":{"directions":{"left":0,"right":0,"up":0,"down":0,"upLeft":2,"upRight":2,"downLeft":2,"downRight":2}}},"position":[5,3]},{"id":"p:1_17","teamId":"1","description":{"type":"Queen","attackPower":5,"count":1,"movement":{"directions":{"left":2,"right":2,"up":2,"down":2,"upLeft":2,"upRight":2,"downLeft":2,"downRight":2}}},"position":[5,4]},{"id":"p:1_18","teamId":"1","description":{"type":"King","attackPower":1,"count":1,"movement":{"directions":{"left":1,"right":1,"up":1,"down":1,"upLeft":1,"upRight":1,"downLeft":1,"downRight":1}}},"position":[5,5]}]}],"currentTeam":0}
""";
    Gson gson = new Gson();
    GameSaveHandler analyzer = new GameSaveHandler();
    analyzer.addGameState(gson.fromJson(gsString, GameState.class));
    Move move1 = new Move();
    move1.setPieceId("p:1_3");
    move1.setNewPosition(new int[] {2, 2});
    Move move2 = new Move();
    move2.setPieceId("p:2_3");
    move2.setNewPosition(new int[] {4, 2});
    Move move3 = new Move();
    move3.setPieceId("p:3_3");
    move3.setNewPosition(new int[] {4, 2});
    Move move4 = new Move();
    move4.setPieceId("p:3_3");
    move4.setNewPosition(new int[] {4, 2});
    analyzer.addMove(move1, "");
    analyzer.addMove(move2, "");
    analyzer.addMove(move3, "");
    analyzer.writeOut();

    SavedGame save = analyzer.readFile();
    HashMap<String, Move> moves = save.getMoves();
    System.out.println(gson.toJson(moves));
    System.out.println(gson.toJson(save.getInitialState()));
  }

  public static void TimeTests() {

    Duration turnTime = Duration.ofSeconds(10);

    Clock currentTime = Clock.systemDefaultZone(); // Inits Calender when the Game Started
    Clock turnEndsBy =
        Clock.fixed(Clock.offset(currentTime, turnTime).instant(), ZoneId.systemDefault());
    for (int i = 0; i < 12; i++) {

      System.out.println(
          Math.toIntExact(
              Duration.between(currentTime.instant(), turnEndsBy.instant()).getSeconds()));
      if (currentTime.instant().isAfter(turnEndsBy.instant())) {
        System.out.println("timed out");
        break;
      }
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
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
            "moveTimeLimitInSeconds": -1
          }
        """;

    // Gson gson = new GsonBuilder().setPrettyPrinting().create();
    Gson gson = new Gson();
    MapTemplate test = gson.fromJson(jsonPayload, MapTemplate.class);
    ServerManager server = new ServerManager(new CommLayer(), new ServerDetails("localhost", "8888"), test);
    System.out.println(server.createGame());
    System.out.println(server.getGameSessionID());
    System.out.println(server.deleteGame());

    /* Client test1Client =
        ClientStepBuilder.newBuilder()
            .enableRestLayer(false)
            .onLocalHost()
            .onPort("8888")
            .enableSaveGame(false)
            .build();
    test1Client.createGame(test);
    test1Client.joinGame("Seph1");
    Client test2Client =
        ClientStepBuilder.newBuilder()
            .enableRestLayer(false)
            .onLocalHost()
            .onPort("8888")
            .enableSaveGame(false)
            .build(); */
    // test2Client.joinExistingGame("localhost", "8888", test1Client.currentGameSessionID, "Seph2");
    // test1Client.startGameController();
    // test2Client.startGameController();
    //  System.out.println(test1Client.currentTeamTurn);
    // test1Client.giveUp();
    /* test1Client.getStateFromServer(); */

    //System.out.println(gson.toJson(test1Client.getCurrentState().getTeams().length));
  /*   int counter = 0;
    for (int i = 0; i < test1Client.getCurrentState().getTeams().length; i++) {
      if (test1Client.getCurrentState().getTeams()[i] != null) {
        counter++;
      }
    }
    System.out.println(counter); */
  }

  public static void joinTest() {

    String jsonPayload =
        """
          {
            "gridSize": [20, 20],
            "teams": 3,
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
            "totalTimeLimitInSeconds": 8,
            "moveTimeLimitInSeconds": 2
          }
        """;

    Gson gson2 = new GsonBuilder().setPrettyPrinting().create();
    Gson gson = new Gson();
    MapTemplate template = gson.fromJson(jsonPayload, MapTemplate.class);
    CommLayer comm = new CommLayer();

    /*  // client.setServer("localhost", "8888");
    try {
      Thread.sleep(2000);

      client2.joinExistingGame("localhost", "8888", client.getSessionID(), "Seph2");
    } catch (InterruptedException e) {
      e.printStackTrace();
    } */
  }

  /*  Client javaClient2 =
         ClientStepBuilder.newBuilder()
             .enableRestLayer(false)
             .onLocalHost()
             .onPort("8888")
             .HumanPlayer()
             .build();
     Client javaClient3 =
         ClientStepBuilder.newBuilder()
             .enableRestLayer(false)
             .onLocalHost()
             .onPort("8888")
             .HumanPlayer()
             .build();
     javaClient.createGame(template);
     javaClient.joinGame("Team1");
     javaClient2.joinExistingGame(
         "localhost", "8888", javaClient.getCurrentGameSessionID(), "Team2");
     javaClient3.joinExistingGame(
         "localhost", "8888", javaClient.getCurrentGameSessionID(), "Team3");
     javaClient.getStateFromServer();
     javaClient2.getStateFromServer();
     javaClient.getSessionFromServer();
     System.out.println(gson.toJson(javaClient.getCurrentState()));
     System.out.println(gson.toJson(javaClient.getCurrentSession()));
     try {
       for (int i = 0; i < 10; i++) {
         Thread.sleep(1000);
         javaClient.getStateFromServer();
         System.out.println(gson.toJson(javaClient.getCurrentState()));
         javaClient.getSessionFromServer();
         System.out.println(gson.toJson(javaClient.getCurrentSession()));
       }

     } catch (InterruptedException e) {
       e.printStackTrace();
     }
  */
  /* Move move = new Move();
  move.setPieceId("p:0_2");
  move.setNewPosition(new int[] {0, 1}); */
  // javaClient2.makeMove(move);
  /*       move.setPieceId("p:1_2");
  move.setNewPosition(new int[] {9, 8});
  javaClient.makeMove(move); */

  /*    javaClient.getStateFromServer();
  javaClient2.getStateFromServer();
  System.out.println(gson.toJson(javaClient.getGrid()));
  System.out.println(gson.toJson(javaClient.getLastMove()));
  System.out.println(gson.toJson(javaClient2.getLastMove())); */
  /*    for(int i = 0; i < 100; i++){
    System.out.println( (int) (Math.random() * 2) );
  } */

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
            "moveTimeLimitInSeconds": 3
          }
        """;

/*     Gson gson = new Gson();
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
            .enableSaveGame(false)
            .build();
    javaClient.createGame(template);
    javaClient.joinGame("0");
    javaClient2.joinExistingGame("localhost", "8888", javaClient.getCurrentGameSessionID(), "1");
    javaClient.getStateFromServer();
    javaClient2.getStateFromServer();
    javaClient.getSessionFromServer();
    javaClient2.getSessionFromServer(); */
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
   /*  javaClient.getStateFromServer();
    javaClient2.getStateFromServer();
    // System.out.println(gson.toJson(javaClient.getCurrentState()));
    AIController Controller = new AIController(javaClient.getCurrentState(), AI.MCTS,0);
    AIController Controller2 = new AIController(javaClient2.getCurrentState(), AI.MCTS_IMPROVED,0);
    for (int i = 0; i < 90; i++) {
      try {
        if (javaClient.isItMyTurn()) {
          System.out.println("it was team 0s turn!");
          javaClient.makeMove(Controller.getNextMove());
          System.out.println("team 0 made a move");
          javaClient.getStateFromServer();
          javaClient2.getStateFromServer();
          Controller.update(javaClient.getCurrentState());
          Controller2.update(javaClient2.getCurrentState());
        } else {
          System.out.println("it was team 1s turn!");
          javaClient2.makeMove(Controller2.getNextMove());
          System.out.println("client 1 made a move");
        }
        Thread.sleep(1000);
      } catch (NoMovesLeftException
          | InvalidShapeException
          | InterruptedException
          | InvalidMove e) {
        javaClient.getStateFromServer();
        Controller.update(javaClient.getCurrentState());
        javaClient2.getStateFromServer();
        Controller2.update(javaClient2.getCurrentState());
        // System.out.println(gson.toJson(javaClient.getGrid()))
        e.printStackTrace(); */
  /*     }
    } */
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
            "totalTimeLimitInSeconds": 5,
            "moveTimeLimitInSeconds": 5
          }
        """;

  /*   Gson gson = new Gson();
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
            .enableSaveGame(false)
            .build();
    javaClient.createGame(template);
    javaClient.joinGame("0");
    javaClient2.joinExistingGame("localhost", "8888", javaClient.getCurrentGameSessionID(), "1");
    for (int i = 0; i < 10; i++) {
      try {
        Thread.sleep(800);
        System.out.println(javaClient.getRemainingGameTimeInSeconds());
        javaClient2.giveUp();
        Thread.sleep(100);
      } catch (InterruptedException e) {

        e.printStackTrace();
      }
    } */
  }





  
}
