package org.ctf.shared.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.ctf.shared.ai.AIConfig;
import org.ctf.shared.ai.AIController;
import org.ctf.shared.ai.GameUtilities;
import org.ctf.shared.ai.GameUtilities.InvalidShapeException;
import org.ctf.shared.ai.GameUtilities.NoMovesLeftException;
import org.ctf.shared.ai.MonteCarloTreeSearch;
import org.ctf.shared.client.service.CommLayerInterface;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.AI;
import org.ctf.shared.gameanalyzer.GameSaveHandler;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.data.exceptions.ForbiddenMove;
import org.ctf.shared.state.data.exceptions.GameOver;
import org.ctf.shared.state.data.exceptions.NoMoreTeamSlots;
import org.ctf.shared.state.data.exceptions.SessionNotFound;
import org.ctf.shared.state.data.exceptions.URLError;
import org.ctf.shared.state.data.exceptions.UnknownError;

/**
 * Extension of Client with support for additional functionality needed by the AI to perform its
 * functions automatically
 *
 * @author rsyed
 */
public class AIClient extends Client {
  private int controllerThinkingTime = Constants.aiDefaultThinkingTimeInSeconds;

  private AI selectedAI;
  private AIConfig aiConfig;
  private AIController controller;
  private boolean enableLogging;
  private String gameIDString;
  private String constructorSetTeamName;
  private boolean saveToken = true;
  private boolean controllerToken = true;
  private boolean firstGameStateToken = true;
  
  private String moveInfo;
  private String moreMoveInfo;
  
