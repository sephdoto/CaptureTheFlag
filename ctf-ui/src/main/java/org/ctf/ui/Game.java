package org.ctf.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.ctf.shared.ai.AI_Tools;

import org.ctf.ui.customobjects.BackgroundCellV2;
import org.ctf.ui.customobjects.CostumFigurePain;

import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import javafx.scene.effect.Glow;
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
		possibleMoves = new ArrayList<int[]>();
		this.state = pane.state;
		cb = pane;
		currentPlayer = null;
		currentTeam = "0";
		
		setCurrentTeamActiveTeamactive();
	}
	
	
	public  void makeGrid(GameState s) {
//		state = s;
//		currentTeam = s.getCurrentTeam();
//		cb.setGameState(state);
//		cb.fillGridPane2();
		//cb.setTeamActive(1, false);
		//cb.setTeamActive(2,true);
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
			c.rc.setFill(Color.WHITE);
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
		String pieceName = currentPlayer.getPiece().getId();
		System.out.println(pieceName);
		this.possibleMoves = AI_Tools.getPossibleMoves(state,pieceName,possibleMoves);
		
//		int z = -1;
//		if (currentPlayer.posY == 2) {
//			z = 3;
//		}
//		if (currentPlayer.posY == 3) {
//			z = 4;
//		}
		for(BackgroundCellV2 c: cb.cells.values()) {
				c.rc.setFill(Color.WHITE);
				c.active = false;
		}
		for (BackgroundCellV2 c : cb.cells.values()) {
			for(int[] pos: possibleMoves) {
				if (c.x == pos[0] && c.y == pos[1]) {
					System.out.println(" " + pos[0] +", " + pos[1]);
					c.rc.setEffect(glow);
					c.rc.setFill(Color.LIGHTBLUE);
					c.active = true;
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
