package org.ctf.ui;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class JoinScene extends Scene {
	HomeSceneController hsc;
	StackPane root;
	StackPane left;

	public JoinScene(HomeSceneController hsc, double width, double height) {
		super(new StackPane(), width, height);
		this.hsc = hsc;
		this.getStylesheets().add(getClass().getResource("MapEditor.css").toExternalForm());
		this.root = (StackPane) this.getRoot();
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
		left = createOptionPane();
		sep.getChildren().add(left);
		StackPane right = createOptionPane();
		sep.getChildren().add(right);
		mainBox.getChildren().add(sep);

		this.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.05; // Beispiel: 5% der Höhe als Spacing
			sep.setSpacing(newSpacing);
		});

		mainBox.getChildren().add(createLeave());
		left.getChildren().add(createLeftcontent());

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

	private VBox createLeftcontent() {
		VBox leftBox = new VBox();
		leftBox.setAlignment(Pos.TOP_CENTER);
		leftBox.setPadding(new Insets(20));
		leftBox.setSpacing(20);
		Text leftheader = new Text("FIND YOUR GAME");
		//leftheader.setStyle("-fx-font-family: Arial; -fx-font-size: 20px; -fx-fill: white;");
		leftheader.getStyleClass().add("custom-header");
		//leftheader.setTextAlignment(TextAlignment.LEFT);
		leftheader.fontProperty().bind(
				Bindings.createObjectBinding(() -> Font.font(leftBox.getWidth() / 18), leftBox.widthProperty()));
		leftBox.getChildren().add(leftheader);
		
		TextField serverIPText = createTextfield("Enter the Server IP");
		leftBox.getChildren().add(serverIPText);
		TextField portText = createTextfield("Enter the Port");
		leftBox.getChildren().add(portText);
		TextField sessionText = createTextfield("Enter the Session ID");
		leftBox.getChildren().add(sessionText);

		return leftBox;
	}

	private TextField createTextfield(String prompt) {
		TextField searchField = new TextField();
		searchField.getStyleClass().add("custom-search-field");
		searchField.setPromptText(prompt);
		searchField.prefHeightProperty().bind(searchField.widthProperty().multiply(0.1));
		searchField.heightProperty().addListener((obs, oldVal, newVal) -> {
			double newFontSize = newVal.doubleValue() * 0.4;
			searchField.setFont(new Font(newFontSize));
		});
		return searchField;
	}
}
