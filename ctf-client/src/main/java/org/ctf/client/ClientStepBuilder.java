package org.ctf.client;

import org.ctf.client.lib.ServerDetails;
import org.ctf.client.service.CommLayer;
import org.ctf.client.service.CommLayerInterface;
import org.ctf.client.service.RestClientLayer;
import org.ctf.shared.constants.Constants;

public class ClientStepBuilder {

  private ClientStepBuilder() {}

  /*
   * First Step to init the Step builder
   */
  public static LayerSelectionStep newBuilder() {
    return new Steps();
  }

  /*
   * Defining the First Step
   * Uses a Boolean to enable RestClient
   * After this is the Host Selector
   */
  public static interface LayerSelectionStep {
    HostStep enableRestLayer(boolean enableRestClient);
  }

  /*
   * Third Step in charge of Server...
   * detects if local host
   * if present goes to port selection.
   */
  public static interface HostStep {
    PortSelectionStep onHost(String host);
  }

  /** 
   * This step is in charge of the Port Selection Next Step available : Player Type Selection 
  */
  public static interface PortSelectionStep {
    PlayerTypeSelectionStep onPort(String breadType);
  }

  /** 
   * This step is in charge of the Port Selection Next Step available : MainFillingStep 
   * 
   */
  public static interface PlayerTypeSelectionStep{
     BuildStep playerSelector(Constants.AI num);
  }

  public static interface BuildStep {

    public Client build();
  }

  private static class Steps
      implements  LayerSelectionStep, HostStep, PortSelectionStep, PlayerTypeSelectionStep, BuildStep{
    private CommLayerInterface comm;
    private String host;
    private String port;
    private Constants.AI playerType;

    @Override
    public HostStep enableRestLayer(boolean enableRestClient) {
      if(enableRestClient){
        this.comm = new RestClientLayer();
      } else {
        this.comm = new CommLayer();
      }
      return this;
    }

    @Override
    public PortSelectionStep onHost(String host) {
     this.host = host;
     return this;
    }
   
    @Override
    public PlayerTypeSelectionStep onPort(String port) {
      this.port = port;
      return this;
     }
    

    @Override
    public BuildStep playerSelector(Constants.AI playerType) {
      this.playerType = playerType;
      return this;
    }

    @Override
    public Client build(){
      ServerDetails serverDetails = new ServerDetails(host,port);
      if (!serverDetails.isLocalhost()) {
        serverDetails.setHost(host);
        serverDetails.setPort(port);
     }
     Client client;
     if(!playerType.equals(Constants.AI.HUMAN)){
      client = new AIClient(comm, host, port); 
      client.setPlayerType(playerType);
     } else {
      client = new Client(comm, host, port);
     }
      return client;
    }
  }
}
