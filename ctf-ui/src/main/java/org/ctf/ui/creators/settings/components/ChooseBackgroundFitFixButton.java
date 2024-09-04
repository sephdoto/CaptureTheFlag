package org.ctf.ui.creators.settings.components;

import org.ctf.shared.constants.Constants;
import javafx.scene.layout.VBox;

/**
 * A button to change the useBackgroundResizeFix Constant.
 * Background Resize Fix is a fix for a problem where the background map is not resized properly.
 * It only occurs on a small number of PCs and we have not found the cure yet.
 * It seems like a calculation error where instead of floats, integers are used.
 * No clue tho, it only occurred for one person
 *  
 * @author sistumpf
 */
public class ChooseBackgroundFitFixButton extends ChooseBooleanButton{

  public ChooseBackgroundFitFixButton(VBox settingsBox) {
    super(settingsBox);

    setUserData("backgroundFitFix");
  }

  /**
   * Starts the BackgroundCalculatorThread for all local AI Clients
   */
  @Override
  protected void switchOn() {
    super.switchOn();
    Constants.useBackgroundResizeFix = true;
  }

  /**
   * Stops the BackgroundCalculatorThread for all local AI Clients
   */
  @Override
  protected void switchOff() {
    super.switchOff();
    Constants.useBackgroundResizeFix = false;
  }

  @Override
  protected boolean readInitialValue() {
    return Constants.useBackgroundResizeFix;
  }
}

