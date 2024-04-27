package org.ctf.shared.ai.mcts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import org.ctf.shared.ai.AI_Tools;
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
  public TreeNode parent;
  TreeNode[] children;
  IdentityHashMap<Piece, ArrayList<int[]>> possibleMoves;
  GameState gameState;
  int[] wins;
  ReferenceMove operateOn;
  
  public TreeNode(TreeNode parent, GameState gameState, int[] wins, ReferenceMove operateOn) {
    this.parent = parent;
    this.gameState = gameState;
    this.wins = wins != null ? wins : new int[gameState.getTeams().length];
    this.operateOn = operateOn;
    initPossibleMovesAndChildren();
  }
  
  /**
   * possibleMoves (filled) and the children array (empty) get initialized,
   */
  public void initPossibleMovesAndChildren() {
    this.possibleMoves = new IdentityHashMap<Piece, ArrayList<int[]>>();
    int children = 0;
    for(Piece p : gameState.getTeams()[gameState.getCurrentTeam()].getPieces()) {
      ArrayList<int[]> movesPieceP = AI_Tools.getPossibleMoves(gameState, p, new ArrayList<int[]>(), operateOn);
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
     int team = AI_Tools.getPreviousTeam(gameState);
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
    * GameState won't be copied here, giving a new GameState that gets inserted into the new node is required.
    * Using the copyGameState method to deep copy and alter the nodes GameState is recommended.
    * @return a copy of the current node
    */
   public TreeNode clone(GameState newState) {
     TreeNode treeNode = new TreeNode(this, newState, Arrays.copyOf(wins, wins.length), operateOn);
     return treeNode;
   }

   /**
    * Used instead of a GameState.clone method, as GameState is not altered in this MCTS implementation.
    * @return a deep copy of this nodes gameState
    */
   //TODO sehr ineffizient, offen für Verbesserungsvorschläge
   public GameState copyGameState() {
     GameState newState = new GameState();
     newState.setCurrentTeam(gameState.getCurrentTeam());
     newState.setLastMove(gameState.getLastMove());
     Team[] teams = new Team[gameState.getTeams().length];
     for(int i=0; i<teams.length; i++) {
       if(gameState.getTeams()[i] == null)
         continue;
       teams[i] = new Team();
       teams[i].setBase(gameState.getTeams()[i].getBase());
       teams[i].setFlags(gameState.getTeams()[i].getFlags());
       teams[i].setId(gameState.getTeams()[i].getId());
       Piece[] pieces = new Piece[gameState.getTeams()[i].getPieces().length];
       for(int j=0; j<pieces.length; j++) {
         pieces[j] = new Piece();
         pieces[j].setDescription(gameState.getTeams()[i].getPieces()[j].getDescription());
         pieces[j].setId(gameState.getTeams()[i].getPieces()[j].getId() + "");
         pieces[j].setTeamId(gameState.getTeams()[i].getPieces()[j].getTeamId() + "");
         pieces[j].setPosition(gameState.getTeams()[i].getPieces()[j].getPosition().clone());
       }
       teams[i].setPieces(pieces);
     }        
     newState.setTeams(teams);
     String[][] grid = new String[gameState.getGrid().length][];
     for(int i = 0; i < gameState.getGrid().length; i++) {
       grid[i] = new String[gameState.getGrid()[i].length];
       System.arraycopy(gameState.getGrid()[i], 0, grid[i], 0, gameState.getGrid()[i].length);
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
     for(String[] s : gameState.getGrid()) {
       for(String ss : s) {
         System.out.print((ss == "" ? " . " : ss) + " ");
       }
       System.out.println();
     }
   }
}