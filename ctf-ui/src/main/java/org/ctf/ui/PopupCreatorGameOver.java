package org.ctf.ui;

import static org.mockito.Mockito.doAnswer;

import org.ctf.shared.ai.AIConfig;
import org.ctf.shared.constants.Enums.AI;
import org.ctf.ui.customobjects.PopUpPane;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class PopupCreatorGameOver {
	Scene scene;
	boolean isMain;
	boolean isAi;
	StackPane root;
	PopUpPane before;
	TextField enterNamefield;
	PopUpPane enterNamePopUp;
	String teamName;
	AIConfig config;
	AI aitype;

	public AI getAitype() {
		return aitype;
	}

	public void setAitype(AI aitype) {
		this.aitype = aitype;
	}

	HomeSceneController hsc;
	private ObjectProperty<Font> popUpLabel;
	private ObjectProperty<Font> leaveButtonText; 
	
	
	public PopupCreatorGameOver(Scene scene,StackPane root, HomeSceneController hsc) {
		this.scene = scene;
		this.root = root;
		this.hsc = hsc;
		popUpLabel = new SimpleObjectProperty<Font>(Font.font(scene.getWidth()/30));
		leaveButtonText = new SimpleObjectProperty<Font>(Font.font(scene.getWidth()/80));

		
		manageFontSizes();
	}
	private void manageFontSizes() {
		scene.widthProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth, Number newWidth) {
				popUpLabel.set(Font.font(newWidth.doubleValue() / 40));
				leaveButtonText.set(Font.font(newWidth.doubleValue() / 80));
			}
		});
	}
	public void createGameOverPopUpforOneWinner(String name) {
		enterNamePopUp = new PopUpPane(scene, 0.6, 0.5);
		StackPane poproot = new StackPane();
		poproot.getChildren().add(createBackgroundImage(poproot));
		VBox top = new VBox();
		top.heightProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.09;
			double padding = newVal.doubleValue() * 0.15;
			top.setSpacing(spacing);
			top.setPadding(new Insets(padding, 0, 0, 0));
		});
		top.setAlignment(Pos.TOP_CENTER);
		Label l = new Label("The Winner is " + name);
		l.prefWidthProperty().bind(enterNamePopUp.widthProperty());
		l.setAlignment(Pos.CENTER);
		l.setTextFill(Color.GOLD);
		l.setFont(Font.font(scene.getWidth()/50));
		l.fontProperty().bind(popUpLabel);
		Button playAgainButton = createConfigButton("Play Again");
		playAgainButton.setVisible(false);
		Button analyseGameButton = createConfigButton("Analyse Game");
		analyseGameButton.setVisible(false);
		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(2), l);
        translateTransition.setFromY(-scene.getHeight());
        translateTransition.setToY(0);
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(2), l);
        scaleTransition.setFromX(0);
        scaleTransition.setFromY(0);
        scaleTransition.setToX(1);
        scaleTransition.setToY(1);
        FadeTransition fadeTransition1 = new FadeTransition(Duration.seconds(1), playAgainButton);
        fadeTransition1.setFromValue(0);
        fadeTransition1.setToValue(1);
        fadeTransition1.setOnFinished(event -> playAgainButton.setVisible(true));
        FadeTransition fadeTransition2 = new FadeTransition(Duration.seconds(1), analyseGameButton);
        fadeTransition2.setFromValue(0);
        fadeTransition2.setToValue(1);
        fadeTransition2.setOnFinished(event -> analyseGameButton.setVisible(true));
        SequentialTransition textTransition = new SequentialTransition(translateTransition, scaleTransition);
        ParallelTransition buttonTransition = new ParallelTransition(fadeTransition1, fadeTransition2);
        SequentialTransition mainTransition = new SequentialTransition(textTransition, buttonTransition);
        mainTransition.play();
        HBox x = createButtonBox();
		x.getChildren().addAll(playAgainButton,analyseGameButton);
		top.getChildren().add(createHeader(poproot));
		top.getChildren().add(l);
        top.getChildren().add(x);
		poproot.getChildren().add(top);
		enterNamePopUp.setContent(poproot);
		root.getChildren().add(enterNamePopUp);
	}
	
	
	public void createGameOverPopUpforMoreWinners(String name) {
		enterNamePopUp = new PopUpPane(scene, 0.6, 0.5);
		StackPane poproot = new StackPane();
		poproot.getChildren().add(createBackgroundImage(poproot));
		VBox top = new VBox();
		top.heightProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.09;
			double padding = newVal.doubleValue() * 0.15;
			top.setSpacing(spacing);
			top.setPadding(new Insets(padding, 0, 0, 0));
		});
		top.setAlignment(Pos.TOP_CENTER);
		Label l = new Label("The Winner is Atkinson ");
		l.prefWidthProperty().bind(enterNamePopUp.widthProperty());
		l.setAlignment(Pos.CENTER);
		l.setTextFill(Color.GOLD);
		l.setFont(Font.font(scene.getWidth()/50));
		l.fontProperty().bind(popUpLabel);
		Button playAgainButton = createConfigButton("Play Again");
		Button analyseGameButton = createConfigButton("Analyse Game");
        HBox x = createButtonBox();
		x.getChildren().addAll(playAgainButton,analyseGameButton);
		top.getChildren().add(createHeader(poproot));
		top.getChildren().add(l);
        top.getChildren().add(x);
		poproot.getChildren().add(top);
		enterNamePopUp.setContent(poproot);
		root.getChildren().add(enterNamePopUp);
	}
	private ScrollPane createWinnersPane(String[] winners) {
		 ScrollPane scroller = new ScrollPane();
		    scroller.getStyleClass().clear();
		    scroller.prefWidthProperty().bind(enterNamePopUp.widthProperty());
		    scroller.setHbarPolicy(ScrollBarPolicy.NEVER);
		    Label l = new Label("Winners");
		    l.setUnderline(true);
			l.prefWidthProperty().bind(enterNamePopUp.widthProperty());
			l.setAlignment(Pos.CENTER);
			l.setTextFill(Color.GOLD);
			l.setFont(Font.font(scene.getWidth()/50));
			l.fontProperty().bind(popUpLabel);
		    VBox winnerBox = new VBox();
		    return scroller;
		    
	}
	

	
	
	 private ImageView createHeader(StackPane conRoot) {
		    Image mp = new Image(getClass().getResourceAsStream("gameOver3.png"));
		    ImageView mpv = new ImageView(mp);
		    mpv.fitHeightProperty().bind(conRoot.heightProperty().multiply(0.5));
		    mpv.fitWidthProperty().bind(conRoot.widthProperty().multiply(0.8));
		    mpv.setPreserveRatio(true);
		    return mpv;
		  }
	
	private ImageView createBackgroundImage(StackPane configRoot) {
		Image mp = new Image(getClass().getResourceAsStream("konfetti2.gif"));
		ImageView mpv = new ImageView(mp);
		mpv.fitHeightProperty().bind(configRoot.heightProperty().divide(1.1));
		mpv.fitWidthProperty().bind(configRoot.widthProperty().divide(1.1));
		//mpv.setPreserveRatio(true);
		mpv.setOpacity(0.7);
		return mpv;
	}
	private HBox createButtonBox() {
		HBox centerLeaveButton = new HBox();
		enterNamePopUp.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.05;
			centerLeaveButton.setSpacing(newSpacing);
		});
		centerLeaveButton.prefHeightProperty().bind(enterNamePopUp.heightProperty().multiply(0.3));
		centerLeaveButton.setAlignment(Pos.CENTER);
		return centerLeaveButton;
	}
	
	private Button createConfigButton(String text) {
		Button configButton = new Button(text);
		configButton.fontProperty().bind(leaveButtonText);
		configButton.getStyleClass().add("leave-button");
		configButton.prefWidthProperty().bind(root.widthProperty().multiply(0.15));
		configButton.prefHeightProperty().bind(configButton.widthProperty().multiply(0.25));
		return configButton;
	}
}
