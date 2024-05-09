package org.ctf.ui;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Random;

import org.ctf.ui.customobjects.BaseRep;
import org.ctf.ui.customobjects.CostumFigurePain;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import configs.GameMode;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
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
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.PopupWindow;
import javafx.stage.Window;
import javafx.util.Duration;
import javafx.scene.Node;


public class WaitingScene extends Scene {
	HomeSceneController hsc;
	StackPane root;
	StackPane left;
	StackPane right;
	Text text;
	VBox testBox;
	Label howManyTeams;
	StackPane clipboardInfo;
	GamePane gm;
	private  ObjectProperty<Color> sceneColorProperty = 
		        new SimpleObjectProperty<>(Color.BLUE);
	private ObjectProperty<Font> waitigFontSize = new SimpleObjectProperty<Font>(Font.getDefault());
	private ObjectProperty<Font> serverInfoHeaderFontSize = new SimpleObjectProperty<Font>(Font.getDefault());
	private ObjectProperty<Font> serverInfoCOntentFontSize = new SimpleObjectProperty<Font>(Font.getDefault());
	private ObjectProperty<Font> addHumanButtonTextFontSIze = new SimpleObjectProperty<Font>(Font.getDefault());
	private ObjectProperty<Font> serverInfoDescription = new SimpleObjectProperty<Font>(Font.getDefault());
	

	public WaitingScene(HomeSceneController hsc, double width, double height) {
		super(new StackPane(), width, height);
		this.hsc = hsc;
		manageFontSizes();
		this.getStylesheets().add(getClass().getResource("MapEditor.css").toExternalForm());
		this.root = (StackPane) this.getRoot();
		this.getStylesheets().add(getClass().getResource("color.css").toExternalForm());
		createLayout();
	       
	}
	
	
	private void createLayout() {
		root.getStyleClass().add("join-root");
		VBox mainBox = createMainBox(root);
		root.getChildren().add(mainBox);
		mainBox.getChildren().add(createHeader());
		HBox middle = createMiddleHBox();
		VBox leftTop = createLeftVBox(middle);
		VBox rightTop = createRightVBox(middle);
		middle.getChildren().addAll(leftTop,rightTop);
		mainBox.getChildren().add(middle);
		
	}
	
