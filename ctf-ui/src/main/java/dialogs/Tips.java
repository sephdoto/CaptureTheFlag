package dialogs;

import java.util.ArrayList;
import java.util.Random;
import org.ctf.shared.constants.Constants;
import org.ctf.ui.controllers.CheatboardListener;
import org.ctf.ui.data.SceneHandler;
import org.jnativehook.keyboard.NativeKeyEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * Tip Strings containing more or less useful information.
 * 
 * @author sistumpf
 */
public class Tips {
  private static ArrayList<String> tips;
  
  static {
    tips = new ArrayList<String>();
    
    tips.add("There is no true randomness in creating GameStates, so every Map looks like in the MapEditor.");
    tips.add("In the MapEditor, you can modify Pieces to walk the way you want, but also their appearance and sounds.");
    tips.add("This Dialog window is just a normal Alert window, but tuned to be unrecognizable. Want to see a normal Alert window?");
    tips.add("I don't feel so good...");
    tips.add("If you ever accidentally exit a scene, just type \"back\" to get taken back there. "
        + "This works for your last " + Constants.lastScenesSize + " scenes.");
    tips.add("Currently there are " + CheatboardListener.howManyCodes() + " different \"cheatCodes\".\n"
        + "Type \"info\" and they will be listed.");
    tips.add("You can click this window and drag it around.");
    
    
    
    //leave this at the bottom or it won't be accurate anymore
    tips.add("There are currently just " + (tips.size()+1) + " available tips. The first one got added on 02.09.2024.");
  }
  
  /**
   * @return a random tip from the collection
   */
  public static String getRandomTip() {
    return tips.get(new Random().nextInt(tips.size()));
  }
  
  /**
   * @return the ArrayList with all available tips
   */
  public static ArrayList<String> getAllTips(){
    return tips;
  }
  

  /**
   * Opens a Dialog with tips, a Tip can have special Dialogs.
   */
  public static void openTipDialog() {
    String randomTip = getRandomTip();
    int tipIndex = tips.indexOf(randomTip);
    int msToClose = -1;
    String closeName = "CLOSE";
    String nextName = "NEXT TIP";
    Runnable[] run = {() -> openTipDialog()};
    
    
    switch(tipIndex) {
      case 0:   //take me to MapEditor
        Dialogs.openDialogThreeButtons("Tip #"+tipIndex, randomTip, msToClose, "TEST IT!", () -> SceneHandler.switchToMapEditorScene(), nextName, closeName, () -> openTipDialog());
        return;
      case 1: 
        Dialogs.openDialogThreeButtons("Tip #"+tipIndex, randomTip, msToClose, "TEST IT!", () -> SceneHandler.switchToMapEditorScene(), nextName, closeName, () -> openTipDialog());
        return;
      case 2:   //normal Alert vs my Alert
        nextName = "SURE!";
        run = 
          new Runnable[] {
              () -> Dialogs.openDialogTwoButtons(
                  "Thats the normal Alert",
                  "Looks kinda boring, doesn't it?",
                  -1, "CLOSE", "NEXT TIP", () -> openTipDialog()),
              () -> 
              {
                Alert alert = new Alert(AlertType.ERROR); 
                alert.setTitle("I am a boring Alet"); 
                alert.setContentText("My color is boring and my Buttons suck"); 
                alert.show();
              }
          };
        break;
      case 3: 
        msToClose = 7500;
        break;
      case 5:
        ArrayList<Integer> match = new ArrayList<Integer>();
        match.add(NativeKeyEvent.VC_I);match.add(NativeKeyEvent.VC_N);match.add(NativeKeyEvent.VC_F);match.add(NativeKeyEvent.VC_O);
        Dialogs.openDialogThreeButtons("Tip #"+tipIndex, randomTip, msToClose, "SHOW ME", () -> CheatboardListener.findAndOpenMatch(match), nextName, closeName, () -> openTipDialog());
        return;
    }
    
    Dialogs.openDialogTwoButtons("Tip #"+tipIndex, randomTip, msToClose, closeName, nextName, run);
  }
}
