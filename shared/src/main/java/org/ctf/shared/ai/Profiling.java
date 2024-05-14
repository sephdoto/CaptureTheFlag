package org.ctf.shared.ai;

import org.ctf.shared.ai.mcts3.MCTS;
import org.ctf.shared.ai.mcts3.ReferenceGameState;
import org.ctf.shared.ai.mcts3.TreeNode;

/**
 * Ich brauch die Klasse hier nur temporär für den Profiler.
 * Er funktioniert leider nicht wirklich bei JUnit Test Klassen.
 * @author sistumpf
 */
public class Profiling {
  public static void main (String[] args) {
    double expansions = 0;
    int count = 0;
    int timeInMilis = 1;
    int simulations = 0;
    int heuristics = 0;
    int crashes = 0;

    MCTS mcts = new MCTS(new TreeNode(null, new ReferenceGameState(TestValues.getTestState()), new int[] {0, 0}, new ReferenceMove(null, new int[] {0,0})), new AIConfig());
    TreeNode rootclone = mcts.getRoot().clone(mcts.getRoot().getReferenceGameState().clone());
    
    for (; count < 10; count++) {

      //      MCTS_TestDouble mcts = new
      // MCTS_TestDouble(MCTSTest.mcts.root.clone(MCTSTest.mcts.root.copyGameState()));
      mcts.setRoot(rootclone.clone(rootclone.getReferenceGameState().clone()));
      mcts.getRoot().setParent(null);
      try {
        mcts.getMove(timeInMilis, new AIConfig().C);
      } catch (NullPointerException npe) {
        crashes++;
      }
    }
    simulations = mcts.simulationCounter.get() / count;
    heuristics = mcts.heuristicCounter.get() / count;
    expansions = ((Math.round(((double) mcts.expansionCounter.get() / count) * 1000)) / 1000.);

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
            + " crashes");
  }
}
