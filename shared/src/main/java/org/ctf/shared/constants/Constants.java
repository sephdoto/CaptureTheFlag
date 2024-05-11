package org.ctf.shared.constants;

import java.io.File;
import java.nio.file.Paths;

/**
 * Constants class to hold control variables
 *
 * @author sistumpf
 */
public class Constants {
  ///////////////////////////////////////////////////////
  //              important stuff for jar              //
  ///////////////////////////////////////////////////////
  private static final String executionLocationURL = Constants.class.getProtectionDomain().getCodeSource().getLocation().getFile();
  public static final boolean ISJAR = executionLocationURL.endsWith(".jar");
  public static final String JARNAME = executionLocationURL.substring(executionLocationURL.lastIndexOf("/") +1);
  public static final String JARPARENTFOLDER = Paths.get("").toAbsolutePath().toString().split(JARNAME)[0] + File.separator;
  public static final String RESOURCEFOLDERNAME = "resources_ctf_team_14";
  public static final String JARRESOURCES = 
      JARPARENTFOLDER
      + RESOURCEFOLDERNAME
      + File.separator;

  ///////////////////////////////////////////////////////
  //             User changeable things                //
  ///////////////////////////////////////////////////////

  public static double soundVolume = 0.4;
  public static double musicVolume = 0.4;
  public static Enums.Themes theme = Enums.Themes.STARWARS;

  ///////////////////////////////////////////////////////
  //                      Strings                      //
  ///////////////////////////////////////////////////////

  // to resources folder
  private static final String CFP14 = "cfp14";
  public static final String toUIResources = ISJAR ? JARRESOURCES :
    Paths.get("").toAbsolutePath().toString().split(CFP14)[0]
        + CFP14
        + File.separator
        + "ctf-ui"
        + File.separator
        + "src"
        + File.separator
        + "main"
        + File.separator
        + "resources"
        + File.separator;
  public static final String sharedResourcesFolder = toUIResources + "game" + File.separator;

  // to AI config folder
  public static final String aiConfigFolder = sharedResourcesFolder + "ai_configs" + File.separator;

  // Default folder for saving games for AI Analysis
  public static final String saveGameFolder = sharedResourcesFolder + "savegames" + File.separator;
  // package map, class JsonTools
  public static final String aboveMapTemplateFolder = sharedResourcesFolder + "maptemplates" + File.separator;
  public static final String mapTemplateFolder = aboveMapTemplateFolder + "templates" + File.separator;
  /**
   * Constants needed for file paths
   *
   * @author rsyed
   */
  public static final String testTemplate = mapTemplateFolder + "serverTester.json";

  public static  final String clientTestingTemplate = mapTemplateFolder + "10x10_2teams_example.json";
  public static final String clientTimeLimitedTemplate =
      mapTemplateFolder + "10x10_2teams_example_timeLimited.json";
  public static final String clientMoveTimeLimitedTemplate =
      mapTemplateFolder + "10x10_2teams_example_moveTimeLimited.json";
  public static final String soundFileTypes = ".wav";

  // Folder for music
  public static final String musicFolder = Constants.toUIResources + "music" + File.separator;


  /**
   * Constants needed to make the base URI of the restClient
   *
   * @author rsyed
   */
  public static final String remoteIP = "localhost";

  public static final String remotePort = "8888";
  public static final String remoteBinder = "/api/";
}
