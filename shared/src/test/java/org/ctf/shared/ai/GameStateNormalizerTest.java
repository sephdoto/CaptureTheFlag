package org.ctf.shared.ai;

import static org.junit.jupiter.api.Assertions.*;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.junit.jupiter.api.Test;

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

  @Test
  void testNormalize() {
    //create unnormalized GameState
    GameState gs = TestValues.getTestState();
    for(int i=0; i<gs.getTeams().length; i++) {
      gs.getTeams()[i].setId(funIds[i]);
      for(int j=0; j<gs.getTeams()[i].getPieces().length; j++) {
        gs.getTeams()[i].getPieces()[j].setId("p:"+ "" + "_" + j+Math.random() * 80085);
      }
    }
    
    GameStateNormalizer gsn = new GameStateNormalizer(gs, true);
    for(int i=0; i<gs.getTeams().length; i++) {
      for(int j=0; j<gs.getTeams()[i].getPieces().length; j++) {
        assertTrue(! gsn.getNormalizedGameState().getTeams()[i].getPieces()[i].getId()
            .equals(gsn.getOriginalGameState().getTeams()[i].getPieces()[j].getId()));
      }
    }
  }

}
