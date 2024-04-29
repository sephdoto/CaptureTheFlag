package org.ctf.shared.constants;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Constants class to hold control variables
 *
 * @author sistumpf
 */
public class Constants {
  //TODO: add "jar:" before all path strings. then everything should work, even if in a jar.
  
  // package map, class JSON_Tools
  public static String mapTemplateFolder =
      Paths.get("").toAbsolutePath().toString().split("cfp14")[0]
          + "cfp14"
          + File.separator
          + "cfp-service-main"
          + File.separator
          + "src"
          + File.separator
          + "main"
          + File.separator
          + "resources"
          + File.separator
          + "maptemplates"
          + File.separator;

  public static String toUIResources = Paths.get("").toAbsolutePath().toString().split("cfp14")[0]
          + "cfp14"
          + File.separator
          + "ctf-ui"
          + File.separator
          + "ressources"
          + File.separator;
  
  public static double soundVolume = 0.4;
  /**
   * This enum contains the different Sound Types and their locations in the project.
   */
  public enum SoundType {
    MOVE("move"),
    KILL("kill"), 
    CAPTURE("capture"), 
    SELECT("select"), 
    DESELECT("deselect"), 
    MISC("misc");
    
    private final String location;
    
    SoundType(final String location){
      this.location = location + File.separator;
    }
    
    public String getLocation() {
      return this.location;
    }
  }
  
  public static double musicVolume = 0.4;
  /**
   * This Enum contains all songs and their locations.
   * Songs in easterEggs wont be returned by getRandom.
   * 
   * @author sistumpf
   */
  public enum Music {
    ELEVATOR("theelevatorbossanova.mp3"), 
    THE_CLONES_THEME("TheClonesTheme-Lofi.mp3"),    //https://www.youtube.com/watch?v=8jXK8fVR8u0
    VODE_AN("VodeAn-Lofi.mp3"),                     //https://www.youtube.com/watch?v=RTv0DGRCyqY
    MERKELWAVE("EverythingIsPossible-Merkelwave.mp3"),  //https://www.youtube.com/watch?v=stFm0ng7DR8
    STARTUP("startup.mp3");                         //https://www.youtube.com/watch?v=Am4wYTiHHx8
    
    private final String text;
    private static final ArrayList<Music> easterEggs = new ArrayList<Music>();
    
    static {
      easterEggs.add(ELEVATOR);
      easterEggs.add(STARTUP);
    }
    
    Music(final String text){
      this.text = text;
    }
    
    public String getLocation() {
      return  "music" + File.separator + text;
    }
    
    public static Music getRandom() {
      Music music;
      do {
        music = values()[(int)(Math.random() * values().length)];
      } while (easterEggs.contains(music));
      return music;
    }
  }
  
  /**
   * Constants needed to make the base URI of the restClient
   *
   * @author rsyed
   */
  public static final String remoteIP = "localhost";

  public static final String remotePort = "8888";
  public static final String remoteBinder = "/api/";

  
  /**
   * Constants needed for file paths
   *
   * @author rsyed
   */
  public static String testTemplate =
  Paths.get("").toAbsolutePath().toString().split("cfp14")[0]
      + "cfp14"
      + File.separator
      + "cfp-service-main"
      + File.separator
      + "src"
      + File.separator
      + "main"
      + File.separator
      + "resources"
      + File.separator
      + "maptemplates"
      + File.separator
      + "serverTester.json";

public static String dataBankPath =
  Paths.get("").toAbsolutePath().toString().split("cfp14")[0]
      + "cfp14"
      + File.separator
      + "cfp-service-main"
      + File.separator
      + "src"
      + File.separator
      + "main"
      + File.separator
      + "resources"
      + File.separator
      + "savedGames"
      + File.separator;


      
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
