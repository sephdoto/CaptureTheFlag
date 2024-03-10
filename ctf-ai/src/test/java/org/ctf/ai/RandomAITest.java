package org.ctf.ai;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.ctf.client.state.data.map.MapTemplate;
import org.ctf.ai.RandomAI.InvalidShapeException;
import org.ctf.ai.RandomAI.NoMovesLeftException;
import org.ctf.client.state.GameState;
import org.ctf.client.state.Move;
import org.ctf.client.state.Piece;
import org.ctf.client.state.Team;
import org.ctf.client.state.data.map.Directions;
import org.ctf.client.tools.JSON_Tools;
import org.ctf.client.tools.JSON_Tools.MapNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;


class RandomAITest {
	static GameState gameState;

	@BeforeEach
	void setUp() {
		gameState = getTestState();
	}

	/**
	 * This Method does not know if no more moves are possible
	 */
	@Test
	void testPickMoveSimple() {
		GameState gameState = getEmptyTestState();					//get an empty gameState that only contains two teams and their bases
		Piece knight = new Piece();
		knight.setId("p:1_1");
		knight.setPosition(new int[]{9,0});
		knight.setTeamId("team1");
		knight.setDescription(getTestTemplate().getPieces()[2]);	//new knight with l-shape movement
		gameState.getTeams()[1].setPieces(new Piece[] {knight});	//add knight to team1's pieces
		gameState.getGrid()[9][0] = knight.getId();					//knight is only able to move 2up1right or 2right1up
		gameState.getGrid()[8][2] = "b";							//now knight only got 1 valid position to jump on, 2up1right, onto 7,1
		int[] onlyPos = new int[] {7,1};							//the only valid position to move on is 7,1

		try {
			assertArrayEquals(onlyPos, RandomAI.pickMoveSimple(gameState).getNewPosition());		//only 1 move possible, onto a free field
		} catch (InvalidShapeException e) {
			fail("All shapes are valid");
		}
		//	<-- end test 1, move to a free field -->  //
		
		Piece pawn = new Piece();
		pawn.setId("p:0_1");
		pawn.setPosition(new int[]{7,1});
		pawn.setTeamId("team0");									//place a pawn onto the only valid position, a knight is able to capture it
		pawn.setDescription(getTestTemplate().getPieces()[0]);		//new pawn
		gameState.getTeams()[0].setPieces(new Piece[] {pawn});		//add pawn to team0's pieces
		gameState.getGrid()[7][1] = pawn.getId();					//little guy gets sacrificed
		
		try {
			assertArrayEquals(onlyPos, RandomAI.pickMoveSimple(gameState).getNewPosition());		//only 1 move possible, capture the pawn!
		} catch (InvalidShapeException e) {
			fail("All shapes are valid");
		}
		//  <-- end test 2, capture a weaker piece -->  //
		
		Piece queen = new Piece();
		queen.setId("p:1_1");
		queen.setPosition(new int[]{5,5});
		queen.setTeamId("team1");
		queen.setDescription(getTestTemplate().getPieces()[4]);		//new queen in the middle of the board
		gameState.getTeams()[1].setPieces(new Piece[] {queen});		//add queen to team1's pieces
		gameState.getGrid()[5][5] = queen.getId();					
		gameState.getGrid()[5][4] = "b";
		gameState.getGrid()[5][6] = "b";
		gameState.getGrid()[4][4] = "b";
		gameState.getGrid()[4][5] = "b";
		gameState.getGrid()[4][6] = "b";
		gameState.getGrid()[6][4] = "b";
		gameState.getGrid()[6][5] = "b";
		gameState.getGrid()[7][7] = "b";		
		gameState.getGrid()[7][1] = "b";			//only valid move now is 6,6
		
		try {
			assertArrayEquals(new int[] {6,6}, RandomAI.pickMoveSimple(gameState).getNewPosition());		//only 1 move possible, onto a free field
		} catch (InvalidShapeException e) {
			fail("All shapes are valid");
		}
		//	<-- end test 3, move queen to a free field -->  //
	}

