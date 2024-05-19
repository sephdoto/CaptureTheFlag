package org.ctf.ui;

import java.io.File;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.ctf.shared.client.AIClient;
import org.ctf.shared.client.Client;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.state.GameState;
import org.ctf.shared.wave.WaveFunctionCollapse;
import org.ctf.ui.customobjects.BaseRep;
import org.ctf.ui.customobjects.CostumFigurePain;
import org.ctf.ui.customobjects.Timer;

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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javassist.expr.Instanceof;

public class PlayGameScreenV2 extends Scene {
	
	 private ScheduledExecutorService scheduler;
	 private ScheduledExecutorService scheduler2;
	 // TODO Remember to close the service before moving away from this scene
	 private int currentTeam;
	 private Client mainClient;
	Label teamTurn;
	HomeSceneController hsc;
	StackPane root;
	HBox captureLoadingLabel;
	StackPane left;
	Text text;
	boolean isRemote;
	VBox testBox;
	Label howManyTeams;
	Label moveTimeLimit;
	Label gameTimeLimit;
	Timer noMoveTimeLimit;
	GamePane gm;
	GameState state;
	VBox right;
	GameState currentState;
	boolean first;
	HBox top;
	private static Circle c;
	private static Label idLabel;
	private static Label typeLabel;
	private static Label attackPowLabel;
	private static Label teamLabel;
	private static Label countLabel;
	StackPane showMapBox;
	StackPane wrapper = new StackPane();
	public  ObjectProperty<Color> sceneColorProperty = 
	        new SimpleObjectProperty<>(Color.BLUE);
	private ObjectProperty<Font> timerLabel = new SimpleObjectProperty<Font>(Font.getDefault());
	private ObjectProperty<Font> timerDescription = new SimpleObjectProperty<Font>(Font.getDefault());
	private static  ObjectProperty<Font> pictureMainDiscription = new SimpleObjectProperty<Font>(Font.getDefault());
	private ObjectProperty<Font> figureDiscription = new SimpleObjectProperty<Font>(Font.getDefault());
	private ObjectProperty<Font> waitigFontSize = new SimpleObjectProperty<Font>(Font.getDefault());
	SimpleObjectProperty<Insets> padding = new SimpleObjectProperty<>(new Insets(10));
	
	
	
	Runnable updateTask = () -> {
		try {
			
//			if(currentTeam != mainClient.getCurrentTeamTurn()) {
//				currentTeam = mainClient.getCurrentTeamTurn();
//					Platform.runLater(() -> {
//						
//						 this.redrawGrid(mainClient.getCurrentState());
//						 this.setTeamTurn(String.valueOf(mainClient.getCurrentTeamTurn()));
//				        });
//			}
			GameState tmp = mainClient.getQueuedGameState();
			if(tmp !=null) {
				currentState = tmp;
			    Platform.runLater(() -> {
			    	if(! mainClient.isGameMoveTimeLimited()) {
			    		noMoveTimeLimit.reset();
			    	}
					 this.redrawGrid(currentState);
					 this.setTeamTurn();
			        });
			}
		} catch (Exception e) {

		}
	};
	
	Runnable updateTask2 = () -> {
		try {
			Platform.runLater(() -> {
				if(mainClient.isGameMoveTimeLimited()) {
				 moveTimeLimit.setText(formatTime(mainClient.getRemainingMoveTimeInSeconds()));
				 if(mainClient.getRemainingMoveTimeInSeconds() <10) {
					 moveTimeLimit.setTextFill(Color.RED);
				 }else {
					 moveTimeLimit.setTextFill(Color.GOLD);
				 }
				}
				if(mainClient.isGameTimeLimited()) {
				 gameTimeLimit.setText(formatTime(mainClient.getRemainingGameTimeInSeconds()));
				 if(mainClient.getRemainingGameTimeInSeconds() <60) {
					 gameTimeLimit.setTextFill(Color.RED);
				 }
				}

	        });
			} catch (Exception e) {

		}
	};

