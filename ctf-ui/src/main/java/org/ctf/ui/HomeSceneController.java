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

 

  @FXML
  public void switchtoHomeScreen(ActionEvent e) {
    Scene scene = App.getScene();
    stage = App.getStage();

    stage.setScene(scene);
  }

  public void switchToWaitGameScene(Stage stage) {
	  stage.setScene(new WaitingScene(this, stage.getWidth(), stage.getHeight()));
  }
  
  public void switchToCreateGameScene(Stage stage) {
	  stage.setScene(new CretaeGameScreenV2(this, stage.getWidth(), stage.getHeight()));
  }
  
  public void switchToJoinScene (Stage stage) {
	  stage.setScene(new JoinScene(this, stage.getWidth(), stage.getHeight()));
  }
  
  public void switchToMapEditorScene (Stage stage) {
	  stage.setScene(new EditorScene(this, stage.getWidth(), stage.getHeight()));
  }
  public Stage getStage() {
	  return stage;
  }
}
