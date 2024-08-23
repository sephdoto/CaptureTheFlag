package org.ctf.ui.creators.settings;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums;
import org.ctf.shared.tools.JsonTools;
import org.ctf.ui.controllers.MusicPlayer;
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
      JsonTools.saveObjectAsJSON(settingsLocation, createJSONObject(), true);
    } catch(JSONException | IOException jse) {
      jse.printStackTrace();
    }
    
    if(getCurrentPlayer() != null && Constants.theme != getCurrentPlayer().theme)
      getCurrentPlayer().updateTheme();
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
    settingObject.put(Enums.UserChangeable.FULL_AI_POWER.getString(), Constants.FULL_AI_POWER);
    settingObject.put(Enums.UserChangeable.BACKGROUND_OPACITY.getString(), Constants.showBackgrounds);

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
    try { Constants.musicVolume = settingObject.getDouble(Enums.UserChangeable.MUSICVOLUME.getString()); } catch (Exception e) {
      System.err.println("No value for " + Enums.UserChangeable.MUSICVOLUME.getString() + " saved.");
    };
    try { Constants.soundVolume = settingObject.getDouble(Enums.UserChangeable.SOUNDVOLUME.getString()); } catch (Exception e) {
      System.err.println("No value for " + Enums.UserChangeable.SOUNDVOLUME.getString() + " saved.");
    };
    try { Constants.theme = Enums.Themes.valueOf(settingObject.getString(Enums.UserChangeable.THEME.getString())); } catch (Exception e) {
      System.err.println("No value for " + Enums.UserChangeable.THEME.getString() + " saved.");
    };
    try { Constants.FULL_AI_POWER = settingObject.getBoolean(Enums.UserChangeable.FULL_AI_POWER.getString()); } catch (Exception e) {
      System.err.println("No value for " + Enums.UserChangeable.FULL_AI_POWER.getString() + " saved.");
    };
    try { Constants.showBackgrounds = settingObject.getDouble(Enums.UserChangeable.BACKGROUND_OPACITY.getString()); } catch (Exception e) {
      System.err.println("No value for " + Enums.UserChangeable.BACKGROUND_OPACITY.getString() + " saved.");
    };
  }

  /**
   * Returns the current MusicPlayer to change the music.
   * 
   * @return current music player
   */
  public static MusicPlayer getCurrentPlayer() {
    return currentPlayer;
  }
}
