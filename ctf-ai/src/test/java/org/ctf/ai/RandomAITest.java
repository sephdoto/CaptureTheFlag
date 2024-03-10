package org.ctf.ai;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.ctf.client.state.data.map.MapTemplate;
import org.ctf.client.state.GameState;
import org.ctf.client.tools.JSON_Tools;
import org.ctf.client.tools.JSON_Tools.MapNotFoundException;


class RandomAITest {
  static GameState gameState;

  @SuppressWarnings("deprecation")
  @BeforeEach
  void setUp() throws Exception{
    try {
      MapTemplate mt = JSON_Tools.readMapTemplate("10x10_2teams_example");
      gameState = AI_Controller.getTestState(mt);
    } catch (MapNotFoundException e) {e.printStackTrace();}
  }
  
  @Test
  void testPickMoveSimple() {
    fail("Not yet implemented");
  }

  @Test
  void testPickMoveComplex() {
    fail("Not yet implemented");
  }

  @Test
  void testGetShapeMove() {
    fail("Not yet implemented");
  }

  @Test
  void testValidShapeDirection() {
    fail("Not yet implemented");
  }

  @Test
  void testGetDirectionMove() {
    fail("Not yet implemented");
  }

  @Test
  void testValidDirection() {
    fail("Not yet implemented");
  }

  @Test
  void testCheckMoveValidity() {
    fail("Not yet implemented");
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
    fail("Not yet implemented");
  }

  @Test
  void testValidPos() {
    fail("Not yet implemented");
  }

  @Test
  void testGetReach() {
    fail("Not yet implemented");
  }
}