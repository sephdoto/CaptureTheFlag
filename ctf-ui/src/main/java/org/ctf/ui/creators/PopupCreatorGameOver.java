package org.ctf.ui.creators;

import java.util.ArrayList;
import org.ctf.shared.client.Client;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.ImageType;
import org.ctf.ui.controllers.ImageController;
import org.ctf.ui.customobjects.PopUpPane;
import org.ctf.ui.data.ClientStorage;
import org.ctf.ui.data.SceneHandler;
import org.ctf.ui.highscore.LeaderBoardController;
import org.ctf.ui.highscore.Score;
import org.ctf.ui.hostGame.CreateGameController;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * Creator for the PopupPanes which are shown when the Game is over. Can create 3 different
 * Popup-Panes in case there is only one winner,in case there are more winners and in case somebody
 * lost while other teams keep playing
 * 
 * @author Manuel Krakowski
 */

public class PopupCreatorGameOver {
  // Attributes which are necessary to show the Popup-Pane on the corresponding scene
  private Scene scene;
  private StackPane root;
  private PopUpPane gameOverPopUp;

  // Attributes to manage the font-sizes
  private ObjectProperty<Font> popUpLabel;
  private ObjectProperty<Font> scoreLabel;
  private ObjectProperty<Font> leaveButtonText;
  private ObjectProperty<Font> moreWinnerheader;
  private ObjectProperty<Font> moreWinnersName;


/**
 * Creates a "game is over" Popup.
 * Clears local clients on creation, as they are not needed after the game has ended.
 * Waits 10 seconds after the game has ended to send a delete Session request.
 * 
 * @param scene
 * @param root
 * @param hsc
 */
  public PopupCreatorGameOver(Scene scene, StackPane root) {
    this.scene = scene;
    this.root = root;
    popUpLabel = new SimpleObjectProperty<Font>(Font.font(scene.getWidth() / 30));
    scoreLabel = new SimpleObjectProperty<Font>(Font.font(scene.getWidth() / 50));
    leaveButtonText = new SimpleObjectProperty<Font>(Font.font(scene.getWidth() / 80));
    moreWinnerheader = new SimpleObjectProperty<Font>(Font.font(scene.getWidth() / 40));
    moreWinnersName = new SimpleObjectProperty<Font>(Font.font(scene.getWidth() / 50));
    manageFontSizes();
    try {
      new Thread() {
        @Override
        public void run() {
          try {
            Client client = ClientStorage.getMainClient();
            sleep(10000);
            client.deleteSession();
          } catch (Exception e) {
            System.err.println("Error at " + e.getStackTrace()[0] 
                + "\n\tSession might have been deleted earlier, no worries."
                + "\n\tCaught in " + this.getStackTrace()[this.getStackTrace().length-2]);
          }
        }
      }.start();
      
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Fits the font-sizes of all the text on the Popup to the screen size
   * 
   * @author Manuel Krakowski
   */
  private void manageFontSizes() {
    scene.widthProperty().addListener(new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth,
          Number newWidth) {
        popUpLabel.set(Font.font(newWidth.doubleValue() / 30));
        scoreLabel.set(Font.font(newWidth.doubleValue() / 50));
        leaveButtonText.set(Font.font(newWidth.doubleValue() / 80));
        moreWinnerheader.set(Font.font(newWidth.doubleValue() / 40));
        moreWinnersName.set(Font.font(newWidth.doubleValue() / 50));
      }
    });
  }


