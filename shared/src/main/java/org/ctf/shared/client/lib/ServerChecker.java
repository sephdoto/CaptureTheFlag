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
   * Checks if server is active through a dummy gameTemplate. Also deletes the session it creates right after so free up the server resources again.
   *
   * @param serverDetails object holding the IP and Port of the server
   * @return true if server is active and ready to make sessions, false if not
   * @author rsyed
   */
  public boolean isServerActive(ServerDetails serverDetails) {
    String Path = Constants.testTemplate;
    MapTemplate mapTemplate;
    GameSessionResponse gSessionResponse = new GameSessionResponse();
    CommLayer comm = new CommLayer();
    Gson gson = new Gson();
    try {
      mapTemplate = gson.fromJson(new BufferedReader(new FileReader(Path)), MapTemplate.class);
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
    } catch (FileNotFoundException e) {
      throw new UnknownError("Test JSON couldnt be found");
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
   * @param serverDetails object holding the IP and Port of the server
   * @param sessionID the sessionID you want to check for activity
   * @return true if the session is present, false if any exception comes back indicating that the session is not there.
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
}
