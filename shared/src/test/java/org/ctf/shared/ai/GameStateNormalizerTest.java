package org.ctf.shared.ai;

import static org.junit.jupiter.api.Assertions.*;
import org.ctf.shared.ai.GameUtilities.InvalidShapeException;
import org.ctf.shared.ai.GameUtilities.NoMovesLeftException;
import org.ctf.shared.ai.random.RandomAI;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.junit.jupiter.api.Test;

/**
 * GameState normalizer normalizes GameStates in its Constructor and unnormalizes normalized moves with a method.
 * 
 * @author sistumpf
 */
class GameStateNormalizerTest {
  String[] funIds = new String[] {
      "Never",
      "Gonna",
      "Give",
      "You",
      "Up",
      "Hehe",
      "Rickrolled"
  };

  /*@Test
  void testIDMap() {
    GameState gs = unnormalizedGameState();
    GameStateNormalizer gsn = new GameStateNormalizer(gs, true);
    for(int i=0; i<gsn.idMap.size(); i++)
      System.out.println(gsn.idMap.keySet().toArray()[i] + " : " + gsn.idMap.get(gsn.idMap.keySet().toArray()[i]));
  }*/
  
  @Test
  void testUpdate() {
    GameState gs = unnormalizedGameState();
    
    GameStateNormalizer gsn = new GameStateNormalizer(gs, true);
    Move move = new Move();
    move.setNewPosition(new int[] {0,0});
    move.setPieceId(gs.getTeams()[0].getPieces()[0].getId());
    move.setTeamId("0");
    gs.setLastMove(move);
    String oldID = gs.getLastMove().getPieceId();
    gsn.update(gs);
    String newID = gsn.getNormalizedGameState().getLastMove().getPieceId();
    assertNotEquals(oldID, newID);
  }
  
  @Test
  void testNormalize() {
    //create unnormalized GameState
    GameState gs = unnormalizedGameState();
    
    GameStateNormalizer gsn = new GameStateNormalizer(gs, true);
    for(int i=0; i<gs.getTeams().length; i++) {
      for(int j=0; j<gs.getTeams()[i].getPieces().length; j++) {
        assertFalse(gsn.getNormalizedGameState().getTeams()[i].getPieces()[i].getId()
            .equals(gsn.getOriginalGameState().getTeams()[i].getPieces()[j].getId()));
      }
    }
  }
  
  @Test 
  void testUnNormalizedMove() throws NoMovesLeftException, InvalidShapeException {
    GameState gs = unnormalizedGameState();
    GameStateNormalizer gsn = new GameStateNormalizer(gs, true);
    
    Move move = RandomAI.pickMoveComplex(gsn.getNormalizedGameState(), new ReferenceMove(null, new int[2])).toMove();
    Move unmove = gsn.unnormalizeMove(move);
    assertFalse(move.getPieceId().equals(unmove.getPieceId()));
    assertArrayEquals(move.getNewPosition(), unmove.getNewPosition());
    assertEquals(
        unmove.getPieceId(),
        gsn.getOriginalGameState()
        .getTeams()[Integer.parseInt(move.getPieceId().split(":")[1].split("_")[0])]
            .getPieces()[Integer.parseInt(move.getPieceId().split("_")[1])]
                .getId()
                );
  }

  /**
   * creates and returns an unnormalized GameState
   * @return an unnormalized GameState
   */
  GameState unnormalizedGameState() {
  //create unnormalized GameState
    GameState gs = TestValues.getTestState();
    for(int i=0; i<gs.getTeams().length; i++) {
      gs.getTeams()[i].setId(funIds[i]);
      for(int j=0; j<gs.getTeams()[i].getPieces().length; j++) {
        gs.getTeams()[i].getPieces()[j].setId("p:"+ i + "_" + (int)(j+Math.random() * 80085));
      }
    }
    
    return gs;
  }
}
