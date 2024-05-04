package org.ctf.shared.state;

import com.google.gson.Gson;
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
   * Needed to analyze moves for being same. Used in Analyzer class and SaveGame class
   *
   * @author rsyed
   * @return boolean True if the objects are the same. False if not
   */
  @Override
  public boolean equals(Object o1) {
    Move temp = (Move) o1;
    if (temp.pieceId != this.pieceId) {
      return false;
    } else if (temp.teamId != this.teamId) {
      return false;
    } else if (Arrays.equals(temp.newPosition, this.newPosition)) {
      return false;
    } else {
      return true;
    }
  }

  /**
   * Needed to analyze moves for being same. Used in Analyzer class and SaveGame class
   *
   * @author rsyed
   * @return int representing the string based hash of a move
   */
  @Override
  public int hashCode() {
    Gson gson = new Gson();
    return gson.toJson(this).hashCode();
  }
}
