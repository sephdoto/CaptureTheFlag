package org.ctf.ui.customobjects;

import java.io.File;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.Themes;
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

/**
 * Allows users to load and display files into an {@link EditorScene} via Drag and Drop. There are
 * constants for different file types such as image and sound files. According to the selected
 * constant the {@link EditorScene} gets updated in different ways. For images the file gets
 * displayed on the visual root. For sound files a play button appears.
 * 
 * @author aniemesc
 */
public class DragAndDropPane extends StackPane {
  public static final int SOUNDS = 0;
  public static final int IMAGES = 1;
  private EditorScene scene;

  /**
   * provides a layout, creates all required UI components and implements the drag and drop
   * mechanism.
   * 
   * @author aniemesc
   * @param scene - {@link EditorScene}
   * @param label - String value for the header
   * @param constant - claims whether image or sound files are loaded
   */
  public DragAndDropPane(EditorScene scene, String label, int constant) {
    this.scene = scene;
    this.widthProperty().addListener((obs, old, newV) -> {
      double padding = newV.doubleValue() * 0.1;
      this.setPadding(new Insets(padding));
    });

    VBox wrapper = createWrapper(label, constant);
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
        scene.inform(file.getName() + " was loaded.");
        switch (constant) {
          case DragAndDropPane.SOUNDS:
            scene.setCurrentSound(file);
            this.getChildren().clear();
            wrapper.getChildren().add(playButton);
            this.getChildren().add(wrapper);
            break;
          case DragAndDropPane.IMAGES:
            scene.setCurrentPicture(file);
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

  /**
   * Creates a {@link VBox} that is used as a wrapper within the DragAndDropPane. 
   * 
   * @author aniemesc
   * @param label - String value for header
   * @param constant - decides the Allignment of the header
   * @return {@link VBox} wrapper
   */
  private VBox createWrapper(String label, int constant) {
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
    if (Constants.theme.equals(Themes.BAYERN)) {
      text.getStyleClass().add("bayern-label");
    } else {
      text.getStyleClass().add("custom-header");
    }
    text.setTextAlignment(TextAlignment.CENTER);
    StackPane.setAlignment(text, Pos.CENTER);
    text.fontProperty().bind(Bindings.createObjectBinding(
        () -> Font.font("Century Gothic", this.getWidth() / 20), this.widthProperty()));
    wrapper.getChildren().add(text);
    return wrapper;
  }
  
  /**
   * Creates a play Button for sound files.
   * 
   * @author Aaron Niemesch
   * @return {@link Button} playButton
   */
  private Button createSoundButton() {
    Button playButton = scene.createControlButton("Play loaded Sound", 0.16, 0.15);
    playButton.setOnAction(e -> {
      if (scene.getCurrentSound() == null) {
        scene.inform("Please enter a .wav sound File!");
        return;
      }
      String filename = scene.getCurrentSound().getName();
      if (!filename.substring(filename.length() - 4, filename.length()).equals(".wav")) {
        scene.inform("Please enter a file in the .wav format!");
        return;
      }
      SoundController.playSound(scene.getCurrentSound());
    });
    return playButton;
  }


}
