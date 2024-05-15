package org.ctf.shared.gameanalyzer;

import java.util.Arrays;
import org.ctf.shared.ai.AIConfig;
import org.ctf.shared.ai.AIController;
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
    
    startAnalyzing();
  }
  
  public void startAnalyzing(){
    for(int turn=1; turn<game.getMoves().size(); turn++) {
      analyzeMove(turn);
      Move next = game.getMoves().get("" + turn);
      if(next != null) {
        System.out.println("Move possible? " + GameUtilities.validPos(new int[] {4,5}, game.getInitialState().getTeams()[1].getPieces()[17], game.getInitialState()));
        System.out.println("Root updated with " + next.getPieceId() + " ? " + update(next));
      }
    }
  }
  
  void analyzeMove(int turn){
    try {
      Move best = getNextMove();
      Move made = getNormalizedGameState().normalizedMove(game.getMoves().get("" +turn));
     
      MonteCarloTreeNode[] children = getMcts().getRoot().getChildren();
      
      try {
        Arrays.sort(children);
      } catch (NullPointerException npe) {npe.printStackTrace();}
      
      System.out.println("\ncurrent team: " + getMcts().getRoot().getGameState().getCurrentTeam());
      for(int child=0; child<children.length; child++) {
        if(moveEquals(children[child].getGameState().getLastMove(), made)) {
          System.out.println(child + " " + (getMcts().getRoot().getChildren()[child].getV() * 100));
          System.out.println("\nYour move was AIs " + (child+1) + " choice");
          break;
        } else {
          System.out.println(child + " " + (getMcts().getRoot().getChildren()[child].getV() * 100));
        }
      }
      
    } catch (NoMovesLeftException | InvalidShapeException e) {
      e.printStackTrace();
    }
  }
  

}
