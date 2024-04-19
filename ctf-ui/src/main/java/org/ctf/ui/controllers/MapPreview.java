package org.ctf.ui.controllers;

import com.google.gson.Gson;
import org.ctf.shared.client.Client;
import org.ctf.shared.client.ClientStepBuilder;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.data.map.MapTemplate;

/**
 * Controller which gets a GameState from server to display as a map preview
 *
 * @author rsyed
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
   * session at the end to save server resources. Waits 80ms between commands in case the server is slow.
   *
   * @return GameState object containing all data from the server (including blocks and placement)
   * @author rsyed
   */
  public GameState getGameState() {
    Client[] clients = new Client[mapTemplate.getTeams()];

    // Init all clients
    for (int i = 0; i < clients.length; i++) {
      clients[i] =
          ClientStepBuilder.newBuilder()
              .enableRestLayer(false)
              .onLocalHost()
              .onPort("8888")
              .HumanPlayer()
              .build();
      if (i == 0) {
        clients[i].createGame(mapTemplate); // Creates a session with the first client
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

  public static void main(String[] args) {
    String jsonPayload =
        """
          {
            "gridSize": [10, 10],
            "teams": 2,
            "flags": 1,
            "blocks": 0,
            "pieces": [
              {
                "type": "Pawn",
                "attackPower": 1,
                "count": 10,
                "movement": {
                  "directions": {
                    "left": 0,
                    "right": 0,
                    "up": 1,
                    "down": 0,
                    "upLeft": 1,
                    "upRight": 1,
                    "downLeft": 0,
                    "downRight": 0
                  }
                }
              },
              {
                "type": "Rook",
                "attackPower": 5,
                "count": 2,
                "movement": {
                  "directions": {
                    "left": 2,
                    "right": 2,
                    "up": 2,
                    "down": 2,
                    "upLeft": 0,
                    "upRight": 0,
                    "downLeft": 0,
                    "downRight": 0
                  }
                }
              },
              {
                "type": "Knight",
                "attackPower": 3,
                "count": 2,
                "movement": {
                  "shape": {
                    "type": "lshape"
                  }
                }
              },
              {
                "type": "Bishop",
                "attackPower": 3,
                "count": 2,
                "movement": {
                  "directions": {
                    "left": 0,
                    "right": 0,
                    "up": 0,
                    "down": 0,
                    "upLeft": 2,
                    "upRight": 2,
                    "downLeft": 2,
                    "downRight": 2
                  }
                }
              },
              {
                "type": "Queen",
                "attackPower": 5,
                "count": 1,
                "movement": {
                  "directions": {
                    "left": 2,
                    "right": 2,
                    "up": 2,
                    "down": 2,
                    "upLeft": 2,
                    "upRight": 2,
                    "downLeft": 2,
                    "downRight": 2
                  }
                }
              },
              {
                "type": "King",
                "attackPower": 1,
                "count": 1,
                "movement": {
                  "directions": {
                    "left": 1,
                    "right": 1,
                    "up": 1,
                    "down": 1,
                    "upLeft": 1,
                    "upRight": 1,
                    "downLeft": 1,
                    "downRight": 1
                  }
                }
              }
            ],
            "placement": "symmetrical",
            "totalTimeLimitInSeconds": -1,
            "moveTimeLimitInSeconds": -1
          }
        """;

    Gson gson = new Gson();
    MapTemplate template = gson.fromJson(jsonPayload, MapTemplate.class);
    MapPreview prev = new MapPreview(template);
    System.out.println(gson.toJson(prev.getGameState()));
  }
}
