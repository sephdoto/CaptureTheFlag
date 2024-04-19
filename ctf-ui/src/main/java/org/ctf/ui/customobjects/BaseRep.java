package org.ctf.ui.customobjects;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class BaseRep extends Pane {
	int posX;	
	int posY;
	Label label;
	int flags;
	String teamID;
	String teamColor;
	
	public BaseRep(int flags, String color, String teamID) {
		teamColor = color;
		this.flags = flags;
		this.teamID = teamID;
		showBaseInformationWhenHovering();
		label = new Label(String.valueOf(flags));
		label.setFont(javafx.scene.text.Font.font(20));
		label.fontProperty().bind(
				Bindings.createObjectBinding(() -> Font.font(this.getWidth() / 2), this.widthProperty()));
	
		label.prefWidthProperty().bind(this.widthProperty());
		label.prefHeightProperty().bind(this.heightProperty());
		label.setAlignment(Pos.BOTTOM_CENTER);
		label.setStyle("-fx-background-color: " + color + "; -fx-border-color: black; -fx-font-weight: bold; -fx-text-fill: white;  -fx-shape: 'M  150 0 L75 200 L225 200  z'");
		this.getChildren().add(label);
	}
	
	public void showBaseInformationWhenHovering() {
		String pieceInfos = "base of team " + teamID;
		Tooltip tooltip = new Tooltip(pieceInfos);
		tooltip.setStyle("-fx-text-fill: " + teamColor + ";");
		Duration delay = new Duration(1);
		tooltip.setShowDelay(delay);
		Duration displayTime = new Duration(10000);
		tooltip.setShowDuration(displayTime);
		tooltip.setFont(new Font(15));
		this.setPickOnBounds(true);
		Tooltip.install(this, tooltip);
	}
}
