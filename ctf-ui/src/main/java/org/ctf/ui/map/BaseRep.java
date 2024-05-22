package org.ctf.ui.map;


import org.ctf.shared.constants.Enums.SoundType;
import org.ctf.ui.controllers.SoundController;
import org.ctf.ui.customobjects.MyCustomColorPicker;
import org.ctf.ui.hostGame.CreateGameScreenV2;
import org.ctf.ui.hostGame.PlayGameScreenV2;
import org.ctf.ui.hostGame.WaitingScene;
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
import javafx.scene.layout.Border;
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
    BackgroundCellV2 parent;
    String teamColor;
    boolean showBasecolers;
    boolean isAttackable;
    PlayGameScreenV2 scene;
    private final EventHandler<MouseEvent> clickHandler;
    
    
    public BaseRep(int flags, String color, String teamID, BackgroundCellV2 parent) {
        this.parent = parent;
        showBasecolers = false;
        isAttackable = false;
        teamColor = color;
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
        label.setStyle("-fx-background-color: white ; -fx-border-color: black; -fx-font-weight: bold; -fx-shape: 'M  150 0 L75 200 L225 200  z'");
        this.getChildren().add(label);
         clickHandler = event -> {
                if(showBasecolers && !isAttackable) {
                 SoundController.playSound("default", SoundType.DESELECT);
                scene.showColorChooser(event.getSceneX(), event.getSceneY(),this);
                }
                if (isAttackable) {
                  SoundController.playSound(Game.getCurrent().getPiece().getDescription().getType(), SoundType.CAPTURE);
                    BaseRep.this.flags = BaseRep.this.flags -1;
                    label.setText(String.valueOf(flags));
                    int[] xy = {parent.getX(), parent.getY()};
                    Game.makeMoveRequest(xy);
                }
            };
         this.setOnMouseClicked(clickHandler);
            
    }
    
    public void showColor(ObjectProperty<Color> sceneColorProperty) {
        label.textFillProperty().bind(sceneColorProperty);
        
    }
    
    public void showDefaultTeamColor(String col) {
      label.setTextFill(Color.valueOf(col));
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
        showBasecolers = true;
        this.scene = scene;
    }

    public void setAttackable() {
        isAttackable = true;
        parent.showAttackable();
    }
    
    public void setUnattacble() {
        isAttackable = false;
        //parent.deselect();
    }

    

    
    
}
