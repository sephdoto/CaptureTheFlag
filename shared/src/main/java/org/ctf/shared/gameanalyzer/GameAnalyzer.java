package org.ctf.shared.gameanalyzer;

import java.util.HashMap;
import org.ctf.shared.ai.AIConfig;
import org.ctf.shared.ai.AIController;
import org.ctf.shared.ai.GameStateNormalizer;
import org.ctf.shared.ai.GameUtilities;
import org.ctf.shared.ai.GameUtilities.InvalidShapeException;
import org.ctf.shared.ai.GameUtilities.NoMovesLeftException;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Enums.AI;
import org.ctf.shared.state.GameState;
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
  int errorAt;

  /**
   * Initializes the AIController with {@link secondsTimeToThink} seconds calculating time per GameState.
   * 
   * @param game the game which gets analyzed
   * @param ai needs to be an MCTS type, if it is not, it gets changed to MCTS improved
   * @param config if this is null, the default config gets applied
   * @param secondsTimeToThink think time in seconds for analyzing one move
   */
  public GameAnalyzer(SavedGame game, AIConfig config) {
    super(game.getInitialState(), Constants.analyzeAI, config, Constants.analyzeTimeInSeconds, false);
    System.out.println(game.getInitialState().getCurrentTeam());
    System.out.println(game.getMoves().get("1").getTeamId());
    errorAt = -1;
    if(config == null)
      super.setConfig(new AIConfig());
    if(Constants.analyzeAI == AI.RANDOM || Constants.analyzeAI == AI.HUMAN) {
      super.setAi(AI.MCTS);
      super.initMCTS();
    }
    this.game = game;
    this.results = new AnalyzedGameState[game.getMoves().size()];

    this.analyze = new AnalyzerThread(game, results);
  }

  /**
   * Calculates the next move with the chose AI.
   * Takes a Move as parameter, that Move will be given to the AI to influence its calculations.
   * Instead of relying completely on its heuristic to determine how good a move is and which
   * Move will be analyzed next, the AI will start out analyzing only the given Move.
   * 
   * @param move a user made move to influence the AI
   * @return the next best move made from the chosen AI
   * @throws NoMovesLeftException
   * @throws InvalidShapeException
   */
  public Move getNextMove(Move influencer) throws NoMovesLeftException, InvalidShapeException {
    if (!this.isActive())
      return null;

    Move move;
    if(influencer != null)
      move = getMcts().getMove(influencer, thinkingTime);
    else 
      move = getMcts().getMove(thinkingTime);
    if(move != null)
      move.setTeamId(move.getPieceId().split(":")[1].split("_")[0]);
    return move == null ? null : getNormalizedGameState().unnormalizeMove(move);
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
      for(; currentlyAnalyzing<game.getMoves().size() && isAnalyzing /*TODO*/; currentlyAnalyzing++) {
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
//      System.out.println(game.getMoves().get("" + (turn)).getTeamId());
      Move best = null; 
      Move made = getNormalizedGameState().normalizedMove(game.getMoves().get("" +turn));
      try {
        if(teamGaveUpChecker(turn)) {
          made = null;
        } else {
          if(game.getMoves().get("" +(turn-1)) != null &&
              made.getTeamId().equals(getNormalizedGameState().normalizedMove(game.getMoves().get("" +(turn-1))).getTeamId())) {
            GameState updated = getMcts().getRoot().getGameState();
            updated.setCurrentTeam(Integer.parseInt(made.getTeamId()));
            System.out.println("a Team didn't move in time, the next Team moved.");
            reinitMcts(updated);
          }
        }
        if(!enoughTeamsLeft()) {
          System.err.println("Unforseen Team shortage, <1 Teams are left in GameState but its not over."
              + "\nThe SavedGame might be corrupted. Stopping the Analysis.");
          setErrorState(turn);
          return;
        }
        
        boolean exceptionCatcher = false;
        while(!exceptionCatcher) {
          try {
            best = getNextMove(made);
            exceptionCatcher = true;
          } catch (NullPointerException nmte) {
//            gameState = getMcts().getRoot().getGameState();
            //TODO
            reinitMcts(GameUtilities.toNextTeam(getMcts().getRoot().getGameState()));
            nmte.printStackTrace();
            System.err.println("Analyzer could need more time.");
//            setErrorState(turn -1);
//            exceptionCatcher = true;
//            throw nmte;
          }
        }
//        System.out.println(getMcts().printResults(best));
      } catch (NoMovesLeftException | InvalidShapeException e) {
        e.printStackTrace();
        Move move = game.getMoves().get("" + (turn));
        System.out.println(move.getPieceId());
        e.printStackTrace();
      }
      
      if(!teamGaveUpRemover(turn, made, best))
        results[turn-1] = new AnalyzedGameState(getMcts(), made, best, this.game.getInitialState(), null);
    }
    
    /**
     * Reinitializes the MCTS AI with a new GameState, in case something the AI could not predict happened.
     * Should be used in case someone gave up or skipped a turn.
     * 
     * @param newGameState the new, updated GameState
     */
    private void reinitMcts(GameState newGameState) {
      GameAnalyzer.this.getNormalizedGameState().overrideNormalizedGameState(newGameState);
      initMCTS();
    }
    
    /**
     * Checks if a Team has given up.
     * If thats the case, the Team gets removed.
     * 
     * @param turn the turn to check if the Team in the SavedGame and MCTS root are not equal
     * @return true if a team gave up and was removed
     */
    private boolean teamGaveUpRemover(int turn, Move made, Move best) {
      //TODO
//      System.out.print("Turn " + turn +". Team in sg: " + game.getMoves().get("" + (turn)).getTeamId() + " , Team in gs: " + getMcts().getRoot().getGameState().getCurrentTeam());
//      System.out.println(" " + game.getMoves().get("" + (turn)).getPieceId() + " : " + game.getMoves().get("" + (turn)).getNewPosition()[0] + "," + game.getMoves().get("" + (turn)).getNewPosition()[1]);
//      System.out.println((turn) + " -- " + game.getTeams().get("" + (turn)));
      if(!teamGaveUpChecker(turn)) {
        return false;
      }
      System.out.println("turn: "+ (turn-1) +". Team "+game.getTeams().get("" + (turn))+" gave up.");
      String[] goneTeams = game.getTeams().get("" + (turn)).split(",");
      
      GameState gameState = new GameState();
      for(String team : goneTeams) {
        HashMap<String, String> unToNorm = getNormalizedGameState().unToNorm;
        HashMap<String, String> normToUn = getNormalizedGameState().normToUn;
        
        gameState = getMcts().getRoot().getGameState();
        GameUtilities.removeTeam(gameState, Integer.parseInt(team));
        setNormalizedGameState(new GameStateNormalizer(gameState, true));
        if(gameState.getTeams()[gameState.getCurrentTeam()] == null)
          GameUtilities.toNextTeam(gameState);
        
        getNormalizedGameState().unToNorm = unToNorm;
        getNormalizedGameState().normToUn = normToUn;
      }

      gameState.setLastMove(null);
      made = null;
      
      results[turn-1] = new AnalyzedGameState(getMcts(), made, best, this.game.getInitialState(), gameState);
      
      initMCTS();
      return true;
    }
    
    /**
     * Checks if a Team has given up.
     * 
     * @param turn the turn to check if the Team in the SavedGame and MCTS root are not equal
     * @return true if a team gave up
     */
    private boolean teamGaveUpChecker(int turn) {
      boolean someoneGaveUp = !game.getTeams().get("" + turn).equals("") &&
          GameUtilities.moveEquals(game.getMoves().get("" + turn), game.getMoves().get("" + (turn -1)));
      return someoneGaveUp;
    }
    
    /**
     * Checks if more than 1 Team is left.
     * 
     * @return true if enough Teams are left to continue the analysis
     */
    private boolean enoughTeamsLeft() {
      GameState gameState =  getMcts().getRoot().getGameState();
      int teams=0;
      for(int i=0; i<gameState.getTeams().length; i++) {
        if(gameState.getTeams()[i] != null)
          teams++;
      }
      return teams > 1;
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
  
  /**
   * If an Error occurs, the Analyzer should communicate that, so all processes relying on it can be stopped.
   * Use this Method to set the Analyzer in an Error State and Stop the analysis.
   */
  private void setErrorState(int turn) {
    errorAt = turn;
    this.analyze.isAnalyzing = false;
  }
  
  /**
   * Returns if any Errors have occurred during the analysis.
   * If true, Error handling can happen and all processes relying on the Analyzer can be stopped.
   * 
   * @return true if no Errors have occurred during the Analysis
   */
  public int noErrors() {
    return errorAt;
  }
}
