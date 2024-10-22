package org.ctf.shared.ai.mcts3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import org.ctf.shared.ai.GameUtilities.InvalidShapeException;
import org.ctf.shared.ai.GameUtilities.NoMovesLeftException;
import org.ctf.shared.ai.ReferenceMove;
import org.ctf.shared.state.Piece;
import org.ctf.shared.state.data.map.Directions;
import org.ctf.shared.state.data.map.ShapeType;

/**
 * An optimized version of GameUtilities, adjusted to use the new Grid.
 *
 * @author sistumpf
 */
public class MCTSUtilities {
  static Random random = new Random();

  static HashMap<Integer, int[]> directionModifiers;
  static{
    directionModifiers = new HashMap<Integer, int[]>();
    directionModifiers.put(0, new int[] {0, -1});
    directionModifiers.put(1, new int[] {0, 1});
    directionModifiers.put(2, new int[] {-1, 0});
    directionModifiers.put(3, new int[] {1, 0});
    directionModifiers.put(4, new int[] {-1, -1});
    directionModifiers.put(5, new int[] {-1, 1});
    directionModifiers.put(6, new int[] {1, -1});
    directionModifiers.put(7, new int[] {1, 1});
  }

  /**
   * Returns the previous teams index in the team array.
   *
   * @param gameState
   * @return previous teams index
   */
  public static int getPreviousTeam(ReferenceGameState gameState) {
    int team = gameState.getCurrentTeam() -1 < 0 ?
        gameState.getTeams().length - 1 : gameState.getCurrentTeam() -1;
    while(gameState.getTeams()[team] == null) {
      team = team -1 < 0 ?
          gameState.getTeams().length - 1 : team -1;
    }
    return team;
  }

  /**
   * Switches a ReferenceGameState current team to the next valid (not null) team.
   *
   * @param gameState
   * @return altered gameState
   */
  public static ReferenceGameState toNextTeam(ReferenceGameState gameState) {
    int teams=gameState.getTeams().length;
    for (int i = (gameState.getCurrentTeam() + 1) % gameState.getTeams().length;
        teams > 0;
        i = (i + 1) % gameState.getTeams().length) {
      if (gameState.getTeams()[i] != null) {
        gameState.setCurrentTeam(i);
        return gameState;
      }
      --teams;
    }
    return gameState;
  }

  /**
   * Removes a certain team from the ReferenceGameState. team is the place of the team in the
   * ReferenceGameState.getTeams Array.
   *
   * @param gameState
   * @param team
   */
  public static void removeTeam(ReferenceGameState gameState, int team) {
    gameState.getGrid().getGrid()
    [gameState.getTeams()[team].getBase()[0]] 
        [gameState.getTeams()[team].getBase()[1]]
            = null;

    for (Piece p : gameState.getTeams()[team].getPieces())
      gameState.getGrid().getGrid()[p.getPosition()[0]][p.getPosition()[1]]
          = null;
    gameState.getTeams()[team] = null;
  }

  /**
   * Returns a valid position on which a Piece can safely respawn.
   *
   * @param grid to generate pseudo random numbers and access the grid
   * @param basePos the position of the base of the Piece that gets respawned
   * @return valid position to respawn a piece on, null shouldn't be returned.
   */
  public static int[] respawnPiecePosition(Grid grid, int[] basePos) {
    int[] xTransforms;
    int[] yTransforms;

    for (int distance = 1; distance < grid.getGrid().length; distance++) {
      xTransforms = fillXTransformations(new int[distance * 8], distance);
      yTransforms = fillYTransformations(new int[distance * 8], distance);

      for (int clockHand = 0; clockHand < distance * 8; clockHand++) {
        int x = basePos[1] + xTransforms[clockHand];
        int y = basePos[0] + yTransforms[clockHand];
        int[] newPos = new int[] {y, x};
        if (positionOutOfBounds(grid, newPos)) continue;

        if (emptyField(grid, newPos)) {
          for (int i = 1, random = seededRandom(grid, i, xTransforms.length, 0);
              ;
              i++, random = seededRandom(grid, i, xTransforms.length, 0)) {
            x = basePos[1] + xTransforms[random];
            y = basePos[0] + yTransforms[random];
            newPos = new int[] {y, x};
            if (positionOutOfBounds(grid, newPos)) continue;
            if (emptyField(grid, newPos)) return newPos;
          }
        }
      }
    }
    return null;
  }

