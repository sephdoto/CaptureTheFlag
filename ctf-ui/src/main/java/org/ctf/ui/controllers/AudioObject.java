package org.ctf.ui.controllers;

import java.io.File;
import org.ctf.shared.constants.Constants.SoundType;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

class AudioObject {
  private String pieceName;
  private String location;
  private SoundType type;
  private boolean custom;

  public AudioObject(String pieceName, SoundType type, boolean custom) {
    this.pieceName = pieceName;
    this.location = type.toString().toLowerCase() + File.separator + pieceName + ".mp3";
    this.type = type;
    this.custom = custom;
  }

  public AudioObject(JSONObject jobject) {
    try {
      this.pieceName = jobject.getString("pieceName");
      this.location = jobject.getString("location");
      this.type = SoundType.valueOf(jobject.getString("type"));
      this.custom = jobject.getBoolean("custom");
    } catch (JSONException e) { e.printStackTrace(); }
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
  
  @Override
  public int hashCode() {
    return (this.type.getLocation() + this.location).hashCode();
  }
  
  @Override
  public boolean equals(Object object) {
    return (this.type + this.location).equals(((AudioObject)object).getSoundType() + ((AudioObject)object).getLocation());
  }

  public JSONObject toJSONObject() {
    JSONObject jobject = new JSONObject();
    try {
      jobject.put("pieceName", this.pieceName);
      jobject.put("location", this.location);
      jobject.put("type", this.type);
      jobject.put("custom", this.custom);
    } catch(JSONException e) {
      e.printStackTrace();
    }
    return jobject;
  }
}