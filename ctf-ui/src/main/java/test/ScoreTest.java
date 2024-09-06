package test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.ctf.shared.gameanalyzer.GameSaveHandler;
import org.ctf.ui.highscore.Score;

class ScoreTest {

  @Test
  void test() {
    GameSaveHandler gsh = new GameSaveHandler();
    gsh.readFile("analyzerTestDataFile");
    
    Score score = new Score(gsh.getSavedGame().getWinner()[0], gsh.getSavedGame());
    System.out.println(score.getPoints());
  }

}
