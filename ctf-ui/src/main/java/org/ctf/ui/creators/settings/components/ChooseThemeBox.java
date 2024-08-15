package org.ctf.ui.creators.settings.components;

import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.Themes;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * A ComboBox containing all different Themes.
 * 
 * @author sistumpf
 */
public class ChooseThemeBox extends ComboBox<String> implements ValueExtractable {
  
  public ChooseThemeBox(VBox settingsBox) {
    createThemeBox(settingsBox);
    setUserData("theme");
  }
  
  private ComboBox<String> createThemeBox(VBox settingsBox){
    HBox.setMargin(this, new Insets(20));
    getStyleClass().add("custom-combo-this-2");
    prefWidthProperty().bind(settingsBox.widthProperty().multiply(0.45));
    prefHeightProperty().bind(widthProperty().multiply(0.18));
    prefHeightProperty().addListener((obs, oldv, newV) -> {
      double size = newV.doubleValue() * 0.35;
      setStyle("-fx-font-size: " + size + "px;");
    });

    Themes[] themes = Themes.values();
    for (Themes st : themes) {
      getItems().add(st.toString());
    }
    setValue(Constants.theme.toString());
    settingsBox.widthProperty().addListener((obs, oldVal, newVal) -> {
      double size = newVal.doubleValue() * 0.035;
      settingsBox.setSpacing(size);
    });
    
    return this;
  }
}
