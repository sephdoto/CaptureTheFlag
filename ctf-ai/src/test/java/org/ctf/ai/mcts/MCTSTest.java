package org.ctf.ai.mcts;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Arrays;
import org.ctf.ai.TestValues;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Piece;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MCTSTest {
  static MCTS mcts;
  static GameState gameState;

  @BeforeEach
  void setUp() {
    gameState = TestValues.getTestState();
    TreeNode parent = new TreeNode(null, gameState, new int[] {0,0});
    mcts = new MCTS(parent);
  }

  @Test
  void testGetMove() {
    gameState = TestValues.getEmptyTestState();
    gameState.getTeams()[0].setBase(new int[] {0,0});
    gameState.getTeams()[0].setFlags(1);
    gameState.getTeams()[0].setId("0");
    Piece[] pieces0 = new Piece[1];
    pieces0[0] = new Piece();
    pieces0[0].setDescription(TestValues.getTestTemplate().getPieces()[2]);
    pieces0[0].setId("p:0_1");
    pieces0[0].setPosition(new int[] {0,1});
    pieces0[0].setTeamId("0");
    gameState.getTeams()[0].setPieces(pieces0);
    
    gameState.getTeams()[1].setBase(new int[] {9,9});
    gameState.getTeams()[1].setFlags(1);
    gameState.getTeams()[1].setId("1");
    Piece[] pieces1 = new Piece[1];
    pieces1[0] = new Piece();
    pieces1[0].setDescription(TestValues.getTestTemplate().getPieces()[2]);
    pieces1[0].setId("p:1_1");
    pieces1[0].setPosition(new int[] {9,8});
    pieces1[0].setTeamId("1");
    gameState.getTeams()[1].setPieces(pieces1);
    gameState.getGrid()[0][1] = pieces0[0].getId();
    gameState.getGrid()[9][8] = pieces1[0].getId();
    
    TreeNode parent = new TreeNode(null, gameState, new int[] {0,0});
    mcts = new MCTS(parent);
 
//    Move move = mcts.getMove(1000, (float)Math.sqrt(2));
//    System.out.println("Piece: " + move.getPieceId() + " moves to " + move.getNewPosition()[0] + ", " + move.getNewPosition()[1]);
  for(int i=0; i<100000; i++)
    mcts.oneRandomMove(mcts.root);
  }

  @Test
  void testSelectAndExpand() {
    fail("Not yet implemented");
  }

  @Test
  void testExpand() {
    fail("Not yet implemented");
  }

  @Test
  void testSimulate() {
    fail("Not yet implemented");
  }

  @Test
  void testTerminalHeuristic() {
    fail("Not yet implemented");
  }

  @Test
  void testPickField() {
    fail("Not yet implemented");
  }

  @Test
  void testBackpropagate() {
    fail("Not yet implemented");
  }

  @Test
  void testIsTerminal() {
    fail("Not yet implemented");
  }

  @Test
  void testIsFullyExpanded() {
    fail("Not yet implemented");
  }

  @Test
  void testBestChild() {
    mcts.root.children = new TreeNode[2];
    mcts.root.children[0] = new TreeNode(null, gameState, new int[] {0,0});
    mcts.root.children[0].parent = mcts.root;
    mcts.root.children[1] = new TreeNode(null, gameState, new int[] {0,0});
    mcts.root.children[1].parent = mcts.root;										//2 Kindknoten als Kinder von root initialisiert

    mcts.root.children[0].wins = new int[] {4, 0};									//Team 0 hat mehr wins als Team 1
    mcts.root.children[1].wins = new int[] {0, 12};									//Team 1 hat mehr wins als Team 0
    mcts.root.wins = new int[] {4, 12};
    TreeNode bestChild = mcts.bestChild(mcts.root, (float)Math.sqrt(2));
    assertEquals(bestChild, mcts.root.children[1]);									//Da Team 1 am Zug ist, ist der Knoten mit mehr wins von 1 besser

    mcts.root.gameState.setCurrentTeam(0);
    bestChild = mcts.bestChild(mcts.root, (float)Math.sqrt(2));
    assertEquals(bestChild, mcts.root.children[0]);									//Da Team 0 jetzt am Zug ist, ist der Knoten mit weniger wins von 1 besser
  }

  @Test
  void testGetRootBest() {
    mcts.root.children = new TreeNode[2];
    mcts.root.children[0] = new TreeNode(null, gameState, new int[] {0,0});
    mcts.root.children[0].parent = mcts.root;
    mcts.root.children[1] = new TreeNode(null, gameState, new int[] {0,0});
    mcts.root.children[1].parent = mcts.root;										//2 Kindknoten als Kinder von root initialisiert

    mcts.root.children[0].wins = new int[] {4, 0};									//Team 0 hat mehr wins als Team 1
    mcts.root.children[1].wins = new int[] {0, 12};									//Team 1 hat mehr wins als Team 0
    mcts.root.wins = new int[] {4, 12};
    TreeNode bestChild = mcts.getRootBest(mcts.root);
    assertEquals(bestChild, mcts.root.children[1]);									//Da Team 1 am Zug ist, ist der Knoten mit mehr wins von 1 besser

    mcts.root.gameState.setCurrentTeam(0);
    bestChild = mcts.getRootBest(mcts.root);
    assertEquals(bestChild, mcts.root.children[0]);									//Da Team 0 jetzt am Zug ist, ist der Knoten mit weniger wins von 1 besser
  }

  /**
   * This method tests if a piece can move onto a free position.
   */
  @Test
  void testOneRandomMove_freePosition() {
    TreeNode root = mcts.root;
    String onlyString = root.possibleMoves.keySet().iterator().next();
    int[] onlyPos = root.possibleMoves.get(onlyString).get(0);
    ArrayList<int[]> posList = new ArrayList<int[]>();
    posList.add(onlyPos);
    root.possibleMoves.clear();
    root.possibleMoves.put(onlyString, posList);

    int[] piece1_8_pos = root.gameState.getTeams()[1].getPieces()[7].getPosition();

    root.children[0] = mcts.oneRandomMove(root);
    int[] piece1_8_pos_new = root.children[0].gameState.getTeams()[1].getPieces()[7].getPosition();

    assertFalse(Arrays.equals(piece1_8_pos, piece1_8_pos_new));
    assertNotEquals(root.gameState.getGrid()[8][5], root.children[0].gameState.getGrid()[8][5]);
  }
  /**
   * This method tests if a piece can capture an occupied position.
   */
  @Test
  void testOneRandomMove_capturePosition() {
    TreeNode root = mcts.root;
    String onlyString = root.possibleMoves.keySet().iterator().next();
    int[] onlyPos = root.possibleMoves.get(onlyString).get(0);
    ArrayList<int[]> posList = new ArrayList<int[]>();
    posList.add(onlyPos);
    root.possibleMoves.clear();
    root.possibleMoves.put(onlyString, posList);
    root.gameState.getTeams()[0].getPieces()[0].setPosition(new int[] {8,7});                       //place on the only square the other piece can move to. attack power should be equal
    root.gameState.getGrid()[8][7] = root.gameState.getTeams()[0].getPieces()[0].getId();           //id placed on grid, I dont have to clear the old position for this test

    int[] piece1_8_pos = root.gameState.getTeams()[1].getPieces()[7].getPosition();
    int piecesTeam0 = root.gameState.getTeams()[0].getPieces().length;

    root.children[0] = mcts.oneRandomMove(root);
    int[] piece1_8_pos_new = root.children[0].gameState.getTeams()[1].getPieces()[7].getPosition();
    int piecesTeam0new = root.children[0].gameState.getTeams()[0].getPieces().length;
    
    assertFalse(Arrays.equals(piece1_8_pos, piece1_8_pos_new));
    assertNotEquals(piecesTeam0, piecesTeam0new);                                                   //a piece got captured, the Pieces Array got smaller
  }
  /**
   * This method tests if a piece can capture a flag.
   */
  @Test
  void testOneRandomMove_captureFlag() {
    TreeNode root = mcts.root;
    String onlyString = root.possibleMoves.keySet().iterator().next();
    int[] onlyPos = root.possibleMoves.get(onlyString).get(0);
    ArrayList<int[]> posList = new ArrayList<int[]>();
    posList.add(onlyPos);
    root.possibleMoves.clear();
    root.possibleMoves.put(onlyString, posList);
    root.gameState.getTeams()[0].setFlags(2);                                                       //1 Flag should be captured
    root.gameState.getTeams()[0].setBase(new int[] {8,7});                                          //base team 1 is now at the targeted position 
    root.gameState.getGrid()[8][7] = "b:0";

    int[] piece1_8_pos = root.gameState.getTeams()[1].getPieces()[7].getPosition();
   
    root.children[0] = mcts.oneRandomMove(root);

    int[] piece1_8_pos_new = root.children[0].gameState.getTeams()[1].getPieces()[7].getPosition();
 
    assertFalse(Arrays.equals(piece1_8_pos, piece1_8_pos_new));
    assertArrayEquals(new int[] {8,8}, root.children[0].gameState.getTeams()[1].getPieces()[7].getPosition());
    assertTrue(root.children[0].gameState.getTeams()[0].getFlags() == 1);
  }

  @Test
  void testRemoveTeamCheck() {
    GameState gameState = mcts.root.gameState;
    String teamId1 = gameState.getTeams()[1].getId();
    gameState.getTeams()[0].setFlags(0);
    mcts.removeTeamCheck(gameState);

    assertTrue(1 == gameState.getTeams().length);
    assertEquals(teamId1, gameState.getTeams()[0].getId());
  }

  @Test
  void testPrintResults() {
    fail("Not yet implemented");
  }

}
