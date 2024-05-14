package org.ctf.shared.ai;

import static org.junit.jupiter.api.Assertions.*;
import org.ctf.shared.ai.GameUtilities.InvalidShapeException;
import org.ctf.shared.ai.GameUtilities.NoMovesLeftException;
import org.ctf.shared.ai.mcts.MCTS;
import org.ctf.shared.ai.mcts.TreeNode;
import org.ctf.shared.constants.Enums.AI;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.junit.jupiter.api.Test;

class AIControllerTest {

  @Test
  void testInit() {
    new AIController(TestValues.getTestState(), AI.RANDOM, new AIConfig(), 0);
  }

  @Test
  void testGetMove() {
    for(AI ai : AI.values()) {
      AIController aic = new AIController(TestValues.getTestState(), ai, new AIConfig(), 0);
      try {
        aic.getNextMove();
      } catch (NoMovesLeftException | InvalidShapeException e) {
        fail("Fehler bei getMove");
      }
    }
  }
  
  @Test
  void testUpdate() throws NoMovesLeftException, InvalidShapeException {
    GameState gsOld = TestValues.getTestState();
    AIController aic = new AIController(gsOld, AI.RANDOM, new AIConfig(), 1);
    Move move = aic.getNextMove();
    MCTS mcts = new MCTS(new TreeNode(null, gsOld, new int[2], new ReferenceMove(null, new int[2])), new AIConfig());
    GameState gs = mcts.root.copyGameState();
    mcts.alterGameState(gs, new ReferenceMove(gs, move));
    
    aic.update(gs);
    
    assertTrue(gs == (aic.getNormalizedGameState().getOriginalGameState()));
    assertFalse(gs == gsOld);
  }
}
