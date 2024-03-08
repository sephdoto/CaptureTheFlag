package org.ctf.client.state.data.map;

import io.swagger.v3.oas.annotations.media.Schema;
import org.json.JSONString;

/**
 * This class represents a possible movement.
 */
public class Movement implements JSONString {

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
    
    /**
     * returns this classes JSON String representation
     * @author sistumpf
     * @return String
     */
    @Override
    public String toJSONString() {
    	return (shape != null ? shape.toJSONString() : directions.toJSONString());
    }
}
