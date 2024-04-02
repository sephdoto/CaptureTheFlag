package test;

import org.ctf.shared.state.Piece;
import org.ctf.shared.state.Team;

public class CreateTestTeam {
	
	
	public static Team createTestTeam1( String color) {
		Piece[] pieces = new Piece[8];
		Team team = new Team();
		team.setColor(color);
		for(int i=0;i<8;i++) {
			int[] pos = {0,i};
			Piece p = TestPieceCreator.createTestPice("1", pos,String.valueOf(i));
			pieces[i] = p;
		}
		team.setPieces(pieces);
		return team;
	}
	
	public static Team createTestTeam2( String color) {
		Piece[] pieces = new Piece[8];
		Team team = new Team();
		team.setColor(color);
		for(int i=0;i<8;i++) {
			int[] pos = {7,i};
			Piece p = TestPieceCreator.createTestPice("2", pos,String.valueOf(i+8));
			pieces[i] = p;
		}
		team.setPieces(pieces);
		return team;
	}
}
