package org.ctf.ai;

import org.ctf.client.tools.JSON_Tools;
import org.ctf.client.tools.JSON_Tools.MapNotFoundException;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.Piece;
import org.ctf.shared.state.Team;
import org.ctf.shared.state.data.map.MapTemplate;


/**
 * This class returns test GameStates and MapTemplates.
 * @author sistumpf
 */
public class TestValues {
	/**
	 * Creates a test GameState from the example Map. 
	 * @return GameState
	 */
	public static GameState getEmptyTestState() {
		Team team1 = new Team();
		team1.setBase(new int[] {0,0});
		team1.setColor("red");
		team1.setId("0");

		Team team2 = new Team();
		team2.setBase(new int[] {9,9});
		team2.setColor("blue");
		team2.setId("1");

		Piece[] pieces1 = new Piece[0];
		team1.setPieces(pieces1);

		Piece[] pieces2 = new Piece[0];
		team2.setPieces(pieces2);

		Move lastMove = new Move();
		lastMove.setNewPosition(null);
		lastMove.setPieceId(null);

		GameState testState = new GameState();
		testState.setCurrentTeam(1);
		String[][] example = new String[][] {
			{"b:0","","","","","","","","",""},
			{"","","","","","","","","",""},
			{"","","","","","","","","",""},
			{"","","","","","","","","",""},
			{"","","","","","","","","",""},
			{"","","","","","","","","",""},
			{"","","","","","","","","",""},
			{"","","","","","","","","",""},
			{"","","","","","","","","",""},
			{"","","","","","","","","","b:1"}
		};
		testState.setGrid(example);
		testState.setLastMove(lastMove);
		testState.setTeams(new Team[]{team1, team2});

		return testState;
	}
	
	/**
	 * Creates a test GameState from the example Map. 
	 * @return GameState
	 */
	public static GameState getTestState() {
		MapTemplate mt = getTestTemplate();
		Team team1 = new Team();
		team1.setBase(new int[] {0,0});
		team1.setColor("red");
		team1.setFlags(mt.getFlags());
		team1.setId("0");

		Team team2 = new Team();
		team2.setBase(new int[] {9,9});
		team2.setColor("blue");
		team2.setFlags(mt.getFlags());
		team2.setId("1");

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
			{"b:0","","","","","","","","",""},
			{"","","","",pieces1[0].getId(),pieces1[1].getId(),"","","",""},
			{"","",pieces1[2].getId(),pieces1[3].getId(),pieces1[4].getId(),pieces1[5].getId(),pieces1[6].getId(),pieces1[7].getId(),"",""},
			{"","","","","","","","","",""},
			{"","","","","","","","b","",""},
			{"","","","b","","","","","",""},
			{"","","","","","","","","",""},
			{"","",pieces2[0].getId(),pieces2[1].getId(),pieces2[2].getId(),pieces2[3].getId(),pieces2[4].getId(),pieces2[5].getId(),"",""},
			{"","","","",pieces2[6].getId(),pieces2[7].getId(),"","","",""},
			{"","","","","","","","","","b:1"}
		};
		testState.setGrid(example);
		testState.setLastMove(lastMove);
		testState.setTeams(new Team[]{team1, team2});

		return testState;
	}

	/**
	 * Returns the test MapTemplate from the resource folder. 
	 * @return MapTemplate
	 */
	@SuppressWarnings("deprecation")
	public static MapTemplate getTestTemplate() {
		MapTemplate mt = new MapTemplate();
		try {
			mt = JSON_Tools.readMapTemplate("10x10_2teams_example");
		} catch (MapNotFoundException e) {e.printStackTrace();}
		return mt;
	}
}
