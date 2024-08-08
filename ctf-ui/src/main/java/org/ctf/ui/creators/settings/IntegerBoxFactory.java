package org.ctf.ui.creators.settings;

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

  static class ChooseUiUpdateTimeIntegerBox extends ChooseIntegerBox{
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
