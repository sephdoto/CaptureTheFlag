package org.ctf.ui;



import org.ctf.ui.customobjects.BackgroundCell;
import org.ctf.ui.customobjects.CostumFigurePain;
import org.ctf.ui.customobjects.Timer;

import configs.Dialogs;
import javafx.animation.Timeline;
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
	private static Label idLabel;
	private static Label typeLabel;
	private static Label attackPowLabel;
	private static Label teamLabel;
	private static Label countLabel;
	static Game game;
	static VBox v;
	static HBox center;
	static GamePane gm;
	static Stage s;
	static StroeMaps maps = new StroeMaps();
	static String[][] exm = { { "b", "p:1_2", "b" }, { "b", "p:2_3", "b" },{ "b", "", "" } };
	static String[][] exm2 = { { "b", "p:1_2", "b","" }, { "b", "p:2_3", "b" ,""},{ "b", "", "","" } };
	static String selected;
	static Label mapName;
	static VBox rightVBox;

	public static void initPlayGameScreen(Stage stage, GamePane pane) {
		gm = pane;
		s = stage;
		game = new Game(gm);
		gm.setGame(game);
		Dialogs.showConfirmationDialog("Start Game", "Willst du das wirklich tun");
		v = new VBox();
		HBox headerBox = createHeaderBox();
		v.getChildren().add(headerBox);
		center = new HBox();
		center.setStyle("-fx-background-color:white");
		VBox right = createRightSide();
		gm = pane;
		//gm.moveFigure(5,);
		//center.prefWidthProperty().bind(stage.widthProperty().multiply(0.8));
		center.getChildren().addAll(gm, right);
		v.getChildren().add(center);
		Scene scene = new Scene(v, 1000, 500);
		stage.setMinHeight(500);
		stage.setMinWidth(900);
		stage.setScene(scene);
		stage.show();
	}
	
	private static HBox createHeaderBox() {
		HBox headerBox = new HBox(30);
		headerBox.setAlignment(Pos.CENTER);
		headerBox.setPadding(new Insets(0, 0, 0, 20));
		headerBox.setStyle("-fx-background-color: white");
		Timer timer  = new Timer(0,0,0);
		headerBox.getChildren().add(timer);
		return headerBox;
	}

	private static VBox createRightSide() {
		VBox rightParent = new VBox();
		rightParent.setPadding(new Insets(30));
		rightParent.setAlignment(Pos.CENTER);
		rightParent.prefWidthProperty().bind(s.widthProperty().multiply(0.3));
		VBox right = new VBox(20);
		right.setStyle("-fx-border-color: black; -fx-border-width: 2px;" + "-fx-background-color: white;"
				+ "-fx-background-radius: 20px; -fx-border-radius: 20px;" + "-fx-alignment: top-center;");
		right.setAlignment(Pos.TOP_CENTER);
		right.setPadding(new Insets(20));
		Label ls = new Label("Description");
		styleLabel2(ls, right);
		VBox desBox = createDiscriptionBox();
		right.getChildren().addAll(ls,desBox);
		rightParent.getChildren().add(right);
		rightVBox = rightParent;
		return rightParent;
	}
	
	public static void styleLabel(Label ls, VBox top) {
		ls.prefWidthProperty().bind(top.widthProperty().multiply(0.8));
		ls.prefHeightProperty().bind(top.heightProperty().multiply(0.2));
		ls.styleProperty().bind(
	                javafx.beans.binding.Bindings.createStringBinding(() -> {
	                    double fontSize = Math.max(20, Math.min(ls.getWidth() / ls.getText().length(), ls.getHeight() / 2));
	                    return "-fx-font-size: " + fontSize + "px;";
	                }, ls.widthProperty(), ls.heightProperty(), ls.textProperty())
	        );
		//ls.setFont(Font.font(30));
		ls.setWrapText(true);
		ls.setAlignment(Pos.BASELINE_LEFT);
	}
	public static void styleLabel2(Label ls, VBox top) {
		ls.prefWidthProperty().bind(top.widthProperty().multiply(0.8));
		ls.prefHeightProperty().bind(top.heightProperty().multiply(0.2));
		ls.styleProperty().bind(
	                javafx.beans.binding.Bindings.createStringBinding(() -> {
	                    double fontSize = Math.max(20, Math.min(ls.getWidth() / ls.getText().length(), ls.getHeight() / 2));
	                    return "-fx-font-size: " + fontSize + "px;";
	                }, ls.widthProperty(), ls.heightProperty(), ls.textProperty())
	        );
		//ls.setFont(Font.font(30));
		ls.setWrapText(true);
		ls.setAlignment(Pos.CENTER);
	}
	
	public static VBox createDiscriptionBox() {
		VBox deBox = new VBox(20);
		deBox.setAlignment(Pos.BASELINE_LEFT);
		
		 idLabel = new Label("id: -");
		 typeLabel = new Label("type: -");
		 teamLabel = new Label("team: -");
		 attackPowLabel = new Label("attackpower: -");
		 countLabel = new Label("count: - ");
		styleLabel(countLabel, deBox);
		styleLabel(teamLabel, deBox);
		styleLabel(idLabel, deBox);
		styleLabel(typeLabel, deBox);
		styleLabel(attackPowLabel, deBox);
		deBox.getChildren().addAll(idLabel,teamLabel, typeLabel,attackPowLabel, countLabel);
		return deBox;
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
