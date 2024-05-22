package org.ctf.ui.controllers;
import java.util.ArrayList;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums;
import org.ctf.shared.constants.Enums.SoundType;
import org.ctf.ui.App;
import org.ctf.ui.creators.ComponentCreator;
import org.jnativehook.GlobalScreen;
import org.jnativehook.keyboard.NativeKeyEvent;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
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
  static Runnable settings;
  
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
    
    ArrayList<Integer> mute = new ArrayList<Integer>();
    mute.add(NativeKeyEvent.VC_M);
    mute.add(NativeKeyEvent.VC_U);
    mute.add(NativeKeyEvent.VC_T);
    mute.add(NativeKeyEvent.VC_E);
    cheatCodes.add(mute);
    
    ArrayList<Integer> half = new ArrayList<Integer>();
    half.add(NativeKeyEvent.VC_H);
    half.add(NativeKeyEvent.VC_A);
    half.add(NativeKeyEvent.VC_L);
    half.add(NativeKeyEvent.VC_F);
    cheatCodes.add(half);
    
    ArrayList<Integer> full = new ArrayList<Integer>();
    full.add(NativeKeyEvent.VC_F);
    full.add(NativeKeyEvent.VC_U);
    full.add(NativeKeyEvent.VC_L);
    full.add(NativeKeyEvent.VC_L);
    cheatCodes.add(full);
    
    ArrayList<Integer> theme = new ArrayList<Integer>();
    theme.add(NativeKeyEvent.VC_T);
    theme.add(NativeKeyEvent.VC_H);
    theme.add(NativeKeyEvent.VC_E);
    theme.add(NativeKeyEvent.VC_M);
    theme.add(NativeKeyEvent.VC_E);
    cheatCodes.add(theme);
    
    ArrayList<Integer> bgc = new ArrayList<Integer>();    
    bgc.add(NativeKeyEvent.VC_B);
    bgc.add(NativeKeyEvent.VC_G);
    bgc.add(NativeKeyEvent.VC_C);
    cheatCodes.add(bgc);

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
      MusicPlayer.shortFade((int)SoundController.getMs("rick", SoundType.MISC), 10, 0.1);
      SoundController.playSound("rick", SoundType.MISC);
    } else if (match == cheatCodes.get(1)) {    // skip current song
      SettingsSetter.getCurrentPlayer().startShuffle();
    } else if (match == cheatCodes.get(2)) {
      System.out.println("reimport resources");
    } else if (match == cheatCodes.get(3)) {    // open settings
      if(settings != null)
        Platform.runLater(() -> {
          SoundController.playSound("Button", SoundType.MISC);
          settings.run();
        });
    } else if (match == cheatCodes.get(4)) {    // mute music and sounds
      MusicPlayer.mp.setVolume(0);
      Constants.musicVolume = 0;
      Constants.soundVolume = 0;
    } else if (match == cheatCodes.get(5)) {    // set music and sounds to 50%
      MusicPlayer.mp.setVolume(0.5);
      Constants.musicVolume = 0.5;
      Constants.soundVolume = 0.5;
    } else if (match == cheatCodes.get(6)) {    // set music and sounds to 100%
      MusicPlayer.mp.setVolume(1);
      Constants.musicVolume = 1;
      Constants.soundVolume = 1;
    } else if (match == cheatCodes.get(7)) {    // switch the current theme
      System.out.println(Constants.theme);
      Constants.theme = Enums.Themes.values()[(Constants.theme.ordinal() +1) % Enums.Themes.values().length];
      SettingsSetter.saveCustomSettings();
      App.chagngeHomescreenBackground();
    } else if (match == cheatCodes.get(8)) {
      App.chagngeHomescreenBackground();
    }
  }
  
  /**
   * Set settings so Settings Screen can be opened via typing settings
   * 
   * @param root StackPane
   * @param startScene Scene
   */
  public static void setSettings(StackPane root, Scene scene) {
    CheatboardListener.settings = () -> {
      SoundController.playSound("Button", SoundType.MISC);
      root.getChildren().add(new ComponentCreator(scene).createSettingsWindow(root));
    };
  }
}
