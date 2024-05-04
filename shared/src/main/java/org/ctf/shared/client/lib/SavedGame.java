package org.ctf.shared.client.lib;

import java.util.HashMap;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;

/**
 * This class is were the save game data is held. It holds a an initial {@link GameState} along with
 * a {@link HashMap} containing all the moves made in the game in a sequential list from 1 to n. Has
 * built-in logic to not save a move which if it is the same as the last one so the logic in Client
 * can be simpler and faster in case the Game does not need to be saved. The {@link Analyzer} serves
 * as a controlling class of this class
 *
 * @author rsyed
 */
public class SavedGame implements java.io.Serializable {
  private GameState initialState;
  private HashMap<String, Move> lastMovesMap = new HashMap<>();
  private int counter = 1;

  /**
   * Inits the HashMap and the counter
   *
   * @author rsyed
   */
  public SavedGame() {}

  /**
   * Setter for the {@link GameState} object
   *
   * @param initialState the best possible initial GameState of the game
   * @author rsyed
   */
  public void setInitialGameState(GameState initialState) {
    this.initialState = initialState;
  }

  /**
   * Adder for the {@link Move} object this class is asked to save. Autochecks if its the same as
   * the one it last saved, saves if different, rejects the save internally otherwise for minimal
   * distraction for the {@link Analyzer} object
   *
   * @param move the move to save
   * @author rsyed
   */
  public void addMove(Move move) {
    if(move==null) return;
      if (lastMovesMap.isEmpty()) {
        lastMovesMap.put(Integer.toString(counter), move);
        counter++;
      } else if (move.hashCode() != lastMovesMap.get(Integer.toString(counter - 1)).hashCode()) {
        this.lastMovesMap.put(Integer.toString(counter), move);
        counter++;
      }
  }

  /**
   * Getter for the {@link GameState} object this class is asked to save.
   *
   * @return GameState object
   * @author rsyed
   */
  public GameState getInitialState() {
    return this.initialState;
  }

  /**
   * Getter for the HashMap containing all the moves.
   *
   * @return HashMap<String,Move> object
   * @author rsyed
   */
  public HashMap<String, Move> getMoves() {
    return this.lastMovesMap;
  }
}
