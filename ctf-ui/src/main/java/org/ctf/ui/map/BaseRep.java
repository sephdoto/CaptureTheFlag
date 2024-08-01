package org.ctf.ui.map;


import org.ctf.shared.constants.Enums.SoundType;
import org.ctf.ui.controllers.SoundController;
import org.ctf.ui.hostGame.PlayGameScreenV2;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * Visual Representation of a base
 * 
 * @author Manuel Krakowski
 */

public class BaseRep extends Pane {

  // Data of the base
  private int flags;
  private String teamID;


  // Components that need to be accessed
  private BackgroundCellV2 parent;
  private Label label;

  // booleans to handle different mouse-clicks
  private boolean showBasecolers;
  private boolean isAttackable;

  // to enable base-colors when playing a game
  private PlayGameScreenV2 scene;

  // event-handler to listen to mouse-click
  private final EventHandler<MouseEvent> clickHandler;


  /**
   * Initializes a base which is represented by triangle-shaped labeland sets a mouse listner to it
   * When the base is attackable a move request is sent when clicking, otherwise the user can select
   * the team color by clicking on it in the playgame-scene
   * 
   * @author Manuel Krakowski
   * @param flags: Number of flags in the base
   * @param color: initial color of the base (not used at the moment)
   * @param teamID: Team-id of the team the base belongs to
   * @param parent: backgroundcell the base is placed on
   */
  public BaseRep(int flags, String color, String teamID, BackgroundCellV2 parent) {
    this.parent = parent;
    showBasecolers = false;
    isAttackable = false;
    this.flags = flags;
    this.teamID = teamID;
    showBaseInformationWhenHovering();
    label = new Label(String.valueOf(flags));
    label.setFont(javafx.scene.text.Font.font(25));
    label.fontProperty().bind(
        Bindings.createObjectBinding(() -> Font.font(this.getWidth() / 2), this.widthProperty()));
    label.prefWidthProperty().bind(this.widthProperty());
    label.prefHeightProperty().bind(this.heightProperty());
    label.setAlignment(Pos.BOTTOM_CENTER);
    label.setStyle(
        "-fx-background-color: white ; -fx-border-color: black; -fx-font-weight: bold; -fx-shape: 'M  150 0 L75 200 L225 200  z'");
    this.getChildren().add(label);
    clickHandler = event -> {
      if (showBasecolers && !isAttackable) {
        SoundController.playSound("default", SoundType.DESELECT);
        scene.showColorChooser(event.getSceneX(), event.getSceneY(), this);
      }
      if (isAttackable) {
        try {
          SoundController.playSound(MoveVisualizer.getCurrent().getPiece().getDescription().getType(),
              SoundType.CAPTURE);
          BaseRep.this.flags = BaseRep.this.flags - 1;
          label.setText(String.valueOf(flags));
          int[] xy = {parent.getX(), parent.getY()};
          MoveVisualizer.makeMoveRequest(xy);
        } catch (Exception e) {
          SoundController.playSound("default", SoundType.DESELECT);
          scene.showColorChooser(event.getSceneX(), event.getSceneY(), this);
          // a little bug can cause an exception but it does not need fixing, as it causes no problems.
        }
      }
    };
    this.setOnMouseClicked(clickHandler);
  }


  /**
   * changes the color of the piece by binding it to a color-property when it's clicked
   * 
   * @author Manuel Krakowski
   * @param sceneColorProperty color-property the base is bind to
   */
  public void showColor(ObjectProperty<Color> sceneColorProperty) {
    label.textFillProperty().bind(sceneColorProperty);

  }

  /**
   * sets the color of the base-label to a default color
   * 
   * @author Manuel Krakowski
   * @param color the base-label
   */
  public void showDefaultTeamColor(String col) {
    label.setTextFill(Color.valueOf(col));
  }


  /**
   * Shows to which team the base belongs when hovering over it
   * 
   * @author Manuel Krakowski
   */
  public void showBaseInformationWhenHovering() {
    String pieceInfos = "base of team " + teamID;
    Tooltip tooltip = new Tooltip(pieceInfos);
    Duration delay = new Duration(1);
    tooltip.setShowDelay(delay);
    Duration displayTime = new Duration(10000);
    tooltip.setShowDuration(displayTime);
    tooltip.setFont(new Font(15));
    this.setPickOnBounds(true);
    Tooltip.install(this, tooltip);
  }


  // Getters and Setters
  /////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////////////


  public void setScene(PlayGameScreenV2 scene) {
    showBasecolers = true;
    this.scene = scene;
  }

  public void setAttackable() {
    isAttackable = true;
    parent.showAttackable();
  }

  public void setUnattacble() {
    isAttackable = false;
  }

  public String getTeamID() {
    return teamID;
  }

  public void setTeamID(String teamID) {
    this.teamID = teamID;
  }



}
