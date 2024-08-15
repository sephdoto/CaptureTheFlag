package test;

import org.ctf.shared.state.Piece;
import org.ctf.shared.state.data.map.Directions;
import org.ctf.shared.state.data.map.Movement;
import org.ctf.shared.state.data.map.PieceDescription;

public class TestPieceCreator {
	public static Piece createTestPice(String teamID, int[] position, String idString) {
		Piece piece = new Piece();
		piece.setPosition(position);
		piece.setId(idString); //Frage für Aaron: WIe werden PieceIds in echt erstellt
		piece.setTeamId(teamID); //Frage an alle: Wie sehen TeamIds in echt aus
		PieceDescription pieceDescription = new PieceDescription();
		pieceDescription.setAttackPower(7);
		pieceDescription.setCount(5); //number of this pieces of this type team owns
		pieceDescription.setType("WarriorV1");
		piece.setDescription(pieceDescription);
		Directions d = new Directions();
		d.setUp(5);
		d.setDown(5);
		d.setLeft(5);
		d.setRight(5);
		Movement m = new Movement();
		m.setDirections(d);
		pieceDescription.setMovement(m);
		return piece;
		
	}
}
