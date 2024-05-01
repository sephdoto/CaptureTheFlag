package org.ctf.ui.controllers;

import java.nio.file.Paths;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.ctf.shared.constants.Constants;

/**
 * This class is used to play music in the background. On creation, it plays a start up sound/song,
 * then the songs get randomly played.
 *
 * @author sistumpf, ysiebenh
 */
public class MusicPlayer {
  MediaPlayer mp;

  public MusicPlayer() {
    start();
  }
  
  public void startShuffle() {
    mp.setOnEndOfMedia(infinitePlay(false));
    mp.setStopTime(mp.getCurrentTime());
  }

  /**
   * This sets the first song as the start up sound/song, then after it ends a random song gets
   * chosen. This repeats forever, till the app is closed.
   *
   * @author sistumpf
   */
  public void start() {
    mp = startUpMusic();
    mp.setOnEndOfMedia(infinitePlay(true));
    mp.play();
  }

  /**
   * plays music in the background
   *
   * @author ysiebenh, sistumpf
   */
  private MediaPlayer getMusic() {
    String trackLocation =
        Paths.get(Constants.toUIResources + Constants.Music.getRandom().getLocation())
            .toUri()
            .toString();
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
    String trackLocation =
        Paths.get(Constants.toUIResources + Constants.Music.STARTUP.getLocation())
            .toUri()
            .toString();
    Media track = new Media(trackLocation);
    MediaPlayer mediaPlayer = new MediaPlayer(track);
    mediaPlayer.setVolume(Constants.musicVolume);
    return mediaPlayer;
  }
  
  /**
   * A Runnable to set as onEndOfMedia for a MediaPlayer.
   * Depending on startScreen the music is playing shuffled or just one start song looped.
   * 
   * @param startScreen if true the start screen song gets looped.
   * @author sistumpf
   * @param startScreen
   * @return
   */
  private Runnable infinitePlay(boolean startScreen) {
    Runnable runnable =
    new Runnable() {
      @Override
      public void run() {
        MusicPlayer.this.mp = startScreen ? startUpMusic() : getMusic();
        mp.setOnEndOfMedia(this);
        mp.play();
      }
    };
    return runnable;
  }
}
