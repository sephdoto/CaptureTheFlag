package org.ctf.client;

import org.ctf.client.lib.ServerDetails;
import org.ctf.client.service.CommLayer;
import org.ctf.client.service.CommLayerInterface;
import org.ctf.client.service.RestClientLayer;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Constants.AI;
import org.ctf.shared.constants.Constants.Port;

public class ClientStepBuilder {

  private ClientStepBuilder() {}

  /*
   * First Step to init the Step builder
   */
  public static LayerSelectionStep newBuilder() { // FirstStep (Starting Step)
    return new Steps();
  }

  /*
   * Defining the First Step
   * Uses a Boolean to enable RestClient
   * After this is the Host Selector
   */
  public static interface LayerSelectionStep { // p st int 1stStep
    HostStep enableRestLayer(boolean enableRestClient); // 2nd step 1stStepMethod
  }

  /*
   * Third Step in charge of Server...
   * detects if local host
   * if present goes to port selection.
   */
  public static interface HostStep { // p st int 2nd step
    PortSelectionStep onLocalHost(); // 3rdStep 2ndStepMethod

    PortSelectionStep onRemoteHost(String host);
  }

  /** */
  public static interface PortSelectionStep { // p st int 3rd step
    PlayerTypeSelectionStep onPort(String breadType); // 4thStep 3rdStepMethod

    PlayerTypeSelectionStep onPort(Constants.Port DEFAULTPORT); // 4thStep 3rdStepMethod
  }

  /** */
  public static interface PlayerTypeSelectionStep { // p st int 4th Step
    BuildStep HumanPlayer();

    BuildStep AIPlayerSelector(Constants.AI num); // LastStep 4thStep Method
  }

  public static interface BuildStep {

    public Client build();
  }

  private static class Steps
      implements LayerSelectionStep,
          HostStep,
          PortSelectionStep,
          PlayerTypeSelectionStep,
          BuildStep {
    private CommLayerInterface comm;
    private String host;
    private String port;
    private Constants.AI playerType;

    @Override
    public HostStep enableRestLayer(boolean enableRestClient) {
      if (enableRestClient) {
        this.comm = new RestClientLayer();
      } else {
        this.comm = new CommLayer();
      }
      return this;
    }

    @Override
    public PortSelectionStep onLocalHost() {
      this.host = "localhost";
      return this;
    }

    @Override
    public PlayerTypeSelectionStep onPort(String port) {
      this.port = port;
      return this;
    }

    @Override
    public BuildStep HumanPlayer() {
      this.playerType = Constants.AI.HUMAN;
      return this;
    }

    @Override
    public BuildStep AIPlayerSelector(AI playerType) {
      this.playerType = playerType;
      return this;
    }

    @Override
    public PortSelectionStep onRemoteHost(String host) {
      this.host = host;
      return this;
    }

    @Override
    public PlayerTypeSelectionStep onPort(Port DEFAULTPORT) {
      this.port = DEFAULTPORT.name();
      return this;
    }

    @Override
    public Client build() {
      ServerDetails serverDetails = new ServerDetails(host, port);
      if (!serverDetails.isLocalhost()) {
        serverDetails.setHost(host);
      }
      serverDetails.setPort(port);
      Client client;
      if (!playerType.equals(Constants.AI.HUMAN)) {
        client = new AIClient(comm, host, port);
        client.setPlayerType(playerType);
      } else {
        client = new Client(comm, host, port);
      }
      return client;
    }

  
  }
}
