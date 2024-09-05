package org.ctf.ui.highscore;

/**
 * POJO to store score data
 *
 * @author sephdoto
 */
public class Score implements Comparable<Score> {

  private String name;
  private Long playerPoints;

  public Score(String name, Long playerPoints) {
    this.name = name;
    this.playerPoints = playerPoints;
  }

  public Score() {}

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

  @Override
  public int compareTo(Score o) {
    return playerPoints.compareTo(o.getPoints());
  }
}
