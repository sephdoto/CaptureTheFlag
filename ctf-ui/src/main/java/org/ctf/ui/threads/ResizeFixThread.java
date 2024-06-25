package org.ctf.ui.threads;

import javafx.stage.Stage;

/**
 * Resizes the UI a tiny bit to force everything to be on its right place
 * 
 * @author sistumpf
 */
public class ResizeFixThread extends Thread {
  Stage stage;

  public ResizeFixThread(Stage stage) {
    this.stage = stage;
  }

  @Override
  public void run() {
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) { e.printStackTrace(); }
    stage.setHeight(stage.getHeight() -1); 
    try {
      Thread.sleep(30);
    } catch (InterruptedException e) { e.printStackTrace(); }
    stage.setHeight(stage.getHeight() +1);
  }
}
