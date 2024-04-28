package org.ctf.ui;

/**
 * @author mkrakows & aniemesc
This Class controls what happens when clicking the buttons on the HomeScreen
 */
import java.io.IOException;

import org.ctf.shared.client.ServerManager;
import org.ctf.shared.client.lib.ServerDetails;
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
	
	public void switchtoHomeScreen(ActionEvent e) {
		Scene scene = App.getScene();
		stage = App.getStage();
		stage.setScene(scene);
	}
	
	public void createGameSession() {
		MapTemplate test = new MapTemplate();
		//serverManager = new ServerManager(new CommLayer(), new ServerDetails(serverID, port),test );
		//serverManager.createGame();
	}

	public void switchToWaitGameScene(Stage stage) {
		stage.setScene(new WaitingScene(this, stage.getWidth(), stage.getHeight()));
	}

	public void switchToCreateGameScene(Stage stage) {
		stage.setScene(new CretaeGameScreenV2(this, stage.getWidth(), stage.getHeight()));
	}

	public void switchToJoinScene(Stage stage) {
		stage.setScene(new JoinScene(this, stage.getWidth(), stage.getHeight()));
	}

	public void switchToMapEditorScene(Stage stage) {
		stage.setScene(new EditorScene(this, stage.getWidth(), stage.getHeight()));
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
