package org.ctf.ui.highscore;

import java.util.HashMap;
import org.ctf.shared.gameanalyzer.SavedGame;

/**
 * POJO to store score data
 *
 * @author sephdoto
 */
public class Score implements Comparable<Score> {

  private String name;
  private Long playerPoints;

  public Score() {}

  public Score(String name, Long playerPoints) {
    this.name = name;
    this.playerPoints = playerPoints;
  }

  public Score(String name, SavedGame sg) {
    this.name = name;
    this.playerPoints = calculateScore(sg);
  }
  
  private long calculateScore(SavedGame sg) {
    HashMap<String, Long> nameTimePairs = fillHashMap(sg);

    // TODO score calculation is possible here
    // it is ok like this but now its just the total time
    
    return nameTimePairs.get(name);
  }
  
  /**
   * Fills a <String, Long> HashMap with the total time it took the players to complete the game
   * 
   * @return filled HashMap
   */
  private HashMap<String, Long> fillHashMap(SavedGame sg){
    HashMap<String, Long> nameTimePairs = new HashMap<String, Long>();
    for(String name : sg.getNames())
      nameTimePairs.put(name, 0L);
    
    for(int i=0; i<sg.getTimestamps().size(); i++) {
      String name = sg.getNames()[(i + getStartPlayerIndex(sg)) % sg.getNames().length];
      Long time = nameTimePairs.get(name);
      time += sg.getTimestamps().get(i);
      nameTimePairs.replace(name, time);
    }
    return nameTimePairs;
  }
  
  /**
   * @return the first players index in the names Array
   */
  private int getStartPlayerIndex(SavedGame sg) {
    for(int i=0; i<sg.getNames().length; i++)
      if(sg.getNames()[i].equals(sg.getFirstPlayer()))
        return i;
    return 0;
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

  @Override
  public int compareTo(Score o) {
    return playerPoints.compareTo(o.getPoints());
  }

  @Override
  public String toString(){
    return "Player " + getplayerName() + " scored " + getPoints();
  }
}
