package org.ctf.shared.constants;

import java.io.File;
import java.nio.file.Paths;
import org.ctf.shared.constants.Enums.AI;
import org.ctf.shared.constants.Enums.Themes;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

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
      "app.jar"; //executionLocationURL.substring(executionLocationURL.lastIndexOf("/") +1);
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
   * Global waiting time in ms.
   */
  public static int globalWaitingTime = 3000;
  /**
   * Time in ms the UI waits till it refreshes.
   */
  public static int UIupdateTime = 50;
  /**
   * True to unleash the AIs full power.
   * If true, whilst waiting the AI calculates what the enemy might do to use that later.
   * Might result in an enormous amount of computation power and RAM dedicated to the AI.
   * Only activate if one MUST win.
   */
  public static boolean FULL_AI_POWER = false;

  /**
   * Size of the lastScenes stack that can be accessed by typing "back"
   */
  public static int lastScenesSize = 3;
  /**
   * How many seconds is waited between two "back" commands, before a new lastScene Stack is finalized.
   */
  public static int backSeconds = 5;
  /**
   * Opacity of the map background, can be lowered to increase piece visibility
   */
  public static double backgroundImageOpacity = 0.7;
  /**
   * True if homescreen images should be shown in other scenes backgrounds
   */
  public static double showBackgrounds = 0.5;
  /**
   * Strength of the border glow around figures
   */
  public static double borderGlowSpread = 0.5;
  
  public static boolean useBackgroundResizeFix = false;
 
  ///////////////////////////////////////////////////////
  //                    other                          //
  ///////////////////////////////////////////////////////
  /**
   * Static vars needed for Server
   *
   * @author rsyed
   */
  public static String userSelectedLocalServerPort = "8888";
  
  /**
   * Solid color overlay, CSS gets defined in data.SceneHandler
   */
  public static Region colorOverlay = new Region();
  
  public static Color defaultBGcolor = Color.web("#202F3B");
  /**
   * Background Object used to set the Scene Backgrounds
   */
  public static Background background = new Background(new BackgroundFill(
      defaultBGcolor,
      CornerRadii.EMPTY,
      Insets.EMPTY
      ));      
  
  ///////////////////////////////////////////////////////
  //             AI Player Settings                    //
  ///////////////////////////////////////////////////////
  public static int randomAiSleepTimeMS = 10;
  public static int aiDefaultThinkingTimeInSeconds = 10;
  public static int aiClientDefaultRefreshTime = 1;
  public static Enums.AI analyzeAI = AI.IMPROVED;
  public static int analyzeTimeInSeconds = 3;

  ///////////////////////////////////////////////////////
  //                      Strings                      //
  ///////////////////////////////////////////////////////
  
  // application name (should be cfp14 or CaptureTheFlag)
  private static final String CFP14 = Paths.get("").toAbsolutePath().getParent().getFileName().toString().equals("cfp14") ? "cfp14" : "CaptureTheFlag";
  
  // to resources folder
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
