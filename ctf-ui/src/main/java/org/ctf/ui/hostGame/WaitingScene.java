package org.ctf.ui.hostGame;

import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.ImageType;
import org.ctf.ui.App;
import org.ctf.ui.controllers.CheatboardListener;
import org.ctf.ui.controllers.HomeSceneController;
import org.ctf.ui.controllers.ImageController;
import org.ctf.ui.creators.PopUpCreator;
import org.ctf.ui.creators.PopUpCreatorEnterTeamName;
import org.ctf.ui.customobjects.MyCustomColorPicker;
import org.ctf.ui.data.ClientStorage;
import org.ctf.ui.data.SceneHandler;


/**
 * Scene in which the user waits for other remote plyers to join his game and has to option to add
 * local players to play on one device
 * 
 * @author Manuel Krakowski
 */

public class WaitingScene extends Scene {
  // Executor Service and data which is changed by it
  private ScheduledExecutorService scheduler;
  private int currentNumber;

  // When a new team joins the corresponding Boxes need to be filled with a color, team-name and
  // type team-id -> corresponding box
  private HashMap<Integer, Label> teamNames = new HashMap<Integer, Label>();
  private HashMap<Integer, Label> teamTypes = new HashMap<Integer, Label>();
  private HashMap<Integer, HBox> colors = new HashMap<Integer, HBox>();

  // Containers and Labels which need to be accessed from different methods
  private StackPane root;
  private Label clipboardInfo;
  private VBox leftBox;

  // Font-Propertys
  private ObjectProperty<Color> sceneColorProperty = new SimpleObjectProperty<>(Color.BLUE);
  private ObjectProperty<Font> waitigFontSize;
  private ObjectProperty<Font> serverInfoHeaderFontSize;
  private ObjectProperty<Font> serverInfoCOntentFontSize;
  private ObjectProperty<Font> addHumanButtonTextFontSIze;
  private ObjectProperty<Font> serverInfoDescription;
  private ObjectProperty<Font> clipBoardInfoText;
  private ObjectProperty<Font> tableHeader;



