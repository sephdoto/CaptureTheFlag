package org.ctf.shared.client.lib;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import org.ctf.shared.client.service.CommLayer;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.state.data.exceptions.SessionNotFound;
import org.ctf.shared.state.data.exceptions.URLError;
import org.ctf.shared.state.data.map.MapTemplate;
import org.ctf.shared.state.dto.GameSessionRequest;
import org.ctf.shared.state.dto.GameSessionResponse;

public class ServerChecker {

  /**
   * Checks if server is active through a dummy gameTemplate. Also deletes the session it creates
   * right after so free up the server resources again.
   *
   * @param serverDetails object holding the IP and Port of the server
   * @return true if server is active and ready to make sessions, false if not
   * @author rsyed
   */
  public boolean isServerActive(ServerDetails serverDetails) {
    String Path = Constants.clientTestingTemplate;
    MapTemplate mapTemplate;
    GameSessionResponse gSessionResponse = new GameSessionResponse();
    CommLayer comm = new CommLayer();
    Gson gson = new Gson();
    try {
      mapTemplate = getTestTemplate();
      GameSessionRequest gsr = new GameSessionRequest();
      gsr.setTemplate(mapTemplate);
      gSessionResponse =
          comm.createGameSession(
              "http://"
                  + serverDetails.getHost()
                  + ":"
                  + serverDetails.getPort()
                  + "/api/gamesession",
              gsr);
    } catch (UnknownError | URLError e) {
      return false;
    }
    comm.deleteCurrentSession(
        "http://"
            + serverDetails.getHost()
            + ":"
            + serverDetails.getPort()
            + "/api/gamesession"
            + "/"
            + gSessionResponse.getId());
    return (gSessionResponse.getId() != null) ? true : false;
  }

  /**
   * Checks if a Session is active at the server specified in the input parameter.
   *
   * @param serverDetails object holding the IP and Port of the server
   * @param sessionID the sessionID you want to check for activity
   * @return true if the session is present, false if any exception comes back indicating that the
   *     session is not there.
   * @author rsyed
   */
  public boolean isSessionActive(ServerDetails serverDetails, String sessionID) {
    CommLayer comm = new CommLayer();
    try {
      comm.getCurrentSessionState(
          "http://"
              + serverDetails.getHost()
              + ":"
              + serverDetails.getPort()
              + "/api/gamesession"
              + "/"
              + sessionID);
    } catch (SessionNotFound | UnknownError | URLError e) {
      return false;
    }
    return true;
  }

  private MapTemplate getTestTemplate() {
    Gson gson = new Gson();
    String jsonPayload =
        """
{"gridSize":[14,7],"teams":2,"flags":1,"pieces":[{"type":"Bishop","attackPower":3,"count":2,"movement":{"directions":{"left":0,"right":0,"up":0,"down":0,"upLeft":20,"upRight":20,"downLeft":20,"downRight":20}}},{"type":"King","attackPower":10,"count":1,"movement":{"directions":{"left":1,"right":1,"up":1,"down":1,"upLeft":1,"upRight":1,"downLeft":1,"downRight":1}}},{"type":"Queen","attackPower":5,"count":1,"movement":{"directions":{"left":20,"right":20,"up":20,"down":20,"upLeft":20,"upRight":20,"downLeft":20,"downRight":20}}},{"type":"Knight","attackPower":3,"count":2,"movement":{"shape":{"type":"lshape"}}},{"type":"Pawn","attackPower":1,"count":4,"movement":{"directions":{"left":1,"right":1,"up":2,"down":2,"upLeft":1,"upRight":1,"downLeft":0,"downRight":0}}},{"type":"Rook","attackPower":5,"count":2,"movement":{"directions":{"left":20,"right":20,"up":20,"down":20,"upLeft":0,"upRight":0,"downLeft":0,"downRight":0}}}],"blocks":14,"placement":"symmetrical","totalTimeLimitInSeconds":-1,"moveTimeLimitInSeconds":-1}
""";
   return gson.fromJson(jsonPayload, MapTemplate.class);
  }
}
