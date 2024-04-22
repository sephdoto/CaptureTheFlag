package org.ctf.ui;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * This class represents a JavaFX scene for Joining remote games. It contains
 * all necessary UI components for search game sessions and to join them as a
 * human player or a selected AI client.
 * 
 * @author aniemesc
 */
public class JoinScene extends Scene {
	HomeSceneController hsc;
	StackPane root;
	StackPane left;
	StackPane right;
	Text info;

	/**
	 * This constructor starts the initialization process of the scene and connects
	 * it to a CSS file.
	 * 
	 * @author aniemesc
	 * @param hsc    - HomeSceneController that connects scene to rest of the
	 *               application
	 * @param width  - double value for init width
	 * @param height - double value for init height
	 */
	public JoinScene(HomeSceneController hsc, double width, double height) {
		super(new StackPane(), width, height);
		this.hsc = hsc;
		this.getStylesheets().add(getClass().getResource("MapEditor.css").toExternalForm());
		this.root = (StackPane) this.getRoot();
		createLayout();

	}

	/**
	 * This method creates the basic layout by adding all major top level containers
	 * to the scene.
	 * 
	 * @author aniemesc
	 */
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
		right = createOptionPane();
		sep.getChildren().add(right);
		info = createInfoText("Please enter all \n" + "necessary information \n " + " and search for \n"
				+ " sessions to \n" + "enter a game.", 18);
		right.getChildren().add(info);
		StackPane.setAlignment(info, Pos.CENTER);

		mainBox.getChildren().add(sep);

		this.widthProperty().addListener((observable, oldValue, newValue) -> {
			double newSpacing = newValue.doubleValue() * 0.05; // Beispiel: 5% der Höhe als Spacing
			sep.setSpacing(newSpacing);
		});

