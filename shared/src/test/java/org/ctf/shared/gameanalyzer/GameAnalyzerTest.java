package org.ctf.shared.gameanalyzer;

import org.ctf.shared.ai.AIConfig;
import org.ctf.shared.ai.GameUtilities;
import org.ctf.shared.constants.Enums.AI;
import org.junit.jupiter.api.Test;

class GameAnalyzerTest {

  @Test
  void testGameAnalyzer() throws InterruptedException {
    GameSaveHandler gsh = new GameSaveHandler();
    gsh.readFile("analyzerTestDataFile");

    int currentMove = 0;
    try {
      GameAnalyzer analyzer = new GameAnalyzer(gsh.getSavedGame(), AI.MCTS, new AIConfig(), 0);
      while(analyzer.isActive() || currentMove < analyzer.howManyMoves()){
        if(currentMove != analyzer.getCurrentlyAnalyzing())
          System.out.println(GameUtilities.howManyTeams(analyzer.results[currentMove++].getPreviousGameState())+ " teams left");
       Thread.sleep(10); 
      }
      System.out.println("Analyzer finished.");
    } catch (NeedMoreTimeException nmte) {
      System.err.println("Error in " + getClass().getCanonicalName() + ":\n\t" + nmte.getLocalizedMessage());
    }
  }
}