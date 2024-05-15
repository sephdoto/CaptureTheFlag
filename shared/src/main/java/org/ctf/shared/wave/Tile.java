package org.ctf.shared.wave;

import java.util.ArrayList;
import org.ctf.shared.constants.Enums.Themes;

/**
 * Representation of a single tile in the grid.
 * 
 * @author ysiebenh
 */
 class Tile {

  // **************************************************
  // Fields
  // **************************************************

  private int value; // the integer value of the image this tile is supposed to represent. 0 if not
                     // collapsed yet.
  ArrayList<Integer> options; // all the possible images this tile can still be constrained by the
                              // tiles around it.

  TileType ruleSet; // stores the rules (i.e. what can go aroung this tile)
  
  private int x;   
  private int y;
  private WaveGrid parentGrid;
  boolean collapsed; 


  // **************************************************
  // Constructor
  // **************************************************

  Tile(int value, int x, int y, ArrayList<Integer> options, WaveGrid parentGrid) {
    this.value = value;
    this.x = x;
    this.y = y;
    this.options = options;
    this.parentGrid = parentGrid;
  }
  
  // **************************************************
  // Package methods
  // **************************************************

  /**
   * removes the TileType with the specified int value from the options array;
   * 
   * @param toRemove
   * @param t
   */
  void removeFromOptions(int toRemove, Tile t) {
    if (t != null) {
      t.options.remove(Integer.valueOf(toRemove));
    }
  }

  void addRules(TileType[] rules) {
    if(value == -1) {
      ruleSet = rules[0];
    }
    else {
      ruleSet = rules[value];
    }
  }

  int getValue() {
    return value;
  }

  /**
   * Sets a value without updating the grid. Use with caution.
   * 
   * @param value
   */
  void setValueSimple(int value) {
    this.value = value;
    if (this.parentGrid != null) {
      this.parentGrid.grid[this.y][this.x] = value;
    }
  }

  /**
   * Collapses one tile and changes the options around it accordingly.
   * 
   * @param value
   */
  void setValue(int value) {
    this.value = value;
    if (this.parentGrid != null) {
      this.parentGrid.grid[this.y][this.x] = value;
    }
    this.collapsed = true;
    this.ruleSet = this.parentGrid.rules[value];
    this.parentGrid.updateEntropy(this);
    this.options = new ArrayList<Integer>();
  }
  
  /**
   * Controller decides what weights are used according to the theme.
   * 
   * @return
   */
  int[] getWeights() {
    if(this.parentGrid.theme == Themes.STARWARS) {
      return getStarWarsWeights();
    }
    else if(this.parentGrid.theme == Themes.LOTR){
      return getLOTRWeights();
    } else {
      int[] weights = new int[options.size()];
      int i = 0;
      for(int x : options) {
        weights[i++] = 1;
      }
      return weights;
    }

  }
  
  /**
   * Hard-codes the weight values for the randomWithWeights method to adjust the likelihood of
   * individual images appearing in the final image.
   * 
   * @return
   */
  int[] getLOTRWeights() {
    int[] weights = new int[options.size()];
    int i = 0;
    for(int x : options) {
      if(x == 1) {
        weights[i] = 10;     //make the "cracks" appear less often
      } else {
        weights[i] = 1;     //default value
      }
      i++;
    }
    return weights;
  }
  
  /**
   * Hard-codes the weight values for the randomWithWeights method to adjust the likelihood of
   * individual images appearing in the final image.
   * 
   * @return
   */
  int[] getStarWarsWeights() {
    int[] weights = new int[options.size()];
    int i = 0;
    for(int x : options) {
      if(x == 3 || x == 4 || x == 5 || x == 6 || x == 35 || x == 36) {
        weights[i] = 2;     //make the "knobs" appear less often
      } else {
        weights[i] = 5;     //default value
      }
      i++;
    }
    return weights;

  }
  
  // **************************************************
  // Getters:
  // **************************************************
  
  int getX() {
    return x;
  }

  int getY() {
    return y;
  }

  /**
   * Calculates the upper neighbor of this tile according to the tiles array saved in
   * the WaveGrid object this tile is stored in.
   * 
   * @return
   */
  Tile getUpperNeighbor() {
    if (this.y == 0) {
      return null;
    }
    int location = (this.y - 1) * this.parentGrid.grid[0].length + this.x;
    if (location < 0 || location >= this.parentGrid.tiles.size()) {
      return null;
    } else {
      Tile neighbor = this.parentGrid.tiles.get(location);
      return neighbor;
    }
  }

  /**
   * Calculates the right neighbor of this tile according to the tiles array saved in
   * the WaveGrid object this tile is stored in.
   * 
   * @return
   */
  Tile getRightNeighbor() {
    if (this.x == this.parentGrid.grid[0].length - 1) {
      return null;
    }
    int location = (this.y) * this.parentGrid.grid[0].length + this.x + 1;
    if (location < 0 || location >= this.parentGrid.tiles.size()) {
      return null;
    } else {
      Tile neighbor = this.parentGrid.tiles.get(location);
      return neighbor;
    }
  }

  /**
   * Calculates the lower neighbor of this tile according to the tiles array saved in
   * the WaveGrid object this tile is stored in.
   * 
   * @return
   */
  Tile getLowerNeighbor() {
    if (this.y == this.parentGrid.grid.length - 1) {
      return null;
    }
    int location = (this.y + 1) * this.parentGrid.grid[0].length + this.x;
    if (location < 0 || location >= this.parentGrid.tiles.size()) {
      return null;
    } else {
      Tile neighbor = this.parentGrid.tiles.get(location);
      return neighbor;
    }
  }

  /**
   * Calculates the left neighbor of this tile according to the tiles array saved in
   * the WaveGrid object this tile is stored in.
   * 
   * @return
   */
  Tile getLeftNeighbor() {
    if (this.x == 0) {
      return null;
    }
    int location = (this.y) * this.parentGrid.grid[0].length + this.x - 1;
    if (location < 0 || location >= this.parentGrid.tiles.size()) {
      return null;
    } else {
      Tile neighbor = this.parentGrid.tiles.get(location);
      return neighbor;
    }
  }

}
