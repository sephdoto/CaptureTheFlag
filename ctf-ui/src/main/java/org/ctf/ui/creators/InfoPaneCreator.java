package org.ctf.ui.creators;

import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;

/**
 * provides methods and constants to generate custom Popups when 
 * hovering over nodes. 
 * 
 * @author aniemesc
 */
public class InfoPaneCreator {
  public static final int TOP = 1;
  public static final int BOTTOM = 2;
  public static final int RIGHT = 3;
  public static final int LEFT = 4;
 
  
  /**
   * Sets the EventHandler for hovering of a node so that a custom Popup appears. The coordinates of
   * the Popup depend on the stated position value. The custom PopUp displays a String value within
   * a styled container. Previous existing EventHandler get incorporated.
   * 
   * @author aniemsc
   * @param node - Node object whose EventHandler gets modified
   * @param stage - MainStage needed to place the custom Popup
   * @param info - String value that shall be displayed
   * @param position - Positioning Constant
   */
  public static void addInfoPane(Node node,Stage stage,String info,int position) {
    EventHandler<? super MouseEvent> previousEnter = node.getOnMouseEntered();
    EventHandler<? super MouseEvent> previousLeave = node.getOnMouseExited();
    Popup popUp = new Popup();
    popUp.getContent().add(createPopUpContent(stage, info));   
    node.setOnMouseEntered( e -> {
      double[] coordinates = getCoordinates(node, position,popUp);
     if(previousEnter!=null) {
       previousEnter.handle(e);
     }    
      popUp.show(stage,coordinates[0], coordinates[1]);     
    });
    node.setOnMouseExited(e -> {
      if(previousEnter!=null) {
        previousLeave.handle(e);
      }   
      popUp.hide();
    });
    
  }
  
  /**
   * Creates and styles the content used for an custom PopUp. The StackPane gets resized 
   * according to the width of the stage and the text gets formatted according
   * to the available space.
   * 
   * @author aniemesc
   * @param stage - Main Stage used for resize properties
   * @param info - String value that shall be displayed
   * @return StackPane object which is the main container of the custom Popup
   */
  private static StackPane createPopUpContent(Stage stage,String info) {
    StackPane stack = new StackPane();
    stack.getStyleClass().add("info-pane");
    stack.prefWidthProperty().bind(stage.widthProperty().multiply(0.25));
    Text text = new Text(info);
    text.styleProperty().bind(Bindings.createStringBinding(() ->
    "-fx-font-size: " + stage.getWidth() / 80, stage.widthProperty()));
    text.wrappingWidthProperty().bind(stage.widthProperty().multiply(0.23));
    text.setFill(Color.WHITE);
    stack.setPadding(new Insets(20));
    stack.getChildren().add(text);
    return stack;   
  }
  
  /**
   * Calculates the relative coordinates for the placement of the custom Popup. In the returned
   * array the first entry resembles the x value and the second entry resembles the y value.
   * Coordinate calculations are depending on the used positioning constant.
   * 
   * @author aniemesc
   * @param node - Node that is needed to get base coordinates
   * @param position - Positioning Constant
   * @param popUp - custom Popup needed for offset calculations
   * @return double array filled with coordinate entries
   */
  private static double[] getCoordinates(Node node,int position,Popup popUp) {
    double[] result = new double[2];
    double popupWidth = popUp.getContent().get(0).prefWidth(0);
    double popUpHeight = popUp.getContent().get(0).prefHeight(0);
    switch (position) {
      case (InfoPaneCreator.RIGHT):
        result[0] = node.localToScreen(node.getBoundsInLocal()).getMaxX();
        result[1] = node.localToScreen(node.getBoundsInLocal()).getCenterY()-popUpHeight/2;
        return result;
      case (InfoPaneCreator.LEFT):
        result[0] = node.localToScreen(node.getBoundsInLocal()).getMinX()- popupWidth;
        result[1] = node.localToScreen(node.getBoundsInLocal()).getCenterY()-popUpHeight/2;
        return result;
      case (InfoPaneCreator.TOP):
      result[0] = node.localToScreen(node.getBoundsInLocal()).getCenterX()- popupWidth/2;
      result[1] = node.localToScreen(node.getBoundsInLocal()).getMinY()-popUpHeight;
      return result;
      case (InfoPaneCreator.BOTTOM):
        result[0] = node.localToScreen(node.getBoundsInLocal()).getCenterX()- popupWidth/2;
      result[1] = node.localToScreen(node.getBoundsInLocal()).getMaxY();
    }
    return result;
  }
}
