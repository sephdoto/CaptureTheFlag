package de.unimannheim.swt.pse.ctf.game;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.function.Consumer;
import org.apache.commons.lang3.stream.Streams;
import org.junit.jupiter.api.Test;
import de.unimannheim.swt.pse.ctf.game.map.MapTemplate;
import de.unimannheim.swt.pse.ctf.game.map.PlacementType;
import de.unimannheim.swt.pse.ctf.game.state.GameState;
import de.unimannheim.swt.pse.ctf.game.state.Piece;
import de.unimannheim.swt.pse.ctf.game.state.Team;

/**
 * @author sistumpf
 */
class PiecePlacerTest {
  @Test
  void testDefensivePlacement() {
    GameState gameState = TestValues.getTestState();
    MapTemplate mt = TestValues.getTestTemplate();
    mt.setGridSize(new int[] {20,10});
    mt.setTeams(3);
//    mt.getPieces()[0].setCount(45);
    gameState.setTeams(new Team[3]);
    BoardController bc = new BoardController(gameState, mt);
    for(int i=0; i<bc.gameState.getTeams().length; i++)
      bc.initializeTeam(i, mt);
    PiecePlacer pp = new PiecePlacer(bc.gameState, bc.boundaries);
    pp.placePiecesDefensive();
    EngineTools.updateGrid(pp.gameState);
//    printGrid(pp.gameState);
//    for(int team=0; team < pp.gameState.getTeams().length; team++)
//      System.out.println("team " + team + " got " + pp.numberPossibleMoves(pp.gameState, team) + " possible moves");
//    System.out.println("total: " + pp.gameStatePossibleMoves(pp.gameState));
  }
  
  @Test
  void testStrengthPiecesComperator() {
    GameState gameState = TestValues.getTestState();
    MapTemplate mt = TestValues.getTestTemplate();
    mt.setGridSize(new int[] {10,10});
    mt.setTeams(2);
//    mt.getPieces()[0].setCount(45);
    gameState.setTeams(new Team[2]);
    BoardController bc = new BoardController(gameState, mt);
    for(int i=0; i<bc.gameState.getTeams().length; i++)
      bc.initializeTeam(i, mt);
    ArrayList<Piece> piecesByStrength = new ArrayList<Piece>();
    int team = 0;
    Streams.of(gameState.getTeams()[team].getPieces()).forEach(p -> piecesByStrength.add(p));
    piecesByStrength.sort(new Comparator<Piece>() {
      @Override
      public int compare(Piece p1, Piece p2) {
        return p2.getDescription().getAttackPower()- p1.getDescription().getAttackPower();
      }
    });
    for(int i=0; i<piecesByStrength.size()-1; i++)
      assertTrue(piecesByStrength.get(i).getDescription().getAttackPower() >= 
      piecesByStrength.get(i+1).getDescription().getAttackPower());
  }
  
  @Test
  void testHillClimbComperator() {
    BoardController bc = new BoardController(TestValues.getTestState(), TestValues.getTestTemplate());
    for(int i=0; i<bc.gameState.getTeams().length; i++)
      bc.initializeTeam(i, TestValues.getTestTemplate());
    PiecePlacer pp = new PiecePlacer(bc.gameState, bc.boundaries);
    pp.randomPlacement(pp.gameState, 0, new HashSet<Piece>());
    BoardController bc2 = new BoardController(TestValues.getTestState(), TestValues.getTestTemplate());
    for(int i=0; i<bc2.gameState.getTeams().length; i++)
      bc2.initializeTeam(i, TestValues.getTestTemplate());
    PiecePlacer pp2 = new PiecePlacer(bc2.gameState, bc2.boundaries);
    pp.hillClimbingSpacedPlaced(0, 100, pp2.gameState, new HashSet<Piece>(), false);
    
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
//    printGrid(pp.gameState);
//    for(int team=0; team < pp.gameState.getTeams().length; team++)
//      System.out.println("team " + team + " got " + pp.numberPossibleMoves(pp.gameState, team) + " possible moves");
//    System.out.println("total: " + pp.gameStatePossibleMoves(pp.gameState));
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
    pp.randomPlacement(pp.gameState, 0, new HashSet<Piece>());
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
    pp.getBestNeighbour(gs, new HashSet<Piece>(), 0, new int[] {0}).getPiece();
  }
  
  @Test
  void testGetShuffledNeighbors() {
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
    pp.placePiecesSymmetrical();
    HashSet<Piece> pieces = new HashSet<Piece>();
    pieces.add(gs.getTeams()[0].getPieces()[0]);
    pieces.add(gs.getTeams()[0].getPieces()[1]);
    ReferenceMove rm = pp.getShuffledNeighbour(gs, pieces, 0, new int[] {1});
//    System.out.println(gs.getTeams()[0].getPieces()[0].getId() + " " + gs.getTeams()[0].getPieces()[0].getPosition()[0] + " " + gs.getTeams()[0].getPieces()[0].getPosition()[1]);
//    System.out.println(gs.getTeams()[0].getPieces()[1].getId() + " " + gs.getTeams()[0].getPieces()[1].getPosition()[0] + " " + gs.getTeams()[0].getPieces()[1].getPosition()[1]);
//    System.out.println(rm.getPiece().getId() + " " + rm.getNewPosition()[0] + " " + rm.getNewPosition()[1]);
    assertTrue(gs.getTeams()[0].getPieces()[0].getId().equals(rm.getPiece().getId()) ||
        gs.getTeams()[0].getPieces()[1].getId().equals(rm.getPiece().getId()));
    assertTrue(Arrays.equals(rm.getNewPosition(), gs.getTeams()[0].getPieces()[0].getPosition()) || 
        Arrays.equals(rm.getNewPosition(), gs.getTeams()[0].getPieces()[1].getPosition()));
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
