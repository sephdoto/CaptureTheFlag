package org.ctf.ui.customobjects;



import org.ctf.ui.Game;

import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * @mkrakows
 * This class represents a general representation of a game figure that is represented by an image
 */
public class CostumFigurePain extends Pane {
	public Game game;
	public String name; //id
	Image bImage;
	ImageView vw;
	BackgroundCell parent;
	public int posX;
	public int posY;
	boolean active;
	
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
	public CostumFigurePain( String name) {
	this.name = name;
	this.active = false;
	this.setOnMouseClicked(new EventHandler<MouseEvent>() {
		
		@Override
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
        borderGlow.setColor(Color.BLACK);
        borderGlow.setOffsetX(0f);
        borderGlow.setOffsetY(0f);
        vw.setEffect(borderGlow);
	}
	
	public void performMouseClick() {
		if(game.currentPlayer != null) {
			game.currentPlayer.disableShadow();				}
	System.out.println("Hallo: " + posX + ", " + posY);
	showShadow();
	game.setCurrent(CostumFigurePain.this);
	game.showPossibleMoves();
	}
	
	public void disableShadow() {
		vw.setEffect(null);
	}
	
	
	public void setImage() {
		this.vw = new ImageView(bImage);
		vw.fitWidthProperty().bind(this.widthProperty());
		vw.fitHeightProperty().bind(this.heightProperty());
		this.getChildren().add(vw);
	}
	

	public void setParent(BackgroundCell parent) {
		this.parent = parent;
		this.posX = parent.x;
		this.posY = parent.y;
	}
	
	public void setActive() {
		this.active = true;
	}
	
	public void setUnactive() {
		this.active = false;
	}
	
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name;
	}
	
	
}
	
