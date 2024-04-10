package org.ctf.client;

import org.ctf.client.service.CommLayerInterface;
import org.ctf.shared.constants.Constants;

public class AIClient extends Client implements Runnable {

  Constants.AI selectedAI;


  AIClient(CommLayerInterface comm, String IP, String port) {
    super(comm, IP, port);
  }

  public void setSelectedAI(Constants.AI selectedAI){
    this.selectedAI = selectedAI;
  }
  
  @Override
  public void run() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'run'");
  }
}
