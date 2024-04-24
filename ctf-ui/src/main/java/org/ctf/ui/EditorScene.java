package org.ctf.ui;

import java.io.File;
import java.io.IOException;

import org.ctf.shared.state.GameState;
import org.ctf.shared.state.data.map.PlacementType;
import org.ctf.shared.tools.JSON_Tools;
import org.ctf.shared.tools.JSON_Tools.IncompleteMapTemplateException;
import org.ctf.ui.controllers.MapPreview;
import org.ctf.ui.customobjects.PopUpPane;

import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import test.CreateTextGameStates;

public class EditorScene extends Scene {
	HomeSceneController hsc;
	StackPane root;
	Parent[] options;
	StackPane leftPane;
	StackPane visualRoot;
	SpinnerValueFactory<Integer> valueFactory;
	TemplateEngine engine;
	ComboBox<String> customFigureBox;
	MenuButton mapMenuButton;
	MenuButton mb;
	Text infoText;
	boolean spinnerchange = false;
	boolean boxchange = false;

	public EditorScene(HomeSceneController hsc, double width, double height) {
		super(new StackPane(), width, height);
		this.hsc = hsc;
		this.getStylesheets().add(getClass().getResource("MapEditor.css").toExternalForm());
		this.root = (StackPane) this.getRoot();
		engine = new TemplateEngine(this);
		options = new Parent[3];
		options[0] = createMapChooser();
		options[1] = createFigureChooser();
		options[2] = createFigureCustomizer();
		createLayout();

	}

	private void createLayout() {
		root.getStyleClass().add("join-root");

		VBox mainBox = new VBox();
		root.getChildren().add(mainBox);
		mainBox.getChildren().add(createHeader());
		mainBox.setAlignment(Pos.TOP_CENTER);
		mainBox.setSpacing(50);
		HBox sep = new HBox();
		sep.setAlignment(Pos.CENTER);
		sep.setSpacing(50);
		sep.setPadding(new Insets(50));
		VBox leftControl = new VBox();
		leftControl.setAlignment(Pos.CENTER);
		leftControl.setSpacing(10);
		leftControl.getChildren().add(createControlBar());
		leftPane = createLeftPane();
		leftPane.getChildren().add(options[0]);
		leftControl.getChildren().add(leftPane);
		createInfotext();
		StackPane textPane = new StackPane();
		textPane.getChildren().add(infoText);
		leftControl.getChildren().add(textPane);
		sep.getChildren().add(leftControl);
		createVisual();
		sep.getChildren().add(visualRoot);
		mainBox.getChildren().add(sep);
		
		
	}

	private ImageView createHeader() {
		Image mp = new Image(getClass().getResourceAsStream("EditorImage.png"));
		ImageView mpv = new ImageView(mp);
		mpv.fitWidthProperty().bind(root.widthProperty().multiply(0.8));
		mpv.setPreserveRatio(true);
		root.widthProperty().addListener(e -> {
			if (root.getWidth() > 1000) {
				mpv.fitWidthProperty().unbind();
				mpv.setFitWidth(800);
			} else if (root.getWidth() <= 1000) {
				mpv.fitWidthProperty().unbind();
				mpv.fitWidthProperty().bind(root.widthProperty().multiply(0.8));
			}
		});
		return mpv;
	}
	
	private void createInfotext() {
		infoText = new Text("");
		infoText.getStyleClass().add("custom-info-label");
		leftPane.widthProperty().addListener((obs,oldVal,newVal) -> {
			double size = newVal.doubleValue()*0.035;
			infoText.setFont(Font.font("Century Gothic",size));
		});		
	}

