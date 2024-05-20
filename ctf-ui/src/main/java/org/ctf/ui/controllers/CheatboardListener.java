package org.ctf.ui.controllers;
import java.util.ArrayList;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.SoundType;
import org.ctf.shared.constants.Enums.Themes;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import javafx.scene.media.AudioClip;
import org.jnativehook.keyboard.NativeKeyAdapter;
import org.jnativehook.NativeHookException;

/**
 * A KeyLogger to trigger easter eggs.
 * We are not allowed to actually send the CheatCodes to the server so the codes are just
 * visual/auditory easter eggs.
 * 
 * @author sistumpf
 */
public class CheatboardListener extends NativeKeyAdapter {
  ArrayList<Integer> currentCode;
  ArrayList<ArrayList<Integer>> cheatCodes;
  int pivot;
  ArrayList<ArrayList<Integer>> pivotList;

  /**
   * Initializes the cheat codes, registers the key logger.
   */
  public CheatboardListener() {  
    initCheatCodes();
    this.pivotList = new ArrayList<ArrayList<Integer>>();
    this.currentCode = new ArrayList<Integer>();

    try {
      GlobalScreen.registerNativeHook();
    } catch (NativeHookException ex) {
      System.err.println("There was a problem registering the native hook.");
      ex.printStackTrace();
    }

    GlobalScreen.addNativeKeyListener(this);
  }

  /**
   * creates the cheat codes and puts them into the list.
   */
  private void initCheatCodes() {
    this.cheatCodes = new ArrayList<ArrayList<Integer>>();
    ArrayList<Integer> rick = new ArrayList<Integer>();
    rick.add(NativeKeyEvent.VC_D);
    rick.add(NativeKeyEvent.VC_Q);
    rick.add(NativeKeyEvent.VC_W);
    rick.add(NativeKeyEvent.VC_4);
    cheatCodes.add(rick);

    ArrayList<Integer> skip = new ArrayList<Integer>();
    skip.add(NativeKeyEvent.VC_S);
    skip.add(NativeKeyEvent.VC_K);
    skip.add(NativeKeyEvent.VC_I);
    skip.add(NativeKeyEvent.VC_P);
    cheatCodes.add(skip);

    ArrayList<Integer> reimportResources = new ArrayList<Integer>();
    reimportResources.add(NativeKeyEvent.VC_R);
    reimportResources.add(NativeKeyEvent.VC_E);
    reimportResources.add(NativeKeyEvent.VC_I);
    reimportResources.add(NativeKeyEvent.VC_M);
    reimportResources.add(NativeKeyEvent.VC_P);
    reimportResources.add(NativeKeyEvent.VC_O);
    reimportResources.add(NativeKeyEvent.VC_R);
    reimportResources.add(NativeKeyEvent.VC_T);
    reimportResources.add(NativeKeyEvent.VC_R);
    reimportResources.add(NativeKeyEvent.VC_E);
    reimportResources.add(NativeKeyEvent.VC_S);
    reimportResources.add(NativeKeyEvent.VC_O);
    reimportResources.add(NativeKeyEvent.VC_U);
    reimportResources.add(NativeKeyEvent.VC_R);
    reimportResources.add(NativeKeyEvent.VC_C);
    reimportResources.add(NativeKeyEvent.VC_E);
    reimportResources.add(NativeKeyEvent.VC_S);
    cheatCodes.add(reimportResources);

    ArrayList<Integer> settings = new ArrayList<Integer>();
    settings.add(NativeKeyEvent.VC_S);
    settings.add(NativeKeyEvent.VC_E);
    settings.add(NativeKeyEvent.VC_T);
    settings.add(NativeKeyEvent.VC_T);
    settings.add(NativeKeyEvent.VC_I);
    settings.add(NativeKeyEvent.VC_N);
    settings.add(NativeKeyEvent.VC_G);
    settings.add(NativeKeyEvent.VC_S);
    cheatCodes.add(settings);
  }

  /**
   * Adds the typed key code to the current code,
   * then checks if the current code matches any saved cheat codes.
   */
  @Override
  public void nativeKeyPressed(NativeKeyEvent e) {
    currentCode.add(e.getKeyCode());
    checkTheCode();

    //    if (e.getKeyCode() == NativeKeyEvent.VC_RIGHT) {
    //      try {
    //        GlobalScreen.unregisterNativeHook();
    //      } catch (NativeHookException ex) {
    //        ex.printStackTrace();
    //      }
    //    }
  }

  /**
   * Checks if the currently types cheat code matches any saved cheat codes.
   * If one codes start key matches, only that code will be checked till it is completed or canceled.
   */
  private void checkTheCode() {
    boolean oneListMatched = false;

    if(pivotList.size() == 0) {
      for(ArrayList<Integer> list : cheatCodes) {
        if(list.get(0) == currentCode.get(0)) {
          pivotList.add(list);
          oneListMatched = true;
        }
      }
      if (oneListMatched) {
        pivot = 1;
      }
    } else {
      for(int i=0; i<pivotList.size(); i++) {
        ArrayList<Integer> pList = pivotList.get(i);
        if(pList.get(pivot) == currentCode.get(pivot)) {
          oneListMatched = true;
          if(pivot +1 == pList.size()) {
            letTheFunBegin(pList);
            pivotList.clear();
            pivot = 0;
            currentCode.clear();
          }
        } else {
          pivotList.remove(pList);
          --i;
        }
      }
      ++pivot;
    }
    if(!oneListMatched) {
      pivot = 0;
      pivotList.clear();
      this.currentCode.clear();
    }
  }

  /**
   * Depending on the cheat code, different things can happen.
   * Whatever happens is decided here.
   * 
   * @param match the ArrayList containing the cheatcode to check for references.
   */
  private void letTheFunBegin(ArrayList<Integer> match) {
    if(match == cheatCodes.get(0)) {    //first list is, of course, rickroll
      System.out.println("gotcha");
      MusicPlayer.shortFade((int)SoundController.getMs("rick", SoundType.MISC), 10, 0.1);
      SoundController.playSound("rick", SoundType.MISC);
    } else if (match == cheatCodes.get(1)) {
      System.out.println("skip");
      SettingsSetter.getCurrentPlayer().startShuffle();
    } else if (match == cheatCodes.get(2)) {
      System.out.println("reimport resources");
    } else if (match == cheatCodes.get(3)) {
      System.out.println("Settings");
    }
  }
}
