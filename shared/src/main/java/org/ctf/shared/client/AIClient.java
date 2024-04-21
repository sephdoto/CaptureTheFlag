package org.ctf.shared.client;

import java.util.ArrayList;
import org.ctf.shared.ai.AI_Controller;
import org.ctf.shared.client.lib.Analyzer;
import org.ctf.shared.client.service.CommLayerInterface;
import org.ctf.shared.constants.Constants.AI;
import org.ctf.shared.state.Move;

/**
 * Extension of Client with support for AI functions
 *
 * @author rsyed
 */
public class AIClient extends Client implements Runnable {

  public AI selectedPlayer;
  public ArrayList<Move> moves;
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
    if (enableSaveGame) {
      saveGameHandler();
    }
  }

  @Override
  public void run() {
    try {
      AI_Controller controller = new AI_Controller(getCurrentState(), this.selectedPlayer);
      // checks if game has a start date and no end date
      while ((this.getEndDate() == null) && (this.getStartDate() != null)) {
        this.getSessionFromServer();
        this.getStateFromServer();
        if (turnSupportFlag) {
          if (isItMyTurn()) {
            this.makeMove(controller.getNextMove());
          }
        } else {
          // TODO Code for ALT Turn making support if server is badly written
        }

        this.getSessionFromServer();
        controller.update(getCurrentState());
        Thread.sleep(800);
      }
    } catch (Exception e) {

    }
  }

  private void saveGameHandler() {
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
  }
}
