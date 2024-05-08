package org.ctf.shared.constants;

import java.io.File;
import java.nio.file.Paths;

/**
 * Constants class to hold control variables
 *
 * @author sistumpf
 */
public class Constants {
  // TODO: add "jar:" before all path strings. then everything should work, even if in a jar.
  static String jar = ""; // "jar:";

  ///////////////////////////////////////////////////////
  //             User changeable things                //
  ///////////////////////////////////////////////////////

  public static double soundVolume = 0.4;
  public static double musicVolume = 0.4;
  public static Enums.Themes theme = Enums.Themes.STARWARS;

  ///////////////////////////////////////////////////////
  //                      Strings                      //
  ///////////////////////////////////////////////////////

  // to shared.resources folder
  public static String sharedResourcesFolder =
      jar
          + Paths.get("").toAbsolutePath().toString().split("cfp14")[0]
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

  // to AI config folder
  public static String aiConfigFolder = sharedResourcesFolder + "ai_configs" + File.separator;
  
  // Default folder for saving games for AI Analysis
  public static String saveGameFolder = sharedResourcesFolder + "savegames" + File.separator;
  // package map, class JsonTools
  public static String mapTemplateFolder = sharedResourcesFolder + "maptemplates" + File.separator;

  /**
   * Constants needed for file paths
   *
   * @author rsyed
   */
  public static String testTemplate = mapTemplateFolder + "serverTester.json";

  public static String clientTestingTemplate = mapTemplateFolder + "10x10_2teams_example.json";
  public static String clientTimeLimitedTemplate =
      mapTemplateFolder + "10x10_2teams_example_timeLimited.json";
  public static String clientMoveTimeLimitedTemplate =
      mapTemplateFolder + "10x10_2teams_example_moveTimeLimited.json";
  public static String toUIResources =
      jar
          + Paths.get("").toAbsolutePath().toString().split("cfp14")[0]
          + "cfp14"
          + File.separator
          + "ctf-ui"
          + File.separator
          + "resources"
          + File.separator;
  public static String soundFileTypes = ".wav";

  // Folder for music
  public static String musicFolder = Constants.toUIResources + "music" + File.separator;

  public static String dataBankPath;

  /**
   * Constants needed to make the base URI of the restClient
   *
   * @author rsyed
   */
  public static final String remoteIP = "localhost";

  public static final String remotePort = "8888";
  public static final String remoteBinder = "/api/";
}
