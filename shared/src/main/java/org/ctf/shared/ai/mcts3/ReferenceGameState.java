package org.ctf.shared.ai.mcts3;

import java.util.ArrayList;
import org.ctf.shared.ai.ReferenceMove;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Piece;

/**
 * This class is an adjusted version of GameState, using the Grid class instead of a String[][] Array.
 * 
 * @author sistumpf
 */
public class ReferenceGameState {
  private Grid grid;
  private Team[] teams;
  private int currentTeam;
  private ReferenceMove lastMove;

  /**
   * Initializes a ReferenceGameState from a normal GameState.
   * 
   * @param gameState
   */
  public ReferenceGameState(GameState gameState) {
    this(new Grid(gameState), Team.toNewTeams(gameState.getTeams()), gameState.getCurrentTeam(), new ReferenceMove(gameState, gameState.getLastMove()));
  }
  
  /**
   * Default Constructor to initialize a ReferenceGameState.
   * 
   * @param grid the grid
   * @param teams the teams
   * @param currentTeam the current team
   * @param lastMove the last made move
   */
  public ReferenceGameState(Grid grid, Team[] teams, int currentTeam, ReferenceMove lastMove) {
    this.grid = grid;
    this.teams = teams;
    this.currentTeam = currentTeam;
    this.lastMove = lastMove == null ? 
        new ReferenceMove(null, new int[2]) : lastMove;
  }

  /**
   * Creates and returns a GameState representing this ReferenceGameState
   * 
   * @return a GameState representation of this ReferenceGameState
   */
  public GameState toGameState() {
    GameState gameState = new GameState();
    gameState.setCurrentTeam(currentTeam);
    gameState.setLastMove(this.lastMove.toMove());
    gameState.setTeams(Team.toOldTeams(teams));
    String[][] stringGrid = new String[grid.getGrid().length][grid.getGrid()[0].length];
    for(int y=0; y<grid.getGrid().length; y++)
      for(int x=0; x<grid.getGrid()[0].length; x++) {
        if(grid.getPosition(x, y) == null)
          stringGrid[y][x] = "";
        else
          stringGrid[y][x] = grid.getPosition(x, y).toString();
      }
    gameState.setGrid(stringGrid);
    return gameState;
  }
  
  /**
   * This method only deep copies the Grid.grid, the pieceVisionGrid and pieceVisions should be initialized in TreeNode.
   * 
   * @return clone of this ReferenceGameState
   */
  @Override
  public ReferenceGameState clone() {
    Team[] teams = new Team[this.teams.length];
    for(int i=0; i<teams.length; i++) {
      if(this.teams[i] == null)
        continue;
      teams[i] = new Team();
      teams[i].setBase(this.teams[i].getBase());
      teams[i].setFlags(this.teams[i].getFlags());
      teams[i].setId(this.teams[i].getId());
      ArrayList<Piece> pieces = new ArrayList<Piece>(this.teams[i].getPieces().size());
      for(int j=0; j<this.teams[i].getPieces().size(); j++) {
        pieces.add(new Piece());
        pieces.get(j).setDescription(this.teams[i].getPieces().get(j).getDescription());
        pieces.get(j).setId(this.teams[i].getPieces().get(j).getId());
        pieces.get(j).setTeamId(this.teams[i].getPieces().get(j).getTeamId());
        pieces.get(j).setPosition(new int[] {this.teams[i].getPieces().get(j).getPosition()[0], this.teams[i].getPieces().get(j).getPosition()[1]});
      }
      teams[i].setPieces(pieces);
    }
    Grid grid = new Grid(teams, this.grid.getGrid().length, this.grid.getGrid()[0].length);
    
    ReferenceMove lastMove;
    if(this.lastMove.getPiece() == null)
      lastMove = new ReferenceMove(null, new int[] {0,0});
    else
      lastMove = new ReferenceMove(
        grid.getPosition(this.lastMove.getPiece().getPosition()[1], this.lastMove.getPiece().getPosition()[0]).getPiece(), 
        this.lastMove.getNewPosition());
    return new ReferenceGameState(grid, teams, this.currentTeam, lastMove);
  }
  
  public Grid getGrid() {
    return grid;
  }

  public void setGrid(Grid grid) {
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

  public ReferenceMove getLastMove() {
    return lastMove;
  }

  public void setLastMove(ReferenceMove lastMove) {
    this.lastMove = lastMove;
  }
}