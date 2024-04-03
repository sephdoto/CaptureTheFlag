package org.ctf.ui;


import static org.mockito.ArgumentMatchers.isNull;

import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import org.ctf.shared.state.GameState;

import configs.Dialogs;
import configs.ImageLoader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import test.CreateTextGameStates;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.cell.ChoiceBoxListCell;
import javafx.scene.layout.BorderPane;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * @author mkrakows
 * This Class contains a scene in that a user can choose a map with a choiceBox and display it on the left side
 */
public class CreateGameScreen  {
	static VBox v;
	static HBox center;
	static GamePane gm;
	static Stage s;
	static String selected;
	static Label mapName;
	static VBox rightVBox;
	static String randomMapName;
	static GameState state;

	public static void initCreateGameScreen(Stage stage) {
		ImageLoader.loadImages();
		s = stage;
		StroeMaps.initDefaultMaps();
		v = new VBox();
		HBox headerBox = createHeaderBox();
		center = new HBox();
		center.setStyle("-fx-background-color:white");
		VBox right = createRightSide();
		randomMapName = StroeMaps.getRandomMapName();
		gm = createLeftSidPane(randomMapName);
		
		center.prefWidthProperty().bind(stage.widthProperty().multiply(0.7));
		center.getChildren().addAll(gm, right);
		v.getChildren().add(headerBox);
		v.getChildren().add(center);
		Scene scene = new Scene(v, 1000, 500);
		stage.setMinHeight(500);
		stage.setMinWidth(900);
		stage.setScene(scene);
		stage.setFullScreen(true);
		stage.show();
	}
	

	private static VBox createRightSide() {
		
		VBox right = new VBox(s.heightProperty().divide(5).doubleValue());
		right.setStyle("-fx-background-color: lightblue");
		right.setAlignment(Pos.TOP_CENTER);
		right.setPadding(new Insets(20));
		right.prefWidthProperty().bind(s.widthProperty().multiply(0.3));
		VBox topV = new VBox(20);
		topV.setAlignment(Pos.TOP_CENTER);
		//topV.setPadding(new Insets(20));
		Label ls = new Label("Choose Map");
		styleDiscriptionLabel(ls, right);
		ChoiceBox<String> chooseMapBox = createChoiceBox(right);
		performChoiceBoxAction(chooseMapBox, ls);
		VBox createGameButtonBox = createCreateGameButtons(right);
		topV.getChildren().addAll(ls,chooseMapBox);
		right.getChildren().addAll(topV,createGameButtonBox);
		rightVBox = right;
		return right;
	}
	
	private static VBox createCreateGameButtons(VBox parent) {
		VBox createGameButtonBox = new VBox(20);
		createGameButtonBox.setStyle("-fx-border-color: black; -fx-border-width: 2px;" + "-fx-background-color: white;"
				+ "-fx-background-radius: 20px; -fx-border-radius: 20px;" + "-fx-alignment: top-center;");
		Button startButton = new Button("Start Game");
		VBox.setMargin(startButton, new Insets(70,0,0,0));
		createGameButtonBox.setPadding(new Insets(20));
		startButton.prefWidthProperty().bind(parent.widthProperty().multiply(0.7));
		startButton.prefHeightProperty().bind(parent.heightProperty().multiply(0.1));
		Dialogs.applyButtonStyle(startButton);
		startButton.setOnAction(event ->{
			PlayGameScreen.initPlayGameScreen(s, state);
		});
		Button backButton = new Button("back");
		Dialogs.applyButtonStyle(backButton);
		VBox.setMargin(backButton, new Insets(10,0,0,0));
		backButton.prefWidthProperty().bind(parent.widthProperty().multiply(0.5));
		backButton.prefHeightProperty().bind(parent.heightProperty().multiply(0.05));
		backButton.setOnAction(e -> {
			goBackToHomeScreen();
		});
		createGameButtonBox.getChildren().addAll(startButton,backButton);
		return createGameButtonBox;
	}
	
	private static void goBackToHomeScreen() {
		Scene scene = App.getScene();
	    s.setScene(scene);
	}
	private static ChoiceBox<String> createChoiceBox(VBox parent) {
		ChoiceBox<String> c = new ChoiceBox<String>();
		c.setStyle("-fx-background-color: white; ");
		c.getItems().addAll(StroeMaps.getValues());
		c.setValue(randomMapName);//wichtig
		c.prefWidthProperty().bind(parent.widthProperty().multiply(0.7));
		c.prefHeightProperty().bind(parent.heightProperty().multiply(0.06));
		return c;
	}
	
	private static void styleDiscriptionLabel(Label ls, VBox parent) {
		ls.setFont(Font.font(30));
		ls.setWrapText(true);
		ls.setAlignment(Pos.CENTER);
		ls.prefHeightProperty().bind(parent.heightProperty().multiply(0.06));
		ls.prefWidthProperty().bind(parent.widthProperty().multiply(0.7));
	}
	
	private static HBox createHeaderBox() {
		HBox headerBox = new HBox(30);
		headerBox.setPadding(new Insets(0, 0, 0, 20));
		headerBox.setStyle("-fx-background-color: violet");
		mapName = new Label("Your Map:");
		mapName.setFont(Font.font(30));		
		headerBox.getChildren().addAll(mapName);
		return headerBox;
	}
	
	private static GamePane createLeftSidPane(String name) {
		state = StroeMaps.getMap(name);
		gm = new GamePane(state);
		gm.prefWidthProperty().bind(s.widthProperty().multiply(0.7));
		gm.prefHeightProperty().bind(s.heightProperty());
		return gm;
	}
	
	private static void performChoiceBoxAction(ChoiceBox<String> cb, Label l2) {
		cb.setOnAction(event -> {
			selected = cb.getValue();
			mapName.setText(selected);
			center.getChildren().clear();
			GamePane p = createLeftSidPane(selected);
			//l2.setText(selected);
			center.getChildren().addAll(p,rightVBox);
		});
	}

	
	
	
}
