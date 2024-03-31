package org.ctf.ai.mcts2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
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
    this.possibleMoves = new IdentityHashMap<Piece, ArrayList<int[]>>();
    this.parent = parent;
    this.gameState = gameState;
    this.grid = grid;
    this.wins = wins != null ? wins : new int[gameState.getTeams().length];
    if(grid.getPieceVisions().keySet().size() == 0)
      initPossibleMovesAndChildren();
  }
  
  public TreeNode(TreeNode parent, GameState gameState, int[] wins) {
    this.possibleMoves = new IdentityHashMap<Piece, ArrayList<int[]>>();
    this.parent = parent;
    this.gameState = gameState;
    this.grid = new Grid(gameState);
    this.wins = wins != null ? wins : new int[gameState.getTeams().length];
    initPossibleMovesAndChildren();
  }
  
  public void initPossibleMovesAndChildren() {
    possibleMoves.clear();
    int children = 0;
    IdentityHashMap<Piece, ArrayList<int[]>> impossibleMoves = new IdentityHashMap<Piece, ArrayList<int[]>>();
    for(Piece p : gameState.getTeams()[gameState.getCurrentTeam()].getPieces()) {
      ArrayList<int[]> movesPieceP = new ArrayList<int[]>();
      impossibleMoves.put(p, MCTS_Tools.getPossibleMovesWithPieceVision(gameState, grid, p, movesPieceP));
      if(movesPieceP.size() > 0) {
        possibleMoves.put(p, movesPieceP);
        children += possibleMoves.get(p).size();
      }
    }  

    int start = gameState.getCurrentTeam();
    for(MCTS_Tools.toNextTeam(gameState); gameState.getCurrentTeam() != start; MCTS_Tools.toNextTeam(gameState)) {
      for(Piece p : gameState.getTeams()[gameState.getCurrentTeam()].getPieces()) {
        ArrayList<int[]> movesTeamI = new ArrayList<int[]>();
        impossibleMoves.put(p, MCTS_Tools.getPossibleMovesWithPieceVision(gameState, grid, p, movesTeamI));
        impossibleMoves.get(p).addAll(movesTeamI);
      }
    }
    
    this.grid = new Grid(grid.getGrid(), possibleMoves, impossibleMoves);
    this.children = new TreeNode[children];
  }
  
  /**
   * This Method only updates a certain amount of pieces from possibleMoves, making it more
   * efficient for simulating only one node till the end.
   * It should only be used for simulating.
   * @param pieces
   */
  public void updateGrids(HashSet<Piece> pieces) {
    for(Piece p : pieces) {
      removeFromGrids(p);
      ArrayList<int[]> impossibleMoves = getIMpossibleMoves(p);
      addToPieceVisions(p, impossibleMoves);
      this.grid.setPosition(new GridObjectContainer(p), p.getPosition()[1], p.getPosition()[0]);
    }
  }
  void addToPieceVisions(Piece p, ArrayList<int[]> positions){
    for(int[] pos : positions) {
      if(this.grid.getPieceVisionGrid()[pos[0]][pos[1]] == null)
        this.grid.getPieceVisionGrid()[pos[0]][pos[1]] = new GridPieceContainer();
      this.grid.getPieceVisionGrid()[pos[0]][pos[1]].getPieces().add(p);
    }
    this.grid.pieceVisions.put(p, positions);
  }
  void removeFromGrids(Piece piece) {
    for(int[] pos : this.grid.pieceVisions.get(piece)) {
      this.grid.getPieceVisionGrid()[pos[0]][pos[1]].getPieces().remove(piece);
    }
    this.grid.pieceVisions.remove(piece);
  }
  ArrayList<int[]> getIMpossibleMoves(Piece piece){
    ArrayList<int[]> impossibleMoves = new ArrayList<int[]>();
    if(grid.getPosition(piece.getPosition()[1], piece.getPosition()[0]).getPiece().equals(piece))
      impossibleMoves.addAll(MCTS_Tools.getPossibleMovesWithPieceVision(gameState, grid, piece, impossibleMoves));
    return impossibleMoves;
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
     printGrids();
   }

   /**
    * prints the grids
    */
   void printGrids() {
     System.out.println("\n");
     for(int i=0; i<grid.getGrid().length; i++) {
       for(int j=0; j<grid.getGrid()[0].length; j++) {
         if(grid.getPieceVisionGrid()[i][j] == null)
           System.out.print(". ");
         else
           System.out.print(grid.getPieceVisionGrid()[i][j].getPieces().size() == 0 ? ". " : grid.getPieceVisionGrid()[i][j].getPieces().size() + " ");
       }
       System.out.print("\t");
       for(int j=0; j<grid.getGrid()[0].length; j++) {
         if(grid.getGrid()[i][j] == null)
           System.out.print(". ");
         else
           System.out.print(grid.getGrid()[i][j].getObject().ordinal() + " ");
       }
       System.out.print("\t");
       for(int j=0; j<grid.getGrid()[0].length; j++) {
         if(grid.getGrid()[i][j] == null)
           System.out.print(". ");
         else
           System.out.print(grid.getGrid()[i][j].toString() + " ");
       }
      System.out.println(); 
     }
     
   }
}