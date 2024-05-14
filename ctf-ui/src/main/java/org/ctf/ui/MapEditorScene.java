package org.ctf.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.ctf.shared.constants.Constants;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.data.map.Directions;
import org.ctf.shared.state.data.map.MapTemplate;
import org.ctf.shared.state.data.map.Movement;
import org.ctf.shared.state.data.map.PieceDescription;
import org.ctf.shared.state.data.map.PlacementType;
import org.ctf.shared.tools.JsonTools;
import org.ctf.shared.tools.JsonTools.IncompleteMapTemplateException;
import org.ctf.ui.controllers.MapPreview;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import test.CreateTextGameStates;

/**
 * Scene that contains all gui components for the Map editor and handles custom
 * map templtates and pieces
 * 
 * @author aniemesc
 */
public class MapEditorScene extends Scene {
	private String[][] feld;
	private Parent[] options;
	private StackPane left;
	private MapTemplate tmpTemplate;
	private MapPreview mp;
	private PieceDescription tmpdescription = new PieceDescription();
	private Movement tmpMovement = new Movement();
	private ComboBox<String> pieceComboBox;
	private HashMap<String, PieceDescription> customPieces = new HashMap<String, PieceDescription>();
	private HomeSceneController hsc;
	private boolean spinnerchange = false;
	private boolean boxchange = false;
	private Text text = new Text("");
	private GamePane visual;
	private StackPane test;

	public MapEditorScene(HomeSceneController hsc) {
		super(new VBox(), 1000, 500);
		this.hsc = hsc;
		this.getStylesheets().add(getClass().getResource("MapEditor.css").toExternalForm());
//		File defaultMap = new File("src" + File.separator + "main" + File.separator + "java" + File.separator + "org"
//				+ File.separator + "ctf" + File.separator + "ui" + File.separator + "default.json");
		File defaultMap = new File(Constants.mapTemplateFolder+"10x10_2teams_example.json");
		try {
			if (defaultMap.exists()) {
				tmpTemplate = JsonTools.readMapTemplate(defaultMap);
				
			} else {
				tmpTemplate = createExampleTemplate();
				System.out.println("Fehler: Default Template konnte nicht geladen werden!"
						+ "Es wurde ein alternatives Template geladen.");
			}

		} catch (IncompleteMapTemplateException | IOException e) {
			System.out.println("fail");
		}

		initializeTemplate();
		initializePieces();
		//printTemplate();
		options = new Parent[3];
		options[0] = createMapChooser();
		options[1] = createFigurChooser();
		options[2] = createFigureCustomizer();
		this.createLayout();
	}

	/**
	 * Creates the basic layout for the scene
	 * 
	 * @author aniemesc
	 */
	private void createLayout() {
		VBox root = (VBox) this.getRoot();
		root.setStyle("-fx-background-color: black;" + "-fx-padding: 25px;" + "-fx-spacing: 50px;"
				+ "-fx-alignment: top-center ;");
		Image meI = new Image(getClass().getResourceAsStream("EditorImage.png"));
		ImageView meIv = new ImageView(meI);
		meIv.setPreserveRatio(true);
		root.getChildren().add(meIv);
		HBox sep = new HBox();
		sep.setAlignment(Pos.CENTER);
		sep.setStyle("-fx-spacing: 25px;");
		root.getChildren().add(sep);
		left = new StackPane();
		left.setStyle("-fx-border-color: rgba(255,255,255,1); -fx-border-width: 2px;" + "-fx-background-color: rgb(25,25,25);"
				+ "-fx-background-radius: 20px; -fx-border-radius: 20px;" + "-fx-alignment: top-center;");
		left.getChildren().add(options[0]);

		VBox leftroot = new VBox();
		MenuButton mb = createMenuButton();
		MenuButton mmb = createMapMenuButton();
		HBox menuBox = new HBox();
		menuBox.setAlignment(Pos.CENTER);
		menuBox.setStyle("-fx-spacing: 20px;");
		menuBox.getChildren().add(mb);
		menuBox.getChildren().add(mmb);
		menuBox.getChildren().add(createSubmit());
		menuBox.getChildren().add(createExit());
		// mb.setStyle("-fx-border-color: transparent;");
		// leftroot.getChildren().add(mb);
		// leftroot.getChildren().add(test);
		leftroot.getChildren().add(menuBox);
		leftroot.getChildren().add(left);
		leftroot.setStyle("-fx-spacing: 20px;");
		leftroot.setAlignment(Pos.CENTER);

		leftroot.getChildren().add(text);
		text.setStyle("-fx-fill: rgba(255, 255, 255, 1);");

		sep.getChildren().add(leftroot);
		//sep.getChildren().add(CreateMapGrid());
		test = new StackPane();
		test.setStyle("-fx-border-color: rgba(255,255,255,1); -fx-border-width: 2px;" + "-fx-background-color: 	rgb(25,25,25);"
				+ "-fx-background-radius: 20px; -fx-border-radius: 20px;" + "-fx-alignment: top-center;");
		
		
		//GamePane visual = new GamePane(CreateTextGameStates.createTestGameState1());
		mp = new MapPreview(tmpTemplate);
//		GameState gamestate = mp.getGameState();
//		visual = new GamePane(mp.getGameState());
		
		test.getChildren().add(visual);
		test.prefWidthProperty().bind(this.widthProperty().multiply(0.4));
		sep.getChildren().add(test);


	}

