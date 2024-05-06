package org.ctf.shared.ai;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.contains;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Piece;
import org.ctf.shared.state.Team;
import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;
import org.openjdk.jol.vm.VM;

import org.ctf.shared.ai.AI_Controller;
import org.ctf.shared.ai.AI_Tools.InvalidShapeException;
import org.ctf.shared.ai.AI_Tools.NoMovesLeftException;
import org.ctf.shared.ai.mcts3.ReferenceGameState;
import org.ctf.shared.ai.random.RandomAI;

/**
 * This class is mainly used to test the memory usage of implemented Objects
 * @author sistumpf
 */
class ObjectMemoryAnalyzer {

//  @Test
  void testPrintOutCurrentMemoryUsage() {
//    TreeNode node = new TreeNode(null, TestValues.getTestState(), null);
//    System.out.println(ClassLayout.parseClass(HashMap.class).toPrintable());


//    System.out.println("The shallow size of TreeNode: " + VM.current().sizeOf(node));
//    System.out.println(ClassLayout.parseInstance(node.gameState.getGrid()).toPrintable());
//    System.out.println(GraphLayout.parseInstance(node).toFootprint());

//    MCTS mcts = new MCTS(node);
//    mcts.getMove(10000, AI_Constants.C);
    
//    System.out.println(GraphLayout.parseInstance(mcts).toFootprint());
  }
  
//  @Test
  void testTreeNodeOldMemory() {
    org.ctf.shared.ai.mcts.TreeNode oldNode = new org.ctf.shared.ai.mcts.TreeNode(null, TestValues.getTestState(), null, new ReferenceMove(null, new int[] {0,0}));
    System.out.println(GraphLayout.parseInstance(oldNode).toFootprint());


  }
  
  //@Test
  void testRandomAIspeed() throws NoMovesLeftException, InvalidShapeException, InterruptedException {
    GameState test = TestValues.getTestState();
    //warm up jit compiler
    for(int i=0; i<1000; i++) {
      RandomAI.pickMoveComplex(test, new ReferenceMove(null, new int[] {0,0}));
    }
    
    long timeigs = 0;
    int sims = 100000;
    for(int i=0; i<sims; i++) {
      long time = System.nanoTime();
      RandomAI.pickMoveComplex(test, new ReferenceMove(null, new int[] {0,0}));
      timeigs+=(System.nanoTime() - time);
    }
    System.out.println((timeigs/sims) + " nonos im durchschnitt");
    Thread.sleep(2000);
  }
  
  @Test
  void testGridMemoryUsage() {
    GameState gameState = TestValues.getTestState();
    ReferenceGameState rGameState = new ReferenceGameState(gameState);
    
    org.ctf.shared.ai.mcts.TreeNode node = new org.ctf.shared.ai.mcts.TreeNode(null, gameState, null, new ReferenceMove(null, new int[2]));
    org.ctf.shared.ai.mcts3.TreeNode node3 = new org.ctf.shared.ai.mcts3.TreeNode(null, new ReferenceGameState(gameState), null, new ReferenceMove(null, new int[2]));

//    System.out.println(GraphLayout.parseInstance(node).toFootprint());
//    System.out.println(GraphLayout.parseInstance(node3).toFootprint());
  }

}
