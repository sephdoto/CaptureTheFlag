package de.unimannheim.swt.pse.ctf.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;
import de.unimannheim.swt.pse.ctf.game.map.PlacementType;
import de.unimannheim.swt.pse.ctf.game.state.GameState;
import de.unimannheim.swt.pse.ctf.game.state.Piece;
import de.unimannheim.swt.pse.ctf.game.state.Team;

/**
 * This class places pieces on a grid.
 * The blocks and bases should already be initialized, 
 * all teams should be present in the GameState,
 * all their pieces should also be there.
 * @author sistumpf
 */
public class PiecePlacer {
  GameState gameState;
  //boundaries = a teams rectangular partition of the map, in those boundaries the starter pieces will be placed
  int[][] boundaries;
  //directions: 
      // int[0-3] =    where the team is "facing", if it is 0 - left; 1 - right; 2 - up; 3 - down
      // int[0-3][] =  positions for AI_Tools.updatePos, encoded as mid-left-right
      // int[4-7] =    where the team is "facing", if it is 4 - left; 5 - right; 6 - up; 7 - down
      // int[4-7][] =  positions for AI_Tools.updatePos, encoded as left-right
  int[][] directions = new int[][] {{0,6,4},{1,5,7},{2,4,5},{3,7,6},
    {3,2},{2,3},{0,1},{1,0}};
  
  public PiecePlacer(GameState gameState, int[][] boundaries) {
    this.gameState = gameState;
    this.boundaries = boundaries;
  }
  
  /**
   * This method shall be called to place the Pieces on the grid.
   * 
   * @author sistumpf
   * @param placement
   */
  public void placePieces(PlacementType placement) {
    switch (placement) {
      case symmetrical:
        placePiecesSymmetrical();
        break;
      case spaced_out:
        placePiecesSpaced();
        break;
      case defensive:
        placePiecesDefensive();
        break;
    }
  }
  
  /**
   * Places the pieces symmetrical on the grid and in the arrays
   *
   * @author sistumpf
   * @param GameState gameState
   */
  void placePiecesSymmetrical() {
    for (Team team : gameState.getTeams()) {
      int facing = nextBaseDirection(team);
      //place first 3 pieces in front of base
      int piece = 0;
      for (; piece < team.getPieces().length && piece<3; piece++) {
        int[] newPos = posInFrontOfBase(team.getBase(), facing, piece);
        if(safeToPlace(Integer.parseInt(team.getId()), newPos)) placePiece(team, piece, newPos);
      }
      //place to the sides of the base, till boundaries are hit
      for(int boundsHit=0; boundsHit<2 && piece<team.getPieces().length; piece++) {
        int reach = (piece-1)/2;
        int[] newPos = AI_Tools.updatePos(team.getBase().clone(), this.directions[4+facing][(piece+boundsHit)%2], reach);
        if(safeToPlace(Integer.parseInt(team.getId()), newPos)) {
          placePiece(team, piece, newPos);
          boundsHit = 0;
        }else {
          piece--;
          boundsHit++;
        }
      }
      //place remaining pieces with respawn piece logic
      for(; piece<team.getPieces().length; piece++) {
        int[] newPos = EngineTools.respawnPiecePosition(gameState, team.getBase());
        if(safeToPlace(Integer.parseInt(team.getId()), newPos)) placePiece(team, piece, newPos);
      }
    }
  }
  private void placePiecesSpaced() {
    
  }  
  private void placePiecesDefensive() {
    
  }
  
  ////////////////////////////////////////////////////
  //        additional helper methods               //
  ////////////////////////////////////////////////////
  /**
   * places a piece, given by its index in the team.pieces array, on the position newPos.
   * 
   * @author sistumpf
   * @param team
   * @param piece
   * @param newPos
   */
  private void placePiece(Team team, int piece, int[] newPos) {
    team.getPieces()[piece].setPosition(newPos);
    gameState.getGrid()[newPos[0]][newPos[1]] = team.getPieces()[piece].getId();
  }
  
