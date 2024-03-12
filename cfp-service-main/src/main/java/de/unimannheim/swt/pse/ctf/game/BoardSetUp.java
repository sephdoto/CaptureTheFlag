package de.unimannheim.swt.pse.ctf.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.unimannheim.swt.pse.ctf.game.map.MapTemplate;
import de.unimannheim.swt.pse.ctf.game.map.PieceDescription;
import de.unimannheim.swt.pse.ctf.game.state.Piece;
import de.unimannheim.swt.pse.ctf.game.state.Team;

public class BoardSetUp {
	   /**
     * This is a helper method to place the flags in the create method
     * 
     * @author ysiebenh
     * @param MapTemplate template, String[][] grid
     * @return String[][] grid with flags placed 
     */
	 static String[][] placeFlags(MapTemplate template, String[][] grid) {
	    	String[][] newGrid = Arrays.copyOf(grid, grid.length);
	        if(template.getTeams() == 2) {
	        	newGrid[newGrid.length/4][newGrid[0].length/2] = "b:1";
	        	newGrid[newGrid.length-(newGrid.length/4)-1][newGrid[0].length/2] = "b:2";
	        }
	        else if(template.getTeams() == 4) {
	        	newGrid[0][0] = "b:1";
	        	newGrid[newGrid.length-1][0] = "b:2";
	        	newGrid[0][newGrid[0].length-1] = "b:3";
	        	newGrid[newGrid.length-1][newGrid[0].length-1] = "b:4";
	        }
	    	return newGrid;
	    }
    
    /**
     * This is a helper method to initialize the teams in create 
     * 
     * @author ysiebenh
     * @param int teamID
     * @return Team thats initialized
     */
     static Team initializeTeam(int teamID, MapTemplate template) {
        //TODO different placement types
    	//Creating the Pieces for the team 
        int count = 1;
        LinkedList<Piece> indPieces = new LinkedList<Piece>();
        for(PieceDescription piece : template.getPieces()) {
        	for(int i = 0; i < piece.getCount();i++) {
        		Piece x = new Piece();
        		x.setId(Integer.toString(count++));
        		x.setDescription(piece);
        		x.setTeamId(Integer.toString(teamID)); //TODO team id
        		indPieces.add(x);
        	}
        }
        
      //initializing team 
        	Team team = new Team();
        	team.setId(Integer.toString(teamID));
            team.setColor(GameEngine.getRandColor());
            if(teamID == 1) {
            	team.setBase(new int[]{0,0});
            }
            else if(teamID == 2) {
            	team.setBase(new int[]{template.getGridSize()[1]-1,template.getGridSize()[0]-1});
            }
            
        	Piece[] pieces = new Piece[indPieces.size()]; //putting the pieces in an array 
        	int iterator = 0;
        	for(Piece p : indPieces) {
        		pieces[iterator++] = p;
        	}
        	team.setPieces(pieces);
        
        
    	return team;
    }
    
    /**
     * This is a helper method to place the pieces on the board in the create method 
     * 
     * @author ysiebenh
     * @param Team[] teams to be placed, String[][] grid upon which they are supposed to be
     * placed 
     * @return String[][] the finished board
     */
     static String[][] placePieces(Team[] teams, String[][] grid){
    	//TODO more than two teams
    	//TODO different types
    	//putting the pieces on the board (team1)
    	String[][] newGrid = Arrays.copyOf(grid, grid.length);
        int row = 1;
        int column = 0;
	    for(int i = 0; i < teams[0].getPieces().length; i++) {
	       	if(column == newGrid[0].length) {
	       		row++;
	       		column = 0;
	       	}
	       	if(!newGrid[row][column].equals("")) {
	       		column++;
	       		i--;
	       	}
	       	else {
	       		Piece piece = teams[0].getPieces()[i];	        
	       		newGrid[row][column] = "p:" + piece.getTeamId() + "_" + piece.getId();
	       		column++;
	       		}
	        }
	        
	    //putting pieces on the board (team2)    
	    row = newGrid.length - 2;
	    column = newGrid[0].length-1;
	    for(int i = 0; i < teams[0].getPieces().length; i++) {
        	if(column == -1) {
        		row--;
		  		column = newGrid[0].length-1;
        	}
        	if(!newGrid[row][column].equals("")) {
	       		column--;
	       		i--;
	       	}
	       	else {
	       		Piece piece = teams[0].getPieces()[i];
	       		newGrid[row][column] = "p:" + piece.getTeamId() + "_" + piece.getId();
	       		column--;
	       	}	
		}
	    return newGrid;
    }
    
    /**
     * This is a helper method to place the blocks on the board in the create method 
     * 
     * @author sistumpf
     * @param MapTemplate mt, used as a seed for pseudo random number generating
     * @param String[][] grid
     * @param int blocks, number of blocks to be placed
     * @return String[][] grid with blocks placed on it
     */
     static String[][] placeBlocks(MapTemplate mt, String[][] grid, int blocks){
    	 ArrayList<Integer[]> freeList = new ArrayList<Integer[]>();
    	 for(int i=0; i<grid.length; i++) {
    		 for(int j=0; j<grid[i].length; j++) {
    			 if(grid[i][j].equals("")) {
    				 freeList.add(new Integer[] {i,j});
    			 }
    		 }
    	 }
    	 
    	 for(; blocks>0; blocks--) {
    		 int x = seededRandom(mt, blocks, freeList.size());
    		 grid[freeList.get(x)[0]][freeList.get(x)[1]] = "b";
    		 freeList.remove(x);
    	 }

        return grid;
    }

     
     /**
      * This method should be used instead of Math.random() to generate deterministic positive pseudo random values.
      * Changing modifier changes the resulting output for the same seed.
      * 
      * @author sistumpf
      * @param MapTemplate mt, gets converted to a random seed
      * @param int modifier, to get different random values with the same seed
      * @param upperBound, upper bound for returned random values, upperBound = 3 -> values 0 to 2
      * @return
      */
     static int seededRandom(MapTemplate mt, int modifier, int upperBound) {
    	 int seed = (new Gson().toJson(mt) + String.valueOf(modifier)).hashCode();
    	 return new Random(seed).nextInt(upperBound);
     }
}
