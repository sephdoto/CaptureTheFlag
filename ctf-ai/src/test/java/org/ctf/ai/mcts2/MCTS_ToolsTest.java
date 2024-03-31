package org.ctf.ai.mcts2;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import org.ctf.ai.TestValues;
import org.ctf.ai.AI_Tools.InvalidShapeException;
import org.ctf.ai.AI_Tools.NoMovesLeftException;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Piece;
import org.ctf.shared.state.Team;
import org.ctf.shared.state.data.map.Directions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MCTS_ToolsTest {
  static GameState gameState;

  @BeforeEach
  void setUp() {
      gameState = TestValues.getTestState();
  }
  
  @Test
  void testPutNeighbouringPieces() {
    ArrayList<Piece> updateThese = new ArrayList<Piece>();
    MCTS mcts = new MCTS(new TreeNode(null, gameState, null));
    MCTS_Tools.putNeighbouringPieces(updateThese, mcts.root.grid, mcts.root.gameState.getTeams()[0].getPieces()[2]);
    ArrayList<Piece> trueNeighbours = new ArrayList<Piece>();
    trueNeighbours.add(mcts.root.gameState.getTeams()[0].getPieces()[3]);
    assertTrue(trueNeighbours.equals(updateThese));
  }
  
  /*@Test
  void testPossibleMovesWithPieceVision() {
    GameState gameState = TestValues.getEmptyTestState();
    gameState.getTeams()[0].
    TreeNode node = new TreeNode(null, gameState, null);
    
  }*/
  
  @Test
  void testPickMoveComplex() {
    try {
      MCTS_Tools.pickMoveComplex(gameState, new Grid(gameState));
    } catch (NoMovesLeftException e) {
      fail("there should be moves left");
    } catch (InvalidShapeException e) {e.printStackTrace();}
    
    gameState = TestValues.getEmptyTestState();
    boolean noMoves = false;
    try {
      MCTS_Tools.pickMoveComplex(gameState, new Grid(gameState));
    } catch (NoMovesLeftException e) {
      noMoves = true;
    } catch (InvalidShapeException e) {e.printStackTrace();}
    assertTrue(noMoves);
  }
  
  @Test
  void testUpdatePos() {
      int[] posititon = new int[] {5,5};

      assertArrayEquals(posititon, MCTS_Tools.updatePos(new int[] {5,6}, 0, 1));        //left
      assertArrayEquals(posititon, MCTS_Tools.updatePos(new int[] {5,4}, 1, 1));        //right
      assertArrayEquals(posititon, MCTS_Tools.updatePos(new int[] {6,5}, 2, 1));        //up
      assertArrayEquals(posititon, MCTS_Tools.updatePos(new int[] {4,5}, 3, 1));        //down
      assertArrayEquals(posititon, MCTS_Tools.updatePos(new int[] {6,6}, 4, 1));        //up left
      assertArrayEquals(posititon, MCTS_Tools.updatePos(new int[] {6,4}, 5, 1));        //up right
      assertArrayEquals(posititon, MCTS_Tools.updatePos(new int[] {4,6}, 6, 1));        //down left
      assertArrayEquals(posititon, MCTS_Tools.updatePos(new int[] {4,4}, 7, 1));        //down right
      assertArrayEquals(new int[]{0,0}, MCTS_Tools.updatePos(new int[] {9,9}, 4, 9));   //lower right corner to upper left corner
  }

  @Test
  void testValidPos() {
      Piece weakPiece = new Piece();
      weakPiece.setDescription(TestValues.getTestTemplate().getPieces()[0]);

      assertTrue(MCTS_Tools.validPos(new int[] {3,3}, gameState.getTeams()[0].getPieces()[0], gameState, new Grid(gameState)));      //valid empty position
      assertFalse(MCTS_Tools.validPos(new int[] {-1,0}, gameState.getTeams()[0].getPieces()[0], gameState, new Grid(gameState)));    //out of bounds 1
      assertFalse(MCTS_Tools.validPos(new int[] {10,0}, gameState.getTeams()[0].getPieces()[0], gameState, new Grid(gameState)));    //out of bounds 2
      assertTrue(MCTS_Tools.validPos(new int[] {2,2}, gameState.getTeams()[0].getPieces()[0], gameState, new Grid(gameState)));      //rook team1 captures another rook from team0
      assertFalse(MCTS_Tools.validPos(new int[] {2,2}, weakPiece, gameState, new Grid(gameState)));                                  //weak Pawn cannot capture a stronger rook from team0
      assertFalse(MCTS_Tools.validPos(new int[] {7,2}, gameState.getTeams()[0].getPieces()[0], gameState, new Grid(gameState)));     //rook team1 cannot capture a team1 piece
      assertFalse(MCTS_Tools.validPos(new int[] {5,3}, gameState.getTeams()[0].getPieces()[0], gameState, new Grid(gameState)));     //5,3 is occupied by a block
      gameState.setCurrentTeam(0);
      assertTrue(MCTS_Tools.validPos(new int[] {9,9}, gameState.getTeams()[0].getPieces()[0], gameState, new Grid(gameState)));      //it is possible to walk on an opponents base
      gameState.setCurrentTeam(1);
      assertTrue(MCTS_Tools.validPos(new int[] {0,0}, gameState.getTeams()[1].getPieces()[0], gameState, new Grid(gameState)));      //it is possible to walk on an opponents base
      gameState.setCurrentTeam(0);
      assertFalse(MCTS_Tools.validPos(new int[] {0,0}, gameState.getTeams()[0].getPieces()[0], gameState, new Grid(gameState)));      //it is not possible to walk on the own base
  }

  @Test
  void testGetReach() {
      Directions rookDirections = TestValues.getTestTemplate().getPieces()[1].getMovement().getDirections();

      assertEquals(2, MCTS_Tools.getReach(rookDirections, 0));
      assertEquals(2, MCTS_Tools.getReach(rookDirections, 1));
      assertEquals(2, MCTS_Tools.getReach(rookDirections, 2));
      assertEquals(2, MCTS_Tools.getReach(rookDirections, 3));
      assertEquals(0, MCTS_Tools.getReach(rookDirections, 4));
      assertEquals(0, MCTS_Tools.getReach(rookDirections, 5));
      assertEquals(0, MCTS_Tools.getReach(rookDirections, 6));
      assertEquals(0, MCTS_Tools.getReach(rookDirections, 7));
      assertEquals(-1, MCTS_Tools.getReach(rookDirections, 8));     //invalid direction: -1
  }
  
  /**
   * This method does not test if a pieces reach in a direction is >0.
   * It just tests if a direction is accessible.
   */
  @Test
  void testValidDirection() {
      Piece rook = gameState.getTeams()[1].getPieces()[1];                //rook on 7,3

      assertFalse(MCTS_Tools.validDirection(gameState, new Grid(gameState), rook, 0));           //a piece on the left doesn't allow the movement to the left
      assertFalse(MCTS_Tools.validDirection(gameState, new Grid(gameState), rook, 1));           //a piece on the right doesn't allow the movement to the right
      assertTrue(MCTS_Tools.validDirection(gameState, new Grid(gameState), rook, 2));            //there's a free position above, this direction is accessible
      assertTrue(MCTS_Tools.validDirection(gameState, new Grid(gameState), rook, 3));            //there's a free position below, this direction is accessible
      assertTrue(MCTS_Tools.validDirection(gameState, new Grid(gameState), rook, 4));            //there's a free position above-left, this direction is accessible
      assertTrue(MCTS_Tools.validDirection(gameState, new Grid(gameState), rook, 5));            //there's a free position above-right, this direction is accessible
      assertTrue(MCTS_Tools.validDirection(gameState, new Grid(gameState), rook, 6));            //there's a free position below-left, this direction is accessible
      assertFalse(MCTS_Tools.validDirection(gameState, new Grid(gameState), rook, 7));           //there's a piece below-right, this direction is not accessible
  }

  /**
   * This method does not test if a pieces reach in a direction is >0.
   * It just tests if a position could be occupied.
   */
  @Test
  void testCheckMoveValidity() {
      Piece rook = gameState.getTeams()[1].getPieces()[1];                //rook on 7,3
      Piece rook2 = gameState.getTeams()[1].getPieces()[3];               //rook on 7,5

      assertNull(MCTS_Tools.checkMoveValidity(gameState, new Grid(gameState), rook, 0, 2));      //rook cannot walk over another same team rook
      assertNull(MCTS_Tools.checkMoveValidity(gameState, new Grid(gameState), rook, 1, 1));      //rook cannot walk onto another same team rook
      assertNotNull(MCTS_Tools.checkMoveValidity(gameState, new Grid(gameState), rook, 2, 1));   //rook can walk on the empty space above
      assertNull(MCTS_Tools.checkMoveValidity(gameState, new Grid(gameState), rook, 2, 3));      //rook cannot jump over the block above
      assertNotNull(MCTS_Tools.checkMoveValidity(gameState, new Grid(gameState), rook, 3, 1));   //rook can walk on the empty space below
      assertNotNull(MCTS_Tools.checkMoveValidity(gameState, new Grid(gameState), rook, 6, 1));   //piece could go to the empty field below-left
      assertNull(MCTS_Tools.checkMoveValidity(gameState, new Grid(gameState), rook2, 4, 4));     //piece could not walk over a block to the empty field above-left 3,1
      assertNull(MCTS_Tools.checkMoveValidity(gameState, new Grid(gameState), rook2, 4, 0));//piece could not walk onto its own position
  }

  @Test
  void testSightLine() {
      assertTrue(MCTS_Tools.sightLine(gameState, new Grid(gameState), new int[]{4,6}, 1, 3));    //free line of sight
      assertTrue(MCTS_Tools.sightLine(gameState, new Grid(gameState), new int[]{7,2}, 0, 0));    //newPos = oldPos
      assertFalse(MCTS_Tools.sightLine(gameState, new Grid(gameState), new int[]{4,8}, 1, 2));   //there is one block
      assertFalse(MCTS_Tools.sightLine(gameState, new Grid(gameState), new int[]{5,5}, 0, 100)); //newPos is not on the grid, outOfBounds
      assertFalse(MCTS_Tools.sightLine(gameState, new Grid(gameState), new int[]{1,1}, 4, 6));   //there is an enemy Piece blocking the line of sight
  }
  
  @Test
  void testOtherTeamsBase() {
    assertTrue(MCTS_Tools.otherTeamsBase(new Grid(gameState), new int[] {0,0}, gameState.getTeams()[1].getPieces()[0].getPosition()));
    assertFalse(MCTS_Tools.otherTeamsBase( new Grid(gameState), new int[] {0,0}, gameState.getTeams()[0].getPieces()[0].getPosition()));
  }
  
  @Test
  void testGetRandomShapeMove() {
      Move move1 = new Move();
      ArrayList<int[]> moveList = new ArrayList<int[]>();
      move1.setNewPosition(null);
      move1.setPieceId(null);
      moveList.add(move1.getNewPosition());

      assertEquals(move1.getNewPosition(), MCTS_Tools.getRandomShapeMove(moveList, null).getNewPosition());
      assertEquals(move1.getPieceId(), MCTS_Tools.getRandomShapeMove(moveList, null).getPieceId());
  }

  @Test
  void testGetShapeMoves() {
      Piece knight = new Piece();
      knight.setId("p:1_8");
      knight.setPosition(new int[]{9,0});
      knight.setTeamId("team1");
      knight.setDescription(TestValues.getTestTemplate().getPieces()[2]);     //new knight with l-shape movement
      gameState.getTeams()[1].getPieces()[7] = knight;
      gameState.getGrid()[9][0] = knight.getId();                             //knight is only able to move 2up1right or 2right1up (+ spaces in-between)
      gameState.getGrid()[9][1] = "b";                                        //blocking off 3 possible moves, 3 remain
      ArrayList<int[]> shapeMoves = new ArrayList<int[]>();                   //this ArrayList contains all possible moves
      shapeMoves.add(new int[] {7,1});
      shapeMoves.add(new int[] {7,0});
      shapeMoves.add(new int[] {8,0});
      
      ArrayList<int[]> aiToolsShapeMoves = new ArrayList<int[]>();
      try {
           aiToolsShapeMoves = MCTS_Tools.getShapeMoves(gameState, new Grid(gameState), knight, new ArrayList<int[]>());
      } catch (InvalidShapeException e) {
          fail("All shapes are valid");
      }
      
      assertEquals(shapeMoves.size(), aiToolsShapeMoves.size());              //both ArrayLists should have the same (ammount of) elements
      assertArrayEquals(shapeMoves.get(0), aiToolsShapeMoves.get(0));
      assertArrayEquals(shapeMoves.get(1), aiToolsShapeMoves.get(1));
      assertArrayEquals(shapeMoves.get(2), aiToolsShapeMoves.get(2));
  }

  @Test
  void testCreateDirectionMap() {
      ArrayList<int[]> dirMap = new ArrayList<int[]>();
      Piece picked = gameState.getTeams()[1].getPieces()[1];                              //rook on 7,3
      dirMap.add(new int[] {2, 2});                                                       //the rook can move 2 fields up
      dirMap.add(new int[] {3, 2});                                                       //the rook can move 2 fields down
      assertArrayEquals(dirMap.toArray(), MCTS_Tools.createDirectionMap(gameState, new Grid(gameState), picked, new ArrayList<int[]>()).toArray());
      
      gameState.getGrid()[6][4] = "b";                                                    //completely enclosing the rook on 7,4
      picked = gameState.getTeams()[1].getPieces()[2];                                    //rook on 7,4
      assertEquals(new ArrayList<int[]>(), MCTS_Tools.createDirectionMap(gameState, new Grid(gameState), picked, new ArrayList<int[]>()));
  }
  
  @Test
  void testGetDirectionMove() {
      Piece rook = gameState.getTeams()[1].getPieces()[1];                //rook on 7,3
      ArrayList<int[]> dirMap = new ArrayList<int[]>();
      dirMap.add(new int[] {2,5});                                        //rook can only move to one field ()
      int[] onlyPosition = new int[] {6,3};                               //the only possible Position is 6,3
      int[] newPosition = MCTS_Tools.getDirectionMove(dirMap, rook, gameState, new Grid(gameState)).getNewPosition();

      assertArrayEquals(onlyPosition, newPosition);
  }
  
  @Test 
  void seededRandomTest(){
    assertEquals(MCTS_Tools.seededRandom(new Grid(TestValues.getTestState()), 0, 10, 0),
    org.ctf.ai.AI_Tools.seededRandom(TestValues.getTestState().getGrid(), 0, 10, 0));
  }
  
  
  
  
  
  @Test
  void testToNextTeam() {
    GameState gameState = new GameState();
    gameState.setCurrentTeam(0);
    Team[] teams = new Team[5];
    for(int i=0; i<5; i++)
      teams[i] = null;
    teams[3] = new Team();
    gameState.setTeams(teams);
    
    MCTS_Tools.toNextTeam(gameState);

    assertEquals(gameState.getTeams()[gameState.getCurrentTeam()].getClass(), Team.class);
    gameState.getTeams()[3] = null;
    gameState.getTeams()[1] = new Team();
    
    MCTS_Tools.toNextTeam(gameState);

    assertEquals(gameState.getTeams()[gameState.getCurrentTeam()].getClass(), Team.class);
  }

  @Test
  void testGetPossibleMoves() {
    GameState gameState = TestValues.getTestState();
    Piece piece = gameState.getTeams()[1].getPieces()[1];
    ArrayList<int[]> moves = MCTS_Tools.getPossibleMoves(gameState, new Grid(gameState), piece, new ArrayList<int[]>());

    ArrayList<int[]> actuallyValidMoves = new ArrayList<int[]>();
    actuallyValidMoves.add(new int[] {6,3});
    actuallyValidMoves.add(new int[] {9,3});
    actuallyValidMoves.add(new int[] {8,3});
    
    assertArrayEquals(moves.toArray(), actuallyValidMoves.toArray());
    
    gameState.getGrid()[6][3] = "b";
    gameState.getGrid()[8][3] = "b";
    moves = MCTS_Tools.getPossibleMoves(gameState, new Grid(gameState), piece, new ArrayList<int[]>());
    actuallyValidMoves.clear();
    
    assertArrayEquals(moves.toArray(), actuallyValidMoves.toArray());
  }
  
  @Test
  void testRespawnPiecePosition() {
    GameState gameState = TestValues.getTestState();
    int[] basePos = new int[] {2,4};
    gameState.getGrid()[0][0] = "";
    gameState.getGrid()[2][4] = "b:0";
    gameState.getGrid()[1][3] = "b";
    gameState.getGrid()[3][3] = "b";
    gameState.getGrid()[3][5] = "b";                                    //1 free field in direct contact to 2,4: 3,4
    int[] pos = MCTS_Tools.respawnPiecePosition(new Grid(gameState) ,basePos);
    assertArrayEquals(new int[] {3,4}, pos);

    gameState.getGrid()[3][4] = "b";                                    //block last free field, distance from base must be +1
    pos = MCTS_Tools.respawnPiecePosition(new Grid(gameState), basePos);
    assertArrayEquals(new int[] {4,5}, pos);                            //randomly chosen field: 4,5. Should stay the same every time (with this gameState)
    
    gameState.getGrid()[0][0] = "b";                                    //alter gameState so the seeded random chooses another field, even though 4,4 is free to be occupied
    pos = MCTS_Tools.respawnPiecePosition(new Grid(gameState), basePos);
    assertArrayEquals(new int[] {3,6}, pos);                            //randomly chosen field: 3,6. Should stay the same every time (with this gameState)
    
    for(int i= 0; i<gameState.getGrid().length; i++) {                  //place a block onto every free position on the grid
      for(int j=0; j<gameState.getGrid().length; j++) {
        if(gameState.getGrid()[i][j] == "") {
          gameState.getGrid()[i][j] = "b";
        }
      }
    }
    
    for(int i= 0; i<gameState.getGrid().length; i++) {
      for(int j=0; j<gameState.getGrid().length; j++) {
        if(gameState.getGrid()[i][j].equals("b")) {
          gameState.getGrid()[i][j] = "";
          pos = MCTS_Tools.respawnPiecePosition(new Grid(gameState), basePos);
          assertArrayEquals(new int[] {i,j}, pos);                       //goes through all the blocks, removes one, tests if the position would be respawned on, places the block back. repeats for all blocks.
          gameState.getGrid()[i][j] = "b"; 
        }
      }
    }
  }
  
  @Test
  void testXTransformations() {
    int[] dist1 = new int[] {-1, 0, 1, 1, 1, 0, -1, -1};
    int[] dist2 = new int[] {-2, -1, 0, 1, 2, 2, 2, 2, 2, 1, 0, -1, -2, -2, -2, -2};
    int[] dist3 = new int[] {-3, -2, -1, 0, 1, 2, 3, 3, 3, 3, 3, 3, 3, 2, 1, 0, -1, -2, -3, -3, -3, -3, -3, -3};
    int[] dist4 = new int[] {-4, -3, -2, -1, 0, 1, 2, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 3, 2, 1, 0, -1, -2, -3, -4, -4, -4, -4, -4, -4, -4, -4};
    assertArrayEquals(dist1, MCTS_Tools.fillXTransformations(new int[8], 1));
    assertArrayEquals(dist2, MCTS_Tools.fillXTransformations(new int[16], 2));
    assertArrayEquals(dist3, MCTS_Tools.fillXTransformations(new int[24], 3));
    assertArrayEquals(dist4, MCTS_Tools.fillXTransformations(new int[32], 4));
  }
  
  @Test
  void testYTransformations() {
    int[] dist1 = new int[] {-1, -1, -1, 0, 1, 1, 1, 0};
    int[] dist2 = new int[] {-2, -2, -2, -2, -2, -1, 0, 1, 2, 2, 2, 2, 2, 1, 0, -1};
    int[] dist3 = new int[] {-3, -3, -3, -3, -3, -3, -3, -2, -1, 0, 1, 2, 3, 3, 3, 3, 3, 3, 3, 2, 1, 0, -1, -2};
    int[] dist4 = new int[] {-4, -4, -4, -4, -4, -4, -4, -4, -4, -3, -2, -1, 0, 1, 2, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 3, 2, 1, 0, -1, -2, -3};
    assertArrayEquals(dist1, MCTS_Tools.fillYTransformations(new int[8], 1));
    assertArrayEquals(dist2, MCTS_Tools.fillYTransformations(new int[16], 2));
    assertArrayEquals(dist3, MCTS_Tools.fillYTransformations(new int[24], 3));
    assertArrayEquals(dist4, MCTS_Tools.fillYTransformations(new int[32], 4));
  }
}
