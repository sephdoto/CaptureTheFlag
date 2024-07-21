package org.ctf.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.ctf.shared.client.lib.ServerChecker;
import org.ctf.shared.client.lib.ServerDetails;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.ImageType;
import org.ctf.shared.constants.Enums.SoundType;
import org.ctf.ui.controllers.CheatboardListener;
import org.ctf.ui.controllers.HomeSceneController;
import org.ctf.ui.controllers.ImageController;
import org.ctf.ui.controllers.MusicPlayer;
import org.ctf.ui.controllers.SettingsSetter;
import org.ctf.ui.controllers.SoundController;
import org.ctf.ui.creators.ComponentCreator;
import java.nio.file.Paths;
import org.ctf.ui.customobjects.*;
import org.ctf.ui.server.PortInUseException;
import org.ctf.ui.server.ServerContainer;

/**
 * @author mkrakows
 * @author rsyed (Bug fixer) startpoint for the GUI, server management features
 * @author aniemesc
 * @author sistumpf adding background music and sounds
 */
public class App extends Application {
  static Stage mainStage;
  static Scene startScene;
  static Scene lockscreen;
  static MusicPlayer backgroundMusic;
  HomeSceneController ssc;
  FadeTransition startTransition;
  ServerContainer serverContainer;
  Process process;
  static StackPane wrapper;
  static StackPane root;
  public static Image backgroundImage;
  public static BackgroundSize backgroundSize = new BackgroundSize(1, 1, true, true, true, true);
  static boolean serverStartSuccess;
  public static double offsetHeight;
  public static double offsetWidth;
  
  //public static ServerPane serverPane;
  

  public void start(Stage stage) {
    mainStage = stage;
    
    stage.focusedProperty().addListener((observable, oldValue, newValue) -> {
      if(newValue) {
        EntryPoint.cbl.registerNativeHook();
      } else {
        EntryPoint.cbl.unregisterNativeHook();
      }
    });
    
//    Parameters params = getParameters();
//    String port = params.getNamed().get("port");
    ssc = new HomeSceneController(mainStage);
    CheatboardListener.setHomeSceneController(ssc);
    SettingsSetter.loadCustomSettings();
    lockscreen = new Scene(createLockScreen(), 1100, 600);
    try {
      lockscreen.getStylesheets().add(Paths.get(Constants.toUIStyles + "MapEditor.css").toUri().toURL().toString());
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    lockscreen.setOnKeyPressed(
        e -> {
          this.changeToHomeScreen();
        });
    lockscreen.setOnMousePressed(
        e -> {
          this.changeToHomeScreen();
        });
    stage.setTitle("Capture The Flag Team 14");
    stage.setScene(lockscreen);
    serverContainer = new ServerContainer();
    try {
      serverContainer.startServer(Constants.localServerPort);
    } catch (PortInUseException e) {
      setTitle("Default Server couldnt start");
    }
    if (serverContainer.checkStatus()) {
      Platform.runLater(
          () -> {
            setTitle("CFP 14" + " Local Server is active @ " + Constants.localServerPort);
          });
    } else {
      setTitle("Internal server start error");
    }
    
    backgroundMusic = new MusicPlayer();
    stage.setOnCloseRequest(
        e -> {
          serverContainer.stopServer();
          this.process.destroy();
          System.exit(0);
        });
    SettingsSetter.giveMeTheAux(backgroundMusic);
    stage.show();
  }

  /**
   * creates a new stage and scene with a stackpane as root container. Sets a background image which
   * is bound to the size of the window and 3 custom buttons. {@link costumObjects.HomeScreenButton
   * }
   *
   * @author mkrakows
   * @author aniemesc
   * @return Parent
   */
  private Parent createParent() {
    root = new StackPane();
    root.setPrefSize(lockscreen.getWidth(), lockscreen.getHeight());
    Image ctf = ImageController.loadThemedImage(ImageType.MISC, "CaptureTheFlag");
    ImageView ctfv = new ImageView(ctf);
    ctfv.fitWidthProperty().bind(mainStage.widthProperty().multiply(0.8));
    ctfv.setPreserveRatio(true);
    StackPane.setAlignment(ctfv, Pos.TOP_CENTER);
    HomeScreenButton i1 =
        new HomeScreenButton(
            "CREATE MAP",
            mainStage,
            () -> {
    		SoundController.playSound("Button", SoundType.MISC);
             ssc.switchToMapEditorScene(mainStage);
            });
    HomeScreenButton i2 =
        new HomeScreenButton(
            "CREATE GAME",
            mainStage,
            () -> {
              // CreateGameScreen.initCreateGameScreen(mainStage);
    		SoundController.playSound("Button", SoundType.MISC);
              ssc.switchToCreateGameScene(mainStage);
            });
    HomeScreenButton i3 =
        new HomeScreenButton(
            "JOIN GAME",
            mainStage,
            () -> {
    			SoundController.playSound("Button", SoundType.MISC);
              ssc.switchToJoinScene(mainStage);
            });
    
    CheatboardListener.setSettings(root, startScene);
    
    HomeScreenButton i4 =
        new HomeScreenButton(
            "SETTINGS",
            mainStage,
            () -> {
              SoundController.playSound("Button", SoundType.MISC);
              root.getChildren().add(new ComponentCreator(startScene).createSettingsWindow(root));
            });
    VBox vbox = new VBox(ctfv, i1, i2, i3, i4);
    vbox.spacingProperty().bind(root.heightProperty().multiply(0.02));
    root.heightProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              double margin = newValue.doubleValue() * 0.1;
              VBox.setMargin(i1, new Insets(margin, 0, 0, 0));
              VBox.setMargin(ctfv, new Insets(margin, 0, 0, 0));
            });

