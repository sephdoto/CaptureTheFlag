package org.ctf.shared.gameanalyzer;

import java.util.Arrays;
import org.ctf.shared.ai.AIController;
import org.ctf.shared.ai.MonteCarloTreeNode;
import org.ctf.shared.ai.MonteCarloTreeSearch;
import org.ctf.shared.constants.Enums.MoveEvaluation;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Team;

/**
 * Represents the most important information that can be extracted out of a move.
 * Works only with an MCTS instance to generate the information from its nodes.
 */
public class AnalyzedGameState {
  private MonteCarloTreeNode previousState;
  private MonteCarloTreeNode userChoice;
  private MonteCarloTreeNode aiChoice;
  private int expansions;
  private int heuristic;
  private int simulations;
  private MoveEvaluation moveEvaluation;
  private int betterMoves;
  private GameState initialGameState;

  /**
   * Generates all the accessible information when getting initialized.
   * 
   * @param mcts the MCTS which analyzed the game
   * @param userChoice the user made move
   * @param aiChoice the ai's best choice
   * @param the initial GameState to get the colors from. Otherwise colors would not be set.
   * @throws NeedMoreTimeException if the game could not be analyzed because too little time was given
   */
  public AnalyzedGameState(MonteCarloTreeSearch mcts, Move userChoice, Move aiChoice, GameState initialGameState) throws NeedMoreTimeException {
    this.previousState = mcts.getRoot().deepCloneWithChildren();
    this.userChoice = findNodeByMove(userChoice);
    this.aiChoice = findNodeByMove(aiChoice);
    this.expansions = mcts.getExpansionCounter().get();
    this.simulations = mcts.getSimulationCounter().get();
    this.heuristic = mcts.getHeuristicCounter().get();
    this.initialGameState = initialGameState;

    generateInformation();
  }

  /**
   * Generates information about the moves, which can be accessed by getters.
   */
  private void generateInformation() {
    this.moveEvaluation = evaluateMove();


    MonteCarloTreeNode[] children = previousState.getChildren();

    try {
      Arrays.sort(children);
    } catch (NullPointerException npe) {npe.printStackTrace();}
    for(int child=0; child<children.length; child++) {
      if(AIController.moveEquals(children[child].getGameState().getLastMove(), userChoice.getGameState().getLastMove())) {
        this.betterMoves = child;
        break;
      }
    }
  }

  /**
   * Simple command line print out of the generated information.
   */
  public void printMe() {
    System.out.println("\ncurrent team: " + previousState.getGameState().getCurrentTeam() + " expansions: " + this.expansions);
    System.out.println(
        "\nYour move was AIs " + (this.betterMoves+1) 
        + " choice with " 
        + ((int)(Math.round((previousState.getChildren()[this.betterMoves].getV() * 100) * 100))) /100 
        + " % win chance, making it " + this.moveEvaluation);
  }

  /**
   * TODO Miss einbauen, Evaluation verbessern
   * 
   * Evaluates the users move, in comparison to the AIs move.
   */
  private MoveEvaluation evaluateMove() {
    int goodMoves = 0;
    int badMoves = 0;
    MoveEvaluation moveEvaluation;

    int difference = getPercentageDifference();
    if(difference <= 1)
      moveEvaluation = MoveEvaluation.BEST;
    else if (difference <= 3)
      moveEvaluation = MoveEvaluation.EXCELLENT;
    else if (difference <= 7)
      moveEvaluation = MoveEvaluation.GOOD;
    else if (difference <= 10)
      moveEvaluation = MoveEvaluation.OK;
    else if (difference <= 15)
      moveEvaluation = MoveEvaluation.INACCURACY;
    else if (difference <= 20)
      moveEvaluation = MoveEvaluation.MISTAKE;
    else
      moveEvaluation = MoveEvaluation.BLUNDER;    

    for(int i=0; i<previousState.getChildren().length; i++)

      moveEvaluation = (badMoves == 1 && moveEvaluation == MoveEvaluation.BLUNDER) ? MoveEvaluation.SUPERBLUNDER :
        (goodMoves == 1 && moveEvaluation == MoveEvaluation.BEST) ? MoveEvaluation.GREAT :
          moveEvaluation;
    return moveEvaluation;
  }

  ///////////////////////////////////////
  //       private useful Methods      //
  ///////////////////////////////////////
  /**
   * Finds the MonteCarloTreeNode which represents a move in this classes MonteCarloTreeSearch.
   * 
   * @param move a move
   * @return the corresponding MonteCarloTreeNode
   */
  private MonteCarloTreeNode findNodeByMove(Move move) {
    for(MonteCarloTreeNode child : this.previousState.getChildren()) {
      if(AIController.moveEquals(move, child.getGameState().getLastMove()))
        return child;
    }
    return null;
  }

  /**
   * Returns the win chance percentage difference between the ai move and user move
   * 
   * @return the win chance percentage difference between the ai move and user move
   */
  private int getPercentageDifference() {
    return (int)Math.round((aiChoice.getV() - userChoice.getV()) * 100);
  }

  /**
   * AI generated GameStates don't have colors.
   * The initial GameState got colors, so this method replaces a GameStates colors with another GameStates colors.
   * 
   * @param gameState GameState to replace colors with
   * @return the same GameState but with colors
   */
  private GameState setColors(GameState gameState) {
    for(int i=0; i<gameState.getTeams().length; i++)
      if(gameState.getTeams()[i] != null)
        gameState.getTeams()[i].setColor(this.initialGameState.getTeams()[i].getColor());
    return gameState;
  }
  ///////////////////////////////////////
  //         getter and setter         //
  ///////////////////////////////////////

  /**
   * Returns the move evaluation, how good the users move was, ranked by an algorithm
   * 
   * @return the move evaluation, how good the users move was, ranked by an algorithm
   */
  public MoveEvaluation getMoveEvaluation() {
    return this.moveEvaluation;
  }
  
  /**
   * Returns the GameState from which the user made his move
   * 
   * @return the GameState from which the user made his move
   */
  public GameState getPreviousGameState() {
    return setColors(previousState.getGameState());
  }
  
  /**
   * Returns the GameState representing the users move
   * 
   * @return the GameState representing the users move
   */
  public GameState getUserChoice() {
    return setColors(userChoice.getGameState());
  }
  
  /**
   * Returns the GameState representing the AIs best choice
   * 
   * @return the GameState representing the AIs best choice
   */
  public GameState getAiChoice() {
    return setColors(aiChoice.getGameState());
  }
  
  /**
   * Returns the AIs calculated win percentage for the users move
   * 
   * @return the AIs calculated win percentage for the users move
   */
  public int getUserWinPercentage() {
    return ((int)(Math.round((this.userChoice.getV() * 100) * 100))) /100 ;
  }
  
  /**
   * Returns the AIs calculated win percentage for its best choice
   * 
   * @return the AIs calculated win percentage for its best choice
   */
  public int getAIWinPercentage() {
    return ((int)(Math.round((this.aiChoice.getV() * 100) * 100))) /100 ;
  }
  
  /**
   * Returns how many moves the AI thinks are better than the users choice
   * 
   * @return how many moves the AI thinks are better than the users choice
   */
  public int howManyBetterMoves() {
    return this.betterMoves;
  }

  public int getExpansions() {
    return expansions;
  }
  public int getHeuristic() {
    return heuristic;
  }

  public int getSimulations() {
    return simulations;
  }
}
