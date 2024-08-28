package org.ctf.ui.creators.settings.components;

import org.ctf.shared.constants.Constants;
import org.ctf.ui.data.Formatter;
import javafx.scene.layout.VBox;

/**
 * Contains the abstract {@link ChooseDoubleBox} class and all of its implementations.
 * 
 * @author sistumpf
 */
public class DoubleBoxFactory {
  /**
   * Abstract {@link ChooseNumberBox} implementation for Double.
   * Applies a default Formatter for Doubles between 0 and 1,
   * implements {@link getValue()} for Double.
   * 
   * @author sistumpf
   */
  abstract public static class ChooseDoubleBox extends ChooseNumberBox<Double> {
    public ChooseDoubleBox(VBox settingsBox, String postfix, String userData) {
      super(settingsBox);
      super.setPostfix(postfix);
      super.setUserData(userData);
    }

    @Override
    protected void applyFormatter() {
      Formatter.applyDoubleFormatter(content, 0., 1.);
    }

    @Override
    public Double getValue() {
      return Double.valueOf(content.getText());
    } 
  }
  
  /**
   * Implementation of {@link ChooseDoubleBox} for background map image opacity
   */
  public static class ChooseMapOpacityBox extends ChooseDoubleBox {
    public ChooseMapOpacityBox(VBox settingsBox) {
      super(settingsBox, "0-1", "opacity");
    }

    @Override
    protected Double getInitialValue() {
      return Constants.backgroundImageOpacity;
    }
  }

  /**
   * Implementation of {@link ChooseDoubleBox} for global background opacity
   */
  public static class ChooseBackgroundOpacityBox extends ChooseDoubleBox {
    public ChooseBackgroundOpacityBox(VBox settingsBox) {
      super(settingsBox, "0-1", "bgOpacity");
    }

    @Override
    protected Double getInitialValue() {
      return Constants.showBackgrounds;
    }
  }

  /**
   * Implementation of {@link ChooseDoubleBox} for figure glow
   */
  public static class ChooseGlowSpreadBox extends ChooseDoubleBox {
    public ChooseGlowSpreadBox(VBox settingsBox) {
      super(settingsBox, "0-1", "glowSpread");
    }

    @Override
    protected Double getInitialValue() {
      return Constants.borderGlowSpread;
    }
  }

}
