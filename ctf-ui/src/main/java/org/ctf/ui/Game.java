package org.ctf.ui;

import java.util.HashMap;

import org.ctf.ui.customobjects.BackgroundCell;
import org.ctf.ui.customobjects.CostumFigurePain;

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

public class Game {
	public CostumFigurePain currentPlayer;
	int currentTeam;
	public GamePane cb;
	//GameState gameState;
	//Team[] teams;
	HashMap<String, CostumFigurePain> team1;
	HashMap<String, CostumFigurePain> team2;

	public Game(GamePane pane) {
		//teams = cb.getTeams();
		//currentTeam = 0;
		//Je nachdem welches team dran ist figuren enablen disabeln
		cb = pane;
		//cb.moveFigure(cb.generateKey(2, 2));
		currentPlayer = null;
		team1 = pane.team1;
		team2 = pane.team2;
		for (String s : team1.keySet()) {
			team1.get(s).setActive();

		}
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
