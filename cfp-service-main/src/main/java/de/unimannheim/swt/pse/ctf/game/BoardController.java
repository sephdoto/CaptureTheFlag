package de.unimannheim.swt.pse.ctf.game;

import java.util.LinkedList;

import de.unimannheim.swt.pse.ctf.game.map.MapTemplate;
import de.unimannheim.swt.pse.ctf.game.map.PieceDescription;
import de.unimannheim.swt.pse.ctf.game.state.GameState;
import de.unimannheim.swt.pse.ctf.game.state.Piece;
import de.unimannheim.swt.pse.ctf.game.state.Team;

public class BoardController {
  double xPartitionsSize;
  double yPartitionsSize;
  MapTemplate template;
  GameState gameState;
  
  public BoardController(GameState gameState, MapTemplate template) {
    this.template = template;
    this.gameState = gameState;
    double[] partitionSizes = getPartitionSizes(template.getGridSize()[0], template.getGridSize()[1], template.getTeams());
    this.yPartitionsSize = partitionSizes[0];
    this.xPartitionsSize = partitionSizes[1];
  }

  /**
   * Helper Method for initializing the Grid with Empty spaces
   *
   * @author rsyed
   * @param int x
   * @param int y
   * @return String[][] with empty boxes
   */
  public String[][] initEmptyGrid(int x, int y) {
    String[][] grid = new String[x][y];
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[i].length; j++) {
        grid[i][j] = "";
      }
    }
    return grid;
  }

   /**
   * Helper method to initialize the teams
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
    Team team = new Team();
    team.setId(Integer.toString(teamID));
    team.setColor(GameEngine.getRandColor());
    team.setFlags(template.getFlags());

    Piece[] pieces = new Piece[indPieces.size()];
    int iterator = 0;
    for (Piece p : indPieces) {
      pieces[iterator++] = p;
    }
    team.setPieces(pieces);
    return team;
  }
  
  /**
   * Partitions the Grid and returns the boundaries using the MapTemplate and GameState Attribute.
   * @return int[][] containing a team and its boundaries as
   *    {team index}{lower y, upper y, lower x, upper x}
   */
  public int[][] getBoundaries(){
    int[][] boundaries = new int[this.template.getTeams()][4];
    for(int p=0, x=0, y=0; p<boundaries.length; p++) {
      boundaries[p][0] = (int)(y * this.yPartitionsSize);
      boundaries[p][1] = (int)((y+1) * this.yPartitionsSize)-1;
      boundaries[p][2] = (int)(x * this.xPartitionsSize);
      boundaries[p][3] = (int)((x+1) * this.xPartitionsSize)-1;
      if((int)((x+1)*xPartitionsSize)>=this.template.getGridSize()[1]) {
        x=0;
        y++;
      } else {
        x++;
      }
    }
    return boundaries;
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
    int bases = template.getTeams();
//    System.out.println("xPartitions: " + (xCuts+1)+ " with size " + xPartitionSize + ", yPartitions: " + (yCuts+1)+ " with size " + yPartitionSize);
    for(int y=0, yc=0, team=0; yc*yPartitionsSize<grid.length; y+=yPartitionsSize, yc++)
      for(int x=0, xc=0; bases>0 && xc*xPartitionsSize<grid[y].length; x+=xPartitionsSize, bases--, xc++) {
        grid[(int)(y + yPartitionsSize/2)][(int)(x + xPartitionsSize/2)] = "b:" + gameState.getTeams()[team].getId();
        gameState.getTeams()[team].setBase(new int[] {(int)(y + yPartitionsSize/2), (int)(x + xPartitionsSize/2)});
        team++;
      }
  }
  
  /**
   * Returns the partition size to cut the board in a(n) = floor(n^2/4) partitions, where teams <= a(n)
   * 
   * @author sistumpf
   * @param gridLengthY = grid.length
   * @param gridLengthX = grid[0].length
   * @param teams
   * @return int[]{yPartitionsSize, xPartitionsSize}
   */
  private double[] getPartitionSizes(int gridLengthY, int gridLengthX, int teams) {
    int yCuts = 1;
    int xCuts = 0;
    for(boolean cutX = true; (xCuts+1) * (yCuts+1) < teams; cutX = !cutX) {
      if(cutX) {
        xCuts++;
      } else {
        yCuts++;
      }
    }
    return new double[] {gridLengthY / (double)(yCuts+1), gridLengthX / (double)(xCuts+1)};
  }
}
