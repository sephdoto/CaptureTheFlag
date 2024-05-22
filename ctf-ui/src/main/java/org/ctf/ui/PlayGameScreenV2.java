package org.ctf.ui;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.ctf.shared.client.AIClient;
import org.ctf.shared.client.Client;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.ImageType;
import org.ctf.shared.state.GameState;
import org.ctf.ui.controllers.CheatboardListener;
import org.ctf.ui.controllers.ImageController;
import org.ctf.ui.customobjects.BaseRep;
import org.ctf.ui.customobjects.CostumFigurePain;
import org.ctf.ui.customobjects.Timer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class PlayGameScreenV2 extends Scene {

  private ScheduledExecutorService scheduler;
  private ScheduledExecutorService scheduler2;

  private Client mainClient;

  private HomeSceneController hsc;
  private StackPane root;
  private HBox captureLoadingLabel;
  private boolean isRemote;
  private GameState currentState;


  private Label moveTimeLimit;
  private Label gameTimeLimit;
  private Timer noMoveTimeLimit;

  private GamePane gm;
  private VBox right;
  private HBox top;
  private ImageView mpv;
  private static Circle c;
  private static Label idLabel;
  private static Label typeLabel;
  private static Label attackPowLabel;
  private static Label teamLabel;
  private static Label countLabel;
  private Button giveUpButton;
  private StackPane showMapBox;
  private ObjectProperty<Color> sceneColorProperty = new SimpleObjectProperty<>(Color.BLUE);
  private SimpleObjectProperty<Insets> padding = new SimpleObjectProperty<>(new Insets(10));
  private ObjectProperty<Font> timerLabel; 
  private ObjectProperty<Font> timerDescription;
  private static ObjectProperty<Font> pictureMainDiscription;
  private ObjectProperty<Font> figureDiscription;
  private ObjectProperty<Font> waitigFontSize;


/**
 * Sets all important attributes and initializes the scene
 * @author Manuel Krakowski
 * @param hsc Controller to switch to different scenes
 * @param width initial width of the screen
 * @param height initial height of the screen
 * @param mainClient Client which is used to pull the newest Data from the server
 * @param isRemote true if the screen is used from remote-join
 */
  public PlayGameScreenV2(HomeSceneController hsc, double width, double height, Client mainClient,
      boolean isRemote) {
    super(new StackPane(), width, height);
    this.mainClient = mainClient;
    this.isRemote = isRemote;
    this.root = (StackPane) this.getRoot();
    this.hsc = hsc;
    initalizePlayGameScreen();
    CheatboardListener.setSettings(root, this);
  }


  

  // **************************************************
  // Start of scene initialization methods
  // **************************************************

  /**
   * initializes the scene by adding css-Stylesheets and starting the schedulers which are used to refresh the data
   * @author Manuel Krakowski
   */
  public void initalizePlayGameScreen() {
    manageFontSizes();
    try {
      this.getStylesheets()
          .add(Paths.get(Constants.toUIStyles + "MapEditor.css").toUri().toURL().toString());
      this.getStylesheets()
          .add(Paths.get(Constants.toUIStyles + "color.css").toUri().toURL().toString());
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    createLayout();
    if (mainClient.isGameTimeLimited() || mainClient.isGameMoveTimeLimited()) {
      scheduler2 = Executors.newScheduledThreadPool(1);
      scheduler2.scheduleAtFixedRate(updateTask2, 0, 1, TimeUnit.SECONDS);
    }
    scheduler = Executors.newScheduledThreadPool(1);
    scheduler.scheduleAtFixedRate(updateTask, 0, 100, TimeUnit.MILLISECONDS);
  }

  
  /**
   * Manages the font-sizes of all text of the screen when the screen size changes
   * @author Manuel Krakowski
   */
  private void manageFontSizes() {
    timerLabel  = new SimpleObjectProperty<Font>(Font.font(this.getWidth()/42));
    timerDescription  = new SimpleObjectProperty<Font>(Font.font(this.getWidth()/60));
    pictureMainDiscription  = new SimpleObjectProperty<Font>(Font.font(this.getWidth()/40));
    figureDiscription  = new SimpleObjectProperty<Font>(Font.font(this.getWidth()/45));
    waitigFontSize  = new SimpleObjectProperty<Font>(Font.font(this.getWidth()/55));
    widthProperty().addListener(new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth,
          Number newWidth) {
        timerLabel.set(Font.font(newWidth.doubleValue() / 42));
        timerDescription.set(Font.font(newWidth.doubleValue() / 60));
        pictureMainDiscription.set(Font.font(newWidth.doubleValue() / 40));
        figureDiscription.set(Font.font(newWidth.doubleValue() / 45));
        padding.set(new Insets(newWidth.doubleValue() * 0.01));
        waitigFontSize.set(Font.font(newWidth.doubleValue() / 55));


      }
    });
  }

  /**
   * Creates the whole layout of the scene
   * @author Manuel Krakowski
   */
  public void createLayout() {
    root.setStyle("-fx-background-color: black");
    root.paddingProperty().bind(padding);
    root.prefHeightProperty().bind(this.heightProperty());
    root.prefWidthProperty().bind(this.widthProperty());
    top = new HBox();
    top.setAlignment(Pos.CENTER);
    right = new VBox();
    right.setAlignment(Pos.BOTTOM_CENTER);
    top.prefHeightProperty().bind(this.heightProperty());
    showMapBox = new StackPane();
    showMapBox.paddingProperty().bind(padding);
    showMapBox.paddingProperty().bind(padding);
    showMapBox.prefWidthProperty().bind(this.widthProperty().multiply(0.7));
    showMapBox.prefHeightProperty().bind(this.heightProperty().multiply(0.9));
    showMapBox.maxWidthProperty().bind(App.getStage().widthProperty().multiply(0.7));
    showMapBox.maxHeightProperty().bind(App.getStage().heightProperty().multiply(0.9));
    showMapBox.getStyleClass().add("option-pane");
    Image mp =
        new Image(new File(Constants.toUIResources + "pictures" + File.separator + "grid.png")
            .toURI().toString());
    mpv = new ImageView(mp);
    StackPane.setAlignment(mpv, Pos.CENTER);
    this.widthProperty().addListener((obs, old, newV) -> {
      mpv.setFitWidth(newV.doubleValue() * 0.8);
    });
    this.heightProperty().addListener((obs, old, newV) -> {
      mpv.setFitHeight(newV.doubleValue() * 0.8);
    });
    mpv.setPreserveRatio(true);
    showMapBox.getChildren().add(mpv);
    top.getChildren().add(showMapBox);
    right.getChildren().add(createGiveUpBox());
    right.getChildren().add(createTopCenter());
    right.getChildren().add(createFigureDesBox());
    right.getChildren()
        .add(createClockBox(mainClient.isGameMoveTimeLimited(), mainClient.isGameTimeLimited()));
    right.setStyle("-fx-background-color: black");
    right.prefWidthProperty().bind(this.widthProperty().multiply(0.3));
    top.getChildren().add(right);
    root.getChildren().add(top);
  }



  // **************************************************
  // End of scene initialization methods
  // **************************************************

  // **************************************************
  // Start of map-visualization methods
  // **************************************************

  /**
   * used by a scheduler to always update the map with the latest GameState from the Queue
   * and constantly check whether the game is over
   * @author Manuel Krakowski
   */
  Runnable updateTask = () -> {
    try {
      if (mainClient.isGameOver()) {
        String[] winners = mainClient.getWinners();
        Platform.runLater(() -> {
          PopupCreatorGameOver gameOverPop = new PopupCreatorGameOver(this, root, hsc);
          if (winners.length == 1) {
            gameOverPop.createGameOverPopUpforOneWinner(winners[0]);
          } else {
            gameOverPop.createGameOverPopUpforMoreWinners(winners);
          }
        });
        scheduler.shutdown();
        scheduler2.shutdown();
      }
      GameState tmp = mainClient.getQueuedGameState();
      if (tmp != null) {
        currentState = tmp;
        Platform.runLater(() -> {
          if (!mainClient.isGameMoveTimeLimited()) {
            noMoveTimeLimit.reset();
          }
          this.redrawGrid(currentState);
          this.setTeamTurn();
        });
      }
    } catch (Exception e) {
    }
  };

  /**
   * Redraws the grid and checks if it's one local player's turn to set its figures active
   * Give-Up button is only enabled in case it's one local players turn
   * @author Manuel Krakowski
   * @param state current state which used to redraw the map
   */
  public void redrawGrid(GameState state) {
    boolean oneClientCanGiveUp = false;
    if (state == null) {
      showMapBox.getChildren().add(new Label("hallo"));
    } else {
      drawGamePane(state);
      if (isRemote) {
        if (mainClient.isItMyTurn() && !(mainClient instanceof AIClient)) {
          Game.initializeGame(gm, mainClient);
        }
      } else {
        for (Client local : CreateGameController.getLocalHumanClients()) {
          if (local.isItMyTurn()) {
            Game.initializeGame(gm, local);
            oneClientCanGiveUp = true;
          }
        }
        if (oneClientCanGiveUp) {
          giveUpButton.setDisable(false);
        } else {
          giveUpButton.setDisable(true);
        }
      }
    }
  }

  /**
   * Draws the map which belongs to a gameState and saves the last figures to show the last move on the map
   * @author Manuel Krakowski
   * @param state GameState which is shown on the map
   */
  private void drawGamePane(GameState state) {
    if (gm != null) {
      CreateGameController.setFigures(gm.getFigures());
      showMapBox.getChildren().remove(gm);
    }
    gm = new GamePane(state, false);
    StackPane.setAlignment(gm, Pos.CENTER);
    gm.maxWidthProperty().bind(mpv.fitWidthProperty());
    gm.maxHeightProperty().bind(mpv.fitHeightProperty());
    gm.prefWidthProperty().bind(mpv.fitWidthProperty());
    gm.prefHeightProperty().bind(mpv.fitHeightProperty());
    gm.enableBaseColors(this);
    showMapBox.getChildren().add(gm);
  }



  /**
   * Updates the content of the container visualizing the Game. It reloads the dynamic background
   * image and redraws the {@link GamePane}. This method should be called to when dynamic background
   * images are generated in a separate Thread.
   * 
   * 
   * @author aniemesc
   */
  public void UpdateLeftSide() {
    showMapBox.getChildren().clear();
    Image mp =
        new Image(new File(Constants.toUIResources + "pictures" + File.separator + "grid.png")
            .toURI().toString());
    mpv = new ImageView(mp);
    StackPane.setAlignment(mpv, Pos.CENTER);
    mpv.setFitWidth(this.getWidth() * 0.8);
    this.widthProperty().addListener((obs, old, newV) -> {
      mpv.setFitWidth(newV.doubleValue() * 0.8);
    });
    mpv.setFitHeight(this.getHeight() * 0.8);
    this.heightProperty().addListener((obs, old, newV) -> {
      mpv.setFitHeight(newV.doubleValue() * 0.8);
    });
    mpv.setPreserveRatio(true);
    showMapBox.getChildren().add(mpv);
    this.drawGamePane(currentState);
  }



  // **************************************************
  // End of map visualization methods
  // **************************************************

  // **************************************************
  // Start of current team turn visualization methods
  // **************************************************

  
  /**
   * Shows the team-name of the current team turn using two different methods in case it it's a local clients turn or not
   * @author Manuel Krakowski
   */
  public void setTeamTurn() {
    boolean onelocal = false;
    captureLoadingLabel.getChildren().clear();
    if (isRemote) {
      if (mainClient.isItMyTurn() && !(mainClient instanceof AIClient)) {
        captureLoadingLabel.getChildren().add(showYourTurnBox());

      } else {
        captureLoadingLabel.getChildren().add(showWaitingBox());
      }
    } else {
      for (Client local : CreateGameController.getLocalHumanClients()) {
        if (local.isItMyTurn()) {
          captureLoadingLabel.getChildren().add(showYourTurnBox());
          onelocal = true;
        }
      }
      if (!onelocal) {
        captureLoadingLabel.getChildren().add(showWaitingBox());
      }
    }
  }

  /**
   * Creates the box in which the String for the current team-turn is shown
   * @author Manuel Krakowski
   * @return team-turn-box
   */
  private HBox createTopCenter() {
    captureLoadingLabel = new HBox();
    captureLoadingLabel.setAlignment(Pos.CENTER);
    captureLoadingLabel.prefWidthProperty().bind(right.widthProperty().multiply(0.8));
    return captureLoadingLabel;
  }


  /**
   * Shows an waiting animation with the team-name in case it's not the turn of one local client
   * @author Manuel Krakowski
   * @return box with waiting animation and team-name
   */
  private VBox showWaitingBox() {
    final Label status = new Label("is making its move");
    status.getStyleClass().add("spinner-des-label");
    final Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, new EventHandler() {
      @Override
      public void handle(Event event) {
        String statusText = status.getText();
        status.setText(("is making its move . . .".equals(statusText)) ? "is making its move ."
            : statusText + " .");
      }
    }), new KeyFrame(Duration.millis(1000)));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
    VBox layout = new VBox();
    if (mainClient.getCurrentTeamTurn() != -1) {
      String teamString = mainClient.getAllTeamNames()[mainClient.getCurrentTeamTurn()];
      teamString += " (" + mainClient.getCurrentTeamTurn() + ")";
      Label teamname = new Label(teamString);
      teamname.prefWidthProperty().bind(this.widthProperty().multiply(0.2));
      teamname.fontProperty().bind(waitigFontSize);
      teamname.setAlignment(Pos.CENTER);
      teamname.textFillProperty().bind(
          CreateGameController.getColors().get(String.valueOf(mainClient.getCurrentTeamTurn())));
      layout.prefWidthProperty().bind(this.widthProperty().multiply(0.17));
      status.fontProperty().bind(waitigFontSize);
      status.textFillProperty().bind(
          CreateGameController.getColors().get(String.valueOf(mainClient.getCurrentTeamTurn())));
      layout.getChildren().add(teamname);
      layout.getChildren().addAll(status);
    }
    return layout;
  }

  /**
   * Shows the team-name without animation in case it's one local client's turn
   * @author Manuel Krakowski
   * @return box with 'your turn' String and team-name
   */
  private VBox showYourTurnBox() {
    Label status = new Label("It's your turn!");
    status.getStyleClass().add("spinner-des-label");
    VBox layout = new VBox();
    String teamString = mainClient.getAllTeamNames()[mainClient.getCurrentTeamTurn()];
    teamString += " (" + mainClient.getCurrentTeamTurn() + ")";
    Label teamname = new Label(teamString);
    teamname.prefWidthProperty().bind(this.widthProperty().multiply(0.2));
    teamname.fontProperty().bind(waitigFontSize);
    teamname.setAlignment(Pos.CENTER);
    teamname.textFillProperty().bind(
        CreateGameController.getColors().get(String.valueOf(mainClient.getCurrentTeamTurn())));
    layout.prefWidthProperty().bind(this.widthProperty().multiply(0.17));
    status.fontProperty().bind(waitigFontSize);
    status.setAlignment(Pos.CENTER);
    status.prefWidthProperty().bind(this.widthProperty().multiply(0.17));
    status.textFillProperty().bind(
        CreateGameController.getColors().get(String.valueOf(mainClient.getCurrentTeamTurn())));
    layout.getChildren().add(teamname);
    layout.getChildren().addAll(status);
    return layout;
  }

  /**
   * Creates the button which is used to give up
   * @author Manuel Krakowski
   * @return Box containing only the give-up-button
   */
  private HBox createGiveUpBox() {
    HBox giveUpBox = new HBox();
    giveUpBox.prefWidthProperty().bind(right.widthProperty());
    giveUpBox.prefHeightProperty().bind(right.heightProperty().multiply(0.05));
    giveUpBox.setAlignment(Pos.TOP_RIGHT);
    giveUpButton = new Button("Give up");
    giveUpButton.prefWidthProperty().bind(giveUpBox.widthProperty().multiply(0.25));
    giveUpButton.getStyleClass().add("leave-button");
    giveUpButton.setOnAction(e -> {
      hsc.switchtoHomeScreen(e);
      CreateGameController.clearUsedNames();
      CreateGameController.clearColors();
      scheduler.shutdown();
      if(scheduler2 != null) {
      scheduler2.shutdown();
      }
    });
    giveUpBox.getChildren().add(giveUpButton);
    return giveUpBox;
  }



  // **************************************************
  // End of current team turn visualization methods
  // **************************************************

  // **************************************************
  //  Start Color Chooser-Usage method
  // **************************************************


  /**
   * Opens a color-chooser window when a teams base is clicked
   * the user can select its team color there
   * @author Manuel Krakowski
   * @param d: Mouse-click x-coordinate
   * @param e: mouse-click y-coordinate
   * @param r: Base which was clicked
   */
  public void showColorChooser(double d, double e, BaseRep r) {
    MyCustomColorPicker myCustomColorPicker = new MyCustomColorPicker();
    myCustomColorPicker.setCurrentColor(sceneColorProperty.get());
    CustomMenuItem itemColor = new CustomMenuItem(myCustomColorPicker);
    itemColor.getStyleClass().add("custom-menu-item");
    itemColor.setHideOnClick(false);
    CreateGameController.getColors().get(r.getTeamID())
        .bind(myCustomColorPicker.customColorProperty());
    for (CostumFigurePain p : gm.getFigures().values()) {
      if (p.getTeamID().equals(r.getTeamID())) {
        p.showTeamColorWhenSelecting(CreateGameController.getColors().get(r.getTeamID()));
      }
    }
    r.showColor(CreateGameController.getColors().get(r.getTeamID()));
    ContextMenu contextMenu = new ContextMenu(itemColor);
    contextMenu.setOnHiding(t -> {
      sceneColorProperty.unbind();
      for (CostumFigurePain m : gm.getFigures().values()) {
        m.unbind();
      }
    });
    contextMenu.show(this.getWindow(), d, e);
  }



  // **************************************************
  // End of Color Chooser-Usage method
  // **************************************************

  // **************************************************
  // Start of timer methods
  // **************************************************

