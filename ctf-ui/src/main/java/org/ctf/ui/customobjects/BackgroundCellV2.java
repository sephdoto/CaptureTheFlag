package org.ctf.ui.customobjects;

import org.ctf.ui.Game;

import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class BackgroundCellV2 extends Pane {
	public int x,y;
    public boolean occupied;
    String name;
    public Circle rc;
    public boolean active;
    public Game game;
	StackPane base;
	
	public BackgroundCellV2(int x, int y){
    	this.setStyle("-fx-background-color: white;"
    			+ "-fx-border-color: grey; "
    			+ "-fx-border-width: 1.5px ");   
		this.x = x;
        this.y = y;
        this.occupied = false;
        this.createBase();
        createCircle();
		
        
    }
	
	public void createCircle() {
		rc = new Circle();
        rc.radiusProperty().bind(Bindings.divide(widthProperty(), 5));
		rc.centerXProperty().bind(widthProperty().divide(2));
		rc.centerYProperty().bind(widthProperty().divide(2));
		rc.setFill(Color.WHITE); 
		base.getChildren().add(rc);
	}
	
	  public void addFigure(CostumFigurePain figure) {
	    	occupied = true;
	    	CostumFigurePain rc = figure;
	    	base.getChildren().add(rc);
	    	figure.setParente(this); 
	    }
	  
	  public void addBlock(BlockRepV3 block) {
	    	occupied = true;
	    	BlockRepV3 blocki = block;
	    	base.getChildren().clear();
	    	base.getChildren().add(blocki);
	    }
	  public int[] getPosition() {
	    	int[] s = {x,y};
	    	return s;
	    }
	
	
	
	public void createBase() {
    	StackPane base = new StackPane();
    	base.prefWidthProperty().bind(Bindings.divide(widthProperty(), 2.5));
    	base.prefHeightProperty().bind(Bindings.divide(heightProperty(), 2.5));
    	base.layoutXProperty().bind(Bindings.subtract(widthProperty().divide(2), base.widthProperty().divide(2)));
    	base.layoutYProperty().bind(Bindings.subtract(widthProperty().divide(2), base.heightProperty().divide(2)));
    	base.setOnMouseClicked(new EventHandler<MouseEvent>() {
	    		
	    		@Override
	    		public void handle(MouseEvent e) {
	    			if(active) {
	    				rc.setFill(Color.WHITE);
	    				int[] xk = {x,y};
	    				game.makeMove(xk);
	    				
	    			}
	    		}
	    	});
    	this.base = base;
    	this.getChildren().add(base);
    }
}
