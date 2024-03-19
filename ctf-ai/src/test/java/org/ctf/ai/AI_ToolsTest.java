package org.ctf.ai;

import org.ctf.ai.AI_Tools.InvalidShapeException;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Piece;
import org.ctf.shared.state.data.map.Directions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.HashMap;

public class AI_ToolsTest {	
	static GameState gameState;

	@BeforeEach
	void setUp() {
		gameState = TestValues.getTestState();
	}

	@Test
	void testGetRandomShapeMove() {
		Move move1 = new Move();
		ArrayList<Move> moveList = new ArrayList<Move>();
		moveList.add(move1);

		assertEquals(move1, RandomAI.getRandomShapeMove(moveList));
	}

	@Test
	void testValidShapeDirection()  {
		Piece knight = new Piece();
		knight.setId("p:1_9");
		knight.setPosition(new int[]{9,0});
		knight.setTeamId("team1");
		knight.setDescription(TestValues.getTestTemplate().getPieces()[2]);	//new knight with l-shape movement
		gameState.getGrid()[9][0] = knight.getId();							//knight is only able to move 2up1right or 2right1up
		gameState.getGrid()[8][2] = "b";									//now knight only got 1 valid position to jump on, 2up1right, onto 7,1
		gameState.getGrid()[8][0] = "b";
		gameState.getGrid()[8][1] = "b";
		gameState.getGrid()[9][1] = "b";									//completely enclosing the knight in blocks

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
	void testCreateShapeMoveList() {
		Piece knight = new Piece();
		knight.setId("p:1_9");
		knight.setPosition(new int[]{9,0});
		knight.setTeamId("team1");
		knight.setDescription(TestValues.getTestTemplate().getPieces()[2]);		//new knight with l-shape movement
		gameState.getGrid()[9][0] = knight.getId();								//knight is only able to move 2up1right or 2right1up
		ArrayList<Move> shapeMoves = new ArrayList<Move>();						//this ArrayList contains both possible moves
		Move move1 = new Move();
		move1.setNewPosition(new int[] {7,1});
		move1.setPieceId(knight.getId());
		Move move2 = new Move();
		move2.setNewPosition(new int[] {8,2});
		move2.setPieceId(knight.getId());
		shapeMoves.add(move1);
		shapeMoves.add(move2);
		
		ArrayList<Move> aiToolsShapeMoves = new ArrayList<Move>();
		try {
			 aiToolsShapeMoves = AI_Tools.createShapeMoveList(gameState, knight);
		} catch (InvalidShapeException e) {
			fail("All shapes are valid");
		}
		
		assertEquals(shapeMoves.size(), aiToolsShapeMoves.size());				//both ArrayLists should have the same (ammount of) elements
		assertArrayEquals(shapeMoves.get(0).getNewPosition(), aiToolsShapeMoves.get(0).getNewPosition());
		assertEquals(shapeMoves.get(0).getPieceId(), aiToolsShapeMoves.get(0).getPieceId());
		assertArrayEquals(shapeMoves.get(1).getNewPosition(), aiToolsShapeMoves.get(1).getNewPosition());
		assertEquals(shapeMoves.get(1).getPieceId(), aiToolsShapeMoves.get(1).getPieceId());
	}

	@Test
	void testCreateDirectionMap() {
		HashMap<Integer, Integer> dirMap = new HashMap<Integer, Integer>();
		Piece picked = gameState.getTeams()[1].getPieces()[1];					//rook on 7,3
		dirMap.put(2, 2);														//the rook can move 2 fields up
		dirMap.put(3, 2);														//the rook can move 2 fields down
		assertEquals(dirMap, AI_Tools.createDirectionMap(gameState, picked));
		
		gameState.getGrid()[6][4] = "b";										//completely enclosing the rook on 7,4
		picked = gameState.getTeams()[1].getPieces()[2];						//rook on 7,4
		assertEquals(new HashMap<Integer, Integer>(), AI_Tools.createDirectionMap(gameState, picked));
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
		weakPiece.setDescription(TestValues.getTestTemplate().getPieces()[0]);

		assertTrue(RandomAI.validPos(new int[] {3,3}, gameState.getTeams()[0].getPieces()[0], gameState));		//valid empty position
		assertFalse(RandomAI.validPos(new int[] {-1,0}, gameState.getTeams()[0].getPieces()[0], gameState));	//out of bounds 1
		assertFalse(RandomAI.validPos(new int[] {10,0}, gameState.getTeams()[0].getPieces()[0], gameState));	//out of bounds 2
		assertTrue(RandomAI.validPos(new int[] {2,2}, gameState.getTeams()[0].getPieces()[0], gameState));		//rook team1 captures another rook from team0
		assertFalse(RandomAI.validPos(new int[] {2,2}, weakPiece, gameState));									//weak Pawn cannot capture a stronger rook from team0
		assertFalse(RandomAI.validPos(new int[] {7,2}, gameState.getTeams()[0].getPieces()[0], gameState));		//rook team1 cannot capture a team1 piece
		assertFalse(RandomAI.validPos(new int[] {5,3}, gameState.getTeams()[0].getPieces()[0], gameState));		//5,3 is occupied by a block
		assertFalse(RandomAI.validPos(new int[] {9,9}, gameState.getTeams()[0].getPieces()[0], gameState));		//it is not possible to walk on the own base
		assertTrue(RandomAI.validPos(new int[] {0,0}, gameState.getTeams()[0].getPieces()[0], gameState));		//it is possible to walk on an opponents base
	}

	@Test
	void testGetReach() {
		Directions rookDirections = TestValues.getTestTemplate().getPieces()[1].getMovement().getDirections();

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
}
