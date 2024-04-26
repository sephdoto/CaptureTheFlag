package org.ctf.shared.client;

import org.ctf.shared.ai.AI_Controller;
import org.ctf.shared.ai.AI_Tools.InvalidShapeException;
import org.ctf.shared.ai.AI_Tools.NoMovesLeftException;
import org.ctf.shared.client.lib.Analyzer;
import org.ctf.shared.client.service.CommLayerInterface;
import org.ctf.shared.constants.Constants.AI;
import org.ctf.shared.state.data.exceptions.SessionNotFound;
import org.ctf.shared.state.data.map.MapTemplate;

/**
 * Extension of Client with support for AI functions
 *
 * @author rsyed
 */
public class AIClient extends Client {

  public AI selectedPlayer;
  public boolean enableSaveGame;
  public Analyzer analyzer;
  public MapTemplate sessionMapTemplate;
  public String alreadyCreatedSessionID;
  public String constructorSetTeamName;
  public boolean creatorMode;
  public boolean joinerMode;
  public String gameCreatedSessionID;

  AIClient(CommLayerInterface comm, String IP, String port, Boolean enableSaveGame, AI selected) {
    super(comm, IP, port, enableSaveGame);
    this.selectedPlayer = selected;
    this.enableSaveGame = enableSaveGame;
  }

  // Creator mode Constructor
  public AIClient(
      CommLayerInterface comm,
      String IP,
      String port,
      Boolean enableSaveGame,
      AI selected,
      MapTemplate mapTemplate,
      String constructorSetTeamName) {
    this(comm, IP, port, enableSaveGame, selected);
    this.sessionMapTemplate = mapTemplate;
    this.constructorSetTeamName = constructorSetTeamName;
    this.creatorMode = true;
    this.joinerMode = false;
  }

  // Joiner mode Constructor
  public AIClient(
      CommLayerInterface comm,
      String IP,
      String port,
      Boolean enableSaveGame,
      AI selected,
      String gameIDString,
      String constructorSetTeamName) {
    this(comm, IP, port, enableSaveGame, selected);
    this.alreadyCreatedSessionID = gameIDString;
    this.constructorSetTeamName = constructorSetTeamName;
    this.creatorMode = false;
    this.joinerMode = true;
  }

  public void runHandler() {
    AIGameHandler();
  }

  public void joinHandler() {}

  public void creatorHandler() {
    createGame(sessionMapTemplate);
    gameCreatedSessionID = getCurrentGameSessionID();
    joinGame(constructorSetTeamName);
  }

  public void pullData() {
    this.getStateFromServer();
    this.getSessionFromServer();
  }

  public void updateController(AI_Controller controller) {
    controller.update(getCurrentState());
  }

  public void AIGameHandler() {
    Thread gameSaverThread =
        new Thread(
            () -> {
              try {
                boolean doOnce = true;
                // checks if game has a start date and no end date
                boolean running = true;
                boolean playmode = true;
                while (running) {
                  /* if (creatorMode) {
                                     createGame(sessionMapTemplate);
                                     gameCreatedSessionID = getCurrentGameSessionID();
                                     joinGame(constructorSetTeamName);
                                     creatorMode = false;
                                     playmode = true;
                                   }

                                   if (joinerMode) {
                                     Thread.sleep(300);
                                     this.joinExistingGame("localhost", "8888", this.alreadyCreatedSessionID, "AITwo");
                                     joinerMode = false;
                                     playmode = true;
                                   }
                  */
                  if ((this.getEndDate() == null) && (this.getStartDate() != null) && playmode) {
                    pullData();

                    AI_Controller controller = new AI_Controller(getCurrentState(), AI.MCTS);

                    updateController(controller);
                    if (enableSaveGame) {
                      analyzer = new Analyzer();
                      if (doOnce) {
                        analyzer.addGameState(getCurrentState());
                        doOnce = false;
                      }
                      analyzer.addMove(getLastMove());
                    }
                    if (turnSupportFlag) {
                      if (isItMyTurn()) {
                        this.makeMove(controller.getNextMove());
                      }
                    } else {
                      // TODO Code for ALT Turn making support if server is badly written
                    }

                    Thread.sleep(800);
                  } else {
                    this.getSessionFromServer();
                    Thread.sleep(1500);
                  }
                }
              } catch (InterruptedException | NoMovesLeftException | InvalidShapeException e) {
                System.out.println("problem in AI COntroller");
                ;
              }
            });
    gameSaverThread.start();
  }

  /* private void saveGameHandler() {
    Thread gameSaverThread =
        new Thread(
            () -> {
              boolean running = true;
              if (enableSaveGame) {
                running = true;
              } else {
                running = false;
              }
              boolean doOnce = true;
              while (running) {
                try {
                  if (getStartDate() != null) {
                    analyzer = new Analyzer();

                    if (doOnce) {
                      this.getStateFromServer();
                      analyzer.addGameState(getCurrentState());
                      doOnce = false;
                    }
                    analyzer.addMove(getLastMove());
                  } else if (getStartDate() != null && getEndDate() != null) {
                    running = false;
                  }
                  Thread.sleep(100);
                } catch (InterruptedException e) {
                  throw new Error("Something went wrong in the Save Game Handler Thread");
                }
              }
            });
    gameSaverThread.start();
  } */

  /**
   * Watcher. Watches for game start
   *
   * @author rsyed
   */
  public void startWatcher() {
    Thread watcherThread =
        new Thread(
            () -> {
              boolean running = true;
              while (running) {
                try {
                  if (getCurrentGameSessionID() != null) {
                    this.getSessionFromServer(); // Gets Session from server
                    if (getStartDate() != null) {
                      this.getSessionFromServer();
                      AIGameHandler();
                      running = false;
                    }
                  }
                  Thread.sleep(1500);
                } catch (InterruptedException e) {
                  throw new Error("Something went wrong in the Client Thread");
                } catch (SessionNotFound e) {
                  System.out.println("Session Aint there");
                }
              }
            });
    watcherThread.start();
  }

  /**
   * Thead which handles client logic for when game has started
   *
   * @author rsyed
   */
  public void gameStartedThread() {
    AI_Controller controller = new AI_Controller(this.getCurrentState(), AI.MCTS);
    Thread gameThread =
        new Thread(
            () -> {
              boolean running = true;
              while (running) {
                try {
                  this.getSessionFromServer();
                  this.getStateFromServer();
                  if (isItMyTurn()) {
                    this.makeMove(controller.getNextMove());
                  }
                  Thread.sleep(100);
                  this.getSessionFromServer();
                  this.getStateFromServer();
                  controller.update(this.getCurrentState());

                  // TODO Additional Logic once Professor updates the server
                  Thread.sleep(500);
                } catch (InterruptedException | NoMovesLeftException | InvalidShapeException e) {
                  throw new Error("Something went wrong in the Client Thread");
                }
              }
            });
    gameThread.start();
  }

  public void setTemplate(MapTemplate mapTemplate) {
    this.sessionMapTemplate = mapTemplate;
  }
}
