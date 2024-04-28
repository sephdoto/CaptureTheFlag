package org.ctf.ui;

import org.ctf.shared.client.ServerManager;
import org.ctf.shared.client.lib.ServerDetails;
import org.ctf.shared.client.service.CommLayer;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.data.map.MapTemplate;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class CretaeGameScreenV2 extends Scene {
	HomeSceneController hsc;
	String selected;
	static GameState state;
	StackPane root;
	StackPane left;
	StackPane right;
	TextField serverIPText;
	TextField portText;
	String serverIP;
	String port;
	HBox sep;
	PopUpPane pop;
	ServerManager serverManager;
	private ObjectProperty<Font> addHumanButtonTextFontSIze = new SimpleObjectProperty<Font>(Font.getDefault());
	private ObjectProperty<Font> addAiCOmboTextFontSIze = new SimpleObjectProperty<Font>(Font.getDefault());
	private ObjectProperty<Font> popUpLabel = new SimpleObjectProperty<Font>(Font.getDefault());
	private ObjectProperty<Font> leaveButtonText = new SimpleObjectProperty<Font>(Font.getDefault());
	private ObjectProperty<Font> aiPowerText = new SimpleObjectProperty<Font>(Font.getDefault());


	public CretaeGameScreenV2(HomeSceneController hsc, double width, double height) {
		super(new StackPane(), width, height);
		this.hsc = hsc;
		this.getStylesheets().add(getClass().getResource("ComboBox.css").toExternalForm());
		this.getStylesheets().add(getClass().getResource("MapEditor.css").toExternalForm());
		this.getStylesheets().add(getClass().getResource("color.css").toExternalForm());
		this.root = (StackPane) this.getRoot();
		createLayout();
	}
	
	private void createChooserPopup() {
	 pop = new PopUpPane(this, 0.5, 0.3);
		root.getChildren().add(pop);
		VBox top = new VBox();
		top.heightProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.06;
			top.setSpacing(spacing);
		});
		Label l = new Label("Choose Game Mode");
		l.prefWidthProperty().bind(pop.widthProperty());
		l.setAlignment(Pos.CENTER);
		l.getStyleClass().add("custom-label");
		l.fontProperty().bind(popUpLabel);
		top.getChildren().add(l);
		HBox chooseButtonBox = new HBox();
		chooseButtonBox.setAlignment(Pos.CENTER);
		pop.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.05; // Beispiel: 5% der Höhe als Spacing
			chooseButtonBox.setSpacing(newSpacing);
		});
		Button human = createAddHumanButton("Play as Human", "user-286.png");
		human.prefWidthProperty().bind(pop.widthProperty().multiply(0.2));
		Button ai = createAddAIButton("Play as AI", "robot1.png");
		ai.prefWidthProperty().bind(human.widthProperty());
		ai.prefHeightProperty().bind(human.heightProperty());
		chooseButtonBox.getChildren().addAll(human,ai );
		top.getChildren().add(chooseButtonBox);
		HBox centerLeaveButton = new HBox();
		//centerLeaveButton.setStyle("-fx-background-color: blue");
		centerLeaveButton.prefHeightProperty().bind(pop.heightProperty().multiply(0.4));
		centerLeaveButton.setAlignment(Pos.CENTER);
		centerLeaveButton.getChildren().add(createCancelButton());
		top.getChildren().add(centerLeaveButton);
		pop.setContent(top);
	}
	
	private void createAiLevelPopUp(){
		root.getChildren().remove(pop);
		portText.setDisable(true);
		serverIPText.setDisable(true);
		PopUpPane pop2 = new PopUpPane(this, 0.5, 0.3);
		VBox top = new VBox();
		top.heightProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.1;
			top.setSpacing(spacing);
		});
		Label l = new Label("Choose AI");
		l.prefWidthProperty().bind(pop.widthProperty());
		l.setAlignment(Pos.CENTER);
		l.getStyleClass().add("custom-label");
		l.fontProperty().bind(popUpLabel);
		top.getChildren().add(l);
		HBox buttonBox = new HBox();
		buttonBox.setAlignment(Pos.CENTER);
		pop2.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.03; 
			double padding = newValue.doubleValue()* 0.03;
			buttonBox.setSpacing(newSpacing);
			buttonBox.setPadding(new Insets(0, padding, 0, padding));
		});
		buttonBox.getChildren().addAll(createAIPowerButton("easy", "green"), createAIPowerButton("medium", "yellow"),createAIPowerButton("strong", "x"));
		top.getChildren().add(buttonBox);
		HBox centerLeaveButton = new HBox();
		//centerLeaveButton.setStyle("-fx-background-color: blue");
		centerLeaveButton.prefHeightProperty().bind(pop.heightProperty().multiply(0.2));
		centerLeaveButton.setAlignment(Pos.CENTER);
		centerLeaveButton.getChildren().add(createBackButton());
		top.getChildren().add(centerLeaveButton);
		pop2.setContent(top);
		root.getChildren().add(pop2);
		
	}
	
	
	private Button createAIPowerButton(String pow, String color) {
		Button power = new Button(pow);
		
		power.fontProperty().bind(aiPowerText);
		power.getStyleClass().add("ai-button");
		power.setStyle("-fx-backround-color:" + color);
		power.prefWidthProperty().bind(root.widthProperty().multiply(0.15));
		power.prefHeightProperty().bind(power.widthProperty().multiply(0.35));
		power.setOnAction(e -> {
			hsc.switchToWaitGameScene(App.getStage());
		});
		return power;
	}
	
	
	private Button createCancelButton() {
		Button exit = new Button("Cancel");
		exit.fontProperty().bind(leaveButtonText);
		exit.getStyleClass().add("leave-button");
		exit.prefWidthProperty().bind(root.widthProperty().multiply(0.1));
		exit.prefHeightProperty().bind(exit.widthProperty().multiply(0.25));
		exit.setOnAction(e -> {
			hsc.switchToCreateGameScene(App.getStage());
		});
		portText.setDisable(false);
		serverIPText.setDisable(false);
		return exit;
	}
	
	private Button createBackButton() {
		Button exit = new Button("back");
		exit.fontProperty().bind(leaveButtonText);
		exit.getStyleClass().add("leave-button");
		exit.prefWidthProperty().bind(root.widthProperty().multiply(0.1));
		exit.prefHeightProperty().bind(exit.widthProperty().multiply(0.25));
		exit.setOnAction(e -> {
			root.getChildren().add(pop); 
		});
		return exit;
	}
	
	private void manageFontSizes() {
		 widthProperty().addListener(new ChangeListener<Number>()
		    {
		        public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth, Number newWidth)
		        {
		            addHumanButtonTextFontSIze.set(Font.font(newWidth.doubleValue()/ 70));
		            addAiCOmboTextFontSIze.set(Font.font(newWidth.doubleValue()/ 60));
		            popUpLabel.set(Font.font(newWidth.doubleValue()/ 60));
		            leaveButtonText.set(Font.font(newWidth.doubleValue()/ 80));
		            aiPowerText.set(Font.font(newWidth.doubleValue()/ 50));
		        }
		    });
	}

	private void createLayout() {
		manageFontSizes();
		ImageLoader.loadImages();
		StroeMaps.initDefaultMaps();
		root.getStyleClass().add("join-root");
		VBox mainBox = new VBox();
		root.getChildren().add(mainBox);
		mainBox.getChildren().add(createHeader());
		mainBox.setAlignment(Pos.TOP_CENTER);
		mainBox.setSpacing(50);
		sep = new HBox();
		sep.setAlignment(Pos.CENTER);
		sep.setSpacing(50);
		left = createOptionPane();
		selected = StroeMaps.getRandomMapName();
		right = createShowMapPane(selected);
		
		sep.getChildren().add(left);
		sep.getChildren().add(right);
		mainBox.getChildren().add(sep);
		//mainBox.getChildren().add(createSettings());
		mainBox.getChildren().add(createLeave());
		this.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.05; 
			sep.setSpacing(newSpacing);
		});
		left.getChildren().add(createLeftcontent());
		



	}
	private Button createLeave() {
		Button exit = new Button("Leave");
		exit.getStyleClass().add("leave-button");
		exit.prefWidthProperty().bind(root.widthProperty().multiply(0.1));
		exit.prefHeightProperty().bind(exit.widthProperty().multiply(0.25));

		exit.setOnAction(e -> {
			hsc.switchtoHomeScreen(e);
		});
		return exit;
	}
	
	
	
