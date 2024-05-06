package org.ctf.ui.customobjects;

import org.ctf.ui.Game;
import org.ctf.ui.GamePane;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
		testColor = Color.rgb(173, 216, 230, 0.7);
		this.setStyle( "-fx-border-color: black; " + "-fx-border-width: 2px ");
	//	this.setStyle( "-fx-background-color: rgb(173, 216, 230, 0.3)");

		this.x = x;
		this.y = y;
		this.occupied = false;
		//this.maxHeightProperty().bind(pane.gridPane.heightProperty().divide(rows));
		//this.maxWidthProperty().bind(pane.gridPane.widthProperty().divide(cols));
		this.createBase();
		//createCircle2();
		createCircle();
	}

	public void createCircle() {
		NumberBinding binding = Bindings.divide(widthProperty(), 6);
		NumberBinding roundSize = Bindings.createIntegerBinding(() ->  binding.intValue(), binding);
		NumberBinding binding2 = Bindings.divide(widthProperty(), 2);
		NumberBinding roundSize2 = Bindings.createIntegerBinding(() ->  binding2.intValue(), binding2);
		rc = new Circle();
		rc.radiusProperty().bind(roundSize);
		rc.centerXProperty().bind(roundSize2);
		rc.centerYProperty().bind(roundSize2);
		rc.setFill(null);
		base.getChildren().add(rc);
	}
	public void createCircle2() {
		NumberBinding binding = Bindings.divide(widthProperty(), 2.8);
		NumberBinding roundSize = Bindings.createIntegerBinding(() ->  binding.intValue(), binding);
		NumberBinding binding2 = Bindings.divide(widthProperty(), 2);
		NumberBinding roundSize2 = Bindings.createIntegerBinding(() ->  binding2.intValue(), binding2);
		rc2 = new Circle();
		rc2.radiusProperty().bind(roundSize);
		rc2.centerXProperty().bind(roundSize2);
		rc2.centerYProperty().bind(roundSize2);
		rc2.setFill(null);
		rc2.setStroke(Color.rgb(173, 216, 230, 0.5));
		rc2.setStrokeWidth(3);
		base.getChildren().add(rc2);
	}

	public void addFigure(CostumFigurePain figure) {
		NumberBinding binding = Bindings.multiply(widthProperty(), 0.5);
		NumberBinding roundSize = Bindings.createIntegerBinding(() ->  binding.intValue(), binding);
		base.getChildren().remove(rc);
		occupied = true;
		child = figure;
		child.maxWidthProperty().bind(roundSize);
		child.maxHeightProperty().bind(roundSize);
		base.getChildren().add(child);
		figure.setParente(this);
	}
	

	public void addBlock() {
		NumberBinding binding = Bindings.multiply(widthProperty(), 0.5);
		NumberBinding roundSize = Bindings.createIntegerBinding(() ->  binding.intValue(), binding);
		occupied = true;
		BlockRepV3 blocki = new BlockRepV3();
		blocki.maxWidthProperty().bind(roundSize);
		blocki.maxHeightProperty().bind(roundSize);
		base.getChildren().clear();
		base.getChildren().add(blocki);
		
	}

	public void addBasis(BaseRep r) {
		NumberBinding binding = Bindings.multiply(widthProperty(), 0.5);
		NumberBinding roundSize = Bindings.createIntegerBinding(() ->  binding.intValue(), binding);
		occupied = true;
		BaseRep basis = r;
		basis.maxWidthProperty().bind(roundSize);
		basis.maxHeightProperty().bind(roundSize);
		base.getChildren().clear();
		base.getChildren().add(basis);
	}
	
	
	
	public void showattackCircle() {
		this.setStyle("-fx-background-color: rgb(255, 0, 0, 0.2);" + "-fx-border-color: red; " + "-fx-border-width: 2px");
		//this.setStyle("-fx-background-color: red;");
		
		
		//rc.setFill(Color.RED);
		//createCircle2();
		
	}
	
	public void testCircle() {
		rc.setFill(Color.WHITE);
	}
	
	public void showPossibleMove() {
		rc.setFill(testColor);
        active = true;
	}
	
	public void showSelected() {
		this.setStyle("-fx-background-color: transparent;" + "-fx-border-color: black; " + "-fx-border-width: 2px ");
		//rc.setFill(testColor);
		//rc2.setFill(testColor);
		createCircle2();
	}
	public void deselect() {
		this.setStyle( " -fx-border-color: black; " + "-fx-border-width: 2px ");
		this.active = false;
		rc.setFill(null);
		if(rc2 != null) {
			base.getChildren().remove(rc2);
		}
		//rc2.setFill(Color.WHITE);
		
	}

	public int[] getPosition() {
		int[] s = { x, y };
		return s;
	}
	
	public void createBase() {
		StackPane base = new StackPane();
		NumberBinding binding = Bindings.multiply(widthProperty(), 0.8);
		NumberBinding roundSize = Bindings.createIntegerBinding(() ->  binding.intValue(), binding);
		NumberBinding pos = Bindings.subtract(widthProperty().divide(2), base.widthProperty().divide(2));
		NumberBinding roundPos1 = Bindings.createIntegerBinding(() ->  pos.intValue(), pos);
		NumberBinding pos2 = Bindings.subtract(widthProperty().divide(2), base.heightProperty().divide(2));
		NumberBinding roundPos2 = Bindings.createIntegerBinding(() ->  pos2.intValue(), pos2);
		
		
		base.setStyle("-fx-background-color: transparent");
		base.setAlignment(Pos.CENTER);
		base.prefWidthProperty().bind(roundSize);
		//base.prefHeightProperty().bind(base.widthProperty());
		base.prefHeightProperty().bind(roundSize);
		//base.maxHeightProperty().bind(widthProperty().multiply(0.8));
		//base.maxWidthProperty().bind(widthProperty().multiply(0.8));
		//base.maxHeightProperty().bind(base.maxWidthProperty());
		base.layoutXProperty().bind(roundPos1);
		base.layoutYProperty().bind(roundPos2);
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
