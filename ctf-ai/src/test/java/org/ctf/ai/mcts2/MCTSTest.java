package org.ctf.ai.mcts2;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Arrays;
import org.ctf.ai.AI_Constants;
import org.ctf.ai.TestValues;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Piece;
import org.ctf.shared.state.data.map.Directions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MCTSTest {
  ReferenceGameState gameState;
  MCTS mcts;
  
  @BeforeEach
  void setUp() {
    gameState = new ReferenceGameState(TestValues.getTestState());
    TreeNode parent = new TreeNode(null, gameState, new int[] {0,0});
    mcts = new MCTS(parent);
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
    double expansions = 0;
    int count = 0;
    int timeInMilis = 100;
    int simulations = 0;
    int heuristics = 0;
    int crashes = 0;

    for(;count<100; count++) {
      MCTS mcts = new MCTS(this.mcts.root.clone(new ReferenceGameState(TestValues.getTestState())));
      try {
        mcts.getMove(timeInMilis, AI_Constants.C);
      } catch(NullPointerException npe) {crashes++;}
      expansions += mcts.expansionCounter.get();
      simulations += mcts.simulationCounter.get();
      heuristics += mcts.heuristicCounter.get();
    }
    simulations /= count;
    heuristics /= count;
    expansions = ((Math.round(((double)expansions/count)*1000))/1000.);

    System.out.println(count + " simulations with " + timeInMilis + " ms, average expansions/run: " + expansions
        + ",\nsimulations till the end: " + simulations + ", heuristic used: " + heuristics + "\nResults computed with " + crashes + " crashes");
  }
  
  @Test
  void testMctsWorks() throws InterruptedException {
    int randomTillEnd = 0;
    while(mcts.isTerminal(mcts.root) == -1) {
      randomTillEnd++;
      mcts.oneMove(mcts.root, mcts.root, true);
      mcts.root.printGrids();
      System.out.println("\nlastmove: " + mcts.root.gameState.getLastMove().getPiece().getId() + " to " +
          mcts.root.gameState.getLastMove().getNewPosition()[0] + " " + mcts.root.gameState.getLastMove().getNewPosition()[1] + "\n\n");
      mcts.removeTeamCheck(mcts.root.gameState);
    }


    gameState = new ReferenceGameState(TestValues.getTestState());
    TreeNode parent = new TreeNode(null, gameState, new int[] {0,0});
    mcts = new MCTS(parent);

    int mctsTillEnd = 0;
    while(mcts.isTerminal(mcts.root) == -1) {

      Move move = mcts.getMove(1000, AI_Constants.C);
      ++mctsTillEnd;      
      TreeNode tn = mcts.root;
      mcts.alterGameStateAndGrid(tn.gameState, new ReferenceMove(tn.gameState.getGrid().getPosition(move.getNewPosition()[1], move.getNewPosition()[0]).getPiece(), move.getNewPosition()));

      mcts.removeTeamCheck(mcts.root.gameState);
            System.out.println("\nROUND: " + mctsTillEnd + "\n" + mcts.printResults(tn.gameState.getLastMove().toMove()) + "\n");
            tn.printGrids();

      if(mcts.isTerminal(tn) != -1)
        break;

      tn = tn.clone(tn.gameState.clone());
      mcts.oneMove(tn, tn, false);
      ++mctsTillEnd;

      mcts.removeTeamCheck(mcts.root.gameState);
            System.out.println("\nROUND: " + mctsTillEnd + "\nRandom: Piece " + tn.gameState.getLastMove().getPiece().getId() + " to " 
            + tn.gameState.getLastMove().getNewPosition()[0] + "," + tn.gameState.getLastMove().getNewPosition()[1] + "\n");
            tn.printGrids();

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
  void testGetMove() {
    GameState gameState = TestValues.getEmptyTestState();
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
    parent.printGrids();

    Move move = mcts.getMove(100, Math.sqrt(2));

//    System.out.println(mcts.printResults(move));
    Piece picked = Arrays.asList(gameState.getTeams()[gameState.getCurrentTeam()].getPieces()).stream().filter(p -> p.getId().equals(move.getPieceId())).findFirst().get();
    ReferenceMove rmove = new ReferenceMove(picked, move.getNewPosition());
    
    mcts.alterGameStateAndGrid(mcts.root.gameState, rmove);
//    mcts.root.printGrid();

    assertEquals(1., mcts.root.children[0].getV());
    //    assertEquals(move.getNewPosition()[0], 0);
    //    assertEquals(move.getNewPosition()[1], 0);
  }
  
  @Test
  void testOneRandomMove() {
    GameState gameState = TestValues.getEmptyTestState();
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
        mcts.oneMove(mcts.root, mcts.root, false);
    }
  }
  
  @Test
  void testMoveOnBase() {
    GameState gameState = TestValues.getEmptyTestState();
    gameState.getTeams()[0].setBase(new int[] {0,0});
    gameState.getTeams()[0].setFlags(1);
    gameState.getTeams()
    [0].setId("0");
    Piece[] pieces0 = new Piece[1];
    pieces0[0] = new Piece();
    pieces0[0].setDescription(TestValues.getTestTemplate().getPieces()[2]);
    pieces0[0].setId("p:0_1");
    pieces0[0].setPosition(new int[] {7,9});
    pieces0[0].setTeamId("0");
    gameState.getTeams()[0].setPieces(pieces0);

    gameState.getTeams()[1].setBase(new int[] {9,9});
    gameState.getTeams()[1].setFlags(1);
    gameState.getTeams()[1].setId("1");
    Piece[] pieces1 = new Piece[1];
    pieces1[0] = new Piece();
    pieces1[0].setDescription(TestValues.getTestTemplate().getPieces()[2]);
    pieces1[0].setId("p:1_1");
    pieces1[0].setPosition(new int[] {2,0});
    pieces1[0].setTeamId("1");
    gameState.getTeams()[1].setPieces(pieces1);
    gameState.getGrid()[7][9] = pieces0[0].getId();
    gameState.getGrid()[2][0] = pieces1[0].getId();

    TreeNode parent = new TreeNode(null, gameState, new int[] {0,0});
    mcts = new MCTS(parent);

    Move move = mcts.getMove(1000, (float)Math.sqrt(2));
//    System.out.println("Piece: " + move.getPieceId() + " moves to " + move.getNewPosition()[0] + ", " + move.getNewPosition()[1]);
    for(int i=0; i<parent.possibleMoves.size(); i++) {
      if(-1 == mcts.isTerminal(mcts.root))
        mcts.oneMove(mcts.root, mcts.root, false);
    }
  }
  
  @Test
  void testTerminalHeuristic() {
    GameState gameState = TestValues.getEmptyTestState();
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
  void testgetAndRemoveMoveHeuristic() {
    GameState gameState = TestValues.getEmptyTestState();
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

    ReferenceMove move = mcts.getAndRemoveMoveHeuristic(mcts.root);
    assertEquals(0, move.getNewPosition()[0]);
    assertEquals(0, move.getNewPosition()[1]);

    gameState.setCurrentTeam(0);
    mcts = new MCTS(new TreeNode(null, gameState, null));
    move = mcts.getAndRemoveMoveHeuristic(mcts.root);
    assertEquals(2, move.getNewPosition()[0]);
    assertEquals(0, move.getNewPosition()[1]);
  }
  
  /**
   * This method tests if a piece can move onto a free position.
   */
  @Test
  void testOneRandomMove_freePosition() {
    TreeNode root = mcts.root;
    Piece onlyPiece = root.possibleMoves.keySet().iterator().next();
    int[] onlyPos = root.possibleMoves.get(onlyPiece).get(0);
    ArrayList<int[]> posList = new ArrayList<int[]>();
    posList.add(onlyPos);
    root.possibleMoves.clear();
    root.possibleMoves.put(onlyPiece, posList);

    ReferenceGameState gameState = root.gameState.clone();
    TreeNode alteredCopy = root.clone(gameState);
    root.children[0] = alteredCopy;
    mcts.oneMove(alteredCopy, root, false);

    GridObjectContainer old = root.gameState.getGrid().getGrid()[onlyPos[0]][onlyPos[1]];
    GridObjectContainer neW = root.children[0].gameState.getGrid().getGrid()[onlyPos[0]][onlyPos[1]];
    
    assertNull(old);
    assertNotNull(neW);
    assertEquals(neW.getPiece().getId(), onlyPiece.getId());
 }
  /**
   * This method tests if a piece can capture an occupied position.
   */
  @Test
  void testOneRandomMove_capturePosition() {
    TreeNode root = mcts.root;
    root.gameState.getTeams()[0].getPieces()[0].setPosition(new int[] {5,6});                       //place on the only square the other piece can move to. attack power should be equal
    root.gameState.getGrid().getGrid()[5][6] = new GridObjectContainer(GridObjects.piece, 0, root.gameState.getTeams()[0].getPieces()[0]);

    int piecesTeam0 = root.gameState.getTeams()[0].getPieces().length;

    mcts.oneMove(root, root, false);

    int piecesTeam0new = root.gameState.getTeams()[0].getPieces().length;

      assertNotEquals(piecesTeam0, piecesTeam0new);                                                   //a piece got captured, the Pieces Array got smaller
  }
  /**
   * This method tests if a piece can capture a flag.
   */
  @Test
  void testOneRandomMove_captureFlag() {
    TreeNode root = mcts.root;
    root.gameState.getTeams()[0].setFlags(2);                                                       //1 Flag should be captured
    root.gameState.getTeams()[0].setBase(new int[] {8,7});                                          //base team 1 is now at the targeted position 
    root.gameState.getGrid().setPosition(new GridObjectContainer(GridObjects.base, 0, null), 7, 8);

    int[] posPiece6 = root.gameState.getTeams()[1].getPieces()[5].getPosition();
    int[] posPiece8 = root.gameState.getTeams()[1].getPieces()[7].getPosition();
    mcts.oneMove(root, root, false);
    
    assertFalse(Arrays.equals(posPiece6,  root.gameState.getTeams()[1].getPieces()[5].getPosition()) &&
        Arrays.equals(posPiece8,  root.gameState.getTeams()[1].getPieces()[7].getPosition()));
    assertTrue(root.gameState.getTeams()[0].getFlags() == 1);
  }
  
  @Test
  void testIsTerminal_noMovesLeft() {
    //two teams
    GameState gameState = TestValues.getTestState();
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
    GameState gameState = TestValues.getTestState();
    gameState.setCurrentTeam(0);
    TreeNode parent = new TreeNode(null, gameState, new int[] {0,0});
    MCTS mcts = new MCTS(parent);

    assertEquals(-1, mcts.isTerminal(mcts.root));
  }
  
  @Test
  void testRemoveTeamCheck() {
    ReferenceGameState gameState = mcts.root.gameState;
    gameState.getTeams()[0].setFlags(0);
    mcts.removeTeamCheck(gameState);

    assertTrue(gameState.getTeams()[0] == null);
  }

  @Test
  void testIsFullyExpanded() {
    assertFalse(mcts.isFullyExpanded(mcts.root));
    mcts.root.possibleMoves.clear();
    assertTrue(mcts.isFullyExpanded(mcts.root));
  }
  
  @Test
  void testMultiSimulate() {
    long totalTime = 0;
    int count = 0;
    for(int i=0; i<1; i++, count++) {
      long time = System.nanoTime();
      mcts.multiSimulate(mcts.root);
      totalTime += System.nanoTime() - time;
      this.mcts = new MCTS(new TreeNode(null, TestValues.getTestState(), null));
    }
    System.out.println("average time for sim: " + (totalTime/count)/1000);
  }

  @Test
  void testSimulate() {
    long totalTime = 0;
    int count = 0;
    for(int i=0; i<10000; i++, count++) {
      long time = System.nanoTime();
      mcts.simulate(mcts.root);
      totalTime += System.nanoTime() - time;
      this.mcts = new MCTS(mcts.root.clone(mcts.root.gameState.clone()));
    }
    System.out.println("average time for sim: " + (totalTime/count)/1000);
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
    mcts.root.printGrids();
    mcts.expand(mcts.root);
    assertTrue(mcts.root.children[0] != null);
    mcts.root.children[0].printGrids();
    mcts.expand(mcts.root);
    mcts.root.children[1].printGrids();
    assertTrue(mcts.root.children[1] != null);
    assertTrue(mcts.root.children[2] == null);
  }

  @Test
  void testBackpropagate() {
    mcts.root.children[0] = mcts.expand(mcts.root);
    mcts.backpropagate(mcts.root.children[0], mcts.simulate(mcts.root.children[0]));

    assertTrue(Arrays.stream(mcts.root.children[0].wins).sum() > 0);
    assertTrue(Arrays.stream(mcts.root.wins).sum() > 0);
  }
  
  @Test
  void testBestChild() {
    mcts.root.children = new TreeNode[2];
    mcts.root.children[0] = new TreeNode(null, MCTS_Tools.toNextTeam(new ReferenceGameState(TestValues.getTestState())), new int[] {0,0});
    mcts.root.children[0].parent = mcts.root;
    mcts.root.children[1] = new TreeNode(null, MCTS_Tools.toNextTeam(new ReferenceGameState(TestValues.getTestState())), new int[] {0,0});
    mcts.root.children[1].parent = mcts.root;                                       //2 Kindknoten als Kinder von root initialisiert

    mcts.root.children[0].wins = new int[] {4, 0};                                  //Team 0 hat mehr wins als Team 1
    mcts.root.children[1].wins = new int[] {0, 12};                                 //Team 1 hat mehr wins als Team 0
    mcts.root.wins = new int[] {4, 12};
    TreeNode bestChild = mcts.bestChild(mcts.root, (float)Math.sqrt(2));
    assertEquals(bestChild, mcts.root.children[1]);                                 //Da Team 1 am Zug ist, ist der Knoten mit mehr wins von 1 besser

    mcts.root.gameState.setCurrentTeam(0);
    mcts.root.children[0].gameState.setCurrentTeam(1);
    mcts.root.children[1].gameState.setCurrentTeam(1);
    bestChild = mcts.bestChild(mcts.root, (float)Math.sqrt(2));
    assertEquals(bestChild, mcts.root.children[0]);                                 //Da Team 0 jetzt am Zug ist, ist der Knoten mit weniger wins von 1 besser
  }
  
  @Test
  void testGetRootBest() {
    mcts.root.children = new TreeNode[2];
    mcts.root.children[0] = new TreeNode(null, MCTS_Tools.toNextTeam(new ReferenceGameState(TestValues.getTestState())), new int[] {0,0});
    mcts.root.children[0].parent = mcts.root;
    mcts.root.children[1] = new TreeNode(null, MCTS_Tools.toNextTeam(new ReferenceGameState(TestValues.getTestState())), new int[] {0,0});
    mcts.root.children[1].parent = mcts.root;                                       //2 Kindknoten als Kinder von root initialisiert

    mcts.root.children[0].wins = new int[] {4, 0};                                  //Team 0 hat mehr wins als Team 1
    mcts.root.children[1].wins = new int[] {0, 12};                                 //Team 1 hat mehr wins als Team 0
    mcts.root.wins = new int[] {4, 12};
    TreeNode bestChild = mcts.getRootBest(mcts.root);
    assertEquals(bestChild, mcts.root.children[1]);                                 //Da Team 1 am Zug ist, ist der Knoten mit mehr wins von 1 besser

    mcts.root.gameState.setCurrentTeam(0);
    mcts.root.children[0].gameState.setCurrentTeam(1);
    mcts.root.children[1].gameState.setCurrentTeam(1);

    bestChild = mcts.getRootBest(mcts.root);
    assertEquals(bestChild, mcts.root.children[0]);                                 //Da Team 0 jetzt am Zug ist, ist der Knoten mit weniger wins von 1 besser
  }

  
  @Test
  void testAlterGameState() {
    TreeNode node = mcts.root;
    Piece piece = (Piece)node.possibleMoves.keySet().toArray()[0];
    ReferenceMove move = new ReferenceMove(piece, node.possibleMoves.get(piece).get(0));
    ReferenceGameState altered = node.gameState.clone();

    mcts.alterGameStateAndGrid(altered, move);

    assertNotEquals(node.gameState.getCurrentTeam(), altered.getCurrentTeam());
    
    GameState test = TestValues.getEmptyTestState();
    
  }
}
