package org.ctf.ui;

import org.ctf.shared.state.GameState;
import org.ctf.ui.customobjects.Timer;
import org.ctf.ui.customobjects.Timer2;

import configs.Dialogs;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
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
	static String[][] exm = { { "b", "p:1_2", "b" }, { "b", "p:2_3", "b" }, { "b", "", "" } };
	static String[][] exm2 = { { "b", "p:1_2", "b", "" }, { "b", "p:2_3", "b", "" }, { "b", "", "", "" } };
	static String selected;
	static Label mapName;
	static VBox rightVBox;

	public static void initPlayGameScreen(Stage stage, GameState state) {
		s = stage;
		gm = createLeftSidPane(state);
		
		game = new Game(gm);
		gm.setGame(game);
		Dialogs.showConfirmationDialog("Start Game", "Willst du das wirklich tun");
		v = new VBox();
		//HBox headerBox = createHeaderBox();
		//v.getChildren().add(headerBox);
		center = new HBox();
		center.setStyle("-fx-background-color:white");
		VBox right = createRightSide();
		center.getChildren().addAll(gm, right);
		v.getChildren().add(center);
		Scene scene = new Scene(v);
		stage.setScene(scene);
		 stage.setFullScreen(true);
		stage.show();
	}
	private static GamePane createLeftSidPane(GameState state) {
		gm = new GamePane(state);
		gm.prefWidthProperty().bind(s.widthProperty().multiply(0.7));
		gm.prefHeightProperty().bind(s.heightProperty());
		return gm;
	}

	private static HBox createHeaderBox() {
		HBox headerBox = new HBox(30);
		headerBox.setAlignment(Pos.CENTER);
		headerBox.setPadding(new Insets(0, 0, 0, 20));
		headerBox.setStyle("-fx-background-color: white");
		Timer timer1 = new Timer(0,0,0);
		Timer2 timer = new Timer2(0, 5, 0);
		headerBox.getChildren().addAll(timer1,timer);
		return headerBox;
	}
	
	private static VBox createRightSide() {
		VBox rightParent = new VBox();
		HBox headerBox = createHeaderBox();
		rightParent.setPadding(new Insets(30));
		rightParent.setAlignment(Pos.TOP_CENTER);
		rightParent.prefWidthProperty().bind(s.widthProperty().multiply(0.3));
		rightParent.prefHeightProperty().bind(s.heightProperty().divide(2));
		VBox right = new VBox(20);
		right.setStyle("-fx-border-color: black; -fx-border-width: 2px;" + "-fx-background-color: white;"
				+ "-fx-background-radius: 20px; -fx-border-radius: 20px;" + "-fx-alignment: top-center;");
		right.setAlignment(Pos.TOP_CENTER);
		right.setPadding(new Insets(20));
		right.prefWidthProperty().bind(s.widthProperty().divide(2));
		right.prefHeightProperty().bind(s.heightProperty().divide(2));
		Label ls = new Label("Description");
		styleLabel2(ls, right);
		VBox desBox = createDiscriptionBox();
		
		right.getChildren().addAll(ls, desBox);
		rightParent.getChildren().addAll(headerBox,right);
		rightVBox = rightParent;
		return rightParent;
	}

	public static void styleLabel(Label ls, VBox top) {
		ls.prefWidthProperty().bind(top.widthProperty().multiply(0.8));
		ls.prefHeightProperty().bind(top.heightProperty().multiply(0.2));
		ls.styleProperty().bind(javafx.beans.binding.Bindings.createStringBinding(() -> {
			double fontSize = Math.max(20, Math.min(ls.getWidth() / ls.getText().length(), ls.getHeight() / 2));
			return "-fx-font-size: " + fontSize + "px;";
		}, ls.widthProperty(), ls.heightProperty(), ls.textProperty()));
		// ls.setFont(Font.font(30));
		ls.setWrapText(true);
		ls.setAlignment(Pos.BASELINE_LEFT);
	}
	

	public static void styleLabel2(Label ls, VBox top) {
		//ls.prefWidthProperty().bind(top.widthProperty().multiply(0.8));
		//ls.prefHeightProperty().bind(top.heightProperty().multiply(0.2));
		ls.styleProperty().bind(javafx.beans.binding.Bindings.createStringBinding(() -> {
			double fontSize = Math.max(20, Math.min(ls.getWidth() / ls.getText().length(), ls.getHeight() / 2));
			return "-fx-font-size: " + fontSize + "px;";
		}, ls.widthProperty(), ls.heightProperty(), ls.textProperty()));
		ls.setWrapText(true);
		ls.setAlignment(Pos.CENTER);
	}

	public static VBox createDiscriptionBox() {
		VBox deBox = new VBox(10);
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
		deBox.getChildren().addAll(idLabel, teamLabel, typeLabel, attackPowLabel, countLabel);
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
