package org.ctf.ui;

import org.ctf.shared.client.Client;
import org.ctf.ui.controllers.RemoteWaitingThread;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class RemoteWaitingScene extends Scene {
	StackPane root;
	Client client;
	Text text = new Text("Test");

	public RemoteWaitingScene(Client client, double width, double height) {
		super(new StackPane(), width, height);
		this.client = client;
		root = (StackPane) this.getRoot();
		this.getStylesheets().add(getClass().getResource("MapEditor.css").toExternalForm());
		createLayout();
		RemoteWaitingThread rwt = new RemoteWaitingThread(this);
		rwt.start();
	}


	public Client getClient() {
		return client;
	}

	public Text getText() {
		return text;
	}

	private void createLayout() {
		root.setStyle(" -fx-background-color: rgb(25,25,25);");
		VBox mainBox = new VBox();
		mainBox.setAlignment(Pos.TOP_CENTER);
		Image mp = new Image(getClass().getResourceAsStream("multiplayerlogo.png"));
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
		mainBox.getChildren().add(text);
		root.getChildren().add(mainBox);
		
		
	}
}
