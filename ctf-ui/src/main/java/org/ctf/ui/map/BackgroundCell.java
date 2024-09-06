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

/**
 * Visual representation of a quadratic cell in the grid in which objects can be placed
 * 
 * @author Manuel Krakowski
 */

public class BackgroundCell extends Pane {


  private int x, y; // Coordinates of the cell in the grid
  private boolean occupied; // indicates whether there currently is an object on the cell
  private Circle rc; // Circle to show possible move on empty cell
  private Circle rc2; // circle to show that figure is selected
  private Circle rc3; // Circle to show possible move on empty cell, shown when hovered
  private boolean active; // When cell is active user can click it to make move-request
  private StackPane base; // StackPane as base of the cell where objects are placed on
  private CustomFigurePane child; // if there is a piece on the cell, null otherwise
  private BaseRep teamBase; // if there is a base on the cell, null otherwise
  private Color testColor;

  /**
   * Initializes a background-cell in the grid
   * 
   * @author Manuel Krakowski
   * @param x x-coordinate in the grid
   * @param y y-coordinate in the grid
   */
  public BackgroundCell(int x, int y) {
    testColor = Color.rgb(173, 216, 230, 0.7);
    this.setStyle("-fx-border-color: black; " + "-fx-border-width: 1.2px ");
    this.x = x;
    this.y = y;
    this.occupied = false;
    this.createBase();
    createCircle();
    createCircle3();
  }

  /**
   * Creates the circle which is used to indicate a possible move on an empty cell
   * 
   * @author Manuel Krakowski
   */
  public void createCircle() {
    rc = new Circle();
    rc.radiusProperty().bind(getBase().widthProperty().divide(4.5));
    rc.centerXProperty().bind(getBase().widthProperty().divide(2));
    rc.centerYProperty().bind(getBase().heightProperty().divide(2));
    rc.setFill(null);
    getBase().getChildren().add(rc);
  }

  /**
   * Creates the circle which is drawn around a figure to indicate that it is currently selected
   * 
   * @author Manuel Krakowski
   */
  public void createCircle2() {
    rc2 = new Circle();
    rc2.radiusProperty().bind(getBase().widthProperty().divide(2.25));
    rc2.centerXProperty().bind(getBase().widthProperty().divide(2));
    rc2.centerYProperty().bind(getBase().heightProperty().divide(2));
    rc2.setFill(null);
    rc2.setStroke(Color.rgb(173, 216, 230, 0.5));
    rc2.setStrokeWidth(3);
    getBase().getChildren().add(rc2);
  }
  
  /**
   * Creates the circle which is drawn around a figure to indicate that it is currently selected
   * 
   * @author Manuel Krakowski
   */
  public void createCircle3() {
    rc3 = new Circle();
    rc3.radiusProperty().bind(getBase().widthProperty().divide(7.5));
    rc3.centerXProperty().bind(getBase().widthProperty().divide(2));
    rc3.centerYProperty().bind(getBase().heightProperty().divide(2));
    rc3.setFill(null);
//    rc3.setStroke(Color.rgb(230, 230, 250, 0.2));
//    rc3.setStrokeWidth(3);
    getBase().getChildren().add(rc3);
  }

  /**
   * Adds a figure to the cell and resizes it with it
   * 
   * @author Manuel Krakowski
   * @param figure {@link CustomFigurePane}
   */
  public void addFigure(CustomFigurePane figure) {
    getBase().getChildren().remove(rc);
    occupied = true;
    child = figure;
    child.maxWidthProperty().bind(getBase().widthProperty().multiply(0.85));
    child.maxHeightProperty().bind(getBase().heightProperty().multiply(0.85));
    getBase().getChildren().add(child);
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
    getBase().getChildren().remove(child);
    this.child = null;
  }


