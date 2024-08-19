package org.ctf.ui.creators.settings;

import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.Themes;
import org.ctf.ui.App;
import org.ctf.ui.controllers.MusicPlayer;
import org.ctf.ui.controllers.SettingsSetter;
import org.ctf.ui.creators.ComponentCreator;
import org.ctf.ui.creators.settings.components.ChooseBooleanButton;
import org.ctf.ui.creators.settings.components.ChooseDoubleBox;
import org.ctf.ui.creators.settings.components.ChooseFullAiPowerButton;
import org.ctf.ui.creators.settings.components.ChooseIntegerBox;
import org.ctf.ui.creators.settings.components.ChooseMusicSlider;
import org.ctf.ui.creators.settings.components.ChooseSoundSlider;
import org.ctf.ui.creators.settings.components.ChooseThemeBox;
import org.ctf.ui.creators.settings.components.DoubleBoxFactory;
import org.ctf.ui.creators.settings.components.IntegerBoxFactory;
import org.ctf.ui.customobjects.PopUpPane;
import org.ctf.ui.data.SceneHandler;
import org.ctf.ui.gameAnalyzer.AiAnalyserNew;
import org.ctf.ui.hostGame.PlayGameScreenV2;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Opening and changing the settings window.
 * 
 * @author sistumpf
 */
public abstract class SettingsWindow extends ComponentCreator {
  private PopUpPane popUp;
  private VBox settingsBox;
  private GridPane gridPane;
  private int row;
  private int column;
  
  /**
   * Initializes the background and value holders to put the different settings in.
   * Also initializes a row and column int, to automatically place components.
   * 
   * @author sistumpf
   */
  public SettingsWindow(String title) {
    settingsBox = new VBox();
    settingsBox.setAlignment(Pos.TOP_CENTER);
    settingsBox.setPadding(new Insets(10));
    settingsBox.getChildren().add(createHeaderText(settingsBox, title, 12));
    
    popUp = new PopUpPane(SceneHandler.getCurrentScene(), 0.5, 0.6, 0.7);
    popUp.setContent(settingsBox);
    
    gridPane = new GridPane();
    gridPane.setVgap(15);
    gridPane.setHgap(15);
    settingsBox.getChildren().add(gridPane);
    
    row = 0;
    column = 0;
    
    fillWithContent();
    addSaveAndCancel();
  }
  
  /**
   * Fills the PopUpPane with components, the order they are added will stay.
   */
  public abstract void fillWithContent(); 
  
  /**
   * Adds a component to the gridPane.
   * Depending on setting, the component differs.
   * 
   * @author sistumpf
   * @param setting depending on it, a different node can be chosen to be added.
   */
  protected void addNewComponent(Settings setting) {
    column = 0;
    Text header = createHeaderText(settingsBox, setting.getName(), 18);
    Node node;
    
    switch(setting) {
      case THEME: 
        node = new ChooseThemeBox(settingsBox);
        break;
      case MUSIC: 
        node = new ChooseMusicSlider(settingsBox);
        break;
      case SOUND:
        node = new ChooseSoundSlider(settingsBox);
        break;
      case FULL_AI_POWER:
          node = new ChooseFullAiPowerButton(settingsBox);
          break;
      case UI_UPDATE_TIME:
        node = IntegerBoxFactory.getUiUpdateBox(settingsBox);
        break;
      case MAP_OPACITY:
        node = DoubleBoxFactory.getMapOpacityBox(settingsBox);
        break;
      case ANALYZER_THINKING_TIME:
        node = IntegerBoxFactory.getAiThinkBox(settingsBox);
        break;
      default:
        node = new Text("something went wrong");
    }
    gridPane.add(header, column, ++row);
    GridPane.setHalignment(header, HPos.LEFT);
    GridPane.setHgrow(header, Priority.ALWAYS);
    gridPane.add(node, ++column, row);
    GridPane.setHalignment(node, HPos.RIGHT);
    GridPane.setHgrow(node, Priority.ALWAYS);
  }
  
