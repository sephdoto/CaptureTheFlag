package org.ctf.shared.client.lib;

import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.data.exceptions.ForbiddenMove;
import org.ctf.shared.state.data.exceptions.GameOver;
import org.ctf.shared.state.data.exceptions.InvalidMove;
import org.ctf.shared.state.data.exceptions.NoMoreTeamSlots;
import org.ctf.shared.state.data.exceptions.SessionNotFound;
import org.ctf.shared.state.data.exceptions.URLError;
import org.ctf.shared.state.data.map.MapTemplate;
import org.ctf.shared.state.dto.GameSession;

/**
 * This Interface defines the minimum functionality a {@link Client} has to implement
 *
 * @author rsyed
 */
public interface GameClientInterface {

  /**
   * Sets the server the Layer should point to
   *
   * @param IP The IP address of the server
   * @param port port number the server is responding to
   * @author rsyed
   */
  void setServer(String IP, String port);

  /**
   * Creates a {@link GameSession} in the server
   *
   * @throws UnknownError (500)
   * @throws URLError (404)
   * @param map The map the server is going to use to create the session
   * @author rsyed
   */
  void createGame(MapTemplate map);

  /**
   * Requests to join the current session with a given team name
   *
   * @param teamName is the string you want your team name to be
   * @throws SessionNotFound
   * @throws NoMoreTeamSlots
   * @throws UnknownError
   * @author rsyed
   */
  void joinGame(String teamName);

  /**
   * Requests to make a {@link Move} in the currently selected game
   *
   * @param move is the move you want to make
   * @throws SessionNotFound (404)
   * @throws ForbiddenMove (403)
   * @throws InvalidMove (409)
   * @throws GameOver (410)
   * @throws UnknownError (500)
   * @author rsyed
   */
  void makeMove(Move move);

  /**
   * Requests to give up in the currentGame
   *
   * @throws SessionNotFound (404)
   * @throws ForbiddenMove (403)
   * @throws GameOver (410)
   * @throws UnknownError (500)
   * @throws URLError (404)
   * @author rsyed
   */
  void giveUp();

  /**
   * Pulls the {@link GameSession} information from the server for the current Session
   *
   * @throws SessionNotFound (404)
   * @throws UnknownError (500)
   * @throws URLError (404)
   * @author rsyed
   */
  void getSessionFromServer();

  /**
   * Pulls the {@link GameState} from the server for the ID
   *
   * @throws SessionNotFound
   * @throws UnknownError
   * @throws URLError (404)
   * @author rsyed
   */
  void getStateFromServer();

  /**
   * Deletes the current session
   *
   * @throws SessionNotFound (404)
   * @throws UnknownError (500)
   * @throws URLError (404)
   * @author rsyed
   */
  void deleteSession();
}
