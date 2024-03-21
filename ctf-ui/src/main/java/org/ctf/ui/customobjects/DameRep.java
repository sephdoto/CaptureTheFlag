package org.ctf.ui.customobjects;

import javafx.beans.binding.Bindings;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class DameRep extends CostumFigurePain {
	public DameRep(String name) {
		super( name);
		super.bImage = new Image(getClass().getResourceAsStream("flagRed.png"));
		super.setImage();
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Dame";
	}
}
