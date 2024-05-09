package org.ctf.ui;

/**
 * @author mkrakows & aniemesc
This Class controls what happens when clicking the buttons on the HomeScreen
 */
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;

import org.ctf.shared.client.Client;
import org.ctf.shared.client.ClientStepBuilder;
import org.ctf.shared.client.lib.ServerDetails;
import org.ctf.shared.client.lib.ServerManager;
import org.ctf.shared.client.service.CommLayer;
import org.ctf.shared.state.data.map.MapTemplate;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class HomeSceneController {
	private Stage stage;
	String port;
	String serverID;
	String sessionID;
	ServerManager serverManager;
	MapTemplate template;
	CretaeGameScreenV2 createGameScreenV2;
	Client mainClient;
	String teamName;
	
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

	public void switchToWaitGameScene(Stage stage) {
		stage.setScene(new WaitingScene(this, stage.getWidth(), stage.getHeight()));
	}
	public void switchToPlayGameScene(Stage stage) {
		stage.setScene(new PlayGameScreenV2(this, stage.getWidth(), stage.getHeight()));
		stage.setFullScreen(true);
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
			try {
				return Inet4Address.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
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