  /**
   * Creates a Game Over Popup-Pane in case that only one player wins the Game including some
   * animations
   * 
   * @author Manuel Krakowski
   * @param name Name of the winner
   */
  public void createGameOverPopUpforOneWinner(String name) {
    gameOverPopUp = new PopUpPane(scene, 0.6, 0.5, 0.6); // TODO
    StackPane poproot = new StackPane();
    poproot.getChildren().add(createBackgroundKonfetti(poproot));
    VBox top = new VBox();
    top.heightProperty().addListener((obs, oldVal, newVal) -> {
      double spacing = newVal.doubleValue() * 0.09;
      double padding = newVal.doubleValue() * 0.15;
      top.setSpacing(spacing);
      top.setPadding(new Insets(padding, 0, 0, 0));
    });
    VBox scoreAndButton = new VBox();
    top.setAlignment(Pos.TOP_CENTER);
    Label l = new Label("The Winner is " + name);
    l.prefWidthProperty().bind(gameOverPopUp.widthProperty());
    l.setAlignment(Pos.CENTER);
    l.setTextFill(Color.GOLD);
    l.setFont(Font.font(scene.getWidth() / 50));
    l.fontProperty().bind(popUpLabel);
    Button playAgainButton = createConfigButton("Play Again");
    playAgainButton.setVisible(false);
    Label score = new Label(calculateScore());
    score.setStyle("-fx-background-fill: red");
    score.prefWidthProperty().bind(gameOverPopUp.widthProperty());
    score.setAlignment(Pos.CENTER);
    score.setTextFill(Color.GOLD);
    score.setFont(Font.font(scene.getWidth() / 50));
    score.fontProperty().bind(scoreLabel);
    score.setVisible(false);
    Button analyseGameButton = createConfigButton("Analyze Game");
    analyseGameButton.setVisible(false);
    TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(2), l);
    translateTransition.setFromY(-scene.getHeight());
    translateTransition.setToY(0);
    ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(2), l);
    scaleTransition.setFromX(0);
    scaleTransition.setFromY(0);
    scaleTransition.setToX(1);
    scaleTransition.setToY(1);
    FadeTransition fadeTransition1 = new FadeTransition(Duration.seconds(1), playAgainButton);
    fadeTransition1.setFromValue(0);
    fadeTransition1.setToValue(1);
    FadeTransition fadeTransition2 = new FadeTransition(Duration.seconds(1), analyseGameButton);
    fadeTransition2.setFromValue(0);
    fadeTransition2.setToValue(1);
    fadeTransition2.setOnFinished(event -> {
      score.setVisible(true);
      playAgainButton.setVisible(true);
      analyseGameButton.setVisible(true);
    });
    SequentialTransition textTransition =
        new SequentialTransition(translateTransition, scaleTransition);
    ParallelTransition buttonTransition = new ParallelTransition(fadeTransition1, fadeTransition2);
    SequentialTransition mainTransition =
        new SequentialTransition(textTransition, buttonTransition);
    mainTransition.play();
    HBox x = createButtonBox();
    x.getChildren().addAll(playAgainButton, analyseGameButton);
    top.getChildren().add(createHeader(poproot, "gameOver3"));
//    top.getChildren().add(l);
    scoreAndButton.getChildren().add(l);
    scoreAndButton.getChildren().add(score);
    scoreAndButton.getChildren().add(x);