  /**
   * adds a Block to the cell and resizes it with it
   * 
   * @author Manuel Krakowski
   * 
   */
  public void addBlock(boolean isVisible) {
    occupied = true;
    BlockRep blocki = new BlockRep();
    if (!isVisible) {
      blocki.setOpacitytoZero();
    }
    blocki.maxWidthProperty().bind(getBase().widthProperty().multiply(0.65));
    blocki.maxHeightProperty().bind(getBase().heightProperty().multiply(0.65));
    getBase().getChildren().clear();
    getBase().getChildren().add(blocki);
  }

  /**
   * Adds a teams base to the cell
   * 
   * @author Manuel Krakowski
   * @param r of a team
   */
  public void addBasis(BaseRep r) {
    occupied = true;
    teamBase = r;
    BaseRep basis = r;
    basis.maxWidthProperty().bind(getBase().widthProperty().multiply(0.7));
    basis.maxHeightProperty().bind(getBase().widthProperty().multiply(0.7));
    getBase().getChildren().clear();
    getBase().getChildren().add(basis);
  }
  
  /**
   * Changes the background-color of the cell when the piece or base on it is attackable
   * 
   * @author Manuel Krakowski
   */
  public void showAttackable() {
    int r=255;
    int g=0;
    int b=0;
    if(getStyle().contains("rgb")) {
      String[] rgb = getStyle().substring(getStyle().indexOf("(") +1,getStyle().indexOf(")")).split(",");
      r = Integer.parseInt(rgb[0]);
      g = Integer.parseInt(rgb[1]);
      b = Integer.parseInt(rgb[2]);
    }
    this.setStyle("-fx-background-color: rgb(" + r + "," + g + "," + b + ",0.2);" + "-fx-border-color: red; "
        + "-fx-border-width: 1.2px");
  }

  /**
   * Changes the color of the cell to indicate that it was part of the last-move
   * 
   * @author Manuel Krakowski
   */
  public void showLastMove() {
    this.setStyle("-fx-background-color: rgb(0, 0, 255, 0.2);" + "-fx-border-color: blue; "
        + "-fx-border-width: 1.2px");
  }

  /**
   * Changes the color of the cell based on a specific coler-code to show the last-move.
   * 
   * @author Manuel Krakowski
   * @author sistumpf
   * @param col Color which the last move representation has
   */
  public void showLastMoveWithColor(String background, String border) {
    background = background.startsWith("0x") ? background.substring(2) : background;
    if(!getStyle().contains("-fx-border-color: red;") && !getStyle().contains("-fx-border-color: rgb(255,0,255,0.4);")) {
//    if(!this.active)
      this.setStyle("-fx-background-color: " + hextoString(background) + "; -fx-border-color: " + border + ";"
        + "-fx-border-width: 1.2px");
    }
  }

  /**
   * transform a hex-color to a rgb-color which is necessary to change the opacity
   * 
   * @author Manuel Krakowski
   * @param col Color in hex-representation
   * @return Coler in rgb representation with a lower opacity
   */
  private String hextoString(String col) {
    col = col.replace("#", "");
    String s = "rgb(";
    int r = Integer.parseInt(col.substring(0, 2), 16);
    s += r;
    s += ",";
    int g = Integer.parseInt(col.substring(2, 4), 16);
    s += g;
    s += ",";
    int b = Integer.parseInt(col.substring(4, 6), 16);
    s += b;
    s += ",";
    s += "0.2)";
    return s;
  }

  /**
   * Draws a little circle on the cell which indicates that the currently selcted piece can move on
   * it
   * 
   * @author Manuel Krakowski
   */
  public void showPossibleMove() {
    rc.setFill(testColor);
    active = true;
  }

  /**
   * Like {@link showPossibleMove()} but with another circle to show this seperately
   * 
   * @author sistumpf
   */
  public void showPossibleMoveOnHover() {
    rc3.setFill(Color.rgb(230, 230, 250, 0.5));
  }
  
  /**
   * Removes the hover circle for indicating a move is possible
   * 
   * @author sistumpf
   */
  public void removePossibleMoveOnHover() {
    rc3.setFill(null);
  }
  
