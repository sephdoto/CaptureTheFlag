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

  /** Returns the list of currently stored scores in descending order (bottom to top) */
  public LeaderBoardEntry[] getEntries();
}
