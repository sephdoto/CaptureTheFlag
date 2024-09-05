package org.ctf.ui.creators.settings.components;

import org.ctf.shared.constants.Constants;
import javafx.scene.layout.VBox;

/**
 * A button to change the showAiStats Constant.
 * If enabled, Dialogs appear after an AI made a move
 *  
 * @author sistumpf
 */
public class ChooseAiStatsButton extends ChooseBooleanButton{

  public ChooseAiStatsButton(VBox settingsBox) {
    super(settingsBox);

    setUserData("showAiStats");
  }

  /**
   * Starts the BackgroundCalculatorThread for all local AI Clients
   */
  @Override
  protected void switchOn() {
    super.switchOn();
    Constants.showAiStats = true;
  }

  /**
   * Stops the BackgroundCalculatorThread for all local AI Clients
   */
  @Override
  protected void switchOff() {
    super.switchOff();
    Constants.showAiStats = false;
  }

  @Override
  protected boolean readInitialValue() {
    return Constants.showAiStats;
  }
}

