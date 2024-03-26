package org.ctf.ai;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ctf.ai.random.RandomAI;
import org.ctf.ai.AI_Tools.InvalidShapeException;
import org.ctf.ai.AI_Tools.NoMovesLeftException;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Piece;


/**
 * Test class for RandomAI
 * @author sistumpf
 */
class RandomAITest {
  static GameState gameState;

  @BeforeEach
  void setUp() {
    gameState = TestValues.getTestState();
  }

  /**
   * testPickMoveSimple does not know if there are no more possible moves.
   * 
   * test 1, move to a free field
   */
  @SuppressWarnings("deprecation")
  @Test
  void testPickMoveSimple_knightMovement() {
    GameState gameState = TestValues.getEmptyTestState();					//get an empty gameState that only contains two teams and their bases
    Piece knight = new Piece();
    knight.setId("p:1_1");
    knight.setPosition(new int[]{9,0});
    knight.setTeamId("1");
    knight.setDescription(TestValues.getTestTemplate().getPieces()[2]);		//new knight with l-shape movement
    gameState.getTeams()[1].setPieces(new Piece[] {knight});				//add knight to team1's pieces
    gameState.getGrid()[9][0] = knight.getId();								//knight is only able to move 2up1right or 2right1up
    gameState.getGrid()[9][1] = "b";										//now knight only got 1 valid direction with 7.1, 7.0, 8.0
    ArrayList<int[]> onlyPos = new ArrayList<int[]>();
    onlyPos.add(new int[] {7,0});
    onlyPos.add(new int[] {7,1});
    onlyPos.add(new int[] {8,0});                                           //the only valid positions

    int[] move = new int[] {};
    try {
      move = RandomAI.pickMoveSimple(gameState).getNewPosition();
    } catch (InvalidShapeException e) {
      fail("All shapes are valid");
    }
    boolean oneOfThree = Arrays.equals(onlyPos.get(0), move) || Arrays.equals(onlyPos.get(1), move) || Arrays.equals(onlyPos.get(2), move);
    assertTrue(oneOfThree);
  }

  /**
   * test 2, capture a weaker piece
   */
  @SuppressWarnings("deprecation")
  @Test
  void testPickMoveSimple_knightCapture() {    
    GameState gameState = TestValues.getEmptyTestState();                    //get an empty gameState that only contains two teams and their bases
    Piece knight = new Piece();
    knight.setId("p:1_1");
    knight.setPosition(new int[]{9,0});
    knight.setTeamId("1");
    knight.setDescription(TestValues.getTestTemplate().getPieces()[2]);     //new knight with l-shape movement
    gameState.getTeams()[1].setPieces(new Piece[] {knight});                //add knight to team1's pieces
    gameState.getGrid()[9][0] = knight.getId();                             //knight is only able to move 2up1right or 2right1up
    gameState.getGrid()[9][1] = "b";                                        //now knight only got 1 valid direction with 7.1, 7.0, 8.0
    Piece pawn = new Piece();
    pawn.setId("p:0_1");
    pawn.setPosition(new int[]{8,0});
    pawn.setTeamId("0");													//place a pawn onto the only valid position, a knight is able to capture it
    pawn.setDescription(TestValues.getTestTemplate().getPieces()[0]);		//new pawn
    gameState.getTeams()[0].setPieces(new Piece[] {pawn});					//add pawn to team0's pieces
    gameState.getGrid()[8][0] = pawn.getId();								//little guy gets sacrificed

    try {
      assertArrayEquals(new int[] {8,0}, RandomAI.pickMoveSimple(gameState).getNewPosition());		//only 1 move possible, capture the pawn!
    } catch (InvalidShapeException e) {
      fail("All shapes are valid");
    }
  }

