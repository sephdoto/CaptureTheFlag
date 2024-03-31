package org.ctf.ai.mcts2;

import java.util.HashSet;
import org.ctf.shared.state.Piece;

public class GridPieceContainer {
  HashSet<Piece> pieces;
  
  public GridPieceContainer() {
    this.pieces = new HashSet<Piece>();
  }
  public GridPieceContainer(HashSet<Piece> pieces) {
    this.pieces = pieces;
  }
  
  public GridPieceContainer clone() {
    HashSet<Piece> pieces = new HashSet<Piece>();
    pieces.addAll(this.pieces);
    return new GridPieceContainer(pieces);
  }
  
  public HashSet<Piece> getPieces() {
    return pieces;
  }
  public void setPieces(HashSet<Piece> pieces) {
    this.pieces = pieces;
  }
}
