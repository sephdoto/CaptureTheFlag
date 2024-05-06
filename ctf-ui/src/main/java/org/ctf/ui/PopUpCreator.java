package org.ctf.ui;

import org.ctf.shared.constants.Descriptions;
import org.ctf.ui.customobjects.PopUpPane;

import configs.ImageLoader;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.PopupWindow.AnchorLocation;
import javafx.util.Duration;

public class PopUpCreator {
	
	 private StackPane root;
	private PopUpPane aiLevelPopUpPane;
	private PopUpPane aiorHumanpopup;
	private Scene scene;
	private PopUpPane aiconfig;
	private ObjectProperty<Font> popUpLabel = new SimpleObjectProperty<Font>(Font.getDefault());
	private ObjectProperty<Font> leaveButtonText = new SimpleObjectProperty<Font>(Font.getDefault());
	private ObjectProperty<Font> aiPowerText = new SimpleObjectProperty<Font>(Font.getDefault());
	private ObjectProperty<Font> aiConfigHeader = new SimpleObjectProperty<Font>(Font.getDefault());
	private ObjectProperty<Font> configButtonText = new SimpleObjectProperty<Font>(Font.getDefault());

	
	public PopUpCreator(Scene scene, StackPane root) {
		this.scene = scene;
		this.root = root;
		manageFontSizes();
	}
	
