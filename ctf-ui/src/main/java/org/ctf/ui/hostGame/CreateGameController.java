package org.ctf.ui.hostGame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import org.ctf.shared.client.Client;
import org.ctf.shared.client.lib.ServerDetails;
import org.ctf.shared.client.lib.ServerManager;
import org.ctf.shared.client.service.CommLayer;
import org.ctf.shared.constants.Enums.AI;
import org.ctf.shared.state.data.map.MapTemplate;
import org.ctf.ui.controllers.HomeSceneController;
import org.ctf.ui.data.ClientStorage;
import org.ctf.ui.map.CustomFigurePane;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;


/**
 * Used to control all the data to communicate between the scenes in the create-game-proces Used to
 * create and store all clients
 * 
 * @author Manuel Krakowski
 */
public class CreateGameController {


  // Data that is necessary to create a GameSession with the Servermanager
  private static String port;
  private static String serverIP;
  private static MapTemplate template;
  private static final String LOCALHOST_STRING = "127.0.0.1";


  // Servermangaer to create a GameSession and the corresponding session-id
  private static ServerManager serverManager;
  private static String sessionID;
  static boolean newSession;


  // Max Number of teams that are allowed in this Game, automatically set when template is set
  private static int maxNumberofTeams;
  // Current Number of Teams in the Session
  @SuppressWarnings("unused")
  private static int currentNumberofTeams;

  // Currently selected team-colors by the user
  private static HashMap<String, ObjectProperty<Color>> colors =
      new HashMap<String, ObjectProperty<Color>>();

  private static HashSet<String> usedTeamNames = new HashSet<String>();
  private static HashMap<String, CustomFigurePane> lastfigures;
  
  // To comunicate between diferent scenes
  private static HomeSceneController hsc;
  private static WaitingScene waitingScene;

  // Data to show the last-player who joined the waiting room
  private static String lastTeamName;
  private static String lasttype;
  private static AI lastAitype;


  /**
   * Initializes the color for every user in the waiting room with black
   * 
   * @author Manuel Krakowski
   */
  public static void initColorHashMap() {
    for (int i = 0; i < CreateGameController.getMaxNumberofTeams(); i++) {
      colors.put(String.valueOf(i), new SimpleObjectProperty<>(Color.BLACK));
    }
  }

  /**
   * Deltes all user selcted colors
   * 
   * @author Manuel Krakowski
   */
  public static void clearColors() {
    colors.clear();
  }

  /**
   * Initializes the colors dirctly with the server colors for remote players
   * 
   * @author Manuel Krakowski
   * @param client client used to get Data from server
   */
  public static void initColorHashMapForRemote(Client client) {
    for (int i = 0; i < client.getTeams().length; i++) {
      String colorString = client.getTeams()[i].getColor();
      Color newColer = Color.web(colorString);
      colors.put(String.valueOf(i), new SimpleObjectProperty<>(newColer));
    }
  }

  /**
   * overwrites black default color if user has no color selcted in waiting-room
   * 
   * @author Manuel Krakowski
   */
  public static void overWriteDefaultWithServerColors() {
    for (int i = 0; i < CreateGameController.getMaxNumberofTeams(); i++) {
      try {
        Color colorSetByUser = colors.get(String.valueOf(i)).get();
        if (colorSetByUser.equals(Color.BLACK)) {
          String colorString;
          if(ClientStorage.getMainClient().getGameSaveHandler().getSavedGame().getInitialState() != null)
            colorString = ClientStorage.getMainClient().getGameSaveHandler().getSavedGame().getInitialState().getTeams()[i].getColor();
          else 
            colorString = ClientStorage.getMainClient().getTeams()[i].getColor();
          Color newColer = Color.web(colorString);
          colors.get(String.valueOf(i)).set(newColer);
        }
      } catch(Exception e) {
        e.printStackTrace();
        colors.get(String.valueOf(i)).set(Color.RED);
      }
    }
  }

  /**
   * Initializes a servermanager with a port,serverIp and template and creates a game session with
   * it
   * 
   * @author Manuel Krakowski
   */
  public static boolean createGameSession() {
    serverManager = new ServerManager(new CommLayer(), new ServerDetails(serverIP, port), template);
    if (serverManager.createGame()) {
      System.out.println("Session erstellt");
      newSession = true;
      System.out.println(serverManager.gameSessionID);
      return true;
    } else {
      System.out.println("None");
      return false;
    }
  }

