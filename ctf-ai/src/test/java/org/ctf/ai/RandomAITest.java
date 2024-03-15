package org.ctf.ai;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.ctf.ai.AI_Tools.InvalidShapeException;
import org.ctf.ai.AI_Tools.NoMovesLeftException;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Piece;


class RandomAITest {
	static GameState gameState;

	@BeforeEach
	void setUp() {
		gameState = TestValues.getTestState();
	}

	/**
	 * This Method does not know if no more moves are possible
	 */
	@Test
	void testPickMoveSimple() {
		GameState gameState = TestValues.getEmptyTestState();					//get an empty gameState that only contains two teams and their bases
		Piece knight = new Piece();
		knight.setId("p:1_1");
		knight.setPosition(new int[]{9,0});
		knight.setTeamId("1");
		knight.setDescription(TestValues.getTestTemplate().getPieces()[2]);		//new knight with l-shape movement
		gameState.getTeams()[1].setPieces(new Piece[] {knight});				//add knight to team1's pieces
		gameState.getGrid()[9][0] = knight.getId();								//knight is only able to move 2up1right or 2right1up
		gameState.getGrid()[8][2] = "b";										//now knight only got 1 valid position to jump on, 2up1right, onto 7,1
		int[] onlyPos = new int[] {7,1};										//the only valid position to move on is 7,1

		try {
			assertArrayEquals(onlyPos, RandomAI.pickMoveSimple(gameState).getNewPosition());		//only 1 move possible, onto a free field
		} catch (InvalidShapeException e) {
			fail("All shapes are valid");
		}
		//	<-- end test 1, move to a free field -->  //
		
		Piece pawn = new Piece();
		pawn.setId("p:0_1");
		pawn.setPosition(new int[]{7,1});
		pawn.setTeamId("0");													//place a pawn onto the only valid position, a knight is able to capture it
		pawn.setDescription(TestValues.getTestTemplate().getPieces()[0]);		//new pawn
		gameState.getTeams()[0].setPieces(new Piece[] {pawn});					//add pawn to team0's pieces
		gameState.getGrid()[7][1] = pawn.getId();								//little guy gets sacrificed
		
		try {
			assertArrayEquals(onlyPos, RandomAI.pickMoveSimple(gameState).getNewPosition());		//only 1 move possible, capture the pawn!
		} catch (InvalidShapeException e) {
			fail("All shapes are valid");
		}
		//  <-- end test 2, capture a weaker piece -->  //
		
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
		//	<-- end test 3, move queen to a free field -->  //
	}

	@Test
	void testPickMoveComplex() {
		GameState gameState = TestValues.getEmptyTestState();					//get an empty gameState that only contains two teams and their bases
		Piece knight = new Piece();
		knight.setId("p:1_1");
		knight.setPosition(new int[]{9,0});
		knight.setTeamId("1");
		knight.setDescription(TestValues.getTestTemplate().getPieces()[2]);		//new knight with l-shape movement
		gameState.getTeams()[1].setPieces(new Piece[] {knight});				//add knight to team1's pieces
		gameState.getGrid()[9][0] = knight.getId();								//knight is only able to move 2up1right or 2right1up
		gameState.getGrid()[8][2] = "b";										//now knight only got 1 valid position to jump on, 2up1right, onto 7,1
		int[] onlyPos = new int[] {7,1};										//the only valid position to move on is 7,1

		try {
			assertArrayEquals(onlyPos, RandomAI.pickMoveComplex(gameState).getNewPosition());		//only 1 move possible, onto a free field
		} catch (InvalidShapeException e) {
			fail("All shapes are valid");
		}
		catch (NoMovesLeftException e) {
			fail("There are still moves left");
		}
		//	<-- end test 1, move to a free field -->  //
		
		Piece pawn = new Piece();
		pawn.setId("p:0_1");
		pawn.setPosition(new int[]{7,1});
		pawn.setTeamId("0");													//place a pawn onto the only valid position, a knight is able to capture it
		pawn.setDescription(TestValues.getTestTemplate().getPieces()[0]);		//new pawn
		gameState.getTeams()[0].setPieces(new Piece[] {pawn});					//add pawn to team0's pieces
		gameState.getGrid()[7][1] = pawn.getId();								//little guy gets sacrificed
		
		try {
			assertArrayEquals(onlyPos, RandomAI.pickMoveComplex(gameState).getNewPosition());		//only 1 move possible, capture the pawn!
		} catch (InvalidShapeException e) {
			fail("All shapes are valid");
		}
		catch (NoMovesLeftException e) {
			fail("There are still moves left");
		}
		//  <-- end test 2, capture a weaker piece -->  //
		
		Piece rook = new Piece();
		rook.setId("p:0_1");
		rook.setPosition(new int[]{7,1});
		rook.setTeamId("0");													//place a rook onto the only valid position, a knight is not able to capture it
		rook.setDescription(TestValues.getTestTemplate().getPieces()[1]);		//new rook
		gameState.getTeams()[0].setPieces(new Piece[] {rook});					//add rook to team0's pieces
		gameState.getGrid()[7][1] = pawn.getId();								//big guy stands rock solid
		
		Exception nml = assertThrows(NoMovesLeftException.class, () -> RandomAI.pickMoveComplex(gameState));
		String expectedMessage = "Team "+gameState.getCurrentTeam()+" can not move.";
		String actualMessage = nml.getMessage();
		
		assertEquals(expectedMessage, actualMessage);
		//  <-- end test 3, rook cannot be captured, no moves left -->  //

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
		
		nml = assertThrows(NoMovesLeftException.class, () -> RandomAI.pickMoveComplex(gameState));
		expectedMessage = "Team "+gameState.getCurrentTeam()+" can not move.";
		actualMessage = nml.getMessage();
		
		assertEquals(expectedMessage, actualMessage);
		//  <-- end test 4, queen enclose by blocks, cannot move -->  //
		
		newGameState.getGrid()[6][6] = "";										//open one block for queen
		newGameState.getGrid()[7][7] = "b";										//only valid move now is 6,6
		
		try {
			assertArrayEquals(new int[] {6,6}, RandomAI.pickMoveComplex(newGameState).getNewPosition());		//only 1 move possible, onto a free field
		} catch (InvalidShapeException e) {
			fail("All shapes are valid");
		}
		catch (NoMovesLeftException e) {
			fail("There are still moves left");
		}
		//	<-- end test 5, move queen to a free field -->  //
	}
}