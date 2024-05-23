package org.ctf.ui.map;

import configs.Dialogs;
import configs.GameMode;
import java.util.ArrayList;
import org.ctf.shared.ai.GameUtilities;
import org.ctf.shared.client.Client;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.data.exceptions.ForbiddenMove;
import org.ctf.shared.state.data.exceptions.GameOver;
import org.ctf.shared.state.data.exceptions.InvalidMove;
import org.ctf.shared.state.data.exceptions.SessionNotFound;

/**
 * Visualizes and handles the possible moves of the currently selected piece
 * 
 * @author Manuel Krakowski
 */
public class MoveVisualizer {

  // GamePane and State which are currently visualized
  private static GamePane cb;
  private static GameState state;

  // Current-player and current team
  private static CostumFigurePain currentPlayer;
  private static int currentTeam;

  // client whose moves are shown and list of moves
  private static Client cliento;
  private static ArrayList<int[]> possibleMoves;


  /**
   * Initializes a MoveHandler with the corresponding gamepane on which the figures should be
   * controlled
   * 
   * @author Manuel Krakowski
   * @param pane GamePane with figures
   * @param client Client whose turn it is
   */
  public static void initializeGame(GamePane pane, Client client) {
    possibleMoves = new ArrayList<int[]>();
    cliento = client;
    state = pane.getState();
    cb = pane;
    currentPlayer = null;
    currentTeam = cb.getState().getCurrentTeam();
    setCurrentTeamActive();

  }

  /**
   * Sets the figures of the team active which currently has its turn
   * 
   * @author Manuel Krakowski
   */
  public static void setCurrentTeamActive() {
    for (CostumFigurePain c : cb.getFigures().values()) {
      if (c.getTeamID().equals(String.valueOf(currentTeam))) {
        c.setActive();
      }
    }
  }


  /**
   * sends a move-request by using the local client who currently has its turn. can be either called
   * by {@link BackgroundCellV2} when an empty cell is clicked {@link CostumFigurePain} when a piece
   * is attacked or BaseRep when a base is conquered
   * 
   * @author Manuel Krakowski
   * @param newPos: position where the current piece wants to move
   */
  public static void makeMoveRequest(int[] newPos) {
    Move move = new Move();
    move.setPieceId(currentPlayer.getPiece().getId());
    move.setNewPosition(newPos);
    try {
      cliento.makeMove(move);
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


  /**
   * Ensures that no objects can be selected directly after a move-request was made
   * 
   * @author Manuel Krakowski
   */
  public static void resetStateAfterMoveRequest() {
    currentPlayer = null;
    for (BackgroundCellV2 c : cb.getCells().values()) {
      c.deselect();
    }
    for (CostumFigurePain cf : cb.getFigures().values()) {
      cf.setUnactive();
      cf.setUnattacble();
    }
    for (BaseRep r : cb.getBases().values()) {
      r.setUnattacble();
    }
  }



  /**
   * Shows the possible moves of the currently selected figure
   * 
   * @author Manuel Krakowski
   */
  public static void showPossibleMoves() {
    String pieceName = currentPlayer.getPiece().getId();
    possibleMoves = GameUtilities.getPossibleMoves(state, pieceName, possibleMoves);
    for (BackgroundCellV2 c : cb.getCells().values()) {
      if (c != currentPlayer.getParentCell()) {
        c.deselect();
      }
    }
    for (CostumFigurePain c : cb.getFigures().values()) {
      c.setUnattacble();
    }
    for (BaseRep b : cb.getBases().values()) {
      b.setUnattacble();
    }
    for (BackgroundCellV2 c : cb.getCells().values()) {
      for (int[] pos : possibleMoves) {
        if (c.getX() == pos[0] && c.getY() == pos[1]) {
          System.out.println(" " + pos[0] + ", " + pos[1]);
          if (!c.isOccupied()) {
            c.showPossibleMove();
          } else if (c.isOccupied()) {
            if (c.getChild() != null) {
              c.getChild().setAttacable();
            } else {
              c.getTeamBase().setAttackable();
            }
          }
        }
      }
    }
  }

  /**
   * Deselects the currently selected figure by clicking on an empty cell
   * 
   * @author Manuel Krakowski
   */
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
