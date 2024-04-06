package org.ctf.ai.mcts2;

import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Piece;
import org.ctf.shared.state.Team;

/**
 * This class is an adjusted version of GameState, using the Grid class instead of a String[][] Array.
 */
public class ReferenceGameState {
  private Grid grid;
  private Team[] teams;
  private int currentTeam;
  private ReferenceMove lastMove;

  public ReferenceGameState(GameState gameState) {
    this(new Grid(gameState), gameState.getTeams(), gameState.getCurrentTeam(), new ReferenceMove(gameState, gameState.getLastMove()));
  }
  
  public ReferenceGameState(Grid grid, Team[] teams, int currentTeam, ReferenceMove lastMove) {
    this.grid = grid;
    this.teams = teams;
    this.currentTeam = currentTeam;
    this.lastMove = lastMove;
  }

  //TODO sehr ineffizient, offen für Verbesserungsvorschläge
  /**
   * This method only deep copies the Grid, the pieceVisionGrid should
   * TODO PRÜFEN OB DAS AUCH SO WICHTIG ist oder ob man deep copien kann und nicht initialisieren muss.
   * Man kann das kopieren überarbeiten, sodass die richtigen pieces aus dem anderen gameState genommen werden. Das ist mal ein TODO
   * @return
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
      Piece[] pieces = new Piece[this.teams[i].getPieces().length];
      for(int j=0; j<pieces.length; j++) {
        pieces[j] = new Piece();
        pieces[j].setDescription(this.teams[i].getPieces()[j].getDescription());
        pieces[j].setId(this.teams[i].getPieces()[j].getId());
        pieces[j].setTeamId(this.teams[i].getPieces()[j].getTeamId());
        pieces[j].setPosition(new int[] {this.teams[i].getPieces()[j].getPosition()[0], this.teams[i].getPieces()[j].getPosition()[1]});
      }
      teams[i].setPieces(pieces);
    }
    return new ReferenceGameState(this.grid.clone(), teams, this.currentTeam, this.lastMove);
    //TODO eventuell lastMove deep clonen?
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