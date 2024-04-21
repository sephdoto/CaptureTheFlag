package org.ctf.shared.state;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * This class represents a move.
 * @author Marcus Kessel
 */
public class Move implements java.io.Serializable {
    @Schema(
            description = "the piece (by its ID) to move"
    )
    private String pieceId;
    @Schema(
            description = "new position of the piece on the board (grid)"
    )
    private int[] newPosition;

    public Move() {
        this.pieceId = "";
        this.newPosition = new int[2];
    }

    public String getPieceId() {
        return pieceId;
    }

    public void setPieceId(String pieceId) {
        this.pieceId = pieceId;
    }

    public int[] getNewPosition() {
        return newPosition;
    }

    public void setNewPosition(int[] newPosition) {
        this.newPosition = newPosition;
    }
}
