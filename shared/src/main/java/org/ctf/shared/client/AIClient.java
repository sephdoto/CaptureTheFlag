package org.ctf.shared.client;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.ctf.shared.ai.AIConfig;
import org.ctf.shared.ai.AIController;
import org.ctf.shared.ai.GameUtilities.InvalidShapeException;
import org.ctf.shared.ai.GameUtilities.NoMovesLeftException;
import org.ctf.shared.client.service.CommLayerInterface;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.AI;
import org.ctf.shared.gameanalyzer.GameSaveHandler;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.data.exceptions.GameOver;
import org.ctf.shared.state.data.exceptions.NoMoreTeamSlots;
import org.ctf.shared.state.data.exceptions.SessionNotFound;

/**
 * Extension of Client with support for additional functionality needed by the AI to perform its
 * functions automatically
 *
 * @author rsyed
 */
public class AIClient extends Client {
  // These two vars controls the client behaviour
  private int aiClientRefreshTime = Constants.aiClientDefaultRefreshTime;
  private int controllerThinkingTime = Constants.aiDefaultThinkingTimeInSeconds;

  private AI selectedAI;
  private AIConfig aiConfig;
  private AIController controller;
  private boolean enableLogging;
  private volatile GameSaveHandler analyzer;
  private String gameIDString;
  private String constructorSetTeamName;
  private boolean saveToken = true;
  private boolean controllerToken = true;

  ScheduledExecutorService aiClientScheduler = Executors.newScheduledThreadPool(2);

  /**
   * Runnable task responsible for joining a game for the AI Client
   * 
   * @return int denoting the time left in seconds
   *
   * @author rsyed
   */

  Runnable aiClientJoinTask =
      () ->
          joinExistingGame(
              serverInfo.getHost(), serverInfo.getPort(), gameIDString, constructorSetTeamName);

  
  /**
   * Runnable task which contains the main automation logic for the server. The task always gets a state first, then inits the AI controller with data. The 
   * task also handles the logger incase its enabled and performs its main function of making a move the client detects that it is its turn.
   * 
   * @return int denoting the time left in seconds
   *
   * @author rsyed
   */
  Runnable playTask =
      () -> {
        try {
          getStateFromServer();
          if (controllerToken) {
            if (moveTimeLimitedGameTrigger) {
              controllerThinkingTime = getRemainingMoveTimeInSeconds() - 1;
              //  logger.info("We had " + controllerThinkingTime + " to think");
            }
            controller =
                new AIController(getCurrentState(), selectedAI, aiConfig, controllerThinkingTime);
            controllerToken = false;
          }
          pullData();
          
          if(selectedAI == AI.RANDOM)
            controller.update(getCurrentState());
          else 
            controller.update(getCurrentState(), getCurrentState().getLastMove());
          
          if (enableLogging) {
            this.analyzer.addMove(getCurrentState().getLastMove());
          }
          if (isItMyTurn()) {
            makeMove(controller.getNextMove());
          }
          pullData();

          if(selectedAI == AI.RANDOM)
            controller.update(getCurrentState());
          else 
            controller.update(getCurrentState(), getCurrentState().getLastMove());

        } catch (NoMovesLeftException | InvalidShapeException e) {
          throw new UnknownError("Games most likely over");
        } catch (GameOver e) {
          if (saveToken && enableLogging) {
            this.analyzer.writeOut();
            saveToken = false;
          } else {
            throw new GameOver();
          }
        } catch (NullPointerException e) {
          e.printStackTrace();
          logger.info("nullpointer exception");
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
   * @para aiConfig the config file for AI to load moves
   * @author rsyed
   */
  AIClient(
      CommLayerInterface comm,
      String IP,
      String port,
      boolean enableLogging,
      AI selected,
      AIConfig aiConfig) {
    super(comm, IP, port, enableLogging);
    this.selectedAI = selected;
    this.aiConfig = aiConfig;
    this.enableLogging = enableLogging;
    if (enableLogging) {
      this.analyzer = new GameSaveHandler();
    }
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
      AIConfig aiConfig,
      String gameIDString,
      String constructorSetTeamName) {
    this(comm, IP, port, enableLogging, selected, aiConfig);
    this.gameIDString = gameIDString;
    this.constructorSetTeamName = constructorSetTeamName;
    startAIGameController();
  }

  /**
   * Changes the sessionID which this client object is pointing to. Functions as a join game command. Overridden as it has to perform extra functions when compared to the one in 
   * Client
   *
   * @param IP of the Server
   * @param port of the Server
   * @param gameSessionID of the Game you want to join
   * @param teamName you want to join with
   * @throws SessionNotFound
   * @throws NoMoreTeamSlots
   * @throws UnknownError
   * @author rsyed
   */
  @Override
  public void joinExistingGame(String IP, String port, String gameSessionID, String teamName) {
    this.currentServer = "http://" + IP + ":" + port + "/api/gamesession";
    this.currentServer = shortURL + "/" + gameSessionID;
    joinGame(teamName);
    getStateFromServer();
    this.isAlive = true;
  }
/**
   * Combines refreshing the session and game state into one.
   *
   * @author rsyed
   */
  @Override
  public void pullData() {
    getSessionFromServer();
    getStateFromServer();
  }

  /**
   * Main method to call to start automation. Schedules the join task to execute immediately and then starts the watcher thread which handles the playing logic.
   *
   * @author rsyed
   */
  protected void startAIGameController() {
    // Join Automatically
    aiClientScheduler.schedule(aiClientJoinTask, 0, TimeUnit.SECONDS);
    gameStartWatcher();
  }

  /**
   * Starts the play task 
   *
   * @author rsyed
   */
  protected void aiPlayerStart() {
    aiClientScheduler.scheduleWithFixedDelay(playTask, 1, aiClientRefreshTime, TimeUnit.SECONDS);
  }

  /**
   * A Watcher thread which calls GameSessionResponse periodically and hands over functionality to
   * GameStartedThread when it has and then terminates itself.
   *
   * @author rsyed
   */
  @Override
  protected void gameStartWatcher() {
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
                    getStateFromServer();
                    if (enableLogging) {
                      analyzer.addGameState(currentState);
                    }
                    aiPlayerStart();
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

  /**
   * Called from the getGameState method. Requests the server specific/set in the Client object to
   * send the current {@link GameState}. Also parses the response and saves data to local variables
   * for easier consumption by the UI. Overriden in the AI Client as it needs to update last game
   * more often for analyzer.
   *
   * @param teamID Team Name for the request. Read from the Client
   * @param teamSecret Team Secret for the Request. Read from the Client
   * @param move Move requested. Needs to be given by the UI
   * @throws SessionNotFound
   * @throws UnknownError
   * @author rsyed
   */
  @Override
  protected synchronized void gameStateHelper() {
    GameState gameState = new GameState();
    try {
      gameState = comm.getCurrentGameState(currentServer);
      normaliseGameState(gameState);
    } catch (SessionNotFound e) {
      throw new SessionNotFound("Session isnt available for this request");
    } catch (UnknownError e) {
      throw new UnknownError("Server Error or Setting error");
    }
    this.grid = gameState.getGrid();
    if (enableQueue) {
      if (isNewGameState(gameState)) {
        this.fifoQueue.offer(gameState);
      }
    }
    this.currentTeamTurn = gameState.getCurrentTeam();
    this.lastMove = gameState.getLastMove();
    if (enableLogging) {
      this.analyzer.addMove(gameState.getLastMove());
    }
    updateLastTeam();
    this.teams = gameState.getTeams();
    this.currentState = gameState;
  }
}