  /**
   * test 3, move queen to a free field
   */
  @SuppressWarnings("deprecation")
  @Test
  void testPickMoveSimple_queenMovement() {
    GameState gameState = TestValues.getEmptyTestState();                  //get an empty gameState that only contains two teams and their bases
    Piece queen = new Piece();
    queen.setId("p:1_1");
    queen.setPosition(new int[]{5,5});
    queen.setTeamId("1");
    queen.setDescription(TestValues.getTestTemplate().getPieces()[4]);		//new queen in the middle of the board
    gameState.getTeams()[1].setPieces(new Piece[] {queen});					//add queen to team1's pieces
    gameState.getGrid()[5][5] = queen.getId();					
    gameState.getGrid()[5][4] = "b";
    gameState.getGrid()[5][6] = "b";
    gameState.getGrid()[4][4] = "b";
    gameState.getGrid()[4][5] = "b";
    gameState.getGrid()[4][6] = "b";
    gameState.getGrid()[6][4] = "b";
    gameState.getGrid()[6][5] = "b";
    gameState.getGrid()[7][7] = "b";		
    gameState.getGrid()[7][1] = "b";										//only valid move now is 6,6

    try {
      assertArrayEquals(new int[] {6,6}, RandomAI.pickMoveSimple(gameState).getNewPosition());		//only 1 move possible, onto a free field
    } catch (InvalidShapeException e) {
      fail("All shapes are valid");
    }
  }

  /**
   * test 1, move to a free field
   */
  @Test
  void testPickMoveComplex_knightMovement() {
    GameState gameState = TestValues.getEmptyTestState();                   //get an empty gameState that only contains two teams and their bases
    Piece knight = new Piece();
    knight.setId("p:1_1");
    knight.setPosition(new int[]{9,0});
    knight.setTeamId("1");
    knight.setDescription(TestValues.getTestTemplate().getPieces()[2]);     //new knight with l-shape movement
    gameState.getTeams()[1].setPieces(new Piece[] {knight});                //add knight to team1's pieces
    gameState.getGrid()[9][0] = knight.getId();                             //knight is only able to move 2up1right or 2right1up
    gameState.getGrid()[9][1] = "b";                                        //now knight only got 1 valid direction with 7.1, 7.0, 8.0
    ArrayList<int[]> onlyPos = new ArrayList<int[]>();
    onlyPos.add(new int[] {7,0});
    onlyPos.add(new int[] {7,1});
    onlyPos.add(new int[] {8,0});                                           //the only valid positions

    int[] move = new int[] {};
    try {
      move = RandomAI.pickMoveComplex(gameState).getNewPosition();
    } catch (InvalidShapeException e) {
      fail("All shapes are valid");
    }
    catch (NoMovesLeftException e) {
      fail("There are still moves left");
    }

    boolean oneOfThree = Arrays.equals(onlyPos.get(0), move) || Arrays.equals(onlyPos.get(1), move) || Arrays.equals(onlyPos.get(2), move);
    assertTrue(oneOfThree);
  }

  /**
   * test 2, capture a weaker piece
   */
  @Test
  void testPickMoveComplex_knightCapture() {    
    GameState gameState = TestValues.getEmptyTestState();                    //get an empty gameState that only contains two teams and their bases
    Piece knight = new Piece();
    knight.setId("p:1_1");
    knight.setPosition(new int[]{9,0});
    knight.setTeamId("1");
    knight.setDescription(TestValues.getTestTemplate().getPieces()[2]);     //new knight with l-shape movement
    gameState.getTeams()[1].setPieces(new Piece[] {knight});                //add knight to team1's pieces
    gameState.getGrid()[9][0] = knight.getId();                             //knight is only able to move 2up1right or 2right1up
    gameState.getGrid()[9][1] = "b";                                        //now knight only got 1 valid direction with 7.1, 7.0, 8.0
    Piece pawn = new Piece();
    pawn.setId("p:0_1");
    pawn.setPosition(new int[]{8,0});
    pawn.setTeamId("0");                                                    //place a pawn onto the only valid position, a knight is able to capture it
    pawn.setDescription(TestValues.getTestTemplate().getPieces()[0]);       //new pawn
    gameState.getTeams()[0].setPieces(new Piece[] {pawn});                  //add pawn to team0's pieces
    gameState.getGrid()[8][0] = pawn.getId();                               //little guy gets sacrificed

    try {
      assertArrayEquals(new int[] {8,0}, RandomAI.pickMoveComplex(gameState).getNewPosition());		//only 1 move possible, capture the pawn!
    } catch (InvalidShapeException e) {
      fail("All shapes are valid");
    }
    catch (NoMovesLeftException e) {
      fail("There are still moves left");
    }
  }