	private void manageFontSizes() {
		 widthProperty().addListener(new ChangeListener<Number>()
		    {
		        public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth, Number newWidth)
		        {
		            waitigFontSize.set(Font.font(newWidth.doubleValue() / 60));
		            serverInfoHeaderFontSize.set(Font.font(newWidth.doubleValue()/ 100));
		            serverInfoCOntentFontSize.set(Font.font(newWidth.doubleValue()/ 65));
		            addHumanButtonTextFontSIze.set(Font.font(newWidth.doubleValue()/ 70));
		            serverInfoDescription.set(Font.font(newWidth.doubleValue()/ 50));
		        }
		    });
	}
	
	
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
	
	private ImageView createHeader() {
		Image mp = new Image(getClass().getResourceAsStream("multiplayerlogo.png"));
		ImageView mpv = new ImageView(mp);
		mpv.fitWidthProperty().bind(root.widthProperty().multiply(0.5));
		mpv.setPreserveRatio(true);
		return mpv;
	}
	
	private HBox createMiddleHBox() {
		HBox sep = new HBox();
		sep.setStyle("-fx-background-color: red");
		sep.prefHeightProperty().bind(this.heightProperty());
		sep.setAlignment(Pos.CENTER);
		sep.setSpacing(50);
		sep.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.05;
			sep.setSpacing(newSpacing);
		});
		return sep;
	}
	
	private VBox createLeftVBox(HBox parent) {
		
		VBox leftBox = new VBox();
		leftBox.prefHeightProperty().bind(parent.heightProperty());
		leftBox.setStyle("-fx-background-color: green");
		leftBox.setAlignment(Pos.TOP_CENTER);
		leftBox.prefWidthProperty().bind(parent.widthProperty().multiply(0.55));
		leftBox.prefHeightProperty().bind(parent.heightProperty().multiply(0.68));
		return leftBox;
	}
	
	private VBox createRightVBox(HBox parent) {
		VBox rightBox = new VBox();
		rightBox.prefHeightProperty().bind(parent.heightProperty());
		rightBox.setStyle("-fx-background-color: yellow");
		rightBox.setAlignment(Pos.TOP_CENTER);
		rightBox.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newPadding = newValue.doubleValue() * 0.04;
			double newSpacing = newValue.doubleValue() * 0.03;
			rightBox.setPadding(new Insets(newPadding));
			rightBox.setSpacing(newSpacing);
		});
		rightBox.prefWidthProperty().bind(parent.widthProperty().multiply(0.35));
		rightBox.prefHeightProperty().bind(parent.heightProperty().multiply(0.68));
		rightBox.getChildren().add(createServerDescription(rightBox,"Server Information"));
		rightBox.getChildren().add(createSeverInfoBox(rightBox));
		return rightBox;
	}
	
	private VBox createSeverInfoBox(VBox parent) {
		VBox serverInfoBox = new VBox();
		serverInfoBox.prefWidthProperty().bind(parent.widthProperty());
		serverInfoBox.setStyle("-fx-background-color: yellow");
		serverInfoBox.setAlignment(Pos.CENTER);
		serverInfoBox.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.05;
			serverInfoBox.setSpacing(newSpacing);
		});
		serverInfoBox.getChildren().add(createInfoLabel(serverInfoBox, "Session-ID", hsc.getSessionID(), 0.8));
		
		HBox dividelowerPart = new HBox();
		dividelowerPart.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.05;
			dividelowerPart.setSpacing(newSpacing);
		});
		dividelowerPart.getChildren().add(createInfoLabel(parent, "port", hsc.getPort(), 0.35));
		dividelowerPart.getChildren().add(createInfoLabel(parent, "Server-IP", hsc.getServerID(), 0.55));
		serverInfoBox.getChildren().add(dividelowerPart);
		return serverInfoBox;
	}
	
	
	private void createShowClipBoardInfoStackPane() {
		
	}
	
	private void copyTextToClipBoard() {
		Clipboard clipboard = Clipboard.getSystemClipboard();
		ClipboardContent content = new ClipboardContent();
		content.putString(hsc.getSessionID());
		clipboard.setContent(content);
	}
	
	private Label createServerDescription(VBox parent, String text) {
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
		labelBox.getChildren().addAll(headerLabel,numberLabel);
		return labelBox;
	}
	
	
	
	
	
	
	
	
	public void showTeamInformation() {
		howManyTeams = new Label();
		int maxteams = 2;     //Add other Methode here
	}
	
	
	public void showColorChooser(double d, double e, BaseRep r) {
		  MyCustomColorPicker myCustomColorPicker = new MyCustomColorPicker();
          myCustomColorPicker.setCurrentColor(sceneColorProperty.get());

          CustomMenuItem itemColor = new CustomMenuItem(myCustomColorPicker);
          itemColor.getStyleClass().add("custom-menu-item");
          itemColor.setHideOnClick(false);
          sceneColorProperty.bind(myCustomColorPicker.customColorProperty());
          for(CostumFigurePain p : gm.getFigures().values()) {
        	  	if(p.getTeamID().equals(r.getTeamID())) {
        		  p.showTeamColorWhenSelecting(sceneColorProperty);
        	  	}
          }
          r.showColor(sceneColorProperty);
          ContextMenu contextMenu = new ContextMenu(itemColor);
          contextMenu.setOnHiding(t->{sceneColorProperty.unbind();
           for(CostumFigurePain m : gm.getFigures().values() ) {
           		m.unbind();
           	}});
          contextMenu.show(this.getWindow(),d,e);
	}
	
	private VBox createAddButtons() {
		VBox v = new VBox();
		v.heightProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.04;
			v.setSpacing(spacing);
			double padding = newVal.doubleValue() * 0.1;
			v.setPadding(new Insets(padding, 0, 0, 0));
		});
		v.prefWidthProperty().bind(this.widthProperty().multiply(0.2));
		Button k = createAddHumanButton("add Human-Player","user-286.png");
		Button b = createAddAIButton("add Bot","robot1.png");
		b.prefHeightProperty().bind(k.heightProperty());
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
        vw.fitWidthProperty().bind(button.widthProperty().divide(5));
        vw.setPreserveRatio(true);
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
        button.setMaxWidth(Double.MAX_VALUE); 
        return button;
	}
	
	private void createLayout2(){
		HBox main = new HBox();
		main.setAlignment(Pos.CENTER);
		main.setSpacing(main.heightProperty().doubleValue() * 0.09);
		main.heightProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.1;
			main.setSpacing(spacing);
		});
		main.prefWidthProperty().bind(this.widthProperty());
		main.getChildren().add(createLeft());
		VBox middleBox = new VBox();
		middleBox.getChildren().add(createHeader());
		middleBox.getChildren().add(createTopCenter());
		middleBox.heightProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.04;
			middleBox.setSpacing(spacing);
		});
		//middleBox.getChildren().add(createShowMapPane("p2"));
		//middleBox.getChildren().add(createREctangleAnimation());
		middleBox.getChildren().add(createLeave());
		middleBox.getChildren().add(createCreateButton());
		middleBox.setAlignment(Pos.TOP_CENTER);
		//middleBox.setStyle("-fx-background-color:red");
		main.getChildren().add(middleBox);
		main.getChildren().add(createAddButtons());
		root.setStyle("-fx-background-color:black");
		root.getChildren().add(main);
	}
	private VBox createLeft() {
		VBox left = new VBox();
		left.heightProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.1;
			left.setPadding(new Insets(spacing, 0, 0, 0));
		});
		VBox labels = new VBox();
		labels.setSpacing(30);
		//labels.getChildren().add(createInfoLabel("port" , hsc.getPort()));
		//labels.getChildren().add(createInfoLabel("Server-ID" , hsc.getServerID()));
		//labels.getChildren().add(createInfoLabel("Session-ID", hsc.getSessionID()));
		left.getChildren().add(labels);
