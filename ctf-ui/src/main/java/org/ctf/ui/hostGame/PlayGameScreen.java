package org.ctf.ui.hostGame;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.ctf.shared.client.AIClient;
import org.ctf.shared.client.Client;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.ImageType;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Piece;
import org.ctf.ui.controllers.ImageController;
import org.ctf.ui.creators.PopupCreatorGameOver;
import org.ctf.ui.customobjects.MyCustomColorPicker;
import org.ctf.ui.customobjects.PieceWalkPane;
import org.ctf.ui.customobjects.Timer;
import org.ctf.ui.data.ClientStorage;
import org.ctf.ui.data.SceneHandler;
import org.ctf.ui.map.BaseRep;
import org.ctf.ui.map.CustomFigurePane;
import org.ctf.ui.map.GamePane;
import org.ctf.ui.map.MoveVisualizer;
import org.ctf.ui.threads.PointAnimation;
import dialogs.Dialogs;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Scene which is shown when a game is played
 *
 * @author sistumpf
 * @author Manuel Krakowski
 */
public class PlayGameScreen extends Scene {

  // Data which is used to always refresh the scene with the newest state
  private ScheduledExecutorService scheduler;
  private ScheduledExecutorService scheduler2;
  private boolean schedulerLock;
  private boolean isRemote;
  private GameState currentState;
  private boolean showBlocks;

  // Time-limits
  private Label moveTimeLimit;
  private Label gameTimeLimit;
  private Timer noMoveTimeLimit;

  // Components that need to be accessed from everywhere in the scene
  private GamePane gm;
  private VBox right;
  private HBox top;
  private ImageView mpv;
  private static Circle figureImage;
  private static Circle figureImageBackground;
  private Button giveUpButton;
  private StackPane showMapBox;
  private StackPane root;
  private HBox captureLoadingLabel;

  // Piece-information that need to be set from outside
  private static Label idLabel;
  private static Label typeLabel;
  private static Label attackPowLabel;
  private static Label teamLabel;
  private static Label countLabel;
  private static PieceWalkPane pieceWalkPane;

  private ObjectProperty<Color> sceneColorProperty = new SimpleObjectProperty<>(Color.BLUE);
  private SimpleObjectProperty<Insets> padding = new SimpleObjectProperty<>(new Insets(10));
  private ObjectProperty<Font> timerLabel;
  private ObjectProperty<Font> timerDescription;
  private static ObjectProperty<Font> pictureMainDiscription;
  private ObjectProperty<Font> figureDiscription;
  private ObjectProperty<Font> waitigFontSize;

  // extra
  /**
   * An animation Thread for the "Game is Over..." text
   */
  private PointAnimation giveUpPanimation;
  /**
   * Keeps track of the time needed to initialize and show a new GamePane in the UI.
   * Is used to estimate the time it takes till all GameStates are shown.
   */
  private ArrayList<Long> timeToShowGameState;
  /**
   * Allows keeping track if the UI has realized the game is over
   */
  private PopupCreatorGameOver gameOverPop;

  /**
   * Sets all important attributes and initializes the scene
   *
   * @author Manuel Krakowski
   * @param hsc Controller to switch to different scenes
   * @param width initial width of the screen
   * @param height initial height of the screen
   * @param isRemote true if the screen is used from remote-join
   */
  public PlayGameScreen(
      double width, double height, boolean isRemote) {
    super(new StackPane(), width, height);
    schedulerLock = true;
    timeToShowGameState = new ArrayList<Long>();
    this.isRemote = isRemote;
    this.root = (StackPane) this.getRoot();
    this.showBlocks = true;
    initalizePlayGameScreen();
   }

  // **************************************************
  // Start of scene initialization methods
  // **************************************************

  /**
   * initializes the scene by adding css-Stylesheets and starting the schedulers which are used to
   * refresh the data
   *
   * @author Manuel Krakowski
   */
  public void initalizePlayGameScreen() {
    manageFontSizes();
    createLayout();

    reinitUiUpdateScheduler();
    if (ClientStorage.getMainClient().isGameTimeLimited() || ClientStorage.getMainClient().isGameMoveTimeLimited()) {
      reinitTimeScheduler();
    }
  }
  
  /**
   * Initiates the UI update scheduler with its task.
   * If a scheduler is already initiated, it gets killed and reinitiated.
   * 
   * @author sistumpf
   */
  public void reinitUiUpdateScheduler() {
    if(scheduler != null)
      scheduler.shutdown();
    scheduler = Executors.newScheduledThreadPool(1);
    scheduler.scheduleAtFixedRate(updateTask, 0, Constants.UIupdateTime, TimeUnit.MILLISECONDS);
  }

  /**
   * Initiates the UI timer scheduler with its task.
   * If a scheduler is already initiated, it gets killed and reinitiated.
   * 
   * @author sistumpf
   */
  public void reinitTimeScheduler() {
    if(scheduler2 != null)
      scheduler2.shutdown();
    scheduler2 = Executors.newScheduledThreadPool(1);
    scheduler2.scheduleAtFixedRate(timeUpdateTask, 0, Constants.UIupdateTime, TimeUnit.MILLISECONDS);
  }

