package de.unimannheim.swt.pse.ctf.game;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Comparator;
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
  void testHillClimbComperator() {
    BoardController bc = new BoardController(TestValues.getTestState(), TestValues.getTestTemplate());
    for(int i=0; i<bc.gameState.getTeams().length; i++)
      bc.initializeTeam(i, TestValues.getTestTemplate());
    PiecePlacer pp = new PiecePlacer(bc.gameState, bc.boundaries);
    pp.randomPlacement(pp.gameState, 0);
    BoardController bc2 = new BoardController(TestValues.getTestState(), TestValues.getTestTemplate());
    for(int i=0; i<bc2.gameState.getTeams().length; i++)
      bc2.initializeTeam(i, TestValues.getTestTemplate());
    PiecePlacer pp2 = new PiecePlacer(bc2.gameState, bc2.boundaries);
    pp.hillClimbingSpacedPlaced(0, 100, pp2.gameState);
    
    ArrayList<GameState> stateList = new ArrayList<GameState>();
    stateList.add(pp.gameState);
    stateList.add(pp2.gameState);
    stateList.sort(new Comparator<GameState>() {
      @Override
      public int compare(GameState o1, GameState o2) {
        return pp.gameStatePossibleMoves(o2) - pp.gameStatePossibleMoves(o1);
      }
    });
    assertTrue(pp.gameStatePossibleMoves(stateList.get(0))  > pp.gameStatePossibleMoves(stateList.get(1)));
  }
  
  @Test
  void testPlacePiecesSpaced() {
    GameState gs = TestValues.getTestState();
    MapTemplate mt = TestValues.getTestTemplate();
    mt.setGridSize(new int[] {10,10});
    mt.setTeams(2);
//    mt.getPieces()[0].setCount(45);
    gs.setTeams(new Team[2]);
    BoardController bc = new BoardController(gs, mt);
    for(int i=0; i<bc.gameState.getTeams().length; i++)
      bc.initializeTeam(i, mt);
    PiecePlacer pp = new PiecePlacer(bc.gameState, bc.boundaries);
    pp.placePieces(PlacementType.spaced_out);
    printGrid(pp.gameState);
    for(int team=0; team < pp.gameState.getTeams().length; team++)
      System.out.println("team " + team + " got " + pp.numberPossibleMoves(pp.gameState, team) + " possible moves");
    System.out.println("total: " + pp.gameStatePossibleMoves(pp.gameState));
  }
  
  @Test
  void testRandomPlacement() {
    GameState gs = TestValues.getTestState();
    MapTemplate mt = TestValues.getTestTemplate();
    mt.setGridSize(new int[] {10,10});
    mt.setTeams(2);
//    mt.getPieces()[0].setCount(45);
    gs.setTeams(new Team[2]);
    BoardController bc = new BoardController(gs, mt);
    for(int i=0; i<bc.gameState.getTeams().length; i++)
      bc.initializeTeam(i, mt);
    PiecePlacer pp = new PiecePlacer(bc.gameState, bc.boundaries);
    pp.randomPlacement(pp.gameState, 0);
//    printGrid(pp.gameState);
  }
  
  @Test
  void testGetNeighbors() {
    GameState gs = TestValues.getTestState();
    MapTemplate mt = TestValues.getTestTemplate();
    mt.setGridSize(new int[] {30,30});
    mt.setTeams(3);
//    mt.getPieces()[0].setCount(145);
    gs.setTeams(new Team[3]);
    BoardController bc = new BoardController(gs, mt);
    for(int i=0; i<bc.gameState.getTeams().length; i++)
      bc.initializeTeam(i, mt);
    PiecePlacer pp = new PiecePlacer(bc.gameState, bc.boundaries);
    pp.getBestNeighbour(gs, 0, new int[] {0}).getPieceId();
  }
  
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
