package org.ctf.shared.wave;

import static org.junit.jupiter.api.Assertions.*;
import org.ctf.shared.wave.WaveGrid;
import org.junit.jupiter.api.Test;

class TileTest {

  @Test
  void testGetNeighbor() {
    WaveGrid wg =
        new WaveGrid(new int[][] {{0, 1, 0, 1}, {1, 0, 1, 0}, {0, 1, 0, 1}, {1, 0, 1, 0}});
    assertEquals(1, wg.tiles.get(8).getUpperNeighbor().getY());
    assertEquals(0, wg.tiles.get(8).getUpperNeighbor().getX());
    
    assertEquals(3, wg.tiles.get(8).getLowerNeighbor().getY());
    assertEquals(0, wg.tiles.get(8).getLowerNeighbor().getX());
    
    assertEquals(2, wg.tiles.get(8).getRightNeighbor().getY());
    assertEquals(1, wg.tiles.get(8).getRightNeighbor().getX());
    
    assertEquals(2, wg.tiles.get(9).getLeftNeighbor().getY());
    assertEquals(0, wg.tiles.get(9).getLeftNeighbor().getX());

  }

}
