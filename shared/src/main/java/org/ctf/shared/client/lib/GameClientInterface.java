package org.ctf.shared.client.lib;

import org.ctf.shared.constants.Constants.AI;
import org.ctf.shared.state.Move;
import org.ctf.shared.state.data.map.MapTemplate;

/**
 * This is the contract the client HAS to fulfill. How it fulfills it is upto the Client Methods
 * throw Exceptions as returns instead of classes
 *
 * @author rsyed
 */
public interface GameClientInterface {

  /**
   * Sets the server the Layer should point to
   *
   * @param IP The IP address of the server
   * @param port port number the server is responding to
   */
  void setServer(String IP, String port);

  /**
   * Creates a GameSession in the server
   *
   * @param map The map the server is going to use to create the session
   */
  void createGame(MapTemplate map);

  /**
   * Requests to join the current session with a given team name
   *
   * @param teamName is the string you want your team name to be
   */
  void joinGame(String teamName);

  /**
   * Requests to make a move in the currently selected game
   *
   * @param move is the move you want to make
   */
  void makeMove(Move move);

  /** Requests to give up in the currentGame */
  void giveUp();

  /** Pulls the GameSession information from the server for the current Session */
  void getSessionFromServer();

  /** Pulls the GameState from the server for the ID */
  void getStateFromServer();

  /** Deletes the current session */
  void deleteSession();

  /** Sets the type of player the Client is going to use */
  void setPlayerType(AI num);
}
