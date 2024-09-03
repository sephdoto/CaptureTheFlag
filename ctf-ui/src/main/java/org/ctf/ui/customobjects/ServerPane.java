package org.ctf.ui.customobjects;

import org.ctf.ui.data.Formatter;
import org.ctf.ui.data.SceneHandler;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Allows users to start a server by entering a port.
 * 
 * @author aniemesc
 */
public class ServerPane extends StackPane {
  private TextField field;
  private Text text;

  /**
   * Creates a resizable and styled container. When clicked a {@link TextField} gets added for the
   * user to enter the port number.
   * 
   * @author aniemesc
   */
  public ServerPane() {
    this.getStyleClass().add("server-pane");
    this.setWidth(1);
    this.paddingProperty().bind(Bindings.createObjectBinding(() -> new Insets(SceneHandler.getMainStage().widthProperty().divide(55).get()), this.widthProperty()));
    text = new Text("START LOCAL SERVER");
    text.fontProperty().bind(Bindings.createObjectBinding(() -> Font.font("Century Gothic", this.getWidth() / 13), this.widthProperty()));
    text.setFill(Color.WHITE);
    createPortField();
    field.setDisable(true);
    field.setVisible(false);
    this.getChildren().add(field);
    this.getChildren().add(text);
    this.setOnMouseEntered(e -> {
      text.setVisible(false);
      field.setDisable(false);
      field.setVisible(true);
      field.requestFocus();
    });
    this.setOnMouseExited(e -> {
      field.setDisable(true);
      field.setVisible(false);
      text.setVisible(true);
    });
  }

  /**   
   * Creates and styles the port {@link TextField} for the ServerPane.
   * 
   * @author aniemesc
   */
  private void createPortField() {
    field = new TextField();
    field.maxWidthProperty().bind(widthProperty().multiply(0.6));
    field.setPromptText("ENTER PORT");
    field.getStyleClass().add("transparent-textfield");
    field.positionCaret(0);
    field.fontProperty().bind(Bindings.createObjectBinding(() -> Font.font("Century Gothic", this.getWidth() / 12), this.widthProperty()));
    Formatter.applyIntegerFormatter(field, 1, 65535);
    StackPane.setAlignment(field, Pos.CENTER);
  }

  /**
   * puts the ServerPane in a static condition.
   * 
   * @author aniemesc
   */
  public void setFinished() {
    this.text.setText("Change Port");
    field.setDisable(true);
    field.setVisible(false);
    text.setVisible(true);
  }

  /**
   * Updates the promt text of the {@link TextField} of the ServerPane.
   * 
   * @author aniemesc
   */
  public void updatePromtText() {
    field.setText("");
    field.setPromptText("Port already in use");
  }
  
  
  
  public TextField getField() {
    return this.field;
  }

}
