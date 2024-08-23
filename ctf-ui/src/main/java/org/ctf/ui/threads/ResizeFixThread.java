package org.ctf.ui.threads;

import org.ctf.ui.data.SceneHandler;
import javafx.stage.Stage;

/**
 * Resizes the UI a tiny bit to force everything to be on its right place.
 * 
 * @deprecated the bug this Thread should fix shall not be a problem anymore.
 * @author sistumpf
 */
public class ResizeFixThread extends Thread {
  Stage stage;
  int resizeFS;
  boolean finished;

  public ResizeFixThread(Stage stage) {
    this.stage = stage;
    this.finished = false;
    this.resizeFS = 0;
  }

  @Override
  public void run() {
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) { e.printStackTrace(); }
    if(!finished) {
      stage.setHeight(stage.getHeight() -1); 
      resizeFS = 1;
    }
    try {
      Thread.sleep(30);
    } catch (InterruptedException e) { e.printStackTrace(); }
    if(!finished) {
      stage.setHeight(stage.getHeight() +1);
      resizeFS = 2;
    }
    this.finished = true;
  }

  @Override
  public void interrupt() {
    this.finished = true;
    switch(resizeFS) {
      case 0: /* do nothing */ break;
      case 1: stage.setHeight(stage.getHeight() +1); 
      SceneHandler.getCurrentScene().getRoot().layout();
      SceneHandler.getCurrentScene().getRoot().requestLayout();
      break;
      case 2: /* do nothing */ break;
    }
  }

  /**
   * @return true if the thread has already finished
   */
  public boolean isFinished() {
    return finished;
  }
}
