package de.unimannheim.swt.pse.ctf.game;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.unimannheim.swt.pse.ctf.game.state.GameState;
import de.unimannheim.swt.pse.ctf.game.state.Move;
import de.unimannheim.swt.pse.ctf.game.state.Piece;

class GameEngineTest {
	static GameEngine gameEngine;

	@BeforeEach
	void setUp() {
		gameEngine = new GameEngine(TestValues.getTestState());
	}

  @Test
  void testCreate() {
    fail("Not yet implemented");
  }

  @Test
  void testGetCurrentGameState() {
    fail("Not yet implemented");
  }

  @Test
  void testGetEndDate() {
    fail("Not yet implemented");
  }

  @Test
  void testGetRemainingGameTimeInSeconds() {
    fail("Not yet implemented");
  }

  @Test
  void testGetRemainingMoveTimeInSeconds() {
    fail("Not yet implemented");
  }

  @Test
  void testGetRemainingTeamSlots() {
    fail("Not yet implemented");
  }

  @Test
  void testGetStartedDate() {
    fail("Not yet implemented");
  }

  @Test
  void testGetWinner() {
    fail("Not yet implemented");
  }

  @Test
  void testGiveUp() {
    fail("Not yet implemented");
  }

  @Test
  void testIsGameOver() {
    fail("Not yet implemented");
  }

  @Test
  void testIsStarted() {
    fail("Not yet implemented");
  }

  @Test
  void testIsValidMove() {
		Piece rook = gameEngine.getCurrentGameState().getTeams()[1].getPieces()[1];			//rook on 7,3
		Piece rook2 = gameEngine.getCurrentGameState().getTeams()[1].getPieces()[3];		//rook on 7,5
		Move move1 = new Move();
		move1.setPieceId(rook.getId());
		Move move2 = new Move();
		move2.setPieceId(rook2.getId());

		move1.setNewPosition(new int[] {7,1});
		assertFalse(gameEngine.isValidMove(move1));		//rook cannot walk over another same team rook
		move1.setNewPosition(new int[] {7,2});
		assertFalse(gameEngine.isValidMove(move1));		//rook cannot walk onto another same team rook
		move1.setNewPosition(new int[] {6,3});
		assertTrue(gameEngine.isValidMove(move1));		//rook can walk on the empty space above
		move1.setNewPosition(new int[] {6,4});
		assertFalse(gameEngine.isValidMove(move1));		//rook cannot jump over the block above TODO
		move1.setNewPosition(new int[] {8,3});
		assertTrue(gameEngine.isValidMove(move1));		//rook can walk on the empty space below
		move2.setNewPosition(new int[] {4,5});
		assertFalse(gameEngine.isValidMove(move2));		//rook could not walk 3 blocks (just 2)
		move2.setNewPosition(new int[] {7,5});
		assertFalse(gameEngine.isValidMove(move2));		//piece could not walk onto its own position
  }

  @Test
  void testJoinGame() {
    fail("Not yet implemented");
  }

  @Test
  void testMakeMove() {
    fail("Not yet implemented");
  }

  @Test
  void testRandomGen() {
    fail("Not yet implemented");
  }

}
