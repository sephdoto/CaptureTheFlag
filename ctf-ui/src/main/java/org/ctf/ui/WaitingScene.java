package org.ctf.ui;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
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
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.util.Duration;

import org.ctf.shared.client.lib.ServerManager;
import org.ctf.ui.customobjects.BaseRep;
import org.ctf.ui.customobjects.CostumFigurePain;

import ch.qos.logback.core.net.SyslogOutputStream;

public class WaitingScene extends Scene {
  // Executor Service and data which is changed by it
  private ScheduledExecutorService scheduler;
  private int currentNumber;
  
  //When a new team joines the corresponding Boxes need to be filled with a color, team-name and type team-id -> corresponding box
  private HashMap<Integer, Label> teamNames = new HashMap<Integer, Label>();
  private HashMap<Integer, Label> teamTypes = new HashMap<Integer, Label>();
  private HashMap<Integer, HBox> colors = new HashMap<Integer, HBox>();
  
  //Controller which is used to switch to the play-game-scene
  private HomeSceneController hsc;
  
  //Containers and Labels which need to be accessed from different methods
  private StackPane root;
  private Label curenntTeams;
  private Label clipboardInfo;
  private VBox leftBox;
 
  
  private ObjectProperty<Color> sceneColorProperty = new SimpleObjectProperty<>(Color.BLUE);
  private ObjectProperty<Font> waitigFontSize = new SimpleObjectProperty<Font>(Font.getDefault());
  private ObjectProperty<Font> serverInfoHeaderFontSize =
      new SimpleObjectProperty<Font>(Font.getDefault());
  private ObjectProperty<Font> serverInfoCOntentFontSize =
      new SimpleObjectProperty<Font>(Font.getDefault());
  private ObjectProperty<Font> addHumanButtonTextFontSIze =
      new SimpleObjectProperty<Font>(Font.getDefault());
  private ObjectProperty<Font> serverInfoDescription =
      new SimpleObjectProperty<Font>(Font.getDefault());
  private ObjectProperty<Font> clipBoardInfoText =
      new SimpleObjectProperty<Font>(Font.getDefault());
  private ObjectProperty<Font> tableHeader = new SimpleObjectProperty<Font>(Font.getDefault());

