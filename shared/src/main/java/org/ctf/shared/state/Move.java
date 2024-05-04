package org.ctf.shared.state;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Arrays;

/**
 * This class represents a move.
 *
 * @author Marcus Kessel
 */
public class Move implements java.io.Serializable {
  @Schema(description = "the piece (by its ID) to move")
  private String pieceId;

  @Schema(description = "unique identifier of the team that makes the move")
  private String teamId;

  @Schema(description = "new position of the piece on the board (grid)")
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

  public String getTeamId() {
    return teamId;
  }

  public void setTeamId(String teamId) {
    this.teamId = teamId;
  }

  /**
   * Overriding equals as we need it for hashing and comparing when saving the game for analysis
   *
   * @author rsyed
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Move other = (Move) obj;
    if (pieceId == null) {
      if (other.pieceId != null) return false;
    } else if (!pieceId.equals(other.pieceId)) return false;
    if (teamId == null) {
      if (other.teamId != null) return false;
    } else if (!teamId.equals(other.teamId)) return false;
    if (!Arrays.equals(newPosition, other.newPosition)) return false;
    return true;
  }

  /**
   * Overriding hashCode as we need it comparing when saving the game for analysis
   *
   * @author rsyed
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((pieceId == null) ? 0 : pieceId.hashCode());
    result = prime * result + ((teamId == null) ? 0 : teamId.hashCode());
    result = prime * result + Arrays.hashCode(newPosition);
    return result;
  }
}
