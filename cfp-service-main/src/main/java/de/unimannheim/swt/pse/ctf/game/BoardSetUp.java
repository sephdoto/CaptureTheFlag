package de.unimannheim.swt.pse.ctf.game;

import com.google.gson.Gson;
import de.unimannheim.swt.pse.ctf.game.map.MapTemplate;
import de.unimannheim.swt.pse.ctf.game.map.PieceDescription;
import de.unimannheim.swt.pse.ctf.game.state.GameState;
import de.unimannheim.swt.pse.ctf.game.state.Piece;
import de.unimannheim.swt.pse.ctf.game.state.Team;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class BoardSetUp {

  /**
   * This is a helper method to initialize the teams in create
   *
   * @author ysiebenh
   * @param int teamID
   * @return Team thats initialized
   */
  static Team initializeTeam(int teamID, MapTemplate template) {
    // TODO different placement types
    // Creating the Pieces for the team
    int count = 1;
    LinkedList<Piece> indPieces = new LinkedList<Piece>();
    for (PieceDescription piece : template.getPieces()) {
      for (int i = 0; i < piece.getCount(); i++) {
        Piece x = new Piece();
        x.setId("p:" + teamID + "_" + Integer.toString(count++));
        x.setDescription(piece);
        x.setTeamId(Integer.toString(teamID));
        indPieces.add(x);
      }
    }

    // initializing team
    Team team = new Team();
    team.setId(Integer.toString(teamID));
    team.setColor(GameEngine.getRandColor());

    // TODO die Bases müssen woanders gesetzt werden.
    

    team.setFlags(template.getFlags());

    Piece[] pieces = new Piece[indPieces.size()]; // putting the pieces in an array
    int iterator = 0;
    for (Piece p : indPieces) {
      pieces[iterator++] = p;
    }
    team.setPieces(pieces);

    return team;
  }

  /**
   * This method decides what method to call for placing pieces.
   *
   * @author sistumpf
   * @param gameState
   * @param template
   */
  static void initPieces(GameState gameState, MapTemplate template) {
	  for (Team team : gameState.getTeams()) {
	      gameState.getGrid()[team.getBase()[0]][team.getBase()[1]] = "b:" + team.getId();
	  }
    switch (template.getPlacement()) {
      case symmetrical:
        placePiecesSymmetrical(gameState);
        break;
      case spaced_out:
        placePiecesSpaced(gameState);
        break;
      case defensive:
        placePiecesDefensive(gameState);
        break;
    }
  }

  /**
   * This Method creates a GameStates grid. Teams, Pieces and Bases must already be initialized in
   * the GameState. Pieces and Bases are placed right on their in gameState.getTeams()[x] specified
   * position. Blocks get placed based on the PlacementType from the MapTemplate.
   *
   * @author sistumpf
   * @param MapTemplate template
   * @param Team[] teams
   * @param String[][] grid
   * @param int blocks
   * @return grid with placed pieces and blocks
   */
  static void initGrid(GameState gameState, MapTemplate template) {
    String[][] grid = gameState.getGrid();

    for (Team team : gameState.getTeams()) {
      //grid[team.getBase()[0]][team.getBase()[1]] = "b:" + team.getId(); //Moved to InitPieces so the pieces will be placed around the base
      for (Piece piece : team.getPieces()) {
        grid[piece.getPosition()[0]][piece.getPosition()[1]] = piece.getId();
      }
    }

    
  }

  /**
   * DUMMY TODO implement this method
   *
   * @param teams
   * @param grid
   * @return
   */
  static void placePiecesSpaced(GameState gameState) {
	
	placePiecesSymmetrical(gameState);
	
	for (Team team : gameState.getTeams()) {
	      //grid[team.getBase()[0]][team.getBase()[1]] = "b:" + team.getId(); //Moved to InitPieces so the pieces will be placed around the base
	      for (Piece piece : team.getPieces()) {
	        gameState.getGrid()[piece.getPosition()[0]][piece.getPosition()[1]] = piece.getId();
	      }
	    }
	for(Team t : gameState.getTeams()) {
		GameState current = gameState;
		while(true) {
				LinkedList<GameState> neighbors = EngineTools.getNeighbors(current, Integer.decode(t.getId()), null);
				System.out.println(neighbors);
				GameState bestNeighbor = EngineTools.getBestState(neighbors, Integer.decode(t.getId()));
				if(EngineTools.valueOf(current,  Integer.decode(t.getId())) > EngineTools.valueOf(bestNeighbor, Integer.decode(t.getId()))) {
					gameState.setGrid(current.getGrid());
					gameState.getTeams()[Integer.decode(t.getId())].setPieces(current.getTeams()[Integer.decode(t.getId())].getPieces());
					break;
				}
				else current = bestNeighbor;
		}
	}
	
  }

  /**
   * DUMMY TODO implement this method
   *
   * @param teams
   * @param grid
   * @return
   */
  static void placePiecesDefensive(GameState gameState) {
    return;
  }

  /**
   * This is a helper method to place the pieces on the board in the create method
   *
   * @author ysiebenh
   * @param Team[] teams to be placed, String[][] grid upon which they are supposed to be placed
   * @return String[][] the finished board
   */
  static void placePiecesSymmetrical(GameState gameState) {
    // TODO more than two teams
    // putting the pieces on the board (team1)
    int row = 1;
    int column = 1;
    for (int i = 0; i < gameState.getTeams()[0].getPieces().length; i++) {
      if (column == gameState.getGrid()[0].length-1) {
        row++;
        column = 1;
        if(gameState.getTeams()[0].getPieces().length - i < gameState.getGrid()[0].length - 2) {
      	  column =  (gameState.getGrid()[0].length/2)- (gameState.getTeams()[0].getPieces().length - i)/2;
        } 
        
      }
    
      
      if (!gameState.getGrid()[row][column].equals("")) {
        column++;
        i--;
      } else {
        Piece piece = gameState.getTeams()[0].getPieces()[i];
        piece.setPosition(new int[] {row, column});
        //gameState.getGrid()[row][column] = piece.getId();
        column++;
      }
    }

    // putting pieces on the board (team2)
    row = gameState.getGrid().length - 2;
    column = 1;
    for (int i = 0; i < gameState.getTeams()[1].getPieces().length; i++) {
      if (column == gameState.getGrid()[1].length-1) {
        row--;
        column = 1;
        if(gameState.getTeams()[0].getPieces().length - i < gameState.getGrid()[1].length - 2) {
        	  column =  (gameState.getGrid()[0].length/2)- (gameState.getTeams()[1].getPieces().length - i)/2;
          } 
      }
      if (!gameState.getGrid()[row][column].equals("")) {
        column++;
        i--;
      } else {
        Piece piece = gameState.getTeams()[1].getPieces()[i];
        piece.setPosition(new int[] {row, column});
        //gameState.getGrid()[row][column] = piece.getId();
        column++;
      }
    }
  }
  
  /**
   * This is a helper method to place the bases in the create method
   * @author yannicksiebenhaar
   */
  
  static void placeBases(GameState gs, MapTemplate mt) {
	  int teams = mt.getTeams();
	  if(teams%2 != 0) teams++;
	  int height = mt.getGridSize()[0]/(teams*2);
	  int width = mt.getGridSize()[1]/teams;
	  for(int i = 0; i < mt.getTeams(); i++) {
		  if (i == 0) {
			  gs.getTeams()[i].setBase(new int[] {height,mt.getGridSize()[1]/2});
		    } else if (i == 1) {
		      gs.getTeams()[i].setBase(new int[] {mt.getGridSize()[0] - 1 - mt.getGridSize()[0]/4,mt.getGridSize()[1]/2});
		    }   
	  }
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
  static void placeBlocks(MapTemplate mt, String[][] grid, int blocks) {
    ArrayList<Integer[]> freeList = new ArrayList<Integer[]>();
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[i].length; j++) {
        if (grid[i][j].equals("")) {
          freeList.add(new Integer[] {i, j});
        }
      }
    }

    for (; blocks > 0; blocks--) {
      int x = seededRandom(mt, blocks, freeList.size());
      grid[freeList.get(x)[0]][freeList.get(x)[1]] = "b";
      freeList.remove(x);
    }
  }

  /**
   * This method should be used instead of Math.random() to generate deterministic positive pseudo
   * random values. Changing modifier changes the resulting output for the same seed.
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