  /**
   * Creates the scene with a StackPane as root container and starts the executor service which
   * always updates the current number of teams in the lobby
   * 
   * @author Manuel Krakowski
   */
  public WaitingScene(double width, double height) {
    super(new StackPane(), width, height);
    manageFontSizes();
    this.root = (StackPane) this.getRoot();
    try {
      this.getStylesheets()
          .add(Paths.get(Constants.toUIStyles + "ComboBox.css").toUri().toURL().toString());
      this.getStylesheets()
          .add(Paths.get(Constants.toUIStyles + "MapEditor.css").toUri().toURL().toString());
      this.getStylesheets()
          .add(Paths.get(Constants.toUIStyles + "color.css").toUri().toURL().toString());
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    createLayout();
    currentNumber = 0;
    scheduler = Executors.newScheduledThreadPool(1);
    scheduler.scheduleAtFixedRate(updateTask, 0, 1, TimeUnit.SECONDS);
  }


  /**
   * Creates the whole layout of the scene
   * 
   * @author Manuel Krakowski
   */
  private void createLayout() {
    root.getStyleClass().add("join-root");
    root.prefHeightProperty().bind(this.heightProperty());
    root.prefWidthProperty().bind(this.widthProperty());
    root.getChildren().add(createBackgroundImage(root));
    VBox mainBox = createMainBox(root);
    root.getChildren().add(mainBox);
    mainBox.getChildren().add(createHeader());
    HBox middle = createMiddleHBox(mainBox);
    VBox leftTop = createLeftVBox(middle);
    VBox rightTop = createRightVBox(middle);
    middle.getChildren().addAll(leftTop, rightTop);
    mainBox.getChildren().add(middle);
  }

  /**
   * Creates A background image which is placed behind the scene
   * 
   * @author Manuel Krakowski
   * @param configRoot Stackpane on which the image is placed
   * @return
   */
  private ImageView createBackgroundImage(StackPane configRoot) {
    Image mp = ImageController.loadThemedImage(ImageType.MISC, "waitingRoom2");
    ImageView mpv = new ImageView(mp);
    mpv.fitHeightProperty().bind(configRoot.heightProperty().divide(1));
    mpv.fitWidthProperty().bind(configRoot.widthProperty().divide(1));
    // mpv.setPreserveRatio(true);
    mpv.setOpacity(0.5);
    return mpv;
  }


  /**
   * fits the size of all the text on the scene to the screen-size
   * 
   * @author Manuel Krakowski
   */
  private void manageFontSizes() {
    waitigFontSize = new SimpleObjectProperty<Font>(Font.font(this.getWidth() / 60));
    serverInfoHeaderFontSize = new SimpleObjectProperty<Font>(Font.font(this.getWidth() / 100));
    serverInfoCOntentFontSize = new SimpleObjectProperty<Font>(Font.font(this.getWidth() / 68));
    addHumanButtonTextFontSIze = new SimpleObjectProperty<Font>(Font.font(this.getWidth() / 50));
    serverInfoDescription = new SimpleObjectProperty<Font>(Font.font(this.getWidth() / 50));
    clipBoardInfoText = new SimpleObjectProperty<Font>(Font.font(this.getWidth() / 60));
    tableHeader = new SimpleObjectProperty<Font>(Font.font(this.getWidth() / 55));
    widthProperty().addListener(new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth,
          Number newWidth) {
        waitigFontSize.set(Font.font(newWidth.doubleValue() / 60));
        serverInfoHeaderFontSize.set(Font.font(newWidth.doubleValue() / 100));
        serverInfoCOntentFontSize.set(Font.font(newWidth.doubleValue() / 68));
        addHumanButtonTextFontSIze.set(Font.font(newWidth.doubleValue() / 50));
        serverInfoDescription.set(Font.font(newWidth.doubleValue() / 50));
        clipBoardInfoText.set(Font.font(newWidth.doubleValue() / 60));
        tableHeader.set(Font.font(newWidth.doubleValue() / 55));
      }
    });
  }


  /**
   * Creates a Vbox which is used to devide the Scene into two patrs, one for the header and one for
   * the content
   * 
   * @author Manuel Krakowski
   * @param parent: Stackpane in which the Vbox is placed for relative resizing
   * @return Vbox
   */
  private VBox createMainBox(StackPane parent) {
    VBox mainBox = new VBox();
    mainBox.prefHeightProperty().bind(parent.heightProperty());
    mainBox.prefWidthProperty().bind(parent.widthProperty());
    mainBox.setAlignment(Pos.TOP_CENTER);
    mainBox.setSpacing(30);
    mainBox.widthProperty().addListener((observable, oldValue, newValue) -> {
      double newSpacing = newValue.doubleValue() * 0.02;
      double newPadding = newValue.doubleValue() * 0.04;
      mainBox.setSpacing(newSpacing);
      mainBox.setPadding(new Insets(0, 0, newPadding, 0));
    });
    return mainBox;
  }

  /**
   * Creates the upper part of the scene which includes just one Image with the Text: 'Lobby'
   * 
   * @author Manuel Krakowski
   * @return ImageView containing the word 'Lobby'
   */
  private ImageView createHeader() {
    Image mp = ImageController.loadThemedImage(ImageType.MISC, "waitingRoomHeader");
    ImageView mpv = new ImageView(mp);
    mpv.fitHeightProperty().bind(root.heightProperty().multiply(0.1));
    mpv.fitWidthProperty().bind(root.widthProperty().multiply(0.7));
    mpv.setPreserveRatio(true);
    return mpv;
  }


  /**
   * Creates a HBox which devides the middle part of the screen into two pats vertically
   * 
   * @author Manuel Krakowski
   * @param parent: main Vbox in which it is placed used for relaive resizing
   * @return seperator-Hbox
   */
  private HBox createMiddleHBox(VBox parent) {
    HBox sep = new HBox();
    // sep.setStyle("-fx-background-color: red");
    sep.prefHeightProperty().bind(parent.heightProperty().multiply(0.8));
    sep.prefWidthProperty().bind(parent.widthProperty());
    sep.setAlignment(Pos.CENTER);
    sep.widthProperty().addListener((observable, oldValue, newValue) -> {
      double newSpacing = newValue.doubleValue() * 0.05;
      sep.setSpacing(newSpacing);
    });
    return sep;
  }


  /**
   * Creates the left side of the screen containing a the waiting label and the table which shows
   * the current players
   * 
   * @author Manuel Krakowski
   * @param parent: used for relative resizing
   * @return
   */
  private VBox createLeftVBox(HBox parent) {
    leftBox = new VBox();
    leftBox.heightProperty().addListener((observable, oldValue, newValue) -> {
      double newSpacing = newValue.doubleValue() * 0.05;
      double newPadding = newValue.doubleValue() * 0.08;
      leftBox.setSpacing(newSpacing);
      leftBox.setPadding(new Insets(newPadding, 0, newSpacing, 0));
    });
    leftBox.setAlignment(Pos.CENTER);
    leftBox.prefWidthProperty().bind(parent.widthProperty().multiply(0.55));
    leftBox.prefHeightProperty().bind(parent.heightProperty().multiply(1));
    leftBox.getChildren().add(createTopCenter());
    leftBox.getChildren().add(createWholeTable(leftBox));
    leftBox.getChildren().add(createLeave());
    return leftBox;
  }

  /**
   * Creates the table which shows the players which are in the waiting-room
   * 
   * @author Manuel Krakowski
   * @param parent: used for relative resizing
   * @return
   */
  private VBox createWholeTable(VBox parent) {
    VBox v = new VBox();
    v.prefWidthProperty().bind(parent.widthProperty().multiply(1));
    v.prefHeightProperty().bind(parent.heightProperty().multiply(0.6));
    v.getChildren().add(createHeaderRow(v));
    v.getChildren().add(createScrollPane(v));
    return v;
  }

  /**
   * Box to ensure that the waiting animation is always centered
   * 
   * @author Manuel Krakowski
   * @return HBox
   */
  private HBox createTopCenter() {
    HBox captureLoadingLabel = new HBox();
    captureLoadingLabel.setAlignment(Pos.CENTER);
    captureLoadingLabel.prefWidthProperty().bind(this.widthProperty().multiply(0.5));
    captureLoadingLabel.getChildren().add(waitingBox());
    return captureLoadingLabel;
  }

  /**
   * Creates a VBox containing a label with an animation of 3 sequentially appearing and
   * disappearing
   * 
   * @author Manuel Krakowski
   * @return Vbox
   */
  private VBox waitingBox() {
    final Label status = new Label("Waiting for Players");
    status.getStyleClass().add("spinner-des-label");
    final Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, new EventHandler() {
      @Override
      public void handle(Event event) {
        String statusText = status.getText();
        status.setText(("Waiting for Players . . .".equals(statusText)) ? "Waiting for Players ."
            : statusText + " .");
      }
    }), new KeyFrame(Duration.millis(1000)));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
    VBox layout = new VBox();
    layout.prefWidthProperty().bind(this.widthProperty().multiply(0.17));
    status.fontProperty().bind(waitigFontSize);
    // layout.setStyle("-fx-background-color: blue");
    layout.getChildren().addAll(status);
    return layout;
  }



  /**
   * Creates the top-row with the column headers
   * 
   * @author Manuel Krakowski
   * @param parent
   * @return header-row
   */
  private HBox createHeaderRow(VBox parent) {
    HBox h = new HBox();
    h.getStyleClass().add("lobby-table-header");
    Label l = createHeaderLabel("Teamcolor", h);
    Label l2 = createHeaderLabel("Teamname", h);
    Label l3 = createHeaderLabel("Type", h);
    h.getChildren().addAll(l, l2, l3);
    return h;
  }

  /**
   * Creates a header-label for the table
   * 
   * @param : text of the label
   * @param h: parent used for relative resizing
   * @return header-label
   */
  private Label createHeaderLabel(String text, HBox h) {
    Label l = new Label(text);
    l.getStyleClass().add("lobby-header-label");
    l.setAlignment(Pos.CENTER);
    l.prefWidthProperty().bind(h.widthProperty().divide(2.9));
    l.fontProperty().bind(tableHeader);
    return l;
  }

  /**
   * Creates the Content of the table with all the players currently in the waiting room
   * 
   * @author Manuel Krakowski
   * @param parent: used for relative resizing
   * @return Scrollpane with current players
   */
  private ScrollPane createScrollPane(VBox parent) {
    ScrollPane scroller = new ScrollPane();
    scroller.getStyleClass().clear();
    scroller.prefWidthProperty().bind(parent.widthProperty());
    scroller.setHbarPolicy(ScrollBarPolicy.NEVER);
    VBox content = new VBox();
    for (int i = 0; i < CreateGameController.getMaxNumberofTeams(); i++) {
      HBox oneRow = new HBox();
      oneRow.prefHeightProperty().bind(this.heightProperty().multiply(0.1));
      oneRow.getStyleClass().add("lobby-table-header");
      oneRow.prefWidthProperty().bind(parent.widthProperty());
      HBox colorBox = new HBox();
      colors.put(i, colorBox);
      Label defaultColorLabel = new Label("?");
      defaultColorLabel.setAlignment(Pos.CENTER);
      defaultColorLabel.prefWidthProperty().bind(colorBox.widthProperty());
      defaultColorLabel.prefHeightProperty().bind(colorBox.heightProperty());
      if ((i % 2 == 0)) {
        defaultColorLabel.getStyleClass().add("lobby-normal-label");
        colorBox.setStyle("-fx-background-color:  #475865");
      } else {
        defaultColorLabel.getStyleClass().add("lobby-normal-label-2");
        colorBox.setStyle("-fx-background-color: grey");
      }
      defaultColorLabel.fontProperty().bind(tableHeader);
      colorBox.setAlignment(Pos.CENTER);
      colorBox.prefWidthProperty().bind(oneRow.widthProperty().divide(2.9));
      colorBox.getChildren().add(defaultColorLabel);
      Label l2 = createNormalLabel("?", oneRow, i);
      teamNames.put(i, l2);
      l2.prefHeightProperty().bind(this.heightProperty().multiply(0.1));
      Label l3 = createNormalLabel("?", oneRow, i);
      teamTypes.put(i, l3);
      l3.prefHeightProperty().bind(this.heightProperty().multiply(0.1));
      oneRow.getChildren().addAll(colorBox, l2, l3);
      content.getChildren().add(oneRow);
    }
    scroller.setContent(content);
    return scroller;
  }


  /**
   * Creates a colored rectangle which indicates the team-color of one team and sets a tooltip to it
   * 
   * @author Manuel Krakowski
   * @param colorBox: parent used for relative resizing
   * @param i: Number of the team the color belongs to
   * @return color-rectangle
   */
  private Rectangle createColorRec(HBox colorBox, int i) {
    Rectangle r = new Rectangle();
    r.setFill(Color.RED);
    r.widthProperty().bind(Bindings.divide(colorBox.widthProperty(), 2.5));
    r.heightProperty().bind(Bindings.divide(colorBox.heightProperty(), 2));
    r.setOnMouseClicked(new EventHandler<MouseEvent>() {
      public void handle(MouseEvent e) {
        showColorChooser(e, i);
      }
    });
    Tooltip tooltip = new Tooltip("Select Team Color by clicking");
    Duration delay = new Duration(1);
    tooltip.setShowDelay(delay);
    Duration displayTime = new Duration(10000);
    tooltip.setShowDuration(displayTime);
    tooltip.setFont(new Font(15));
    r.setPickOnBounds(true);
    Tooltip.install(r, tooltip);
    r.fillProperty().bind(CreateGameController.getColors().get(String.valueOf(i)));
    return r;
  }

  /**
   * Creates a color-chooser which can be used by the user to change the color of the different
   * teams The selected color will be used as team-coler when the game starts
   * 
   * @author Manuel Krakowski
   * @param e: MouseEvent causing the activation of the popup
   * @param i: team-number the color belongs to
   */
  public void showColorChooser(MouseEvent e, int i) {
    MyCustomColorPicker myCustomColorPicker = new MyCustomColorPicker();
    myCustomColorPicker.setCurrentColor(sceneColorProperty.get());
    CustomMenuItem itemColor = new CustomMenuItem(myCustomColorPicker);
    itemColor.setHideOnClick(false);
    CreateGameController.getColors().get(String.valueOf(i))
        .bind(myCustomColorPicker.customColorProperty());
    ContextMenu contextMenu = new ContextMenu(itemColor);
    contextMenu.setOnHiding(t -> sceneColorProperty.unbind());
    contextMenu.show(this.getWindow(), e.getScreenX(), e.getScreenY());
  }



  /**
   * Creates a normal label to display the content in the table
   * 
   * @author Manuel Krakowski
   * @param text: String that is displayed by the label
   * @param h: parent used for relative resizing
   * @param i: number of the team the label belong to
   * @return: Label
   */
  private Label createNormalLabel(String text, HBox h, int i) {
    Label l = new Label(text);
    l.setAlignment(Pos.CENTER);
    if ((i % 2) == 0) {
      l.getStyleClass().add("lobby-normal-label");
    } else {
      l.getStyleClass().add("lobby-normal-label-2");
    }

    l.prefWidthProperty().bind(h.widthProperty().divide(2.9));
    // l.setStyle("-fx-border-color:black");
    l.fontProperty().bind(tableHeader);
    return l;
  }


  /**
   * Creates the right side of the screen including server-infos and local players-buttons
   * 
   * @author Manuel Krakowski
   * @param parent: used for relative resizing
   * @return vbox: top container of the right side
   */
  private VBox createRightVBox(HBox parent) {
    VBox rightBox = new VBox();
    rightBox.getStyleClass().add("option-pane");
    rightBox.setAlignment(Pos.TOP_CENTER);
    rightBox.widthProperty().addListener((observable, oldValue, newValue) -> {
      double newPadding = newValue.doubleValue() * 0.04;
      double newSpacing = newValue.doubleValue() * 0.07;
      rightBox.setPadding(new Insets(newPadding));
      rightBox.setSpacing(newSpacing);
    });
    rightBox.prefWidthProperty().bind(parent.widthProperty().multiply(0.35));
    rightBox.maxHeightProperty().bind(parent.heightProperty().multiply(0.65));
    rightBox.getChildren().add(createSeverInfoBox(rightBox));
    rightBox.getChildren().add(createAddButtons(rightBox));
    return rightBox;
  }

  /**
   * Creates the upper part of the right side with all the server-information
   * 
   * @author Manuel Krakowski
   * @param parent: used for relative resizing
   * @return Vbox with all the server information
   */
  private VBox createSeverInfoBox(VBox parent) {
    VBox serverInfoBox = new VBox();
    serverInfoBox.prefWidthProperty().bind(parent.widthProperty());
    serverInfoBox.prefHeightProperty().bind(parent.heightProperty().multiply(0.5));
    serverInfoBox.setAlignment(Pos.CENTER);
    serverInfoBox.widthProperty().addListener((observable, oldValue, newValue) -> {
      double newSpacing = newValue.doubleValue() * 0.05;
      serverInfoBox.setSpacing(newSpacing);
    });
    serverInfoBox.getChildren().add(createGeneralDescription(serverInfoBox, "Server Information"));
    serverInfoBox.getChildren().add(
        createInfoLabel(serverInfoBox, "Session-ID", CreateGameController.getSessionID(), 0.8));

    HBox dividelowerPart = new HBox();
    dividelowerPart.widthProperty().addListener((observable, oldValue, newValue) -> {
      double newSpacing = newValue.doubleValue() * 0.05;
      dividelowerPart.setSpacing(newSpacing);
    });
    dividelowerPart.getChildren()
        .add(createInfoLabel(parent, "port", CreateGameController.getPort(), 0.35));
    dividelowerPart.getChildren()
        .add(createInfoLabel(parent, "Server-IP", CreateGameController.getServerIP(), 0.55));
    serverInfoBox.getChildren().add(dividelowerPart);
    serverInfoBox.getChildren().add(createShowClipBoardInfoStackPane(serverInfoBox));
    return serverInfoBox;
  }


  /**
   * Creates a Label which states which server information is shown in a box
   * 
   * @author Manuel Krakowski
   * @param parent: used for relative resizing
   * @param text: Text that is shown by the label
   * @return
   */
  private Label createGeneralDescription(VBox parent, String text) {
    Label l = new Label(text);
    l.getStyleClass().add("aiConfig-label");
    l.setAlignment(Pos.CENTER);
    l.fontProperty().bind(serverInfoDescription);
    l.prefWidthProperty().bind(parent.widthProperty().multiply(0.7));
    return l;
  }

  /**
   * Creates a info Label to show server information, shows animation when it is clicked
   * 
   * @author Manuel Krakowski
   * @param parent: used for relative resizing
   * @param header: describes the text that is shown in the box
   * @param content: server information
   * @param relWidth: rel-width to the parent
   * @return
   */
  private VBox createInfoLabel(VBox parent, String header, String content, double relWidth) {
    VBox labelBox = new VBox();
    labelBox.prefWidthProperty().bind(parent.widthProperty().multiply(relWidth));
    labelBox.getStyleClass().add("info-vbox");
    Label headerLabel = new Label(header);
    headerLabel.fontProperty().bind(serverInfoHeaderFontSize);
    headerLabel.getStyleClass().add("des-label");
    Label numberLabel = new Label(content);
    numberLabel.getStyleClass().add("number-label");
    numberLabel.fontProperty().bind(serverInfoCOntentFontSize);
    labelBox.setOnMouseClicked(event -> {
      System.out.println(content);
      copyTextToClipBoard(content);
      showClipInfo(header);
      FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), labelBox);
      fadeTransition.setFromValue(1.0);
      fadeTransition.setToValue(0.0);
      fadeTransition.setOnFinished(e -> {
      });

      fadeTransition.setFromValue(0.0);
      fadeTransition.setToValue(1.0);
      fadeTransition.play();
    });
    labelBox.getChildren().addAll(headerLabel, numberLabel);
    return labelBox;
  }


  /**
   * Creates a Stackpane with a label that is used to inform the user that the text was successfully
   * copied to the clipboard
   * 
   * @author Manuel Krakowski
   * @param parent
   * @return
   */
  private StackPane createShowClipBoardInfoStackPane(VBox parent) {
    StackPane displayClipBoardText = new StackPane();
    displayClipBoardText.prefWidthProperty().bind(parent.widthProperty());
    displayClipBoardText.setAlignment(Pos.CENTER);
    clipboardInfo = new Label("");
    clipboardInfo.getStyleClass().add("des-label");
    clipboardInfo.fontProperty().bind(clipBoardInfoText);
    displayClipBoardText.getChildren().add(clipboardInfo);
    return displayClipBoardText;
  }

  /**
   * Copies text to the clipboard
   * 
   * @author Manuel Krakowski
   * @param text: text that is copied to the clipboard
   */
  private void copyTextToClipBoard(String text) {
    Clipboard clipboard = Clipboard.getSystemClipboard();
    ClipboardContent content = new ClipboardContent();
    content.putString(text);
    clipboard.setContent(content);
  }

  /**
   * Shows an animation when informaton is successfully copied to the clipboard by the user
   * 
   * @author Manuel Krakowski
   * @param copyText: Text that was successfully copied
   */
  private void showClipInfo(String copyText) {
    clipboardInfo.setText("Copied " + copyText + " to clipboard");
    FadeTransition fade = new FadeTransition(Duration.seconds(1), clipboardInfo);
    fade.setDelay(Duration.seconds(1));
    fade.setFromValue(1.0);
    fade.setToValue(0.0);
    fade.setOnFinished(event -> {
      clipboardInfo.setText("");
      clipboardInfo.setOpacity(1);
    });
    fade.play();
  }


  /**
   * Creates the button that is used to add a local human-client
   * 
   * @author Manuel Krakowski
   * @param text: text displayed on the button
   * @return add-human-button
   */
  private Button createAddHumanButton(String text) {
    Button button = new Button(text);
    button.getStyleClass().add("button25");
    button.fontProperty().bind(addHumanButtonTextFontSIze);
    Image mp = ImageController.loadThemedImage(ImageType.MISC, "humanForButton");
    ImageView vw = new ImageView(mp);
    button.setGraphic(vw);
    button.setContentDisplay(ContentDisplay.RIGHT);
    vw.fitWidthProperty().bind(button.widthProperty().divide(8));
    vw.setPreserveRatio(true);
    button.setOnAction(e -> {
      PopUpCreatorEnterTeamName popi =
          new PopUpCreatorEnterTeamName(this, root, null, false, false);
      popi.createEnterNamePopUp();
    });
    button.setMaxWidth(Double.MAX_VALUE);
    return button;
  }

  /**
   * Creates the button that is used to add a local ai-client
   * 
   * @author Manuel Krakowski
   * @param text: text displayed on the button
   * @return: add-ai-button
   */
  private Button createAddAIButton(String text) {
    Button button = new Button(text);
    button.getStyleClass().add("button25");
    button.fontProperty().bind(addHumanButtonTextFontSIze);
    Image mp = ImageController.loadThemedImage(ImageType.MISC, "robotForButton");
    ImageView vw = new ImageView(mp);
    button.setGraphic(vw);
    button.setContentDisplay(ContentDisplay.RIGHT);
    vw.fitWidthProperty().bind(button.widthProperty().divide(8));
    vw.setPreserveRatio(true);
    button.setOnAction(e -> {
      PopUpCreator aiPopCreator = new PopUpCreator(this, root);
      aiPopCreator.createAiLevelPopUp(null, null, null);
    });
    button.setMaxWidth(Double.MAX_VALUE);
    return button;
  }


  /**
   * Creates the box with the buttons to add local-players
   * 
   * @author Manuel Krakowski
   * @param parent: used for relative resizing
   * @return buttonbox
   */
  private VBox createAddButtons(VBox parent) {
    VBox v = new VBox();
    // v.setStyle("-fx-background-color: blue");
    v.setAlignment(Pos.TOP_CENTER);
    v.heightProperty().addListener((obs, oldVal, newVal) -> {
      double spacing = newVal.doubleValue() * 0.08;
      v.setSpacing(spacing);
    });
    v.prefWidthProperty().bind(parent.widthProperty().multiply(0.6));
    Button k = createAddHumanButton("add Human-Player");
    Button b = createAddAIButton("add Bot");

    v.getChildren().add(createGeneralDescription(v, "Add local players"));
    v.getChildren().add(b);
    v.getChildren().add(k);
    return v;
  }

  /**
   * Creates the leave-button on the bottom-part of the screen
   * 
   * @author Manuel Krakowski
   * @return leave-button
   */
  private Button createLeave() {
    Button exit = new Button("Leave");
    exit.getStyleClass().add("leave-button");
    exit.fontProperty().bind(serverInfoCOntentFontSize);
    exit.prefWidthProperty().bind(root.widthProperty().multiply(0.1));
    exit.prefHeightProperty().bind(exit.widthProperty().multiply(0.35));
    exit.setOnAction(e -> {
      ClientStorage.getMainClient().shutdown();
      ClientStorage.clearAllClients();
      ClientStorage.setMainClient(null);
      scheduler.shutdown();
      CreateGameController.deleteGame();
      CreateGameController.clearUsedNames();
      CreateGameController.clearColors();
      SceneHandler.switchToHomeScreen();
    });
    return exit;
  }

  /**
   * Task that regulary checks if the current number of teams in the session has changed If the game
   * has started on the server the scheduler is shutdown and it switched to the play game screen if
   * the game hasnt started the team-number is updated in the scene
   * 
   * @author Manuel Krakowski
   */
  Runnable updateTask = () -> {
    try {
      if (ClientStorage.getMainClient() != null) {
        if (ClientStorage.getMainClient().getStartDate() != null) {
          scheduler.shutdown();
          Platform.runLater(() -> {
            SceneHandler.switchToPlayGameScene(false);
          });
        } else {
          if (CreateGameController.getServerManager().getCurrentNumberofTeams() != currentNumber) {
            currentNumber = CreateGameController.getServerManager().getCurrentNumberofTeams();

            Platform.runLater(() -> {
              this.setCUrrentTeams(currentNumber);
            });
          }
        }
      }
    } catch (Exception e) {

    }
  };

  /**
   * Method that is called when the number of teams in the session has changed and the game hasn't
   * started Updates the table with team-name,type and color of the team that is new in the session
   * 
   * @author Manuel Krakowski
   * @param current number of teams
   */
  public void setCUrrentTeams(int i) {
    // System.out.println(i-1);
    if ((i - 1) >= 0) {
      HBox toAdd = colors.get(i - 1);
      toAdd.getChildren().clear();
      toAdd.getChildren().add(createColorRec(toAdd, i - 1));
      String text = "";
      if (CreateGameController.getLasttype().equals("HUMAN")) {
        text = "local Human";
        teamNames.get(i - 1).setText(CreateGameController.getLastTeamName());
      } else if (CreateGameController.getLasttype().equals("AI")) {
        text = "AI (" + CreateGameController.getLastAitype().name() + ")";
        teamNames.get(i - 1).setText(CreateGameController.getLastTeamName());
      } else if (CreateGameController.getLasttype().equals("UNKNOWN")) {
        text = "Remote Player";
        if (CreateGameController.getServerManager().getGameStateFromSession().getTeams()[i
            - 1] != null) {
          teamNames.get(i - 1).setText(
              CreateGameController.getServerManager().getGameStateFromSession().getTeams()[i - 1]
                  .getId());
        } else {
          teamNames.get(i - 1).setText(String.valueOf(i));
        }
      }
      CreateGameController.setLasttype("UNKNOWN");
      if (i - 1 == 0) {
        text += " (You)";
      }
      teamTypes.get(i - 1).setText(text);

    }
  }
}
