package test;

import de.unimannheim.swt.pse.ctf.game.map.PieceDescription;
import de.unimannheim.swt.pse.ctf.game.state.Piece;

public class TestPieceCreator {
	public static Piece createTestPice() {
		Piece piece = new Piece();
		piece.setId("WarriorV1"); //Frage für Aaron: WIe werden PieceIds in echt erstellt
		piece.setTeamId("1"); //Frage an alle: Wie sehen TeamIds in echt aus
		PieceDescription pieceDescription = new PieceDescription();
		pieceDescription.setAttackPower(7);
		pieceDescription.setCount(5); //number of this pieces of this type team owns
		pieceDescription.setType("WarriorV1");
		piece.setDescription(pieceDescription);
		return piece;
	}
}
