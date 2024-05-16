package org.ctf.shared.ai.mcts3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import org.ctf.shared.ai.MonteCarloTreeNode;
import org.ctf.shared.ai.ReferenceMove;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Piece;

/**
 * This class represents a node in the MCTS Tree.
 * It contains the current gameState, wins, possibleMoves for the current team, its parent and the children.
 * The parent is the game one move prior, the children are the game one move further.
 * @author sistumpf
 */
public class TreeNode implements MonteCarloTreeNode, Comparable<TreeNode> {
  private TreeNode parent;
  private TreeNode[] children;
  private IdentityHashMap<Piece, ArrayList<int[]>> possibleMoves;
  private ReferenceGameState gameState;
  private int[] wins;
  private ReferenceMove operateOn;
  
  public TreeNode(TreeNode parent, ReferenceGameState gameState, int[] wins, ReferenceMove operateOn) {
    this.setParent(parent);
    this.setReferenceGameState(gameState);
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
    for(Piece p : getReferenceGameState().getTeams()[getReferenceGameState().getCurrentTeam()].getPieces()) {
      ArrayList<int[]> movesPieceP = MCTSUtilities.getPossibleMoves(getReferenceGameState(), p, new ArrayList<int[]>(), getOperateOn());
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
     int team = MCTSUtilities.getPreviousTeam(getReferenceGameState());
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
    * Deep clones this TreeNode without parent, but with deep copies of all its children.
    * 
    * @return a deep copy of this node and its children
    */
   public TreeNode deepCloneWithChildren() {
     TreeNode node = deepClone();
     for(int i=0; i<getChildren().length; i++)
       node.children[i] = children[i].deepClone();
     return node;
   }
   
   /**
    * Deep clones this TreeNode, the returned node got neither a parent, nor this nodes children.
    * The children get initialized but don't have any value, as they just got created.
    * 
    * @return a deep copy of the node
    */
   public TreeNode deepClone() {
     return new TreeNode(null, gameState.clone(), Arrays.copyOf(getWins(), getWins().length), operateOn);
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
     for(int y=0; y<getReferenceGameState().getGrid().getGrid().length; y++) {
       for(int x=0; x<getReferenceGameState().getGrid().getGrid()[y].length; x++) {
         if(getReferenceGameState().getGrid().getPosition(x, y) == null)
           System.out.print(" . ");
         else
         System.out.print(getReferenceGameState().getGrid().getPosition(x, y));
       }
       System.out.println();
     }
   }

  public TreeNode getParent() {
    return parent;
  }

  public void setParent(MonteCarloTreeNode parent) {
    this.parent = (TreeNode)parent;
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

  public ReferenceGameState getReferenceGameState() {
    return gameState;
  }

  public void setReferenceGameState(ReferenceGameState gameState) {
    this.gameState = gameState;
  }
  
  public GameState getGameState() {
    return this.gameState.toGameState();
  }
}