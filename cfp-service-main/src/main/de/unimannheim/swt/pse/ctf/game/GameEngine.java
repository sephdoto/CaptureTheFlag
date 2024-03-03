//TODO Add license if applicable
package de.unimannheim.swt.pse.ctf.game;

import java.util.Date;
import de.unimannheim.swt.pse.ctf.game.exceptions.Accepted;
import de.unimannheim.swt.pse.ctf.game.exceptions.SessionNotFound;
import de.unimannheim.swt.pse.ctf.game.exceptions.UnknownError;

import de.unimannheim.swt.pse.ctf.controller.GameSession;
import de.unimannheim.swt.pse.ctf.game.exceptions.GameOver;
import de.unimannheim.swt.pse.ctf.game.exceptions.InvalidMove;
import de.unimannheim.swt.pse.ctf.game.exceptions.NoMoreTeamSlots;
import de.unimannheim.swt.pse.ctf.game.map.MapTemplate;
import de.unimannheim.swt.pse.ctf.game.state.GameState;
import de.unimannheim.swt.pse.ctf.game.state.Move;
import de.unimannheim.swt.pse.ctf.game.state.Piece;
import de.unimannheim.swt.pse.ctf.game.state.Team;
import com.google.gson.Gson;

/**
 * Game Engine Implementation
 * 
 * @author rsyed
 */
public class GameEngine implements Game {

    Team currentTeam;
    String teamSecret;
    GameSession currentSession;
    private GameState gameState;

    /**
     * Creates a game session with the corresponding Map passed onto as the Template
     * 
     * @param template
     * @return GameState
     */
    @Override
    public GameState create(MapTemplate template) {
        // Create a GameSession and

        return null;
    }

    /**
     * Get current state of the game
     *
     * @return GameState
     */
    @Override
    public GameState getCurrentGameState() {
        return null;
    }

    /**
     * @return End date of game
     */
    @Override
    public Date getEndDate() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     *
     * @return -1 if no total game time limit set, 0 if over, > 0 if seconds remain
     */
    @Override
    public int getRemainingGameTimeInSeconds() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     *
     * @return -1 if no move time limit set, 0 if over, > 0 if seconds remain
     */
    @Override
    public int getRemainingMoveTimeInSeconds() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @return number of remaining team slots
     */
    @Override
    public int getRemainingTeamSlots() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @return Start {@link Date} of game
     */
    @Override
    public Date getStartedDate() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Get winner(s) (if any)
     *
     * @return {@link Team#getId()} if there is a winner
     */
    @Override
    public String[] getWinner() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * A team has to option to give up a game (i.e., losing the game as a result).
     *
     * Assume that a team can only give up if it is its move (turn).
     *
     * @param teamId Team ID
     */
    @Override
    public void giveUp(String teamId) {

    }

    /**
     * Checks whether the game is over based on the current {@link GameState}.
     *
     * @return true if game is over, false if game is still running.
     */
    @Override
    public boolean isGameOver() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Checks whether the game is started based on the current {@link GameState}.
     *
     * <ul>
     * <li>{@link Game#isGameOver()} == false</li>
     * <li>{@link Game#getCurrentGameState()} != null</li>
     * </ul>
     *
     * @return
     */
    @Override
    public boolean isStarted() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Checks whether a move is valid based on the current game state.
     *
     * @param move {@link Move}
     * @return true if move is valid based on current game state, false otherwise
     */
    // TODO Need to understand logic for how valid move is calculated
    @Override
    public boolean isValidMove(Move move) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Updates a game and its state based on team join request (add team).
     *
     * <ul>
     * <li>adds team if a slot is free (array element is null)</li>
     * <li>if all team slots are finally assigned, implicitly starts the game by
     * picking a starting team at random</li>
     * </ul>
     *
     * @param teamId Team ID
     * @return Team
     * @throws NoMoreTeamSlots No more team slots available
     */

    // TODO do proper joinGameLogic
    @Override
    public Team joinGame(String teamId) {
        return null;
    }

    /**
     * Make a move
     *
     * @param move {@link Move}
     * @throws InvalidMove Requested move is invalid
     * @throws GameOver    Game is over
     */
    @Override
    public void makeMove(Move move) {
        // TODO Implement
    }

}
