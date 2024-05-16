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
  int currentlyAnalyzing;
  
  /**
   * Initializes the AIController with {@link calculatingTime} seconds calculating time per GameState.
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
    this.currentlyAnalyzing = 0;
    
    startAnalyzing();
  }
  
  public void startAnalyzing(){
    for(; currentlyAnalyzing<game.getMoves().size(); currentlyAnalyzing++) {
      analyzeMove(currentlyAnalyzing +1);
      Move next = game.getMoves().get("" + (currentlyAnalyzing +1));
      if(next != null) {
        if(update(next));
          getMcts().setExpansionCounter(getMcts().getRoot().getNK());
      }
    }
  }
  
  void analyzeMove(int turn){
    try {
      Move best = getNormalizedGameState().normalizedMove(getNextMove());
      Move made = getNormalizedGameState().normalizedMove(game.getMoves().get("" +turn));
     
      results[currentlyAnalyzing] = new AnalyzedGameState(getMcts(), made, best);
      
    } catch (NoMovesLeftException | InvalidShapeException e) {
      e.printStackTrace();
    }
  }
  

}
