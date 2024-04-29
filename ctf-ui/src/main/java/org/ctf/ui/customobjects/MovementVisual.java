package org.ctf.ui.customobjects;

import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class MovementVisual extends GridPane {
	Circle[][] circles = new Circle[11][11];
	public MovementVisual(VBox root) {
		super();
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
				c.setFill(Color.BLACK);
				c.setOpacity(0);
				c.radiusProperty().bind(pane.widthProperty().multiply(0.35));
				pane.getChildren().add(c);
				circles[j][i] = c;
				if(i==5&&j==5) {
					c.setFill(Color.RED);
					c.setOpacity(1);

				}
				
			}
		}
	}
	
	public void updateMovementOptions(String event) {
		
	}
}
