package org.ctf.ui;


import static org.mockito.ArgumentMatchers.isNull;

import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import org.ctf.shared.state.GameState;

import configs.Dialogs;
import configs.GameMode;
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
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.cell.ChoiceBoxListCell;
import javafx.scene.effect.DropShadow;
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
	static VBox topContainerVBox;
	static HBox center;
	static GamePane gm;
	static Stage s;
	static String selected;
	static Label mapName;
	static VBox rightVBox;
	static GameState state;

	public static void initCreateGameScreen(Stage stage) {
		ImageLoader.loadImages();
		s = stage;
		StroeMaps.initDefaultMaps();
		topContainerVBox = new VBox();
		HBox headerBox = createHeaderBox();
		center = new HBox();
		center.setStyle("-fx-background-color:beige");
		VBox right = createRightSide();
		chooseDefaultMap();
		center.prefWidthProperty().bind(stage.widthProperty().multiply(0.7));
		center.getChildren().addAll(gm, right);
		topContainerVBox.getChildren().add(headerBox);
		topContainerVBox.getChildren().add(center);
		Scene scene = new Scene(topContainerVBox, 1000, 500);
		stage.setMinHeight(500);
		stage.setMinWidth(900);
		stage.setScene(scene);
		stage.setFullScreen(true);
		stage.show();
	}
	
	private static void chooseDefaultMap() {
		selected = StroeMaps.getRandomMapName();
		displayMapname();
		gm = createLeftSidPane(selected);
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
		Button startOnlineGame = createStartButton("Start Online Game", parent, GameMode.Online);
		Button startAiGame = createStartButton("Start Offline Game", parent, GameMode.OneDevice);
		Button startSingleDeviceGame = createStartButton("Play Against Ai", parent, GameMode.Online);
		createGameButtonBox.setPadding(new Insets(20));
		createGameButtonBox.getChildren().addAll(startOnlineGame,startSingleDeviceGame,startAiGame);
		return createGameButtonBox;
	}
	@Deprecated
	private static Button createBackButton(VBox parent) {
		Button backButton = new Button("back");
		applyButtonStyle(backButton);
		VBox.setMargin(backButton, new Insets(10,0,0,0));
		backButton.prefWidthProperty().bind(parent.widthProperty().multiply(0.5));
		backButton.prefHeightProperty().bind(parent.heightProperty().multiply(0.05));
		backButton.setOnAction(e -> {
			//perfromBackButtonClick();
		});
		return backButton;
	}
	
	private static Button createStartButton(String text, VBox parent, GameMode mode) {
		Button startButton = new Button(text);
		startButton.prefWidthProperty().bind(parent.widthProperty().multiply(0.7));
		startButton.prefHeightProperty().bind(parent.heightProperty().multiply(0.1));
		applyButtonStyle(startButton);
		startButton.setOnAction(event ->{
			PlayGameScreen.initPlayGameScreen(s, state, mode);
		});
		return startButton;
	}
	
	private static void perfromBackButtonClick(String header, String content) {
		boolean goBack = Dialogs.showConfirmationDialog(header, content);
		if (goBack) {
			Scene scene = App.getScene();
		    s.setScene(scene);
		}
	}
	
	private static ChoiceBox<String> createChoiceBox(VBox parent) {
		ChoiceBox<String> c = new ChoiceBox<String>();
		c.setStyle("-fx-background-color: white; ");
		c.getItems().addAll(StroeMaps.getValues());
		c.setValue(selected);//wichtig
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
		HBox headerBox = new HBox();
		//headerBox.setStyle("-fx-background-color: lightblue");
		HBox h1 = new HBox();
		h1.prefWidthProperty().bind(headerBox.widthProperty().multiply(0.7011));
		h1.setStyle("-fx-background-color: beige");
		h1.setAlignment(Pos.CENTER);
		HBox h2 = new HBox();
		h2.prefWidthProperty().bind(headerBox.widthProperty().multiply(0.3));
		h2.setStyle("-fx-background-color: lightblue");
		h2.setAlignment(Pos.TOP_RIGHT);
		MenuBar m = createBackButtonMenuBar("Go Back", "do you wnat to go back to the HomeScreen");
		h2.getChildren().add(m);
		mapName = new Label();
		mapName.setFont(Font.font(30));		
		h1.getChildren().addAll(mapName);
		headerBox.getChildren().addAll(h1,h2);
		return headerBox;
	}
	public static  MenuBar createBackButtonMenuBar(String header, String content ) {
	    MenuItem back = new MenuItem("go back to homescreen");
	    
	    back.setOnAction(
	        e -> {
	          perfromBackButtonClick(header,content);
	        });
	    Menu fileMenu = new Menu("Leave Game");
	    fileMenu.getItems().add(0, back);
	    MenuItem lightMode = new MenuItem("lightmode");
	    MenuItem darkMode = new MenuItem("darkmode");
	    Menu themeMenu = new Menu("Theme");
	    
	    themeMenu.getItems().addAll(lightMode,darkMode);
	    MenuBar mn = new MenuBar();
	    mn.setStyle("-fx-background-color:"
			      + " white;"
			      + " -fx-text-fill: black");
	    mn.getMenus().addAll(themeMenu,fileMenu);
	    return mn;
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
			displayMapname();
			center.getChildren().clear();
			GamePane p = createLeftSidPane(selected);
			//l2.setText(selected);
			center.getChildren().addAll(p,rightVBox);
		});
	}
	
	private static void displayMapname() {
		mapName.setText("Your map: " +  selected);
	}
	
public static void applyButtonStyle(Button button) {
	    button.setStyle("-fx-background-color:"
			      + " linear-gradient(#5a5c5e, #3e3f41);"
			      + " -fx-background-radius: 20; -fx-border-radius: 20;"
			      + " -fx-text-fill: #FFFFFF");
	    button.hoverProperty().addListener((observable, oldValue, newValue) -> {
	      if (newValue) {
	        button.setStyle("-fx-background-color: linear-gradient(#6a6c6e, #4e4f51);"
			          + " -fx-background-radius: 20; -fx-border-radius: 20;"
			          + "-fx-text-fill: #FFFFFF");
	      } else {
	        button.setStyle("-fx-background-color:"
	  		      + " linear-gradient(#5a5c5e, #3e3f41);"
			      + " -fx-background-radius: 20; -fx-border-radius: 20;"
			      + " -fx-text-fill: #FFFFFF");
	      }
	    });
	  }

	
	
	
}
