package org.ctf.UI.customObjects;

import org.ctf.UI.Game;

import javafx.beans.binding.Bindings;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
/**
 * @author mkrakows
 * just for the purpose of resizing. this class contains a pane with a costumFigure placed in the middle of it. This pane
 * is later placed on top of a backgroundCell in the GridPane when the figure is on the map
 */
public class BlueFlagRepV1 extends Pane {
	BackgroundCell parent;
	Game quatsch;
	public BlueFlagRepV1(int size, BackgroundCell parentCell) {
		this.parent = parentCell;
	Image bImage = new Image(getClass().getResourceAsStream("flagBlue.png"));
	CostumFigurePain rc = new CostumFigurePain(bImage, "Queen",parent,quatsch);
	rc.prefWidthProperty().bind(Bindings.divide(widthProperty(), 2));
	rc.prefHeightProperty().bind(Bindings.divide(heightProperty(), 2));
	rc.layoutXProperty().bind(Bindings.subtract(widthProperty().divide(2), rc.widthProperty().divide(2)));
	rc.layoutYProperty().bind(Bindings.subtract(widthProperty().divide(2), rc.heightProperty().divide(2)));
//	Image bImage = new Image(getClass().getResourceAsStream("flagBlue.png"));
//	ImageView vw = new ImageView(bImage);
//	vw.fitWidthProperty().bind(rc.widthProperty());
//	vw.fitHeightProperty().bind(rc.heightProperty());
//	rc.getChildren().add(vw);
	getChildren().add(rc);
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Queen";
	}
}
