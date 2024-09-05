package org.ctf.ui.gameAnalyzer;

import java.io.File;
import java.util.ArrayList;
import org.ctf.shared.ai.AIConfig;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.gameanalyzer.AnalyzedGameState;
import org.ctf.shared.gameanalyzer.GameAnalyzer;
import org.ctf.shared.gameanalyzer.GameSaveHandler;
import org.ctf.shared.gameanalyzer.NeedMoreTimeException;
import org.ctf.shared.state.GameState;
import org.ctf.ui.map.GamePane;
import javafx.application.Platform;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;


/**
 * Analyzed a saved Game and classifies the users moves by comparing them to the moves of an AI
 * 
 * @author Manuel Krakowski
 */
public class AiAnalyzerScene extends Scene {
  // Containers and Labels which need to be accessed from different methods
  protected StackPane root;
  protected VBox leftBox;
  protected StackPane showMapBox;
  protected GamePane gm;
  protected ScrollPane scroller;
  protected VBox content;
  /**
   * The first thing a user sees, when no Move has been analyzed yet.
   */
  protected Label firstMessage;

  protected Button humanOrAiButton;
  protected Button back;
  protected Button next;
  
  protected ObjectProperty<Font> popUpLabel;
  protected ObjectProperty<Font> leaveButtonText;
  protected ObjectProperty<Font> informUser;

  protected ObjectProperty<Font> moveTableHeader;
  protected ObjectProperty<Font> moveTableContent;
  SimpleObjectProperty<Insets> padding =
      new SimpleObjectProperty<>(new Insets(this.getWidth() * 0.01));

  protected HBox[] rows;

  protected ArrayList<Label[]> teamLabels;
  protected Label[] classificationlabels;
  protected Double[] percentagesbyUser;
  protected Double[] percentagesbyAI;
  protected int[] expansions;
  protected GameState[] userStates;
  protected GameState[] aiStates;
  protected String[] moveColors;
  protected int[] heuristics;
  protected int[] simulations;
  protected VBox progressBar;
  protected int totalmoves;
  int currentMove;
  protected AnalyzedGameState[] analysedGames;
  GameSaveHandler gsh;
  protected boolean showHuman;
  public boolean switched;

  boolean colorHasChanged;

  private GameAnalyzer analyzer;

  private AnalyzerUtils utils;

  public AiAnalyzerScene(double width, double height) {
    super(new StackPane(), width, height);
    if (!createGameSaver()) {
      switched = false;
      return;
    }
    switched = true;
    colorHasChanged = false;
    manageFontSizes();
    rows = new HBox[totalmoves];
    teamLabels = new ArrayList<Label[]>(totalmoves);
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

    this.root = (StackPane) this.getRoot();
    currentMove = -1;
    popUpLabel = new SimpleObjectProperty<Font>(Font.font(this.getWidth() / 50));
    leaveButtonText = new SimpleObjectProperty<Font>(Font.font(this.getWidth() / 80));
    moveTableHeader = new SimpleObjectProperty<Font>(Font.font(this.getWidth() / 50));
    moveTableContent = new SimpleObjectProperty<Font>(Font.font(this.getWidth() / 60));
    informUser = new SimpleObjectProperty<Font>(Font.font(this.getWidth() / 30));

    utils = new AnalyzerUtils(this);
    createLayout();
    initalize();
    utils.makeClickable();
    utils.addKeyListeners();
    

    humanOrAiButton.setFocusTraversable(false);
    back.setFocusTraversable(false);
    next.setFocusTraversable(false);
  }