//	private ImageView createSettings() {
//		Image mp = new Image(getClass().getResourceAsStream("Settings_(iOS).png"));
//		ImageView mpv = new ImageView(mp);
//		mpv.fitWidthProperty().bind(this.widthProperty().multiply(0.05));
//		mpv.setPreserveRatio(true);
//		return mpv;
//	}
	
	private ImageView createHeader() {
		Image mp = new Image(getClass().getResourceAsStream("multiplayerlogo.png"));
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
	
	
	private VBox createLeftcontent() {
		VBox leftBox = new VBox();
		leftBox.setAlignment(Pos.TOP_CENTER);
		//leftBox.setPadding(new Insets(20));
		leftBox.setSpacing(left.heightProperty().doubleValue() * 0.06);
		left.heightProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.06;
			leftBox.setSpacing(spacing);
		});
		VBox serverInfoBox = new VBox();
		serverInfoBox.getStyleClass().add("option-pane");
		serverInfoBox.prefWidthProperty().bind(left.widthProperty());
		serverInfoBox.setAlignment(Pos.TOP_CENTER);
		serverInfoBox.setSpacing(left.heightProperty().doubleValue() * 0.09);
		serverInfoBox.heightProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.09;
			serverInfoBox.setSpacing(spacing);
		});
		serverInfoBox.getChildren().add(createHeader(leftBox, "select sever"));
		HBox enterSeverInfoBox = new HBox();
		enterSeverInfoBox.prefHeightProperty().bind(serverInfoBox.heightProperty().multiply(0.6));
		enterSeverInfoBox.prefWidthProperty().bind(serverInfoBox.widthProperty());
		enterSeverInfoBox.setAlignment(Pos.CENTER);
		enterSeverInfoBox.setSpacing(enterSeverInfoBox.widthProperty().doubleValue()*0.06);
		enterSeverInfoBox.widthProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.06;
			enterSeverInfoBox.setSpacing(spacing);
		});
		 serverIPText = createTextfield("Enter the Server IP");
		serverIPText.prefWidthProperty().bind(enterSeverInfoBox.widthProperty().multiply(0.4));
		enterSeverInfoBox.getChildren().add(serverIPText);
		portText = createTextfield("Enter the Port");
		portText.prefWidthProperty().bind(enterSeverInfoBox.widthProperty().multiply(0.4));
		enterSeverInfoBox.getChildren().add(portText);
		serverInfoBox.getChildren().add(enterSeverInfoBox);
		leftBox.getChildren().add(serverInfoBox);
		
		
		VBox buttonBox = new VBox();
		buttonBox.getStyleClass().add("option-pane");
		buttonBox.prefHeightProperty().bind(left.heightProperty().multiply(0.7));
		buttonBox.getChildren().add(createHeader(leftBox, "Choose Map"));
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setPadding(new Insets(20));
		//buttonBox.setSpacing(buttonBox.heightProperty().doubleValue() * 0.06);
		buttonBox.heightProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.2;
			buttonBox.setSpacing(spacing);
		});
		buttonBox.getChildren().add(createChoiceBox(buttonBox));
		buttonBox.getChildren().add(createCreateButton());
		leftBox.getChildren().add(buttonBox);
		
		return leftBox;
	}
	private Button createCreateButton() {
		Button search = new Button("Create");
		search.getStyleClass().add("leave-button");
		search.prefWidthProperty().bind(root.widthProperty().multiply(0.15));
		search.prefHeightProperty().bind(search.widthProperty().multiply(0.25));
		search.fontProperty().bind(Bindings.createObjectBinding(
				() -> Font.font("Century Gothic", search.getHeight() * 0.4), search.heightProperty()));
		search.setOnAction(e -> {
			if(portText.getText().isEmpty() ) {
				informationmustBeEntered(portText);
			}
			if(serverIPText.getText().isEmpty()) {
				informationmustBeEntered(serverIPText);
			}
			if(!portText.getText().isEmpty() && !serverIPText.getText().isEmpty() ) {
				serverIP = serverIPText.getText();
				port = portText.getText();
				hsc.setPort(port);
				hsc.setServerID(serverIP);
				hsc.createGameSession();
				this.createChooserPopup();
			}
		});
		 
		return search;
	}
	
	

	private void informationmustBeEntered(TextField t) {
		t.getStyleClass().add("custom-search-field2-mustEnter");
		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.05), t);
        translateTransition.setFromX(-2);
        translateTransition.setToX(2);
        translateTransition.setCycleCount(10);
        translateTransition.setAutoReverse(true);
        translateTransition.playFromStart();
        translateTransition.setOnFinished(event -> {
        	t.getStyleClass().remove("custom-search-field2-mustEnter");
	        t.getStyleClass().add("custom-search-field2");
        });
	}
	
	
	private Button createAddHumanButton(String text, String src) {
		Button button = new Button(text);
		button.getStyleClass().add("button25");
		button.fontProperty().bind(addHumanButtonTextFontSIze);
		Image mp = new Image(getClass().getResourceAsStream(src));
		ImageView vw = new ImageView(mp);
		button.setGraphic(vw);
        button.setContentDisplay(ContentDisplay.RIGHT);
        vw.fitWidthProperty().bind(button.widthProperty().divide(5));
        vw.setPreserveRatio(true);
        button.setMaxWidth(Double.MAX_VALUE); 
        button.setOnAction(e -> {
			hsc.switchToWaitGameScene(App.getStage());
		});
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
        button.setMaxWidth(Double.MAX_VALUE);
    	button.setOnAction(e -> {
			createAiLevelPopUp();
		});
        return button;
        
	}
	
	private Text createHeader(VBox leftBox, String text) {
		Text leftheader = new Text(text);
		leftheader.getStyleClass().add("custom-header");
		leftheader.fontProperty()
				.bind(Bindings.createObjectBinding(() -> Font.font(leftBox.getWidth() / 18), leftBox.widthProperty()));
		return leftheader;
	}
	
	public StackPane createOptionPane() {
		StackPane pane = new StackPane();
		//pane.getStyleClass().add("option-pane");
		pane.setPrefSize(250, 250);
		pane.prefWidthProperty().bind(this.widthProperty().multiply(0.4));
		pane.prefHeightProperty().bind(pane.widthProperty().multiply(0.8));

		return pane;
	}
	
	private TextField createTextfield(String prompt) {
		TextField searchField = new TextField();
		searchField.getStyleClass().add("custom-search-field2");
		searchField.setPromptText(prompt);
		searchField.prefHeightProperty().bind(searchField.widthProperty().multiply(0.2));
		searchField.heightProperty().addListener((obs, oldVal, newVal) -> {
			double newFontSize = newVal.doubleValue() * 0.4;
			searchField.setFont(new Font(newFontSize));
		});
		return searchField;
	}
	
	private void fitTextfield(TextField tf, Label lbl, double max ) {
	     double defaultFontSize = 32;
	     Font defaultFont = Font.font(defaultFontSize);
        lbl.setFont(defaultFont);
        lbl.textProperty().addListener((observable, oldValue, newValue) -> {
            
            Text tmpText = new Text(newValue);
            tmpText.setFont(defaultFont);

            double textWidth = tmpText.getLayoutBounds().getWidth();
            if (textWidth <= max) {
                lbl.setFont(defaultFont);
            } else {
               
                double newFontSize = defaultFontSize * max / textWidth;
                lbl.setFont(Font.font(defaultFont.getFamily(), newFontSize));
            }

        });
        lbl.textProperty().bind(tf.textProperty());
	}
	
	private  ComboBox<String> createChoiceBox(VBox parent) {
		ComboBox<String> c = new ComboBox<String>();
		c.getStyleClass().add("combo-box");
		 c.setCellFactory(param -> new ListCell<String>() {
	            @Override
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
			    @Override
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
		c.getItems().addAll(StroeMaps.getValues());
		c.setValue(selected);
		c.prefWidthProperty().bind(parent.widthProperty().multiply(0.8));
		c.prefHeightProperty().bind(parent.heightProperty().multiply(0.1));
		c.setOnAction(event -> {
			selected = c.getValue();
			sep.getChildren().remove(right);
			right = createShowMapPane(selected);
			sep.getChildren().add(right);
			
		});
		return c;
	}

	private StackPane createShowMapPane(String name) {
		StackPane showMapBox = new StackPane();
		showMapBox.getStyleClass().add("option-pane");
		showMapBox.prefWidthProperty().bind(this.widthProperty().multiply(0.4));
		//showMapBox.setStyle("-fx-background-color: white");
		showMapBox.prefHeightProperty().bind(showMapBox.widthProperty());
		showMapBox.getStyleClass().add("show-GamePane");
		state = StroeMaps.getMap(name);
		GamePane gm = new GamePane(state);
		showMapBox.getChildren().add(gm);
		return showMapBox;
	}
	public String getServerIP() {
		return serverIP;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public ServerManager getServerManager() {
		return serverManager;
	}

	public void setServerManager(ServerManager serverManager) {
		this.serverManager = serverManager;
	}
}

	