  /**
   * This method is needed to respawn a piece, it adds all positions in a certain radius around the
   * base to an Array.
   *
   * @param xTrans translations on x-axis
   * @param distance from base
   * @return Array containing Transformations to use on the base position
   */
  public static int[] fillXTransformations(int[] xTrans, int distance) {
    int side = -1;
    for (int i = 0; i < distance * 8; i++) {
      if (i < 1 + distance * 2) xTrans[i] = ++side - distance;
      else if (i < (distance * 8) / 2) xTrans[i] = distance;
      else if (i < (distance * 8) - (2 * (distance - 1)) - 1) xTrans[i] = side-- - distance;
      else xTrans[i] = -1 * distance;
    }

    return xTrans;
  }

  /**
   * This method is needed to respawn a piece, it adds all positions in a certain radius around the
   * base to an Array.
   *
   * @param yTrans translations on y-axis
   * @param distance from base
   * @return Array containing Transformations to use on the base position
   */
  public static int[] fillYTransformations(int[] yTrans, int distance) {
    int side = 0;
    for (int i = 0; i < distance * 8; i++) {
      if (i < 1 + distance * 2) yTrans[i] = -1 * distance;
      else if (i < (distance * 8) / 2) yTrans[i] = ++side - distance;
      else if (i < (distance * 8) - (2 * (distance - 1)) - 1) yTrans[i] = distance;
      else yTrans[i] = side-- - distance;
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
  public static int seededRandom(Grid grid, int modifier, int upperBound, int lowerBound) {
    StringBuilder sb = new StringBuilder();
    Stream.of(grid.getGrid())
    .forEach(s -> Stream.of(s).forEach(ss -> sb.append(ss == null ? "" : ss.toString())));
    int seed = sb.append(modifier).toString().hashCode();
    random.setSeed(seed);
    return random.nextInt(upperBound - lowerBound) + lowerBound;
  }

  /**
   * The old getPossibleMoves method, which takes a pieceId String instead of a piece.
   * It finds the piece in the gameState, then calls the new getPossibleMoves method.
   * @param gameState
   * @param pieceId
   * @param possibleMoves
   * @return ArrayList<int[]> that contains all valid positions a piece could move to
   */
  public static ArrayList<int[]> getPossibleMoves(
      ReferenceGameState gameState, String pieceId, ArrayList<int[]> possibleMoves) {
    Piece piece = 
        gameState.getTeams()[Integer.parseInt(pieceId.split(":")[1].split("_")[0])].getPieces()
        .stream()
        .filter(p -> p.getId().equals(pieceId))
        .findFirst()
        .get();
    return getPossibleMoves(gameState, piece, possibleMoves, new ReferenceMove(null, new int[] {0,0}));
  }

  /**
   * Given a Piece and a ReferenceGameState containing the Piece, an ArrayList with all valid locations the
   * Piece can walk on is returned. The ArrayList contains int[2] values, representing a (y,x)
   * location on the grid.
   *
   * @param gameState
   * @param piece
   * @param possibleMoves, will be cleared and filled
   * @param change a Reference move that gets altered instead of creating and abandoning a new object
   * @return arraylist that contains all valid positions a piece could move to
   */
  public static ArrayList<int[]> getPossibleMoves(
      ReferenceGameState gameState, Piece piece, ArrayList<int[]> possibleMoves, ReferenceMove change) {
    possibleMoves.clear();
    ArrayList<int[]> dirMap = new ArrayList<int[]>();
    if (piece.getDescription().getMovement().getDirections() == null) {
      try {
        possibleMoves.addAll(getShapeMoves(gameState, piece, possibleMoves));
      } catch (InvalidShapeException e) {
        e.printStackTrace();
      }

    } else {
      dirMap = createDirectionMap(gameState, piece, dirMap, change);
      for (int[] entry : dirMap) {
        for (int reach = entry[1]; reach > 0; reach--) {
          change = checkMoveValidity(gameState, piece, entry[0], reach, change);
          if (change.getPiece() != null) possibleMoves.add(change.getNewPosition());
        }
      }
    }
    return possibleMoves;
  }

  /**
   * Selects and returns a random Move from an ArrayList which only contains valid Moves.
   *
   * @param positionArrayList
   * @param piece
   * @param change a Reference move that gets altered instead of creating and abandoning a new object
   * @return randomly picked move
   */
  public static ReferenceMove getRandomShapeMove(ArrayList<int[]> positionArrayList, Piece piece, ReferenceMove change) {
    change.setPiece(piece);
    change.setNewPosition(positionArrayList.get(ThreadLocalRandom.current().nextInt(positionArrayList.size())));
    return change;
  }

  /**
   * Creates an ArrayList with all valid Moves a piece with shape movement can do.
   *
   * @param gameState
   * @param piece
   * @param positions an ArrayList containing all valid moves
   * @return arraylist with all shape moves
   * @throws InvalidShapeException if the Shape is not yet implemented here
   */
  public static ArrayList<int[]> getShapeMoves(
      ReferenceGameState gameState, Piece piece, ArrayList<int[]> positions) throws InvalidShapeException {
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
        } else if ((i + 2) % 3 != 0) {
          if(sightLine(
              gameState,
              new int[] {
                  piece.getPosition()[0] + yTransforms[12+ (i / 3)] + yTransforms[1 + ((i / 3) * 3)],
                  piece.getPosition()[1] + xTransforms[12+ (i / 3)] + xTransforms[1 + ((i / 3) * 3)]
              },
              direction[i],
              3)) {
            positions.add(newPos);
          }
        } else if ((i + 2) % 3 == 0){
          if(sightLine(
              gameState,
              new int[] {
                  piece.getPosition()[0] + yTransforms[i],
                  piece.getPosition()[1] + xTransforms[i]
              },
              direction[i],
              2)) {
            positions.add(newPos);
          } 
        }
      }
    }
    return positions;
  }

  /**
   * Creates an ArrayList containing all a pieces valid directions and its maximum reach into that
   * direction in int[direction, reach] pairs. This map only applies for the Piece picked. The reach
   * value is directly from MapTemplate, this method only checks if the positions adjacent to a
   * piece are occupied.
   *
   * @param gameState
   * @param picked
   * @param dirMap, will be cleared and filled
   * @param change a Reference move that gets altered instead of creating and abandoning a new object
   * @return ArrayList<int[direction,reach]>
   */
  public static ArrayList<int[]> createDirectionMap(
      ReferenceGameState gameState, Piece picked, ArrayList<int[]> dirMap, ReferenceMove change) {
    dirMap.clear();
    for (int i = 0; i < 8; i++) {
      int reach = getReach(picked.getDescription().getMovement().getDirections(), i);
      if (reach > 0) {
        if (validDirection(gameState, picked, i, change)) {
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
   * method picks a random dirction-reach pair and returns a Move to this position using 
   * checkMoveValidity. If the
   * position is invalid this process is tried again till a valid move is generated. If a random
   * position is invalid the HashMap reach value is lowered to ensure the same position is not
   * picked again. This method assumes the HashMap contains elements and all directions contain at
   * least 1 valid position.
   *
   * @param dirMap
   * @param piece
   * @param gameState
   * @param change a Reference move that gets altered instead of creating and abandoning a new object
   * @return a valid move
   */
  public static ReferenceMove getDirectionMove(ArrayList<int[]> dirMap, Piece piece, ReferenceGameState gameState, ReferenceMove change) {
    int randomDir = ThreadLocalRandom.current().nextInt(dirMap.size());
    int reach;

    while (true) {
      reach = ThreadLocalRandom.current().nextInt(dirMap.get(randomDir)[1]) + 1;
      change = checkMoveValidity(gameState, piece, dirMap.get(randomDir)[0], reach, change);
      if (change.getPiece() != null) return change;
      dirMap.get(randomDir)[1] = reach - 1;
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
   * @param change a Reference move that gets altered instead of creating and abandoning a new object
   * @return false if there are no possible moves in this direction, true otherwise.
   */
  public static boolean validDirection(ReferenceGameState gameState, Piece piece, int direction, ReferenceMove change) {
    //  return checkMoveValidity(gameState, piece, direction, 1, change).getPiece() != null;
    int[] pos = new int[] {piece.getPosition()[0], piece.getPosition()[1]};
    updatePos(pos, direction, 1);
    return validPos(pos, piece, gameState);
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
   * @param change a Reference move that gets altered instead of creating and abandoning a new object
   * @return a Move instance with the piece and its new position
   * @return null if the piece can't occupy the position or the position is not in the grid
   */
  public static ReferenceMove checkMoveValidity(ReferenceGameState gameState, Piece piece, int direction, int reach, ReferenceMove change) {
    int[] pos = new int[] {piece.getPosition()[0], piece.getPosition()[1]};
    updatePos(pos, direction, reach);
    change.setPiece(null);

    if (!validPos(pos, piece, gameState)) {
      return change;
    } else if (!sightLine(gameState, new int[] {pos[0], pos[1]}, direction, reach)) {
      return change;
    } else {
      change.setNewPosition(pos);
      change.setPiece(piece);
      return change;
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
  public static boolean sightLine(
      ReferenceGameState gameState, int[] newPos, int direction, int reach) {
    --reach;
    for (; reach > 0; reach--) {
      updatePos(newPos, direction, -1);
      try {
        if (gameState.getGrid().getGrid()[newPos[0]][newPos[1]] == null) {
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
    int[] modifier = directionModifiers.get(direction);
    pos[0] += modifier[0] * reach;
    pos[1] += modifier[1] * reach;
    return pos;
  }

  /**
   * Checks if a piece can occupy a given position. Does not check sightLine()
   *
   * @param pos
   * @param piece
   * @param gameState
   * @return true if the position can be occupied.
   */
  public static boolean validPos(int[] pos, Piece piece, ReferenceGameState gameState) {
    // checks if the position can be occupied
    try {
      if (emptyField(gameState.getGrid(), pos)) return true;
      if (occupiedByBlock(gameState.getGrid(), pos)) return false;
      if (occupiedBySameTeam(gameState, piece.getPosition(), pos)) return false;
      if (otherTeamsBase(gameState.getGrid(), pos, piece.getPosition())) return true;
      if (occupiedByWeakerOpponent(gameState.getGrid().getPosition(pos[1], pos[0]).getPiece(), piece))
        return true;
    } catch(IndexOutOfBoundsException iob) {
      return false;
    }

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
  public static boolean positionOutOfBounds(Grid grid, int[] pos) {
    return (pos[0] < 0
        || pos[1] < 0
        || pos[0] >= grid.getGrid().length
        || pos[1] >= grid.getGrid()[0].length);
  }

  /**
   * Checks if a position on the grid is empty.
   *
   * @param grid
   * @param pos
   * @return true if the position is an empty Field "" and can be occupied
   */
  public static boolean emptyField(Grid grid, int[] pos) {
    return grid.getPosition(pos[1], pos[0]) == null;
  }

  /**
   * Checks if a position on the grid contains a block.
   *
   * @param grid
   * @param pos
   * @return true if the position is occupied by a block and cannot be walked on
   */
  public static boolean occupiedByBlock(Grid grid, int[] pos) {
    return grid.getPosition(pos[1], pos[0]).getObject() == GridObjects.block;
  }

  /**
   * Checks if a position on the grid contains a piece.
   *
   * @param grid
   * @param pos
   * @return true if the position is occupied by a piece
   */
  public static boolean occupiedByPiece(Grid grid, int[] pos) {
    return grid.getPosition(pos[1], pos[0]).getObject() == GridObjects.piece;
  }

  /**
   * Checks if a position on the grid is occupied by a piece from the same team as the moving piece.
   *
   * @param gameState
   * @param oldPos to get the moving piece
   * @param pos
   * @return true if the position is occupied by a Piece of the same Team
   */
  public static boolean occupiedBySameTeam(ReferenceGameState gameState, int[] oldPos, int[] pos) {
    return gameState.getGrid().getPosition(oldPos[1], oldPos[0]).getTeamId()
        == gameState.getGrid().getPosition(pos[1], pos[0]).getTeamId();
  }

  /**
   * Checks if a position on the grid is occupied by a piece with a weaker or the same AttackPower
   * as a given piece.
   *
   * @param opponent
   * @param picked
   * @return true if the position is occupied by a weaker opponent that can be captured
   */
  public static boolean occupiedByWeakerOpponent(Piece opponent, Piece picked) {
    if (opponent != null) {
      return opponent.getDescription().getAttackPower() <= picked.getDescription().getAttackPower();
    }
    return false;
  }

  /**
   * Checks if a position on the grid is occupied by an opponents base.
   *
   * @param grid
   * @param newPos
   * @param oldPos represents the moving piece
   * @return true if the position is occupied by another teams base and a flag can be captured
   */
  public static boolean otherTeamsBase(Grid grid, int[] newPos, int[] oldPos) {
    if (grid.getPosition(newPos[1], newPos[0]).getObject() == GridObjects.base) {
      if (grid.getPosition(oldPos[1], oldPos[0]).getTeamId()
          != grid.getPosition(newPos[1], newPos[0]).getTeamId()) return true;
    }
    return false;
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
        return 0;
    }
  }

  /**
   * Adjusted RandomAI to be used with ReferenceGameStates. Given a GameState, the next move is
   * randomly chosen. A random piece is chosen out of all pieces, if it is able to move its move is
   * randomly chosen. If the piece is not able to move a new piece is chosen from the remaining
   * pieces. If no move is possible a NoMovesLeftException is thrown. If a piece moves in an unknown
   * Shape an InvalidShapeException is thrown.
   *
   * @param gameState
   * @return a valid random Move
   * @throws NoMovesLeftException
   * @throws InvalidShapeException
   */
  @SuppressWarnings("unchecked")
  public static ReferenceMove pickMoveComplex(ReferenceGameState gameState, ReferenceMove operateOn)
      throws NoMovesLeftException, InvalidShapeException {
    ArrayList<Piece> piecesCurrentTeam = (ArrayList<Piece>) gameState.getTeams()[gameState.getCurrentTeam()].getPieces().clone();
    ArrayList<int[]> dirMap = new ArrayList<int[]>();
    ArrayList<int[]> shapeMoves = new ArrayList<int[]>();

    while (piecesCurrentTeam.size() > 0) {
      int random = (int) (Math.random() * piecesCurrentTeam.size());
      Piece picked = piecesCurrentTeam.get(random);

      if (picked.getDescription().getMovement().getDirections() != null) { // move if Directions
        dirMap = createDirectionMap(gameState, picked, dirMap, operateOn);
        if (dirMap.size() > 0) {
          return getDirectionMove(dirMap, picked, gameState, operateOn);
        } else {
          piecesCurrentTeam.remove(random);
          continue;
        }
      } else { // Move if Shape
        shapeMoves = getShapeMoves(gameState, picked, shapeMoves);
        if (shapeMoves.size() > 0) {
          return getRandomShapeMove(shapeMoves, picked, operateOn);
        } else {
          piecesCurrentTeam.remove(random);
          continue;
        }
      }
    }

    if (piecesCurrentTeam.size() == 0)
      throw new NoMovesLeftException(gameState.getTeams()[gameState.getCurrentTeam()].getId());

    return null;
  }
}
