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
    GameAnalyzer analyzer = new GameAnalyzer(gsh.getSavedGame(), AI.RANDOM, new AIConfig(), 1);
  }

}
