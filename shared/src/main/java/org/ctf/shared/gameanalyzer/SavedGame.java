package org.ctf.shared.gameanalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;

/**
 * This class is were the save game data is held. It holds a an initial {@link GameState} along with
 * a {@link HashMap} containing all the moves made in the game in a sequential list from 1 to n. 
 * Also saves how many teams were alive at my Move.
 * New in V2:
 * Saves the starting time as UNIX timestamp, the time every player needed for his moves,
 * all players names corresponding to their indexes in the GameState and in names,
 * the winners names and the starting players name.
 * 
 * Has built-in logic to not save a move which if it is the same as the last one so the logic in Client
 * can be simpler and faster in case the Game does not need to be saved. The {@link GameSaveHandler} serves
 * as a controlling class of this class
 * 
 * @author rsyed
 * @author sistumpf
 * @version 2.0
 */
public class SavedGame implements java.io.Serializable {
  private static final long serialVersionUID = -645723293296500965L;
  private GameState initialState;
  private HashMap<String, String> teamsGaveUpMap;
  private HashMap<String, Move> lastMovesMap;
  private int counter;
  // new in V2
  /** The start time as a Unix timestamp */
  private long startingTime;
  /** The matches winners */
  private String[] winners;
  /** All the players names, not their int IDs */
  private String[] names;
  /** The first players name, not as an int ID but as his actual name */
  private String firstPlayer;
  /** 
   * ArrayList containing all the turn times.
   * Before looking at this List, {@link firstPlayer} is important to know whose entry is at index 0.
   * {@link startingTime} might be interesting, as it marks the games start as a UNIX timestamp, but not needed for this List.
   * If someone didn't make a move and therefore got skipped, his time still appears in this list,
   * making it potentially bigger than the lastMovesMap.
   */
  private ArrayList<Integer> timestamps;

  /**
   * Inits the HashMap and the counter
   *
   * @author rsyed
   */
  public SavedGame() {
    teamsGaveUpMap = new HashMap<>();
    lastMovesMap = new HashMap<>();
    counter = 1;
    this.timestamps = new ArrayList<Integer>();
   }

  /**
   * Setter for the {@link GameState} object
   *
   * @param initialState the best possible initial GameState of the game
   * @author rsyed
   */
  public void setInitialGameState(GameState initialState) {
    this.initialState = initialState;
    this.teamsGaveUpMap.put("0", "");
  }

  /**
   * Adder for the {@link Move} object this class is asked to save. Autochecks if its the same as
   * the one it last saved, saves if different, rejects the save internally otherwise for minimal
   * distraction for the {@link GameSaveHandler} object
   *
   * @param move the move to save
   * @param teams a String containing the Teams who gave up after a Move, seperated by ","
   * @author rsyed, sistumpf
   */
  public void addMove(Move move, String teams) {
    if(move==null) return;
      if (lastMovesMap.isEmpty() || 
          move.hashCode() != lastMovesMap.get(Integer.toString(counter - 1)).hashCode() ||
          (!teams.equals("") && !teams.equals(teamsGaveUpMap.get("" + (counter -1))))) {
        lastMovesMap.put(Integer.toString(counter), move); 
        teamsGaveUpMap.put("" + counter, teams);
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
  
  /**
   * Getter for the HashMap containing all the teams.
  *
  * @return a HashMap containing all the Teams at a certain point in time
  * @author sistumpf
  */
 public HashMap<String, String> getTeams() {
   return this.teamsGaveUpMap;
 }

  public String[] getWinner() {
    return winners;
  }

  public void setWinner(String[] winners) {
    this.winners = winners;
  }

  public String[] getNames() {
    return names;
  }

  public void setNames(String[] names) {
    this.names = names;
  }

  public ArrayList<Integer> getTimestamps() {
    return timestamps;
  }

  /**
   * Adds a players Move time to the list, measured in ms
   * 
   * @param ms the time it took for one Move (or till he got skipped)
   */
  public void addMoveDuration(int ms) {
    timestamps.add(ms);
  }

  public long getStartingTime() {
    return startingTime;
  }

  public void setStartingTime(long startingTime) {
    this.startingTime = startingTime;
  }

  public String getFirstPlayer() {
    return firstPlayer;
  }

  public void setFirstPlayer(String firstPlayer) {
    this.firstPlayer = firstPlayer;
  }
}
