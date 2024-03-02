package org.ctf.AI;
//TODO Fix Wildcard import...Google Code Bullshit doesnt allow wildcard impports
import de.unimannheim.swt.pse.ctf.game.state.*;

public class AI_Controller {

	//used for testing
	public static void main(String[] args) {
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
		piece.setDescription(null);
		piece.setId(null);
		piece.setPosition(null);
		piece.setTeamId(null);
		
		Move lastMove = new Move();
		lastMove.setNewPosition(null);
		lastMove.setPieceId(null);
		
		GameState testState = new GameState();
		testState.setCurrentTeam(1);
		String[][] example = new String[][] {{"","","","",""},{"","","","",""},{"","","","",""},{"","","","",""},{"","","","",""}};
				String x = "[\n" +
                "   [\"b:1\", \"\", \"\", \"\", \"\"],\n" +
                "   [\"\", \"p:1_1\", \"p:1_2\", \"p:1_3\", \"\"],\n" +
                "   [\"b\", \"\", \"\", \"\", \"b\"],\n" +
                "   [\"\", \"p:2_1\", \"p:2_2\", \"p:2_3\", \"\"],\n" +
                "   [\"\", \"\", \"\", \"\", \"b:2\"]\n" +
                "]";
		testState.setGrid(example);
		testState.setLastMove(lastMove);
		testState.setTeams(new Team[]{team1, team2});
		
		System.out.println(x);
		RandomAI.pickMove("");
	}
	
}
