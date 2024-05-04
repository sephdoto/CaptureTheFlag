package org.ctf.ui.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.Themes;

/**
 * This class is used to play music in the background. On creation, it plays a start up sound/song,
 * then the songs get randomly played.
 *
 * @author sistumpf, ysiebenh
 */
public class MusicPlayer {
  MediaPlayer mp;
  Themes theme;

  public MusicPlayer() {
    start();
    this.theme = Constants.theme;
  }

  /**
   * Starts shuffling other songs than the startup song.
   * Uses the fadeInAndOut Method to do it, as it replaces the old functionality.
   * Instead of switching the two songs with a hard cut they fade in and out now.
   * 
   * @author sistumpf
   */
  public void startShuffle() {
    fadeInAndOut();
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
   * Updates the background music to match the current theme.
   * 
   * @author sistumpf
   */
  public void updateTheme() {
    this.theme = Constants.theme;
    fadeInAndOut();
  }

  /**
   * Fades the old song out and creates a new MusicPlayer that slowly fades in.
   * 
   * @author sistumpf
   */
  private void fadeInAndOut() {
    new FadeOutOfExistence(this.mp, 1000).start();
    this.mp = getMusic();
    this.mp.setVolume(0);
    this.mp.setOnEndOfMedia(infinitePlay(false));
    this.mp.play();
    new FadeIntoExistence(this.mp, 3000).start();
  }

  /**
   * Plays music in the background
   *
   * @author ysiebenh, sistumpf
   */
  private MediaPlayer getMusic() {
    String trackLocation = getMP3(false);
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
    String trackLocation = getMP3(true);
    Media track = new Media(trackLocation);
    MediaPlayer mediaPlayer = new MediaPlayer(track);
    mediaPlayer.setVolume(Constants.musicVolume);
    return mediaPlayer;
  }

  /**
   * Returns a random file name parsed into a path uri.
   * The returned String depends on the current Theme and startup.
   * 
   * @author sistumpf
   * @param startup True if a song for the start screen should be returned
   * @return a uri string, pointing to an existing song in the correct folder
   */
  private String getMP3(boolean startup) {
    String location = Constants.musicFolder + Constants.theme.toString().toLowerCase() 
        + File.separator + (startup ? "startup" : "background") + File.separator;
    try {
      List<Path> list = Files.list(Path.of(location)).toList();
      return list.get(ThreadLocalRandom.current().nextInt(list.size())).toAbsolutePath().toUri().toString();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return "";
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

  /**
   * Fades a given MusicPlayer out.
   * Steadily decreases its volume till it reaches 0, then stops it.
   * 
   * @author sistumpf
   */
  private class FadeOutOfExistence extends Thread {
    MediaPlayer mp;
    int millisTillStop;

    public FadeOutOfExistence(MediaPlayer mp, int millis) {
      this.mp = mp;
      this.millisTillStop = millis;
    }

    public void run() {
      for(int millis = (int)(millisTillStop * Constants.musicVolume); millis > 0; millis--) {
        mp.setVolume((double)millis /millisTillStop);
        try {
          Thread.sleep(1);
        } catch (InterruptedException e) { e.printStackTrace(); }
      }
      mp.stop();
    }
  }

  /**
   * Fades a given MusicPlayer in.
   * Steadily increases its volume till it reaches Constants.musicVolume.
   * 
   * @author sistumpf
   */
  private class FadeIntoExistence extends Thread {
    MediaPlayer mp;
    int millisTillFull;

    public FadeIntoExistence(MediaPlayer mp, int millis) {
      this.mp = mp;
      this.millisTillFull = millis;
    }

    public void run() {
      for(int millis = 0; (double)millis / millisTillFull < Constants.musicVolume; millis++) {
        mp.setVolume((double)millis / millisTillFull);
        try {
          Thread.sleep(1);
        } catch (InterruptedException e) { e.printStackTrace(); }
      }
    }
  }
}
