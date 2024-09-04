package org.ctf.ui.highscore;

import org.ctf.shared.gameanalyzer.SavedGame;

/**
 * POJO representing a HighScoreBoard Entry
 *
 * @author sephdoto
 */

public class LeaderBoardEntry implements Comparable<LeaderBoardEntry>{

  private String name;
  private Score score;
  private SavedGame savedGame;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Score getScore() {
    return score;
  }

  public void setScore(Score score) {
    this.score = score;
  }

  public SavedGame getSavedGame() {
    return savedGame;
  }

  public void setSavedGame(SavedGame savedGame) {
    this.savedGame = savedGame;
  }

  @Override
  public int compareTo(LeaderBoardEntry o) {
    return this.getScore().compareTo(o.getScore());
  }
}
