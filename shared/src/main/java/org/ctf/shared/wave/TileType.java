package org.ctf.shared.wave;

/**
 * Representation of one unique Tile Type with a unique set of rules and value (the value is saved
 * in "type" here)
 * 
 * @author ysiebenh
 */

public class TileType {

  // **************************************************
  // Fields
  // **************************************************

  int type;
  final int TYPEMAX = 5; // this needs to be adjusted to the number of possible tiles
  String up;
  String right;
  String down;
  String left;
  int[] notCompatibleUp;
  int[] notCompatibleRight;
  int[] notCompatibleDown;
  int[] notCompatibleLeft;
  
  // **************************************************
  // Constructors
  // **************************************************
  
  public TileType(int type) {
    this.type = type;
    notCompatibleUp = new int[TYPEMAX];
    notCompatibleRight = new int[TYPEMAX];
    notCompatibleDown = new int[TYPEMAX];
    notCompatibleLeft = new int[TYPEMAX];
    hardRules();
    generateRules();
  }

  private TileType() {
    notCompatibleUp = new int[TYPEMAX];
    notCompatibleRight = new int[TYPEMAX];
    notCompatibleDown = new int[TYPEMAX];
    notCompatibleLeft = new int[TYPEMAX];
    hardRules();
  }
  
  // **************************************************
  // Package methods
  // **************************************************

  static TileType[] generateRuleSet() {
    TileType[] rules = new TileType[6];
    for (int i = 0; i <= 5; i++) {
      rules[i] = new TileType(i);
    }
    return rules;
  }

  // **************************************************
  // Private methods
  // **************************************************

  private void generateRules() {
    TileType[] types = new TileType[5 + 1];
    for (int i = 0; i <= 5; i++) {
      types[i] = new TileType();
      types[i].type = i;
      types[i].hardRules();
    }

    for (int i = 1; i <= 5; i++) {
      int c = 0;
      for (int j = 1; j <= 5; j++) {
        if (!types[i].up.equals(types[j].down)) {
          types[i].notCompatibleUp[c++] = types[j].type;
        }
      }
    }

    for (int i = 1; i <= 5; i++) {
      int c = 0;
      for (int j = 1; j <= 5; j++) {
        if (!types[i].right.equals(types[j].left)) {
          types[i].notCompatibleRight[c++] = types[j].type;
        }
      }
    }

    for (int i = 1; i <= 5; i++) {
      int c = 0;
      for (int j = 1; j <= 5; j++) {
        if (!types[i].down.equals(types[j].up)) {
          types[i].notCompatibleDown[c++] = types[j].type;
        }
      }
    }

    for (int i = 1; i <= 5; i++) {
      int c = 0;
      for (int j = 1; j <= 5; j++) {
        if (!types[i].left.equals(types[j].right)) {
          types[i].notCompatibleLeft[c++] = types[j].type;
        }
      }
    }

    this.notCompatibleUp = types[type].notCompatibleUp;
    this.notCompatibleRight = types[type].notCompatibleRight;
    this.notCompatibleDown = types[type].notCompatibleDown;
    this.notCompatibleLeft = types[type].notCompatibleLeft;
  }

  /**
   * The rules of the tiles are hard-coded for every TileType by assigning a String to every side
   * that represents which pieces can fit together
   */
  private void hardRules() {
    switch (this.type) {
      case 0:
        break;
      case 1:
        up = "AAA";
        right = "AAA";
        down = "AAA";
        left = "AAA";
        break;
      case 2:
        up = "AAA";
        right = "ABA";
        down = "ABA";
        left = "ABA";
        break;
      case 3:
        up = "ABA";
        right = "AAA";
        down = "ABA";
        left = "ABA";
        break;
      case 4:
        up = "ABA";
        right = "ABA";
        down = "AAA";
        left = "ABA";
        break;
      case 5:
        up = "ABA";
        right = "ABA";
        down = "ABA";
        left = "AAA";
        break;
    }
  }

}
