package org.ctf.shared.wave;

import java.time.LocalDateTime;
import java.util.ArrayList;
/**
 * @author ysiebenh
 */
public class WaveGrid {
  
  // **************************************************
  // Fields
  // **************************************************
  
  public int[][] grid;  // saves the grid with the images 
  public ArrayList<Tile> tiles;  //saves all the tiles in one loooong array
  public ArrayList<ArrayList<Integer>> options; //saves the options according to the tile grid 
  private int uniqueImages; //the amount of different images used
  TileType[] rules;;
  
  // **************************************************
  // Constructor
  // **************************************************
  
  WaveGrid(int[][] grid, int images) {
    long nowMillis = System.currentTimeMillis();
    this.grid = grid;
    this.uniqueImages = images;
    tiles = new ArrayList<Tile>();
    int index = 0;
    for(int y = 0, i = 0; y < grid.length; y++) {
      for(int x = 0; x < grid[y].length;x++) {
        Tile thisTile = new Tile(grid[y][x],x,y,new ArrayList<Integer>(), this);
        thisTile.collapsed = grid[y][x] == 0 ? false : true ;
        thisTile.index = index++;
        //thisTile.addRules();
        tiles.add(thisTile);
      }
    }
    this.setOptions();
    this.setRules();
    
    System.out.println(" WaveGrid initializer took " + (System.currentTimeMillis() - nowMillis) + "ms");
  }
  
  // **************************************************
  // Package methods
  // **************************************************
  
  
  /**
   * Updates the options in the grid when a new tile has been collapsed.
   * @param t
   */
  void updateEntropy(Tile t) {

    for (int r : t.ruleSet.notCompatibleUp) {
      t.removeFromOptions(r, t.getUpperNeighbor());
    }
    for (int r : t.ruleSet.notCompatibleRight) {
      t.removeFromOptions(r, t.getRightNeighbor());
    }
    for (int r : t.ruleSet.notCompatibleDown) {
      t.removeFromOptions(r, t.getLowerNeighbor());
    }
    for (int r : t.ruleSet.notCompatibleLeft) {
      t.removeFromOptions(r, t.getLeftNeighbor());
    }
  }
  
  // **************************************************
  // Private methods 
  // **************************************************
  
  /**
   * adds every single image to the options array (coded as integers 1-x)
   * @param pictures
   */
  private void setOptions() {
    options = new ArrayList<ArrayList<Integer>>(tiles.size());
    for(int i = 0; i < tiles.size(); i++) {
      options.add(new ArrayList<Integer>());
      for(int o = 1; o <= uniqueImages; o++) {
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
  
  
  /**
   * Generates the rules for every single piece and adjusting the options accordingly for the
   * initial grid.
   */
  private void setRules(){
    rules = TileType.generateRuleSet();
    for (Tile t : tiles) {
      t.addRules(rules);
    
      
      for (int r : t.ruleSet.notCompatibleUp) {
        t.removeFromOptions(r, t.getUpperNeighbor());
      }
      for (int r : t.ruleSet.notCompatibleRight) {
        t.removeFromOptions(r, t.getRightNeighbor());
      }
      for (int r : t.ruleSet.notCompatibleDown) {
        t.removeFromOptions(r, t.getLowerNeighbor());
      }
      for (int r : t.ruleSet.notCompatibleLeft) {
        t.removeFromOptions(r, t.getLeftNeighbor());
      }
      
    }
  }
 
}
