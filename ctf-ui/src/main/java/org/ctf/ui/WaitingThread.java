package org.ctf.ui;

import java.io.IOException;
import java.nio.channels.ClosedSelectorException;
import java.util.concurrent.TimeUnit;

import org.ctf.shared.client.lib.ServerManager;

import javafx.application.Platform;


public class WaitingThread {

	
	boolean active;
	int currentTeams;
	HomeSceneController hsc;
	ServerManager serverManager;
	long millis = 1000;

	public WaitingThread(ServerManager servermanager, HomeSceneController hsc) {
		//this.hsc = hsc;
		//this.serverManager = servermanager;
		//this.currentTeams = servermanager.getCurrentNumberofTeams();
		//this.currentTeams = 1;
		//hsc.updateTeamsinWaitingScene("Teams:  " + currentTeams);
		//active = true;
		//run();
	}

	public void run() {
		while (active) {
			 Platform.runLater(() -> {
		            try {
		                //an event with a button maybe
		                System.out.println("button is clicked");
		            } catch (ClosedSelectorException ex) {
		                
		            }
		        });
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			if(currentTeams != serverManager.getCurrentNumberofTeams() ) {
//				currentTeams = serverManager.getCurrentNumberofTeams();
//				hsc.updateTeamsinWaitingScene("Teams:  " + currentTeams);
//			}
			
		}
	}
	
	
	public void setAcive() {
		active = true;
		run();
	}

	public void setUnactive() {
		active = false;
	}
}
