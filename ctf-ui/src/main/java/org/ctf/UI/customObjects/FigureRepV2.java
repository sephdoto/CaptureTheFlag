package org.ctf.ui.customobjects;

import javafx.beans.binding.Bindings;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
/**
 * @mkrakows
 * This class contains a circle in a pane, which size is scalable with the size of the GridPane in which it is placed
 */
public class FigureRepV2 extends Pane {
	public FigureRepV2(int size, Color color) {
		Circle circle = new Circle(size);
		circle.setFill(color);
		circle.radiusProperty().bind(Bindings.divide(widthProperty(), 4));

		circle.centerXProperty().bind(widthProperty().divide(2));
		circle.centerYProperty().bind(widthProperty().divide(2));
		getChildren().add(circle);
	}
}
