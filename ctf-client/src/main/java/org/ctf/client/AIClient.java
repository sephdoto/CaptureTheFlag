package org.ctf.client;

import java.util.ArrayList;

import org.ctf.client.service.CommLayerInterface;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.state.Move;

public class AIClient extends Client implements Runnable {

  Constants.AI selectedAI;
  ArrayList<Move> moves;

  AIClient(CommLayerInterface comm, String IP, String port) {
    super(comm, IP, port);
    moves = new ArrayList<Move>();
  }

  public void setSelectedAI(Constants.AI selectedAI) {
    this.selectedAI = selectedAI;
  }

  @Override
  public void run() {
    try {
      // checks if game has a start date and no end date
      while ((this.getEndDate() == null) && (this.getStartDate() != null)) {
        //Saves the last move locally
        Move lastMove = this.getLastMove();
       
        if( (this.getLastMove() != null) && (this.getLastMove() != lastMove)) {
          moves.add(getLastMove());
        }
        this.getSessionFromServer();
        this.getStateFromServer();
      }
      Thread.sleep(800);
    } catch (Exception e) {

    }

  }
}
