package org.ctf.ui.customobjects;

import java.util.ArrayList;
import org.ctf.shared.constants.Descriptions;
import org.ctf.shared.constants.Enums.AIConfigs;
import org.ctf.shared.constants.Enums.ImageType;
import org.ctf.ui.controllers.ImageController;
import org.ctf.ui.creators.InfoPaneCreator;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Custom StackPane used for selecting an AI. It displays the AI type and an icon that displays more
 * information about the type when hovering. When clicked it shows two Buttons for loading and
 * editing AI configurations
 * 
 * @author aniemesc
 */
public class ButtonPane extends StackPane {
  private Button loadButton;
  private Button editButton;
  private Text text;
  private ImageView vw;

  /**
   * Provides a layout and creates all UI components for selecting an AI type.
   * 
   * @author aniemec
   * @param aiName - name of the AI type
   * @param stage - main stage of the application
   * @param InfoPanePosition - constant providing the positioning for the {@link InfoPaneCreator}
   * @param connected - List of all ButtonPanes in the application
   */
  public ButtonPane(AIConfigs aiName, Stage stage, int InfoPanePosition,
      ArrayList<ButtonPane> connected) {
    loadButton = createConfigButton("Load");
    editButton = createConfigButton("Edit");
    this.getStyleClass().add("ai-button-easy");
    text = new Text(aiName.toString());
    text.fontProperty().bind(Bindings.createObjectBinding(
        () -> Font.font("Century Gothic", this.getHeight() * 0.3), this.heightProperty()));
    StackPane.setAlignment(text, Pos.CENTER_LEFT);
    this.widthProperty().addListener((obs, old, newV) -> {
      double padding = newV.doubleValue() * 0.05;
      this.setPadding(new Insets(padding));
    });
    text.setFill(Color.WHITE);
    text.setMouseTransparent(true);
    this.getChildren().add(text);
    Image mp = ImageController.loadThemedImage(ImageType.MISC, "i1");
    vw = new ImageView(mp);
    StackPane.setAlignment(vw, Pos.CENTER_RIGHT);
    vw.fitHeightProperty().bind(this.heightProperty().multiply(0.5));
    vw.setPreserveRatio(true);
    this.getChildren().add(vw);
    InfoPaneCreator.addInfoPane(vw, stage, Descriptions.describe(aiName), InfoPanePosition);
    this.setOnMouseClicked(e -> {
      this.getChildren().clear();
      this.getChildren().add(createButtonBox());
      for (ButtonPane bp : connected) {
        if (bp != this) {
          bp.reset();
        }
      }
    });
  }

  /**
   * Creates a container for the edit and load buttons.
   * 
   * @author aniemesc
   * @return {@link HBox} container
   */
  private HBox createButtonBox() {
    HBox hbox = new HBox();
    StackPane.setAlignment(hbox, Pos.CENTER);
    hbox.setAlignment(Pos.CENTER);
    hbox.spacingProperty().bind(this.widthProperty().multiply(0.05));
    hbox.getChildren().addAll(loadButton, editButton);
    return hbox;
  }

  /**
   * Creates a styled and resizable Button which is used in the ButtonPane.
   * 
   * @author aniemesc
   * @param label - String value
   * @return {@link Button}
   */
  private Button createConfigButton(String label) {
    Button but1 = new Button(label);
    but1.prefWidthProperty().bind(this.widthProperty().multiply(0.4));
    but1.prefHeightProperty().bind(this.heightProperty().multiply(0.4));
    but1.getStyleClass().add("config-button");
    but1.prefHeightProperty().addListener((obs, oldv, newV) -> {
      double size = newV.doubleValue() * 0.5;
      but1.setFont(Font.font("Century Gothic", size));
    });
    return but1;
  }

  /**
   * Resets the ButtonPane to its initial state displaying the AI type
   * 
   * @author aniemesc
   */
  public void reset() {
    this.getChildren().clear();
    this.getChildren().addAll(text, vw);
  }

  public Button getLoadButton() {
    return this.loadButton;
  }

  public Button getEditButton() {
    return this.editButton;
  }
}