	@Test
	void testPickMoveComplex() {
		GameState gameState = getEmptyTestState();					//get an empty gameState that only contains two teams and their bases
		Piece knight = new Piece();
		knight.setId("p:1_1");
		knight.setPosition(new int[]{9,0});
		knight.setTeamId("team1");
		knight.setDescription(getTestTemplate().getPieces()[2]);	//new knight with l-shape movement
		gameState.getTeams()[1].setPieces(new Piece[] {knight});	//add knight to team1's pieces
		gameState.getGrid()[9][0] = knight.getId();					//knight is only able to move 2up1right or 2right1up
		gameState.getGrid()[8][2] = "b";							//now knight only got 1 valid position to jump on, 2up1right, onto 7,1
		int[] onlyPos = new int[] {7,1};							//the only valid position to move on is 7,1

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
		pawn.setTeamId("team0");									//place a pawn onto the only valid position, a knight is able to capture it
		pawn.setDescription(getTestTemplate().getPieces()[0]);		//new pawn
		gameState.getTeams()[0].setPieces(new Piece[] {pawn});		//add pawn to team0's pieces
		gameState.getGrid()[7][1] = pawn.getId();					//little guy gets sacrificed
		
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
		rook.setTeamId("team0");									//place a rook onto the only valid position, a knight is not able to capture it
		rook.setDescription(getTestTemplate().getPieces()[1]);		//new rook
		gameState.getTeams()[0].setPieces(new Piece[] {rook});		//add rook to team0's pieces
		gameState.getGrid()[7][1] = pawn.getId();					//big guy stands rock solid
		
		Exception nml = assertThrows(NoMovesLeftException.class, () -> RandomAI.pickMoveComplex(gameState));
		String expectedMessage = "Team team1 can not move.";
		String actualMessage = nml.getMessage();
		
		assertEquals(expectedMessage, actualMessage);
		//  <-- end test 3, rook cannot be captured, no moves left -->  //

		GameState newGameState = getEmptyTestState();							//get new empty GameState
		Piece queen = new Piece();
		queen.setId("p:1_1");
		queen.setPosition(new int[]{5,5});
		queen.setTeamId("team1");
		queen.setDescription(getTestTemplate().getPieces()[4]);		//new queen in the middle of the board
		newGameState.getTeams()[1].setPieces(new Piece[] {queen});		//add queen to team1's pieces
		newGameState.getGrid()[5][5] = queen.getId();					
		newGameState.getGrid()[5][4] = "b";
		newGameState.getGrid()[5][6] = "b";
		newGameState.getGrid()[4][4] = "b";
		newGameState.getGrid()[4][5] = "b";
		newGameState.getGrid()[4][6] = "b";
		newGameState.getGrid()[6][4] = "b";
		newGameState.getGrid()[6][5] = "b";
		newGameState.getGrid()[6][6] = "b";							//enclose queen with blocks, no valid moves left
		
		nml = assertThrows(NoMovesLeftException.class, () -> RandomAI.pickMoveComplex(gameState));
		expectedMessage = "Team team1 can not move.";
		actualMessage = nml.getMessage();
		
		assertEquals(expectedMessage, actualMessage);
		//  <-- end test 4, queen enclose by blocks, cannot move -->  //
		
		newGameState.getGrid()[6][6] = "";							//open one block for queen
		newGameState.getGrid()[7][7] = "b";							//only valid move now is 6,6
		
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

	@Test
	void testGetShapeMove() {
		Move move1 = new Move();
		ArrayList<Move> moveList = new ArrayList<Move>();
		moveList.add(move1);
		
		assertEquals(move1, RandomAI.getShapeMove(moveList));
	}

	@Test
	void testValidShapeDirection()  {
		Piece knight = new Piece();
		knight.setId("p:1_9");
		knight.setPosition(new int[]{9,0});
		knight.setTeamId("team1");
		knight.setDescription(getTestTemplate().getPieces()[2]);	//new knight with l-shape movement
		gameState.getGrid()[9][0] = knight.getId();					//knight is only able to move 2up1right or 2right1up
		gameState.getGrid()[8][2] = "b";							//now knight only got 1 valid position to jump on, 2up1right, onto 7,1
		gameState.getGrid()[8][0] = "b";
		gameState.getGrid()[8][1] = "b";
		gameState.getGrid()[9][1] = "b";							//completely enclosing the knight in blocks
		
		try {
			assertNull(RandomAI.validShapeDirection(gameState, knight, 0));		//invalid direction (OutOfBounds): 2up1left
			assertNotNull(RandomAI.validShapeDirection(gameState, knight, 1));	//only valid direction: 2up1right
			assertNull(RandomAI.validShapeDirection(gameState, knight, 2));		//invalid direction (block): 2right1up
			assertNull(RandomAI.validShapeDirection(gameState, knight, 3));		//invalid direction (OutOfBounds): 2right1down
			assertNull(RandomAI.validShapeDirection(gameState, knight, 4));		//invalid direction (OutOfBounds): 2down1left
			assertNull(RandomAI.validShapeDirection(gameState, knight, 5));		//invalid direction (OutOfBounds): 2down1right
			assertNull(RandomAI.validShapeDirection(gameState, knight, 6));		//invalid direction (OutOfBounds): 2left1up
			assertNull(RandomAI.validShapeDirection(gameState, knight, 7));		//invalid direction (OutOfBounds): 2left1down
			assertNull(RandomAI.validShapeDirection(gameState, knight, 8));		//invalid direction (direction does not exist)
		} catch (InvalidShapeException e) {
			fail("All shapes are valid");
		}
	}

	@Test
	void testGetDirectionMove() {
		Piece rook = gameState.getTeams()[1].getPieces()[1];				//rook on 7,3
		HashMap<Integer, Integer> dirMap = new HashMap<Integer, Integer>();
		dirMap.put(2,5);													//rook can only move to one field ()
		int[] onlyPosition = new int[]{6,3};								//the only possible Position is 6,3
		int[] newPosition = RandomAI.getDirectionMove(dirMap, rook, gameState).getNewPosition();
		
		assertArrayEquals(onlyPosition, newPosition);
	}

	/**
	 * This method does not test if a pieces reach in a direction is >0.
	 * It just tests if a direction is accessible.
	 */
	@Test
	void testValidDirection() {
		Piece rook = gameState.getTeams()[1].getPieces()[1];				//rook on 7,3

		assertFalse(RandomAI.validDirection(gameState, rook, 0));			//a piece on the left doesn't allow the movement to the left
		assertFalse(RandomAI.validDirection(gameState, rook, 1));			//a piece on the right doesn't allow the movement to the right
		assertTrue(RandomAI.validDirection(gameState, rook, 2));			//there's a free position above, this direction is accessible
		assertTrue(RandomAI.validDirection(gameState, rook, 3));			//there's a free position below, this direction is accessible
		assertTrue(RandomAI.validDirection(gameState, rook, 4));			//there's a free position above-left, this direction is accessible
		assertTrue(RandomAI.validDirection(gameState, rook, 5));			//there's a free position above-right, this direction is accessible
		assertTrue(RandomAI.validDirection(gameState, rook, 6));			//there's a free position below-left, this direction is accessible
		assertFalse(RandomAI.validDirection(gameState, rook, 7));			//there's a piece below-right, this direction is not accessible
	}

	/**
	 * This method does not test if a pieces reach in a direction is >0.
	 * It just tests if a position could be occupied.
	 */
	@Test
	void testCheckMoveValidity() {
		Piece rook = gameState.getTeams()[1].getPieces()[1];				//rook on 7,3
		Piece rook2 = gameState.getTeams()[1].getPieces()[3];				//rook on 7,5

		assertNull(RandomAI.checkMoveValidity(gameState, rook, 0, 2));		//rook cannot walk over another same team rook
		assertNull(RandomAI.checkMoveValidity(gameState, rook, 1, 1));		//rook cannot walk onto another same team rook
		assertNotNull(RandomAI.checkMoveValidity(gameState, rook, 2, 1));	//rook can walk on the empty space above
		assertNull(RandomAI.checkMoveValidity(gameState, rook, 2, 3));		//rook cannot jump over the block above
		assertNotNull(RandomAI.checkMoveValidity(gameState, rook, 3, 1));	//rook can walk on the empty space below
		assertNotNull(RandomAI.checkMoveValidity(gameState, rook, 6, 1));	//piece could go to the empty field below-left
		assertNull(RandomAI.checkMoveValidity(gameState, rook2, 4, 4));		//piece could not walk over a block to the empty field above-left 3,1
		assertNull(RandomAI.checkMoveValidity(gameState, rook2, 4, 0));		//piece could not walk onto its own position
	}

	@Test
	void testSightLine() {
		assertTrue(RandomAI.sightLine(gameState, new int[]{4,6}, 1, 3));    //free line of sight
		assertTrue(RandomAI.sightLine(gameState, new int[]{7,2}, 0, 0));    //newPos = oldPos
		assertFalse(RandomAI.sightLine(gameState, new int[]{4,8}, 1, 2));   //there is one block
		assertFalse(RandomAI.sightLine(gameState, new int[]{5,5}, 0, 100)); //newPos is not on the grid, outOfBounds
		assertFalse(RandomAI.sightLine(gameState, new int[]{1,1}, 4, 6));   //there is an enemy Piece blocking the line of sight
	}

	@Test
	void testUpdatePos() {
		int[] posititon = new int[] {5,5};

		assertArrayEquals(posititon, RandomAI.updatePos(new int[] {5,6}, 0, 1));        //left
		assertArrayEquals(posititon, RandomAI.updatePos(new int[] {5,4}, 1, 1));        //right
		assertArrayEquals(posititon, RandomAI.updatePos(new int[] {6,5}, 2, 1));        //up
		assertArrayEquals(posititon, RandomAI.updatePos(new int[] {4,5}, 3, 1));        //down
		assertArrayEquals(posititon, RandomAI.updatePos(new int[] {6,6}, 4, 1));        //up left
		assertArrayEquals(posititon, RandomAI.updatePos(new int[] {6,4}, 5, 1));        //up right
		assertArrayEquals(posititon, RandomAI.updatePos(new int[] {4,6}, 6, 1));        //down left
		assertArrayEquals(posititon, RandomAI.updatePos(new int[] {4,4}, 7, 1));        //down right
		assertArrayEquals(new int[]{0,0}, RandomAI.updatePos(new int[] {9,9}, 4, 9));   //lower right corner to upper left corner
	}

	@Test
	void testValidPos() {
		Piece weakPiece = new Piece();
		weakPiece.setDescription(getTestTemplate().getPieces()[0]);

		assertTrue(RandomAI.validPos(new int[] {3,3}, gameState.getTeams()[0].getPieces()[0], gameState));		//valid empty position
		assertFalse(RandomAI.validPos(new int[] {-1,0}, gameState.getTeams()[0].getPieces()[0], gameState));	//out of bounds 1
		assertFalse(RandomAI.validPos(new int[] {10,0}, gameState.getTeams()[0].getPieces()[0], gameState));	//out of bounds 2
		assertTrue(RandomAI.validPos(new int[] {2,2}, gameState.getTeams()[0].getPieces()[0], gameState));		//rook team1 captures another rook from team0
		assertFalse(RandomAI.validPos(new int[] {2,2}, weakPiece, gameState));									//weak Pawn cannot capture a stronger rook from team0
		assertFalse(RandomAI.validPos(new int[] {7,2}, gameState.getTeams()[0].getPieces()[0], gameState));		//rook team1 cannot capture a team1 piece
		assertFalse(RandomAI.validPos(new int[] {5,3}, gameState.getTeams()[0].getPieces()[0], gameState));		//5,3 is occupied by a block

		//TODO walk on same team base check (I dont know the behavior for this)
		assertFalse(RandomAI.validPos(new int[] {9,9}, gameState.getTeams()[0].getPieces()[0], gameState));
		//TODO walk on opponent base check (I dont know the behavior for this)
		assertFalse(RandomAI.validPos(new int[] {0,0}, gameState.getTeams()[0].getPieces()[0], gameState));
	}

	@Test
	void testGetReach() {
		Directions rookDirections = getTestTemplate().getPieces()[1].getMovement().getDirections();

		assertEquals(2, RandomAI.getReach(rookDirections, 0));
		assertEquals(2, RandomAI.getReach(rookDirections, 1));
		assertEquals(2, RandomAI.getReach(rookDirections, 2));
		assertEquals(2, RandomAI.getReach(rookDirections, 3));
		assertEquals(0, RandomAI.getReach(rookDirections, 4));
		assertEquals(0, RandomAI.getReach(rookDirections, 5));
		assertEquals(0, RandomAI.getReach(rookDirections, 6));
		assertEquals(0, RandomAI.getReach(rookDirections, 7));
		assertEquals(-1, RandomAI.getReach(rookDirections, 8));		//invalid direction: -1
	}

	
	/**
	 * Creates a test GameState from the example Map. 
	 * @return GameState
	 */
	private GameState getEmptyTestState() {
		Team team1 = new Team();
		team1.setBase(new int[] {0,0});
		team1.setColor("red");
		team1.setFlag(new int[] {0,0});
		team1.setId("1");

		Team team2 = new Team();
		team2.setBase(new int[] {9,9});
		team2.setColor("blue");
		team2.setFlag(new int[] {9,9});
		team2.setId("0");

		Piece[] pieces1 = new Piece[0];
		team1.setPieces(pieces1);

		Piece[] pieces2 = new Piece[0];
		team2.setPieces(pieces2);

		Move lastMove = new Move();
		lastMove.setNewPosition(null);
		lastMove.setPieceId(null);

		GameState testState = new GameState();
		testState.setCurrentTeam(1);
		String[][] example = new String[][] {
			{"b:0","","","","","","","","",""},
			{"","","","","","","","","",""},
			{"","","","","","","","","",""},
			{"","","","","","","","","",""},
			{"","","","","","","","","",""},
			{"","","","","","","","","",""},
			{"","","","","","","","","",""},
			{"","","","","","","","","",""},
			{"","","","","","","","","",""},
			{"","","","","","","","","","b:1"}
		};
		testState.setGrid(example);
		testState.setLastMove(lastMove);
		testState.setTeams(new Team[]{team1, team2});

		return testState;
	}
	
	/**
	 * Creates a test GameState from the example Map. 
	 * @return GameState
	 */
	private GameState getTestState() {
		MapTemplate mt = getTestTemplate();
		Team team1 = new Team();
		team1.setBase(new int[] {0,0});
		team1.setColor("red");
		team1.setFlag(new int[] {0,0});
		team1.setId("0");

		Team team2 = new Team();
		team2.setBase(new int[] {9,9});
		team2.setColor("blue");
		team2.setFlag(new int[] {9,9});
		team2.setId("1");

		Piece[] pieces1 = new Piece[8];
		for(int i=0; i<8; i++) {
			pieces1[i] = new Piece(); 
			pieces1[i].setDescription(mt.getPieces()[1]);
			pieces1[i].setId("p:0_"+(i+1));
			if(i<2)
				pieces1[i].setPosition(new int[] {1,4+i});
			else
				pieces1[i].setPosition(new int[] {2,i});    
			pieces1[i].setTeamId(team1.getId());
		}
		team1.setPieces(pieces1);

		Piece[] pieces2 = new Piece[8];
		for(int i=0; i<8; i++) {
			pieces2[i] = new Piece(); 
			pieces2[i].setDescription(mt.getPieces()[1]);
			pieces2[i].setId("p:1_"+(i+1));
			if(i<6)
				pieces2[i].setPosition(new int[] {7,2+i});
			else
				pieces2[i].setPosition(new int[] {8,i-2});  
			pieces2[i].setTeamId(team1.getId());
		}
		team2.setPieces(pieces2);

		Move lastMove = new Move();
		lastMove.setNewPosition(null);
		lastMove.setPieceId(null);

		GameState testState = new GameState();
		testState.setCurrentTeam(1);
		String[][] example = new String[][] {
			{"b:0","","","","","","","","",""},
			{"","","","",pieces1[0].getId(),pieces1[1].getId(),"","","",""},
			{"","",pieces1[2].getId(),pieces1[3].getId(),pieces1[4].getId(),pieces1[5].getId(),pieces1[6].getId(),pieces1[7].getId(),"",""},
			{"","","","","","","","","",""},
			{"","","","","","","","b","",""},
			{"","","","b","","","","","",""},
			{"","","","","","","","","",""},
			{"","",pieces2[0].getId(),pieces2[1].getId(),pieces2[2].getId(),pieces2[3].getId(),pieces2[4].getId(),pieces2[5].getId(),"",""},
			{"","","","",pieces2[6].getId(),pieces2[7].getId(),"","","",""},
			{"","","","","","","","","","b:1"}
		};
		testState.setGrid(example);
		testState.setLastMove(lastMove);
		testState.setTeams(new Team[]{team1, team2});

		return testState;
	}

	/**
	 * Returns the test MapTemplate from the resource folder. 
	 * @return MapTemplate
	 */
	@SuppressWarnings("deprecation")
	private MapTemplate getTestTemplate() {
		MapTemplate mt = new MapTemplate();
		try {
			mt = JSON_Tools.readMapTemplate("10x10_2teams_example");
		} catch (MapNotFoundException e) {e.printStackTrace();}
		return mt;
	}
}