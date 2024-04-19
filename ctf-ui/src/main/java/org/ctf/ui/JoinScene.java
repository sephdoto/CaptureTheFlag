package org.ctf.ui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class JoinScene extends Scene {
	HomeSceneController hsc;
	StackPane root;
	public JoinScene(HomeSceneController hsc,double width,double height) {
		super(new StackPane(),width,height);
		this.hsc = hsc;
		this.getStylesheets().add(getClass().getResource("MapEditor.css").toExternalForm());
		this.root = (StackPane)this.getRoot();
		createLayout();
		
	}
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
		StackPane left = createOptionPane();
		sep.getChildren().add(left);
		StackPane right = createOptionPane();
		sep.getChildren().add(right);
		mainBox.getChildren().add(sep);
		
		this.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newSpacing = newValue.doubleValue() * 0.05; // Beispiel: 5% der Höhe als Spacing
            sep.setSpacing(newSpacing);
        });
		
		
		mainBox.getChildren().add(createLeave());
		
		
	}
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
	
	public StackPane createOptionPane() {
		StackPane pane = new StackPane();
		pane.getStyleClass().add("option-pane");
		pane.setPrefSize(250, 250);
		pane.prefWidthProperty().bind(this.widthProperty().multiply(0.4));
		pane.prefHeightProperty().bind(pane.widthProperty().multiply(0.8));
		
		return pane;
	}
	
	private Button createLeave() {
		Button exit = new Button("LEAVE");
		exit.getStyleClass().add("leave-button");
		exit.prefWidthProperty().bind(root.widthProperty().multiply(0.1));
		exit.prefHeightProperty().bind(exit.widthProperty().multiply(0.25));
		
		exit.setOnAction(e -> {
			hsc.switchtoHomeScreen(e);
		});
		return exit;
	}
}
