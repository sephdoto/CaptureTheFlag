package org.ctf.shared.client;

import org.ctf.shared.client.lib.ServerDetails;
import org.ctf.shared.client.service.CommLayerInterface;
import org.ctf.shared.state.data.map.MapTemplate;
import org.ctf.shared.state.dto.GameSessionRequest;
import org.ctf.shared.state.dto.GameSessionResponse;

/**
 * Abstraction which servers as a Manager for creating and deleting Sessions. Removes the need for
 * the Client to make sessions leading to simplifications in the UI flow.
 *
 * @author rsyed
 */
public class ServerManager {
  CommLayerInterface comm;
  public ServerDetails serverDetails;
  MapTemplate map;
  public String currentServer;
  public String gameSessionID;

  /**
   * Constructor
   *
   * @param comm An object of {@link CommLayerInterface}. Either CommLayer or RestClientLayer
   * @param serverInfo An object of {@link ServerDetails}. Contains URL and Port information
   * @param map An object of {@link MapTemplate}. Is the map it will use to create the Session.
   * @author rsyed
   */
  public ServerManager(CommLayerInterface comm, ServerDetails serverDetails, MapTemplate map) {
    this.comm = comm;
    this.map = map;
    this.serverDetails = serverDetails;
    this.currentServer =
        "http://" + serverDetails.getHost() + ":" + serverDetails.getPort() + "/api/gamesession";
        
  }

  /**
   * Creates a game session in the server set in the object.
   *
   * @return True on Success, False otherwise
   * @author rsyed
   */
  public boolean createGame() {
    GameSessionRequest gsr = new GameSessionRequest();
    gsr.setTemplate(map);
    new GameSessionRequest().setTemplate(map);
    GameSessionResponse gSessionResponse = comm.createGameSession(currentServer, gsr);
    if (gSessionResponse.getId() != null) {
      this.gameSessionID = gSessionResponse.getId();
    }
    return gSessionResponse.getId() != null;
  }

  /**
   * Deletes the game session held in the object
   *
   * @return True on Success, False otherwise
   * @author rsyed
   */
  public boolean deleteGame() {
    try {
      comm.deleteCurrentSession(currentServer + "/" + gameSessionID);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  //Getters and Setters
  public String getGameSessionID() {
    return this.gameSessionID;
  }
  public void setMapTemplate(MapTemplate mapTemplate){
    this.map = mapTemplate;
  }

  public void setServer(ServerDetails serverDetails){
    this.serverDetails = serverDetails;
  }
}