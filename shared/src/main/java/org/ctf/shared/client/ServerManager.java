package org.ctf.shared.client;

import org.ctf.shared.client.lib.ServerDetails;
import org.ctf.shared.client.service.CommLayerInterface;
import org.ctf.shared.state.data.map.MapTemplate;
import org.ctf.shared.state.dto.GameSessionRequest;
import org.ctf.shared.state.dto.GameSessionResponse;

public class ServerManager {
  CommLayerInterface comm;
  public ServerDetails serverInfo;
  MapTemplate map;
  public String currentServer;
  public String gameSessionID;

  public ServerManager(CommLayerInterface comm, ServerDetails serverInfo, MapTemplate map) {
    this.comm = comm;
    this.map = map;
    this.currentServer =
        "http://" + serverInfo.getHost() + ":" + serverInfo.getPort() + "/api/gamesession";
  }

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

  public boolean deleteGame() {
    try {
      comm.deleteCurrentSession(currentServer + "/" + gameSessionID);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public String getGameSessionID() {
    return this.gameSessionID;
  }
}