  /**
   * On exit activation, sets the values from all settings and saves them in Constants.
   * 
   * @author sistumpf
   */
  protected void addSaveOnExit(Button exit) {
    exit.setOnAction(e -> {
      for (Node node : gridPane.getChildren()) {
        if(node.getUserData() == null) continue;
        try {
          switch((String) node.getUserData()) {
            case "theme": Constants.theme = Themes.valueOf((String) ((ChooseThemeBox) node).getValue()); break;
            case "music": Constants.musicVolume = (double) ((ChooseMusicSlider) node).getValue(); break;
            case "sound": Constants.soundVolume = (double) ((ChooseSoundSlider) node).getValue(); break;
            case "fullAiPower": Constants.FULL_AI_POWER = (boolean) ((ChooseBooleanButton) node).getValue(); break;
            case "opacity": 
              Constants.backgroundImageOpacity = (double) ((ChooseDoubleBox) node).getValue(); 
              if(SceneHandler.getCurrentScene() instanceof PlayGameScreenV2)
                ((PlayGameScreenV2)SceneHandler.getCurrentScene()).updateLeftSide();
              break;
            case "updateTime": 
              Constants.UIupdateTime = (int) ((ChooseIntegerBox) node).getValue();
              if(SceneHandler.getCurrentScene() instanceof PlayGameScreenV2)
                ((PlayGameScreenV2)SceneHandler.getCurrentScene()).reinitUiUpdateScheduler();
              break;
            case "aiThinkTime":
              Constants.analyzeTimeInSeconds = (int) ((ChooseIntegerBox) node).getValue();
              if(SceneHandler.getCurrentScene() instanceof AiAnalyserNew)
                ((AiAnalyserNew)SceneHandler.getCurrentScene()).getAnalyzer().setThinkingTime(
                    Constants.analyzeTimeInSeconds * 1111
                    );
              break;
            
            case "booleanButton": System.out.println((boolean) ((ChooseBooleanButton) node).getValue()); break;
            case "doubleBox": System.out.println((double) ((ChooseDoubleBox) node).getValue()); break;
            case "integerBox": System.out.println((int) ((ChooseIntegerBox) node).getValue()); break;
          }
        } catch (IllegalArgumentException | SecurityException e1) {
          e1.printStackTrace();
        }
      }

      SettingsSetter.saveCustomSettings();
      App.chagngeHomescreenBackground();
      ((StackPane)SceneHandler.getCurrentScene().getRoot()).getChildren().remove(popUp);
      SceneHandler.closeSettings();
      exit.setOnAction(null);
    });
  }
  
  /**
   * Adds the save and cancel buttons and their special behavior.
   * 
   * @author sistumpf
   */
  private void addSaveAndCancel() {
    VBox.setMargin(gridPane, new Insets(0, 50, 15, 50));
    Button save = createControlButton(settingsBox, "Save");
    save.setOnAction(null);
    addSaveOnExit(save);
    Button cancel = createControlButton(settingsBox, "Cancel");
    cancel.setOnAction(e -> {
      ((StackPane)SceneHandler.getCurrentScene().getRoot()).getChildren().remove(popUp);
      SettingsSetter.loadCustomSettings();
      MusicPlayer.setMusicVolume(Constants.musicVolume);
      SceneHandler.closeSettings();
    });
    HBox buttonBox = new HBox();
    buttonBox.spacingProperty().bind(settingsBox.widthProperty().multiply(0.05));
    buttonBox.setAlignment(Pos.BOTTOM_CENTER);
    buttonBox.getChildren().addAll(save, cancel);
    
    VBox.setVgrow(buttonBox, Priority.ALWAYS);
    VBox.setMargin(buttonBox, new Insets(10, 0, 10, 0));
    settingsBox.getChildren().add(buttonBox);
  }
  
  /**
   * @author sistumpf
   * @return content of the Settings Window, its PopUpPane.
   */
  public PopUpPane getContent() {
    return popUp;
  }
}
