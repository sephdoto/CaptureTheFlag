package org.ctf.ui;

import java.util.HashMap;
import java.util.Set;
/**
 * @mkrakows
 * This class is used to Store Maps which are uniqly identified by their name
 */
public  class StoreMapArrays {
	public static HashMap<String, String[][]> maps = new HashMap<String, String[][]>();
	
	
	public void putMap(String name,String[][] map) {
		maps.put(name, map);
	}
	
	public Set<String> getValues() {
		return maps.keySet();
	}
	
	public String[][] getMap(String key){
		return maps.get(key);
	}
}
