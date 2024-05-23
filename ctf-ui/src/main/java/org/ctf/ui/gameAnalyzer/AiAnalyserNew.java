package org.ctf.ui.gameAnalyzer;

import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.Arrays;
import org.ctf.shared.ai.AIConfig;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.AI;
import org.ctf.shared.constants.Enums.ImageType;
import org.ctf.shared.gameanalyzer.AnalyzedGameState;
import org.ctf.shared.gameanalyzer.GameAnalyzer;
import org.ctf.shared.gameanalyzer.GameSaveHandler;
import org.ctf.shared.gameanalyzer.NeedMoreTimeException;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Piece;
import org.ctf.ui.App;
import org.ctf.ui.StroeMaps;
import org.ctf.ui.controllers.HomeSceneController;
import org.ctf.ui.controllers.ImageController;
import org.ctf.ui.map.GamePane;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;



public class AiAnalyserNew extends Scene {

  // Controller which is used to switch to the play-game-scene
  private HomeSceneController hsc;

  // Containers and Labels which need to be accessed from different methods
  private StackPane root;
  private VBox leftBox;
  private StackPane showMapBox;
  private GamePane gm;
  private ScrollPane scroller;
  private VBox content;
  private double[] savedscrollvalues;

  private ObjectProperty<Font> popUpLabel;
  private ObjectProperty<Font> leaveButtonText;
  private ObjectProperty<Font> informUser;

  private ObjectProperty<Font> moveTableHeader;
  private ObjectProperty<Font> moveTableContent;
  SimpleObjectProperty<Insets> padding =
      new SimpleObjectProperty<>(new Insets(this.getWidth() * 0.01));
  
  private HBox[] rows;
  
  
  private Label[] teamLabels;
  private Label[] classificationlabels;
  private Double[] percentagesbyUser;
  private Double[] percentagesbyAI;
  private int[] expansions;
  private GameState[] userStates;
  private GameState[] aiStates;
  private String[] moveColors;
  private int[] heuristics;
  private int[] simulations;
  private VBox progressBar;
  private  int totalmoves;
  private int scrollBackIndicator;
  int currentMove;
  private AnalyzedGameState[] analysedGames;
  GameSaveHandler gsh;
  private boolean showHuman;



