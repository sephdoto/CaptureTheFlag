package org.ctf.ui.highscore;

import java.io.Serializable;

import org.ctf.shared.gameanalyzer.SavedGame;

/**
 * POJO representing a HighScoreBoard Entry
 *
 * @author sephdoto
 */

public class LeaderBoardEntry implements Comparable<LeaderBoardEntry>, Serializable {

  private String name;
  private Long points;
  private SavedGame savedGame;

  public LeaderBoardEntry(String name, Long points, SavedGame savedGame){
    this.name = name;
    this.points = points;
    this.savedGame = savedGame;
  }

  public LeaderBoardEntry(){
  }
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getPoints() {
    return points;
  }

  public void setPoints(Long points) {
    this.points = points;
  }

  public SavedGame getSavedGame() {
    return savedGame;
  }

  public void setSavedGame(SavedGame savedGame) {
    this.savedGame = savedGame;
  }

  @Override
  public int compareTo(LeaderBoardEntry o) {
    return this.getPoints().compareTo(o.getPoints());
  }
}