  /**
   * Shows a piece is attackable, on hovering
   * 
   * @author sistumpf
   */
  public void hoverAttackable() {
    int r=255;
    int g=94;
    int b=94;
    if(getStyle().contains("rgb")) {
      String[] rgb = getStyle().substring(getStyle().indexOf("(") +1,getStyle().indexOf(")")).split(",");
      r = Integer.parseInt(rgb[0]);
      g = Integer.parseInt(rgb[1]);
      b = Integer.parseInt(rgb[2]);
    }
    this.setStyle("-fx-background-color: rgb(" + r + "," + g + "," + b + ",0.1);" + "-fx-border-color: rgb(255,0,255,0.4); "
        + "-fx-border-width: 1.2px");
  }

  public void removeHoverAttackable() {
    if(getStyle().contains("-fx-border-color: rgb(" + 255 + "," + 0 + "," + 255 + ",0.4);"))
      setStyle("-fx-background-color: transparent;" + "-fx-border-color: black; "
        + "-fx-border-width: 1.2px ");
  }
  
  /**
   * Draws a bigger circle around the figure which is currently selected
   * 
   * @author Manuel Krakowski
   */
  public void showSelected() {
    this.setStyle("-fx-background-color: transparent;" + "-fx-border-color: black; "
        + "-fx-border-width: 1.2px ");
    createCircle2();
  }

  /**
   * resets the cell to is default-style
   * 
   * @author Manuel Krakowski
   */
  public void deselect() {
    if(getStyle().contains("-fx-border-color: red;"))
      setStyle(" -fx-border-color: black; " + "-fx-border-width: 1.2px ");
    active = false;
    rc.setFill(null);
    if (rc2 != null) {
      getBase().getChildren().remove(rc2);
    }
  }

  @Deprecated
  public void resetCircle() {
    rc.setFill(Color.WHITE);
  }

  /**
   * Creates the base of the Cell Where all objects on it are placed
   * 
   * @author Manuel Krakowski
   */
  public void createBase() {
    StackPane base = new StackPane();
    NumberBinding pos =
        Bindings.subtract(widthProperty().divide(2), base.widthProperty().divide(2));
    NumberBinding roundPos1 = Bindings.createIntegerBinding(() -> pos.intValue(), pos);
    NumberBinding pos2 =
        Bindings.subtract(widthProperty().divide(2), base.heightProperty().divide(2));
    NumberBinding roundPos2 = Bindings.createIntegerBinding(() -> pos2.intValue(), pos2);
    
    base.setStyle("-fx-background-color: transparent");
    base.setAlignment(Pos.CENTER);
    base.prefWidthProperty().bind(widthProperty().multiply(0.8));
    base.prefHeightProperty().bind(widthProperty().multiply(0.8));
    base.maxWidthProperty().bind(widthProperty().multiply(0.8));
    base.maxHeightProperty().bind(widthProperty().multiply(0.8));
    base.layoutXProperty().bind(roundPos1);
    base.layoutYProperty().bind(roundPos2);
    base.setOnMouseClicked(new EventHandler<MouseEvent>() {

      @Override
      public void handle(MouseEvent e) {
        if (active) {
          performClickOnCell();
        } else if (!occupied && MoveVisualizer.isCurrentlyHovering() ) {
            MoveVisualizer.removeHoverPossibleMoves(child);
        } else if (!occupied && MoveVisualizer.isCurrentlySelected()) {
          MoveVisualizer.deselectFigure();
        }
      }
    });
    this.base = base;
    this.getChildren().add(base);
  }
  
  /**
   * When the cell is clicked a move-request is sent
   * 
   * @author Manuel Krakowski
   */
  public void performClickOnCell() {
    SoundController.playSound(MoveVisualizer.getCurrent().getPiece().getDescription().getType(),
        SoundType.MOVE);
    int[] xk = {x, y};
    MoveVisualizer.makeMoveRequest(xk);
  }


  // Getters and Setters
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
  
  public boolean isActive() {
    return active;
  }

  public boolean isOccupied() {
    return occupied;
  }

  public CustomFigurePane getChild() {
    return child;
  }

  public void setUActive() {
    this.active = false;
  }

  public StackPane getBase() {
    return base;
  }
}
