package org.ctf.ui.customobjects;

import java.io.File;
import org.ctf.ui.EditorScene;
import org.ctf.ui.controllers.SoundController;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class DragAndDropPane extends StackPane{
  public static final int SOUNDS = 0;
  public static final int IMAGES = 1;
  private EditorScene scene; 
  
  public DragAndDropPane(EditorScene scene,String label,int constant) {
    this.scene = scene;
    this.widthProperty().addListener((obs,old,newV) -> {
      double padding = newV.doubleValue()*0.1; 
      this.setPadding(new Insets(padding));
    });
    
    VBox wrapper = createWrapper(label,constant);
    this.getChildren().add(wrapper);
    Button playButton = createSoundButton();

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
          switch (constant) {
            case DragAndDropPane.SOUNDS:
              this.getChildren().clear();
              wrapper.getChildren().add(playButton);
              this.getChildren().add(wrapper);              
              break;
            case DragAndDropPane.IMAGES:
            this.getChildren().clear();
            Image image = new Image(file.toURI().toString());
            ImageView imageView = new ImageView(image);
            imageView.fitWidthProperty().bind(this.widthProperty().multiply(0.5));
            imageView.fitHeightProperty().bind(imageView.fitWidthProperty());
            StackPane.setAlignment(imageView, Pos.BOTTOM_CENTER);
            this.getChildren().add(wrapper);
            this.getChildren().add(imageView);
          }            
      }
      event.setDropCompleted(success);
      event.consume();   
    });
  }
  
  private VBox createWrapper(String label,int constant) {
    VBox wrapper = new VBox();
    wrapper.setSpacing(25);
    StackPane.setAlignment(wrapper, Pos.TOP_CENTER);
    switch (constant) {
      case DragAndDropPane.SOUNDS:
        wrapper.setAlignment(Pos.CENTER);
        break;
      case DragAndDropPane.IMAGES:
        wrapper.setAlignment(Pos.TOP_CENTER);
    }  
    
    Text text = new Text(label);
    text.getStyleClass().add("custom-header");
    text.setTextAlignment(TextAlignment.CENTER);
    StackPane.setAlignment(text, Pos.CENTER); 
    text.fontProperty().bind(Bindings.createObjectBinding(
        () -> Font.font("Century Gothic", this.getWidth() / 20), this.widthProperty()));
    wrapper.getChildren().add(text);
    return wrapper;
  }
  
  private Button createSoundButton() {
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
    return playButton;
  }
  
  
}
