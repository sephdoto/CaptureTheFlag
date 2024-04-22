package de.unimannheim.swt.pse.ctf.game;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.unimannheim.swt.pse.ctf.game.state.GameState;
import de.unimannheim.swt.pse.ctf.game.state.Move;
import de.unimannheim.swt.pse.ctf.game.state.Piece;
import de.unimannheim.swt.pse.ctf.game.state.Team;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

/**
 * This class contains useful methods for the GameEngine.
 */
public class EngineTools extends AI_Tools {
  /**
   * A given move is made on the GameState.
   * Only updates the Team array, the Grid and the last move.
   * Checks for removing teams are not made here.
   * 
   * @author sistumpf
   * @param gameState
   * @param move
   */
  public static void computeMove(GameState gameState, Move move) {
    String occupant = gameState.getGrid()[move.getNewPosition()[0]][move.getNewPosition()[1]];
    Piece picked =
        Arrays.asList(gameState.getTeams()[gameState.getCurrentTeam()].getPieces()).stream()
            .filter(p -> p.getId().equals(move.getPieceId()))
            .findFirst()
            .get();
    int[] oldPos = picked.getPosition();

    gameState.getGrid()[oldPos[0]][oldPos[1]] = "";

    if (occupant.contains("p:")) {
      int occupantTeam = Integer.parseInt(occupant.split(":")[1].split("_")[0]);
      gameState.getTeams()[occupantTeam].setPieces(
          Arrays.asList(gameState.getTeams()[occupantTeam].getPieces()).stream()
              .filter(p -> !p.getId().equals(occupant))
              .toArray(Piece[]::new));
      gameState.getGrid()[move.getNewPosition()[0]][move.getNewPosition()[1]] = move.getPieceId();
      picked.setPosition(move.getNewPosition());
    } else if (occupant.contains("b:")) {
      int occupantTeam = Integer.parseInt(occupant.split(":")[1].split("_")[0]);
      gameState.getTeams()[occupantTeam].setFlags(
          gameState.getTeams()[occupantTeam].getFlags() - 1);
      picked.setPosition(
          EngineTools.respawnPiecePosition(
              gameState, gameState.getTeams()[gameState.getCurrentTeam()].getBase()));
      gameState.getGrid()[picked.getPosition()[0]][picked.getPosition()[1]] = picked.getId();
    } else {
      gameState.getGrid()[move.getNewPosition()[0]][move.getNewPosition()[1]] = move.getPieceId();
      picked.setPosition(move.getNewPosition());
    }

    gameState.setLastMove(move);
  }
  
  /**
   * Starting from the current team, the following teams which cannot move get removed.
   *
   * @author sistumpf
   * @param gameState
   * @return true if only one team is left
   */
  public static boolean removeMovelessTeams(GameState gameState) {
    for(int i=0; i < gameState.getTeams().length && gameState.getTeams()[i] != null; i++) {
      if(gameState.getTeams()[i].getFlags() <= 0)
        removeTeam(gameState, i);
      else if(gameState.getTeams()[i].getPieces().length == 0)
        removeTeam(gameState, i);
    }
    
    while (numberOfTeamsLeft(gameState) > 1
        && !teamGotMovesLeft(gameState, gameState.getCurrentTeam())) {
      removeTeam(gameState, gameState.getCurrentTeam()); // removed and set to null
      gameState.setCurrentTeam(getNextTeam(gameState));
    }
    return numberOfTeamsLeft(gameState) <= 1 ? true : false;
  }

  /**
   * This method returns how many Teams in a GameState are not null (= still playing)
   *
   * @author sistumpf
   * @param gameState
   * @return number of teams left
   */
  public static int numberOfTeamsLeft(GameState gameState) {
    int i = gameState.getTeams().length;
    for (int j = i - 1; j >= 0; i--, j--) if (gameState.getTeams()[j] != null) i++;
    return i;
  }

