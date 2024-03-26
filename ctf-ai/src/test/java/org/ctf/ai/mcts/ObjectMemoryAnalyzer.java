package org.ctf.ai.mcts;

import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import org.ctf.ai.AI_Constants;
import org.ctf.ai.TestValues;
import org.ctf.shared.state.GameState;
import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;
import org.openjdk.jol.vm.VM;

class ObjectMemoryAnalyzer {

  @Test
  void testPrintOutCurrentMemoryUsage() {
    TreeNode node = new TreeNode(null, TestValues.getTestState(), null);
//    System.out.println(VM.current().details());
    
//    System.out.println(ClassLayout.parseClass(TreeNode.class).toPrintable());
//    System.out.println(ClassLayout.parseClass(GameState.class).toPrintable());
//    System.out.println(ClassLayout.parseClass(HashMap.class).toPrintable());


//    System.out.println("The shallow size of TreeNode: " + VM.current().sizeOf(node));
//    System.out.println(ClassLayout.parseInstance(node.gameState.getGrid()).toPrintable());
    System.out.println(GraphLayout.parseInstance(node).toFootprint());

    MCTS mcts = new MCTS(node);
    mcts.getMove(10000, AI_Constants.C);
    
    System.out.println(GraphLayout.parseInstance(mcts).toFootprint());
  }

}
