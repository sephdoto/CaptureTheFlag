package org.ctf.UI.customObjects;

/**
 * @author mkrakows
 * this class represents a region that for now just contains a rectangle that has  the size of the region
 * The color of the rectangle is set from the outside
 */

import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class FigureRepV1 extends Region {
	BackgroundRepV1 parentBackgroundRepV1;
	private Rectangle rc;

	public FigureRepV1(BackgroundRepV1 v, Color col) {
		parentBackgroundRepV1 = v;
		rc = new Rectangle();
		rc.setFill(col);
		rc.setStroke(Color.BLACK);
		rc.setStrokeWidth(2);
		getChildren().add(rc);
		bindToPArent();
	}

	public void bindToPArent() {
		this.prefHeightProperty().bind(parentBackgroundRepV1.heightProperty());
		this.prefWidthProperty().bind(parentBackgroundRepV1.widthProperty());
	}

	protected void layoutChildren() {
		super.layoutChildren();
		double rcHeight = this.getHeight() * 0.7;
		double rcWidth = this.getWidth() * 0.7;
		double x = (this.getWidth() - rcWidth) / 2;
		double y = (this.getHeight() - rcHeight) / 2;
		rc.setWidth(rcWidth);
		rc.setHeight(rcHeight);
		rc.setLayoutX(x);
		rc.setLayoutY(y);
	}
}
