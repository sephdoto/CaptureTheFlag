package org.ctf.ui.customobjects;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

public class PopUpPane extends StackPane {
	StackPane popUp;
	public PopUpPane(Scene scene,double width,double height) {
		this.getStyleClass().add("blur-pane");
		StackPane popUp = new StackPane();
		popUp.maxWidthProperty().bind(this.widthProperty().multiply(width));
		popUp.maxHeightProperty().bind(this.heightProperty().multiply(height));
		popUp.getStyleClass().add("pop-up-pane");
		this.getChildren().add(popUp);
	}
	public void setContent(Parent parent) {
		popUp.getChildren().add(parent);
	}
	public StackPane getPopUp() {
		return popUp;
	}
}
