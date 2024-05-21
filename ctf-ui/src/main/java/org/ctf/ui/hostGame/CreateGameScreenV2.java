package org.ctf.ui.hostGame;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.ctf.shared.client.lib.ServerManager;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.ImageType;
import org.ctf.shared.constants.Enums.Themes;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.data.map.MapTemplate;
import org.ctf.shared.tools.JsonTools;
import org.ctf.shared.wave.WaveFunctionCollapse;
import org.ctf.ui.App;
import org.ctf.ui.CreateGameController;
import org.ctf.ui.GamePane;
import org.ctf.ui.HomeSceneController;
import org.ctf.ui.PopUpCreator;
import org.ctf.ui.PopUpCreatorEnterTeamName;
import org.ctf.ui.StroeMaps;
import org.ctf.ui.controllers.ImageController;
import org.ctf.ui.customobjects.PopUpPane;
import configs.ImageLoader;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class CreateGameScreenV2 extends Scene {
	
	HomeSceneController hsc;
	String selected;
	static GameState state;
	StackPane root;
	StackPane left;
	StackPane right;
	TextField serverIPText;
	TextField portText;
	String serverIP;
	String teamName;
	String port;
	HBox sep;
	TextField enterNamefield;
	PopUpCreator popUpCreator;
	PopUpPane aiOrHumanPop;
	PopUpPane enterNamePopUp;
	PopUpPane aiConfigPopUpPane;
	PopUpPane aiLevelPopUpPane;
	StackPane showMapBox;
	MapTemplate template;
	ServerManager serverManager;
	HashMap<MapTemplate, GameState> maps;
	GamePane gm;
	ComboBox<String> c;
	private ObjectProperty<Font> addHumanButtonTextFontSIze;
	private ObjectProperty<Font> addAiCOmboTextFontSIze = new SimpleObjectProperty<Font>(Font.getDefault());
	private ObjectProperty<Font> popUpLabel = new SimpleObjectProperty<Font>(Font.getDefault());
	private ObjectProperty<Font> leaveButtonText = new SimpleObjectProperty<Font>(Font.getDefault());
	private ObjectProperty<Font> aiPowerText = new SimpleObjectProperty<Font>(Font.getDefault());
	SimpleObjectProperty<Insets> padding = new SimpleObjectProperty<>(new Insets(10));

	public CreateGameScreenV2(HomeSceneController hsc, double width, double height) {
		super(new StackPane(), width, height);
		this.hsc = hsc;
	    try {
        this.getStylesheets().add(Paths.get(Constants.toUIStyles + "ComboBox.css").toUri().toURL().toString());
        this.getStylesheets().add(Paths.get(Constants.toUIStyles + "MapEditor.css").toUri().toURL().toString());
        this.getStylesheets().add(Paths.get(Constants.toUIStyles + "color.css").toUri().toURL().toString());
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
		this.root = (StackPane) this.getRoot();
		popUpCreator = new PopUpCreator(this, root,hsc);
		createLayout();
	}
	
	
	/**
	 * Changes the font-sizes in relation to the screen size
	 * @author Manuel Krakowski
	 */
	private void manageFontSizes() {
		 addAiCOmboTextFontSIze = new SimpleObjectProperty<Font>(Font.font(this.getWidth()/60));
		 addHumanButtonTextFontSIze = new SimpleObjectProperty<Font>(Font.font(this.getWidth()/70));
		 popUpLabel = new SimpleObjectProperty<Font>(Font.font(this.getWidth()/50));
		 leaveButtonText = new SimpleObjectProperty<Font>(Font.font(this.getWidth()/50));
		 addAiCOmboTextFontSIze = new SimpleObjectProperty<Font>(Font.font(this.getWidth()/80));
		 aiPowerText = new SimpleObjectProperty<Font>(Font.font(this.getWidth()/50));


		widthProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth, Number newWidth) {
				addHumanButtonTextFontSIze.set(Font.font(newWidth.doubleValue() / 70));
				addAiCOmboTextFontSIze.set(Font.font(newWidth.doubleValue() / 60));
				popUpLabel.set(Font.font(newWidth.doubleValue() / 50));
				leaveButtonText.set(Font.font(newWidth.doubleValue() / 80));
				aiPowerText.set(Font.font(newWidth.doubleValue() / 50));
				padding.set(new Insets(newWidth.doubleValue()*0.01));
			}
		});
	}
	
	/**
	 * Creates the layout of the whole scene including all components
	 * @author Manuel Krakowski
	 */
	private void createLayout() {
		manageFontSizes();
		ImageLoader.loadImages();
		StroeMaps.initDefaultMaps();
		root.getStyleClass().add("join-root");
		VBox mainBox = createMainBox();
		mainBox.setAlignment(Pos.TOP_CENTER);
		sep = createMiddleSperator();
		left = createBasicPane();
		left.getChildren().add(createMiddleLeft());
		selected = StroeMaps.getRandomMapName();
		right = createShowMapPane(selected);
		sep.getChildren().add(left);
		sep.getChildren().add(right);
		mainBox.getChildren().add(sep);
		mainBox.getChildren().add(createLeave());
		root.getChildren().add(mainBox);
	}
	
	/**
	 * Creates A Vbox which will be used to divide the whole screen vertically into 3 main parts
	 * @author Manuel Krakowski
	 * @return Vbox
	 */
	private VBox createMainBox() {
		VBox mainBox = new VBox();
		mainBox.setSpacing(30);
		mainBox.getChildren().add(createTop());
		mainBox.setAlignment(Pos.TOP_CENTER);
		mainBox.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.03;
			mainBox.setSpacing(newSpacing);
		});
		return mainBox;
	}
	
	/**
	 * Creates an Image which is the header of the whole scene
	 * @author Manuel Krakowski
	 * @return
	 */
	private ImageView createTop() {
	    Image mp = ImageController.loadThemedImage(ImageType.MISC, "multiplayerlogo");
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
	 * Creates A HBox which will be used to divide the middle part of the screen horizontally into 2 main parts
	 * @author Manuel Krakowski
	 * @return
	 */
	private HBox createMiddleSperator() {
		sep = new HBox();
		sep.setAlignment(Pos.CENTER);
		sep.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.05;
			sep.setSpacing(newSpacing);
		});
		return sep;
	}
	
	/**
	 * Creates a empty stackPane which is used for the left side
	 * @author Manuel Krakowski
	 */
	public StackPane createBasicPane() {
		StackPane pane = new StackPane();
		pane.setPrefSize(250, 250);
		pane.prefWidthProperty().bind(this.widthProperty().multiply(0.4));
		pane.prefHeightProperty().bind(pane.widthProperty().multiply(0.8));
		return pane;
	}
	
	/**
	 * Creates the Vbox in the middle-left in which contains tow boxes in which the user can select a server and choose a map
	 * @author Manuel Krakowski
	 * @return
	 */
	private VBox createMiddleLeft() {
		VBox leftBox = new VBox();
		leftBox.setAlignment(Pos.TOP_CENTER);
		leftBox.setSpacing(left.heightProperty().doubleValue() * 0.06);
		left.heightProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.06;
			leftBox.setSpacing(spacing);
		});
		VBox serverInfoBox = createServerInfoBox(leftBox);
		leftBox.getChildren().add(serverInfoBox);
		VBox buttonBox = createChooseMapBox(leftBox);
		leftBox.getChildren().add(buttonBox);
		return leftBox;
	}
	
	/**
	 * Creates a Box containing one header and two textfields in which the user can enter the server-information
	 * @author Manuel Krakowski
	 * @param parent: used for relative resizing
	 * @return
	 */
	private VBox createServerInfoBox(VBox parent) {
		VBox serverInfoBox = new VBox();
		VBox.setVgrow(serverInfoBox, Priority.ALWAYS);
		serverInfoBox.getStyleClass().add("option-pane");
		serverInfoBox.prefWidthProperty().bind(left.widthProperty());
		serverInfoBox.setAlignment(Pos.TOP_CENTER);
		serverInfoBox.setSpacing(left.heightProperty().doubleValue() * 0.09);
		serverInfoBox.heightProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.09;
			serverInfoBox.setSpacing(spacing);
		});
		serverInfoBox.getChildren().add(createHeader(parent, "select sever"));
		HBox enterSeverInfoBox = new HBox();
		enterSeverInfoBox.prefHeightProperty().bind(serverInfoBox.heightProperty().multiply(0.6));
		enterSeverInfoBox.prefWidthProperty().bind(serverInfoBox.widthProperty());
		enterSeverInfoBox.setAlignment(Pos.CENTER);
		enterSeverInfoBox.setSpacing(enterSeverInfoBox.widthProperty().doubleValue() * 0.06);
		enterSeverInfoBox.widthProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.06;
			enterSeverInfoBox.setSpacing(spacing);
		});
		serverIPText = createTextfield("Enter the Server IP",0.2);
		serverIPText.prefWidthProperty().bind(enterSeverInfoBox.widthProperty().multiply(0.4));
		enterSeverInfoBox.getChildren().add(serverIPText);
		portText = createTextfield("Enter the Port",0.2);
		portText.prefWidthProperty().bind(enterSeverInfoBox.widthProperty().multiply(0.4));
		enterSeverInfoBox.getChildren().add(portText);
		serverInfoBox.getChildren().add(enterSeverInfoBox);
		return serverInfoBox;
	}
	
	/**
	 * Creates a Header Text
	 * @param leftBox: parent for relative resizing
	 * @param text: Text that the header should show
	 * @return
	 */
	private Text createHeader(VBox leftBox, String text) {
		Text leftheader = new Text(text);
		leftheader.getStyleClass().add("custom-header");
		leftheader.fontProperty()
				.bind(Bindings.createObjectBinding(() -> Font.font(leftBox.getWidth() / 18), leftBox.widthProperty()));
		return leftheader;
	}
	
	/**
	 * Creates a Box in which the user can select a map from a ComboBox and start a game with it by clicking on a button
	 * @author Manuel Krakowski
	 * @param parent: used for relative resizing
	 * @return
	 */
	private VBox createChooseMapBox(VBox parent) {
		VBox buttonBox = new VBox();
		buttonBox.getStyleClass().add("option-pane");
		buttonBox.prefHeightProperty().bind(left.heightProperty().multiply(0.7));
		buttonBox.getChildren().add(createHeader(parent, "Choose Map"));
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setPadding(new Insets(20));
		buttonBox.heightProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.2;
			buttonBox.setSpacing(spacing);
		});
		buttonBox.getChildren().add(createChoiceBox(buttonBox));
		buttonBox.getChildren().add(createCreateButton());
		return buttonBox;
	}
	
	
	/**
	 * Creates a simple Textfield
	 * @author Manuel Krakowski
	 * @param prompt: Prompt text of the textfield
	 * @param x: Height of the textfield in relation to its width
	 * @return
	 */
	public static TextField createTextfield(String prompt, double x) {
		TextField searchField = new TextField();
		searchField.getStyleClass().add("custom-search-field2");
		searchField.setPromptText(prompt);
		searchField.prefHeightProperty().bind(searchField.widthProperty().multiply(x));
		searchField.heightProperty().addListener((obs, oldVal, newVal) -> {
			double newFontSize = newVal.doubleValue() * 0.4;
			searchField.setFont(new Font(newFontSize));
		});
		return searchField;
	}
	
	/**
	 * Creates the ComboBox in which the user can select a map
	 * @author Manuel Krakowski
	 * @param parent: used for relative resizing
	 * @return 
	 */
	private ComboBox<String> createChoiceBox(VBox parent) {
	    c = new ComboBox<String>();
		c.getStyleClass().add("combo-box");
		c.getItems().addAll(this.getTemplateNames());
		c.setValue(c.getItems().get(0));
		c.prefWidthProperty().bind(parent.widthProperty().multiply(0.8));
		c.prefHeightProperty().bind(parent.heightProperty().multiply(0.1));
		c.setCellFactory(param -> new ListCell<String>() {
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
				} else {
					setText(item);
					setAlignment(javafx.geometry.Pos.CENTER);
				}
			}
		});
		c.setButtonCell(new ListCell<String>() {
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setText(null);
				} else {
					setText(item);
					setAlignment(javafx.geometry.Pos.CENTER);
				}
			}
		});
		c.setOnAction(event -> {
			perfromChoiceBoxAction(c);
		});
		return c;
	}
	
	
	/**
	 * when a map from the choiceBox is selected the respective Template and GameState are accessed from the folder using
	 * {@link JsonTools}. The respective Map is shown on the right side using the GameState.
	 * @author Manuel Krakowski
	 * @author aniemesc
	 * @param c: ChoiceBox from which the user selects the map
	 */
    private void perfromChoiceBoxAction(ComboBox<String> c) {
      selected = c.getValue();
      showMapBox.getChildren().clear();
      maps = JsonTools.getTemplateAndGameState(selected);
      if (!maps.isEmpty()) {
        Map.Entry<MapTemplate, GameState> entry = maps.entrySet().iterator().next();
        template = entry.getKey();
        state = entry.getValue();
      }
      gm = new GamePane(state);
      ImageView iv = this.createBackgroundImage(gm.vBox);
      StackPane.setAlignment(iv, Pos.CENTER);
      sep.getChildren().remove(showMapBox);
      createShowMapPane("lol");
      sep.getChildren().add(showMapBox);
      // showMapBox.getChildren().add(iv);
      // showMapBox.getChildren().add(gm);
      // gm.maxWidthProperty().bind(App.getStage().widthProperty().multiply(0.4));
      // showMapBox.getChildren().add(createBackgroundImage(gm.vBox));
      // StackPane.setAlignment(gm, Pos.CENTER);
      // showMapBox.getChildren().add(gm);
    }
	
	
	public ArrayList<String> getTemplateNames(){
		File templateFolder = new File(JsonTools.mapTemplates);
		if(templateFolder.isDirectory()) {
			String[] names = templateFolder.list();
			for(int i=0;i<names.length;i++) {
				names[i] =   names[i].substring(0, names[i].length()-5);
			}
			ArrayList<String> result = new ArrayList<String>();
			result.addAll(Arrays.asList(names));
			return result;
			}		
		return new ArrayList<String>();
	}
	
	
	/**
	 * Creates the button which is used to create a game when it is clicked, if the
	 * Textfields with the server info are empty an animation is shown and the create action can#t be executed
	 * @author Manuel Krakowski
	 * @return create-button
	 */
	private Button createCreateButton() {
		Button search = new Button("Create");
		search.getStyleClass().add("leave-button");
		search.prefWidthProperty().bind(root.widthProperty().multiply(0.15));
		search.prefHeightProperty().bind(search.widthProperty().multiply(0.25));
		search.fontProperty().bind(Bindings.createObjectBinding(
				() -> Font.font("Century Gothic", search.getHeight() * 0.4), search.heightProperty()));
		search.setOnAction(e -> {
			if (portText.getText().isEmpty()) {
				informationmustBeEntered(portText,"custom-search-field2-mustEnter","custom-search-field2");
			}
			if (serverIPText.getText().isEmpty()) {
				informationmustBeEntered(serverIPText,"custom-search-field2-mustEnter","custom-search-field2");
			}
			if (!portText.getText().isEmpty() && !serverIPText.getText().isEmpty()) {
				perfromCreateButtonClick();
				
			}
		});
		return search;
	}
	
	/**
	 * Creates a wobbling animation of a textfield
	 * @author Manuel Krakowski
	 * @param t: Textfield on which the animation is shown
	 * @param mustEnterStyle: Style of the Textfield during the animation
	 * @param defaultStyle: default Style of the textfield before and after the animation
	 */
	public static void informationmustBeEntered(TextField t, String mustEnterStyle, String defaultStyle) {
		t.getStyleClass().add(mustEnterStyle);
		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.05), t);
		translateTransition.setFromX(-2);
		translateTransition.setToX(2);
		translateTransition.setCycleCount(10);
		translateTransition.setAutoReverse(true);
		translateTransition.playFromStart();
		translateTransition.setOnFinished(event -> {
			t.getStyleClass().remove(mustEnterStyle);
			t.getStyleClass().add(defaultStyle);
		});
	}
	
	/**
	 * when the create Button is clicked successfully a Game Session is created with the help of a controller
	 * and a Popup Pane is shown in which the user can decide if he wants to play as human or as Ai
	 * @author Manuel Krakowski
	 */
	private void perfromCreateButtonClick() {
		serverIP = serverIPText.getText();
		port = portText.getText();
		CreateGameController.setPort(port);
		CreateGameController.setServerIP(serverIP);
		CreateGameController.setTemplate(template);
		if(!CreateGameController.createGameSession()) {
			informationmustBeEntered(serverIPText,"custom-search-field2-mustEnter","custom-search-field2");
			informationmustBeEntered(portText,"custom-search-field2-mustEnter","custom-search-field2");
		}else {
			this.createChooserPopup();
		}

	}
	
	
	
	/**
	 * Cretaes the Stackpane on the right side, which will always contain the currently selected map from the ComboBox
	 * @author Manuel Krakowski
	 * @author aniemesc
	 * @param name: Hier muss man das noch ändern 
	 * @return
	 */
	private StackPane createShowMapPane(String name) {
      showMapBox = new StackPane();
      showMapBox.getStyleClass().add("option-pane");
      showMapBox.prefWidthProperty().bind(this.widthProperty().multiply(0.4));
      showMapBox.prefHeightProperty().bind(showMapBox.widthProperty());
      showMapBox.maxWidthProperty().bind(App.getStage().widthProperty().multiply(0.45));
      showMapBox.maxHeightProperty().bind(App.getStage().heightProperty().multiply(0.65));
      showMapBox.getStyleClass().add("show-GamePane");
      showMapBox.paddingProperty().bind(padding);
      // state = StroeMaps.getMap(name);
      maps = JsonTools.getTemplateAndGameState(c.getValue());
      if (!maps.isEmpty()) {
        Map.Entry<MapTemplate, GameState> entry = maps.entrySet().iterator().next();
        template = entry.getKey();
        state = entry.getValue();
      }

      gm = new GamePane(state);
      StackPane.setAlignment(gm, Pos.CENTER);
      gm.maxWidthProperty().bind(App.getStage().widthProperty().multiply(0.4));
      gm.maxHeightProperty().bind(App.getStage().heightProperty().multiply(0.6));
      showMapBox.getChildren().add(createBackgroundImage(gm.vBox));
      showMapBox.getChildren().add(gm);
      return showMapBox;
    }
	/**
	 * 
	 * @author Manuel Krakowski
	 * @author aniemesc
	 * @param vBox
	 * @return
	 */
    public ImageView createBackgroundImage(VBox vBox) {
      WaveFunctionCollapse backgroundcreator =
          new WaveFunctionCollapse(state.getGrid(), Constants.theme);
      backgroundcreator.saveToResources();
      Image mp =
          new Image(new File(Constants.toUIResources + "pictures" + File.separator + "grid.png")
              .toURI().toString());
      ImageView mpv = new ImageView(mp);
      StackPane.setAlignment(mpv, Pos.CENTER);
      mpv.fitHeightProperty().bind(vBox.heightProperty().multiply(1));
      mpv.fitWidthProperty().bind(vBox.widthProperty().multiply(1));

      // mpv.setPreserveRatio(true);
      mpv.setOpacity(1);
      return mpv;
    }
	
    /**
     * Creates the leave-button on the bottom of the scene
     * @author Manuel Krakowski
     * @return leave-button
     */
	private Button createLeave() {
		Button exit = new Button("Leave");
		exit.getStyleClass().add("leave-button");
		exit.prefWidthProperty().bind(root.widthProperty().multiply(0.1));
		exit.prefHeightProperty().bind(exit.widthProperty().multiply(0.25));
		exit.setOnAction(e -> {
			//hsc.switchtoHomeScreen(e);
			//PopupCreatorGameOver g = new PopupCreatorGameOver(this, root, hsc);
			//g.createGameOverPopUpYouLost();
			//hsc.switchToTestScene(App.getStage());
		  hsc.switchToAnalyzerScene(App.getStage());
		});
		return exit;
	}
	
	/**
	 * Creates the play as human button which is shown in the chooserPopup and fits its size to it
	 * @author Manuel Krakowski
	 * @param text: text displayed on the button
	 * @param src: button-image-name
	 * @return play-as-human-button
	 */
	private Button createAddHumanButton(String text, String src) {
		Button button = new Button(text);
		button.getStyleClass().add("button25");
		button.fontProperty().bind(addHumanButtonTextFontSIze);
	    Image mp = ImageController.loadThemedImage(ImageType.MISC, src);

		ImageView vw = new ImageView(mp);
		button.setGraphic(vw);
		button.setContentDisplay(ContentDisplay.RIGHT);
		vw.fitWidthProperty().bind(button.widthProperty().divide(5));
		vw.setPreserveRatio(true);
		button.setMaxWidth(Double.MAX_VALUE);
		button.setOnAction(e -> {
			PopUpCreatorEnterTeamName popi = new PopUpCreatorEnterTeamName(this, root, aiOrHumanPop, hsc,true,false);
			popi.createEnterNamePopUp();
		});
		return button;
	}

	/**
	 * Creates the play-as-Ai-button which is shown in the chooserPopup and fits its size to it
	 * @param text: 
	 * @param src
	 * @return
	 */
	private Button createAddAIButton(String text, String src) {
		Button button = new Button(text);
		button.getStyleClass().add("button25");
		button.fontProperty().bind(addHumanButtonTextFontSIze);
	    Image mp = ImageController.loadThemedImage(ImageType.MISC, src);
		ImageView vw = new ImageView(mp);
		button.setGraphic(vw);
		button.setContentDisplay(ContentDisplay.RIGHT);
		vw.fitWidthProperty().bind(button.widthProperty().divide(8));
		vw.setPreserveRatio(true);
		button.setMaxWidth(Double.MAX_VALUE);
		button.setOnAction(e -> {
			popUpCreator.createAiLevelPopUp(aiOrHumanPop,portText,serverIPText);
		});
		return button;

	}
	
	private Button createCancelButton() {
		Button exit = new Button("Cancel");
		exit.fontProperty().bind(leaveButtonText);
		exit.getStyleClass().add("leave-button");
		exit.prefWidthProperty().bind(root.widthProperty().multiply(0.1));
		exit.prefHeightProperty().bind(exit.widthProperty().multiply(0.25));
		exit.setOnAction(e -> {
			portText.setDisable(false);
			serverIPText.setDisable(false);
			CreateGameController.deleteGame();
			//hsc.deleteGame();
			root.getChildren().remove(aiOrHumanPop);
		});
		
		return exit;
	}
	
	

	private void createChooserPopup() {
		aiOrHumanPop = new PopUpPane(this, 0.5, 0.3);
		portText.setDisable(true);
		serverIPText.setDisable(true);
		root.getChildren().add(aiOrHumanPop);
		VBox top = new VBox();
		top.heightProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.06;
			top.setSpacing(spacing);
		});
		Label l = new Label("Choose Game Mode");
		l.prefWidthProperty().bind(aiOrHumanPop.widthProperty());
		l.setAlignment(Pos.CENTER);
		l.getStyleClass().add("custom-label");
		l.fontProperty().bind(popUpLabel);
		top.getChildren().add(l);
		HBox chooseButtonBox = new HBox();
		chooseButtonBox.setAlignment(Pos.CENTER);
		aiOrHumanPop.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.05; 
			chooseButtonBox.setSpacing(newSpacing);
		});
		Button human = createAddHumanButton("Play as Human", "humanForButton");
		human.prefWidthProperty().bind(aiOrHumanPop.widthProperty().multiply(0.2));
		Button ai = createAddAIButton("Play as AI", "robotForButton");
		ai.prefWidthProperty().bind(human.widthProperty());
		ai.prefHeightProperty().bind(human.heightProperty());
		chooseButtonBox.getChildren().addAll(human, ai);
		top.getChildren().add(chooseButtonBox);
		HBox centerLeaveButton = new HBox();
		// centerLeaveButton.setStyle("-fx-background-color: blue");
		centerLeaveButton.prefHeightProperty().bind(aiOrHumanPop.heightProperty().multiply(0.4));
		centerLeaveButton.setAlignment(Pos.CENTER);
		centerLeaveButton.getChildren().add(createCancelButton());
		top.getChildren().add(centerLeaveButton);
		aiOrHumanPop.setContent(top);
	}
	
	
	
	
	
	


}
