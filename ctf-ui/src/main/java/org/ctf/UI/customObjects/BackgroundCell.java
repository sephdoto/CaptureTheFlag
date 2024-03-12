package org.ctf.UI.customObjects;

import javafx.scene.layout.StackPane;

/**
 * @mkrakows
 * This Class represents a cell of the GridPane, which can be accessed by its x,y coordinates
 * On this BackGroundCell figures can be placed
 */
public class BackgroundCell extends StackPane {
	 int x,y;
	    boolean occupied;
	    String name;

	    public BackgroundCell(int x, int y){
	        this.x = x;
	        this.y = y;
	        this.occupied = false;
	    }
	    
	    @Override
	    public String toString() {
	        String status;
	        if(this.occupied) status = "Occupied";
	        else status = "Not occupied";
	        //return "Square" + this.x + this.y + " - " + status;
	        return "Square";
	    }
	    
	    public int[] getPosition() {
	    	int[] s = {x,y};
	    	return s;
	    }
	    
	    
}
