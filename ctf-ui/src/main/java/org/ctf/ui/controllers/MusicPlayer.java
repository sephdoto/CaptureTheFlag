package org.ctf.ui.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
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
  
  /**
   * Starts shuffling other songs than the startup song.
   * 
   * @author sistumpf
   */
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
    String location = Constants.musicFolder + (startup ? "startup" : "background") + File.separator;
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
}
