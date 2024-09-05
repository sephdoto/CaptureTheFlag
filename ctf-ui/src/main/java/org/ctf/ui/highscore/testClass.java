package org.ctf.ui.highscore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.gameanalyzer.SavedGame;

public class testClass {
  public static void main(String[] args) throws IOException {

    String toTest = "aaaaaaaaaadsdasdadasd";
    boolean b = toTest.matches("(?i).*ai.*");
    System.out.println(b);

    SortedSet<LeaderBoardEntry> dataSet = new TreeSet<>();
    SavedGame sg = new SavedGame();
    dataSet.add(new LeaderBoardEntry("Seph", 12, sg));
    dataSet.add(new LeaderBoardEntry("Seph", 13232, sg));
    dataSet.add(new LeaderBoardEntry("Seph", 12312, sg));
    dataSet.add(new LeaderBoardEntry("Seph", 12132, sg));
    dataSet.add(new LeaderBoardEntry("Seph", 14444442, sg));

    Iterator iterator = dataSet.iterator();
    while (iterator.hasNext()) {
      LeaderBoardEntry element = (LeaderBoardEntry) iterator.next();
      System.out.println(element.getPoints());
    }

    dataSet.add(new LeaderBoardEntry("Seph123123", 1111, sg));

    Iterator iterator1 = dataSet.iterator();
    while (iterator1.hasNext()) {
      LeaderBoardEntry element = (LeaderBoardEntry) iterator1.next();
      System.out.println(element.getPoints());
    }

    File f = new File(Constants.TOLEADERBOARD + "leaderboard" + ".json");
    if (f.exists() && !f.isDirectory()) {
      System.out.println("file already Exists");
    } else {
      String fileName = "leaderboard";
      FileOutputStream fileOutStream =
          new FileOutputStream(Constants.TOLEADERBOARD + fileName + ".json");

      ObjectOutputStream objectOutStream = new ObjectOutputStream(fileOutStream);

      objectOutStream.writeObject(new LeaderBoard());
      objectOutStream.close();
      fileOutStream.close();
    }
  }
}
