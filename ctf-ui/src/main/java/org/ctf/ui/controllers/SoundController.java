package org.ctf.ui.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;
import javafx.application.Platform;
import javafx.scene.media.AudioClip;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.SoundType;
import org.ctf.shared.constants.Enums.Themes;
import org.ctf.shared.tools.JsonTools;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * This classes purpose is playing sounds that are linked to pieces and their actions.
 * Custom sounds can be created and removed by the user.
 * Only .wav files are supported, as other file types caused issues (JavaFX's fault).
 * 
 * @author sistumpf
 */
public class SoundController {
  private static HashMap<String, AudioObject> audioClips;
  public static String soundFolderLocation;
  private static String linkedSoundsFile;


  /**
   * ONLY USED FOR TESTING!! TODO REMOVE THIS
   * @param args
   * @throws JSONException
   * @throws IOException
   * @throws InterruptedException 
   */
  public static void main(String args[]) throws JSONException, IOException, InterruptedException {
    Platform.startup(() -> {});

//    for(Themes theme : Themes.values()) {
//      for(SoundType type : SoundType.values()) {
    String soundName = "Button";
    String saveAs = "Button";
    Themes theme = Themes.BAYERN;  
    SoundType type = SoundType.MISC;
        System.out.println("Sound can be changed? " + soundCanBeChanged(saveAs, theme, type));
//        System.out.println(saveSound(saveAs, theme, type, new File("D:\\Musik\\Audacity\\" + soundName + ".wav"), false));
//      }
//    }
    playSound("Button", SoundType.MISC);
    System.out.println("Is default sound? " + isDefaultSound("notexisting", Themes.STARWARS, SoundType.CAPTURE));
    System.out.println("Can I override a default sound? " + 
        saveSound("Default", Themes.STARWARS, SoundType.CAPTURE, new File(
            soundFolderLocation + "starwars" + File.separator + SoundType.MOVE.getLocation() + "Default.wav"
            ), false));
    Thread.sleep(5000);
    Platform.exit();
  }

  /**
   * Initializes important folder and file locations,
   * also initializes {@link audioClips}, the HashMap containing all saved AudioObjects.
   *
   *@author sistumpf
   */
  static {
    soundFolderLocation = Constants.toUIResources + "sounds" + File.separator;
    linkedSoundsFile = soundFolderLocation + "linkedSounds.json";
    initAudioClips();
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
   * Returns if the requested Sound falls back to a default sound.
   * This might be because the Default sound is requested or there is no custom sound yet.
   * 
   * @author sistumpf
   * @param piece A pieces name given by the type String from PieceDescription
   * @param theme The theme the sound belongs to
   * @param type The type the pieces sound belongs to (e.g. Move / Capture)
   * @return true if a Default sound is used
   */
  public static boolean isDefaultSound(String piece, Themes theme, SoundType type) {
    if(piece.equals("Default"))
      return true;
    return !audioClips.containsKey(piece + theme + type);
  }

  /**
   * Returns if the sound is custom made by the user and can be changed,
   * or its a developer made sound that should stay and never be changed.
   * 
   * @param piece A pieces name given by the type String from PieceDescription
   * @param theme The theme the sound belongs to
   * @param type The type the pieces sound belongs to (e.g. Move / Capture)
   * @return true if the user can change this sound
   */
  public static boolean soundCanBeChanged(String piece, Themes theme, SoundType type) {
    if(!audioClips.containsKey(piece + theme + type))
      return true;
    return audioClips.get(piece + theme + type).getCustom();
  }

  /**
   * Creates an AudioObject.class Object from a given sound and its information,
   * copies it to its according place in ui.resources, adds it to linkedSounds.json
   * and reinitializes {@link audioClips}.
   * 
   * @author sistumpf
   * @param pieceName The Pieces name, gets used to name the .wav file and identify it
   * @param type The SoundType, e.g. MOVE or CAPTURE
   * @param sound The file locating the sound.wav to copy
   * @param custom true if its a custom sound, then it can be deleted by the user
   * @return true if the sound got saved successfully
   */
  public static boolean saveSound(String pieceName, Themes theme, SoundType type, File sound, boolean custom) {
    if(!soundCanBeChanged(pieceName, theme, type))
      return false;

    AudioObject audio = new AudioObject(pieceName, theme, type, custom);
    if (!copyAudioFile(sound, audio)) return false;
    return addToJSON(audio);
  }

  /**
   * Returns a pieces sound as AudioClip depending on the SoundType.
   * If a piece got no sounds associated with it the SoundTypes default sound is returned.
   * 
   * @author sistumpf
   * @param piece A pieces name given by the type String from PieceDescription
   * @param type The type the pieces sound belongs to (e.g. Move / Capture)
   */
  private static AudioClip getSound(String piece, Themes theme, SoundType type) {
    try {
      File file = new File(
          audioClips.get(piece + theme.toString() + type.toString()).constructLocation()
          );
      return new AudioClip(file.toURI().toString());
    } catch (NullPointerException npe) {
      return new AudioClip(
          new File(
              audioClips.get(
                  "Default" + theme.toString() + type.toString()
                  ).constructLocation()).toURI().toString()
          );
    }
  }

  /**
   * Loads all in linkedSounds.json referenced sounds into {@link audioClips}.
   *
   * @author sistumpf
   */
  private static void initAudioClips() {
    audioClips = new HashMap<String, AudioObject>();
    JSONArray jarray = null;
    try {
      jarray = readSoundJSON();
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    for (int i = 0; i < jarray.length(); i++) {
      try {
        AudioObject sound = new AudioObject(jarray.getJSONObject(i));
        audioClips.put(sound.getPieceName() + sound.getTheme() + sound.getSoundType(), sound);
      } catch (JSONException je) {
        je.printStackTrace();
      }
    }
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
   * Adds an AudioObject to linkedSounds.json
   * 
   * @author sistumpf
   * @param audio The new AudioObject to add to linkedSounds.json
   * @return true if the AudioObject got successfully added to linkedSounds.json
   */
  private static boolean addToJSON(AudioObject audio) {
    JSONArray jarray = null;
    try {
      jarray = readSoundJSON();
      HashSet<AudioObject> set = new HashSet<AudioObject>();
      for (int i = 0; i < jarray.length(); i++) set.add(new AudioObject(jarray.getJSONObject(i)));
      set.add(audio);
      JsonTools.saveObjectAsJSON(linkedSoundsFile, set, true);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    initAudioClips();
    return true;
  }

  /**
   * Copies a given file into its according SoundType folder.
   * The file gets named after the AudioObjects pieceName.
   * The file should be a .wav, we got strange errors with mp3.
   * 
   * @author sistumpf
   * @param audioFile the file that gets copied
   * @param audio containing the name and new location of the file
   * @return true if the file got copied successfully
   */
  private static boolean copyAudioFile(File audioFile, AudioObject audio) {
    Path sourcePath = audioFile.toPath();
    Path targetPath = Paths.get(audio.constructLocation());

    try {
      Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
}
