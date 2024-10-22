package org.ctf.shared.ai;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import java.util.ArrayList;
import org.ctf.shared.ai.GameUtilities.InvalidShapeException;
import org.ctf.shared.ai.random.RandomAI;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Piece;
import org.ctf.shared.state.data.map.Directions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for original GameUtilities Methods, used in RandomAI
 *
 * @author sistumpf
 */
public class AI_ToolsTest {
  static GameState gameState;
  static ReferenceMove operateOn = new ReferenceMove(null, new int[] {0,0});
  
  @BeforeEach
  void setUp() {
    gameState = TestValues.getTestState();
  }

  @Test
  void testToNextTeam() {
    GameState gameState = TestValues.getTestState();
    gameState.getTeams()[1] = null;
    gameState.setCurrentTeam(0);
    GameUtilities.toNextTeam(gameState);
    assertTrue(gameState.getCurrentTeam() != 1);
  }
  
  @Test
  void testGetRandomShapeMove() {
    Move move1 = new Move();
    ArrayList<int[]> moveList = new ArrayList<int[]>();
    move1.setNewPosition(new int[] {0,0});
    move1.setPieceId("");
    moveList.add(move1.getNewPosition());

    assertEquals(
        move1.getNewPosition(), RandomAI.getRandomShapeMove(moveList, null, operateOn).getNewPosition());
    assertEquals(move1.getPieceId(), RandomAI.getRandomShapeMove(moveList, null, operateOn).toMove().getPieceId());
  }

  @Test
  void testGetShapeMoves() {
    Piece knight = new Piece();
    knight.setId("p:1_9");
    knight.setPosition(new int[] {9, 0});
    knight.setTeamId("team1");
    knight.setDescription(
        TestValues.getTestTemplate().getPieces()[2]); // new knight with l-shape movement
    gameState.getGrid()[9][0] =
        knight.getId(); // knight is only able to move 2up1right or 2right1up (+ spaces in-between)
    gameState.getGrid()[9][1] = "b"; // blocking off 3 possible moves, 3 remain
    ArrayList<int[]> shapeMoves =
        new ArrayList<int[]>(); // this ArrayList contains all possible moves
    shapeMoves.add(new int[] {7, 1});
    shapeMoves.add(new int[] {7, 0});
    shapeMoves.add(new int[] {8, 0});

    ArrayList<int[]> aiToolsShapeMoves = new ArrayList<int[]>();
    try {
      aiToolsShapeMoves = GameUtilities.getShapeMoves(gameState, knight, new ArrayList<int[]>());
    } catch (InvalidShapeException e) {
      fail("All shapes are valid");
    }

    assertEquals(
        shapeMoves.size(),
        aiToolsShapeMoves.size()); // both ArrayLists should have the same (ammount of) elements
    assertArrayEquals(shapeMoves.get(0), aiToolsShapeMoves.get(0));
    assertArrayEquals(shapeMoves.get(1), aiToolsShapeMoves.get(1));
    assertArrayEquals(shapeMoves.get(2), aiToolsShapeMoves.get(2));
  }

  @Test
  void testCreateDirectionMap() {
    ArrayList<int[]> dirMap = new ArrayList<int[]>();
    Piece picked = gameState.getTeams()[1].getPieces()[1]; // rook on 7,3
    dirMap.add(new int[] {2, 2}); // the rook can move 2 fields up
    dirMap.add(new int[] {3, 2}); // the rook can move 2 fields down
    assertArrayEquals(
        dirMap.toArray(),
        GameUtilities.createDirectionMap(gameState, picked, new ArrayList<int[]>(), operateOn).toArray());

    gameState.getGrid()[6][4] = "b"; // completely enclosing the rook on 7,4
    picked = gameState.getTeams()[1].getPieces()[2]; // rook on 7,4
    assertEquals(
        new ArrayList<int[]>(),
        GameUtilities.createDirectionMap(gameState, picked, new ArrayList<int[]>(), operateOn));
  }

  @Test
  void testGetDirectionMove() {
    Piece rook = gameState.getTeams()[1].getPieces()[1]; // rook on 7,3
    ArrayList<int[]> dirMap = new ArrayList<int[]>();
    dirMap.add(new int[] {2, 5}); // rook can only move to one field ()
    int[] onlyPosition = new int[] {6, 3}; // the only possible Position is 6,3
    int[] newPosition = RandomAI.getDirectionMove(dirMap, rook, gameState, operateOn).getNewPosition();

    assertArrayEquals(onlyPosition, newPosition);
  }

