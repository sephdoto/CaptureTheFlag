package org.ctf.ui.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import org.ctf.shared.constants.Constants.SoundType;
import org.ctf.shared.tools.JSON_Tools;
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
  private static String soundFolderLocation;
  private static String linkedSoundsFile;

  
  /**
   * ONLY USED FOR TESTING!! TODO REMOVE THIS
   * @param args
   * @throws JSONException
   * @throws IOException
   */
  public static void main(String args[]) throws JSONException, IOException {
    Platform.startup(() -> {});

//    for(SoundType type : SoundType.values()) {
//      saveSound("Default", type, new File(soundFolderLocation +
//          SoundType.KILL.getLocation() + "Default.wav"), false);
//    }
    playSound("Defa2ult", SoundType.CAPTURE);
        try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
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
   * 
   * @author sistumpf
   * @param piece A pieces name given by the type String from PieceDescription
   * @param type The type the pieces sound belongs to (e.g. Move / Capture)
   */
  public static void playSound(String piece, SoundType type) {
    getSound(piece, type).play();
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
  public static boolean saveSound(String pieceName, SoundType type, File sound, boolean custom) {
    AudioObject audio = new AudioObject(pieceName, type, custom);
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
  private static AudioClip getSound(String piece, SoundType type) {
    try {
    File file = new File(soundFolderLocation + audioClips.get(piece + type.toString()).getLocation());
    return new AudioClip(file.toURI().toString());
    } catch (NullPointerException npe) {
      return new AudioClip(new File(soundFolderLocation + audioClips.get("Default" + type.toString()).getLocation()).toURI().toString());
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
        audioClips.put(sound.getPieceName() + sound.getSoundType(), sound);
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
      JSON_Tools.saveObjectAsJSON(linkedSoundsFile, set, true);
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
    Path targetPath = Paths.get(soundFolderLocation + audio.getLocation());

    try {
      Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
}
