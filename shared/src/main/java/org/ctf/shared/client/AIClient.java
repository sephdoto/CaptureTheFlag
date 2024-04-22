package org.ctf.shared.client;

import org.ctf.shared.ai.AI_Controller;
import org.ctf.shared.ai.AI_Tools.InvalidShapeException;
import org.ctf.shared.ai.AI_Tools.NoMovesLeftException;
import org.ctf.shared.client.lib.Analyzer;
import org.ctf.shared.client.service.CommLayerInterface;
import org.ctf.shared.constants.Constants.AI;
import org.ctf.shared.state.data.exceptions.SessionNotFound;

/**
 * Extension of Client with support for AI functions
 *
 * @author rsyed
 */
public class AIClient extends Client {

  public AI selectedPlayer;
  public boolean enableSaveGame;
  public Analyzer analyzer;

  AIClient(CommLayerInterface comm, String IP, String port) {
    super(comm, IP, port);
    // moves = new ArrayList<Move>();
  }

  AIClient(CommLayerInterface comm, String IP, String port, AI selected, Boolean enableSaveGame) {
    this(comm, IP, port);
    this.selectedPlayer = selected;
    this.enableSaveGame = enableSaveGame;
  }

  public void runHandler(){
    AIGameHandler();
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
                while (running) {
                  if ((this.getEndDate() == null) && (this.getStartDate() != null)) {
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
                  if(getCurrentGameSessionID() != null){
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
}
