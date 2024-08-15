package org.ctf.shared.state;

import org.ctf.shared.state.data.map.PieceDescription;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * This class represents a piece.
 * @author Marcus Kessel
 */
public class Piece implements java.io.Serializable{
    private static final long serialVersionUID = -7203243527880816441L;
    @Schema(
            description = "unique piece identifier"
    )
    private String id;
    @Schema(
            description = "team owning the piece"
    )
    private String teamId;
    @Schema(
            description = "the description of the piece (including its attack power etc.)"
    )
    private PieceDescription description;
    @Schema(
            description = "current position of the piece"
    )
    private int[] position;

    public Piece() {
        this.id = "";
        this.teamId = "";
        this.description = null;
        this.position = new int[2];
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int[] getPosition() {
        return position;
    }

    public void setPosition(int[] position) {
        this.position = position;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public PieceDescription getDescription() {
        return description;
    }

    public void setDescription(PieceDescription description) {
        this.description = description;
    }
}
