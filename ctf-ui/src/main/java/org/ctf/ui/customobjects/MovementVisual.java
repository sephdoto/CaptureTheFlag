package org.ctf.ui.customobjects;

import java.util.function.Consumer;

import org.ctf.ui.TemplateEngine;

import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Displays a visual representation for the movement options for a custom 
 * piece in the map editor. Consists of a grid of StackPanes and a connection
 * to a TemplateEngine to update current move option.
 * 
 * @author aniemesc
 */
public class MovementVisual extends GridPane {
	Circle[][] circles = new Circle[11][11];
	TemplateEngine engine;

	/**
	 * Initializes basic grid  
	 * @author aniemesc 
	 * @param root - VBox container within the editor scene
	 * @param engine - TemplateEngine
	 */
	public MovementVisual(VBox root, TemplateEngine engine) {
		super();
		this.engine = engine;
		StackPane.setAlignment(this, Pos.CENTER);
		for (int i = 0; i < 11; i++) {
			for (int j = 0; j < 11; j++) {
				StackPane pane = new StackPane();
				pane.setStyle("-fx-border-color: rgba(0,0,0,1);" + "	 -fx-border-width: 2px;"
						+ "	 -fx-background-color: rgba(255,255,255);");
				pane.prefWidthProperty().bind(root.widthProperty().divide(11));
				pane.prefHeightProperty().bind(root.heightProperty().divide(11));
				this.add(pane, i, j);
				Circle c = new Circle();
				//c.setFill(Color.BLACK);
				c.getStyleClass().add("move-circle");
				c.setOpacity(0);
				c.radiusProperty().bind(pane.widthProperty().multiply(0.3));
				pane.getChildren().add(c);
				circles[j][i] = c;
				if (i == 5 && j == 5) {
					c.setStyle("-fx-fill: red;");
					c.setOpacity(1);

				}

			}
		}
	}

	/**
	 * Creates Consumer Objects used for updating the grid. Consumers change 
	 * opacity of circle objects according to String value .
	 * @author aniemesc
	 * @param direc - String value for direction
	 */
	public void updateMovementOptions(String direc) {
		switch (direc) {
		case "Left":
			Consumer<Integer[]> changeLeft = (arr) -> {
				circles[5][5+(5-arr[0])].setOpacity((arr[1]==0)?0:0.4);
			};
			handle(engine.getTmpMovement().getDirections().getLeft(),changeLeft);
			break;
		case "Right":
			Consumer<Integer[]> changeRight = (arr) -> {
				circles[5][arr[0]].setOpacity((arr[1]==0)?0:0.4);
			};
			handle(engine.getTmpMovement().getDirections().getRight(),changeRight);
			break;
		case "Up":
			Consumer<Integer[]> changeUp = (arr) -> {
				circles[5+(5-arr[0])][5].setOpacity((arr[1]==0)?0:0.4);
			};
			handle(engine.getTmpMovement().getDirections().getUp(),changeUp);
			break;
		case "Down":
			Consumer<Integer[]> changeDown = (arr) -> {
				circles[arr[0]][5].setOpacity((arr[1]==0)?0:0.4);
			};
			handle(engine.getTmpMovement().getDirections().getDown(),changeDown);
			break;
		case "Up-Left":
			Consumer<Integer[]> changeUpLeft = (arr) -> {
				circles[5+(5-arr[0])][5+(5-arr[0])].setOpacity((arr[1]==0)?0:0.4);
			};
			handle(engine.getTmpMovement().getDirections().getUpLeft(),changeUpLeft);
			break;
		case "Up-Right":
			Consumer<Integer[]> changeUpRight = (arr) -> {
				circles[5+(5-arr[0])][arr[0]].setOpacity((arr[1]==0)?0:0.4);
			};
			handle(engine.getTmpMovement().getDirections().getUpRight(),changeUpRight);
			break;
		case "Down-Left":
			Consumer<Integer[]> changeDownLeft = (arr) -> {
				circles[arr[0]][5+(5-arr[0])].setOpacity((arr[1]==0)?0:0.4);
			};
			handle(engine.getTmpMovement().getDirections().getDownLeft(),changeDownLeft);
			break;
		case "Down-Right":
			Consumer<Integer[]> changeDownRight = (arr) -> {
				circles[arr[0]][arr[0]].setOpacity((arr[1]==0)?0:0.4);
			};
			handle(engine.getTmpMovement().getDirections().getDownRight(),changeDownRight);
			break;
		default:
			System.out.println("Unknown");
			break;
		}
	}
	
	/**
	 * Updates grid by executing consumer.
	 * @author aniemesc
	 * @param till - int value equal to (length of grid/2)
	 * @param change - Consumer that takes grid coordinate and opacity value
	 */
	private void handle(int till, Consumer<Integer[]> change) {
		if (6 + till > circles.length) {
			return;
		}
		for (int i = 6; i < circles.length; i++) {
			Integer[] arr = {i,0};
			change.accept(arr);
		}
		for (int i = 6; i < 6 + till; i++) {
			Integer[] arr = {i,1};
			change.accept(arr);
		}
	}
}
