package org.ctf.shared.ai;

import java.util.Arrays;
import org.ctf.shared.ai.mcts2.ReferenceGameState;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Piece;

/**
 * This class represents a move as the Move class.
 * The moving Piece is saved by its reference, not its Id, making finding the moved piece easier.
 * @author sistumpf
 */
public class ReferenceMove {

  private Piece piece;
  private int[] newPosition;

  /**
   * Default constructor to initialize a ReferenceMove.
   * @param piece
   * @param newPos
   */
  public ReferenceMove(Piece piece, int[] newPos) {
    this.piece = piece;
    this.newPosition = newPos;
  }

  /**
   * Constructor to initialize a ReferenceMove from a Move.
   * The Piece gets taken from a gameState which gets searched for the PieceID in move.
   * @param gameState
   * @param move
   */
  public ReferenceMove(GameState gameState, Move move) {
    if(move == null)
      return;
    
    if(move.getPieceId() != null  && !move.getPieceId().equals("")) {
      this.newPosition = move.getNewPosition();
      this.piece =
          Arrays.stream(
              gameState.getTeams()[Integer.parseInt(move.getPieceId().split(":")[1].split("_")[0])]
                  .getPieces())
          .filter(p -> p.getId().equals(move.getPieceId()))
          .findFirst()
          .get();
    }
  }

  /**
   * Constructor to initialize a ReferenceMove from a Move.
   * The Piece gets taken from a referenceGameState which gets searched for the PieceID in move.
   * @param gameState
   * @param move
   */
  public ReferenceMove(ReferenceGameState gameState, Move move) {
    if(move.getPieceId() != null) {
      this.newPosition = move.getNewPosition();
      this.piece =
          Arrays.stream(
              gameState.getTeams()[Integer.parseInt(move.getPieceId().split(":")[1].split("_")[0])]
                  .getPieces())
          .filter(p -> p.getId().equals(move.getPieceId()))
          .findFirst()
          .get();
    }
  }

  /**
   * Alternative constructor for mcts3, does the same as the one above.
   * 
   * @param gameState
   * @param move
   */
  public ReferenceMove(org.ctf.shared.ai.mcts3.ReferenceGameState gameState,
      Move move) {
    if(move.getPieceId() != null) {
      this.newPosition = move.getNewPosition();
      this.piece = gameState.getTeams()[Integer.parseInt(move.getPieceId().split(":")[1].split("_")[0])]
          .getPieces()
          .stream()
          .filter(p -> p.getId().equals(move.getPieceId()))
          .findFirst()
          .get();
    }
  }

  /**
   * Converts a ReferenceMove back to a normal Move.
   * @return Move representing this ReferenceMove.
   */
  public Move toMove() {
    Move move = new Move();
    move.setNewPosition(this.newPosition);
    if(this.piece != null) {
      move.setPieceId(this.piece.getId());
      move.setTeamId(this.getPiece().getTeamId());
    }
    return move;
  }

  public Piece getPiece() {
    return piece;
  }

  public void setPiece(Piece piece) {
    this.piece = piece;
  }

  public int[] getNewPosition() {
    return newPosition;
  }

  public void setNewPosition(int[] newPosition) {
    this.newPosition = newPosition;
  }
}