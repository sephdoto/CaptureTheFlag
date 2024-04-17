package org.ctf.shared.client;

import java.util.ArrayList;
import org.ctf.shared.ai.AI_Controller;
import org.ctf.shared.client.service.CommLayerInterface;
import org.ctf.shared.constants.Constants.AI;
import org.ctf.shared.state.Move;

public class AIClient extends Client implements Runnable {

  public AI selectedPlayer;
  public ArrayList<Move> moves;

  AIClient(CommLayerInterface comm, String IP, String port) {
    super(comm, IP, port);
    //moves = new ArrayList<Move>();
  }

  AIClient(CommLayerInterface comm, String IP, String port, AI selected) {
    this(comm, IP, port);
    this.selectedPlayer = selected;

  }

  @Override
  public void run() {
    try {
      AI_Controller controller = new AI_Controller(getCurrentState(), this.selectedPlayer);
      // checks if game has a start date and no end date
      while ((this.getEndDate() == null) && (this.getStartDate() != null)) {
        this.getSessionFromServer();
        this.getStateFromServer();
        if(turnSupportFlag){
          if(isItMyTurn()){
            this.makeMove(controller.getNextMove());
          }
        } else {
          //TODO Code for ALT Turn making support if server is badly written
        }
        
        this.getSessionFromServer();
        controller.update(getCurrentState());
        Thread.sleep(800);
      }  
    } catch (Exception e) {

    }
  }
}
