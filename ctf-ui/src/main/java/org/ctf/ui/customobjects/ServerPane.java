package org.ctf.ui.customobjects;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
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
    this.widthProperty().addListener((obs, old, newV) -> {
      this.setPadding(new Insets(newV.doubleValue() * 0.1));
    });
    text = new Text("START LOCAL SERVER");
    text.fontProperty().bind(Bindings.createObjectBinding(() -> Font.font("Century Gothic", this.getWidth() / 15), this.widthProperty()));
    text.setFill(Color.WHITE);
    this.getChildren().add(text);
    createPortField();
    this.setOnMouseEntered(e -> {
      this.getChildren().remove(text);
      this.getChildren().add(field);
    });
    this.setOnMouseExited(e -> {
      this.getChildren().remove(field);
      this.getChildren().remove(text);
      this.getChildren().add(text);
    });
  }

  /**
   * Creates and styles the port {@link TextField} for the ServerPane.
   * 
   * @author aniemesc
   */
  private void createPortField() {
    field = new TextField();
    field.maxWidthProperty().bind(this.widthProperty().multiply(0.6));
    field.prefHeightProperty().bind(this.heightProperty().multiply(0.4));
    field.setPromptText("ENTER PORT");
    field.getStyleClass().add("transparent-textfield");
    field.positionCaret(0);
    field.fontProperty().bind(Bindings.createObjectBinding(() -> Font.font("Century Gothic", this.getWidth() / 12), this.widthProperty()));
    field.setTextFormatter( new TextFormatter<> (c ->
    {
      if (c.getControlNewText().equals("")) {
        return c;
      } else if (c.getControlNewText().matches("-?\\d*")) {
        if(Integer.parseInt(c.getControlNewText()) <= 65535)
          return c;
      }
      return null;
    }));
    StackPane.setAlignment(field, Pos.CENTER);
  }

  /**
   * puts the ServerPane in a static condition.
   * 
   * @author aniemesc
   */
  public void setFinished() {
    this.getChildren().clear();
    this.text.setText("Change Port");
    this.getChildren().add(text);
    this.setOnMouseClicked(e -> {
      this.getChildren().clear();
      this.getChildren().add(field);
    });
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
