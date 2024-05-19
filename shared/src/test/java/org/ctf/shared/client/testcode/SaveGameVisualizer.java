package org.ctf.shared.client.testcode;

import com.google.gson.Gson;
import java.util.HashMap;

import org.ctf.shared.gameanalyzer.GameSaveHandler;
import org.ctf.shared.state.Move;

public class SaveGameVisualizer {

  public static void main(String[] args) {
    GameSaveHandler analyzer = new GameSaveHandler();
    Gson gson = new Gson();
    boolean b = analyzer.readFile("analyzerTestDataFile");
    System.out.println(b);
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