	public PlayGameScreenV2(HomeSceneController hsc, double width, double height,Client mainClient,boolean isRemote) {
		super(new StackPane(), width, height);
		this.mainClient = mainClient;
		this.isRemote = isRemote;
		 this.getStylesheets().add(getClass().getResource("MapEditor.css").toExternalForm());
		initalizePlayGameScreen(hsc);
	}
	
	
	
	public void initalizePlayGameScreen(HomeSceneController hsc) {
		//this.mainClient = CreateGameController.getMainClient();
		currentTeam = -1;
		this.hsc = hsc;
		manageFontSizes();
		first = true;
		this.getStylesheets().add(getClass().getResource("MapEditor.css").toExternalForm());
		this.root = (StackPane) this.getRoot();
		createLayout();
		this.getStylesheets().add(getClass().getResource("color.css").toExternalForm());
		if(mainClient.isGameTimeLimited() || mainClient.isGameMoveTimeLimited()) {
			scheduler2 = Executors.newScheduledThreadPool(1);
			scheduler2.scheduleAtFixedRate(updateTask2, 0, 1, TimeUnit.SECONDS);
		}
		scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleAtFixedRate(updateTask, 0, 100, TimeUnit.MILLISECONDS);
	}
	
	
	
	
	public void createLayout() {
		root.setStyle("-fx-background-color: black");
		root.paddingProperty().bind(padding);
		root.prefHeightProperty().bind(this.heightProperty());
		root.prefWidthProperty().bind(this.widthProperty());
		top = new HBox();
		top.setAlignment(Pos.CENTER);
		//VBox left = new VBox();
		right = new VBox();
		right.setAlignment(Pos.BOTTOM_CENTER);
		//left.setAlignment(Pos.CENTER);
		top.prefHeightProperty().bind(this.heightProperty());
//		left.prefHeightProperty().bind(this.heightProperty());
//		left.prefWidthProperty().bind(this.widthProperty().multiply(0.7));
//		left.getChildren().add(createShowMapPane("p2"));
		top.getChildren().add(wrapper);
		//top.getChildren().add(createShowMapPane());
		right.getChildren().add(createTopCenter());
		right.getChildren().add(imageTest());
		right.getChildren().add(createClockBox(mainClient.isGameMoveTimeLimited(), mainClient.isGameTimeLimited()));
		right.setStyle("-fx-background-color: black");
		right.prefWidthProperty().bind(this.widthProperty().multiply(0.3));
		top.getChildren().add(right);
		root.getChildren().add(top);
		//PullGameStateThreads p = new PullGameStateThreads();
	}
	
	public void redrawGrid(GameState state) {
		if (state == null) {
			showMapBox.getChildren().add(new Label("hallo"));
		} else {
			System.out.println(state.getCurrentTeam());
			System.out.println(state.getGrid()[0].length);
			drawGamePane(state);
			if (isRemote) {
				if (mainClient.isItMyTurn() && !(mainClient instanceof AIClient)) {
					Game.initializeGame(gm, mainClient);
				}
			} else {
				for (Client local : CreateGameController.getLocalHumanClients()) {
					//System.out.println("Local: " + local.getTeamID());
					if (local.isItMyTurn()) {
						Game.initializeGame(gm, local);
					}
				}
			}
		}
	}
	
	private void drawGamePane(GameState state) {
		if (gm != null) {
			CreateGameController.setFigures(gm.getFigures());
			showMapBox.getChildren().remove(gm);
		}
		 gm = new GamePane(state);
	     StackPane.setAlignment(gm, Pos.CENTER);
	     gm.maxWidthProperty().bind(this.widthProperty().multiply(0.7));
	     gm.maxHeightProperty().bind(this.heightProperty().multiply(0.9));
	     gm.enableBaseColors(this);
//	     if (first) {
//		     showMapBox.getChildren().add(createBackgroundImage(gm.vBox,state));
//		     first = false;
//		}
	     wrapper.getChildren().remove(showMapBox);
	     createShowMapPane();
	     showMapBox.getChildren().add(gm);
	     wrapper.getChildren().add(showMapBox);
	}
	
	 private  String formatTime(int totalSeconds) {
	        int hours = totalSeconds / 3600;
	        int minutes = (totalSeconds % 3600) / 60;
	        int seconds = totalSeconds % 60;

	        return String.format("%d:%02d:%02d", hours, minutes, seconds);
	    }
	
