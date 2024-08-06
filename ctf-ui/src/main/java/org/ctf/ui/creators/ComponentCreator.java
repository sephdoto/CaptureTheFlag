package org.ctf.ui.creators;

import java.text.DecimalFormat;
import org.ctf.shared.ai.AIConfig;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.Themes;
import org.ctf.ui.App;
import org.ctf.ui.controllers.MusicPlayer;
import org.ctf.ui.controllers.SettingsSetter;
import org.ctf.ui.customobjects.MovementVisual;
import org.ctf.ui.customobjects.PopUpPane;
import org.ctf.ui.data.SceneHandler;
import org.ctf.ui.editor.EditorScene;
import org.ctf.ui.editor.TemplateEngine;
import org.ctf.ui.hostGame.CreateGameScreenV2;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Provides creator methods which can be used to generate UI components needed in different parts of
 * the application.
 * 
 * @author aniemesc
 * @author sistumpf
 */
public class ComponentCreator {
  public static DecimalFormat df = new DecimalFormat("0.00");

  /**
   * Generates a Window for submitting a map template in an {@link EditorScene}.
   * 
   * @author aniemesc
   * @return StackPane for Submitting templates
   */
  public StackPane createSubmitWindow() {
    PopUpPane popUp = new PopUpPane(SceneHandler.getCurrentScene(), 0.4, 0.4, 0.95);
    VBox vbox = new VBox();
    vbox.setAlignment(Pos.TOP_CENTER);
    vbox.setPadding(new Insets(10));
    vbox.setSpacing(15);
    Text header = EditorScene.createHeaderText(vbox, "Save your Template", 15);
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
    buttonBox.getChildren().add(createLeaveSubmit(vbox, popUp));
    StackPane.setAlignment(buttonBox, Pos.BOTTOM_CENTER);
    vbox.getChildren().add(buttonBox);
    popUp.setContent(vbox);
    return popUp;
  }

