package org.ctf.ui;


import org.ctf.shared.state.GameState;

public class PullGameStateThreads extends Thread {
	boolean active;
	GameState currenState;
	long millis = 10000000;

	public PullGameStateThreads() {
		active = true;
		run();
	}

	public void run() {
		while (active) {
			if(Game.getCurrent() != null) {
			Game.getCurrent().performSelectClick();
			}
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public void setAcive(Game game) {
		active = true;
	}

	public void setUnactive() {
		active = false;
	}
}
