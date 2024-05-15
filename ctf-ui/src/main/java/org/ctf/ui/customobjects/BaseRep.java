package org.ctf.ui.customobjects;


import org.ctf.ui.CretaeGameScreenV2;
import org.ctf.ui.MyCustomColorPicker;
import org.ctf.ui.PlayGameScreenV2;
import org.ctf.ui.WaitingScene;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class BaseRep extends Pane {
	int posX;	
	int posY;
	Label label;
	int flags;
	String teamID;
	String teamColor;
	PlayGameScreenV2 scene;
	private final EventHandler<MouseEvent> clickHandler;
	
	
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
		label.setStyle("-fx-background-color: white ; -fx-border-color: black; -fx-font-weight: bold; -fx-shape: 'M  150 0 L75 200 L225 200  z'");
		//label.setOpacity(0);
		this.getChildren().add(label);
		 clickHandler = event -> {
	            // Rufe die Methode zur Verarbeitung des Mausklicks in einer anderen Klasse auf
	            scene.showColorChooser(event.getSceneX(), event.getSceneY(),this);
	        };
	     this.setOnMouseClicked(clickHandler);
            
	}
	
	public void showColor(ObjectProperty<Color> sceneColorProperty) {
		label.textFillProperty().bind(sceneColorProperty);
	}
	
	public String getTeamID() {
		return teamID;
	}

	public void setTeamID(String teamID) {
		this.teamID = teamID;
	}

	public void showBaseInformationWhenHovering() {
		String pieceInfos = "base of team " + teamID;
		Tooltip tooltip = new Tooltip(pieceInfos);
		//tooltip.setStyle("-fx-text-fill: " + teamColor + ";");
		Duration delay = new Duration(1);
		tooltip.setShowDelay(delay);
		Duration displayTime = new Duration(10000);
		tooltip.setShowDuration(displayTime);
		tooltip.setFont(new Font(15));
		this.setPickOnBounds(true);
		Tooltip.install(this, tooltip);
	}

	

	public void setScene(PlayGameScreenV2 scene) {
		this.scene = scene;
	}

	
	
}
