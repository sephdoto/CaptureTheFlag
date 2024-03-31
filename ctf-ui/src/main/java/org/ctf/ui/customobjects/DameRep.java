package org.ctf.ui.customobjects;

import javafx.beans.binding.Bindings;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class DameRep extends CostumFigurePain {
	public DameRep(String name) {
		super( name);
		Image g = new Image(getClass().getResourceAsStream("flagRed.png"));
		super.setImage(g);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Dame";
	}
}
