package de.unimannheim.swt.pse.ctf.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.stream.Stream;
import de.unimannheim.swt.pse.ctf.game.map.MapTemplate;
import de.unimannheim.swt.pse.ctf.game.state.GameState;
import de.unimannheim.swt.pse.ctf.game.state.Move;
import de.unimannheim.swt.pse.ctf.game.state.Piece;
import de.unimannheim.swt.pse.ctf.game.state.Team;

/**
 * This class contains useful methods for the GameEngine.
 * @author sistumpf & ysiebenh
 */
public class EngineTools extends AI_Tools {
  /**
   * Returns a GameStates next valid (not null) team.
   * @param gameState
   * @return next Team != null
   */
  public static int getNextTeam(GameState gameState) {
    for(int i=(gameState.getCurrentTeam()+1) % gameState.getTeams().length; ;i = (i + 1) % gameState.getTeams().length) {
      if(gameState.getTeams()[i] != null) {
        return i;
      }
    }
  }
  
  /**
   * Removes a certain team from the GameState.
   * team is the place of the team in the GameState.getTeams Array.
   * @param gameState
   * @param team
   */
  public static void removeTeam(GameState gameState, int team) {
    gameState.getGrid()[gameState.getTeams()[team].getBase()[0]][gameState.getTeams()[team].getBase()[1]] = "";
    for(Piece p : gameState.getTeams()[team].getPieces())
      gameState.getGrid()[p.getPosition()[0]][p.getPosition()[1]] = "";
    gameState.getTeams()[team] = null;
  }
  
  /**
   * This method should be used instead of Math.random() to generate deterministic positive pseudo
   * random values. Changing modifier changes the resulting output for the same seed.
   *
   * @param grid, used as a base to generate a random seed
   * @param modifier, to get different random values with the same seed
   * @param upperBound, upper bound for returned random values, upperBound = 3 -> values 0 to 2
   * @param lowerBound, like upperBound but on the lower end and included in the return value
   * @return pseudo random value
   */
  public static int seededRandom(String[][] grid, int modifier, int upperBound, int lowerBound) {
    StringBuilder sb = new StringBuilder();
    Stream.of(grid).forEach(s -> Stream.of(s).forEach(ss -> sb.append(ss)));
    int seed = sb.append(modifier).toString().hashCode();
    return new Random(seed).nextInt(upperBound - lowerBound) + lowerBound;
  }

  /**
   * Returns a valid position on which a Piece can safely respawn.
   * TODO test if this really works
   * @param gameState to access the grid and generate pseudo random numbers
   * @param basePos the position of the base of the Piece that gets respawned
   * @return valid position to respawn a piece on, null shouldn't be returned (compiler needs it).
   */
  public static int[] respawnPiecePosition(GameState gameState, int[] basePos) {
    int[] xTransforms;
    int[] yTransforms;

    for (int distance = 1; distance < gameState.getGrid().length; distance++) {
      xTransforms = fillXTransformations(new int[distance * 8], distance);
      yTransforms = fillYTransformations(new int[distance * 8], distance);
      
      
      for (int clockHand = 0; clockHand < distance * 8; clockHand++) {
        int x = basePos[1] + xTransforms[clockHand];
        int y = basePos[0] + yTransforms[clockHand];
        int[] newPos = new int[] {y, x};
        if (positionOutOfBounds(gameState.getGrid(), newPos)) continue;

        if (emptyField(gameState.getGrid(), newPos)) {
          for (int i = 1, random = seededRandom(gameState.getGrid(), i, xTransforms.length, 0); ;
              i++, random = seededRandom(gameState.getGrid(), i, xTransforms.length, 0)) {
            x = basePos[1] + xTransforms[random];
            y = basePos[0] + yTransforms[random];
            newPos = new int[] {y, x};
            if (positionOutOfBounds(gameState.getGrid(), newPos)) continue;
            if (emptyField(gameState.getGrid(), newPos)) return newPos;
          }
        }
      }
    }
    return null;
  }
  
