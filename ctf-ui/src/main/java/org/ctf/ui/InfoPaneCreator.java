package org.ctf.ui;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

/**
 * provides methods and constants to generate info panes which pop up when 
 * hovering over nodes. 
 * 
 * @author aniemesc
 */
public class InfoPaneCreator {
  public static final int CENTERED = 0;
  public static final int TOP = 0;
  public static final int BOTTOM = 0;
  public static final int RIGHT = 0;
  public static final int LEFT = 0;
 
  public static void addInfoPane(Node node,Scene scene,String info,int position) {
    EventHandler<? super MouseEvent> previousEnter = node.getOnMouseEntered();
    EventHandler<? super MouseEvent> previousLeave = node.getOnMouseExited();
    
  }
  
  private static StackPane createPopUpContent(Scene scene,String info) {
    StackPane stack = new StackPane();
    stack.getStyleClass().add("info-pane");
    stack.prefWidthProperty().bind(scene.widthProperty().multiply(0.25));
    return null;
    
  }
}
