package org.ctf.shared.ai.mcts3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import org.ctf.shared.ai.ReferenceMove;
import org.ctf.shared.state.Piece;

/**
 * This class represents a node in the MCTS Tree.
 * It contains the current gameState, wins, possibleMoves for the current team, its parent and the children.
 * The parent is the game one move prior, the children are the game one move further.
 * @author sistumpf
 */
public class TreeNode implements Comparable<TreeNode> {
  public TreeNode parent;
  TreeNode[] children;
  IdentityHashMap<Piece, ArrayList<int[]>> possibleMoves;
  ReferenceGameState gameState;
  int[] wins;
  ReferenceMove operateOn;
  
  public TreeNode(TreeNode parent, ReferenceGameState gameState, int[] wins, ReferenceMove operateOn) {
    this.parent = parent;
    this.gameState = gameState;
    this.operateOn = operateOn;
    this.wins = wins != null ? wins : new int[gameState.getTeams().length];
    initPossibleMovesAndChildren();
  }
  
  /**
   * possibleMoves (filled) and the children array (empty) get initialized,
   */
  public void initPossibleMovesAndChildren() {
    this.possibleMoves = new IdentityHashMap<Piece, ArrayList<int[]>>();
    int children = 0;
    for(Piece p : gameState.getTeams()[gameState.getCurrentTeam()].getPieces()) {
      ArrayList<int[]> movesPieceP = MCTS_Tools.getPossibleMoves(gameState, p, new ArrayList<int[]>(), operateOn);
      if(movesPieceP.size() > 0) {
        possibleMoves.put(p, movesPieceP);
        children += possibleMoves.get(p).size();
      }
    }

    this.children = new TreeNode[children];
  }

  /** 
   * @return total simulations played from this node
   */
   public int getNK() {
     return Arrays.stream(wins).sum();
   }

   /** 
    * Returns the average wins for the team which move lead to this node.
    * @return V value for UCT
    */
   public double getV() {
     int team = MCTS_Tools.getPreviousTeam(gameState);
     return wins[team] / (double)getNK();
   }

   /**
    * @return returns the UCT value of the current node
    */
   public double getUCT(double C) {
     return getV() + C * Math.sqrt((double)Math.log(parent.getNK()) / getNK());
   }

   /**
    * Copies the current TreeNode, rotates the player Attribute to the next player.
    * ReferenceGameState won't be copied here, giving a new ReferenceGameState that gets inserted into the new node is required.
    * Using the copyGameState method to deep copy and alter the nodes ReferenceGameState is recommended.
    * @return a copy of the current node
    */
   public TreeNode clone(ReferenceGameState newState) {
     TreeNode treeNode = new TreeNode(this, newState, Arrays.copyOf(wins, wins.length), operateOn);
     return treeNode;
   }

   /**
    * Compares two nodes with their V value.
    * @param node to compare to
    * @return as super.compareTo
    */
   @Override  
   public int compareTo(TreeNode node) {
     return Double.compare(node.getV(), getV());
   }

   /**
    * prints the node and its important attributes to the console
    * TODO more attributes need to be implemented
    */
   public void printMe(String s) {
     printGrid();
   }

   /**
    * prints the grid
    */
   public void printGrid() {
     for(int y=0; y<gameState.getGrid().getGrid().length; y++) {
       for(int x=0; x<gameState.getGrid().getGrid()[y].length; x++) {
         if(gameState.getGrid().getPosition(x, y) == null)
           System.out.print(" . ");
         else
         System.out.print(gameState.getGrid().getPosition(x, y));
       }
       System.out.println();
     }
   }
}