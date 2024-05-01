package org.ctf.shared.wave;

import java.util.ArrayList;

/**
 * Representation of a single tile in the Grid.
 * 
 * @author ysiebenh
 */
public class Tile {

  // **************************************************
  // Fields
  // **************************************************

  private int value;
  ArrayList<Integer> options;
  private int x;
  private int y;
  private WaveGrid parentGrid;
  boolean collapsed; // TODO implement properly
  int index;
  TileType ruleSet;

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

  void removeFromOptions(int r, Tile t) {
    if (t != null) {
      t.options.remove(Integer.valueOf(r));
    }
  }

  void addRules(TileType[] rules) {
    ruleSet = rules[value];
  }

  int getValue() {
    return value;
  }

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
  
  // **************************************************
  // GetNeighbor methods:
  // **************************************************
  
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
