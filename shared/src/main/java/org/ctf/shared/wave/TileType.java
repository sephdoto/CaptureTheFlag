package org.ctf.shared.wave;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicIntegerArray;
import org.ctf.shared.constants.Enums;

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
  Enums.Themes theme;
  String up;
  String right;
  String down;
  String left;
  LinkedList<Integer> notCompatibleUp;
  LinkedList<Integer> notCompatibleRight;
  LinkedList<Integer> notCompatibleDown;
  LinkedList<Integer> notCompatibleLeft;
  
  // **************************************************
  // Constructors
  // **************************************************
  
  public TileType(int type, Enums.Themes theme) {
    this.type = type;
    this.theme = theme;
    notCompatibleUp = new LinkedList<Integer>();
    notCompatibleRight = new LinkedList<Integer>();
    notCompatibleDown = new LinkedList<Integer>();
    notCompatibleLeft = new LinkedList<Integer>();
    hardRules(theme);
    generateRules();
  }

  private TileType() {
    notCompatibleUp = new LinkedList<Integer>();
    notCompatibleRight = new LinkedList<Integer>();
    notCompatibleDown = new LinkedList<Integer>();
    notCompatibleLeft = new LinkedList<Integer>();
    hardRules(theme);
  }
  
  // **************************************************
  // Package methods
  // **************************************************

  static TileType[] generateRuleSet(Enums.Themes theme) {
    TileType[] rules = new TileType[WaveFunctionCollapse.imagesAmount+1];
    for (int i = 0; i <= WaveFunctionCollapse.imagesAmount; i++) {
      rules[i] = new TileType(i, theme);
    }
    return rules;
  }

  // **************************************************
  // Private methods
  // **************************************************

  private void generateRules() {
    TileType[] types = new TileType[WaveFunctionCollapse.imagesAmount+1];
    for (int i = 0; i <= WaveFunctionCollapse.imagesAmount; i++) {
      types[i] = new TileType();
      types[i].type = i;
      types[i].hardRules(this.theme);
    }

    for (int i = 1; i <= WaveFunctionCollapse.imagesAmount; i++) {
      for (int j = 1; j <= WaveFunctionCollapse.imagesAmount; j++) {
        if (!types[i].up.equals(types[j].down)) {
          types[i].notCompatibleUp.add(types[j].type);
        }
        
        if (!types[i].right.equals(types[j].left)) {
          types[i].notCompatibleRight.add(types[j].type);
        }
        
        if (!types[i].down.equals(types[j].up)) {
          types[i].notCompatibleDown.add(types[j].type);
        }
        
        if (!types[i].left.equals(types[j].right)) {
          types[i].notCompatibleLeft.add(types[j].type);
        }
      }
    }
    this.notCompatibleUp = types[type].notCompatibleUp;
    this.notCompatibleRight = types[type].notCompatibleRight;
    this.notCompatibleDown = types[type].notCompatibleDown;
    this.notCompatibleLeft = types[type].notCompatibleLeft;
  }
  
  private void hardRules(Enums.Themes theme) {
    if(theme == Enums.Themes.STARWARS) {
      hardSWRules();
    }
    else if(theme == Enums.Themes.BAYERN) {
      hardBayernRules();
    }
  }
  
  private void hardBayernRules() {
    switch (this.type) {
      case 0:
        break;
      case 1:
        up = "A";
        right = "B";
        down = "C";
        left = "D";
        break;
      case 2:
        up = "C";
        right = "E";
        down = "A";
        left = "F";
        break;
      case 3:
        up = "G";
        right = "F";
        down = "I";
        left = "E";
        break;
      case 4:
        up = "I";
        right = "D";
        down = "G";
        left = "B";
        break;
    }
  }
  /**
   * The rules of the tiles are hard-coded for every TileType by assigning a String to every side
   * that represents which pieces can fit together.  This version works for the "circuit" tiles.
   * The Strings are always to be read from left to right\top to bottom
   */
  private void hardSWRules() {
    switch (this.type) {
      case 0:
        break;
      case 1:
        //First image
        up = "BBB";
        right = "BBB";
        down = "BBB";
        left = "BBB";
        break;
      case 2:
        //second image:
        up = "GGG";
        right = "GGG";
        down = "GGG";
        left = "GGG";
        break;
      case 3:
        //third image 
        up = "GGG";
        right = "GLG";
        down = "GGG";
        left = "GGG";
        break;
      case 4:
        up = "GGG";
        right = "GGG";
        down = "GLG";
        left = "GGG";
        break;
      case 5: 
        up = "GGG";
        right = "GGG";
        down = "GGG";
        left = "GLG";
        break;
      case 6:
        up = "GLG";
        right = "GGG";
        down = "GGG";
        left = "GGG";
        break;
      case 7:
        //fourth image
        up = "GGG";
        right = "GWG";
        down = "GGG";
        left = "GWG";
        break;
      case 8:
        up = "GWG";
        right = "GGG";
        down = "GWG";
        left = "GGG";
        break;
      case 9:
        //fifth image
        up = "BGG";
        right = "GLG";
        down = "BGG";
        left = "BBB";
        //notCompatibleLeft.add(Integer.valueOf(11));
        break;
      case 10:
        up = "BBB";
        right = "BGG";
        down = "GLG";
        left = "BGG";
        //notCompatibleUp.add(Integer.valueOf(12));
        break;
      case 11:
        up = "GGB";
        right = "BBB";
        down = "GGB";
        left = "GLG";
        //notCompatibleRight.add(Integer.valueOf(9));
        break;
      case 12:
        up = "GLG";
        right = "GGB";
        down = "BBB";
        left = "GGB";
        //notCompatibleDown.add(Integer.valueOf(10));
        break;
      case 13:
        //sixth image
        up = "BGG";
        right = "GGG";
        down = "GGG";
        left = "BGG";
        //notCompatibleLeft.add(Integer.valueOf(14));
        //notCompatibleUp.add(Integer.valueOf(16));
        break;
      case 14:
        up = "GGB";
        right = "BGG";
        down = "GGG";
        left = "GGG";
        //notCompatibleUp.add(Integer.valueOf(15));
        //notCompatibleRight.add(Integer.valueOf(13));
        break;
      case 15:
        up = "GGG";
        right = "GGB";
        down = "GGB";
        left = "GGG";
        //notCompatibleRight.add(Integer.valueOf(16));
        //notCompatibleDown.add(Integer.valueOf(14));
        break;
      case 16:
        up = "GGG";
        right = "GGG";
        down = "BGG";
        left = "GGB";
        //notCompatibleDown.add(Integer.valueOf(13));
        //notCompatibleLeft.add(Integer.valueOf(15));
        break;
      case 17:
        //seventh image
        up = "GGG";
        right = "GLG";
        down = "GGG";
        left = "GLG";
        break;
      case 18:
        up = "GLG";
        right = "GGG";
        down = "GLG";
        left = "GGG";
        break;
      case 19:
        //eighth image
        up = "GWG";
        right = "GLG";
        down = "GWG";
        left = "GLG";
        break;
      case 20:
        up = "GLG";
        right = "GWG";
        down = "GLG";
        left = "GWG";
        break;
      case 21:
        //ninth image
        up = "GWG";
        right = "GGG";
        down = "GLG";
        left = "GGG";
        break;
      case 22:
        up = "GGG";
        right = "GWG";
        down = "GGG";
        left = "GLG";
        break;
      case 23:
        up = "GLG";
        right = "GGG";
        down = "GWG";
        left = "GGG";
        break;
      case 24:
        up = "GGG";
        right = "GLG";
        down = "GGG";
        left = "GWG";
        break;
      case 25:
        //tenth image:
        up = "GLG";
        right = "GLG";
        down = "GGG";
        left = "GLG";
        break;
      case 26:
        up = "GLG";
        right = "GLG";
        down = "GLG";
        left = "GGG";
        break;
      case 27:
        up = "GGG";
        right = "GLG";
        down = "GLG";
        left = "GLG";
        break;
      case 28:
        up = "GLG";
        right = "GGG";
        down = "GLG";
        left = "GLG";
        break;
      case 29:
        //eleventh image:
        up = "GLG";
        right = "GLG";
        down = "GLG";
        left = "GLG";
        break;
      case 30:
        up = "GLG";
        right = "GLG";
        down = "GLG";
        left = "GLG";
        break;
      case 31:
        //twelfth image
        up = "GLG";
        right = "GLG";
        down = "GGG";
        left = "GGG";
        break;
      case 32:
        up = "GGG";
        right = "GLG";
        down = "GLG";
        left = "GGG";
        break;
      case 33:
        up = "GGG";
        right = "GGG";
        down = "GLG";
        left = "GLG";
        break;
      case 34:
        up = "GLG";
        right = "GGG";
        down = "GGG";
        left = "GLG";
        break;
      case 35:
        //thirteenth image
        up = "GGG";
        right = "GLG";
        down = "GGG";
        left = "GLG";
        break;
      case 36:
        up = "GLG";
        right = "GGG";
        down = "GLG";
        left = "GGG";
        break;  
      case 37:
        //block
        up = "BBB";
        right = "BBB";
        down = "BBB";
        left = "BBB";
        break;
      case 38:
        //fourteenth image
        up = "BGG";
        right = "GGB";
        down = "BBB";
        left = "BBB";
        break;
      case 39:
        up = "BBB";
        right = "BGG";
        down = "BGG";
        left = "BBB";
        break;
      case 40:
        up = "BBB";
        right = "BBB";
        down = "GGB";
        left = "BGG";
        break;
      case 41:
        up = "GGB";
        right = "BBB";
        down = "BBB";
        left = "GGB";
        break;
    }
  }

  /**
   * The rules of the tiles are hard-coded for every TileType by assigning a String to every side
   * that represents which pieces can fit together. This version works for the "Rooms" tiles.
   * The Strings are always read from left to right\top to bottom
   */
  private void hardRoomRules() {
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
