package org.ctf.ui.creators.settings;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * A basic boolean button that can be adjusted to change different values in Constants
 * 
 * @author sistumpf
 */
public abstract class ChooseBooleanButton extends StackPane implements ValueExtractable {
  protected final Rectangle back = new Rectangle(40, 10, Color.WHITE);
  protected final Button button = new Button();
  
  protected String colorOffSide = "#b30000";
  protected String colorOffMid = "#ff0000";
  protected String colorOnSide = "#00662e";
  protected String colorOnMid = "#00ff00";
  
  protected String buttonStyleOff = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 0.2, 0.0, 0.0, 2); -fx-background-color: " + colorOffSide + ";";
  protected String buttonStyleOn = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 0.2, 0.0, 0.0, 2); -fx-background-color: " + colorOnSide + ";";
  protected boolean state;


  public ChooseBooleanButton(VBox settingsBox) {
    createBooleanButton(settingsBox);
    
    EventHandler<Event> click = new EventHandler<Event>() {
      @Override
      public void handle(Event e) {
        if (state) {
          switchOff();
        } else {
          switchOn();
        }
      }
    };

    button.setFocusTraversable(false);
    setOnMouseClicked(click);
    button.setOnMouseClicked(click);

    setUserData("booleanButton");
  }

  protected void createBooleanButton(VBox settingsBox) {
    getChildren().addAll(back, button);
    setMinSize(40, 10);
    back.maxWidth(40);
    back.minWidth(40);
    back.maxHeight(10);
    back.minHeight(10);
    back.setArcHeight(back.getHeight());
    back.setArcWidth(back.getHeight());
    Double r = 2.0;
    button.setShape(new Circle(r));
    button.setMaxSize(15, 15);
    button.setMinSize(15, 15);
    
    if(readInitialValue()) {
      switchOn();
    } else {
      switchOff();
    }
  }

  /**
   * Switches the button on, adjusts the color and the saved boolean.
   */
  protected void switchOn() {
    button.setStyle(buttonStyleOn);
    back.setFill(Color.valueOf(colorOnMid));
    setAlignment(button, Pos.CENTER);
    button.setTranslateX(back.getX() + back.getWidth() /2 - 3);
    state = true;
  }
  
  /**
   * Switches the button on, adjusts the color and the saved boolean.
   */
  protected void switchOff() {
    button.setStyle(buttonStyleOff);
    back.setFill(Color.valueOf(colorOffMid));
    setAlignment(button, Pos.CENTER);
    button.setTranslateX(back.getX() - back.getWidth() /2 - 3);
    state = false;
  }
  
  /**
   * Loads in the variables boolean value from Constants when first loaded.
   * 
   * @return the boolean value from Constants
   */
  abstract protected boolean readInitialValue();
  
  @Override
  public Object getValue() {
    return state;
  }
}
