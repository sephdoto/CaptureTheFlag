package org.ctf.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

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
import org.ctf.ui.customobjects.CostumFigurePain;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

public class CreateGameController {
	
	
	// Data that is necessary to create a GameSession with the Servermanager
	private static String port;
	

	private static  String serverIP;
	private static MapTemplate template;
	private static final String LOCALHOST_STRING = "127.0.0.1";

	
	

	// Servermangaer to create a GameSession and the corresponding session-id
	private static ServerManager serverManager;
	private static String sessionID;

	
	//Max Number of teams that are allowed in this Game, automatically set when template is set
	private static int maxNumberofTeams;
	//Current Number of Teams in the Session
	private static int currentNumberofTeams;
	
	
	private static HashMap<String,  ObjectProperty<Color>> colors = new HashMap<String, ObjectProperty<Color>>();
	
	

	// Client that is used to pull the newest GameState and redraw the GamePane with it
	private static Client mainClient;
	

	private static HashMap<String, CostumFigurePain> lastfigures;

	//List of all Human-Clients on one device
	private static ArrayList<Client> localHumanClients = new ArrayList<Client>();
	//List of all AI-clients on one device
	//private static ArrayList<AIClient> localAIClients = new ArrayList<AIClient>();
	
	
	private static HomeSceneController hsc;
	private static WaitingScene waitingScene;
	
	
	private static String lastTeamName;
	private static String lasttype;
	private static AI lastAitype;
	
	
	

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

	public static void initColorHashMap() {
		for(int i=0; i<CreateGameController.getMaxNumberofTeams(); i++) {
			colors.put(String.valueOf(i), new SimpleObjectProperty<>(Color.BLACK));
		}
	}
	
	/**
	 * Initializes a servermanager with a port,serverIp and template and creates a game session with it
	 * @author Manuel Krakowski
	 */
	public static boolean createGameSession() {
		serverManager = new ServerManager(new CommLayer(), new ServerDetails(serverIP, port), template);
		if (serverManager.createGame()) {
			System.out.println("Session erstellt");
			return true;
		} else {
			System.out.println("None");
			return false;
		}
	}
	
	/**
	 * deltes the GameSession that was created be the ServerManger
	 * @author Manuel Krakowski
	 */
	public static void deleteGame() {
		serverManager.deleteGame();
	}
	
	/**
	 * Creates a Human-Client and enables Auto-join
	 * @author Manuel Krakowski
	 * @param teamName: TeamName of the Client. Selected by the user until the Game starts. 
	 *				     Overwritten by Integer when Game is started
	 *@param isMain: true if the client is used as mainClient, false otherwise
	 * @return human client
	 */
	public static void createHumanClient(String teamName, boolean isMain) {
		sessionID = serverManager.getGameSessionID();
		Client c =
		ClientStepBuilder.newBuilder()
		.enableRestLayer(false)
		.onRemoteHost(serverIP)
		.onPort(port)
		.enableSaveGame(false)
		.enableAutoJoin(sessionID, teamName)
		.build();
		if (isMain) {
			mainClient = c;
		}
		localHumanClients.add(c);
	}
	
	
	public static Client getMainClient() {
		return mainClient;
	}

	public static void setMainClient(Client mainClient) {
		CreateGameController.mainClient = mainClient;
	}

	/**
	 * Creates an AI CLient
	 * @author Manuel Krakowski
	 * @param teamName
	 * @param aitype: one of 4 different Ai-Types
	 * @param config: If Ai is configurable the AI-COnfig, null otherwise
	 * @param isMain: true if the client is used as mainClient, false otherwise
	 * @return
	 */
	public static  void createAiClient(String teamName, AI aitype, AIConfig config, boolean isMain) {
		AIClient aiClient = 
		AIClientStepBuilder.newBuilder()
		.enableRestLayer(false)
		.onRemoteHost(serverIP)
		.onPort(port)
		.aiPlayerSelector(aitype, config)
		.enableSaveGame(false)
		.gameData(sessionID, teamName)
		.build();
		if(isMain) {
			mainClient = aiClient;
		}
		//localAIClients.add(aiClient);
	}
	
	
	
//	public static void startWaitingLobbyThread() {
//		//WaitingLobbyThread waitingLobbyThread = new WaitingLobbyThread(serverManager);
//		//waitingLobbyThread.start();
//	}
	
	public static void updateTeamNumberFromRemote(int currentNumber) {
		currentNumberofTeams = currentNumber;
		waitingScene.setCUrrentTeams(currentNumber);
	}
	
	public static void updateTeamNumberfromLocal() {
		
	}
	
	
	
	
	public static void setServerIP(String serverIP) {
		if(serverIP.equalsIgnoreCase("localhost")) {
			CreateGameController.serverIP = LOCALHOST_STRING;
		}else {
			CreateGameController.serverIP = serverIP;
		}
	}
	
	
	public static String getServerIP() {
		if (serverIP.equals(LOCALHOST_STRING)) {
			URL url;
			try {
				url = new URL("https://api.ipify.org");
				BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
				String ipAddress = reader.readLine();
				System.out.println("Öffentliche IP-Adresse: " + ipAddress);
				reader.close();
				//return ipAddress;
			 return InetAddress.getLocalHost().getHostAddress();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return serverIP;
	}

	
	public static MapTemplate getTemplate() {
		return template;
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
	
	

}
