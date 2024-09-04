package org.ctf.ui.highscore;

import org.ctf.shared.gameanalyzer.SavedGame;

/**
 * POJO to store score data
 *
 * @author sephdoto
 */
public class Score implements Comparable<Score> {

  private String name;
  private Integer playerScore;
  private SavedGame savedGame;

  public String getplayerName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getScore() {
    return playerScore;
  }

  public void setScore(Integer playerScore) {
    this.playerScore = playerScore;
  }

  public void setScore(int playerScore) {
    this.playerScore = playerScore;
  }

  public SavedGame getSavedGame() {
    return savedGame;
  }

  public void setSavedGame(SavedGame savedGame) {
    this.savedGame = savedGame;
  }

  @Override
  public int compareTo(Score o) {
    return playerScore.compareTo(o.getScore());
  }
}
