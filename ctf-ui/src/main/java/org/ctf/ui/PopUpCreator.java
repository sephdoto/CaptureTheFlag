package org.ctf.ui;

import java.util.HashMap;

import org.ctf.shared.ai.AI_Config;
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
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
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


/**
 * @author Manuel Krakowski
 * Creates the PopUpPanes to choose an AI an and custom it if possible
 */
public class PopUpCreator {
	private StackPane root;
	private PopUpPane aiLevelPopUpPane;
	private PopUpPane aiorHumanpopup;
	private Scene scene;
	private PopUpPane aiconfig;
	private AI_Config defaultConfig;
	private HashMap<String, Integer> multipliers = new HashMap<String, Integer>();
	private SpinnerValueFactory<Integer> values;
	private SpinnerValueFactory<Double> values2;
	private ObjectProperty<Font> popUpLabel = new SimpleObjectProperty<Font>(Font.getDefault());
	private ObjectProperty<Font> leaveButtonText = new SimpleObjectProperty<Font>(Font.getDefault());
	private ObjectProperty<Font> aiPowerText = new SimpleObjectProperty<Font>(Font.getDefault());
	private ObjectProperty<Font> aiConfigHeader = new SimpleObjectProperty<Font>(Font.getDefault());
	private ObjectProperty<Font> configButtonText = new SimpleObjectProperty<Font>(Font.getDefault());
	private ObjectProperty<Font> configDescriptionLabel = new SimpleObjectProperty<Font>(Font.getDefault());
	private ObjectProperty<Font> spinnerLabel = new SimpleObjectProperty<Font>(Font.getDefault());


