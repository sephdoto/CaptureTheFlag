package org.ctf.shared.gameanalyzer;

import java.util.Arrays;
import org.ctf.shared.ai.AIConfig;
import org.ctf.shared.ai.AIController;
import org.ctf.shared.ai.GameStateNormalizer;
import org.ctf.shared.ai.GameUtilities;
import org.ctf.shared.ai.GameUtilities.InvalidShapeException;
import org.ctf.shared.ai.GameUtilities.NoMovesLeftException;
import org.ctf.shared.ai.MonteCarloTreeNode;
import org.ctf.shared.constants.Enums.AI;
import org.ctf.shared.state.Move;

/**
 * GameAnalyzer inherits AIController, as the Controller got all utilities to use the AIs.
 * It uses AIController to analyze the game with an MCTS Algorithm and returns the important information.
 * 
 * @author sistumpf
 */
public class GameAnalyzer extends AIController {
  SavedGame game;
  AnalyzedGameState[] results;
  AnalyzerThread analyze;

  /**
   * Initializes the AIController with {@link secondsTimeToThink} seconds calculating time per GameState.
   * 
   * @param game the game which gets analyzed
   * @param ai needs to be an MCTS type, if it is not, it gets changed to MCTS improved
   * @param config if this is null, the default config gets applied
   * @param secondsTimeToThink think time in seconds for analyzing one move
   */
  public GameAnalyzer(SavedGame game, AI ai, AIConfig config, int secondsTimeToThink) {
    super(game.getInitialState(), ai, config, secondsTimeToThink);
    if(config == null)
      super.setConfig(new AIConfig());
    if(ai == AI.RANDOM || ai == AI.HUMAN) {
      super.setAi(AI.MCTS);
      super.initMCTS();
    }
    this.game = game;
    this.results = new AnalyzedGameState[game.getMoves().size()];

    this.analyze = new AnalyzerThread(game, results);
  }

  /**
   * Analyzes the game in the background, allowing multitasking.
   * Gets started on creation, so if the Thread is initialized it only stops when its done analyzing.
   */
  private class AnalyzerThread extends Thread{
    SavedGame game;
    AnalyzedGameState[] results;
    int currentlyAnalyzing;
    boolean isAnalyzing;

    /**
     * Initialize with a SavedGame and an AnalyzedGameState array and the game will be analyzed.
     * Results are put in results.
     * 
     * @param game SavedGame to analyze
     * @param results AnalyzedGameState array to put the analyzing results into
     */
    public AnalyzerThread(SavedGame game, AnalyzedGameState[] results) {
      this.game = game;
      this.results = results;
      this.currentlyAnalyzing = 0;
      this.isAnalyzing = true;
      this.start();
    }
    
    /**
     * Analyzes the game, sets {@link isAnalyzing} and {@link GameAnalyzer.setActive()} false
     */
    @Override
    public void run() {
      analyzeGame();
      this.isAnalyzing = false;
      GameAnalyzer.this.setActive(false);
    }
    
    /**
     * Analyzes the complete game move for move.
     * Every freshly analyzed move gets added to {@link results}
     */
    public void analyzeGame() {
      for(; currentlyAnalyzing<game.getMoves().size(); currentlyAnalyzing++) {
        analyzeMove(currentlyAnalyzing +1);
        Move next = game.getMoves().get("" + (currentlyAnalyzing +1));
        if(next != null) {
          if(update(next)) {
            getMcts().setExpansionCounter(getMcts().getRoot().getNK());
            getMcts().setHeuristicCounter(0);
            getMcts().setSimulationCounter(0);
          }
        }
      }
    }

    /**
     * Analyzes only one move, adds it to {@link results}
     * 
     * @param turn index+1 of the current move
     * @throws NeedMoreTimeException if more time is needed
     */
    public void analyzeMove(int turn) throws NeedMoreTimeException {
      try {
        Move best = getNormalizedGameState().normalizedMove(getNextMove());
        Move made = getNormalizedGameState().normalizedMove(game.getMoves().get("" +turn));
        try {
          results[currentlyAnalyzing] = new AnalyzedGameState(getMcts(), made, best, this.game.getInitialState());
        } catch (NeedMoreTimeException nmte) {
          nmte.mentionTime(getThinkingTime());
          throw nmte;
        }
      } catch (NoMovesLeftException | InvalidShapeException e) {
        e.printStackTrace();
      }
    }
    
    /**
     * Returns the index of the results array, which is currently being analyzed.
     * 
     * @return index of currently analyzing move
     */
    protected int getCurrentlyAnalyzing() {
      return currentlyAnalyzing;
    }
    
    /**
     * @return true if the game is currently being analyzed
     */
    protected boolean isAnalyzing() {
      return isAnalyzing;
    }
  }
  
  /**
   * Returns the current results array
   * 
   * @return the current results array
   */
  public AnalyzedGameState[] getResults() {
    return this.results;
  }
  
  /**
   * Returns the index of the results array, which is currently being analyzed.
   * Use together with {@link isAnalyzing()} to call the already analyzed moves.
   * 
   * @return index of currently analyzing move
   */
  public int getCurrentlyAnalyzing() {
    return this.analyze.getCurrentlyAnalyzing();
  }
  
  /**
   * Use this as an indicator if the analyzing Thread is running.
   * super.isActive() works too, but this method should be used.
   * 
   * @return true if the game is currently being analyzed
   */
  public boolean isAnalyzing() {
    return this.analyze.isAnalyzing();
  }
  
  /**
   * Returns how many moves were made in the saved game.
   * 
   * @return how many moves were made in the saved game.
   */
  public int howManyMoves() {
    return this.game.getMoves().size();
  }
}
