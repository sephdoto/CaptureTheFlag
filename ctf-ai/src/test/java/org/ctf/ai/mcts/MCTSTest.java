package org.ctf.ai.mcts;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Arrays;
import org.ctf.ai.TestValues;
import org.ctf.shared.ai.AI_Tools;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Piece;
import org.ctf.shared.state.data.map.Directions;
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
  void bestMctsAlgorithm() {
    gameState = TestValues.getTestState();
    TreeNode parent = new TreeNode(null, gameState, new int[] {0,0});
    MCTS_TestDouble mcts = new MCTS_TestDouble(parent);

    int mctsTillEnd = 0;
    int timeForMove = 50;

    while(mcts.isTerminal(mcts.root) == -1) {
      mcts = new MCTS_TestDouble(mcts.root.clone((mcts.root.gameState)));

      Move move = mcts.getMove(timeForMove, Constants.C);
      ++mctsTillEnd;      
      TreeNode tn = mcts.root;
      mcts.alterGameState(tn.gameState, move);

      mcts.removeTeamCheck(mcts.root.gameState);
            System.out.println("\nROUND: " + mctsTillEnd + ", MCTS_TestDouble\n" + mcts.printResults(tn.gameState.getLastMove()) + "\n");
            tn.printGrid();

      if(mcts.isTerminal(tn) != -1)
        break;

      clearNodeParentAndChildren(tn);

      tn = tn.clone((tn.gameState));

      MCTS mcts2 = new MCTS(tn);
      move = mcts2.getMove(timeForMove, Constants.C);
      ++mctsTillEnd;      
      tn = mcts2.root;
      mcts2.alterGameState(tn.gameState, move);

      mcts2.removeTeamCheck(mcts2.root.gameState);
            System.out.println("\nROUND: " + mctsTillEnd + ", MCTS\n" + mcts2.printResults(tn.gameState.getLastMove()) + "\n");
            tn.printGrid();

      clearNodeParentAndChildren(tn);

      mcts = new MCTS_TestDouble(tn);
    }
        System.out.println("\n\nWinner is... " + (mctsTillEnd%2!=0 ? "MCTS_TestDouble " : "MCTS"));
  }

  void clearNodeParentAndChildren(TreeNode tn) {
    if(tn.parent != null)
      for(int i=0; i<tn.parent.children.length; i++) {
        tn.parent.children[i] = null;
      }
    tn.parent = null;
    for(int i = 0; i<tn.children.length; i++)
      tn.children[i] = null;
    tn.wins = new int[] {0,0};
  }

  @Test
  void testPerformance() throws InterruptedException {
    long expansions = 0;
    int count = 0;
    int timeInMilis = 5000;
    int[] wins = new int[] {0,0};
    int crashes = 0;

    for(;count<2; count++) {

      //      MCTS_TestDouble mcts = new MCTS_TestDouble(MCTSTest.mcts.root.clone(MCTSTest.mcts.root.copyGameState()));
      MCTS mcts = new MCTS(MCTSTest.mcts.root.clone(MCTSTest.mcts.root.copyGameState()));
      mcts.root.wins = new int[] {0,0};
      try {
        mcts.getMove(timeInMilis, Constants.C);
      } catch(NullPointerException npe) {crashes++;}
      expansions += mcts.expansionCounter.get();
      wins[0] += mcts.root.wins[0];
      wins[1] += mcts.root.wins[1];
    }
    wins[0] /= count;
    wins[1] /= count;

    System.out.println(count + " simulations with " + timeInMilis + " ms, average expansions/run: " + ((Math.round(((double)expansions/count)*1000))/1000.)
        + ",\tavg. wins: [" + wins[0] + ", " + wins[1] + "]" + "\nResults computed with " + crashes + " crashes");
  }

  @Test
  void testGetMove() {
    gameState = TestValues.getEmptyTestState();
    gameState.getTeams()[0].setBase(new int[] {0,0});
    gameState.getTeams()[0].setFlags(1);
    gameState.getTeams()[0].setId("0");
    Piece[] pieces0 = new Piece[1];
    pieces0[0] = new Piece();
    pieces0[0].setDescription(TestValues.getTestTemplate().getPieces()[4]);
    pieces0[0].setId("p:0_1");
    pieces0[0].setPosition(new int[] {0,1});
    pieces0[0].setTeamId("0");
    gameState.getTeams()[0].setPieces(pieces0);

    gameState.getTeams()[1].setBase(new int[] {9,9});
    gameState.getTeams()[1].setFlags(1);
    gameState.getTeams()[1].setId("1");
    Piece[] pieces1 = new Piece[1];
    pieces1[0] = new Piece();
    pieces1[0].setDescription(TestValues.getTestTemplate().getPieces()[4]);
    pieces1[0].setId("p:1_1");
    pieces1[0].setPosition(new int[] {2,0});
    pieces1[0].setTeamId("1");
    gameState.getTeams()[1].setPieces(pieces1);
    gameState.getGrid()[0][1] = pieces0[0].getId();
    gameState.getGrid()[2][0] = pieces1[0].getId();

    gameState.setCurrentTeam(1);
    TreeNode parent = new TreeNode(null, gameState, new int[] {0,0});
    mcts = new MCTS(parent);

    System.out.println("parent Grid:");
    parent.printGrid();

    Move move = mcts.getMove(100, Math.sqrt(2));

    System.out.println(mcts.printResults(move));
    mcts.alterGameState(mcts.root.gameState, move);
    mcts.root.printGrid();

    assertEquals(mcts.root.children[0].getV(), 1.);
    //    assertEquals(move.getNewPosition()[0], 0);
    //    assertEquals(move.getNewPosition()[1], 0);
  }

  @Test
  void testMctsWorks() {
    int randomTillEnd = 0;
    while(mcts.isTerminal(mcts.root) == -1) {
      randomTillEnd++;
      mcts.oneMove(mcts.root, mcts.root);
      mcts.removeTeamCheck(mcts.root.gameState);
    }

    gameState = TestValues.getTestState();
    TreeNode parent = new TreeNode(null, gameState, new int[] {0,0});
    mcts = new MCTS(parent);

    int mctsTillEnd = 0;
    while(mcts.isTerminal(mcts.root) == -1) {

      Move move = mcts.getMove(1000, Constants.C);
      ++mctsTillEnd;      
      TreeNode tn = mcts.root;
      mcts.alterGameState(tn.gameState, move);

      mcts.removeTeamCheck(mcts.root.gameState);
      //      System.out.println("\nROUND: " + mctsTillEnd + "\n" + mcts.printResults(tn.gameState.getLastMove()) + "\n");
      //      tn.printGrid();

      if(mcts.isTerminal(tn) != -1)
        break;

      tn = tn.clone(tn.copyGameState());
      mcts.oneMove(tn, tn);
      ++mctsTillEnd;

      mcts.removeTeamCheck(mcts.root.gameState);
      //      System.out.println("\nROUND: " + mctsTillEnd + "\nRandom: Piece " + tn.gameState.getLastMove().getPieceId() + " to " 
      //      + tn.gameState.getLastMove().getNewPosition()[0] + "," + tn.gameState.getLastMove().getNewPosition()[1] + "\n");
      //      tn.printGrid();

      for(int i=0; i<tn.parent.children.length; i++) {
        tn.parent.children[i] = null;
      }
      tn.parent = null;
      for(int i = 0; i<tn.children.length; i++)
        tn.children[i] = null;
      tn.wins = new int[] {0,0};


      mcts = new MCTS(tn);
    }
    System.out.println("\"testMctsWorks\": random steps till end: " + randomTillEnd + ", mcts steps till end: " + mctsTillEnd);
    assertTrue(mctsTillEnd < randomTillEnd);
  }

  @Test
  void testOneRandomMove() {
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

    Move move = mcts.getMove(1000, (float)Math.sqrt(2));
//    System.out.println("Piece: " + move.getPieceId() + " moves to " + move.getNewPosition()[0] + ", " + move.getNewPosition()[1]);
    for(int i=0; i<parent.possibleMoves.size(); i++) {
      if(-1 == mcts.isTerminal(mcts.root))
        mcts.oneMove(mcts.root, mcts.root);
    }
  }

  @Test
  void testSelectAndExpand() {
    TreeNode firstChild = mcts.selectAndExpand(mcts.root, 1);
    assertEquals(firstChild, mcts.root.children[0]);
    TreeNode secondChild = mcts.selectAndExpand(mcts.root, 1);
    assertEquals(secondChild, mcts.root.children[1]);
  }

  @Test
  void testExpand() {
    mcts.expand(mcts.root);
    assertTrue(mcts.root.children[0] != null);
    mcts.expand(mcts.root);
    assertTrue(mcts.root.children[1] != null);
    assertTrue(mcts.root.children[2] == null);
  }

  @Test
  /**
   * this only works if simulations and heuristics are both valued as 1 in MCTS.simulate
   */
  void testMultiSimulate() {
    long time = System.currentTimeMillis();
    int[] winners = mcts.multiSimulate(mcts.root);
    System.out.println("time for multiSim: " + (System.currentTimeMillis() - time));

//    assertEquals(Arrays.stream(winners).sum(), Constants.numThreads);
  }

  @Test
  void testSimulate() {
    long time = System.currentTimeMillis();
    int[] winners = mcts.simulate(mcts.root);
    System.out.println("time for sim: " + (System.currentTimeMillis() - time));

    assertEquals(Arrays.stream(winners).sum(), 1);
  }

  @Test
  void testTerminalHeuristic() {
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
    pieces1[0].setDescription(TestValues.getTestTemplate().getPieces()[1]);
    pieces1[0].getDescription().setAttackPower(3);
    pieces1[0].setId("p:1_1");
    pieces1[0].setPosition(new int[] {2,0});
    pieces1[0].setTeamId("1");
    gameState.getTeams()[1].setPieces(pieces1);
    gameState.getGrid()[0][1] = pieces0[0].getId();
    gameState.getGrid()[2][0] = pieces1[0].getId();
    MCTS mcts = new MCTS(new TreeNode(null, gameState, null));

    assertTrue(mcts.terminalHeuristic(mcts.root) == 1);
    mcts.root.gameState.setCurrentTeam(0);

    //TODO: Die Heuristik kann noch nicht erkennen, was geschlagen werden kann
    assertFalse(mcts.terminalHeuristic(mcts.root) == 0);
  }


  @Test
  void testBackpropagate() {
    mcts.root.children[0] = mcts.expand(mcts.root);
    mcts.backpropagate(mcts.root.children[0], mcts.simulate(mcts.root.children[0]));

    assertTrue(Arrays.stream(mcts.root.children[0].wins).sum() > 0);
    assertTrue(Arrays.stream(mcts.root.wins).sum() > 0);
  }

  @Test
  void testgetAndRemoveMoveHeuristic() {
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
    pieces1[0].setDescription(TestValues.getTestTemplate().getPieces()[1]);
    pieces1[0].getDescription().setAttackPower(3);
    pieces1[0].setId("p:1_1");
    pieces1[0].setPosition(new int[] {2,0});
    pieces1[0].setTeamId("1");
    gameState.getTeams()[1].setPieces(pieces1);
    gameState.getGrid()[0][1] = pieces0[0].getId();
    gameState.getGrid()[2][0] = pieces1[0].getId();
    MCTS mcts = new MCTS(new TreeNode(null, gameState, null));

    Move move = mcts.getAndRemoveMoveHeuristic(mcts.root);
    assertEquals(0, move.getNewPosition()[0]);
    assertEquals(0, move.getNewPosition()[1]);

    gameState.setCurrentTeam(0);
    mcts = new MCTS(new TreeNode(null, gameState, null));
    move = mcts.getAndRemoveMoveHeuristic(mcts.root);
    assertEquals(2, move.getNewPosition()[0]);
    assertEquals(0, move.getNewPosition()[1]);
  }

  @Test
  void testIsTerminal_noMovesLeft() {
    //two teams
    gameState = TestValues.getTestState();
    gameState.setCurrentTeam(0);
    for(Piece p : gameState.getTeams()[0].getPieces()) {
      Directions d = p.getDescription().getMovement().getDirections();
      d.setDown(0);
      d.setDownLeft(0);
      d.setDownRight(0);
      d.setLeft(0);
      d.setRight(0);
      d.setUp(0);
      d.setUpLeft(0);
      d.setUpRight(0);
    }
    TreeNode parent = new TreeNode(null, gameState, new int[] {0,0});
    MCTS mcts = new MCTS(parent);

    assertEquals(1, mcts.isTerminal(mcts.root));

    //TODO test for 3 teams
  }
  @Test
  void testIsTerminal_movesLeft() {
    gameState = TestValues.getTestState();
    gameState.setCurrentTeam(0);
    TreeNode parent = new TreeNode(null, gameState, new int[] {0,0});
    MCTS mcts = new MCTS(parent);

    assertEquals(-1, mcts.isTerminal(mcts.root));
  }


  @Test
  void testIsFullyExpanded() {
    assertFalse(mcts.isFullyExpanded(mcts.root));
    mcts.root.possibleMoves.clear();
    assertTrue(mcts.isFullyExpanded(mcts.root));
  }

  @Test
  void testBestChild() {
    mcts.root.children = new TreeNode[2];
    mcts.root.children[0] = new TreeNode(null, AI_Tools.toNextTeam(mcts.root.copyGameState()), new int[] {0,0});
    mcts.root.children[0].parent = mcts.root;
    mcts.root.children[1] = new TreeNode(null, AI_Tools.toNextTeam(mcts.root.copyGameState()), new int[] {0,0});
    mcts.root.children[1].parent = mcts.root;										//2 Kindknoten als Kinder von root initialisiert

    mcts.root.children[0].wins = new int[] {4, 0};									//Team 0 hat mehr wins als Team 1
    mcts.root.children[1].wins = new int[] {0, 12};									//Team 1 hat mehr wins als Team 0
    mcts.root.wins = new int[] {4, 12};
    TreeNode bestChild = mcts.bestChild(mcts.root, (float)Math.sqrt(2));
    assertEquals(bestChild, mcts.root.children[1]);									//Da Team 1 am Zug ist, ist der Knoten mit mehr wins von 1 besser

    mcts.root.gameState.setCurrentTeam(0);
    mcts.root.children[0].gameState.setCurrentTeam(1);
    mcts.root.children[1].gameState.setCurrentTeam(1);
    bestChild = mcts.bestChild(mcts.root, (float)Math.sqrt(2));
    assertEquals(bestChild, mcts.root.children[0]);									//Da Team 0 jetzt am Zug ist, ist der Knoten mit weniger wins von 1 besser
  }

  @Test
  void testGetRootBest() {
    mcts.root.children = new TreeNode[2];
    mcts.root.children[0] = new TreeNode(null, AI_Tools.toNextTeam(mcts.root.copyGameState()), new int[] {0,0});
    mcts.root.children[0].parent = mcts.root;
    mcts.root.children[1] = new TreeNode(null, AI_Tools.toNextTeam(mcts.root.copyGameState()), new int[] {0,0});
    mcts.root.children[1].parent = mcts.root;										//2 Kindknoten als Kinder von root initialisiert

    mcts.root.children[0].wins = new int[] {4, 0};									//Team 0 hat mehr wins als Team 1
    mcts.root.children[1].wins = new int[] {0, 12};									//Team 1 hat mehr wins als Team 0
    mcts.root.wins = new int[] {4, 12};
    TreeNode bestChild = mcts.getRootBest(mcts.root);
    assertEquals(bestChild, mcts.root.children[1]);									//Da Team 1 am Zug ist, ist der Knoten mit mehr wins von 1 besser

    mcts.root.gameState.setCurrentTeam(0);
    mcts.root.children[0].gameState.setCurrentTeam(1);
    mcts.root.children[1].gameState.setCurrentTeam(1);

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

    GameState gameState = AI_Tools.toNextTeam(root.copyGameState());
    mcts.oneMove(root, root);
    root.children[0] = mcts.root;
    int[] piece1_8_pos_new = root.children[0].gameState.getTeams()[1].getPieces()[7].getPosition();

    assertFalse(Arrays.equals(piece1_8_pos, piece1_8_pos_new));
    assertNotEquals(gameState.getGrid()[8][5], root.children[0].gameState.getGrid()[8][5]);
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

    mcts.oneMove(root, root);
    root.children[0] = mcts.root;
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

    mcts.oneMove(root, root);
    root.children[0] = mcts.root;

    int[] piece1_8_pos_new = root.children[0].gameState.getTeams()[1].getPieces()[7].getPosition();

    assertFalse(Arrays.equals(piece1_8_pos, piece1_8_pos_new));
    assertArrayEquals(new int[] {8,8}, root.children[0].gameState.getTeams()[1].getPieces()[7].getPosition());
    assertTrue(root.children[0].gameState.getTeams()[0].getFlags() == 1);
  }

  @Test
  void testRemoveTeamCheck() {
    GameState gameState = mcts.root.gameState;
    gameState.getTeams()[0].setFlags(0);
    mcts.removeTeamCheck(gameState);

    assertTrue(gameState.getTeams()[0] == null);
  }

  @Test
  void testAlterGameState() {
    TreeNode node = mcts.root;
    String pieceId = (String)node.possibleMoves.keySet().toArray()[0];
    Move move = new Move();
    move.setNewPosition(node.possibleMoves.get(pieceId).get(0));
    move.setPieceId(pieceId);
    GameState altered = node.copyGameState();

    mcts.alterGameState(altered, move);

    assertNotEquals(node.gameState.getCurrentTeam(), altered.getCurrentTeam());
  }
}
