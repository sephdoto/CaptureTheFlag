package org.ctf.shared.client.testClasses;

import com.google.gson.Gson;
import java.util.HashMap;
import org.ctf.shared.client.lib.Analyzer;
import org.ctf.shared.state.Move;

public class SaveGameVisualizer {

  public static void main(String[] args) {
    Analyzer analyzer = new Analyzer();
    Gson gson = new Gson();
    boolean b = analyzer.readFile("20240504-063951");
    HashMap<String, Move> mappy = analyzer.getSavedGame().getMoves();
    for (HashMap.Entry<String, Move> entry : mappy.entrySet()) {

      String key = entry.getKey();

      Move value = entry.getValue();
      System.out.println(gson.toJson(key));
      System.out.println(gson.toJson(value));
      // System.out.println(key + " " + value);
    }
  }
}
