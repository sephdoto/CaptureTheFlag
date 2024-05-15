package org.ctf.ui;

import java.time.Duration;

import org.ctf.shared.client.lib.ServerManager;

import javafx.application.Platform;

public class WaitingLobbyThread extends Thread {
	ServerManager serverManager;
	int currentTeams;
	boolean active;
	
	public WaitingLobbyThread(ServerManager serverManager) {
		this.serverManager = serverManager;
		currentTeams = 1;
		active = true;
	}
	
	
	public void run() {
		while(active) {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 Platform.runLater(() -> {
			 if(serverManager.getCurrentNumberofTeams() != currentTeams) {
				   currentTeams = serverManager.getCurrentNumberofTeams();
				   CreateGameController.updateTeamNumberFromRemote(currentTeams);
			 }
	        });
		}
		
	}
	
	public void stopThread() {
		active = false;
	}
}
