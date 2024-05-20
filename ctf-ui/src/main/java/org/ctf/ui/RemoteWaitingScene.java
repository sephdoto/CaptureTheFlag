package org.ctf.ui;

import org.ctf.shared.client.Client;
import org.ctf.shared.client.lib.ServerManager;
import org.ctf.shared.constants.Enums.ImageType;
import org.ctf.ui.controllers.ImageController;
import org.ctf.ui.controllers.RemoteWaitingThread;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class RemoteWaitingScene extends Scene {
	StackPane root;
	Client client;
	Text text;
	HomeSceneController hsc;
	ServerManager serverManager;

    


  public RemoteWaitingScene(Client client, double width, double height,HomeSceneController hsc,ServerManager serverManager) {
		super(new StackPane(), width, height);
		this.hsc = hsc;
		this.client = client;
		this.serverManager = serverManager;
		root = (StackPane) this.getRoot();
		this.getStylesheets().add(getClass().getResource("MapEditor.css").toExternalForm());
		createLayout();
		RemoteWaitingThread rwt = new RemoteWaitingThread(this);
		rwt.start();
	}


	public HomeSceneController getHsc() {
    return hsc;
  }

	public ServerManager getServerManager() {
      return serverManager;
    }
	
  public Client getClient() {
		return client;
	}

	public Text getText() {
		return text;
	}

	private void createLayout() {
		//root.setStyle(" -fx-background-color: rgb(25,25,25);");
	 Image backgroundImage = ImageController.loadRandomThemedImage(ImageType.HOME);
	    BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT,
	        BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, App.backgroundSize);
	    root.setBackground(new Background(background));
		VBox mainBox = new VBox();
		mainBox.setAlignment(Pos.TOP_CENTER);
		Image mp = ImageController.loadThemedImage(ImageType.MISC, "multiplayerlogo");
		ImageView mpv = new ImageView(mp);
		mpv.fitWidthProperty().bind(root.widthProperty().multiply(0.65));
		mpv.setPreserveRatio(true);
		mainBox.getChildren().add(mpv);
		StackPane.setAlignment(mainBox, Pos.TOP_CENTER);
		VBox.setMargin(mpv, new Insets(this.getHeight()*0.05,0,0,0));
		this.heightProperty().addListener((obs,old,newV) -> {
			double margin = newV.doubleValue()*0.05;
			VBox.setMargin(mpv, new Insets(margin,0,0,0));
		});
		text = new Text("Please wait for the game to start.");
		text.getStyleClass().add("custom-info-label");
		text.fontProperty()
        .bind(
            Bindings.createObjectBinding(
                () -> Font.font("Century Gothic", App.getStage().getWidth() / 50),
                App.getStage().widthProperty()));
		
		
		//text.setOpacity(0);
//		FadeTransition startTransition = new FadeTransition(Duration.millis(1500), text);
//	    startTransition.setFromValue(0.1);
//	    startTransition.setToValue(1.0);
//	    startTransition.setDelay(Duration.millis(2000));
//	    startTransition.setAutoReverse(true); //
//	    startTransition.setCycleCount(Timeline.INDEFINITE);
//	    startTransition.play();
	    StackPane wrapper = new StackPane();
	    wrapper.getStyleClass().add("loading-pane"); 
	    wrapper.maxWidthProperty().bind(this.widthProperty().multiply(0.5));
	    wrapper.maxHeightProperty().bind(this.heightProperty().multiply(0.4));
	    wrapper.minWidthProperty().bind(this.widthProperty().multiply(0.5));
        wrapper.maxWidthProperty().bind(this.heightProperty().multiply(0.4));
	    
	    
	    wrapper.getChildren().add(text);
	    
		StackPane.setAlignment(text, Pos.CENTER);
	    //root.getChildren().add(text);
		root.getChildren().add(wrapper);
		root.getChildren().add(mainBox);
		
		
	}


  public StackPane getRootPane() {
    return root;
  }
}