  /**
   * This method does not test if a pieces reach in a direction is >0. It just tests if a direction
   * is accessible.
   */
  @Test
  void testValidDirection() {
    Piece rook = gameState.getTeams()[1].getPieces()[1]; // rook on 7,3

    assertFalse(
        RandomAI.validDirection(
            gameState, rook, 0, operateOn)); // a piece on the left doesn't allow the movement to the left
    assertFalse(
        RandomAI.validDirection(
            gameState, rook, 1, operateOn)); // a piece on the right doesn't allow the movement to the right
    assertTrue(
        RandomAI.validDirection(
            gameState, rook, 2, operateOn)); // there's a free position above, this direction is accessible
    assertTrue(
        RandomAI.validDirection(
            gameState, rook, 3, operateOn)); // there's a free position below, this direction is accessible
    assertTrue(
        RandomAI.validDirection(
            gameState, rook,
            4, operateOn)); // there's a free position above-left, this direction is accessible
    assertTrue(
        RandomAI.validDirection(
            gameState, rook,
            5, operateOn)); // there's a free position above-right, this direction is accessible
    assertTrue(
        RandomAI.validDirection(
            gameState, rook,
            6, operateOn)); // there's a free position below-left, this direction is accessible
    assertFalse(
        RandomAI.validDirection(
            gameState, rook, 7, operateOn)); // there's a piece below-right, this direction is not accessible
  }

  /**
   * This method does not test if a pieces reach in a direction is >0. It just tests if a position
   * could be occupied.
   */
  @Test
  void testCheckMoveValidity() {
    Piece rook = gameState.getTeams()[1].getPieces()[1]; // rook on 7,3
    Piece rook2 = gameState.getTeams()[1].getPieces()[3]; // rook on 7,5

    assertNull(
        RandomAI.checkMoveValidity(
            gameState, rook, 0, 2, operateOn).getPiece()); // rook cannot walk over another same team rook
    assertNull(
        RandomAI.checkMoveValidity(
            gameState, rook, 1, 1, operateOn).getPiece()); // rook cannot walk onto another same team rook
    assertNotNull(
        RandomAI.checkMoveValidity(
            gameState, rook, 2, 1, operateOn).getPiece()); // rook can walk on the empty space above
    assertNull(
        RandomAI.checkMoveValidity(
            gameState, rook, 2, 3, operateOn).getPiece()); // rook cannot jump over the block above
    assertNotNull(
        RandomAI.checkMoveValidity(
            gameState, rook, 3, 1, operateOn).getPiece()); // rook can walk on the empty space below
    assertNotNull(
        RandomAI.checkMoveValidity(
            gameState, rook, 6, 1, operateOn).getPiece()); // piece could go to the empty field below-left
    assertNull(
        RandomAI.checkMoveValidity(
            gameState, rook2, 4, 4, operateOn).getPiece()); // piece could not walk over a block to the empty field above-left 3,1
    assertNull(
        RandomAI.checkMoveValidity(
            gameState, rook2, 4, 0, operateOn).getPiece()); // piece could not walk onto its own position
  }

  @Test
  void testSightLine() {
    assertTrue(RandomAI.sightLine(gameState, new int[] {4, 6}, 1, 3)); // free line of sight
    assertTrue(RandomAI.sightLine(gameState, new int[] {7, 2}, 0, 0)); // newPos = oldPos
    assertFalse(RandomAI.sightLine(gameState, new int[] {4, 8}, 1, 2)); // there is one block
    assertFalse(
        RandomAI.sightLine(
            gameState, new int[] {5, 5}, 0, 100)); // newPos is not on the grid, outOfBounds
    assertFalse(
        RandomAI.sightLine(
            gameState,
            new int[] {1, 1},
            4,
            6)); // there is an enemy Piece blocking the line of sight
  }

  @Test
  void testOtherTeamsBase() {
    assertTrue(
        GameUtilities.otherTeamsBase(
            gameState.getGrid(), new int[] {0, 0}, gameState.getTeams()[1].getPieces()[0]));
    assertFalse(
        GameUtilities.otherTeamsBase(
            gameState.getGrid(), new int[] {0, 0}, gameState.getTeams()[0].getPieces()[0]));
  }

