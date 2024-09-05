package org.ctf.ui.highscore;

/**
 * POJO to store score data
 *
 * @author sephdoto
 */
public class Score implements Comparable<Score> {

  private String name;
  private Integer playerPoints;

  public String getplayerName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getPoints() {
    return playerPoints;
  }

  public void setPoints(Integer playerPoints) {
    this.playerPoints = playerPoints;
  }

  public void setPoints(int playerPoints) {
    this.playerPoints = playerPoints;
  }

  @Override
  public int compareTo(Score o) {
    return playerPoints.compareTo(o.getPoints());
  }
}
