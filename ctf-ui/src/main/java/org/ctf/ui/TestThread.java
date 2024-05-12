package org.ctf.ui;

import java.time.Duration;

import org.ctf.shared.client.lib.ServerManager;

import javafx.application.Platform;

public class TestThread extends Thread {
	HomeSceneController hsc;
	ServerManager serverManager;
	int currentTeams;
	boolean active;
	
	public TestThread(HomeSceneController hsc, ServerManager serverManager) {
		this.hsc = hsc;
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
			 //double random = Math.random();
			 if(serverManager.getCurrentNumberofTeams() != currentTeams)
				 currentTeams = serverManager.getCurrentNumberofTeams();
	          hsc.updateTeamsinWaitingScene("currentTeams:" + currentTeams);
	        });
		}
		
	}
	
	public void stopThread() {
		active = false;
	}
}
