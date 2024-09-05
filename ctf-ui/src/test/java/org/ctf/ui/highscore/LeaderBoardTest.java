package org.ctf.ui.highscore;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import org.ctf.shared.gameanalyzer.SavedGame;
import org.junit.jupiter.api.Test;

class LeaderBoardTest {

  @Test
  void testDataBank() {
    SortedSet<LeaderBoardEntry> dataSet = new TreeSet<>();
    SavedGame sg = new SavedGame();
    dataSet.add(new LeaderBoardEntry("Seph", 12, sg));
    dataSet.add(new LeaderBoardEntry("Seph2", 14444442, sg));
    dataSet.add(new LeaderBoardEntry("Seph4123", 12312, sg));
    dataSet.add(new LeaderBoardEntry("Seph3123", 12132, sg));
    dataSet.add(new LeaderBoardEntry("Seph1212", 13232, sg));

    Integer[] output = new Integer[5];
    @SuppressWarnings("rawtypes")
    Iterator iterator = dataSet.iterator();

    int i = 0;
    while (iterator.hasNext() && i < 5) {
      LeaderBoardEntry element = (LeaderBoardEntry) iterator.next();
      output[i] = element.getPoints();
      i++;
    }

    assertEquals(output[0], Integer.valueOf(12));
    assertEquals(output[1], Integer.valueOf(12132));
    assertEquals(output[2], Integer.valueOf(12312));
    assertEquals(output[3], Integer.valueOf(13232));
    assertEquals(output[4], Integer.valueOf(14444442));
  }

  @Test
  void testReplaceNewScore() {
    SortedSet<LeaderBoardEntry> dataSet = new TreeSet<>();
    SavedGame sg = new SavedGame();
    dataSet.add(new LeaderBoardEntry("Seph", 12, sg));
    dataSet.add(new LeaderBoardEntry("Seph2", 14444442, sg));
    dataSet.add(new LeaderBoardEntry("Seph4123", 12312, sg));
    dataSet.add(new LeaderBoardEntry("Seph3123", 12132, sg));
    dataSet.add(new LeaderBoardEntry("Seph1212", 13232, sg));

    Integer[] output = new Integer[5];
    @SuppressWarnings("rawtypes")
    Iterator iterator = dataSet.iterator();

    int i = 0;
    while (iterator.hasNext() && i < 5) {
      LeaderBoardEntry element = (LeaderBoardEntry) iterator.next();
      output[i] = element.getPoints();
      i++;
    }

    assertEquals(output[0], Integer.valueOf(12));
    assertEquals(output[1], Integer.valueOf(12132));
    assertEquals(output[2], Integer.valueOf(12312));
    assertEquals(output[3], Integer.valueOf(13232));
    assertEquals(output[4], Integer.valueOf(14444442));

    if (dataSet.first().getPoints() < 1111) {
      dataSet.add(new LeaderBoardEntry("Seph123123", 1111, sg));
    }
    
    Integer[] newOutput = new Integer[5];
    int j = 0;
    Iterator iterator1 = dataSet.iterator();
    while (iterator1.hasNext() && j < 5) {
      LeaderBoardEntry element = (LeaderBoardEntry) iterator1.next();
      newOutput[j] = element.getPoints();
      j++;
    }
    assertEquals(newOutput[0], Integer.valueOf(1111));
  }

  @Test
  void testCreateEntry() {}

  @Test
  void testDeleteScore() {}

  @Test
  void testGetEntries() {}

  @Test
  void testGetInstance() {}

  @Test
  void testIsSaveAIScores() {}

  @Test
  void testIsTop10Score() {}

  @Test
  void testReadResolve() {}

  @Test
  void testSetSaveAIScores() {}
}
