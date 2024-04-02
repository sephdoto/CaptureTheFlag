package org.ctf.ui;



public class PullGameStateThreads extends Thread {

	boolean active;
	Game game;
	long millis = 1000;

	public void run() {
		while (active) {
			// GameState x = Client.requestGameState
			//GameState xy = new GameState();
//			if (xy.getCurrentTeam() != game.currentTeam) {
//				game.makeGrid(xy);
//			}
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
		this.game = game;
	}

	public void setUnactive() {
		active = false;
	}
}
