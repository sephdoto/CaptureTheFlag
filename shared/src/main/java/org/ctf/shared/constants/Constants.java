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
  /**
   * Auto-generated String containing the jar files execution location
   */
  private static final String executionLocationURL =
      Constants.class.getProtectionDomain().getCodeSource().getLocation().getFile();
  /**
   * Name of the jar. Might be hardcoded to avoid Maven Test failures.
   * TODO remove "app.jar" for exporting as jar.
   */
  public static final String JARNAME =
      "app.jar"; // executionLocationURL.substring(executionLocationURL.lastIndexOf("/") +1);
  /**
   * Automatically determines if the code is run via a jar.
   */
  public static final boolean ISJAR = executionLocationURL.endsWith(JARNAME);
  /**
   * A String Path to the folder the jar is located in.
   */
  public static final String JARPARENTFOLDER =
      Paths.get("").toAbsolutePath().toString().split(JARNAME)[0] + File.separator;
  /**
   * Name of the resource folder that gets generated and used for the jar.
   */
  public static final String RESOURCEFOLDERNAME = "resources_ctf_team_14";
  /**
   * String Path pointing to the resource folder
   */
  public static final String JARRESOURCES = JARPARENTFOLDER + RESOURCEFOLDERNAME + File.separator;

  ///////////////////////////////////////////////////////
  //             User changeable things                //
  ///////////////////////////////////////////////////////
  /**
   * Volume Sounds get played at. 0 to 1
   */
  public static double soundVolume = 0.4;
  /**
   * Volume Music get played at. 0 to 1
   */
  public static double musicVolume = 0.4;
  /**
   * The current {@link Themes}
   */
  public static Enums.Themes theme = Enums.Themes.STARWARS;

  /**
   * Static vars needed for Server and AI Config
   *
   * @author rsyed
   */
  ///////////////////////////////////////////////////////
  //             Server Setting                        //
  ///////////////////////////////////////////////////////
  public static String userSelectedLocalServerPort = "8888";

  ///////////////////////////////////////////////////////
  //             AI Player Settings                    //
  ///////////////////////////////////////////////////////

  public static int aiDefaultThinkingTimeInSeconds = 10;
  public static int aiClientDefaultRefreshTime = 1;

  ///////////////////////////////////////////////////////
  //                      Strings                      //
  ///////////////////////////////////////////////////////

  // to resources folder
  private static final String CFP14 = "CaptureTheFlag";
  public static final String toUIResources =
      ISJAR
          ? JARRESOURCES
          : Paths.get("").toAbsolutePath().toString().split(CFP14)[0]
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
  public static final String toUIStyles = Constants.toUIResources + "styling" + File.separator;
  public static final String sharedResourcesFolder = toUIResources + "game" + File.separator;
  public static final String toUIPictures = Constants.toUIResources + "pictures" + File.separator;

  // to AI config folder
  public static final String aiConfigFolder = sharedResourcesFolder + "ai_configs" + File.separator;
  // F:\VS Code Repo\cfp14\ctf-ui\src\main\resources\game\savegames\analyzerTestDataFile.savedgame
  // Default folder for saving games for AI Analysis
  public static final String saveGameFolder = sharedResourcesFolder + "savegames" + File.separator;
  // package map, class JsonTools
  public static final String aboveMapTemplateFolder =
      sharedResourcesFolder + "maptemplates" + File.separator;
  public static final String mapTemplateFolder =
      aboveMapTemplateFolder + "templates" + File.separator;

  /**
   * Constants needed for file paths
   *
   * @author rsyed
   */
  public static final String testTemplate = mapTemplateFolder + "Default.json";

  public static final String clientTestingTemplate =
      mapTemplateFolder + "10x10_2teams_example.json";
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

  /**
   * Constants needed for Resource Controller
   *
   * @author rsyed
   */
  public static final int BUFFER_SIZE = 8 * 1024;

  public static final String JAR_PREFIX = "jar:file:";

  /**
   * Constants needed to point to the server JAR to start it
   *
   * @author rsyed
   */
  public static final String SERVERJARNAME =
      "server.jar"; // executionLocationURL.substring(executionLocationURL.lastIndexOf("/") +1);

  public static final String START_SERVER_JAR_COMMAND =
      "java -jar \"" + Constants.toUIResources + Constants.SERVERJARNAME + "\" --server.port=8888";
  public static String localServerPort;
}
