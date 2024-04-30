package org.ctf.shared.client;

import org.ctf.shared.client.service.CommLayer;
import org.ctf.shared.client.service.CommLayerInterface;
import org.ctf.shared.client.service.RestClientLayer;
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
    LoggerEnabler onPort(String port);

    /**
     * Method for setting the port using a Constant ENUM from Constants.Port
     *
     * @param port example String "9999" etc
     */
    LoggerEnabler onPort(Port def);
  }

  public static interface LoggerEnabler {
    /**
     * Method to enable if the AI Game will be logged players
     *
     * @param selector True for Enabling Save, False for disabled
     */
    BuildStep enableSaveGame(boolean selector);
  }

  /** Build Step */
  public static interface BuildStep {

    public Client build();
  }

  /** Builder class itself where code gets implemented and the object creation happens */
  private static class Steps
      implements LayerSelectionStep, HostStep, PortSelectionStep, LoggerEnabler, BuildStep {
    private CommLayerInterface comm;
    private String host;
    private String port;
    private boolean enableSave;

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
     * @return
     */
    @Override
    public LoggerEnabler onPort(String port) {
      this.port = port;
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
     * @return
     */
    @Override
    public LoggerEnabler onPort(Port def) {
      this.port = def.toString();
      return this;
    }

    /**
     * Option Presented if AI Client is selected. Enables the game to be saved as a SavedGame
     *
     * @param selector True for Enabling Save, False for Disabled
     * @return
     */
    @Override
    public Steps enableSaveGame(boolean selector) {
      this.enableSave = selector;
      return this;
    }

    @Override
    public Client build() {
      return new Client(comm, host, port, enableSave);
    }
  }
}
