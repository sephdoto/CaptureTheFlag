package org.ctf.ai;


import org.ctf.client.state.Piece;
import org.ctf.client.state.Move;
import org.ctf.client.state.Team;
import org.ctf.client.state.data.map.MapTemplate;
import org.ctf.client.state.data.map.Shape;
import org.ctf.client.state.data.map.ShapeType;
import org.ctf.client.tools.JSON_Tools;

import java.io.File;

import org.ctf.ai.RandomAI.InvalidShapeException;
import org.ctf.ai.RandomAI.NoMovesLeftException;
import org.ctf.client.constants.Constants;
import org.ctf.client.state.GameState;

public class AI_Controller {

	//used for testing
	public static void main(String[] args) {
		MapTemplate mt = null;
		try {
			mt = JSON_Tools.readMapTemplate(new File(Constants.mapTemplateFolder+"10x10_2teams_example.json"));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		
		GameState testState = getTestState(mt);
		Move move = new Move();
		double time = System.nanoTime();
		try {
			move = RandomAI.pickMove(testState, true);
		} catch (NoMovesLeftException e) {e.printStackTrace();}
		catch (InvalidShapeException inse) {inse.printStackTrace();}
		System.out.println(((System.nanoTime() - time)/1000000) + " ms");
		System.out.println(move.getPieceId() + " " + move.getNewPosition()[0] + "." + move.getNewPosition()[1]);
	}
	
	public static GameState getTestState(MapTemplate mt) {
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
		
		/*for(Piece p : pieces2) {
			p.getDescription().getMovement().setDirections(null);
			p.getDescription().getMovement().setShape(new Shape());
			p.getDescription().getMovement().getShape().setType(ShapeType.lshape);
		}*/
		
		
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
