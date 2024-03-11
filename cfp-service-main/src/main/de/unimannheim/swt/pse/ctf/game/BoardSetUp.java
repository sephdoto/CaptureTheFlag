package de.unimannheim.swt.pse.ctf.game;

import java.util.Arrays;
import java.util.LinkedList;

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
        	newGrid[0][0] = "b:1";
        	newGrid[newGrid.length-1][newGrid[0].length-1] = "b:2";
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
        	Piece piece = teams[0].getPieces()[i];	        	
        	newGrid[row][column] = "p:" + piece.getTeamId() + "_" + piece.getId();
	        column++;
	        }
	        
	    //putting pieces on the board (team2)    
	    row = newGrid.length - 2;
	    column = newGrid[0].length-1;
	    for(int i = 0; i < teams[0].getPieces().length; i++) {
        	if(column == -1) {
        		row--;
		  		column = newGrid[0].length-1;
        	}
    	Piece piece = teams[0].getPieces()[i];
    	newGrid[row][column] = "p:" + piece.getTeamId() + "_" + piece.getId();
    	column--;	
		}
	    return newGrid;
    }
    
    /**
     * This is a helper method to place the pieces on the board in the create method 
     * 
     * @author ysiebenh
     * @param String[][] grid, int blocks number of blocks
     * @return String[][] the finished board
     */
     static String[][] placeBlocks(String[][] grid, int blocks){
    	 //placing blocks   TODO odd numbers?(only divisible by 2)
    	String[][] newGrid = Arrays.copyOf(grid, grid.length);
        for(int i = 0; i < blocks; i++) {
        	int x = (int) (Math.random() * grid[i].length);
        	int y = (int) (Math.random() * (grid.length/2));
        	
        	if(newGrid[x][y].equals("")) {
        		newGrid[x][y] = "b";
        		newGrid[newGrid.length-x-1][newGrid[0].length-y-1] = "b";
        		i++;	
        	}
        	else i--;
        }
        return newGrid;
    }
}