  /**
   * Generates resized TextField in the style for the submitting window.
   * 
   * @author aniemesc
   * @param vBox - container used for resizing
   * @return TextField that can be added to the main container
   */
  public static TextField createNameField(VBox vBox) {
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
    nameField.textProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.length() > 20) {
        nameField.setText(oldValue);
      }
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
  protected Text createinfo(VBox vbox) {
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
   * @param vBox - main container of the submitting window
   * @param popUp - submitting window
   * @param info - Text that provides saving information
   * @param nameField - TextField of the submitting window
   * @return Button used for saving templates
   */
  protected Button createSubmit(VBox vBox, PopUpPane popUp, Text info, TextField nameField) {
    Button submit = new Button("Submit");
    submit.getStyleClass().add("save-button");
    submit.setOnAction(e -> {
      if (nameField.getText().equals("")) {
        CreateGameScreenV2.informationmustBeEntered(nameField, "custom-search-field",
            "custom-search-field");
        return;
      }
      if (TemplateEngine.getTemplateNames().contains(nameField.getText())) {
        info.setText(nameField.getText() + " already exits!");
      } else {
        ((EditorScene)SceneHandler.getCurrentScene()).getEngine().saveTemplate(nameField.getText());
        ((EditorScene)SceneHandler.getCurrentScene()).addMapItem(nameField.getText());
        ((EditorScene)SceneHandler.getCurrentScene()).getRootPane().getChildren().remove(popUp);
        ((EditorScene)SceneHandler.getCurrentScene()).inform(nameField.getText() + " was saved!");
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
   * @param vBox - main container of the submitting window
   * @param popUp - submitting window
   * @return Button used for closing the submitting window
   */
  protected Button createLeaveSubmit(VBox vBox, PopUpPane popUp) {
    Button exit = new Button("Abort");
    exit.getStyleClass().add("leave-button");
    exit.setOnAction(e -> {
      ((EditorScene)SceneHandler.getCurrentScene()).getRootPane().getChildren().remove(popUp);
    });
    exit.prefWidthProperty().bind(vBox.widthProperty().multiply(0.25));
    exit.prefHeightProperty().bind(exit.widthProperty().multiply(0.25));
    exit.prefHeightProperty().addListener((obs, oldv, newV) -> {
      double size = newV.doubleValue() * 0.5;
      exit.setFont(Font.font("Century Gothic", size));
    });
    return exit;
  }

  /**
   * Creates a basic grid for a {@link MovementVisual}.
   * 
   * @author aniemesc
   * @param stack - used for resizing
   * @return basic grid for movement visualisation
   */
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
        if (i == 5 && j == 5) {
          c.setFill(Color.RED);
          c.setOpacity(1);
        }
      }
    }
    return grid;
  }

  /**
   * Generates the leave button for a settings window.
   * 
   * @author aniemesc
   * @param vBox - main container of the submitting window
   * @param popUp - submitting window
   * @return Button used for closing the submitting window
   */
  public static Button createControlButton(VBox vBox, String label) {
    Button button = new Button(label);
    button.getStyleClass().add("leave-button");
    button.prefWidthProperty().bind(vBox.widthProperty().multiply(0.3));
    button.prefHeightProperty().bind(button.widthProperty().multiply(0.25));
    button.prefHeightProperty().addListener((obs, oldv, newV) -> {
      double size = newV.doubleValue() * 0.5;
      button.setFont(Font.font("Century Gothic", size));
    });
    return button;
  }

  /**
   * Creates a styled and resizable Text.
   * 
   * @author aniemesc
   * @param vBox - container used for resizing 
   * @param label - displayed String value
   * @param divider - int value for ratio
   * @return
   */
  public static Text createHeaderText(VBox vBox, String label, int divider) {
    Text leftheader = new Text(label);
    leftheader.getStyleClass().add("custom-header");
    leftheader.fontProperty().bind(Bindings.createObjectBinding(
        () -> Font.font("Century Gothic", vBox.getWidth() / divider), vBox.widthProperty()));
    return leftheader;
  }

  /**
   * Creates a styled and resizable {@link Slider} for a settings window.
   * 
   * @param value - initial value for the SLider
   * @param vBox - container used for resizing 
   * @return {@link Slider}
   */
  public static Slider createSlider(double value, VBox vBox) {
    Slider slider = new Slider();
    slider.prefWidthProperty().bind(vBox.widthProperty().multiply(0.3));
    slider.getStyleClass().add("mySlider");
    slider.setMin(0);
    slider.setMax(1);
    slider.setValue(value);
    slider.setBlockIncrement(0.1);
    return slider;
  }
  
  /**
   * Creates an {@link PopUpPane} containing an User Interface to load custom AI configurations.
   * 
   * @param popUpCreator - {@link PopUpCreator} 
   * @return {@link PopUpPane} providing the option to load AI configurations.
   */
  public static PopUpPane createAIWindow(PopUpCreator popUpCreator) {
    PopUpPane popUp = new PopUpPane(SceneHandler.getMainStage().getScene(), 0.6, 0.4, 0.95);
    VBox vbox = new VBox();
    vbox.setAlignment(Pos.TOP_CENTER);
    vbox.setPadding(new Insets(10));
    vbox.widthProperty().addListener((obs, oldVal, newVal) -> {
      double size = newVal.doubleValue() * 0.035;
      vbox.setSpacing(size);
    });
    vbox.getChildren().add(createHeaderText(vbox, "Choose an AI Configuration", 18));
    popUp.setContent(vbox);

    ComboBox<String> configBox = new ComboBox<String>();
    // configBox.getStyleClass().add("custom-combo-box-2");
    for (String name : AIConfig.getTemplateNames()) {
      configBox.getItems().add(name);
    }
    configBox.setValue(configBox.getItems().get(0));
    configBox.prefWidthProperty().bind(vbox.widthProperty().multiply(0.4));
    configBox.prefHeightProperty().bind(configBox.widthProperty().multiply(0.15625));
    configBox.prefHeightProperty().addListener((obs, oldv, newV) -> {
      double size = newV.doubleValue() * 0.4;
      configBox.setStyle("-fx-font-size: " + size + "px;");
    });

    vbox.getChildren().add(configBox);
    HBox sep = new HBox();
    sep.setAlignment(Pos.CENTER);
    sep.widthProperty().addListener((obs, oldVal, newVal) -> {
      double size = newVal.doubleValue() * 0.1;
      sep.setSpacing(size);
    });

    Button select = new Button("Select");
    select.getStyleClass().add("join-button");
    select.setOnAction(e -> {
      popUpCreator.getRoot().getChildren().remove(popUp);
      popUpCreator.getRoot().getChildren()
          .add(popUpCreator.createConfigPane(1, 1, new AIConfig(configBox.getValue())));
    });
    select.prefWidthProperty().bind(vbox.widthProperty().multiply(0.25));
    select.prefHeightProperty().bind(select.widthProperty().multiply(0.25));
    select.prefHeightProperty().addListener((obs, oldv, newV) -> {
      double size = newV.doubleValue() * 0.5;
      select.setFont(Font.font("Century Gothic", size));
    });

    Button leave = createControlButton(vbox, "Back");
    leave.setOnAction(e -> {
      popUpCreator.getRoot().getChildren().remove(popUp);
      popUpCreator.getRoot().getChildren().add(popUpCreator.getAiLevelPopUpPane());
    });
    leave.prefWidthProperty().bind(vbox.widthProperty().multiply(0.25));
    leave.prefHeightProperty().bind(leave.widthProperty().multiply(0.25));
    leave.prefHeightProperty().addListener((obs, oldv, newV) -> {
      double size = newV.doubleValue() * 0.5;
      leave.setFont(Font.font("Century Gothic", size));
    });
    VBox buttonBox = new VBox();
    buttonBox.setAlignment(Pos.TOP_CENTER);
    vbox.widthProperty().addListener((obs, oldVal, newVal) -> {
      double size = newVal.doubleValue() * 0.035;
      buttonBox.setSpacing(size);
    });
    buttonBox.getChildren().addAll(select, leave);
    sep.getChildren().addAll(configBox, buttonBox);
    vbox.getChildren().add(sep);
    return popUp;
  }
}
