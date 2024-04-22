package org.ctf.ui;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class EditorScene extends Scene {
	HomeSceneController hsc;
	StackPane root;
	Parent[] options;
	StackPane leftPane;
	
	public EditorScene(HomeSceneController hsc, double width, double height) {
		super(new StackPane(), width, height);
		this.hsc = hsc;
		this.getStylesheets().add(getClass().getResource("MapEditor.css").toExternalForm());
		this.root = (StackPane) this.getRoot();
		options = new Parent[3];
		options[0] = createMapChooser();
		options[1] = createFigureChooser();
		options[2] = createFigureCustomizer();
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
		VBox leftControl = new VBox();
		leftControl.getChildren().add(createSearch());
		leftControl.getChildren().add(createLeftPane());
		sep.getChildren().add(leftControl);
		mainBox.getChildren().add(sep);
		
	}
	
	private ImageView createHeader() {
		Image mp = new Image(getClass().getResourceAsStream("EditorImage.png"));
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
	
	private GridPane createMapChooser() {
		return null;
	}
	private GridPane createFigureChooser() {
		return null;
	}
	private GridPane createFigureCustomizer() {
		return null;
	}
	
	private StackPane createLeftPane() {
		StackPane pane = new StackPane();
		pane.getStyleClass().add("option-pane");
		pane.setPrefSize(250, 250);
		pane.prefWidthProperty().bind(this.widthProperty().multiply(0.4));
		pane.prefHeightProperty().bind(pane.widthProperty().multiply(0.8));

		return pane;
	}
	
	private Button createControlButton(String label) {
		Button but = new Button(label);
		but.getStyleClass().add("leave-button");
		but.prefWidthProperty().bind(root.widthProperty().multiply(0.1));
		but.prefHeightProperty().bind(but.widthProperty().multiply(0.3));
		but.fontProperty().bind(Bindings.createObjectBinding(
				() -> Font.font("Century Gothic", but.getHeight() * 0.25), but.heightProperty()));
		return but;
	}
	private Button createSearch() {
		Button search = new Button("Search");
		search.getStyleClass().add("leave-button");
		search.prefWidthProperty().bind(root.widthProperty().multiply(0.15));
		search.prefHeightProperty().bind(search.widthProperty().multiply(0.25));
		search.fontProperty().bind(Bindings.createObjectBinding(
				() -> Font.font("Century Gothic", search.getHeight() * 0.4), search.heightProperty()));
		return search;
	}
}
