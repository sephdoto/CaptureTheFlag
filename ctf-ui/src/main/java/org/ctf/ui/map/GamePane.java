package org.ctf.ui.map;

import java.util.HashMap;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;


/**
 * @author mkrakows
 * This class represents the GameBoard on which the figures are placed
 * it is realized y using a GridPane and resizable for any kind of map
 */
public class GamePane extends HBox{

  String[][] map;
  final GameState state;
  Team[] teams;
  int rows;
  int cols;
  int currentTeam;
  
  private String colerforAnlyzer;
private int[] oldPosinAnalyzer;
  public int[] getOldPosinAnalyzer() {
  return oldPosinAnalyzer;
  
}

public void setOldPosinAnalyzer(int[] oldPosinAnalyzer) {
  this.oldPosinAnalyzer = oldPosinAnalyzer;
  cells.get(generateKey(oldPosinAnalyzer[0], oldPosinAnalyzer[1])).showLastMoveForAnalyzer(colerforAnlyzer);
}

  public VBox vBox;
  int anzTeams;
  HashMap<String, CostumFigurePain> figures = new HashMap<String, CostumFigurePain>();
  public ChangeListener<Number> heightListener;
  public ChangeListener<Number> widthListener;
  HashMap<Integer, BaseRep> bases = new HashMap<Integer, BaseRep>();
  HashMap<Integer, BackgroundCellV2> cells = new HashMap<Integer, BackgroundCellV2>();
  public GridPane gridPane;
  SimpleObjectProperty<Double> prefWidth;
  SimpleObjectProperty<Double> prefHeight;
  SimpleObjectProperty<Double> minWidth;
  SimpleObjectProperty<Double> minHeight;
  SimpleObjectProperty<Double> minSize;
  SimpleObjectProperty<Double> min;
  NumberBinding binding;
  private boolean blocksvisible;
  private boolean inAnalyser;

