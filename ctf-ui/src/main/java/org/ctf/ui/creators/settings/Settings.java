package org.ctf.ui.creators.settings;

public enum Settings {
  THEME("Theme"),
  MUSIC("Music Volume"),
  SOUND("Sound Volume");
  
  private String name;
  
  private Settings(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
}
