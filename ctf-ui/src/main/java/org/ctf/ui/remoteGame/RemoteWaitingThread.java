package org.ctf.ui.remoteGame;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.ctf.shared.constants.Constants;
import org.ctf.ui.App;
import org.ctf.ui.PlayGameScreenV2;
import javafx.application.Platform;

/**
 * Updates a {@link RemoteWaitingScene} and initializes a Scene switch to the
 * {@link PlayGameScreenV2} according to data pulled from a Client.
 * 
 * @author aniemesc
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
 */
  public void run() {
    try {
      BufferedImage image = ImageIO.read(new File(
          Constants.toUIResources + File.separator + "pictures" + File.separator + "tuning1.png"));
      ImageIO.write(image, "png", new File(
          Constants.toUIResources + File.separator + "pictures" + File.separator + "grid.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      while (isactive) {
        Thread.sleep(1000);
        String begin = (rws.getClient().isGameStarted()) ? "Initiliazing Game \n"
            : "Waiting for more Teams to Join ... \n";
        String teams = "There are currently " + rws.getServerManager().getCurrentNumberofTeams()
            + "/" + rws.getServerManager().getMaxNumberofTeams() + " in the lobby!";
        Platform.runLater(() -> {
          rws.getText().setText(begin + teams);
        });
        if (rws.getClient().isGameStarted()) {
          isactive = false;
          Thread.sleep(1000);
          Platform.runLater(() -> {
            rws.getHsc().switchToPlayGameScene(App.getStage(), rws.getClient(), true);
          });
          WaveCollapseThread waveCollapseThread = new WaveCollapseThread(rws.getClient().getGrid(),rws.getHsc());
          waveCollapseThread.start();
        }
      }

    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
