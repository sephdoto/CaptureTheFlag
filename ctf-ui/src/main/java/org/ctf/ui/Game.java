package org.ctf.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.ctf.ui.customobjects.BackgroundCell;
import org.ctf.ui.customobjects.CostumFigurePain;

import de.unimannheim.swt.pse.ctf.game.state.GameState;
import de.unimannheim.swt.pse.ctf.game.state.Move;
import de.unimannheim.swt.pse.ctf.game.state.Team;
//import de.unimannheim.swt.pse.ctf.game.state.GameState;
//import de.unimannheim.swt.pse.ctf.game.state.Move;
//import de.unimannheim.swt.pse.ctf.game.state.Team;
import javafx.scene.effect.Glow;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class Game extends Thread {
	public CostumFigurePain currentPlayer;
	GameState state;
	int currentTeam;
	public GamePane cb;
	static String[][] exm3 = {
			  {"", "p:1_2", "p:1_3", "p:1_4", "p:1_5", "p:1_6", "p:1_7", "p:1_8"},
			  {"", "p:1_1", "", "", "", "", "", ""},
			  {"", "", "", "", "", "", "", ""},
			  {"", "", "", "b", "", "", "", ""},
			  {"", "", "b", "", "", "", "", ""},
			  {"", "", "", "", "b", "", "", ""},
			  {"", "", "", "", "", "", "", ""},
			  {"p:2_3", "p:2_4", "p:2_5", "p:2_6", "p:2_8", "p:2_9", "p:2_10", "p:2_13"}
			};
	
	Team[] teams;
	
	HashMap<String, CostumFigurePain> team1;
	HashMap<String, CostumFigurePain> team2;

	public Game(GamePane pane) {
		cb = pane;
		currentPlayer = null;
		cb.setTeamActive(1);
	}
	
	public void run() {
		
	}
	public  void makeGrid() {
		cb.setGameState(state);
		cb.fillGridPane2();
		cb.setTeamActive(2);
		cb.setGame(this);
		
	}
	//hier wird Move Objekt an Client gesendet
	public Move makeMove(int[] newPos) {
		Move move = new Move();
		move.setNewPosition(newPos);
		state = new GameState();
		state.setGrid(exm3);
		this.makeGrid();
		return move;
	}
	
	

	public void showPossibleMoves() {
		Glow glow = new Glow();
		glow.setLevel(0.2);
		int z = -1;
		if (currentPlayer.posY == 2) {
			z = 3;
		}
		if (currentPlayer.posY == 3) {
			z = 4;
		}
		for (BackgroundCell c : cb.cells.values()) {
			if (c.x == 2 && c.y == z) {
				c.rc.setEffect(glow);
				c.rc.setFill(Color.LIGHTBLUE);
				c.active = true;
			} else {
				if (!c.occupied) {
					c.rc.setFill(Color.WHITE);
					c.active = false;
				}
			}
		}
	}
	
//	public Move createMoveforClient(int[] newPos) {
//		Move move = new Move();
//		move.setNewPosition(newPos);
//		move.setPieceId(currentPlayer.name);
//		//TeamSecret?
//		//CommLayer.makeMoveRequest()
//		return move;
//		
//	}

	public CostumFigurePain getCurrent() {
		return currentPlayer;
	}

	public void setCurrent(CostumFigurePain current) {
		this.currentPlayer = current;
	}

}
