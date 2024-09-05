package org.ctf.ui.map;

import java.util.ArrayList;
import org.ctf.shared.ai.GameUtilities;
import org.ctf.shared.client.Client;
import org.ctf.shared.constants.Enums.SoundType;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Piece;
import org.ctf.shared.state.data.exceptions.ForbiddenMove;
import org.ctf.shared.state.data.exceptions.GameOver;
import org.ctf.shared.state.data.exceptions.InvalidMove;
import org.ctf.shared.state.data.exceptions.SessionNotFound;
import org.ctf.ui.controllers.SoundController;
import dialogs.Dialogs;

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
  private static CustomFigurePane currentPlayer;
  private static int currentTeam;

  // client whose moves are shown and list of moves
  private static Client cliento;
  private static ArrayList<int[]> possibleMoves;
  
  private static boolean currentlyHovering;
  private static boolean currentlySelected;

  static {
    possibleMoves = new ArrayList<int[]>();
    currentlyHovering = false;
    currentlySelected = false;
  }
  
  /**
   * Initializes a MoveHandler with the corresponding gamepane on which the figures should be
   * controlled
   * 
   * @author Manuel Krakowski
   * @param pane GamePane with figures
   * @param client Client whose turn it is
   */
  public static void initializeGame(GamePane pane, Client client) {
    cliento = client;
    setState(pane.getState());
    setCb(pane);
    currentPlayer = null;
    currentTeam = cb.getState().getCurrentTeam();
    setCurrentTeamActive();
  }
  
  /**
   * Initializes this MoveVisualizer but doesn't initialize making Moves
   * 
   * @author sistumpf
   * @param pane GamePane with figures
   */
  public static void initializeGame(GamePane pane) {
    setState(pane.getState());
    setCb(pane);
    currentPlayer = null;
    currentTeam = cb.getState().getCurrentTeam();
  }

  /**
   * Sets the figures of the team active which currently has its turn
   * 
   * @author Manuel Krakowski
   */
  public static void setCurrentTeamActive() {
    for (CustomFigurePane c : cb.getFigures().values()) {
      if (c.getTeamID().equals(String.valueOf(currentTeam))) {
        c.setActive();
      }
    }
  }


  /**
   * sends a move-request by using the local client who currently has its turn. can be either called
   * by {@link BackgroundCell} when an empty cell is clicked {@link CustomFigurePane} when a piece
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
      resetStateAfterMoveRequest();
    } catch (SessionNotFound e) {
      Dialogs.openDialog("Session not found", e.getMessage(), -1, -1);
    } catch (ForbiddenMove e) {
      Dialogs.openDialog("Forbidden Move", e.getMessage(), -1, -1);
    } catch (InvalidMove e) {
      Dialogs.openDialog("Invalid Move", e.getMessage(), -1, -1);
    } catch (GameOver e) {
      Dialogs.openDialog("Game Over", e.getMessage(), -1, -1);
    } catch (UnknownError e) {
      Dialogs.openDialog("Unknown Error", e.getMessage(), -1, -1);
    }
  }


  /**
   * Ensures that no objects can be selected directly after a move-request was made
   * 
   * @author Manuel Krakowski
   */
  public static void resetStateAfterMoveRequest() {
    currentPlayer = null;
    for (BackgroundCell c : cb.getCells().values()) {
      c.deselect();
    }
    for (CustomFigurePane cf : cb.getFigures().values()) {
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
    currentlySelected = true;
    String pieceName = currentPlayer.getPiece().getId();
    possibleMoves = GameUtilities.getPossibleMoves(state, pieceName, possibleMoves);
    for (BackgroundCell c : cb.getCells().values()) {
      if (c != currentPlayer.getParentCell()) {
        c.removePossibleMoveOnHover();
        c.deselect();
      }
    }
    for (CustomFigurePane c : cb.getFigures().values()) {
      c.removeHoverAttackable();
      c.setUnattacble();
    }
    for (BaseRep b : cb.getBases().values()) {
      b.removeHoverAttackable();
      b.setUnattacble();
    }
    cb.showLastMove();
    for (BackgroundCell c : cb.getCells().values()) {
      for (int[] pos : possibleMoves) {
        if (c.getX() == pos[0] && c.getY() == pos[1]) {
//          System.out.println(" " + pos[0] + ", " + pos[1]);
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
   * Shows the possible moves of the currently selected figure,
   * but slightly different than {@link showPossibleMoves()}. 
   * 
   * @author sistumpf
   */
  public static void hoverPossibleMoves(CustomFigurePane currentPlayer, Piece piece) {
    int currentTeam = state.getCurrentTeam();
    state.setCurrentTeam(Integer.parseInt(piece.getTeamId()));
    possibleMoves = GameUtilities.getPossibleMoves(state, piece.getId(), possibleMoves);
    state.setCurrentTeam(currentTeam);
   
    removeHoverPossibleMoves(currentPlayer);
    currentlyHovering = true;
    
    //cb.showLastMove();
    for (BackgroundCell c : cb.getCells().values()) {
      for (int[] pos : possibleMoves) {
        if (c.getX() == pos[0] && c.getY() == pos[1]) {
//          System.out.println(" " + pos[0] + ", " + pos[1]);
          if (!c.isOccupied()) {
            c.showPossibleMoveOnHover();
          } else {
            if (c.getChild() != null) {
              if(!c.getChild().getPiece().getTeamId().equals(piece.getTeamId()))
                c.getChild().hoverAttackable();
            } else {
              c.getTeamBase().hoverAttackable();
            }
          }
        }
      }
    }
  }

  /**
   * Removes the selection of hover-possible moves and hover-attackable pieces
   */
  public static void removeHoverPossibleMoves(CustomFigurePane currentPlayer) {
    currentlyHovering = false;
    for (BackgroundCell c : cb.getCells().values()) {
      if (currentPlayer == null || c != currentPlayer.getParentCell()) {
        c.removePossibleMoveOnHover();
      }
    }
    for (CustomFigurePane c : cb.getFigures().values()) {
      if(!c.getParentCell().isActive())
        c.removeHoverAttackable();
    }
    for (BaseRep b : cb.getBases().values()) {
      if(!b.getParentCell().isActive())
        b.removeHoverAttackable();
    }
    cb.showLastMove();
  }
  
  /**
   * Deselects the currently selected figure by clicking on an empty cell
   * 
   * @author Manuel Krakowski
   */
  public static void deselectFigure() {
    currentlySelected = false;
    if (cb != null) {
      for (BackgroundCell c : cb.getCells().values()) {
        if(c.getStyle().equals("-fx-background-color: transparent;" + "-fx-border-color: black; "
        + "-fx-border-width: 1.2px ")) {
          if(c.getChild() != null)
            SoundController.playSound(c.getChild().getPiece().getDescription().getType(), SoundType.DESELECT);
        }
        c.deselect();
      }
      for (CustomFigurePane cf : cb.getFigures().values()) {
        cf.setUnattacble();
      }
      currentPlayer = null;
      cb.showLastMove();
    }
  }

  public static CustomFigurePane getCurrent() {
    return currentPlayer;
  }

  public static void setCurrent(CustomFigurePane current) {
    currentPlayer = current;
  }

  private static void setState(GameState state) {
    MoveVisualizer.state = GameUtilities.deepCopyGameState(state);
  }

  public static void setCb(GamePane cb) {
    MoveVisualizer.cb = cb;
    MoveVisualizer.setState(cb.getState());
  }

  public static boolean isCurrentlyHovering() {
    return currentlyHovering;
  }

  public static boolean isCurrentlySelected() {
    return currentlySelected;
  }
}
