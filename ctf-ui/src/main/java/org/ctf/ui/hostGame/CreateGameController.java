package org.ctf.ui.hostGame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.ctf.shared.ai.AIConfig;
import org.ctf.shared.client.AIClient;
import org.ctf.shared.client.AIClientStepBuilder;
import org.ctf.shared.client.Client;
import org.ctf.shared.client.ClientStepBuilder;
import org.ctf.shared.client.lib.ServerDetails;
import org.ctf.shared.client.lib.ServerManager;
import org.ctf.shared.client.service.CommLayer;
import org.ctf.shared.constants.Enums.AI;
import org.ctf.shared.state.data.map.MapTemplate;
import org.ctf.ui.controllers.HomeSceneController;
import org.ctf.ui.map.CostumFigurePain;
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


  // Max Number of teams that are allowed in this Game, automatically set when template is set
  private static int maxNumberofTeams;
  // Current Number of Teams in the Session
  private static int currentNumberofTeams;

  // Currently selected team-colors by the user
  private static HashMap<String, ObjectProperty<Color>> colors =
      new HashMap<String, ObjectProperty<Color>>();


  // Client that is used to pull the newest GameState and redraw the GamePane with it
  private static Client mainClient;

  private static HashSet<String> usedTeamNames = new HashSet<String>();
  private static HashMap<String, CostumFigurePain> lastfigures;

  // List of all Human-Clients on one device
  private static ArrayList<Client> localHumanClients = new ArrayList<Client>();
  // List of all AI-Clients on one device
  private static ArrayList<Client> localAIClients = new ArrayList<Client>();
  
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
      Color colorSetByUser = colors.get(String.valueOf(i)).get();
      if (colorSetByUser.equals(Color.BLACK)) {
        String colorString = mainClient.getTeams()[i].getColor();
        Color newColer = Color.web(colorString);
        colors.get(String.valueOf(i)).set(newColer);
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

  /**
   * Creates a Human-Client and enables Auto-join
   * 
   * @author Manuel Krakowski
   * @param teamName: TeamName of the Client. Selected by the user until the Game starts.
   *        Overwritten by Integer when Game is started
   * @param isMain: true if the client is used as mainClient, false otherwise
   * @return human client
   */
  public static void createHumanClient(String teamName, boolean isMain) {
    sessionID = serverManager.getGameSessionID();
    Client c;
    if (!isMain) {
      c = ClientStepBuilder.newBuilder().enableRestLayer(false).onRemoteHost(serverIP).onPort(port)
          .enableSaveGame(false).enableAutoJoin(sessionID, teamName).build();
    } else {
      c = ClientStepBuilder.newBuilder().enableRestLayer(false).onRemoteHost(serverIP).onPort(port)
          .enableSaveGame(true).enableAutoJoin(sessionID, teamName).build();
      setMainClient(c);
    }
    localHumanClients.add(c);
  }



  /**
   * Creates an AI CLient
   * 
   * @author Manuel Krakowski
   * @param teamName
   * @param aitype: one of 4 different Ai-Types
   * @param config: If Ai is configurable the AI-COnfig, null otherwise
   * @param isMain: true if the client is used as mainClient, false otherwise
   * @return
   */
  public static void createAiClient(String teamName, AI aitype, AIConfig config, boolean isMain) {
    sessionID = serverManager.getGameSessionID();
    AIClient aiClient;
    if (!isMain) {
      aiClient = AIClientStepBuilder.newBuilder().enableRestLayer(false).onRemoteHost(serverIP)
          .onPort(port).aiPlayerSelector(aitype, config).enableSaveGame(false)
          .gameData(sessionID, teamName).build();
    } else {
      aiClient = AIClientStepBuilder.newBuilder().enableRestLayer(false).onRemoteHost(serverIP)
          .onPort(port).aiPlayerSelector(aitype, config).enableSaveGame(true)
          .gameData(sessionID, teamName).build();
      setMainClient(aiClient);
    }
    localAIClients.add(aiClient);
  }



  // Getters and Setters
  /////////////////////////////////////////////////////////////////////////////////
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
        System.out.println("Öffentliche IP-Adresse: " + ipAddress);
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

  public static Client getMainClient() {
    return mainClient;
  }

  public static void setMainClient(Client mainClient) {
    mainClient.enableGameStateQueue(true);
    CreateGameController.mainClient = mainClient;
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

  public static ArrayList<Client> getLocalHumanClients() {
    return localHumanClients;
  }

  public static void setLocalHumanClients(ArrayList<Client> localHumanClients) {
    CreateGameController.localHumanClients = localHumanClients;
  }
  
  public static ArrayList<Client> getLocalAIClients() {
    return localAIClients;
  }

  public static void setLocalAIClients(ArrayList<Client> localAIClients) {
    CreateGameController.localAIClients = localAIClients;
  }

  public static HashMap<String, ObjectProperty<Color>> getColors() {
    return colors;
  }

  public static void setColors(HashMap<String, ObjectProperty<Color>> colors) {
    CreateGameController.colors = colors;
  }

  public static HashMap<String, CostumFigurePain> getLastFigures() {
    return lastfigures;
  }

  public static void setFigures(HashMap<String, CostumFigurePain> figures) {
    lastfigures = figures;
  }

  public static void setName(String name) {
    usedTeamNames.add(name);
  }

  public static boolean isNameUsed(String name) {
    return usedTeamNames.contains(name);
  }

  public static void clearLocalClients() {
    localHumanClients.clear();
    localAIClients.clear();
  }
}
