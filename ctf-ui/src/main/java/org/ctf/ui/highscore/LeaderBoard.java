package org.ctf.ui.highscore;

import java.io.Serializable;
import org.ctf.shared.gameanalyzer.SavedGame;

public class LeaderBoard implements LeaderBoardInterface, Serializable {

  private static final long serialVersionUID = -7604766932017737115L;

  private LeaderBoard() {}

  private static class SingletonHelper {
    private static final LeaderBoard instance = new LeaderBoard();
  }

  public static LeaderBoard getInstance() {
    return SingletonHelper.instance;
  }

  protected Object readResolve() {
    return getInstance();
  }

  public boolean saveAIScores;

  @Override
  public boolean addScore(Score score, SavedGame game) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'addScore'");
  }

  @Override
  public LeaderBoardEntry createEntry(Score score, SavedGame game) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'createEntry'");
  }

  @Override
  public boolean isTop10Score(Score score) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'isTopScore'");
  }

  @Override
  public boolean deleteScore(Score score) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'deleteScore'");
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
   * @param saveAIScores true if you want the board to save AI scores, false if not
   */
  public void setSaveAIScores(boolean saveAIScores) {
    this.saveAIScores = saveAIScores;
  }
}
