package org.ctf.shared.state.dto;

import org.ctf.shared.state.data.map.MapTemplate;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * This class is used to represent a request to create a new game session.
 * @author Marcus Kessel
 */
public class GameSessionRequest {

    @Schema(
            description = "the map to use for a new game session"
    )
    private MapTemplate template;

    public MapTemplate getTemplate() {
        return template;
    }

    public void setTemplate(MapTemplate template) {
        this.template = template;
    }
}