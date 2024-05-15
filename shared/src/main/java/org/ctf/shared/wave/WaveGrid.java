package org.ctf.shared.wave;

import java.util.ArrayList;
import org.ctf.shared.constants.Enums.Themes;

/**
 * Representation of the grid that the Wave Function Collapse algorithm works with.
 * 
 * @author ysiebenh
 * 
 */
public class WaveGrid {

  // **************************************************
  // Fields
  // **************************************************
  
  public int[][] grid; // saves the grid with the images
  public ArrayList<Tile> tiles; // saves all the tiles in one loooong array
  TileType[] rules; // a copy to save the rules for all INDIVIDUAL tiles as refernce
  Themes theme;

  // **************************************************
  // Constructor
  // **************************************************

  /**
   * Initializes the int[][] grid and the tiles List. Creates tiles and fills them with rules from
   * the ruleset.
   * 
   * @param grid
   * @param images
   * @param theme
   */
  public WaveGrid(int[][] grid, int images, Themes theme) {
    
    this.theme = theme;
    int[][] newGrid = new int[grid.length][grid[0].length];
    
    for(int i = 0; i < grid.length; i++) {
      for(int j = 0; j < grid[i].length; j++) {
        newGrid[i][j] = grid[i][j];
      }
    }
    this.grid = newGrid;
    tiles = new ArrayList<Tile>();
    for (int y = 0; y < grid.length; y++) {
      for (int x = 0; x < grid[y].length; x++) {
        Tile thisTile = new Tile(grid[y][x], x, y, new ArrayList<Integer>(), this);
          thisTile.collapsed = grid[y][x] == 0 ? false : true;
        tiles.add(thisTile);
      }
    }
    this.setOptions();  // initializes every non-collapsed tile with all options 
    this.setRules();    // sets the rules and updates the options accordingly
  }

  // **************************************************
  // Package methods
  // **************************************************


  /**
   * Updates the options in the grid when a new tile has been collapsed. Used in Tile.setValue().
   * 
   * @param t
   */
  void updateEntropy(Tile t) {
    if (t != null && t.collapsed) {

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
  
  // **************************************************
  // Private methods 
  // **************************************************
  
  /**
   * Adds every single image to the initial set of options (coded as integers 1-x).
   * 
   * @param pictures
   */
  private void setOptions() {
    
    for (int i = 0; i < tiles.size(); i++) {

      if (tiles.get(i).getValue() == 0) {
        int o = this.theme == Themes.STARWARS ? 2 : 1; // for the Star Wars pattern the first image
                                                       // isn't used in the algorithm
        for (; o <= WaveFunctionCollapse.imagesAmount; o++) {
          tiles.get(i).options.add(o);
        }
      } else {
        tiles.get(i).collapsed = true;
      }
    }
    }
  

  /**
   * Generates the rules for every single piece and adjusts the options accordingly for the
   * initial grid.
   */
  private void setRules() {
    rules = TileType.generateRuleSet(this.theme);
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
