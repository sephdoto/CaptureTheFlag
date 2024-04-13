package org.ctf.shared.ai.mcts2;

import java.util.Arrays;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Piece;

/**
 * This class is used to store information about an object in the grid without Strings.
 * It stores information about objects in the grid which would be intensive to compute otherwise.
 * @author sistumpf
 */
public class GridObjectContainer {
  GridObjects object;
  Piece piece;
  int teamId;
  
  public GridObjectContainer(GridObjects object, int teamId, Piece piece) {
    switch(object) {
      case block:
        this.object = object;
      case base:
        this.object = object;
        this.teamId = teamId;
        break;
      case piece:
        this.object = object;
        this.teamId = teamId;
        this.piece = piece;
        break;
    }
  }
  
  public GridObjectContainer(Grid grid, int x, int y) {
    switch(grid.getGrid()[y][x].getObject()) {
      case block:
        this.object = GridObjects.block;
        break;
      case base:
        this.object = GridObjects.base;
        this.teamId = grid.getGrid()[y][x].getTeamId();
        break;
      case piece:
        this.object = GridObjects.piece;
        this.teamId = grid.getGrid()[y][x].getTeamId();
        this.piece = grid.getGrid()[y][x].getPiece();
    }
  }
  
  public GridObjectContainer(GameState gameState, int x, int y) {
    if(gameState.getGrid()[y][x].equals("b")) {
      this.object = GridObjects.block;
      
    } else if (gameState.getGrid()[y][x].contains("b")){
      this.object = GridObjects.base;
      int start = gameState.getGrid()[y][x].indexOf(":") + 1, 
          indexUnderscore = gameState.getGrid()[y][x].indexOf("_", start);
      this.teamId =  Integer.parseInt(
          gameState.getGrid()[y][x], start, indexUnderscore == -1 ? gameState.getGrid()[y][x].length(): indexUnderscore, 10);

    } else {
      this.object = GridObjects.piece;
      int start = gameState.getGrid()[y][x].indexOf(":") + 1, 
          indexUnderscore = gameState.getGrid()[y][x].indexOf("_", start);
      this.teamId =  Integer.parseInt(
          gameState.getGrid()[y][x], start, indexUnderscore == -1 ? gameState.getGrid()[y][x].length(): indexUnderscore, 10);
      this.piece =
          Arrays.asList(gameState.getTeams()[teamId].getPieces()).stream()
              .filter(p -> p.getId().equals(gameState.getGrid()[y][x]))
              .findFirst()
              .get();
    }
  }

  public GridObjectContainer(Piece piece) {
      this.object = GridObjects.piece;
      this.teamId =  Integer.parseInt(piece.getTeamId());
      this.piece = piece;
  }
  
  
  @Override
  public GridObjectContainer clone() {
    return new GridObjectContainer(this.object, this.teamId, this.piece);
  }
  
  @Override
  public String toString() {
    switch(this.object) {
      case block:
        return "b";
      case base:
        return "b:" + this.teamId;
      case piece:
        return this.piece.getId();
      default:
        return "";
    }
  }
  
  public boolean equals(GridObjectContainer compare) {
    if(this.object != compare.object)
      return false;
    if(!this.piece.getId().equals(compare.getPiece().getId()))
      return false;
    if(this.teamId != compare.teamId)
      return false;
    return true;
  }
  
  public GridObjects getObject() {
    return object;
  }

  public void setObject(GridObjects object) {
    this.object = object;
  }

  public Piece getPiece() {
    return piece;
  }

  public void setPiece(Piece piece) {
    this.piece = piece;
  }

  public int getTeamId() {
    return teamId;
  }

  public void setTeamId(int teamId) {
    this.teamId = teamId;
  }
}