//		Image mp = new Image(getClass().getResourceAsStream("ct2.png"));
//		ImageView mpv = new ImageView(mp);
//		left.getChildren().add(mpv);
		return left;
	}
	
	private HBox createIPandPortBox() {
		HBox labelBox = new HBox();
		this.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.05;
			labelBox.setSpacing(newSpacing);
		});
		return labelBox;
		
	}
	
	private VBox waitingBox() {
		 final Label    status   = new Label("Waiting for Players");
		 status.getStyleClass().add("des-label2");
		    final Timeline timeline = new Timeline(
		      new KeyFrame(Duration.ZERO, new EventHandler() {
		        @Override public void handle(Event event) {
		          String statusText = status.getText();
		          status.setText(
		            ("Waiting for Players . . .".equals(statusText))
		              ? "Waiting for Players ." 
		              : statusText + " ."
		          );
		        }
		      }),  
		      new KeyFrame(Duration.millis(1000))
		    );
		    timeline.setCycleCount(Timeline.INDEFINITE);
		    timeline.play();
		    VBox layout = new VBox();
		    layout.prefWidthProperty().bind(this.widthProperty().multiply(0.17));
		    status.fontProperty().bind(waitigFontSize);
		    //layout.setStyle("-fx-background-color: blue");
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
		exit.prefHeightProperty().bind(exit.widthProperty().multiply(0.25));
		exit.setOnAction(e -> {
			hsc.switchtoHomeScreen(e);
		});
		return exit;
	}
	
	private Button createCreateButton() {
		Button search = new Button("Create");
		search.getStyleClass().add("leave-button");
		search.prefWidthProperty().bind(root.widthProperty().multiply(0.15));
		search.prefHeightProperty().bind(search.widthProperty().multiply(0.25));
		search.fontProperty().bind(Bindings.createObjectBinding(
				() -> Font.font("Century Gothic", search.getHeight() * 0.4), search.heightProperty()));
		search.setOnAction(e -> {
			hsc.switchToPlayGameScene(App.getStage());
			//hsc.switchToTestScene(App.getStage());
		});

		return search;
	}
	 
}