    StackPane.setAlignment(vbox, Pos.TOP_CENTER);
    vbox.setAlignment(Pos.TOP_CENTER);
    vbox.setMaxWidth(50);
    App.wrapper = new StackPane();
    root.getChildren().addAll(wrapper, vbox);
    BackgroundImage background =
        new BackgroundImage(
            App.backgroundImage,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            App.backgroundSize);
    App.wrapper.setBackground(new Background(background));
    addServerPane(root);
    return root;
  }
  
  private void addServerPane(StackPane stack) {
    ServerPane serverPane = new ServerPane();
    StackPane serverPaneWrapper = new StackPane();
    serverPaneWrapper.prefWidthProperty().bind(stack.widthProperty().multiply(0.22));
    serverPaneWrapper.prefHeightProperty().bind(serverPane.widthProperty().multiply(0.35));
    serverPaneWrapper.maxWidthProperty().bind(stack.widthProperty().multiply(0.22));
    serverPaneWrapper.maxHeightProperty().bind(serverPane.widthProperty().multiply(0.35));
    StackPane.setAlignment(serverPaneWrapper, Pos.BOTTOM_RIGHT);
   serverPaneWrapper.setPadding(new Insets(10));
    stack.widthProperty().addListener((obs, old, newV) -> {
     double neu = newV.doubleValue();
      serverPaneWrapper.setPadding(new Insets(0,neu*0.03,neu*0.03,0));
    });
    serverPaneWrapper.getChildren().add(serverPane);
    stack.getChildren().add(serverPaneWrapper);
    serverPane.getField().setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.ENTER) {
        String port = serverPane.getField().getText();
        serverContainer.startServer(port);
        if(serverContainer.checkStatus()) {
          Constants.userSelectedLocalServerPort = port;
          serverPane.setFinished();
        } else {
          serverPane.updatePromtText();
        }
      }
    });
  }

  public static void adjustHomescreen(double width, double height) {
    App.root.setPrefWidth(width);
    App.root.setPrefHeight(height);
  }

  public static void chagngeHomescreenBackground() {
    Image bImage = ImageController.loadRandomThemedImage(ImageType.HOME);
    BackgroundSize backgroundSize = new BackgroundSize(1, 1, true, true, true, true);
    BackgroundImage background =
        new BackgroundImage(
            bImage,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            backgroundSize);
    App.wrapper.setBackground(new Background(background));
  }

  private void changeToHomeScreen() {
    startScene = new Scene(createParent());
    try {
      startScene.getStylesheets().add(Paths.get(Constants.toUIStyles + "MapEditor.css").toUri().toURL().toString());
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    mainStage.setScene(startScene);
    App.offsetHeight = mainStage.getHeight()-startScene.getHeight();
    App.offsetWidth = mainStage.getWidth()-startScene.getWidth();
    System.out.println("offsetHeight: " + App.offsetHeight + ", offsetWidth: " + App.offsetWidth);
    backgroundMusic.startShuffle();
    startTransition.stop();
  }

  /**
   * Generates all UI components and initializes Transitions required for the lock screen of the
   * application.
   *
   * @author aniemesc
   * @return Root of the lock screen
   */
  private Parent createLockScreen() {
    StackPane layer = new StackPane();
    backgroundImage = ImageController.loadRandomThemedImage(ImageType.HOME);
    BackgroundImage background =
        new BackgroundImage(
            backgroundImage,
            BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
            backgroundSize);
    layer.setBackground(new Background(background));
    layer
        .widthProperty()
        .addListener(
            (obs, old, newV) -> {
              double size = newV.doubleValue() * 0.02;
              layer.setPadding(new Insets(size));
            });
    layer.setPadding(new Insets(50));
    VBox root = new VBox();
    root.setAlignment(Pos.CENTER);

    Image ctf = ImageController.loadThemedImage(ImageType.MISC, "CaptureTheFlag");
    ImageView ctfv = new ImageView(ctf);
    ctfv.fitWidthProperty().bind(mainStage.widthProperty().multiply(0.8));
    ctfv.setPreserveRatio(true);
    FadeTransition ft = new FadeTransition(Duration.millis(5000), ctfv);
    ft.setFromValue(0.0);
    ft.setToValue(1.0);
    ft.play();
    root.getChildren().add(ctfv);

    Text text = new Text("Press any Key to Start!");
    layer
        .heightProperty()
        .addListener(
            (obs, old, newV) -> {
//              double size = newV.doubleValue() * 0.2;
              VBox.setMargin(ctfv, new Insets(0, 0, newV.doubleValue() * 0.6, 0));
            });
    text.setStyle("-fx-fill: white ;");
    text.setOpacity(0);

    Text filler = new Text("");
    root.getChildren().add(filler);
    layer.getChildren().add(text);

    startTransition = new FadeTransition(Duration.millis(1500), text);
    startTransition.setFromValue(0.1);
    startTransition.setToValue(1.0);
    startTransition.setDelay(Duration.millis(2000));
    startTransition.setAutoReverse(true); //
    startTransition.setCycleCount(Timeline.INDEFINITE);
    startTransition.play();
    text.fontProperty()
        .bind(
            Bindings.createObjectBinding(
                () -> Font.font("Century Gothic", mainStage.getWidth() / 40),
                mainStage.widthProperty()));

    layer.getChildren().add(root);
    return layer;
  }

  /**
   * Changes the title of the game window to present extra infomration to the user
   *
   * @author rsyed
   * @param title The title you want the window to have
   */
  public static void setTitle(String title) {
    mainStage.setTitle(title);
  }

  /**
   * Creates a background for the lockscreen.
   *
   * @author rsyed
   * @return String which is the current title of the UI Window
   */
  public static String getTitle() {
    return mainStage.getTitle();
  }

  public static void main(String[] args) {
    launch(args);
  }

  public static Stage getStage() {
    return mainStage;
  }

  public static Scene getScene() {
    return startScene;
  }
}
