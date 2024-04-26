package org.ctf.ui.customobjects;

import org.ctf.ui.Game;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

public class BackgroundCellV2 extends Pane {
	public int x, y;
	boolean occupied;
	String name;
	public Circle rc;
	public Circle rc2;
	public boolean active;
	//public Game game;
	public StackPane base;
	CostumFigurePain child;
	Color testColor;

	public BackgroundCellV2(int x, int y) {
		testColor = Color.rgb(173, 216, 230, 0.5);
		this.setStyle("-fx-background-color: baa98e;" + "-fx-border-color: black; " + "-fx-border-width: 1px ");
		this.x = x;
		this.y = y;
		this.occupied = false;
		this.createBase();
		createCircle2();
		createCircle();
	}

	public void createCircle() {
		rc = new Circle();
		rc.radiusProperty().bind(Bindings.divide(widthProperty(), 6));
		rc.centerXProperty().bind(widthProperty().divide(2));
		rc.centerYProperty().bind(widthProperty().divide(2));
		rc.setFill(null);
		base.getChildren().add(rc);
	}
	public void createCircle2() {
		rc2 = new Circle();
		rc2.radiusProperty().bind(Bindings.divide(widthProperty(), 3));
		rc2.centerXProperty().bind(widthProperty().divide(2));
		rc2.centerYProperty().bind(widthProperty().divide(2));
		rc2.setFill(null);
		base.getChildren().add(rc2);
	}

	public void addFigure(CostumFigurePain figure) {
		rc.setFill(null);
		occupied = true;
		child = figure;
	
		child.maxWidthProperty().bind(Bindings.divide(widthProperty(), 2));
		child.maxHeightProperty().bind(Bindings.divide(heightProperty(), 2));
		
		base.getChildren().add(child);
		figure.setParente(this);
	}
	

	public void addBlock() {
		occupied = true;
		BlockRepV3 blocki = new BlockRepV3();
		base.getChildren().clear();
		base.getChildren().add(blocki);
		
	}

	public void addBasis(int flags, String color, String teamID) {
		occupied = true;
		BaseRep basis = new BaseRep(flags, color, teamID);
		base.getChildren().clear();
		base.getChildren().add(basis);
	}
	
	public void showCross() {
		 Line line1 = new Line();
	        line1.startXProperty().bind(base.widthProperty().divide(8));
	        line1.startYProperty().bind(base.heightProperty().divide(8));
	        line1.endXProperty().bind(base.widthProperty().multiply(7).divide(8));
	        line1.endYProperty().bind(base.heightProperty().multiply(7).divide(8));
	        Line line2 = new Line();
	        line2.startXProperty().bind(base.widthProperty().divide(8));
	        line2.startYProperty().bind(base.heightProperty().multiply(7).divide(8));
	        line2.endXProperty().bind(base.widthProperty().multiply(7).divide(8));
	        line2.endYProperty().bind(base.heightProperty().divide(8));
	        Color crossColor = Color.RED;
	        crossColor = crossColor.deriveColor(0, 1, 1, 0.8);
	        line1.setStroke(crossColor);
	        line1.setStrokeWidth(4);
	        line2.setStroke(crossColor);
	        line2.setStrokeWidth(4);
	        base.getChildren().addAll(line1, line2);
	}
	
	public void showattackCircle() {
		this.setStyle("-fx-background-color: red;" + "-fx-border-color: black; " + "-fx-border-width: 1px ");
	}
	
	public void showPossibleMove() {
		rc.setFill(testColor);
        active = true;
	}
	
	public void showSelected() {
		//this.setStyle("-fx-background-color: rgba(173, 216, 230, 0.5);" + "-fx-border-color: black; " + "-fx-border-width: 1.5px ");
		rc.setFill(testColor);
		rc2.setFill(testColor);
	}
	public void deselect() {
		this.setStyle("-fx-background-color: white;" + "-fx-border-color: black; " + "-fx-border-width: 1px ");
		this.active = false;
		rc.setFill(Color.WHITE);
		rc2.setFill(Color.WHITE);
		
	}

	public int[] getPosition() {
		int[] s = { x, y };
		return s;
	}
	
	public void createBase() {
		StackPane base = new StackPane();
		base.setAlignment(Pos.CENTER);
		base.prefWidthProperty().bind(Bindings.divide(widthProperty(), 2));
		base.prefHeightProperty().bind(Bindings.divide(heightProperty(), 2));
		base.layoutXProperty().bind(Bindings.subtract(widthProperty().divide(2), base.widthProperty().divide(2)));
		base.layoutYProperty().bind(Bindings.subtract(widthProperty().divide(2), base.heightProperty().divide(2)));
		base.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {
				if(active) {
				performClickOnCell();
				} else if(!occupied){
					Game.deselectFigure();
				}
			}
		});
		this.base = base;
		this.getChildren().add(base);
	}
	

	public void performClickOnCell() {
			int[] xk = { x, y };
			Game.makeMoveRequest(xk);
	}

	public void setActive() {
		this.active = true;
	}
	
	public boolean isOccupied() {
		return occupied;
	}
	public void removeFigure() {
		occupied = false;
		base.getChildren().remove(child);
		this.child = null;
		
	}
	
	public CostumFigurePain getChild() {
		return child;
	}
	
	public void setUActive() {
		this.active = false;
	}
}