	Runnable updateTask = () -> {
		try {
			if (CreateGameController.getMainClient() != null) {
				if (CreateGameController.getMainClient().getStartDate() != null) {
					scheduler.shutdown();
					Platform.runLater(() -> {
						 hsc.switchToPlayGameScene(App.getStage(), CreateGameController.getMainClient(), false);
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
	 * Creates the scene with a StackPane as root container and starts the executor service which always updates the current number of teams in the lobby
	 *@author Manuel Krakowski
	 */
  public WaitingScene(HomeSceneController hsc, double width, double height) {
    super(new StackPane(), width, height);
    this.hsc = hsc;
    manageFontSizes();
    this.getStylesheets().add(getClass().getResource("MapEditor.css").toExternalForm());
    this.root = (StackPane) this.getRoot();
    this.getStylesheets().add(getClass().getResource("ComboBox.css").toExternalForm());
    this.getStylesheets().add(getClass().getResource("color.css").toExternalForm());
    createLayout();
    currentNumber = 0;
	scheduler = Executors.newScheduledThreadPool(1);
	scheduler.scheduleAtFixedRate(updateTask, 0, 1, TimeUnit.SECONDS);
  }
  
  
  /**
   * Creates the whole layout of the scene
   * @author Manuel Krakowski
   */
  private void createLayout() {
    root.getStyleClass().add("join-root");
    root.prefHeightProperty().bind(this.heightProperty());
    root.prefWidthProperty().bind(this.widthProperty());
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
   * fits the size of all the text on the scene to the screen-size
   * @author Manuel Krakowski
   */
  private void manageFontSizes() {
    widthProperty()
        .addListener(
            new ChangeListener<Number>() {
              public void changed(
                  ObservableValue<? extends Number> observableValue,
                  Number oldWidth,
                  Number newWidth) {
                waitigFontSize.set(Font.font(newWidth.doubleValue() / 60));
                serverInfoHeaderFontSize.set(Font.font(newWidth.doubleValue() / 100));
                serverInfoCOntentFontSize.set(Font.font(newWidth.doubleValue() / 68));
                addHumanButtonTextFontSIze.set(Font.font(newWidth.doubleValue() / 50));
                serverInfoDescription.set(Font.font(newWidth.doubleValue() / 50));
                clipBoardInfoText.set(Font.font(newWidth.doubleValue() / 60));
                tableHeader.set(Font.font(newWidth.doubleValue() / 50));
              }
            });
  }
  

  /**
   * Creates a Vbox which is used to devide the Scene into two patrs, one for the header and one for the content
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
    mainBox
        .widthProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              double newSpacing = newValue.doubleValue() * 0.02;
              double newPadding = newValue.doubleValue()*0.04;
              mainBox.setSpacing(newSpacing);
              mainBox.setPadding(new Insets(0,0, newPadding, 0));
            });
    return mainBox;
  }

  /**
   * Creates the upper part of the scene which includes just one Image with the Text: 'Lobby'
   * @author Manuel Krakowski
   * @return ImageView containing the word 'Lobby'
   */
  private ImageView createHeader() {
    Image mp = new Image(getClass().getResourceAsStream("multiplayerlogo.png"));
    ImageView mpv = new ImageView(mp);
    mpv.fitHeightProperty().bind(root.heightProperty().multiply(0.1));
    mpv.fitWidthProperty().bind(root.widthProperty().multiply(0.5));
    mpv.setPreserveRatio(true);
    return mpv;
  }

  
  /**
   * Creates a HBox which devides the middle part of the screen into two pats vertically
   * @author Manuel Krakowski
   * @param parent: main Vbox in which it is placed used for relaive resizing
   * @return seperator-Hbox
   */
  private HBox createMiddleHBox(VBox parent) {
    HBox sep = new HBox();
    // sep.setStyle("-fx-background-color: red");
    sep.prefHeightProperty().bind(parent.heightProperty().multiply(0.9));
    sep.prefWidthProperty().bind(parent.widthProperty());
    sep.setAlignment(Pos.CENTER);
    sep.widthProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              double newSpacing = newValue.doubleValue() * 0.05;
              sep.setSpacing(newSpacing);
            });
    return sep;
  }

  
  
  private VBox createLeftVBox(HBox parent) {
    leftBox = new VBox();
    leftBox.heightProperty()
    .addListener(
        (observable, oldValue, newValue) -> {
          double newSpacing = newValue.doubleValue() * 0.05;
          double newPadding = newValue.doubleValue() * 0.08;
          leftBox.setSpacing(newSpacing);
          leftBox.setPadding(new Insets(newPadding, 0, newSpacing, 0));
        });
    leftBox.setAlignment(Pos.CENTER);
    leftBox.prefWidthProperty().bind(parent.widthProperty().multiply(0.55));
    leftBox.prefHeightProperty().bind(parent.heightProperty().multiply(1));
    //leftBox.getChildren().add(createTestLabel(leftBox));
   // leftBox.getChildren().add(createTestLabel2(leftBox));
    leftBox.getChildren().add(createTopCenter());
   leftBox.getChildren().add(createWholeTable(leftBox));
   leftBox.getChildren().add(createLeave());
    return leftBox;
  }
  
  private VBox createWholeTable(VBox parent) {
	  VBox v = new VBox();
	  v.prefWidthProperty().bind(parent.widthProperty().multiply(1));
	  v.prefHeightProperty().bind(parent.heightProperty().multiply(1));
	  v.getChildren().add(createHeaderRow(v));
	  v.getChildren().add(createScrollPane(v));
	  return v;
  }

  private ScrollPane createScrollPane(VBox parent) {
    ScrollPane scroller = new ScrollPane();
    scroller.getStyleClass().clear();
    //scroller.getStyleClass().add("scroll-pane");
   // scroller.prefHeightProperty().bind(parent.heightProperty().multiply(0.75));
    scroller.prefWidthProperty().bind(parent.widthProperty());
    scroller.setHbarPolicy(ScrollBarPolicy.NEVER);
    
    VBox content = new VBox();
    for (int i = 0; i < CreateGameController.getMaxNumberofTeams(); i++) {
      //content.getChildren().add(createNormalRow(scroller));
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
    	    if((i%2 == 0)) {
    	    	defaultColorLabel.getStyleClass().add("lobby-normal-label");
    	    	colorBox.setStyle("-fx-background-color:  #475865");
    	    }else {
    	    	defaultColorLabel.getStyleClass().add("lobby-normal-label-2");
    	    	colorBox.setStyle("-fx-background-color: grey");

    	    }
    	    defaultColorLabel.fontProperty().bind(tableHeader);
    	    colorBox.setAlignment(Pos.CENTER);
    	    //colorBox.setStyle("-fx-border-color: black");
    	    colorBox.prefWidthProperty().bind(oneRow.widthProperty().divide(2.9));
    	    colorBox.getChildren().add(defaultColorLabel);
    	    Label l2 = createNormalLabel("?", oneRow,i);
    	    teamNames.put(i, l2);
    	    l2.prefHeightProperty().bind(this.heightProperty().multiply(0.1));
    	    Label l3 = createNormalLabel("?", oneRow,i);
    	    teamTypes.put(i, l3);
    	    l3.prefHeightProperty().bind(this.heightProperty().multiply(0.1));
    	    oneRow.getChildren().addAll(colorBox, l2, l3);
    	    content.getChildren().add(oneRow);
    }
    scroller.setContent(content);
    return scroller;
  }
  
  private HBox createCountdownBox() {
	  HBox h = new HBox();
	  Label l = new Label("Hey");
	  h.getChildren().add(l);
	  return h;
	
}
  
  private Rectangle createColorRec( HBox colorBox, int i) {

	    Rectangle r = new Rectangle();
	    r.setFill(Color.RED);
	    r.widthProperty().bind(Bindings.divide(colorBox.widthProperty(), 2.5));
	    r.heightProperty().bind(Bindings.divide(colorBox.heightProperty(), 2));
	    r.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				showColorChooser(e, i);
			}
		});
	    //InfoPaneCreator.addInfoPane(r, hsc.getStage(), "Select Team Color by clicking", InfoPaneCreator.RIGHT);
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
  
  public void showColorChooser(MouseEvent e, int i) {
	  MyCustomColorPicker myCustomColorPicker = new MyCustomColorPicker();
      myCustomColorPicker.setCurrentColor(sceneColorProperty.get());
      CustomMenuItem itemColor = new CustomMenuItem(myCustomColorPicker);
      itemColor.setHideOnClick(false);
      //sceneColorProperty.bind(myCustomColorPicker.customColorProperty());
      CreateGameController.getColors().get(String.valueOf(i)).bind(myCustomColorPicker.customColorProperty());
      ContextMenu contextMenu = new ContextMenu(itemColor);
      contextMenu.setOnHiding(t->sceneColorProperty.unbind());
      contextMenu.show(this.getWindow(),e.getScreenX(),e.getScreenY());
}

  private HBox createHeaderRow(VBox parent) {
    HBox h = new HBox();
    h.getStyleClass().add("lobby-table-header");
    Label l = createHeaderLabel("Teamcolor", h);
    Label l2 = createHeaderLabel("Teamname", h);
    Label l3 = createHeaderLabel("Type", h);
    h.getChildren().addAll(l, l2, l3);
    return h;
  }

  private Label createHeaderLabel(String text, HBox h) {
    Label l = new Label(text);
    l.getStyleClass().add("lobby-header-label");
    l.setAlignment(Pos.CENTER);
    l.prefWidthProperty().bind(h.widthProperty().divide(2.9));
   // l.setStyle("-fx-border-color:black");
    l.fontProperty().bind(tableHeader);
    return l;
  }
  
  private Label createNormalLabel(String text, HBox h,int i) {
	    Label l = new Label(text);
	    l.setAlignment(Pos.CENTER);
	    if((i % 2) == 0) {
	    	 l.getStyleClass().add("lobby-normal-label");
	    }else {
	    	l.getStyleClass().add("lobby-normal-label-2");
	    }
	   
	    l.prefWidthProperty().bind(h.widthProperty().divide(2.9));
	   // l.setStyle("-fx-border-color:black");
	    l.fontProperty().bind(tableHeader);
	    return l;
	  }

  private HBox createNormalRow(ScrollPane parent) {
    HBox oneRow = new HBox();
    oneRow.prefHeightProperty().bind(this.heightProperty().multiply(0.1));
    oneRow.prefWidthProperty().bind(parent.widthProperty());
    HBox colorBox = new HBox();
    Pane colorRec = new Pane();
    colorRec.setStyle("-fx-background-color: blue");
    colorRec.prefWidthProperty().bind(Bindings.divide(colorBox.widthProperty(), 2.5));
    colorRec.maxHeightProperty().bind(Bindings.divide(colorBox.heightProperty(), 2));
    colorBox.setAlignment(Pos.CENTER);
    colorBox.setStyle("-fx-border-color: black");
    colorBox.prefWidthProperty().bind(oneRow.widthProperty().divide(3));
    colorBox.getChildren().add(colorRec);
    Label l2 = createHeaderLabel("Team", oneRow);
    l2.prefHeightProperty().bind(this.heightProperty().multiply(0.1));
    Label l3 = createHeaderLabel("Type", oneRow);
    l3.prefHeightProperty().bind(this.heightProperty().multiply(0.1));
    oneRow.getChildren().addAll(colorBox, l2, l3);
    return oneRow;
  }

  private VBox createRightVBox(HBox parent) {
    VBox rightBox = new VBox();
    //rightBox.setStyle("-fx-background-color: yellow");
    rightBox.getStyleClass().add("option-pane");
    rightBox.setAlignment(Pos.TOP_CENTER);
    rightBox
        .widthProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
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

  private VBox createSeverInfoBox(VBox parent) {
    VBox serverInfoBox = new VBox();
    serverInfoBox.prefWidthProperty().bind(parent.widthProperty());
    serverInfoBox.prefHeightProperty().bind(parent.heightProperty().multiply(0.5));
   // serverInfoBox.setStyle("-fx-background-color: blue");
    serverInfoBox.setAlignment(Pos.CENTER);
    serverInfoBox
        .widthProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              double newSpacing = newValue.doubleValue() * 0.05;
              serverInfoBox.setSpacing(newSpacing);
            });
    serverInfoBox.getChildren().add(createGeneralDescription(serverInfoBox, "Server Information"));
    serverInfoBox
        .getChildren()
        .add(
            createInfoLabel(serverInfoBox, "Session-ID", CreateGameController.getSessionID(), 0.8));

    HBox dividelowerPart = new HBox();
    dividelowerPart
        .widthProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              double newSpacing = newValue.doubleValue() * 0.05;
              dividelowerPart.setSpacing(newSpacing);
            });
    dividelowerPart
        .getChildren()
        .add(createInfoLabel(parent, "port", CreateGameController.getPort(), 0.35));
    dividelowerPart
        .getChildren()
        .add(createInfoLabel(parent, "Server-IP", CreateGameController.getServerIP(), 0.55));
    serverInfoBox.getChildren().add(dividelowerPart);
    serverInfoBox.getChildren().add(createShowClipBoardInfoStackPane(serverInfoBox));
    return serverInfoBox;
  }

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

