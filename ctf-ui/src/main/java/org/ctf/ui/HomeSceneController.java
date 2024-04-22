package org.ctf.ui;

/**
 * @author mkrakows & aniemesc
This Class controls what happens when clicking the buttons on the HomeScreen
 */
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class HomeSceneController {
  @FXML private Stage stage;
  private Scene scene;
  private HBox root;

  public void switchtoScene2() throws IOException {
    root = FXMLLoader.load(getClass().getResource("LoadMapScene.fxml"));
    stage = App.getStage();
    if (root instanceof Pane) root.getChildren().add(0, createBackButtonMenuBar());
    scene = new Scene(root);
    stage.setScene(scene);
    stage.setMaximized(true);
  }

  @FXML
  public void switchtoHomeScreen(ActionEvent e) {
    Scene scene = App.getScene();
    stage = App.getStage();

    stage.setScene(scene);
  }

  public MenuBar createBackButtonMenuBar() {
    MenuItem backButton = new MenuItem("back");
    backButton.setOnAction(
        e -> {
          this.switchtoHomeScreen(e);
        });
    Menu fileMenu = new Menu("<-");
    fileMenu.getItems().add(0, backButton);
    MenuBar mn = new MenuBar();
    mn.getMenus().add(fileMenu);
    return mn;
  }
  
  public void switchToJoinScene (Stage stage) {
	  stage.setScene(new JoinScene(this, stage.getWidth(), stage.getHeight()));
  }
  
  public void switchToMapEditorScene (Stage stage) {
	  stage.setScene(new EditorScene(this, stage.getWidth(), stage.getHeight()));
  }
}
