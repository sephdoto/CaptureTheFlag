package org.ctf.shared.gameanalyzer;

import java.util.Arrays;
import org.ctf.shared.ai.GameUtilities;
import org.ctf.shared.ai.MonteCarloTreeNode;
import org.ctf.shared.ai.MonteCarloTreeSearch;
import org.ctf.shared.constants.Enums.MoveEvaluation;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;

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
  private GameState userGaveUp;

  /**
   * Generates all the accessible information when getting initialized.
   * 
   * @param mcts the MCTS which analyzed the game
   * @param userChoice the user made move
   * @param aiChoice the ai's best choice
   * @param the initial GameState to get the colors from. Otherwise colors would not be set.
   * @throws NeedMoreTimeException if the game could not be analyzed because too little time was given
   */
  public AnalyzedGameState(MonteCarloTreeSearch mcts, Move userChoice, Move aiChoice, GameState initialGameState, GameState userGaveUp) throws NeedMoreTimeException {
    this.previousState = mcts.getRoot().deepCloneWithChildren();
    this.aiChoice = findNodeByMove(aiChoice);
    this.expansions = mcts.getExpansionCounter().get();
    this.simulations = mcts.getSimulationCounter().get();
    this.heuristic = mcts.getHeuristicCounter().get();
    this.initialGameState = initialGameState;
    if(userChoice != null) {
      this.userChoice = findNodeByMove(userChoice);
      generateInformation();
    }
    this.userGaveUp = userGaveUp;
  }

  /**
   * Generates information about the moves, which can be accessed by getters.
   */
  private void generateInformation() {
    this.moveEvaluation = evaluateMove();


    MonteCarloTreeNode[] children = previousState.getChildren();

    int errors = 0;
    Exception ex = null;
    try {
      Arrays.sort(children);
    } catch (NullPointerException npe) {npe.printStackTrace();}
    for(int child=0; child<children.length; child++) {
      try {
        if(GameUtilities.moveEquals(children[child].getGameState().getLastMove(), userChoice.getGameState().getLastMove())) {
          this.betterMoves = child;
          break;
        }
      } catch (Exception e) {
        ex = e;
        errors++;
      };
    }
    if(errors > 0)
      System.err.println(
          " userChoice was " + errors + " times null. A prior Move might not have been recorded. : " 
              + ex.getClass().getCanonicalName() 
              + " at (AnalyzedGameState.java:" 
              + getExceptionLineNumber(ex)
              +")");
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
    try {
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
    } catch (Exception e) {
      moveEvaluation = MoveEvaluation.ERROR;
    }
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
      if(GameUtilities.moveEquals(move, child.getGameState().getLastMove()))
        return child;
    }

//    System.err.println("No child found.");
//    this.previousState.printGrid();
//    for(MonteCarloTreeNode child : this.previousState.getChildren())
//      System.out.println("\t" + child.getGameState().getLastMove().getPieceId() +
//          " [" + child.getGameState().getLastMove().getNewPosition()[0] + "," + child.getGameState().getLastMove().getNewPosition()[1] + "]" + "    " + 
//          move.getPieceId() + 
//          " [" + move.getNewPosition()[0] + "," + move.getNewPosition()[1] + "]");
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
   * TODO: for testing purposes, the colors have been hardcoded.
   *
   * @param gameState GameState to replace colors with
   * @return the same GameState but with colors
   */
  private GameState setColors(GameState gameState) {
    if(this.initialGameState != null)
      for(int i=0; i<gameState.getTeams().length; i++)
        if(gameState.getTeams()[i] != null) {
          String defaultColor = this.initialGameState.getTeams()[i].getColor();
          gameState.getTeams()[i].setColor(defaultColor);
          switch(i) {
            case 0: gameState.getTeams()[i].setColor("#ff00ff");
            break;
            case 1: gameState.getTeams()[i].setColor("#cccc00");
            break;
            case 2: gameState.getTeams()[i].setColor("#ff0000");
            break;
            case 3: gameState.getTeams()[i].setColor("#069420");
            break;
            case 4: gameState.getTeams()[i].setColor("#0092FF");
            break;
            case 5: gameState.getTeams()[i].setColor("#ff8200");
            break;
            default: if(defaultColor.startsWith("0x")) gameState.getTeams()[i].setColor("#" + defaultColor.substring(2, 8));
          }
        }

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
    if(userGaveUp != null)
      return setColors(userGaveUp);
    if(userChoice != null)
      return setColors(userChoice.getGameState());
    else return null;
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
    try {
      return ((int)(Math.round((this.userChoice.getV() * 100) * 100))) /100 ;
    } catch (Exception e) {
      return 0;
    }
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

  public GameState getUserGaveUp() {
    return this.userGaveUp;
  }
  
  /**
   * Finds this class in an Exceptions StackTrace and returns the line number, the Exception got thrown at
   * 
   * @author sistumpf
   * @param e Exception to search the StackTrace
   * @return line number which caused the Exception, 1 if something unforseen happened
   */
  private int getExceptionLineNumber(Exception e) {
    for(StackTraceElement s : e.getStackTrace())
      if(s.toString().contains("AnalyzedGameState.java"))
        return Integer.parseInt(s.toString().split("AnalyzedGameState.java:")[1].replace(")", ""));
    return 1;
  }
}
