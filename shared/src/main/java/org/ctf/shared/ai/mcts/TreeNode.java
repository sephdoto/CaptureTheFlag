package org.ctf.shared.ai.mcts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import org.ctf.shared.ai.GameUtilities;
import org.ctf.shared.ai.ReferenceMove;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Piece;
import org.ctf.shared.state.Team;

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
  private GameState gameState;
  private int[] wins;
  private ReferenceMove operateOn;
  
  public TreeNode(TreeNode parent, GameState gameState, int[] wins, ReferenceMove operateOn) {
    this.setParent(parent);
    this.setGameState(gameState);
    this.setWins(wins != null ? wins : new int[gameState.getTeams().length]);
    this.setOperateOn(operateOn);
    initPossibleMovesAndChildren();
  }
  
  /**
   * possibleMoves (filled) and the children array (empty) get initialized,
   */
  public void initPossibleMovesAndChildren() {
    this.setPossibleMoves(new IdentityHashMap<Piece, ArrayList<int[]>>());
    int children = 0;
    for(Piece p : getGameState().getTeams()[getGameState().getCurrentTeam()].getPieces()) {
      ArrayList<int[]> movesPieceP = GameUtilities.getPossibleMoves(getGameState(), p, new ArrayList<int[]>(), operateOn);
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
     int team = GameUtilities.getPreviousTeam(getGameState());
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
    * GameState won't be copied here, giving a new GameState that gets inserted into the new node is required.
    * Using the copyGameState method to deep copy and alter the nodes GameState is recommended.
    * @return a copy of the current node
    */
   public TreeNode clone(GameState newState) {
     TreeNode treeNode = new TreeNode(this, newState, Arrays.copyOf(getWins(), getWins().length), operateOn);
     return treeNode;
   }

   /**
    * Used instead of a GameState.clone method, as GameState is not altered in this MCTS implementation.
    * @return a deep copy of this nodes gameState
    */
   //TODO sehr ineffizient, offen für Verbesserungsvorschläge
   public GameState copyGameState() {
     GameState newState = new GameState();
     newState.setCurrentTeam(getGameState().getCurrentTeam());
     newState.setLastMove(getGameState().getLastMove());
     Team[] teams = new Team[getGameState().getTeams().length];
     for(int i=0; i<teams.length; i++) {
       if(getGameState().getTeams()[i] == null)
         continue;
       teams[i] = new Team();
       teams[i].setBase(getGameState().getTeams()[i].getBase());
       teams[i].setFlags(getGameState().getTeams()[i].getFlags());
       teams[i].setId(getGameState().getTeams()[i].getId());
       Piece[] pieces = new Piece[getGameState().getTeams()[i].getPieces().length];
       for(int j=0; j<pieces.length; j++) {
         pieces[j] = new Piece();
         pieces[j].setDescription(getGameState().getTeams()[i].getPieces()[j].getDescription());
         pieces[j].setId(getGameState().getTeams()[i].getPieces()[j].getId() + "");
         pieces[j].setTeamId(getGameState().getTeams()[i].getPieces()[j].getTeamId() + "");
         pieces[j].setPosition(getGameState().getTeams()[i].getPieces()[j].getPosition().clone());
       }
       teams[i].setPieces(pieces);
     }        
     newState.setTeams(teams);
     String[][] grid = new String[getGameState().getGrid().length][];
     for(int i = 0; i < getGameState().getGrid().length; i++) {
       grid[i] = new String[getGameState().getGrid()[i].length];
       System.arraycopy(getGameState().getGrid()[i], 0, grid[i], 0, getGameState().getGrid()[i].length);
     }
     newState.setGrid(grid);
     
     return newState;
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
     for(String[] s : getGameState().getGrid()) {
       for(String ss : s) {
         System.out.print((ss == "" ? " . " : ss) + " ");
       }
       System.out.println();
     }
   }

  public TreeNode[] getChildren() {
    return children;
  }

  public void setChildren(TreeNode[] children) {
    this.children = children;
  }

  public TreeNode getParent() {
    return parent;
  }

  public void setParent(TreeNode parent) {
    this.parent = parent;
  }

  public IdentityHashMap<Piece, ArrayList<int[]>> getPossibleMoves() {
    return possibleMoves;
  }

  public void setPossibleMoves(IdentityHashMap<Piece, ArrayList<int[]>> possibleMoves) {
    this.possibleMoves = possibleMoves;
  }

  public GameState getGameState() {
    return gameState;
  }

  public void setGameState(GameState gameState) {
    this.gameState = gameState;
  }

  public int[] getWins() {
    return wins;
  }

  public void setWins(int[] wins) {
    this.wins = wins;
  }

  public void setOperateOn(ReferenceMove operateOn) {
    this.operateOn = operateOn;
  }
  
  public ReferenceMove getOperateOn() {
    return operateOn;
  }
}