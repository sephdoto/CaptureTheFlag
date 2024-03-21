package org.ctf.ui;



import org.ctf.ui.customobjects.CostumFigurePain;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class PlayGameScreen {
	static Game game;
	static VBox v;
	static HBox center;
	static GamePane gm;
	static Stage s;
	static StoreMapArrays maps = new StoreMapArrays();
	static String[][] exm = { { "b", "p:1_2", "b" }, { "b", "p:2_3", "b" },{ "b", "", "" } };
	static String[][] exm2 = { { "b", "p:1_2", "b","" }, { "b", "p:2_3", "b" ,""},{ "b", "", "","" } };
	static String selected;
	static Label mapName;
	static VBox rightVBox;

	public static void initPlayGameScreen(Stage stage, GamePane pane) {
		gm = pane;
		s = stage;
		game = new Game(gm);
		for(CostumFigurePain cm : gm.allFigures) {
			cm.game = game;
		}
		
		v = new VBox();
		center = new HBox();
		center.setStyle("-fx-background-color:white");
		VBox right = createRightSide();
		gm = pane;
		//center.prefWidthProperty().bind(stage.widthProperty().multiply(0.8));
		center.getChildren().addAll(gm, right);
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
		right.prefWidthProperty().bind(s.widthProperty().multiply(0.2));
		//right.addListner()
		//VBox topRight = new VBox(20);
		Label ls = new Label("Description");
		ls.prefWidthProperty().bind(right.widthProperty().multiply(0.8));
		ls.prefHeightProperty().bind(right.heightProperty().multiply(0.2));
		ls.styleProperty().bind(
	                javafx.beans.binding.Bindings.createStringBinding(() -> {
	                    double fontSize = Math.max(20, Math.min(ls.getWidth() / ls.getText().length(), ls.getHeight() / 2));
	                    return "-fx-font-size: " + fontSize + "px;";
	                }, ls.widthProperty(), ls.heightProperty(), ls.textProperty())
	        );
		//ls.setFont(Font.font(30));
		ls.setWrapText(true);
		ls.setAlignment(Pos.CENTER);
		ls.prefHeightProperty().bind(right.heightProperty().multiply(0.06));
		ls.prefWidthProperty().bind(right.widthProperty().multiply(0.7));
		right.getChildren().addAll(ls);
		rightVBox = right;
		return right;
	}
	
	
	
	
	
	
	
	
	
}
