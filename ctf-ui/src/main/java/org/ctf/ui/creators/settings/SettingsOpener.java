package org.ctf.ui.creators.settings;

public class SettingsOpener {
  public static SettingsWindow getDefaultSettings() {
    SettingsWindow settings = new SettingsWindow("Settings") {
      @Override
      public void fillWithContent() {
        addNewComponent(Settings.THEME);
        addNewComponent(Settings.MUSIC);
        addNewComponent(Settings.SOUND);
        addNewComponent(Settings.TOURNAMENT_MODE);
        addNewComponent(Settings.FULL_AI_POWER);
      }
    };
    
    return settings;
  }
  
  public static SettingsWindow getAdvancedSettings() {
    SettingsWindow settings = new SettingsWindow("Advanced") {
      @Override
      public void fillWithContent() {
        addNewComponent(Settings.UI_UPDATE_TIME);
        addNewComponent(Settings.CLIENT_SLEEP_TIME);
        addNewComponent(Settings.BACKGROUND_OPACITY);
        addNewComponent(Settings.MAP_OPACITY);
        addNewComponent(Settings.BACKGROUND_FIT_FIX);
        addNewComponent(Settings.GLOW_SPREAD);
      }
    };
    
    return settings;
  }
  
  public static SettingsWindow getBdvancedSettings() {
    SettingsWindow settings = new SettingsWindow("Bdvanced") {
      @Override
      public void fillWithContent() {
        addNewComponent(Settings.FORCE_THINK_TIME);
        addNewComponent(Settings.RANDOM_SLEEP_TIME);
        addNewComponent(Settings.ANALYZER_THINKING_TIME);
        addNewComponent(Settings.AI_STATS);
      }
    };
    
    return settings;
  }

}
