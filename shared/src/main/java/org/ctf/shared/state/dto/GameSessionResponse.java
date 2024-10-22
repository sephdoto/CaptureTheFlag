package org.ctf.shared.state.dto;

import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * This class is used to represent the state of a game session.
 */
public class GameSessionResponse {

    @Schema(
            description = "unique game session identifier"
    )
    private String id;
    @Schema(
            description = "the date the game started"
    )
    private Date gameStarted;
    @Schema(
            description = "the date the game ended (or ends if game time is over)"
    )
    private Date gameEnded;
    @Schema(
            description = "if no total game time limit set, 0 if over, > 0 if seconds remain"
    )
    private int remainingGameTimeInSeconds;
    @Schema(
            description = "-1 if no move time limit set, 0 if over, > 0 if seconds remain"
    )
    private int remainingMoveTimeInSeconds;
    @Schema(
            description = "true if game is over, false otherwise"
    )
    private boolean gameOver;
    @Schema(
            description = "the winner(s) of the game (if any)"
    )
    private String[] winner;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public String[] getWinner() {
        return winner;
    }

    public void setWinner(String[] winner) {
        this.winner = winner;
    }

    public Date getGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(Date gameStarted) {
        this.gameStarted = gameStarted;
    }

    public Date getGameEnded() {
        return gameEnded;
    }

    public void setGameEnded(Date gameEnded) {
        this.gameEnded = gameEnded;
    }

    public int getRemainingGameTimeInSeconds() {
        return remainingGameTimeInSeconds;
    }

    public void setRemainingGameTimeInSeconds(int remainingGameTimeInSeconds) {
        this.remainingGameTimeInSeconds = remainingGameTimeInSeconds;
    }

    public int getRemainingMoveTimeInSeconds() {
        return remainingMoveTimeInSeconds;
    }

    public void setRemainingMoveTimeInSeconds(int remainingMoveTimeInSeconds) {
        this.remainingMoveTimeInSeconds = remainingMoveTimeInSeconds;
    }
}
