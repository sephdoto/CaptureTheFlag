package org.ctf.UI.customObjects;

import org.ctf.UI.Game;

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
	Game game;
	String name;
	Image bImage;
	BackgroundCell parent;
	int posX;
	int posY;
	
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
	public CostumFigurePain(Image bImage, String name, BackgroundCell parent, Game game) {
	this.game = game;
	this.posX = parent.x;
	this.posY = parent.y;
	this.parent = parent;
	this.bImage = bImage;
	this.name = name;
	ImageView vw = new ImageView(bImage);
	vw.fitWidthProperty().bind(this.widthProperty());
	vw.fitHeightProperty().bind(this.heightProperty());
	this.getChildren().add(vw);
	this.setOnMouseClicked(new EventHandler<MouseEvent>() {

		@Override
		public void handle(MouseEvent e) {
			System.out.println("Hallo: " + parent.getPosition()[0]+ ", " + parent.getPosition()[1]);
			showShadow(vw);
	        game.setCurrent(CostumFigurePain.this);
		}
	});
	}
	
	/**
	 * @author mkrakows
	 * creates a Shadow which can be used to highlight the currently selected Figure
	 * @param ImageView
	 */
	public void showShadow(ImageView vw) {
		DropShadow borderGlow = new DropShadow();
        borderGlow.setColor(Color.BLACK);
        borderGlow.setOffsetX(0f);
        borderGlow.setOffsetY(0f);
        vw.setEffect(borderGlow);
	}
	

	public void setParent(BackgroundCell parent) {
		this.parent = parent;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name;
	}
	
	
}
	