/**
 * Creates a box containing the timers for the move-time and for the game-time
 * @author Manuel Krakowski
 * @param movetimelimited true if the move time is limited, false otherwise
 * @param gametimeLimited true if the game time is limited, false otherwise
 * @return
 */
  private HBox createClockBox(boolean movetimelimited, boolean gametimeLimited) {
    HBox timerBox = new HBox();
    timerBox.setAlignment(Pos.CENTER);
    timerBox.prefWidthProperty().bind(right.widthProperty());
    timerBox.widthProperty().addListener((observable, oldValue, newValue) -> {
      double newSpacing = newValue.doubleValue() * 0.09;
      double padding = newValue.doubleValue() * 0.02;
      timerBox.setSpacing(newSpacing);
      timerBox.setPadding(new Insets(0, padding, 0, padding));
    });
    VBox timer1;
    VBox timer2;
    if (movetimelimited) {
      timer1 = createMoveTimeLimitTimer(timerBox, "Move Time");
      System.out.println("move time limited");
    } else {
      timer1 = createNoMoveTimeLimitTimer(timerBox, "Move Time");
    }
    if (gametimeLimited) {
      timer2 = createMoveTimeLimitTimer(timerBox, "Game Time");
      System.out.println("Game time limited");
    } else {
      timer2 = createNoMoveTimeLimitTimer(timerBox, "Game Time");
    }

    timerBox.getChildren().addAll(timer1, timer2);
    return timerBox;
  }

