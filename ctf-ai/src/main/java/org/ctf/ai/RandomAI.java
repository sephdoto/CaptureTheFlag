package org.ctf.ai;

import org.ctf.client.state.GameState;
import org.ctf.client.state.Piece;
import org.ctf.client.state.Move;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


/**
 * @author sistumpf
 * Everything needed for choosing a random move
 */
public class RandomAI extends AI_Tools {
  /**
   * Use this to get a random move from a GameState with either the complex or simple algorithm.
   * The complex algorithm is recommended but might be a little slower sometimes.
   * @param gameState
   * @param complex
   * @return a valid random move
   * @throws NoMovesLeftException
   * @throws InvalidShapeException
   */
  public static Move pickMove(GameState gameState, boolean complex) throws NoMovesLeftException, InvalidShapeException {
    if(complex)
      return pickMoveComplex(gameState);
    else {
      return pickMoveSimple(gameState);
    }
  }

  /**
   * Given a GameState, the next move is randomly chosen.
   * Heavily relies on randomness, might be a lot faster or a lot slower than {@link #pickMoveComplex(GameState gameState)}.
   * This Method cannot notice when there are no moves left.
   * @param gameState
   * @return a valid random Move
   * @throws InvalidShapeException
   */
  public static Move pickMoveSimple(GameState gameState) throws InvalidShapeException {
    Move move = new Move();

    Piece[] pieces = gameState.getTeams()[gameState.getCurrentTeam()].getPieces();
    do {
      Piece picked = pieces[(int)(Math.random() * pieces.length)];
      if(picked.getDescription().getMovement().getDirections() != null) {
        int randomDirection = (int)(Math.random()*8);
        int reach = (int)(Math.random() * getReach(picked.getDescription().getMovement().getDirections(), randomDirection));
        move = checkMoveValidity(gameState, picked, randomDirection, reach);
      } else {
        move = validShapeDirection(gameState, picked, (int)(Math.random()*8));
      }
    } while (move == null);
    return move;
  }

  /**
   * Given a GameState, the next move is randomly chosen.
   * A random piece is chosen out of all pieces, if it is able to move its move is randomly chosen.
   * If the piece is not able to move a new piece is chosen from the remaining pieces.
   * If no move is possible a NoMovesLeftException is thrown.
   * If a piece moves in an unknown Shape an InvalidShapeException is thrown.
   * @param gameState
   * @return a valid random Move
   * @throws NoMovesLeftException
   * @throws InvalidShapeException 
   */
  public static Move pickMoveComplex(GameState gameState) throws NoMovesLeftException, InvalidShapeException {
    ArrayList<Piece> piecesCurrentTeam = new ArrayList<Piece>(Arrays.asList(gameState.getTeams()[gameState.getCurrentTeam()].getPieces()));
    Move move = new Move();

    while(piecesCurrentTeam.size() > 0) {		
      int random = (int)(Math.random() * piecesCurrentTeam.size());
      Piece picked = piecesCurrentTeam.get(random);

      if(picked.getDescription().getMovement().getDirections() != null) {		//move if Directions
        HashMap<Integer,Integer> dirMap = createDirectionMap(gameState, picked);
        if(dirMap.size() > 0) {
          return getDirectionMove(dirMap, picked, gameState);
        } else {
        	piecesCurrentTeam.remove(random);
          continue;
        }
      } else {																	//Move if Shape
    	ArrayList<Move> shapeMoves = createShapeMoveList(gameState, picked);
        if(shapeMoves.size() > 0) {
          return getRandomShapeMove(shapeMoves);
        } else {
        	piecesCurrentTeam.remove(random);
          continue;
        }
      }
    }

    if(piecesCurrentTeam.size() == 0)
      throw new NoMovesLeftException(gameState.getTeams()[gameState.getCurrentTeam()].getId());

    return move;
  }
}
