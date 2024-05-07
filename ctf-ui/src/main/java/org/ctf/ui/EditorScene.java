package org.ctf.ui;

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
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.ctf.shared.state.data.exceptions.Accepted;
import org.ctf.ui.controllers.MapPreview;
import org.ctf.ui.customobjects.MovementVisual;

/**
 * Represents a JavaFX scene for the map editor. It contains all necessary UI components for
 * loading, customizing, rendering and saving map templates.
 * 
 * @author aniemesc
 */
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
  MediaPlayer mediaPlayer;
  VBox directionsContainer;
  MovementVisual movementVisual;

  /**
   * Starts the initialization process of the scene, generates different menu panes and connects it
   * to a CSS file.
   * 
   * @author aniemesc
   * @param hsc - HomeSceneController that connects scene to rest of the application
   * @param width - double value for width init
   * @param height - double value for height init
   */
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


  /**
   * This method creates the basic layout by adding all major top level containers to the scene.
   * 
   * @author aniemesc
   */
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

  /**
   * This Method creates the header Image for the scene.
   * 
   * @author aniemesc
   * @return ImageView that gets added to the scene
   */
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

  /**
   * Initializes the infoText attribute of the scene.
   * 
   * @author aniemesc
   */
  private void createInfotext() {
    infoText = new Text("");
    infoText.getStyleClass().add("custom-info-label");
    leftPane.widthProperty().addListener((obs, oldVal, newVal) -> {
      double size = newVal.doubleValue() * 0.035;
      infoText.setFont(Font.font("Century Gothic", size));
    });
  }

  /**
   * Presents text input on the UI by setting the infoText attribute and playing a FadeTransition.
   * 
   * @author aniemesc
   * @param info - String value that gets presented
   */
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

  /**
   * Creates all necessary UI components for the option pane that allows users to set general
   * template parameters. It consists of a grid containing different spinners and combo boxes and
   * their corresponding labels.
   * 
   * @author aniemesc
   * @return VBox - main container of "Edit Map" option
   */
  private VBox createMapChooser() {
    VBox mapRoot = new VBox();
    mapRoot.setSpacing(10);
    mapRoot.widthProperty().addListener((obs, oldVal, newVal) -> {
      double spacing = newVal.doubleValue() * 0.07;
      mapRoot.setSpacing(spacing);
    });
    mapRoot.setPadding(new Insets(10));
    mapRoot.setAlignment(Pos.TOP_CENTER);
    mapRoot.getChildren().add(createHeaderText(mapRoot, "Edit Map", 18));
    GridPane controlgrid = new GridPane();
    mapRoot.widthProperty().addListener((obs, oldVal, newVal) -> {
      double spacing = newVal.doubleValue() * 0.07;
      controlgrid.setHgap(spacing);
    });
    mapRoot.heightProperty().addListener((obs, oldVal, newVal) -> {
      double spacing = newVal.doubleValue() * 0.07;
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
    Spinner<Integer> rowsSpinner =
        createMapSpinner(1, 100, engine.tmpTemplate.getGridSize()[0]);
    createChangeListener(rowsSpinner, "Rows", false);
    controlgrid.add(rowsSpinner, 1, 0);
    Spinner<Integer> colSpinner =
        createMapSpinner(1, 100, engine.tmpTemplate.getGridSize()[1]);
    createChangeListener(colSpinner, "Cols", false);
    controlgrid.add(colSpinner, 1, 1);
    Spinner<Integer> teamSpinner = createMapSpinner(2, 50, engine.tmpTemplate.getTeams());
    createChangeListener(teamSpinner, "Teams", false);
    controlgrid.add(teamSpinner, 1, 2);
    Spinner<Integer> flagSpinner = createMapSpinner(1, 100, engine.tmpTemplate.getTeams());
    createChangeListener(flagSpinner, "Flags", false);
    controlgrid.add(flagSpinner, 1, 3);
    Spinner<Integer> blockSpinner =
        createMapSpinner(0, 500, engine.tmpTemplate.getBlocks());
    createChangeListener(blockSpinner, "Blocks", false);
    controlgrid.add(blockSpinner, 3, 0);
    Spinner<Integer> turnTimeSpinner =
        createMapSpinner(-1, 600, engine.tmpTemplate.getMoveTimeLimitInSeconds());
    createChangeListener(turnTimeSpinner, "TurnTime", false);
    controlgrid.add(turnTimeSpinner, 3, 1);
    int init = (engine.tmpTemplate.getTotalTimeLimitInSeconds() == -1) ? -1
        : engine.tmpTemplate.getTotalTimeLimitInSeconds() / 60;
    Spinner<Integer> gameTimeSpinner = createMapSpinner(-1, 600, init);
    createChangeListener(gameTimeSpinner, "GameTime", false);
    controlgrid.add(gameTimeSpinner, 3, 2);
    controlgrid.add(createPlacementBox(mapRoot), 3, 3);
    mapRoot.getChildren().add(controlgrid);
    return mapRoot;
  }

  /**
   * Creates all necessary UI components for the option pane that allows users to add pieces on the
   * map. It consists of a grid containing spinners for default pieces and a combo box for selecting
   * custom pieces.
   * 
   * @author aniemesc
   * @return VBox - main container of "Add Pieces" option
   */
  private VBox createFigureChooser() {
    VBox pieceRoot = new VBox();
//    pieceRoot.setSpacing(10);
    pieceRoot.setPadding(new Insets(20));
    pieceRoot.widthProperty().addListener((obs, oldVal, newVal) -> {
      double spacing = newVal.doubleValue() * 0.03;
      pieceRoot.setSpacing(spacing);
    });
    pieceRoot.setAlignment(Pos.TOP_CENTER);
    pieceRoot.getChildren().add(createHeaderText(pieceRoot, "Add Pieces", 19));
    GridPane controlgrid = new GridPane();
    pieceRoot.widthProperty().addListener((obs, oldVal, newVal) -> {
      double spacing = newVal.doubleValue() * 0.05;
      controlgrid.setHgap(spacing);
    });
    pieceRoot.heightProperty().addListener((obs, oldVal, newVal) -> {
      double spacing = newVal.doubleValue() * 0.03;
      controlgrid.setVgap(spacing);
    });
    controlgrid.add(createText(pieceRoot, "Pawn", 30), 0, 0);
    controlgrid.add(createText(pieceRoot, "Knight", 30), 0, 1);
    controlgrid.add(createText(pieceRoot, "Bishop", 30), 0, 2);
    controlgrid.add(createText(pieceRoot, "Rook", 30), 2, 0);
    controlgrid.add(createText(pieceRoot, "Queen", 30), 2, 1);
    controlgrid.add(createText(pieceRoot, "King", 30), 2, 2);
    Spinner<Integer> pawnSpinner =
        createMapSpinner(0, 500, engine.getPieceCount("Pawn"));
    createChangeListener(pawnSpinner, "Pawn", false);
    controlgrid.add(pawnSpinner, 1, 0);
    Spinner<Integer> knightSpinner =
        createMapSpinner(0, 500, engine.getPieceCount("Knight"));
    createChangeListener(knightSpinner, "Knight", false);
    controlgrid.add(knightSpinner, 1, 1);
    Spinner<Integer> bishopSpinner =
        createMapSpinner(0, 500, engine.getPieceCount("Bishop"));
    createChangeListener(bishopSpinner, "Bishop", false);
    controlgrid.add(bishopSpinner, 1, 2);
    Spinner<Integer> rookSpinner =
        createMapSpinner(0, 500, engine.getPieceCount("Rook"));
    createChangeListener(rookSpinner, "Rook", false);
    controlgrid.add(rookSpinner, 3, 0);
    Spinner<Integer> queenSpinner =
        createMapSpinner(0, 500, engine.getPieceCount("Queen"));
    createChangeListener(queenSpinner, "Queen", false);
    controlgrid.add(queenSpinner, 3, 1);
    Spinner<Integer> kingSpinner =
        createMapSpinner(0, 500, engine.getPieceCount("King"));
    createChangeListener(kingSpinner, "King", false);
    controlgrid.add(kingSpinner, 3, 2);
    pieceRoot.getChildren().add(controlgrid);

    pieceRoot.getChildren().add(createHeaderText(pieceRoot, "Custom Figures", 20));
    pieceRoot.getChildren().add(createFigureBar(pieceRoot));
    return pieceRoot;
  }

  /**
   * Creates all necessary UI components for the option pane that allows users to customize 
   * and add their own pieces. It consists of a grid containing several control 
   * items and their corresponding labels.
   * 
   * @author aniemesc
   * @return VBox - main container of "Add Pieces" option
   */
  private VBox createFigureCustomizer() {
    VBox customRoot = new VBox();
    customRoot.setSpacing(10);
    customRoot.widthProperty().addListener((obs, oldVal, newVal) -> {
      double spacing = newVal.doubleValue() * 0.05;
      customRoot.setSpacing(spacing);
    });
    customRoot.setPadding(new Insets(20));
    customRoot.setAlignment(Pos.TOP_CENTER);
    GridPane controlgrid = new GridPane();
    customRoot.widthProperty().addListener((obs, oldVal, newVal) -> {
      double spacing = newVal.doubleValue() * 0.05;
      controlgrid.setHgap(spacing);
    });
    customRoot.heightProperty().addListener((obs, oldVal, newVal) -> {
      double spacing = newVal.doubleValue() * 0.1;
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
    controlgrid.add(createShapeBox(), 1, 1);
    Spinner<Integer> strenghthSpinner = createMapSpinner(0, 500, 0);
    controlgrid.add(strenghthSpinner, 3, 0);

    Spinner<Integer> valueSpinner = createMapSpinner(0, 500, 0);

    ComboBox<String> directionsBox = createDirectionsBox(valueSpinner);
    valueSpinner.getValueFactory().valueProperty().addListener((obs, old, newValue) -> {
      engine.handleDirectionValue(directionsBox, newValue);
      movementVisual.updateMovementOptions(directionsBox.getValue());
    });
    controlgrid.add(directionsBox, 3, 1);
    controlgrid.add(valueSpinner, 3, 2);
    controlgrid.add(createAddButton(customRoot, namefield, strenghthSpinner), 1, 2);
    customRoot.getChildren().add(controlgrid);

    return customRoot;
  }
/**
 * Creates the Container for displaying the different option tabs on 
 * the left side of the scene.
 * @author aniemesc
 * @return StackPane container
 */
  private StackPane createLeftPane() {
    StackPane pane = new StackPane();
    pane.getStyleClass().add("option-pane");
    pane.setPadding(new Insets(10));
    pane.prefWidthProperty().bind(this.widthProperty().multiply(0.4));
    pane.prefHeightProperty().bind(pane.widthProperty().multiply(0.65));

    return pane;
  }

  /**
   * Creates the container that displays the menu buttons for loading a map
   * and switching between option tabs
   * @author aniemesc
   * @return HBox container
   */
  private HBox createControlBar() {
    HBox controlBar = new HBox();
    controlBar.setSpacing(10);
    controlBar.getChildren().add(createMenuButton());
    createMapMenuButton();
    controlBar.getChildren().add(mapMenuButton);
    controlBar.getChildren().add(createExit());
    controlBar.getChildren().add(createSubmit());
    return controlBar;
  }

  /**
   * Creates and styles buttons for the scene.
   * @author aniemesc
   * @param label - String value for button initialization
   * @return Button object
   */
  private Button createControlButton(String label) {
    Button but = new Button(label);
    but.getStyleClass().add("leave-button");
    but.prefWidthProperty().bind(root.widthProperty().multiply(0.1));
    but.prefHeightProperty().bind(but.widthProperty().multiply(0.25));
    but.prefHeightProperty().addListener((obs, oldv, newV) -> {
      double size = newV.doubleValue() * 0.5;
      but.setFont(Font.font("Century Gothic", size));
    });
    return but;
  }

  /**
   * Creates the Leave Button for the scene.
   * @author aniemesc
   * @return Button for leaving
   */
  private Button createExit() {
    Button exit = createControlButton("Leave");
    exit.setOnAction(e -> {
      hsc.switchtoHomeScreen(e);
    });
    return exit;
  }

  /**
   * Creates the Submit Button which opens a submitting window.
   * @author aniemesc
   * @return Button for submitting templates
   */
  private Button createSubmit() {
    Button submit = createControlButton("Submit");
    submit.setOnAction(e -> {
      engine.printTemplate();
      // root.getChildren().add(new PopUpPane(this, 0.4, 0.4));
      root.getChildren().add(new ComponentCreator(this).createSubmitWindow());
    });
    return submit;
  }

  /**
   * Creates the MenuButton which allows the user to switch
   *  between the option tabs "Edit Map","Add Pieces" and "Custom Pieces".
   * @author aniemesc
   * @return MenuButton for switching option tabs.
   */
  private MenuButton createMenuButton() {
    mb = new MenuButton("Edit Map");
    mb.getStyleClass().add("custom-menu-button");
    mb.prefWidthProperty().bind(root.widthProperty().multiply(0.1));
    mb.prefHeightProperty().bind(mb.widthProperty().multiply(0.25));
    // mb.prefHeightProperty().addListener((obs, oldv, newV) -> {
    // System.out.println("hey");
    // double size = newV.doubleValue() * 0.5;
    // mb.setFont(Font.font("Century Gothic", size));
    // });
    MenuItem mapMenuItem = new MenuItem("Edit Map");
    MenuItem figureMenuItem = new MenuItem("Add Pieces");
    MenuItem configMenuItem = new MenuItem("Custom Pieces");
    mb.getItems().addAll(mapMenuItem, figureMenuItem, configMenuItem);
    mapMenuItem.setOnAction(event -> {
      leftPane.getChildren().clear();
      leftPane.getChildren().add(options[0]);
      mb.setText("Edit Map");
      updateVisualRoot();
    });
    figureMenuItem.setOnAction(event -> {
      leftPane.getChildren().clear();
      leftPane.getChildren().add(options[1]);
      mb.setText("Add Pieces");
      updateVisualRoot();
    });
    configMenuItem.setOnAction(event -> {
      leftPane.getChildren().clear();
      leftPane.getChildren().add(options[2]);
      mb.setText("Custom Pieces");
      visualRoot.getChildren().clear();
      visualRoot.getChildren().add(directionsContainer);
    });
    return mb;
  }

  /**
   * Creates the MenuButton which allows the user to load map templates
   * into the editor scene.
   * @author aniemesc
   * @return MenuButton for loading map templates
   */
  private MenuButton createMapMenuButton() {
    mapMenuButton = new MenuButton("Load Map");
    mapMenuButton.getStyleClass().add("custom-menu-button");
    mapMenuButton.prefWidthProperty().bind(root.widthProperty().multiply(0.1));
    mapMenuButton.prefHeightProperty().bind(mb.widthProperty().multiply(0.25));
    for (String mapName : engine.getTemplateNames()) {
      addMapItem(mapName);
    }
    return mb;
  }

  /**
   * Creates a MenuItem and adds it to the MapMenuButton. When triggered it
   * causes the corresponding map template to be loaded in the map editor.
   * @author aniemesc
   * @param mapName - Name of the maptemplate
   */
  public void addMapItem(String mapName) {
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

  /**
   * Creates a custom Text Header.
   * @author aniemesc
   * @param vBox - Container used for resize dependency  
   * @param label - String value
   * @param divider - int value that determines the resize ratio
   * @return
   */
  public static Text createHeaderText(VBox vBox, String label, int divider) {
    Text leftheader = new Text(label);
    leftheader.getStyleClass().add("custom-header");
    leftheader.fontProperty().bind(Bindings.createObjectBinding(
        () -> Font.font("Century Gothic", vBox.getWidth() / divider), vBox.widthProperty()));
    return leftheader;
  }

  /**
   * Creates a custom Text.
   * @author aniemesc
   * @param vBox - Container used for resize dependency
   * @param label - String value
   * @param divider - int value that determines the resize ratio
   * @return
   */
  private Text createText(VBox vBox, String label, int divider) {
    Text text = new Text(label);
    text.getStyleClass().add("custom-info-label");
    text.fontProperty().bind(Bindings.createObjectBinding(
        () -> Font.font("Century Gothic", vBox.getWidth() / divider), vBox.widthProperty()));
    return text;
  }

  /**
   * Creates and styles a custom spinner object.
   * @author aniemesc
   * @param min - minimum int value of spinner
   * @param max - maximum int value of spinner
   * @param cur - current int value of spinner
   * @return custom spinner object
   */
  private Spinner<Integer> createMapSpinner(int min, int max, int cur) {
    this.valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(min, max, cur);
    Spinner<Integer> spinner = new Spinner<>(valueFactory);
    spinner.getStyleClass().add("spinner");
    spinner.setEditable(true);
    spinner.prefWidthProperty().bind(this.widthProperty().multiply(0.1));
    spinner.prefHeightProperty().bind(spinner.widthProperty().multiply(0.25));
    return spinner;
  }

  /**
   * Creates the ComboBox for choosing the placement type of the map template.
   * @param vBox - Container used for resize dependency
   * @author aniemesc
   * @return ComboBox for placement type
   */
  private ComboBox<String> createPlacementBox(VBox vBox) {
    ObservableList<String> options =
        FXCollections.observableArrayList("Symmetric", "Spaced Out", "Defensive");
    ComboBox<String> placementBox = new ComboBox<>(options);
    switch (engine.tmpTemplate.getPlacement()) {
      case symmetrical:
        placementBox.setValue("Symmetric");
        break;
      case spaced_out:
        placementBox.setValue("Spaced Out");
        break;
      case defensive:
        placementBox.setValue("Defenisve");;
        break;
      default:
    }
    placementBox.setOnAction(e -> {
      engine.setPlacement(placementBox.getValue());
      updateVisualRoot();
    });
    placementBox.getStyleClass().add("custom-combo-box-2");
    placementBox.prefWidthProperty().bind(this.widthProperty().multiply(0.1));
    placementBox.prefHeightProperty().bind(placementBox.widthProperty().multiply(0.25));
    placementBox.prefHeightProperty().addListener((obs, oldv, newV) -> {
      double size = newV.doubleValue() * 0.4;
      placementBox.setStyle("-fx-font-size: " + size + "px;");
  });
    return placementBox;
  }

  /**
   * Creates and styles the ComboBox responsible for choosing saved 
   * custom pieces in the "Add Pieces" option tab.
   * @author aniemesc
   * @return ComboBox for choosing saved pieces
   */
  private ComboBox<String> createFigureBox() {
    customFigureBox = new ComboBox<>();
    engine.fillCustomBox(customFigureBox);
    customFigureBox.getStyleClass().add("custom-combo-box-2");
    customFigureBox.prefWidthProperty().bind(this.widthProperty().multiply(0.15));
    customFigureBox.prefHeightProperty().bind(customFigureBox.widthProperty().multiply(0.18));
    customFigureBox.prefHeightProperty().addListener((obs, oldv, newV) -> {
      double size = newV.doubleValue() * 0.25;
      customFigureBox.setStyle("-fx-font-size: " + size + "px;");
  });
    return customFigureBox;
  }

  /**
   * Creates and styles the ComboBox responsible for choosing  
   * a movement shape in the "Configure Pieces" option tab.
   * @author aniemesc
   * @return ComboBox for choosing a movement shape
   */
  private ComboBox<String> createShapeBox() {
    ObservableList<String> options = FXCollections.observableArrayList("None", "L-Shape");
    ComboBox<String> shapeBox = new ComboBox<>(options);
    shapeBox.setValue(options.get(0));
    shapeBox.getStyleClass().add("custom-combo-box-2");
    shapeBox.prefWidthProperty().bind(this.widthProperty().multiply(0.1));
    shapeBox.prefHeightProperty().bind(shapeBox.widthProperty().multiply(0.25));
    shapeBox.prefHeightProperty().addListener((obs, oldv, newV) -> {
      double size = newV.doubleValue() * 0.4;
      shapeBox.setStyle("-fx-font-size: " + size + "px;");
  });
    return shapeBox;
  }

  /**
   * Creates and styles the ComboBox responsible for choosing  
   * a direction in the "Configure Pieces" option tab.
   * @author aniemesc
   * @param vaSpinner - spinner object for selecting the corresponding value
   * @return ComboBox for choosing a direction
   */
  private ComboBox<String> createDirectionsBox(Spinner<Integer> vaSpinner) {
    ObservableList<String> options = FXCollections.observableArrayList("Left", "Right", "Up",
        "Down", "Up-Left", "Up-Right", "Down-Left", "Down-Right");
    ComboBox<String> directionBox = new ComboBox<>(options);
    directionBox.setOnAction(e -> {
      engine.handleDirection(directionBox.getValue(), vaSpinner);
    });
    directionBox.setValue(options.get(0));
    directionBox.getStyleClass().add("custom-combo-box-2");
    directionBox.prefWidthProperty().bind(this.widthProperty().multiply(0.1));
    directionBox.prefHeightProperty().bind(directionBox.widthProperty().multiply(0.25));
    directionBox.prefHeightProperty().addListener((obs, oldv, newV) -> {
      double size = newV.doubleValue() * 0.4;
      directionBox.setStyle("-fx-font-size: " + size + "px;");
  });
    return directionBox;
  }

  /**
   * Creates the container for the UI components required for adding 
   * custom pieces in the Add Pieces" option tab.
   * @param vBox - Container used for resize dependency
   * @return HBox container
   */
  private HBox createFigureBar(VBox vBox) {
    HBox chooseBar = new HBox();
    chooseBar.setAlignment(Pos.CENTER);
    vBox.widthProperty().addListener((obs, oldVal, newVal) -> {
      double spacing = newVal.doubleValue() * 0.06;
      chooseBar.setSpacing(spacing);
    });
    chooseBar.getChildren().add(createFigureBox());
    Spinner<Integer> customSpinner = createMapSpinner(0, 100, 0);
    chooseBar.getChildren().add(customSpinner);
    customFigureBox.setValue("Choose Custom Piece");
    customFigureBox.setOnAction(e -> {
      boxchange = true;
      int customcount = engine.getPieceCount(customFigureBox.getValue());
      if (customSpinner.getValue() == customcount) {
        boxchange = false;
      }
      customSpinner.getValueFactory().setValue(customcount);
    });
    createChangeListener(customSpinner, "custom", true);
    return chooseBar;
  }

  /**
   * Creates and styles  a TextField for naming custom pieces in 
   * the "Configure Pieces" option tab.
   * @author aniemesc
   * @param vbox - Container used for resize dependency
   * @return TextField for naming custom pieces
   */
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
        textField.setText(oldValue); 
      }
    });
    return textField;
  }

  /**
   * Creates and styles  a Button for adding custom pieces in 
   * the "Configure Pieces" option tab.
   * @author aniemesc
   * @param vbox - Container used for resize dependency
   * @param name - TextField containing the name of the piece
   * @param strength - Spinner containing the strength value of the piece
   * @return Button for adding custom pieces
   */
  private Button createAddButton(VBox vbox, TextField name, Spinner<Integer> strength) {
    Button addButton = new Button("Add");
    addButton.getStyleClass().add("join-button");
    addButton.prefWidthProperty().bind(vbox.widthProperty().multiply(0.25));
    addButton.prefHeightProperty().bind(addButton.widthProperty().multiply(0.25));
    // addButton.fontProperty().bind(Bindings.createObjectBinding(
    // () -> Font.font("Century Gothic", addButton.getHeight() * 0.35),
    // addButton.heightProperty()));
    addButton.setOnAction(e -> {
      engine.addpiece(name, strength);
    });
    return addButton;
  }

  /**
   * Creates the container for the visual map representation and calls methods 
   * for initializing its content.
   * @author aniemesc
   */
  private void createVisual() {
    visualRoot = new StackPane();
    visualRoot.getStyleClass().add("option-pane");
    visualRoot.setPadding(new Insets(10));
    visualRoot.prefWidthProperty().bind(root.widthProperty().multiply(0.45));
    visualRoot.prefHeightProperty().bind(root.heightProperty().multiply(0.75));
    // GamePane visual = new GamePane(CreateTextGameStates.createTestGameState1());
    createDirectionsVisual();
    updateVisualRoot();
    // visualRoot.getChildren().add(visual);
  }

  /**
   * Creates a custom ChangeListener for Spinner objects that uses the template 
   * engine to handle events.
   * @author aniemesc
   * @param spinner - Spinner that the listener gets added to
   * @param event - String representation of an event 
   * @param custom - boolean value that tells you whether the spinner is for custom pieces
   */
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
      } ;
    });
  }
  
  /**
   * Sets the flag that indicates whether a spinner was recently changed 
   * @author aniemesc
   * @param value - boolean value indicating if a spinner was changed 
   */
  public void setSpinnerChange(boolean value) {
    this.spinnerchange = value;
  }

  /**
   * Updates the visual map representation based on the current map template.
   * @author aniemesc
   * @author rsyed: Bug fixes
   */
  private void updateVisualRoot() {
    MapPreview mp = new MapPreview(engine.tmpTemplate);
    visualRoot.getChildren().clear();
    try {
      visualRoot.getChildren().add(new GamePane(mp.getGameState()));
    } catch (Accepted e) {
      e.getMessage();
    }

    // GridPane stack = new GridPane();
    // Button but = new Button("hi");
    // StackPane.setAlignment(but, Pos.CENTER);
    // stack.getChildren().add(but);
    // visualRoot.getChildren().add(directionsContainer);
  }

  /**
   * Initializes the MovementVisual of the EditorScene used for displaying the 
   * current movement options of a custom piece
   * @author aniemesc
   */
  private void createDirectionsVisual() {
    this.directionsContainer = new VBox();
    directionsContainer.maxWidthProperty().bind(visualRoot.widthProperty().multiply(0.75));
    directionsContainer.maxHeightProperty().bind(visualRoot.widthProperty().multiply(0.75));
    StackPane.setAlignment(directionsContainer, Pos.CENTER);
    movementVisual = new MovementVisual(directionsContainer, engine);
    directionsContainer.getChildren().add(movementVisual);

  }

  public ComboBox<String> getCustomFigureBox() {
    return customFigureBox;
  }

  public TemplateEngine getEngine() {
    return this.engine;
  }

  public StackPane getRootPane() {
    return this.root;
  }
}
