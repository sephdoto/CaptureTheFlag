package org.ctf.shared.constants;

import java.io.File;
import java.util.ArrayList;

public class Enums {
  /**
   * This enum contains the above mentioned variables.
   * It safes them with their name to put in the settings.json file.
   * 
   * @author sistumpf
   */
  public enum UserChangeable {
    soundVolume("soundVolume"),
    musicVolume("musicVolume");
    
    
    private final String name;
    private UserChangeable(final String name) {
      this.name = name;
    }
    
    public String getString() {return this.name;};
  }
  
  /**
   * This Enum contains all songs and their locations. Songs in easterEggs wont be returned by
   * getRandom.
   *
   * @author sistumpf
   */
  public enum Music {
    ELEVATOR("theelevatorbossanova.mp3"),
    THE_CLONES_THEME("TheClonesTheme-Lofi.mp3"), // https://www.youtube.com/watch?v=8jXK8fVR8u0
    VODE_AN("VodeAn-Lofi.mp3"), // https://www.youtube.com/watch?v=RTv0DGRCyqY
    MERKELWAVE(
        "EverythingIsPossible-Merkelwave.mp3"), // https://www.youtube.com/watch?v=stFm0ng7DR8
    STARTUP("theforcetheme.mp3"); // https://www.youtube.com/watch?v=Am4wYTiHHx8

    private final String text;
    private static ArrayList<Music> easterEggs = new ArrayList<Music>();

    static {
      easterEggs.add(ELEVATOR);
      easterEggs.add(STARTUP);
    }

    Music(final String text) {
      this.text = text;
    }

    public String getLocation() {
      return "music" + File.separator + text;
    }

    public static Music getRandom() {
      Music music;
      do {
        music = values()[(int) (Math.random() * values().length)];
      } while (easterEggs.contains(music));
      return music;
    }
  }
  
  /** 
   * This enum contains the different Sound Types and their locations in the project.
   *
   * @author sistumpf
   */
  public enum SoundType {
    MOVE("move"),
    KILL("kill"),
    CAPTURE("capture"),
    SELECT("select"),
    DESELECT("deselect"),
    MISC("misc");

    private final String location;

    SoundType(final String location) {
      this.location = location + File.separator;
    }

    public String getLocation() {
      return this.location;
    }
  }
  
  /**
   * Enums for AI Client
   *
   * @author rsyed
   */
  public enum AI {
    HUMAN,
    RANDOM,
    MCTS,
    MCTS_IMPROVED
  }

  /**
   * Optional enums for Port Selection in Client
   *
   * @author rsyed
   */
  public enum Port {
    DEFAULT("8888");

    public final String label;

    private Port(String label) {
      this.label = label;
    }

    public boolean equalsName(String otherEnum) {
      return label.equals(otherEnum);
    }

    public String toString() {
      return this.label;
    }
  }
}