  /**
   * test 3, rook cannot be captured, no moves left
   */
  @Test
  void testPickMove_impossibleKnightCapture() {
    GameState gameState = TestValues.getEmptyTestState();                    //get an empty gameState that only contains two teams and their bases
    Piece knight = new Piece();
    knight.setId("p:1_1");
    knight.setPosition(new int[]{9,0});
    knight.setTeamId("1");
    knight.setDescription(TestValues.getTestTemplate().getPieces()[2]);     //new knight with l-shape movement
    gameState.getTeams()[1].setPieces(new Piece[] {knight});                //add knight to team1's pieces
    gameState.getGrid()[9][0] = knight.getId();                             //knight is only able to move 2up1right or 2right1up
    gameState.getGrid()[9][1] = "b";                                        //now knight only got 1 valid direction with 7.1, 7.0, 8.0
    Piece rook = new Piece();
    rook.setId("p:0_1");
    rook.setPosition(new int[]{8,0});
    rook.setTeamId("0");													//place a rook onto the only valid position, a knight is not able to capture it
    rook.setDescription(TestValues.getTestTemplate().getPieces()[1]);		//new rook
    gameState.getTeams()[0].setPieces(new Piece[] {rook});					//add rook to team0's pieces
    gameState.getGrid()[8][0] = rook.getId();								//big guy stands rock solid

    Exception nml = assertThrows(NoMovesLeftException.class, () -> RandomAI.pickMoveComplex(gameState));
    String expectedMessage = "Team "+gameState.getCurrentTeam()+" can not move.";
    String actualMessage = nml.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }

  /**
   * test 4, queen enclose by blocks, cannot move
   */
  @Test
  void testPickMoveComplex_trappedQueen() {
    GameState newGameState = TestValues.getEmptyTestState();				//get new empty GameState
    Piece queen = new Piece();
    queen.setId("p:1_1");
    queen.setPosition(new int[]{5,5});
    queen.setTeamId("1");
    queen.setDescription(TestValues.getTestTemplate().getPieces()[4]);		//new queen in the middle of the board
    newGameState.getTeams()[1].setPieces(new Piece[] {queen});				//add queen to team1's pieces
    newGameState.getGrid()[5][5] = queen.getId();					
    newGameState.getGrid()[5][4] = "b";
    newGameState.getGrid()[5][6] = "b";
    newGameState.getGrid()[4][4] = "b";
    newGameState.getGrid()[4][5] = "b";
    newGameState.getGrid()[4][6] = "b";
    newGameState.getGrid()[6][4] = "b";
    newGameState.getGrid()[6][5] = "b";
    newGameState.getGrid()[6][6] = "b";										//enclose queen with blocks, no valid moves left

    Exception nml = assertThrows(NoMovesLeftException.class, () -> RandomAI.pickMoveComplex(newGameState));
    String expectedMessage = "Team "+newGameState.getCurrentTeam()+" can not move.";
    String actualMessage = nml.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }
  
  /**
   * test 5, move queen to a free field
   */
  @Test
  void testPickMoveComplex_freedQueen() {
    GameState newGameState = TestValues.getEmptyTestState();                //get new empty GameState
    Piece queen = new Piece();
    queen.setId("p:1_1");
    queen.setPosition(new int[]{5,5});
    queen.setTeamId("1");
    queen.setDescription(TestValues.getTestTemplate().getPieces()[4]);      //new queen in the middle of the board
    newGameState.getTeams()[1].setPieces(new Piece[] {queen});              //add queen to team1's pieces
    newGameState.getGrid()[5][5] = queen.getId();                   
    newGameState.getGrid()[5][4] = "b";
    newGameState.getGrid()[5][6] = "b";
    newGameState.getGrid()[4][4] = "b";
    newGameState.getGrid()[4][5] = "b";
    newGameState.getGrid()[4][6] = "b";
    newGameState.getGrid()[6][4] = "b";
    newGameState.getGrid()[6][5] = "b";
    newGameState.getGrid()[7][7] = "b";										//only valid move now is 6,6

    try {
      assertArrayEquals(new int[] {6,6}, RandomAI.pickMoveComplex(newGameState).getNewPosition());		//only 1 move possible, onto a free field
    } catch (InvalidShapeException e) {
      fail("All shapes are valid");
    }
    catch (NoMovesLeftException e) {
      fail("There are still moves left");
    }
  }
}