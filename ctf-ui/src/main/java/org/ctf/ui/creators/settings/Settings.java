package org.ctf.ui.creators.settings;

/**
 * All settings components are saved here.
 * If it exists, it can be put into settings.
 * Not everything listed here must be put in, but it should.
 * 
 * @author sistumpf
 */
public enum Settings {
  THEME("Theme "),
  MUSIC("Music Volume "),
  SOUND("Sound Volume "),
  FULLAIPOWER("Full AI Power ");
  
  private String name;
  
  private Settings(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
}
