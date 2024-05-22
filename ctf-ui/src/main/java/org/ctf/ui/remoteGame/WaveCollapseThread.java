package org.ctf.ui.remoteGame;

import org.ctf.shared.constants.Constants;
import org.ctf.shared.wave.WaveFunctionCollapse;
import org.ctf.ui.HomeSceneController;
import org.ctf.ui.PlayGameScreenV2;
import javafx.application.Platform;

/**
 * Generates the dynamic background for a remote {@link PlayGameScreenV2} using
 * {@link WaveFunctionCollapse}. This is required as the remote client has no knowledge of the map
 * template before the game starts and generating the dynamic background image is highly time
 * consuming which massively delay the initialization of the {@link PlayGameScreenV2}. When finished
 * it updates the {@link PlayGameScreenV2}.
 * 
 * @author aniemesc
 */
public class WaveCollapseThread extends Thread {
  private String[][] grid;
  private HomeSceneController hsc;

  public WaveCollapseThread(String[][] grid, HomeSceneController hsc) {
    this.grid = grid;
    this.hsc = hsc;
  }

  /**
   * Saves a dynamic background image to resources and updates the {@link PlayGameScreenV2}.
   * 
   * @author aniemesc
   */
  public void run() {
    WaveFunctionCollapse backgroundcreator = new WaveFunctionCollapse(grid, Constants.theme);
    backgroundcreator.saveToResources();
    Platform.runLater(() -> {
      hsc.getPlayGameScreenV2().UpdateLeftSide();
    });
  }
}