  private void copyTextToClipBoard(String text) {
    Clipboard clipboard = Clipboard.getSystemClipboard();
    ClipboardContent content = new ClipboardContent();
    content.putString(text);
    clipboard.setContent(content);
  }
  

  private void showClipInfo(String copyText) {
    clipboardInfo.setText("Copied " + copyText + " to clipboard");
    FadeTransition fade = new FadeTransition(Duration.seconds(1), clipboardInfo);
    fade.setDelay(Duration.seconds(1));
    fade.setFromValue(1.0);
    fade.setToValue(0.0);
    fade.setOnFinished(
        event -> {
          clipboardInfo.setText("");
          clipboardInfo.setOpacity(1);
        });
    fade.play();
  }

  private Label createGeneralDescription(VBox parent, String text) {
    Label l = new Label(text);
    l.getStyleClass().add("aiConfig-label");
    l.setAlignment(Pos.CENTER);
    l.fontProperty().bind(serverInfoDescription);
    l.prefWidthProperty().bind(parent.widthProperty().multiply(0.7));
    return l;
  }

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
    labelBox.setOnMouseClicked(
        event -> {
          System.out.println(content);
          copyTextToClipBoard(content);
          showClipInfo(header);
          FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), labelBox);
          fadeTransition.setFromValue(1.0);
          fadeTransition.setToValue(0.0);
          fadeTransition.setOnFinished(e -> {});

