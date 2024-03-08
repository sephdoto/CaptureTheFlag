package org.ctf.client.state;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * This class represents a team together with all its pieces.
 */
public class Team {
    @Schema(
            description = "unique team identifier"
    )
    private String id;
    @Schema(
            description = "unique team color"
    )
    private String color;
    @Schema(
            description = "the base(s) of the team"
    )
    private int[] base;
    @Schema(
            description = "how many flags are in each base"
    )
    private int[] flag;
    @Schema(
            description = "remaining pieces on the grid"
    )
    private Piece[] pieces;

    public Team() {
        this.id = "";
        this.color = "";
        this.base = new int[2];
        this.flag = new int[2];
        this.pieces = new Piece[0];
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int[] getBase() {
        return base;
    }

    public void setBase(int[] base) {
        this.base = base;
    }

    public int[] getFlag() {
        return flag;
    }

    public void setFlag(int[] flag) {
        this.flag = flag;
    }

    public Piece[] getPieces() {
        return pieces;
    }

    public void setPieces(Piece[] pieces) {
        this.pieces = pieces;
    }

}
