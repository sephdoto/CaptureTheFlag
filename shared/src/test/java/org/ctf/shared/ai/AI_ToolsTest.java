package org.ctf.shared.ai;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import org.ctf.shared.ai.AI_Tools;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Piece;
import org.ctf.shared.state.Team;
import org.ctf.shared.state.data.map.MapTemplate;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * This class is used for testing Methods which are not used in the ctf-ai module.
 * Those methods were implemented for different use cases than the RandomAI.
 * @author sistumpf
 */
class AI_ToolsTest {
  

  @Test
  void testToNextTeam() {
    GameState gameState = new GameState();
    gameState.setCurrentTeam(0);
    Team[] teams = new Team[5];
    for(int i=0; i<5; i++)
      teams[i] = null;
    teams[3] = new Team();
    gameState.setTeams(teams);
    
    AI_Tools.toNextTeam(gameState);

    assertEquals(gameState.getTeams()[gameState.getCurrentTeam()].getClass(), Team.class);
    gameState.getTeams()[3] = null;
    gameState.getTeams()[1] = new Team();
    
    AI_Tools.toNextTeam(gameState);

    assertEquals(gameState.getTeams()[gameState.getCurrentTeam()].getClass(), Team.class);
  }

  @Test
  void testGetPossibleMoves() {
    GameState gameState = getTestState();
    String pieceId = gameState.getTeams()[1].getPieces()[1].getId();
    ArrayList<int[]> moves = AI_Tools.getPossibleMoves(gameState, pieceId, new ArrayList<int[]>());

    ArrayList<int[]> actuallyValidMoves = new ArrayList<int[]>();
    actuallyValidMoves.add(new int[] {6,3});
    actuallyValidMoves.add(new int[] {9,3});
    actuallyValidMoves.add(new int[] {8,3});
    
    assertArrayEquals(moves.toArray(), actuallyValidMoves.toArray());
    
    gameState.getGrid()[6][3] = "b";
    gameState.getGrid()[8][3] = "b";
    moves = AI_Tools.getPossibleMoves(gameState, pieceId, new ArrayList<int[]>());
    actuallyValidMoves.clear();
    
    assertArrayEquals(moves.toArray(), actuallyValidMoves.toArray());
  }
  
