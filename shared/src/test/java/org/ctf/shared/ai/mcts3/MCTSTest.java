package org.ctf.shared.ai.mcts3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.Arrays;
import org.ctf.shared.ai.AIConfig;
import org.ctf.shared.ai.ReferenceMove;
import org.ctf.shared.ai.TestValues;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Piece;
import org.ctf.shared.state.data.map.Directions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author sistumpf
 */
class MCTSTest {
  ReferenceGameState gameState;
  MCTS mcts;
  
  @BeforeEach
  void setUp() {
    gameState = new ReferenceGameState(TestValues.getTestState());
    TreeNode parent = new TreeNode(null, gameState, new int[] {0,0}, new ReferenceMove(null, new int[2]));
    mcts = new MCTS(parent, new AIConfig());
  }
  
  void clearNodeParentAndChildren(TreeNode tn) {
    if(tn.getParent() != null)
      for(int i=0; i<tn.getParent().getChildren().length; i++) {
        tn.getParent().getChildren()[i] = null;
      }
    tn.setParent(null);
    for(int i = 0; i<tn.getChildren().length; i++)
      tn.getChildren()[i] = null;
    tn.setWins(new int[] {0,0});
  }
  
  @Test
  void testMakeEmptyMove() {
    GameState gameState = TestValues.getTestState();
    gameState.setLastMove(null);
    ReferenceGameState gs = new ReferenceGameState(gameState);
    MCTS mcts = new MCTS(new TreeNode(null, gs, null, new ReferenceMove(null, new int[2])), new AIConfig());
    mcts.getMove(100);
  }

//  @Test
  void testPerformance() throws InterruptedException {
    double expansions = 0;
    int count = 0;
    int timeInMilis = 10000;
    int simulations = 0;
    int heuristics = 0;
    int crashes = 0;

    for(;count<1; count++) {
      MCTS mcts = new MCTS(this.mcts.getRoot().clone(new ReferenceGameState(TestValues.getTestState())), new AIConfig());
      try {
        mcts.getMove(timeInMilis);
      } catch(NullPointerException npe) {crashes++;}
      expansions += mcts.getExpansionCounter().get();
      simulations += mcts.simulationCounter.get();
      heuristics += mcts.heuristicCounter.get();
    }
    simulations /= count;
    heuristics /= count;
    expansions = ((Math.round(((double)expansions/count)*1000))/1000.);

    System.out.println(count + " simulations with " + timeInMilis + " ms, average expansions/run: " + expansions
        + ",\nsimulations till the end: " + simulations + ", heuristic used: " + heuristics + "\nResults computed with " + crashes + " crashes");
  }
  
//  @Test   TODO funktioniert aber sollte nicht beim testen ausgeführt werden, dauert so lang
  void testMctsWorks() throws InterruptedException {
    int randomTillEnd = 0;
    while(mcts.isTerminal(mcts.getRoot().getReferenceGameState(), new ReferenceMove(null, new int[2])) == -1) {
      randomTillEnd++;
      mcts.oneMove(mcts.getRoot(), mcts.getRoot(), true, new ReferenceMove(null, new int[2]));
//      mcts.root.printGrid();
//      System.out.println("\nlastmove: " + mcts.root.gameState.getLastMove().getPiece().getId() + " to " +
//          mcts.root.gameState.getLastMove().getNewPosition()[0] + " " + mcts.root.gameState.getLastMove().getNewPosition()[1] + "\n\n");
      mcts.removeTeamCheck(mcts.getRoot().getReferenceGameState());
    }


    gameState = new ReferenceGameState(TestValues.getTestState());
    TreeNode parent = new TreeNode(null, gameState, new int[] {0,0}, new ReferenceMove(null, new int[2]));

    int mctsTillEnd = 0;

    MCTS gameMCTS = new MCTS(parent, new AIConfig());
    while(gameMCTS.isTerminal(gameMCTS.getRoot().getReferenceGameState(), new ReferenceMove(null, new int[2])) == -1) {
      gameMCTS.setExpansionCounter(0);
      gameMCTS.simulationCounter.set(0);
      gameMCTS.heuristicCounter.set(0);
      Move move = gameMCTS.getMove(1000);
      ++mctsTillEnd;      
      
      System.out.println("\nROUND: " + mctsTillEnd + "\n" + gameMCTS.printResults(move) + "\n");
      
      gameMCTS.alterGameState(gameMCTS.getRoot().getReferenceGameState(), new ReferenceMove(gameMCTS.getRoot().getReferenceGameState(), move));

      gameMCTS.getRoot().printGrid();
      
      if(gameMCTS.isTerminal(gameMCTS.getRoot().getReferenceGameState(), new ReferenceMove(null, new int[2])) != -1)
        break;
      gameMCTS.getRoot().initPossibleMovesAndChildren();
      gameMCTS.oneMove(gameMCTS.getRoot(), gameMCTS.getRoot(), false, new ReferenceMove(null, new int[2]));
      ++mctsTillEnd;


      gameMCTS.removeTeamCheck(gameMCTS.getRoot().getReferenceGameState());
      System.out.println("\nROUND: " + mctsTillEnd + "\nRandom: Piece " + gameMCTS.getRoot().getReferenceGameState().getLastMove().getPiece().getId() + " to " 
          + gameMCTS.getRoot().getReferenceGameState().getLastMove().getNewPosition()[0] + "," + gameMCTS.getRoot().getReferenceGameState().getLastMove().getNewPosition()[1] + "\n");
      gameMCTS.getRoot().printGrid();

      if(gameMCTS.getRoot().getParent() != null)
        for(int i=0; i<gameMCTS.getRoot().getParent().getChildren().length; i++) {
          gameMCTS.getRoot().getParent().getChildren()[i] = null;
        }
      gameMCTS.getRoot().setParent(null);
      for(int i = 0; i<gameMCTS.getRoot().getChildren().length; i++)
        gameMCTS.getRoot().getChildren()[i] = null;
      gameMCTS.getRoot().setWins(new int[] {0,0});


//      mcts = new MCTS(gameMCTS.root);
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
    TreeNode parent = new TreeNode(null, new ReferenceGameState(gameState), new int[] {0,0}, new ReferenceMove(null, new int[2]));
    mcts = new MCTS(parent, new AIConfig());

//    System.out.println("parent Grid:");
//    parent.printGrid();

    Move move = mcts.getMove(100);

//    System.out.println(mcts.printResults(move));
    Piece picked = Arrays.asList(gameState.getTeams()[gameState.getCurrentTeam()].getPieces()).stream().filter(p -> p.getId().equals(move.getPieceId())).findFirst().get();
    ReferenceMove rmove = new ReferenceMove(picked, move.getNewPosition());
    
    mcts.alterGameState(mcts.getRoot().getReferenceGameState(), rmove);
//    mcts.root.printGrid();

    assertEquals(1, (int)Math.round(mcts.getRoot().getChildren()[0].getV()));
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

    TreeNode parent = new TreeNode(null, new ReferenceGameState(gameState), new int[] {0,0}, new ReferenceMove(null, new int[2]));
    mcts = new MCTS(parent, new AIConfig());

    mcts.getMove(100);
//    System.out.println("Piece: " + move.getPieceId() + " moves to " + move.getNewPosition()[0] + ", " + move.getNewPosition()[1]);
    for(int i=0; i<parent.getPossibleMoves().size(); i++) {
      if(-1 == mcts.isTerminal(mcts.getRoot().getReferenceGameState(), new ReferenceMove(null, new int[2])))
        mcts.oneMove(mcts.getRoot(), mcts.getRoot(), false, new ReferenceMove(null, new int[2]));
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

    TreeNode parent = new TreeNode(null, new ReferenceGameState(gameState), new int[] {0,0}, new ReferenceMove(null, new int[2]));
    mcts = new MCTS(parent, new AIConfig());

    mcts.getMove(100);
//    System.out.println("Piece: " + move.getPieceId() + " moves to " + move.getNewPosition()[0] + ", " + move.getNewPosition()[1]);
    for(int i=0; i<parent.getPossibleMoves().size(); i++) {
      if(-1 == mcts.isTerminal(mcts.getRoot().getReferenceGameState(), new ReferenceMove(null, new int[2])))
        mcts.oneMove(mcts.getRoot(), mcts.getRoot(), false, new ReferenceMove(null, new int[2]));
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
    MCTS mcts = new MCTS(new TreeNode(null, new ReferenceGameState(gameState), null, new ReferenceMove(null, new int[2])), new AIConfig());

    assertTrue(mcts.terminalHeuristic(mcts.getRoot()) == 1);
    mcts.getRoot().getReferenceGameState().setCurrentTeam(0);

    //TODO: Die Heuristik kann noch nicht erkennen, was geschlagen werden kann
    assertFalse(mcts.terminalHeuristic(mcts.getRoot()) == 0);
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
    MCTS mcts = new MCTS(new TreeNode(null, new ReferenceGameState(gameState), null, new ReferenceMove(null, new int[2])), new AIConfig());
    mcts.getMove(100);
    mcts.getRoot().initPossibleMovesAndChildren();
    Move move = mcts.getAndRemoveMoveHeuristic(mcts.getRoot());
    assertEquals(0, move.getNewPosition()[0]);
    assertEquals(0, move.getNewPosition()[1]);

    gameState.setCurrentTeam(0);
    mcts = new MCTS(new TreeNode(null, new ReferenceGameState(gameState), null, new ReferenceMove(null, new int[2])), new AIConfig());
    move = mcts.getAndRemoveMoveHeuristic(mcts.getRoot());
    assertEquals(2, move.getNewPosition()[0]);
    assertEquals(0, move.getNewPosition()[1]);
  }
  
  /**
   * This method tests if a piece can move onto a free position.
   */
  @Test
  void testOneRandomMove_freePosition() {
    TreeNode root = mcts.getRoot();
    Piece onlyPiece = root.getPossibleMoves().keySet().iterator().next();
    int[] onlyPos = root.getPossibleMoves().get(onlyPiece).get(0);
    ArrayList<int[]> posList = new ArrayList<int[]>();
    posList.add(onlyPos);
    root.getPossibleMoves().clear();
    root.getPossibleMoves().put(onlyPiece, posList);

    ReferenceGameState gameState = root.getReferenceGameState().clone();
    TreeNode alteredCopy = root.clone(gameState);
    root.getChildren()[0] = alteredCopy;
    mcts.oneMove(alteredCopy, root, false, new ReferenceMove(null, new int[2]));

    GridObjectContainer old = root.getReferenceGameState().getGrid().getGrid()[onlyPos[0]][onlyPos[1]];
    GridObjectContainer neW = root.getChildren()[0].getReferenceGameState().getGrid().getGrid()[onlyPos[0]][onlyPos[1]];
    
    assertNull(old);
    assertNotNull(neW);
    assertEquals(neW.getPiece().getId(), onlyPiece.getId());
 }
  /**
   * This method tests if a piece can capture an occupied position.
   */
  @Test
  void testOneRandomMove_capturePosition() {
    TreeNode root = mcts.getRoot();
    root.getReferenceGameState().getTeams()[0].getPieces().get(0).setPosition(new int[] {5,6});                       //place on the only square the other piece can move to. attack power should be equal
    root.getReferenceGameState().getGrid().getGrid()[5][6] = new GridObjectContainer(GridObjects.piece, 0, root.getReferenceGameState().getTeams()[0].getPieces().get(0));

    int piecesTeam0 = root.getReferenceGameState().getTeams()[0].getPieces().size();

    mcts.oneMove(root, root, false, new ReferenceMove(null, new int[2]));

    int piecesTeam0new = root.getReferenceGameState().getTeams()[0].getPieces().size();

      assertNotEquals(piecesTeam0, piecesTeam0new);                                                   //a piece got captured, the Pieces Array got smaller
  }
  /**
   * This method tests if a piece can capture a flag.
   */
  @Test
  void testOneRandomMove_captureFlag() {
    TreeNode root = mcts.getRoot();
    root.getReferenceGameState().getTeams()[0].setFlags(2);                                                       //1 Flag should be captured
    root.getReferenceGameState().getTeams()[0].setBase(new int[] {8,7});                                          //base team 1 is now at the targeted position 
    root.getReferenceGameState().getGrid().setPosition(new GridObjectContainer(GridObjects.base, 0, null), 7, 8);

    int[] posPiece6 = root.getReferenceGameState().getTeams()[1].getPieces().get(5).getPosition();
    int[] posPiece8 = root.getReferenceGameState().getTeams()[1].getPieces().get(7).getPosition();
    mcts.oneMove(root, root, false, new ReferenceMove(null, new int[2]));
    
    assertFalse(Arrays.equals(posPiece6,  root.getReferenceGameState().getTeams()[1].getPieces().get(5).getPosition()) &&
        Arrays.equals(posPiece8,  root.getReferenceGameState().getTeams()[1].getPieces().get(7).getPosition()));
    assertTrue(root.getReferenceGameState().getTeams()[0].getFlags() == 1);
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
    TreeNode parent = new TreeNode(null, new ReferenceGameState(gameState), new int[] {0,0}, new ReferenceMove(null, new int[2]));
    MCTS mcts = new MCTS(parent, new AIConfig());

//    parent.printGrid();
    
    assertEquals(1, mcts.isTerminal(mcts.getRoot().getReferenceGameState(), new ReferenceMove(null, new int[2])));

    //TODO test for 3 teams
  }
  
  @Test
  void testIsTerminal_movesLeft() {
    GameState gameState = TestValues.getTestState();
    gameState.setCurrentTeam(0);
    TreeNode parent = new TreeNode(null, new ReferenceGameState(gameState), new int[] {0,0}, new ReferenceMove(null, new int[2]));
    MCTS mcts = new MCTS(parent, new AIConfig());

    assertEquals(-1, mcts.isTerminal(mcts.getRoot().getReferenceGameState(), new ReferenceMove(null, new int[2])));
  }
  
  @Test
  void testRemoveTeamCheck() {
    ReferenceGameState gameState = mcts.getRoot().getReferenceGameState();
    gameState.getTeams()[0].setFlags(0);
    mcts.removeTeamCheck(gameState);

    assertTrue(gameState.getTeams()[0] == null);
  }

  @Test
  void testIsFullyExpanded() {
    assertFalse(mcts.isFullyExpanded(mcts.getRoot()));
    mcts.getRoot().getPossibleMoves().clear();
    assertTrue(mcts.isFullyExpanded(mcts.getRoot()));
  }
  
  /*@Test
  void testMultiSimulate() {
    long totalTime = 0;
    int count = 0;
    for(int i=0; i<10; i++, count++) {
      long time = System.nanoTime();
      mcts.multiSimulate(mcts.root);
      totalTime += System.nanoTime() - time;
      this.mcts = new MCTS(new TreeNode(null, new ReferenceGameState(TestValues.getTestState()), null, new ReferenceMove(null, new int[2])), new AI_Config());
    }
    System.out.println("average time for multi sim: " + (totalTime/count)/1000 + " µs" );
  }*/

  @Test
  void testSimulate() {
    long totalTime = 0;
    int count = 0;
    for(int i=0; i<10; i++, count++) {
      long time = System.nanoTime();
      mcts.simulate(mcts.getRoot());
      totalTime += System.nanoTime() - time;
      this.mcts = new MCTS(mcts.getRoot().clone(mcts.getRoot().getReferenceGameState().clone()), new AIConfig());
    }
    System.out.println("average time for sim: " + (totalTime/count)/1000 + " µs" );
  }

  @Test
  void testSelectAndExpand() {
    TreeNode firstChild = mcts.selectAndExpand(mcts.getRoot(), 1);
    assertEquals(firstChild, mcts.getRoot().getChildren()[0]);
    TreeNode secondChild = mcts.selectAndExpand(mcts.getRoot(), 1);
    assertEquals(secondChild, mcts.getRoot().getChildren()[1]);
  }

  @Test
  void testExpand() {
//    mcts.root.printGrid();
    mcts.expand(mcts.getRoot());
    assertTrue(mcts.getRoot().getChildren()[0] != null);
//    mcts.root.children[0].printGrid();
    mcts.expand(mcts.getRoot());
//    mcts.root.children[1].printGrid();
    assertTrue(mcts.getRoot().getChildren()[1] != null);
    assertTrue(mcts.getRoot().getChildren()[2] == null);
  }

  @Test
  void testBackpropagate() {
    mcts.getRoot().getChildren()[0] = mcts.expand(mcts.getRoot());
    mcts.backpropagate(mcts.getRoot().getChildren()[0], mcts.simulate(mcts.getRoot().getChildren()[0]));
    mcts.getRoot().getChildren()[0] = mcts.expand(mcts.getRoot());
    mcts.backpropagate(mcts.getRoot().getChildren()[0], mcts.simulate(mcts.getRoot().getChildren()[0]));
    mcts.getRoot().getChildren()[0] = mcts.expand(mcts.getRoot());
    mcts.backpropagate(mcts.getRoot().getChildren()[0], mcts.simulate(mcts.getRoot().getChildren()[0]));

    assertTrue(Arrays.stream(mcts.getRoot().getChildren()[0].getWins()).sum() > 0);
    assertTrue(Arrays.stream(mcts.getRoot().getWins()).sum() > 0);
  }
  
  @Test
  void testBestChild() {
    mcts.getRoot().setChildren(new TreeNode[2]);
    mcts.getRoot().getChildren()[0] = new TreeNode(null, MCTSUtilities.toNextTeam(new ReferenceGameState(TestValues.getTestState())), new int[] {0,0}, new ReferenceMove(null, new int[2]));
    mcts.getRoot().getChildren()[0].setParent(mcts.getRoot());
    mcts.getRoot().getChildren()[1] = new TreeNode(null, MCTSUtilities.toNextTeam(new ReferenceGameState(TestValues.getTestState())), new int[] {0,0}, new ReferenceMove(null, new int[2]));
    mcts.getRoot().getChildren()[1].setParent(mcts.getRoot());                                       //2 Kindknoten als Kinder von root initialisiert

    mcts.getRoot().getChildren()[0].setWins(new int[] {4, 0});                                  //Team 0 hat mehr wins als Team 1
    mcts.getRoot().getChildren()[1].setWins(new int[] {0, 12});                                 //Team 1 hat mehr wins als Team 0
    mcts.getRoot().setWins(new int[] {4, 12});
    TreeNode bestChild = mcts.bestChild(mcts.getRoot(), (float)Math.sqrt(2));
    assertEquals(bestChild, mcts.getRoot().getChildren()[1]);                                 //Da Team 1 am Zug ist, ist der Knoten mit mehr wins von 1 besser

    mcts.getRoot().getReferenceGameState().setCurrentTeam(0);
    mcts.getRoot().getChildren()[0].getReferenceGameState().setCurrentTeam(1);
    mcts.getRoot().getChildren()[1].getReferenceGameState().setCurrentTeam(1);
    bestChild = mcts.bestChild(mcts.getRoot(), (float)Math.sqrt(2));
    assertEquals(bestChild, mcts.getRoot().getChildren()[0]);                                 //Da Team 0 jetzt am Zug ist, ist der Knoten mit weniger wins von 1 besser
  }
  
  @Test
  void testGetRootBest() {
    mcts.getRoot().setChildren(new TreeNode[2]);
    mcts.getRoot().getChildren()[0] = new TreeNode(null, MCTSUtilities.toNextTeam(new ReferenceGameState(TestValues.getTestState())), new int[] {0,0}, new ReferenceMove(null, new int[2]));
    mcts.getRoot().getChildren()[0].setParent(mcts.getRoot());
    mcts.getRoot().getChildren()[1] = new TreeNode(null, MCTSUtilities.toNextTeam(new ReferenceGameState(TestValues.getTestState())), new int[] {0,0}, new ReferenceMove(null, new int[2]));
    mcts.getRoot().getChildren()[1].setParent(mcts.getRoot());                                       //2 Kindknoten als Kinder von root initialisiert

    mcts.getRoot().getChildren()[0].setWins(new int[] {4, 0});                                  //Team 0 hat mehr wins als Team 1
    mcts.getRoot().getChildren()[1].setWins(new int[] {0, 12});                                 //Team 1 hat mehr wins als Team 0
    mcts.getRoot().setWins(new int[] {4, 12});
    TreeNode bestChild = mcts.getRootBest(mcts.getRoot());
    assertEquals(bestChild, mcts.getRoot().getChildren()[1]);                                 //Da Team 1 am Zug ist, ist der Knoten mit mehr wins von 1 besser

    mcts.getRoot().getReferenceGameState().setCurrentTeam(0);
    mcts.getRoot().getChildren()[0].getReferenceGameState().setCurrentTeam(1);
    mcts.getRoot().getChildren()[1].getReferenceGameState().setCurrentTeam(1);

    bestChild = mcts.getRootBest(mcts.getRoot());
    assertEquals(bestChild, mcts.getRoot().getChildren()[0]);                                 //Da Team 0 jetzt am Zug ist, ist der Knoten mit weniger wins von 1 besser
  }

  
  @Test
  void testAlterGameState() {
    TreeNode node = mcts.getRoot();
    Piece piece = (Piece)node.getPossibleMoves().keySet().toArray()[0];
    ReferenceMove move = new ReferenceMove(piece, node.getPossibleMoves().get(piece).get(0));
    ReferenceGameState altered = node.getReferenceGameState().clone();

    mcts.alterGameState(altered, move);

    assertNotEquals(node.getReferenceGameState().getCurrentTeam(), altered.getCurrentTeam());
  }
}
