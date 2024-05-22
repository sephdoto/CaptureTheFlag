package org.ctf.shared.gameanalyzer;

import static org.junit.jupiter.api.Assertions.*;
import org.ctf.shared.ai.AIConfig;
import org.ctf.shared.constants.Enums.AI;
import org.junit.jupiter.api.Test;

class GameAnalyzerTest {

  @Test
  void testGameAnalyzer() {
    GameSaveHandler gsh = new GameSaveHandler();
    gsh.readFile("analyzerTestDataFile");

    int currentMove = 0;
    try {
      GameAnalyzer analyzer = new GameAnalyzer(gsh.getSavedGame(), AI.MCTS, new AIConfig(), 0);
      while(analyzer.isActive()){
        if(currentMove != analyzer.getCurrentlyAnalyzing())
          analyzer.results[currentMove++].printMe();
          System.out.print("");
        }
      
    } catch (NeedMoreTimeException nmte) {
      System.err.println("Error in " + getClass().getCanonicalName() + ":\n\t" + nmte.getLocalizedMessage());
    }
  }
}