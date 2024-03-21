package org.ctf.ui;


import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.cell.ChoiceBoxListCell;
import javafx.scene.layout.BorderPane;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

/**
 * @author mkrakows
 * This Class contains a scene in that a user can choose a map with a choiceBox and display it on the left side
 */
public class CreateGameScreen  {
	static VBox v;
	static HBox center;
	static GamePane gm;
	static Stage s;
	static StoreMapArrays maps = new StoreMapArrays();
	static String[][] exm = { { "b", "p:1_2", "b" }, { "b", "p:2_3", "b" },{ "b", "", "" } };
	static String[][] exm2 = { { "b", "p:1_2", "b","" }, { "b", "p:2_3", "b" ,""},{ "b", "", "","" } };
	static String[][] exm3 = {
			  {"p:1_1", "p:1_2", "p:1_3", "p:1_4", "p:1_5", "p:1_6", "p:1_7", "p:1_8"},
			  {"", "", "", "", "", "", "", ""},
			  {"", "", "", "", "", "", "", ""},
			  {"", "", "", "b", "", "", "", ""},
			  {"", "", "b", "", "", "", "", ""},
			  {"", "", "", "", "b", "", "", ""},
			  {"", "", "", "", "", "", "", ""},
			  {"p:2_3", "p:2_3", "p:2_3", "p:2_3", "p:2_3", "p:2_3", "p:2_3", "p:2_3"}
			};
	  
	static String selected;
	static Label mapName;
	static VBox rightVBox;

	public static void initCreateGameScreen(Stage stage) {
		s = stage;
		maps.putMap("exmaple", exm);
		maps.putMap("example2", exm2);
		maps.putMap("exm3", exm3);
		v = new VBox();
		HBox headerBox = createHeaderBox();
		center = new HBox();
		center.setStyle("-fx-background-color:white");
		VBox right = createRightSide();
		gm = createInitLeftSidPane(exm);
		center.prefWidthProperty().bind(stage.widthProperty().multiply(0.7));
		center.getChildren().addAll(gm, right);
		v.getChildren().add(headerBox);
		v.getChildren().add(center);
		Scene scene = new Scene(v, 1000, 500);
		stage.setMinHeight(500);
		stage.setMinWidth(900);
		stage.setScene(scene);
		stage.show();
	}

	private static VBox createRightSide() {
		VBox right = new VBox(20);
		right.setStyle("-fx-background-color: lightblue");
		right.setAlignment(Pos.TOP_CENTER);
		right.setPadding(new Insets(20));
		right.prefWidthProperty().bind(s.widthProperty().multiply(0.3));
		//VBox topRight = new VBox(20);
		Label ls = new Label("Choose Map");
		ls.setFont(Font.font(30));
		ls.setWrapText(true);
		ls.setAlignment(Pos.CENTER);
		ls.prefHeightProperty().bind(right.heightProperty().multiply(0.06));
		ls.prefWidthProperty().bind(right.widthProperty().multiply(0.7));
		ChoiceBox<String> c = new ChoiceBox<String>();
		c.setStyle("-fx-background-color: white; ");
		c.getItems().addAll(maps.getValues());
		performChoiceBoxAction(c, ls);
		c.prefWidthProperty().bind(right.widthProperty().multiply(0.7));
		c.prefHeightProperty().bind(right.heightProperty().multiply(0.06));
		Button h = new Button("Start Game");
		VBox.setMargin(h, new Insets(90,0,0,0));
		h.prefWidthProperty().bind(right.widthProperty().multiply(0.7));
		h.prefHeightProperty().bind(right.heightProperty().multiply(0.1));
		h.setOnAction(event ->{
			PlayGameScreen.initPlayGameScreen(s, gm);
		});
		
		Button backButton = new Button("back");
		VBox.setMargin(backButton, new Insets(100,0,0,0));
		backButton.prefWidthProperty().bind(right.widthProperty().multiply(0.5));
		backButton.prefHeightProperty().bind(right.heightProperty().multiply(0.05));
		backButton.setOnAction(e -> {
			Scene scene = App.getScene();
		    s = App.getStage();
		    s.setScene(scene);
		});
		
		
		right.getChildren().addAll(ls,c,h,backButton);
		rightVBox = right;
		return right;
	}
	private static HBox createHeaderBox() {
		HBox headerBox = new HBox(30);
		headerBox.setPadding(new Insets(0, 0, 0, 20));
		headerBox.setStyle("-fx-background-color: violet");
		//javafx.scene.control.Button bx = new Button("back");
		mapName = new Label("Your Map:");
		mapName.setFont(Font.font(30));		
		//headerBox.getChildren().add( bx);
		headerBox.getChildren().addAll(mapName);
		return headerBox;
	}
	
	private static GamePane createLeftSidPane(String name) {
		String[][] map = maps.getMap(name);
		gm = new GamePane(map);
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
	private static GamePane createInitLeftSidPane(String[][] name) {
		GamePane gm = new GamePane(name);
		gm.prefWidthProperty().bind(s.widthProperty().multiply(0.7));
		gm.prefHeightProperty().bind(s.heightProperty());
		return gm;
	}
	
	
	
}
