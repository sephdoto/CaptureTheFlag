package org.ctf.ui.controllers;

import org.ctf.shared.client.Client;
import org.ctf.shared.state.data.exceptions.UnknownError;
import org.ctf.ui.GamePane;
import org.ctf.ui.RemoteWaitingScene;

import javafx.application.Platform;
import javafx.scene.text.Text;

public class RemoteWaitingThread extends Thread {
	RemoteWaitingScene rws;
	boolean isactive;
	

	public RemoteWaitingThread(RemoteWaitingScene rws)  {
		this.rws = rws;
		isactive = true;
	}

	public void run() {
		try {
			Thread.sleep(1000);
			while (isactive) {
				 Platform.runLater(() -> {
		               rws.getText().setText(rws.getClient().isGameStarted()+"");		              
		             });
				
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
