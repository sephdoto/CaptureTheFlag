package org.ctf.UI.customObjects;


import javafx.beans.binding.Bindings;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * @mkrakows
 * Creates a Pane with a black rectangle in the middle of it which can be used to represent a block
 */
public class BlockRepV2 extends Pane {
	public BlockRepV2() {
		Rectangle rc = new Rectangle();
		rc.widthProperty().bind(Bindings.divide(widthProperty(), 2.5));
		rc.heightProperty().bind(Bindings.divide(heightProperty(), 2.5));
		rc.layoutXProperty().bind(Bindings.subtract(widthProperty().divide(2), rc.widthProperty().divide(2)));
		rc.layoutYProperty().bind(Bindings.subtract(widthProperty().divide(2), rc.heightProperty().divide(2)));
		getChildren().add(rc);
		
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Block";
	}
}