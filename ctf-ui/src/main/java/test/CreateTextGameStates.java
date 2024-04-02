package test;

import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Piece;
import org.ctf.shared.state.Team;
import org.ctf.shared.state.data.map.MapTemplate;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class CreateTextGameStates {

static String[][] exm3 = {
		  {"p:1_0", "p:1_1", "p:1_2", "p:1_3", "p:1_4", "p:1_5", "p:1_6", "p:1_7"},
		  {"", "", "", "", "", "", "", ""},
		  {"", "", "", "", "", "", "", ""},
		  {"", "", "", "b", "", "", "", ""},
		  {"", "", "b", "", "", "", "", ""},
		  {"", "", "", "", "b", "", "", ""},
		  {"", "", "", "", "", "", "", ""},
		  {"p:2_8", "p:2_9", "p:2_10", "p:2_11", "p:2_12", "p:2_13", "p:2_14", "p:2_15"}
		};
static String[][] exm2 = {
		  {"p:1_0", "p:1_1", "p:1_2", "p:1_3", "p:1_4", "p:1_5", "p:1_6", "p:1_7"},
		  {"", "", "", "", "", "", "", ""},
		  {"", "b", "", "", "", "", "", ""},
		  {"", "", "", "b", "", "b", "", ""},
		  {"", "", "b", "", "", "", "", ""},
		  {"", "", "", "", "b", "", "", ""},
		  {"", "", "", "", "", "", "", ""},
		  {"p:2_8", "p:2_9", "p:2_10", "p:2_11", "p:2_12", "p:2_13", "p:2_14", "p:2_15"}
		};
static String[][] exm1 = {
		  {"p:1_0", "p:1_1", "p:1_2", "p:1_3", "p:1_4", "p:1_5", "p:1_6", "p:1_7"},
		  {"", "", "", "", "", "", "", ""},
		  {"", "", "b", "", "", "b", "", ""},
		  {"", "b", "", "b", "", "", "", ""},
		  {"", "", "b", "", "", "", "", ""},
		  {"", "", "", "", "b", "", "", ""},
		  {"", "", "", "", "", "", "", ""},
		  {"p:2_8", "p:2_9", "p:2_10", "p:2_11", "p:2_12", "p:2_13", "p:2_14", "p:2_15"}
		};


public static GameState createTestGameState1() {
	GameState gameState = new GameState();
	Team[] teams = new Team[2];
	teams[0] = CreateTestTeam.createTestTeam1("blue");
	teams[1] = CreateTestTeam.createTestTeam2("red");
	gameState.setTeams(teams);
	gameState.setGrid(exm3);
	return gameState;
}
public static GameState createTestGameState2() {
	GameState gameState2 = new GameState();
	Team[] teams = new Team[2];
	teams[0] = CreateTestTeam.createTestTeam1("blue");
	teams[1] = CreateTestTeam.createTestTeam2("red");
	gameState2.setTeams(teams);
	gameState2.setGrid(exm2);
	return gameState2;
}
public static GameState createTestGameState3() {
	GameState gameState3 = new GameState();
	Team[] teams = new Team[2];
	teams[0] = CreateTestTeam.createTestTeam1("blue");
	teams[1] = CreateTestTeam.createTestTeam2("red");
	gameState3.setTeams(teams);
	gameState3.setGrid(exm1);
	return gameState3;
}
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
      pieces2[i].setTeamId(team2.getId());
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
    String mapString = "{\"gridSize\":[10,10],\"teams\":2,\"flags\":1,\"pieces\":[{\"type\":\"Pawn\",\"attackPower\":1,\"count\":10,\"movement\":{\"directions\":{\"left\":0,\"right\":0,\"up\":1,\"down\":0,\"upLeft\":1,\"upRight\":1,\"downLeft\":0,\"downRight\":0}}},{\"type\":\"WarriorV1\",\"attackPower\":5,\"count\":2,\"movement\":{\"directions\":{\"left\":2,\"right\":2,\"up\":2,\"down\":2,\"upLeft\":0,\"upRight\":0,\"downLeft\":0,\"downRight\":0}}},{\"type\":\"Knight\",\"attackPower\":3,\"count\":2,\"movement\":{\"shape\":{\"type\":\"lshape\"}}},{\"type\":\"Bishop\",\"attackPower\":3,\"count\":2,\"movement\":{\"directions\":{\"left\":0,\"right\":0,\"up\":0,\"down\":0,\"upLeft\":2,\"upRight\":2,\"downLeft\":2,\"downRight\":2}}},{\"type\":\"Queen\",\"attackPower\":5,\"count\":1,\"movement\":{\"directions\":{\"left\":2,\"right\":2,\"up\":2,\"down\":2,\"upLeft\":2,\"upRight\":2,\"downLeft\":2,\"downRight\":2}}},{\"type\":\"King\",\"attackPower\":1,\"count\":1,\"movement\":{\"directions\":{\"left\":1,\"right\":1,\"up\":1,\"down\":1,\"upLeft\":1,\"upRight\":1,\"downLeft\":1,\"downRight\":1}}}],\"blocks\":0,\"placement\":\"symmetrical\",\"totalTimeLimitInSeconds\":-1,\"moveTimeLimitInSeconds\":-1}\r\n";
    Gson gson = new Gson();
    new TypeToken<>() {}.getType(); 
    return gson.fromJson(mapString, MapTemplate.class);
  }

}
