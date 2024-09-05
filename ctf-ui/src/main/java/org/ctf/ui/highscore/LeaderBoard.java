package org.ctf.ui.highscore;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;
import org.ctf.shared.gameanalyzer.SavedGame;

public class LeaderBoard implements LeaderBoardInterface, Serializable {

  /** Start of Leaderboard DATA Block */
  private boolean saveAIScores;

  private SortedSet<LeaderBoardEntry> dataSet = new TreeSet<>();

  @Override
  public boolean addScore(Score score, SavedGame game) {
    return dataSet.add(createEntry(score, game));
  }

  @Override
  public LeaderBoardEntry createEntry(Score score, SavedGame game) {
    return new LeaderBoardEntry(score.getplayerName(), score.getPoints(), game);
  }

  @Override
  public LeaderBoardEntry[] getEntries() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getEntries'");
  }

  /**
   * Checks if the score is AI Player
   *
   * @param score the score that has to be checked
   * @return true if the score entry is an AI, false if not
   */
  // TODO Change detector if a different one is needed
  private boolean isAIPlayer(Score score) {
    return score.getplayerName().matches("(?i).*ai.*");
  }

  /**
   * Returns if AI Player scores have to be saved
   *
   * @return true if they have to be saved, false if not
   */
  public boolean isSaveAIScores() {
    return saveAIScores;
  }

  /**
   * Sets the behaviour of the leader board
   *
   * @param saveAIScores true if you want the board to save AI scores, false if not
   */
  public void setSaveAIScores(boolean saveAIScores) {
    this.saveAIScores = saveAIScores;
  }
}
