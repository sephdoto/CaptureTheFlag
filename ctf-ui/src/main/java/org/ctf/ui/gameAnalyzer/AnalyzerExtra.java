package org.ctf.ui.gameAnalyzer;

import java.util.Arrays;
import org.ctf.shared.ai.GameUtilities;
import org.ctf.shared.constants.Enums;
import org.ctf.shared.constants.Enums.SoundType;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Piece;
import org.ctf.ui.controllers.SoundController;
import org.ctf.ui.data.SceneHandler;
import org.ctf.ui.map.GamePane;
import org.ctf.ui.map.MoveVisualizer;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * As {@link AnalyzerUtils} contains important methods for creating the layout,
 * things the AU methods rely on are located here
 * 
 * @author sistumpf
 */
public class AnalyzerExtra {
  protected AiAnalyzerScene scene;

  public AnalyzerExtra(AiAnalyzerScene scene) {
    this.scene = scene;
  }

  /**
   * When the Ai-button is clicked the Ai's Move is shown
   * 
   * @param aiButton
   * @author Manuel Krakowski
   */
  protected void performAiButtonClick(Button aiButton) {
    if(!isClickable(0)) return;
    if (scene.currentMove >= 0) {
      if (!scene.showHuman) {
        SoundController.playSound("AIButton", SoundType.MISC);
        setNewAiState();
        setNewProgressAi();
        aiButton.setText("Show Your Choice");
        scene.showHuman = true;
      } else {
        SoundController.playSound("HumanButton", SoundType.MISC);
        setNewGameState();
        setNewProgress();
        aiButton.setText("Show AI's Choice");
        scene.showHuman = false;
      }
    }
  }

  /**
   * When the back-button is clicked the move one before is shown
   * 
   * @author Manuel Krakowski
   * @param aiButton
   */
  protected void performBackClick(Button aiButton) {
    if(!isClickable(-1)) return;
    scene.showHuman = false;
    SoundController.playSound("BackButton", SoundType.MISC);
    if (scene.currentMove >= 1) {
      aiButton.setText("Show AI's Choice");
      scene.rows[scene.currentMove].getStyleClass().clear();
      scene.rows[--scene.currentMove].getStyleClass().add("blue-glow-hbox");
      setNewProgress();
      setNewToolTip();
      setNewGameState();
      if (scene.currentMove > 0 && (scene.currentMove % 5 == 0)) {
        scrollToLabel(scene.scroller, scene.content, scene.content.getChildren().get(scene.currentMove - 5));
      }
    }

  }

  /**
   * When the next button is clicked the next move is shown
   * 
   * @author Manuel Krakowski
   * @param aiButton
   */
  protected void performNextClick(Button aiButton) {
    if(!isClickable(1)) return;
    scene.showHuman = false;
    SoundController.playSound("NextButton", SoundType.MISC);
    if (scene.currentMove < scene.totalmoves - 1) {
      aiButton.setText("Show AI's Choice");
      if(scene.currentMove >= 0)  scene.rows[scene.currentMove].getStyleClass().clear();
      scene.rows[++scene.currentMove].getStyleClass().add("blue-glow-hbox");
      //      System.out.println(currentMove);
      setNewProgress();
      setNewToolTip();
      setNewGameState();

      if (scene.currentMove > 0 && (scene.currentMove % 5 == 0)) {
        scrollToLabel(scene.scroller, scene.content, scene.content.getChildren().get(scene.currentMove - 5));
      }
    }
  }

