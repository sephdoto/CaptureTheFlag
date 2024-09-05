package org.ctf.ui.creators;

import org.ctf.shared.ai.AIConfig;
import org.ctf.shared.constants.Enums.AI;
import org.ctf.shared.constants.Enums.SoundType;
import org.ctf.ui.controllers.SoundController;
import org.ctf.ui.customobjects.PopUpPane;
import org.ctf.ui.data.ClientCreator;
import org.ctf.ui.data.SceneHandler;
import org.ctf.ui.hostGame.CreateGameController;
import org.ctf.ui.hostGame.CreateGameScreen;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * Creates a Popup in which the user has to enter a team name into a textField which is used to
 * create a client
 * 
 * @author Manuel Krakowski
 */
public class PopUpCreatorEnterTeamName {
  private Scene scene;
  private boolean isMain;
  private boolean isAi;
  private StackPane root;
  private PopUpPane before;
  private TextField enterNamefield;
  private PopUpPane enterNamePopUp;
  private String teamName;
  private AIConfig config;
  private AI aitype;
  private ObjectProperty<Font> popUpLabel;
  private ObjectProperty<Font> leaveButtonText;

  /**
   * Initializes the Popup-Creator depending on in which situation it is used
   * 
   * @author Manuel Krakowski
   * @param scene scene on that popup is shown
   * @param root root of the scene on which it is shown
   * @param before popup that was shown before, null it none was shwon before
   * @param hsc controller to switch scenes
   * @param isMain if the team-name is the team-name of the main-creator
   * @param isAi if the teamname is for an Ai
   */
  public PopUpCreatorEnterTeamName(Scene scene, StackPane root, PopUpPane before, boolean isMain, boolean isAi) {
    this.scene = scene;
    this.root = root;
    this.before = before;
    this.isMain = isMain;
    this.isAi = isAi;
    popUpLabel = new SimpleObjectProperty<Font>(Font.font(scene.getWidth() / 50));
    leaveButtonText = new SimpleObjectProperty<Font>(Font.font(scene.getWidth() / 80));
    manageFontSizes();
  }

