package org.ctf.ai.mcts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.ctf.ai.AI_Tools;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Piece;
import org.ctf.shared.state.Team;

public class TreeNode {
    TreeNode parent;
    TreeNode[] children;
    HashMap<String, ArrayList<int[]>> possibleMoves;
    GameState gameState;
    int[] wins;
    
    public TreeNode(TreeNode parent, GameState gameState, int player, int[] wins) {
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
     * prints the node and its important attributes to the console
     * TODO needs to be implemented
     */
    public void printMe(String s) {
        System.out.println("TO BE PRINTED " + s);
    }
    
    /**
     * Deep copies the current TreeNode, rotates the player Attribute to the next player.
     * @return a copy of the current node
     */
    public TreeNode clone() {
      TreeNode treeNode = new TreeNode(this, copyGameState(gameState), (gameState.getCurrentTeam() +1) % gameState.getTeams().length, Arrays.copyOf(wins, wins.length));
      return treeNode;
    }
    
    //TODO implementieren?
    public GameState copyGameState(GameState gameState) {
    	GameState newState = new GameState();
    	gameState.setCurrentTeam(gameState.getCurrentTeam());
    	gameState.setLastMove(gameState.getLastMove());
    	Team[] teams = new Team[gameState.getTeams().length];
    	for(int i=0; i<teams.length; i++)
    	gameState.setTeams(null);
    	
    	String[][] newGrid = new String[gameState.getGrid().length][gameState.getGrid()[0].length];
    	for(int i=0; i<gameState.getGrid().length; i++)
    		newGrid[i] = gameState.getGrid()[i].clone();
    	gameState.setGrid(newGrid);
    	return gameState;
    }
}