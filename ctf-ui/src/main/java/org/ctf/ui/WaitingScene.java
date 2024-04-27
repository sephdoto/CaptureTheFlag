package org.ctf.ui;

import java.util.Iterator;

import org.ctf.ui.customobjects.BaseRep;
import org.ctf.ui.customobjects.CostumFigurePain;

import de.unimannheim.swt.pse.ctf.game.state.GameState;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.PopupWindow;
import javafx.stage.Window;
import javafx.util.Duration;
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
		HBox main = new HBox();
		main.setAlignment(Pos.CENTER);
		main.setSpacing(main.heightProperty().doubleValue() * 0.09);
		main.heightProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.2;
			main.setSpacing(spacing);
		});
		main.prefWidthProperty().bind(this.widthProperty());
		main.getChildren().add(createLeft());
		VBox middleBox = new VBox();
		middleBox.getChildren().add(createHeader());
		middleBox.getChildren().add(createTopCenter());
		middleBox.heightProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.04;
			middleBox.setSpacing(spacing);
		});
		middleBox.getChildren().add(createShowMapPane("p2"));
		middleBox.setAlignment(Pos.TOP_CENTER);
		//middleBox.setStyle("-fx-background-color:red");
		
		middleBox.prefHeightProperty().bind(this.heightProperty());
		main.getChildren().add(middleBox);
		main.getChildren().add(createLeft());
		root.setStyle("-fx-background-color:black");
		root.getChildren().add(main);
		
		
		
	}
	private VBox createLeft() {
		VBox left = new VBox();
		left.heightProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.1;
			left.setPadding(new Insets(spacing, 0, 0, 0));
		});
		VBox labels = new VBox();
		labels.setSpacing(30);
		labels.getChildren().add(createInfoLabel("port" , "1234565656"));
		labels.getChildren().add(createInfoLabel("Server-ID" , "1234"));
		left.getChildren().add(labels);
//		Image mp = new Image(getClass().getResourceAsStream("ct2.png"));
//		ImageView mpv = new ImageView(mp);
//		left.getChildren().add(mpv);
		return left;
	}
	
	private VBox waitingBox() {
		 final Label    status   = new Label("Waiting for Players");
		 status.getStyleClass().add("des-label2");
		    final Timeline timeline = new Timeline(
		      new KeyFrame(Duration.ZERO, new EventHandler() {
		        @Override public void handle(Event event) {
		          String statusText = status.getText();
		          status.setText(
		            ("Waiting for Players . . .".equals(statusText))
		              ? "Waiting for Players ." 
		              : statusText + " ."
		          );
		        }
		      }),  
		      new KeyFrame(Duration.millis(1000))
		    );
		    timeline.setCycleCount(Timeline.INDEFINITE);
		    timeline.play();
		    VBox layout = new VBox();
		    layout.prefWidthProperty().bind(this.widthProperty().multiply(0.17));
		    //layout.setStyle("-fx-background-color: blue");
		    layout.getChildren().addAll(status);
		   
		    return layout;
	}
	
	private HBox createTopCenter() {
		HBox captureLoadingLabel = new HBox();
		captureLoadingLabel.setAlignment(Pos.CENTER);
		captureLoadingLabel.prefWidthProperty().bind(this.widthProperty().multiply(0.5));
		captureLoadingLabel.getChildren().add(waitingBox());
		return captureLoadingLabel;
		
	}
	
	
	
	private VBox createInfoLabel(String header, String content) {
		VBox labelBox = new VBox();
		labelBox.getStyleClass().add("info-vbox");
		Label headerLabel = new Label(header);
		headerLabel.getStyleClass().add("des-label");
		Label numberLabel = new Label(content);
		numberLabel.getStyleClass().add("number-label");
		labelBox.getChildren().addAll(headerLabel,numberLabel);
		return labelBox;
		
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
		showMapBox.prefHeightProperty().bind(this.heightProperty().multiply(0.65));
		showMapBox.getStyleClass().add("show-GamePane");
		org.ctf.shared.state.GameState state = StroeMaps.getMap(name);
		gm = new GamePane(state);
		gm.enableBaseColors(this);
		showMapBox.getChildren().add(gm);
		return showMapBox;
	}
	
	
	
	
	
	

	 
}
