package org.ctf.shared.gameanalyzer;

import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Method;
import org.ctf.shared.ai.AIConfig;
import org.ctf.shared.ai.GameUtilities.InvalidShapeException;
import org.ctf.shared.ai.GameUtilities.NoMovesLeftException;
import org.ctf.shared.ai.ReferenceMove;
import org.ctf.shared.ai.TestValues;
import org.ctf.shared.ai.mcts3.MCTS;
import org.ctf.shared.ai.mcts3.ReferenceGameState;
import org.ctf.shared.ai.mcts3.TreeNode;
import org.ctf.shared.ai.random.RandomAI;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.junit.jupiter.api.Test;

class AnalyzedGameStateTest {

  @Test
  void testGeneralFunctionality() throws NoMovesLeftException, InvalidShapeException {
    MCTS mcts = new MCTS(new TreeNode(null, new ReferenceGameState(TestValues.getTestState()), new int[2], new ReferenceMove(null, new int[2])), new AIConfig());
    Move user = RandomAI.pickMoveComplex(mcts.getRoot().getGameState(), new ReferenceMove(null, new int[2])).toMove();
    Move ai = mcts.getMove(100);
    
    AnalyzedGameState ags = new AnalyzedGameState(mcts, user, ai, new GameState());
    ags.getAiChoice();
    System.out.println(ags.getMoveEvaluation());
  }
  
  @Test
  void percentageDifferenceTest() throws NoMovesLeftException, InvalidShapeException {
    MCTS mcts = new MCTS(new TreeNode(null, new ReferenceGameState(TestValues.getTestState()), new int[2], new ReferenceMove(null, new int[2])), new AIConfig());
    Move user = RandomAI.pickMoveComplex(mcts.getRoot().getGameState(), new ReferenceMove(null, new int[2])).toMove();
    Move ai = mcts.getMove(100);
    
    AnalyzedGameState ags = new AnalyzedGameState(mcts, user, ai, new GameState());
    ags.getAiChoice();
//    System.out.println(getPercentageDifference(ags));
  }

  
  
  /////////////////////////////////////////////
  // test methods to access private methods  //
  /////////////////////////////////////////////
  int getPercentageDifference(AnalyzedGameState ags) {    
    try {
    Method privateMethod = ags.getClass().getDeclaredMethod("getPercentageDifference");
    privateMethod.setAccessible(true);
      return (Integer)privateMethod.invoke(ags);
    } catch(Exception e) {e.printStackTrace();}
    return 0;
  }
}
