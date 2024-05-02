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
    notCompatibleUp = new int[WaveFunctionCollapse.IMAGES_AMOUNT];
    notCompatibleRight = new int[WaveFunctionCollapse.IMAGES_AMOUNT];
    notCompatibleDown = new int[WaveFunctionCollapse.IMAGES_AMOUNT];
    notCompatibleLeft = new int[WaveFunctionCollapse.IMAGES_AMOUNT];
    hardRules();
    generateRules();
  }

  private TileType() {
    notCompatibleUp = new int[WaveFunctionCollapse.IMAGES_AMOUNT];
    notCompatibleRight = new int[WaveFunctionCollapse.IMAGES_AMOUNT];
    notCompatibleDown = new int[WaveFunctionCollapse.IMAGES_AMOUNT];
    notCompatibleLeft = new int[WaveFunctionCollapse.IMAGES_AMOUNT];
    hardRules();
  }
  
  // **************************************************
  // Package methods
  // **************************************************

  static TileType[] generateRuleSet() {
    TileType[] rules = new TileType[WaveFunctionCollapse.IMAGES_AMOUNT+1];
    for (int i = 0; i <= WaveFunctionCollapse.IMAGES_AMOUNT; i++) {
      rules[i] = new TileType(i);
    }
    return rules;
  }

  // **************************************************
  // Private methods
  // **************************************************

  private void generateRules() {
    TileType[] types = new TileType[WaveFunctionCollapse.IMAGES_AMOUNT+1];
    for (int i = 0; i <= WaveFunctionCollapse.IMAGES_AMOUNT; i++) {
      types[i] = new TileType();
      types[i].type = i;
      types[i].hardRules();
    }

    for (int i = 1; i <= WaveFunctionCollapse.IMAGES_AMOUNT; i++) {
      for (int j = 1, c = 0; j <= WaveFunctionCollapse.IMAGES_AMOUNT; j++,c++) {
        if (!types[i].up.equals(types[j].down)) {
          types[i].notCompatibleUp[c] = types[j].type;
        }
        
        if (!types[i].right.equals(types[j].left)) {
          types[i].notCompatibleRight[c] = types[j].type;
        }
        
        if (!types[i].down.equals(types[j].up)) {
          types[i].notCompatibleDown[c] = types[j].type;
        }
        
        if (!types[i].left.equals(types[j].right)) {
          types[i].notCompatibleLeft[c] = types[j].type;
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
   * that represents which pieces can fit together.
   */
  private void hardRules() {
    switch (this.type) {
      case 0:
        break;
      case 1:
        //First image
        up = "AAA";
        right = "AAA";
        down = "AAA";
        left = "AAA";
        break;
      case 2:
        //second image:
        up = "AAA";
        right = "ABA";
        down = "ABA";
        left = "AAA";
        break;
      case 3:
        up = "AAA";
        right = "AAA";
        down = "ABA";
        left = "ABA";
        break;
      case 4:
        up = "ABA";
        right = "AAA";
        down = "AAA";
        left = "ABA";
        break;
      case 5: //right edge
        up = "ABA";
        right = "ABA";
        down = "AAA";
        left = "AAA";
        break;
      case 6:
        //third image: 
        up = "ABA";
        right = "AAA";
        down = "ABA";
        left = "AAA";
        break;
      case 7:
        up = "AAA";
        right = "ABA";
        down = "AAA";
        left = "ABA";
        break;
      case 8:
        //fourth image:
        up = "AAB";
        right = "BBB";
        down = "AAB";
        left = "AAA";
        break;
      case 9:
        up = "AAA";
        right = "AAB";
        down = "BBB";
        left = "AAB";
        break;
      case 10:
        up = "BAA";
        right = "AAA";
        down = "BAA";
        left = "BBB";
        break;
      case 11:
        up = "BBB";
        right = "BAA";
        down = "AAA";
        left = "BAA";
        break;
      case 12:
        //fifth image:
        up = "AAB";
        right = "BBB";
        down = "AAB";
        left = "ABA";
        break;
      case 13:
        up = "ABA";
        right = "AAB";
        down = "BBB";
        left = "AAB";
        break;
      case 14:
        up = "BAA";
        right = "ABA";
        down = "BAA";
        left = "BBB";
        break;
      case 15:
        up = "BBB";
        right = "BAA";
        down = "ABA";
        left = "BAA";
        break;
      case 16:
        //sixth image:
        up = "AAB";
        right = "BAA";
        down = "AAA";
        left = "AAA";
        break;
      case 17:
        up = "AAA";
        right = "AAB";
        down = "AAB";
        left = "AAA";
        break;
      case 18:
        up = "AAA";
        right = "AAA";
        down = "BAA";
        left = "AAB";
        break;
      case 19:
        up = "BAA";
        right = "AAA";
        down = "AAA";
        left = "BAA";
        break;
      case 20:
        //seventh image:
        up = "ABA";
        right = "AAA";
        down = "ABA";
        left = "ABA";
        break;
      case 21:
        up = "ABA";
        right = "ABA";
        down = "AAA";
        left = "ABA";
        break;
      case 22:
        up = "ABA";
        right = "ABA";
        down = "ABA";
        left = "AAA";
        break;
      case 23:
        up = "AAA";
        right = "ABA";
        down = "ABA";
        left = "ABA";
        break;
      case 24:
        //eighth image:
        up = "BBB";
        right = "BBB";
        down = "BBB";
        left = "BBB";
        break;
      case 25:
        //ninth image:
        up = "BBB";
        right = "BBB";
        down = "AAB";
        left = "BAA";
        break;
      case 26:
        up = "AAB";
        right = "BBB";
        down = "BBB";
        left = "AAB";
        break;
      case 27:
        up = "BAA";
        right = "AAB";
        down = "BBB";
        left = "BBB";
        break;
      case 28:
        up = "BBB";
        right = "BAA";
        down = "BAA";
        left = "BBB";
        break;
    }
  }
  
  /**
   * The rules of the tiles are hard-coded for every TileType by assigning a String to every side
   * that represents which pieces can fit together
   */
  private void hardPipeRules() {
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
