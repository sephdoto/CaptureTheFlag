package org.ctf.shared.gameanalyzer;

import org.ctf.shared.ai.AIConfig;
import org.ctf.shared.ai.AIController;
import org.ctf.shared.constants.Enums.AI;

/**
 * GameAnalyzer inherits AIController, as the Controller got all utilities to use the AIs.
 * It uses AIController to analyze the game with an MCTS Algorithm and returns the important information.
 * TODO not finished yet, almost nothing done here.
 * 
 * @author sistumpf
 */
public class GameAnalyzer extends AIController {
  SavedGame game;
  int calculatingTime = 3;
  
  /**
   * Initializes the AIController with {@link calculatingTime} seconds calculating time per GameState.
   * 
   * @param game the game which gets analyzed
   * @param ai needs to be an MCTS type, if it is not, it gets changed to MCTS improved
   * @param config if this is null, the default config gets applied.
   */
  public GameAnalyzer(SavedGame game, AI ai, AIConfig config) {
    super(game.getInitialState(), ai, config, 3);
    if(config == null)
      super.setConfig(new AIConfig());
    if(ai == AI.RANDOM || ai == AI.HUMAN)
      super.setAi(AI.IMPROVED);
    this.game = game;
  }
}
