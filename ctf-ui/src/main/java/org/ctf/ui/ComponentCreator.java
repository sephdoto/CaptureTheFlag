package org.ctf.ui;

import org.ctf.ui.customobjects.PopUpPane;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Provides creator methods which can be used to generate UI components needed
 * in different parts of the application.
 * 
 * @author aniemesc
 */
public class ComponentCreator {
	EditorScene editorscene;

	/**
	 * Sets the scene of the ComponentCreator.
	 * 
	 * @author aniemesc
	 * @param editorScene - EditorScene object
	 */
	public ComponentCreator(EditorScene editorScene) {
		this.editorscene = editorScene;
	}

	/**
	 * Generates a Window for submitting a map template in an editor scene.
	 * 
	 * @author aniemesc
	 * @return StackPane for Submitting templates
	 */
	public StackPane createSubmitWindow() {
		PopUpPane popUp = new PopUpPane(editorscene, 0.4, 0.4);
		// root.setContent(new Button("Hi"));
		VBox vbox = new VBox();
		vbox.setAlignment(Pos.TOP_CENTER);
		vbox.setPadding(new Insets(10));
		vbox.setSpacing(15);
		Text header = editorscene.createHeaderText(vbox, "Save your Template", 15);
		vbox.getChildren().add(header);
		TextField nameField = createNameField(vbox);
		Text info = createinfo(vbox);
		vbox.getChildren().add(nameField);
		vbox.getChildren().add(info);
		HBox buttonBox = new HBox();
		buttonBox.setAlignment(Pos.CENTER);
		vbox.widthProperty().addListener((obs, oldv, newV) -> {
			double size = newV.doubleValue() * 0.05;
			buttonBox.setSpacing(size);
		});
		vbox.heightProperty().addListener((obs, oldv, newV) -> {
			double size = newV.doubleValue() * 0.1;
			VBox.setMargin(buttonBox, new Insets(size));
		});
		VBox.setMargin(buttonBox, new Insets(25));
		buttonBox.getChildren().add(createSubmit(vbox, popUp, info, nameField));
		buttonBox.getChildren().add(createLeave(vbox, popUp));
		StackPane.setAlignment(buttonBox, Pos.BOTTOM_CENTER);
		vbox.getChildren().add(buttonBox);
		popUp.setContent(vbox);
		return popUp;
	}

	/**
	 * Genreates the Textfield requiered for the submitting window.
	 * 
	 * @author aniemesc
	 * @param vBox - main container of the submitting window
	 * @return Textfield that can be added to the main container
	 */
	private TextField createNameField(VBox vBox) {
		TextField nameField = new TextField();
		nameField.getStyleClass().add("custom-search-field");
		// nameField.setPromptText("Enter a unique Name");
		nameField.setAlignment(Pos.CENTER);
		nameField.maxWidthProperty().bind(vBox.widthProperty().multiply(0.75));
		nameField.prefHeightProperty().bind(nameField.widthProperty().multiply(0.15));
		nameField.heightProperty().addListener((obs, oldVal, newVal) -> {
			double size = newVal.doubleValue() * 0.45;
			nameField.setFont(Font.font("Century Gothic", size));
		});
		return nameField;
	}

	/**
	 * Generates the Text required for the submitting window.
	 * 
	 * @author aniemesc
	 * @param vBox - main container of the submitting window
	 * @return Text that can be added to the main container
	 */
	private Text createinfo(VBox vbox) {
		Text info = new Text("");
		info.getStyleClass().add("custom-info-label");
		vbox.widthProperty().addListener((obs, oldVal, newVal) -> {
			double size = newVal.doubleValue() * 0.035;
			info.setFont(Font.font("Century Gothic", size));
		});
		return info;
	}

	/**
	 * Generates the Button which saves a template in an editor scene.
	 * 
	 * @author aniemesc
	 * @param vBox      - main container of the submitting window
	 * @param popUp     - submitting window
	 * @param info      - Text that provides saving information
	 * @param nameField - TextField of the submitting window
	 * @return Button used for saving templates
	 */
	private Button createSubmit(VBox vBox, PopUpPane popUp, Text info, TextField nameField) {
		Button submit = new Button("Submit");
		submit.getStyleClass().add("save-button");
		submit.setOnAction(e -> {
			if (editorscene.getEngine().getTemplateNames().contains(nameField.getText())) {
				info.setText(nameField.getText() + " already exits!");
			} else {
				editorscene.getEngine().saveTemplate(nameField.getText());
				editorscene.getRootPane().getChildren().remove(popUp);
			}
		});
		submit.prefWidthProperty().bind(vBox.widthProperty().multiply(0.25));
		submit.prefHeightProperty().bind(submit.widthProperty().multiply(0.25));
		submit.prefHeightProperty().addListener((obs, oldv, newV) -> {
			double size = newV.doubleValue() * 0.5;
			submit.setFont(Font.font("Century Gothic", size));
		});
		return submit;
	}

	/**
	 * Generates the leave button for a submitting window.
	 * 
	 * @author aniemesc
	 * @param vBox  - main container of the submitting window
	 * @param popUp - submitting window
	 * @return Button used for closing the submitting window
	 */
	private Button createLeave(VBox vBox, PopUpPane popUp) {
		Button exit = new Button("Abort");
		exit.getStyleClass().add("leave-button");
		exit.setOnAction(e -> {
			editorscene.getRootPane().getChildren().remove(popUp);
		});
		exit.prefWidthProperty().bind(vBox.widthProperty().multiply(0.25));
		exit.prefHeightProperty().bind(exit.widthProperty().multiply(0.25));
		exit.prefHeightProperty().addListener((obs, oldv, newV) -> {
			double size = newV.doubleValue() * 0.5;
			exit.setFont(Font.font("Century Gothic", size));
		});
		return exit;
	}

	public GridPane createCustomVisual(VBox stack) {
		GridPane grid = new GridPane();
		StackPane.setAlignment(grid, Pos.CENTER);
		for (int i = 0; i < 11; i++) {
			for (int j = 0; j < 11; j++) {
				StackPane pane = new StackPane();
				pane.setStyle("-fx-border-color: rgba(0,0,0,1);" + "	 -fx-border-width: 2px;"
						+ "	 -fx-background-color: rgba(255,255,255);");
				pane.prefWidthProperty().bind(stack.widthProperty().divide(11));
				pane.prefHeightProperty().bind(stack.heightProperty().divide(11));
				grid.add(pane, i, j);
				Circle c = new Circle();
				c.setFill(Color.BLACK);
				c.setOpacity(0);
				c.radiusProperty().bind(pane.widthProperty().multiply(0.35));
				pane.getChildren().add(c);
				if(i==5&&j==5) {
					c.setFill(Color.RED);
					c.setOpacity(1);

				}
				
			}
		}
		
		return grid;
	}
}
