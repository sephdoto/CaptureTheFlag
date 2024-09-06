package org.ctf.ui.controllers;
import java.util.ArrayList;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums;
import org.ctf.shared.constants.Enums.SoundType;
import org.ctf.ui.creators.settings.SettingsSetter;
import org.ctf.ui.data.FixedStack;
import org.ctf.ui.data.SceneHandler;
import org.ctf.ui.highscore.LeaderBoardController;
import org.ctf.ui.highscore.Score;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyAdapter;
import org.jnativehook.keyboard.NativeKeyEvent;
import dialogs.Dialogs;
import dialogs.Dialogs.Dialog;
import dialogs.Tips;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * A KeyLogger to trigger easter eggs.
 * We are not allowed to actually send the CheatCodes to the server so the codes are just
 * visual/auditory easter eggs.
 * 
 * @author sistumpf
 */
public class CheatboardListener extends NativeKeyAdapter {
  int pivot;
  static ArrayList<Integer> currentCode;
  static ArrayList<ArrayList<Integer>> cheatCodes;
  static ArrayList<ArrayList<Integer>> pivotList;
  static ArrayList<String> infos;
  
  private static FixedStack<Scene> lastScenesCopy;
  private static long backtracking = -1;
  
  
  
  static {
    infos = new ArrayList<String>();
    initCheatCodes();
    pivotList = new ArrayList<ArrayList<Integer>>();
    currentCode = new ArrayList<Integer>();
  }
  
  /**
   * Registers this Listener as a native hook
   */
  public void registerNativeHook() {
    if(!GlobalScreen.isNativeHookRegistered())  {
      try {
        GlobalScreen.registerNativeHook();
        GlobalScreen.addNativeKeyListener(CheatboardListener.this);
      } catch (NativeHookException ex) {
        System.err.println("There was a problem registering the native hook.");
        ex.printStackTrace();
      }
    }
  }
  
  /**
   * Unregisters this Listener as a native hook
   */
  public void unregisterNativeHook() {
    if(GlobalScreen.isNativeHookRegistered()) {
      try {
        GlobalScreen.unregisterNativeHook();
        GlobalScreen.removeNativeKeyListener(CheatboardListener.this);
      } catch (NativeHookException ex) {
        System.err.println("There was a problem unregistering the native hook.");
        ex.printStackTrace();
      }
    }
  }