	public ImageView createBackgroundImage(VBox vBox) {
	      // Image mp = new Image(getClass().getResourceAsStream("gridSTARWARS.png"));
	     
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
	
	private StackPane createShowMapPane() {
		
		//VBox.setVgrow(outerbox, Priority.ALWAYS);
		
		showMapBox = new StackPane();
		//showMapBox.getStyleClass().add("play-pane");		
		//showMapBox.paddingProperty().bind(padding);
		showMapBox.paddingProperty().bind(padding);
		showMapBox.prefWidthProperty().bind(this.widthProperty().multiply(0.7));
	     showMapBox.prefHeightProperty().bind(this.heightProperty().multiply(0.9));
	     showMapBox.maxWidthProperty().bind(App.getStage().widthProperty().multiply(0.7));
	     showMapBox.maxHeightProperty().bind(App.getStage().heightProperty().multiply(0.9));
		showMapBox.getStyleClass().add("option-pane");
		showMapBox.getChildren().add(createBackgroundImage(gm.vBox));
		return showMapBox;
	}
	
	
	private VBox waitingBox(String playerName) {
		String showString = "team " +  playerName + "'s turn";
		 final Label    status   = new Label(showString);
		 status.getStyleClass().add("des-label2");
		    final Timeline timeline = new Timeline(
		      new KeyFrame(Duration.ZERO, new EventHandler() {
		        @Override public void handle(Event event) {
		          String statusText = status.getText();
		          String s = showString + " . . .";
		          String s2 = showString + " .";
		          status.setText(
		            (s.equals(statusText))
		              ? s2
		              : statusText + " ."
		          );
		        }
		      }),  
		      new KeyFrame(Duration.millis(1000))
		    );
		    timeline.setCycleCount(Timeline.INDEFINITE);
		    timeline.play();
		    VBox layout = new VBox();
		    layout.prefWidthProperty().bind(right.widthProperty().multiply(0.55));
		    status.fontProperty().bind(figureDiscription);
		    //layout.setStyle("-fx-background-color: blue");
		    layout.getChildren().addAll(status);
		    return layout;
	}
	
	 private VBox showWaitingBox() {
		    final Label status = new Label("is making its move");
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
		                        ("is making its move . . .".equals(statusText))
		                            ? "is making its move ."
		                            : statusText + " .");
		                  }
		                }),
		            new KeyFrame(Duration.millis(1000)));
		    timeline.setCycleCount(Timeline.INDEFINITE);
		    timeline.play();
		    VBox layout = new VBox();
		    String teamString = mainClient.getAllTeamNames()[mainClient.getCurrentTeamTurn()];
		    teamString += " (" + mainClient.getCurrentTeamTurn() + ")";
		    Label teamname = new Label(teamString);
		    teamname.prefWidthProperty().bind(this.widthProperty().multiply(0.2));
		    teamname.fontProperty().bind(waitigFontSize);
		    teamname.setAlignment(Pos.CENTER);
		    teamname.textFillProperty().bind(CreateGameController.getColors().get(String.valueOf(mainClient.getCurrentTeamTurn())));
		    layout.prefWidthProperty().bind(this.widthProperty().multiply(0.17));
		    status.fontProperty().bind(waitigFontSize);
		    status.textFillProperty().bind(CreateGameController.getColors().get(String.valueOf(mainClient.getCurrentTeamTurn())));
		    // layout.setStyle("-fx-background-color: blue");
		    layout.getChildren().add(teamname);
		    layout.getChildren().addAll(status);
		    return layout;
		  }
	 
	 private VBox showYourTurnBox() {
		    Label status = new Label("It's your turn!");
		    status.getStyleClass().add("spinner-des-label");
		    VBox layout = new VBox();
		    String teamString = mainClient.getAllTeamNames()[mainClient.getCurrentTeamTurn()];
		    teamString += " (" + mainClient.getCurrentTeamTurn() + ")";
		    Label teamname = new Label(teamString);
		    teamname.prefWidthProperty().bind(this.widthProperty().multiply(0.2));
		    teamname.fontProperty().bind(waitigFontSize);
		    teamname.setAlignment(Pos.CENTER);
		    teamname.textFillProperty().bind(CreateGameController.getColors().get(String.valueOf(mainClient.getCurrentTeamTurn())));
		    layout.prefWidthProperty().bind(this.widthProperty().multiply(0.17));
		    status.fontProperty().bind(waitigFontSize);
		    status.setAlignment(Pos.CENTER);
		    status.prefWidthProperty().bind(this.widthProperty().multiply(0.17));
		    status.textFillProperty().bind(CreateGameController.getColors().get(String.valueOf(mainClient.getCurrentTeamTurn())));
		    layout.getChildren().add(teamname);
		    layout.getChildren().addAll(status);
		    return layout;
		  }
	 
	
	private HBox createTopCenter() {
		captureLoadingLabel = new HBox();
		captureLoadingLabel.setAlignment(Pos.CENTER);
		//captureLoadingLabel.setStyle("-fx-background-color: yellow");
		captureLoadingLabel.prefWidthProperty().bind(right.widthProperty().multiply(0.8));
		//captureLoadingLabel.getChildren().add(waitingBox("3"));
		//teamTurn = new Label("Current team:");
		captureLoadingLabel.getChildren().add(waitingBox(String.valueOf(mainClient.getCurrentTeamTurn())));
		return captureLoadingLabel;
	}
	
	public void setTeamTurn() {
		boolean onelocal = false;
		captureLoadingLabel.getChildren().clear();
		if (isRemote) {
			if (mainClient.isItMyTurn() && !(mainClient instanceof AIClient)) {
				captureLoadingLabel.getChildren().add(showYourTurnBox());

			}else {
				captureLoadingLabel.getChildren().add(showWaitingBox());
			}
		} else {
			for (Client local : CreateGameController.getLocalHumanClients()) {
				if (local.isItMyTurn()) {
					captureLoadingLabel.getChildren().add(showYourTurnBox());
					onelocal = true;
				}
			}
			if (!onelocal) {
					captureLoadingLabel.getChildren().add(showWaitingBox());
			}
		}
	}
	
	private void manageFontSizes() {
		 widthProperty().addListener(new ChangeListener<Number>()
		    {
		        public void changed(ObservableValue<? extends Number> observableValue, Number oldWidth, Number newWidth)
		        {
		        	timerLabel.set(Font.font(newWidth.doubleValue() / 40));
		        	timerDescription.set(Font.font(newWidth.doubleValue() / 60));
		        	pictureMainDiscription.set(Font.font(newWidth.doubleValue() / 40));
		        	figureDiscription.set(Font.font(newWidth.doubleValue() / 45));
		        	padding.set(new Insets(newWidth.doubleValue()*0.01));
		        	waitigFontSize.set(Font.font(newWidth.doubleValue() / 55));

		        	
		        }
		    });
	}
	
	public void showColorChooser(double d, double e, BaseRep r) {
		  MyCustomColorPicker myCustomColorPicker = new MyCustomColorPicker();
        myCustomColorPicker.setCurrentColor(sceneColorProperty.get());
        CustomMenuItem itemColor = new CustomMenuItem(myCustomColorPicker);
        itemColor.getStyleClass().add("custom-menu-item");
        itemColor.setHideOnClick(false);
        //colors.get(r.getTeamID()).bind(myCustomColorPicker.customColorProperty());
        CreateGameController.getColors().get(r.getTeamID()).bind(myCustomColorPicker.customColorProperty());
        for(CostumFigurePain p : gm.getFigures().values()) {
      	  	if(p.getTeamID().equals(r.getTeamID())) {
      		 // p.showTeamColorWhenSelecting(colors.get(r.getTeamID()));
      	  		p.showTeamColorWhenSelecting(CreateGameController.getColors().get(r.getTeamID()));
      	  	}
        }
        //r.showColor(sceneColorProperty);
        ContextMenu contextMenu = new ContextMenu(itemColor);
        contextMenu.setOnHiding(t->{sceneColorProperty.unbind();
         for(CostumFigurePain m : gm.getFigures().values() ) {
         		m.unbind();
         	}});
        contextMenu.show(this.getWindow(),d,e);
	}
	
	private HBox createClockBox(boolean movetimelimited, boolean gametimeLimited) {
		HBox timerBox = new HBox();
		timerBox.setAlignment(Pos.CENTER);
		//timerBox.getStyleClass().add("timer-box");
		timerBox.prefWidthProperty().bind(right.widthProperty());
		timerBox.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.09;
			double padding = newValue.doubleValue() * 0.02;
			timerBox.setSpacing(newSpacing);
			timerBox.setPadding(new Insets(0, padding, 0, padding));
		});
		VBox timer1;
		VBox timer2;
		if(movetimelimited) {
			timer1 = createTimer2(timerBox, "Move Time");
			System.out.println("move time limited");
		}else {
			 timer1 = createTimer(timerBox, "Move Time");
		}
		if(gametimeLimited) {
			timer2 = createTimer2(timerBox, "Game Time");
			System.out.println("Game time limited");
		}else {
			 timer2 =  createTimer(timerBox, "Game Time");
		}
		
		timerBox.getChildren().addAll(timer1,timer2);
		return timerBox;
	}
	
	private VBox createTimer(HBox timerBox, String text) {
		VBox timerwithDescrip = new VBox();
		timerwithDescrip.setAlignment(Pos.CENTER);
		timerwithDescrip.prefWidthProperty().bind(timerBox.widthProperty().multiply(0.35));
		timerwithDescrip.prefHeightProperty().bind(timerBox.widthProperty().multiply(0.35));
		Label desLabel = new Label(text);
		desLabel.setAlignment(Pos.CENTER);
		desLabel.fontProperty().bind(timerDescription);
		desLabel.getStyleClass().add("des-timer");
		timerwithDescrip.getChildren().add(desLabel);
		if(text.equals("Move Time")) {
			noMoveTimeLimit = new Timer(0,0,0);
			noMoveTimeLimit.prefWidthProperty().bind(timerBox.widthProperty().multiply(0.35));
			noMoveTimeLimit.prefHeightProperty().bind(noMoveTimeLimit.widthProperty().multiply(0.35));
			noMoveTimeLimit.getStyleClass().add("timer-label");
			noMoveTimeLimit.fontProperty().bind(timerLabel);
			timerwithDescrip.getChildren().add(noMoveTimeLimit);
		}else {
		Timer t = new Timer(0,0,0);
		t.prefWidthProperty().bind(timerBox.widthProperty().multiply(0.35));
		t.prefHeightProperty().bind(t.widthProperty().multiply(0.35));
		t.getStyleClass().add("timer-label");
		t.fontProperty().bind(timerLabel);
		timerwithDescrip.getChildren().add(t);
		}
		return timerwithDescrip;
	}
	
	
	private VBox createTimer2(HBox timerBox, String text) {
		VBox timerwithDescrip = new VBox();
		timerwithDescrip.setAlignment(Pos.CENTER);
		timerwithDescrip.prefWidthProperty().bind(timerBox.widthProperty().multiply(0.35));
		timerwithDescrip.prefHeightProperty().bind(timerBox.widthProperty().multiply(0.35));
		Label desLabel = new Label(text);
		desLabel.setAlignment(Pos.CENTER);
		desLabel.fontProperty().bind(timerDescription);
		desLabel.getStyleClass().add("des-timer");
		timerwithDescrip.getChildren().add(desLabel);
		if (text.equals("Move Time")) {
			moveTimeLimit = new Label();
			moveTimeLimit.prefWidthProperty().bind(timerBox.widthProperty().multiply(0.35));
			moveTimeLimit.prefHeightProperty().bind(moveTimeLimit.widthProperty().multiply(0.35));
			moveTimeLimit.getStyleClass().add("timer-label");
			moveTimeLimit.fontProperty().bind(timerLabel);
			timerwithDescrip.getChildren().add(moveTimeLimit);
		}else {
			gameTimeLimit = new Label();
			gameTimeLimit.prefWidthProperty().bind(timerBox.widthProperty().multiply(0.35));
			gameTimeLimit.prefHeightProperty().bind(gameTimeLimit.widthProperty().multiply(0.35));
			gameTimeLimit.getStyleClass().add("timer-label");
			gameTimeLimit.fontProperty().bind(timerLabel);
			timerwithDescrip.getChildren().add(gameTimeLimit);
		}
		return timerwithDescrip;
	}
	
	
	
	
	
	private HBox imageTest() {
		HBox h1 = new HBox();
		h1.prefHeightProperty().bind(this.heightProperty().multiply(0.65));
		h1.prefWidthProperty().bind(h1.heightProperty().multiply(0.3));
		h1.widthProperty().addListener((observable, oldValue, newValue) -> {
			double padding = newValue.doubleValue() * 0.08;
			h1.setPadding(new Insets(padding, padding, padding, padding));
		});
		//h1.setStyle("-fx-background-color: red");
		h1.setAlignment(Pos.CENTER);
		VBox x = new VBox();
		
		x.widthProperty().addListener((observable, oldValue, newValue) -> {
			double padding = newValue.doubleValue() * 0.05;
			x.setPadding(new Insets(padding, padding, padding, padding));
		});
		x.getStyleClass().add("option-pane");
		HBox pict = new HBox();
		//pict.setStyle("-fx-background-color: green");
		pict.prefHeightProperty().bind(x.heightProperty().multiply(0.1));
		typeLabel = new Label("Yoda");
		typeLabel.fontProperty().bind(pictureMainDiscription);
		typeLabel.setAlignment(Pos.CENTER_LEFT);
		typeLabel.prefHeightProperty().bind(pict.heightProperty());
		typeLabel.prefWidthProperty().bind(pict.widthProperty().multiply(0.7));
		typeLabel.getStyleClass().add("figure-label");
		StackPane p = new StackPane();
		p.prefWidthProperty().bind(pict.widthProperty().multiply(0.3));
		//p.setStyle("-fx-background-color: yellow");
		Image mp = new Image(getClass().getResourceAsStream("Yoda.png"));
		c = new Circle();
		c.radiusProperty().bind(Bindings.divide(widthProperty(), 23));
		c.setFill(new ImagePattern(mp));
		Circle c2 = new Circle();
		c2.setFill(Color.WHITE);
		 c2.setStroke(Color.BLACK);
	      c2.setStrokeWidth(2);
		c2.radiusProperty().bind(Bindings.divide(widthProperty(), 21));
		pict.getChildren().addAll(typeLabel,p);
		p.getChildren().addAll(c2,c);
		x.getChildren().add(pict);
		x.getChildren().add(createDeslabelBox());
		h1.getChildren().add(x);
		return h1;
	}
	private void fitText( Label lbl, double max) {
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
	}
	
	
	private VBox createDeslabelBox() {
		VBox deBox = new VBox(10);
		deBox.heightProperty().addListener((observable, oldValue, newValue) -> {
			double spacing = newValue.doubleValue() * 0.08;
			deBox.setSpacing(spacing);
		});
		deBox.setAlignment(Pos.BASELINE_LEFT);
		idLabel = new Label("id: -");
		handleLabel(idLabel, deBox);
		teamLabel = new Label("team: -");
		handleLabel(teamLabel, deBox);
		attackPowLabel = new Label("attackpower: -");
		handleLabel(attackPowLabel, deBox);
		countLabel = new Label("count: - ");
		handleLabel(countLabel, deBox);
		deBox.getChildren().addAll(idLabel, teamLabel, attackPowLabel, countLabel);
		return deBox;
	}
	
	private void handleLabel(Label l, VBox parent) {
		l.fontProperty().bind(figureDiscription);
		l.prefWidthProperty().bind(parent.widthProperty());
		l.getStyleClass().add("figure-label");
	}
		
	
	
	public static void setFigureImage(Image img) {
		c.setFill(new ImagePattern(img));
	}
	
	
	public static void setIdLabelText(String text) {
		idLabel.setText(text);
	}

	public static void setTypeLabelText(String text) {
		typeLabel.setText(text);
	}

	public static void setAttackPowLabelText(String text) {
		attackPowLabel.setText(text);
	}

	public static void setCountLabelText(String text) {
		countLabel.setText(text);
	}

	public static void setTeamLabelText(String text) {
		teamLabel.setText(text);
	}

}
