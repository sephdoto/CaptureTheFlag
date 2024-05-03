package org.ctf.shared.client;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.ctf.shared.ai.AI_Controller;
import org.ctf.shared.ai.AI_Tools.InvalidShapeException;
import org.ctf.shared.ai.AI_Tools.NoMovesLeftException;
import org.ctf.shared.client.lib.Analyzer;
import org.ctf.shared.client.service.CommLayerInterface;
import org.ctf.shared.constants.Enums.AI;
import org.ctf.shared.state.data.exceptions.NoMoreTeamSlots;
import org.ctf.shared.state.data.exceptions.SessionNotFound;

/**
 * Extension of Client with support for additional functionality needed by the AI to perform its
 * functions automatically
 *
 * @author rsyed
 */
public class AIClient extends Client {

  public AI selectedAI;
  public boolean enableLogging;
  public Analyzer analyzer;
  public String gameIDString;
  public String constructorSetTeamName;
  public long refreshTime = 1000L;
  ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);

  Runnable refreshTask =
      () -> {
        try {
          pullData();
          TimeUnit.MILLISECONDS.sleep(refreshTime);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      };

  Runnable joinTask =
      () -> {
        joinExistingGame(
            serverInfo.getHost(), serverInfo.getPort(), gameIDString, constructorSetTeamName);
      };

  Runnable playTask =
      () -> {
        try {
          getStateFromServer();
          int thinkingTime = 10;
          if (moveTimeLimitedGameTrigger) {
            thinkingTime = getRemainingMoveTimeInSeconds() - 1;
            System.out.println("We had " +thinkingTime + " to think");
          }
          AI_Controller controller = new AI_Controller(getCurrentState(), selectedAI, thinkingTime);
          pullData();
          controller.update(getCurrentState());
          if (isItMyTurn()) {
            makeMove(controller.getNextMove());
          }
          pullData();
          controller.update(getCurrentState());
        } catch (NoMovesLeftException | InvalidShapeException e) {
          throw new UnknownError("Games most likely over");
        }
      };

  /**
   * Base constructor.
   *
   * @param comm Sets the comm layer the client is going to use
   * @param IP the IP to connect to Exp "localhost" or "192.xxx.xxx.xxx"
   * @param port the port the server is at Exp 9999 / 8080 /
   * @param enableLogging tells the client to start logging the moves for later analysis by an AI
   * @param selected the AI Enum which is later used for the by the AI Controller class to call for
   *     moves
   * @author rsyed
   */
  AIClient(CommLayerInterface comm, String IP, String port, Boolean enableLogging, AI selected) {
    super(comm, IP, port, enableLogging);
    this.selectedAI = selected;
    this.enableLogging = enableLogging;
  }

  /**
   * Constructor called the the AI Client has to perform a Join function
   *
   * @param comm Sets the comm layer the client is going to use
   * @param IP the IP to connect to Exp "localhost" or "192.xxx.xxx.xxx"
   * @param port the port the server is at Exp 9999 / 8080 /
   * @param enableLogging tells the client to start logging the moves for later analysis by an AI
   * @param selected the AI Enum used by the AI Controller class to generate moves
   * @param gameIDString the Game Session ID to use when connecting
   * @param constructorSetTeamName the team name desired to be used
   * @author rsyed
   */
  public AIClient(
      CommLayerInterface comm,
      String IP,
      String port,
      Boolean enableLogging,
      AI selected,
      String gameIDString,
      String constructorSetTeamName) {
    this(comm, IP, port, enableLogging, selected);
    this.gameIDString = gameIDString;
    this.constructorSetTeamName = constructorSetTeamName;
  }

  /**
   * Changes the sessionID which this client object is pointing to. Functions as a join game command
   *
   * @param IP
   * @param port
   * @param gameSessionID
   * @param teamName
   * @throws SessionNotFound
   * @throws NoMoreTeamSlots
   * @throws UnknownError
   * @author rsyed
   */
  public void joinExistingGame(String IP, String port, String gameSessionID, String teamName) {
    this.currentServer = "http://" + IP + ":" + port + "/api/gamesession";
    this.currentServer = shortURL + "/" + gameSessionID;
    joinGame(teamName);
    getStateFromServer();
  }

  public void pullData() {
    getSessionFromServer();
    getStateFromServer();
  }

  /**
   * Main method to call to start automation
   *
   * @author rsyed
   */
  public void startAIGameController() {
    // Join Automatically
    scheduler.schedule(joinTask, 0, TimeUnit.SECONDS);
    gameStartWatcher();
  }

  /**
   * Main thread for the AI Client. Refreshes data on its own and check if it its turn, on true
   * makes a move, pulls data anew from Server and updates the controller
   *
   * @author rsyed
   */
  public void AIPlayerStart() {
    scheduler.scheduleWithFixedDelay(playTask, 2, 2, TimeUnit.SECONDS);
  }

  /**
   * A Watcher thread which calls GameSessionResponse periodically and hands over functionality to
   * GameStartedThread when it has and then terminates itself.
   *
   * @author rsyed
   */
  public void gameStartWatcher() {
    Thread watcherThread =
        new Thread(
            () -> {
              boolean running = true;
              while (running) {
                try {
                  Long sleep = 1000L;
                  Thread.sleep(sleep);
                  getSessionFromServer(); // Gets Session from server
                  if (getStartDate() != null) {
                    AIPlayerStart();
                    running = false;
                  }
                  Thread.sleep(sleep);
                } catch (InterruptedException e) {
                  throw new Error("Something went wrong in the Client Thread");
                }
              }
            });
    watcherThread.start();
  }
}