  /**
   * creates the cheat codes and puts them into the list.
   */
  private static void initCheatCodes() {    
    cheatCodes = new ArrayList<ArrayList<Integer>>();
    ArrayList<Integer> rick = new ArrayList<Integer>();
    rick.add(NativeKeyEvent.VC_D);
    rick.add(NativeKeyEvent.VC_Q);
    rick.add(NativeKeyEvent.VC_W);
    rick.add(NativeKeyEvent.VC_4);
    cheatCodes.add(rick);
    infos.add("never gonna give you hints");

    ArrayList<Integer> debug = new ArrayList<Integer>();
    debug.add(NativeKeyEvent.VC_D);
    debug.add(NativeKeyEvent.VC_E);
    debug.add(NativeKeyEvent.VC_B);
    debug.add(NativeKeyEvent.VC_U);
    debug.add(NativeKeyEvent.VC_G);
    cheatCodes.add(debug);
    infos.add("changes depending on what I'm testing");
    
    ArrayList<Integer> skip = new ArrayList<Integer>();
    skip.add(NativeKeyEvent.VC_S);
    skip.add(NativeKeyEvent.VC_K);
    skip.add(NativeKeyEvent.VC_I);
    skip.add(NativeKeyEvent.VC_P);
    cheatCodes.add(skip);
    infos.add("skips to the next song");

    ArrayList<Integer> mute = new ArrayList<Integer>();
    mute.add(NativeKeyEvent.VC_M);
    mute.add(NativeKeyEvent.VC_U);
    mute.add(NativeKeyEvent.VC_T);
    mute.add(NativeKeyEvent.VC_E);
    cheatCodes.add(mute);    
    infos.add("sets sound and music to 0% without saving");

    ArrayList<Integer> half = new ArrayList<Integer>();
    half.add(NativeKeyEvent.VC_H);
    half.add(NativeKeyEvent.VC_A);
    half.add(NativeKeyEvent.VC_L);
    half.add(NativeKeyEvent.VC_F);
    cheatCodes.add(half);
    infos.add("sets sound and music to 50% without saving");

    ArrayList<Integer> full = new ArrayList<Integer>();
    full.add(NativeKeyEvent.VC_F);
    full.add(NativeKeyEvent.VC_U);
    full.add(NativeKeyEvent.VC_L);
    full.add(NativeKeyEvent.VC_L);
    cheatCodes.add(full);
    infos.add("sets sound and music to 100% without saving");

    ArrayList<Integer> theme = new ArrayList<Integer>();
    theme.add(NativeKeyEvent.VC_T);
    theme.add(NativeKeyEvent.VC_H);
    theme.add(NativeKeyEvent.VC_E);
    theme.add(NativeKeyEvent.VC_M);
    theme.add(NativeKeyEvent.VC_E);
    cheatCodes.add(theme);
    infos.add("cycles to the next theme");

    ArrayList<Integer> bgc = new ArrayList<Integer>();    
    bgc.add(NativeKeyEvent.VC_B);
    bgc.add(NativeKeyEvent.VC_G);
    bgc.add(NativeKeyEvent.VC_C);
    cheatCodes.add(bgc);
    infos.add("changes the background image");

    ArrayList<Integer> analyze = new ArrayList<Integer>();    
    analyze.add(NativeKeyEvent.VC_A);
    analyze.add(NativeKeyEvent.VC_N);
    analyze.add(NativeKeyEvent.VC_A);
    analyze.add(NativeKeyEvent.VC_L);
    analyze.add(NativeKeyEvent.VC_Y);
    cheatCodes.add(analyze);
    infos.add("opens the analyzer");

    ArrayList<Integer> home = new ArrayList<Integer>();    
    home.add(NativeKeyEvent.VC_H);
    home.add(NativeKeyEvent.VC_O);
    home.add(NativeKeyEvent.VC_M);
    home.add(NativeKeyEvent.VC_E);
    cheatCodes.add(home);
    infos.add("switches to home screen");

    ArrayList<Integer> map = new ArrayList<Integer>();    
    map.add(NativeKeyEvent.VC_M);
    map.add(NativeKeyEvent.VC_A);
    map.add(NativeKeyEvent.VC_P);
    cheatCodes.add(map);
    infos.add("switches to map editor");

    ArrayList<Integer> create = new ArrayList<Integer>();    
    create.add(NativeKeyEvent.VC_C);
    create.add(NativeKeyEvent.VC_R);
    create.add(NativeKeyEvent.VC_E);
    create.add(NativeKeyEvent.VC_A);
    create.add(NativeKeyEvent.VC_T);
    create.add(NativeKeyEvent.VC_E);
    cheatCodes.add(create);
    infos.add("switches to create game");
    
    ArrayList<Integer> join = new ArrayList<Integer>();    
    join.add(NativeKeyEvent.VC_J);   
    join.add(NativeKeyEvent.VC_O);   
    join.add(NativeKeyEvent.VC_I);   
    join.add(NativeKeyEvent.VC_N);
    cheatCodes.add(join);
    infos.add("switches to join game");
    
    ArrayList<Integer> back = new ArrayList<Integer>();    
    back.add(NativeKeyEvent.VC_B);   
    back.add(NativeKeyEvent.VC_A);   
    back.add(NativeKeyEvent.VC_C);   
    back.add(NativeKeyEvent.VC_K);
    cheatCodes.add(back);
    infos.add("switchtes between the last (" + Constants.lastScenesSize + ") scenes");
    
    ArrayList<Integer> cs = new ArrayList<Integer>();    
    cs.add(NativeKeyEvent.VC_C);   
    cs.add(NativeKeyEvent.VC_S);   
    cheatCodes.add(cs);    
    infos.add("closes settings without saving to json");

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
    infos.add("opens normal settings");
    
    ArrayList<Integer> advanced = new ArrayList<Integer>();    
    advanced.add(NativeKeyEvent.VC_A);   
    advanced.add(NativeKeyEvent.VC_D);   
    advanced.add(NativeKeyEvent.VC_V);   
    advanced.add(NativeKeyEvent.VC_A);   
    advanced.add(NativeKeyEvent.VC_N);   
    advanced.add(NativeKeyEvent.VC_C);   
    advanced.add(NativeKeyEvent.VC_E);   
    advanced.add(NativeKeyEvent.VC_D);   
    cheatCodes.add(advanced);
    infos.add("opens advanced settings");
    
    ArrayList<Integer> bdvanced = new ArrayList<Integer>();    
    bdvanced.add(NativeKeyEvent.VC_B);   
    bdvanced.add(NativeKeyEvent.VC_D);   
    bdvanced.add(NativeKeyEvent.VC_V);   
    bdvanced.add(NativeKeyEvent.VC_A);   
    bdvanced.add(NativeKeyEvent.VC_N);   
    bdvanced.add(NativeKeyEvent.VC_C);   
    bdvanced.add(NativeKeyEvent.VC_E);   
    bdvanced.add(NativeKeyEvent.VC_D);   
    cheatCodes.add(bdvanced);
    infos.add("opens second advanced settings");
    
    ArrayList<Integer> info = new ArrayList<Integer>();
    info.add(NativeKeyEvent.VC_I);
    info.add(NativeKeyEvent.VC_N);
    info.add(NativeKeyEvent.VC_F);
    info.add(NativeKeyEvent.VC_O);
    cheatCodes.add(info);
    infos.add("shows all available codes");

    ArrayList<Integer> tip = new ArrayList<Integer>();
    tip.add(NativeKeyEvent.VC_T);
    tip.add(NativeKeyEvent.VC_I);
    tip.add(NativeKeyEvent.VC_P);
    cheatCodes.add(tip);
    infos.add("opens a random tip/fact/I was bored");
    
    ArrayList<Integer> scores = new ArrayList<Integer>();
    scores.add(NativeKeyEvent.VC_P);
    scores.add(NativeKeyEvent.VC_E);
    scores.add(NativeKeyEvent.VC_P);
    scores.add(NativeKeyEvent.VC_E);
    scores.add(NativeKeyEvent.VC_S);
    cheatCodes.add(scores);
    infos.add("shows the top players in leaderboard");
  }

