package org.ctf.ui.creators.settings.components;

import org.ctf.shared.constants.Constants;
import javafx.scene.layout.VBox;

/**
 * Contains all implementations for the abstract {@link ChooseDoubleBox} class, to save space as the classes are pretty small.
 * 
 * @author sistumpf
 */
public class DoubleBoxFactory {
  public static class ChooseMapOpacityBox extends ChooseDoubleBox {
    
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
  
  public static class ChooseBackgroundOpacityBox extends ChooseDoubleBox {

    public ChooseBackgroundOpacityBox(VBox settingsBox) {
      super(settingsBox);
      super.setPostfix("0-1");
      super.setUserData("bgOpacity");
    }

    @Override
    protected double getInitialValue() {
      return Constants.showBackgrounds;
    }
  }
  
  public static class ChooseGlowSpreadBox extends ChooseDoubleBox {

    public ChooseGlowSpreadBox(VBox settingsBox) {
      super(settingsBox);
      super.setPostfix("0-1");
      super.setUserData("glowSpread");
    }

    @Override
    protected double getInitialValue() {
      return Constants.borderGlowSpread;
    }
  }

}
