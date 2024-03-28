package org.ctf.ai;

import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Stream;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Piece;
import org.ctf.shared.state.data.map.Directions;
import org.ctf.shared.state.data.map.ShapeType;

/**
 * @author sistumpf This class includes useful methods to analyze and work with GameStates and
 *     Moves.
 */
public class AI_Tools {
  static Random random = new Random();
  
  /**
   * Returns the previous teams index in the team array.
   * @param gameState
   * @return previous teams index
   */
  public static int getPreviousTeam(GameState gameState) {
    for(int i=(gameState.getCurrentTeam()-1) % gameState.getTeams().length; ;i = (i - 1) % gameState.getTeams().length) {
      i = i<0? -1*i : i;
      if(gameState.getTeams()[i] != null) {
        return i;
      }
    }

  }
  
  /**
   * Switches a GameState current team to the next valid (not null) team.
   * @param gameState
   * @return altered gameState
   */
  public static GameState toNextTeam(GameState gameState) {
    for(int i=(gameState.getCurrentTeam()+1) % gameState.getTeams().length; ;i = (i + 1) % gameState.getTeams().length) {
      if(gameState.getTeams()[i] != null) {
        gameState.setCurrentTeam(i);
        return gameState;
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
   * This method is needed to respawn a piece, it adds all positions in a certain radius around the base to an Array.
   * @param xTrans
   * @param distance
   * @return Array containing Transformations to use on the base position
   */
  public static int[] fillXTransformations(int[] xTrans, int distance){
    int side = -1;
    for(int i=0; i<distance*8; i++) {
      if(i< 1+ distance*2)
        xTrans[i] = ++side - distance;
      else if(i< (distance*8)/2)
        xTrans[i] = distance;
      else if(i< (distance*8) - (2 * (distance-1)) - 1)
        xTrans[i] = side-- - distance;
      else  
        xTrans[i] = -1*distance;
    }
    
    return xTrans;
  }

  /**
   * This method is needed to respawn a piece, it adds all positions in a certain radius around the base to an Array.
   * @param yTrans
   * @param distance
   * @return Array containing Transformations to use on the base position
   */
  public static int[] fillYTransformations(int[] yTrans, int distance){
    int side = 0;
    for(int i=0; i<distance*8; i++) {
      if(i< 1+ distance*2)
        yTrans[i] = -1*distance;
      else if(i< (distance*8)/2)
        yTrans[i] = ++side - distance;
      else if(i< (distance*8) - (2 * (distance-1)) - 1)
        yTrans[i] = distance;
      else
        yTrans[i] = side-- - distance;
    }
    
    return yTrans;
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
    random.setSeed(seed);
    return random.nextInt(upperBound - lowerBound) + lowerBound;
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
  public static ArrayList<int[]> getPossibleMoves(GameState gameState, Piece piece, ArrayList<int[]> possibleMoves) {
    possibleMoves.clear();
    ArrayList<int[]> dirMap = new ArrayList<int[]>();
    if (piece.getDescription().getMovement().getDirections() == null) {
      try {
        getShapeMoves(gameState, piece, possibleMoves);
      } catch (InvalidShapeException e) {
        e.printStackTrace();
      }

    } else {
      dirMap = AI_Tools.createDirectionMap(gameState, piece, dirMap);
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
   * Selects and returns a random Move from an ArrayList which only contains valid Moves.
   *
   * @param positionArrayList
   * @param pieceId
   * @return randomly picked move
   */
  public static Move getRandomShapeMove(ArrayList<int[]> positionArrayList, String pieceId) {
    Move move = new Move();
    move.setPieceId(pieceId);
    move.setNewPosition(positionArrayList.get((int) (positionArrayList.size() * Math.random())));
    return move;
  }

  /**
   * Creates an ArrayList with all valid Moves a piece with shape movement can do.
   *
   * @param gameState
   * @param piece
   * @return ArrayList containing all valid moves
   * @throws InvalidShapeException if the Shape is not yet implemented here
   */
  public static ArrayList<int[]> getShapeMoves(GameState gameState, Piece piece, ArrayList<int[]> positions)
      throws InvalidShapeException {
    positions.clear();
    int[] xTransforms;
    int[] yTransforms;
    int[] direction;

    if (piece.getDescription().getMovement().getShape().getType() == ShapeType.lshape) {
      // transforms go left-down-right-up, first 12 outer layer, then inner layer
      xTransforms =
          new int[] {-2, -2, -2, -1, 0, 1, 2, 2, 2, 1, 0, -1, /*inner layer*/ -1, 0, 1, 0};
      yTransforms =
          new int[] {-1, 0, 1, 2, 2, 2, 1, 0, -1, -2, -2, -2, /*inner layer*/ 0, 1, 0, -1};
      direction = new int[] {0, 0, 0, 3, 3, 3, 1, 1, 1, 2, 2, 2};
    } else {
      throw new InvalidShapeException(
          piece.getDescription().getMovement().getShape().getType().toString());
    }

    for (int i = 0; i < xTransforms.length; i++) {
      int[] newPos =
          new int[] {
              piece.getPosition()[0] + yTransforms[i], piece.getPosition()[1] + xTransforms[i]
      };
      if (validPos(newPos, piece, gameState)) {
        if (i >= direction.length) {
          positions.add(newPos);
        } else if (sightLine(
            gameState,
            new int[] {
                piece.getPosition()[0] + yTransforms[(1 + (i / 3) * 3)],
                piece.getPosition()[1] + xTransforms[(1 + (i / 3) * 3)]
            },
            direction[i],
            2)) {
          positions.add(newPos);
        }
      }
    }
    return positions;
  }

  /**
   * Creates an ArrayList containing all a pieces valid directions and its maximum reach into that direction in int[direction, reach] pairs.
   * This map only applies for the Piece picked. The reach value is directly
   * from MapTemplate, this method only checks if the positions adjacent to a piece are occupied.
   *
   * @param gameState
   * @param picked
   * @return ArrayList<int[direction,reach]>
   */
  public static ArrayList<int[]> createDirectionMap(GameState gameState, Piece picked, ArrayList<int[]> dirMap) {
    dirMap.clear();
    for (int i = 0; i < 8; i++) {
      int reach = getReach(picked.getDescription().getMovement().getDirections(), i);
      if (reach > 0) {
        if (validDirection(gameState, picked, i)) {
          dirMap.add(new int[] {i, reach});
        } else {
          continue;
        }
      }
    }
    return dirMap;
  }

  /**
   * Returns a Move from a given HashMap of possible directions and and their reach to move in. This
   * method picks a random dirction-reach pair and returns a Move to this position using {@link
   * #checkMoveValidity(GameState gameState, Piece piece, int direction, int reach)}. If the
   * position is invalid this process is tried again till a valid move is generated. If a random
   * position is invalid the HashMap reach value is lowered to ensure the same position is not
   * picked again. This method assumes the HashMap contains elements and all directions contain at
   * least 1 valid position.
   *
   * @param dirMap
   * @param piece
   * @param gameState
   * @return a valid move
   */
  public static Move getDirectionMove(ArrayList<int[]> dirMap, Piece piece, GameState gameState) {
    int randomDir = (int) (dirMap.size() * Math.random());
    int reach;

    while (true) {
      reach = (int) (Math.random() * dirMap.get(randomDir)[1] + 1);
      Move newPos = checkMoveValidity(gameState, piece, dirMap.get(randomDir)[0], reach);
      if (newPos != null) return newPos;
        dirMap.get(randomDir)[1] =  reach - 1;
      continue;
    }
  }

  /**
   * This method tests if a piece could walk into a given direction. It does not test if a pieces
   * reach in a direction is >0. The direction is given as an int (0-7).
   *
   * @param gameState
   * @param piece
   * @param direction
   * @return false if there are no possible moves in this direction, true otherwise.
   */
  public static boolean validDirection(GameState gameState, Piece piece, int direction) {
    return checkMoveValidity(gameState, piece, direction, 1) != null;
  }

  /**
   * Returns the Move if a piece can occupy specific position. This method does not test if a pieces
   * reach in a direction is >0. The direction is given as an int (0-7) and reach as an int that
   * specifies how many fields into that direction.
   *
   * @param gameState
   * @param piece
   * @param direction
   * @param reach
   * @return a Move instance with the piece and its new position
   * @return null if the piece can't occupy the position or the position is not in the grid
   */
  public static Move checkMoveValidity(GameState gameState, Piece piece, int direction, int reach) {
    int[] pos = new int[] {piece.getPosition()[0], piece.getPosition()[1]};
    updatePos(pos, direction, reach);

    if (!validPos(pos, piece, gameState)) {
      return null;
    } else if (!sightLine(gameState, new int[] {pos[0], pos[1]}, direction, reach)) {
      return null;
    } else {
      Move move = new Move();
      move.setPieceId(piece.getId());
      move.setNewPosition(pos);
      return move;
    }
  }

  /**
   * Checks if two positions have a direct line of sight. The line of sight can be disrupted by
   * pieces, blocks or bases in between them. The old position is calculated from the new Position
   * minus the reach in the negative direction the piece took to get to newPos. In simpler words, if
   * a piece went from 2,2 to 2,0 (2 to left) newPos would be [2,0], reach would be 2 and the
   * direction 0 (left)
   *
   * @param gameState
   * @param newPos
   * @param direction
   * @param reach
   * @return true if there is no obstacle in between
   * @return false if any obstacle is in between or the target position is not on the grid
   */
  public static boolean sightLine(GameState gameState, int[] newPos, int direction, int reach) {
    --reach;
    for (; reach > 0; reach--) {
      newPos = updatePos(newPos, direction, -1);
      try {
        if (gameState.getGrid()[newPos[0]][newPos[1]].equals("")) {
          continue;
        } else {
          return false;
        }
      } catch (IndexOutOfBoundsException ioe) {
        return false;
      }
    }
    return true;
  }

  /**
   * Updates the y,x position of a piece. A given int[2] positional Array is altered by going a
   * given amount of steps (reach) into a given direction.
   *
   * @param pos
   * @param direction
   * @param reach
   * @return updated position
   */
  public static int[] updatePos(int[] pos, int direction, int reach) {
    switch (direction) {
      case 0:
        pos[1] -= reach;
        break; // left
      case 1:
        pos[1] += reach;
        break; // right
      case 2:
        pos[0] -= reach;
        break; // up
      case 3:
        pos[0] += reach;
        break; // down
      case 4:
        pos[1] -= reach;
        pos[0] -= reach;
        break; // left Up
      case 5:
        pos[1] += reach;
        pos[0] -= reach;
        break; // right Up
      case 6:
        pos[1] -= reach;
        pos[0] += reach;
        break; // left Down
      case 7:
        pos[1] += reach;
        pos[0] += reach;
        break; // right Down
    }
    return pos;
  }

  /**
   * Checks if a piece can occupy a given position.
   *
   * @param pos
   * @param piece
   * @param gameState
   * @return true if the position can be occupied.
   */
  public static boolean validPos(int[] pos, Piece piece, GameState gameState) {
    // checks if the position can be occupied
    if (positionOutOfBounds(gameState.getGrid(), pos)) return false;
    if (emptyField(gameState.getGrid(), pos)) return true;
    if (occupiedByBlock(gameState.getGrid(), pos)) return false;
    if (occupiedBySameTeam(gameState, pos)) return false;
    if (otherTeamsBase(gameState.getGrid(), pos, piece)) return true;
    if (occupiedByWeakerOpponent(gameState, pos, piece)) return true;

    // if opponent is stronger or something unforeseen happens
    return false;
  }

  /**
   * Checks if a position is not contained in the grid.
   *
   * @param grid
   * @param pos
   * @return true if the position is out of bounds
   */
  public static boolean positionOutOfBounds(String[][] grid, int[] pos) {
    return (pos[0] < 0 || pos[1] < 0 || pos[0] >= grid.length || pos[1] >= grid[0].length);
  }

  /**
   * Checks if a position on the grid contains an empty String.
   *
   * @param grid
   * @param pos
   * @return true if the position is an empty Field "" and can be occupied
   */
  public static boolean emptyField(String[][] grid, int[] pos) {
    return grid[pos[0]][pos[1]].equals("");
  }

  /**
   * Checks if a position on the grid contains a block.
   *
   * @param grid
   * @param pos
   * @return true if the position is occupied by a block and cannot be walked on
   */
  public static boolean occupiedByBlock(String[][] grid, int[] pos) {
    return grid[pos[0]][pos[1]].equals("b");
  }

  /**
   * Checks if a position on the grid is occupied by a piece from the current team.
   *
   * @param grid
   * @param pos
   * @return true if the position is occupied by a Piece of the same Team
   */
  public static boolean occupiedBySameTeam(GameState gameState, int[] pos) {
    return gameState.getCurrentTeam() == getOccupantTeam(gameState.getGrid(), pos);
  }

  /**
   * Checks if a position on the grid is occupied by a piece with a weaker or the same AttackPower
   * as a given piece.
   *
   * @param gameState
   * @param pos
   * @param picked
   * @return true if the position is occupied by a weaker opponent that can be captured
   */
  public static boolean occupiedByWeakerOpponent(GameState gameState, int[] pos, Piece picked) {
    for (Piece p :
      gameState
      .getTeams()[getOccupantTeam(gameState.getGrid(), pos)]
          .getPieces()) {
      if (p.getId().equals(gameState.getGrid()[pos[0]][pos[1]])) {
        if (p.getDescription().getAttackPower() <= picked.getDescription().getAttackPower()) {
          return true;
        } else {
          return false;
        }
      }
    }
    return false;
  }

  /**
   * Checks if a position on the grid is occupied by an opponents base.
   *
   * @param grid
   * @param pos
   * @param picked
   * @return true if the position is occupied by another teams base and a flag can be captured
   */
  public static boolean otherTeamsBase(String[][] grid, int[] pos, Piece picked) {
    if (grid[pos[0]][pos[1]].contains("b:")) {
      if(Integer.parseInt(picked.getTeamId()) != getOccupantTeam(grid, pos))
        return true;
    }
    return false;
  }
  
  /**
   * This method returns an occupants (piece/base) team.
   * It splits it Id and parses the enclosed TeamId to Integer.
   * @param gameState
   * @param pos
   * @return
   */
  public static int getOccupantTeam(String[][] grid, int[] pos) {
    StringBuilder sb = new StringBuilder().append(grid[pos[0]][pos[1]]);
    int indexUnderscore = sb.indexOf("_");
    return Integer.parseInt(sb.substring(sb.indexOf(":")+1, indexUnderscore == -1 ? sb.length() : indexUnderscore));
  } 


  /**
   * Returns a pieces maximum reach into a certain direction. Assumes the direction is valid,
   * doesn't catch Null Pointer Exceptions.
   *
   * @param directions
   * @param dir
   * @return reach into direction dir
   */
  public static int getReach(Directions directions, int dir) {
    switch (dir) {
      case 0:
        return directions.getLeft();
      case 1:
        return directions.getRight();
      case 2:
        return directions.getUp();
      case 3:
        return directions.getDown();
      case 4:
        return directions.getUpLeft();
      case 5:
        return directions.getUpRight();
      case 6:
        return directions.getDownLeft();
      case 7:
        return directions.getDownRight();
      default:
        return -1;
    }
  }

  /** Gets thrown if the current team cannot move. */
  public static class NoMovesLeftException extends Exception {
    private static final long serialVersionUID = -5045376294141974451L;

    public NoMovesLeftException(String team) {
      super("Team " + team + " can not move.");
    }
  }

  /** Gets thrown if a Shape is not yet implemented in RandomAI. */
  public static class InvalidShapeException extends Exception {
    private static final long serialVersionUID = -574558731715073847L;

    public InvalidShapeException(String shape) {
      super("Unknown shape: " + shape);
    }
  }
}