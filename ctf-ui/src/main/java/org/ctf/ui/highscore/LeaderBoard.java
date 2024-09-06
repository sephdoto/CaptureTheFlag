package org.ctf.ui.highscore;

import java.util.SortedSet;
import java.util.TreeSet;

public class LeaderBoard implements LeaderBoardInterface {

  private SortedSet<Score> dataSet = new TreeSet<>();

  @Override
  public boolean addScore(Score score) {
    return dataSet.add(score);
  }

  @Override
  public Score[] getEntries(int number) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getEntries'");
  }
}
