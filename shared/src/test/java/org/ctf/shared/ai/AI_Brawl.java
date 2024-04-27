package org.ctf.shared.ai;

import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.junit.jupiter.api.Test;

/**
 * This class is for AI battles. One AI against another AI, may the best one win!
 *
 * @author sistumpf
 */
public class AI_Brawl {

  /*@BeforeEach
  void setup() {

  }*/

//  @Test
  void mctsVSmcts2() {
    GameState playOn = TestValues.getTestState();
    playOn.setCurrentTeam(0);
    int milisForMove = 5000;
    int roundCounter = 0;

    while (true) {
      org.ctf.shared.ai.mcts.TreeNode root =
          new org.ctf.shared.ai.mcts.TreeNode(null, playOn, null, new ReferenceMove(null, new int[] {0,0}));
      root.printGrid();

      org.ctf.shared.ai.mcts.MCTS mcts = new org.ctf.shared.ai.mcts.MCTS(root);
      Move move = new Move();

      move = mcts.getMove(milisForMove, AI_Constants.C);

      System.out.println("\nMCTS Round " + ++roundCounter + ":\n" + mcts.printResults(move));
      mcts.alterGameState(playOn, new ReferenceMove(playOn, move));
      mcts.removeTeamCheck(playOn);

      if (mcts.isTerminal(playOn, new ReferenceMove(null, new int[] {0,0})) != -1) break;
      else System.out.println("ISTERMINAL??? " + mcts.isTerminal(playOn, new ReferenceMove(null, new int[] {0,0})));
      org.ctf.shared.ai.mcts2.TreeNode root2 =
          new org.ctf.shared.ai.mcts2.TreeNode(null, playOn, null);
      root2.printGrids();
      org.ctf.shared.ai.mcts2.MCTS mcts2 = new org.ctf.shared.ai.mcts2.MCTS(root2);
      move = mcts2.getMove(milisForMove, AI_Constants.C);
      System.out.println(
          "\nMCTS_TWOOOOO Round " + ++roundCounter + ":\n" + mcts2.printResults(move));
      mcts.alterGameState(playOn, new ReferenceMove(playOn, move));
      mcts.removeTeamCheck(playOn);

      if (mcts.isTerminal(playOn, new ReferenceMove(null, new int[] {0,0})) != -1) break;
      else System.out.println("ISTERMINAL??? " + mcts.isTerminal(playOn, new ReferenceMove(null, new int[] {0,0})));
    }

    //    org.ctf.ai.mcts2.TreeNode root2 = new org.ctf.ai.mcts2.TreeNode(null, playOn, null);
    //    root2.printGrids();
  }
}
