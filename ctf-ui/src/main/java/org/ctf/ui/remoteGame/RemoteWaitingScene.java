package org.ctf.ui.remoteGame;

import java.net.MalformedURLException;
import java.nio.file.Paths;
import org.ctf.shared.client.Client;
import org.ctf.shared.client.lib.ServerManager;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.ImageType;
import org.ctf.ui.App;
import org.ctf.ui.controllers.HomeSceneController;
import org.ctf.ui.controllers.ImageController;
import org.ctf.ui.data.SceneHandler;
import org.ctf.ui.hostGame.PlayGameScreenV2;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Provides a JavaFx Scene that is shown while waiting for an remote game to start. It displays the
 * current state and the number of players waiting in the lobby. It switches over to the
 * {@link PlayGameScreenV2} when the sessions starts.
 * 
 * @author aniemesc
 */
public class RemoteWaitingScene extends Scene {
  private StackPane root;
  private Text text;
  private ServerManager serverManager;

  /**
   * Starts the initialization of the scene and connects it to a CSS file.
   * 
   * @author aniemesc
   * @param client - Client used to create an {@link RemoteWaitingThread}
   * @param width - double for the width of the scene
   * @param height - double for the height of the scene
   * @param hsc - {@link HomeSceneController}
   * @param serverManager - {@link ServerManager}
   */
  public RemoteWaitingScene(double width, double height, ServerManager serverManager) {
    super(new StackPane(), width, height);
    this.serverManager = serverManager;
    root = (StackPane) this.getRoot();
    try {
      this.getStylesheets()
          .add(Paths.get(Constants.toUIStyles + "MapEditor.css").toUri().toURL().toString());
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    createLayout();
    RemoteWaitingThread rwt = new RemoteWaitingThread(this);
    rwt.start();
  }

  /**
   * Creates all required UI components for the Scene.
   * 
   * @author aniemesc
   */
  private void createLayout() {
    Image backgroundImage = ImageController.loadRandomThemedImage(ImageType.HOME);
    BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT,
        BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, App.backgroundSize);
    root.setBackground(new Background(background));
    VBox mainBox = new VBox();
    mainBox.setAlignment(Pos.TOP_CENTER);
    Image mp = ImageController.loadThemedImage(ImageType.MISC, "multiplayerlogo");
    ImageView mpv = new ImageView(mp);
    mpv.fitWidthProperty().bind(root.widthProperty().multiply(0.65));
    mpv.setPreserveRatio(true);
    mainBox.getChildren().add(mpv);
    StackPane.setAlignment(mainBox, Pos.TOP_CENTER);
    VBox.setMargin(mpv, new Insets(this.getHeight() * 0.05, 0, 0, 0));
    this.heightProperty().addListener((obs, old, newV) -> {
      double margin = newV.doubleValue() * 0.05;
      VBox.setMargin(mpv, new Insets(margin, 0, 0, 0));
    });
    text = new Text("Please wait for the game to start.");
    text.getStyleClass().add("custom-info-label");
    text.fontProperty()
        .bind(Bindings.createObjectBinding(
            () -> Font.font("Century Gothic", SceneHandler.getMainStage().getWidth() / 50),
            SceneHandler.getMainStage().widthProperty()));

    StackPane wrapper = new StackPane();
    wrapper.getStyleClass().add("loading-pane");
    wrapper.maxWidthProperty().bind(this.widthProperty().multiply(0.5));
    wrapper.maxHeightProperty().bind(this.heightProperty().multiply(0.4));
    wrapper.minWidthProperty().bind(this.widthProperty().multiply(0.5));
    wrapper.maxWidthProperty().bind(this.heightProperty().multiply(0.4));
    wrapper.getChildren().add(text);

    StackPane.setAlignment(text, Pos.CENTER);
    // root.getChildren().add(text);
    root.getChildren().add(wrapper);
    root.getChildren().add(mainBox);
  }

  public ServerManager getServerManager() {
    return serverManager;
  }

  public Text getText() {
    return text;
  }

  public StackPane getRootPane() {
    return root;
  }
}
