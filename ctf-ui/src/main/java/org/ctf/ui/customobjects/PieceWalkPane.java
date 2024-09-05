package org.ctf.ui.customobjects;

import org.ctf.shared.ai.GameUtilities;
import org.ctf.shared.constants.Enums.ImageType;
import org.ctf.shared.state.Piece;
import org.ctf.shared.state.data.map.Directions;
import org.ctf.shared.state.data.map.Movement;
import org.ctf.shared.state.data.map.Shape;
import org.ctf.shared.state.data.map.ShapeType;
import org.ctf.ui.controllers.ImageController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

/**
 * Indicates how a Piece walks, in which Direction or in what Shape.
 * inspired by {@link MovementVisual.java}
 * 
 * @author sistumpf
 */
public class PieceWalkPane extends GridPane {
  int[] xTransforms;
  int[] yTransforms;
  Circle[][] circles;
  Label[][] labels;
  StackPane middle;
  private ObjectProperty<Font> labelSize;
  
  public PieceWalkPane(VBox root) {
    super(0,0);
    this.setAlignment(Pos.CENTER);
    this.setPrefWidth(1);
    createLabelBindings();
    circles = new Circle[5][5];
    labels = new Label[5][5];
    for (int x = 0; x < 5; x++) {
      for (int y = 0; y < 5; y++) {
        createComponent(x,y);
      }
    }
  }
  
  /**
   * Updates the Circles to represent movement in a Direction
   * 
   * @param piece
   */
  public void update(Piece piece) {
    clearPane();
    addPicture(piece.getDescription().getType());
    Movement movement = piece.getDescription().getMovement();
    if(movement.getShape() == null)
      handleDirections(movement.getDirections());
    else 
      handleShapes(movement.getShape());
  }
  

  /////////////////////////////////////////////////
  ///           Handling Directions             ///
  /////////////////////////////////////////////////
  
  private void handleDirections(Directions directions) {
    xTransforms = new int[] {-1, 1, 0, 0, -1, 1, -1, 1, /* inner layer */ -2, 2, 0, 0, -2, 2, -2, 2};
    yTransforms = new int[] {0, 0, -1, 1, -1, -1, 1, 1, /* inner layer */ 0, 0, -2, 2, -2, -2, 2, 2};

    for(int i=0; i<8; i++) {
      if(GameUtilities.getReach(directions, i) > 0)
        showCircle(2+yTransforms[i], 2+xTransforms[i], "");
    }
    for(int i=8; i<16; i++) {
      int reach = GameUtilities.getReach(directions, i-8);
      if(reach > 2)
        showCircle(2+yTransforms[i], 2+xTransforms[i], "" + (reach -1));
      else if (reach == 2)
        showCircle(2+yTransforms[i], 2+xTransforms[i], "");
    }
  }
  
  private void handleShapes(Shape shape) {
    if(shape.getType() == ShapeType.lshape) {
      xTransforms = new int[] {-2, -2, -2, -1, 0, 1, 2, 2, 2, 1, 0, -1, /*inner layer*/ -1, 0, 1, 0};
      yTransforms = new int[] {-1, 0, 1, 2, 2, 2, 1, 0, -1, -2, -2, -2, /*inner layer*/ 0, 1, 0, -1};
      handleLShape();
    }
  }
  private void handleLShape() {
    for (int i = 0; i < xTransforms.length; i++) {
      showCircle(2+xTransforms[i], 2+yTransforms[i], "");
    }
  }
  

  
  /////////////////////////////////////////////////
  ///           Creating Components             ///
  /////////////////////////////////////////////////
  /**
   * Shows a circle at y,x
   * 
   * @param x x coordinate in GridPane
   * @param y y coordinate in GridPane
   * @param reach the reach into a direction to display on the circle
   */
  void showCircle(int y, int x, String reach) {
    circles[y][x].setOpacity(1);
    labels[y][x].setText(reach);
  }
  
  /**
   * Clears the whole Pane
   */
  void clearPane() {
    for(int y=0; y<circles.length; y++)
      for(int x=0; x<circles[y].length; x++) {
        circles[y][x].setOpacity(0);
        labels[y][x].setText("");
      }
  }
  
  /**
   * Creates a StackPane at x,y and places a Circle into it
   * 
   * @param x x coordinate in GridPane
   * @param y y coordinate in GridPane
   */
  private void createComponent(int x, int y) {
    StackPane pane = new StackPane();
    pane.getStyleClass().add("move-pane");
    pane.setPrefHeight(1);
    pane.setPrefWidth(1);
    pane.prefWidthProperty().bind(widthProperty().divide(7));
    pane.prefHeightProperty().bind(pane.prefWidthProperty());
    pane.minHeightProperty().bind(pane.prefHeightProperty());
    this.add(pane, x, y);
    
    // add Circle
    Circle c = createCircle(pane);
    circles[y][x] = c;
    
    Label l = createLabel(pane);
    labels[y][x] = l;
    
    // add Piece Image
    if (x == 2 && y == 2) {
      middle = pane;
      addPicture("Default");
    }
  }
  
  private void addPicture(String pieceName) {
    middle.getChildren().clear();
    Image piece = ImageController.loadThemedImage(ImageType.PIECE, pieceName);
    ImageView pieceView = new ImageView(piece);
    pieceView.fitWidthProperty().bind(middle.widthProperty().multiply(0.6));
    pieceView.fitHeightProperty().bind(pieceView.fitWidthProperty());
    middle.getChildren().add(pieceView);
  }
  
  private Label createLabel(StackPane pane) {
    Label l = new Label();
    l.fontProperty().bind(labelSize);
    l.getStyleClass().add("move-label");
    pane.getChildren().add(l);
    return l;
  }
  
  private void createLabelBindings() {
    labelSize = new SimpleObjectProperty<Font>(Font.font(this.getWidth() / 15));
    widthProperty().addListener(new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth,
          Number newWidth) {
        labelSize.set(Font.font(newWidth.doubleValue() / 15));
      }
    });

  }
  
  /**
   * Creates a circle, binds and adds it to a StackPane
   * @param pane StackPane to add the circle to
   * @return the finished Circle
   */
  private Circle createCircle(StackPane pane) {
    Circle c = new Circle();
    c.getStyleClass().add("move-circle");
    c.setOpacity(0);
    c.radiusProperty().bind(pane.widthProperty().multiply(0.3));
    pane.getChildren().add(c);
    return c;
  }
}