  /**
   * Manages the font-sizes of all text of the screen when the screen size changes
   *
   * @author Manuel Krakowski
   */
  private void manageFontSizes() {
    timerLabel = new SimpleObjectProperty<Font>(Font.font(this.getWidth() / 42));
    timerDescription = new SimpleObjectProperty<Font>(Font.font(this.getWidth() / 60));
    pictureMainDiscription = new SimpleObjectProperty<Font>(Font.font(this.getWidth() / 40));
    figureDiscription = new SimpleObjectProperty<Font>(Font.font(this.getWidth() / 45));
    waitigFontSize = new SimpleObjectProperty<Font>(Font.font(this.getWidth() / 55));

    widthProperty()
        .addListener(
            new ChangeListener<Number>() {
              public void changed(
                  ObservableValue<? extends Number> observableValue,
                  Number oldWidth,
                  Number newWidth) {
                timerLabel.set(Font.font(newWidth.doubleValue() / 42));
                timerDescription.set(Font.font(newWidth.doubleValue() / 60));
                pictureMainDiscription.set(Font.font(newWidth.doubleValue() / 40));
                if((newWidth.doubleValue() / 45) *6 < heightProperty().multiply(0.7).doubleValue() || newWidth.doubleValue() < oldWidth.doubleValue())
                  figureDiscription.set(Font.font(newWidth.doubleValue() / 45));
                padding.set(new Insets(newWidth.doubleValue() * 0.01));
                waitigFontSize.set(Font.font(newWidth.doubleValue() / 55));
//                }
              }
            });
//    SceneHandler.getMainStage().maximizedProperty().addListener((change, oldValue, newValue) -> {
//      setFontSizes();
//    });
  }

