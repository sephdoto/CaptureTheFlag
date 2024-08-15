package org.ctf.ui.creators.settings.components;

import org.ctf.shared.client.AIClient;
import org.ctf.shared.client.Client;
import org.ctf.shared.constants.Constants;
import org.ctf.ui.data.ClientStorage;
import javafx.scene.layout.VBox;

/**
 * A button to change the FULL_AI_POWER Constant
 * 
 * @author sistumpf
 */
public class ChooseFullAiPowerButton extends ChooseBooleanButton{

  public ChooseFullAiPowerButton(VBox settingsBox) {
    super(settingsBox);
    
    setUserData("fullAiPower");
  }

  /**
   * Starts the BackgroundCalculatorThread for all local AI Clients
   */
  @Override
  protected void switchOn() {
    super.switchOn();
    Constants.FULL_AI_POWER = true;
    for(Client client : ClientStorage.getLocalAIClients())
      ((AIClient) client).getController().startBct();
  }

  /**
   * Stops the BackgroundCalculatorThread for all local AI Clients
   */
  @Override
  protected void switchOff() {
    super.switchOff();
    Constants.FULL_AI_POWER = false;
    for(Client client : ClientStorage.getLocalAIClients())
      if(((AIClient) client).getController() != null)
        ((AIClient) client).getController().interruptBct();
  }
  
  @Override
  protected boolean readInitialValue() {
    return Constants.FULL_AI_POWER;
  }

}