  ScheduledExecutorService aiClientScheduler = Executors.newScheduledThreadPool(1);
  ExecutorService aiPlayScheduler = Executors.newSingleThreadExecutor();

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
            }
            controller =
                new AIController(getCurrentState(), selectedAI, aiConfig, controllerThinkingTime, true);
            controllerToken = false;
          }
          pullData();
          if(isAlive)
            tryMakeMove();
          if(this.isGameOver()) {
            if(enableLogging)
              gameSaveHandler.getSavedGame().setWinner(winners);
            this.aiPlayScheduler.shutdown();
          } else {
            sleep();
            startPlayTask();
          }
        } catch (NoMovesLeftException | InvalidShapeException e) {
          System.out.println(e instanceof NoMovesLeftException ? "no moves left" :"invalid shape");
          throw new UnknownError("Games most likely over");
        } catch (GameOver e) {
          this.shutdown();
        } catch (NullPointerException e) {
          e.printStackTrace();
          logger.info("nullpointer exception for " + this.requestedTeamName +" , he is " + (isAlive ? "alive" : "dead"));
        }
      };

      /**
       * Kills all Client processes, shutting it down.
       * 
       * @author sistumpf
       */
      @Override
      public void shutdown() {
        super.shutdown();
        this.aiPlayScheduler.shutdown();
        this.aiClientScheduler.shutdown();
        if(getController() != null)
          this.getController().shutDown();
        if (saveToken && enableLogging) {
          this.gameSaveHandler.writeOut();
          saveToken = false;
        }
      }

      /**
       * Tries to update the AIController.
       * If it succeeds, the currentGameState is a new one, so its last Move is added to the gameSaveHandler.
       * If it is this clients turn, it makes a move.
       * In case the update returns false, a short delay can be added to postpone the next playTask.
       * 
       * @author sistumpf
       * @throws NoMovesLeftException
       * @throws InvalidShapeException
       */
      private void tryMakeMove() throws NoMovesLeftException, InvalidShapeException {
        boolean updated = getController().update(getCurrentState(), getCurrentState().getLastMove());
        if(updated || firstGameStateToken) {
          if(updated)
            firstGameStateToken = false;
          if (enableLogging) {
            if(!isMoveEmpty(getCurrentState().getLastMove()))
              this.gameSaveHandler.addMove(getCurrentState().getLastMove(), GameUtilities.teamsGaveUp(lastState, currentState));
          }
          if (isItMyTurn()) {
            Move move = getController().getNextMove();
            if(getController().getAi() != AI.RANDOM && getController().getMcts() != null) {
              MonteCarloTreeSearch ai = getController().getMcts();
              this.setMoveInfo(ai.getDepth(), ai.getExpansionCounter().get(), ai.getSimulationCounter().get(), ai.getHeuristicCounter().get(), ai.getChance(), ai.printResults(move));
              System.out.println(getController().getAi() + ":\n" + getController().getMcts().printResults(move));
            }
            if(!isGameOver())
              makeMove(move);
          }
        } else {
          // delay can be added HERE, does not have to tho. depends on the resource usage, AI or idk ...
          // Gedanken machen, bei random braucht man keinen delay TODO
        }
      }

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
    this.moveInfo = null;
    this.moreMoveInfo = null;
    if (enableLogging) {
      gameSaveHandler = new GameSaveHandler();
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
   * Used to send a give up request to the current session. Throws exceptions depending on errors
   *
   * @throws SessionNotFound (404)
   * @throws ForbiddenMove (403)
   * @throws GameOver (410)
   * @throws UnknownError (500)
   * @throws URLError (404)
   * @author rsyed
   * @author sistumpf
   */
  @Override
  public void giveUp() {
    //  logger.info(requestedTeamName + " wants to give up");
    comm.giveUp(currentServer, requestedTeamName, teamSecret);
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
    super.joinExistingGame(IP, port, gameSessionID, teamName);
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
  protected void startPlayTask() {
//    aiClientScheduler.scheduleWithFixedDelay(playTask, 10, aiClientRefreshTime, TimeUnit.MILLISECONDS);
    aiPlayScheduler.submit(playTask);
  }

  /**
   * A Watcher thread which calls GameSessionResponse periodically and hands over functionality to
   * GameStartedThread when it has and then terminates itself.
   *
   * @author rsyed
   */
  @Override
  protected void gameStartWatcher() {
    watcherThread =
        new Thread() {
      boolean active = true;

      @Override
      public void interrupt() {
        this.active = false;
      }

      @Override
      public void run() {
        while (active) {
          try {
            Long sleep = 1000L;
            getSessionFromServer(); // Gets Session from server
            if (getStartDate() != null) {
              getStateFromServer();
              if (enableLogging) {
                gameSaveHandler.addGameState(currentState);
              }
              active = false;
              new Thread(() -> {
                try {
                  Thread.sleep(100);
                } catch(Exception e) {e.printStackTrace();};
                startPlayTask();
              }).start();
              startMoveTimeThread();
            }
            Thread.sleep(sleep);
          } catch (InterruptedException e) {
            System.err.println("Something went wrong in the Client Thread");
          } catch (Exception ex) {
            //TODO just a reminder that nothing happens.
            //Errors will get caught when the client is ready but the game has not been started yet.
            //We dont need sleep time, we need the extra 20 ms for AI :)
            // ~simon
          }
        }
      }
    };
    watcherThread.start();
  }

  /**
   * Called from the getGameState method. Requests the server specific/set in the Client object to
   * send the current {@link GameState}. Also parses the response and saves data to local variables
   * for easier consumption by the UI. Overriden in the AI Client as it needs to update last game
   * more often for gameSaveHandler.
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
      initMyTeam(gameState);
      if (gameState.getTeams()[myTeam] == null) {
        if(isAlive)
          this.controller.shutDown();
        this.isAlive = false;
      }
    } catch (SessionNotFound e) {
      throw new SessionNotFound("Session isnt available for this request");
    } catch (UnknownError e) {
      throw new UnknownError("Server Error or Setting error");
    }


    if (isNewGameState(gameState)) {
      gameState = normalizeGameState(gameState);
      this.currentTeamTurn = gameState.getCurrentTeam();
      this.lastState = currentState;
      this.currentState = gameState;
      this.teams = gameState.getTeams();
      this.lastMove = gameState.getLastMove();
      updateLastTeam();
      this.grid = gameState.getGrid();

      if (enableQueue) {
        this.fifoQueue.offer(gameState);
      }

      if (enableLogging) {
        if(!isMoveEmpty(gameState.getLastMove()))
          this.gameSaveHandler.addMove(gameState.getLastMove(), GameUtilities.teamsGaveUp(lastState, currentState));
      }
    }
  }

  public AIController getController() {
    return controller;
  }

  public String getMoveInfo() {
    return moveInfo;
  }
  
  public String getMoreMoveInfo() {
    return moreMoveInfo;
  }

  public void setMoveInfo(int depth, int expansions, int simulations, int heuristics, double chance, String moreMoveInfo) {
    this.moveInfo = 
        "depth: " + depth + "\n" 
            + "expansions: " + expansions + "\n"
            + "simulations: " + simulations + "\n"
            + "heuristics: " + heuristics + "\n"
            + "certainty: " + (int)Math.round(chance) + "%";
    this.moreMoveInfo = moreMoveInfo;
  }
  
  public void clearMoveInfo() {
    this.moveInfo = null;
    this.moreMoveInfo = null;
  }
}