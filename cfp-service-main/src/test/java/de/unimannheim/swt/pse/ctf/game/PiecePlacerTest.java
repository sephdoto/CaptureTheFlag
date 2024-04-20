package de.unimannheim.swt.pse.ctf.game;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import de.unimannheim.swt.pse.ctf.game.map.MapTemplate;
import de.unimannheim.swt.pse.ctf.game.state.GameState;
import de.unimannheim.swt.pse.ctf.game.state.Team;

class PiecePlacerTest {

  @Test
  void testNextBaseDirection() {
    BoardController bc = new BoardController(TestValues.getTestState(), TestValues.getTestTemplate());
//    bc.gameState.getTeams()[0].setBase(new int[] {9,5});
    int dir = new PiecePlacer(bc.gameState, null).nextBaseDirection(bc.gameState.getTeams()[0]);
    assertEquals(3, dir);
    dir = new PiecePlacer(bc.gameState, null).nextBaseDirection(bc.gameState.getTeams()[1]);
    assertEquals(2, dir);
    
    GameState gs = TestValues.getTestState();
    MapTemplate mt = TestValues.getTestTemplate();
    mt.setTeams(3);
    gs.setTeams(new Team[3]);
    bc = new BoardController(gs, mt);
    bc.gameState.getTeams()[2].setBase(new int[] {4, 2});
    
    dir = new PiecePlacer(bc.gameState, null).nextBaseDirection(bc.gameState.getTeams()[0]);
    assertEquals(3, dir);
    dir = new PiecePlacer(bc.gameState, null).nextBaseDirection(bc.gameState.getTeams()[1]);
    assertEquals(0, dir);
  }

  void printGrid(GameState gameState) {
    for(int y=0; y<gameState.getGrid().length; y++) {
      for(int x=0; x<gameState.getGrid()[y].length; x++)
        System.out.print(gameState.getGrid()[y][x].equals("") ? ".    " : gameState.getGrid()[y][x] + " ");
      System.out.println();
    }
  }
}
