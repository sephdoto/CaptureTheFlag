package org.ctf.ui.controllers;

import java.io.File;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.SoundType;
import org.ctf.shared.constants.Enums.Themes;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is used to store information about sounds.
 * It contains the associated piece and the action the sound belongs to,
 * and its location in memory.
 * It also stores if the sound is user-made, if not it can't be altered.
 * 
 * @author sistumpf
 */
class AudioObject {
  private String pieceName;
  private String fileType;
  private SoundType type;
  private Themes theme;
  private boolean custom;

  /**
   * This constructor creates an AudioObject from its defining features.
   * 
   * @author sistumpf
   * @param pieceName The piece's name which is represented by the audioObject.
   * @param type The SoundType associated with the piece's sound
   * @param custom true if the audio is user-made (= it can be deleted by the user)
   */
  public AudioObject(String pieceName, Themes theme, SoundType type, boolean custom) {
    this.pieceName = pieceName;
    this.type = type;
    this.theme = theme;
    this.fileType = ".wav";
    this.custom = custom;
  }

  /**
   * This constructor creates an AudioObject from a JSONObject, representing an AudioObject.
   * 
   * @author sistumpf
   * @param jobject to create the AudioObject from
   */
  public AudioObject(JSONObject jobject) {
    try {
      this.pieceName = jobject.getString("pieceName");
      this.fileType = jobject.getString("fileType");
      this.type = SoundType.valueOf(jobject.getString("type"));
      this.theme = Themes.valueOf(jobject.getString("theme"));
      this.custom = jobject.getBoolean("custom");
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Constructs the Audio Files Location from the information in the AudioObject
   * 
   * @author sistumpf
   * @return location of the addressed audio file.
   */
  public String constructLocation() {
    return SoundController.soundFolderLocation + theme.toString().toLowerCase() + File.separator + type.toString().toLowerCase() + File.separator + pieceName + fileType;
  }

  public String getFileType() {
    return this.fileType;
  }

  public String getPieceName() {
    return this.pieceName;
  }

  public SoundType getSoundType() {
    return this.type;
  }

  public boolean getCustom() {
    return this.custom;
  }
  
  public Themes getTheme() {
    return this.theme;
  }

  /**
   * Custom hashCode method that creates a hashCode from the AudioObjects unique properties.
   * 
   * @author sistumpf
   */
  @Override
  public int hashCode() {
    return (theme.toString() + type.toString() + pieceName + Constants.soundFileTypes).hashCode();
  }

  /**
   * Custom equals method that compares two AudioObjects locations
   * 
   * @author sistumpf
   */
  @Override
  public boolean equals(Object object) {
    return this.hashCode() == ((AudioObject) object).hashCode();
  }
}
