package org.ctf.shared.client;

import org.ctf.shared.client.service.CommLayer;
import org.ctf.shared.client.service.CommLayerInterface;
import org.ctf.shared.client.service.RestClientLayer;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.constants.Constants.AI;
import org.ctf.shared.constants.Constants.Port;
import org.ctf.shared.state.data.map.MapTemplate;

/**
 * Defines the Step Builder Pattern which must be used to create an object of the AIClient Class
 *
 * @author rsyed
 */
public class AIClientStepBuilder {

  private AIClientStepBuilder() {}

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
    PlayerTypeSelectionStep onPort(Port def);
  }

  /**
   * Fourth and last customization. Selects what kind of player the client is going to take input
   * from
   */
  public static interface PlayerTypeSelectionStep {
    /**
     * Method which creates an instance of AIClient.java. Extension has build in support for AI
     * players
     *
     * @param num Exp: Constants.AI.MCTS, Constants.AI.MCTS.RANDOM, etc
     */
    LoggerEnabler AIPlayerSelector(Constants.AI num);
  }

  public static interface LoggerEnabler {
    /**
     * Method to enable if the AI Game will be logged players
     *
     * @param selector True for Enabling Save, False for disabled
     */
    CreatorOrJoinerStep enableSaveGame(boolean selector);
  }

  public static interface CreatorOrJoinerStep {
    /**
     * Method to enable if the AI Game will be logged players
     *
     * @param selector True for Enabling Save, False for disabled
     */
    BuildStep createGameMode(MapTemplate mapTemplate, String teamName);

    BuildStep joinerGameMode(String GameID, String teamName);
  }

  /** Build Step */
  public static interface BuildStep {

    public AIClient build();
  }

  /** Builder class itself where code gets implemented and the object creation happens */
  private static class Steps
      implements LayerSelectionStep,
          HostStep,
          PortSelectionStep,
          PlayerTypeSelectionStep,
          LoggerEnabler,
          CreatorOrJoinerStep,
          BuildStep {
    private CommLayerInterface comm;
    private String host;
    private String port;
    private AI ai;
    private boolean enableSave;
    private MapTemplate mapTemplate;
    private String teamName;
    private String gameSessionGiven;
    private boolean joinerMode;
    private boolean creatorMode;

    /**
     * Sets the underlying layer in use by the Client
     *
     * @param enableRestClient true to enable restClient based layer, false to use Java based one
     */
    @Override
    public HostStep enableRestLayer(boolean enableRestClient) {
      if (enableRestClient) {
        this.comm = new RestClientLayer();
      } else {
        this.comm = new CommLayer();
      }
      return this;
    }

    /** Auto set IP address as LocalHost */
    @Override
    public PortSelectionStep onLocalHost() {
      this.host = "localhost";
      return this;
    }

    /**
     * Step for port input as String
     *
     * @param port to connect to as String. Example "8888"
     */
    @Override
    public PlayerTypeSelectionStep onPort(String port) {
      this.port = port;
      return this;
    }

    /**
     * Step for AI Selection for the layer
     *
     * @param ai the AI enum to specify the AI the Client is going to use. Example AI.MCTS
     */
    @Override
    public LoggerEnabler AIPlayerSelector(AI ai) {
      this.ai = ai;
      return this;
    }

    /**
     * Step for remote host input
     *
     * @param host the remote IP address as "192.xxx.xxx.xxx"
     */
    @Override
    public PortSelectionStep onRemoteHost(String host) {
      this.host = host;
      return this;
    }

    /**
     * Port.DEFAULT for 8888
     *
     * @param def an Enum
     */
    @Override
    public PlayerTypeSelectionStep onPort(Port def) {
      this.port = def.toString();
      return this;
    }

    /**
     * Option Presented if AI Client is selected. Enables the game to be saved as a SavedGame
     *
     * @param selector True for Enabling Save, False for Disabled
     */
    @Override
    public CreatorOrJoinerStep enableSaveGame(boolean selector) {
      this.enableSave = selector;
      return this;
    }

    @Override
    public BuildStep createGameMode(MapTemplate mapTemplate, String teamName) {
      this.mapTemplate = mapTemplate;
      this.teamName = teamName;
      this.creatorMode = true;
      this.joinerMode = false;
      return this;
    }

    @Override
    public BuildStep joinerGameMode(String GameID, String teamName) {
      this.gameSessionGiven = GameID;
      this.teamName = teamName;
      this.creatorMode = false;
      this.joinerMode = true;
      return this;
    }

    @Override
    public AIClient build() {
      AIClient client;
      if (creatorMode) {
        client = new AIClient(comm, host, port, enableSave, ai, mapTemplate, teamName);
      } else { // (joinerMode) 
        client = new AIClient(comm, host, port, enableSave, ai, gameSessionGiven, teamName);
      } 
      return client;
    }
  }
}