  /**
   * Creates the whole layout of the scene
   *
   * @author sistumpf
   * @author Manuel Krakowski
   */
  public void createLayout() {
//    root.prefHeightProperty().bind(this.heightProperty());
//    root.prefWidthProperty().bind(this.widthProperty());
    top = new HBox();
    top.setAlignment(Pos.CENTER);
    right = new VBox();
    right.setAlignment(Pos.CENTER);
    top.prefHeightProperty().bind(this.heightProperty());
    top.prefWidthProperty().bind(this.widthProperty());
    top.minHeightProperty().bind(this.heightProperty());
    top.minWidthProperty().bind(this.widthProperty());
    top.maxHeightProperty().bind(this.heightProperty());
    top.maxWidthProperty().bind(this.widthProperty());
    showMapBox = new StackPane();
    showMapBox.paddingProperty().bind(padding);
    NumberBinding width = top.widthProperty().multiply(0.6);
    NumberBinding height = top.heightProperty().multiply(0.9);
    showMapBox.prefWidthProperty().bind(width);
    showMapBox.prefHeightProperty().bind(height);
    showMapBox.maxWidthProperty().bind(width);
    showMapBox.maxHeightProperty().bind(height);
    showMapBox.getStyleClass().add("option-pane");
    File grid = new File(Constants.toUIPictures + File.separator + "grid.png");
    String gridPicName = grid.exists() ? "grid.png" : "tuning1.png";
    Image mp =
        new Image(
            new File(Constants.toUIResources + "pictures" + File.separator + gridPicName)
                .toURI()
                .toString());
    mpv = new ImageView(mp);
//    StackPane.setAlignment(mpv, Pos.CENTER);
    if (gm != null) {
      mpv.fitWidthProperty().bind(gm.maxWidthProperty());
      mpv.fitHeightProperty().bind(gm.maxHeightProperty());
    } else {
      mpv.fitWidthProperty().bind(showMapBox.widthProperty().multiply(0.8));
      mpv.fitHeightProperty().bind(showMapBox.heightProperty().multiply(0.8));
    }
    mpv.setPreserveRatio(true);
    showMapBox.getChildren().add(mpv);
    right.getChildren().add(createGiveUpBox());
    right.getChildren().add(createTopCenter());
    right.getChildren().add(createFigureDesBox());
    HBox clockBox = createClockBox(ClientStorage.getMainClient().isGameMoveTimeLimited(), ClientStorage.getMainClient().isGameTimeLimited());
    clockBox.setUserData("clock");
    right
        .getChildren()
        .add(clockBox);
    right.prefWidthProperty().bind(this.widthProperty().multiply(0.3));
    right.maxWidthProperty().bind(this.widthProperty().multiply(0.3));
    right.prefHeightProperty().bind(this.heightProperty().multiply(0.9));
    right.maxHeightProperty().bind(this.heightProperty());
    top.getChildren().add(showMapBox);
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
   * used by a scheduler to always update the map with the latest GameState from the Queue and
   * constantly check whether the game is over
   *
   * @author sistumpf
   * @author Manuel Krakowski
   */
  Runnable updateTask =
      () -> {
        try {
          checkGameOver();
          updateUI(false);
        } catch (Exception e) {
          e.printStackTrace();
        }
      };
      
  /**
   * Calls a Task to update and redraw the UI.
   *
   * @author sistumpf
   * @param forceRedraw forces a UI redraw, even if there are no queued GameStates
   */
  private void updateUI(boolean forceRedraw) {
    if (ClientStorage.getMainClient() != null && (ClientStorage.getMainClient().queuedGameStates() > 0 || forceRedraw)) {
      if(Constants.showAiStats) {
        for(AIClient ai : ClientStorage.getLocalAIClients())
          if(ai.getMoveInfo() != null) {
            int timeTillClose = ai.getRemainingMoveTimeInSeconds() < 5 ? 8 : (int)(ai.getRemainingMoveTimeInSeconds() * 0.9);
            timeTillClose = Constants.forceAiThinkingTime > 0 ? Constants.forceAiThinkingTime : timeTillClose;
            String fullInfo = ai.getMoreMoveInfo();
            Dialogs.openDialogTwoButtons(ai.getRequestedTeamName() + "-AI Statistics: ", ai.getMoveInfo(), timeTillClose * 1000, 180, "CLOSE", "ENHANCE", 
                () -> Dialogs.openDialog("Full " + ai.getRequestedTeamName() + " Statistics", fullInfo, -1, 825));
            ai.clearMoveInfo();
          }
      }
      if (schedulerLock || forceRedraw) {
        schedulerLock = false;

        GameState newState = ClientStorage.getMainClient().getQueuedGameState();
        if (gm != null)
          while ((newState == null && ClientStorage.getMainClient().queuedGameStates() > 0)) 
            newState = ClientStorage.getMainClient().getQueuedGameState();

        if (newState != null) currentState = newState;
        
        RedrawTask redrawTask = new RedrawTask(newState);
        new Thread(redrawTask).start();

        Platform.runLater(
            () -> {
              redrawTask.setOnSucceeded(
                  event -> {
                    if(ClientStorage.getMainClient() != null) {
                      GamePane oldGm = gm;
                      if (redrawTask.getValue() != null) {
                        this.gm = redrawTask.getValue();
                        if (oldGm != null) {
                          CreateGameController.setFigures(oldGm.getFigures());
                          showMapBox.getChildren().remove(oldGm);
                        }
                        StackPane.setAlignment(gm, Pos.CENTER);
                        gm.enableBaseColors(this);

                        if(Constants.useBackgroundResizeFix) {
                          BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
                          javafx.scene.layout.BackgroundImage image = new javafx.scene.layout.BackgroundImage(mpv.getImage(),
                              BackgroundRepeat.NO_REPEAT,
                              BackgroundRepeat.NO_REPEAT,
                              BackgroundPosition.CENTER,
                              backgroundSize
                              );
                          gm.setBackground(new Background(image));
                        } else {
                          if(!showMapBox.getChildren().contains(mpv))
                            showMapBox.getChildren().add(mpv);
                        }
                        
                        showMapBox.getChildren().add(gm);

                        Text text = new Text("queued gs: " + ClientStorage.getMainClient().queuedGameStates());
                        if (oldText != null) showMapBox.getChildren().remove(oldText);
                        if(ClientStorage.getMainClient().queuedGameStates() > 0) {
                          showMapBox.getChildren().add(text);
                          oldText = text;
                        }

                      }

                      // update the giveUp button and the clickable pieces
                      Client active = isALocalClientsTurn();
                      if(active != null && !(active instanceof AIClient))
                        MoveVisualizer.initializeGame(gm, active);
                      else
                        MoveVisualizer.initializeGame(gm);  
                      
                      // Update the "it is your turn" label
                      PlayGameScreen.this.setTeamTurn();

                      schedulerLock = true;
                      timeToShowGameState.add(
                          System.currentTimeMillis() - redrawTask.getStartTimeMillis());
                    }
                  });
            });
      }
    }
  }
  Text oldText;
  
  /**
   * A Task to generate the new GamePane for the UI, so it does not happen in javaFX main Thread.
   * Also saves the time it started to generate the GamePane for later analysis. If an Exception or
   * anything else stops the Task, the schedulerLock gets opened again.
   *
   * @author sistumpf
   */
  private class RedrawTask extends Task<GamePane> {
    private GameState toDraw;
    private long startTimeMillis;

    public RedrawTask(GameState toDraw) {
      this.startTimeMillis = System.currentTimeMillis();
      this.toDraw = toDraw;
      this.setOnCancelled(
          (e) -> {
            schedulerLock = true;
            System.out.println("Redraw Task Cancelled");
          });
      this.setOnFailed(
          (e) -> {
            schedulerLock = true;
            System.out.println("Redraw Task Failed");
            PlayGameScreen.this.reinitUiUpdateScheduler();
          });
    }

    @Override
    protected GamePane call() throws Exception {
      
      GamePane gp = null;
      if (toDraw != null) {
        if (!ClientStorage.getMainClient().isGameMoveTimeLimited()) {
          noMoveTimeLimit.reset();
        }
        gp = createGamePane(toDraw);
      } else {
        gp = createGamePane(currentState);
      }
      return gp;
    }

    public long getStartTimeMillis() {
      return startTimeMillis;
    }
  }

  /**
   * Checks if the game is over and everything was displayed by the UI. If thats the case, a game
   * over pop up is displayed.
   *
   * @author sistumpf
   */
  private void checkGameOver() {
    if (ClientStorage.getMainClient().isGameOver() && gameOverPop == null) {
      if (giveUpPanimation == null) {
        giveUpButton.setDisable(true);
        giveUpPanimation = new PointAnimation(giveUpButton, "", "Give up", 7, 175);
        giveUpPanimation.start();
      }
      
      if (ClientStorage.getMainClient().queuedGameStates() <= 0) {
        giveUpPanimation.interrupt();
        String[] winners = ClientStorage.getMainClient().getWinners();
        Platform.runLater(
            () -> {
              gameOverPop = new PopupCreatorGameOver(this, root);
              if (winners.length <= 1) {
                gameOverPop.createGameOverPopUpforOneWinner(winners[0]);
              } else {
                gameOverPop.createGameOverPopUpforMoreWinners(winners);
              }
              SceneHandler.closeSettings();
            });
        
        stopTimers();
        
        if (scheduler != null) scheduler.shutdown();
      }
    }
  }
  
  /**
   * Stops the move and game time Timers and shuts down the scheduler responsible for updating them.
   * 
   * @author sistumpf
   */
  public void stopTimers() {
    if (scheduler2 != null) scheduler2.shutdown();
    
    for(Node node : right.getChildren()) {
      if(node.getUserData() != null && node.getUserData().equals("clock")) {
        for(Node box : ((HBox) node).getChildren()) {
          if(box.getUserData() != null && box.getUserData().equals("timer")) {
            ((Timer)((VBox) box).getChildren().get(1)).stop(); 
          }
        }
      }
    }
  }

  /**
   * Checks if it is a local AI or Human clients turn. If that's the case, the giveUp button gets
   * enabled. Returns the active
   *
   * @author sistumpf
   * @return the local client which's turn it is
   */
  private Client isALocalClientsTurn() {
    Client isMyTurn = null;
    if (isRemote) {
      if (ClientStorage.getMainClient().isItMyTurn()) {
        isMyTurn = ClientStorage.getMainClient();
      }
    } else {
      // check for human clients
      for (Client local : ClientStorage.getLocalHumanClients()) {
        if (local.isItMyTurn()) {
          isMyTurn = local;
          break;
        }
      }
      // check for AI clients
      if (isMyTurn == null)
        for (Client local : ClientStorage.getLocalAIClients()) {
          if (local.isItMyTurn()) {
            isMyTurn = local;
            break;
          }
        }
    }
    disableGiveUpButton(isMyTurn == null);
    return isMyTurn;
  }

  /**
   * Disables the giveUpButton in a new Platform.runLater()
   *
   * @author sistumpf
   * @param disableGiveUp true if the giveUp button will be disabled
   */
  private void disableGiveUpButton(boolean disableGiveUp) {
    Platform.runLater(
        () -> {
          giveUpButton.setDisable(disableGiveUp);
        });
  }

  /**
   * Creates the map which belongs to a gameState
   *
   * @author Manuel Krakowski
   * @param state GameState which is shown on the map
   */
  private GamePane createGamePane(GameState state) {
    GamePane gm = new GamePane(state, showBlocks, "", showMapBox.widthProperty(), showMapBox.heightProperty(), 0.9);
    StackPane.setAlignment(gm, Pos.CENTER);
    gm.enableBaseColors(this);
    return gm;
  }

  /**
   * Updates the content of the container visualizing the Game. It reloads the dynamic background
   * image and redraws the {@link GamePane}. This method should be called to when dynamic background
   * images are generated in a separate Thread.
   *
   * @author sistumpf
   * @author aniemesc
   */
  public void updateLeftSide() {
    showMapBox.getChildren().clear();
    this.showBlocks = false;
    
    Image mp = ImageController.loadFallbackImage(ImageType.MISC);
    String path = Constants.toUIResources + "pictures" + File.separator + "grid.png";
    while (!new File(Constants.toUIResources + "pictures" + File.separator + "grid.png").exists())
      try {
        Thread.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    try (RandomAccessFile file = new RandomAccessFile(path, "r")) {
      FileChannel channel = file.getChannel();
      FileLock lock = channel.lock(0, Long.MAX_VALUE, true);

      try {
        mp = new Image(new File(path).toURI().toString());
      } finally {
        lock.release();
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
    mpv = new ImageView(mp);
    mpv.setOpacity(Constants.backgroundImageOpacity);
    StackPane.setAlignment(mpv, Pos.CENTER);
    if (gm != null) {
      mpv.fitWidthProperty().bind(gm.maxWidthProperty());
      mpv.fitHeightProperty().bind(gm.maxHeightProperty());
    } else {
      mpv.fitWidthProperty().bind(showMapBox.widthProperty().multiply(0.9));
      mpv.fitHeightProperty().bind(showMapBox.heightProperty().multiply(0.9));
    }
    mpv.setPreserveRatio(true);
    showMapBox.getChildren().add(mpv);
    if (gm != null) updateUI(true);
  }

  // **************************************************
  // End of map visualization methods
  // **************************************************

  // **************************************************
  // Start of current team turn visualization methods
  // **************************************************

  /**
   * Shows the team-name of the current team turn using two different methods in case it it's a
   * local clients turn or not 
   *
   * @author Manuel Krakowski
   */
  public void setTeamTurn() {
    boolean onelocal = false;
    captureLoadingLabel.getChildren().clear();
    if (!ClientStorage.getMainClient().isGameOver()) {
      if (isRemote && !ClientStorage.getMainClient().isGameOver()) {
        if (ClientStorage.getMainClient().isItMyTurn() && !(ClientStorage.getMainClient() instanceof AIClient)) {
          captureLoadingLabel.getChildren().add(showYourTurnBox());

        } else {
          captureLoadingLabel.getChildren().add(showWaitingBox());
        }
      } else if (!ClientStorage.getMainClient().isGameOver()) {
        for (Client local : ClientStorage.getLocalHumanClients()) {
          if (local.isItMyTurn()) {
            captureLoadingLabel.getChildren().add(showYourTurnBox());
            onelocal = true;
            break;
          }
        }
        if (!onelocal) {
          captureLoadingLabel.getChildren().add(showWaitingBox());
        }
      }
    } else {
      captureLoadingLabel.getChildren().add(createWaitingForGameOverLabel());
    }
  }

  /**
   * Writes a "waiting for game over" message into a VBox that contains, how long it approximately
   * takes to display all the GameStates. Should be used if a game is over but the UI has not
   * displayed the full game yet.
   *
   * @author sistumpf
   * @return a "waiting for game over" text in a VBox
   */
  public VBox createWaitingForGameOverLabel() {
    Label status = new Label("wait approx. " + approximateWaitingTime() + "s");
    status.getStyleClass().add("spinner-des-label");
    VBox layout = new VBox();
    Label teamname = new Label("Game is Over");
    teamname.prefWidthProperty().bind(this.widthProperty().multiply(0.2));
    teamname.fontProperty().bind(waitigFontSize);
    teamname.setAlignment(Pos.CENTER);
    teamname.textFillProperty().bind(new SimpleObjectProperty<>(Color.GOLDENROD));
    layout.prefWidthProperty().bind(this.widthProperty().multiply(0.17));
    status.fontProperty().bind(waitigFontSize);
    status.setAlignment(Pos.CENTER);
    status.prefWidthProperty().bind(this.widthProperty().multiply(0.17));
    status.textFillProperty().bind(new SimpleObjectProperty<>(Color.GOLDENROD));
    layout.getChildren().add(teamname);
    layout.getChildren().addAll(status);
    return layout;
  }

  /**
   * Approximates the time it takes to show the remaining GameStates in the queue. It uses the
   * median of the times it took to display the GameStates to calculate the approximation. It should
   * be an overestimation for longer times.
   *
   * @author sistumpf
   * @return an approximation of the time it takes to show the remaining GameStates in the queue (in
   *     s)
   */
  private float approximateWaitingTime() {
    double median;
    try {
      median = timeToShowGameState.stream().mapToLong(l -> l).average().getAsDouble();
    } catch (NoSuchElementException nsee) {
      return Float.NaN;
    }
    median = median > Constants.UIupdateTime ? median : Constants.UIupdateTime;
    double multiplier =
        Constants.UIupdateTime > median
            ? Constants.UIupdateTime
            : ((median - Constants.UIupdateTime) / 1.5) + Constants.UIupdateTime;
    float time = (float) (Math.round((ClientStorage.getMainClient().queuedGameStates() * multiplier) / 100) / 10.);

    //    System.out.println("multiplier: " + multiplier + ", median: " + median + ", time: " +
    // time);
    return time;
  }

  /**
   * Creates the box in which the String for the current team-turn is shown
   *
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
   *
   * @author Manuel Krakowski
   * @return box with waiting animation and team-name
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  private VBox showWaitingBox() {
    final Label status = new Label("is making its move");
    status.getStyleClass().add("spinner-des-label");
    final Timeline timeline =
        new Timeline(
            new KeyFrame(
                Duration.ZERO,
                new EventHandler() {
                  @Override
                  public void handle(Event event) {
                    String statusText = status.getText();
                    status.setText(
                        ("is making its move . . .".equals(statusText))
                            ? "is making its move ."
                            : statusText + " .");
                  }
                }),
            new KeyFrame(Duration.millis(1000)));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
    VBox layout = new VBox();
    if (ClientStorage.getMainClient().getCurrentTeamTurn() != -1) {
      String teamString = ClientStorage.getMainClient().getAllTeamNames()[ClientStorage.getMainClient().getCurrentTeamTurn()];
      teamString += " (" + ClientStorage.getMainClient().getCurrentTeamTurn() + ")";
      Label teamname = new Label(teamString);
      teamname.prefWidthProperty().bind(this.widthProperty().multiply(0.2));
      teamname.fontProperty().bind(waitigFontSize);
      teamname.setAlignment(Pos.CENTER);
      try {
        teamname
            .textFillProperty()
            .bind(
                CreateGameController.getColors()
                    .get(String.valueOf(ClientStorage.getMainClient().getCurrentTeamTurn())));
      } catch (Exception e) {
        e.printStackTrace();
      }
      layout.prefWidthProperty().bind(this.widthProperty().multiply(0.17));
      status.fontProperty().bind(waitigFontSize);
      status
          .textFillProperty()
          .bind(
              CreateGameController.getColors()
                  .get(String.valueOf(ClientStorage.getMainClient().getCurrentTeamTurn())));

      teamname.setEffect(makeShadowEffect());
      layout.setEffect(makeShadowEffect());
      
      layout.getChildren().add(teamname);
      layout.getChildren().addAll(status);
    }
    return layout;
  }

  /**
   * Shows the team-name without animation in case it's one local client's turn.
   * Adjusts the color and a DropShadow for the team name.
   *
   * @author Manuel Krakowski
   * @author sistumpf
   * @return box with 'your turn' String and team-name
   */
  private VBox showYourTurnBox() {
    Label status = new Label("It's your turn!");
    status.getStyleClass().add("spinner-des-label");
    VBox layout = new VBox();
    String teamString = ClientStorage.getMainClient().getAllTeamNames()[ClientStorage.getMainClient().getCurrentTeamTurn()];
    teamString += " (" + ClientStorage.getMainClient().getCurrentTeamTurn() + ")";
    Label teamname = new Label(teamString);
    teamname.prefWidthProperty().bind(this.widthProperty().multiply(0.2));
    teamname.fontProperty().bind(waitigFontSize);
    teamname.setAlignment(Pos.CENTER);
    teamname
        .textFillProperty()
        .bind(
            CreateGameController.getColors().get(String.valueOf(ClientStorage.getMainClient().getCurrentTeamTurn())));
    layout.prefWidthProperty().bind(this.widthProperty().multiply(0.17));
    status.fontProperty().bind(waitigFontSize);
    status.setAlignment(Pos.CENTER);
    status.prefWidthProperty().bind(this.widthProperty().multiply(0.17));
    status
        .textFillProperty()
        .bind(
            CreateGameController.getColors().get(String.valueOf(ClientStorage.getMainClient().getCurrentTeamTurn())));
    
    teamname.setEffect(makeShadowEffect());
    layout.setEffect(makeShadowEffect());
    
    teamname.setStyle(teamString);
    layout.getChildren().add(teamname);
    layout.getChildren().addAll(status);
    return layout;
  }

  /**
   * Creates two DropShadows for outlining colored text
   * 
   * @author sistumpf
   * @return DropShadow effect
   */
  private DropShadow makeShadowEffect() {
    DropShadow edge = new DropShadow();
    edge.setOffsetY(0f);
    edge.setOffsetX(0f);
    edge.setColor(
            CreateGameController.getColors()
            .get(
                String.valueOf(ClientStorage.getMainClient().getCurrentTeamTurn())
                ).get()
            .darker().darker());
    edge.setWidth(7);
    edge.setHeight(7);
    edge.setSpread(5);
    edge.setRadius(6);
    edge.setBlurType(BlurType.ONE_PASS_BOX);
    
    DropShadow backgroundBlur = new DropShadow();
    backgroundBlur.setInput(edge);
    backgroundBlur.setColor(CreateGameController.getColors()
        .get(
            String.valueOf(ClientStorage.getMainClient().getCurrentTeamTurn())
            ).get()
        .brighter().brighter());
    backgroundBlur.setWidth(4);
    backgroundBlur.setHeight(4);
    backgroundBlur.setSpread(1);
    backgroundBlur.setRadius(2);
    
    return backgroundBlur;
  }
  
  /**
   * Returns a color-inverse
   * 
   * @author sistumpf
   * @param color to calculate the inverse of
   * @return the inverse of color
   */
  public static Color getComplementaryColor(Color color) {
    double red = 1.0 - color.getRed();
    double green = 1.0 - color.getGreen();
    double blue = 1.0 - color.getBlue();

    red = blue = green = 0;
    
    return new Color(red, green, blue, 0.5);
}
  
  /**
   * Creates the button which is used to give up
   *
   * @author sistumpf
   * @author Manuel Krakowski
   * @return Box containing only the give-up-button
   */
  private HBox createGiveUpBox() {
    HBox giveUpBox = new HBox();
    giveUpBox.prefHeightProperty().bind(right.heightProperty().multiply(0.05));
    giveUpBox.setAlignment(Pos.BOTTOM_RIGHT);
    giveUpButton = new Button("Give up");
    giveUpButton.prefWidthProperty().bind(giveUpBox.widthProperty().multiply(0.25));
    giveUpButton.setMinWidth(61);
    giveUpButton.getStyleClass().add("leave-button");
    giveUpButton.setOnAction(
        e -> {
          ArrayList<Client> allClients = new ArrayList<Client>();
          allClients.addAll(ClientStorage.getLocalHumanClients());
          allClients.addAll(ClientStorage.getLocalAIClients());
          for (Client client : allClients) {
            if (client.isItMyTurn()) {
              client.giveUp();
              ClientStorage.updateAllClients();
              updateUI(true);
              break;
            }
          }
        });
    giveUpBox.getChildren().add(giveUpButton);
    return giveUpBox;
  }


  // **************************************************
  // End of current team turn visualization methods
  // **************************************************

  // **************************************************
  // Start Color Chooser-Usage method
  // **************************************************

  /**
   * Opens a color-chooser window when a teams base is clicked the user can select its team color
   * there
   *
   * @author Manuel Krakowski
   * @param d: Mouse-click x-coordinate
   * @param e: mouse-click y-coordinate
   * @param r: Base which was clicked
   */
  public void showColorChooser(double d, double e, BaseRep r) {
    MyCustomColorPicker myCustomColorPicker = new MyCustomColorPicker();
    myCustomColorPicker.setCurrentColor(CreateGameController.getColors().get(r.getTeamID()).get());
    CustomMenuItem itemColor = new CustomMenuItem(myCustomColorPicker);
    itemColor.getStyleClass().add("custom-menu-item");
    itemColor.setHideOnClick(false);
    CreateGameController.getColors()
        .get(r.getTeamID())
        .bind(myCustomColorPicker.customColorProperty());
    for (CustomFigurePane p : gm.getFigures().values()) {
      if (p.getTeamID().equals(r.getTeamID())) {
        p.showTeamColorWhenSelecting(CreateGameController.getColors().get(r.getTeamID()));
      }
    }
    r.showColor(CreateGameController.getColors().get(r.getTeamID()));
    ContextMenu contextMenu = new ContextMenu(itemColor);
    contextMenu.setOnHiding(
        t -> {
          sceneColorProperty.unbind();
          for (CustomFigurePane m : gm.getFigures().values()) {
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
   *
   * @author Manuel Krakowski
   * @param movetimelimited true if the move time is limited, false otherwise
   * @param gametimeLimited true if the game time is limited, false otherwise
   * @return
   */
  private HBox createClockBox(boolean movetimelimited, boolean gametimeLimited) {
    HBox timerBox = new HBox();
    timerBox.setAlignment(Pos.CENTER);
    timerBox.prefWidthProperty().bind(right.widthProperty());
    timerBox
        .widthProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
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
   * Creates a not move-time-limited timer which is counting the time up using a custom {@link
   * Timer}
   *
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
    timerwithDescrip.setUserData("timer");
    return timerwithDescrip;
  }

  /**
   * Creates a move-time-limited timer which is using data from the server to refresh the time
   *
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
   *
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

  /**
   * updtaes the time with data from the client if move time or game time limits are set
   *
   * @author Manuel Krakowski
   * @author sistumpf
   */
  Runnable timeUpdateTask =
      () -> {
        try {
          Platform.runLater(
              () -> {
                if (ClientStorage.getMainClient().isGameMoveTimeLimited() && !ClientStorage.getMainClient().isGameOver()) {
                  moveTimeLimit.setText(formatTime(ClientStorage.getMainClient().getRemainingMoveTimeInSeconds()));
                  if (ClientStorage.getMainClient().getRemainingMoveTimeInSeconds() < 10) {
                    moveTimeLimit.setTextFill(Color.RED);
                  } else {
                    moveTimeLimit.setTextFill(Color.GOLD);
                  }
                }
                if (ClientStorage.getMainClient().isGameTimeLimited() && !ClientStorage.getMainClient().isGameOver()) {
                  gameTimeLimit.setText(formatTime(ClientStorage.getMainClient().getRemainingGameTimeInSeconds()));
                  if (ClientStorage.getMainClient().getRemainingGameTimeInSeconds() < 60) {
                    gameTimeLimit.setTextFill(Color.RED);
                  }
                }
              });
        } catch (Exception e) {
          e.printStackTrace();
        }
      };

  // **************************************************
  // End of timer methods
  // **************************************************

  // **************************************************
  // Start of figure description box creation methods
  // **************************************************

  /**
   * Creates a box in which information about a piece and a picture of it is shown
   *
   * @author Manuel Krakowski
   * @return piece-description-Hbox
   */
  private HBox createFigureDesBox() {
    HBox hBox = new HBox();
    hBox.setAlignment(Pos.CENTER);
    VBox vBox = new VBox();
    vBox.getStyleClass().add("option-pane");
    HBox pict = new HBox();
    typeLabel = new Label("-");
    typeLabel.fontProperty().bind(pictureMainDiscription);
    typeLabel.setAlignment(Pos.CENTER_LEFT);
    typeLabel.getStyleClass().add("figure-label");
    StackPane p = new StackPane();
    Image mp = ImageController.loadThemedImage(ImageType.MISC, "question-mark");
    figureImage = new Circle();
    figureImage.setFill(new ImagePattern(mp));
    figureImageBackground = new Circle();
    figureImageBackground.setFill(Color.WHITE);
    figureImageBackground.setStroke(Color.BLACK);
    figureImageBackground.setStrokeWidth(2);
    pict.getChildren().addAll(typeLabel, p);
    p.getChildren().addAll(figureImageBackground, figureImage);
    vBox.getChildren().add(pict);
    vBox.getChildren().add(createDeslabelBox());
    hBox.getChildren().add(vBox);
    
    hBox.prefHeightProperty().bind(this.heightProperty().multiply(0.65));
    hBox.prefWidthProperty().bind(right.widthProperty().multiply(0.8));
    double pad1 = hBox.widthProperty().doubleValue() * 0.05;
    hBox.setPadding(new Insets(pad1, 0, pad1, 0));
    hBox.widthProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              double padding = newValue.doubleValue() * 0.08;
              hBox.setPadding(new Insets(padding, padding, padding, padding));
            });
    double pad = vBox.widthProperty().doubleValue() * 0.05;
    vBox.setPadding(new Insets(pad, pad, pad, pad));
    vBox.widthProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              double padding = newValue.doubleValue() * 0.05;
              vBox.setPadding(new Insets(padding, padding, padding, padding));
            });

    pict.prefHeightProperty().bind(vBox.heightProperty().multiply(0.1));
    pict.prefWidthProperty().bind(right.widthProperty().multiply(0.75));
    p.prefWidthProperty().bind(pict.widthProperty().multiply(0.3));
    typeLabel.prefHeightProperty().bind(pict.heightProperty().multiply(0.8));
    typeLabel.prefWidthProperty().bind(pict.widthProperty().multiply(0.7));
    figureImage.radiusProperty().bind(pict.widthProperty().divide(6));
    figureImageBackground.radiusProperty().bind(pict.widthProperty().divide(6));
    return hBox;
  }

  /**
   * Creates A Label which is used to display piece-information
   * 
   * @author Manuel Krakowski
   * @return description label
   */
  private VBox createDeslabelBox() {
    VBox deBox = new VBox();

    deBox.setPrefWidth(1);
    deBox.setPrefHeight(1);

//    deBox
//        .heightProperty()
//        .addListener(
//            (observable, oldValue, newValue) -> {
//              double spacing = newValue.doubleValue() * 0.08;
//              deBox.setSpacing(spacing);
//            });
    deBox.setAlignment(Pos.CENTER_LEFT);
//    idLabel = new Label("id: -");
//    handleLabel(idLabel, deBox);
//    teamLabel = new Label("team: -");
//    handleLabel(teamLabel, deBox);
    attackPowLabel = new Label("attackpower: -");
    handleLabel(attackPowLabel);
    countLabel = new Label("count: - ");
    handleLabel(countLabel);
    pieceWalkPane = new PieceWalkPane(deBox);
    deBox.getChildren().addAll(attackPowLabel, countLabel, pieceWalkPane);

    attackPowLabel.prefWidthProperty().bind(deBox.widthProperty());
    countLabel.prefWidthProperty().bind(deBox.widthProperty());
    pieceWalkPane.prefWidthProperty().bind(deBox.widthProperty().divide(4));
    pieceWalkPane.prefHeightProperty().bind(deBox.widthProperty());
    
    return deBox;
  }

  /**
   * styles description label
   *
   * @author Manuel Krakowski
   * @param l label
   * @param parent used for relative resizing
   */
  private void handleLabel(Label l) {
    l.fontProperty().bind(figureDiscription);
    l.getStyleClass().add("figure-label");
  }

  // **************************************************
  // End of of figure description box creation methods
  // **************************************************

  // **************************************************
  // Start of Getters and Setters
  // **************************************************

  public static void setFigureBackground(Color color) {
    figureImageBackground.setFill(color);
  }
  
  public static void setFigureImage(Image img) {
    figureImage.setFill(new ImagePattern(img));
  }

  public static void setPieceWalkPane(Piece piece) {
    pieceWalkPane.update(piece);
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
