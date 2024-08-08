package org.ctf.ui.creators.settings;

import org.ctf.shared.constants.Constants;
import javafx.scene.layout.VBox;

/**
 * Contains all implementations for the abstract {@link ChooseDoubleBox} class, to save space as the classes are pretty small.
 * 
 * @author sistumpf
 */
public class DoubleBoxFactory {

  public static ChooseMapOpacityBox getMapOpacityBox(VBox settingsBox) {
    return new ChooseMapOpacityBox(settingsBox);
  }
  
  static class ChooseMapOpacityBox extends ChooseDoubleBox {
    
    public ChooseMapOpacityBox(VBox settingsBox) {
      super(settingsBox);
      super.setPostfix("0-1");
      super.setUserData("opacity");
    }

    @Override
    protected double getInitialValue() {
      return Constants.backgroundImageOpacity;
    }
  }

}
