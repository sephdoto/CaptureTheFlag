package org.ctf.ai;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.ctf.client.state.data.map.MapTemplate;
import org.ctf.client.state.GameState;
import org.ctf.client.state.Move;
import org.ctf.client.state.Piece;
import org.ctf.client.state.Team;
import org.ctf.client.tools.JSON_Tools;
import org.ctf.client.tools.JSON_Tools.MapNotFoundException;


class RandomAITest {
  static GameState gameState;

  @BeforeEach
  void setUp() throws Exception{
    gameState = getTestState();
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
    fail("Not yet implemented");
  }

  @Test
  void testGetReach() {
    fail("Not yet implemented");
  }
  
  
  /**
   * Creates a test GameState from the example Map. 
   * @return GameState
   */
  @SuppressWarnings("deprecation")
  private GameState getTestState() {
    MapTemplate mt = new MapTemplate();
    try {
      mt = JSON_Tools.readMapTemplate("10x10_2teams_example");
    } catch (MapNotFoundException e) {e.printStackTrace();}
    Team team1 = new Team();
    team1.setBase(new int[] {0,0});
    team1.setColor("red");
    team1.setFlag(new int[] {0,0});
    team1.setId("team0");
    
    Team team2 = new Team();
    team2.setBase(new int[] {9,9});
    team2.setColor("blue");
    team2.setFlag(new int[] {9,9});
    team2.setId("team1");
    
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
        {"","","","","","","","","",""},
        {"","","","",pieces1[0].getId(),pieces1[1].getId(),"","","",""},
        {"","",pieces1[2].getId(),pieces1[3].getId(),pieces1[4].getId(),pieces1[5].getId(),pieces1[6].getId(),pieces1[7].getId(),"",""},
        {"","","","","","","","","",""},
        {"","","","","","","","b","",""},
        {"","","","b","","","","","",""},
        {"","","","","","","","","",""},
        {"","",pieces2[0].getId(),pieces2[1].getId(),pieces2[2].getId(),pieces2[3].getId(),pieces2[4].getId(),pieces2[5].getId(),"",""},
        {"","","","",pieces2[6].getId(),pieces2[7].getId(),"","","",""},
        {"","","","","","","","","",""}
        };
    testState.setGrid(example);
    testState.setLastMove(lastMove);
    testState.setTeams(new Team[]{team1, team2});
    
    return testState;
}
}