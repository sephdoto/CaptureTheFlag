package org.ctf.ai.mcts2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Piece;
import org.ctf.shared.state.Team;

public class TreeNode implements Comparable<TreeNode> {
  TreeNode parent;
  TreeNode[] children;
  IdentityHashMap<Piece, ArrayList<int[]>> possibleMoves;
  GameState gameState;
  Grid grid;
  int[] wins;

  public TreeNode(TreeNode parent, GameState gameState, Grid grid, int[] wins) {
    this.parent = parent;
    this.gameState = gameState;
    this.grid = grid;
    this.wins = wins != null ? wins : new int[gameState.getTeams().length];
    initPossibleMovesAndChildren();
  }
  
  public TreeNode(TreeNode parent, GameState gameState, int[] wins) {
    this.parent = parent;
    this.gameState = gameState;
    this.grid = new Grid(gameState);
    this.wins = wins != null ? wins : new int[gameState.getTeams().length];
    initPossibleMovesAndChildren();
  }
  
  public void initPossibleMovesAndChildren() {
    this.possibleMoves = new IdentityHashMap<Piece, ArrayList<int[]>>();
    int children = 0;
    for(Piece p : gameState.getTeams()[gameState.getCurrentTeam()].getPieces()) {
      ArrayList<int[]> movesPieceP = MCTS_Tools.getPossibleMoves(gameState, grid, p, new ArrayList<int[]>());
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
    * 
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
    * GameState won't be copied here, giving a new GameState that gets inserted into the new node is required.
    * Using the copyGameState method to deep copy and alter the nodes GameState is recommended.
    * @return a copy of the current node
    */
   public TreeNode clone(GameState newState) {
     TreeNode treeNode = new TreeNode(this, newState, grid.clone(), Arrays.copyOf(wins, wins.length));
     return treeNode;
   }

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
         pieces[j].setId(gameState.getTeams()[i].getPieces()[j].getId());
         pieces[j].setTeamId(gameState.getTeams()[i].getPieces()[j].getTeamId());
         pieces[j].setPosition(new int[] {gameState.getTeams()[i].getPieces()[j].getPosition()[0],gameState.getTeams()[i].getPieces()[j].getPosition()[1]});
       }
       teams[i].setPieces(pieces);
     }        
     newState.setTeams(teams);
     return newState;
   }

   @Override
   public int compareTo(TreeNode node) {
     return Double.compare(node.getV(), getV());
   }

   /**
    * prints the node and its important attributes to the console
    * TODO needs to be implemented
    */
   public void printMe(String s) {
     printGrid();
   }

   /**
    * prints the grid
    */
   public void printGrid() {
     for(GridObjectContainer[] s : this.grid.getGrid()) {
       for(GridObjectContainer ss : s) {
         System.out.print((ss == null ? "x" : ss.toString()) + " ");
       }
       System.out.println();
     }
   }
}