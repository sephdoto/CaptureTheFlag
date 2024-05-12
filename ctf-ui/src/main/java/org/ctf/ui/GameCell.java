package org.ctf.ui;

import javafx.beans.binding.Bindings;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

public class GameCell extends Pane {
	public GameCell() {
		this.setStyle("-fx-border-color: black");
        final Circle circle = new Circle(10);
        circle.radiusProperty().bind(Bindings.divide(widthProperty(), 4));

        circle.centerXProperty().bind(widthProperty().divide(2));
        circle.centerYProperty().bind(widthProperty().divide(2));

        getChildren().add(circle);
    }
}
