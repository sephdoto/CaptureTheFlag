package org.ctf.client;

import org.ctf.client.lib.ServerDetails;
import org.ctf.client.service.CommLayer;
import org.ctf.client.service.CommLayerInterface;
import org.ctf.client.service.RestClientLayer;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Constants.AI;
import org.ctf.shared.constants.Constants.Port;

/**
 * Defines the Step Builder Pattern which must be used to create an object of the Client Class
 *
 * @author rsyed
 */
public class ClientStepBuilder {

  private ClientStepBuilder() {}

  /** Starter method for the builder */
  public static LayerSelectionStep newBuilder() {
    return new Steps();
  }

  /** Defining the First Step RestClient Selector */
  public static interface LayerSelectionStep {
    /**
     * @param enableRestClient enables on true, disables on false
     */
    HostStep enableRestLayer(boolean enableRestClient);
  }

  /** Second Step in charge of Server... Two options in this step. Either Local or Remote host. */
  public static interface HostStep {
    /** This method sets the client to use LocalHost as IP */
    PortSelectionStep onLocalHost();

    /**
     * This method sets a remote host IP
     *
     * @param String host IP in format "xxx.xxx.xxx.xxx" as String
     */
    PortSelectionStep onRemoteHost(String host);
  }

  /**
   * Third Step in the builder: Port selector. Either you can pass a String for custom port or a
   * Constants.Port ENUM for a predefined port
   */
  public static interface PortSelectionStep {
    /**
     * Method for a custom port as a String xxxx
     *
     * @param port example String "9999" etc
     */
    PlayerTypeSelectionStep onPort(String port);

    /**
     * Method for setting the port using a Constant ENUM from Constants.Port
     *
     * @param port example String "9999" etc
     */
    PlayerTypeSelectionStep onPort(Constants.Port DEFAULTPORT);
  }

  /**
   * Fourth and last customization. Selects what kind of player the client is going to take input
   * from
   */
  public static interface PlayerTypeSelectionStep {
    /** Method which sets Human player. Creates an instance of Client.java */
    BuildStep HumanPlayer();

    /**
     * Method which creates an instance of AIClient.java. Extension has build in support for AI
     * players
     *
     * @param num Exp: Constants.AI.MCTS, Constants.AI.MCTS.RANDOM, etc
     */
    BuildStep AIPlayerSelector(Constants.AI num);
  }

  /** Build Step */
  public static interface BuildStep {

    public Client build();
  }

  /** Builder class itself where code gets implemented and the object creation happens */
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
