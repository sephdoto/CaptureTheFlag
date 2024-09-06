package org.ctf.ui.highscore;

import java.util.Iterator;
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
    Score[] re;
    if(dataSet.size() < number){
      re = new Score[dataSet.size()];
    } else {
      re = new Score[number];
    }
    Iterator iterator = dataSet.iterator();
    int i = 0;
    while (iterator.hasNext() && i <= number-1) {
      Score element = (Score) iterator.next();
      if(element != null){
        re[i] = element;
        i++;
      }
    }
    return re;
  }
}
