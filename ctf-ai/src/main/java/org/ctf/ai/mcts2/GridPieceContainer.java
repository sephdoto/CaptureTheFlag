package org.ctf.ai.mcts2;

import java.util.ArrayList;
import org.ctf.shared.state.Piece;

public class GridPieceContainer {
  ArrayList<Piece> pieces;
  
  public GridPieceContainer() {
    this.pieces = new ArrayList<Piece>();
  }
  public GridPieceContainer(ArrayList<Piece> pieces) {
    this.pieces = pieces;
  }
  
  public GridPieceContainer clone() {
    ArrayList<Piece> pieces = new ArrayList<Piece>();
    pieces.addAll(this.pieces);
    return new GridPieceContainer(pieces);
  }
  
  public ArrayList<Piece> getPieces() {
    return pieces;
  }
  public void setPieces(ArrayList<Piece> pieces) {
    this.pieces = pieces;
  }
}
