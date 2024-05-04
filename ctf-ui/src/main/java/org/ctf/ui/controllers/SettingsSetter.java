package org.ctf.ui.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums;
import org.ctf.shared.tools.JSON_Tools;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This classes public methods should be called to load and save the custom settings.
 * If a new user changeable setting gets added, it must be put in {@link Constants.UserChangeable}
 * and be added in {@link createJSONObject()} and createJSON{@link setCustomSettings()}.
 * 
 * @author sistumpf
 */
public class SettingsSetter {
  private static String settingsLocation = Constants.toUIResources + "settings.json";
  private static MusicPlayer currentPlayer;
  
  /**
   * Saves a given MusicPlayer as the currentPlayer, so SettingsSetter can modify it.
   * When changing the Theme, SettingsSetter can change the background music accordingly.
   * 
   * @author sistumpf
   */
  public static void giveMeTheAux(MusicPlayer currentPlayer) {
    SettingsSetter.currentPlayer = currentPlayer;
  }

  /**
   * Call this to load custom settings from a settings.json file located at {@link settingsLocation}.
   * If no such file exists a new one gets created with the default settings.
   * 
   * @author sistumpf
   */
  public static void loadCustomSettings() {
    try {
      File file = new File(settingsLocation);
      if(!file.exists()) {
        saveCustomSettings();
        return;
      }
      setCustomSettings(new JSONObject(Files.readString(Paths.get(settingsLocation), StandardCharsets.UTF_8)));
    } catch(JSONException | IOException jse) {
      jse.printStackTrace();
    }
  }

  /**
   * Call this to save the current settings into a settings.json file located at {@link settingsLocation}.
   * Also updates the music if necessary.
   * 
   * @author sistumpf
   * @throws JSONException
   * @throws IOException 
   */
  public static void saveCustomSettings() {
    try {
      JSON_Tools.saveObjectAsJSON(settingsLocation, createJSONObject(), true);
    } catch(JSONException | IOException jse) {
      jse.printStackTrace();
    }
    
    if(currentPlayer != null && Constants.theme != currentPlayer.theme)
      currentPlayer.updateTheme();
  }

  /**
   * Returns a JSONObject containing all user changeable settings.
   * Add new changeable things here.
   * 
   * @author sistumpf
   * @return JSONObject containing all changeable settings from Constants
   * @throws JSONException
   */
  private static JSONObject createJSONObject() throws JSONException {
    JSONObject settingObject = new JSONObject();

    settingObject.put(Enums.UserChangeable.MUSICVOLUME.getString(), Constants.musicVolume);
    settingObject.put(Enums.UserChangeable.SOUNDVOLUME.getString(), Constants.soundVolume);
    settingObject.put(Enums.UserChangeable.THEME.getString(), Constants.theme);

    return settingObject;
  }

  /**
   * This method extracts the values out of the json Object and sets them in Constants.
   * Add new changeable things here.
   * 
   * @author sistumpf
   * @param settingObject
   * @throws JSONException
   */
  private static void setCustomSettings(JSONObject settingObject) throws JSONException {
    Constants.musicVolume = settingObject.getDouble(Enums.UserChangeable.MUSICVOLUME.getString());
    Constants.soundVolume = settingObject.getDouble(Enums.UserChangeable.SOUNDVOLUME.getString());
    Constants.theme = Enums.Themes.valueOf(settingObject.getString(Enums.UserChangeable.THEME.getString()));
  }
}
