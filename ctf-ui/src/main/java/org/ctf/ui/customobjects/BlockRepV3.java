package org.ctf.ui.customobjects;

import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class BlockRepV3 extends Region {
	private Rectangle rc;
	public BlockRepV3() {
		rc = new Rectangle();
		rc.setFill(Color.BLACK);
		rc.setStroke(Color.BLACK);
		rc.setStrokeWidth(6);
		getChildren().add(rc);
	}
	protected void layoutChildren() {
		super.layoutChildren();
		rc.setWidth(this.getWidth());
		rc.setHeight(this.getHeight());

	}
}