          fadeTransition.setFromValue(0.0);
          fadeTransition.setToValue(1.0);
          fadeTransition.play();
        });
    labelBox.getChildren().addAll(headerLabel, numberLabel);
    return labelBox;
  }

  private VBox createAddButtons(VBox parent) {
    VBox v = new VBox();
    // v.setStyle("-fx-background-color: blue");
    v.setAlignment(Pos.TOP_CENTER);
    v.heightProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              double spacing = newVal.doubleValue() * 0.08;
              v.setSpacing(spacing);
            });
    v.prefWidthProperty().bind(parent.widthProperty().multiply(0.6));
    Button k = createAddHumanButton("add Human-Player", "user-286.png");
    Button b = createAddAIButton("add Bot", "robot1.png");

    v.getChildren().add(createGeneralDescription(v, "Add local players"));
    v.getChildren().add(b);
    v.getChildren().add(k);
    return v;
  }

  private Button createAddHumanButton(String text, String src) {
    Button button = new Button(text);
    button.getStyleClass().add("button25");
    button.fontProperty().bind(addHumanButtonTextFontSIze);
    Image mp = new Image(getClass().getResourceAsStream(src));
    ImageView vw = new ImageView(mp);
    button.setGraphic(vw);
    button.setContentDisplay(ContentDisplay.RIGHT);
    vw.fitWidthProperty().bind(button.widthProperty().divide(8));
    vw.setPreserveRatio(true);
    button.setOnAction(e -> {
		PopUpCreatorEnterTeamName popi = new PopUpCreatorEnterTeamName(this, root, null, hsc,false,false);
		popi.createEnterNamePopUp();
	});
    button.setMaxWidth(Double.MAX_VALUE);
    return button;
  }

  private Button createAddAIButton(String text, String src) {
    Button button = new Button(text);
    button.getStyleClass().add("button25");
    button.fontProperty().bind(addHumanButtonTextFontSIze);
    Image mp = new Image(getClass().getResourceAsStream(src));
    ImageView vw = new ImageView(mp);
    button.setGraphic(vw);
    button.setContentDisplay(ContentDisplay.RIGHT);
    vw.fitWidthProperty().bind(button.widthProperty().divide(8));
    vw.setPreserveRatio(true);
    button.setOnAction(e -> {
		PopUpCreator aiPopCreator = new PopUpCreator(this, root, hsc);
		aiPopCreator.createAiLevelPopUp(null, null, null);
	});
    button.setMaxWidth(Double.MAX_VALUE);
    return button;
  }

  private Label createTestLabel(VBox parent) {
    Label test = new Label(" max teams:" + CreateGameController.getMaxNumberofTeams());
    test.prefWidthProperty().bind(parent.widthProperty());
    return test;
  }

  private Label createTestLabel2(VBox parent) {
    curenntTeams = new Label(" current teams: 1");
    curenntTeams.prefWidthProperty().bind(parent.widthProperty());
    return curenntTeams;
  }

  public void updateData() {
   // TODO Put in methods which you want run 
  }

  public void setCUrrentTeams(int i) {
    //curenntTeams.setText("Current Ts:" + String.valueOf(i));
    System.out.println(i-1);
    if((i-1) >= 0){
    HBox toAdd = colors.get(i-1);
    //String color = CreateGameController.getMainClient().getTeams()[i-1].getColor();
    toAdd.getChildren().clear();
    toAdd.getChildren().add(createColorRec( toAdd,i-1));
    String text = "";
    if(CreateGameController.getLasttype().equals("HUMAN")) {
    	text = "local Human";
    	 teamNames.get(i-1).setText(CreateGameController.getLastTeamName());
    }else if (CreateGameController.getLasttype().equals("AI")) {
		text = "AI (" + CreateGameController.getLastAitype().name() + ")" ;
		 teamNames.get(i-1).setText(CreateGameController.getLastTeamName());
	}else if(CreateGameController.getLasttype().equals("UNKNOWN")) {
		text = "Remote Player";
		if(CreateGameController.getServerManager().getGameStateFromSession().getTeams()[i-1] != null) {
			teamNames.get(i-1).setText(CreateGameController.getServerManager().getGameStateFromSession().getTeams()[i-1].getId());
		}else {
			teamNames.get(i-1).setText(String.valueOf(i));
		}
	}
    CreateGameController.setLasttype("UNKNOWN");
    if(i-1 == 0 ) {
    	text += " (You)";
    }
    teamTypes.get(i-1).setText(text);
		
    }
  }

  

  public void showTeamInformation() {
  }

 

  private void createLayout2() {
    HBox main = new HBox();
    main.setAlignment(Pos.CENTER);
    main.setSpacing(main.heightProperty().doubleValue() * 0.09);
    main.heightProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              double spacing = newVal.doubleValue() * 0.1;
              main.setSpacing(spacing);
            });
    main.prefWidthProperty().bind(this.widthProperty());
    main.getChildren().add(createLeft());
    VBox middleBox = new VBox();
    middleBox.getChildren().add(createHeader());
    middleBox.getChildren().add(createTopCenter());
    middleBox
        .heightProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              double spacing = newVal.doubleValue() * 0.04;
              middleBox.setSpacing(spacing);
            });
    // middleBox.getChildren().add(createShowMapPane("p2"));
    // middleBox.getChildren().add(createREctangleAnimation());
    middleBox.getChildren().add(createLeave());
    middleBox.getChildren().add(createCreateButton());
    middleBox.setAlignment(Pos.TOP_CENTER);
    // middleBox.setStyle("-fx-background-color:red");
    main.getChildren().add(middleBox);
    root.setStyle("-fx-background-color:black");
    root.getChildren().add(main);
  }

  private VBox createLeft() {
    VBox left = new VBox();
    left.heightProperty()
        .addListener(
            (obs, oldVal, newVal) -> {
              double spacing = newVal.doubleValue() * 0.1;
              left.setPadding(new Insets(spacing, 0, 0, 0));
            });
    VBox labels = new VBox();
    labels.setSpacing(30);
    // labels.getChildren().add(createInfoLabel("port" , hsc.getPort()));
    // labels.getChildren().add(createInfoLabel("Server-ID" , hsc.getServerID()));
    // labels.getChildren().add(createInfoLabel("Session-ID", hsc.getSessionID()));
    left.getChildren().add(labels);
    //		Image mp = new Image(getClass().getResourceAsStream("ct2.png"));
    //		ImageView mpv = new ImageView(mp);
    //		left.getChildren().add(mpv);
    return left;
  }

  private HBox createIPandPortBox() {
    HBox labelBox = new HBox();
    this.widthProperty()
        .addListener(
            (observable, oldValue, newValue) -> {
              double newSpacing = newValue.doubleValue() * 0.05;
              labelBox.setSpacing(newSpacing);
            });
    return labelBox;
  }

  private VBox waitingBox() {
    final Label status = new Label("Waiting for Players");
    status.getStyleClass().add("spinner-des-label");
    final Timeline timeline =
        new Timeline(
            new KeyFrame(
                Duration.ZERO,
                new EventHandler() {
                  @Override
                  public void handle(Event event) {
                    String statusText = status.getText();
                    status.setText(
                        ("Waiting for Players . . .".equals(statusText))
                            ? "Waiting for Players ."
                            : statusText + " .");
                  }
                }),
            new KeyFrame(Duration.millis(1000)));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
    VBox layout = new VBox();
    layout.prefWidthProperty().bind(this.widthProperty().multiply(0.17));
    status.fontProperty().bind(waitigFontSize);
    // layout.setStyle("-fx-background-color: blue");
    layout.getChildren().addAll(status);
    return layout;
  }

  private HBox createTopCenter() {
    HBox captureLoadingLabel = new HBox();
    captureLoadingLabel.setAlignment(Pos.CENTER);
    captureLoadingLabel.prefWidthProperty().bind(this.widthProperty().multiply(0.5));
    captureLoadingLabel.getChildren().add(waitingBox());
    return captureLoadingLabel;
  }

  private Button createLeave() {
    Button exit = new Button("Leave");
    exit.getStyleClass().add("leave-button");
    exit.prefWidthProperty().bind(root.widthProperty().multiply(0.1));
    exit.prefHeightProperty().bind(exit.widthProperty().multiply(0.35));
    exit.setOnAction(
        e -> {
        CreateGameController.deleteGame();
          hsc.switchtoHomeScreen(e);
        });
    return exit;
  }

  private Button createCreateButton() {
    Button search = new Button("Create");
    search.getStyleClass().add("leave-button");
    search.prefWidthProperty().bind(root.widthProperty().multiply(0.15));
    search.prefHeightProperty().bind(search.widthProperty().multiply(0.25));
    search
        .fontProperty()
        .bind(
            Bindings.createObjectBinding(
                () -> Font.font("Century Gothic", search.getHeight() * 0.4),
                search.heightProperty()));
    search.setOnAction(
        e -> {
          //hsc.switchToPlayGameScene(App.getStage(),CreateGameController.getMainClient(), false);
          // hsc.switchToTestScene(App.getStage());
        });

    return search;
  }
}
