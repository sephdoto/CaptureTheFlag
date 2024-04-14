package de.unimannheim.swt.pse.ctf.game;

import de.unimannheim.swt.pse.ctf.game.map.MapTemplate;
import de.unimannheim.swt.pse.ctf.game.state.GameState;
import de.unimannheim.swt.pse.ctf.game.state.Move;
import de.unimannheim.swt.pse.ctf.game.state.Team;
import java.util.ArrayList;
import java.util.Date;

public class NewGameEngine implements Game {

  // **************************************************
  // Fields
  // **************************************************

  // **************************************************
  // Required by GameEngine
  // **************************************************
  private GameState gameState; // MAIN Data Store for GameEngine
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
  public Team joinGame(String teamId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'joinGame'");
  }


  @Override
  public void makeMove(Move move) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'makeMove'");
  }

  @Override
  public void giveUp(String teamId) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'giveUp'");
  }

  @Override
  public boolean isValidMove(Move move) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method isValidMove");
  }

  /**
   * Checks whether the game is over based on the current {@link GameState}.
   *
   * @author rsyed
   * @return true if game is over, false if game is still running.
   */
  @Override
  public boolean isStarted() {
    if (isGameOver() && (getCurrentGameState() != null)) {
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean isGameOver() {
    throw new UnsupportedOperationException("Unimplemented method 'isGameOver'");
  }

  /**
   * If the game is over a String Array containing all winner IDs is returned. This method relies on
   * the fact that loser teams get set to null in the gameState.teams Array.
   *
   * @author Code: sistumpf
   * @author Fixes: rsyed
   * @return {@link Team#getId()} if there is a winner
   */
  @Override
  public String[] getWinner() {
    ArrayList<String> winners = new ArrayList<String>();
    if (this.isGameOver()) {
      for (Team team : this.gameState.getTeams()) {
        if (team != null) {
          winners.add(team.getId());
        }
      }
    }
    return winners.toArray(new String[winners.size()]);
  }

  /**
   * Simple Getter for the date the Game Started On
   *
   * @author rsyed
   * @return Start {@link Date} of game
   */
  @Override
  public Date getStartedDate() {
    return startedDate;
  }

  /**
   * Simple Getter for the date the game Ended on
   *
   * @author rsyed
   * @return End date of game
   */
  @Override
  public Date getEndDate() {
    return this.endDate;
  }

  /**
   * Simple Getter for the GameState Object
   *
   * @author rsyed
   * @return Current {@link GameState} of the Session
   */
  @Override
  public GameState getCurrentGameState() {
    return this.gameState;
  }

  /**
   * Checks how many empty objects are left in the Team[] in the gameState
   * 
   * @author rsyed
   * @return number of remaining team slots
   */
  @Override
  public int getRemainingTeamSlots() {
    Team[] currentTeamObject = gameState.getTeams();
    int counter = 0;
    for (Team t : currentTeamObject) {
      if (t == null) {
        counter++;
      }
    }
    return counter;
  }

  // **************************************************
  // Alt Game Mode Methods
  // **************************************************

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

  // **************************************************
  // End of Alt Game Mode Methods
  // **************************************************

}
