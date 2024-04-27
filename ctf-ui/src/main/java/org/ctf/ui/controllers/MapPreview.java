package org.ctf.ui.controllers;

import org.ctf.shared.client.Client;
import org.ctf.shared.client.ClientStepBuilder;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.data.exceptions.Accepted;
import org.ctf.shared.state.data.map.MapTemplate;

/**
 * Controller which gets a GameState from server to display as a map preview
 *
 * @author rsyed & ysiebenh
 */
public class MapPreview {

  private MapTemplate mapTemplate;

  /**
   * Constructor
   *
   * @param mapTemplate The Map which you need a generated GameState for
   * @author rsyed
   */
  public MapPreview(MapTemplate mapTemplate) {
    this.mapTemplate = mapTemplate;
  }

  /**
   * Method internally generates Clients for each Player. Joins a server with it and deletes the
   * session at the end to save server resources. Waits 80ms between commands in case the server is
   * slow.
   *
   * @return GameState object containing all data from the server (including blocks and placement)
   * @author rsyed & ysiebenh
   */
  public GameState getGameState() throws Accepted {
    Client[] clients = new Client[mapTemplate.getTeams()];

    // Init all clients
    for (int i = 0; i < clients.length; i++) {
      clients[i] =
          ClientStepBuilder.newBuilder()
              .enableRestLayer(false)
              .onLocalHost()
              .onPort("8888")
              .enableSaveGame(false)
              .build();
      if (i == 0) {
        clients[0].createGame(mapTemplate); // Creates a session with the first client
        clients[0].joinGame("0"); // Creates a session with the first client
      } else {
        clients[i].joinExistingGame(
            "localhost",
            "8888",
            clients[0].getCurrentGameSessionID(),
            Integer.toString(i)); // Joins the other clients for team creation
        try {
          Thread.sleep(80);
        } catch (InterruptedException e) {
          e.printStackTrace();
          throw new UnknownError("Something went wrong in the thread for Map Preview creation");
        }
      }
    }

    clients[0].getStateFromServer();
    GameState ret = clients[0].getCurrentState(); // saves the return state
    clients[0].deleteSession();

    return ret;
  }
}
