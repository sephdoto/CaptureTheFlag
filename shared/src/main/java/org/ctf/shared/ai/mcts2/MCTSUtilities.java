package org.ctf.shared.ai.mcts2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.stream.Stream;
import org.ctf.shared.ai.ReferenceMove;
import org.ctf.shared.ai.GameUtilities.InvalidShapeException;
import org.ctf.shared.ai.GameUtilities.NoMovesLeftException;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Piece;
import org.ctf.shared.state.data.map.Directions;
import org.ctf.shared.state.data.map.ShapeType;

/**
 * An optimized GameUtilities class, adjusted to use the new Grid class
 *
 * @author sistumpf
 */
public class MCTSUtilities {
  static Random random = new Random();

  public static void putNeighbouringPieces(HashSet<Piece> pieceList, Grid grid, int[] center) {
    try {
      pieceList.addAll(grid.getPieceVisionGrid()[center[0]][center[1]].getPieces());
    } catch (NullPointerException npe) {
      /* if only the piece itself is on a position this gets thrown */
    }
    ;
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
   * This method is needed to respawn a piece, it adds all positions in a certain radius around the
   * base to an Array.
   *
   * @param xTrans translations on x-axis
   * @param distance from the base
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
   * @param distance from the base
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
   * Returns the next teams index in the team array.
   *
   * @param gameState
   * @return altered gameState
   */
  public static int getNextTeam(ReferenceGameState gameState) {
    for (int i = (gameState.getCurrentTeam() + 1) % gameState.getTeams().length;
        ;
        i = (i + 1) % gameState.getTeams().length) {
      if (gameState.getTeams()[i] != null) {
        return i;
      }
    }
  }

  /**
   * Removes a certain team from the GameState.
   *
   * @param gameState
   * @param team the place of the team in the ReferenceGameState.getTeams Array
   */
  public static void removeTeam(ReferenceGameState gameState, int team) {
    gameState
    .getGrid()
    .setPosition(
        null, gameState.getTeams()[team].getBase()[1], gameState.getTeams()[team].getBase()[0]);
    for (int i = 0; i < gameState.getTeams()[team].getPieces().length; i++) {
      Piece p = gameState.getTeams()[team].getPieces()[i];
      gameState.getGrid().setPosition(null, p.getPosition()[1], p.getPosition()[0]);
      if (gameState.getGrid().pieceVisions.size() > 0)
        for (int[] pos : gameState.getGrid().pieceVisions.get(p))
          gameState.getGrid().pieceVisionGrid[pos[0]][pos[1]].pieces.remove(p);
      gameState.getGrid().pieceVisions.remove(p);
      // TODO ich weiß nicht ob der garbage collector das team löscht oder ich den array erst leeren
      // muss. Später schauen.
    }
    gameState.getTeams()[team] = null;
  }

  /**
   * Given a Piece and a ReferenceGameState containing the Piece, a given ArrayList is altered to
   * contain all valid locations the Piece can walk on. The ArrayList contains int[2] values,
   * representing a (y,x) location on the grid. This method returns the first sightline violations
   * for a piece (if it's caused by another piece), so the PieceVision Grid can be correctly
   * initialized.
   *
   * @param gameState
   * @param piece that moves
   * @param possibleMoves will contain all possible moves after the method is finished.
   * @return ArrayList<int[]> that contains all first sightline violations could move to but they
   *     are occupied by another piece.
   */
  public static ArrayList<int[]> getPossibleMovesWithPieceVision(
      ReferenceGameState gameState, Piece piece, ArrayList<int[]> possibleMoves) {
    possibleMoves.clear();
    ArrayList<int[]> pieceInSightP = new ArrayList<int[]>();
    ArrayList<int[]> dirMap = new ArrayList<int[]>();
    if (piece.getDescription().getMovement().getDirections() == null) {
      try {
        pieceInSightP.addAll(getShapeMovesWithPieceVision(gameState, piece, possibleMoves));
      } catch (InvalidShapeException e) {
        e.printStackTrace();
      }

    } else {
      dirMap = createDirectionMapWithPieceVision(gameState, piece, dirMap);
      for (int[] entry : dirMap) {
        for (int reach = entry[1]; reach > 0; reach--) {
          Move move = new Move();
          move = checkMoveValidity(gameState, piece, entry[0], reach);
          if (move != null) {
            possibleMoves.add(move.getNewPosition());
          } else {
            int[] newPos = updatePos(piece.getPosition().clone(), entry[0], reach);
            if (!positionOutOfBounds(gameState.getGrid(), newPos)
                && sightLine(gameState, newPos.clone(), entry[0], reach)
                && occupiedByPiece(gameState.getGrid(), newPos)) {
              pieceInSightP.add(newPos);
            }
          }
        }
      }
    }
    return pieceInSightP;
  }

  /**
   * Creates an ArrayList containing all a pieces valid directions and its maximum reach into that
   * direction in int[direction, reach] pairs. This map only applies for the Piece picked. The reach
   * value is directly from MapTemplate, this method only checks if the positions adjacent to a
   * piece are occupied.
   *
   * @param gameState
   * @param picked
   * @return direction, reach list
   */
  public static ArrayList<int[]> createDirectionMapWithPieceVision(
      ReferenceGameState gameState, Piece picked, ArrayList<int[]> dirMap) {
    dirMap.clear();
    for (int i = 0; i < 8; i++) {
      int reach = getReach(picked.getDescription().getMovement().getDirections(), i);
      if (reach > 0) {
        int[] pos = updatePos(picked.getPosition().clone(), i, 1);
        if (positionOutOfBounds(gameState.getGrid(), pos)) continue;
        if (emptyField(gameState.getGrid(), pos)
            || occupiedByPiece(gameState.getGrid(), pos)
            || otherTeamsBase(gameState.getGrid(), pos, picked.getPosition())) {
          dirMap.add(new int[] {i, reach});
        } else {
          continue;
        }
      }
    }
    return dirMap;
  }

  /**
   * Alters an ArrayList with all valid Moves a piece with shape movement can do. Returns an
   * ArrayList with the first sightline violations, if they are caused by another Piece.
   *
   * @param gameState
   * @param piece
   * @param positions will be altered to contain all valid moves
   * @return ArrayList containing all invalid moves
   * @throws InvalidShapeException if the Shape is not yet implemented here
   */
  public static ArrayList<int[]> getShapeMovesWithPieceVision(
      ReferenceGameState gameState, Piece piece, ArrayList<int[]> positions)
          throws InvalidShapeException {

    ArrayList<int[]> pieceInSightP = new ArrayList<int[]>();
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
                piece.getPosition()[0] + yTransforms[12+ (i / 3)] + yTransforms[1 + ((i / 3) * 3)],
                piece.getPosition()[1] + xTransforms[12+ (i / 3)] + xTransforms[1 + ((i / 3) * 3)]
            },
            direction[i],
            3)) {
          positions.add(newPos);
        }
      } else if (!positionOutOfBounds(gameState.getGrid(), newPos)
          && occupiedByPiece(gameState.getGrid(), newPos)) { // TODO NEU EINGEFÜGT; TESTEN!
        if (i >= direction.length) {
          pieceInSightP.add(newPos);
        } else if (sightLine(
            gameState,
            new int[] {
                piece.getPosition()[0] + yTransforms[(1 + (i / 3) * 3)],
                piece.getPosition()[1] + xTransforms[(1 + (i / 3) * 3)]
            },
            direction[i],
            2)) {
          pieceInSightP.add(newPos);
        }
      }
    }
    return pieceInSightP;
  }

  /**
   * Given a Piece and a GameState containing the Piece, an ArrayList with all valid locations the
   * Piece can walk on is returned. The ArrayList contains int[2] values, representing a (y,x)
   * location on the grid.
   *
   * @param gameState
   * @param piece
   * @return ArrayList<int[]> that contains all valid positions a piece could move to
   */
  public static ArrayList<int[]> getPossibleMoves(
      ReferenceGameState gameState, Piece piece, ArrayList<int[]> possibleMoves) {
    possibleMoves.clear();
    ArrayList<int[]> dirMap = new ArrayList<int[]>();
    if (piece.getDescription().getMovement().getDirections() == null) {
      try {
        getShapeMoves(gameState, piece, possibleMoves);
      } catch (InvalidShapeException e) {
        e.printStackTrace();
      }

    } else {
      dirMap = createDirectionMap(gameState, piece, dirMap);
      for (int[] entry : dirMap) {
        for (int reach = entry[1]; reach > 0; reach--) {
          Move move = new Move();
          move = checkMoveValidity(gameState, piece, entry[0], reach);
          if (move != null) possibleMoves.add(move.getNewPosition());
        }
      }
    }
    return possibleMoves;
  }

  /**
   * Creates an ArrayList with all valid Moves a piece with shape movement can do.
   *
   * @param gameState
   * @param piece
   * @return ArrayList containing all valid moves
   * @throws InvalidShapeException if the Shape is not yet implemented here
   */
  public static ArrayList<int[]> getShapeMoves(
      ReferenceGameState gameState, Piece piece, ArrayList<int[]> positions)
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
   * @return direction, piece list
   */
  public static ArrayList<int[]> createDirectionMap(
      ReferenceGameState gameState, Piece picked, ArrayList<int[]> dirMap) {
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
   * This method tests if a piece could walk into a given direction. It does not test if a pieces
   * reach in a direction is >0. The direction is given as an int (0-7).
   *
   * @param gameState
   * @param piece
   * @param direction
   * @return false if there are no possible moves in this direction, true otherwise.
   */
  public static boolean validDirection(ReferenceGameState gameState, Piece piece, int direction) {
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
  public static Move checkMoveValidity(
      ReferenceGameState gameState, Piece piece, int direction, int reach) {
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
   * Checks if a piece can occupy a given position. Does not check sightLine()
   *
   * @param pos
   * @param piece
   * @param gameState
   * @return true if the position can be occupied.
   */
  public static boolean validPos(int[] pos, Piece piece, ReferenceGameState gameState) {
    // checks if the position can be occupied
    if (positionOutOfBounds(gameState.getGrid(), pos)) return false;
    if (emptyField(gameState.getGrid(), pos)) return true;
    if (occupiedByBlock(gameState.getGrid(), pos)) return false;
    if (occupiedBySameTeam(gameState, piece.getPosition(), pos)) return false;
    if (otherTeamsBase(gameState.getGrid(), pos, piece.getPosition())) return true;
    if (occupiedByWeakerOpponent(gameState.getGrid().getPosition(pos[1], pos[0]).getPiece(), piece))
      return true;

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
    //    System.out.println(grid.getPosition(newPos[1], newPos[0]).getObject());
    if (grid.getPosition(newPos[1], newPos[0]).getObject() == GridObjects.base) {
      if (grid.getPosition(oldPos[1], oldPos[0]).getTeamId()
          != grid.getPosition(newPos[1], newPos[0]).getTeamId()) return true;
    }
    return false;
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
  public static ReferenceMove pickMoveComplex(ReferenceGameState gameState)
      throws NoMovesLeftException, InvalidShapeException {
    ArrayList<Piece> piecesCurrentTeam =
        new ArrayList<Piece>(
            Arrays.asList(gameState.getTeams()[gameState.getCurrentTeam()].getPieces()));
    ArrayList<int[]> dirMap = new ArrayList<int[]>();
    ArrayList<int[]> shapeMoves = new ArrayList<int[]>();

    while (piecesCurrentTeam.size() > 0) {
      int random = (int) (Math.random() * piecesCurrentTeam.size());
      Piece picked = piecesCurrentTeam.get(random);

      if (picked.getDescription().getMovement().getDirections() != null) { // move if Directions
        dirMap = createDirectionMap(gameState, picked, dirMap);
        if (dirMap.size() > 0) {
          return getDirectionMove(dirMap, picked, gameState);
        } else {
          piecesCurrentTeam.remove(random);
          continue;
        }
      } else { // Move if Shape
        shapeMoves = getShapeMoves(gameState, picked, shapeMoves);
        if (shapeMoves.size() > 0) {
          return getRandomShapeMove(shapeMoves, picked);
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

  /**
   * Selects and returns a random Move from an ArrayList which only contains valid Moves.
   *
   * @param positionArrayList
   * @param piece
   * @return randomly picked move
   */
  public static ReferenceMove getRandomShapeMove(ArrayList<int[]> positionArrayList, Piece piece) {
    return new ReferenceMove(
        piece, positionArrayList.get((int) (positionArrayList.size() * Math.random())));
  }

  /**
   * Returns a Move from a given HashMap of possible directions and and their reach to move in. This
   * method picks a random dirction-reach pair and returns a Move to this position using {@link
   * #checkMoveValidity(ReferenceGameState gameState, Piece piece, int direction, int reach)}. If
   * the position is invalid this process is tried again till a valid move is generated. If a random
   * position is invalid the HashMap reach value is lowered to ensure the same position is not
   * picked again. This method assumes the HashMap contains elements and all directions contain at
   * least 1 valid position.
   *
   * @param dirMap
   * @param piece
   * @param gameState
   * @return a valid move
   */
  public static ReferenceMove getDirectionMove(
      ArrayList<int[]> dirMap, Piece piece, ReferenceGameState gameState) {
    int randomDir = (int) (dirMap.size() * Math.random());
    int reach;

    while (true) {
      reach = (int) (Math.random() * dirMap.get(randomDir)[1] + 1);
      Move newPos = checkMoveValidity(gameState, piece, dirMap.get(randomDir)[0], reach);
      if (newPos != null) return new ReferenceMove(piece, newPos.getNewPosition());
      dirMap.get(randomDir)[1] = reach - 1;
      continue;
    }
  }
}
