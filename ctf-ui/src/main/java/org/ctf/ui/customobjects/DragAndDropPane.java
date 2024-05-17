package org.ctf.ui.customobjects;

import java.io.File;
import org.ctf.ui.EditorScene;
import org.ctf.ui.controllers.SoundController;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class DragAndDropPane extends StackPane{
 EditorScene scene; 
  public DragAndDropPane(EditorScene scene) {
    this.scene = scene;
    VBox wrapper = new VBox();
    wrapper.setAlignment(Pos.CENTER);
    wrapper.setSpacing(25);
    StackPane.setAlignment(wrapper, Pos.CENTER);
    Text text = new Text("Drag and Drop a \n sound file in the .wav format!");
    text.setTextAlignment(TextAlignment.CENTER);
    StackPane.setAlignment(text, Pos.CENTER);
    text.getStyleClass().add("custom-header");
    text.fontProperty().bind(Bindings.createObjectBinding(
        () -> Font.font("Century Gothic", this.getWidth() / 20), this.widthProperty()));
    wrapper.getChildren().add(text);
    this.getChildren().add(wrapper);
    Button playButton = scene.createControlButton("Play loaded Sound", 0.16, 0.15);
    playButton.setOnAction(e -> {
      if(scene.getCurrentSound() == null) {
        scene.inform("Please enter a .wav sound File!");
        return;
      }
      String filename = scene.getCurrentSound().getName();
      if(!filename.substring(filename.length()-4, filename.length()).equals(".wav")) {
        scene.inform("Please enter a file in the .wav format!");
        return;
      }
      SoundController.playSound(scene.getCurrentSound());
    });
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
          wrapper.getChildren().add(playButton);
          this.getChildren().add(wrapper);
      }
      event.setDropCompleted(success);
      event.consume();
    
    });
  }
}
