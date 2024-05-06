package org.ctf.ui;

import org.ctf.shared.state.GameState;
import org.ctf.ui.customobjects.BaseRep;
import org.ctf.ui.customobjects.CostumFigurePain;
import org.ctf.ui.customobjects.Timer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class PlayGameScreenV2 extends Scene {
	HomeSceneController hsc;
	StackPane root;
	StackPane left;
	Text text;
	VBox testBox;
	Label howManyTeams;
	GamePane gm;
	GameState state;
	VBox right;
	private static Circle c;
	private static Label idLabel;
	private static Label typeLabel;
	private static Label attackPowLabel;
	private static Label teamLabel;
	private static Label countLabel;
	StackPane showMapBox;
	private  ObjectProperty<Color> sceneColorProperty = 
	        new SimpleObjectProperty<>(Color.BLUE);
	private ObjectProperty<Font> timerLabel = new SimpleObjectProperty<Font>(Font.getDefault());
	private ObjectProperty<Font> timerDescription = new SimpleObjectProperty<Font>(Font.getDefault());
	private static  ObjectProperty<Font> pictureMainDiscription = new SimpleObjectProperty<Font>(Font.getDefault());
	private ObjectProperty<Font> figureDiscription = new SimpleObjectProperty<Font>(Font.getDefault());
	SimpleObjectProperty<Insets> padding = new SimpleObjectProperty<>(new Insets(10));
	
	
	public PlayGameScreenV2(HomeSceneController hsc, double width, double height) {
		super(new StackPane(), width, height);
		this.hsc = hsc;
		

		manageFontSizes();
		this.getStylesheets().add(getClass().getResource("MapEditor.css").toExternalForm());
		this.root = (StackPane) this.getRoot();
		createLayout();
		 this.getStylesheets().add(getClass().getResource("color.css").toExternalForm());
	}
	
	
	public void createLayout() {
		root.setStyle("-fx-background-color: black");
		root.paddingProperty().bind(padding);
		root.prefHeightProperty().bind(this.heightProperty());
		root.prefWidthProperty().bind(this.widthProperty());
		HBox top = new HBox();
		top.setAlignment(Pos.CENTER);
		VBox left = new VBox();
		right = new VBox();
		right.setAlignment(Pos.BOTTOM_CENTER);
		left.setAlignment(Pos.CENTER);
		top.prefHeightProperty().bind(this.heightProperty());
		left.prefHeightProperty().bind(this.heightProperty());
		left.prefWidthProperty().bind(this.widthProperty().multiply(0.7));
		left.getChildren().add(createShowMapPane("p2"));
		top.getChildren().add(left);
		right.getChildren().add(createTopCenter());
		right.getChildren().add(imageTest());
		right.getChildren().add(createClockBox());
		right.setStyle("-fx-background-color: black");
		right.prefWidthProperty().bind(this.widthProperty().multiply(0.3));
		top.getChildren().add(right);
		root.getChildren().add(top);
		//PullGameStateThreads p = new PullGameStateThreads();
	}
	
	public void redrawGrid(GameState state) {
		showMapBox.getChildren().clear();
		gm = new GamePane(state);
		gm.enableBaseColors(this);
		showMapBox.getChildren().add(gm);
		
	}
	
	private VBox createShowMapPane(String name) {
		VBox outerbox = new VBox();
		VBox.setVgrow(outerbox, Priority.ALWAYS);
		outerbox.prefHeightProperty().bind(root.heightProperty());
		//outerbox.setFillWidth(true);
		showMapBox = new StackPane();
		showMapBox.getStyleClass().add("play-pane");
		//showMapBox.prefWidthProperty().bind(this.widthProperty().multiply(0.7));
		showMapBox.paddingProperty().bind(padding);
		showMapBox.prefHeightProperty().bind(outerbox.heightProperty());
		showMapBox.getStyleClass().add("show-GamePane");
		state = StroeMaps.getMap(name);
		gm = new GamePane(state);
//		gm.prefHeightProperty().bind(this.heightProperty().multiply(0.9));
//		gm.prefWidthProperty().bind(this.widthProperty().multiply(0.9));
		gm.enableBaseColors(this);
		showMapBox.getChildren().add(gm);
	
		outerbox.getChildren().add(showMapBox);
		return outerbox;
	}
	
	
	private VBox waitingBox(String playerName) {
		String showString = "team " +  playerName + "s turn";
		 final Label    status   = new Label(showString);
		 status.getStyleClass().add("des-label2");
		    final Timeline timeline = new Timeline(
		      new KeyFrame(Duration.ZERO, new EventHandler() {
		        @Override public void handle(Event event) {
		          String statusText = status.getText();
		          String s = showString + " . . .";
		          String s2 = showString + " .";
		          status.setText(
		            (s.equals(statusText))
		              ? s2
		              : statusText + " ."
		          );
		        }
		      }),  
		      new KeyFrame(Duration.millis(1000))
		    );
		    timeline.setCycleCount(Timeline.INDEFINITE);
		    timeline.play();
		    VBox layout = new VBox();
		    layout.prefWidthProperty().bind(right.widthProperty().multiply(0.55));
		    status.fontProperty().bind(figureDiscription);
		    //layout.setStyle("-fx-background-color: blue");
		    layout.getChildren().addAll(status);
		    return layout;
	}
	
	private HBox createTopCenter() {
		HBox captureLoadingLabel = new HBox();
		captureLoadingLabel.setAlignment(Pos.CENTER);
		//captureLoadingLabel.setStyle("-fx-background-color: yellow");
		captureLoadingLabel.prefWidthProperty().bind(right.widthProperty().multiply(0.8));
		captureLoadingLabel.getChildren().add(waitingBox("3"));
		return captureLoadingLabel;
		
	}
	
	private void manageFontSizes() {
		 widthProperty().addListener(new ChangeListener<Number>()
		    {
		        public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth, Number newWidth)
		        {
		        	timerLabel.set(Font.font(newWidth.doubleValue() / 40));
		        	timerDescription.set(Font.font(newWidth.doubleValue() / 60));
		        	pictureMainDiscription.set(Font.font(newWidth.doubleValue() / 40));
		        	figureDiscription.set(Font.font(newWidth.doubleValue() / 45));
		        	padding.set(new Insets(newWidth.doubleValue()*0.01));
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
         	}});
        contextMenu.show(this.getWindow(),d,e);
	}
	
	private HBox createClockBox() {
		
		HBox timerBox = new HBox();
		timerBox.setAlignment(Pos.CENTER);
		timerBox.getStyleClass().add("timer-box");
		timerBox.prefWidthProperty().bind(right.widthProperty());
		timerBox.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.09;
			double padding = newValue.doubleValue() * 0.02;
			timerBox.setSpacing(newSpacing);
			timerBox.setPadding(new Insets(0, padding, 0, padding));
		});
		VBox timer1 =  createTimer(timerBox, "Game Time");
		VBox timer2 =  createTimer(timerBox, "Move Time");
		timerBox.getChildren().addAll(timer1,timer2);
		return timerBox;
	}
	
	private VBox createTimer(HBox timerBox, String text) {
		VBox timerwithDescrip = new VBox();
		timerwithDescrip.setAlignment(Pos.CENTER);
		timerwithDescrip.prefWidthProperty().bind(timerBox.widthProperty().multiply(0.35));
		timerwithDescrip.prefHeightProperty().bind(timerBox.widthProperty().multiply(0.35));
		Label desLabel = new Label(text);
		desLabel.setAlignment(Pos.CENTER);
		desLabel.fontProperty().bind(timerDescription);
		desLabel.getStyleClass().add("des-timer");
		timerwithDescrip.getChildren().add(desLabel);
		Timer t = new Timer(0,0,0);
		t.prefWidthProperty().bind(timerBox.widthProperty().multiply(0.35));
		t.prefHeightProperty().bind(t.widthProperty().multiply(0.35));
		t.getStyleClass().add("timer-label");
		t.fontProperty().bind(timerLabel);
		timerwithDescrip.getChildren().add(t);
		return timerwithDescrip;
	}
	
	private HBox imageTest() {
		HBox h1 = new HBox();
		h1.prefHeightProperty().bind(this.heightProperty().multiply(0.7));
		h1.prefWidthProperty().bind(h1.heightProperty().multiply(0.3));
		h1.widthProperty().addListener((observable, oldValue, newValue) -> {
			double padding = newValue.doubleValue() * 0.08;
			h1.setPadding(new Insets(padding, padding, padding, padding));
		});
		//h1.setStyle("-fx-background-color: red");
		h1.setAlignment(Pos.CENTER);
		VBox x = new VBox();
		
		x.widthProperty().addListener((observable, oldValue, newValue) -> {
			double padding = newValue.doubleValue() * 0.05;
			x.setPadding(new Insets(padding, padding, padding, padding));
		});
		x.getStyleClass().add("option-pane");
		HBox pict = new HBox();
		//pict.setStyle("-fx-background-color: green");
		pict.prefHeightProperty().bind(x.heightProperty().multiply(0.1));
		typeLabel = new Label("Yoda");
		typeLabel.fontProperty().bind(pictureMainDiscription);
		typeLabel.setAlignment(Pos.CENTER_LEFT);
		typeLabel.prefHeightProperty().bind(pict.heightProperty());
		typeLabel.prefWidthProperty().bind(pict.widthProperty().multiply(0.7));
		typeLabel.getStyleClass().add("figure-label");
		StackPane p = new StackPane();
		p.prefWidthProperty().bind(pict.widthProperty().multiply(0.3));
		//p.setStyle("-fx-background-color: yellow");
		Image mp = new Image(getClass().getResourceAsStream("Yoda.png"));
		c = new Circle();
		c.radiusProperty().bind(Bindings.divide(widthProperty(), 23));
		c.setFill(new ImagePattern(mp));
		Circle c2 = new Circle();
		c2.setFill(Color.WHITE);
		 c2.setStroke(Color.BLACK);
	      c2.setStrokeWidth(2);
		c2.radiusProperty().bind(Bindings.divide(widthProperty(), 21));
		pict.getChildren().addAll(typeLabel,p);
		p.getChildren().addAll(c2,c);
		x.getChildren().add(pict);
		x.getChildren().add(createDeslabelBox());
		h1.getChildren().add(x);
		return h1;
	}
	private void fitText( Label lbl, double max) {
		double defaultFontSize = 32;
		Font defaultFont = Font.font(defaultFontSize);
		lbl.setFont(defaultFont);
		lbl.textProperty().addListener((observable, oldValue, newValue) -> {

			Text tmpText = new Text(newValue);
			tmpText.setFont(defaultFont);

			double textWidth = tmpText.getLayoutBounds().getWidth();
			if (textWidth <= max) {
				lbl.setFont(defaultFont);
			} else {

				double newFontSize = defaultFontSize * max / textWidth;
				lbl.setFont(Font.font(defaultFont.getFamily(), newFontSize));
			}

		});
	}
	
	
	private VBox createDeslabelBox() {
		VBox deBox = new VBox(10);
		deBox.heightProperty().addListener((observable, oldValue, newValue) -> {
			double spacing = newValue.doubleValue() * 0.08;
			deBox.setSpacing(spacing);
		});
		deBox.setAlignment(Pos.BASELINE_LEFT);
		idLabel = new Label("id: -");
		handleLabel(idLabel, deBox);
		teamLabel = new Label("team: -");
		handleLabel(teamLabel, deBox);
		attackPowLabel = new Label("attackpower: -");
		handleLabel(attackPowLabel, deBox);
		countLabel = new Label("count: - ");
		handleLabel(countLabel, deBox);
		deBox.getChildren().addAll(idLabel, teamLabel, attackPowLabel, countLabel);
		return deBox;
	}
	
	private void handleLabel(Label l, VBox parent) {
		l.fontProperty().bind(figureDiscription);
		l.prefWidthProperty().bind(parent.widthProperty());
		l.getStyleClass().add("figure-label");
	}
		
	
	
	public static void setFigureImage(Image img) {
		c.setFill(new ImagePattern(img));
	}
	
	
	public static void setIdLabelText(String text) {
		idLabel.setText(text);
	}

	public static void setTypeLabelText(String text) {
		typeLabel.setText(text);
	}

	public static void setAttackPowLabelText(String text) {
		attackPowLabel.setText(text);
	}

	public static void setCountLabelText(String text) {
		countLabel.setText(text);
	}

	public static void setTeamLabelText(String text) {
		teamLabel.setText(text);
	}

}
