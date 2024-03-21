package org.ctf.ui.customobjects;

import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * @mkrakows
 * This Class represents a cell of the GridPane, which can be accessed by its x,y coordinates
 * On this BackGroundCell figures can be placed
 */
public class BackgroundCell extends Region {
	public int x,y;
	    public boolean occupied;
	    String name;
	    public Circle rc;
	    public boolean active;
	    
	    public BackgroundCell(int x, int y){
	    	
	    	this.setStyle("-fx-background-color: white;"
	    			+ "-fx-border-color: grey; "
	    			+ "-fx-border-width: 1.5px ");
	    	//this.setStyle("-fx-border-color: red");
	    	rc = new Circle();
			rc.setFill(Color.WHITE);      
			this.x = x;
	        this.y = y;
	        this.occupied = false;
	        this.getChildren().add(rc);
	        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
	    		
	    		@Override
	    		public void handle(MouseEvent e) {
	    			if(active) {
	    				rc.setFill(Color.RED);
	    				
	    			}
	    		}
	    	});
	        
	    }
	    public int[] getPosition() {
	    	int[] s = {x,y};
	    	return s;
	    }
	    
	    public void addFigure(CostumFigurePain figure) {
	    	occupied = true;
	    	this.getChildren().remove(rc);
	    	CostumFigurePain rc = figure;
	    	rc.prefWidthProperty().bind(Bindings.divide(widthProperty(), 2));
	    	rc.prefHeightProperty().bind(Bindings.divide(heightProperty(), 2));
	    	rc.layoutXProperty().bind(Bindings.subtract(widthProperty().divide(2), rc.widthProperty().divide(2)));
	    	rc.layoutYProperty().bind(Bindings.subtract(widthProperty().divide(2), rc.heightProperty().divide(2)));
	    	getChildren().add(rc);
	    	figure.setParent(this);
	    }
	    public void addBlock(BlockRepV3 block) {
	    	occupied = true;
	    	this.getChildren().clear();
	    	BlockRepV3 blocki = block;
	    	blocki.prefWidthProperty().bind(Bindings.divide(widthProperty(), 2.5));
	    	blocki.prefHeightProperty().bind(Bindings.divide(heightProperty(), 2.5));
	    	blocki.layoutXProperty().bind(Bindings.subtract(widthProperty().divide(2), blocki.widthProperty().divide(2)));
	    	blocki.layoutYProperty().bind(Bindings.subtract(widthProperty().divide(2), blocki.heightProperty().divide(2)));
	    	getChildren().add(blocki);
	    	
	    }
	    protected void layoutChildren() {
			super.layoutChildren();
			rc.radiusProperty().bind(Bindings.divide(widthProperty(), 5));
			rc.centerXProperty().bind(widthProperty().divide(2));
			rc.centerYProperty().bind(widthProperty().divide(2));
			

		}
	    
	    
}
