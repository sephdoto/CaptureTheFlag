package org.ctf.shared.client.lib;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import org.ctf.shared.client.service.CommLayer;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.state.data.exceptions.URLError;
import org.ctf.shared.state.data.map.MapTemplate;
import org.ctf.shared.state.dto.GameSessionRequest;
import org.ctf.shared.state.dto.GameSessionResponse;

public class ServerChecker {

  /**
   * Checks if server is active through a dummy gameTemplate
   *
   * @param ip IP of the server
   * @param port port of the server
   * @return true if server is active and ready to make sessions, false if not
   */
  public boolean isServerActive(String ip, String port) {
    String Path = Constants.testTemplate;
    MapTemplate mapTemplate;
    GameSessionResponse gSessionResponse = new GameSessionResponse();
    CommLayer comm = new CommLayer();
    Gson gson = new Gson();
    try {
      mapTemplate = gson.fromJson(new BufferedReader(new FileReader(Path)), MapTemplate.class);
      GameSessionRequest gsr = new GameSessionRequest();
      gsr.setTemplate(mapTemplate);
      gSessionResponse = comm.createGameSession("http://" + ip + ":" + port + "/api/gamesession", gsr);
    } catch (UnknownError | URLError e) {
      return false;
    } catch (FileNotFoundException e) {
      throw new UnknownError("Test JSON couldnt be found");
    }
    comm.deleteCurrentSession(
        "http://" + ip + ":" + port + "/api/gamesession" + "/" + gSessionResponse.getId());
    return (gSessionResponse.getId() != null) ? true : false;
  }
}
