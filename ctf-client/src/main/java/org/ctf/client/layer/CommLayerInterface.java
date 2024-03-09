package org.ctf.client.layer;

import org.ctf.client.state.GameState;
import org.ctf.client.state.Move;
import org.ctf.client.state.data.exceptions.Accepted;
import org.ctf.client.state.data.exceptions.ForbiddenMove;
import org.ctf.client.state.data.exceptions.GameOver;
import org.ctf.client.state.data.exceptions.InvalidMove;
import org.ctf.client.state.data.exceptions.NoMoreTeamSlots;
import org.ctf.client.state.data.exceptions.SessionNotFound;
import org.ctf.client.state.data.exceptions.URLError;
import org.ctf.client.state.data.exceptions.UnknownError;
import org.ctf.client.state.data.map.MapTemplate;
import org.ctf.client.state.data.wrappers.GameSessionResponse;
import org.ctf.client.state.data.wrappers.JoinGameResponse;

public interface CommLayerInterface {

  /**
   * Requests the server specified in the URL parameter to create a GameSession using the map in the
   * MapTemplate parameter. Returns the server reponse as well as HTTP status codes thrown as
   * exceptions
   *
   * @param URL
   * @param map
   * @returns GameSessionResponse
   * @throws UnknownError (500)
   * @throws URLError (404)
   * @throws Accepted (200)
   */
  GameSessionResponse createGameSession(String URL, MapTemplate map);

  /**
   * Method makes a move request to the API using the input parameters. Returns the server reponse
   * as well as HTTP status codes thrown as exceptions
   *
   * @param URL
   * @param teamID
   * @param teamSecret
   * @param move
   * @throws Accepted (200)
   * @throws ForbiddenMove (403)
   * @throws SessionNotFound (404)
   * @throws InvalidMove (409)
   * @throws GameOver (410)
   * @throws UnknownError (500)
   * @throws URLError (404)
   */
  JoinGameResponse joinGame(String URL, String teamName);

  /**
   * Method makes a move request to the API using the input parameters. Returns the server reponse
   * as well as HTTP status codes thrown as exceptions
   *
   * @param URL
   * @param teamID
   * @param teamSecret
   * @param move
   * @throws Accepted (200)
   * @throws ForbiddenMove (403)
   * @throws SessionNotFound (404)
   * @throws InvalidMove (409)
   * @throws GameOver (410)
   * @throws UnknownError (500)
   * @throws URLError (404)
   */
  void makeMove(String URL, String teamID, String teamSecret, Move move);

  /**
   * Used to send a give up request to the server specified in the URL param. Other params are the
   * data needed to make a complete request. Returns the server reponse as well as HTTP status codes
   * thrown as exceptions.
   *
   * @param URL
   * @param teamID
   * @param teamSecret
   * @throws Accepted (200)
   * @throws SessionNotFound (404)
   * @throws ForbiddenMove (403)
   * @throws GameOver (410)
   * @throws UnknownError (500)
   * @throws URLError (404)
   */
  void giveUp(String URL, String teamID, String teamSecret);

  /**
   * Requests the server to return the current state of the session, session and server are
   * specified in the input param URL. Returns the server reponse as well as HTTP status codes
   * thrown as exceptions Functions as a refresh command for the URL specified session.
   *
   * @param URL
   * @return GameSessionResponse
   * @throws Accepted (200)
   * @throws SessionNotFound (404)
   * @throws UnknownError (500)
   * @throws URLError (404)
   */
  GameSessionResponse getCurrentSessionState(String URL);

  /**
   * Requests the server to delete the current session which is specified in the URL. Returns the
   * server reponse which are HTTP status codes thrown as exceptions.
   *
   * @param URL
   * @throws Accepted (200)
   * @throws SessionNotFound (404)
   * @throws UnknownError (500)
   * @throws URLError (404)
   */
  void deleteCurrentSession(String URL);

  /**
   * Requests the session, specified in the URL, to return its GameState. Returns the GameState
   * object as well as the HTTP status codes which are thrown as exceptions
   *
   * @param URL
   * @return GameState
   * @throws Accepted
   * @throws SessionNotFound
   * @throws UnknownError
   * @throws URLError (404)
   */
  GameState getCurrentGameState(String URL);
}