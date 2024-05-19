package org.ctf.ui;

import org.ctf.shared.constants.Constants;
import org.ctf.shared.state.GameState;
import org.ctf.shared.wave.WaveFunctionCollapse;

import javafx.application.Platform;

public class WaveCollapseThread extends Thread {
  String[][] grid;
  public WaveCollapseThread(String[][] grid) {
    this.grid = grid;
  }
  
  public void run() {
    WaveFunctionCollapse backgroundcreator =
        new WaveFunctionCollapse(grid, Constants.theme);
    backgroundcreator.saveToResources();
  }
}
