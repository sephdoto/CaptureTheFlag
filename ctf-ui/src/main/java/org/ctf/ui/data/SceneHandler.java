package org.ctf.ui.data;

import java.io.File;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.SoundType;
import org.ctf.ui.App;
import org.ctf.ui.controllers.SoundController;
import org.ctf.ui.creators.settings.SettingsWindow;
import org.ctf.ui.editor.EditorScene;
import org.ctf.ui.gameAnalyzer.AiAnalyserNew;
import org.ctf.ui.hostGame.CreateGameController;
import org.ctf.ui.hostGame.CreateGameScreenV2;
import org.ctf.ui.hostGame.PlayGameScreenV2;
import org.ctf.ui.hostGame.WaitingScene;
import org.ctf.ui.remoteGame.JoinScene;
import org.ctf.ui.remoteGame.WaveCollapseThread;
import org.ctf.ui.threads.ResizeFixThread;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * A centralized Scene and Stage handling class.
 * It contains the one and only main Stage, and all other important Scenes.
 * 
 * @author sistumpf
 */
public class SceneHandler {
  /**
   * The main Stage, all Scenes get displayed on
   */
  private static Stage mainStage;
  /**
   * The currently displayed Scene
   */
  private static Scene currentScene;
  /**
   * Saves the last Scenes to switch back to.
   * The maximum amount of scenes to save can be specified in static.
   */
  private static FixedStack<Scene> lastScenes;
  /**
   * The home menu Scene.
   * It gets saved as an attribute to put listeners on.
   */
  private static Scene homeScene;
  /**
   * Keeps track of the settings window so it cannot be opened multiple times.
   * false allows the settings to be opened.
   */
  private static boolean settingsOpen;
  
  ///***************************************///
  /*/               static                  /*/ 
  ///***************************************///
  
  static {
    lastScenes = new FixedStack<Scene>(Constants.lastScenesSize);
    settingsOpen = false;
  }
  
  ///***************************************///
  /*/             open Popups               /*/ 
  ///***************************************///
  
  public static void openSettingsWindow() {
    if(!settingsOpen) {
      SoundController.playSound("Button", SoundType.MISC);
      ((StackPane)SceneHandler.getCurrentScene().getRoot()).getChildren().add(new SettingsWindow().fillWithContent());
      settingsOpen = true;
    }
  }
  
  
  ///***************************************///
  /*/          switching Scenes             /*/ 
  ///***************************************///

  
  public static void switchToHomeScreen() {
    App.adjustHomescreen(mainStage.getScene().getWidth(), mainStage.getScene().getHeight());
    switchCurrentScene(homeScene);
  }
  
  public static void switchToWaitGameScene() {
    CreateGameController.initColorHashMap();
    switchCurrentScene(
        new WaitingScene(SceneHandler.getMainStage().getWidth() - App.offsetWidth, SceneHandler.getMainStage().getHeight() - App.offsetHeight)
        );
  }
  
  public static void switchToCreateGameScene() {
    SceneHandler.switchCurrentScene(
        new CreateGameScreenV2(
            SceneHandler.getMainStage().getWidth() - App.offsetWidth, SceneHandler.getMainStage().getHeight() - App.offsetHeight)
        );
  }

  public static void switchToJoinScene() {
    switchCurrentScene(new JoinScene(
            SceneHandler.getMainStage().getWidth() - App.offsetWidth, SceneHandler.getMainStage().getHeight() - App.offsetHeight)
        );
  }

  public static void switchToAnalyzerScene() {
    AiAnalyserNew scene = 
        new AiAnalyserNew(SceneHandler.getMainStage().getWidth() - App.offsetWidth, SceneHandler.getMainStage().getHeight() - App.offsetHeight);
    if(scene.switched) {
      switchCurrentScene(scene);
    }
  }
  
  public static void switchToMapEditorScene() {
    switchCurrentScene(
        new EditorScene(
            SceneHandler.getMainStage().getWidth() - App.offsetWidth, SceneHandler.getMainStage().getHeight() - App.offsetHeight)
        );
  }
  
  /**
   * Switches to the PlayGameScene, calls the WaveCollapseThread to generate and change the
   * background. Always calls the WaveCollapseThread, in case not our server is used to generate the
   * GameState.
   *
   * @author sistumpf
   * @param isRemote
   */
  public static void switchToPlayGameScene(boolean isRemote) {
    // delete last grid
    File grid = new File(Constants.toUIPictures + File.separator + "grid.png");
    grid.delete();

    // update main client if necessary
    for (int i = 0; i < ClientStorage.getMainClient().getTeams().length; i++) {
      if (ClientStorage.getMainClient().getTeams()[i] == null) {
        ClientStorage.getMainClient().pullData();
      }
    }

    PlayGameScreenV2 playOn =
        new PlayGameScreenV2(
            SceneHandler.getMainStage().getWidth() - App.offsetWidth,
            SceneHandler.getMainStage().getHeight() - App.offsetHeight,
            isRemote);
    switchCurrentScene(playOn);

    if (isRemote) {
      CreateGameController.initColorHashMapForRemote(ClientStorage.getMainClient());
    } else {
      CreateGameController.overWriteDefaultWithServerColors();
    }

    // generate new grid
    new WaveCollapseThread(playOn, ClientStorage.getMainClient().getGrid()).start();
  }
    
  ///***************************************///
  /*/          internal methods             /*/
  ///***************************************///
  
  /**
   * Switch to another Scene with this method and this method only.
   * 
   * @param scene the new Scene to switch to
   */
  public static void switchCurrentScene(Scene scene) {
    if(resizeFixThread != null) {
      resizeFixThread.interrupt();
      resizeFixThread = null;
    }
    if(currentScene != null)
      lastScenes.push(currentScene);
    currentScene = scene;
    mainStage.setScene(scene);

    resizeFixThread = new ResizeFixThread(mainStage);
    resizeFixThread.start();
  }
  
  private static ResizeFixThread resizeFixThread;

  
  ///***************************************///
  /*/        getters and setters            /*/
  ///***************************************///
  
  /**
   * Sets the main Stages title as a given String
   * 
   * @author sistumpf
   * @param string Text to set the Title as
   */
  public static void setTitle(String string) {
    Platform.runLater(
        () -> {
          getMainStage().setTitle(string);
        });
  }
  
  public static Stage getMainStage() {
    return mainStage;
  }
  public static void setMainStage(Stage mainStage) {
    SceneHandler.mainStage = mainStage;
  }
  public static Scene getCurrentScene() {
    return currentScene;
  }
  public static FixedStack<Scene> getLastScenes() {
    return lastScenes;
  }
  public static void setLastScenes(FixedStack<Scene> lastScenes) {
    SceneHandler.lastScenes = lastScenes;
  }
  public static Scene getHomeScene() {
    return homeScene;
  }
  public static void setHomeScene(Scene homeScene) {
    SceneHandler.homeScene = homeScene;
  }
  public static boolean areSettingsOpen() {
    return settingsOpen;
  }
  public static void setSettingsOpen(boolean settingsOpen) {
    SceneHandler.settingsOpen = settingsOpen;
  }
}
