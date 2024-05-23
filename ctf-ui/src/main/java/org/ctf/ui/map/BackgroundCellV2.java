package org.ctf.ui.map;

import org.ctf.shared.constants.Enums.SoundType;
import org.ctf.ui.controllers.SoundController;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class BackgroundCellV2 extends Pane {
  private int x, y; // Coordinates of the cell in the grid
  private boolean occupied; // indicates whether there currently is an object on the cell
  private Circle rc; // Circle to show possible move on empty cell
  private Circle rc2; // circle to show that figure is selected
  private boolean active; // When cell is active user can click it to make move-request
  private StackPane base; // StackPane as base of the cell where objects are placed on
  private CostumFigurePain child; // if there is a piece on the cell, null otherwise
  private BaseRep teamBase; // if there is a base on the cell, null otherwise
  Color testColor;

  public BackgroundCellV2(int x, int y) {
    testColor = Color.rgb(173, 216, 230, 0.7);
    this.setStyle("-fx-border-color: black; " + "-fx-border-width: 1.2px ");
    this.x = x;
    this.y = y;
    this.occupied = false;
    this.createBase();
    createCircle();
  }

  /**
   * Creates the circle which is used to indicate a possible move on an empty cell
   * 
   * @author Manuel Krakowski
   */
  public void createCircle() {
    NumberBinding binding = Bindings.divide(widthProperty(), 6);
    NumberBinding roundSize = Bindings.createIntegerBinding(() -> binding.intValue(), binding);
    NumberBinding binding2 = Bindings.divide(widthProperty(), 2);
    NumberBinding roundSize2 = Bindings.createIntegerBinding(() -> binding2.intValue(), binding2);
    rc = new Circle();
    rc.radiusProperty().bind(roundSize);
    rc.centerXProperty().bind(roundSize2);
    rc.centerYProperty().bind(roundSize2);
    rc.setFill(null);
    base.getChildren().add(rc);
  }

  /**
   * Creates the circle which is drawn around a figure to indicate that it is currently selected
   * 
   * @author Manuel Krakowski
   */
  public void createCircle2() {
    NumberBinding binding = Bindings.divide(widthProperty(), 2.8);
    NumberBinding roundSize = Bindings.createIntegerBinding(() -> binding.intValue(), binding);
    NumberBinding binding2 = Bindings.divide(widthProperty(), 2);
    NumberBinding roundSize2 = Bindings.createIntegerBinding(() -> binding2.intValue(), binding2);
    rc2 = new Circle();
    rc2.radiusProperty().bind(roundSize);
    rc2.centerXProperty().bind(roundSize2);
    rc2.centerYProperty().bind(roundSize2);
    rc2.setFill(null);
    rc2.setStroke(Color.rgb(173, 216, 230, 0.5));
    rc2.setStrokeWidth(3);
    base.getChildren().add(rc2);
  }

  /**
   * Adds a figure to the cell and resizes it with it
   * 
   * @author Manuel Krakowski
   * @param figure {@link CostumFigurePain}
   */
  public void addFigure(CostumFigurePain figure) {
    NumberBinding binding = Bindings.multiply(widthProperty(), 0.5);
    NumberBinding roundSize = Bindings.createIntegerBinding(() -> binding.intValue(), binding);
    base.getChildren().remove(rc);
    occupied = true;
    child = figure;
    child.maxWidthProperty().bind(roundSize);
    child.maxHeightProperty().bind(roundSize);
    base.getChildren().add(child);
    figure.setParente(this);
  }

  /**
   * removes the current figure from the Backgroundcell
   * 
   * @author Manuel Krakowski
   */
  @Deprecated
  public void removeFigure() {
    occupied = false;
    base.getChildren().remove(child);
    this.child = null;
  }


  /**
   * adds a Block to the cell and resizes it with it
   * @author Manuel Krakowski
   * 
   */
  public void addBlock(boolean isVisible) {
    NumberBinding binding = Bindings.multiply(widthProperty(), 0.5);
    NumberBinding roundSize = Bindings.createIntegerBinding(() -> binding.intValue(), binding);
    occupied = true;
    BlockRepV3 blocki = new BlockRepV3();
    if(!isVisible) {
      blocki.setOpacitytoZero();
    }
    blocki.maxWidthProperty().bind(roundSize);
    blocki.maxHeightProperty().bind(roundSize);
    base.getChildren().clear();
    base.getChildren().add(blocki);

  }

  /**
   * Adds a teams base to the cell
   * @author Manuel Krakowski
   * @param r of a team
   */
  public void addBasis(BaseRep r) {
    NumberBinding binding = Bindings.multiply(widthProperty(), 0.6);
    NumberBinding roundSize = Bindings.createIntegerBinding(() -> binding.intValue(), binding);
    occupied = true;
    teamBase = r;
    BaseRep basis = r;
    basis.maxWidthProperty().bind(roundSize);
    basis.maxHeightProperty().bind(roundSize);
    base.getChildren().clear();
    base.getChildren().add(basis);
  }


  /**
   * Changes the background-color of the cell when the piece or base on it is attackable
   * @author Manuel Krakowski
   */
  public void showAttackable() {
    this.setStyle("-fx-background-color: rgb(255, 0, 0, 0.2);" + "-fx-border-color: red; "
        + "-fx-border-width: 1.2px");
  }

  /**
   * Changes the color of the cell to indicate that it was part of the last-move
   * @author Manuel Krakowski
   */
  public void showLastMove() {
    this.setStyle("-fx-background-color: rgb(0, 0, 255, 0.2);" + "-fx-border-color: blue; "
        + "-fx-border-width: 1.2px");
  }
  
  /**
   * Changes the color of the cell based on a specific coler-code to show the last-move
   * @author Manuel Krakowski
   * @param col Color which the last move representation has
   */
  public void showLastMoveForAnalyzer(String col) {
    this.setStyle("-fx-background-color: " + hextoString(col) + "; -fx-border-color: " + col + ";"
        + "-fx-border-width: 1.2px");
  }
  
  /**
   * transform a hex-color to a rgb-color which is necessary to change the opacity
   * @author Manuel Krakowski
   * @param col Color in hex-representation
   * @return Coler in rgb representation with a lower opacity
   */
  private String hextoString(String col) {
    col = col.replace("#", "");
    String s = "rgb(";
    int r = Integer.parseInt(col.substring(0, 2), 16);
    s+= r;
    s+= ",";
    int g = Integer.parseInt(col.substring(2, 4), 16);
    s+= g;
    s+= ",";
    int b = Integer.parseInt(col.substring(4, 6), 16);
    s+= b;
    s+= ",";
    s+= "0.2)";
    return s;
  }

  /**
   * Draws a little circle on the cell which indicates that the currently selcted piece can move on it
   * @author Manuel Krakowski
   */
  public void showPossibleMove() {
    rc.setFill(testColor);
    active = true;
  }

  /**
   * Draws a bigger circle around the figure which is currently selected
   * @author Manuel Krakowski
   */
  public void showSelected() {
    this.setStyle("-fx-background-color: transparent;" + "-fx-border-color: black; "
        + "-fx-border-width: 1.2px ");
    createCircle2();
  }

  /**
   * resets the cell to is default-style
   * @author Manuel Krakowski
   */
  public void deselect() {
    this.setStyle(" -fx-border-color: black; " + "-fx-border-width: 1.2px ");
    this.active = false;
    rc.setFill(null);
    if (rc2 != null) {
      base.getChildren().remove(rc2);
    }
  }
  
  @Deprecated
  public void resetCircle() {
    rc.setFill(Color.WHITE);
  }
  

  
  /**
   * Creates the base of the Cell Where all objects on it are placed
   * @author Manuel Krakowski
   */
  public void createBase() {
    StackPane base = new StackPane();
    NumberBinding binding = Bindings.multiply(widthProperty(), 0.8);
    NumberBinding roundSize = Bindings.createIntegerBinding(() -> binding.intValue(), binding);
    NumberBinding pos =
        Bindings.subtract(widthProperty().divide(2), base.widthProperty().divide(2));
    NumberBinding roundPos1 = Bindings.createIntegerBinding(() -> pos.intValue(), pos);
    NumberBinding pos2 =
        Bindings.subtract(widthProperty().divide(2), base.heightProperty().divide(2));
    NumberBinding roundPos2 = Bindings.createIntegerBinding(() -> pos2.intValue(), pos2);
    base.setStyle("-fx-background-color: transparent");
    base.setAlignment(Pos.CENTER);
    base.prefWidthProperty().bind(roundSize);
    base.prefHeightProperty().bind(roundSize);
    base.layoutXProperty().bind(roundPos1);
    base.layoutYProperty().bind(roundPos2);
    base.setOnMouseClicked(new EventHandler<MouseEvent>() {

      @Override
      public void handle(MouseEvent e) {
        if (active) {
          performClickOnCell();
        } else if (!occupied) {
          Game.deselectFigure();
        }
      }
    });
    this.base = base;
    this.getChildren().add(base);
  }

  /**
   * When the cell is clicked a move-request is sent
   * @author Manuel Krakowski
   */
  public void performClickOnCell() {
    SoundController.playSound(Game.getCurrent().getPiece().getDescription().getType(), SoundType.MOVE);
    int[] xk = {x, y};
    Game.makeMoveRequest(xk);
  }
  
  
 //Getters and Setters
 /////////////////////////////////////////////////////////////////////////////////
 //////////////////////////////////////////////////////////////////////////////////
  
  
  
  public BaseRep getTeamBase() {
    return teamBase;
  }

  public void setTeamBase(BaseRep teamBase) {
    this.teamBase = teamBase;
  }
  
  public int[] getPosition() {
    int[] s = {x, y};
    return s;
  }

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  public void setActive() {
    this.active = true;
  }

  public boolean isOccupied() {
    return occupied;
  }

  public CostumFigurePain getChild() {
    return child;
  }

  public void setUActive() {
    this.active = false;
  }
}
