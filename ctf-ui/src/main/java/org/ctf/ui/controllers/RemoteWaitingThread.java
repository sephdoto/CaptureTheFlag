package org.ctf.ui.controllers;

import org.ctf.shared.client.Client;
import org.ctf.shared.state.data.exceptions.UnknownError;
import org.ctf.ui.GamePane;
import javafx.application.Platform;

public class RemoteWaitingThread extends Thread {
	Client client;
	String s;
	boolean isactive;

	public RemoteWaitingThread(Client client) {
		this.client = client;
		isactive = true;
	}

	public void run() {
		try {
			Thread.sleep(1000);
			while (isactive) {
				System.out.println(client.isGameStarted());
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
