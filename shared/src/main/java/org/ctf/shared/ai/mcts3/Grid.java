package org.ctf.shared.ai.mcts3;

import java.util.LinkedList;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Piece;

/**
 * This new Grid replaces the "dumb" String[][] Grid with a smart GridObjectContainer Grid.
 * 
 * @author sistumpf
 */
public class Grid {
  static int[][] blocks;
  GridObjectContainer[][] grid;
  
  public Grid(GridObjectContainer[][] grid) {
    this.grid = grid;
  }
  
  /**
   * Creates a grid from an array of teams.
   * Use this to clone a grid with cloned references to pieces.
   * @param teams
   * @param height
   * @param width
   */
  public Grid(Team[] teams, int height, int width) {
    this.grid = new GridObjectContainer[height][width];
    for(int[] block : Grid.blocks)
      grid[block[0]][block[1]] = new GridObjectContainer(GridObjects.block, 0, null);
    for(Team team : teams) {
      if(team == null)
        continue;
      grid[team.getBase()[0]][team.getBase()[1]] = new GridObjectContainer(GridObjects.base, Integer.parseInt(team.getId()), null);
      for(Piece piece : team.getPieces())
        grid[piece.getPosition()[0]][piece.getPosition()[1]] = new GridObjectContainer(piece);
    }
  }
  
  public Grid(Grid grid) {
    this.grid = new GridObjectContainer[grid.getGrid().length][grid.getGrid()[0].length];
    for(int y=0; y<this.grid.length; y++) {
      for(int x=0; x<this.grid[y].length; x++) {
        if(grid.getGrid()[y][x] != null)
          this.grid[y][x] = new GridObjectContainer(grid, x, y);
      }
    }
  }
  
  public Grid(GameState gameState) {
    this.grid = new GridObjectContainer[gameState.getGrid().length][gameState.getGrid()[0].length];
    LinkedList<int[]> blocks = new LinkedList<int[]>();
    for(int y=0; y<this.grid.length; y++) {
      for(int x=0; x<gameState.getGrid()[y].length; x++) {
        if(gameState.getGrid()[y][x].equals("b")) {
          this.grid[y][x] = new GridObjectContainer(GridObjects.block, 0, null);
          blocks.add(new int[] {y, x});
        }
      }
    }
    Grid.blocks = blocks.toArray(new int[blocks.size()][]);
    for(org.ctf.shared.state.Team team : gameState.getTeams()) {
      if(team == null)
        continue;
      this.grid[team.getBase()[0]][team.getBase()[1]] = new GridObjectContainer(GridObjects.base, Integer.parseInt(team.getId()), null);
      for(Piece p : team.getPieces())
        this.grid[p.getPosition()[0]][p.getPosition()[1]] = new GridObjectContainer(p);
    }
  }

  /**
   * Deep clones a Grid but not the pieceVision or pieceVisionGrid.
   */
  @Override
  public Grid clone(){
    GridObjectContainer[][] clone = new GridObjectContainer[this.grid.length][this.grid[0].length];
    for(int y=0; y<this.grid.length; y++) {
      for(int x=0; x<this.grid[y].length; x++) {
        if(this.grid[y][x] != null)
          clone[y][x] = this.grid[y][x].clone();
        else
          clone[y][x] = null;
      }
    }
    return new Grid(clone);
  }
  
  public boolean equals(Grid compare) {
    for(int y=0; y<this.grid.length; y++) {
      for(int x=0; x<this.grid[y].length; x++) {
        if(this.grid[y][x] == null)
          if(compare.grid[y][x] == null)
            continue;
          else
            return false;
        else
          if(compare.grid[y][x] == null)
            return false;
        if(!this.grid[y][x].equals(compare.grid[y][x]))
          return false;
      }
    }
    return true;
  }
  
  public GridObjectContainer getPosition(int x, int y) {
    return this.grid[y][x];
  }
  
  public void setPosition(GridObjectContainer goc, int x, int y) {
    this.grid[y][x] = goc;
  }
  
  public GridObjectContainer[][] getGrid() {
    return grid;
  }

  public void setGrid(GridObjectContainer[][] grid) {
    this.grid = grid;
  }
}
