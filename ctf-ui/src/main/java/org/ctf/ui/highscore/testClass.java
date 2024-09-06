package org.ctf.ui.highscore;

import org.ctf.shared.gameanalyzer.SavedGame;

public class testClass {
  public static void main(String[] args) {
    SavedGame sg = new SavedGame();
    LeaderBoardController lcCon = new LeaderBoardController();
    lcCon.getBoard().addScore(new Score("Seph", 12L));
    lcCon.getBoard().addScore(new Score("Seph", 12321L));
    lcCon.getBoard().addScore(new Score("Seph", 121231L));
    lcCon.getBoard().addScore(new Score("Seph", 111232L));
    boolean b = lcCon.saveCurrentBoard();
    System.out.println(b + "what happened");
  }
}
