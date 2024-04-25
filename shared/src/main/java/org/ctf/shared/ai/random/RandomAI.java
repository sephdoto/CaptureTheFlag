package org.ctf.shared.ai.random;

import java.util.ArrayList;
import java.util.Arrays;
import org.ctf.shared.ai.AI_Tools;
import org.ctf.shared.ai.ReferenceMove;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Piece;

/**
 * @author sistumpf Two different Algorithms for choosing random moves. Simple relies purely on
 *     randomness while choosing pieces and their moves and does not realize positions without valid
 *     moves; Complex tries to minimize choosing invalid moves and has noMoreMove checks built in.
 *     Simple is not recommended but could probably be useful in some cases.
 */
public class RandomAI extends AI_Tools {
  /**
   * Given a GameState, the next move is randomly chosen. Heavily relies on randomness, might be a
   * lot faster or a lot slower than {@link #pickMoveComplex(GameState gameState)}. This Method is
   * not able to notice when there are no moves left.
   *
   * @param gameState
   * @return a valid random Move
   * @throws InvalidShapeException
   */
  @Deprecated
  public static Move pickMoveSimple(GameState gameState) throws InvalidShapeException {
    ReferenceMove move = null;
    Piece[] pieces = gameState.getTeams()[gameState.getCurrentTeam()].getPieces();
    do {
      Piece picked = pieces[(int) (Math.random() * pieces.length)];
      if (picked.getDescription().getMovement().getDirections() != null) {
        int randomDirection = (int) (Math.random() * 8);
        int reach =
            (int)
                (Math.random()
                    * getReach(
                        picked.getDescription().getMovement().getDirections(), randomDirection));
        move = checkMoveValidity(gameState, picked, randomDirection, reach, new ReferenceMove(null, new int[] {0,0}));
      } else {
        move =
            getRandomShapeMove(
                getShapeMoves(gameState, picked, new ArrayList<int[]>()), picked, new ReferenceMove(null, new int[] {0,0}));
      }
    } while (move.getPiece() == null);
    return move.toMove();
  }

  /**
   * Given a GameState, the next move is randomly chosen. A random piece is chosen out of all
   * pieces, if it is able to move its move is randomly chosen. If the piece is not able to move a
   * new piece is chosen from the remaining pieces. If no move is possible a NoMovesLeftException is
   * thrown. If a piece moves in an unknown Shape an InvalidShapeException is thrown.
   *
   * @param gameState
   * @return a valid random Move
   * @throws NoMovesLeftException
   * @throws InvalidShapeException
   */
  public static ReferenceMove pickMoveComplex(GameState gameState, ReferenceMove change)
      throws NoMovesLeftException, InvalidShapeException {
    ArrayList<Piece> piecesCurrentTeam =
        new ArrayList<Piece>(
            Arrays.asList(gameState.getTeams()[gameState.getCurrentTeam()].getPieces()));
    ArrayList<int[]> dirMap = new ArrayList<int[]>();

    while (piecesCurrentTeam.size() > 0) {
      int random = (int) (Math.random() * piecesCurrentTeam.size());
      Piece picked = piecesCurrentTeam.get(random);

      if (picked.getDescription().getMovement().getDirections() != null) { // move if Directions
        dirMap = createDirectionMap(gameState, picked, dirMap, change);
        if (dirMap.size() > 0) {
          return getDirectionMove(dirMap, picked, gameState, change);
        } else {
          piecesCurrentTeam.remove(random);
          continue;
        }
      } else { // Move if Shape
        dirMap = getShapeMoves(gameState, picked, dirMap);
        if (dirMap.size() > 0) {
          return getRandomShapeMove(dirMap, picked, change);
        } else {
          piecesCurrentTeam.remove(random);
          continue;
        }
      }
    }

  throw new NoMovesLeftException(gameState.getTeams()[gameState.getCurrentTeam()].getId());
  }
}
