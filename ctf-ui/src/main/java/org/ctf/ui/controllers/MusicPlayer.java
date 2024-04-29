package org.ctf.ui.controllers;

import java.nio.file.Paths;
import org.ctf.shared.constants.Constants;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * This class is used to play music in the background.
 * On creation, it plays a start up sound/song, then the songs get randomly played.
 * 
 * @author sistumpf, ysiebenh
 */
public class MusicPlayer {
  MediaPlayer mp;
  
  public MusicPlayer() {
    start();
  }
  
  /**
   * This sets the first song as the start up sound/song, then after it ends a random song gets chosen.
   * This repeats forever, till the app is closed.
   * 
   * @author sistumpf
   */
  public void start() {
    mp = startUpMusic();
    mp.setOnEndOfMedia(new Runnable() {
      @Override
      public void run() {
        MusicPlayer.this.mp = getMusic();
        mp.setOnEndOfMedia(this);
        mp.play();
      }});
    mp.play();
  }
  
  /**
   * plays music in the background
   * 
   * @author ysiebenh, sistumpf
   */
  private MediaPlayer getMusic() {
      String trackLocation = Paths.get(Constants.toUIResources + Constants.Music.getRandom().getLocation()).toUri().toString();
      Media track = new Media(trackLocation);
      MediaPlayer mediaPlayer = new MediaPlayer(track);
      mediaPlayer.setVolume(Constants.musicVolume);
      return mediaPlayer;
  }

  /**
   * returns the track to play on startUp
   * 
   * @author sistumpf
   */
  private MediaPlayer startUpMusic() {
      String trackLocation = Paths.get(Constants.toUIResources + Constants.Music.STARTUP.getLocation()).toUri().toString();
      Media track = new Media(trackLocation);
      MediaPlayer mediaPlayer = new MediaPlayer(track);
      mediaPlayer.setVolume(Constants.musicVolume);
      return mediaPlayer;
  }
}