  /**
   * Given a Piece and a GameState containing the Piece, an ArrayList with all valid locations the
   * Piece can walk on is returned. The ArrayList contains int[2] values, representing a (y,x)
   * location on the grid.
   *
   * @param GameState gameState
   * @param String pieceID
   * @return ArrayList<int[]> that contains all valid positions a piece could move to
   */
  public static ArrayList<int[]> getPossibleMoves(GameState gameState, String pieceID) {
    Piece piece =
        Arrays.stream(
                gameState.getTeams()[Integer.parseInt(pieceID.split(":")[1].split("_")[0])]
                    .getPieces())
            .filter(p -> p.getId().equals(pieceID))
            .findFirst()
            .get();
    ArrayList<int[]> possibleMoves = new ArrayList<int[]>();
    ArrayList<int[]> dirMap = new ArrayList<int[]>();
    if (piece.getDescription().getMovement().getDirections() == null) {
      try {
        getShapeMoves(gameState, piece);
      } catch (InvalidShapeException e) {
        e.printStackTrace();
      }

    } else {
      dirMap = AI_Tools.createDirectionMap(gameState, piece);
      for (int[] entry : dirMap) {
        for (int reach = entry[1]; reach > 0; reach--) {
          Move move = new Move();
          try {
            move = AI_Tools.checkMoveValidity(gameState, piece, entry[0], reach);
          } catch(Exception e) {
            System.out.println(2);
            move = AI_Tools.checkMoveValidity(gameState, piece, entry[0], reach);
          }
          if (move != null) possibleMoves.add(move.getNewPosition());
        }
      }
    }
    return possibleMoves;
  }
  
  /**
   * Creates x and y boundaries for all teams
   * 
   * @author ysiebenh
   * @returns an Integer array which stores the upper and lower x and y boundaries for each team
   *          (format: int[teamID][{lowerY,UpperY,lowerX,upperX, orientation(south = 0; north = 1}]
   */
  static int[][] cutUpGrid(GameState gs) {
    //TODO add more than four players
    int[][] teams = null;
    if(gs.getTeams().length == 2) {
      teams = new int[2][5];
      teams[0][0] = 0;
      teams[0][1] = gs.getGrid().length/2;
      teams[0][2] = 0;
      teams[0][3] = gs.getGrid()[0].length;
      teams[0][4] = 0;
      teams[1][0] = gs.getGrid().length/2;
      teams[1][1] = gs.getGrid().length;
      teams[1][2] = 0;
      teams[1][3] = gs.getGrid()[0].length;
      teams[1][4] = 1;
      
    } else if(gs.getTeams().length == 3 || gs.getTeams().length == 4){
      teams = new int[4][5];
      
      teams[0][0] = 0;
      teams[0][1] = gs.getGrid().length/2;
      teams[0][2] = 0;
      teams[0][3] = gs.getGrid()[0].length/2;
      teams[0][4] = 0;
      
      teams[1][0] = 0;
      teams[1][1] = gs.getGrid().length/2;
      teams[1][2] = gs.getGrid()[0].length/2;
      teams[1][3] = gs.getGrid()[0].length;
      teams[1][4] = 0;
      
      teams[2][0] = gs.getGrid().length/2;
      teams[2][1] = gs.getGrid().length;
      teams[2][2] = 0;
      teams[2][3] = gs.getGrid()[0].length/2;
      teams[2][4] = 1;
      
      teams[3][0] = gs.getGrid().length/2;
      teams[3][1] = gs.getGrid().length;
      teams[3][2] = gs.getGrid()[0].length/2;
      teams[3][3] = gs.getGrid()[0].length;
      teams[3][4] = 1;
      
      
    } else if(gs.getTeams().length == 5 || gs.getTeams().length == 6){
      
    } else if(gs.getTeams().length == 7 || gs.getTeams().length == 8){
      
    }

    return teams;
  }
  
  
  // ******************************
  // Helper methods for the hill-climbing in the spaced_out placement 
  // ******************************

  
  /**
   * Helper method for spaced placement
   * @author ysiebenh
   */
  static LinkedList<GameState> getNeighbors(GameState gs, int teamID, int[] startEnd) {

    LinkedList<GameState> result = new LinkedList<GameState>();

    for (int i = 0; i < gs.getTeams()[teamID].getPieces().length; i++) {
      if (teamID == 0) {
        gs.setCurrentTeam(teamID);
        for (int y = 0; y < gs.getGrid().length / 2; y++) {
          for (int x = 0; x < gs.getGrid()[y].length; x++) {

            if (gs.getGrid()[y][x].equals("")) {
              GameState newGs = deepCopyGameState(gs);
              newGs.getGrid()[newGs.getTeams()[teamID].getPieces()[i]
                  .getPosition()[0]][newGs.getTeams()[teamID].getPieces()[i].getPosition()[1]] = "";
              newGs.getTeams()[teamID].getPieces()[i].setPosition(new int[] {y, x});
              newGs.getGrid()[newGs.getTeams()[teamID].getPieces()[i]
                  .getPosition()[0]][newGs.getTeams()[teamID].getPieces()[i].getPosition()[1]] =
                      newGs.getTeams()[teamID].getPieces()[i].getId();
              newGs.setGrid(newGs.getGrid());
              result.add(newGs);
            }
          }
        }
      } else if (teamID == 1) {
        gs.setCurrentTeam(teamID);
        for (int y = gs.getGrid().length / 2; y < gs.getGrid().length; y++) {
          for (int x = 0; x < gs.getGrid()[y].length; x++) {

            if (gs.getGrid()[y][x].equals("")) {
              GameState newGs = deepCopyGameState(gs);
              newGs.getGrid()[newGs.getTeams()[teamID].getPieces()[i]
                  .getPosition()[0]][newGs.getTeams()[teamID].getPieces()[i].getPosition()[1]] = "";
              newGs.getTeams()[teamID].getPieces()[i].setPosition(new int[] {y, x});
              newGs.getGrid()[newGs.getTeams()[teamID].getPieces()[i]
                  .getPosition()[0]][newGs.getTeams()[teamID].getPieces()[i].getPosition()[1]] =
                      newGs.getTeams()[teamID].getPieces()[i].getId();
              newGs.setGrid(newGs.getGrid());
              result.add(newGs);
            }
          }
        }
      }
    }
    return result;
  }
  /**
   * helper for the spaced placement
   * @author ysiebenh
   * @return
   */
  static int valueOf(GameState gs, int teamID) {
    int result = 0;
    for (Piece p : gs.getTeams()[teamID].getPieces()) {
      result += getPossibleMoves(gs, p.getId()).size();
    }
    return result;
  }
  
