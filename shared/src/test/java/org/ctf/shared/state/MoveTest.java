package org.ctf.shared.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class MoveTest {
  static Move testMove = new Move();
  static Move testMove2 = new Move();
  static Move testMove3 = new Move();
  static Move testMove4 = new Move();
  static Move testMove5 = new Move();
  static Move testMove6 = new Move();
  static Move testMove7 = new Move();

  @BeforeAll
  static void BeforeAll() {
    testMove.setTeamId("Team1");
    testMove.setPieceId("piece1");
    testMove.setNewPosition(new int[] {0, 1});
    testMove2.setTeamId("Team1");
    testMove2.setPieceId("piece1");
    testMove2.setNewPosition(new int[] {0, 1});
    testMove3.setTeamId("Team1");
    testMove3.setPieceId("piece1");
    testMove3.setNewPosition(new int[] {0, 4});
    testMove4.setTeamId("Team2");
    testMove4.setPieceId("piece1");
    testMove4.setNewPosition(new int[] {0, 4});
    testMove5.setTeamId("Team2");
    testMove5.setPieceId("piece2");
    testMove5.setNewPosition(new int[] {0, 4});
    testMove6.setTeamId(null);
    testMove6.setPieceId("piece2");
    testMove6.setNewPosition(new int[] {0, 4});
    testMove7.setTeamId(null);
    testMove7.setPieceId(null);
    testMove7.setNewPosition(new int[] {0, 4});
  }

  @Test
  void testEquals() {
    assertNotEquals(testMove3, testMove4);
    assertEquals(testMove, testMove2);
    assertNotEquals(testMove2, testMove3);
    int testOtherClass = 5;
    assertNotEquals(testMove3, testOtherClass);
    assertNotEquals(testMove3, null);
    assertNotEquals(testMove3, testMove4);
    assertEquals(testMove, testMove);
    assertNotEquals(testMove6, testMove5);
    assertNotEquals(testMove6, testMove7);
  }

  @Test
  void testHashCode() {}
}
