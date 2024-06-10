package org.ctf.shared.ai;

import java.util.HashMap;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Piece;
import org.ctf.shared.state.Team;

/**
 * To ensure the AI always does what it should, the first step is to use normalized GameStates.
 * An original GameState given, a normalized one gets created for use in the AI.
 * The original one can be used to send a move back to the server.
 * 
 * @author sistumpf
 */
public class GameStateNormalizer {
  private boolean rowThanColumn;
  private GameState originalGameState;
  private GameState normalizedGameState;
  public HashMap<String, String> unToNorm;
  public HashMap<String, String> normToUn;

  /**
   * Generated a normalized GameState and saves it as an attribute
   * 
   * @param original the original, unnormalized GameState
   * @param rowThanColummn true if moves contain newPos as [y,x], false if it is [x,y]
   * @param client true if the normalizer is used by the client, then lastMove won't be altered
   */
  public GameStateNormalizer(GameState original, boolean rowThanColumn, boolean client) {
    this.rowThanColumn = rowThanColumn;
    this.originalGameState = original;

    if(!client) {
      if(original.getLastMove() == null)
        original.setLastMove(new Move());
      if(original.getLastMove().getNewPosition() == null)
        original.getLastMove().setNewPosition(new int[] {0,0});
    }
    
    this.unToNorm = new HashMap<String, String>();
    this.normToUn = new HashMap<String, String>();
    this.normalizedGameState = deepNormalizeGameState(originalGameState);  
    for(int team=0; team<original.getTeams().length; team++) {
      if(original.getTeams()[team] != null)
        for(int piece=0, point=0; point<original.getTeams()[team].getPieces().length; point++) {
          if(original.getTeams()[team].getPieces()[point] != null) {
            unToNorm.put(original.getTeams()[team].getPieces()[point].getId(), normalizedGameState.getTeams()[team].getPieces()[piece].getId());
            normToUn.put(normalizedGameState.getTeams()[team].getPieces()[piece].getId(), original.getTeams()[team].getPieces()[point].getId());
            piece++;
          } 
        }
    }
  }

  /**
   * Generated a normalized GameState and saves it as an attribute
   * 
   * @param original the original, unnormalized GameState
   * @param rowThanColummn true if moves contain newPos as [y,x], false if it is [x,y]
   */
  public GameStateNormalizer(GameState original, boolean rowThanColumn) {
    this.rowThanColumn = rowThanColumn;
    this.originalGameState = original;

    if(original.getLastMove() == null)
      original.setLastMove(new Move());
    if(original.getLastMove().getNewPosition() == null)
      original.getLastMove().setNewPosition(new int[] {0,0});
    
    this.unToNorm = new HashMap<String, String>();
    this.normToUn = new HashMap<String, String>();
    this.normalizedGameState = deepNormalizeGameState(originalGameState);  
    for(int team=0; team<original.getTeams().length; team++) {
      if(original.getTeams()[team] != null)
        for(int piece=0, point=0; point<original.getTeams()[team].getPieces().length; point++) {
          if(original.getTeams()[team].getPieces()[point] != null) {
            unToNorm.put(original.getTeams()[team].getPieces()[point].getId(), normalizedGameState.getTeams()[team].getPieces()[piece].getId());
            normToUn.put(normalizedGameState.getTeams()[team].getPieces()[piece].getId(), original.getTeams()[team].getPieces()[point].getId());
            piece++;
          } 
        }
    }
  }

