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
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

public class Game {
	
	static GamePane cb; // Das GamePane wird nur einmal geladen und anhand von neuen GameStates verändert
	static ArrayList<int[]> possibleMoves;
	static CostumFigurePain currentPlayer;
	static GameState state;
	static String currentTeam;
	static Move lastMove;
	static String myTeam;

	
	
	public static void initializeGame(GamePane pane) {
		possibleMoves = new ArrayList<int[]>();
		state = pane.state;
		cb = pane;
		currentPlayer = null;
		currentTeam = "0";
		setCurrentTeamActiveTeamactive();
	}
	
	
	public static void perfomMove(Move m) {
		lastMove = m;
		int x = lastMove.getNewPosition()[0];
		int y= lastMove.getNewPosition()[1];
		CostumFigurePain mover = cb.getFigures().get(lastMove.getPieceId());
		cb.moveFigure(x, y, mover);
		
		
	}

	// hier wird Move Objekt an Client gesendet
	public static void makeMoveRequest(int[] newPos) {
		// currentPlayer.getParentCell().setOccupied(false);
		Move move = new Move();
		move.setPieceId(currentPlayer.getPiece().getId());
		move.setNewPosition(newPos);
		cb.moveFigure(newPos[0], newPos[1], currentPlayer);
		resetStateAfterMoveRequest();
		
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

	public static void setCurrentTeamActiveTeamactive() {
		for (CostumFigurePain c : cb.getFigures().values()) {
			if (c.getTeamID().equals(currentTeam)) {
				c.setActive();
			}
		}
	}

	public static void showPossibleMoves() {
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
					Color color = Color.rgb(150, 150, 150, 0.5);
					c.rc.setFill(color);
					c.active = true;
					} else if (c.isOccupied()) {
						//System.out.println("Diese Zelle " + pos[0] + ", " + pos[1]);
					}
				}

			}
		}
	}
	

	public static CostumFigurePain getCurrent() {
		return currentPlayer;
	}

	public static void setCurrent(CostumFigurePain current) {
		currentPlayer = current;
	}

}
