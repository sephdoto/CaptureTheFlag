package org.ctf.ui.creators.settings.components;

import org.ctf.shared.constants.Constants;
import javafx.scene.layout.VBox;

/**
 * Contains all implementations for the abstract {@link ChooseIntegerBox} class, to save space as the classes are pretty small.
 * 
 * @author sistumpf
 */
public class IntegerBoxFactory {
  public static ChooseUiUpdateTimeIntegerBox getUiUpdateBox(VBox settingsBox) {
    return new ChooseUiUpdateTimeIntegerBox(settingsBox);
  }
  
  public static ChooseAiThinkingTimeBox getAiThinkBox(VBox settingsBox) {
    return new ChooseAiThinkingTimeBox(settingsBox);
  }

  /**
   * AI think time in seconds box
   */
  public static class ChooseAiThinkingTimeBox extends ChooseIntegerBox {
    public ChooseAiThinkingTimeBox(VBox settingsBox) {
      super(settingsBox);
      super.setPostfix("s   ");
      super.setUserData("aiThinkTime");
    }

    @Override
    protected int getInitialValue() {
      return Constants.analyzeTimeInSeconds;
    }
  }
  
  /**
   * UI update time box
   */
  public static class ChooseUiUpdateTimeIntegerBox extends ChooseIntegerBox {
    public ChooseUiUpdateTimeIntegerBox(VBox settingsBox) {
      super(settingsBox);
      super.setPostfix("ms");
      super.setUserData("updateTime");
    }

    @Override
    protected int getInitialValue() {
      return Constants.UIupdateTime;
    }
  }
}
