package org.ctf.ui;

import org.ctf.shared.state.GameState;
import org.ctf.ui.customobjects.Timer;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

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
	private ObjectProperty<Font> timerLabel = new SimpleObjectProperty<Font>(Font.getDefault());
	private ObjectProperty<Font> timerDescription = new SimpleObjectProperty<Font>(Font.getDefault());
	
	
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
		HBox top = new HBox();
		top.setAlignment(Pos.CENTER);
		VBox left = new VBox();
		right = new VBox();
		right.setAlignment(Pos.CENTER);
		left.setAlignment(Pos.CENTER);
		top.prefHeightProperty().bind(this.heightProperty());
		left.prefHeightProperty().bind(this.heightProperty());
		left.prefWidthProperty().bind(this.widthProperty().multiply(0.7));
		left.getChildren().add(createShowMapPane("p1"));
		top.getChildren().add(left);
		right.getChildren().add(createClockBox());
		right.setStyle("-fx-background-color: black");
		right.prefWidthProperty().bind(this.widthProperty().multiply(0.3));
		top.getChildren().add(right);
		root.getChildren().add(top);
	}
	
	private void manageFontSizes() {
		 widthProperty().addListener(new ChangeListener<Number>()
		    {
		        public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth, Number newWidth)
		        {
		        	timerLabel.set(Font.font(newWidth.doubleValue() / 40));
		        	timerDescription.set(Font.font(newWidth.doubleValue() / 60));
		        }
		    });
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
		
	private StackPane createShowMapPane(String name) {
		StackPane showMapBox = new StackPane();
		showMapBox.getStyleClass().add("play-pane");
		showMapBox.prefWidthProperty().bind(this.widthProperty().multiply(0.7));
		
		showMapBox.prefHeightProperty().bind(this.heightProperty());
		showMapBox.getStyleClass().add("show-GamePane");
		state = StroeMaps.getMap(name);
		GamePane gm = new GamePane(state);
		showMapBox.getChildren().add(gm);
		return showMapBox;
	}
}
