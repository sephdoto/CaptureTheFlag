package org.ctf.ui;

import configs.ImageLoader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.ctf.shared.client.lib.ServerChecker;
import org.ctf.shared.client.lib.ServerDetails;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.ImageType;
import org.ctf.ui.controllers.ImageController;
import org.ctf.ui.controllers.MusicPlayer;
import org.ctf.ui.controllers.SettingsSetter;
import org.ctf.ui.customobjects.*;


/**
 * @author mkrakows
 * @author rsyed (Bug fixer) startpoint for the GUI, server management features
 * @author aniemesc
 * @author sistumpf adding background music and sounds
 */
public class App extends Application {
  static Stage mainStage;
  static Scene startScene;
  static MusicPlayer backgroundMusic;
  HomeSceneController ssc;
  FadeTransition startTransition;
  Process process;
  static StackPane wrapper;
  static StackPane root;
  static Image backgroundImage;
  static BackgroundSize backgroundSize = new BackgroundSize(1, 1, true, true, true, true);
  static boolean serverStartSuccess;

  public void start(Stage stage) {
    mainStage = stage;
    Parameters params = getParameters();
    String port = params.getNamed().get("port");
    ssc = new HomeSceneController(mainStage);
    SettingsSetter.loadCustomSettings();
    ImageLoader.loadImages();
    Scene lockscreen = new Scene(createLockScreen(), 1000, 500);
    lockscreen.getStylesheets().add(getClass().getResource("MapEditor.css").toExternalForm());
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
    startServer("8888");
    backgroundMusic = new MusicPlayer();
    stage.setOnCloseRequest(
        e -> {
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
    root.setPrefSize(mainStage.getWidth(), mainStage.getHeight());
    Image ctf = ImageController.loadThemedImage(ImageType.MISC, "CaptureTheFlag");
    ImageView ctfv = new ImageView(ctf);
    ctfv.fitWidthProperty().bind(mainStage.widthProperty().multiply(0.8));
    ctfv.setPreserveRatio(true);
    StackPane.setAlignment(ctfv, Pos.TOP_CENTER);
    HomeScreenButton i1 = new HomeScreenButton("CREATE MAP", mainStage, () -> {
      ssc.switchToMapEditorScene(mainStage);
    });
    HomeScreenButton i2 = new HomeScreenButton("CREATE GAME", mainStage, () -> {
      // CreateGameScreen.initCreateGameScreen(mainStage);
      ssc.switchToCreateGameScene(mainStage);
    });
    HomeScreenButton i3 = new HomeScreenButton("JOIN GAME", mainStage, () -> {
      ssc.switchToJoinScene(mainStage);
    });
    HomeScreenButton i4 = new HomeScreenButton("SETTINGS", mainStage, () -> {
      root.getChildren().add(new ComponentCreator(startScene).createSettingsWindow(root));
    });
    VBox vbox = new VBox(ctfv, i1, i2, i3, i4);
    vbox.spacingProperty().bind(root.heightProperty().multiply(0.02));
    root.heightProperty().addListener((observable, oldValue, newValue) -> {
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
        new BackgroundImage(App.backgroundImage, BackgroundRepeat.NO_REPEAT,
            BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, App.backgroundSize);
    App.wrapper.setBackground(new Background(background));
    return root;
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
    startScene.getStylesheets().add(getClass().getResource("MapEditor.css").toExternalForm());
    mainStage.setScene(startScene);
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
    BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT,
        BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
    layer.setBackground(new Background(background));
    layer.widthProperty().addListener((obs, old, newV) -> {
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
    layer.heightProperty().addListener((obs, old, newV) -> {
      double size = newV.doubleValue() * 0.2;
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
    text.fontProperty().bind(Bindings.createObjectBinding(
        () -> Font.font("Century Gothic", mainStage.getWidth() / 40), mainStage.widthProperty()));
   
    layer.getChildren().add(root);
    return layer;
  }

  /**
   * Creates a star filled background for the STARWARS theme.
   *
   * @author aniemesc
   * @param layer - Base container of the lockscreen
   */
  @Deprecated
  private void createBackground(Pane layer) {
    Circle c = new Circle();
    for (int i = 0; i < 200; i++) {
      c = new Circle(Math.random() * 1500, Math.random() * 1000, 0.5 + Math.random() * 1);
      c.setFill(Color.WHITE);
      layer.getChildren().add(c);
    }
  }

  /**
   * Starts a server at the designated port using the jar present in resources. Also sets the title
   * of the window to give information about the port of the server
   *
   * @author rsyed
   * @param port the port you want to start the server at
   * @return boolean true if server is active, false if it fails
   */
  public boolean startServer(String port) {
    try {
      ProcessBuilder processBuilder =
          new ProcessBuilder(
              "java", "-jar", Constants.toUIResources + "server.jar", "--server.port=" + port);
      processBuilder.redirectErrorStream(true);
      process = processBuilder.start();
      System.out.println(process.isAlive());
      new Thread(
              () -> {
                try (BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                  String line;
                  while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                  }

                } catch (IOException e) {
                  e.printStackTrace();
                }
              })
          .start();

    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    Thread checkServerThread =
        new Thread(
            () -> {
              boolean running = true;
              while (running) {
                try {
                  Long sleep = 5000L;
                  Thread.sleep(sleep);
                  if (new ServerChecker().isServerActive(new ServerDetails("localhost", port))) {
                    Constants.localServerPort = port;
                    serverStartSuccess = true;
                    if (serverStartSuccess) {
                      Platform.runLater(
                          () -> {
                            setTitle("CFP 14" + " Local Server is active @ " + port);
                          });
                    }
                  }
                  running = false;
                } catch (InterruptedException e) {
                  serverStartSuccess = false;
                  throw new Error("Server couldnt start");
                }
              }
            });
    checkServerThread.start();

    return serverStartSuccess;
  }

  /**
   * Kills the process holding the server instance inside of it. Closing the server
   *
   * @author rsyed
   */
  public void closeServer() {
    this.process.destroy();
    setTitle("CFP 14" + " Local Server is inactive");
    System.exit(0);
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