	public PopUpCreator(Scene scene, StackPane root) {
		this.scene = scene;
		this.root = root;
		manageFontSizes();
	}
	/**
	 * @author Manuel Krakowski
	 * fits the size of all text that is displayed to teh size of the screen
	 */
	private void manageFontSizes() {
		root.widthProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth, Number newWidth) {
				popUpLabel.set(Font.font(newWidth.doubleValue() / 50));
				leaveButtonText.set(Font.font(newWidth.doubleValue() / 80));
				aiPowerText.set(Font.font(newWidth.doubleValue() / 50));
				leaveButtonText.set(Font.font(newWidth.doubleValue() / 80));
				aiConfigHeader.set(Font.font(newWidth.doubleValue() / 40));
				configButtonText.set(Font.font(newWidth.doubleValue() / 60));
				configDescriptionLabel.set(Font.font(newWidth.doubleValue() / 50));
				spinnerLabel.set(Font.font(newWidth.doubleValue() / 70));

			}
		});
	}
	
	
	/**
	 * @author Manuel Krakowski
	 * Creates a PopupPane in which the user can choose one of 4 different AIs
	 * 
	 * @param aiOrHuman: PopUpPane that is shown before this one, can be used to go back to it
	 * @param portText: text which should be disabled in case of the create game screen
	 * @param serverIPText: text which should be disabled in case of the create game screen
	 * @return PopUpPane that should be shown
	 */
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
		centerLeaveButton.getChildren().add(createBackButton());
		top.getChildren().add(centerLeaveButton);
		aiLevelPopUpPane.setContent(top);
		root.getChildren().add(aiLevelPopUpPane);
		return aiLevelPopUpPane;
	}
	
	
	/**
	 * @author Manuel Krakowski
	 * Creates a back button, which can be used to go back to the PopUpPane which was shown before the CHooseAi PopupPane
	 * @param text
	 */
	private Button createBackButton() {
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
	
	/**
	 * @author Manuel Krakowski
	 * Creates a button containing the name of the AI and an image 
	 * @param pow: Name of the Ai
	 * @param relSpacing
	 * @return
	 */
	private Button createAIPowerButton(String aiName, double relSpacing) {
		Button power = new Button(aiName);
		power.fontProperty().bind(aiPowerText);
		power.getStyleClass().add("ai-button-easy");
		Image mp = new Image(getClass().getResourceAsStream("i1.png"));
		ImageView vw = new ImageView(mp);
		power.setGraphic(vw);
		power.setContentDisplay(ContentDisplay.RIGHT);
		power.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * relSpacing;
			power.setGraphicTextGap(newSpacing);
		});
		String showAiInfo = Descriptions.describe(aiName);
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

	
	/**
	 * @author Manuel Krakowski
	 * Creates the pane in that a user can customize an AI with all its components
	 * @param widht relative with in relation to the scene 
	 * @param hight relative height in relation to the scene
	 * @return Popupane to custom  Ai
	 */
	public  PopUpPane createConfigPane(double widht, double hight) {
		defaultConfig = new AI_Config();
		createConfigMaps();
		aiconfig = new PopUpPane(scene, widht, hight);
		StackPane configRoot = new StackPane();
		configRoot.getChildren().add(createBackgroundImage(configRoot));
		configRoot.getStyleClass().add("join-root");
		VBox mainBox = createMainBox(configRoot);
		mainBox.getChildren().add(createHeader());
		HBox sep = createMiddleHBox();
		VBox leftBoss = createLeftVBox(sep);
		leftBoss.getChildren().add(createHeader(leftBoss, "Heuristic Multipliers"));
		VBox rightBoss = createRightVBox(sep);
		rightBoss.getChildren().add(createHeader(rightBoss, "Hyperparameters"));
		StackPane left = createContentStackPane(0.3,0.67);
		HBox topBox = createTopHbox(left);
		VBox leftColumn = createColumnVbox(topBox, 0.45);
		VBox rightColmn = createColumnVbox(topBox, 0.45);
		StackPane right = createContentStackPane(0.45,0.67);
		aiconfig.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newPadding = newValue.doubleValue() * 0.02;
			left.setPadding(new Insets(newPadding));
			right.setPadding(new Insets(newPadding));
		});
		HBox topBox2 = createTopHbox(right);
		VBox onlyColumn = createColumnVbox(topBox2, 0.9);
		fillColumns(leftColumn, rightColmn,onlyColumn);
		topBox.getChildren().addAll(leftColumn, rightColmn);
		topBox2.getChildren().add(onlyColumn);
		left.getChildren().add(topBox);
		right.getChildren().add(topBox2);
		leftBoss.getChildren().add(left);
		rightBoss.getChildren().add(right);
		sep.getChildren().add(leftBoss);
		sep.getChildren().add(rightBoss);
		mainBox.getChildren().add(sep);
		HBox buttomBox = createButtomHBox();
		buttomBox.getChildren().addAll(createConfigButton("Play and Save"), createConfigButton("Save"),createConfigButton("Back"));
		mainBox.getChildren().add(buttomBox);
		configRoot.getChildren().add(mainBox);
		aiconfig.setContent(configRoot);
		return aiconfig;
	}
	
	/**
	 * @author Manuel Krakowski
	 * Cretaes a transparent, resizbale Background Image for the whole scene
	 * @param configRoot Stackpane on that the background-image should be placed
	 * @return background-image
	 */
	private ImageView createBackgroundImage(StackPane configRoot) {
		Image mp = new Image(getClass().getResourceAsStream("tuning1.png"));
		ImageView mpv = new ImageView(mp);
		mpv.fitHeightProperty().bind(configRoot.heightProperty().divide(1.2));
		mpv.fitWidthProperty().bind(configRoot.widthProperty().divide(1.2));
		mpv.setPreserveRatio(true);
		mpv.setOpacity(0.2);
		return mpv;
	}
	
	/**@author Manuel Krakowski
	 * Box which contains all the components of the scene. Divided into 3 parts: Header on top, Boxes with all
	 *  Multipliers and hyperparams in the middle, Buttons to submit and leave in the bottom
	 * @param parent StackPane in that the box is placed
	 * @return
	 */
	private VBox createMainBox(StackPane parent) {
		VBox mainBox = new VBox();
		mainBox.prefHeightProperty().bind(parent.heightProperty());
		mainBox.prefWidthProperty().bind(parent.widthProperty());
		mainBox.setAlignment(Pos.TOP_CENTER);
		mainBox.setSpacing(30);
		mainBox.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.03;
			mainBox.setSpacing(newSpacing);
		});
		return mainBox;
	}
	
	/**
	 * @author Manuel Krakowski
	 * @return Creates the Header of the whole scene with the name AI-Generator
	 */
	private ImageView createHeader() {
		Image mp = new Image(getClass().getResourceAsStream("aiGen.png"));
		ImageView mpv = new ImageView(mp);
		mpv.fitWidthProperty().bind(aiconfig.widthProperty().multiply(0.6));
		mpv.setPreserveRatio(true);
		mpv.fitHeightProperty().bind(aiconfig.heightProperty().multiply(0.08));
		return mpv;
	}
	
	
	/**
	 * @author Manuel Krakowski
	 * @return Creates the Hbox which is the top COntainer of the middle part of the Screen, which will contain the two vboxes
	 */
	private HBox createMiddleHBox() {
		HBox sep = new HBox();
		sep.setAlignment(Pos.CENTER);
		sep.setSpacing(50);
		sep.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.05;
			sep.setSpacing(newSpacing);
		});
		return sep;
	}
	
	/**
	 * @author Manuel Krakowski
	 * Creates Left Side of the Screen in which the multipliers will be placed
	 * @param HBOX which contains the two VBoxes for multipliers(this one) and for hyperparams
	 * @return Box in that multipliers will be placed
	 */
	private VBox createLeftVBox(HBox parent){
		VBox leftBox = new VBox();
		//leftBox.setStyle("-fx-background-color: blue");
		leftBox.setAlignment(Pos.TOP_CENTER);
		leftBox.prefWidthProperty().bind(parent.widthProperty().multiply(0.55));
		leftBox.prefHeightProperty().bind(parent.heightProperty().multiply(0.68));
		return leftBox;
	}
	
	
	/**
	 * @author Manuel Krakowski
	 * Creates Right Side of the Screen in which the hyperparams will be placed
	 * @param HBOX which contains the two VBoxes for hyperparams(this one) and for mulitpliers
	 * @return Box in that multipliers will be placed
	 */
	private VBox createRightVBox(HBox parent){
		VBox rightBox = new VBox();
		//rightBox.setStyle("-fx-background-color: blue");
		rightBox.setAlignment(Pos.TOP_CENTER);
		rightBox.prefWidthProperty().bind(parent.widthProperty().multiply(0.35));
		rightBox.prefHeightProperty().bind(parent.heightProperty().multiply(0.68));
		return rightBox;
	}
	
	
	/**
	 * @author Manuel Krakowski
	 * Creates the header-Text which can be used for the multiplier and the hyperparm-box
	 * @param parent: VBox in which the header and the box with the content will be placed
	 * @param text: Text that the label should display
	 * @return
	 */
	private Label createHeader(VBox parent, String text) {
		Label l = new Label(text);
		l.getStyleClass().add("aiConfig-label");
		l.setAlignment(Pos.CENTER);
		l.fontProperty().bind(aiConfigHeader);
		l.prefWidthProperty().bind(parent.widthProperty().multiply(0.7));
		return l;
	}
	
	
	/**
	 * @author Manuel Krakowski
	 * @param relWidth with in Relation two the whole screen
	 * @param relHeight height in relation of the whole scene
	 * @return: Creates the box with border in which the parameters to modify the AI will be placed
	 */
	public StackPane createContentStackPane(double relWidth, double relHeight) {
		StackPane pane = new StackPane();
		pane.setAlignment(Pos.CENTER);
		pane.getStyleClass().add("option-pane");
		pane.setPrefSize(250, 250);
		pane.prefWidthProperty().bind(aiconfig.widthProperty().multiply(relWidth));
		pane.prefHeightProperty().bind(aiconfig.heightProperty().multiply(relHeight)); //maybe change aiconfig to parent here to make resizing more fluent
		return pane;
	}
	
	
	/**
	 * @author Manuel Krakowski
	 * @param Stackpane Box with border
	 * @return Creates a HBox which is espically imporant in the left Box two divide it into two Columns
	 */
	private HBox createTopHbox(StackPane parent) {
		HBox topBox = new HBox();
		topBox.setAlignment(Pos.CENTER);
		topBox.prefHeightProperty().bind(parent.heightProperty());
		topBox.prefWidthProperty().bind(parent.widthProperty());
		topBox.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.05;
			topBox.setSpacing(newSpacing);
		});
		return topBox;
	}
	
	
	/**
	 * @author Manuel Krakowski
	 * @param parent Box in which columns will be placed
	 * @param relWidth how much with of he Parent the column should fill relativly
	 * @return Column which will contain the content
	 */
	private VBox createColumnVbox(HBox parent, double relWidth) {
		VBox column = new VBox();
		column.prefWidthProperty().bind(parent.widthProperty().multiply(relWidth));
		column.prefHeightProperty().bind(parent.heightProperty());
		column.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.12;
			column.setSpacing(newSpacing);
		});
		return column; 
	}
	
	
	/**
	 * @author Manuel Krakowski
	 * Finally creates one row containg a short description label, info icon and spinner
	 * @param parent: Column in which the row should be placed
	 * @param text: Description Text for param
	 * @param min: min value for param
	 * @param max: max value for param
	 * @param current: default value for param
	 * @param isDouble: if param is double value is set, only one param is a double currently
	 * @return Hbox: one row
	 */
	private HBox createOneRowHBox(VBox parent, String text, int min, int max, int current, boolean isDouble) {
		HBox oneRow = new HBox();
		//oneRow.setStyle("-fx-background-color: yellow");
		oneRow.prefHeightProperty().bind(parent.heightProperty().divide(3));
		oneRow.prefWidthProperty().bind(parent.widthProperty());
		VBox divideRow = new VBox();
		divideRow.prefWidthProperty().bind(oneRow.widthProperty());
		divideRow.prefHeightProperty().bind(oneRow.heightProperty());
		HBox upperpart = createUpperPartOfRow(text, oneRow, divideRow);
		HBox lowerPart = new HBox();
		lowerPart.setAlignment(Pos.CENTER);
		lowerPart.prefHeightProperty().bind(divideRow.heightProperty().divide(2));
		if(isDouble) {
			Spinner<Double> spinner = createConfigSpinnerDouble(0, Double.MAX_VALUE, current, lowerPart);
			
			lowerPart.getChildren().add(spinner);
		}else {
			Spinner<Integer> spinner = createConfigSpinner(min, max, current, lowerPart);
			createIntegerSpinnerListener(spinner, text);
			lowerPart.getChildren().add(spinner);
		}
		divideRow.getChildren().addAll(upperpart,lowerPart);
		oneRow.getChildren().add(divideRow);
		return oneRow;
	}
	
	
	/**
	 * @author Manuel Krakowski
	 * creates the upper Part of the row containg a short descrition label and a info icon which shows information when hovering
	 * @param text: Text of the Label
	 * @param oneRow: Row in which it is placed
	 * @param divideRow: Vbox in which the whole row is placed
	 * @return HBox: upper part of the row
	 */
	private HBox createUpperPartOfRow(String text, HBox oneRow, VBox divideRow) {
		HBox upperpart = new HBox();
		upperpart.setAlignment(Pos.CENTER);
		upperpart.prefHeightProperty().bind(oneRow.heightProperty().divide(2));
		upperpart.prefWidthProperty().bind(divideRow.widthProperty().multiply(0.8));
		upperpart.setAlignment(Pos.CENTER);
		Label l = new Label(text);
		l.getStyleClass().add("spinner-des-label");
		l.fontProperty().bind(configDescriptionLabel);
		l.setAlignment(Pos.CENTER_LEFT);
		HBox upperLeft = new HBox();
		upperLeft.getChildren().add(l);
		upperLeft.prefWidthProperty().bind(upperpart.widthProperty().multiply(0.7));
		Image mp = new Image(getClass().getResourceAsStream("i1.png"));
		ImageView vw = new ImageView(mp);
		vw.fitHeightProperty().bind(upperpart.heightProperty().multiply(0.7));
		vw.fitWidthProperty().bind(upperpart.widthProperty().multiply(0.2));
		vw.setPreserveRatio(true);
		HBox upperRight = new HBox();
		upperRight.prefWidthProperty().bind(upperpart.widthProperty().multiply(0.1));
		upperRight.setAlignment(Pos.CENTER_RIGHT);
		upperRight.getChildren().add(vw);
		upperpart.getChildren().addAll(upperLeft, upperRight);
		return upperpart;
	}
	
	/**
	 * @author Manuel Krakowski
	 * creates an Integer-Spinner which can be used to modify a param
	 * @param min: min value of param
	 * @param max; max vallue of param
	 * @param cur: default value of param
	 * @param parent: Hbox in which Spinner will be placed
	 * @return integer-spinner for one param
	 */
	private Spinner<Integer> createConfigSpinner(int min, int max, int cur, HBox parent) {
	    this.values = new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, cur);
	    Spinner<Integer> spinner = new Spinner<>(values);
	    spinner.getStyleClass().add("spinner");
	    TextField spinnerText = spinner.getEditor();
	    spinnerText.fontProperty().bind(spinnerLabel);
	    spinner.setEditable(true);
	    spinner.prefWidthProperty().bind(parent.widthProperty().multiply(0.8));
	    spinner.prefHeightProperty().bind(parent.heightProperty().multiply(1));
	    return spinner;
	  }
	
	/**
	 * @author Manuel Krakowski
	 * creates an Double-Spinner which can be used to modify a param
	 * @param min: min value of param
	 * @param max; max vallue of param
	 * @param cur: default value of param
	 * @param parent: Hbox in which Spinner will be placed
	 * @return Double-spinner for one param
	 */
	private Spinner<Double> createConfigSpinnerDouble(double min, double max, double cur, HBox parent) {
	    this.values2 = new SpinnerValueFactory.DoubleSpinnerValueFactory(min, max, cur);
	    Spinner<Double> spinner = new Spinner<>(values2);
	    spinner.getStyleClass().add("spinner");
	    TextField spinnerText = spinner.getEditor();
	    spinnerText.fontProperty().bind(spinnerLabel);
	    spinner.setEditable(true);
	    spinner.prefWidthProperty().bind(parent.widthProperty().multiply(0.8));
	    spinner.prefHeightProperty().bind(parent.heightProperty().multiply(1));
	    return spinner;
	  }
	
	 private void createIntegerSpinnerListener(Spinner<Integer> spinner, String valueToModify) {
		    spinner.getValueFactory().valueProperty().addListener((obs, old, newValue) -> {
		    	changeConfigIntValue(valueToModify, newValue);
		    });
		  }
	
	private void changeConfigIntValue(String s, int newValue) {
		switch (s) {
		case "Attack-Power":
			defaultConfig.attackPowerMultiplier= newValue;
			System.out.println("Attack Power: " + newValue);
			break;
		case "Pieces":
			defaultConfig.pieceMultiplier = newValue;
			break;
		case "Base-Distance":
			defaultConfig.distanceBaseMultiplier = newValue;
			break;
		default:
			break;
		}
	}
	/**
	 * @author Manuel Krakowski
	 * Initializes a Map with the names and default values of all ai-multiplier values to fill the containers faster
	 */
	private void createConfigMaps() {
		multipliers.put("Attack-Power", defaultConfig.attackPowerMultiplier);
		multipliers.put("Pieces",defaultConfig.pieceMultiplier);
		multipliers.put("Base-Distance", defaultConfig.distanceBaseMultiplier);
		multipliers.put("Directions",defaultConfig.directionMultiplier);
		multipliers.put("Flags", defaultConfig.flagMultiplier);
		multipliers.put("Shape-Reach", defaultConfig.shapeReachMultiplier);
	}
	
	/**
	 * @author Manuel Krakowski
	 * Finaly creates all the Multipliers and hyperparams with their spinners and sets their names and default values
	 * @param multiplyerLeft: Left Column of the multiplier-Box
	 * @param multiplyerRight: Right Column of the multiplier-Box
	 * @param hyperparam: Only Column of the hyperparam-box
	 */
	private void fillColumns(VBox multiplyerLeft, VBox multiplyerRight, VBox hyperparam) {
		int i=0;
		for(String multilier: multipliers.keySet()) {
			if(i<3 ){
				HBox oneRow = createOneRowHBox(multiplyerLeft, multilier, 0, Integer.MAX_VALUE, multipliers.get(multilier),false);
				multiplyerLeft.getChildren().add(oneRow);
			} else {
				HBox oneRow = createOneRowHBox(multiplyerRight, multilier, 0, Integer.MAX_VALUE, multipliers.get(multilier),false);
				multiplyerRight.getChildren().add(oneRow);
			}
			i++;
		}
		HBox row1 = createOneRowHBox(hyperparam, "UCT-C", -1, -1,-1 , true);
		hyperparam.getChildren().add(row1);
		HBox row2 = createOneRowHBox(hyperparam, "Max-Steps", 0, Integer.MAX_VALUE, defaultConfig.MAX_STEPS, false);
		hyperparam.getChildren().add(row2);
		HBox row3 = createOneRowHBox(hyperparam, "Number of Threads", 1, Integer.MAX_VALUE, defaultConfig.numThreads, false);
		hyperparam.getChildren().add(row3);
	}
	
	
	
	
	
	/**
	 * @author Manuel Krakowski
	 * Creates the Hbox in the bottom of the screen which will contain 3 buttons
	 * @return Hbox
	 */
	private HBox createButtomHBox() {
		HBox buttonBox = new HBox();
		buttonBox.setAlignment(Pos.TOP_CENTER);
		buttonBox.setSpacing(50);
		aiconfig.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.05;
			buttonBox.setSpacing(newSpacing);
		});
		return buttonBox;
	}
	
	/**
	 * @author Manuel Krakowski
	 * creates a button to either save/play, play or leave with always the same design
	 * @param text
	 * @return
	 */
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
	
	
	
	
	
	
}