		mainBox.getChildren().add(createLeave());
		left.getChildren().add(createLeftcontent());

	}

	/**
	 * This Method creates the header Image for the scene.
	 * 
	 * @author aniemesc
	 * @return ImageView that gets added to the scene
	 */
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

	/**
	 * This Method creates the top level StackPanes for the scene.
	 * 
	 * @author aniemesc
	 * @return StackPane that gets added to the scene
	 */
	public StackPane createOptionPane() {
		StackPane pane = new StackPane();
		pane.getStyleClass().add("option-pane");
		pane.setPrefSize(250, 250);
		pane.prefWidthProperty().bind(this.widthProperty().multiply(0.4));
		pane.prefHeightProperty().bind(pane.widthProperty().multiply(0.8));

		return pane;
	}

	/**
	 * This Method creates the leave button for the scene.
	 * 
	 * @author aniemesc
	 * @return Button that gets added to the scene
	 */
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

	/**
	 * This Method creates the UI components for the StackPane on the left hand side
	 * of the scene.
	 * 
	 * @author aniemesc
	 * @return VBox that gets added to the left StackPane
	 */
	private VBox createLeftcontent() {
		VBox leftBox = new VBox();
		leftBox.setAlignment(Pos.TOP_CENTER);
		leftBox.setPadding(new Insets(20));
		leftBox.setSpacing(left.heightProperty().doubleValue() * 0.06);
		left.heightProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.06;
			leftBox.setSpacing(spacing);
		});
		leftBox.getChildren().add(createLeftHeader(leftBox));

		TextField serverIPText = createTextfield("Enter the Server IP");
		leftBox.getChildren().add(serverIPText);
		TextField portText = createTextfield("Enter the Port");
		leftBox.getChildren().add(portText);
		TextField sessionText = createTextfield("Enter the Session ID");
		leftBox.getChildren().add(sessionText);

		leftBox.getChildren().add(createSearch());

		return leftBox;
	}

	/**
	 * This Method creates the UI components for the StackPane on the right hand
	 * side of the scene.
	 * 
	 * @author aniemesc
	 * @return StackPane that gets added to the right StackPane
	 */
	private VBox createRightContent() {
		VBox rightBox = new VBox();
		rightBox.setAlignment(Pos.TOP_CENTER);
		rightBox.setPadding(new Insets(20));
		rightBox.setSpacing(20);
		left.heightProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.06;
			rightBox.setSpacing(spacing);
		});
		Text rightHeader = new Text("Session Was Found!");
		rightHeader.getStyleClass().add("custom-header");
		rightHeader.fontProperty()
				.bind(Bindings.createObjectBinding(() -> Font.font(right.getWidth() / 18), right.widthProperty()));
		rightBox.getChildren().add(rightHeader);
		rightBox.getChildren().add(createSessionInfo(12345, 4, 20));
		rightBox.getChildren().add(createButtonOption());

		return rightBox;
	}

	/**
	 * This method creates TextFields that can be added to the scene
	 * 
	 * @author aniemesc
	 * @param prompt - String value for the user prompt
	 * @return Textfield that can be added to the scene
	 */
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

	/**
	 * This method creates the header for the left StackPane
	 * 
	 * @author aniemesc
	 * @param leftBox - left StackPane
	 * @return Text that can be added to the left StackPane
	 */
	private Text createLeftHeader(VBox leftBox) {
		Text leftheader = new Text("FIND YOUR GAME");
		leftheader.getStyleClass().add("custom-header");
		leftheader.fontProperty()
				.bind(Bindings.createObjectBinding(() -> Font.font(leftBox.getWidth() / 18), leftBox.widthProperty()));
		return leftheader;
	}

	/**
	 * This Method creates the search button for the left StackPane.
	 * 
	 * @author aniemesc
	 * @return Button that gets added to the left StackPane
	 */
	private Button createSearch() {
		Button search = new Button("Search");
		search.getStyleClass().add("leave-button");
		search.prefWidthProperty().bind(root.widthProperty().multiply(0.15));
		search.prefHeightProperty().bind(search.widthProperty().multiply(0.25));
		search.fontProperty().bind(Bindings.createObjectBinding(
				() -> Font.font("Century Gothic", search.getHeight() * 0.4), search.heightProperty()));
		search.setOnAction(e -> {
			right.getChildren().remove(info);
			right.getChildren().add(createRightContent());
		});
		return search;
	}

	/**
	 * Method that creates styled text that can be used within the scene.
	 * 
	 * @author Aaron Niemesch
	 * @param s - String value input
	 * @param divider - int value that defines font ration in relation to scene
	 * @return Text that can be added to the scene
	 */
	private Text createInfoText(String s, int divider) {
		Text info = new Text(s);
		info.getStyleClass().add("custom-header");
		info.setTextAlignment(TextAlignment.CENTER);
		info.fontProperty().bind(Bindings.createObjectBinding(
				() -> Font.font("Century Gothic", right.getWidth() / divider), right.widthProperty()));
		return info;
	}
	
	/**
	 * Method that creates visual info objects belonging to a game session
	 * 
	 * @author aniemesc
	 * @param id - int value for game ID
	 * @param teams - int value for number of waiting teams
	 * @param space - int value for number of open spots
	 * @return StackPane that gets added to the right StackPane
	 */
	private StackPane createSessionInfo(int id, int teams, int space) {
		StackPane sessionInfoBox = new StackPane();
		sessionInfoBox.getStyleClass().add("session-info");
		sessionInfoBox.prefWidthProperty().bind(this.widthProperty().multiply(0.1));
		sessionInfoBox.prefHeightProperty().bind(sessionInfoBox.widthProperty().multiply(0.1));
		VBox textBox = new VBox();
		textBox.setPadding(new Insets(15));
		textBox.setSpacing(right.heightProperty().doubleValue() * 0.03);
		right.heightProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.03;
			textBox.setSpacing(spacing);
		});
		Text idtext = createInfoText("Session ID:  " + id, 25);
		textBox.getChildren().add(idtext);

		Text teamText = createInfoText("Teams:  " + teams + "/" + space, 25);
		textBox.getChildren().add(teamText);
		sessionInfoBox.getChildren().add(textBox);

		return sessionInfoBox;
	}

	/**
	 * This Method creates the join buttons for the right StackPane.
	 * 
	 * @author aniemesc
	 * @param label - String value for button
	 * @return Button that gets added to the right StackPane
	 */
	private Button createJoinButton(String label) {
		Button join = new Button(label);
		join.getStyleClass().add("join-button");
		join.prefWidthProperty().bind(root.widthProperty().multiply(0.15));
		join.prefHeightProperty().bind(join.widthProperty().multiply(0.25));
		join.fontProperty().bind(Bindings.createObjectBinding(
				() -> Font.font("Century Gothic", join.getHeight() * 0.35), join.heightProperty()));
		return join;
	}
	
	/**
	 * This method creats the layout for the different join options.
	 * 
	 * @author aniemsc
	 * @return GridPane that contains UI components for joining 
	 */
	private GridPane createButtonOption() {
		GridPane buttonBox = new GridPane();
		buttonBox.setAlignment(Pos.CENTER);
		buttonBox.setHgap(right.widthProperty().doubleValue() * 0.05);
		right.widthProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.05;
			buttonBox.setHgap(spacing);
		});
		buttonBox.setVgap(right.heightProperty().doubleValue() * 0.03);
		right.widthProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.03;
			buttonBox.setVgap(spacing);
		});
		Button playerButton = createJoinButton("Join as Player");
		buttonBox.add(playerButton, 0, 0);

		Button aiButton = createJoinButton("Join as AI-Client");
		buttonBox.add(aiButton, 1, 0);

//		Button testButton = createJoinButton("Test");
//		buttonBox.add(testButton,1,1);
		buttonBox.add(createAiChooser(), 1, 1);

		return buttonBox;

	}
	
	/**
	 * This method creates the UI components for choosing an AI client
	 * 
	 * @author aniemesc
	 * @return VBox that can be added to the join layout
	 */
	private VBox createAiChooser() {
		VBox childBox = new VBox();
		childBox.setAlignment(Pos.CENTER);
		right.heightProperty().addListener((obs, oldVal, newVal) -> {
			double spacing = newVal.doubleValue() * 0.03;
			childBox.setSpacing(spacing);
		});
		Text pickText = createInfoText("Pick an AI-Client", 25);
		childBox.getChildren().add(pickText);
		ObservableList<String> options = FXCollections.observableArrayList("MCTS V1", "MCTS V2");
		ComboBox<String> aiComboBox = new ComboBox<>(options);
		aiComboBox.setValue(options.get(0));
		aiComboBox.prefWidthProperty().bind(root.widthProperty().multiply(0.15));
		aiComboBox.prefHeightProperty().bind(aiComboBox.widthProperty().multiply(0.25));
		aiComboBox.getStyleClass().add("custom-combo-box-2");
		childBox.getChildren().add(aiComboBox);
		return childBox;
	}
	
	

}
