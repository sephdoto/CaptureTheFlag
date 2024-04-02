package org.ctf.ui.customobjects;



import org.ctf.ui.Game;
import org.ctf.ui.PlayGameScreen;

import configs.ImageLoader;

import java.util.ArrayList;

import org.ctf.shared.ai.AI_Tools;
import org.ctf.shared.state.Piece;
import javafx.event.EventHandler;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import test.CreateTextGameStates;

/**
 * @mkrakows
 * This class contains a visual Representation of a Piece
 */
public class CostumFigurePain extends Pane {
	Game game;
	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	String teamID;
	Piece piece;
	String type;
	Image bImage;
	ImageView vw;
	int posX;	
	int posY;
	boolean active;
	public BackgroundCellV2 parent;
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
		possibleMoves = new ArrayList<int[]>();
		System.out.println("id " + piece.getId());
		System.out.println("team: " + piece.getTeamId());
		AI_Tools.getPossibleMoves(CreateTextGameStates.getTestState(), piece.getId(), possibleMoves);
		//System.out.println(possibleMoves.size());
	this.piece = piece;
	this.type = piece.getDescription().getType();

	this.teamID = piece.getTeamId();
	this.setImage();
	this.active = false;
	this.setOnMouseClicked(new EventHandler<MouseEvent>() {
		public void handle(MouseEvent e) {
			if(active) {
				performMouseClick();
			}
		}
	});
	}
	
	/**
	 * @author mkrakows
	 * creates a Shadow which can be used to highlight the currently selected Figure
	 * @param ImageView
	 */
	public void showShadow() {
		DropShadow borderGlow = new DropShadow();
        borderGlow.setColor(Color.RED);
        borderGlow.setOffsetX(0f);
        borderGlow.setOffsetY(0f);
        vw.setEffect(borderGlow);
	}
	
	
	public void performMouseClick() {
		if(game.currentPlayer != null) {
			game.currentPlayer.disableShadow();				}
	System.out.println("Hallo: " + posX + ", " + posY);
	showShadow();
	showPieceInformation();
	game.setCurrent(CostumFigurePain.this);
	game.showPossibleMoves();
	
	}
	
	public void showPieceInformation() {
		PlayGameScreen.setIdLabelText("id: " + piece.getId());
		PlayGameScreen.setTypeLabelText("type: "+ piece.getDescription().getType());
		PlayGameScreen.setAttackPowLabelText("attack power: " + piece.getDescription().getAttackPower());
		PlayGameScreen.setCountLabelText("count: " + piece.getDescription().getCount());
		PlayGameScreen.setTeamLabelText("team: " + piece.getTeamId());
		
	}
	
	public void disableShadow() {
		vw.setEffect(null);
	}
	
	
	public void setImage() {
		this.bImage = ImageLoader.getImageByName(type);
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
	
	//Mehtode sollte automatisch aufgerufen werden wenn Piece bewegt wird
	public void updatePos() {
		this.posX = piece.getPosition()[0];
		this.posY = piece.getPosition()[1];
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

	
	
	
}
	
