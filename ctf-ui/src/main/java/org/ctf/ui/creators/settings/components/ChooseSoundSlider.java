package org.ctf.ui.creators.settings.components;

import org.ctf.shared.constants.Constants;
import org.ctf.ui.creators.ComponentCreator;
import javafx.geometry.Pos;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * A Slider for changing the Sound.
 * 
 * @author sistumpf
 */
public class ChooseSoundSlider extends HBox implements ValueExtractable {
  private Slider slider;
  
  public ChooseSoundSlider(VBox settingsBox) {
    createSoundSlider(settingsBox);
    setUserData("sound");
  }
  
  public void createSoundSlider(VBox settingsBox) {
    setSpacing(10);
    setAlignment(Pos.CENTER);
    
    Text soundValue = ComponentCreator.createHeaderText(settingsBox, ComponentCreator.df.format(Constants.soundVolume), 28);
    
    slider = ComponentCreator.createSlider(Constants.soundVolume, settingsBox);
    slider.valueProperty().addListener((obs, old, newV) -> {
      Constants.soundVolume = slider.getValue();
      soundValue.setText(ComponentCreator.df.format(slider.getValue()));
    });
    getChildren().addAll(slider, soundValue);
  }
  
  @Override
  public Object getValue() {
    return slider == null ? 0 : slider.getValue();
  }
}