  /**
   * Adds the typed key code to the current code,
   * then checks if the current code matches any saved cheat codes.
   */
  @Override
  public void nativeKeyPressed(NativeKeyEvent e) {
    if(NativeKeyEvent.getKeyText(e.getKeyCode()).length() == 1) {
      currentCode.add(e.getKeyCode());
      checkTheCode(e);
    }

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
  private void checkTheCode(NativeKeyEvent e) {
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
            pivotList.clear();
            pivot = 0;
            currentCode.clear();
            letTheFunBegin(pList);
          }
        } else {
          pivotList.remove(pList);
          --i;
        }
      }
      ++pivot;
    }
    if(!oneListMatched) {
      boolean firstTime = pivot > 0;

      pivot = 0;
      pivotList.clear();
      currentCode.clear();
      if(firstTime)
        nativeKeyPressed(e);
    }
  }

  /**
   * @return number of currently available codes
   */
  public static int howManyCodes() {
    return cheatCodes.size();
  }
  
  /**
   * Compares all cheatCodes to list, if one matches, its event gets triggered
   * 
   * @param list ArrayList containing NativeKeyEvent keyCodes
   */
  public static void findAndOpenMatch(ArrayList<Integer> list) {
    for(ArrayList<Integer> cheatCodeList : cheatCodes) {
      boolean matches = true;
      for(int i=0; i<list.size() && i<cheatCodeList.size(); i++)
        if(list.get(i) != cheatCodeList.get(i)) {
          matches = false;
          break;
        }
      if(matches) {
        letTheFunBegin(cheatCodeList);
        return;
      }
    }
  }
  
  /**
   * Depending on the cheat code, different things can happen.
   * Whatever happens is decided here.
   * 
   * @param match the ArrayList containing the cheatcode to check for references.
   */
  public static void letTheFunBegin(ArrayList<Integer> match) {
    if(match == cheatCodes.get(0)) {            // first list is, of course, rickroll
      MusicPlayer.shortFade((int)SoundController.getMs("rick", SoundType.MISC), 10, 0.1);
      SoundController.playSound("rick", SoundType.MISC);
    } else if (match == cheatCodes.get(1)) {    // whatever needs to be debugged
      //      ((PlayGameScreen)SceneHandler.getCurrentScene()).stopTimers();
      Dialogs.openDialog(
          "Lorem ipsum dolor sit amet", 
          "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Never gonna give you up, never gonna let you down. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.",
          -1, -1,
          () -> {
            for(int i=0; i<60; i++) {
              Score score = new Score("TestEntry", (long)(Math.random() * 90000));
              LeaderBoardController.addEntry(score);
            }
          });
      
    } else if (match == cheatCodes.get(2)) {    // skip current song
      SettingsSetter.getCurrentPlayer().startShuffle();
    } else if (match == cheatCodes.get(3)) {    // mute music and sounds
      MusicPlayer.mp.setVolume(0);
      Constants.musicVolume = 0;
      Constants.soundVolume = 0;
    } else if (match == cheatCodes.get(4)) {    // set music and sounds to 50%
      MusicPlayer.mp.setVolume(0.5);
      Constants.musicVolume = 0.5;
      Constants.soundVolume = 0.5;
    } else if (match == cheatCodes.get(5)) {    // set music and sounds to 100%
      MusicPlayer.mp.setVolume(1);
      Constants.musicVolume = 1;
      Constants.soundVolume = 1;
    } else if (match == cheatCodes.get(6)) {    // switch the current theme
      Constants.theme = Enums.Themes.values()[(Constants.theme.ordinal() +1) % Enums.Themes.values().length];
      SceneHandler.changeBackgroundImage();
      SceneHandler.updateBackground();
      SettingsSetter.saveCustomSettings();
    } else if (match == cheatCodes.get(7)) {    // switch home screen background
      SceneHandler.changeBackgroundImage();
      SceneHandler.updateBackground();
    } else if (match == cheatCodes.get(8)) {    // open analyzer
      Platform.runLater(
          new Runnable() {
            public void run(){
              SceneHandler.switchToAnalyzerScene();
            }
          }
          );
    } else if(match == cheatCodes.get(9)) {    // switch to home screen
      Platform.runLater(
          new Runnable() {
            public void run(){
              SceneHandler.switchToHomeScreen();
            }
          }
          );
    } else if(match == cheatCodes.get(10)) {    // switch to map editor
      Platform.runLater(
          new Runnable() {
            public void run(){
              SceneHandler.switchToMapEditorScene();
            }
          }
          );
    } else if(match == cheatCodes.get(11)) {    // switch to create game scene
      Platform.runLater(
          new Runnable() {
            public void run(){
              SceneHandler.switchToCreateGameScene();
            }
          }
          );
    } else if(match == cheatCodes.get(12)) {    // switch to join game scene
      Platform.runLater(
          new Runnable() {
            public void run(){
              SceneHandler.switchToJoinScene();
            }
          }
          );
    } else if(match == cheatCodes.get(13)) {    // switch to the last scene(s)
      Platform.runLater(
          new Runnable() {
            public void run(){
              if((backtracking == -1 || 
                  System.currentTimeMillis() - backtracking >= (Constants.backSeconds * 1000)) &&
                  SceneHandler.getLastScenes().size() != 0) {
                //make a copy of lastScenes and reset it in SceneHandler
                lastScenesCopy = SceneHandler.getLastScenes();
                SceneHandler.setLastScenes(new FixedStack<Scene>(Constants.lastScenesSize));
                SceneHandler.switchCurrentScene(lastScenesCopy.pop());
                backtracking = System.currentTimeMillis();
              } else {
                if(lastScenesCopy != null && lastScenesCopy.size() > 0) {
                  SceneHandler.switchCurrentScene(lastScenesCopy.pop());
                  backtracking = System.currentTimeMillis();  
                } else {
                  backtracking = -1;
                }
              }
            }
          }
          );
    } else if(match == cheatCodes.get(14)) {    // closes the currently opened settings (close settings)
      SceneHandler.closeSettings();
    } else if (match == cheatCodes.get(15)) {    // open settings
      Platform.runLater(() -> {
        SceneHandler.openSettingsWindow("default");
      });
    } else if(match == cheatCodes.get(16)) {    // open advanced settings
      Platform.runLater(() -> {
        SceneHandler.openSettingsWindow("advanced");
      });
    } else if(match == cheatCodes.get(17)) {    // open bdvanced (advances 2) settings
      Platform.runLater(() -> {
        SceneHandler.openSettingsWindow("bdvanced");
      });
    } else if(match == cheatCodes.get(18)) {    // opens a dialog showing all available codes
      String[] codes = new String[cheatCodes.size()];
      String allCodes = "";
      for(int i=0; i<cheatCodes.size(); i++) {
        StringBuilder code = new StringBuilder();
          for(Integer c : cheatCodes.get(i))
          code.append(NativeKeyEvent.getKeyText(c));
        codes[i] = code.toString();
      }
      for(int code=0; code<cheatCodes.size(); code++) {
        StringBuilder insets = new StringBuilder().append(" ");
        for(int i= (cheatCodes.size()-1 - code)/10; i>0; i--)
          insets.append("  ");
        allCodes += "(" + code + ")" + insets.toString() + codes[code] + " : " + infos.get(code) + "\n";
      }
      Dialogs.openDialog("All available \"CheatCodes\"", 
          allCodes,
          -1, -1);
    } else if (match == cheatCodes.get(19)){    // opens a random tip
      Tips.openTipDialog();
    } else if (match == cheatCodes.get(20)) {
      openScoreDialog(10);
    }
  }
  
  private static void openScoreDialog(int top) {
    int size = 10;
    Platform.runLater(() -> {
      Dialog dialog;
      if(!LeaderBoardController.getEntryString(top, top+size).isEmpty()) {
        dialog = Dialogs.getDialogTwoButtons("LeaderBoard " + (top-size) + " to " + top, LeaderBoardController.getEntryString(top-size, top), -1, 300, "OK", "NEXT 10", () -> openScoreDialog(top + size));
      } else {
        dialog = new Dialog("LeaderBoard " + (top-size) + " to " + top, LeaderBoardController.getEntryString(top-size, top), -1, 300);
      }
      ((Text)((StackPane)dialog.getDialogPane().getChildren().get(3)).getChildren().get(0)).setTextAlignment(TextAlignment.CENTER);
      dialog.show();
    });
  }
}
