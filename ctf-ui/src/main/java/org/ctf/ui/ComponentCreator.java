package org.ctf.ui;

import java.text.DecimalFormat;
import org.ctf.shared.ai.AIConfig;
import org.ctf.shared.ai.mcts2.Grid;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.Themes;
import org.ctf.ui.controllers.MusicPlayer;
import org.ctf.ui.controllers.SettingsSetter;
import org.ctf.ui.customobjects.PopUpPane;

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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Provides creator methods which can be used to generate UI components needed in different parts of
 * the application.
 * 
 * @author aniemesc
 */
public class ComponentCreator {
  EditorScene editorscene;
  Scene scene;
  public static DecimalFormat df = new DecimalFormat("0.00"); 

  /**
   * Sets the scene of the ComponentCreator.
   * 
   * @author aniemesc
   * @param editorScene - EditorScene object
   */
  public ComponentCreator(EditorScene editorScene) {
    this.editorscene = editorScene;
  }

  public ComponentCreator(Scene scene) {
    this.scene = scene;
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
    buttonBox.getChildren().add(createLeaveSubmit(vbox, popUp));
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
   * @param vBox - main container of the submitting window
   * @param popUp - submitting window
   * @param info - Text that provides saving information
   * @param nameField - TextField of the submitting window
   * @return Button used for saving templates
   */
  private Button createSubmit(VBox vBox, PopUpPane popUp, Text info, TextField nameField) {
    Button submit = new Button("Submit");
    submit.getStyleClass().add("save-button");
    submit.setOnAction(e -> {
      if(nameField.getText().equals("")) {
        CretaeGameScreenV2.informationmustBeEntered(nameField, "custom-search-field", "custom-search-field");
        return;
      }     
      if (editorscene.getEngine().getTemplateNames().contains(nameField.getText())) {
        info.setText(nameField.getText() + " already exits!");
      } else {
        editorscene.getEngine().saveTemplate(nameField.getText());
        editorscene.addMapItem(nameField.getText());
        editorscene.getRootPane().getChildren().remove(popUp);
        editorscene.inform(nameField.getText()+" was saved!");
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
  private Button createLeaveSubmit(VBox vBox, PopUpPane popUp) {
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
        if (i == 5 && j == 5) {
          c.setFill(Color.RED);
          c.setOpacity(1);
        }
      }
    }
    return grid;
  }

  public StackPane createSettingsWindow(StackPane root) {
    PopUpPane popUp = new PopUpPane(scene, 0.5, 0.6);
    VBox vbox = new VBox();
    vbox.setAlignment(Pos.TOP_CENTER);
    vbox.setPadding(new Insets(10));
   
    vbox.getChildren().add(createHeaderText(vbox, "Settings", 12));
   
    
    
    ComboBox<String> box = new ComboBox<String>();
    HBox.setMargin(box, new Insets(20));
    box.getStyleClass().add("custom-combo-box-2");
    box.prefWidthProperty().bind(vbox.widthProperty().multiply(0.45));
    box.prefHeightProperty().bind(box.widthProperty().multiply(0.18));
    box.prefHeightProperty().addListener((obs, oldv, newV) -> {
      double size = newV.doubleValue() * 0.35;
      box.setStyle("-fx-font-size: " + size + "px;");
    });
    Themes[] themes = Themes.values();
    for (Themes st : themes) {
      box.getItems().add(st.toString());
    }
    
   
    vbox.widthProperty().addListener((obs, oldVal, newVal) -> {
      double size = newVal.doubleValue() * 0.035;
      vbox.setSpacing(size);
      
    });
    
    
    GridPane grid = new GridPane();
    grid.setVgap(15);
    grid.setHgap(15);
   grid.add(box, 1, 0);
    grid.add(createHeaderText(vbox, "Music Volume", 18), 0, 1);
    grid.add(createHeaderText(vbox, "Sound Volume", 18), 0, 2);
    grid.add(createHeaderText(vbox, "Theme", 18), 0, 0);
   
    HBox musicBox = new HBox();
    musicBox.setSpacing(10);
    musicBox.setAlignment(Pos.CENTER);
    
    HBox soundBox = new HBox();
    soundBox.setSpacing(10);
    soundBox.setAlignment(Pos.CENTER);
    
    Text musicValue = createHeaderText(vbox,df.format(Constants.musicVolume), 28);   
    Text soundValue = createHeaderText(vbox,df.format(Constants.soundVolume), 28);
   
    Slider musicSlider = createSlider(Constants.musicVolume,vbox);
    musicSlider.valueProperty().addListener((obs, old, newV) -> {
      MusicPlayer.setMusicVolume(musicSlider.getValue());
      musicValue.setText(df.format(musicSlider.getValue()));
    });
   
    
    Slider soundSlider = createSlider(Constants.soundVolume,vbox);
    soundSlider.valueProperty().addListener((obs, old, newV) -> {
      soundValue.setText(df.format(soundSlider.getValue()));
    });
    musicBox.getChildren().addAll(musicSlider,musicValue);
    grid.add(musicBox, 1, 1);
    soundBox.getChildren().addAll(soundSlider,soundValue);
    grid.add(soundBox, 1, 2);
    vbox.getChildren().add(grid);
   
//    vbox.widthProperty().addListener((obs, oldv, newV) -> {
//      double size = newV.doubleValue() * 0.2;
//      VBox.setMargin(grid, new Insets(15, size, 15, size));
//    });
    VBox.setMargin(grid, new Insets(0, 50, 15, 50));
    Button save = createControlButton(vbox,"Save");
    addSaveListener(save, musicSlider, soundSlider, popUp, root);
    Button cancel = createControlButton(vbox, "Cancel");
    cancel.setOnAction(e -> {
      root.getChildren().remove(popUp);
      SettingsSetter.loadCustomSettings();
      MusicPlayer.setMusicVolume(Constants.musicVolume);
    });
    HBox buttonBox = new HBox();
    buttonBox.spacingProperty().bind(vbox.widthProperty().multiply(0.05));
    buttonBox.setAlignment(Pos.CENTER);
    buttonBox.getChildren().addAll(save,cancel);
    vbox.getChildren().add(buttonBox);
    popUp.setContent(vbox);
    return popUp;
  }

  /**
   * Generates the leave button for a settings window.
   * 
   * @author aniemesc
   * @param vBox - main container of the submitting window
   * @param popUp - submitting window
   * @return Button used for closing the submitting window
   */
  public static Button createControlButton(VBox vBox,String label) {
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

  private void addSaveListener(Button exit, Slider musicSlider, Slider soundSlider, PopUpPane popUp,
      StackPane root) {
    exit.setOnAction(e -> {
      root.getChildren().remove(popUp);
      MusicPlayer.setMusicVolume(musicSlider.getValue());
      Constants.soundVolume = soundSlider.getValue();
      SettingsSetter.saveCustomSettings();
    });
  }

  public static Text createHeaderText(VBox vBox, String label, int divider) {
    Text leftheader = new Text(label);
    leftheader.getStyleClass().add("custom-header");
    leftheader.fontProperty().bind(Bindings.createObjectBinding(
        () -> Font.font("Century Gothic", vBox.getWidth() / divider), vBox.widthProperty()));
    return leftheader;
  }

  private Slider createSlider(double value,VBox vBox) {
    Slider slider = new Slider();
    slider.prefWidthProperty().bind(vBox.widthProperty().multiply(0.3));
    slider.getStyleClass().add("mySlider");
    slider.setMin(0);
    slider.setMax(1);
    slider.setValue(value);
    slider.setBlockIncrement(0.1);
    return slider;
  }
  
  public static PopUpPane createAIWindow(PopUpCreator popUpCreator) {
	  PopUpPane popUp = new PopUpPane(App.getStage().getScene(), 0.6, 0.4);
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
	    //configBox.getStyleClass().add("custom-combo-box-2");
	    for(String name : AIConfig.getTemplateNames()) {
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
	    	popUpCreator.getRoot().getChildren().add(popUpCreator.createConfigPane(1, 1,new AIConfig(configBox.getValue())));   
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
        buttonBox.getChildren().addAll(select,leave);
        sep.getChildren().addAll(configBox,buttonBox);
	    vbox.getChildren().add(sep);
	    return popUp;
  }
}
