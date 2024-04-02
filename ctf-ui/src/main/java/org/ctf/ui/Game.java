package org.ctf.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.ctf.shared.ai.AI_Tools;
import org.ctf.ui.customobjects.BackgroundCell;
import org.ctf.ui.customobjects.BackgroundCellV2;
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

public class Game   {
	public CostumFigurePain currentPlayer;
	GameState state;
	String currentTeam;
	public GamePane cb;
	
	
	ArrayList<int[]> possibleMoves;
	HashMap<String, CostumFigurePain> team1;
	HashMap<String, CostumFigurePain> team2;

	public Game(GamePane pane) {
		cb = pane;
		currentPlayer = null;
		cb.setTeamActive(1,true);
	}
	
	
	public  void makeGrid(GameState s) {
//		state = s;
//		currentTeam = s.getCurrentTeam();
//		cb.setGameState(state);
//		cb.fillGridPane2();
		cb.setTeamActive(1, false);
		cb.setTeamActive(2,true);
		cb.setGame(this);
		
	}
	//hier wird Move Objekt an Client gesendet
	public Move makeMove(int[] newPos) {
		//currentPlayer.parent.occupied = false;
		Move move = new Move();
		move.setNewPosition(newPos);
		cb.moveFigure(newPos[0],newPos[1],currentPlayer);
		resetStateAfterMoveRequest();
//		cb.setTeamActive(1, false);
//		cb.setTeamActive(2,true);
		return move;
	}
	
	

	public void resetStateAfterMoveRequest() {
		currentPlayer.disableShadow();
		currentPlayer = null;
		for(BackgroundCellV2 c: cb.cells.values()) {
			c.active = false;
		}
		for(CostumFigurePain cf: cb.allFigures) {
			cf.setUnactive();
		}
	}
	
	public void setCurrentTeamActiveTeamactive() {
		for(CostumFigurePain c: cb.allFigures) {
			if(c.getTeamID().equals(currentTeam)) {
				c.setActive();
			}
		}
	}
	
	
	public void showPossibleMoves() {
		Glow glow = new Glow();
		glow.setLevel(0.2);
		this.possibleMoves = AI_Tools.getPossibleMoves(null, null, possibleMoves);
		int z = -1;
		if (currentPlayer.posY == 2) {
			z = 3;
		}
		if (currentPlayer.posY == 3) {
			z = 4;
		}
		for (BackgroundCellV2 c : cb.cells.values()) {
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
