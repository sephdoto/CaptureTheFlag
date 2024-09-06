package org.ctf.ui.highscore;


/**
 * Defines the functionality which needs to be implemented
 *
 * @author rsyed
 */
public interface LeaderBoardInterface {

  /**
   * Adds the score to the list of top 10 scores
   *
   * @param score the score you want added to the HighScoreBoard
   * @param game saves the game with the score
   * @return true if score is added, false if rejected
   */
  public boolean addScore(Score score);

  /**
   * Returns the list of currently stored scores in descending order (bottom to top)
   *
   * @param numberOfEntriesYouWant
   * @return LeaderBoardEntry objects in an array
   */
  public Score[] getEntries(int numberOfEntriesYouWant);
}