  /**
   * Updates this normalizer with a new unnormalized GameState.
   * The normalized GameState gets updated, but keeps old IDs.
   * 
   * @param newState the new unnormalized GameState to update with
   */
  public void update(GameState newState) {
    for(int team=0; team<newState.getTeams().length; team++) {
      if(newState.getTeams()[team] == null) continue;
      int[] base = newState.getTeams()[team].getBase();
      newState.getGrid()[base[0]][base[1]] = "b:" + team;
      newState.getTeams()[team].setId("" + team);
      
      int pcsLen = 0;
      for(int point=0; point<newState.getTeams()[team].getPieces().length; point++) {
        if(newState.getTeams()[team].getPieces()[point] != null)
          pcsLen++;
      }
      Piece[] pieces = new Piece[pcsLen];
      
      for(int piece=0,point=0; point<newState.getTeams()[team].getPieces().length; point++) {
        if(newState.getTeams()[team].getPieces()[point] == null) continue;
        pieces[piece] = newState.getTeams()[team].getPieces()[point];
        int[] piecePos = pieces[piece].getPosition();
        newState.getGrid()[piecePos[0]][piecePos[1]] = unToNorm.get(pieces[piece].getId());
        pieces[piece].setTeamId("" + team);
        pieces[piece].setId(unToNorm.get(pieces[piece].getId()));
        piece++;
      }
      newState.getTeams()[team].setPieces(pieces);
    }

    if(newState.getLastMove() != null) {
      Move move = new Move();
      /*
      for(int i=0; i<newState.getTeams().length; i++) {
        if (newState.getTeams()[i] == null) continue;
        for(int j=0; j<newState.getTeams()[i].getPieces().length; j++) {
          if(newState.getTeams()[i].getPieces()[j] == null) continue;
          if(newState.getTeams()[i].getPieces()[j].getId().equals(newState.getLastMove().getPieceId())) {
            move.setTeamId("" + i);
            move.setPieceId(newState.getTeams()[i].getPieces()[j].getId());
            break;
          }
        }
      }*/
      move.setPieceId(unToNorm.get(newState.getLastMove().getPieceId()));
      move.setTeamId(move.getPieceId().split(":")[1].split("_")[0]);
      if(this.rowThanColumn)
        move.setNewPosition(newState.getLastMove().getNewPosition().clone());
      else
        move.setNewPosition(new int[] {
            newState.getLastMove().getNewPosition()[1], 
            newState.getLastMove().getNewPosition()[0]
        });
      newState.setLastMove(move);
    }

    this.normalizedGameState = newState;
  }

  /**
   * A normalized move given, the move gets unnormalized to represent the original GameState.
   * The team and piece Id get changed, if needed the [x,y] or [y,x] coordinates also get changed.
   * No grid is required, so a GameState must not represent the current state of the game.
   * 
   * @param move the move generated with a normalized GameState
   * @return the same move but adjusted to the original GameState
   */
  public Move unnormalizeMove(Move move) { Move unmove = new Move();
  unmove.setNewPosition(
      this.rowThanColumn ? 
          move.getNewPosition() :
            new int[] {move.getNewPosition()[1], move.getNewPosition()[0]}
      );
  unmove.setPieceId(this.normToUn.get(move.getPieceId()));
    try {
      unmove.setTeamId(unmove.getPieceId().split(":")[1].split("_")[0]);
    } catch(ArrayIndexOutOfBoundsException e) {
      // this might happen when a team did not set the IDs right
    }
    return unmove;
  }
  /**
   * Aun unnormalized move given, the move gets normalized to represent the normalized GameState.
   * The team and piece Id get changed, if needed the [x,y] or [y,x] coordinates also get changed.
   * No grid is required, so a GameState must not represent the current state of the game.
   * 
   * @param move the move generated with a original GameState
   * @return the same move but adjusted to the normalized GameState
   */
  public Move normalizedMove(Move move) {
    Move normove = new Move();
    boolean found=false;
    normove.setNewPosition(
        this.rowThanColumn ? 
            move.getNewPosition() :
              new int[] {move.getNewPosition()[1], move.getNewPosition()[0]}
        );
    for(int i=0; i< originalGameState.getTeams().length; i++) {
      if(originalGameState.getTeams()[i] == null) continue;
      for(int j=0; j<originalGameState.getTeams()[i].getPieces().length; j++)
        if(originalGameState.getTeams()[i].getPieces()[j].getId().equals(move.getPieceId())) {
          normove.setTeamId("" + i);
          normove.setPieceId(normalizedGameState.getTeams()[i].getPieces()[j].getId());
          found = true;
          break;
        }
    }
    return found ? normove : null;
  }

