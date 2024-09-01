package org.ctf.ui.creators.settings.components;

import org.ctf.shared.constants.Constants;
import org.ctf.ui.data.Formatter;
import javafx.scene.layout.VBox;

/**
 * Contains the abstract {@link ChooseIntegerBox} class and all of its implementations.
 * 
 * @author sistumpf
 */
public class IntegerBoxFactory {
  /**
   * Abstract {@link ChooseNumberBox} implementation for Integer.
   * Applies a default Formatter for positive Integers,
   * implements {@link getValue()} for Integer.
   * 
   * @author sistumpf
   */
  abstract public static class ChooseIntegerBox extends ChooseNumberBox<Integer> {
    public ChooseIntegerBox(VBox settingsBox, String postfix, String userData) {
      super(settingsBox);
      super.setPostfix(postfix);
      super.setUserData(userData);
    }

    @Override
    protected void applyFormatter() {
      Formatter.applyIntegerFormatter(content, 0, null);
    }

    @Override
    public Integer getValue() {
      return Integer.valueOf(content.getText());
    } 
  }

  /**
   * Implementation of {@link ChooseIntegerBox} for AI think time in seconds
   */
  public static class ChooseAiThinkingTimeBox extends ChooseIntegerBox {
    public ChooseAiThinkingTimeBox(VBox settingsBox) {
      super(settingsBox, "s   ", "aiThinkTime");
    }

    @Override
    protected Integer getInitialValue() {
      return Constants.analyzeTimeInSeconds;
    }
  }
  
  /**
   * Implementation of {@link ChooseIntegerBox} for UI update time
   */
  public static class ChooseUiUpdateTimeIntegerBox extends ChooseIntegerBox {
    public ChooseUiUpdateTimeIntegerBox(VBox settingsBox) {
      super(settingsBox, "ms", "updateTime");
    }

    @Override
    protected Integer getInitialValue() {
      return Constants.UIupdateTime;
    }
  }
  
  /**
   * Implementation of {@link ChooseIntegerBox} for the time RandomAI sleeps when making a move
   */
  public static class ChooseRandomAISleepTimeBox extends ChooseIntegerBox {
    public ChooseRandomAISleepTimeBox(VBox settingsBox) {
      super(settingsBox, "ms", "randomSleepTime");
    }
    
    @Override
    protected Integer getInitialValue() {
      return Constants.randomAiSleepTimeMS;
    }
  }
}
