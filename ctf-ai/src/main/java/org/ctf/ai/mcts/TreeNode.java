package org.ctf.ai.mcts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.ctf.shared.ai.AI_Tools;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Piece;
import org.ctf.shared.state.Team;

public class TreeNode {
    TreeNode parent;
    TreeNode[] children;
    HashMap<String, ArrayList<int[]>> possibleMoves;
    GameState gameState;
    int[] wins;
    
    public TreeNode(TreeNode parent, GameState gameState, int[] wins) {
        this.parent = parent;
        this.possibleMoves = new HashMap<String, ArrayList<int[]>>();
        int children = 0;
        for(Piece p : gameState.getTeams()[gameState.getCurrentTeam()].getPieces()) {
          possibleMoves.put(p.getId(), AI_Tools.getPossibleMoves(gameState, p.getId()));
          children += possibleMoves.get(p.getId()).size();
        }
        this.children = new TreeNode[children];
        this.gameState = gameState;
        this.wins = new int[gameState.getTeams().length];
    }

    /** 
     * @return total simulations played from this node
     */
    public int getNK() {
        return Arrays.stream(wins).sum();
    }

    /** 
     * returns V value for UCT depending on the player,
     * due to the creation of children the player is stored as a boolean attribute of this class.
     * @return V value for UCT
     */
    public float getV() {
        return wins[gameState.getCurrentTeam()] / getNK();
    }
    
    /**
     * @return returns the UCT value of the current node
     */
    public float getUCT(float C) {
        return getV() + C * (float)Math.sqrt((float)Math.log(parent.getNK()) / getNK());
    }
    
    /**
     * Copies the current TreeNode, rotates the player Attribute to the next player.
     * GameState won't be copied here, giving a new GameState that gets inserted into the new node is required.
     * Using the copyGameState method to deep copy and alter the nodes GameState is recommended.
     * @return a copy of the current node
     */
    public TreeNode clone(GameState newState) {
      newState.setCurrentTeam((gameState.getCurrentTeam() +1) % gameState.getTeams().length);
      TreeNode treeNode = new TreeNode(this, newState, Arrays.copyOf(wins, wins.length));
      return treeNode;
    }
    
    //TODO sehr ineffizient, offen für Verbesserungsvorschläge
    public GameState copyGameState() {
    	GameState newState = new GameState();
    	newState.setCurrentTeam(gameState.getCurrentTeam());
    	newState.setLastMove(gameState.getLastMove());
    	Team[] teams = new Team[gameState.getTeams().length];
    	for(int i=0; i<teams.length; i++) {
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
    	String[][] newGrid = new String[gameState.getGrid().length][gameState.getGrid()[0].length];
    	for(int i=0; i<gameState.getGrid().length; i++)
    		newGrid[i] = gameState.getGrid()[i].clone();
    	newState.setGrid(newGrid);
    	
    	return newState;
    }
    
    
    
    /**
     * prints the node and its important attributes to the console
     * TODO needs to be implemented
     */
    public void printMe(String s) {
      printGrid();
      System.out.println();
    }
    
    /**
     * prints the grid
     */
    public void printGrid() {
      for(String[] s : gameState.getGrid()) {
        for(String ss : s) {
          System.out.print((ss == "" ? "x" : ss) + " ");
        }
        System.out.println();
      }
    }
}