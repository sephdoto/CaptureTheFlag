package de.unimannheim.swt.pse.ctf.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import de.unimannheim.swt.pse.ctf.game.map.MapTemplate;
import de.unimannheim.swt.pse.ctf.game.state.GameState;
import de.unimannheim.swt.pse.ctf.game.state.Move;
import de.unimannheim.swt.pse.ctf.game.state.Piece;
import de.unimannheim.swt.pse.ctf.game.state.Team;

/**
 * This class contains usefull methods for GameEngine to clean it up.
 * @author sistumpf
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
   * Helper method for spaced placement
   * @author yannicksiebenhaar
   */
  public static LinkedList<GameState> getNeighbors(GameState gs, int teamID, int[] startEnd){
	  
	  LinkedList<GameState> result = new LinkedList<GameState>();
	  
	  for(int i = 0; i < gs.getTeams()[teamID].getPieces().length; i++) {
		  GameState newGs = new GameState();
		  Team[] teams = new Team[gs.getTeams().length];
		  int c = 0;
		  for(Team t : gs.getTeams()) {
			  Team newTeam = new Team();
			  newTeam.setId(t.getId());
			  Piece[] newPieces = new Piece[t.getPieces().length];
			  int j = 0;
			  for(Piece piece : t.getPieces()) {
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
		  for( String[] yAxis : gs.getGrid()) {
			  y = 0;
			  for(String value : yAxis) {
				 
				  grid[x][y] = value; 
				  y++;
			  }
			  x++;
		  }
		  
		  newGs.setCurrentTeam(gs.getCurrentTeam());
		  int newY = 0;
		  int newX = 0;
		  if(teamID == 0) {
			  do {
				  newY = (int) (Math.random() * newGs.getGrid().length/2);
				  newX = (int) (Math.random() * newGs.getGrid()[0].length);
			  	  } while(!grid[newY][newX].equals(""));
		  }
		  else if(teamID == 1) {
			  do {
				  newY = (int) (Math.random() * newGs.getGrid().length/2 * 2);
				  newX = (int) (Math.random() * newGs.getGrid()[0].length);
				  } while(!grid[newY][newX].equals(""));
		  }
		  System.out.println(newY); //TODO why no 0
		  grid[newGs.getTeams()[teamID].getPieces()[i].getPosition()[0]][newGs.getTeams()[teamID].getPieces()[i].getPosition()[1]] = "";
		  newGs.getTeams()[teamID].getPieces()[i].setPosition(new int[] {newY, newX});
		  newGs.setGrid(grid);
		  result.add(newGs);
		  
		  for (Team team : newGs.getTeams()) {
		      //grid[team.getBase()[0]][team.getBase()[1]] = "b:" + team.getId(); //Moved to InitPieces so the pieces will be placed around the base
		      for (Piece piece : team.getPieces()) {
		        grid[piece.getPosition()[0]][piece.getPosition()[1]] = piece.getId();
		      }
		    }
	  }
	  return result;
  }
  /**
   * helper for the spaced placement
   * @author yannicksiebenhaar
   * @return
   */
  public static int valueOf(GameState gs, int teamID) {
	  int result = 0;
	  for(Piece p : gs.getTeams()[teamID].getPieces()) {
		  result += getPossibleMoves(gs, p.getId()).size();
	  }
	  return result;
  }
  
  public static GameState getBestState(LinkedList<GameState> list, int teamID) {
	  GameState current = list.getFirst();
	  for( GameState gs : list) {
		  if(valueOf(gs, teamID) > valueOf(current, teamID)) {
			  current = gs;
		  }
	  }
	  return current;
  }
  
}
