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
    try {
    GameAnalyzer analyzer = new GameAnalyzer(gsh.getSavedGame(), AI.IMPROVED, new AIConfig(), 0);
    } catch (NeedMoreTimeException nmte) {
      System.err.println("Error in " + getClass().getCanonicalName() + ":\n\t" + nmte.getLocalizedMessage());
    }
  }

}
