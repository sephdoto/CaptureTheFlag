package org.ctf.shared.ai;

import org.ctf.shared.ai.mcts.MCTS;
import org.ctf.shared.ai.mcts.TreeNode;

/**
 * Ich brauch die Klasse hier nur temporär für den Profiler.
 * Er funktioniert leider nicht wirklich bei JUnit Test Klassen.
 * @author sistumpf
 */
public class Profiling {
  public static void main (String[] args) {
    double expansions = 0;
    int count = 0;
    int timeInMilis = 1000;
    int simulations = 0;
    int heuristics = 0;
    int crashes = 0;

    MCTS mcts = new MCTS(new TreeNode(null, TestValues.getTestState(), new int[] {0, 0}, new ReferenceMove(null, new int[] {0,0})));
    TreeNode rootclone = mcts.root.clone(mcts.root.copyGameState());
    
    for (; count < 50; count++) {

      //      MCTS_TestDouble mcts = new
      // MCTS_TestDouble(MCTSTest.mcts.root.clone(MCTSTest.mcts.root.copyGameState()));
      mcts.root = rootclone.clone(rootclone.copyGameState());
      mcts.root.parent = null;
      try {
        mcts.getMove(timeInMilis, AI_Constants.C);
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
