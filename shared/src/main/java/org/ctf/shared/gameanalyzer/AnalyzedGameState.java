package org.ctf.shared.gameanalyzer;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import org.ctf.shared.ai.AIController;
import org.ctf.shared.ai.MonteCarloTreeNode;
import org.ctf.shared.ai.MonteCarloTreeSearch;
import org.ctf.shared.constants.Enums.MoveEvaluation;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;

public class AnalyzedGameState {
  private MonteCarloTreeNode previousState;
  private MonteCarloTreeNode userChoice;
  private MonteCarloTreeNode aiChoice;
  private int expansions;
  MoveEvaluation moveEvaluation;
  private int betterMoves;



  public AnalyzedGameState(MonteCarloTreeSearch mcts, Move userChoice, Move aiChoice) throws NeedMoreTimeException {
    this.previousState = mcts.getRoot().deepCloneWithChildren();
    this.userChoice = findNodeByMove(userChoice);
    this.aiChoice = findNodeByMove(aiChoice);
    this.expansions = mcts.getExpansionCounter().get();

    generateInformation();
  }


  private void generateInformation() {
    this.moveEvaluation = evaluateMove();


    MonteCarloTreeNode[] children = previousState.getChildren();

    try {
      Arrays.sort(children);
    } catch (NullPointerException npe) {npe.printStackTrace();}

    System.out.println("\ncurrent team: " + previousState.getGameState().getCurrentTeam() + " expansions: " + this.expansions);
    for(int child=0; child<children.length; child++) {
      if(AIController.moveEquals(children[child].getGameState().getLastMove(), userChoice.getGameState().getLastMove())) {
        System.out.println(
            "\nYour move was AIs " + (child+1) 
            + " choice with " 
            + ((int)(Math.round((previousState.getChildren()[child].getV() * 100) * 100))) /100 
            + " % win chance, making it " + this.moveEvaluation);
        this.betterMoves = child;
        break;
      }
    }
  }

  /**
   * TODO
   * Miss einbauen,
   * Evaluation verbessern
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

  private MonteCarloTreeNode findNodeByMove(Move move) {
    for(MonteCarloTreeNode child : this.previousState.getChildren()) {
      if(AIController.moveEquals(move, child.getGameState().getLastMove()))
        return child;
    }
    return null;
  }

  private int getPercentageDifference() {
    return (int)Math.round((aiChoice.getV() - userChoice.getV()) * 100);
  }

  ///////////////////////////////////////
  //         getter and setter         //
  ///////////////////////////////////////

  public MoveEvaluation getMoveEvaluation() {
    return this.moveEvaluation;
  }

  public GameState getPreviousGameState() {
    return previousState.getGameState();
  }

  public GameState getUserChoice() {
    return userChoice.getGameState();
  }

  public GameState getAiChoice() {
    return aiChoice.getGameState();
  }

  public int getUserWinPercentage() {
    return ((int)(Math.round((this.userChoice.getV() * 100) * 100))) /100 ;
  }

  public int getAIWinPercentage() {
    return ((int)(Math.round((this.aiChoice.getV() * 100) * 100))) /100 ;
  }

  public int howManyBetterMoves() {
    return this.betterMoves;
  }
}