//    top.getChildren().add(score);
    top.getChildren().add(scoreAndButton);
    poproot.getChildren().add(top);
    gameOverPopUp.setContent(poproot);
    root.getChildren().add(gameOverPopUp);
  }

  /**
   * Calculates a score, in case the MainClient is a winner
   * 
   * @author sistumpf
   */
  private String calculateScore() {
    String highScore = "";
    ArrayList<Score> scores = new ArrayList<Score>();
    for(String winner : ClientStorage.getMainClient().getWinners()) {
      for(Client client : ClientStorage.getLocalHumanClients()) {
        if(winner.equals(client.getRequestedTeamName())) {
          Score newScore = new Score(client.getRequestedTeamName(), ClientStorage.getMainClient().getGameSaveHandler().savedGame);
          scores.add(newScore);
          if(Constants.tournamentMode)
            LeaderBoardController.addEntry(newScore);
        }
      }
    }
    
    long bestScore = -1;
    for(Score score : scores) {
      if(bestScore < score.getPoints()) {
        bestScore = score.getPoints();
        highScore = score.getplayerName() + "'s total time: " + bestScore;
      }
    }

    ClientStorage.clearAllClients();

    if(bestScore < 0)
      return "";
    else
      if(Constants.tournamentMode)
        LeaderBoardController.saveCurrentBoard();
    return highScore;
  }
  

  /**
   * Creates a Game Over Popup-Pane in case their is more than one Winner, showing the list of all
   * Winners a scrollPane
   * 
   * @author Manuel Krakowski
   * @param names List of the names of all the winners
   */
  public void createGameOverPopUpforMoreWinners(String[] names) {
    gameOverPopUp = new PopUpPane(scene, 0.6, 0.8, 0.6); //TODO
    StackPane poproot = new StackPane();
    poproot.getChildren().add(createBackgroundKonfetti(poproot));
    VBox top = new VBox();
    top.heightProperty().addListener((obs, oldVal, newVal) -> {
      double spacing = newVal.doubleValue() * 0.09;
      double padding = newVal.doubleValue() * 0.15;
      top.setSpacing(spacing);
      top.setPadding(new Insets(padding, 0, 0, 0));
    });

    Label score = new Label(calculateScore());
    score.setStyle("-fx-background-fill: red");
    score.prefWidthProperty().bind(gameOverPopUp.widthProperty());
    score.setAlignment(Pos.CENTER);
    score.setTextFill(Color.GOLD);
    score.setFont(Font.font(scene.getWidth() / 50));
    score.fontProperty().bind(scoreLabel);
    
    top.setAlignment(Pos.TOP_CENTER);
    Button playAgainButton = createConfigButton("Play Again");
    Button analyseGameButton = createConfigButton("Analyze Game");
    HBox x = createButtonBox();
    x.getChildren().addAll(playAgainButton, analyseGameButton);
    top.getChildren().add(createHeader(poproot, "gameOver3"));
    
    VBox scoreAndButton = new VBox();
    scoreAndButton.getChildren().add(createWinnersPane(names));
    scoreAndButton.getChildren().add(score);
    scoreAndButton.getChildren().add(x);
    top.getChildren().add(scoreAndButton);
//    top.getChildren().add(createWinnersPane(names));
//    top.getChildren().add(score);
//    top.getChildren().add(x);
    poproot.getChildren().add(top);
    gameOverPopUp.setContent(poproot);
    root.getChildren().add(gameOverPopUp);
  }


  /**
   * Creates the scrollpane with the list of all the winners which is used for the Game Over
   * Popup-Pane for more winners
   * 
   * @author Manuel Krakowski
   * @param winners List of the names of all the winners
   * @return Vbox inclduing one header label and the scrollpane with all the winners names
   */
  private VBox createWinnersPane(String[] winners) {
    VBox winnerPane = new VBox();
    winnerPane.prefWidthProperty().bind(gameOverPopUp.widthProperty().multiply(0.3));
    winnerPane.heightProperty().addListener((obs, oldVal, newVal) -> {
      double spacing = newVal.doubleValue() * 0.065;
      winnerPane.setSpacing(spacing);
    });
    winnerPane.setAlignment(Pos.TOP_CENTER);
    Label header = new Label("Winners");
    header.prefWidthProperty().bind(gameOverPopUp.widthProperty());
    header.setAlignment(Pos.CENTER);
    header.setTextFill(Color.GOLD);
    header.setFont(Font.font(scene.getWidth() / 50));
    header.fontProperty().bind(moreWinnerheader);
    ScrollPane scroller = new ScrollPane();
    // scroller.setStyle("-fx-background-color:blue");
    scroller.getStyleClass().clear();
    scroller.getStyleClass().add("scroll-pane");
    scroller.maxWidthProperty().bind(gameOverPopUp.widthProperty().multiply(0.3));
    scroller.minHeightProperty().bind(gameOverPopUp.heightProperty().multiply(0.2));
    scroller.setHbarPolicy(ScrollBarPolicy.NEVER);
    VBox content = new VBox();
    content.prefWidthProperty().bind(scroller.widthProperty());
    content.prefHeightProperty().bind(scroller.heightProperty());
    content.setStyle("-fx-background-color: black");
    content.heightProperty().addListener((obs, oldVal, newVal) -> {
      double spacing = newVal.doubleValue() * 0.04;
      content.setSpacing(spacing);
    });
    content.setAlignment(Pos.TOP_CENTER);
    for (String winner : winners) {
      Label l = new Label(winner);
      l.prefWidthProperty().bind(content.widthProperty());
      l.setAlignment(Pos.CENTER);
      l.setTextFill(Color.GOLD);
      l.setFont(Font.font(scene.getWidth() / 50));
      l.fontProperty().bind(moreWinnersName);
      content.getChildren().add(l);
    }
    scroller.setContent(content);
    winnerPane.getChildren().addAll(header, scroller);
    return winnerPane;
  }


  /**
   * Greates an Image containing the words 'Game Over' which is showed on top of the Popup-Pane
   * 
   * @author Manuel Krakowski
   * @param conRoot StackPane on which the image is placed, used for relative resizing
   * @return Game-Over-Image
   */
  private ImageView createHeader(StackPane conRoot, String imageName) {
    Image mp = ImageController.loadThemedImage(ImageType.MISC, imageName);
    ImageView mpv = new ImageView(mp);
    mpv.fitHeightProperty().bind(conRoot.heightProperty().multiply(0.5));
    mpv.fitWidthProperty().bind(conRoot.widthProperty().multiply(0.8));
    mpv.setPreserveRatio(true);
    return mpv;
  }


  /**
   * Creates a GIF which constantly shows Konfetti falling down in the background of the PopUp-Pane
   * 
   * @author Manuel Krakowski
   * @param configRoot StackPane on which the Konfetti is placed
   * @return
   */
  private ImageView createBackgroundKonfetti(StackPane configRoot) {
    Image mp = ImageController.loadThemedImage(ImageType.MISC, "konfetti2");
    // Image mp = new Image(getClass().getResourceAsStream("konfetti2.gif"));
    ImageView mpv = new ImageView(mp);
    mpv.fitHeightProperty().bind(configRoot.heightProperty().divide(1.1));
    mpv.fitWidthProperty().bind(configRoot.widthProperty().divide(1.1));
    // mpv.setPreserveRatio(true);
    mpv.setOpacity(0.7);
    return mpv;
  }



  /**
   * Creates the Box in which the 'Play Again' and the 'Analyze Game' buttons are placed
   * 
   * @author Manuel Krakowski
   * @return Box for the buttons
   */
  private HBox createButtonBox() {
    HBox centerLeaveButton = new HBox();
    gameOverPopUp.widthProperty().addListener((observable, oldValue, newValue) -> {
      double newSpacing = newValue.doubleValue() * 0.05;
      centerLeaveButton.setSpacing(newSpacing);
    });
    centerLeaveButton.prefHeightProperty().bind(gameOverPopUp.heightProperty().multiply(0.3));
    centerLeaveButton.setAlignment(Pos.CENTER);
    return centerLeaveButton;
  }

  /**
   * performs the action that happens when the 'play Again' Button is clicked which is going back to
   * the home-Screen
   * 
   * @author Manuel Krakowski
   * @param b Play-Again-button
   */
  private void perfromPlayAgain(Button b) {
    b.setOnAction(e -> {
      SceneHandler.switchToHomeScreen();
      CreateGameController.clearUsedNames();
      CreateGameController.clearColors();
    });
  }


  private void perfromAnalyseGame(Button b) {
    b.setOnAction(e -> {
      SceneHandler.switchToAnalyzerScene();
      CreateGameController.clearUsedNames();
      CreateGameController.clearColors();

    });
  }

  /**
   * Creates a default button with a special style and size, method is used to create play-again and
   * analyze-game buttons
   * 
   * @author Manuel Krakowski
   * @param text Text that is displayed on the button
   * @return Button
   */
  private Button createConfigButton(String text) {
    Button configButton = new Button(text);
    configButton.fontProperty().bind(leaveButtonText);
    configButton.getStyleClass().add("leave-button");
    configButton.prefWidthProperty().bind(root.widthProperty().multiply(0.15));
    configButton.prefHeightProperty().bind(configButton.widthProperty().multiply(0.25));
    if (text.equals("Play Again")) {
      perfromPlayAgain(configButton);
    } else {
      perfromAnalyseGame(configButton);
    }
    return configButton;
  }

  /**
   * Creates a PopupPane which tells the user that he lost the game
   * 
   * @author Manuel Krakowski
   */
  public void createGameOverPopUpYouLost(String name) {
    gameOverPopUp = new PopUpPane(scene, 0.6, 0.5, 0.6);    //TODO
    StackPane poproot = new StackPane();
    poproot.getChildren().add(createBackgroundscelett(poproot));
    VBox top = new VBox();
    top.heightProperty().addListener((obs, oldVal, newVal) -> {
      double spacing = newVal.doubleValue() * 0.09;
      double padding = newVal.doubleValue() * 0.15;
      top.setSpacing(spacing);
      top.setPadding(new Insets(padding, 0, 0, 0));
    });
    top.setAlignment(Pos.TOP_CENTER);
    Label l = new Label(name);
    l.prefWidthProperty().bind(top.widthProperty());
    l.setAlignment(Pos.CENTER);
    l.setTextFill(Color.RED);
    l.setFont(Font.font(scene.getWidth() / 50));
    l.fontProperty().bind(popUpLabel);
    Button playAgainButton = createConfigButton("Play Again");
    Button analyseGameButton = createConfigButton("Watch Game");
    HBox x = createButtonBox();
    x.getChildren().addAll(playAgainButton, analyseGameButton);
    top.getChildren().add(createHeader(poproot, "youLost"));
    top.getChildren().add(l);
    top.getChildren().add(x);
    poproot.getChildren().add(top);
    gameOverPopUp.setContent(poproot);
    root.getChildren().add(gameOverPopUp);
  }

  /**
   * Creates a Background GIG of a skeleton
   * 
   * @author Manuel Krakowski
   * @param configRoot:parent for relative resizing
   * @return Imageview of a skelton
   */
  private ImageView createBackgroundscelett(StackPane configRoot) {
    Image mp = ImageController.loadThemedImage(ImageType.MISC, "DootDoot");
    ImageView mpv = new ImageView(mp);
    mpv.fitHeightProperty().bind(configRoot.heightProperty().divide(1.1));
    mpv.fitWidthProperty().bind(configRoot.widthProperty().divide(1.1));
    mpv.setOpacity(0.4);
    return mpv;
  }

}
