package org.ctf.ui.creators.settings.components;

import org.ctf.shared.constants.Constants;
import javafx.scene.layout.VBox;

/**
 * Button to enable and disable Tournament Mode
 * 
 * @author sistumpf
 */
public class ChooseTournamentModeButton extends ChooseBooleanButton {
  
  public ChooseTournamentModeButton(VBox settingsBox) {
    super(settingsBox);
    setUserData("tournamentMode");
  }

  /**
   * Starts the BackgroundCalculatorThread for all local AI Clients
   */
  @Override
  protected void switchOn() {
    super.switchOn();
    Constants.tournamentMode = true;
  }

  /**
   * Stops the BackgroundCalculatorThread for all local AI Clients
   */
  @Override
  protected void switchOff() {
    super.switchOff();
    Constants.tournamentMode = false;
  }
  
  @Override
  protected boolean readInitialValue() {
    return Constants.tournamentMode;
  }
}