/**
 * Creates a not move-time-limited timer which is counting the time up using a custom {@link Timer} 
 * @author Manuel Krakowski
 * @param timerBox: Box in which the timer is placed used for relative resizing in it
 * @param text: text describing which timer it is
 * @return not move-time-limited timer
 */
  private VBox createNoMoveTimeLimitTimer(HBox timerBox, String text) {
    VBox timerwithDescrip = new VBox();
    timerwithDescrip.setAlignment(Pos.CENTER);
    timerwithDescrip.prefWidthProperty().bind(timerBox.widthProperty().multiply(0.35));
    timerwithDescrip.prefHeightProperty().bind(timerBox.widthProperty().multiply(0.35));
    Label desLabel = new Label(text);
    desLabel.setAlignment(Pos.CENTER);
    desLabel.fontProperty().bind(timerDescription);
    desLabel.getStyleClass().add("des-timer");
    timerwithDescrip.getChildren().add(desLabel);
    if (text.equals("Move Time")) {
      noMoveTimeLimit = new Timer(0, 0, 0);
      noMoveTimeLimit.prefWidthProperty().bind(timerBox.widthProperty().multiply(0.35));
      noMoveTimeLimit.prefHeightProperty().bind(noMoveTimeLimit.widthProperty().multiply(0.35));
      noMoveTimeLimit.getStyleClass().add("timer-label");
      noMoveTimeLimit.fontProperty().bind(timerLabel);
      timerwithDescrip.getChildren().add(noMoveTimeLimit);
    } else {
      Timer t = new Timer(0, 0, 0);
      t.prefWidthProperty().bind(timerBox.widthProperty().multiply(0.35));
      t.prefHeightProperty().bind(t.widthProperty().multiply(0.35));
      t.getStyleClass().add("timer-label");
      t.fontProperty().bind(timerLabel);
      timerwithDescrip.getChildren().add(t);
    }
    return timerwithDescrip;
  }

  /**
   * Creates a move-time-limited timer which is using data from the server to refresh the time
   * @author Manuel Krakowski
   * @param timerBox: box in which timer is placed used for relative resizing
   * @param text: Text to describe which timer it is
   * @return move-time-limited timer
   */
  private VBox createMoveTimeLimitTimer(HBox timerBox, String text) {
    VBox timerwithDescrip = new VBox();
    timerwithDescrip.setAlignment(Pos.CENTER);
    timerwithDescrip.prefWidthProperty().bind(timerBox.widthProperty().multiply(0.35));
    timerwithDescrip.prefHeightProperty().bind(timerBox.widthProperty().multiply(0.35));
    Label desLabel = new Label(text);
    desLabel.setAlignment(Pos.CENTER);
    desLabel.fontProperty().bind(timerDescription);
    desLabel.getStyleClass().add("des-timer");
    timerwithDescrip.getChildren().add(desLabel);
    if (text.equals("Move Time")) {
      moveTimeLimit = new Label();
      moveTimeLimit.prefWidthProperty().bind(timerBox.widthProperty().multiply(0.35));
      moveTimeLimit.prefHeightProperty().bind(moveTimeLimit.widthProperty().multiply(0.35));
      moveTimeLimit.getStyleClass().add("timer-label");
      moveTimeLimit.fontProperty().bind(timerLabel);
      timerwithDescrip.getChildren().add(moveTimeLimit);
    } else {
      gameTimeLimit = new Label();
      gameTimeLimit.prefWidthProperty().bind(timerBox.widthProperty().multiply(0.35));
      gameTimeLimit.prefHeightProperty().bind(gameTimeLimit.widthProperty().multiply(0.35));
      gameTimeLimit.getStyleClass().add("timer-label");
      gameTimeLimit.fontProperty().bind(timerLabel);
      timerwithDescrip.getChildren().add(gameTimeLimit);
    }
    return timerwithDescrip;
  }

  /**
   * Converts the time from seconds to the String-format which is used in the timers
   * @author Manuel Krakowski
   * @param totalSeconds time which
   * @return time in seconds as formatted String
   */
  private String formatTime(int totalSeconds) {
    int hours = totalSeconds / 3600;
    int minutes = (totalSeconds % 3600) / 60;
    int seconds = totalSeconds % 60;
    return String.format("%d:%02d:%02d", hours, minutes, seconds);
  }


  Runnable updateTask2 = () -> {
    try {
      Platform.runLater(() -> {
        if (mainClient.isGameMoveTimeLimited()) {
          moveTimeLimit.setText(formatTime(mainClient.getRemainingMoveTimeInSeconds()));
          if (mainClient.getRemainingMoveTimeInSeconds() < 10) {
            moveTimeLimit.setTextFill(Color.RED);
          } else {
            moveTimeLimit.setTextFill(Color.GOLD);
          }
        }
        if (mainClient.isGameTimeLimited()) {
          gameTimeLimit.setText(formatTime(mainClient.getRemainingGameTimeInSeconds()));
          if (mainClient.getRemainingGameTimeInSeconds() < 60) {
            gameTimeLimit.setTextFill(Color.RED);
          }
        }

      });
    } catch (Exception e) {


    }
  };

  // **************************************************
  // End of CRUD Call Methods
  // **************************************************

  // **************************************************
  // Start of CRUD Call Parsers
  // **************************************************



  private HBox createFigureDesBox() {
    HBox h1 = new HBox();
    h1.prefHeightProperty().bind(this.heightProperty().multiply(0.65));
    h1.prefWidthProperty().bind(h1.heightProperty().multiply(0.3));
    h1.minWidthProperty().bind(h1.heightProperty().multiply(0.3));
    h1.widthProperty().addListener((observable, oldValue, newValue) -> {
      double padding = newValue.doubleValue() * 0.08;
      h1.setPadding(new Insets(padding, padding, padding, padding));
    });

    h1.setAlignment(Pos.CENTER);
    VBox x = new VBox();

    x.widthProperty().addListener((observable, oldValue, newValue) -> {
      double padding = newValue.doubleValue() * 0.05;
      x.setPadding(new Insets(padding, padding, padding, padding));
    });
    x.getStyleClass().add("option-pane");
    HBox pict = new HBox();
    pict.prefHeightProperty().bind(x.heightProperty().multiply(0.1));
    typeLabel = new Label("-");
    typeLabel.fontProperty().bind(pictureMainDiscription);
    typeLabel.setAlignment(Pos.CENTER_LEFT);
    typeLabel.prefHeightProperty().bind(pict.heightProperty());
    typeLabel.prefWidthProperty().bind(pict.widthProperty().multiply(0.7));
    typeLabel.getStyleClass().add("figure-label");
    StackPane p = new StackPane();
    p.prefWidthProperty().bind(pict.widthProperty().multiply(0.3));
    Image mp = ImageController.loadThemedImage(ImageType.MISC, "question-mark");
    c = new Circle();
    c.radiusProperty().bind(Bindings.divide(widthProperty(), 23));
    c.setFill(new ImagePattern(mp));
    Circle c2 = new Circle();
    c2.setFill(Color.WHITE);
    c2.setStroke(Color.BLACK);
    c2.setStrokeWidth(2);
    c2.radiusProperty().bind(Bindings.divide(widthProperty(), 21));
    pict.getChildren().addAll(typeLabel, p);
    p.getChildren().addAll(c2, c);
    x.getChildren().add(pict);
    x.getChildren().add(createDeslabelBox());
    h1.getChildren().add(x);
    return h1;
  }



  private VBox createDeslabelBox() {
    VBox deBox = new VBox(10);
    deBox.heightProperty().addListener((observable, oldValue, newValue) -> {
      double spacing = newValue.doubleValue() * 0.08;
      deBox.setSpacing(spacing);
    });
    deBox.setAlignment(Pos.BASELINE_LEFT);
    idLabel = new Label("id: -");
    handleLabel(idLabel, deBox);
    teamLabel = new Label("team: -");
    handleLabel(teamLabel, deBox);
    attackPowLabel = new Label("attackpower: -");
    handleLabel(attackPowLabel, deBox);
    countLabel = new Label("count: - ");
    handleLabel(countLabel, deBox);
    deBox.getChildren().addAll(idLabel, teamLabel, attackPowLabel, countLabel);
    return deBox;
  }

  private void handleLabel(Label l, VBox parent) {
    l.fontProperty().bind(figureDiscription);
    l.prefWidthProperty().bind(parent.widthProperty());
    l.getStyleClass().add("figure-label");
  }

  // **************************************************
  // End of CRUD Call Methods
  // **************************************************

  // **************************************************
  // Start of CRUD Call Parsers
  // **************************************************



  public static void setFigureImage(Image img) {
    c.setFill(new ImagePattern(img));
  }

  public static void setIdLabelText(String text) {
    idLabel.setText(text);
  }

  public static void setTypeLabelText(String text) {
    typeLabel.setText(text);
  }

  public static void setAttackPowLabelText(String text) {
    attackPowLabel.setText(text);
  }

  public static void setCountLabelText(String text) {
    countLabel.setText(text);
  }

  public static void setTeamLabelText(String text) {
    teamLabel.setText(text);
  }

}
