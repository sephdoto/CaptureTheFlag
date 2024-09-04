package org.ctf.ui.highscore;


import org.ctf.shared.gameanalyzer.SavedGame;

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
  public boolean addScore(Score score, SavedGame game);

  /**
   * Creates an LeaderBoardEntry Object from Score Object
   *
   * @param score the score you want added to the HighScoreBoard
   * @param game saves the game with the score
   * @return true if score is added, false if rejected
   */
  public LeaderBoardEntry createEntry(Score score, SavedGame game);

  /**
   * Checks if the score is one of the top 10 scores. To be used as an internal check while using
   * addScore
   *
   * @param score the score that has to be checked
   * @return true the entry can be a top 10 entry, false if not
   */
  public boolean isTop10Score(Score score);

  /**
   * Deletes a score from the list. Might not be needed because of an implementation using ordered
   * list
   *
   * @param score the score you want to delete
   * @return true the entry can be a top 10 entry, false if not
   */
  public boolean deleteScore(Score score);

  /** Returns the list of currently stored scores in descending order (top to bottom) */
  public LeaderBoardEntry[] getEntries();
}
