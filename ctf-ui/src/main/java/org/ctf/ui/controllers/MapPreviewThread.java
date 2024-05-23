package org.ctf.ui.controllers;

import org.ctf.shared.state.GameState;
import org.ctf.shared.state.data.exceptions.UnknownError;
import org.ctf.ui.editor.EditorScene;
import org.ctf.ui.map.GamePane;
import javafx.application.Platform;
import javafx.scene.layout.StackPane;

/**
 * Generates a {@link GamePane} that resembles a preview for what the map will look like when playing and 
 * updates the {@link EditorScene} when finished. A seperate thread for this task is crucial as calculations
 * for large templates can take some time and would cause the application to freeze when executed 
 * in the main thread.
 * 
 * @author aniemesc
 */
public class MapPreviewThread extends Thread {
  private EditorScene editorScene;
  
  /**
   * Connects to an {@link EditorScene}
   * 
   * @author aniemesc
   * @param scene {@link EditorScene}
   */
  public MapPreviewThread(EditorScene scene) {
    this.editorScene = scene;
  }

  /**
   * Generates a preview {@link GamePane}, cleans up the memory and updates the visual root of the 
   * {@link EditorScene}. If the server detects an invalid template due to random block placement 
   * the {@link EditorScene} gets informed.
   * 
   * @author aniemesc
   */
  public void run() {
    try {
      MapPreview mp = new MapPreview(editorScene.getEngine().getTmpTemplate());
      GameState state = mp.getGameState();
      editorScene.setState(state);
      GamePane gp = new GamePane(state,true,"");
      Platform.runLater(() -> {
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
  
  /**
   * Deletes the reference to the {@link EditorScene}.
   * 
   */
  public void cleanUp() {
    this.editorScene = null;
  }
}