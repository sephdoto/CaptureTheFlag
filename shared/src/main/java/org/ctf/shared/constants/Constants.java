package org.ctf.shared.constants;

import java.io.File;
import java.nio.file.Paths;

/**
 * Constants class to hold control variables
 *
 * @author sistumpf
 */
public class Constants {
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
