package org.ctf.TL.layer;

import org.ctf.TL.data.map.MapTemplate;
import org.ctf.TL.data.wrappers.GameSessionResponse;
import org.ctf.TL.data.wrappers.JoinGameResponse;
import org.ctf.TL.exceptions.Accepted;
import org.ctf.TL.state.GameState;
import org.ctf.TL.state.Move;
import org.ctf.TL.state.Team;

public interface CommInterface {

    /**
     * Creates a Game Session if object is not connected to any game session
     * Receives a template object which it uses to create the body for the API
     * request
     * Returns GameSessionResponse and HTTP Codes
     * 200 Game session created
     * 500 Unknown error occurred
     * 
     * @param template
     * @return 
     */
    GameSessionResponse createGameSession(MapTemplate template);

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
     * @param mov
     */
    void makeMove(Move mov);

    /**
     * Joins a Game Session if object is connected to a game session
     * Receives a teamName which is used as teamID
     * Return Codes
     * 200 Team joined
     * 404 Game session not found
     * 429 No more team slots available
     * 500 Unknown error occurred
     * 
     * The Return is data needed for your team to communicate with
     * @param teamName
     * 
     * @return String JSON
     */
    JoinGameResponse joinGame(String teamName);

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
     * @param ID Team ID which wants to give up
     * @param secret team password
     */
    void giveUp(String ID, String secret);

    /**
     * Gets the current Session
     * Return Codes
     * 200 Game session response returned
     * 404 Game session not found
     * 500 Unknown error occurred
     * @throws Accepted 
     * 
     */
    void getCurrentSession();

    /**
     * Delets the current Session
     * Return Codes
     * 200 Game session response returned
     * 404 Game session not found
     * 500 Unknown error occurred
     */
    void deleteCurrentSession();

    /**
     * Gets the current Session
     * Returns Game State Object
     * Codes internally thrown
     * 200 Game state returned
     * 404 Game session not found
     * 500 Unknown error occurred
     * 
     * @return GameState
     */
    GameState getCurrentState();

}