  /**
   * Inefficient way for checking if a team got moves left but should be okay for GameEngine
   *
   * @author sistumpf
   * @param gameState
   * @param teamIndex
   * @return true if the team got moves left
   */
  public static boolean teamGotMovesLeft(GameState gameState, int teamIndex) {
    for (int i = gameState.getTeams()[teamIndex].getPieces().length - 1; i >= 0; i--) {
      if (getPossibleMoves(gameState, gameState.getTeams()[teamIndex].getPieces()[i]).size()
          > 0) return true;
    }
    return false;
  }

  /**
   * Returns a GameStates next valid (not null) team.
   *
   * @param gameState
   * @return next Team != null
   * @author sistumpf
   */
  public static int getNextTeam(GameState gameState) {
    for (int i = (gameState.getCurrentTeam() + 1) % gameState.getTeams().length;
        ;
        i = (i + 1) % gameState.getTeams().length) {
      if (gameState.getTeams()[i] != null) {
        return i;
      }
    }
  }

  /**
   * Returns a GameStates next valid (null) team.
   * !! only use when teams join, otherwise the output might not be correct !!
   * @param gameState
   * @return next Team == null
   * @author rsyed
   */
  public static int getNextEmptyTeamSlot(GameState gameState) {
    int ret = 0;
    for (int i = 0; i < gameState.getTeams().length; i++) {
      if(gameState.getTeams()[i] == null){
        ret = i;
      }
    }
    return ret;
  }

  /**
   * Removes a certain team from the GameState. team is the place of the team in the GameState.getTeams Array.
   *
   * @param gameState
   * @param team
   * @author sistumpf
   */
  public static void removeTeam(GameState gameState, int team) {
    gameState
            .getGrid()[gameState.getTeams()[team].getBase()[0]][
            gameState.getTeams()[team].getBase()[1]] =
        "";
    for (Piece p : gameState.getTeams()[team].getPieces())
      gameState.getGrid()[p.getPosition()[0]][p.getPosition()[1]] = "";
    gameState.getTeams()[team] = null;
  }

  /**
   * This method should be used instead of Math.random() to generate deterministic positive pseudo
   * random values. Changing modifier changes the resulting output for the same seed.
   *
   * @author sistumpf
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
   *
   * @author sistumpf
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
          for (int i = 1, random = seededRandom(gameState.getGrid(), i, xTransforms.length, 0);
              ;
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
   * @author sistumpf
   * @param GameState gameState
   * @param String pieceID
   * @return ArrayList<int[]> that contains all valid positions a piece could move to
   */
  public static ArrayList<int[]> getPossibleMoves(GameState gameState, Piece piece) {
    ArrayList<int[]> possibleMoves = new ArrayList<int[]>();
    ArrayList<int[]> dirMap = new ArrayList<int[]>();
    if (piece.getDescription().getMovement().getDirections() == null) {
      try {
        return getShapeMoves(gameState, piece);
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
          } catch (Exception e) {
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
   * helper for the spaced placement
   *
   * @author rsyed
   * @return
   */
  public static GameState deepCopyGameState(GameState gs) {
    ObjectMapper mapper = new ObjectMapper();
    GameState re = new GameState();
    try {
      re = mapper.readValue(mapper.writeValueAsString(gs), GameState.class);
    } catch (JsonGenerationException e) {
      System.out.println("Error in deepCopyGameState JSON Method");
    } catch (JsonMappingException e) {
      System.out.println("Error in deepCopyGameState JSON Method");
    } catch (IOException e) {
      System.out.println("Error in deepCopyGameState JSON Method");
    }
    return re;
  }

  /**
   * Updates the positions of the pieces based on their position in the Team Object
   *
   * @author ysiebenh, sistumpf
   */
  static void updateGrid(GameState gs) {
    for (Team team : gs.getTeams()) {
      for (Piece piece : team.getPieces()) {
        if(!positionOutOfBounds(gs.getGrid(), piece.getPosition()))
          gs.getGrid()[piece.getPosition()[0]][piece.getPosition()[1]] = piece.getId();
      }
    }
  }
}
