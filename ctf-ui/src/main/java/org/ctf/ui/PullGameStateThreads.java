package org.ctf.ui;

import org.ctf.shared.client.AutomatedClient;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.data.exceptions.SessionNotFound;
import org.ctf.shared.state.data.exceptions.URLError;

import configs.Dialogs;

public class PullGameStateThreads extends Thread {
	boolean active;
	AutomatedClient client;
	GameState currenState;
	long millis = 1000;

	public PullGameStateThreads(AutomatedClient c) {
		client = c;
		run();
	}

	public void run() {
		while (active) {
			refreshGameState();
			
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void checkForChanges() {
		
	}
	
	
	public void refreshGameState() {
		try {
			client.getStateFromServer();
		} catch (SessionNotFound e) {
			Dialogs.showExceptionDialog("Session not found", e.getMessage());
		} catch (UnknownError e) {
			Dialogs.showExceptionDialog("Unknown Error", e.getMessage());
		} catch (URLError e) {
			Dialogs.showExceptionDialog("URL Error", e.getMessage());
		}
	}
	public void setAcive(Game game) {
		active = true;
	}

	public void setUnactive() {
		active = false;
	}
}
