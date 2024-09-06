package org.ctf.ui.highscore;

import org.ctf.shared.gameanalyzer.SavedGame;

/**
 * POJO to store score data
 *
 * @author sephdoto
 */
public class Score implements Comparable<Score> {

  private String name;
  private Long playerPoints;
  private SavedGame sg;

  public Score() {}

  public Score(String name, Long playerPoints) {
    this.name = name;
    this.playerPoints = playerPoints;
  }

  public Score(String name, SavedGame sg) {
    this.name = name;
    this.sg = sg;
  }

  public String getplayerName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getPoints() {
    return playerPoints;
  }

  public void setPoints(Long playerPoints) {
    this.playerPoints = playerPoints;
  }

  public SavedGame getSg() {
    return sg;
  }

  public void setSg(SavedGame sg) {
    this.sg = sg;
  }

  @Override
  public int compareTo(Score o) {
    return playerPoints.compareTo(o.getPoints());
  }
}