	/**
	 * Creates and fills the container for the 'Edit Map' option
	 * 
	 * @author aniemesc
	 * @return VBox editMap
	 */
	private VBox createMapChooser() {
		VBox controlBox = new VBox();
		controlBox.setAlignment(Pos.CENTER);
		controlBox.setStyle("-fx-spacing: 25px; -fx-padding: 25px;");

		GridPane controlgrid = new GridPane();
		controlgrid.setHgap(25);
		controlgrid.setVgap(20);
		controlBox.getChildren().add(controlgrid);

		Label rowlabel = new Label("Rows");
		rowlabel.getStyleClass().add("custom-label");
		controlgrid.add(rowlabel, 0, 0);
		SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100,
				tmpTemplate.getGridSize()[0]);
		Spinner<Integer> spinner = new Spinner<>(valueFactory);
		spinner.getValueFactory().valueProperty().addListener((obs, old, newValue) -> {
			if (spinnerchange) {
				spinnerchange = false;
				return;
			}
			tmpTemplate.getGridSize()[0] = newValue;
			if (!TemplateChecker.checkTemplate(tmpTemplate)) {
				tmpTemplate.getGridSize()[0] = old;
				spinnerchange = true;
				spinner.getValueFactory().setValue(old);
				System.out.println("Zu wenig Platz");
			}
		});

		controlgrid.add(spinner, 1, 0);