  public AiAnalyserNew(HomeSceneController hsc, double width, double height) {
    super(new StackPane(), width, height);
    createGameSaver();
    // totalmoves = 10;
    this.hsc = hsc;
    manageFontSizes();
    rows = new HBox[totalmoves];
    teamLabels = new Label[totalmoves];
    classificationlabels = new Label[totalmoves];
    percentagesbyUser = new Double[totalmoves];
    percentagesbyAI = new Double[totalmoves];
    userStates = new GameState[totalmoves];
    expansions = new int[totalmoves];
    simulations = new int[totalmoves];
    heuristics = new int[totalmoves];
    aiStates = new GameState[totalmoves];
    moveColors = new String[totalmoves];
    showHuman = false;

    scrollBackIndicator = 0;
    this.root = (StackPane) this.getRoot();
    currentMove = 0;
    savedscrollvalues = new double[totalmoves];
    try {
      this.getStylesheets()
          .add(Paths.get(Constants.toUIStyles + "MapEditor.css").toUri().toURL().toString());
      this.getStylesheets()
          .add(Paths.get(Constants.toUIStyles + "ComboBox.css").toUri().toURL().toString());
      this.getStylesheets()
          .add(Paths.get(Constants.toUIStyles + "color.css").toUri().toURL().toString());
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    popUpLabel = new SimpleObjectProperty<Font>(Font.font(this.getWidth() / 50));
    leaveButtonText = new SimpleObjectProperty<Font>(Font.font(this.getWidth() / 80));
    moveTableHeader = new SimpleObjectProperty<Font>(Font.font(this.getWidth() / 50));
    moveTableContent = new SimpleObjectProperty<Font>(Font.font(this.getWidth() / 60));
    informUser = new SimpleObjectProperty<Font>(Font.font(this.getWidth() / 30));

    createLayout();
    initalize();
  }
  
  private void createGameSaver() {
    gsh = new GameSaveHandler();
    gsh.readFile("20240523-065940");
    totalmoves = gsh.savedGame.getMoves().size();
  }

  private void initalize() {
    
    int currentMove = 0;
    try {
      GameAnalyzer analyzer = new GameAnalyzer(gsh.getSavedGame(), AI.MCTS, new AIConfig(), 10);
      analysedGames = analyzer.getResults();
      while (analyzer.isActive()|| (currentMove < analyzer.howManyMoves())) {
        if (currentMove != analyzer.getCurrentlyAnalyzing()) {
         
          AnalyzedGameState g = analyzer.getResults()[currentMove];
          g.printMe();
          GameState state = g.getPreviousGameState();
          teamLabels[currentMove].setText("Team:" + state.getCurrentTeam());
          classificationlabels[currentMove].setText(g.getMoveEvaluation().name());
          String col = g.getMoveEvaluation().getColor();
          classificationlabels[currentMove].setStyle("-fx-text-fill: " + col);
          moveColors[currentMove] = col;
          percentagesbyUser[currentMove] =Double.valueOf(g.getUserWinPercentage());
          percentagesbyAI[currentMove] = Double.valueOf(g.getAIWinPercentage());
          simulations[currentMove] = g.getSimulations();
          heuristics[currentMove] = g.getHeuristic();
          expansions[currentMove] = g.getExpansions();
         
          userStates[currentMove] = state;
          aiStates[currentMove] = g.getAiChoice();
          currentMove++;
        }
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }

    } catch (NeedMoreTimeException nmte) {
      System.err.println(
          "Error in " + getClass().getCanonicalName() + ":\n\t" + nmte.getLocalizedMessage());
    }
  }


  private void manageFontSizes() {
    widthProperty().addListener(new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth,
          Number newWidth) {
        popUpLabel.set(Font.font(newWidth.doubleValue() / 50));
        leaveButtonText.set(Font.font(newWidth.doubleValue() / 80));
        moveTableHeader.set(Font.font(newWidth.doubleValue() / 50));
        moveTableContent.set(Font.font(newWidth.doubleValue() / 60));
        informUser.set(Font.font(newWidth.doubleValue() / 30));

        padding.set(new Insets(newWidth.doubleValue() * 0.01));
      }
    });
  }

  /**
   * Creates the whole layout of the scene
   * 
   * @author Manuel Krakowski
   */
  private void createLayout() {
    root.getStyleClass().add("join-root");
    root.prefHeightProperty().bind(this.heightProperty());
    root.prefWidthProperty().bind(this.widthProperty());
    VBox mainVBox = createMainBox(root);
    mainVBox.getChildren().add(createHeader());
    HBox sep = createMiddleHBox(mainVBox);
    sep.getChildren().add(createProgressBar(sep));

    sep.getChildren().add(createMapBox(sep));
    sep.getChildren().add(createAllMovesVBox(sep));
    mainVBox.getChildren().add(sep);
    root.getChildren().add(mainVBox);
  }


  private VBox createProgressBar(HBox parent) {
    VBox progresscontainer = new VBox();
    progresscontainer.setAlignment(Pos.CENTER);
    progresscontainer.prefWidthProperty().bind(parent.widthProperty().multiply(0.1));
    progresscontainer.prefHeightProperty().bind(parent.heightProperty().multiply(0.85));
    progresscontainer.maxHeightProperty().bind(parent.heightProperty().multiply(0.85));
    progressBar = new VBox();
    progressBar.setPadding(new Insets(progressBar.getHeight() * 0.01));
    progressBar.widthProperty().addListener((observable, oldValue, newValue) -> {
      double newPadding = newValue.doubleValue() * 0.01;
      progressBar.setPadding(new Insets(newPadding, newPadding, newPadding, newPadding));
    });
    progressBar.getStyleClass().add("option-pane");
    // progressBar.setAlignment(Pos.BOTTOM_CENTER);
   Tooltip tooltip = new Tooltip("Expandierte Knoten:" + "\n" + "angewendete Heuristiken:" + "\n"
       + "Angewendete Simulationen:");
    tooltip.setStyle("-fx-background-color: blue");
    Duration delay = new Duration(1);
    tooltip.setShowDelay(delay);
    Duration displayTime = new Duration(10000);
    tooltip.setShowDuration(displayTime);
    tooltip.setFont(new Font(15));
    progressBar.setPickOnBounds(true);    Tooltip.install(progressBar, tooltip);
    progressBar.prefWidthProperty().bind(progresscontainer.widthProperty().divide(2));
    progressBar.maxWidthProperty().bind(progresscontainer.widthProperty().divide(2));
    progressBar.prefHeightProperty().bind(progresscontainer.heightProperty());
 progresscontainer.getChildren().add(progressBar);
    VBox progress = new VBox();
    progress.prefHeightProperty().bind(progressBar.heightProperty().multiply(1));
    progress.prefWidthProperty().bind(progressBar.widthProperty());
    progress.getStyleClass().add("progress-pane");
    Label l = new Label("100.0");    l.fontProperty().bind(moveTableContent);
    l.getStyleClass().add("vertical-label");
    progress.getChildren().add(l);
    progressBar.getChildren().add(progress);
    return progresscontainer;

  }
  
  private void setNewProgress() {
    progressBar.getChildren().clear();
    VBox progress = new VBox();
    progress.prefHeightProperty().bind(progressBar.heightProperty().multiply(percentagesbyUser[currentMove]/100));
    progress.prefWidthProperty().bind(progressBar.widthProperty());
    progress.getStyleClass().add("progress-pane");
//    progress.setStyle(" -fx-background-color: " + userStates[currentMove].getTeams()[userStates[currentMove].getCurrentTeam()].getColor()+ "; \r\n"
//        + "     -fx-background-radius: 20px; \r\n"
//        + "     -fx-border-radius: 20px;\r\n"
//        + "     -fx-alignment: center;");
    Label l = new Label(String.valueOf(percentagesbyUser[currentMove])+ "%");
    l.fontProperty().bind(moveTableContent);
    l.getStyleClass().add("vertical-label");
    progress.getChildren().add(l);
    progressBar.getChildren().add(progress);
  }
  
  private void setNewToolTip() {
    Tooltip tooltip = new Tooltip("Expandierte Knoten: " + expansions[currentMove] + "\n" + "angewendete Heuristiken: " + heuristics[currentMove]+ "\n"
        +  "Angewendete Simulationen: " + simulations[currentMove]);
    tooltip.setStyle("-fx-background-color: blue");
    Duration delay = new Duration(1);
    tooltip.setShowDelay(delay);
    Duration displayTime = new Duration(10000);
    tooltip.setShowDuration(displayTime);
    tooltip.setFont(new Font(15));
    progressBar.setPickOnBounds(true);
    Tooltip.install(progressBar, tooltip);
  }
  
  private void setNewProgressAi() {
    progressBar.getChildren().clear();
    VBox progress = new VBox();
    progress.prefHeightProperty().bind(progressBar.heightProperty().multiply(percentagesbyAI[currentMove]/100));
    progress.prefWidthProperty().bind(progressBar.widthProperty());
    progress.getStyleClass().add("progress-pane");
//    progress.setStyle(" -fx-background-color: " + moveColors[currentMove] + "; \r\n"
//        + "     -fx-background-radius: 20px; \r\n"
//        + "     -fx-border-radius: 20px;\r\n"
//        + "     -fx-alignment: center;");
    Label l = new Label(String.valueOf(percentagesbyAI[currentMove])+ "%");
    l.fontProperty().bind(moveTableContent);
    l.getStyleClass().add("vertical-label");
    progress.getChildren().add(l);
    progressBar.getChildren().add(progress);
  }
  
  

  /**
   * Creates a Vbox which is used to devide the Scene into two patrs, one for the header and one for
   * the content
   * 
   * @author Manuel Krakowski
   * @param parent: Stackpane in which the Vbox is placed for relative resizing
   * @return Vbox
   */
  private VBox createMainBox(StackPane parent) {
    VBox mainBox = new VBox();
    mainBox.prefHeightProperty().bind(parent.heightProperty());
    mainBox.prefWidthProperty().bind(parent.widthProperty());
    mainBox.setAlignment(Pos.TOP_CENTER);
    mainBox.setSpacing(30);
    mainBox.widthProperty().addListener((observable, oldValue, newValue) -> {
      double newSpacing = newValue.doubleValue() * 0.02;
      // double newPadding = newValue.doubleValue()*0.04;
      mainBox.setSpacing(newSpacing);
      // mainBox.setPadding(new Insets(0,0, newPadding, 0));
    });
    return mainBox;
  }

  /**
   * Creates the upper part of the scene which includes just one Image with the Text: 'Lobby'
   * 
   * @author Manuel Krakowski
   * @return ImageView containing the word 'Lobby'
   */
  private ImageView createHeader() {
    Image mp = ImageController.loadThemedImage(ImageType.MISC, "GameAnalyzerHeader");
    ImageView mpv = new ImageView(mp);
    mpv.fitHeightProperty().bind(root.heightProperty().multiply(0.1));
    mpv.fitWidthProperty().bind(root.widthProperty().multiply(0.7));
    mpv.setPreserveRatio(true);
    return mpv;
  }


  /**
   * Creates a HBox which devides the middle part of the screen into two pats vertically
   * 
   * @author Manuel Krakowski
   * @param parent: main Vbox in which it is placed used for relaive resizing
   * @return seperator-Hbox
   */
  private HBox createMiddleHBox(VBox parent) {
    HBox sep = new HBox();
    sep.prefHeightProperty().bind(parent.heightProperty().multiply(0.85));
    sep.prefWidthProperty().bind(parent.widthProperty());
    sep.setAlignment(Pos.TOP_CENTER);
    sep.widthProperty().addListener((observable, oldValue, newValue) -> {
      double newSpacing = newValue.doubleValue() * 0.03;
      sep.setSpacing(newSpacing);
    });
    return sep;
  }

  private VBox createMapBox(HBox parent) {
    VBox mapBox = new VBox();
    mapBox.prefHeightProperty().bind(parent.heightProperty());
    mapBox.prefWidthProperty().bind(parent.widthProperty().multiply(0.5));
    // mapBox.setStyle("-fx-background-color: blue");
    mapBox.heightProperty().addListener((observable, oldValue, newValue) -> {
      double newSpacing = newValue.doubleValue() * 0.04;
      mapBox.setSpacing(newSpacing);
    });
    mapBox.getChildren().add(createShowMapPane("p1", mapBox));

    mapBox.getChildren().add(createControlMapBox(mapBox));
    return mapBox;
  }

  private HBox createControlMapBox(VBox parent) {
    HBox h = new HBox();
    h.prefHeightProperty().bind(parent.heightProperty().multiply(0.1));
    h.setAlignment(Pos.CENTER);
    h.prefWidthProperty().bind(parent.widthProperty());
    h.widthProperty().addListener((observable, oldValue, newValue) -> {
      double newSpacing = newValue.doubleValue() * 0.04;
      h.setSpacing(newSpacing);
    });
    Button b = new Button();
    b.prefHeightProperty().bind(h.heightProperty().multiply(1));
    b.prefWidthProperty().bind(h.widthProperty().divide(10));
    b.setOnAction(e -> {
      perfromNextClick();
    });
    b.getStyleClass().add("triangle-button");
    b.fontProperty().bind(leaveButtonText);
    Button rec = new Button("Show AI's Choice");
    rec.prefHeightProperty().bind(h.heightProperty().multiply(1));
    rec.setOnAction(e -> {
      performAiButtonClick(rec);
    });
    rec.prefWidthProperty().bind(h.widthProperty().divide(4));
    rec.getStyleClass().add("rectangle-button");
    rec.fontProperty().bind(leaveButtonText);
    Button leftRec = new Button("");
    leftRec.setOnAction(e -> {
      perfomBackClick();
    });
    leftRec.prefHeightProperty().bind(h.heightProperty().multiply(1));
    leftRec.prefWidthProperty().bind(h.widthProperty().divide(10));
    leftRec.getStyleClass().add("triangle-button-left");
    leftRec.fontProperty().bind(leaveButtonText);
    h.getChildren().addAll(leftRec, rec, b);
    return h;
  }
  
  private void performAiButtonClick(Button aiButton) {
    if(currentMove >=1) {
    if(!showHuman) {
    setNewAiState();
    setNewProgressAi();
    aiButton.setText("Show Your Choice");
    showHuman = true;
    }else {
      setNewGameState();
      setNewProgress();
      aiButton.setText("Show AI's Choice");
      showHuman = false;
    }
    }
    
    
  }

  private void perfomBackClick() {
    if (currentMove > 1) {
      
      rows[currentMove].getStyleClass().clear();
      rows[--currentMove].getStyleClass().add("blue-glow-hbox");
      System.out.println(currentMove);
      setNewProgress();
      setNewToolTip();
      setNewGameState();
      if ((currentMove % 5 == 0)) {
        // scroller.setVvalue(savedscrollvalues[scrollBackIndicator--]);
      }
    }

  }

  private void perfromNextClick() {
    if (currentMove < totalmoves - 1) {
     
      rows[currentMove].getStyleClass().clear();
      rows[++currentMove].getStyleClass().add("blue-glow-hbox");
      System.out.println(currentMove);
      setNewProgress();
      setNewToolTip();
      setNewGameState();
      if ((currentMove % 5 == 0)) {
        scrollToLabel(scroller, content, content.getChildren().get(currentMove - 5));
      }
    }

  }

  private void perfromShowAiBestMove() {

  }

  private StackPane createShowMapPane(String name, VBox parent) {
    showMapBox = new StackPane();
    showMapBox.getStyleClass().add("option-pane");
    showMapBox.prefWidthProperty().bind(parent.widthProperty());
    showMapBox.prefHeightProperty().bind(parent.heightProperty().multiply(0.85));
    // showMapBox.maxWidthProperty().bind(App.getStage().widthProperty().multiply(0.45));
    // showMapBox.maxHeightProperty().bind(App.getStage().heightProperty().multiply(0.65));
    // showMapBox.getStyleClass().add("show-GamePane");
    showMapBox.paddingProperty().bind(padding);
    Label l = new Label("Click the Next-Button" + "\n" + "to start the Analysis");
    l.fontProperty().bind(informUser);
    l.setAlignment(Pos.CENTER);
    l.prefWidthProperty().bind(showMapBox.widthProperty());
    l.prefHeightProperty().bind(showMapBox.heightProperty());
    showMapBox.getChildren().add(l);
//    GameState state = StroeMaps.getMap(name);
//    gm = new GamePane(state, true,"");
//    StackPane.setAlignment(gm, Pos.CENTER);
//    gm.maxWidthProperty().bind(App.getStage().widthProperty().multiply(0.4));
//    gm.maxHeightProperty().bind(App.getStage().heightProperty().multiply(0.6));
//    showMapBox.getChildren().add(gm);
    return showMapBox;
  }
  
  private void setNewGameState() {
    showMapBox.getChildren().clear();
    GameState statebefore = userStates[currentMove-1];
   Move m = userStates[currentMove].getLastMove();
    Piece p = 
        Arrays.stream(
            statebefore.getTeams()[Integer.parseInt(m.getPieceId().split(":")[1].split("_")[0])]
                .getPieces())
        .filter(pe -> pe.getId().equals(m.getPieceId()))
        .findFirst()
        .get();
    
    gm = new GamePane(userStates[currentMove], true,moveColors[currentMove]);
    gm.setOldPosinAnalyzer(p.getPosition());
    StackPane.setAlignment(gm, Pos.CENTER);
    gm.maxWidthProperty().bind(App.getStage().widthProperty().multiply(0.4));
    gm.maxHeightProperty().bind(App.getStage().heightProperty().multiply(0.6));
    showMapBox.getChildren().add(gm);
  }
  
  private void setNewAiState() {
    showMapBox.getChildren().clear();
    GameState statebefore = aiStates[currentMove-1];
   Move m = aiStates[currentMove].getLastMove();
    Piece p = 
        Arrays.stream(
            statebefore.getTeams()[Integer.parseInt(m.getPieceId().split(":")[1].split("_")[0])]
                .getPieces())
        .filter(pe -> pe.getId().equals(m.getPieceId()))
        .findFirst()
        .get();
    gm = new GamePane(aiStates[currentMove], true,moveColors[currentMove]);
    gm.setOldPosinAnalyzer(p.getPosition());
    StackPane.setAlignment(gm, Pos.CENTER);
    gm.maxWidthProperty().bind(App.getStage().widthProperty().multiply(0.4));
    gm.maxHeightProperty().bind(App.getStage().heightProperty().multiply(0.6));
    showMapBox.getChildren().add(gm);
  }

 

  /**
   * Creates the right side of the screen containing a header and a scrollPane with all moves
   * 
   * @author Manuel Krakowski
   * @param parent: used for relative resizing
   * @return
   */
  private VBox createAllMovesVBox(HBox parent) {
    leftBox = new VBox();
    leftBox.setAlignment(Pos.TOP_CENTER);
    leftBox.prefWidthProperty().bind(parent.widthProperty().multiply(0.3));
    leftBox.prefHeightProperty().bind(parent.heightProperty().multiply(0.85));
    leftBox.maxHeightProperty().bind(parent.heightProperty().multiply(0.85));

    // leftBox.setStyle("-fx-background-color: green");
    leftBox.heightProperty().addListener((observable, oldValue, newValue) -> {
      double newSpacing = newValue.doubleValue() * 0.03;
      leftBox.setSpacing(newSpacing);

    });
    leftBox.getChildren().add(createHeaderLabel("Moves", leftBox));
    leftBox.getChildren().add(createScrollPane(leftBox));

    return leftBox;
  }

  /**
   * Creates a header-label for the table
   * 
   * @param : text of the label
   * @param h: parent used for relative resizing
   * @return header-label
   */
  private Label createHeaderLabel(String text, VBox parent) {
    Label l = new Label(text);
    l.setTextFill(Color.GOLD);
    // l.getStyleClass().add("lobby-header-label");
    l.setAlignment(Pos.CENTER);
    l.prefWidthProperty().bind(parent.widthProperty());
    l.fontProperty().bind(moveTableHeader);
    return l;
  }

  /**
   * Creates the Content of the table with all the players currently in the waiting room
   * 
   * @author Manuel Krakowski
   * @param parent: used for relative resizing
   * @return Scrollpane with current players
   */
  private ScrollPane createScrollPane(VBox parent) {
    scroller = new ScrollPane();
    scroller.getStyleClass().clear();

    // scroller.setStyle("-fx-background-color: grey");
    scroller.prefWidthProperty().bind(parent.widthProperty());
    scroller.prefHeightProperty().bind(parent.heightProperty().multiply(0.93));
    scroller.setHbarPolicy(ScrollBarPolicy.NEVER);
    content = new VBox();

    content.prefWidthProperty().bind(scroller.widthProperty());
    content.prefHeightProperty().bind(scroller.heightProperty());
    content.setAlignment(Pos.TOP_CENTER);
    for (int i = 0; i < totalmoves; i++) {
      content.getChildren().add(createOneRow(content, i));
    }
    scroller.setContent(content);

    return scroller;
  }

  private HBox createOneRow(VBox parent, int moveNr) {
    HBox oneRow = new HBox();
    oneRow.prefWidthProperty().bind(parent.widthProperty());
    Label moveNrLabel = createNormalLabel(oneRow, moveNr);
    Label teamLabel = createTeamLabel(oneRow, moveNr);
    teamLabels[moveNr] = teamLabel;
    Label moveLabel = createMoveClassificationLabel(oneRow, moveNr, "");
    classificationlabels[moveNr] = moveLabel;
    oneRow.getChildren().addAll(moveNrLabel, teamLabel, moveLabel);
    rows[moveNr] = oneRow;
    return oneRow;
  }



  private void scrollToLabel(ScrollPane scrollPane, VBox vbox, javafx.scene.Node label) {
    Bounds viewportBounds = scrollPane.getViewportBounds();
    Bounds contentBounds = label.getBoundsInParent();

    double viewportHeight = viewportBounds.getHeight();
    double contentHeight = vbox.getHeight();

    double scrollOffset = contentBounds.getMinY() / (contentHeight - viewportHeight);
    scrollPane.setVvalue(scrollOffset);
  }

  /**
   * Creates a normal label to display the content in the table
   * 
   * @author Manuel Krakowski
   * @param text: String that is displayed by the label
   * @param h: parent used for relative resizing
   * @param i: number of the team the label belong to
   * @return: Label
   */
  private Label createNormalLabel(HBox h, int i) {
    Label l = new Label(String.valueOf(i));
    l.setAlignment(Pos.CENTER);
    if ((i % 2) == 0) {
      l.getStyleClass().add("lobby-normal-label");
    } else {
      l.getStyleClass().add("lobby-normal-label-2");
    }

    l.prefWidthProperty().bind(h.widthProperty().multiply(0.2));
    // l.setStyle("-fx-border-color:black");
    l.fontProperty().bind(moveTableContent);
    return l;
  }

  /**
   * Creates a normal label to display the content in the table
   * 
   * @author Manuel Krakowski
   * @param text: String that is displayed by the label
   * @param h: parent used for relative resizing
   * @param i: number of the team the label belong to
   * @return: Label
   */
  private Label createTeamLabel(HBox h, int i) {
    Label l = new Label("?");
    l.setAlignment(Pos.CENTER);
    if ((i % 2) == 0) {
      l.getStyleClass().add("lobby-normal-label");
    } else {
      l.getStyleClass().add("lobby-normal-label-2");
    }
    l.prefWidthProperty().bind(h.widthProperty().multiply(0.3));
    l.fontProperty().bind(moveTableContent);
    return l;
  }

  private Label createMoveClassificationLabel(HBox h, int i, String s) {
    Label l = new Label("?");
    l.setAlignment(Pos.CENTER);
    if ((i % 2) == 0) {
      l.getStyleClass().add("lobby-normal-label");
    } else {
      l.getStyleClass().add("lobby-normal-label-2");
    }
    l.prefWidthProperty().bind(h.widthProperty().multiply(0.5));
    l.fontProperty().bind(moveTableContent);
    return l;
  }
}

