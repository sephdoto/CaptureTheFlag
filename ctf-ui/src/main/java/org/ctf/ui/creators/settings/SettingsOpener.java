package org.ctf.ui.creators.settings;

public class SettingsOpener {
  public static SettingsWindow getDefaultSettings() {
    SettingsWindow settings = new SettingsWindow("Settings") {
      @Override
      public void fillWithContent() {
        addNewComponent(Settings.THEME);
        addNewComponent(Settings.MUSIC);
        addNewComponent(Settings.SOUND);
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
        addNewComponent(Settings.MAP_OPACITY);
        addNewComponent(Settings.ANALYZER_THINKING_TIME);
      }
    };
    
    return settings;
  }
}
