package org.ctf.ui;

import java.io.BufferedReader;
/**
 * @author mkrakows & aniemesc
This Class controls what happens when clicking the buttons on the HomeScreen
 */
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import org.ctf.shared.client.Client;
import org.ctf.shared.client.ClientStepBuilder;
import org.ctf.shared.client.lib.ServerDetails;
import org.ctf.shared.client.lib.ServerManager;
import org.ctf.shared.client.service.CommLayer;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.data.map.MapTemplate;

import javafx.beans.property.ObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Stop;
import javafx.stage.Stage;


public class HomeSceneController {
	private Stage stage;
	WaitingThread waitingThread;
	String port;
	String serverID;
	String sessionID;
	ServerManager serverManager;
	//TestThread t;
	MapTemplate template;
	CretaeGameScreenV2 createGameScreenV2;
	PlayGameScreenV2 playGameScreenV2;
	WaitingScene waitingScene;
	Client mainClient;
	String teamName;
	String teamTurn;
	public  ObjectProperty<Color> lastcolor;
	boolean mainClientIsHuman;
	
	public void switchtoHomeScreen(ActionEvent e) {
		Scene scene = App.getScene();
		stage = App.getStage();
		stage.setScene(scene);
	}
	
	public HomeSceneController(Stage stage) {
	  this.stage = stage;
	}
	
	public void createGameSession() {
		System.out.println("Hallo ch bin der Manger");
		System.out.println(serverID);
		System.out.println(port);
		serverManager = new ServerManager(new CommLayer(), new ServerDetails(serverID, port),template);
		if(serverManager.createGame()) {
			System.out.println("Session erstellt" );
		}else {
			System.out.println("None");
		}
	}
	
	public void deleteGame() {
		serverManager.deleteGame();
	}
	
	public void createHumanClient() {
		sessionID = serverManager.getGameSessionID();
		System.out.println(teamName);
		mainClient =
		ClientStepBuilder.newBuilder()
		.enableRestLayer(false)
		.onLocalHost()
		.onPort(port)
		.enableSaveGame(false)
		.enableAutoJoin(sessionID, teamName)
		.build();
	}
	
	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}
	
	public void updateTeamsinWaitingScene(String text) {
		//waitingScene.setCUrrentTeams(text);
	}
	
	public void redraw(GameState state) {
		//playGameScreenV2.redrawGrid(state, this);
	}
	
	public ObjectProperty<Color> getLastcolor() {
		return lastcolor;
	}

	public void setLastcolor(ObjectProperty<Color> lastcolor) {
		this.lastcolor = lastcolor;
	}

	public void setTeamTurn(String s) {
		this.teamTurn = s;
		playGameScreenV2.setTeamTurn(s);
	}

	public void switchToWaitGameScene(Stage stage) {
		waitingScene = new WaitingScene(this, stage.getWidth(), stage.getHeight());
		stage.setScene(waitingScene);
		//t = new TestThread(this, serverManager);
		//t.start();
		//CreateGameController.startWaitingLobbyThread();
		CreateGameController.initColorHashMap();
	}
	
	public void switchToPlayGameScene(Stage stage) {
		playGameScreenV2 = new PlayGameScreenV2(this, stage.getWidth(), stage.getHeight());
		stage.setScene(playGameScreenV2);
//		if(t != null) {
//			t.stopThread();
//		}
		//GameStatePuller g = new GameStatePuller(CreateGameController.getMainClient(), this);
		//g.start();
		
		stage.setFullScreen(true);
	}

	public void setMainClient(Client mainClient) {
		this.mainClient = mainClient;
	}

	public void switchToCreateGameScene(Stage stage) {
		createGameScreenV2=  new CretaeGameScreenV2(this, stage.getWidth(), stage.getHeight());
		stage.setScene(createGameScreenV2);
	}

	public void switchToJoinScene(Stage stage) {
		stage.setScene(new JoinScene(this, stage.getWidth(), stage.getHeight()));
	}

	public void switchToMapEditorScene(Stage stage) {
		stage.setScene(new EditorScene(this, stage.getWidth(), stage.getHeight()));
	}
	
	public void switchToTestScene(Stage stage) {
		stage.setScene(new TestScene(this, stage).getScene());
	}

	public MapTemplate getTemplate() {
		return template;
	}

	public void setTemplate(MapTemplate template) {
		this.template = template;
	}
	
	public int getMaxNumberofTemas() {
		return template.getTeams();
	}

	public Stage getStage() {
		return stage;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getServerID() {
		if (serverID.equals("localhost")) {
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	            
		}
		return serverID;
	}

	public void setServerID(String serverID) {
		this.serverID = serverID;
	}
	public ServerManager getServerManager() {
		return serverManager;
	}
	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public void setServerManager(ServerManager serverManager) {
		this.serverManager = serverManager;
	}
	
	
}
