package test;

import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Team;

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

public static GameState createTestGameState() {
	GameState gameState = new GameState();
	Team[] teams = new Team[2];
	teams[0] = CreateTestTeam.createTestTeam1("blue");
	teams[1] = CreateTestTeam.createTestTeam2("red");
	gameState.setTeams(teams);
	gameState.setGrid(exm3);
	return gameState;
}
}
