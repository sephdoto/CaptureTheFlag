package org.ctf.ui.remoteGame;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.state.data.exceptions.SessionNotFound;
import org.ctf.ui.App;
import org.ctf.ui.controllers.CheatboardListener;
import org.ctf.ui.data.ClientStorage;
import org.ctf.ui.data.SceneHandler;
import org.ctf.ui.hostGame.PlayGameScreenV2;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.layout.StackPane;

/**
 * Updates a {@link RemoteWaitingScene} and initializes a Scene switch to the
 * {@link PlayGameScreenV2} according to data pulled from a Client.
 * 
 * @author aniemesc
 * @author sistumpf
 */
public class RemoteWaitingThread extends Thread {
  private RemoteWaitingScene rws;
  private boolean isactive;

/**
 * Sets the {@link RemoteWaitingScene} and sets the running flag on true.
 * 
 * @author aniemesc
 * @param rws - The conected 
 */
  public RemoteWaitingThread(RemoteWaitingScene rws) {
    this.rws = rws;
    isactive = true;
  }
/**
 * Overwrites the background image in resources with an default picture and constantly pulls from the client
 * whether the game has started. If so it updates the {@link RemoteWaitingScene} and starts an {@link WaveCollapseThread} that
 * will finally generate the background picture.
 * 
 * @author aniemesc
 * @author sistumpf
 */
  public void run() {
    /*try {
      BufferedImage image = ImageIO.read(new File(
          Constants.toUIResources + File.separator + "pictures" + File.separator + "tuning1.png"));
      ImageIO.write(image, "png", new File(
          Constants.toUIResources + File.separator + "pictures" + File.separator + "grid.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }*/
    try {
      while (isactive) {
        try {
          Thread.sleep(50);
          String begin = (ClientStorage.getMainClient().isGameStarted()) ? "Initiliazing Game \n"
              : "Waiting for more Teams to Join ... \n";
          String teams = "There are currently " + rws.getServerManager().getCurrentNumberofTeams()
              + "/" + rws.getServerManager().getMaxNumberofTeams() + " in the lobby!";
          Platform.runLater(() -> {
            rws.getText().setText(begin + teams);
          });
          if (ClientStorage.getMainClient().isGameStarted() && ClientStorage.getMainClient().getGrid() != null) {
            isactive = false;
            Thread.sleep(100);
            Platform.runLater(() -> {
              SceneHandler.switchToPlayGameScene(true);
            });
          }
        } catch (SessionNotFound e) {
          this.isactive = false;
          ClientStorage.getMainClient().shutdown();

          for(int i=0; i<Constants.globalWaitingTime; i++) {
            final int ms = i;
            Platform.runLater(() -> {
              rws.getText().setText("Server closed by host. \nReturning to Home Screen in " 
            + Math.round((Constants.globalWaitingTime - ms)/100)/10.
            + " s");
            });
            Thread.sleep(1);
          }
          Platform.runLater(
              new Runnable() {
                public void run(){
                  SceneHandler.switchToHomeScreen();
                }
              }
              );
        }
      }

  } catch (InterruptedException e) {
    e.printStackTrace();
}
  }

}
