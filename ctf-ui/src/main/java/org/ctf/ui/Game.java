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

public class Game {
	static CostumFigurePain currentPlayer;
	static GameState state;
	static String currentTeam;
	static GamePane cb;
	static ArrayList<int[]> possibleMoves;
	Move lastMove;

	// Das GamePane wird einmal geladen und anhand von neuen GameStates verändert
	public Game(GamePane pane) {
		possibleMoves = new ArrayList<int[]>();
		state = pane.state;
		cb = pane;
		currentPlayer = null;
		currentTeam = "0";

		setCurrentTeamActiveTeamactive();
	}

	
	
	public void makeGrid(GameState s) {
//		state = s;
//		currentTeam = s.getCurrentTeam();
//		cb.setGameState(state);
//		cb.fillGridPane2();
		// cb.setTeamActive(1, false);
		// cb.setTeamActive(2,true);
		cb.setGame(this);

	}

		//Ohne Konstruktor und Lastmove einfach von dr Klasse
	public void perfomMove(Move m) {
		lastMove = m;
		int x = lastMove.getNewPosition()[0];
		int y= lastMove.getNewPosition()[1];
		CostumFigurePain mover = cb.getFigures().get(lastMove.getPieceId());
		cb.moveFigure(x, y, mover);
		
		
	}

	// hier wird Move Objekt an Client gesendet
	public  Move makeMoveRequest(int[] newPos) {
		// currentPlayer.getParentCell().setOccupied(false);
		Move move = new Move();
		move.setPieceId(currentPlayer.getPiece().getId());
		move.setNewPosition(newPos);
		cb.moveFigure(newPos[0], newPos[1], currentPlayer);
		resetStateAfterMoveRequest();
		return move;
	}

	public static void resetStateAfterMoveRequest() {
		currentPlayer.disableShadow();
		currentPlayer = null;
		for (BackgroundCellV2 c : cb.getCells().values()) {
			c.rc.setFill(Color.WHITE);
			c.active = false;
		}
		for (CostumFigurePain cf : cb.getFigures().values()) {
			cf.setUnactive();
		}
	}

	public void setCurrentTeamActiveTeamactive() {
		for (CostumFigurePain c : cb.getFigures().values()) {
			if (c.getTeamID().equals(currentTeam)) {
				c.setActive();
			}
		}
	}

	public void showPossibleMoves() {
		Glow glow = new Glow();
		glow.setLevel(0.2);
		String pieceName = currentPlayer.getPiece().getId();
		possibleMoves = AI_Tools.getPossibleMoves(state, pieceName, possibleMoves);
		for (BackgroundCellV2 c : cb.getCells().values()) {
			c.rc.setFill(Color.WHITE);
			c.active = false;
		}
		for (BackgroundCellV2 c : cb.getCells().values()) {
			for (int[] pos : possibleMoves) {
				if (c.x == pos[0] && c.y == pos[1]) {
					System.out.println(" " + pos[0] + ", " + pos[1]);
					if(!c.isOccupied()) {
					
					c.rc.setEffect(glow);
					c.rc.setFill(Color.LIGHTBLUE);
					c.active = true;
					} else if (c.isOccupied()) {
						//System.out.println("Diese Zelle " + pos[0] + ", " + pos[1]);
					}
				}

			}
		}
	}

	public CostumFigurePain getCurrent() {
		return currentPlayer;
	}

	public void setCurrent(CostumFigurePain current) {
		currentPlayer = current;
	}

}
