package org.ctf.ui.remoteGame;

import java.io.File;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.wave.WaveFunctionCollapse;
import org.ctf.ui.controllers.HomeSceneController;
import org.ctf.ui.data.SceneHandler;
import org.ctf.ui.hostGame.PlayGameScreenV2;
import javafx.application.Platform;

/**
 * Generates the dynamic background for a remote {@link PlayGameScreenV2} using
 * {@link WaveFunctionCollapse}. This is required as the remote client has no knowledge of the map
 * template before the game starts and generating the dynamic background image is highly time
 * consuming which massively delay the initialization of the {@link PlayGameScreenV2}. When finished
 * it updates the {@link PlayGameScreenV2}.
 * 
 * @author sistumpf
 * @author aniemesc
 */
public class WaveCollapseThread extends Thread {
  private String[][] grid;
  PlayGameScreenV2 changeBG;

  public WaveCollapseThread(PlayGameScreenV2 changeBG, String[][] grid) {
    this.grid = grid;
    this.changeBG = changeBG;
  }

  /**
   * Saves a dynamic background image to resources and updates the {@link PlayGameScreenV2}.
   * 
   * @author aniemesc
   */
  public void run() {
    WaveFunctionCollapse backgroundcreator = new WaveFunctionCollapse(grid, Constants.theme);
    if(!new File(Constants.toUIResources + "pictures" + File.separator + "grid.png").exists()) 
      backgroundcreator.saveToResources();
    while(!new File(Constants.toUIResources + "pictures" + File.separator + "grid.png").exists())
      try {
        Thread.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    Platform.runLater(() -> {
      try {
        changeBG.updateLeftSide();
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }
}