	private void manageFontSizes() {
		root.widthProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth, Number newWidth) {
				popUpLabel.set(Font.font(newWidth.doubleValue() / 50));
				leaveButtonText.set(Font.font(newWidth.doubleValue() / 80));
				aiPowerText.set(Font.font(newWidth.doubleValue() / 50));
				leaveButtonText.set(Font.font(newWidth.doubleValue() / 80));
				aiConfigHeader.set(Font.font(newWidth.doubleValue() / 50));
				configButtonText.set(Font.font(newWidth.doubleValue() / 60));
			}
		});
	}
	
	public PopUpPane createAiLevelPopUp(PopUpPane aiOrHuman, TextField portText, TextField serverIPText) {
		aiorHumanpopup = aiOrHuman;
		root.getChildren().remove(aiorHumanpopup);
		portText.setDisable(true);
		serverIPText.setDisable(true);
		aiLevelPopUpPane = new PopUpPane(scene, 0.6, 0.4); 
		VBox top = new VBox();
		top.heightProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.1;
			top.setSpacing(spacing);
		});
		Label l = new Label("Choose AI");
		l.prefWidthProperty().bind(aiLevelPopUpPane.widthProperty());
		l.setAlignment(Pos.CENTER);
		l.getStyleClass().add("custom-label");
		l.fontProperty().bind(popUpLabel);
		top.getChildren().add(l);
		HBox buttonBox = new HBox();
		buttonBox.setAlignment(Pos.CENTER);
		aiLevelPopUpPane.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.03;
			double padding = newValue.doubleValue() * 0.03;
			buttonBox.setSpacing(newSpacing);
			buttonBox.setPadding(new Insets(0, padding, 0, padding));
		});
		buttonBox.getChildren().addAll(createAIPowerButton("RANDOM", 0.365), createAIPowerButton("MCTS",0.53));
		HBox buttonBox2 = new HBox();
		buttonBox2.setAlignment(Pos.CENTER);
		aiLevelPopUpPane.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.03;
			double padding = newValue.doubleValue() * 0.03;
			buttonBox2.setSpacing(newSpacing);
			buttonBox2.setPadding(new Insets(0, padding, 0, padding));
		});
		buttonBox2.getChildren().addAll(createAIPowerButton("MCTS-IMPROVED",0.05), createAIPowerButton("EXPERIMENTAL",0.15));
		top.getChildren().addAll(buttonBox, buttonBox2);
		HBox centerLeaveButton = new HBox();
		centerLeaveButton.prefHeightProperty().bind(aiLevelPopUpPane.heightProperty().multiply(0.2));
		centerLeaveButton.setAlignment(Pos.CENTER);
		centerLeaveButton.getChildren().add(createBackButton("ai"));
		top.getChildren().add(centerLeaveButton);
		aiLevelPopUpPane.setContent(top);
		root.getChildren().add(aiLevelPopUpPane);
		return aiLevelPopUpPane;
	}
	
	
	private Button createBackButton(String text) {
		Button exit = new Button("back");
		exit.fontProperty().bind(leaveButtonText);
		exit.getStyleClass().add("leave-button");
		exit.prefWidthProperty().bind(root.widthProperty().multiply(0.1));
		exit.prefHeightProperty().bind(exit.widthProperty().multiply(0.25));
		exit.setOnAction(e -> {
				root.getChildren().remove(aiLevelPopUpPane);
				root.getChildren().add(aiorHumanpopup);
		});
		return exit;
	}
	
	private Button createAIPowerButton(String pow, double relSpacing) {
		Button power = new Button(pow);
		power.fontProperty().bind(aiPowerText);
		power.getStyleClass().add("ai-button-easy");
		Image mp = new Image(getClass().getResourceAsStream("i1.png"));
		ImageView vw = new ImageView(mp);
		power.setGraphic(vw);
		power.setContentDisplay(ContentDisplay.RIGHT);
		power.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * relSpacing; // Beispiel: 5% der Höhe als Spacing
			power.setGraphicTextGap(newSpacing);
		});
		String showAiInfo = Descriptions.describe(pow);
		vw.fitWidthProperty().bind(power.widthProperty().divide(8));
		vw.setPreserveRatio(true);
		power.prefWidthProperty().bind(root.widthProperty().multiply(0.22));
		power.prefHeightProperty().bind(power.widthProperty().multiply(0.45));
		power.setOnAction(e -> {
			//hsc.switchToWaitGameScene(App.getStage());
			root.getChildren().remove(aiorHumanpopup);
			root.getChildren().add(createConfigPane(1,1));
		});
		return power;
	}

	
	
	
	public  PopUpPane createConfigPane(double widht, double hight) {
		aiconfig = new PopUpPane(scene, widht, hight);
		StackPane configRoot = new StackPane();
		configRoot.getStyleClass().add("join-root");
		configRoot.getChildren().add(createMainBox());
		VBox mainBox = createMainBox();
		mainBox.getChildren().add(createHeader());
		HBox sep = createMiddleHBox();
		VBox leftBoss = createLeftVBox();
		leftBoss.getChildren().add(createLeftHeader(leftBoss));
		VBox rightBoss = createRightVBox();
		rightBoss.getChildren().add(createLeftHeader(rightBoss));
		StackPane left = createOptionPane(0.3,0.6);
		StackPane right = createOptionPane(0.45,0.6);
		leftBoss.getChildren().add(left);
		rightBoss.getChildren().add(right);
		sep.getChildren().add(leftBoss);
		sep.getChildren().add(rightBoss);
		mainBox.getChildren().add(sep);
		HBox buttomBox = createButtomHBox();
		buttomBox.getChildren().addAll(createConfigButton("Play and Save"), createConfigButton("Save"),createConfigButton("Leave"));
		mainBox.getChildren().add(buttomBox);
		configRoot.getChildren().add(mainBox);
		aiconfig.setContent(configRoot);
		return aiconfig;
	}
	
	private VBox createMainBox() {
		VBox mainBox = new VBox();
		mainBox.setAlignment(Pos.TOP_CENTER);
		mainBox.setSpacing(30);
		aiconfig.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.03;
			mainBox.setSpacing(newSpacing);
		});
		return mainBox;
	}
	
	private VBox createLeftVBox(){
		VBox leftBox = new VBox();
		leftBox.setStyle("-fx-background-color: blue");
		leftBox.setAlignment(Pos.TOP_CENTER);
		leftBox.prefWidthProperty().bind(aiconfig.widthProperty().multiply(0.55));
		leftBox.prefHeightProperty().bind(aiconfig.heightProperty().multiply(0.65));
		return leftBox;
	}
	
	private VBox createRightVBox(){
		VBox rightBox = new VBox();
		rightBox.setStyle("-fx-background-color: blue");
		rightBox.setAlignment(Pos.TOP_CENTER);
		rightBox.prefWidthProperty().bind(aiconfig.widthProperty().multiply(0.35));
		rightBox.prefHeightProperty().bind(aiconfig.heightProperty().multiply(0.65));
		return rightBox;
	}
	
	private Label createLeftHeader(VBox parent) {
		Label l = new Label("Hyperparameters");
		l.setAlignment(Pos.CENTER);
		l.fontProperty().bind(aiConfigHeader);
		l.prefWidthProperty().bind(parent.widthProperty().multiply(0.7));
		return l;
	}
	
	private HBox createMiddleHBox() {
		HBox sep = new HBox();
		sep.setAlignment(Pos.CENTER);
		sep.setSpacing(50);
		aiconfig.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.05;
			sep.setSpacing(newSpacing);
		});
		return sep;
	}
	
	private HBox createButtomHBox() {
		HBox buttonBox = new HBox();
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setSpacing(50);
		aiconfig.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.05;
			buttonBox.setSpacing(newSpacing);
		});
		return buttonBox;
	}
	
	private Button createConfigButton(String text) {
		Button configButton = new Button(text);
		configButton.fontProperty().bind(configButtonText);
		configButton.getStyleClass().add("leave-button");
		configButton.prefWidthProperty().bind(root.widthProperty().multiply(0.15));
		configButton.prefHeightProperty().bind(configButton.widthProperty().multiply(0.25));
		configButton.setOnAction(e -> {
			//hsc.switchToCreateGameScene(App.getStage());
		});
		return configButton;
	}
	
	
	

	
	
	
	public StackPane createOptionPane(double relWidth, double relHeight) {
		StackPane pane = new StackPane();
		pane.getStyleClass().add("option-pane");
		pane.setPrefSize(250, 250);
		pane.prefWidthProperty().bind(aiconfig.widthProperty().multiply(relWidth));
		pane.prefHeightProperty().bind(aiconfig.heightProperty().multiply(relHeight));
		return pane;
	}
	
	private ImageView createHeader() {
		Image mp = new Image(getClass().getResourceAsStream("multiplayerlogo.png"));
		ImageView mpv = new ImageView(mp);
		mpv.fitWidthProperty().bind(aiconfig.widthProperty().multiply(0.6));
		mpv.setPreserveRatio(true);
		mpv.fitHeightProperty().bind(aiconfig.heightProperty().multiply(0.08));
		return mpv;
	}
	
	
	
}
