package org.ctf.ui;

import configs.Dialogs;
import configs.GameMode;
import java.util.ArrayList;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import org.ctf.shared.ai.AI_Tools;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.data.exceptions.ForbiddenMove;
import org.ctf.shared.state.data.exceptions.GameOver;
import org.ctf.shared.state.data.exceptions.InvalidMove;
import org.ctf.shared.state.data.exceptions.SessionNotFound;
import org.ctf.ui.customobjects.BackgroundCellV2;
import org.ctf.ui.customobjects.CostumFigurePain;

public class Game {

	static GamePane cb; // Das GamePane wird nur einmal geladen und anhand von neuen GameStates
						// verändert
	static ArrayList<int[]> possibleMoves;
	static CostumFigurePain currentPlayer;
	static GameState state;
	static String currentTeam;
	static Move lastMove;
	static String myTeam;
	static GameMode mode;

	public static void initializeGame(GamePane pane, GameMode mode) {
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
		int y = lastMove.getNewPosition()[1];
		CostumFigurePain mover = cb.getFigures().get(lastMove.getPieceId());
		cb.moveFigure(x, y, mover);
		if (mode == GameMode.OneDevice) {
			setCurrentTeamActiveTeamactive();
		} else {
			if (currentTeam.equals(myTeam)) {
				setCurrentTeamActiveTeamactive();
			}
		}
	}

	// hier wird Move Objekt an Client gesendet
	public static void makeMoveRequest(int[] newPos) {
		Move move = new Move();
		move.setPieceId(currentPlayer.getPiece().getId());
		move.setNewPosition(newPos);
		cb.moveFigure(newPos[0], newPos[1], currentPlayer); //
		try {
			// JavaClient.makeMoveRequest(move)
		} catch (SessionNotFound e) {
			Dialogs.showExceptionDialog("Session not found", e.getMessage());
		} catch (ForbiddenMove e) {
			Dialogs.showExceptionDialog("Forbidden Move", e.getMessage());
		} catch (InvalidMove e) {
			Dialogs.showExceptionDialog("Invalid Move", e.getMessage());
		} catch (GameOver e) {
			Dialogs.showExceptionDialog("Game Over", e.getMessage());
		} catch (UnknownError e) {
			Dialogs.showExceptionDialog("Unknown Error", e.getMessage());
		}
		resetStateAfterMoveRequest();
	}

	public static void resetStateAfterMoveRequest() {
		currentPlayer = null;
		for (BackgroundCellV2 c : cb.getCells().values()) {
			c.deselect();
		}
		for (CostumFigurePain cf : cb.getFigures().values()) {
			cf.setUnactive();
			cf.setUnattacble();
		}
		//PlayGameScreen.resetTimer();
	}

	public void deleteTeam(String teamToDelete) {
		for (CostumFigurePain c : cb.getFigures().values()) {
			if (c.getTeamID().equals(teamToDelete)) {
				c.getParentCell().removeFigure();
				cb.getFigures().remove(c.getId(), c);
			}
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
		String pieceName = currentPlayer.getPiece().getId();
		possibleMoves = AI_Tools.getPossibleMoves(state, pieceName, possibleMoves);
		for (BackgroundCellV2 c : cb.getCells().values()) {
			if (c != currentPlayer.getParentCell()) {
				c.deselect();
			}
		}
		for (CostumFigurePain c : cb.getFigures().values()) {
			c.setUnattacble();
		}
		for (BackgroundCellV2 c : cb.getCells().values()) {
			for (int[] pos : possibleMoves) {
				if (c.x == pos[0] && c.y == pos[1]) {
					System.out.println(" " + pos[0] + ", " + pos[1]);
					if (!c.isOccupied()) {
						c.showPossibleMove();
					} else if (c.isOccupied()) {
						System.out.println("Attackble: " + c.x + ", c.y");
						c.getChild().setAttacable();
					}
				}
			}
		}
	}

	public static void deselectFigure() {
		if (cb != null) {
			for (BackgroundCellV2 c : cb.getCells().values()) {
 				c.deselect();
			}
			for (CostumFigurePain cf : cb.getFigures().values()) {
				cf.setUnattacble();
			}
			currentPlayer = null;
		}
	}

	public static CostumFigurePain getCurrent() {
		return currentPlayer;
	}

	public static void setCurrent(CostumFigurePain current) {
		currentPlayer = current;
	}
}
