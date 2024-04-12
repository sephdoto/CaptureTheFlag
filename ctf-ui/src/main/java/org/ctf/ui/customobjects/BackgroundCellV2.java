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

public class BackgroundCellV2 extends Pane {
	public int x, y;
	boolean occupied;
	String name;
	public Circle rc;
	public boolean active;
	//public Game game;
	public StackPane base;
	CostumFigurePain child;

	public BackgroundCellV2(int x, int y) {
		this.setStyle("-fx-background-color: white;" + "-fx-border-color: black; " + "-fx-border-width: 1.5px ");
		this.x = x;
		this.y = y;
		this.occupied = false;
		this.createBase();
		createCircle();
	}

	public void createCircle() {
		rc = new Circle();
		rc.radiusProperty().bind(Bindings.divide(widthProperty(), 6));
		rc.centerXProperty().bind(widthProperty().divide(2));
		rc.centerYProperty().bind(widthProperty().divide(2));
		rc.setFill(Color.WHITE);
		base.getChildren().add(rc);
	}

	public void addFigure(CostumFigurePain figure) {
		occupied = true;
		child = figure;
		base.getChildren().add(child);
		figure.setParente(this);
	}
	

	public void addBlock(BlockRepV3 block) {
		occupied = true;
		BlockRepV3 blocki = block;
		base.getChildren().clear();
		base.getChildren().add(blocki);
	}

//	public Game getGame() {
//		return game;
//	}
//
//	public void setGame(Game game) {
//		this.game = game;
//	}

	public int[] getPosition() {
		int[] s = { x, y };
		return s;
	}

	public void createBase() {
		StackPane base = new StackPane();
		//base.setStyle("-fx-background-color: blue");
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
			//rc.setFill(Color.RED);
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
