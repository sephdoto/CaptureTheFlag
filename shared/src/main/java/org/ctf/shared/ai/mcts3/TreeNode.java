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
  private TreeNode parent;
  private TreeNode[] children;
  private IdentityHashMap<Piece, ArrayList<int[]>> possibleMoves;
  private ReferenceGameState gameState;
  private int[] wins;
  private ReferenceMove operateOn;
  
  public TreeNode(TreeNode parent, ReferenceGameState gameState, int[] wins, ReferenceMove operateOn) {
    this.setParent(parent);
    this.setGameState(gameState);
    this.setOperateOn(operateOn);
    this.setWins(wins != null ? wins : new int[gameState.getTeams().length]);
    initPossibleMovesAndChildren();
  }
  
  /**
   * possibleMoves (filled) and the children array (empty) get initialized,
   */
  public void initPossibleMovesAndChildren() {
    this.setPossibleMoves(new IdentityHashMap<Piece, ArrayList<int[]>>());
    int children = 0;
    for(Piece p : getGameState().getTeams()[getGameState().getCurrentTeam()].getPieces()) {
      ArrayList<int[]> movesPieceP = MCTSUtilities.getPossibleMoves(getGameState(), p, new ArrayList<int[]>(), getOperateOn());
      if(movesPieceP.size() > 0) {
        getPossibleMoves().put(p, movesPieceP);
        children += getPossibleMoves().get(p).size();
      }
    }

    this.setChildren(new TreeNode[children]);
  }

  /** 
   * @return total simulations played from this node
   */
   public int getNK() {
     return Arrays.stream(getWins()).sum();
   }

   /** 
    * Returns the average wins for the team which move lead to this node.
    * @return V value for UCT
    */
   public double getV() {
     int team = MCTSUtilities.getPreviousTeam(getGameState());
     return getWins()[team] / (double)getNK();
   }

   /**
    * @return returns the UCT value of the current node
    */
   public double getUCT(double C) {
     return getV() + C * Math.sqrt((double)Math.log(getParent().getNK()) / getNK());
   }

   /**
    * Copies the current TreeNode, rotates the player Attribute to the next player.
    * ReferenceGameState won't be copied here, giving a new ReferenceGameState that gets inserted into the new node is required.
    * Using the copyGameState method to deep copy and alter the nodes ReferenceGameState is recommended.
    * @return a copy of the current node
    */
   public TreeNode clone(ReferenceGameState newState) {
     TreeNode treeNode = new TreeNode(this, newState, Arrays.copyOf(getWins(), getWins().length), getOperateOn());
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
     for(int y=0; y<getGameState().getGrid().getGrid().length; y++) {
       for(int x=0; x<getGameState().getGrid().getGrid()[y].length; x++) {
         if(getGameState().getGrid().getPosition(x, y) == null)
           System.out.print(" . ");
         else
         System.out.print(getGameState().getGrid().getPosition(x, y));
       }
       System.out.println();
     }
   }

  public TreeNode getParent() {
    return parent;
  }

  public void setParent(TreeNode parent) {
    this.parent = parent;
  }

  public TreeNode[] getChildren() {
    return children;
  }

  public void setChildren(TreeNode[] children) {
    this.children = children;
  }

  public IdentityHashMap<Piece, ArrayList<int[]>> getPossibleMoves() {
    return possibleMoves;
  }

  public void setPossibleMoves(IdentityHashMap<Piece, ArrayList<int[]>> possibleMoves) {
    this.possibleMoves = possibleMoves;
  }

  public ReferenceGameState getGameState() {
    return gameState;
  }

  public void setGameState(ReferenceGameState gameState) {
    this.gameState = gameState;
  }

  public int[] getWins() {
    return wins;
  }

  public void setWins(int[] wins) {
    this.wins = wins;
  }

  public ReferenceMove getOperateOn() {
    return operateOn;
  }

  public void setOperateOn(ReferenceMove operateOn) {
    this.operateOn = operateOn;
  }
}