  /**
   * 
   * 
   * @author sistumpf
   * @param modifier
   * @return
   */
  protected boolean isClickable(int modifier) {
    try {
      return scene.currentMove + modifier >= 0 
          && scene.currentMove + modifier < scene.analysedGames.length 
          && scene.aiStates[scene.currentMove + modifier] != null;
    } catch (NullPointerException e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Changes the percentage qualification of the move when the move that currently is looked at
   * changes
   * 
   * @author Manuel Krakowski
   */
  protected void setNewProgress() {
    scene.progressBar.getChildren().clear();
    VBox progress = new VBox();
    progress.setStyle("-fx-background-color: " + getCurrentTeamsColor() + ";");
    progress.prefHeightProperty()
    .bind(scene.progressBar.heightProperty().multiply(scene.percentagesbyUser[scene.currentMove] / 100));
    progress.prefWidthProperty().bind(scene.progressBar.widthProperty());
    progress.getStyleClass().add("progress-pane");
    Label l = new Label(String.valueOf(scene.percentagesbyUser[scene.currentMove]) + "%");
    l.fontProperty().bind(scene.moveTableContent);
    l.getStyleClass().add("vertical-label");
    progress.getChildren().add(l);
    scene.progressBar.getChildren().add(progress);
  }

  /**
   * Changes the data in the tooltip when the move that is currently lloked at changes
   * 
   * @author Manuel Krakowski
   */
  protected void setNewToolTip() {
    Tooltip tooltip = new Tooltip(
        "depth: " + scene.analysedGames[scene.currentMove].getDepth() + "\n"
        + "expanded nodes: " + scene.expansions[scene.currentMove] + "\n"
        + "heuristics applied: " + scene.heuristics[scene.currentMove] + "\n"
        + "complete simulations: " + scene.simulations[scene.currentMove]);
    tooltip.setStyle("-fx-background-color: " + getCurrentTeamsColor() + ";");
    Duration delay = new Duration(1);
    tooltip.setShowDelay(delay);
    Duration displayTime = new Duration(10000);
    tooltip.setShowDuration(displayTime);
    tooltip.setFont(new Font(15));
    scene.progressBar.setPickOnBounds(true);
    Tooltip.install(scene.progressBar, tooltip);
  }

  /**
   * Changes the percentage qualification of the move by an Ai when the ai-button is clicked
   * 
   * @author Manuel Krakowski
   */
  protected void setNewProgressAi() {
    scene.progressBar.getChildren().clear();
    VBox progress = new VBox();
    progress.setStyle("-fx-background-color: " + getCurrentTeamsColor() + ";");
    progress.prefHeightProperty()
    .bind(scene.progressBar.heightProperty().multiply(scene.percentagesbyAI[scene.currentMove] / 100));
    progress.prefWidthProperty().bind(scene.progressBar.widthProperty());
    progress.getStyleClass().add("progress-pane");
    // progress.setStyle(" -fx-background-color: " + moveColors[currentMove] + "; \r\n"
    // + " -fx-background-radius: 20px; \r\n"
    // + " -fx-border-radius: 20px;\r\n"
    // + " -fx-alignment: center;");
    Label l = new Label(String.valueOf(scene.percentagesbyAI[scene.currentMove]) + "%");
    l.fontProperty().bind(scene.moveTableContent);
    l.getStyleClass().add("vertical-label");
    progress.getChildren().add(l);
    scene.progressBar.getChildren().add(progress);
  }

  /**
   * Returns the Color String of the current Team.
   * The current team is not the one which's move is next, but who just made the move.
   * 
   * @author sistumpf
   * @return the current teams color String
   */
  protected String getCurrentTeamsColor() {
    return scene.aiStates[scene.currentMove].getTeams()[currentTeamInt()].getColor();
  }

  /**
   * As an addition to getPreviousTeam and getNextTeam, currentTeamInt returns the current Teams index.
   * 
   * @author sistumpf
   * @return the current Teams index
   */
  protected int currentTeamInt() {
    int team = scene.aiStates[scene.currentMove].getCurrentTeam() -1 < 0 ?
        scene.aiStates[scene.currentMove].getTeams().length - 1 : scene.aiStates[scene.currentMove].getCurrentTeam() -1;
    while(scene.aiStates[scene.currentMove].getTeams()[team] == null) {
      team = team -1 < 0 ?
          scene.aiStates[scene.currentMove].getTeams().length - 1 : team -1;
    }
    return team;
  }

  /**
   * Shows a new gameState in the map.
   * 
   * @author sistumpf, Manuel Krakowski
   */
  protected void setNewGameState() {
    if(scene.userStates[scene.currentMove] != null) {
      scene.showMapBox.getChildren().clear();
      scene.gm = new GamePane(scene.userStates[scene.currentMove], true, scene.moveColors[scene.currentMove], null, null, 0);
      MoveVisualizer.setCb(scene.gm);
      if(!teamGaveUpChecker(scene.currentMove+1) && scene.userStates[scene.currentMove].getLastMove() != null) {
        Move m = scene.userStates[scene.currentMove].getLastMove();
        Piece p = Arrays
            .stream(scene.analysedGames[scene.currentMove].getPreviousGameState()
                .getTeams()[Integer.parseInt(m.getPieceId().split(":")[1].split("_")[0])]
                    .getPieces())
            .filter(pe -> pe.getId().equals(m.getPieceId())).findFirst().get();
        if(p != null) {
          scene.gm.setOldPosinAnalyzer(p.getPosition());
        }
      } else {
        clearAllCells(); 
      }

      StackPane.setAlignment(scene.gm, Pos.CENTER);
      scene.gm.maxWidthProperty().bind(SceneHandler.getMainStage().widthProperty().multiply(0.4));
      scene.gm.maxHeightProperty().bind(SceneHandler.getMainStage().heightProperty().multiply(0.6));
      scene.showMapBox.getChildren().add(scene.gm);
    }
  }

  /**
   * Checks if a Team has given up.
   * 
   * @author sistumpf
   * @param turn the turn to check if the Team in the SavedGame and MCTS root are not equal
   * @return true if a team gave up
   */
  protected boolean teamGaveUpChecker(int turn) {
    boolean someoneGaveUp = !scene.gsh.savedGame.getTeams().get("" + turn).equals("") &&
        GameUtilities.moveEquals(scene.gsh.savedGame.getMoves().get("" + turn), scene.gsh.savedGame.getMoves().get("" + (turn -1)));
    return someoneGaveUp;
  }

  /**
   * Clears all selections from the cells
   */
  protected void clearAllCells() {
    for(int y=0; y<scene.gm.getState().getGrid().length; y++)
      for(int x=0; x<scene.gm.getState().getGrid()[0].length; x++)
        scene.gm.getCells().get(scene.gm.generateKey(y, x))
        .deselect();
  }

  /**
   * Shows the move of the ai in the map
   * 
   * @author sistumpf, Manuel Krakowski
   */
  protected void setNewAiState() {
    scene.showMapBox.getChildren().clear();
    GameState statebefore = scene.analysedGames[scene.currentMove].getPreviousGameState();
    Move m = scene.aiStates[scene.currentMove].getLastMove();
    Piece p = Arrays
        .stream(statebefore.getTeams()[Integer.parseInt(m.getPieceId().split(":")[1].split("_")[0])]
            .getPieces())
        .filter(pe -> pe.getId().equals(m.getPieceId())).findFirst().get();
    scene.gm = new GamePane(scene.aiStates[scene.currentMove], true, Enums.MoveEvaluation.BEST.getColor(), null, null, 0);
    scene.gm.setOldPosinAnalyzer(p.getPosition());
    StackPane.setAlignment(scene.gm, Pos.CENTER);
    scene.gm.maxWidthProperty().bind(SceneHandler.getMainStage().widthProperty().multiply(0.4));
    scene.gm.maxHeightProperty().bind(SceneHandler.getMainStage().heightProperty().multiply(0.6));
    scene.showMapBox.getChildren().add(scene.gm);
  }

  /**
   * Changes where the scrollpane's content is shown when the next button is clicked
   * 
   * @author Manuel Krakowski
   * @param scrollPane
   * @param vbox
   * @param label
   */
  protected void scrollToLabel(ScrollPane scrollPane, VBox vbox, javafx.scene.Node label) {
    Bounds viewportBounds = scrollPane.getViewportBounds();
    Bounds contentBounds = label.getBoundsInParent();

    double viewportHeight = viewportBounds.getHeight();
    double contentHeight = vbox.getHeight();

    double scrollOffset = contentBounds.getMinY() / (contentHeight - viewportHeight);
    scrollPane.setVvalue(scrollOffset);
  }
}
