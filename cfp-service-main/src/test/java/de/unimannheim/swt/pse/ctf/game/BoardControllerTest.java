package de.unimannheim.swt.pse.ctf.game;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import de.unimannheim.swt.pse.ctf.game.map.MapTemplate;
import de.unimannheim.swt.pse.ctf.game.map.PlacementType;
import de.unimannheim.swt.pse.ctf.game.state.GameState;
import de.unimannheim.swt.pse.ctf.game.state.Team;

class BoardControllerTest {
  BoardController bordi;
  
  @BeforeEach
  void setUp() throws Exception {
    GameState state = TestValues.getTestState();
    MapTemplate template = TestValues.getTestTemplate();
    int teams = 2;
    template.setTeams(teams);
    state.setTeams(new Team[teams]);
    state.setGrid(new String[1][1]);
    bordi = new BoardController(state, template);
  }

  @Test
  void testPlacePiecesSymmetrical() {
    new PiecePlacer(bordi.gameState, bordi.boundaries).placePieces(PlacementType.symmetrical);
    printGrid(bordi.gameState);
  }

  @Test
  void testGetBoundaries() {
    MapTemplate template = new MapTemplate();
    template.setGridSize(new int[] {20,20});
    int teams = 5;
    template.setTeams(teams);
    GameState state = new GameState();
    state.setTeams(new Team[teams]);
    for(int i=0; i<teams; i++) state.getTeams()[i] = new Team();
    BoardController bordi = new BoardController(state, template);
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

  void printGrid(GameState gameState) {
    for(int y=0; y<gameState.getGrid().length; y++) {
      for(int x=0; x<gameState.getGrid()[y].length; x++)
        System.out.print(gameState.getGrid()[y][x].equals("") ? ".    " : gameState.getGrid()[y][x] + " ");
      System.out.println();
    }
  }
}
