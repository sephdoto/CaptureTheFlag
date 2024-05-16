package org.ctf.ui.controllers;

import org.ctf.shared.client.Client;
import org.ctf.shared.state.data.exceptions.UnknownError;
import org.ctf.ui.App;
import org.ctf.ui.GamePane;
import org.ctf.ui.RemoteWaitingScene;

import javafx.application.Platform;
import javafx.scene.control.Button;
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
			
			while (isactive) {
			  Thread.sleep(2000); 
			  Platform.runLater(() -> {
		            System.out.println(rws.getClient().isGameStarted()+"");
		            rws.getText().setText(rws.getClient().isGameStarted()+"");
		            if(rws.getClient().isGameStarted()) {
		            Button but = new Button("hello");
		            rws.getRootPane().getChildren().add(but);
		            but.setOnAction(e ->{
		              rws.getHsc().switchToPlayGameScene(App.getStage(), rws.getClient(),true);
		            });
		            isactive = false;
		            } 
		            
		           
		              
		             
				    		              
		             });
				
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
