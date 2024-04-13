package org.ctf.shared.ai;

//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.contains;
import java.util.HashMap;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Piece;
import org.ctf.shared.state.Team;
//import org.junit.jupiter.api.Test;
//import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;
//import org.openjdk.jol.vm.VM;

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
  void testGridMemoryUsage() {
    GameState gameState = TestValues.getTestState();
    HashMap<int[], Piece> piecePos = new  HashMap<int[], Piece>();
    for(int i=0; i<gameState.getGrid().length; i++)
      for(int j=0; j<gameState.getGrid()[i].length; j++)
        if(gameState.getGrid()[i][j].contains("p:"))
          for(Team t:gameState.getTeams())
            for(Piece p: t.getPieces())
              if(p.getId().equals(gameState.getGrid()[i][j]))
                  piecePos.put(new int[] {i,j}, p);

    System.out.println(GraphLayout.parseInstance(gameState).toFootprint());
    System.out.println(GraphLayout.parseInstance(piecePos).toFootprint());
  }

}
