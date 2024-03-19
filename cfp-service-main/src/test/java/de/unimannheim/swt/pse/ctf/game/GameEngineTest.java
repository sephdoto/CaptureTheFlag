package de.unimannheim.swt.pse.ctf.game;

import static org.junit.jupiter.api.Assertions.*;
import java.sql.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.unimannheim.swt.pse.ctf.game.state.GameState;
import de.unimannheim.swt.pse.ctf.game.state.Move;
import de.unimannheim.swt.pse.ctf.game.state.Piece;

class GameEngineTest {
	static GameEngine gameEngine;

	@BeforeEach
	void setUp() {
		gameEngine = new GameEngine(TestValues.getTestState(), false, true, new Date(System.currentTimeMillis() + 10000));
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
    assertEquals(9, gameEngine.getRemainingGameTimeInSeconds());    //the freshly started game ends 10 seconds after generating, returned int time should be 9
    
    gameEngine = new GameEngine(TestValues.getTestState(), false, true, new Date(System.currentTimeMillis() - 10));
    assertEquals(0, gameEngine.getRemainingGameTimeInSeconds());    //the freshly started game ended a few ms ago, returned time should be 0 (game over)
    
    gameEngine = new GameEngine(TestValues.getTestState(), false, false, new Date(System.currentTimeMillis() - 10));
    assertEquals(-1, gameEngine.getRemainingGameTimeInSeconds());    //the freshly started game got no time limit, returned time should be -1 (no time limit set)
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
  void testGameOverCheck() {
    gameEngine.gameOverCheck();
    assertFalse(gameEngine.isGameOver());                                       //ongoing game, not game over
    
    GameState gameState = TestValues.getTestState();
    gameState.getTeams()[0].setFlags(0);
    gameEngine = new GameEngine(gameState, false, true, new Date(852003));      //new GameEngine with modified gameState (team0 flags = 0)
    gameEngine.gameOverCheck();
    assertTrue(gameEngine.isGameOver());                                        //a team got no flags left
  
    gameState = TestValues.getTestState();
    gameState.getTeams()[0].setPieces(new Piece[] {});
    gameEngine = new GameEngine(gameState, false, true, new Date(852003));      //new GameEngine with modified gameState (team0 pieces = {})
    gameEngine.gameOverCheck();
    assertTrue(gameEngine.isGameOver());                                        //a team got no pieces left
  
    gameState = TestValues.getTestState();
    gameEngine = new GameEngine(gameState, false, true, new Date(System.currentTimeMillis() - 1000000));  //new GameEngine with modified gameState (already ended)
    gameEngine.gameOverCheck();
    assertTrue(gameEngine.isGameOver());                                        //game time over
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
