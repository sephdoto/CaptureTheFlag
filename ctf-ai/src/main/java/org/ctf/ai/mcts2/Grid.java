package org.ctf.ai.mcts2;

import org.ctf.shared.state.GameState;

/**
 * This class replaces the "dumb" String[][] Grid with a smart GridObjectContainer Grid.
 */
public class Grid {
  GridObjectContainer[][] grid;
  
  public Grid(GridObjectContainer[][] grid) {
    this.grid = grid;
  }
  
  public Grid(GameState gameState) {
    this.grid = new GridObjectContainer[gameState.getGrid().length][];
    for(int y=0; y<this.grid.length; y++) {
      this.grid[y] = new GridObjectContainer[gameState.getGrid()[y].length];
      for(int x=0; x<gameState.getGrid()[y].length; x++) {
        if(!gameState.getGrid()[y][x].equals(""))
          this.grid[y][x] = new GridObjectContainer(gameState, x, y);
      }
    }
  }

  /**
   * Deep clones a Grid
   */
  @Override
  public Grid clone(){
    GridObjectContainer[][] clone = new GridObjectContainer[this.grid.length][];
    for(int y=0; y<this.grid.length; y++) {
      clone[y] = new GridObjectContainer[this.grid[y].length];
      for(int x=0; x<this.grid[y].length; x++) {
        if(this.grid[y][x] != null)
          clone[y][x] = this.grid[y][x].clone();
      }
    }
    return new Grid(clone);
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
