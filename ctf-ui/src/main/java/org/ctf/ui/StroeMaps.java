package org.ctf.ui;

import java.util.HashMap;
import java.util.Set;

import de.unimannheim.swt.pse.ctf.game.state.GameState;
/**
 * @mkrakows
 * This class is used to Store Maps which are uniqly identified by their name
 */
public  class StroeMaps {
	public static HashMap<String, GameState> maps = new HashMap<String, GameState>();
	
	
	public void putMap(String name,GameState state) {
		maps.put(name, state);
	}
	
	public Set<String> getValues() {
		return maps.keySet();
	}
	
	public GameState getMap(String key){
		return maps.get(key);
	}
}
