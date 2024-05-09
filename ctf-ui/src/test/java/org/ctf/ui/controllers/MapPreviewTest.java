package org.ctf.ui.controllers;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.unimannheim.swt.pse.ctf.CtfApplication;
import java.io.IOException;

import org.ctf.shared.constants.Enums.Port;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.data.map.MapTemplate;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MapPreviewTest {
  /**
   * @author rsyed
   */
  @BeforeAll
  static void setup() {
    String[] args = new String[] {"--server.port=" + Port.DEFAULT.toString()};
    CtfApplication.main(args);
  }

  @Test
  void test() {
    MapPreview mp = new MapPreview(createGameTemplate());
    GameState gs = mp.getGameState();
    String[][] grid = gs.getGrid();

    for (String[] y : grid) {
      for (String x : y) {
        System.out.print(x.isEmpty() ? "+" : x);
      }
      System.out.println("");
    }

    assertNotNull(grid);
  }

   /**
   * @author rsyed
   */
  private MapTemplate createGameTemplate() {
    ObjectMapper objectMapper = new ObjectMapper();
    MapTemplate mapTemplate = null;
    try {
      mapTemplate =
          objectMapper.readValue(
              getClass().getResourceAsStream("/maptemplates/10x10_2teams_example.json"),
              MapTemplate.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return mapTemplate;
  }
}
