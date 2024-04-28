package org.ctf.ui.controllers;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.HashSet;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Constants.SoundType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.scene.media.AudioClip;
import org.json.JSONArray;
import org.json.JSONException;


public class SoundController {
  private static HashMap<String, AudioObject> audioClips;
  private static String soundFolderLocation;
  private static String linkedSoundsFile;
  
  public static void main(String args[]) throws JSONException, IOException {
//    for(SoundType type : SoundType.values()) {
//      saveSound("Default", type, new File(soundFolderLocation + SoundType.CAPTURE.getLocation() + "DefaultTest.mp3"), false);
//    }
    HashMap<String, AudioObject> audioClips = SoundController.audioClips;
    playSound("Default", SoundType.CAPTURE);
    String src = "ressources/sounds/capture/ceeb.mp3";
    AudioClip ac = new AudioClip(Paths.get(src).toAbsolutePath().toUri().toString());
    
    ac.play();
    System.out.println(ac.getVolume() + " " +ac.isPlaying());
  }

  static {
    soundFolderLocation = Constants.toUIResources + "sounds" + File.separator;
    linkedSoundsFile = soundFolderLocation + "linkedSounds.json";
    initAudioClips();
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
    } catch(Exception e) {
      e.printStackTrace();
      return;
    }
    
    for(int i=0; i<jarray.length(); i++) {
      try {
        AudioObject sound = new AudioObject(jarray.getJSONObject(i));
        audioClips.put(sound.getPieceName() + sound.getSoundType(), sound);
      } catch(JSONException je) {
        je.printStackTrace();
      }
    }
  }

  private static JSONArray readSoundJSON() throws JSONException, IOException {
    try {
      return new JSONArray(Files.readString(Paths.get(linkedSoundsFile), StandardCharsets.UTF_8));
    } catch(Exception e) {
      e.printStackTrace();
      return new JSONArray();
    }
  }
  
  private static void saveSoundJSON(HashSet<AudioObject> audioObjects) throws IOException, JSONException {
    Gson gson = new GsonBuilder().setPrettyPrinting().create(); 
    byte[] contentBytes = gson.toJson(audioObjects).getBytes();
    File file = new File(linkedSoundsFile);
    Files.write(file.toPath(), contentBytes);

  }

  public static boolean saveSound(String pieceName, SoundType type, File sound, boolean custom) {
    AudioObject audio = new AudioObject(pieceName, type, custom);
    if(!copyAudioFile(sound, audio))
      return false;
    return addToJSON(audio);
  }
  
  private static boolean addToJSON(AudioObject audio) {
    JSONArray jarray = null;
    try {
      jarray = readSoundJSON();
      HashSet<AudioObject> set = new HashSet<AudioObject>();
      for(int i=0; i<jarray.length(); i++)
        set.add(new AudioObject(jarray.getJSONObject(i)));
      set.add(audio);
      saveSoundJSON(set);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    initAudioClips();
    return true;
  }
  
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

  public static void playSound(String piece, SoundType type) {
    getSound(piece+type.toString()).play();
  }

  private static AudioClip getSound(String key) {
    return new AudioClip(new File(soundFolderLocation + audioClips.get(key).getLocation()).toURI().toString());
  }
}
