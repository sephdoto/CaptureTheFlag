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
  FULL_AI_POWER("Full AI Power "),
  UI_UPDATE_TIME("UI update time "),
  MAP_OPACITY("map opacity "),
  ANALYZER_AI("Analyzer AI "),
  ANALYZER_THINKING_TIME("Analyzer time "),
  BACKGROUND_OPACITY("Background opacity "),
  GLOW_SPREAD("Glow intensity "),
  RANDOM_SLEEP_TIME("RandomAI wait time "),
  BACKGROUND_FIT_FIX("Background-fit Fix "),
  AI_STATS("Show AI Stats "),
  CLIENT_SLEEP_TIME("Client sleep time "),
  FORCE_THINK_TIME("Forced AI think time "),
  TOURNAMENT_MODE("Is Tournament ");
  
  private String name;
  
  private Settings(String name) {
    this.name = name;
  }
  
  public String getName() {
    return name;
  }
}
