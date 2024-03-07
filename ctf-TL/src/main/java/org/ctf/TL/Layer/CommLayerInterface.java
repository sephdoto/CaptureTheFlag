package org.ctf.TL.layer;

import org.ctf.TL.data.map.MapTemplate;
import org.ctf.TL.data.wrappers.GameSessionResponse;
import org.ctf.TL.data.wrappers.JoinGameResponse;
import org.ctf.TL.exceptions.Accepted;
import org.ctf.TL.exceptions.ForbiddenMove;
import org.ctf.TL.exceptions.GameOver;
import org.ctf.TL.exceptions.InvalidMove;
import org.ctf.TL.exceptions.NoMoreTeamSlots;
import org.ctf.TL.exceptions.SessionNotFound;
import org.ctf.TL.exceptions.URLError;
import org.ctf.TL.exceptions.UnknownError;
import org.ctf.TL.state.GameState;
import org.ctf.TL.state.Move;

public interface CommLayerInterface {

    /**
     * Creates a Game Session if object is not connected to any game session
     * Receives a template object which it uses to create the body for the API
     * request
     * Returns GameSessionResponse containing a session ID, start and end date,
     * Gameover flag, and winner flag
     * 200 Game session created
     * 500 Unknown error occurred
     * 
     * @param URL
     * @param map
     * 
     * @returns GameSessionResponse
     * @throws UnknownError
     * @throws URLError
     * @throws Accepted
     */
    GameSessionResponse createGameSession(String URL, MapTemplate map);

    /**
     * Joins a Game Session if object is connected to a game session
     * Receives a
     * String URL to use while making the request
     * String for proposed teamID
     * Return Codes
     * 200 Team joined
     * 404 Game session not found
     * 429 No more team slots available
     * 500 Unknown error occurred
     * 
     * The Return is data needed for your team to communicate with
     * 
     * @param URL
     * @param teamName
     * 
     * @return String JSON
     * 
     * @throws SessionNotFound
     * @throws NoMoreTeamSlots
     * @throws UnknownError
     * @throws Accepted
     */
    JoinGameResponse joinGame(String URL, String teamName);

    /**
     * Makes a move on the board if object is connected to a game session and a game
     * is joined
     * Takes a Move Object as an input
     * Returns
     * 200 Valid move
     * 403 Move is forbidden for given team (anti-cheat)
     * 404 Game session not found
     * 409 Invalid move
     * 410 Game is over
     * 500 Unknown error occurred
     * 
     * @param URL
     * @param teamID
     * @param teamSecret
     * @param move
     * 
     * @throws Accepted
     * @throws ForbiddenMove
     * @throws SessionNotFound
     * @throws InvalidMove
     * @throws GameOver
     * @throws UnknownError
     */
    void makeMove(String URL, String teamID, String teamSecret, Move move);

    /**
     * Gives up in Game Session if object is connected to a game session and a game
     * is joined
     * Return Codes
     * 200 Request completed
     * 403 Give up is forbidden for given team (anti-cheat)
     * 404 Game session not found
     * 410 Game is over
     * 500 Unknown error occurred
     * 
     * @param URL
     * @param teamID
     * @param teamSecret
     * 
     * @throws Accepted
     * @throws SessionNotFound
     * @throws ForbiddenMove
     * @throws GameOver
     * @throws UnknownError
     */
    void giveUp(String URL, String teamID, String teamSecret);

    /**
     * Gets the State of the current Session
     * Return Codes
     * 200 Game session response returned
     * 404 Game session not found
     * 500 Unknown error occurred
     * 
     * @param URL
     * @return GameSessionResponse
     * @throws Accepted
     * @throws SessionNotFound
     * @throws UnknownError
     */
    GameSessionResponse getCurrentSessionState(String URL);

    /**
     * Deletes the current Session
     * Return Codes
     * 200 Game session response returned
     * 404 Game session not found
     * 500 Unknown error occurred
     * 
     * @param URL
     * @throws Accepted
     * @throws SessionNotFound
     * @throws UnknownError
     * 
     */
    void deleteCurrentSession(String URL);

    /**
     * Gets the current Session
     * Returns GameState Object
     * Codes internally thrown
     * 200 Game state returned
     * 404 Game session not found
     * 500 Unknown error occurred
     * 
     * @param URL
     * @return GameState
     * @throws Accepted
     * @throws SessionNotFound
     * @throws UnknownError
     */
    GameState getCurrentGameState(String URL);

}