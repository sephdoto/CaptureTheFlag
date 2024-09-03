package org.ctf.ui.gameAnalyzer;

import org.ctf.shared.constants.Enums.ImageType;
import org.ctf.ui.controllers.ImageController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * Main Layout creating Methods for {@link AiAnalyzerScene} are located here.
 * 
 * @author sistumpf
 */
public class AnalyzerUtils extends AnalyzerExtra {
  public AnalyzerUtils(AiAnalyzerScene scene) {
    super(scene);
  }
  
  @SuppressWarnings("incomplete-switch")
  protected void addKeyListeners() {
    scene.addEventFilter(KeyEvent.KEY_PRESSED, (e) -> {
      switch(e.getCode()) {
        case W, UP, KP_UP: 
          focusUs(scene.humanOrAiButton);
          if(!scene.showHuman) performAiButtonClick(scene.humanOrAiButton);
          break;
        case S, DOWN, KP_DOWN: 
          focusUs(scene.humanOrAiButton);
          if(scene.showHuman) performAiButtonClick(scene.humanOrAiButton);
          break;
        case A, LEFT, KP_LEFT: 
          focusUs(scene.back);
          performBackClick(scene.humanOrAiButton);
          break;
        case D, RIGHT, KP_RIGHT:
          focusUs(scene.next);
          performNextClick(scene.humanOrAiButton);
          break;
      }
    });
  }
  
  void focusUs(Button button) {
    button.requestFocus();
  }
  
  /**
   * Scrolls to the new Move in case the user is looking at his newest user move.
   * If thats not the case the user is analyzing older moves or the best AI choice, making scrolling a distraction.
   * 
   * @author sistumpf
   * @param newMove new move index
   */
  protected void tryScrolling(int newMove) {
    if(newMove == scene.currentMove + 1 && !scene.showHuman) {
      performNextClick(new Button(""));
    }
  }

  /**
   * Makes the "Moves" HBoxes clickable
   * 
   * @author sistumpf
   */
  protected void makeClickable() {
    for(Node move : scene.content.getChildren()) {
      ((HBox) move).setOnMouseClicked(e -> {
        int clickedOn = Integer.parseInt(((Label)((HBox) move).getChildren().get(0)).getText());
        if(isClickable(clickedOn - scene.currentMove)) {
          if(scene.currentMove >= 0)  scene.rows[scene.currentMove].getStyleClass().clear();
          scene.currentMove = clickedOn -1;
          performNextClick(new Button(""));
        }
      });
    }
  }
  
  /**
   * Creates a custom progress-bar which shows how good the move of the user was in %
   * 
   * @author Manuel Krakowski
   * @author sistumpf
   * @param parent used for relative resizing
   * @return progress-bar
   */
  public VBox createProgressBar(HBox parent) {
    VBox progresscontainer = new VBox();
    progresscontainer.setAlignment(Pos.CENTER);
    progresscontainer.prefWidthProperty().bind(parent.widthProperty().multiply(0.1));
    progresscontainer.prefHeightProperty().bind(parent.heightProperty().multiply(0.85));
    progresscontainer.maxHeightProperty().bind(parent.heightProperty().multiply(0.85));
    scene.progressBar = new VBox();
    scene.progressBar.setPadding(new Insets(scene.progressBar.getHeight() * 0.01));
    scene.progressBar.widthProperty().addListener((observable, oldValue, newValue) -> {
      double newPadding = newValue.doubleValue() * 0.01;
      scene.progressBar.setPadding(new Insets(newPadding, newPadding, newPadding, newPadding));
    });
    scene.progressBar.getStyleClass().add("option-pane");
    // scene.progressBar.setAlignment(Pos.BOTTOM_CENTER);
    Tooltip tooltip = new Tooltip("Expandierte Knoten:" + "\n" + "angewendete Heuristiken:" + "\n"
        + "Angewendete Simulationen:");
    tooltip.setStyle("-fx-background-color: blue");
    Duration delay = new Duration(1);
    tooltip.setShowDelay(delay);
    Duration displayTime = new Duration(10000);
    tooltip.setShowDuration(displayTime);
    tooltip.setFont(new Font(15));
    scene.progressBar.setPickOnBounds(true);
    Tooltip.install(scene.progressBar, tooltip);
    scene.progressBar.prefWidthProperty().bind(progresscontainer.widthProperty().divide(2));
    scene.progressBar.maxWidthProperty().bind(progresscontainer.widthProperty().divide(2));
    scene.progressBar.prefHeightProperty().bind(progresscontainer.heightProperty());
    progresscontainer.getChildren().add(scene.progressBar);
    VBox progress = new VBox();
    progress.prefHeightProperty().bind(scene.progressBar.heightProperty().multiply(1));
    progress.prefWidthProperty().bind(scene.progressBar.widthProperty());
    progress.getStyleClass().add("progress-pane");
    Label l = new Label("100.0");
    l.fontProperty().bind(scene.moveTableContent);
    l.getStyleClass().add("vertical-label");
    progress.getChildren().add(l);
    scene.progressBar.getChildren().add(progress);
    return progresscontainer;
  }

