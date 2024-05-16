package org.ctf.ui;

import org.ctf.shared.ai.AIConfig;
import org.ctf.shared.constants.Enums.AI;
import org.ctf.ui.customobjects.PopUpPane;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class PopUpCreatorEnterTeamName {
	Scene scene;
	boolean isMain;
	boolean isAi;
	StackPane root;
	PopUpPane before;
	TextField enterNamefield;
	PopUpPane enterNamePopUp;
	String teamName;
	AIConfig config;
	

	HomeSceneController hsc;
	private ObjectProperty<Font> popUpLabel = new SimpleObjectProperty<Font>(Font.getDefault());
	private ObjectProperty<Font> leaveButtonText = new SimpleObjectProperty<Font>(Font.getDefault());
	
	
	public PopUpCreatorEnterTeamName(Scene scene,StackPane root, PopUpPane before, HomeSceneController hsc, boolean isMain, boolean isAi) {
		this.scene = scene;
		this.root = root;
		this.before = before;
		this.hsc = hsc;
		this.isMain = isMain;
		this.isAi = isAi;
		manageFontSizes();
	}
	
	private void manageFontSizes() {
		scene.widthProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth, Number newWidth) {
				popUpLabel.set(Font.font(newWidth.doubleValue() / 50));
				leaveButtonText.set(Font.font(newWidth.doubleValue() / 80));
			}
		});
	}
	
	
	
	public void createEnterNamePopUp() {
		enterNamePopUp = new PopUpPane(scene, 0.5, 0.3);
		root.getChildren().remove(before);
		VBox top = new VBox();
		top.heightProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.09;
			top.setSpacing(spacing);
		});
		Label l = new Label("Select Team Name");
		l.prefWidthProperty().bind(enterNamePopUp.widthProperty());
		l.setAlignment(Pos.CENTER);
		l.getStyleClass().add("custom-label");
		l.fontProperty().bind(popUpLabel);
		top.getChildren().add(l);
		HBox enterNameBox = new HBox();
		enterNameBox.setAlignment(Pos.CENTER);
		enterNamefield = CretaeGameScreenV2.createTextfield("Your Team Name",0.5);
		enterNamefield.prefWidthProperty().bind(enterNameBox.widthProperty().multiply(0.8));
		enterNameBox.getChildren().add(enterNamefield);
		top.getChildren().add(enterNameBox);
		HBox centerLeaveButton = new HBox();
		enterNamePopUp.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.05; 
			centerLeaveButton.setSpacing(newSpacing);
		});
		centerLeaveButton.prefHeightProperty().bind(enterNamePopUp.heightProperty().multiply(0.4));
		centerLeaveButton.setAlignment(Pos.CENTER);
		centerLeaveButton.getChildren().addAll(createEnterButton(),createBackButton("name"));
		top.getChildren().add(centerLeaveButton);
		enterNamePopUp.setContent(top);
		root.getChildren().add(enterNamePopUp);
	}

	
	private Button createEnterButton() {
		Button exit = new Button("Enter");
		exit.fontProperty().bind(leaveButtonText);
		exit.getStyleClass().add("leave-button");
		exit.prefWidthProperty().bind(root.widthProperty().multiply(0.1));
		exit.prefHeightProperty().bind(exit.widthProperty().multiply(0.25));
		exit.setOnAction(e -> {
			if (enterNamefield.getText().isEmpty()) {
				CretaeGameScreenV2.informationmustBeEntered(enterNamefield, "custom-search-field2-mustEnter","custom-search-field2");
			}else {
				teamName = enterNamefield.getText();
			
			if (isMain && !isAi) {
				CreateGameController.createHumanClient(teamName, true);
				hsc.switchToWaitGameScene(App.getStage());
			}
			if(!isMain && !isAi) {
				CreateGameController.createHumanClient(teamName, true);
				root.getChildren().remove(enterNamePopUp);
			}
			if(isMain && isAi) {
				CreateGameController.createAiClient(teamName, AI.RANDOM, config, isMain);
				hsc.switchToWaitGameScene(App.getStage());
			}
			if(!isMain && isAi) {
				CreateGameController.createAiClient(teamName, AI.RANDOM, config, isMain);
				root.getChildren().remove(enterNamePopUp);
			}
			}
		});
		return exit;
	}
	
	

	private Button createBackButton(String text) {
		Button exit = new Button("back");
		exit.fontProperty().bind(leaveButtonText);
		exit.getStyleClass().add("leave-button");
		exit.prefWidthProperty().bind(root.widthProperty().multiply(0.1));
		exit.prefHeightProperty().bind(exit.widthProperty().multiply(0.25));
		exit.setOnAction(e -> {
			if(!isMain && !isAi) {
				root.getChildren().remove(enterNamePopUp);
			}else {
				root.getChildren().remove(enterNamePopUp);
				root.getChildren().add(before);
			}
			
		});
		return exit;
	}
	public AIConfig getConfig() {
		return config;
	}

	public void setConfig(AIConfig config) {
		this.config = config;
	}
}
