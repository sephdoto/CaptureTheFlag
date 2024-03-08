package org.ctf.ai;


import org.ctf.client.state.Piece;
import org.ctf.client.state.Move;
import org.ctf.client.state.Team;
import org.ctf.client.state.data.map.MapTemplate;
import org.ctf.client.tools.JSON_Tools;

import java.io.File;
import java.io.IOException;

import org.ctf.client.constants.Constants;
import org.ctf.client.state.GameState;

public class AI_Controller {

	//used for testing
	public static void main(String[] args) {
		MapTemplate mt = null;
		try {
			mt = JSON_Tools.readMapTemplate(new File(Constants.mapTemplateFolder+"test.json"));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		Team team1 = new Team();
		team1.setBase(new int[] {2,2});			//
		team1.setColor("red");
		team1.setFlag(new int[] {2,2});			//
		team1.setId("team1");
		team1.setPieces(null);
		Team team2 = new Team();
		team2.setBase(new int[] {1,1});			//
		team2.setColor("blue");
		team2.setFlag(new int[] {1,1});			//
		team2.setId("team2");
		team2.setPieces(null);
		
		Piece piece = new Piece();
		piece.setDescription(mt.getPieces()[0]);
		piece.setId("pawnTeam1");
		piece.setPosition(new int[] {0,0});
		piece.setTeamId("team1");
		
		Move lastMove = new Move();
		lastMove.setNewPosition(null);
		lastMove.setPieceId(null);
		
		GameState testState = new GameState();
		testState.setCurrentTeam(1);
		String[][] example = new String[][] {
			{"","","","","","","","","",""},
			{"","","","","","","","","",""},
			{"","","","","","","","","",""},
			{"","","","","","","","","",""},
			{"","","","","","","","","",""},
			{"","","","","","","","","",""},
			{"","","","","","","","","",""},
			{"","","","","","","","","",""},
			{"","","","","","","","","",""},
			{"","","","","","","","","",""}
			};
		testState.setGrid(example);
		testState.setLastMove(lastMove);
		testState.setTeams(new Team[]{team1, team2});
		
		System.out.println(mt.getPieces()[0].getType());
		
		mt.setGridSize(null);
		try {
			JSON_Tools.saveMapTemplateAsFile("test", mt);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RandomAI.pickMove("");
	}
	
}