  public GameState getOriginalGameState() {
    return this.originalGameState;
  }

  public GameState getNormalizedGameState() {
    return this.normalizedGameState;
  }

  /**
   * Deep copies a GameState and adjusts some values to be normalized.
   * The Team id gets changed to represent its place in the Array.
   * The Piece id gets adjusted accordingly in teams Array and on the Grid.
   * The last move gets adjusted accordingly, also its newPos indexes get changed to represent [y,x].
   *
   * @author sistumpf
   * @return a deep copy and normalized version of a given GameState
   */
  private GameState deepNormalizeGameState(GameState gameState) {
    GameState newState = new GameState();
    newState.setCurrentTeam(gameState.getCurrentTeam());
    String[][] grid = new String[gameState.getGrid().length][];
    for (int i = 0; i < gameState.getGrid().length; i++) {
      grid[i] = new String[gameState.getGrid()[i].length];
      System.arraycopy(gameState.getGrid()[i], 0, grid[i], 0, gameState.getGrid()[i].length);
    }
    newState.setGrid(grid);

    Team[] teams = new Team[gameState.getTeams().length];
    for (int i = 0; i < teams.length; i++) {
      if (gameState.getTeams()[i] == null) continue;
      teams[i] = new Team();
      teams[i].setBase(gameState.getTeams()[i].getBase());
      newState.getGrid()[gameState.getTeams()[i].getBase()[0]][gameState.getTeams()[i].getBase()[1]] = "b:" + i;
      teams[i].setFlags(gameState.getTeams()[i].getFlags());
      teams[i].setId("" + i);
      teams[i].setColor(gameState.getTeams()[i].getColor());
      int pcsLen = 0;
      for(int j = 0;  j < gameState.getTeams()[i].getPieces().length; j++)
        if(gameState.getTeams()[i].getPieces()[j] != null)
          pcsLen++;
      Piece[] pieces = new Piece[pcsLen];
      for (int j = 0, pointer = 0; pointer < pieces.length; pointer++) {
        if(gameState.getTeams()[i].getPieces()[pointer] == null) continue;
        pieces[j] = new Piece();
        pieces[j].setDescription(gameState.getTeams()[i].getPieces()[pointer].getDescription());
        pieces[j].setId("p:" + i + "_" + j);
        pieces[j].setTeamId("" + i);
        pieces[j].setPosition(gameState.getTeams()[i].getPieces()[pointer].getPosition().clone());
        newState.getGrid()[pieces[j].getPosition()[0]][pieces[j].getPosition()[1]] = pieces[j].getId();
        j++;
      }
      teams[i].setPieces(pieces);
    }
    newState.setTeams(teams);

    if(gameState.getLastMove() != null) {
      Move move = new Move();
      for(int i=0; i<teams.length; i++) {
        if (gameState.getTeams()[i] == null) continue;
        for(int j=0; j<teams[i].getPieces().length; j++) {
          if(gameState.getTeams()[i].getPieces()[j] == null) continue;
          if(gameState.getTeams()[i].getPieces()[j].getId().equals(gameState.getLastMove().getPieceId())) {
            move.setTeamId("" + i);
            move.setPieceId(newState.getTeams()[i].getPieces()[j].getId());
            break;
          }
        }
      }
      if(this.rowThanColumn)
        move.setNewPosition(gameState.getLastMove().getNewPosition().clone());
      else
        move.setNewPosition(new int[] {
            gameState.getLastMove().getNewPosition()[1], 
            gameState.getLastMove().getNewPosition()[0]
        });
      newState.setLastMove(move);
    }

    return newState;
  }
}
