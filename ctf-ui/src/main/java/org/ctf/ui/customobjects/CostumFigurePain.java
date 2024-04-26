package org.ctf.ui.customobjects;



import org.ctf.ui.Game;
import org.ctf.ui.PlayGameScreen;


import configs.ImageLoader;

import java.util.ArrayList;


import org.ctf.shared.state.Piece;

import javafx.beans.property.ObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.util.Duration;
import test.CreateTextGameStates;

/**
 * @mkrakows
 * This class contains a visual Representation of a Piece
 */
public class CostumFigurePain extends Pane {
	//Achtung: DIe Position von dem Piece, das zu einem Costumfigurepain gehört kann abweichen von der Position
	//des CostumFigurePain, da das Piece sich während des Spiels bew
	String teamID;
	Piece piece;
	String type;
	Image bImage;
	ImageView vw;
	int posX;	
	int posY;
	boolean active;
	boolean attacable;
	BackgroundCellV2 parent;
	DropShadow borderGlow;
	

	ArrayList<int[]> possibleMoves;
	
	/**
	 * @author mkrakows
	 * A custom figure is created and a mouseListener is added to it. When clicking on the figure it is highlighted and the currently selected
	 * figure in game is set 
	 * {@link Game.current}
	 * @param bImage: one can choose an Image that should represent a figure when creating one
	 * @param name: Identifier of the Figure, Later the id should be used here
	 * @param parent: A Figure always has the square it is placed on as an Parameter, this one represents the initial Cell
	 * the figure is placed on in the GridPane
	 * {@link BackgroundCell}
	 * @param game: A figure always knows the game it is playing in (Access to Game has to be guaranteed when a figure is chosen with 
	 * a MouseClick)
	 */
	public CostumFigurePain(Piece piece) {
	this.piece = piece;
	this.type = piece.getDescription().getType();
	this.teamID = piece.getTeamId();
	this.setImage();
	this.active = false;
	showPieceInformationWhenHovering();
	this.setOnMouseClicked(new EventHandler<MouseEvent>() {
		public void handle(MouseEvent e) {
			if(active && ! attacable) {
				performSelectClick();
			}
			if(attacable) {
				performAttackClick();
			}
		}
	});
	}
	
	/**
	 * @author mkrakows
	 * creates a Shadow which can be used to highlight the currently selected Figure
	 * @param ImageView
	 */
	public void showTeamColor(String colorString) {
		Color col = Color.valueOf(colorString);
		borderGlow = new DropShadow();
        borderGlow.setColor(col);
        borderGlow.setOffsetX(0f);
        borderGlow.setOffsetY(0f);
        vw.setEffect(borderGlow);
	}
	public void showTeamColorWhenSelecting(ObjectProperty<Color> sceneColorProperty) {
		borderGlow = new DropShadow();
        borderGlow.colorProperty().bind(sceneColorProperty);
        borderGlow.setOffsetX(0f);
        borderGlow.setOffsetY(0f);
        vw.setEffect(borderGlow);
	}
	
	public void unbind() {
		if(borderGlow.colorProperty() != null) {
		borderGlow.colorProperty().unbind();
		}
		System.out.println("hallo ich bin aus team" + teamID);
	}
	
	
	public void performAttackClick() {
		int[] xk = { posX, posY };
		Game.makeMoveRequest(xk);
	}
	
	
	
	public void performSelectClick() {
	System.out.println("Hallo: " + posX + ", " + posY);
	showPieceInformationWhenClicked();
	Game.setCurrent(CostumFigurePain.this);
	Game.showPossibleMoves();
	parent.showSelected();
	}
	
	public void showPieceInformationWhenClicked() {
		PlayGameScreen.setIdLabelText("id: " + piece.getId());
		PlayGameScreen.setTypeLabelText("type: "+ piece.getDescription().getType());
		PlayGameScreen.setAttackPowLabelText("attack power: " + piece.getDescription().getAttackPower());
		PlayGameScreen.setCountLabelText("count: " + piece.getDescription().getCount());
		PlayGameScreen.setTeamLabelText("team: " + piece.getTeamId());
	}
	
	public void showPieceInformationWhenHovering() {
		String pieceInfos = "type: " + piece.getDescription().getType() + "\n" +
							"attack power: " +  piece.getDescription().getAttackPower() + "\n" +
							"count: " + piece.getDescription().getCount() + "\n" + 
							"pieceid: " + piece.getId();
			
		Tooltip tooltip = new Tooltip(pieceInfos);
		Duration delay = new Duration(1);
		tooltip.setShowDelay(delay);
		Duration displayTime = new Duration(10000);
		tooltip.setShowDuration(displayTime);
		tooltip.setFont(new Font(15));
		this.setPickOnBounds(true);
		Tooltip.install(this, tooltip);
	}
	
	public void setImage() {
		if(ImageLoader.getImageByName(type) != null) {
			this.bImage = ImageLoader.getImageByName(type);
		} else {
			this.bImage = ImageLoader.getDefaultImage();
		}
		this.vw = new ImageView(bImage);
		vw.fitWidthProperty().bind(this.widthProperty());
		vw.fitHeightProperty().bind(this.heightProperty());
		this.getChildren().add(vw);
	}
	

	public void setParente(BackgroundCellV2 parent) {
		this.parent = parent;
		this.posX = parent.x;
		this.posY = parent.y;
	}
	
	//Mehtode wird in der jetzigen implementierung nicht benötigt
	public void updatePos(int x1, int y1) {
		this.posX = x1;
		this.posY = y1;
		int[] pos = {x1,y1};
		piece.setPosition(pos);
	}
	
	public void setActive() {
		this.active = true;
	}
	
	public void setUnactive() {
		this.active = false;
	}
	
	public Piece getPiece() {
		return piece;
	}

	public String getTeamID() {
		return teamID;
	}
	
	public BackgroundCellV2 getParentCell() {
		return parent;
	}

	public void setParent(BackgroundCellV2 parent) {
		this.parent = parent;
	}
	
	public boolean isAttacable() {
		return attacable;
	}

	public void setAttacable() {
		this.attacable = true;
		parent.showattackCircle();
		
	}
	
	public void setUnattacble() {
		parent.deselect();
		this.attacable = false;
	}
}
	