  /**
   * Manges all font-sizes on the popup
   * 
   * @author Manuel Krakowski
   */
  private void manageFontSizes() {
    scene.widthProperty().addListener(new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth,
          Number newWidth) {
        popUpLabel.set(Font.font(newWidth.doubleValue() / 50));
        leaveButtonText.set(Font.font(newWidth.doubleValue() / 80));
      }
    });
  }


  /**
   * Creates a Popup in which the user can enter the teamname
   * 
   * @author Manuel Krakowski
   */
  public void createEnterNamePopUp() {
    enterNamePopUp = new PopUpPane(scene, 0.5, 0.3, 0.95);
    root.getChildren().remove(before);
    VBox top = new VBox();
    top.heightProperty().addListener((obs, oldVal, newVal) -> {
      double spacing = newVal.doubleValue() * 0.09;
      top.setSpacing(spacing);
    });
    Label l = new Label("Select Team Name");
    l.prefWidthProperty().bind(enterNamePopUp.widthProperty());
    l.setAlignment(Pos.CENTER);
    l.getStyleClass().add("custom-label");
    l.setFont(Font.font(scene.getWidth() / 50));
    l.fontProperty().bind(popUpLabel);
    top.getChildren().add(l);
    HBox enterNameBox = new HBox();
    enterNameBox.setAlignment(Pos.CENTER);
    enterNamefield = CreateGameScreen.createTextfield("Team Name", null, 0.5);
    enterNamefield.prefWidthProperty().bind(enterNameBox.widthProperty().multiply(0.8));
    enterNameBox.getChildren().add(enterNamefield);
    top.getChildren().add(enterNameBox);
    HBox centerLeaveButton = new HBox();
    enterNamePopUp.widthProperty().addListener((observable, oldValue, newValue) -> {
      double newSpacing = newValue.doubleValue() * 0.05;
      centerLeaveButton.setSpacing(newSpacing);
    });
    centerLeaveButton.prefHeightProperty().bind(enterNamePopUp.heightProperty().multiply(0.4));
    centerLeaveButton.setAlignment(Pos.CENTER);
    centerLeaveButton.getChildren().addAll(createEnterButton(), createBackButton("name"));
    top.getChildren().add(centerLeaveButton);
    enterNamePopUp.setContent(top);
    root.getChildren().add(enterNamePopUp);
  }


  /**
   * Creates a enter button. Depending on when then Popup is shown a different action is executed
   * 
   * @author Manuel Krakowski
   * @return Enter-Button
   */
  private Button createEnterButton() {
    Button exit = new Button("Enter");
    exit.fontProperty().bind(leaveButtonText);
    exit.getStyleClass().add("leave-button");
    exit.prefWidthProperty().bind(root.widthProperty().multiply(0.1));
    exit.prefHeightProperty().bind(exit.widthProperty().multiply(0.25));
    exit.setOnAction(e -> {
      SoundController.playSound("Button", SoundType.MISC);
      if (enterNamefield.getText().isEmpty()) {
        CreateGameScreen.informationmustBeEntered(enterNamefield,
            "custom-search-field2-mustEnter", "custom-search-field2");
      } else if (CreateGameController.isNameUsed(enterNamefield.getText())) {
        CreateGameScreen.informationmustBeEntered(enterNamefield,
            "custom-search-field2-mustEnter", "custom-search-field2");
        enterNamefield.clear();
        enterNamefield.setPromptText("Enter a unique Teamname");
      } else {
        teamName = enterNamefield.getText();
        CreateGameController.setName(teamName);
        CreateGameController.setLastTeamName(teamName);

        if (isMain && !isAi) {
          ClientCreator.createHumanClient(
              isMain, teamName, false, CreateGameController.getServerIP(), CreateGameController.getPort(), CreateGameController.getServerManager().getGameSessionID());
          SceneHandler.switchToWaitGameScene();
          CreateGameController.setLasttype("HUMAN");
        }
        if (!isMain && !isAi) {
          ClientCreator.createHumanClient(
              isMain, teamName, false, CreateGameController.getServerIP(), CreateGameController.getPort(), CreateGameController.getServerManager().getGameSessionID());
          root.getChildren().remove(enterNamePopUp);
          CreateGameController.setLasttype("HUMAN");

        }
        if (isMain && isAi) {
          ClientCreator.createAiClient(
              isMain, teamName, false, CreateGameController.getServerIP(), CreateGameController.getPort(), CreateGameController.getServerManager().getGameSessionID(), aitype, config);
          SceneHandler.switchToWaitGameScene();
          CreateGameController.setLasttype("AI");
          CreateGameController.setLastAitype(aitype);

        }
        if (!isMain && isAi) {
          ClientCreator.createAiClient(
              isMain, teamName, false, CreateGameController.getServerIP(), CreateGameController.getPort(), CreateGameController.getServerManager().getGameSessionID(), aitype, config);
          root.getChildren().remove(enterNamePopUp);
          CreateGameController.setLasttype("AI");
          CreateGameController.setLastAitype(aitype);
        }
      }
    });
    return exit;
  }


  /**
   * Creates a Back button which rests the scene to its state before the popup was shown
   * 
   * @author Manuel Krakowski
   * @param text
   * @return
   */
  private Button createBackButton(String text) {
    Button exit = new Button("back");
    exit.setFont(Font.font(scene.getWidth() / 80));
    exit.fontProperty().bind(leaveButtonText);
    exit.getStyleClass().add("leave-button");
    exit.prefWidthProperty().bind(root.widthProperty().multiply(0.1));
    exit.prefHeightProperty().bind(exit.widthProperty().multiply(0.25));
    exit.setOnAction(e -> {
      if (!isMain && !isAi) {
        root.getChildren().remove(enterNamePopUp);
      } else {
        root.getChildren().remove(enterNamePopUp);
        root.getChildren().add(before);
      }
    });
    return exit;
  }


  public AIConfig getConfig() {
    return config;
  }

  public void setConfig(AIConfig config) {
    this.config = config;
  }

  public AI getAitype() {
    return aitype;
  }

  public void setAitype(AI aitype) {
    this.aitype = aitype;
  }
}
