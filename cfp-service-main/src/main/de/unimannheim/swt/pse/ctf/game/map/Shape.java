package de.unimannheim.swt.pse.ctf.game.map;

import io.swagger.v3.oas.annotations.media.Schema;
import org.json.JSONObject;
import org.json.JSONString;


/**
 * This class represents a shape movement (e.g., L-shape as known from chess).
 */
public class Shape implements JSONString{

    @Schema(
            description = "the type of movement"
    )
    private ShapeType type;

    public ShapeType getType() {
        return type;
    }

    public void setType(ShapeType type) {
        this.type = type;
    }
    

    /**
     * returns this classes JSON String representation
     * @author sistumpf
     * @return String
     */
    @Override
    public String toJSONString() {
    	return new JSONObject().put("shape", new JSONObject().put("type",type)).toString();
    }
}