  /**
   * Creates a Vbox which is used to devide the Scene into two patrs, one for the header and one for
   * the content
   * 
   * @author Manuel Krakowski
   * @param parent Stackpane in which the Vbox is placed for relative resizing
   * @return Vbox
   */
  protected VBox createMainBox(StackPane parent) {
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
   * Creates the upper part of the scene which includes just one Image with the Text:
   * 'Game-Analyzer'
   * 
   * @author Manuel Krakowski
   * @return ImageView containing the word 'Game-Analyzer'
   */
  protected ImageView createHeader() {
    Image mp = ImageController.loadThemedImage(ImageType.MISC, "GameAnalyzerHeader");
    ImageView mpv = new ImageView(mp);
    mpv.fitHeightProperty().bind(scene.root.heightProperty().multiply(0.1));
    mpv.fitWidthProperty().bind(scene.root.widthProperty().multiply(0.7));
    mpv.setPreserveRatio(true);
    return mpv;
  }

  /**
   * Creates a HBox which devides the middle part of the screen into two pats vertically
   * 
   * @author Manuel Krakowski
   * @param parent main Vbox in which it is placed used for relaive resizing
   * @return seperator-Hbox
   */
  protected HBox createMiddleHBox(VBox parent) {
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

  /**
   * Box in which the map is shwon
   * 
   * @author Manuel Krakowski
   * @param parent used for relative resizing
   * @return map-box
   */
  protected VBox createMapBox(HBox parent) {
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

  /**
   * Creates the box to control which move is currently shown on the map
   * 
   * @author Manuel Krakowski
   * @param parent used for relative resizing
   * @return
   */
  protected HBox createControlMapBox(VBox parent) {
    HBox h = new HBox();
    h.prefHeightProperty().bind(parent.heightProperty().multiply(0.1));
    h.setAlignment(Pos.CENTER);
    h.prefWidthProperty().bind(parent.widthProperty());
    h.widthProperty().addListener((observable, oldValue, newValue) -> {
      double newSpacing = newValue.doubleValue() * 0.04;
      h.setSpacing(newSpacing);
    });
    scene.next = new Button();
    scene.next.prefHeightProperty().bind(h.heightProperty().multiply(1));
    scene.next.prefWidthProperty().bind(h.widthProperty().divide(10));

    scene.next.getStyleClass().add("triangle-button");
    scene.next.fontProperty().bind(scene.leaveButtonText);
    scene.humanOrAiButton = new Button("Show AI's Choice");
    scene.humanOrAiButton.prefHeightProperty().bind(h.heightProperty().multiply(1));
    scene.humanOrAiButton.setOnAction(e -> {
      performAiButtonClick(scene.humanOrAiButton);
    });
    scene.next.setOnAction(e -> {
      performNextClick(scene.humanOrAiButton);
    });
    scene.humanOrAiButton.prefWidthProperty().bind(h.widthProperty().divide(4));
    scene.humanOrAiButton.getStyleClass().add("rectangle-button");
    scene.humanOrAiButton.fontProperty().bind(scene.leaveButtonText);
    scene.back = new Button("");
    scene.back.setOnAction(e -> {
      performBackClick(scene.humanOrAiButton);
    });
    scene.back.prefHeightProperty().bind(h.heightProperty().multiply(1));
    scene.back.prefWidthProperty().bind(h.widthProperty().divide(10));
    scene.back.getStyleClass().add("triangle-button-left");
    scene.back.fontProperty().bind(scene.leaveButtonText);
    h.getChildren().addAll(scene.back, scene.humanOrAiButton, scene.next);
    return h;
  }

  /**
   * Creates a Stackpane in which the map is shown
   * 
   * @author Manuel Krakowski
   * @param name
   * @param parent
   * @return
   */
  protected StackPane createShowMapPane(String name, VBox parent) {
    scene.showMapBox = new StackPane();
    scene.showMapBox.getStyleClass().add("option-pane");
    scene.showMapBox.prefWidthProperty().bind(parent.widthProperty());
    scene.showMapBox.prefHeightProperty().bind(parent.heightProperty().multiply(0.85));
    // showMapBox.maxWidthProperty().bind(App.getStage().widthProperty().multiply(0.45));
    // showMapBox.maxHeightProperty().bind(App.getStage().heightProperty().multiply(0.65));
    // showMapBox.getStyleClass().add("show-GamePane");
    scene.showMapBox.paddingProperty().bind(scene.padding);
    scene.firstMessage = new Label("Click the Next-Button" + "\n" + "to start the Analysis");
    scene.firstMessage.fontProperty().bind(scene.informUser);
    scene.firstMessage.setAlignment(Pos.CENTER);
    scene.firstMessage.prefWidthProperty().bind(scene.showMapBox.widthProperty());
    scene.firstMessage.prefHeightProperty().bind(scene.showMapBox.heightProperty());
    scene.showMapBox.getChildren().add(scene.firstMessage);
    return scene.showMapBox;
  }

  /**
   * Creates the right side of the screen containing a header and a scrollPane with all moves
   * 
   * @author Manuel Krakowski
   * @param parent used for relative resizing
   * @return
   */
  protected VBox createAllMovesVBox(HBox parent) {
    scene.leftBox = new VBox();
    scene.leftBox.setAlignment(Pos.TOP_CENTER);
    scene.leftBox.prefWidthProperty().bind(parent.widthProperty().multiply(0.3));
    scene.leftBox.prefHeightProperty().bind(parent.heightProperty().multiply(0.85));
    scene.leftBox.maxHeightProperty().bind(parent.heightProperty().multiply(0.85));

    // leftBox.setStyle("-fx-background-color: green");
    scene.leftBox.heightProperty().addListener((observable, oldValue, newValue) -> {
      double newSpacing = newValue.doubleValue() * 0.03;
      scene.leftBox.setSpacing(newSpacing);
    });
    scene.leftBox.getChildren().add(createHeaderLabel("Moves", scene.leftBox));
    scene.leftBox.getChildren().add(createScrollPane(scene.leftBox));

    return scene.leftBox;
  }

  /**
   * Creates a header-label for the table
   * 
   * @param text text of the label
   * @param h parent used for relative resizing
   * @return header-label
   */
  protected Label createHeaderLabel(String text, VBox parent) {
    Label l = new Label(text);
    l.setTextFill(Color.GOLD);
    // l.getStyleClass().add("lobby-header-label");
    l.setAlignment(Pos.CENTER);
    l.prefWidthProperty().bind(parent.widthProperty());
    l.fontProperty().bind(scene.moveTableHeader);
    return l;
  }

  /**
   * Creates the Content of the table with all the players currently in the waiting room
   * 
   * @author Manuel Krakowski
   * @param parent  used for relative resizing
   * @return Scrollpane with current players
   */
  protected ScrollPane createScrollPane(VBox parent) {
    scene.scroller = new ScrollPane();
    scene.scroller.getStyleClass().clear();

    // scene.scroller.setStyle("-fx-background-color: grey");
    scene.scroller.prefWidthProperty().bind(parent.widthProperty());
    scene.scroller.prefHeightProperty().bind(parent.heightProperty().multiply(0.93));
    scene.scroller.setHbarPolicy(ScrollBarPolicy.NEVER);
    scene.content = new VBox();

    scene.content.prefWidthProperty().bind(scene.scroller.widthProperty());
    scene.content.prefHeightProperty().bind(scene.scroller.heightProperty());
    scene.content.setAlignment(Pos.TOP_CENTER);
    for (int i = 0; i < scene.totalmoves; i++) {
      scene.content.getChildren().add(createOneRow(scene.content, i));
    }
    scene.scroller.setContent(scene.content);

    return scene.scroller;
  }

  /**
   * Creates one row containg one move
   * 
   * @author Manuel Krakowski
   * @param parent
   * @param moveNr
   * @return
   */
  protected HBox createOneRow(VBox parent, int moveNr) {
    HBox oneRow = new HBox();
    oneRow.prefWidthProperty().bind(parent.widthProperty());
    Label moveNrLabel = createNormalLabel(oneRow, moveNr);
    HBox teamLabel = new HBox();
    teamLabel.setAlignment(Pos.CENTER);
    scene.teamLabels[moveNr] = createTeamLabel(oneRow, moveNr, teamLabel);
    Label moveLabel = createMoveClassificationLabel(oneRow, moveNr, "");
    scene.classificationlabels[moveNr] = moveLabel;
    oneRow.getChildren().addAll(moveNrLabel, teamLabel, moveLabel);
    scene.rows[moveNr] = oneRow;
    return oneRow;
  }

  /**
   * Creates a normal label to display the content in the table
   * 
   * @author Manuel Krakowski
   * @param text String that is displayed by the label
   * @param h parent used for relative resizing
   * @param i number of the team the label belong to
   * @return Label
   */
  protected Label createNormalLabel(HBox h, int i) {
    Label l = new Label(String.valueOf(i));
    l.setAlignment(Pos.CENTER);
    if ((i % 2) == 0) {
      l.getStyleClass().add("lobby-normal-label");
    } else {
      l.getStyleClass().add("lobby-normal-label-2");
    }

    l.prefWidthProperty().bind(h.widthProperty().multiply(0.2));
    // l.setStyle("-fx-border-color:black");
    l.fontProperty().bind(scene.moveTableContent);
    return l;
  }

  /**
   * Creates a normal label to display the content in the table
   * 
   * @author Manuel Krakowski
   * @param text String that is displayed by the label
   * @param h parent used for relative resizing
   * @param i number of the team the label belong to
   * @param teamLabel a HBox to put prefix and teamName into
   * @return Label
   */
  protected Label createTeamLabel(HBox h, int i, HBox teamLabel) {
    Label teamName = new Label(scene.gsh.getSavedGame().getMoves().get(1+i + "").getTeamId());
    Label prefix = new Label("Team: ");
    
//    l.setAlignment(Pos.CENTER);
    
    if ((i % 2) == 0) {
      teamLabel.getStyleClass().add("lobby-normal-label");
      prefix.getStyleClass().add("lobby-normal-label");
      teamName.getStyleClass().add("lobby-normal-label");
    } else {
      teamLabel.getStyleClass().add("lobby-normal-label-2");
      prefix.getStyleClass().add("lobby-normal-label-2");
      teamName.getStyleClass().add("lobby-normal-label-2");
    }
    String color = "red";
    
    try {
      color = scene.gsh.getSavedGame().getInitialState().getTeams()[Integer.parseInt(teamName.getText())].getColor();
    } catch(Exception e) {};
    
    prefix.setStyle("-fx-text-fill: white;");
    teamName.setStyle("-fx-text-fill: #"+ color.substring(2) +";");
    teamLabel.prefWidthProperty().bind(h.widthProperty().multiply(0.3));
    teamName.fontProperty().bind(scene.moveTableContent);
    prefix.fontProperty().bind(scene.moveTableContent);
    teamLabel.getChildren().addAll(prefix, teamName);
    return teamName;
  }

  /**
   * Creates a Label to classificate a move
   * 
   * @author Manuel Krakowski
   * @param h
   * @param i
   * @param s
   * @return
   */
  protected Label createMoveClassificationLabel(HBox h, int i, String s) {
    Label l = new Label("?");
    l.setAlignment(Pos.CENTER);
    if ((i % 2) == 0) {
      l.getStyleClass().add("lobby-normal-label");
    } else {
      l.getStyleClass().add("lobby-normal-label-2");
    }
    l.prefWidthProperty().bind(h.widthProperty().multiply(0.5));
    l.fontProperty().bind(scene.moveTableContent);
    return l;
  }
}
