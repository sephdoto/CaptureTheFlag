package org.ctf.ai.mcts2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Piece;
import org.ctf.shared.state.Team;

/**
 * This class replaces the "dumb" String[][] Grid with a smart GridObjectContainer Grid.
 * It also contains a separate pieceVision Grid containing the pieces and the valid positions they could go to.
 * Adding to that pieceVision Grid is a HashMap contained, to store pieces and all positions they can access.
 * @author sistumpf
 */
public class Grid {
  static int[][] blocks;
  GridObjectContainer[][] grid;
  GridPieceContainer[][] pieceVisionGrid;
  IdentityHashMap<Piece, ArrayList<int[]>> pieceVisions;
  
  public Grid(GridObjectContainer[][] grid, GridPieceContainer[][] pieceVisionGrid, IdentityHashMap<Piece, ArrayList<int[]>> pieceVisions) {
    this.grid = grid;
    this.pieceVisionGrid = pieceVisionGrid;
    this.pieceVisions = pieceVisions;
  }

  @SafeVarargs
  public Grid(GridObjectContainer[][] grid, IdentityHashMap<Piece, ArrayList<int[]>> ... moves ) {
    this.grid = grid;
    this.pieceVisions = new IdentityHashMap<Piece, ArrayList<int[]>>();
    this.pieceVisionGrid = new GridPieceContainer[grid.length][grid[0].length];
    for(IdentityHashMap<Piece, ArrayList<int[]>> possibleMoves : moves) {
      for(Piece p : possibleMoves.keySet()) {
        for(int[] pos : possibleMoves.get(p)) {
          if(this.pieceVisionGrid[pos[0]][pos[1]] == null)
            this.pieceVisionGrid[pos[0]][pos[1]] = new GridPieceContainer();
          this.pieceVisionGrid[pos[0]][pos[1]].getPieces().add(p);
          if(this.pieceVisions.get(p) == null)
            this.pieceVisions.put(p, new ArrayList<int[]>());
          this.pieceVisions.get(p).add(pos);
        }
      }
    }
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
    
    this.pieceVisionGrid = new GridPieceContainer[height][width];
    this.pieceVisions = new IdentityHashMap<Piece, ArrayList<int[]>>();  
  }
  
  public Grid(Grid grid) {
    this.grid = new GridObjectContainer[grid.getGrid().length][grid.getGrid()[0].length];
    for(int y=0; y<this.grid.length; y++) {
      for(int x=0; x<this.grid[y].length; x++) {
        if(grid.getGrid()[y][x] != null)
          this.grid[y][x] = new GridObjectContainer(grid, x, y);
      }
    }
    this.pieceVisionGrid = new GridPieceContainer[grid.getGrid().length][grid.getGrid()[0].length];
    this.pieceVisions = new IdentityHashMap<Piece, ArrayList<int[]>>();
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
    for(Team team : gameState.getTeams()) {
      if(team == null)
        continue;
      this.grid[team.getBase()[0]][team.getBase()[1]] = new GridObjectContainer(GridObjects.base, Integer.parseInt(team.getId()), null);
      for(Piece p : team.getPieces())
        this.grid[p.getPosition()[0]][p.getPosition()[1]] = new GridObjectContainer(p);
    }
    this.pieceVisionGrid = new GridPieceContainer[grid.length][grid[0].length];
    this.pieceVisions = new IdentityHashMap<Piece, ArrayList<int[]>>();
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
      }
    }
    /*GridPieceContainer[][] cloneVision = new GridPieceContainer[grid.length][grid[0].length];
    for(int y=0; y<this.pieceVisionGrid.length; y++) {
      for(int x=0; x<this.pieceVisionGrid[y].length; x++) {
        if(this.pieceVisionGrid[y][x] != null) {

          cloneVision[y][x] = this.pieceVisionGrid[y][x].clone();
        }
      }
    }

    IdentityHashMap<Piece, ArrayList<int[]>> clonePieceVisions = new IdentityHashMap<Piece, ArrayList<int[]>>();
    for(Piece key : this.pieceVisions.keySet()) {
      ArrayList<int[]> list = new ArrayList<int[]>();
      for(int[] pos : this.pieceVisions.get(key)) {
        list.add(pos);
      }   
      clonePieceVisions.put(key, list);
    }*/
    
    return new Grid(clone, new IdentityHashMap<Piece, ArrayList<int[]>>(), new IdentityHashMap<Piece, ArrayList<int[]>>());
  }
  
  public boolean equals(Grid compare) {
    for(int y=0; y<this.grid.length; y++) {
      for(int x=0; x<this.grid[y].length; x++) {
        if(this.grid[y][x] == null)
          if(compare.grid[y][x] == null)
            continue;
          else
            return false;
        if(this.pieceVisionGrid[y][x] == null)
          if(compare.pieceVisionGrid[y][x] == null)
            continue;
          else
            return false;
        if(!this.grid[y][x].equals(compare.grid[y][x]))
          return false;
        if(!this.pieceVisionGrid[y][x].equals(compare.pieceVisionGrid[y][x]))
          return false;
      }
    }
    if(this.pieceVisions.keySet().size() != compare.pieceVisions.size())
      return false;
    for(Piece p : this.pieceVisions.keySet()) {
      boolean contains = false;
      for(Piece comP : compare.getPieceVisions().keySet())
        if(p.getId().equals(comP.getId())) {
          contains = true;
          if(this.pieceVisions.get(p).size() != compare.pieceVisions.get(comP).size())
            return false;
          for(int i=0; i<this.pieceVisions.get(p).size(); i++) {
            if(!Arrays.equals(this.pieceVisions.get(p).get(i), compare.pieceVisions.get(comP).get(i)))
                return false;
          }
        }
      if(!contains)
        return false;
    }
    
    return true;
  }
  
  public IdentityHashMap<Piece, ArrayList<int[]>> getPieceVisions() {
    return pieceVisions;
  }

  public void setPieceVisions(IdentityHashMap<Piece, ArrayList<int[]>> pieceVisions) {
    this.pieceVisions = pieceVisions;
  }

  public GridPieceContainer[][] getPieceVisionGrid() {
    return pieceVisionGrid;
  }

  public void setPieceVisionGrid(GridPieceContainer[][] pieceVisionGrid) {
    this.pieceVisionGrid = pieceVisionGrid;
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
