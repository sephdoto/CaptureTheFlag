package org.ctf.ui;

import org.ctf.shared.constants.Constants;
import org.ctf.shared.wave.WaveFunctionCollapse;
import javafx.application.Platform;

public class WaveCollapseThread extends Thread {
  private String[][] grid;
  private HomeSceneController hsc;
  public WaveCollapseThread(String[][] grid,HomeSceneController hsc) {
    this.grid = grid;
    this.hsc = hsc;
  }
  
  public void run() {
    WaveFunctionCollapse backgroundcreator =
        new WaveFunctionCollapse(grid, Constants.theme);
    backgroundcreator.saveToResources();
    Platform.runLater(()->{
      hsc.getPlayGameScreenV2().UpdateLeftSide();
    });
  }
}
