package org.ctf.ui;

import java.util.HashMap;
import java.util.Set;
import org.ctf.shared.state.GameState;
import test.CreateTextGameStates;

/**
 * @mkrakows This class is used to Store Maps which are uniqly identified by their name
 */
public class StroeMaps {
  public static HashMap<String, GameState> maps = new HashMap<String, GameState>();
  public static Set<String> mapNames;

  public static void putMap(String name, GameState state) {
    maps.put(name, state);
  }

  public static Set<String> getValues() {
    return maps.keySet();
  }

  public static GameState getMap(String key) {
    return maps.get(key);
  }

  public static String getRandomMapName() {
    //		mapNames = maps.keySet();
    //		int size = mapNames.size();
    //		int random = new Random().nextInt(size);
    //		int i=0;
    //		for(String s: mapNames) {
    //			if(i == random ) {
    //				return s;
    //			}
    //			i++;
    //		}
    return "p1";
  }

  public static void initDefaultMaps() {
    putMap("p1", CreateTextGameStates.getTestState());
    putMap("p2", CreateTextGameStates.testGameState());
    putMap("p3", CreateTextGameStates.getTestState());
  }
}
