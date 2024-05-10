package de.unimannheim.swt.pse.ctf.game;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.ctf.shared.client.lib.SavedGame;
import org.ctf.shared.state.Move;
import org.junit.jupiter.api.Test;

public class SavedGameTest {

  @Test
  void testAddMove() {
    SavedGame game = new SavedGame();
    Move on = new Move();
    on.setTeamId("2");
    on.setPieceId("2_0");
    on.setNewPosition(new int[] {2, 3});
    game.addMove(on);
    game.addMove(on);
    game.addMove(on);
    Move one = new Move();
    one.setTeamId("2");
    one.setPieceId("2_0");
    one.setNewPosition(new int[] {2, 4});
    game.addMove(one);
    assertTrue(game.getMoves().size() == 2);
  }
}