	public void inform(String info) {
		infoText.setText(info);
		FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), infoText);
		fadeTransition.setDelay(Duration.seconds(1));
		fadeTransition.setFromValue(1.0);
		fadeTransition.setToValue(0.0);
		fadeTransition.setOnFinished(event -> {
			infoText.setText("");
			infoText.setOpacity(1);
		});
		fadeTransition.play();
	}
	
	private VBox createMapChooser() {
		VBox mapRoot = new VBox();
		mapRoot.setSpacing(10);
		mapRoot.setPadding(new Insets(10));
		mapRoot.setAlignment(Pos.TOP_CENTER);
		mapRoot.getChildren().add(createHeaderText(mapRoot, "Edit Map", 18));
		GridPane controlgrid = new GridPane();
		mapRoot.widthProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.05;
			controlgrid.setHgap(spacing);
		});
		mapRoot.heightProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.05;
			controlgrid.setVgap(spacing);
		});
		controlgrid.add(createText(mapRoot, "Rows", 30), 0, 0);
		controlgrid.add(createText(mapRoot, "Collums", 30), 0, 1);
		controlgrid.add(createText(mapRoot, "Teams", 30), 0, 2);
		controlgrid.add(createText(mapRoot, "Flags", 30), 0, 3);
		controlgrid.add(createText(mapRoot, "Blocks", 30), 2, 0);
		controlgrid.add(createText(mapRoot, "Turn Time", 30), 2, 1);
		controlgrid.add(createText(mapRoot, "Game Time", 30), 2, 2);
		controlgrid.add(createText(mapRoot, "Placement", 30), 2, 3);
		Spinner<Integer> rowsSpinner = createMapSpinner(mapRoot, 1, 100, engine.tmpTemplate.getGridSize()[0]);
		createChangeListener(rowsSpinner, "Rows", false);
		controlgrid.add(rowsSpinner, 1, 0);
		Spinner<Integer> colSpinner = createMapSpinner(mapRoot, 1, 100, engine.tmpTemplate.getGridSize()[1]);
		createChangeListener(colSpinner, "Cols", false);
		controlgrid.add(colSpinner, 1, 1);
		Spinner<Integer> teamSpinner = createMapSpinner(mapRoot, 2, 50, engine.tmpTemplate.getTeams());
		createChangeListener(teamSpinner, "Teams", false);
		controlgrid.add(teamSpinner, 1, 2);
		Spinner<Integer> flagSpinner = createMapSpinner(mapRoot, 1, 100, engine.tmpTemplate.getTeams());
		createChangeListener(flagSpinner, "Flags", false);
		controlgrid.add(flagSpinner, 1, 3);
		Spinner<Integer> blockSpinner = createMapSpinner(mapRoot, 0, 500, engine.tmpTemplate.getBlocks());
		createChangeListener(blockSpinner, "Blocks", false);
		controlgrid.add(blockSpinner, 3, 0);
		Spinner<Integer> turnTimeSpinner = createMapSpinner(mapRoot, -1, 600,
				engine.tmpTemplate.getMoveTimeLimitInSeconds());
		createChangeListener(turnTimeSpinner, "TurnTime", false);
		controlgrid.add(turnTimeSpinner, 3, 1);
		int init = (engine.tmpTemplate.getTotalTimeLimitInSeconds() == -1) ? -1
				: engine.tmpTemplate.getTotalTimeLimitInSeconds() / 60;
		Spinner<Integer> gameTimeSpinner = createMapSpinner(mapRoot, -1, 600, init);
		createChangeListener(gameTimeSpinner, "GameTime", false);
		controlgrid.add(gameTimeSpinner, 3, 2);
		controlgrid.add(createPlacementBox(mapRoot), 3, 3);

		mapRoot.getChildren().add(controlgrid);
		return mapRoot;
	}

	private VBox createFigureChooser() {
		VBox pieceRoot = new VBox();
		pieceRoot.setSpacing(10);
		pieceRoot.setPadding(new Insets(20));
		pieceRoot.setAlignment(Pos.TOP_CENTER);
		pieceRoot.getChildren().add(createHeaderText(pieceRoot, "Add Pieces", 18));
		GridPane controlgrid = new GridPane();
		pieceRoot.widthProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.05;
			controlgrid.setHgap(spacing);
		});
		pieceRoot.heightProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.05;
			controlgrid.setVgap(spacing);
		});
		controlgrid.add(createText(pieceRoot, "Pawn", 30), 0, 0);
		controlgrid.add(createText(pieceRoot, "Knight", 30), 0, 1);
		controlgrid.add(createText(pieceRoot, "Bishop", 30), 0, 2);
		controlgrid.add(createText(pieceRoot, "Rook", 30), 2, 0);
		controlgrid.add(createText(pieceRoot, "Queen", 30), 2, 1);
		controlgrid.add(createText(pieceRoot, "King", 30), 2, 2);
		Spinner<Integer> pawnSpinner = createMapSpinner(pieceRoot, 0, 500, engine.getPieceCount("Pawn"));
		createChangeListener(pawnSpinner, "Pawn", false);
		controlgrid.add(pawnSpinner, 1, 0);
		Spinner<Integer> knightSpinner = createMapSpinner(pieceRoot, 0, 500, engine.getPieceCount("Knight"));
		createChangeListener(knightSpinner, "Knight", false);
		controlgrid.add(knightSpinner, 1, 1);
		Spinner<Integer> bishopSpinner = createMapSpinner(pieceRoot, 0, 500, engine.getPieceCount("Bishop"));
		createChangeListener(bishopSpinner, "Bishop", false);
		controlgrid.add(bishopSpinner, 1, 2);
		Spinner<Integer> rookSpinner = createMapSpinner(pieceRoot, 0, 500, engine.getPieceCount("Rook"));
		createChangeListener(rookSpinner, "Rook", false);
		controlgrid.add(rookSpinner, 3, 0);
		Spinner<Integer> queenSpinner = createMapSpinner(pieceRoot, 0, 500, engine.getPieceCount("Queen"));
		createChangeListener(queenSpinner, "Queen", false);
		controlgrid.add(queenSpinner, 3, 1);
		Spinner<Integer> kingSpinner = createMapSpinner(pieceRoot, 0, 500, engine.getPieceCount("King"));
		createChangeListener(kingSpinner, "King", false);
		controlgrid.add(kingSpinner, 3, 2);
		pieceRoot.getChildren().add(controlgrid);

		pieceRoot.getChildren().add(createHeaderText(pieceRoot, "Custom Figures", 18));
		pieceRoot.getChildren().add(createFigureBar(pieceRoot));
		return pieceRoot;
	}

	private VBox createFigureCustomizer() {
		VBox customRoot = new VBox();
		customRoot.setSpacing(10);
		customRoot.setPadding(new Insets(20));
		customRoot.setAlignment(Pos.TOP_CENTER);
		GridPane controlgrid = new GridPane();
		customRoot.widthProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.05;
			controlgrid.setHgap(spacing);
		});
		customRoot.heightProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.05;
			controlgrid.setVgap(spacing);
		});
		customRoot.getChildren().add(createHeaderText(customRoot, "Configure your own Piece", 15));
		controlgrid.add(createText(customRoot, "Name", 30), 0, 0);
		controlgrid.add(createText(customRoot, "Shape", 30), 0, 1);
		controlgrid.add(createText(customRoot, "Strength", 30), 2, 0);
		controlgrid.add(createText(customRoot, "Directions", 30), 2, 1);
		controlgrid.add(createText(customRoot, "Value", 30), 2, 2);
		TextField namefield = (createNameField(customRoot));
		controlgrid.add(namefield, 1, 0);
		controlgrid.add(createShapeBox(customRoot), 1, 1);
		Spinner<Integer> strenghthSpinner = createMapSpinner(customRoot, 0, 500, 0);
		controlgrid.add(strenghthSpinner, 3, 0);

		Spinner<Integer> valueSpinner = createMapSpinner(customRoot, 0, 500, 0);

		ComboBox<String> directionsBox = createDirectionsBox(customRoot, valueSpinner);
		valueSpinner.getValueFactory().valueProperty().addListener((obs, old, newValue) -> {
			engine.handleDirectionValue(directionsBox,newValue);
		});
		controlgrid.add(directionsBox, 3, 1);
		controlgrid.add(valueSpinner, 3, 2);
		controlgrid.add(createAddButton(customRoot, namefield, strenghthSpinner), 1, 2);
		customRoot.getChildren().add(controlgrid);

		return customRoot;
	}

	private StackPane createLeftPane() {
		StackPane pane = new StackPane();
		pane.getStyleClass().add("option-pane");
		pane.setPadding(new Insets(10));
		pane.prefWidthProperty().bind(this.widthProperty().multiply(0.4));
		pane.prefHeightProperty().bind(pane.widthProperty().multiply(0.5));
		
		return pane;
	}

	private HBox createControlBar() {
		HBox controlBar = new HBox();
		controlBar.setSpacing(10);
		controlBar.getChildren().add(createMenuButton());
		mapMenuButton = createMapMenuButton();
		controlBar.getChildren().add(mapMenuButton);
		controlBar.getChildren().add(createExit());
		controlBar.getChildren().add(createSubmit());
		return controlBar;
	}

	private Button createControlButton(String label) {
		Button but = new Button(label);
		but.getStyleClass().add("leave-button");
		but.prefWidthProperty().bind(root.widthProperty().multiply(0.1));
		but.prefHeightProperty().bind(but.widthProperty().multiply(0.25));
		return but;
	}

	private Button createExit() {
		Button exit = createControlButton("Leave");
		exit.setOnAction(e -> {
			hsc.switchtoHomeScreen(e);
		});
		return exit;
	}

	private Button createSubmit() {
		Button submit = createControlButton("Submit");
		submit.setOnAction(e -> {
			engine.printTemplate();
			root.getChildren().add(new PopUpPane(this, 0.4, 0.4));
		});
		return submit;
	}

	private MenuButton createMenuButton() {
		mb = new MenuButton("Edit Map");
		mb.getStyleClass().add("custom-menu-button");
		mb.prefWidthProperty().bind(root.widthProperty().multiply(0.1));
		mb.prefHeightProperty().bind(mb.widthProperty().multiply(0.25));
		MenuItem mapMenuItem = new MenuItem("Edit Map");
		MenuItem figureMenuItem = new MenuItem("Add Pieces");
		MenuItem configMenuItem = new MenuItem("Costum Pieces");
		mb.getItems().addAll(mapMenuItem, figureMenuItem, configMenuItem);
		mapMenuItem.setOnAction(event -> {
			leftPane.getChildren().clear();
			leftPane.getChildren().add(options[0]);
			mb.setText("Edit Map");

		});
		figureMenuItem.setOnAction(event -> {
			leftPane.getChildren().clear();
			leftPane.getChildren().add(options[1]);
			mb.setText("Add Pieces");
		});
		configMenuItem.setOnAction(event -> {
			leftPane.getChildren().clear();
			leftPane.getChildren().add(options[2]);
			mb.setText("Costum Pieces");
		});
		return mb;
	}

	private MenuButton createMapMenuButton() {
		MenuButton mb = new MenuButton("Load Map");
		mb.getStyleClass().add("custom-menu-button");
		mb.prefWidthProperty().bind(root.widthProperty().multiply(0.1));
		mb.prefHeightProperty().bind(mb.widthProperty().multiply(0.25));
		for(String mapName : engine.getTemplateNames()) {
			addMapItem(mapName, mb);
		}		
		return mb;
	}
	private void addMapItem(String mapName,MenuButton mapMenuButton) {
		MenuItem item = new MenuItem(mapName);
		item.setOnAction(e -> {
			engine.loadTemplate(mapName);
			engine.initializePieces();
			options[0] = createMapChooser();
			options[1] = createFigureChooser();
			leftPane.getChildren().clear();
			leftPane.getChildren().add(options[0]);
			mb.setText("Edit Map");			
			updateVisualRoot();
			inform(mapName + "was loaded.");
		});
		mapMenuButton.getItems().add(item);
	}

	private Text createHeaderText(VBox vBox, String label, int divider) {
		Text leftheader = new Text(label);
		leftheader.getStyleClass().add("custom-header");
		leftheader.fontProperty().bind(Bindings.createObjectBinding(
				() -> Font.font("Century Gothic", vBox.getWidth() / divider), vBox.widthProperty()));
		return leftheader;
	}

	private Text createText(VBox vBox, String label, int divider) {
		Text text = new Text(label);
		text.getStyleClass().add("custom-info-label");
		text.fontProperty().bind(Bindings.createObjectBinding(
				() -> Font.font("Century Gothic", vBox.getWidth() / divider), vBox.widthProperty()));
		return text;
	}

	private Spinner<Integer> createMapSpinner(VBox parent, int min, int max, int cur) {
		this.valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, cur);
		Spinner<Integer> spinner = new Spinner<>(valueFactory);
		spinner.getStyleClass().add("spinner");
		spinner.setEditable(true);
		// spinner.setStyle("-fx-background-color: rgb(15,15,15);");
		spinner.prefWidthProperty().bind(this.widthProperty().multiply(0.1));
		spinner.prefHeightProperty().bind(spinner.widthProperty().multiply(0.25));
		return spinner;

	}

	private ComboBox<String> createPlacementBox(VBox vBox) {
		ObservableList<String> options = FXCollections.observableArrayList("Symmetric", "Spaced Out", "Defensive");
		ComboBox<String> placementBox = new ComboBox<>(options);
		switch (engine.tmpTemplate.getPlacement()) {
		case symmetrical:
			placementBox.setValue("Symmetric");
			break;
		case spaced_out:
			placementBox.setValue("Spaced Out");
			break;
		case defensive:
			placementBox.setValue("Defenisve");
			;
			break;
		default:
		}
		placementBox.setOnAction(e -> {
			engine.setPlacement(placementBox.getValue());
			updateVisualRoot();
		});
		placementBox.getStyleClass().add("custom-combo-box-2");
		placementBox.prefWidthProperty().bind(vBox.widthProperty().multiply(0.25));
		placementBox.prefHeightProperty().bind(placementBox.widthProperty().multiply(0.25));
		return placementBox;
	}

	private ComboBox<String> createFigureBox(VBox vBox) {

		customFigureBox = new ComboBox<>();
		engine.fillCustomBox(customFigureBox);
		// placementBox.setValue(options.get(0));
		customFigureBox.getStyleClass().add("custom-combo-box-2");
		customFigureBox.prefWidthProperty().bind(vBox.widthProperty().multiply(0.4));
		customFigureBox.prefHeightProperty().bind(customFigureBox.widthProperty().multiply(0.6));
		return customFigureBox;
	}

	private ComboBox<String> createShapeBox(VBox vBox) {
		ObservableList<String> options = FXCollections.observableArrayList("None", "L-Shape");
		ComboBox<String> shapeBox = new ComboBox<>(options);
		shapeBox.setValue(options.get(0));
		shapeBox.getStyleClass().add("custom-combo-box-2");
		shapeBox.prefWidthProperty().bind(vBox.widthProperty().multiply(0.25));
		shapeBox.prefHeightProperty().bind(shapeBox.widthProperty().multiply(0.25));
		return shapeBox;
	}

	private ComboBox<String> createDirectionsBox(VBox vBox, Spinner<Integer> vaSpinner) {
		ObservableList<String> options = FXCollections.observableArrayList("Left", "Right", "Up", "Down", "Up-Left",
				"Up-Right", "Down-Left", "Down-Right");
		ComboBox<String> directionBox = new ComboBox<>(options);
		directionBox.setOnAction(e -> {
			engine.handleDirection(directionBox.getValue(), vaSpinner);
		});
		directionBox.setValue(options.get(0));
		directionBox.getStyleClass().add("custom-combo-box-2");
		directionBox.prefWidthProperty().bind(vBox.widthProperty().multiply(0.25));
		directionBox.prefHeightProperty().bind(directionBox.widthProperty().multiply(0.25));
		return directionBox;
	}

	private HBox createFigureBar(VBox vBox) {
		HBox chooseBar = new HBox();
		chooseBar.setAlignment(Pos.CENTER);
		vBox.widthProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.06;
			chooseBar.setSpacing(spacing);
		});
		chooseBar.getChildren().add(createFigureBox(vBox));
		Spinner<Integer> customSpinner = createMapSpinner(vBox, 0, 100, 0);
		chooseBar.getChildren().add(customSpinner);
		customFigureBox.setValue("Choose Custom Piece");
		customFigureBox.setOnAction(e -> {
			boxchange = true;
			int customcount = engine.getPieceCount(customFigureBox.getValue());
			if(customSpinner.getValue()==customcount) {
				boxchange = false;
			}
			customSpinner.getValueFactory().setValue(customcount);
		});
		createChangeListener(customSpinner, "custom", true);

		return chooseBar;
	}

	private TextField createNameField(VBox vbox) {
		TextField textField = new TextField();
		textField.getStyleClass().add("custom-search-field");
		textField.prefWidthProperty().bind(vbox.widthProperty().multiply(0.25));
		textField.prefHeightProperty().bind(textField.widthProperty().multiply(0.1));
		textField.heightProperty().addListener((obs, oldVal, newVal) -> {
			double newFontSize = newVal.doubleValue() * 0.4;
			textField.setFont(new Font(newFontSize));
		});
		 textField.textProperty().addListener((observable, oldValue, newValue) -> {
	            if (newValue.length() > 15) {
	                textField.setText(oldValue); // Wenn die Länge überschritten wird, wird der Text auf den vorherigen Wert zurückgesetzt
	            }
	        });
		return textField;
	}

	private Button createAddButton(VBox vbox, TextField name, Spinner<Integer> strength) {
		Button addButton = new Button("Add");
		addButton.getStyleClass().add("join-button");
		addButton.prefWidthProperty().bind(vbox.widthProperty().multiply(0.25));
		addButton.prefHeightProperty().bind(addButton.widthProperty().multiply(0.25));
//		addButton.fontProperty().bind(Bindings.createObjectBinding(
//				() -> Font.font("Century Gothic", addButton.getHeight() * 0.35), addButton.heightProperty()));
		addButton.setOnAction(e -> {
			engine.addpiece(name, strength);
		});
		return addButton;
	}

	private void createVisual() {
		visualRoot = new StackPane();
		visualRoot.getStyleClass().add("option-pane");
		visualRoot.setPadding(new Insets(10));
		visualRoot.prefWidthProperty().bind(root.widthProperty().multiply(0.45));
		visualRoot.prefHeightProperty().bind(root.heightProperty().multiply(0.75));
		// GamePane visual = new GamePane(CreateTextGameStates.createTestGameState1());
		updateVisualRoot();
		// visualRoot.getChildren().add(visual);

	}

	private void createChangeListener(Spinner<Integer> spinner, String event, boolean custom) {
		
		spinner.getValueFactory().valueProperty().addListener((obs, old, newValue) -> {
			System.out.println("Change!!");
			if (spinnerchange) {
				spinnerchange = false;
				return;
			}
			if (custom && boxchange) {
				System.out.println("boxchange!");
				boxchange = false;
				return;
			}
			if (engine.handleSpinnerEvent(event, spinner, old, newValue)) {
				spinner.setDisable(true);
				updateVisualRoot();
				spinner.setDisable(false);
			}
			;

		});
	}

	public void setSpinnerChange(boolean value) {
		this.spinnerchange = value;
	}

	private void updateVisualRoot() {
		MapPreview mp = new MapPreview(engine.tmpTemplate);
		visualRoot.getChildren().clear();
		visualRoot.getChildren().add(new GamePane(mp.getGameState()));

	}

	public ComboBox<String> getCustomFigureBox() {
		return customFigureBox;
	}

}
