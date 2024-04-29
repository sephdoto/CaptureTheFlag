package org.ctf.ui.controllers;

import java.io.File;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Constants.SoundType;
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
  private String location;
  private SoundType type;
  private boolean custom;

  /**
   * This constructor creates an AudioObject from its defining features.
   * 
   * @author sistumpf
   * @param pieceName The piece's name which is represented by the audioObject.
   * @param type The SoundType associated with the piece's sound
   * @param custom true if the audio is user-made (= it can be deleted by the user)
   */
  public AudioObject(String pieceName, SoundType type, boolean custom) {
    this.pieceName = pieceName;
    this.location = type.toString().toLowerCase() + File.separator + pieceName + Constants.soundFileTypes;
    this.type = type;
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
      this.location = jobject.getString("location");
      this.type = SoundType.valueOf(jobject.getString("type"));
      this.custom = jobject.getBoolean("custom");
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public String getLocation() {
    return location;
  }

  public String getPieceName() {
    return pieceName;
  }

  public SoundType getSoundType() {
    return this.type;
  }

  public boolean getCustom() {
    return this.custom;
  }

  /**
   * Custom hashCode method that creates a hashCode from the AudioObjects location.
   * 
   * @author sistumpf
   */
  @Override
  public int hashCode() {
    return (this.location).hashCode();
  }

  /**
   * Custom equals method that compares two AudioObjects locations
   * 
   * @author sistumpf
   */
  @Override
  public boolean equals(Object object) {
    return (this.location).equals(((AudioObject) object).getLocation());
  }
}
