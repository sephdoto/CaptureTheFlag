package org.ctf.ui;

import java.util.Iterator;

import org.ctf.ui.customobjects.BaseRep;
import org.ctf.ui.customobjects.CostumFigurePain;

import de.unimannheim.swt.pse.ctf.game.state.GameState;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.PopupWindow;
import javafx.stage.Window;
import javafx.scene.Node;


public class WaitingScene extends Scene {
	HomeSceneController hsc;
	StackPane root;
	StackPane left;
	StackPane right;
	Text text;
	VBox testBox;
	GamePane gm;
	private  ObjectProperty<Color> sceneColorProperty = 
		        new SimpleObjectProperty<>(Color.BLUE);

	public WaitingScene(HomeSceneController hsc, double width, double height) {
		super(new StackPane(), width, height);
		this.hsc = hsc;
		this.getStylesheets().add(getClass().getResource("MapEditor.css").toExternalForm());
		this.root = (StackPane) this.getRoot();
		createLayout();
		 this.getStylesheets().add(getClass().getResource("color.css").toExternalForm());
	        this.setOnMouseClicked(e->{
	            if(e.getButton().equals(MouseButton.SECONDARY)){
	                MyCustomColorPicker myCustomColorPicker = new MyCustomColorPicker();
	                myCustomColorPicker.setCurrentColor(sceneColorProperty.get());

	                CustomMenuItem itemColor = new CustomMenuItem(myCustomColorPicker);
	                itemColor.getStyleClass().add("custom-menu-item");
	                itemColor.setHideOnClick(false);
	                sceneColorProperty.bind(myCustomColorPicker.customColorProperty());
	                ContextMenu contextMenu = new ContextMenu(itemColor);
	                contextMenu.setHideOnEscape(true);
	                
	                contextMenu.setOnHiding(t->{sceneColorProperty.unbind();
	                
	               System.out.println("hihihi");
	                for(CostumFigurePain m : gm.getFigures().values() ) {
	                		m.unbind();
	                	}});
	                contextMenu.show(this.getWindow(),e.getScreenX(),e.getScreenY());
	            }
	        });
	}
	
	
	public void showColorChooser(double d, double e, BaseRep r) {
		  MyCustomColorPicker myCustomColorPicker = new MyCustomColorPicker();
          myCustomColorPicker.setCurrentColor(sceneColorProperty.get());

          CustomMenuItem itemColor = new CustomMenuItem(myCustomColorPicker);
          itemColor.getStyleClass().add("custom-menu-item");
          itemColor.setHideOnClick(false);
          sceneColorProperty.bind(myCustomColorPicker.customColorProperty());
          for(CostumFigurePain p : gm.getFigures().values()) {
        	  	if(p.getTeamID().equals(r.getTeamID())) {
        		  p.showTeamColorWhenSelecting(sceneColorProperty);
        	  	}
        		  
          }
          r.showColor(sceneColorProperty);
          ContextMenu contextMenu = new ContextMenu(itemColor);
          contextMenu.setOnHiding(t->{sceneColorProperty.unbind();
          
          System.out.println("hihihi");
           for(CostumFigurePain m : gm.getFigures().values() ) {
           		m.unbind();
           		sceneColorProperty = new SimpleObjectProperty<>(Color.BLUE);
           	}});
          contextMenu.show(this.getWindow(),d,e);
	}
	
	
	private void createLayout(){
		root.getChildren().add(createShowMapPane("p2"));
//		 Rectangle rect = new Rectangle(400,400);
//	        rect.fillProperty().bind(sceneColorProperty);
//	        root.getChildren().add(rect);
	}
	
	private ImageView createHeader() {
		Image mp = new Image(getClass().getResourceAsStream("multiplayerlogo.png"));
		ImageView mpv = new ImageView(mp);
		mpv.fitWidthProperty().bind(root.widthProperty().multiply(0.8));
		mpv.setPreserveRatio(true);
		root.widthProperty().addListener(e -> {
			if (root.getWidth() > 1000) {
				mpv.fitWidthProperty().unbind();
				mpv.setFitWidth(800);
			} else if (root.getWidth() <= 1000) {
				mpv.fitWidthProperty().unbind();
				mpv.fitWidthProperty().bind(root.widthProperty().multiply(0.8));
			}
		});
		return mpv;
	}
	
	private StackPane createShowMapPane(String name) {
		StackPane showMapBox = new StackPane();
		showMapBox.getStyleClass().add("option-pane");
		showMapBox.prefWidthProperty().bind(this.widthProperty().multiply(0.4));
		//showMapBox.setStyle("-fx-background-color: white");
		showMapBox.prefHeightProperty().bind(showMapBox.widthProperty());
		showMapBox.getStyleClass().add("show-GamePane");
		org.ctf.shared.state.GameState state = StroeMaps.getMap(name);
		gm = new GamePane(state);
		gm.enableBaseColors(this);
		showMapBox.getChildren().add(gm);
		return showMapBox;
	}
	
	
	
	
	
	

	 
}