  /**
   * Checks if a position is not out of bounds and the position is empty (no pieces/bases/grids)
   * 
   * @author sistumpf
   * @param newPos
   * @return
   */
  private boolean safeToPlace(int team, int[] newPos) {
    return !positionOutOfBounds(team, newPos) && 
        AI_Tools.emptyField(gameState.getGrid(), newPos);
  }
  
  /**
   * Checks if a position is not contained in the teams partition, using this.boundaries
   *    -> {team index}{lower y, upper y, lower x, upper x}
   *    
   * @author sistumpf
   * @param team index
   * @param pos position
   * @return true if the position is out of bounds
   */
  public boolean positionOutOfBounds(int team, int[] pos) {
    return (pos[0] < this.boundaries[team][0] || pos[1] < this.boundaries[team][2] ||
        pos[0] > this.boundaries[team][1] || pos[1] > this.boundaries[team][3]);
  }
  
  /**
   * Returns the 3 positions in front of the base, which one depends on midLeftRight
   * 
   * @author sistumpf
   * @param base position
   * @param facing : in what direction the team is facing:  0 - left; 1 - right; 2 - up; 3 - down
   * @param midLeftRight : if the position up (0), up-left(1) or up-right(2) is targetted
   * @return updated position
   */
  int[] posInFrontOfBase(int[] base, int facing, int midLeftRight) {
    return AI_Tools.updatePos(base.clone(), this.directions[facing][midLeftRight], 1);
  }
  
  /**
   * Returns the direction with the shortest distance to a teams base.
   *
   * @author sistumpf
   * @param ourBase
   * @param enemyBase
   * @return 0 - left; 1 - right; 2 - up; 3 - down
   */
  int nextBaseDirection(Team we) {
    int[] ourBase = we.getBase();
    HashMap<Double, Integer> distances = new HashMap<Double, Integer>();
    for(int team=0; team<gameState.getTeams().length; team++) {
      if(team == Integer.parseInt(we.getId())) continue;
      int[] enemyBase = gameState.getTeams()[team].getBase();
      distances.put(Math.sqrt(Math.pow(ourBase[1] - enemyBase[1], 2) + Math.pow(ourBase[0] - enemyBase[0], 2)), team);
//      System.out.println("Distance to team " + team + " = " + Math.sqrt(Math.pow(ourBase[1] - enemyBase[1], 2) + Math.pow(ourBase[0] - enemyBase[0], 2)));
    }
    DoubleSummaryStatistics stats = distances.keySet().stream().collect(Collectors.summarizingDouble(Double::doubleValue));
    int nearestTeam = distances.get(stats.getMin());
//    System.out.println("nearest team: " + nearestTeam + ", base pos: " + gameState.getTeams()[nearestTeam].getBase()[0] + 
//    "," + gameState.getTeams()[nearestTeam].getBase()[1]);
    int xDifference = ourBase[1] - gameState.getTeams()[nearestTeam].getBase()[1];
    int yDifference = ourBase[0] - gameState.getTeams()[nearestTeam].getBase()[0];
    if(Math.abs(xDifference) > Math.abs(yDifference))
      return xDifference > 0 ? 0 : 1;
    return yDifference > 0 ? 2 : 3;
  }
  
  /**
   * Places the pieces on the board randomly using the {@link EngineTools#seededRandom(String[][],
   * int, int, int) seededRandom} method
   *
   * @author ysiebenh, sistumpf
   * @param GameState gameState
   */
  private void randomPlacement() {
    int i = 0;
    for (Team t : gameState.getTeams()) {
      for (Piece p : t.getPieces()) {
        int newY = 0;
        int newX = 0;
        int m = 0;
        do {
          newY = EngineTools.seededRandom(gameState.getGrid(), m++, boundaries[i][1]+1, boundaries[i][0]);
          newX = EngineTools.seededRandom(gameState.getGrid(), 1 - m++, boundaries[i][3]+1, boundaries[i][2]);
        } while (!gameState.getGrid()[newY][newX].equals(""));
        p.setPosition(new int[] {newY, newX});
        gameState.getGrid()[newY][newX] = p.getId();
      }
      i++;
    }
  }
}
