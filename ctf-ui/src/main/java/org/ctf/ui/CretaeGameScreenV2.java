package org.ctf.ui;

import org.ctf.shared.state.GameState;

import configs.ImageLoader;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class CretaeGameScreenV2 extends Scene {
	HomeSceneController hsc;
	String selected;
	static GameState state;
	StackPane root;
	StackPane left;
	StackPane right;
	Text info;
	HBox sep;
	


	public CretaeGameScreenV2(HomeSceneController hsc, double width, double height) {
		super(new StackPane(), width, height);
		this.hsc = hsc;
		this.getStylesheets().add(getClass().getResource("ComboBox.css").toExternalForm());
		this.getStylesheets().add(getClass().getResource("MapEditor.css").toExternalForm());
		this.getStylesheets().add(getClass().getResource("color.css").toExternalForm());
		this.root = (StackPane) this.getRoot();
		createLayout();
	}

	private void createLayout() {
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
			double newSpacing = newValue.doubleValue() * 0.05; // Beispiel: 5% der Höhe als Spacing
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
		TextField serverIPText = createTextfield("Enter the Server IP");
		serverIPText.prefWidthProperty().bind(enterSeverInfoBox.widthProperty().multiply(0.4));
		enterSeverInfoBox.getChildren().add(serverIPText);
		TextField portText = createTextfield("Enter the Port");
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
			hsc.switchToWaitGameScene(App.getStage());
		});
		return search;
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
	                    setAlignment(javafx.geometry.Pos.CENTER); // Zentriert den Text in der Zelle
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
			            setAlignment(javafx.geometry.Pos.CENTER); // Zentriert den Text in der Zelle
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
}

	
