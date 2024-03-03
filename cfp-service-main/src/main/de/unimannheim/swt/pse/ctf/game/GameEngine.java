//TODO Add license if applicable
package de.unimannheim.swt.pse.ctf.game;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;

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
import io.swagger.v3.oas.annotations.media.Schema;

import com.google.gson.Gson;

/**
 * Game Engine Implementation
 * 
 * @author rsyed
 */
public class GameEngine implements Game {

    private MapTemplate currentTemplate; // Saves a copy of the template
    private GameState gameState;
    private int maxTeams;
    private int remainingTeamSlots;
    private int maxFlags;
    private boolean isStarted; // Initial value
    private boolean isGameOver;
    private boolean timeLimit;
    private Date startedDate;
    private Date endDate;

    /**
     * Creates a game session with the corresponding Map passed onto as the Template
     * 
     * @param template
     * @return GameState
     */
    @Override
    public GameState create(MapTemplate template) {
        // TODO Parse the Map template to create an Initial Game State
        this.currentTemplate = template; // Saves a copy of the initial template

        GameState gameState = new GameState();
        gameState.setGrid(new String[template.getGridSize()[0]][template.getGridSize()[1]]); // Ints with empty grid of
                                                                                             // specified size //Parsing
                                                                                             // the Size
        this.maxTeams = template.getTeams(); // Setting Max Number of teams allowed
        this.remainingTeamSlots = maxTeams; // Setting Initial Number of Teams in GameEngine Instance
        this.maxFlags = template.getFlags(); // Setting Initial Number of Teams in GameEngine Instance

        // Setting Flags
        this.isStarted = false;
        this.isGameOver = false;

        // Setting State
        this.gameState = gameState;
        return this.gameState;
    }

    /**
     * Get current state of the game
     *
     * @return GameState
     */
    @Override
    public GameState getCurrentGameState() {
        return this.gameState;
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
        if (!timeLimit) { // If no limit is set returns -1
            return -1;
        } else if (isGameOver) { // if GameOver flag is set returns 0
            return 0;
        }
        return 1; // Otherwise returns 1
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
        return remainingTeamSlots;
    }

    /**
     * @return Start {@link Date} of game
     */
    @Override
    public Date getStartedDate() {
        return startedDate;
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
        Team[] teamBlock = getCurrentGameState().getTeams();
        int currentTeam = getCurrentGameState().getCurrentTeam() - 1;

        if (teamBlock[currentTeam].getId().equals(teamId)) {
            // CODE TO DELETE WHATS LEFT OF THE TEAM and give up
        }
    }

    /**
     * Checks whether the game is over based on the current {@link GameState}.
     *
     * @return true if game is over, false if game is still running.
     */
    @Override
    public boolean isGameOver() {
        if (isGameOver) {
            return true;
        } else {
            return false;
        }
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
        if (isStarted && !isGameOver && getCurrentGameState() != null) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether a move is valid based on the current game state.
     *
     * @param move {@link Move}
     * @return true if move is valid based on current game state, false otherwise
     */
    @Override
    public boolean isValidMove(Move move) {
        // TODO Need to write logic
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
        // Initial check if Slots are even available
        if (remainingTeamSlots < 0) {
            throw new NoMoreTeamSlots();
        }

        Team[] currentTeams = getCurrentGameState().getTeams(); // Gets array of teams from currentGameState

        for (int i = 0; i <= currentTeams.length; i++) { // Loops over it to
            if (currentTeams[i] != null) { // find the first empty team
                currentTeams[i].setId(teamId); // sets the team ID
                remainingTeamSlots--;
                break;
            }
        }

        // TODO Extra Method to place pieces on the Grid when joining

        // Code for handling game start
        startGame();

        // Code for return
        int teamSelector = randomGen(currentTemplate.getTeams() - 1, 5); // updates team selector with a random Number
        return currentTeams[teamSelector];
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

    /**
     * Returns a random number between 0 and max
     * Does specified number of iterations
     *
     * @param max
     * @param iterations
     * 
     */
    public int randomGen(int max, int iterations) {
        Random rand = new Random();
        int ret = 0;
        for (int i = 0; i <= iterations; i++) {
            ret = rand.nextInt(max);
        }
        return ret;
    }

    /**
     * Helper method to Start the game
     * 
     */
    private void startGame() {
        this.isStarted = true; // Starts the game because all teams have joined

        LocalDateTime localDateTime = LocalDateTime.now(); // Gets a localDateTime Obj
        this.startedDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        Long seconds = Integer.toUnsignedLong(currentTemplate.getTotalTimeLimitInSeconds());

        if (seconds > 0) { // Checks if its a timed Game
            LocalDateTime modifiedDateTime = localDateTime.plusMinutes(seconds); // Adds seconds to a new Object
            this.endDate = Date.from(modifiedDateTime.atZone(ZoneId.systemDefault()).toInstant()); // Converts and saves
                                                                                                   // it to endDate
        } else if (seconds == 0) {
            this.isGameOver = true;
        } else {
            this.timeLimit = false;
        }
    }
}