  /**
   * helper for the spaced placement
   * @author ysiebenh
   * @return
   */
  static GameState getBestState(LinkedList<GameState> list, int teamID) {
    GameState current = list.getFirst();
    for (GameState gs : list) {
      if (valueOf(gs, teamID) > valueOf(current, teamID)) {
        current = gs;
      }
    }
    return current;
  }
  
  /**
   * Deep Copies a GameState (hopefully)
   * @author yannicksiebenhaar
   * @return
   */
  static GameState deepCopyGameState(GameState gs) {
    GameState newGs = new GameState();
    Team[] teams = new Team[gs.getTeams().length];
    int c = 0;
    for (Team t : gs.getTeams()) {
      Team newTeam = new Team();
      newTeam.setId(t.getId());
      Piece[] newPieces = new Piece[t.getPieces().length];
      int j = 0;
      for (Piece piece : t.getPieces()) {
        Piece newPiece = new Piece();
        newPiece.setId(piece.getId());
        newPiece.setPosition(piece.getPosition());
        newPiece.setTeamId(piece.getTeamId());
        newPiece.setDescription(piece.getDescription());
        newPieces[j] = newPiece;
        j++;
      }
      newTeam.setPieces(newPieces);
      teams[c++] = newTeam;
    }
    newGs.setTeams(teams);
    String[][] grid = new String[gs.getGrid().length][gs.getGrid()[0].length];
    int x = 0;
    int y = 0;
    for (String[] yAxis : gs.getGrid()) {
      y = 0;
      for (String value : yAxis) {

        grid[x][y] = value;
        y++;
      }
      x++;
    }
    newGs.setGrid(grid);
    newGs.setCurrentTeam(gs.getCurrentTeam());
    return newGs;
  }
  
  /**
   * Returns a List of the Pieces sorted by Strength 
   * @author ysiebenh
   * @return
   */
  static LinkedList<Piece> getStrongest(Piece[] pieces) {
    LinkedList<Piece> list = new LinkedList<Piece>();
    
    for(Piece p : pieces) {
      list.add(p);
    }
    Collections.sort(list, new EngineTools().new StrengthComparator());
    
    return list;
    
  }
  
  
  /**
   * Updates the positions of the pieces based on their position in the Team Object  
   * @author ysiebenh
   */
  static void updateGrid(GameState gs) {
    for (Team team : gs.getTeams()) {
      //grid[team.getBase()[0]][team.getBase()[1]] = "b:" + team.getId(); //Moved to InitPieces so the pieces will be placed around the base
      for (Piece piece : team.getPieces()) {
        gs.getGrid()[piece.getPosition()[0]][piece.getPosition()[1]] = piece.getId();
      }
    }
  }
  
  // ******************************
  // Inner Classes
  // ******************************

  class StrengthComparator implements Comparator <Piece>{
    
    @Override
    public int compare(Piece a, Piece b) {
        if(a.getDescription().getAttackPower() > b.getDescription().getAttackPower()) return -1;
        else if(a.getDescription().getAttackPower() < b.getDescription().getAttackPower()) return 1;
        else return 0;      
    }
}


}
