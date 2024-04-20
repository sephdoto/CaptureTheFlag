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
  int[][] boundaries;
  
  public PiecePlacer(GameState gameState, int[][] boundaries) {
    this.gameState = gameState;
    this.boundaries = boundaries;
  }
  
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
   * Places the pieces symmetrical on the board
   *
   * @author sistumpf
   * @param GameState gameState
   */
  void placePiecesSymmetrical() {
    for (int team = 0; team < gameState.getTeams().length; team++) {
      for (int piece = 0; piece < gameState.getTeams()[0].getPieces().length; piece++) {
        ;
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
   * Returns the direction with the shortest distance to a teams base.
   *
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
