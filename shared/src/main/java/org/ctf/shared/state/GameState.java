package org.ctf.shared.state;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * This class is used to represent the current game state for a specific game session.
 * @author Marcus Kessel
 */
public class GameState implements java.io.Serializable {
    private static final long serialVersionUID = 8846830301573918260L;
    @Schema(
            description = "the current grid (game board) as a two-dimensional list (array). " +
                    "each element contains a string reference to a piece, a block, a team's base or an empty square." +
                    "format: \"\" (empty string) for an empty square, \"b\" for block, \"b:tid\" for team's base with team id, \"p:tid_pid\" for a piece's identifier.",
            example = "[\n" +
                    "   [\"b:1\", \"\", \"\", \"\", \"\"],\n" +
                    "   [\"\", \"p:1_1\", \"p:1_2\", \"p:1_3\", \"\"],\n" +
                    "   [\"b\", \"\", \"\", \"\", \"b\"],\n" +
                    "   [\"\", \"p:2_1\", \"p:2_2\", \"p:2_3\", \"\"],\n" +
                    "   [\"\", \"\", \"\", \"\", \"b:2\"]\n" +
                    "]"
    )
    private String[][] grid;
    @Schema(
            description = "teams playing the game"
    )
    private Team[] teams;
    @Schema(
            description = "the current team making the next move"
    )
    private int currentTeam;
    @Schema(
            description = "the last move"
    )
    private Move lastMove;

    public GameState() {
        this.grid = new String[10][10];
        this.teams = new Team[0];
        this.currentTeam = -1;
        this.lastMove = null;
    }

    public String[][] getGrid() {
        return grid;
    }

    public void setGrid(String[][] grid) {
        this.grid = grid;
    }

    public Team[] getTeams() {
        return teams;
    }

    public void setTeams(Team[] teams) {
        this.teams = teams;
    }

    public int getCurrentTeam() {
        return currentTeam;
    }

    public void setCurrentTeam(int currentTeam) {
        this.currentTeam = currentTeam;
    }

    public Move getLastMove() {
        return lastMove;
    }

    public void setLastMove(Move lastMove) {
        this.lastMove = lastMove;
    }
}

