package org.ctf.shared.wave;

import java.util.ArrayList;

public class WaveGrid {
  public int[][] grid;  // saves the grid with the images 
  public ArrayList<Tile> tiles;  //saves all the tiles in one loooong array
  public ArrayList<ArrayList<Integer>> options; //saves the options according to the tile grid 
  
  public WaveGrid(int[][] grid) {
    this.grid = grid;
    tiles = new ArrayList<Tile>();
    int index = 0;
    for(int y = 0, i = 0; y < grid.length; y++) {
      for(int x = 0; x < grid[y].length;x++) {
        Tile thisTile = new Tile(grid[y][x],x,y,new ArrayList<Integer>(), this);
        thisTile.collapsed = grid[y][x] == 0 ? false : true ;
        thisTile.index = index++;
        thisTile.addRules();
        tiles.add(thisTile);
      }
    }
    this.setOptions(5);
    this.setRules();
  }
  
  public void setOptions(int pictures) {
    options = new ArrayList<ArrayList<Integer>>(tiles.size());
    for(int i = 0; i < tiles.size(); i++) {
      options.add(new ArrayList<Integer>());
      for(int o = 1; o <= pictures; o++) {
        if (tiles.get(i).getValue() == 0) {
          options.get(i).add(Integer.valueOf(o));
          tiles.get(i).options.add(o);
        }
        else {
          tiles.get(i).collapsed = true;
        }
      }
    }
  }
  
  
  
  public void setRules(){
    for (Tile t : tiles) {
      t.addRules();
      for (int r : t.notCompatibleUp) {
        t.removeFromOptions(r, t.getUpperNeighbor());
      }
      for (int r : t.notCompatibleRight) {
        t.removeFromOptions(r, t.getRightNeighbor());
      }
      for (int r : t.notCompatibleDown) {
        t.removeFromOptions(r, t.getLowerNeighbor());
      }
      for (int r : t.notCompatibleLeft) {
        t.removeFromOptions(r, t.getLeftNeighbor());
      }
      
      /*
      switch (t.getValue()) {
        
        case 0:
          break;
        case 1:
          t.removeFromOptions(1, t.getUpperNeighbor());
          t.removeFromOptions(1, t.getRightNeighbor());
          t.removeFromOptions(1, t.getLowerNeighbor());
          t.removeFromOptions(1, t.getLeftNeighbor());
          break;
        case 2:
          t.removeFromOptions(2, t.getUpperNeighbor());
          t.removeFromOptions(2, t.getRightNeighbor());
          t.removeFromOptions(2, t.getLowerNeighbor());
          t.removeFromOptions(2, t.getLeftNeighbor());
          break;
        default:
          break;
      }
      */
      
    }
  }
}
