package org.ctf.shared.client;

import org.ctf.shared.ai.AI_Controller;
import org.ctf.shared.ai.AI_Tools.InvalidShapeException;
import org.ctf.shared.ai.AI_Tools.NoMovesLeftException;
import org.ctf.shared.client.lib.Analyzer;
import org.ctf.shared.client.service.CommLayerInterface;
import org.ctf.shared.constants.Constants.AI;
import org.ctf.shared.state.data.exceptions.NoMoreTeamSlots;
import org.ctf.shared.state.data.exceptions.SessionNotFound;

/**
 * Extension of Client with support for additional functionality needed by the AI to perform its
 * functions automatically
 *
 * @author rsyed
 */
public class AIClient extends Client {

  public AI selectedPlayer;
  public boolean enableLogging;
  public Analyzer analyzer;
  public String gameIDString;
  public String constructorSetTeamName;
  AI_Controller controller;

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
    this.selectedPlayer = selected;
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
   * Starts the Automation of the Client. Call it after object creation
   *
   * @author rsyed
   */
  public void startAutomation() {
    AIGameHandler();
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
    controller = new AI_Controller(currentState, selectedPlayer);
  }

  /**
   * Thread checks which mode the AI Client is configured for and calls the handler needed to start
   * a new thread and then perform the function asked for
   *
   * @author rsyed
   */
  public void AIGameHandler() {
    Thread firsThread =
        new Thread(
            () -> {
              boolean running = true;
              while (running) {
                try {
                  if (isServerActive()) {
                    joinExistingGame(
                        serverInfo.getHost(),
                        serverInfo.getPort(),
                        gameIDString,
                        constructorSetTeamName);
                  }
                  Thread.sleep(1000);
                  pullData();
                  Thread.sleep(1000);
                  startGameController();
                  AIPlayerStart();
                  running = false;
                } catch (Exception e) {
                  System.out.println("Error Occured in First handoff");
                }
              }
            });
    firsThread.start();
  }

  /**
   * Main thread for the AI Client. Refreshes data on its own and check if it its turn, on true
   * makes a move, pulls data anew from Server and updates the controller
   *
   * @author rsyed
   */
  public void AIPlayerStart() {

    Thread gameThread =
        new Thread(
            () -> {
              boolean running = true;
              while (running) {
                try {

                  Thread.sleep(500);

                } catch (Exception e) {
                  throw new Error("Something went wrong in the Client Thread");
                }
              }
            });
    gameThread.start();
  }

  /**
   * Thead which handles client logic for when game has started. Just pulls data periodically set by
   * the refesh time var
   *
   * @author rsyed
   */
  public void gameStartedThread() {
    Thread gameThread =
        new Thread(
            () -> {
              boolean running = true;

              while (running) {
                try {
                  pullData();
                  controller.update(getCurrentState());
                    makeMove(controller.getNextMove());
                  pullData();
                  controller.update(getCurrentState());
                  Thread.sleep(2000);
                } catch (InterruptedException | NoMovesLeftException | InvalidShapeException e) {
                  throw new Error("Something went wrong in the Client Thread");
                }
              }
            });
    gameThread.start();
  }

  /**
   * Main CONTROLLER for Client. Call when either a game is created or a game is joined Starts the
   * automatic refreshing of data
   *
   * @author rsyed
   */
  public void startGameController() {
    gameStartWatcher();
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
                  /*    joinExistingGame(
                  serverInfo.getHost(),
                  serverInfo.getPort(),
                  gameIDString,
                  constructorSetTeamName); */
                  Thread.sleep(sleep);
                  this.getSessionFromServer(); // Gets Session from server
                  if (getStartDate() != null) {
                    gameStartedThread();
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
