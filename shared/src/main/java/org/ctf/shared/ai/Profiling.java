package org.ctf.shared.ai;

import org.ctf.shared.ai.mcts3.MCTS;
import org.ctf.shared.ai.mcts3.ReferenceGameState;
import org.ctf.shared.ai.mcts3.TreeNode;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Piece;

/**
 * Ich brauch die Klasse hier nur temporär für den Profiler.
 * Er funktioniert leider nicht wirklich bei JUnit Test Klassen.
 * @author sistumpf
 */
public class Profiling {
  public static void main (String[] args) {
    /*double expansions = 0;
    int count = 0;
    int timeInMilis = 10;
    int simulations = 0;
    int heuristics = 0;
    int crashes = 0;

    MCTS mcts = new MCTS(new TreeNode(null, new ReferenceGameState(TestValues.getTestState()), new int[] {0, 0}, new ReferenceMove(null, new int[] {0,0})), new AIConfig());
    TreeNode rootclone = mcts.getRoot().clone(mcts.getRoot().getReferenceGameState().clone());
    
    for (; count < 1; count++) {

      //      MCTS_TestDouble mcts = new
      // MCTS_TestDouble(MCTSTest.mcts.root.clone(MCTSTest.mcts.root.copyGameState()));
      mcts.setRoot(rootclone.clone(rootclone.getReferenceGameState().clone()));
      mcts.getRoot().setParent(null);
      try {
        mcts.getMove(timeInMilis);
      } catch (NullPointerException npe) {
        npe.printStackTrace();
        crashes++;
        break;
      }
    }
    simulations = mcts.simulationCounter.get() / count;
    heuristics = mcts.heuristicCounter.get() / count;
    expansions = ((Math.round(((double) mcts.getExpansionCounter().get() / count) * 1000)) / 1000.);

    System.out.println(
        count
            + " simulations with "
            + timeInMilis
            + " ms, average expansions/run: "
            + expansions
            + ",\nsimulations till the end: "
            + simulations
            + ", heuristic used: "
            + heuristics
            + "\nResults computed with "
            + crashes
            + " crashes");*/
    
      GameState gameState = TestValues.getEmptyTestState();
      gameState.getTeams()[0].setBase(new int[] {0,0});
      gameState.getTeams()[0].setFlags(1);
      gameState.getTeams()
      [0].setId("0");
      Piece[] pieces0 = new Piece[1];
      pieces0[0] = new Piece();
      pieces0[0].setDescription(TestValues.getTestTemplate().getPieces()[2]);
      pieces0[0].setId("p:0_1");
      pieces0[0].setPosition(new int[] {7,9});
      pieces0[0].setTeamId("0");
      gameState.getTeams()[0].setPieces(pieces0);

      gameState.getTeams()[1].setBase(new int[] {9,9});
      gameState.getTeams()[1].setFlags(1);
      gameState.getTeams()[1].setId("1");
      Piece[] pieces1 = new Piece[1];
      pieces1[0] = new Piece();
      pieces1[0].setDescription(TestValues.getTestTemplate().getPieces()[2]);
      pieces1[0].setId("p:1_1");
      pieces1[0].setPosition(new int[] {2,0});
      pieces1[0].setTeamId("1");
      gameState.getTeams()[1].setPieces(pieces1);
      gameState.getGrid()[7][9] = pieces0[0].getId();
      gameState.getGrid()[2][0] = pieces1[0].getId();

      TreeNode parent = new TreeNode(null, new ReferenceGameState(gameState), new int[] {0,0}, new ReferenceMove(null, new int[2]));
      MCTS mcts = new MCTS(parent, new AIConfig());

      mcts.getMove(100);
    
  }
}
