package org.ctf.shared.ai.mcts3;

import java.util.HashSet;
import org.ctf.shared.state.Piece;

/**
 * This class represents a single (y,x) coordinate on the grid and all the pieces that can see this coordinate.
 * @author sistumpf
 */
public class GridPieceContainer {
  HashSet<Piece> pieces;
  
  public GridPieceContainer() {
    this.pieces = new HashSet<Piece>();
  }
  public GridPieceContainer(HashSet<Piece> pieces) {
    this.pieces = pieces;
  }
  
  @Override
  public GridPieceContainer clone() {
    HashSet<Piece> pieces = new HashSet<Piece>();
    pieces.addAll(this.pieces);
    return new GridPieceContainer(pieces);
  }
  
  public boolean equals(GridPieceContainer compare) {
    for(Piece piece : this.pieces) {
      boolean contains = false;
      for(Piece cPiece : compare.getPieces())
        if(piece.getId().equals(cPiece.getId()))
          contains = true;
      if(!contains)
        return false;
    }
    return true;
  }
  
  public HashSet<Piece> getPieces() {
    return pieces;
  }
  public void setPieces(HashSet<Piece> pieces) {
    this.pieces = pieces;
  }
}
