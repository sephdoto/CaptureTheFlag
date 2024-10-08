package org.ctf.shared.state.data.map;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * This class represents a possible movement.
 * @author Marcus Kessel
 */
public class Movement implements java.io.Serializable{

    private static final long serialVersionUID = -6872336510868653036L;
    @Schema(
            description = "directions a piece can move. if set, shape must NOT be set"
    )
    private Directions directions;
    @Schema(
            description = "shapes a piece can move (e.g., L. if set, directions must NOT be set"
    )
    private Shape shape;

    public Directions getDirections() {
        return directions;
    }

    public void setDirections(Directions directions) {
        this.directions = directions;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }
}
