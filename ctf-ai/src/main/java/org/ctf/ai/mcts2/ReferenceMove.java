package org.ctf.ai.mcts2;

import java.util.Arrays;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Piece;

/**
 * This class represents a move as the Move class.
 * The moving Piece is saved by its reference, not its Id, making finding the moved piece easier.
 */
public class ReferenceMove {

  private Piece piece;
  private int[] newPosition;

  public ReferenceMove(Piece piece, int[] newPos) {
    this.piece = piece;
    this.newPosition = newPos;
  }

  public ReferenceMove(GameState gameState, Move move) {
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

  public Move toMove() {
    Move move = new Move();
    move.setNewPosition(this.newPosition);
    if(this.piece != null)
      move.setPieceId(this.piece.getId());
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