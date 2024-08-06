package org.ctf.ui.creators.settings;

import org.ctf.shared.constants.Constants;
import org.ctf.ui.controllers.MusicPlayer;
import org.ctf.ui.creators.ComponentCreator;
import javafx.geometry.Pos;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * A Slider for changing the Music.
 * 
 * @author sistumpf
 */
public class ChooseMusicSlider extends HBox implements ValueExtractable {
  private Slider slider;
  
  public ChooseMusicSlider(VBox settingsBox) {
    createMusicSlider(settingsBox);
    setUserData("music");
  }
  
  public void createMusicSlider(VBox settingsBox) {
    setSpacing(10);
    setAlignment(Pos.CENTER);
    
    Text musicValue = ComponentCreator.createHeaderText(settingsBox, ComponentCreator.df.format(Constants.musicVolume), 28);
    
    slider = ComponentCreator.createSlider(Constants.musicVolume, settingsBox);
    slider.valueProperty().addListener((obs, old, newV) -> {
      MusicPlayer.setMusicVolume(slider.getValue());
      musicValue.setText(ComponentCreator.df.format(slider.getValue()));
    });

    getChildren().addAll(slider, musicValue);
  }
  
  @Override
  public Object getValue() {
    return slider == null ? 0 : slider.getValue();
  }
}
