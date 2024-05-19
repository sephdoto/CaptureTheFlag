package org.ctf.ui.controllers;

import org.ctf.shared.ai.GameUtilities;
import org.ctf.shared.state.data.exceptions.UnknownError;
import org.ctf.ui.EditorScene;
import org.ctf.ui.GamePane;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;

public class MapPreviewThread extends Thread {
  private EditorScene editorScene;

  public MapPreviewThread(EditorScene scene) {
    this.editorScene = scene;
  }

  public void run() {
    try {
      MapPreview mp = new MapPreview(editorScene.getEngine().getTmpTemplate());
      GamePane gp = new GamePane(mp.getGameState());
      Platform.runLater(() -> {
        //editorScene.inform("hey");
        StackPane root = editorScene.getVisualRoot();
        if(root.getChildren().size() > 0 && root.getChildren().get(0) instanceof GamePane ) {
          ((GamePane) root.getChildren().get(0)).destroyReferences();
        }
        root.getChildren().clear();
        root.getChildren().add(gp);
        cleanUp();
      });
    } catch (UnknownError e) {
      Platform.runLater(() -> {
        editorScene.inform("Not enough Space on one Site!");
        editorScene.setValidTemplate(false);
        editorScene.getVisualRoot().getChildren().clear();
        editorScene.getVisualRoot().getChildren().add(editorScene.getInvalidText());
        cleanUp();
      });
    }
  }
  
  public void cleanUp() {
    this.editorScene = null;
  }
}