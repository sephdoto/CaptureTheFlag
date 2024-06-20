package org.ctf.ui.customobjects;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

/**
 * Provides a custom Container that functions as a internal pop up window which disables the
 * background and that can be used in different parts of the application.
 * 
 * @author aniemesc
 */
public class PopUpPane extends StackPane {
  StackPane popUp;

  /**
   * Initializes and styles the custom PopUpPane.
   * 
   * @param scene - Scene on which PopUpPane should be placed
   * @param widthRatio - Percentage of Width that PopUpPane should cover
   * @param heightRatio - Percentage of Height that PopUpP
   */
  public PopUpPane(Scene scene, double width, double height, double opacity) {
    this.getStyleClass().add("blur-pane");
    popUp = new StackPane();
    popUp.maxWidthProperty().bind(this.widthProperty().multiply(width));
    popUp.maxHeightProperty().bind(this.heightProperty().multiply(height));
    popUp.getStyleClass().add("pop-up-pane");
    popUp.setStyle("-fx-background-color: rgba(0, 0, 0, " + opacity + ");");
    this.getChildren().add(popUp);
  }

  /**
   * Sets the content of a PopUpPane.
   * 
   * @author aniemesc
   * @param parent - Parent object
   */
  public void setContent(Parent parent) {
    popUp.getChildren().add(parent);
  }

  /**
   * Returns the internal Container of the PopUpPane.
   * 
   * @author aniemesc
   * @return
   */
  public StackPane getPopUp() {
    return popUp;
  }
}