  @Test
  void testRespawnPiecePosition() {
    GameState gameState = getTestState();
    int[] basePos = new int[] {2,4};
    gameState.getGrid()[0][0] = "";
    gameState.getGrid()[2][4] = "b:0";
    gameState.getGrid()[1][3] = "b";
    gameState.getGrid()[3][3] = "b";
    gameState.getGrid()[3][5] = "b";                                    //1 free field in direct contact to 2,4: 3,4
    int[] pos = AI_Tools.respawnPiecePosition(gameState, basePos);
    assertArrayEquals(new int[] {3,4}, pos);

    gameState.getGrid()[3][4] = "b";                                    //block last free field, distance from base must be +1
    pos = AI_Tools.respawnPiecePosition(gameState, basePos); 
    assertArrayEquals(new int[] {4,5}, pos);                            //randomly chosen field: 4,5. Should stay the same every time (with this gameState)
   
    gameState.getGrid()[0][0] = "b";                                    //alter gameState so the seeded random chooses another field, even though 4,4 is free to be occupied
    pos = AI_Tools.respawnPiecePosition(gameState, basePos);
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
          pos = AI_Tools.respawnPiecePosition(gameState, basePos);
          assertArrayEquals(new int[] {i,j}, pos);                       //goes through all the blocks, removes one, tests if the position would be respawned on, places the block back. repeats for all blocks.
          gameState.getGrid()[i][j] = "b"; 
        }
      }
    }
  }
  
  void printGrid(GameState state) {
    for(int i= 0; i<state.getGrid().length; i++) {
      for(int j=0; j<state.getGrid().length; j++) {
        String s = state.getGrid()[i][j];
        System.out.print(s.equals("") ? "x " : s + " ");
      }
      System.out.println();
    }
    System.out.println("\n\n");
  }

  @Test
  void testXTransformations() {
    int[] dist1 = new int[] {-1, 0, 1, 1, 1, 0, -1, -1};
    int[] dist2 = new int[] {-2, -1, 0, 1, 2, 2, 2, 2, 2, 1, 0, -1, -2, -2, -2, -2};
    int[] dist3 = new int[] {-3, -2, -1, 0, 1, 2, 3, 3, 3, 3, 3, 3, 3, 2, 1, 0, -1, -2, -3, -3, -3, -3, -3, -3};
    int[] dist4 = new int[] {-4, -3, -2, -1, 0, 1, 2, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 3, 2, 1, 0, -1, -2, -3, -4, -4, -4, -4, -4, -4, -4, -4};
    assertArrayEquals(dist1, AI_Tools.fillXTransformations(new int[8], 1));
    assertArrayEquals(dist2, AI_Tools.fillXTransformations(new int[16], 2));
    assertArrayEquals(dist3, AI_Tools.fillXTransformations(new int[24], 3));
    assertArrayEquals(dist4, AI_Tools.fillXTransformations(new int[32], 4));
  }
  
  @Test
  void testYTransformations() {
    int[] dist1 = new int[] {-1, -1, -1, 0, 1, 1, 1, 0};
    int[] dist2 = new int[] {-2, -2, -2, -2, -2, -1, 0, 1, 2, 2, 2, 2, 2, 1, 0, -1};
    int[] dist3 = new int[] {-3, -3, -3, -3, -3, -3, -3, -2, -1, 0, 1, 2, 3, 3, 3, 3, 3, 3, 3, 2, 1, 0, -1, -2};
    int[] dist4 = new int[] {-4, -4, -4, -4, -4, -4, -4, -4, -4, -3, -2, -1, 0, 1, 2, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 3, 2, 1, 0, -1, -2, -3};
    assertArrayEquals(dist1, AI_Tools.fillYTransformations(new int[8], 1));
    assertArrayEquals(dist2, AI_Tools.fillYTransformations(new int[16], 2));
    assertArrayEquals(dist3, AI_Tools.fillYTransformations(new int[24], 3));
    assertArrayEquals(dist4, AI_Tools.fillYTransformations(new int[32], 4));
  }

  /**
   * Creates a test GameState from the example Map.
   *
   * @return GameState
   */
  public static GameState getTestState() {
    MapTemplate mt = getTestTemplate();
    Team team1 = new Team();
    team1.setBase(new int[] {0, 0});
    team1.setColor("red");
    team1.setFlags(mt.getFlags());
    team1.setId("0");

    Team team2 = new Team();
    team2.setBase(new int[] {9, 9});
    team2.setColor("blue");
    team2.setFlags(mt.getFlags());
    team2.setId("1");

    Piece[] pieces1 = new Piece[8];
    for (int i = 0; i < 8; i++) {
      pieces1[i] = new Piece();
      pieces1[i].setDescription(mt.getPieces()[1]);
      pieces1[i].setId("p:0_" + (i + 1));
      if (i < 2) pieces1[i].setPosition(new int[] {1, 4 + i});
      else pieces1[i].setPosition(new int[] {2, i});
      pieces1[i].setTeamId(team1.getId());
    }
    team1.setPieces(pieces1);

    Piece[] pieces2 = new Piece[8];
    for (int i = 0; i < 8; i++) {
      pieces2[i] = new Piece();
      pieces2[i].setDescription(mt.getPieces()[1]);
      pieces2[i].setId("p:1_" + (i + 1));
      if (i < 6) pieces2[i].setPosition(new int[] {7, 2 + i});
      else pieces2[i].setPosition(new int[] {8, i - 2});
      pieces2[i].setTeamId(team1.getId());
    }
    team2.setPieces(pieces2);

    Move lastMove = new Move();
    lastMove.setNewPosition(null);
    lastMove.setPieceId(null);

    GameState testState = new GameState();
    testState.setCurrentTeam(1);
    String[][] example =
        new String[][] {
      {"b:0", "", "", "", "", "", "", "", "", ""},
      {"", "", "", "", pieces1[0].getId(), pieces1[1].getId(), "", "", "", ""},
      {
        "",
        "",
        pieces1[2].getId(),
        pieces1[3].getId(),
        pieces1[4].getId(),
        pieces1[5].getId(),
        pieces1[6].getId(),
        pieces1[7].getId(),
        "",
        ""
      },
      {"", "", "", "", "", "", "", "", "", ""},
      {"", "", "", "", "", "", "", "b", "", ""},
      {"", "", "", "b", "", "", "", "", "", ""},
      {"", "", "", "", "", "", "", "", "", ""},
      {
        "",
        "",
        pieces2[0].getId(),
        pieces2[1].getId(),
        pieces2[2].getId(),
        pieces2[3].getId(),
        pieces2[4].getId(),
        pieces2[5].getId(),
        "",
        ""
      },
      {"", "", "", "", pieces2[6].getId(), pieces2[7].getId(), "", "", "", ""},
      {"", "", "", "", "", "", "", "", "", "b:1"}
    };
    testState.setGrid(example);
    testState.setLastMove(lastMove);
    testState.setTeams(new Team[] {team1, team2});

    return testState;
  }  
  /**
   * Returns the test MapTemplate.
   *
   * @return MapTemplate
   */
  static MapTemplate getTestTemplate() {
    String mapString = "{\"gridSize\":[10,10],\"teams\":2,\"flags\":1,\"pieces\":[{\"type\":\"Pawn\",\"attackPower\":1,\"count\":10,\"movement\":{\"directions\":{\"left\":0,\"right\":0,\"up\":1,\"down\":0,\"upLeft\":1,\"upRight\":1,\"downLeft\":0,\"downRight\":0}}},{\"type\":\"Rook\",\"attackPower\":5,\"count\":2,\"movement\":{\"directions\":{\"left\":2,\"right\":2,\"up\":2,\"down\":2,\"upLeft\":0,\"upRight\":0,\"downLeft\":0,\"downRight\":0}}},{\"type\":\"Knight\",\"attackPower\":3,\"count\":2,\"movement\":{\"shape\":{\"type\":\"lshape\"}}},{\"type\":\"Bishop\",\"attackPower\":3,\"count\":2,\"movement\":{\"directions\":{\"left\":0,\"right\":0,\"up\":0,\"down\":0,\"upLeft\":2,\"upRight\":2,\"downLeft\":2,\"downRight\":2}}},{\"type\":\"Queen\",\"attackPower\":5,\"count\":1,\"movement\":{\"directions\":{\"left\":2,\"right\":2,\"up\":2,\"down\":2,\"upLeft\":2,\"upRight\":2,\"downLeft\":2,\"downRight\":2}}},{\"type\":\"King\",\"attackPower\":1,\"count\":1,\"movement\":{\"directions\":{\"left\":1,\"right\":1,\"up\":1,\"down\":1,\"upLeft\":1,\"upRight\":1,\"downLeft\":1,\"downRight\":1}}}],\"blocks\":0,\"placement\":\"symmetrical\",\"totalTimeLimitInSeconds\":-1,\"moveTimeLimitInSeconds\":-1}\r\n";
    Gson gson = new Gson();
    new TypeToken<>() {}.getType(); 
    return gson.fromJson(mapString, MapTemplate.class);
  }
}
