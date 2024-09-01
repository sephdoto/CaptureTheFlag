package org.ctf.ui.data;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import org.ctf.shared.client.lib.ServerManager;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.ImageType;
import org.ctf.shared.constants.Enums.SoundType;
import org.ctf.ui.App;
import org.ctf.ui.controllers.ImageController;
import org.ctf.ui.controllers.SoundController;
import org.ctf.ui.creators.settings.SettingsOpener;
import org.ctf.ui.creators.settings.SettingsWindow;
import org.ctf.ui.editor.EditorScene;
import org.ctf.ui.gameAnalyzer.AiAnalyzerScene;
import org.ctf.ui.hostGame.CreateGameController;
import org.ctf.ui.hostGame.CreateGameScreenV2;
import org.ctf.ui.hostGame.PlayGameScreenV2;
import org.ctf.ui.hostGame.WaitingScene;
import org.ctf.ui.remoteGame.JoinScene;
import org.ctf.ui.remoteGame.RemoteWaitingScene;
import org.ctf.ui.remoteGame.WaveCollapseThread;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * A centralized Scene and Stage handling class.
 * It contains the one and only main Stage, and all other important Scenes.
 * Scene Backgrounds and css Stylesheets get applied here.
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
   * Normal state is null
   */
  private static SettingsWindow settingsWindow;
  
  ///***************************************///
  /*/               static                  /*/ 
  ///***************************************///
  
  static {
    lastScenes = new FixedStack<Scene>(Constants.lastScenesSize);
  }
  
  ///***************************************///
  /*/             open Popups               /*/ 
  ///***************************************///
  
  /**
   * Opens a settings window, depending on its name.
   *
   * @param settings "default" or "advanced" to open different windows
   */
  public static void openSettingsWindow(String settings) {
    if(settingsWindow != null) {
      closeSettings();
    }

    SoundController.playSound("Button", SoundType.MISC);
    switch (settings) {
      case "advanced" : 
        settingsWindow = SettingsOpener.getAdvancedSettings();
        break;
      default: 
        settingsWindow = SettingsOpener.getDefaultSettings();
    }
    ((StackPane)SceneHandler.getCurrentScene().getRoot()).getChildren().add(settingsWindow.getContent());
  }

  
  ///***************************************///
  /*/          switching Scenes             /*/ 
  ///***************************************///
  
  public static void switchToHomeScreen() {
    App.adjustHomescreen(mainStage.getScene().getWidth(), mainStage.getScene().getHeight());
    switchCurrentScene(homeScene);
    
//    addStyleSheet("MapEditor");
//    addStyleSheet("ComboBox");
    addStyleSheet("home");
  }
  
  public static void switchToWaitGameScene() {
    CreateGameController.initColorHashMap();
    switchCurrentScene(
        new WaitingScene(SceneHandler.getMainStage().getWidth() - App.offsetWidth, SceneHandler.getMainStage().getHeight() - App.offsetHeight)
        );
    addStyleSheet("waitingRoom");    
    addStyleSheet("color");
  }
  
  public static void switchToRemoteWaitGameScene(ServerManager serverManager) {
    switchCurrentScene(
        new RemoteWaitingScene(SceneHandler.getMainStage().getWidth() - App.offsetWidth, SceneHandler.getMainStage().getHeight() - App.offsetHeight, serverManager)
    );
    
    addStyleSheet("MapEditor");
    addStyleSheet("ComboBox");
  }
  
  public static void switchToCreateGameScene() {
    SceneHandler.switchCurrentScene(
        new CreateGameScreenV2(
            SceneHandler.getMainStage().getWidth() - App.offsetWidth, SceneHandler.getMainStage().getHeight() - App.offsetHeight)
        );
    addStyleSheet("createGame");
  }

  public static void switchToJoinScene() {
    switchCurrentScene(new JoinScene(
            SceneHandler.getMainStage().getWidth() - App.offsetWidth, SceneHandler.getMainStage().getHeight() - App.offsetHeight)
        );
    addStyleSheet("joinGame");
  }

  public static void switchToAnalyzerScene() {
    AiAnalyzerScene scene = 
        new AiAnalyzerScene(SceneHandler.getMainStage().getWidth() - App.offsetWidth, SceneHandler.getMainStage().getHeight() - App.offsetHeight);
    if(scene.switched) {
      switchCurrentScene(scene);
      addStyleSheet("color");
      addStyleSheet("MapEditor");
    }
  }
  
  public static void switchToMapEditorScene() {
    switchCurrentScene(
        new EditorScene(
            SceneHandler.getMainStage().getWidth() - App.offsetWidth, SceneHandler.getMainStage().getHeight() - App.offsetHeight)
        );    
    
    addStyleSheet("MapEditor");
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
    
    addStyleSheet("MapEditor");
    addStyleSheet("color");
    addStyleSheet("ComboBox");
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
    if(currentScene != null)
      lastScenes.push(currentScene);
    currentScene = scene;
    updateBackgroundVisibility();
    updateBackground();
    
    scene.setOnMousePressed(
        event -> {
          scene.getRoot().requestFocus();
        });
    
    addStyleSheet("settings");
    addStyleSheet("popUps"); 
    
    mainStage.setScene(scene);
  }
  
  /**
   * Applies a Stylesheet to the current Scene.
   * 
   * @param name the Stylesheets filename without ".css"
   */
  private static void addStyleSheet(String name) {
    try {
      currentScene.getStylesheets()
          .add(Paths.get(Constants.toUIStyles + name + ".css").toUri().toURL().toString());
    } catch (MalformedURLException e) {
      System.err.println("Error applying Stylesheet " + name);
    }
  }
  
  /**
   * Changes the background image to a completely new one.
   * Effects and other corrections could be applied here.
   * 
   * @author sistumpf
   */
  public static void changeBackgroundImage() {
      Image bImage = ImageController.loadRandomThemedImage(ImageType.HOME);
      /*int x=10;
      int y=10;
      ImageView ima = new ImageView(bImage);
      ima.setFitWidth(bImage.getWidth());
    ima.setFitHeight(bImage.getHeight());
    var blur = new ColorAdjust();
    blur.setBrightness(-0.7);
    ima.setEffect(blur);
    var light = new Lighting();
    light.setLight(new Light.Spot(45, 45, 45, 1, Color.WHITE));
    ima.setEffect(light);
      WritableImage writableImage = new WritableImage((int)bImage.getWidth(), (int)bImage.getHeight());
      SnapshotParameters params = new SnapshotParameters();
      ima.snapshot(params, writableImage);
*/
      BackgroundSize backgroundSize = new BackgroundSize(1, 1, true, true, true, true);
      BackgroundImage background =
          new BackgroundImage(
              /*writableImage*/ bImage,
              BackgroundRepeat.NO_REPEAT,
              BackgroundRepeat.NO_REPEAT,
              BackgroundPosition.CENTER,
              backgroundSize);
      Constants.background = new Background(background);
  }
  
  /**
   * Updates the solid color layer above the background
   * 
   * @author sistumpf
   */
  public static void updateBackgroundVisibility() {
    Constants.colorOverlay.setStyle("-fx-background-color: rgba(" 
        + (int) Math.round(255 * Constants.defaultBGcolor.getRed()) +", " 
        + (int) Math.round(255 * Constants.defaultBGcolor.getGreen()) + ", "
        + (int) Math.round(255 * Constants.defaultBGcolor.getBlue()) +", "
        + Constants.showBackgrounds
        +");");
    ((StackPane) currentScene.getRoot()).getChildren().remove(Constants.colorOverlay);
    if(currentScene != homeScene)
      ((StackPane) currentScene.getRoot()).getChildren().add(Constants.colorOverlay);
    Constants.colorOverlay.toBack();
  }
  
  ///***************************************///
  /*/        getters and setters            /*/
  ///***************************************///
  
  /**
   * Updates the current Scenes Background to represent the Background Object from Constants.
   * Backgrounds for certain scenes can be hardcoded here.
   * 
   * @author sistumpf
   */
  public static void updateBackground() {
    if(currentScene instanceof WaitingScene)
      ((StackPane)currentScene.getRoot()).setBackground(ImageController.loadThemesBackgroundImage(ImageType.MISC, "waitingRoom2"));
    else {
      ((StackPane)currentScene.getRoot()).setBackground(Constants.background);
      ((StackPane)homeScene.getRoot()).setBackground(Constants.background);
    }
  }
  
  /**
   * Closes the currently open settingsWindow without saving it to the JSON.
   * Does not load the old saved values, all changes to Constants stay.
   * 
   * @author sistumpf
   */
  public static void closeSettings() {
    if(settingsWindow != null) {
      SettingsWindow toClose = settingsWindow;
      settingsWindow = null;
      
      Platform.runLater(() -> {
        ((StackPane)SceneHandler.getCurrentScene().getRoot()).getChildren().remove(toClose.getContent());
      });
    }
  }
  
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
}
