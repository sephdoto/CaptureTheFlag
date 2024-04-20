package de.unimannheim.swt.pse.ctf.game;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import de.unimannheim.swt.pse.ctf.game.map.MapTemplate;
import de.unimannheim.swt.pse.ctf.game.state.GameState;
import de.unimannheim.swt.pse.ctf.game.state.Team;

class BoardControllerTest {
  static BoardController bordi;
  
  @BeforeAll
  static void setUpBeforeClass() throws Exception {
    bordi = new BoardController(TestValues.getTestState(), TestValues.getTestTemplate());
  }

  @Test
  void testBoardController() {
    fail("Not yet implemented");
  }

  @Test
  void testInitEmptyGrid() {
    fail("Not yet implemented");
  }

  @Test
  void testInitializeTeam() {
    fail("Not yet implemented");
  }

  @Test
  void testGetBoundaries() {
    MapTemplate template = new MapTemplate();
    template.setGridSize(new int[] {20,20});
    template.setTeams(5);
    BoardController bordi = new BoardController(null, template);
    int[][] b = bordi.getBoundaries();
    for(int[] bI : b) {
      for(int bII : bI) {
//        System.out.print(bII + " ");
      }
//      System.out.println();
    }
    for(int i=0; i<b.length; i++)
      for(int j=0; j<b.length; j++) {
        if(i==j) continue;
        assertFalse(Arrays.equals(b[i], b[j]));
      }
  }

  @Test
  void testPlaceBases() {
    GameState gameState = TestValues.getTestState();
    bordi.placeBases(gameState);
    for(Team team : gameState.getTeams())
      assertEquals("b:"+team.getId(), gameState.getGrid()[team.getBase()[0]][team.getBase()[1]]);
  }

}
