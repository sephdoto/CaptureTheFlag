package org.ctf.ui.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.Themes;

/**
 * MusicPlayer uses a MediaPlayer to play music in the background. 
 * On creation, it plays a start up song, then the songs get randomly played.
 *
 * @author sistumpf, ysiebenh
 */
public class MusicPlayer {
  public static MediaPlayer mp;
  Themes theme;
  static Set<FadeIntoExistence> fadingIn;

  public MusicPlayer() {
    fadingIn = ConcurrentHashMap.newKeySet();
    start();
    this.theme = Constants.theme;
  }

  /**
   * Adjusts the music volume in Constants and for the current player.
   * 
   * @author sistumpf
   * @param volume
   */
  public static void setMusicVolume(double volume) {
    Constants.musicVolume = volume;
    mp.setVolume(volume);
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
   * Fades the currently playing music to a lower volume,
   * waits ms milliseconds, then fades the music loud again.
   * 
   * @param ms milliseconds the fade should take
   */
  public static void shortFade(int totalMs, int muteDivisor, double minVolume) {
    new FadeLow(mp, totalMs, muteDivisor, minVolume).start();;
  }
  
  /**
   * Fades the old song out and creates a new MusicPlayer that slowly fades in.
   * 
   * @author sistumpf
   */
  private void fadeInAndOut() {
    new FadeOutOfExistence(mp, 1500).start();
    mp = getMusic();
    mp.setVolume(0);
    mp.setOnEndOfMedia(infinitePlay(false));
    mp.play();
    FadeIntoExistence fade = new FadeIntoExistence(mp, 3500);
    fade.start();
    fadingIn.add(fade);
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
   * @return a Runnable that plays a new Song and sets this Runnable on end of Media.
   */
  private Runnable infinitePlay(boolean startScreen) {
    Runnable runnable =
        new Runnable() {
      @Override
      public void run() {
        mp = startScreen ? startUpMusic() : getMusic();
        mp.setOnEndOfMedia(this);
        mp.play();
      }
    };
    return runnable;
  }

  /**
   * Fades the music to a certain volume, stays there for a bit, fades it up again.
   * 
   * @author sistumpf
   */
  private static class FadeLow extends FadeIntoExistence {
    int muteDivisor;
    double minVolume;
    
    public FadeLow(MediaPlayer mp, int millisToTake, int muteDivisor, double minVolume) {
      super(mp, millisToTake);
      this.muteDivisor = muteDivisor;
      this.minVolume = minVolume;
    }
    
    @Override
    public void run() {
      for(FadeIntoExistence fade: MusicPlayer.fadingIn)
        fade.allowedToRun = false;
      
      //fade quiet
      for(double changePerMS = (mp.getVolume() - minVolume) / (millisTillFull / (double)muteDivisor); 
          allowedToRun && mp.getVolume() > minVolume; 
          millisTillFull -= 1) {
        mp.setVolume(mp.getVolume() - changePerMS);
        MusicPlayer.sleep(1);
      }
      //stay quiet
      MusicPlayer.sleep((int)Math.round((millisTillFull / (double)muteDivisor) * (muteDivisor -1.5)));
      //fade in again
      new FadeIntoExistence(mp, (int)Math.round(millisTillFull / (double)muteDivisor)).start();
    }
  }
  
  /**
   * Fades a given MusicPlayer out.
   * Steadily decreases its volume till it reaches 0, then stops it.
   * 
   * @author sistumpf
   */
  private static class FadeOutOfExistence extends Thread {
    MediaPlayer mp;
    int millisTillStop;

    public FadeOutOfExistence(MediaPlayer mp, int millis) {
      this.mp = mp;
      this.millisTillStop = millis;
      if(MusicPlayer.fadingIn != null)
        fadingIn.forEach(t -> t.endNow());
    }

    @Override
    public void run() {
      for(int millis = (int)(millisTillStop * mp.getVolume()); millis > 0; millis -= 1) {
        mp.setVolume((double)millis /millisTillStop);
        MusicPlayer.sleep(1);
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
  private static class FadeIntoExistence extends Thread {
    MediaPlayer mp;
    int millisTillFull;
    boolean allowedToRun;

    public FadeIntoExistence(MediaPlayer mp, int millis) {
      this.mp = mp;
      this.millisTillFull = millis;
      this.allowedToRun = true;
    }

    @Override
    public void run() {
      for(int millis = 0; allowedToRun && (double)millis / millisTillFull < Constants.musicVolume; millis += 1) {
        mp.setVolume((double)millis / millisTillFull);
        MusicPlayer.sleep(1);
      }
      MusicPlayer.fadingIn.remove(this);
    }

    public void endNow() {
      this.allowedToRun = false;
    }
  }
  
  /**
   * Thread.sleep but catches the Exception
   * 
   * @author sistumpf
   * @param ms
   */
  private static void sleep(int ms) {
    try {
      Thread.sleep(ms);
    } catch (InterruptedException e) { 
      e.printStackTrace(); 
    }
  }
}
