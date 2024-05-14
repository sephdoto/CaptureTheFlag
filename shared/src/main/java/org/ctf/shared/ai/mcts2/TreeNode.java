package org.ctf.shared.ai.mcts2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.IdentityHashMap;
import org.ctf.shared.state.GameState;
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

  /**
   * Creates a new TreeNode with given Parameters.
   * This should be the default way to generate a TreeNode.
   * @param parent
   * @param gameState
   * @param grid : the nodes grid containing the current piece positions. pieceVisions and pieceVisionGrid are getting generated from it
   * @param wins
   */
  public TreeNode(TreeNode parent, ReferenceGameState gameState, Grid grid, int[] wins) {
    this.setPossibleMoves(new IdentityHashMap<Piece, ArrayList<int[]>>());
    this.setParent(parent);
    this.setGameState(gameState);

    IdentityHashMap<Piece, ArrayList<int[]>> impossibleMoves = new IdentityHashMap<Piece, ArrayList<int[]>>();
    int start = gameState.getCurrentTeam();
    for(MCTSUtilities.toNextTeam(gameState); gameState.getCurrentTeam() != start; MCTSUtilities.toNextTeam(gameState)) {
      for(Piece p : gameState.getTeams()[gameState.getCurrentTeam()].getPieces()) {
        ArrayList<int[]> movesTeamI = new ArrayList<int[]>();
        impossibleMoves.put(p, MCTSUtilities.getPossibleMovesWithPieceVision(gameState, p, movesTeamI));
        impossibleMoves.get(p).addAll(movesTeamI);
      }
    }
    this.getGameState().setGrid(new Grid(grid.grid, impossibleMoves));

    this.setWins(wins != null ? wins : new int[gameState.getTeams().length]);
    if(grid.getPieceVisions().keySet().size() == 0)
      initPossibleMovesAndChildren();
  }

  /**
   * Initializes a TreeNode from a ReferenceGameState.
   * @param parent
   * @param gameState
   * @param wins
   */
  public TreeNode(TreeNode parent, ReferenceGameState gameState, int[] wins) {
    this.setPossibleMoves(new IdentityHashMap<Piece, ArrayList<int[]>>());
    this.setParent(parent);
    this.setGameState(gameState);
    this.getGameState().setGrid(gameState.getGrid());
    this.setWins(wins != null ? wins : new int[gameState.getTeams().length]);
    initPossibleMovesAndChildren();
  }

  /**
   * Initializes a TreeNode from a GameState, should only be used once per MCTS for the root node.
   * @param parent
   * @param gameState
   * @param wins
   */
  public TreeNode(TreeNode parent, GameState gameState, int[] wins) {
    this.setPossibleMoves(new IdentityHashMap<Piece, ArrayList<int[]>>());
    this.setParent(parent);
    this.setGameState(new ReferenceGameState(gameState));
    this.setWins(wins != null ? wins : new int[gameState.getTeams().length]);
    initPossibleMovesAndChildren();
  }

  /**
   * possibleMoves (filled) and the children array (empty) get initialized,
   * also initializes the whole grid, with pieceVisions and pieceVisionGrid.
   */
  public void initPossibleMovesAndChildren() {
    getPossibleMoves().clear();
    int children = 0;
    IdentityHashMap<Piece, ArrayList<int[]>> impossibleMoves = new IdentityHashMap<Piece, ArrayList<int[]>>();
    for(Piece p : getGameState().getTeams()[getGameState().getCurrentTeam()].getPieces()) {
      ArrayList<int[]> movesPieceP = new ArrayList<int[]>();
      impossibleMoves.put(p, MCTSUtilities.getPossibleMovesWithPieceVision(getGameState(), p, movesPieceP));
      if(movesPieceP.size() > 0) {
        getPossibleMoves().put(p, movesPieceP);
        children += getPossibleMoves().get(p).size();
      }
    }  

    int start = getGameState().getCurrentTeam();
    for(MCTSUtilities.toNextTeam(getGameState()); getGameState().getCurrentTeam() != start; MCTSUtilities.toNextTeam(getGameState())) {
      for(Piece p : getGameState().getTeams()[getGameState().getCurrentTeam()].getPieces()) {
        //        System.out.print("\n" + p.getId() + " on " + p.getPosition()[0] + "-" + p.getPosition()[1] + ": "); TODO
        ArrayList<int[]> movesTeamI = new ArrayList<int[]>();
        impossibleMoves.put(p, MCTSUtilities.getPossibleMovesWithPieceVision(getGameState(), p, movesTeamI));
        impossibleMoves.get(p).addAll(movesTeamI);
        //        impossibleMoves.get(p).forEach(s -> System.out.print(s[0] + "-" + s[1] + ", "));
      }
    }
    //    System.out.println();
    this.getGameState().setGrid(new Grid(getGameState().getGrid().getGrid(), getPossibleMoves(), impossibleMoves));
    this.setChildren(new TreeNode[children]);
  }

  /**
   * This Method only updates a certain amount of pieces from possibleMoves, making it more
   * efficient for simulating only one node till the end.
   * It should only be used with simulating.
   * @param pieces
   */
  public void updateGrids(HashSet<Piece> pieces) {
    for(Piece p : pieces) {
      removeFromGrids(p);
      addToPieceVisions(p, getIMpossibleMoves(p));
    }
  }
  void addToPieceVisions(Piece p, ArrayList<int[]> positions){
    for(int[] pos : positions) {
      if(this.getGameState().getGrid().getPieceVisionGrid()[pos[0]][pos[1]] == null)
        this.getGameState().getGrid().getPieceVisionGrid()[pos[0]][pos[1]] = new GridPieceContainer();
      this.getGameState().getGrid().getPieceVisionGrid()[pos[0]][pos[1]].getPieces().add(p);
    }
    if(positions.size() > 0)
      this.getGameState().getGrid().pieceVisions.put(p, positions);
  }
  void removeFromGrids(Piece piece) {
    for(int[] pos : this.getGameState().getGrid().pieceVisions.get(piece)) {
      this.getGameState().getGrid().getPieceVisionGrid()[pos[0]][pos[1]].getPieces().remove(piece);
    }
    this.getGameState().getGrid().pieceVisions.remove(piece);
  }
  ArrayList<int[]> getIMpossibleMoves(Piece piece){
    ArrayList<int[]> impossibleMoves = new ArrayList<int[]>();
    if(this.getGameState().getGrid().getPosition(piece.getPosition()[1], piece.getPosition()[0]).getPiece().getId().equals(piece.getId()))
      impossibleMoves.addAll(MCTSUtilities.getPossibleMovesWithPieceVision(getGameState(), piece, impossibleMoves));
    return impossibleMoves;
  }

  /** 
   * @return total simulations played from this node
   */
  public int getNK() {
    return Arrays.stream(getWins()).sum();
  }

  /** 
   * Returns the average wins for the team which's move lead to this node.
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
   * Copies the current TreeNode, requires the ReferenceGameState contained in the node.
   * The gameState won't be copied here, giving a new GameState that gets inserted into the new node is required.
   * Using the ReferenceGameState.clone() method to deep copy and alter the ReferenceGameState is recommended.
   * @param ReferenceGameState
   * @return a copy of the current node
   */
  public TreeNode clone(ReferenceGameState newState) {
    TreeNode treeNode = new TreeNode(this, newState, Arrays.copyOf(getWins(), getWins().length));
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
   * TODO print more important attributes.
   */
  public void printMe(String s) {
    printGrids();
  }

  /**
   * prints the grids
   */
  public void printGrids() {
    for(int i=0; i<getGameState().getGrid().getGrid().length; i++) {
      for(int j=0; j<getGameState().getGrid().getGrid()[0].length; j++) {
        if(getGameState().getGrid().getGrid()[i][j] != null && getGameState().getGrid().getGrid()[i][j].getPiece() != null)
          System.out.print("x ");
        else if(getGameState().getGrid().getPieceVisionGrid()[i][j] == null)
          System.out.print(". ");
        else
          System.out.print(getGameState().getGrid().getPieceVisionGrid()[i][j].getPieces().size() == 0 ? ". " : getGameState().getGrid().getPieceVisionGrid()[i][j].getPieces().size() + " ");
      }
      System.out.print("\t");
      for(int j=0; j<getGameState().getGrid().getGrid()[0].length; j++) {
        if(getGameState().getGrid().getGrid()[i][j] == null)
          System.out.print(". ");
        else
          System.out.print(getGameState().getGrid().getGrid()[i][j].getObject().ordinal() + " ");
      }
      System.out.print("\t");
      for(int j=0; j<getGameState().getGrid().getGrid()[0].length; j++) {
        if(getGameState().getGrid().getGrid()[i][j] == null)
          System.out.print(". ");
        else
          System.out.print(getGameState().getGrid().getGrid()[i][j].toString() + " ");
      }
      System.out.println(); 
    }
    System.out.println("\n");
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
}