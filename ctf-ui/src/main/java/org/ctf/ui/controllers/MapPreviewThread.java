package org.ctf.ui.controllers;

import org.ctf.shared.state.data.exceptions.UnknownError;
import org.ctf.ui.EditorScene;
import org.ctf.ui.GamePane;
import javafx.application.Platform;

public class MapPreviewThread extends Thread {
  private EditorScene editorScene;
  
  public MapPreviewThread(EditorScene scene) {
    this.editorScene = scene;
  }
  
  public void run() {
      try {
        int count = 0;
          while (count==0) {
             
            try {
              MapPreview mp = new MapPreview(editorScene.getEngine().getTmpTemplate());
              GamePane gp = new GamePane(mp.getGameState());
              Platform.runLater(() -> {
                //editorScene.inform("hey");
                editorScene.getVisualRoot().getChildren().clear();
                editorScene.getVisualRoot().getChildren().add(gp);
             });
            } catch (UnknownError e) {
              // TODO: handle exception
              Platform.runLater(() -> {
                editorScene.inform("Not enough Space on one Site!");
                editorScene.setValidTemplate(false);
                editorScene.getVisualRoot().getChildren().clear();
                editorScene.getVisualRoot().getChildren().add(editorScene.getInvalidText());
             });
            }
            
            count++;
             
              Thread.sleep(1);
          }
      } catch (InterruptedException e) {
          e.printStackTrace();
      }
  }
}
