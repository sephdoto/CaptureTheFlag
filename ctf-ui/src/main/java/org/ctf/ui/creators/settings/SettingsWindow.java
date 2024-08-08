package org.ctf.ui.creators.settings;

import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.Themes;
import org.ctf.ui.App;
import org.ctf.ui.controllers.MusicPlayer;
import org.ctf.ui.controllers.SettingsSetter;
import org.ctf.ui.creators.ComponentCreator;
import org.ctf.ui.creators.settings.DoubleBoxFactory.ChooseMapOpacityBox;
import org.ctf.ui.creators.settings.IntegerBoxFactory.ChooseUiUpdateTimeIntegerBox;
import org.ctf.ui.customobjects.PopUpPane;
import org.ctf.ui.data.SceneHandler;
import org.ctf.ui.hostGame.PlayGameScreenV2;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * Opening and changing the settings window.
 * 
 * @author sistumpf
 */
public class SettingsWindow extends ComponentCreator {
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
  public SettingsWindow() {
    settingsBox = new VBox();
    settingsBox.setAlignment(Pos.TOP_CENTER);
    settingsBox.setPadding(new Insets(10));
    settingsBox.getChildren().add(createHeaderText(settingsBox, "Settings", 12));
    
    popUp = new PopUpPane(SceneHandler.getCurrentScene(), 0.5, 0.6, 0.7);
    popUp.setContent(settingsBox);
    
    gridPane = new GridPane();
    gridPane.setVgap(15);
    gridPane.setHgap(15);
    settingsBox.getChildren().add(gridPane);
    
    row = 0;
    column = 0;
  }
  
  /**
   * Fills the PopUpPane with components, the order they are added will stay.
   * 
   * @author sistumpf
   * @return {@link PopUpPane} the popUpPane containing all settings
   */
  public StackPane fillWithContent() {
    addNewComponent(Settings.THEME);
    addNewComponent(Settings.MUSIC);
    addNewComponent(Settings.SOUND);
    addNewComponent(Settings.FULLAIPOWER);
    addNewComponent(Settings.UIUPDATETIME);
    addNewComponent(Settings.MAPOPACITY);
    
    addSaveAndCancel();
    return popUp;
  }
  
  /**
   * Adds a component to the gridPane.
   * Depending on setting, the component differs.
   * 
   * @author sistumpf
   * @param setting depending on it, a different node can be chosen to be added.
   */
  private void addNewComponent(Settings setting) {
    column = 0;
    gridPane.add(createHeaderText(settingsBox, setting.getName(), 18), column, ++row);
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
      case FULLAIPOWER:
          node = new ChooseFullAiPowerButton(settingsBox);
          break;
      case UIUPDATETIME:
        node = IntegerBoxFactory.getUiUpdateBox(settingsBox);
        break;
      case MAPOPACITY:
        node = DoubleBoxFactory.getMapOpacityBox(settingsBox);
        break;
      default:
        node = new Text("something went wrong");
    }
    
    gridPane.add(node, ++column, row); 
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
              Constants.backgroundImageOpacity = (double) ((ChooseMapOpacityBox) node).getValue(); 
              if(SceneHandler.getCurrentScene() instanceof PlayGameScreenV2)
                ((PlayGameScreenV2)SceneHandler.getCurrentScene()).updateLeftSide();
              break;
            case "updateTime": 
              Constants.UIupdateTime = (int) ((ChooseUiUpdateTimeIntegerBox) node).getValue();
              if(SceneHandler.getCurrentScene() instanceof PlayGameScreenV2)
                ((PlayGameScreenV2)SceneHandler.getCurrentScene()).reinitUiUpdateScheduler();

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
      SceneHandler.setSettingsOpen(false);
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
      SceneHandler.setSettingsOpen(false);
    });
    HBox buttonBox = new HBox();
    buttonBox.spacingProperty().bind(settingsBox.widthProperty().multiply(0.05));
    buttonBox.setAlignment(Pos.CENTER);
    buttonBox.getChildren().addAll(save, cancel);
    settingsBox.getChildren().add(buttonBox);
  }
}
