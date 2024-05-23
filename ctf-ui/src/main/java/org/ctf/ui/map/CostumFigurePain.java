package org.ctf.ui.map;

import org.ctf.ui.controllers.ImageController;
import org.ctf.ui.controllers.SoundController;
import org.ctf.ui.hostGame.PlayGameScreenV2;
import org.ctf.shared.constants.Enums.ImageType;
import org.ctf.shared.constants.Enums.SoundType;
import org.ctf.shared.state.Piece;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * 
 * visual Representation of a Piece
 * @author Manuel Krakowski
 */
public class CostumFigurePain extends Pane {
  //Data about the corresponding piece
	private String teamID;
	private Piece piece;
	private String type;
	
	//Data about the position
	private int posX;	
	private int posY;
	private BackgroundCellV2 parent;
	
	//Data about the condition
	private boolean active;
	private boolean attacable;
	
	//Data about the image belonging to the piece
	private Image bImage;
    private ImageView vw;
    private DropShadow borderGlow;
	

    /**
     * Initialazes the CostumfigurePain with the corresponding piece, sets its image and sets a 
     * Mouse-Listener to it
     * @author Manuel Krakowski
     * @param piece corresponding piece
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
	 * Creates a glow based on a color which is selected by the server representing the team-color
	 * @author Manuel Krakowski
	 * @param colorString team-color given by the server
	 */
	public void showTeamColor(String colorString) {
		Color col = Color.valueOf(colorString);
		borderGlow = new DropShadow();
        borderGlow.setColor(col);
        borderGlow.setOffsetX(0f);
        borderGlow.setOffsetY(0f);
        vw.setEffect(borderGlow);
	}
	
	/**
     * Creates a glow based on a color-property which can be selected by the user
     * @author Manuel Krakowski
     * @param colorString team-color given by the server
     */
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
	}
	
	
	public void performAttackClick() {
	   SoundController.playSound(piece.getDescription().getType(), SoundType.KILL);
		int[] xk = { posX, posY };
		Game.makeMoveRequest(xk);
	}
	
	
	
	public void performSelectClick() {
	SoundController.playSound(piece.getDescription().getType(), SoundType.SELECT);
	showPieceInformationWhenClicked();
	Game.setCurrent(CostumFigurePain.this);
	Game.showPossibleMoves();
	parent.showSelected();
	}
	
	public void showPieceInformationWhenClicked() {
		PlayGameScreenV2.setIdLabelText("id: " + piece.getId());
		PlayGameScreenV2.setTypeLabelText( piece.getDescription().getType());
		PlayGameScreenV2.setAttackPowLabelText("attack power: " + piece.getDescription().getAttackPower());
		PlayGameScreenV2.setCountLabelText("count: " + piece.getDescription().getCount());
		PlayGameScreenV2.setTeamLabelText("team: " + piece.getTeamId());
		PlayGameScreenV2.setFigureImage(bImage);
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
//		if(ImageLoader.getImageByName(type) != null) {
//			//this.bImage = ImageLoader.getImageByName(type);
//			this.bImage = ImageController.loadThemedImage(ImageType.PIECE, type);
//		} else {
//			this.bImage = ImageLoader.getDefaultImage();
//		}
		this.bImage = ImageController.loadThemedImage(ImageType.PIECE, type);
		this.vw = new ImageView(bImage);
		vw.fitWidthProperty().bind(this.widthProperty());
		vw.fitHeightProperty().bind(this.heightProperty());
		this.getChildren().add(vw);
	}
	
	public void showAnimation() {
		Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    DropShadow dropShadow = new DropShadow();
                    dropShadow.setColor(Color.YELLOW);
                    vw.setEffect(dropShadow);
                }),
                new KeyFrame(Duration.seconds(0.5)),
                new KeyFrame(Duration.seconds(1), e -> {
                    vw.setEffect(null);
                })
        );
        timeline.setCycleCount(Animation.INDEFINITE); 
        timeline.play();
	}
	

	public void setParente(BackgroundCellV2 parent) {
		this.parent = parent;
		this.posX = parent.getX();
		this.posY = parent.getY();
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
		parent.showAttackable();
	}
	public int getPosX() {
      return posX;
  }

  public void setPosX(int posX) {
      this.posX = posX;
  }

  public int getPosY() {
      return posY;
  }

  public void setPosY(int posY) {
      this.posY = posY;
  }
	
	public void setUnattacble() {
		parent.deselect();
		this.attacable = false;
	}

	public void setAlignment(Pos center) {
		this.setAlignment(center);
		
	}
}
	