  /**
   * deltes the GameSession that was created be the ServerManger
   * 
   * @author Manuel Krakowski
   */
  public static void deleteGame() {
    serverManager.deleteGame();
  }

  /////////////////////////////////////////////////////////////////////////////////
  // Getters and Setters
  //////////////////////////////////////////////////////////////////////////////////



  /**
   * Sets the ip-adresse. Is called with data that user entered in textfield if user types in string
   * 'localhost' localhost-adress is used
   * 
   * @author Manuel Krakowski
   * @param serverIP
   */
  public static void setServerIP(String serverIP) {
    if (serverIP.equalsIgnoreCase("localhost")) {
      CreateGameController.serverIP = LOCALHOST_STRING;
    } else {
      CreateGameController.serverIP = serverIP;
    }
  }


  /**
   * Gets the server-ip, can retun both, local and open ip-adress
   * 
   * @author Manuel Krakowski
   * @return
   */
  public static String getServerIP() {
    if (serverIP.equals(LOCALHOST_STRING)) {
      URL url;
      try {
        url = new URL("https://api.ipify.org");
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        String ipAddress = reader.readLine();
        if(newSession)
          System.out.println("Öffentliche IP-Adresse: " + ipAddress);
        newSession = false;
        reader.close();
        // return ipAddress;
        return InetAddress.getLocalHost().getHostAddress();
      } catch (IOException e) {
//        e.printStackTrace();
        System.out.println("no internet connection");
      }
    }
    return serverIP;
  }


  public static MapTemplate getTemplate() {
    return template;
  }

  public static void clearUsedNames() {
    usedTeamNames.clear();
  }

  public static AI getLastAitype() {
    return lastAitype;
  }

  public static void setLastAitype(AI lastAitype) {
    CreateGameController.lastAitype = lastAitype;
  }

  public static void updateTeamNumberFromRemote(int currentNumber) {
    currentNumberofTeams = currentNumber;
    waitingScene.setCUrrentTeams(currentNumber);
  }


  public static String getLasttype() {
    return lasttype;
  }

  public static void setLasttype(String lasttype) {
    CreateGameController.lasttype = lasttype;
  }

  public static String getLastTeamName() {
    return lastTeamName;
  }

  public static void setLastTeamName(String lastTeamName) {
    CreateGameController.lastTeamName = lastTeamName;
  }

  public static void setTemplate(MapTemplate template) {
    CreateGameController.template = template;
    maxNumberofTeams = template.getTeams();
  }

  public static String getPort() {
    return port;
  }

  public static void setPort(String port) {
    CreateGameController.port = port;
  }

  public static HomeSceneController getHsc() {
    return hsc;
  }

  public static void setHsc(HomeSceneController hsc) {
    CreateGameController.hsc = hsc;
  }

  public static WaitingScene getWaitingScene() {
    return waitingScene;
  }

  public static void setWaitingScene(WaitingScene waitingScene) {
    CreateGameController.waitingScene = waitingScene;
  }

  public static int getMaxNumberofTeams() {
    return maxNumberofTeams;
  }

  public static void setMaxNumberofTeams(int maxNumberofTeams) {
    CreateGameController.maxNumberofTeams = maxNumberofTeams;
  }

  public static String getSessionID() {
    return sessionID;
  }

  public static void setSessionID(String sessionID) {
    CreateGameController.sessionID = sessionID;
  }

  public static ServerManager getServerManager() {
    return serverManager;
  }

  public static void setServerManager(ServerManager serverManager) {
    CreateGameController.serverManager = serverManager;
  }

  public static HashMap<String, ObjectProperty<Color>> getColors() {
    return colors;
  }

  public static void setColors(HashMap<String, ObjectProperty<Color>> colors) {
    CreateGameController.colors = colors;
  }

  public static HashMap<String, CustomFigurePane> getLastFigures() {
    return lastfigures;
  }

  public static void setFigures(HashMap<String, CustomFigurePane> figures) {
    lastfigures = figures;
  }

  public static void setName(String name) {
    usedTeamNames.add(name);
  }

  public static boolean isNameUsed(String name) {
    return usedTeamNames.contains(name);
  }
}
