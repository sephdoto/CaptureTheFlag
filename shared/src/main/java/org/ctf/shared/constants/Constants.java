package org.ctf.shared.constants;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Constants class to hold control variables
 *
 * @author sistumpf
 */
public class Constants {
  // TODO: add "jar:" before all path strings. then everything should work, even if in a jar.
  static String jar = "";//"jar:";
  

  ///////////////////////////////////////////////////////
  //             User changeable things                //
  ///////////////////////////////////////////////////////
  
  public static double soundVolume = 0.4;
  public static double musicVolume = 0.4;

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
  
  
  ///////////////////////////////////////////////////////
  //                      Strings                      //
  ///////////////////////////////////////////////////////

  // to shared.resources folder
  public static String sharedResourcesFolder =
      jar + Paths.get("").toAbsolutePath().toString().split("cfp14")[0]
          + "cfp14"
          + File.separator
          + "shared"
          + File.separator
          + "src"
          + File.separator
          + "main"
          + File.separator
          + "java"
          + File.separator
          + "org"
          + File.separator
          + "ctf"
          + File.separator
          + "shared"
          + File.separator
          + "resources"
          + File.separator;

  // Default folder for saving games for AI Analysis
  public static String saveGameFolder = sharedResourcesFolder + "savegames" + File.separator;
  // package map, class JSON_Tools
  public static String mapTemplateFolder = sharedResourcesFolder + "maptemplates" + File.separator;

  /**
   * Constants needed for file paths
   *
   * @author rsyed
   */
  public static String testTemplate = mapTemplateFolder + "serverTester.json";
  public static String clientTestingTemplate = mapTemplateFolder + "10x10_2teams_example.json";
  public static String clientTimeLimitedTemplate = mapTemplateFolder + "10x10_2teams_example_timeLimited.json";
  public static String clientMoveTimeLimitedTemplate = mapTemplateFolder + "10x10_2teams_example_moveTimeLimited.json";
  public static String toUIResources =
      jar + Paths.get("").toAbsolutePath().toString().split("cfp14")[0]
          + "cfp14"
          + File.separator
          + "ctf-ui"
          + File.separator
          + "resources"
          + File.separator;
  public static String soundFileTypes = ".wav";

  /**
   * Constants needed to make the base URI of the restClient
   *
   * @author rsyed
   */
  public static final String remoteIP = "localhost";
  public static final String remotePort = "8888";
  public static final String remoteBinder = "/api/";

  
  ///////////////////////////////////////////////////////
  //                       ENUMS                       //
  ///////////////////////////////////////////////////////
  
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
