package de.unimannheim.swt.pse.ctf.game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import com.google.gson.Gson;
import de.unimannheim.swt.pse.ctf.game.exceptions.TooManyPiecesException;
import de.unimannheim.swt.pse.ctf.game.map.MapTemplate;
import de.unimannheim.swt.pse.ctf.game.map.PieceDescription;
import de.unimannheim.swt.pse.ctf.game.state.GameState;
import de.unimannheim.swt.pse.ctf.game.state.Piece;
import de.unimannheim.swt.pse.ctf.game.state.Team;

public class BoardController {
  double xPartitionsSize;
  double yPartitionsSize;
  int numberOfTeams;
  int[] gridSize;
  GameState gameState;
  int[][] boundaries;
  
  /**
   * This constructor should be called to initialize a completely new GameState in create().
   * Initializes the Grid and places Bases and Blocks on it, 
   * Initializes new Teams() with their bases.
   * @param gameState
   * @param template
   */
  public BoardController(GameState gameState, MapTemplate template) {
    this.numberOfTeams = template.getTeams();
    this.gridSize = template.getGridSize();
    this.gameState = gameState;
    double[] partitionSizes = getPartitionSizes();
    this.yPartitionsSize = partitionSizes[0];
    this.xPartitionsSize = partitionSizes[1];
    this.boundaries = getBoundaries();
    
    //init//
    for(int i=0; i<numberOfTeams; i++) {
      gameState.getTeams()[i] = new Team();
      gameState.getTeams()[i].setId(""+i); 
    }
    initEmptyGrid();
    placeBases(gameState);
    placeBlocks(template, gameState.getGrid(), template.getBlocks());
  }

  /**
   * This constructor should be called to update an already initialized GameState.
   * @param gameState
   */
  public BoardController(GameState gameState) {
    this.gameState = gameState;
    this.gridSize = new int[] {gameState.getGrid().length, gameState.getGrid()[0].length};
    this.numberOfTeams = gameState.getTeams().length;
    double[] partitionSizes = getPartitionSizes();
    this.yPartitionsSize = partitionSizes[0];
    this.xPartitionsSize = partitionSizes[1];
    this.boundaries = getBoundaries();
  }
  
  /**
   * Helper Method for initializing the Grid with Empty spaces
   *
   * @author rsyed
   * @return String[][] with empty boxes
   */
  public void initEmptyGrid() {
    String[][] grid = new String[this.gridSize[0]][this.gridSize[1]];
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[i].length; j++) {
        grid[i][j] = "";
      }
    }
    this.gameState.setGrid(grid);
  }

  /**
   * A team gets initialized and put in the GameState.
   *
   * @author ysiebenh
   * @param int teamID, MapTemplate template
   * @return initialized team
   */
  public Team initializeTeam(int teamID, MapTemplate template) {

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

    // initializing the team
    Team team = this.gameState.getTeams()[teamID];
    team.setId(Integer.toString(teamID));
    team.setColor(GameEngine.getRandColor());
    team.setFlags(template.getFlags());

    Piece[] pieces = new Piece[indPieces.size()];
    int iterator = 0;
    for (Piece p : indPieces) {
      pieces[iterator++] = p;
    }
    team.setPieces(pieces);
    this.gameState.getTeams()[teamID] = team;
    return team;
  }
  
  /**
   * Partitions the Grid and returns the boundaries using the MapTemplate and GameState Attribute.
   * The upper and lower boundary is inclusive.
   * @return int[][] containing a team and its boundaries as
   *    {team index}{lower y, upper y, lower x, upper x}
   */
  public int[][] getBoundaries(){
    int[][] boundaries = new int[this.numberOfTeams][4];
    for(int p=0, x=0, y=0; p<boundaries.length; p++) {
      boundaries[p][0] = (int)(y * this.yPartitionsSize);
      boundaries[p][1] = (int)((y+1) * this.yPartitionsSize)-1;
      boundaries[p][2] = (int)(x * this.xPartitionsSize);
      boundaries[p][3] = (int)((x+1) * this.xPartitionsSize)-1;
      if((int)((x+1)*xPartitionsSize)>=this.gridSize[1]) {
        x=0;
        y++;
      } else {
        x++;
      }
    }
    return boundaries;
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
  void placeBlocks(MapTemplate mt, String[][] grid, int blocks) {
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

  /**
   * Places the bases on the grid and assigns them to a team.
   *
   * @author sistumpf
   * @param GameState gameState
   * @param MapTemplate template
   */
  public void placeBases(GameState gameState) {
    String[][] grid = gameState.getGrid();
    int bases = this.numberOfTeams;
//    System.out.println("xPartitions: " + (xCuts+1)+ " with size " + xPartitionSize + ", yPartitions: " + (yCuts+1)+ " with size " + yPartitionSize);
    for(int y=0, yc=0, team=0; yc*yPartitionsSize<grid.length; y+=yPartitionsSize, yc++)
      for(int x=0, xc=0; bases>0 && xc*xPartitionsSize<grid[y].length; x+=xPartitionsSize, bases--, xc++) {
        grid[(int)(y + yPartitionsSize/2)][(int)(x + xPartitionsSize/2)] = "b:" + gameState.getTeams()[team].getId();
        gameState.getTeams()[team].setBase(new int[] {(int)(y + yPartitionsSize/2), (int)(x + xPartitionsSize/2)});
        team++;
      }
    this.gameState.setGrid(grid);
  }
  
  /**
   * Returns the partition size to cut the board in a(n) = floor(n^2/4) partitions, where teams <= a(n)
   * 
   * @author sistumpf
   * @return int[]{yPartitionsSize, xPartitionsSize}
   */
  private double[] getPartitionSizes() {
    int yCuts = 1;
    int xCuts = 0;
    for(boolean cutX = true; (xCuts+1) * (yCuts+1) < this.numberOfTeams; cutX = !cutX) {
      if(cutX) {
        xCuts++;
      } else {
        yCuts++;
      }
    }
    return new double[] {this.gridSize[0] / (double)(yCuts+1), this.gridSize[1] / (double)(xCuts+1)};
  }
  
  /////////////////////////////////////////////////////////
  //                    PLACEMENT TYPES                  //
  /////////////////////////////////////////////////////////
  
  /**
   * Chooses the correct method to place the pieces onto the grid.
   *
   * @author sistumpf
   * @param GameState gameState
   * @param MapTemplate template
   * @throws TooManyPiecesException
   */
  void initPieces(MapTemplate template) throws TooManyPiecesException {

    //Exception Calculator
    if (gameState.getTeams()[0].getPieces().length
        > (gameState.getGrid().length / 2) * (gameState.getGrid()[0].length - 2)) {
      throw new TooManyPiecesException("Too many Pieces! Make the board bigger! :)");
    }
    
    //Base teamID assigner
    for (Team team : gameState.getTeams()) {
      gameState.getGrid()[team.getBase()[0]][team.getBase()[1]] = "b:" + team.getId();
    }
    
    new PiecePlacer(gameState, this.boundaries).placePieces(template.getPlacement());
  }
}
