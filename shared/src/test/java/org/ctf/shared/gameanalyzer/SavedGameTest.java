package org.ctf.shared.gameanalyzer;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

/**
 * Testing the functionality of writing, saving and loading SaveGames
 * 
 * @author sistumpf
 */
class SavedGameTest {
//  @Test
  void testRealGame() {
    GameSaveHandler gsh = new GameSaveHandler();
    gsh.readFile("2024_09_05 15-51_52");
    SavedGame save = gsh.getSavedGame();
    
    System.out.println("first player: " + save.getFirstPlayer());
    System.out.println("starting time: " + save.getStartingTime());
    System.out.print("winner(s): ");
    for(String s : save.getWinner())
      System.out.print(s + " ");
    System.out.print("\nall players: ");
    for(String s : save.getNames())
      System.out.print(s + " ");
    System.out.print("\ntimes in ms: ");
    for(int s : save.getTimestamps())
      System.out.print(s + " ");
    System.out.println();
    System.out.println(save.getMoves().size() + " " + save.getTimestamps().size());
  }

  /**
   * Tests the new added Data in "V2", playernames and timestamps
   */
  @Test
  void version2test() {
    String[] players = new String[] {"A", "B", "C"};
    String firstPlayer = "B";
    long startingTime = System.currentTimeMillis();
    int[] times = new int[] {20,100,30,20,40,100};
    String[] winner = {"A"};
    
    GameSaveHandler gsh = new GameSaveHandler();
    SavedGame save = gsh.getSavedGame();
    save.setFirstPlayer(firstPlayer);
    save.setNames(players);
    save.setStartingTime(startingTime);
    save.setWinner(winner);
    for(int ms : times)
      save.addMoveDuration(ms);
    
    gsh.writeOut();
    GameSaveHandler newGsh = new GameSaveHandler();
    assertTrue(newGsh.readFile(gsh.getLastFileName()));
    assertTrue(gsh.deleteLastSavedFile());
    SavedGame newSave = newGsh.getSavedGame();
    
    assertEquals(save.getFirstPlayer(), newSave.getFirstPlayer());
    assertEquals(save.getStartingTime(), newSave.getStartingTime());
    assertArrayEquals(save.getWinner(), newSave.getWinner());
    assertArrayEquals(save.getNames(), newSave.getNames());
    save.getTimestamps().equals(newSave.getTimestamps());
  }

}