  public GamePane(GameState state,boolean blocksVisible,String col) {
    initSizes();
    //this.setStyle("-fx-background-color: yellow");
    this.state = state;
    this.map = state.getGrid();
    this.blocksvisible = blocksVisible;
    this.colerforAnlyzer = col;
    this.currentTeam = state.getCurrentTeam();
    rows = map.length;
    cols = map[0].length;
    vBox = new VBox();
    //vBox.setStyle("-fx-background-color: red");
    vBox.alignmentProperty().set(Pos.CENTER);
    alignmentProperty().set(Pos.CENTER);
    //paddingProperty().set(new Insets(20));

    gridPane = new GridPane();

    //gridPane.setStyle("-fx-border-color:black; -fx-border-width: 3px");
    // gridPane.setGridLinesVisible(true);
    binding = Bindings.min(widthProperty().divide(cols), heightProperty().divide(rows));
    NumberBinding roundSize = Bindings.createIntegerBinding(() ->  binding.intValue(), binding);
    vBox.prefWidthProperty().bind(roundSize.multiply(cols));
    vBox.prefHeightProperty().bind(roundSize.multiply(rows));
    vBox.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
    vBox.setFillWidth(true);
    gridPane.setSnapToPixel(false);
    VBox.setVgrow(gridPane, Priority.ALWAYS);
    for (int i = 0; i < cols; i++) {
      ColumnConstraints columnConstraints = new ColumnConstraints(Control.USE_PREF_SIZE,
          Control.USE_COMPUTED_SIZE, Double.MAX_VALUE);
      columnConstraints.setHgrow(Priority.SOMETIMES);
      //columnConstraints.setPercentWidth(getWidth()/rows);
      gridPane.getColumnConstraints().add(columnConstraints);
    }
    for (int j = 0; j < rows; j++) {
      RowConstraints rowConstraints = new RowConstraints(Control.USE_PREF_SIZE, Control.USE_COMPUTED_SIZE,
          Double.MAX_VALUE);
      rowConstraints.setVgrow(Priority.SOMETIMES);
      gridPane.getRowConstraints().add(rowConstraints);
    }
    vBox.getChildren().add(gridPane);
    getChildren().add(vBox);
    HBox.setHgrow(this, Priority.ALWAYS);
    this.fillGrid();
    //setCurrentTeamActive();
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
   * Adds width and height listeners to the object.
   * Should be called in constructor.
   * 
   * @author mkrakows, sistumpf
   */
  private void addListeners() {
    this.heightListener = new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth, Number newWidth) {
        if(MoveVisualizer.getCurrent()!=null) {
          MoveVisualizer.getCurrent().performSelectClick();
        }
      }
    };
    vBox.heightProperty().addListener(this.heightListener);
    this.widthListener = new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth, Number newWidth) {
        if(MoveVisualizer.getCurrent()!=null) {
          //Game.getCurrent().performSelectClick();
        }

      }
    };
    this.widthProperty().addListener(this.widthListener);
  }
  
  /**
   * Inits the differend min and preferred sizes.
   * 
   * @author mkrakows, sistumpf
   */
  private void initSizes() {
    prefWidth = new SimpleObjectProperty<Double>();
    prefHeight = new SimpleObjectProperty<Double>();
    minWidth = new SimpleObjectProperty<>(getWidth() / cols);
    minHeight = new SimpleObjectProperty<>(getHeight() / rows);
    minSize = new SimpleObjectProperty<>(
        minWidth.get() < minHeight.get() ? minWidth.get() : minHeight.get()
        );
    min = new SimpleObjectProperty<Double>();
  }

  private void showLastMove() {
    if (state.getLastMove() != null && state.getLastMove().getNewPosition() != null) {
      Move lastMove = state.getLastMove();
      int xNewPos = lastMove.getNewPosition()[0];
      int yNewPos = lastMove.getNewPosition()[1];
      if(!colerforAnlyzer.equals("")) {
        cells.get(generateKey(xNewPos, yNewPos)).showLastMoveForAnalyzer(colerforAnlyzer);

      }else {
        cells.get(generateKey(xNewPos, yNewPos)).showLastMove();

      }
      if(CreateGameController.getLastFigures() != null && colerforAnlyzer.equals("")) {
        CostumFigurePain old = CreateGameController.getLastFigures().get(lastMove.getPieceId());
        int xOldPosX = old.getPosX();
        int oldPosY = old.getPosY();
        cells.get(generateKey(xOldPosX, oldPosY)).showLastMove();
      }
    }
  }

  public  ImageView createBackgroundImage() {
    Image mp = new Image(getClass().getResourceAsStream("tuning1.png"));
    ImageView mpv = new ImageView(mp);
    mpv.fitHeightProperty().bind(vBox.heightProperty());
    mpv.fitWidthProperty().bind(vBox.widthProperty());
    mpv.setPreserveRatio(true);
    mpv.setOpacity(0.2);
    return mpv;
  }

  public void moveFigure(int x, int y, CostumFigurePain mover) {
    mover.getParentCell().removeFigure();
    //BackgroundCellV2 oldField = mover.getParentCell();
    BackgroundCellV2 newField = cells.get(generateKey(x, y));
    //showTransition(mover, oldField, newField);
    if(newField.isOccupied()) {
      CostumFigurePain figureToDelete = newField.getChild();
      System.out.println("XXX" + figureToDelete.getPiece().getId());
      boolean delted = figures.remove(figureToDelete.getPiece().getId(), figureToDelete); 
      System.out.println(delted);
      newField.removeFigure();
    }
    newField.addFigure(mover);
  }

  public void enableBaseColors(PlayGameScreenV2 scene) {
    for(BaseRep b: bases.values()) {
      b.setScene(scene);
    }
  }

  //    public  void setCurrentTeamActive() {
  //        for (CostumFigurePain c : figures.values()) {
  //            if (c.getTeamID().equals(String.valueOf(currentTeam))) {
  //                c.setActive();
  //            }
  //        }
  //    }


  public int generateKey(int x, int y) {
    return (x+y)*(x+y+1) / 2 + x;
  }

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

  public void fillGrid() {
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        BackgroundCellV2 child = new BackgroundCellV2(i, j);
        cells.put(generateKey(i, j),child);
        String objectOnMap = map[i][j];
        if(objectOnMap.equals("b")) {
          if(blocksvisible) {
            child.addBlock(true);
          }else {
            child.addBlock(false);
          }
        } else if (objectOnMap.startsWith("b:")) {

        }else if (objectOnMap.startsWith("b:2")) {
          //Add base of team 2 here
        }
        GridPane.setRowIndex(child, i);
        GridPane.setColumnIndex(child, j);
        gridPane.getChildren().add(child);
      }
    }
    teams = state.getTeams();
    for(int i=0;i<teams.length;i++) {
      Team currenTeam = teams[i];
      if(currenTeam == null) continue;
      int baseX = currenTeam.getBase()[0];
      int baseY = currenTeam.getBase()[1];
      String teamColor = currenTeam.getColor();
      BaseRep b = new BaseRep(currenTeam.getFlags(),teamColor, currenTeam.getId(), cells.get(generateKey(baseX, baseY)));
      if(!CreateGameController.getColors().isEmpty()) {
        b.showColor(CreateGameController.getColors().get(b.getTeamID()));
      }else {
        b.showDefaultTeamColor(teamColor);
      }
      bases.put(i, b);
      cells.get(generateKey(baseX, baseY)).addBasis(b);
      Piece[] pieces = currenTeam.getPieces();
      for(Piece piece: pieces) {
        CostumFigurePain pieceRep = new CostumFigurePain(piece);
        if ( !CreateGameController.getColors().isEmpty()) {
          pieceRep.showTeamColorWhenSelecting(CreateGameController.getColors().get(piece.getTeamId()));
        }else {
          pieceRep.showTeamColor(teamColor);//
        }
        figures.put(piece.getId(), pieceRep);
        //allFigures.add(pieceRep);
        int x = piece.getPosition()[0];
        int y = piece.getPosition()[1];
        cells.get(generateKey(x, y)).addFigure(pieceRep);
      }
    }

  }
}
