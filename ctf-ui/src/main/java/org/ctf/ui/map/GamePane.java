package org.ctf.ui.map;

import java.util.HashMap;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Piece;
import org.ctf.shared.state.Team;
import org.ctf.ui.hostGame.CreateGameController;
import org.ctf.ui.hostGame.PlayGameScreenV2;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;


/**
 *
 * Representation of a Map using a GameState
 * 
 * @author Manuel Krakowski
 *
 */
public class GamePane extends HBox {

  // Data of the GameState which needs to be represented on the map
  private String[][] map;
  private final GameState state;
  private Team[] teams;
  private int rows;
  private int cols;
  private int currentTeam;
  private String colerforAnlyzer;
  private VBox vBox;
  private boolean blocksvisible;


  // Stored Objects on the map
  private HashMap<Integer, BaseRep> bases = new HashMap<Integer, BaseRep>();
  private HashMap<Integer, BackgroundCellV2> cells = new HashMap<Integer, BackgroundCellV2>();
  private HashMap<String, CostumFigurePain> figures = new HashMap<String, CostumFigurePain>();


  // Attributes for resizing
  public ChangeListener<Number> heightListener;
  public ChangeListener<Number> widthListener;
  private GridPane gridPane;
  private SimpleObjectProperty<Double> prefWidth;
  private SimpleObjectProperty<Double> prefHeight;
  private SimpleObjectProperty<Double> minWidth;
  private SimpleObjectProperty<Double> minHeight;
  private SimpleObjectProperty<Double> minSize;
  private SimpleObjectProperty<Double> min;
  private NumberBinding binding;



  /**
   * Initializes the structure of the map and sets all values that are necessary for resizing it
   * 
   * @author Manuel Krakowski
   * @param state GameState which is represented on the map
   * @param blocksVisible true if blocks are as black rectangles, false if blocks are included in
   *        background image
   * @param col only used for the Analyzer
   */
  public GamePane(GameState state, boolean blocksVisible, String col) {
    if(Constants.backgroundImageOpacity < 0.5) blocksVisible = true;
    initSizes();
    this.state = state;
    this.map = state.getGrid();
    this.blocksvisible = blocksVisible;
    this.colerforAnlyzer = col;
    this.currentTeam = state.getCurrentTeam();
    rows = map.length;
    cols = map[0].length;
    vBox = new VBox();
    vBox.alignmentProperty().set(Pos.CENTER);
    alignmentProperty().set(Pos.CENTER);
    gridPane = new GridPane();
    
    binding = Bindings.min(widthProperty().divide(cols), heightProperty().divide(rows));
    NumberBinding roundSize = Bindings.createDoubleBinding(() -> binding.doubleValue(), binding);
    vBox.prefWidthProperty().bind(roundSize.multiply(cols));
    vBox.prefHeightProperty().bind(roundSize.multiply(rows));
    vBox.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
    vBox.setFillWidth(true);
    
    gridPane.setSnapToPixel(true);
    VBox.setVgrow(gridPane, Priority.ALWAYS);
    for (int i = 0; i < cols; i++) {
      ColumnConstraints columnConstraints =
          new ColumnConstraints(Control.USE_PREF_SIZE, Control.USE_COMPUTED_SIZE, Double.MAX_VALUE);
      columnConstraints.setHgrow(Priority.SOMETIMES);
      gridPane.getColumnConstraints().add(columnConstraints);
    }
    for (int j = 0; j < rows; j++) {
      RowConstraints rowConstraints =
          new RowConstraints(Control.USE_PREF_SIZE, Control.USE_COMPUTED_SIZE, Double.MAX_VALUE);
      rowConstraints.setVgrow(Priority.SOMETIMES);
      gridPane.getRowConstraints().add(rowConstraints);
    }
    vBox.getChildren().add(gridPane);
    getChildren().add(vBox);
    HBox.setHgrow(this, Priority.ALWAYS);
    this.fillGrid();
    showLastMove();
    addListeners();
    System.gc();
  }



  /**
   * Removes the width and height listeners, preparing the object for garbage collection.
   * 
   * @author sistumpf
   */
  public void destroyReferences() {
    vBox.heightProperty().removeListener(widthListener);
    widthListener = null;
    vBox.heightProperty().removeListener(heightListener);
    heightListener = null;

  }