  @Test
  void testUpdatePos() {
    int[] posititon = new int[] {5, 5};

    assertArrayEquals(posititon, RandomAI.updatePos(new int[] {5, 6}, 0, 1)); // left
    assertArrayEquals(posititon, RandomAI.updatePos(new int[] {5, 4}, 1, 1)); // right
    assertArrayEquals(posititon, RandomAI.updatePos(new int[] {6, 5}, 2, 1)); // up
    assertArrayEquals(posititon, RandomAI.updatePos(new int[] {4, 5}, 3, 1)); // down
    assertArrayEquals(posititon, RandomAI.updatePos(new int[] {6, 6}, 4, 1)); // up left
    assertArrayEquals(posititon, RandomAI.updatePos(new int[] {6, 4}, 5, 1)); // up right
    assertArrayEquals(posititon, RandomAI.updatePos(new int[] {4, 6}, 6, 1)); // down left
    assertArrayEquals(posititon, RandomAI.updatePos(new int[] {4, 4}, 7, 1)); // down right
    assertArrayEquals(
        new int[] {0, 0},
        RandomAI.updatePos(new int[] {9, 9}, 4, 9)); // lower right corner to upper left corner
  }

  @Test
  void testValidPos() {
    Piece weakPiece = new Piece();
    weakPiece.setDescription(TestValues.getTestTemplate().getPieces()[0]);

    assertTrue(
        RandomAI.validPos(
            new int[] {3, 3},
            gameState.getTeams()[0].getPieces()[0],
            gameState)); // valid empty position
    assertFalse(
        RandomAI.validPos(
            new int[] {-1, 0},
            gameState.getTeams()[0].getPieces()[0],
            gameState)); // out of bounds 1
    assertFalse(
        RandomAI.validPos(
            new int[] {10, 0},
            gameState.getTeams()[0].getPieces()[0],
            gameState)); // out of bounds 2
    assertTrue(
        RandomAI.validPos(
            new int[] {2, 2},
            gameState.getTeams()[0].getPieces()[0],
            gameState)); // rook team1 captures another rook from team0
    assertFalse(
        RandomAI.validPos(
            new int[] {2, 2},
            weakPiece,
            gameState)); // weak Pawn cannot capture a stronger rook from team0
    assertFalse(
        RandomAI.validPos(
            new int[] {7, 2},
            gameState.getTeams()[0].getPieces()[0],
            gameState)); // rook team1 cannot capture a team1 piece
    assertFalse(
        RandomAI.validPos(
            new int[] {5, 3},
            gameState.getTeams()[0].getPieces()[0],
            gameState)); // 5,3 is occupied by a block
    gameState.setCurrentTeam(0);
    assertTrue(
        RandomAI.validPos(
            new int[] {9, 9},
            gameState.getTeams()[0].getPieces()[0],
            gameState)); // it is possible to walk on an opponents base
    gameState.setCurrentTeam(1);
    assertTrue(
        RandomAI.validPos(
            new int[] {0, 0},
            gameState.getTeams()[1].getPieces()[0],
            gameState)); // it is possible to walk on an opponents base
    gameState.setCurrentTeam(0);
    assertFalse(
        RandomAI.validPos(
            new int[] {0, 0},
            gameState.getTeams()[0].getPieces()[0],
            gameState)); // it is not possible to walk on the own base
  }

  @Test
  void testGetReach() {
    Directions rookDirections =
        TestValues.getTestTemplate().getPieces()[1].getMovement().getDirections();

    assertEquals(2, RandomAI.getReach(rookDirections, 0));
    assertEquals(2, RandomAI.getReach(rookDirections, 1));
    assertEquals(2, RandomAI.getReach(rookDirections, 2));
    assertEquals(2, RandomAI.getReach(rookDirections, 3));
    assertEquals(0, RandomAI.getReach(rookDirections, 4));
    assertEquals(0, RandomAI.getReach(rookDirections, 5));
    assertEquals(0, RandomAI.getReach(rookDirections, 6));
    assertEquals(0, RandomAI.getReach(rookDirections, 7));
    assertEquals(-1, RandomAI.getReach(rookDirections, 8)); // invalid direction: -1
  }
}
