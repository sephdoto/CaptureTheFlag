package org.ctf.ui.customobjects;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class BackgroundCellV3 extends StackPane {
	public int x, y;
	boolean occupied;
	String name;
	//public Circle rc;
	//public Circle rc2;
	public boolean active;
	CostumFigurePain child;


public BackgroundCellV3(int x, int y) {
	
	this.setStyle("-fx-background-color: baa98e;" + "-fx-border-color: black; " + "-fx-border-width: 1px ");
	this.x = x;
	this.y = y;
	this.occupied = false;
}

public void addFigure(CostumFigurePain figure) {
	occupied = true;
	child = figure;
	child.prefWidthProperty().bind(Bindings.divide(widthProperty(), 2));
	child.prefHeightProperty().bind(Bindings.divide(heightProperty(), 2));
	child.layoutXProperty().bind(Bindings.subtract(widthProperty().divide(2), child.widthProperty().divide(2)));
	child.layoutYProperty().bind(Bindings.subtract(widthProperty().divide(2), child.heightProperty().divide(2)));
	this.getChildren().add(child);
}

public void addBlock() {
	occupied = true;
	BlockRepV3 blocki = new BlockRepV3();
	blocki.prefWidthProperty().bind(Bindings.divide(widthProperty(), 2));
	blocki.prefHeightProperty().bind(Bindings.divide(heightProperty(), 2));
	blocki.layoutXProperty().bind(Bindings.subtract(widthProperty().divide(2), blocki.widthProperty().divide(2)));
	blocki.layoutYProperty().bind(Bindings.subtract(widthProperty().divide(2), blocki.heightProperty().divide(2)));
	this.getChildren().add(blocki);
	
}

public void addBasis(BaseRep r) {
	occupied = true;
	BaseRep basis = r;
	basis.prefWidthProperty().bind(Bindings.divide(widthProperty(), 2));
	basis.prefHeightProperty().bind(Bindings.divide(heightProperty(), 2));
	basis.layoutXProperty().bind(Bindings.subtract(widthProperty().divide(2), basis.widthProperty().divide(2)));
	basis.layoutYProperty().bind(Bindings.subtract(widthProperty().divide(2), basis.heightProperty().divide(2)));
	this.getChildren().add(basis);
}




}
