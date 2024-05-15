package org.ctf.ui;

import org.ctf.shared.client.Client;

import javafx.application.Platform;

public class GameStatePuller extends Thread {
	HomeSceneController hsc;
	Client mainClient;
	boolean active;
	int currentTeam;
	
	
	public GameStatePuller(Client mainClient, HomeSceneController hsc) {
		this.hsc = hsc;
		this.mainClient = mainClient;
		currentTeam = mainClient.getCurrentTeamTurn();
		 Platform.runLater(() -> {
			 hsc.redraw(mainClient.getCurrentState());
			 hsc.setTeamTurn(String.valueOf(currentTeam));
	        });
		active = true;
	}
	
	public void run() {
		while(active) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (currentTeam != mainClient.getCurrentTeamTurn()) {
			currentTeam = mainClient.getCurrentTeamTurn();
			 Platform.runLater(() -> {
				 hsc.redraw(mainClient.getCurrentState());
				 hsc.setTeamTurn(String.valueOf(currentTeam));
		        });
		}
		}
	}
}
