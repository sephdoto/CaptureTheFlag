package org.ctf.shared.wave;

import java.util.ArrayList;

public class Tile {

  private int value;
  public ArrayList<Integer> options;
  private int x;
  private int y;
  private WaveGrid parentGrid;
  boolean collapsed;
  public int index;
  int[] notCompatibleUp;
  int[] notCompatibleRight;
  int[] notCompatibleDown;
  int[] notCompatibleLeft;
  
  public Tile(int value, int x, int y, ArrayList<Integer> options, WaveGrid parentGrid) {
    this.value = value;
    this.x = x;
    this.y = y;
    this.options = options;
    this.parentGrid = parentGrid;
  }
  
  public void removeFromOptions(int r, Tile t) {
    if (t != null) {
      t.options.remove(Integer.valueOf(r));
     // t.parentGrid.options.get(t.index).remove(Integer.valueOf(r));
    }
  }
  
  public void addRules() {
    switch(this.value) {
      case 0: 
        notCompatibleUp = new int[0];
        notCompatibleRight = new int[0];
        notCompatibleDown = new int[0];
        notCompatibleLeft = new int[0];
        break;
      case 1: 
        notCompatibleUp = new int[] {2,3,5};
        notCompatibleRight = new int[] {2,3,4};
        notCompatibleDown = new int[] {3,4,5};
        notCompatibleLeft = new int[] {2,4,5};
        break;
      case 2: 
        notCompatibleUp = new int[] {2,3,5};
        notCompatibleRight = new int[] {1,5};
        notCompatibleDown = new int[] {1,2};
        notCompatibleLeft = new int[] {1,3};
        break;
      case 3: 
        notCompatibleUp = new int[] {1,4};
        notCompatibleRight = new int[] {2,3,4};
        notCompatibleDown = new int[] {1,2};
        notCompatibleLeft = new int[] {1,3};
        break;
      case 4: 
        notCompatibleUp = new int[] {1,4};
        notCompatibleRight = new int[] {1,5};
        notCompatibleDown = new int[] {3,4,5};
        notCompatibleLeft = new int[] {1,3};
        break;
      case 5: 
        notCompatibleUp = new int[] {1,4};
        notCompatibleRight = new int[] {1,5};
        notCompatibleDown = new int[] {1,2};
        notCompatibleLeft = new int[] {2,4,5};
        break;
    }
  }
  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
    if(this.parentGrid != null) {
      this.parentGrid.grid[this.y][this.x] = value;
    }
    this.collapsed = true;
    this.parentGrid.setRules();
    this.options = new ArrayList<Integer>();
  }

  public int getX() {
    return x;
  }

  public void setX(int x) {
    this.x = x;
  }

  public int getY() {
    return y;
  }

  public void setY(int y) {
    this.y = y;
  }

  public Tile getUpperNeighbor() {
    if(this.getY() == 0) {
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

  public Tile getRightNeighbor() {
    if(this.getX() == this.parentGrid.grid[0].length-1) {
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

  public Tile getLowerNeighbor() {
    if(this.getY() == this.parentGrid.grid.length-1) {
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

  public Tile getLeftNeighbor() {
    if(this.getX() == 0) {
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
