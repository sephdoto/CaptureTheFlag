package org.ctf.shared.ai;

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
    
    this.normalizedGameState = deepNormalizeGameState(originalGameState);
  }

  /**
   * A normalized move given, the move gets unnormalized, to represent the original GameState.
   * The team and piece Id get changed, if needed the [x,y] or [y,x] coordinates also get changed.
   * No grid is required, so a GameState must not represent the current state of the game.
   * 
   * @param move the move generated with a normalized GameState
   * @return the same move but adjusted to the original GameState
   */
  public Move unnormalizeMove(Move move) {
    Move unmove = new Move();
    unmove.setNewPosition(
        this.rowThanColumn ? 
            move.getNewPosition() :
              new int[] {move.getNewPosition()[1], move.getNewPosition()[0]}
            );
    for(int i=0; i< normalizedGameState.getTeams().length; i++)
      for(int j=0; j<normalizedGameState.getTeams()[i].getPieces().length; j++)
        if(normalizedGameState.getTeams()[i].getPieces()[j].getId().equals(move.getPieceId())) {
          unmove.setTeamId("" + i);
          unmove.setPieceId(originalGameState.getTeams()[i].getPieces()[j].getId());
        }
    return unmove;
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
      teams[i].setFlags(gameState.getTeams()[i].getFlags());
      teams[i].setId("" + i);
      teams[i].setColor(gameState.getTeams()[i].getColor());
      Piece[] pieces = new Piece[gameState.getTeams()[i].getPieces().length];
      for (int j = 0; j < pieces.length; j++) {
        pieces[j] = new Piece();
        pieces[j].setDescription(gameState.getTeams()[i].getPieces()[j].getDescription());
        pieces[j].setId("p:" + i + "_" + j);
        pieces[j].setTeamId("" + i);
        pieces[j].setPosition(gameState.getTeams()[i].getPieces()[j].getPosition().clone());
        newState.getGrid()[pieces[j].getPosition()[0]][pieces[j].getPosition()[1]] = pieces[j].getId();
      }
      teams[i].setPieces(pieces);
    }
    newState.setTeams(teams);
    
    Move move = new Move();
    for(int i=0; i<teams.length; i++)
      for(int j=0; j<teams[i].getPieces().length; j++)
        if(teams[i].getPieces()[j].getId().equals(gameState.getLastMove().getPieceId())) {
          move.setTeamId("" + i);
          move.setPieceId(newState.getTeams()[i].getPieces()[j].getId());
        }
    
    if(this.rowThanColumn)
      move.setNewPosition(gameState.getLastMove().getNewPosition().clone());
    else
      move.setNewPosition(new int[] {
          gameState.getLastMove().getNewPosition()[1], 
          gameState.getLastMove().getNewPosition()[0]
              });
    newState.setLastMove(gameState.getLastMove());
    
    return newState;
  }
}
