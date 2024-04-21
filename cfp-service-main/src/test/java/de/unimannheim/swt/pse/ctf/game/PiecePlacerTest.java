package de.unimannheim.swt.pse.ctf.game;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import de.unimannheim.swt.pse.ctf.game.map.MapTemplate;
import de.unimannheim.swt.pse.ctf.game.map.PlacementType;
import de.unimannheim.swt.pse.ctf.game.state.GameState;
import de.unimannheim.swt.pse.ctf.game.state.Team;

/**
 * @author sistumpf
 */
class PiecePlacerTest {
  @Test
  void testPlacePiecesSymmetrical() {
    GameState gs = TestValues.getTestState();
    MapTemplate mt = TestValues.getTestTemplate();
    mt.setGridSize(new int[] {30,30});
    mt.setTeams(3);
//    mt.getPieces()[0].setCount(45);
    gs.setTeams(new Team[3]);
    BoardController bc = new BoardController(gs, mt);
    for(int i=0; i<bc.gameState.getTeams().length; i++)
      bc.initializeTeam(i, mt);
    PiecePlacer pp = new PiecePlacer(bc.gameState, bc.boundaries);
    pp.placePieces(PlacementType.symmetrical);
//    printGrid(pp.gameState);
  }
  
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
    int teams = 3;
    mt.setTeams(teams);
    gs.setTeams(new Team[teams]);
    bc = new BoardController(gs, mt);
    while(--teams>=0) bc.initializeTeam(teams, mt);
    bc.gameState.getTeams()[2].setBase(new int[] {4, 2});
    
    dir = new PiecePlacer(bc.gameState, null).nextBaseDirection(bc.gameState.getTeams()[0]);
    assertEquals(3, dir);
    dir = new PiecePlacer(bc.gameState, null).nextBaseDirection(bc.gameState.getTeams()[1]);
    assertEquals(0, dir);
  }

  void printGrid(GameState gameState) {
    for(int y=0; y<gameState.getGrid().length; y++) {
      for(int x=0; x<gameState.getGrid()[y].length; x++)
        System.out.print(gameState.getGrid()[y][x].equals("") ? ".     " : gameState.getGrid()[y][x] + " ");
      System.out.println();
    }
  }
}