  /**
   * Adds width and height listeners to the object. Should be called in constructor.
   * 
   * @author Manuel Krakowski, sistumpf
   */
  private void addListeners() {
    this.heightListener = new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth,
          Number newWidth) {
        if (MoveVisualizer.getCurrent() != null) {
          MoveVisualizer.getCurrent().performSelectClick();
        }
      }
    };
    vBox.heightProperty().addListener(this.heightListener);
    this.widthListener = new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth,
          Number newWidth) {
        if (MoveVisualizer.getCurrent() != null) {
          // Game.getCurrent().performSelectClick();
        }

      }
    };
    this.widthProperty().addListener(this.widthListener);
  }

  /**
   * Inits the differend min and preferred sizes.
   * 
   * @author Manuel Krakowski, sistumpf
   */
  private void initSizes() {
    prefWidth = new SimpleObjectProperty<Double>();
    prefHeight = new SimpleObjectProperty<Double>();
    minWidth = new SimpleObjectProperty<>(getWidth() / cols);
    minHeight = new SimpleObjectProperty<>(getHeight() / rows);
    minSize = new SimpleObjectProperty<>(
        minWidth.get() < minHeight.get() ? minWidth.get() : minHeight.get());
    min = new SimpleObjectProperty<Double>();
  }


  /**
   * highlights the last move on the map
   * 
   * @author Manuel Krakowski
   */
  public void showLastMove() {
    if (state.getLastMove() != null && state.getLastMove().getNewPosition() != null && !state.getLastMove().getPieceId().equals("")) {
      Move lastMove = state.getLastMove();
      int xNewPos = lastMove.getNewPosition()[0];
      int yNewPos = lastMove.getNewPosition()[1];
      if (!colerforAnlyzer.equals("")) {
        cells.get(generateKey(xNewPos, yNewPos)).showLastMoveWithColor(colerforAnlyzer, colerforAnlyzer);
      } else {
        setDynamicCellBackground(cells.get(generateKey(xNewPos, yNewPos)));
      }
      if(CreateGameController.getLastFigures() != null) {
        CostumFigurePain old = CreateGameController.getLastFigures().get(lastMove.getPieceId());
        if (CreateGameController.getLastFigures() != null && colerforAnlyzer.equals("") && old != null) {
          int xOldPosX = old.getPosX();
          int oldPosY = old.getPosY();
          setDynamicCellBackground(cells.get(generateKey(xOldPosX, oldPosY)));
        }
      }
    }
  }
  
  /**
   * Tries to set the cell background to the last teams color,
   * if it fails, the default color gets chosen.
   * 
   * @author sistumpf
   */
  private void setDynamicCellBackground(BackgroundCellV2 cell){
    try {
      cell.showLastMoveWithColor(CreateGameController.getColors().get(state.getLastMove().getTeamId()).get().toString(), "blue");
    } catch (Exception e) {
      e.printStackTrace();
      cell.showLastMove();
    };
  }

  /**
   * highlights the old pos of the last move in analyzer (only used in game-analyzer)
   * 
   * @author Manuel Krakowski
   * @param oldPosinAnalyzer old pos of last move
   */
  public void setOldPosinAnalyzer(int[] oldPosinAnalyzer) {
    cells.get(generateKey(oldPosinAnalyzer[0], oldPosinAnalyzer[1]))
        .showLastMoveWithColor(colerforAnlyzer, colerforAnlyzer);
  }



  /**
   * Enables the option for the user to select team-colors by clicking on bases
   * 
   * @author Manuel Krakowski
   * @param scene scene in which base colors are enabled by clicking
   */
  public void enableBaseColors(PlayGameScreenV2 scene) {
    for (BaseRep b : bases.values()) {
      b.setScene(scene);
    }
  }



  /**
   * Generates a unique value to store the position of a background-cell
   * 
   * @author Manuel Krakowski
   * @param x x-coordinate of cell
   * @param y y-coordinate of cell
   * @return
   */
  public int generateKey(int x, int y) {
    return (x + y) * (x + y + 1) / 2 + x;
  }


  /**
   * Fills the map with all objects that are currently placed on it in the game-state
   * 
   * @author Manuel Krakowski
   */
  public void fillGrid() {
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        BackgroundCellV2 child = new BackgroundCellV2(i, j);
        cells.put(generateKey(i, j), child);
        String objectOnMap = map[i][j];
        if (objectOnMap.equals("b")) {
          if (blocksvisible) {
            child.addBlock(true);
          } else {
            child.addBlock(false);
          }
        } else if (objectOnMap.startsWith("b:")) {
          // currently not used
        } else if (objectOnMap.startsWith("b:2")) {
          /// Currently not used
        }
        GridPane.setRowIndex(child, i);
        GridPane.setColumnIndex(child, j);
        gridPane.getChildren().add(child);
      }
    }
    teams = state.getTeams();
    for (int i = 0; i < teams.length; i++) {
      Team currenTeam = teams[i];
      if (currenTeam == null)
        continue;
      int baseX = currenTeam.getBase()[0];
      int baseY = currenTeam.getBase()[1];
      String teamColor = currenTeam.getColor();
      teamColor = teamColor.equals("") ? "#ffffff" : teamColor; //TODO
      
      BaseRep b = new BaseRep(currenTeam.getFlags(), teamColor, currenTeam.getId(),
          cells.get(generateKey(baseX, baseY)));
      if (!CreateGameController.getColors().isEmpty()) {
        b.showColor(CreateGameController.getColors().get(b.getTeamID()));
      } else {
        b.showDefaultTeamColor(teamColor);
      }
      bases.put(i, b);
      cells.get(generateKey(baseX, baseY)).addBasis(b);
      Piece[] pieces = currenTeam.getPieces();
      for (Piece piece : pieces) {
        CostumFigurePain pieceRep = new CostumFigurePain(piece);
        if (!CreateGameController.getColors().isEmpty()) {
          pieceRep
              .showTeamColorWhenSelecting(CreateGameController.getColors().get(piece.getTeamId()));
        } else {
          pieceRep.showTeamColor(teamColor);//
        }
        figures.put(piece.getId(), pieceRep);
        // allFigures.add(pieceRep);
        int x = piece.getPosition()[0];
        int y = piece.getPosition()[1];
        cells.get(generateKey(x, y)).addFigure(pieceRep);
      }
    }

  }

  // Getters and Setters
  /////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////

  public HashMap<String, CostumFigurePain> getFigures() {
    return figures;
  }

  public void setFigures(HashMap<String, CostumFigurePain> figures) {
    this.figures = figures;
  }

  public HashMap<Integer, BackgroundCellV2> getCells() {
    return cells;
  }

  public void setCells(HashMap<Integer, BackgroundCellV2> cells) {
    this.cells = cells;
  }

  public HashMap<Integer, BaseRep> getBases() {
    return bases;
  }

  public void setBases(HashMap<Integer, BaseRep> bases) {
    this.bases = bases;
  }

  public VBox getvBox() {
    return vBox;
  }

  public void setvBox(VBox vBox) {
    this.vBox = vBox;
  }

  public GameState getState() {
    return state;
  }

}