  /**
   * goes thorugh all the moves that were made in the game and saved them internally to handle the
   * data
   * 
   * @author sistumpf, Manuel Krakowski
   */
  private void initalize() {
    try {
      //adjust SavedGame initial team and move team (weird bug, only fix I see right now @sistumpf) TODO
//      System.out.println("first Team: " + gsh.getSavedGame().getInitialState().getCurrentTeam() + ", first Move Team: " + gsh.getSavedGame().getMoves().get("1").getTeamId());
//      gsh.getSavedGame().getInitialState().setCurrentTeam(Integer.parseInt(gsh.getSavedGame().getMoves().get("1").getTeamId()));

      analyzer = new GameAnalyzer(gsh.getSavedGame(), new AIConfig());
      analysedGames = getAnalyzer().getResults();
      Thread initThread = new Thread() {
        public void run() {
          int movePointer = 0;
          while ((getAnalyzer().isActive() || movePointer < getAnalyzer().howManyMoves()) && getAnalyzer().noErrors() -1 != movePointer) {
            if (movePointer != getAnalyzer().getCurrentlyAnalyzing()) {
              //              int currentMove = analyzer.getCurrentlyAnalyzing() -1;
              int currentMove = movePointer;
              AnalyzedGameState g = getAnalyzer().getResults()[currentMove];
              Runnable showResults = new Runnable() {
                @Override
                public void run() {
                  GameState state = g.getPreviousGameState();
                  
//                  teamLabels.get(currentMove)[1].setText("" + state.getCurrentTeam());                      //TODO teamlabel

                  String name = gsh.getSavedGame().getNames()[Integer.parseInt(gsh.getSavedGame().getMoves().get(currentMove+1 + "").getTeamId())];
                  teamLabels.get(currentMove)[0].setText("");
                  teamLabels.get(currentMove)[1].setText(name);
                  
                  String col;
                  if(g.getMoveEvaluation() != null) {
                    classificationlabels[currentMove].setText(g.getMoveEvaluation().name());
                    col = g.getMoveEvaluation().getColor();
                    percentagesbyUser[currentMove] = Double.valueOf(g.getUserWinPercentage());
                    userStates[currentMove] = g.getUserChoice();
                  } else {
                    classificationlabels[currentMove].setText("gave up");
                    col = "1b02fa";  
                    percentagesbyUser[currentMove] = 0.;
                    userStates[currentMove] = g.getUserGaveUp() == null ? state : g.getUserGaveUp();
//                    teamLabels.get(currentMove)[1].setText("" + g.getAiChoice().getLastMove().getTeamId()); //TODO teamlabel
                    name = gsh.getSavedGame().getNames()[Integer.parseInt(gsh.getSavedGame().getMoves().get(currentMove + "").getTeamId())];
                    teamLabels.get(currentMove)[0].setText("");
                    teamLabels.get(currentMove)[1].setText(name);
                  };
                  classificationlabels[currentMove].setStyle("-fx-text-fill: " + col);
                  moveColors[currentMove] = col;
                  percentagesbyAI[currentMove] = Double.valueOf(g.getAIWinPercentage());
                  simulations[currentMove] = g.getSimulations();
                  heuristics[currentMove] = g.getHeuristic();
                  expansions[currentMove] = g.getExpansions();
                  aiStates[currentMove] = g.getAiChoice();
                  changeColors();
                  utils.tryScrolling(currentMove);
                }};
                Platform.runLater(showResults);
                movePointer ++;
            }
            try {
              Thread.sleep(100);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        }
      };
      initThread.start();
    } catch (NeedMoreTimeException nmte) {
      System.err.println(
          "Error in " + getClass().getCanonicalName() + ":\n\t" + nmte.getLocalizedMessage());
    } catch (NullPointerException e) {
      firstMessage.setText("This SaveGame does not"
          + "\ncontain any Moves.");
      firstMessage.setTextAlignment(TextAlignment.CENTER);
      firstMessage.setAlignment(Pos.CENTER);
    }
  }

  /**
   * Creates the whole layout of the scene
   * 
   * @author Manuel Krakowski
   */
  private void createLayout() {
    root.prefHeightProperty().bind(this.heightProperty());
    root.prefWidthProperty().bind(this.widthProperty());
    VBox mainVBox = utils.createMainBox(root);
    mainVBox.getChildren().add(utils.createHeader());
    HBox sep = utils.createMiddleHBox(mainVBox);
    sep.getChildren().add(utils.createProgressBar(sep));
    sep.getChildren().add(utils.createMapBox(sep));
    sep.getChildren().add(utils.createAllMovesVBox(sep));
    mainVBox.getChildren().add(sep);
    root.getChildren().add(mainVBox);
  }

  /**
   * Opens a FileChooser so the user can select a game
   * 
   * @author sistumpf
   * @return true if a file was choosen
   */
  private boolean createGameSaver() {
    gsh = new GameSaveHandler();
    FileChooser choose = new FileChooser();
    choose.setInitialDirectory(new File(Constants.saveGameFolder));
    choose.setTitle("choose your saved game");
    File file = choose.showOpenDialog(null);
    if (file != null)
      gsh.readFile(file.getName().substring(0, file.getName().lastIndexOf(".")));
    else
      return false;
    //    System.out.println(file.getName().substring(0, file.getName().lastIndexOf(".")));
    totalmoves = gsh.savedGame.getMoves().size();
    return true;
  }
  
  /**
   * Manages the font-sizes on the whole scene
   * 
   * @author Manuel Krakowski
   */
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
   * Changes the colors to some predetermined colors.
   * 
   *  @author sistumpf
   */
  private void changeColors() {
    if(colorHasChanged) return;

    for(int entry=0; entry<teamLabels.size(); entry++) {
      int teamInGameState = 0;
      if(entry == 0) {
        String name = teamLabels.get(entry)[1].getText();
        for(int i=0; i<gsh.getSavedGame().getNames().length; i++)
          if(name.equals(gsh.getSavedGame().getNames()[i]))
            teamInGameState = i;
      } else {
        teamInGameState = Integer.parseInt(teamLabels.get(entry)[1].getText());
      }
      if(aiStates[currentMove+1].getTeams()[teamInGameState] != null) {
        teamLabels.get(entry)[1].setStyle("-fx-text-fill: "+ aiStates[currentMove+1].getTeams()[teamInGameState].getColor() +";");
      }
    }
    colorHasChanged = true;
  }

  public GameAnalyzer getAnalyzer() {
    return analyzer;
  }
}

