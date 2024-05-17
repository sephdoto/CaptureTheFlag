package org.ctf.ui.customobjects;

import java.io.File;
import org.ctf.ui.EditorScene;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class DragAndDropPane extends StackPane{
 EditorScene scene; 
  public DragAndDropPane(EditorScene scene) {
    this.scene = scene;
    Text text = new Text("Drag and Drop a \n sound file in the .wav format!");
    text.setTextAlignment(TextAlignment.CENTER);
    StackPane.setAlignment(text, Pos.CENTER);
    text.getStyleClass().add("custom-header");
    text.fontProperty().bind(Bindings.createObjectBinding(
        () -> Font.font("Century Gothic", this.getWidth() / 20), this.widthProperty()));
   this.getChildren().add(text);
    this.setOnDragOver(event -> {
      if (event.getGestureSource() != this && event.getDragboard().hasFiles()) {
        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
    }
    event.consume();
    });
    this.setOnDragDropped(event -> {
      Dragboard dragboard = event.getDragboard();
      boolean success = false;
      if (dragboard.hasFiles()) {
          File file = dragboard.getFiles().get(0);
         scene.inform(file.getName()+" was loaded.");
          scene.setCurrentSound(file);
      }
      event.setDropCompleted(success);
      event.consume();
    
    });
  }
}
