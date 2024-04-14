package de.unimannheim.swt.pse.ctf.game;

import de.unimannheim.swt.pse.ctf.game.map.MapTemplate;
import de.unimannheim.swt.pse.ctf.game.state.GameState;
import de.unimannheim.swt.pse.ctf.game.state.Move;
import de.unimannheim.swt.pse.ctf.game.state.Team;
import java.util.Date;

public class NewGameEngine implements Game {

  // **************************************************
  // Fields
  // **************************************************

  // **************************************************
  // Required by GameEngine
  // **************************************************
  private GameState gameState; // MAIN Data Store for GameEngine
  private boolean isGameOver;
  private int remainingTeamSlots;
  private Date startedDate;
  private Date endDate;
  // **************************************************
  // End of Required by GameState
  // **************************************************

  // **************************************************
  // Nice to haves
  // **************************************************
  private MapTemplate currentTemplate; // Saves a copy of the template

  // **************************************************
  // END of Nice to haves
  // **************************************************

  @Override
  public GameState create(MapTemplate template) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'create'");
  }

  @Override
  public GameState getCurrentGameState() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getCurrentGameState'");
  }

  @Override
  public Team joinGame(String teamId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'joinGame'");
  }

  @Override
  public int getRemainingTeamSlots() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getRemainingTeamSlots'");
  }

  @Override
  public void makeMove(Move move) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'makeMove'");
  }

  @Override
  public int getRemainingGameTimeInSeconds() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getRemainingGameTimeInSeconds'");
  }

  @Override
  public int getRemainingMoveTimeInSeconds() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getRemainingMoveTimeInSeconds'");
  }

  @Override
  public void giveUp(String teamId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'giveUp'");
  }

  @Override
  public boolean isValidMove(Move move) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'isValidMove'");
  }

  @Override
  public boolean isStarted() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'isStarted'");
  }

  @Override
  public boolean isGameOver() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'isGameOver'");
  }

  @Override
  public String[] getWinner() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getWinner'");
  }

  @Override
  public Date getStartedDate() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getStartedDate'");
  }

  @Override
  public Date getEndDate() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getEndDate'");
  }
}
