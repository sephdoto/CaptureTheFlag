package org.ctf.ui.highscore;


import org.ctf.shared.gameanalyzer.SavedGame;

public class testClass {
  public static void main(String[] args) {
    SavedGame sg = new SavedGame();
    LeaderBoardController lcCon = new LeaderBoardController();
    LeaderBoardController.addEntry(new Score("Seph", 12L));
    LeaderBoardController.addEntry(new Score("Seph", 12321L));
    LeaderBoardController.addEntry(new Score("Seph", 121231L));
    LeaderBoardController.addEntry(new Score("Seph", 1112333212L));
    LeaderBoardController.addEntry(new Score("Seph", 11123332L));
    LeaderBoardController.addEntry(new Score("Seph", 11123332L));
    LeaderBoardController.addEntry(new Score("Seph", 11133232L));
    boolean b = LeaderBoardController.saveCurrentBoard();
    System.out.println(b + "what happened");

    Score[] arr = LeaderBoardController.getEntries(5);
    for (Score s : arr) {
        System.out.println(s);
    }
  }
}
