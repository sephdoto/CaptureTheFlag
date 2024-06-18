package org.ctf.ui.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import javafx.application.Platform;
import javafx.scene.media.AudioClip;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.SoundType;
import org.ctf.shared.constants.Enums.Themes;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.util.StringUtils;

/**
 * This classes purpose is playing sounds that are linked to pieces and their actions.
 * Custom sounds can be created and removed by the user.
 * Only .wav files are supported, as other file types caused issues (JavaFX's fault).
 * 
 * @author sistumpf
 */
public class SoundController {
  public static String soundFolderLocation;
  private static String linkedSoundsFile;


  /**
   * ONLY USED FOR TESTING!! TODO REMOVE THIS
   * @param args
   * @throws JSONException
   * @throws IOException
   * @throws InterruptedException 
   */
  /*public static void main(String args[]) throws JSONException, IOException, InterruptedException {
    Platform.startup(() -> {});

//    for(Themes theme : Themes.values()) {
//      for(SoundType type : SoundType.values()) {
//        playSound("Default", type);
//        Thread.sleep(1000);
//      }
    Themes theme = Themes.STARWARS;
      String soundName = "mech-keyboard-02-102918";
      String saveAs = "NextButtonXXX";
      //    Themes theme = Themes.BAYERN;  
      SoundType type = SoundType.MISC;
      saveSound(saveAs, theme, type, new File("C:\\Users\\Simon Stumpf\\Downloads\\" + soundName + ".mp3"), false);
//    }
    //    }
    //    System.out.println("Can I override a default sound? " + 
    //        saveSound("Default", Themes.STARWARS, SoundType.CAPTURE, new File(
    //            soundFolderLocation + "starwars" + File.separator + SoundType.MOVE.getLocation() + "Default.wav"
    //            ), false));
    Thread.sleep(5000);
    Platform.exit();
  }*/

  /**
   * Initializes important folder and file locations.
   *
   *@author sistumpf
   */
  static {
    soundFolderLocation = Constants.toUIResources + "sounds" + File.separator;
    linkedSoundsFile = soundFolderLocation + "linkedSounds.json";
  }

  /**
   * Plays a pieces sound depending on the SoundType.
   * If a piece got no sounds associated with it the SoundTypes default sound is played.
   * Picks the sound for the current theme.
   * 
   * @author sistumpf
   * @param piece A pieces name given by the type String from PieceDescription
   * @param type The type the pieces sound belongs to (e.g. Move / Capture)
   */
  public static void playSound(String piece, SoundType type) {
    AudioClip audio = getSound(piece, Constants.theme, type);
    audio.setVolume(Constants.soundVolume);
    audio.play();
  }

  /**
   * Plays a sound with a file that links to the sound.
   * 
   * @author sistumpf
   * @param file the sound file that gets played
   */
  public static void playSound(File file) {
    try {
      new AudioClip(file.toURI().toString()).play();
    } catch (Exception e) {
      e.printStackTrace();
      //TODO lustigen "kann nicht abgespielt werden" sound abspielen
    }
  }

  /**
   * Plays a pieces sound depending on the SoundType.
   * If a piece got no sounds associated with it the SoundTypes default sound is played.
   * The theme can be specified and doesn't have to be the current theme.
   * 
   * @author sistumpf
   * @param piece A pieces name given by the type String from PieceDescription
   * @param theme The theme the sound belongs to
   * @param type The type the pieces sound belongs to (e.g. Move / Capture)
   */
  public static void playSound(String piece, Themes theme, SoundType type) {
    getSound(piece, theme, type).play();
  }

  /**
   * Copies a Sound File to its according place in ui.resources
   * 
   * @author sistumpf
   * @param pieceName The Pieces name, gets used to name the .wav file and identify it
   * @param type The SoundType, e.g. MOVE or CAPTURE
   * @param sound The file locating the sound.wav to copy
   * @param custom true if its a custom sound, then it can be deleted by the user
   * @return true if the sound got saved successfully
   */
  public static void saveSound(String pieceName, Themes theme, SoundType type, File sound, boolean custom) {
    Path sourcePath = sound.toPath();
    Path targetPath = Paths.get(
        soundFolderLocation 
        + theme.toString().toLowerCase() 
        + File.separator 
        + type.toString().toLowerCase() 
        + File.separator 
        + pieceName 
        + "."
        + StringUtils.getFilenameExtension(sound.getPath())
        );

    try {
      Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Returns the time an Audio takes to play.
   * 
   * @author sistumpf
   * @param piece name of the piece
   * @param type the SoundType
   * @return the total duration in ms
   */
  public static int getMs(String piece, SoundType type) {
    try {
      File file = getSoundFile(piece, Constants.theme, type);
      AudioFile audioMetadata = AudioFileIO.read(file);
      return (int) Math.round(audioMetadata.getAudioHeader().getPreciseTrackLength() * 1000);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }

  /**
   * Returns a pieces sound as AudioClip depending on the SoundType.
   * If a piece got no sounds associated with it the SoundTypes default sound is returned.
   * 
   * @author sistumpf
   * @param piece A pieces name given by the type String from PieceDescription
   * @param type The type the pieces sound belongs to (e.g. Move / Capture)
   * @return the corresponding AudioClip
   */
  private static AudioClip getSound(String piece, Themes theme, SoundType type) {
    try {
      File file = getSoundFile(piece, theme, type);
      return new AudioClip(file.toURI().toString());
    } catch (NullPointerException npe) {
      return new AudioClip(
          new File(getSoundFile("Default", theme, type).toURI().toString()).toURI().toString()
          );
    }
  }

  private static File getSoundFile(String piece, Themes theme, SoundType type) {
    String location = soundFolderLocation + theme.toString().toLowerCase() + File.separator + type.toString().toLowerCase() + File.separator;
    File locationFile = new File(location);
    for(String extension : getAllExtensionsIn(locationFile)) {
      if(new File(locationFile.getAbsolutePath() + File.separator + piece+"."+extension).exists())
        return new File(location + piece + "." + extension);
    }
//    File file = new File(soundFolderLocation + theme.toString().toLowerCase() + File.separator + type.toString().toLowerCase() + File.separator + piece + fileType);
//    return new File();
//    return new File(
//        audioClips.get(piece + theme.toString() + type.toString()).constructLocation()
//        );
    return new File(location + "Default" + "." + "wav");
  }

  /**
   * Reads linkedSounds.json and returns it as a JSONArray
   * 
   * @author sistumpf
   * @return a JASONArray containing all linkedSounds.json objects
   * @throws JSONException
   * @throws IOException
   */
  private static JSONArray readSoundJSON() throws JSONException, IOException {
    try {
      return new JSONArray(Files.readString(Paths.get(linkedSoundsFile), StandardCharsets.UTF_8));
    } catch (Exception e) {
      e.printStackTrace();
      return new JSONArray();
    }
  }
  
  /**
   * Returns all file extensions in a given directory
   * 
   * @author sistumpf
   * @param dir the directory which gets scanned for the extensions
   * @return all possible extensions
   */
  private static HashSet<String> getAllExtensionsIn(File dir) {
    HashSet<String> extSet = new HashSet<String>();
    if(!dir.isDirectory())
      return extSet;
    
    for(File file : dir.listFiles())
      extSet.add(StringUtils.getFilenameExtension(file.getPath()));
    
    return extSet;
  }
}
