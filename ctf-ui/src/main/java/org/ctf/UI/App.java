package org.ctf.UI;

import org.ctf.UI.customObjects.*;
import java.io.IOException;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * @author mkrakows
 * @author rsyed (Bug fixer) startpoint for the Gui Application Opens HomeScreen
 */
public class App extends Application {
  static Stage mainStage;
  static Scene startScene;
  HomeSceneController ssc = new HomeSceneController();

  public void start(Stage stage) {
    mainStage = stage;
    startScene = new Scene(createParent());
    stage.setScene(startScene);
    stage.show();
  }

  /**
   * @author mkrakows creates a new stage and scene with a stackpane as root container. Sets a
   *     background image which is bound to the size of the window and 3 custom buttons. {@link
   *     costumObjects.HomeScreenButton }
   * @return Parent
   */
  private Parent createParent() {
    StackPane root = new StackPane();
    root.setPrefSize(600, 600);
    Image bImage = new Image(getClass().getResourceAsStream("output.jpg"));
    ImageView vw = new ImageView(bImage);
    vw.fitWidthProperty().bind(root.widthProperty());
    vw.fitHeightProperty().bind(root.heightProperty());
    HomeScreenButton i1 =
        new HomeScreenButton(
            "CREATE MAP",
            () -> {
              try {
                ssc.switchtoScene2();
              } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
            });
    HomeScreenButton i2 = new HomeScreenButton("CREATE GAME", () -> {});
    HomeScreenButton i3 = new HomeScreenButton("JOIN GAME", () -> {new HomeSceneController().switchToJoinScene(mainStage);});
    VBox vbox = new VBox(11, i1, i2, i3);
    vbox.setAlignment(Pos.CENTER);
    vbox.setMaxWidth(50);
    root.getChildren().addAll(vw, vbox);
    return root;
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