		Label collabel = new Label("Collums");
		collabel.getStyleClass().add("custom-label");
		controlgrid.add(collabel, 0, 1);
		valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, tmpTemplate.getGridSize()[1]);
		Spinner<Integer> spinner2 = new Spinner<>(valueFactory);
		spinner2.getValueFactory().valueProperty().addListener((obs, old, newValue) -> {
			if (spinnerchange) {
				spinnerchange = false;
				return;
			}
			tmpTemplate.getGridSize()[1] = newValue;
			if (!TemplateChecker.checkTemplate(tmpTemplate)) {
				tmpTemplate.getGridSize()[1] = old;
				spinnerchange = true;
				spinner2.getValueFactory().setValue(old);
				System.out.println("Zu wenig Platz");
			}
		});
		controlgrid.add(spinner2, 1, 1);

		Label teamslabel = new Label("Teams");
		teamslabel.getStyleClass().add("custom-label");
		controlgrid.add(teamslabel, 0, 2);
		valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, tmpTemplate.getTeams());
		Spinner<Integer> spinner3 = new Spinner<>(valueFactory);
		spinner3.getValueFactory().valueProperty().addListener((obs, old, newValue) -> {
			if (spinnerchange) {
				spinnerchange = false;
				return;
			}
			tmpTemplate.setTeams(newValue);
			if (!TemplateChecker.checkTemplate(tmpTemplate)) {
				tmpTemplate.setTeams(old);
				spinnerchange = true;
				spinner3.getValueFactory().setValue(old);
				System.out.println("Zu wenig Platz für " + newValue + " Teams");
			}
		});

		controlgrid.add(spinner3, 1, 2);

		Label flagslabel = new Label("Flags");
		flagslabel.getStyleClass().add("custom-label");
		controlgrid.add(flagslabel, 0, 3);
		valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, tmpTemplate.getFlags());
		Spinner<Integer> spinner4 = new Spinner<>(valueFactory);
		spinner4.getValueFactory().valueProperty().addListener((obs, old, newValue) -> {
			tmpTemplate.setFlags(newValue);
		});
		controlgrid.add(spinner4, 1, 3);

		Label blockslabel = new Label("Blocks");
		blockslabel.getStyleClass().add("custom-label");
		controlgrid.add(blockslabel, 2, 0);
		valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, tmpTemplate.getBlocks());
		Spinner<Integer> spinner5 = new Spinner<>(valueFactory);
		spinner5.getValueFactory().valueProperty().addListener((obs, old, newValue) -> {
			if (spinnerchange) {
				spinnerchange = false;
				return;
			}
			tmpTemplate.setBlocks(newValue);
			if (!TemplateChecker.checkTemplate(tmpTemplate)) {
				tmpTemplate.setBlocks(old);
				spinnerchange = true;
				spinner5.getValueFactory().setValue(old);
				System.out.println("Zu wenig Platz für " + newValue + " Blocks");
			}
		});

		controlgrid.add(spinner5, 3, 0);

		Label time1label = new Label("Thinking Time \n in seconds");
		time1label.getStyleClass().add("custom-label");
		controlgrid.add(time1label, 2, 1);
		valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 500,
				tmpTemplate.getMoveTimeLimitInSeconds());
		Spinner<Integer> spinner6 = new Spinner<>(valueFactory);
		spinner6.getValueFactory().valueProperty().addListener((obs, old, newValue) -> {
			tmpTemplate.setMoveTimeLimitInSeconds(newValue);
		});
		controlgrid.add(spinner6, 3, 1);

		Label time2label = new Label("Gametime \n in Minutes");
		time2label.getStyleClass().add("custom-label");
		controlgrid.add(time2label, 2, 2);
		valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 500,
				tmpTemplate.getTotalTimeLimitInSeconds() / 60);
		Spinner<Integer> spinner7 = new Spinner<>(valueFactory);
		spinner7.getValueFactory().valueProperty().addListener((obs, old, newValue) -> {
			tmpTemplate.setTotalTimeLimitInSeconds(newValue * 60);
		});
		controlgrid.add(spinner7, 3, 2);

		Label placementlabel = new Label("Placement");
		placementlabel.getStyleClass().add("custom-label");
		controlgrid.add(placementlabel, 2, 3);
		ObservableList<String> options = FXCollections.observableArrayList("Symmetric", "Spaced Out", "Defensive");
		ComboBox<String> placementBox = new ComboBox<>(options);
		placementBox.prefWidthProperty().bind(spinner7.widthProperty());
		switch (tmpTemplate.getPlacement()) {
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
		controlgrid.add(placementBox, 3, 3);
		placementBox.setOnAction(e -> {
			switch (placementBox.getValue()) {
			case "Symmetrical":
				tmpTemplate.setPlacement(PlacementType.symmetrical);
				break;
			case "Spaced Out":
				tmpTemplate.setPlacement(PlacementType.spaced_out);
				break;
			case "Defensive":
				tmpTemplate.setPlacement(PlacementType.defensive);
				break;
			default:
			}
		});
		return controlBox;
	}

	/**
	 * Creates and fills the container for the 'Add Pieces' option
	 * 
	 * @author aniemesc
	 * @return HBox addPieces
	 */
	private HBox createFigurChooser() {
		HBox controlBox = new HBox();
		controlBox.setAlignment(Pos.CENTER);
		VBox names1 = new VBox();
		names1.setStyle("-fx-spacing: 20px; -fx-padding: 10px;");

		Label t1 = new Label("Pawn");
		t1.getStyleClass().add("custom-label");
		names1.getChildren().add(t1);
		Label t2 = new Label("Knight");
		t2.getStyleClass().add("custom-label");
		names1.getChildren().add(t2);
		Label dameL = new Label("Queen");
		dameL.getStyleClass().add("custom-label");
		names1.getChildren().add(dameL);

		VBox choose1 = new VBox();
		choose1.setStyle("-fx-spacing: 15px; -fx-padding: 10px;");
		SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100,
				initialValue("Pawn"));
		Spinner<Integer> bauerSpinner = new Spinner<>(valueFactory);
		bauerSpinner.setPrefWidth(100);
		bauerSpinner.getValueFactory().valueProperty().addListener((obs, old, newValue) -> {
			if (spinnerchange) {
				spinnerchange = false;
				return;
			}
			if (newValue == 0) {
				if (tmpTemplate.getPieces().length == 1) {
					spinnerchange = true;
					bauerSpinner.getValueFactory().setValue(old);
					System.out.println("Mindestens eine Figur noetig");
				}
			}
			betterUpdateCount("Pawn", newValue);
			if (!TemplateChecker.checkTemplate(tmpTemplate)) {
				betterUpdateCount("Pawn", old);
				spinnerchange = true;
				bauerSpinner.getValueFactory().setValue(old);
				System.out.println("Nicht genug Platz fuer " + newValue + " Bauern je Team");
			}
		});
		// bauerSpinner.prefHeightProperty().bind(t1.heightProperty());
		choose1.getChildren().add(bauerSpinner);

		valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, initialValue("Knight"));
		Spinner<Integer> springerSpinner = new Spinner<>(valueFactory);
		springerSpinner.setPrefWidth(100);
		springerSpinner.getValueFactory().valueProperty().addListener((obs, old, newValue) -> {
			if (spinnerchange) {
				spinnerchange = false;
				return;
			}
			if (newValue == 0) {
				if (tmpTemplate.getPieces().length == 1) {
					spinnerchange = true;
					springerSpinner.getValueFactory().setValue(old);
					System.out.println("Mindestens eine Figur noetig");
				}
			}
			betterUpdateCount("Knight", newValue);
			if (!TemplateChecker.checkTemplate(tmpTemplate)) {
				betterUpdateCount("Knight", old);
				spinnerchange = true;
				springerSpinner.getValueFactory().setValue(old);
				System.out.println("Nicht genug Platz fuer " + newValue + " Springer je Team");
			}

		});
		// springerSpinner.prefHeightProperty().bind(t1.heightProperty());
		choose1.getChildren().add(springerSpinner);

		valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, initialValue("Queen"));
		Spinner<Integer> dameSpinner = new Spinner<>(valueFactory);
		dameSpinner.setPrefWidth(100);
		dameSpinner.getValueFactory().valueProperty().addListener((obs, old, newValue) -> {
			if (spinnerchange) {
				spinnerchange = false;
				return;
			}
			if (newValue == 0) {
				if (tmpTemplate.getPieces().length == 1) {
					spinnerchange = true;
					dameSpinner.getValueFactory().setValue(old);
					System.out.println("Mindestens eine Figur noetig");
				}
			}
			betterUpdateCount("Queen", newValue);
			if (!TemplateChecker.checkTemplate(tmpTemplate)) {
				betterUpdateCount("Queen", old);
				spinnerchange = true;
				dameSpinner.getValueFactory().setValue(old);
				System.out.println("Nicht genug Platz fuer " + newValue + " Damen je Team");
			}

		});
		// dameSpinner.prefHeightProperty().bind(t1.heightProperty());
		choose1.getChildren().add(dameSpinner);

		VBox names2 = new VBox();
		names2.setStyle("-fx-spacing: 20px; -fx-padding: 10px;");
		Label t3 = new Label("Bishop");
		t3.getStyleClass().add("custom-label");
		names2.getChildren().add(t3);
		Label t4 = new Label("Rook");
		t4.getStyleClass().add("custom-label");
		names2.getChildren().add(t4);

		VBox choose2 = new VBox();
		choose2.setStyle("-fx-spacing: 15px; -fx-padding: 10px;");

		valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, initialValue("Bishop"));
		Spinner<Integer> laufSpinner = new Spinner<>(valueFactory);
		laufSpinner.setPrefWidth(100);
		laufSpinner.getValueFactory().valueProperty().addListener((obs, old, newValue) -> {
			if (spinnerchange) {
				spinnerchange = false;
				return;
			}
			if (newValue == 0) {
				if (tmpTemplate.getPieces().length == 1) {
					spinnerchange = true;
					laufSpinner.getValueFactory().setValue(old);
					System.out.println("Mindestens eine Figur noetig");
				}
			}
			betterUpdateCount("Bishop", newValue);
			if (!TemplateChecker.checkTemplate(tmpTemplate)) {
				betterUpdateCount("Bishop", old);
				spinnerchange = true;
				laufSpinner.getValueFactory().setValue(old);
				System.out.println("Nicht genug Platz fuer " + newValue + " Lauefer je Team");
			}

		});
		choose2.getChildren().add(laufSpinner);

		valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, initialValue("Rook"));
		Spinner<Integer> turmSpinner = new Spinner<>(valueFactory);
		turmSpinner.setPrefWidth(100);
		turmSpinner.getValueFactory().valueProperty().addListener((obs, old, newValue) -> {
			if (spinnerchange) {
				spinnerchange = false;
				return;
			}
			if (newValue == 0) {
				if (tmpTemplate.getPieces().length == 1) {
					spinnerchange = true;
					turmSpinner.getValueFactory().setValue(old);
					System.out.println("Mindestens eine Figur noetig");
				}
			}
			betterUpdateCount("Rook", newValue);
			if (!TemplateChecker.checkTemplate(tmpTemplate)) {
				betterUpdateCount("Rook", newValue);
				spinnerchange = true;
				turmSpinner.getValueFactory().setValue(old);
				System.out.println("Nicht genug Platz fuer " + newValue + " Tuerme je Team");
			}

		});
		choose2.getChildren().add(turmSpinner);

		valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0);
		Spinner<Integer> customSpinner = new Spinner<>(valueFactory);
		customSpinner.setPrefWidth(100);
		choose2.getChildren().add(customSpinner);

		//ObservableList<String> options = FXCollections.observableArrayList("Custom1", "Custom2");

		
		pieceComboBox = new ComboBox<>();
		// comboBox.prefWidthProperty().bind(turmSpinner.widthProperty());
		pieceComboBox.getStyleClass().add("custom-combo-box");
		pieceComboBox.setValue("Customs");
		String[] names = { "Pawn", "Knight", "Queen", "Bishop", "Rook" };
		ArrayList<String> defaultPieces = new ArrayList<String>(Arrays.asList(names));
		for (String type : customPieces.keySet()) {
			if (!defaultPieces.contains(type)) {
				pieceComboBox.getItems().add(type);
			}
		}
		pieceComboBox.setOnAction(e -> {
			boxchange = true;
			int customcount = customPieces.get(pieceComboBox.getValue()).getCount();
			customSpinner.getValueFactory().setValue(customcount);

		});
		customSpinner.getValueFactory().valueProperty().addListener((obs, old, newv) -> {
			if (boxchange) {
				boxchange = false;
				return;
			}
			if (spinnerchange) {
				spinnerchange = false;
				return;
			}
			if (newv == 0) {
				if (tmpTemplate.getPieces().length == 1) {
					spinnerchange = true;
					customSpinner.getValueFactory().setValue(old);
					System.out.println("Mindestens eine Figur noetig");
				}
			}
			betterUpdateCount(pieceComboBox.getValue(), newv);
			if (!TemplateChecker.checkTemplate(tmpTemplate)) {
				betterUpdateCount(pieceComboBox.getValue(), old);
				spinnerchange = true;
				customSpinner.getValueFactory().setValue(old);
				System.out.println("Nicht genug Platz fuer " + newv + " " + pieceComboBox.getValue() + " je Team");
			}
		});

		names2.getChildren().add(pieceComboBox);

		controlBox.getChildren().add(names1);
		controlBox.getChildren().add(choose1);
		controlBox.getChildren().add(names2);
		controlBox.getChildren().add(choose2);
		return controlBox;
	}

	/**
	 * Creates and fills the container for the 'Custom Pieces' option
	 * 
	 * @author aniemesc
	 * @return VBox customPieces
	 */
	private VBox createFigureCustomizer() {
		VBox controlBox = new VBox();
		controlBox.setAlignment(Pos.CENTER);
		controlBox.setStyle("-fx-spacing: 25px; -fx-padding: 25px;");

		GridPane controlgrid = new GridPane();
		controlgrid.setHgap(25);
		controlgrid.setVgap(20);
		controlBox.getChildren().add(controlgrid);

		Label namelabel = new Label("Name");
		namelabel.getStyleClass().add("custom-label");
		controlgrid.add(namelabel, 0, 0);

		TextField textfield = new TextField("");
		controlgrid.add(textfield, 1, 0);

		Label strengthlabel = new Label("Strength");
		strengthlabel.getStyleClass().add("custom-label");
		controlgrid.add(strengthlabel, 2, 0);

		SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 5);
		Spinner<Integer> strengthSpinner = new Spinner<>(valueFactory);
		controlgrid.add(strengthSpinner, 3, 0);

		Label shapeLabel = new Label("Shape");
		shapeLabel.getStyleClass().add("custom-label");
		controlgrid.add(shapeLabel, 0, 1);

		ObservableList<String> options = FXCollections.observableArrayList("None", "L-Shape");

		ComboBox<String> comboBox = new ComboBox<>(options);
		// comboBox.prefWidthProperty().bind(turmSpinner.widthProperty());
		comboBox.getStyleClass().add("custom-combo-box");
		comboBox.setValue("None");
		comboBox.prefWidthProperty().bind(textfield.widthProperty());
		controlgrid.add(comboBox, 1, 1);

		Label directionLabel = new Label("Directions");
		directionLabel.getStyleClass().add("custom-label");
		controlgrid.add(directionLabel, 2, 1);

		Label valueLabel = new Label("Value");
		valueLabel.getStyleClass().add("custom-label");
		controlgrid.add(valueLabel, 2, 2);

		ObservableList<String> options2 = FXCollections.observableArrayList("Left", "Right", "Up", "Down", "Up-Left",
				"Up-Right", "Down-Left", "Down-Right");

		ComboBox<String> comboBox2 = new ComboBox<>(options2);
		// comboBox.prefWidthProperty().bind(turmSpinner.widthProperty());
		comboBox2.getStyleClass().add("custom-combo-box");
		comboBox2.setValue("Left");
		comboBox2.prefWidthProperty().bind(textfield.widthProperty());

		controlgrid.add(comboBox2, 3, 1);

		valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100,
				tmpMovement.getDirections().getLeft());
		Spinner<Integer> direcSpinner = new Spinner<>(valueFactory);
		controlgrid.add(direcSpinner, 3, 2);

		Button addPiece = new Button("Add Custom Piece");
		addPiece.prefWidthProperty().bind(textfield.widthProperty());
		controlgrid.add(addPiece, 1, 2);
		addPiece.setOnAction(e -> {
			Directions directions = genrateMovementCopy();
			PieceDescription result = new PieceDescription();
			Movement movement = new Movement();
			movement.setDirections(directions);
			result.setMovement(movement);
			result.setType(textfield.getText());
			result.setAttackPower(strengthSpinner.getValueFactory().getValue());
			customPieces.put(result.getType(), result);
			pieceComboBox.getItems().add(result.getType());
			this.inform(result.getType() + " was added to custom Pieces.");

		});

		comboBox2.setOnAction(e -> {
			switch (comboBox2.getValue()) {
			case "Left":
				direcSpinner.getValueFactory().setValue(tmpMovement.getDirections().getLeft());
				break;
			case "Right":
				System.out.println("Test");
				direcSpinner.getValueFactory().setValue(tmpMovement.getDirections().getRight());
				break;
			case "Up":
				direcSpinner.getValueFactory().setValue(tmpMovement.getDirections().getUp());
				break;
			case "Down":
				direcSpinner.getValueFactory().setValue(tmpMovement.getDirections().getDown());
				break;
			case "Up-Left":
				direcSpinner.getValueFactory().setValue(tmpMovement.getDirections().getUpLeft());
				break;
			case "Up-Right":
				direcSpinner.getValueFactory().setValue(tmpMovement.getDirections().getUpRight());
				break;
			case "Down-Left":
				direcSpinner.getValueFactory().setValue(tmpMovement.getDirections().getDownLeft());
				break;
			case "Down-Right":
				direcSpinner.getValueFactory().setValue(tmpMovement.getDirections().getDownRight());
				break;
			default:
				System.out.println("Unknown");
				break;
			}
		});

		direcSpinner.getValueFactory().valueProperty().addListener((obs, old, newv) -> {
			switch (comboBox2.getValue()) {
			case "Left":
				tmpMovement.getDirections().setLeft(newv);
				break;
			case "Right":
				System.out.println("Test");
				tmpMovement.getDirections().setRight(newv);
				break;
			case "Up":
				tmpMovement.getDirections().setUp(newv);
				break;
			case "Down":
				tmpMovement.getDirections().setDown(newv);
				break;
			case "Up-Left":
				tmpMovement.getDirections().setUpLeft(newv);
				break;
			case "Up-Right":
				tmpMovement.getDirections().setUpRight(newv);
				break;
			case "Down-Left":
				tmpMovement.getDirections().setDownLeft(newv);
				break;
			case "Down-Right":
				tmpMovement.getDirections().setDownRight(newv);
				break;
			default:
				System.out.println("Unknown");
				break;
			}
		});

		return controlBox;
	}

	/**
	 * Initial test method that initializes the tmpTemplate
	 * 
	 * @author aniemesc
	 * 
	 */
	private void initializeTemplate() {
		feld = new String[5][5];
		for (int row = 0; row < 5; row++) {
			for (int col = 0; col < 5; col++) {
				if (row == 0 || row == 4) {
					feld[row][col] = "R";
				} else {
					feld[row][col] = "L";
				}
			}
		}
		this.tmpMovement.setDirections(new Directions());
		this.createExamplePieces();
		ArrayList<PieceDescription> usedPieces = new ArrayList<PieceDescription>();
		for (String type : customPieces.keySet()) {
			if (customPieces.get(type).getCount() > 0) {
				usedPieces.add(customPieces.get(type));
			}
		}
//		int[] gridsize = { 10, 10 };
//		this.tmpTemplate.setGridSize(gridsize);
//
//		PieceDescription[] result = usedPieces.toArray(new PieceDescription[usedPieces.size()]);
//		tmpTemplate.setPieces(result);
	}

	/**
	 * test method for genrating an example piece
	 * 
	 * @author aniemesc
	 * 
	 */
	private void createExamplePieces() {
		Movement bm = new Movement();
		Directions directions = new Directions();
		directions.setUp(1);
		bm.setDirections(directions);
		PieceDescription bauer = new PieceDescription();
		bauer.setAttackPower(5);
		bauer.setType("Bauer");
		bauer.setMovement(bm);
		bauer.setCount(5);
		this.customPieces.put("Bauer", bauer);
	}
	/**
	 * test method for genrating an example template
	 * 
	 * @author aniemesc
	 * @return MapTemplate example
	 */
	private MapTemplate createExampleTemplate() {
		createExamplePieces();
		MapTemplate example = new MapTemplate();
		int[] gridsize = { 10, 10 };
		example.setGridSize(gridsize);
		example.setBlocks(0);
		example.setFlags(1);
		example.setMoveTimeLimitInSeconds(60);
		example.setTotalTimeLimitInSeconds(180);
		example.setTeams(2);
		example.setPlacement(PlacementType.symmetrical);
		ArrayList<PieceDescription> usedPieces = new ArrayList<PieceDescription>();
		for (String type : customPieces.keySet()) {
			if (customPieces.get(type).getCount() > 0) {
				usedPieces.add(customPieces.get(type));
			}
		}
		PieceDescription[] result = usedPieces.toArray(new PieceDescription[usedPieces.size()]);
		example.setPieces(result);
		return example;
	}

	/**
	 * helping method for generating a copy of a Directions object
	 * 
	 * @author aniemesc
	 * 
	 */
	public Directions genrateMovementCopy() {
		Directions result = new Directions();
		result.setLeft(this.tmpMovement.getDirections().getLeft());
		result.setUp(this.tmpMovement.getDirections().getUp());
		result.setRight(this.tmpMovement.getDirections().getRight());
		result.setDown(this.tmpMovement.getDirections().getDown());
		result.setUpLeft(this.tmpMovement.getDirections().getUpLeft());
		result.setUpRight(this.tmpMovement.getDirections().getUpRight());
		result.setDownLeft(this.tmpMovement.getDirections().getDownLeft());
		result.setDownRight(this.tmpMovement.getDirections().getDownRight());
		return result;
	}

	/**
	 * method that genrates the option menu button
	 * 
	 * @author aniemesc
	 * @return MenuButton optionMenuButton
	 * 
	 */
	private MenuButton createMenuButton() {
		MenuButton mb = new MenuButton("Edit Map");
		mb.setPrefSize(130, 30);
		mb.getStyleClass().add("custom-menu-button");
		MenuItem mapMenuItem = new MenuItem("Edit Map");
		MenuItem figureMenuItem = new MenuItem("Add Pieces");
		MenuItem configMenuItem = new MenuItem("Costum Pieces");
		mb.getItems().addAll(mapMenuItem, figureMenuItem, configMenuItem);
		mapMenuItem.setOnAction(event -> {
			left.getChildren().clear();
			left.getChildren().add(options[0]);
			mb.setText("Edit Map");

		});
		figureMenuItem.setOnAction(event -> {
			left.getChildren().clear();
			left.getChildren().add(options[1]);
			mb.setText("Add Pieces");
		});
		configMenuItem.setOnAction(event -> {
			left.getChildren().clear();
			left.getChildren().add(options[2]);
			mb.setText("Costum Pieces");
		});
		return mb;
	}

	/**
	 * method that genrates the map menu button
	 * 
	 * @author aniemesc
	 * @return MenuButton mapMenuButton
	 * 
	 */
	private MenuButton createMapMenuButton() {
		MenuButton mb = new MenuButton("Load Map");
		mb.setPrefSize(130, 30);
		mb.getStyleClass().add("custom-menu-button");

		MenuItem default1Item = new MenuItem("Map One");
		MenuItem default2Item = new MenuItem("Map Two");

		mb.getItems().addAll(default1Item, default2Item);
		default1Item.setOnAction(event -> {
			File defaultMap = new File("src" + File.separator + "main" + File.separator + "java" + File.separator
					+ "org" + File.separator + "ctf" + File.separator + "ui" + File.separator + "default.json");
			try {
				tmpTemplate = JsonTools.readMapTemplate(defaultMap);
			} catch (IncompleteMapTemplateException | IOException e) {
				System.out.println("fail");
			}
			initializePieces();
			options[0] = createMapChooser();
			options[1] = createFigurChooser();
			left.getChildren().clear();
			left.getChildren().add(options[0]);
			this.test.getChildren().remove(visual);
			mp = new MapPreview(tmpTemplate);
//			GameState gamestate = mp.getGameState();
//			visual = new GamePane(gamestate);
			this.test.getChildren().add(visual);
		});
		default2Item.setOnAction(event -> {
			File defaultMap = new File("src" + File.separator + "main" + File.separator + "java" + File.separator
					+ "org" + File.separator + "ctf" + File.separator + "ui" + File.separator + "default2.json");
			try {
				tmpTemplate = JsonTools.readMapTemplate(defaultMap);
			} catch (IncompleteMapTemplateException | IOException e) {
				System.out.println("fail");
			}
			initializePieces();
			options[0] = createMapChooser();
			options[1] = createFigurChooser();
			left.getChildren().clear();
			left.getChildren().add(options[0]);
		});

		return mb;
	}

	/**
	 * method that genrates the exit button
	 * 
	 * @author aniemesc
	 * @return Button exit
	 * 
	 */
	private Button createExit() {
		Button exit = new Button("LEAVE");
		exit.setPrefSize(100, 25);
		exit.setStyle("-fx-text-fill: white;" + "-fx-background-color: rgba(0,0,0,0.4);"
				+ "-fx-border-color: white; -fx-border-width: 2px;");
		exit.setOnMouseEntered(e -> {
			exit.setStyle("-fx-text-fill: white;" + "-fx-background-color: rgba(0,0,0,0.4);"
					+ "-fx-border-color: red; -fx-border-width: 2px;");
		});
		exit.setOnMouseExited(e -> {
			exit.setStyle("-fx-text-fill: white;" + "-fx-background-color: rgba(0,0,0,0.4);"
					+ "-fx-border-color: #FFCCE5; -fx-border-width: 2px;");
		});
		exit.setOnAction(e -> this.hsc.switchtoHomeScreen(e));
		exit.setFont(Font.font("System", FontWeight.BOLD, 14));
		return exit;
	}

	/**
	 * method that genrates the submit button
	 * 
	 * @author aniemesc
	 * @return Button submit
	 * 
	 */
	private Button createSubmit() {
		Button submit = new Button("SUBMIT");
		submit.setPrefSize(100, 25);
		submit.setStyle("-fx-text-fill: white;" + "-fx-background-color: rgba(0,0,0,0.4);"
				+ "-fx-border-color: white; -fx-border-width: 2px;");
		submit.setOnMouseEntered(e -> {
			submit.setStyle("-fx-text-fill: white;" + "-fx-background-color: rgba(0,0,0,0.4);"
					+ "-fx-border-color: green; -fx-border-width: 2px;");
		});
		submit.setOnMouseExited(e -> {
			submit.setStyle("-fx-text-fill: white;" + "-fx-background-color: rgba(0,0,0,0.4);"
					+ "-fx-border-color: #FFCCE5; -fx-border-width: 2px;");
		});
		submit.setOnAction(e -> {
			try {
				JsonTools.saveTemplateWithGameState("test", tmpTemplate, mp.getGameState());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		submit.setFont(Font.font("System", FontWeight.BOLD, 14));
		return submit;
	}

	/**
	 * method that genrates a test grid
	 * 
	 * @author aniemesc
	 * @return Gridpane testgrid
	 * 
	 */
	private GridPane CreateMapGrid() {
		GridPane gridPane = new GridPane();
		for (int row = 0; row < 5; row++) {
			for (int col = 0; col < 5; col++) {
				StackPane stack = new StackPane();
				Rectangle rectangle = new Rectangle(50, 50);
				rectangle.setStroke(Color.BLACK);
				rectangle.setStrokeWidth(1);
				stack.getChildren().add(rectangle);
				if (feld[row][col].equals("R")) {
					Circle c = new Circle(10);
					c.setFill(Color.BLACK);
					stack.getChildren().add(c);
				}
				gridPane.add(stack, col, row);
				if (Math.random() < 0.2) {
					rectangle.setFill(Color.rgb(255, 255, 153));
				} else if (Math.random() < 0.7) {
					rectangle.setFill(Color.rgb(255, 255, 120));
				} else {
					rectangle.setFill(Color.rgb(255, 255, 180));
				}

			}
		}
		return gridPane;

	}

	/**
	 * method that generates a text representation of a template
	 * 
	 * @author aniemesc
	 * 
	 * 
	 */
	public void printTemplate() {
		StringBuffer buf = new StringBuffer();
		for (PieceDescription p : tmpTemplate.getPieces()) {
			buf.append(p.getType() + " " + p.getCount() + "\n");
		}
		System.out.println("Grid:" + tmpTemplate.getGridSize()[0] + " " + tmpTemplate.getGridSize()[1] + "\n"
				+ "Teams/Flags/Blocks" + tmpTemplate.getTeams() + tmpTemplate.getFlags() + tmpTemplate.getBlocks()
				+ "\n" + "placement" + tmpTemplate.getPlacement().toString() + "\n" + "thinktime "
				+ tmpTemplate.getMoveTimeLimitInSeconds() + buf.toString());
	}

	/**
	 * method that updates the custompieces hash map when a new template is loaded
	 * 
	 * @author aniemesc
	 * 
	 * 
	 */
	private void initializePieces() {
		ArrayList<String> usedTypes = new ArrayList<String>();
		for (PieceDescription piece : tmpTemplate.getPieces()) {
			customPieces.put(piece.getType(), piece);
			usedTypes.add(piece.getType());
		}
		for (PieceDescription piece : customPieces.values()) {
			if (!usedTypes.contains(piece.getType())) {
				piece.setCount(0);
			}
		}

	}

	/**
	 * method that delivers count value
	 * 
	 * @author aniemesc
	 * @param String name
	 * @return int value
	 * 
	 */
	private int initialValue(String name) {
		if (!customPieces.containsKey(name)) {
			return 0;
		} else {
			return customPieces.get(name).getCount();
		}
	}

	@Deprecated
	private void updateCount(String s, int value) {
		customPieces.get(s).setCount(value);
		if (value == 0) {
			int updatedlength = tmpTemplate.getPieces().length - 1;
			PieceDescription[] updatedPieces = new PieceDescription[updatedlength];
			int next = 0;
			for (int i = 0; i < tmpTemplate.getPieces().length; i++) {
				if (!tmpTemplate.getPieces()[i].getType().equals(s)) {
					updatedPieces[next] = tmpTemplate.getPieces()[i];
					next++;
				}
			}
			tmpTemplate.setPieces(updatedPieces);
			for (PieceDescription p : tmpTemplate.getPieces()) {
				if (p == null) {
					System.out.println("leer");
				} else {
					System.out.println(p.getType());
				}
			}
			return;
		}

		for (PieceDescription piece : tmpTemplate.getPieces()) {
			if (piece.getType().equals(s)) {
				piece.setCount(value);
				return;
			}
		}

		int updatedlength = tmpTemplate.getPieces().length + 1;
		PieceDescription[] updatedPieces = new PieceDescription[updatedlength];
		for (int i = 0; i < tmpTemplate.getPieces().length; i++) {
			updatedPieces[i] = tmpTemplate.getPieces()[i];
		}
		updatedPieces[updatedlength - 1] = customPieces.get(s);
		tmpTemplate.setPieces(updatedPieces);
	}

	/**
	 * method that updates the number of pieces of a maptemplate
	 * 
	 * @author aniemesc
	 * @param String type
	 * @param int    newValue
	 * 
	 */
	public void betterUpdateCount(String s, int newValue) {
		customPieces.get(s).setCount(newValue);
		int number = (int) customPieces.values().stream().filter(p -> p.getCount() > 0).count();
		PieceDescription[] updated = new PieceDescription[number];
		int i = 0;
		for (PieceDescription p : customPieces.values()) {
			if (p.getCount() > 0) {
				updated[i] = p;
				i++;
			}
		}
		tmpTemplate.setPieces(updated);

	}

	/**
	 * method that inform the user via the gui using a textinput
	 * 
	 * @author aniemesc
	 * @param String info
	 * 
	 */
	private void inform(String s) {
		text.setText(s);
		FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), text);
		fadeTransition.setDelay(Duration.seconds(1));
		fadeTransition.setFromValue(1.0);
		fadeTransition.setToValue(0.0);
		fadeTransition.setOnFinished(event -> {
			text.setText("");
			text.setOpacity(1);
		});
		fadeTransition.play();
	}

}
