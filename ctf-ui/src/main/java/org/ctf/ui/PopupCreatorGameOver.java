package org.ctf.ui;

import org.ctf.shared.ai.AIConfig;
import org.ctf.shared.constants.Enums.AI;
import org.ctf.ui.customobjects.PopUpPane;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

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
		popUpLabel = new SimpleObjectProperty<Font>(Font.font(scene.getWidth()/50));
		leaveButtonText = new SimpleObjectProperty<Font>(Font.font(scene.getWidth()/80));
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
		enterNamePopUp = new PopUpPane(scene, 0.6, 0.5);
		StackPane poproot = new StackPane();
		poproot.getChildren().add(createBackgroundImage(poproot));
		//poproot.getChildren().add(createHeader(poproot));
		VBox top = new VBox();
		top.heightProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.09;
			top.setSpacing(spacing);
		});
		Label l = new Label("Select Team Name");
		l.prefWidthProperty().bind(enterNamePopUp.widthProperty());
		l.setAlignment(Pos.CENTER);
		l.getStyleClass().add("custom-label");
		l.setFont(Font.font(scene.getWidth()/50));
		l.fontProperty().bind(popUpLabel);
		top.getChildren().add(createHeader(poproot));
		poproot.getChildren().add(top);
		enterNamePopUp.setContent(poproot);
		root.getChildren().add(enterNamePopUp);
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
}
