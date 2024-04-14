package de.unimannheim.swt.pse.ctf.game;

import de.unimannheim.swt.pse.ctf.game.map.MapTemplate;
import de.unimannheim.swt.pse.ctf.game.state.GameState;
import de.unimannheim.swt.pse.ctf.game.state.Move;
import de.unimannheim.swt.pse.ctf.game.state.Team;
import java.time.Clock;
import java.time.Duration;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  private Clock currentTime;
  
  // **************************************************
  // End of Required by GameState
  // **************************************************

  // **************************************************
  // Nice to haves
  // **************************************************
  private MapTemplate copyOfTemplate; // Saves a copy of the template
  private static final Logger LOG = LoggerFactory.getLogger(NewGameEngine.class);
  private boolean TeamsAreFull;

  // **************************************************
  // END of Nice to haves
  // **************************************************

  // **************************************************
  // Alt Mode Data : Never Asked for directly: Internal use Vars
  // **************************************************
  private boolean timeLimitedGameTrigger;
  private boolean moveTimeLimitedGameTrigger;

  private Clock gameShouldEndBy;
  private Duration totalGameTime;
  private Clock turnEndsBy;
  private Duration turnTime;
  private int graceTime;      //Time added to be fair for processing delays
  // **************************************************
  // END of Alt Mode Data
  // **************************************************

  @Override
  public GameState create(MapTemplate template) {
    this.copyOfTemplate = template; // Template Copy Box

    initAltGameModeLogic(template); // Inits Alt Game mode support
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
   * Checks whether the Game SESSION is started based on the current {@link GameState}.
   *
   * @author rsyed
   * @return true if game is started, false is over
   */
  @Override
  public boolean isStarted() {
    if (isGameOver() && (getCurrentGameState() != null)) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Checks whether the game is over based on the current {@link GameState}.
   *
   * @author rsyed
   * @return true if game is over, false if game is still running.
   */
  @Override
  public boolean isGameOver() {
    if (getEndDate() != null) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * If the game is over a String Array containing all winner IDs is returned. This method relies on
   * the fact that loser teams get set to null in the gameState.teams Array.
   *
   * @author Code: sistumpf
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
    int counter = 0;
    for (Team t : gameState.getTeams()) {
      if (t == null) {
        counter++;
      }
    }
    return counter;
  }

  // **************************************************
  // Alt Game Mode Methods
  // **************************************************

  /**
   * Helper method to check if alt game modes are set and to start logic accordingly
   *
   * @author rsyed
   */
  private void initAltGameModeLogic(MapTemplate template) {
    if ((template.getMoveTimeLimitInSeconds() != -1)
        || (template.getTotalTimeLimitInSeconds() != -1)) { // If flags are set
      this.currentTime = Clock.systemDefaultZone(); // Start BaseClock
      if (template.getTotalTimeLimitInSeconds() != -1) {
        this.timeLimitedGameTrigger = true;
        this.totalGameTime = Duration.ofSeconds(copyOfTemplate.getTotalTimeLimitInSeconds() + graceTime);
        timeLimitedHandler();
      }
      if (template.getMoveTimeLimitInSeconds() != -1) {
        this.moveTimeLimitedGameTrigger = true;
        this.turnTime = Duration.ofSeconds(copyOfTemplate.getMoveTimeLimitInSeconds() + graceTime);
        moveTimeLimitedHander();
      }
    } else {
      this.timeLimitedGameTrigger = false;
      this.moveTimeLimitedGameTrigger = false;
    }
  }

  /**
   * Checks how much time is left for the game mode
   *
   * @author rsyed
   * @return -1 if no total game time limit set, 0 if over, > 0 if seconds remain
   */
  @Override
  public int getRemainingGameTimeInSeconds() {
    if (copyOfTemplate.getTotalTimeLimitInSeconds() == -1) {
      return -1;
    }
    if (isGameOver()) {
      return 0;
    } else {
      return Math.toIntExact(
          Duration.between(currentTime.instant(), gameShouldEndBy.instant()).getSeconds());
    }
  }

  /**
   * Handler which should be called incase the Game is a TimeLimited Game
   *
   * @author rsyed
   */
  public void timeLimitedHandler() {
    Thread timeLimitedThread =
        new Thread(
            () -> {
              boolean setOnceTrigger = true;
              while (timeLimitedGameTrigger) {
                if (TeamsAreFull) {
                  if (setOnceTrigger) {
                    setWhenGameShouldEnd();
                    setOnceTrigger = false;
                  }
                  // Checks if Clock says its past game end time
                  if (currentTime.instant().isAfter(gameShouldEndBy.instant())) {
                    gameOverHandler(); // Calls the Handler incase game has to end
                    timeLimitedGameTrigger = false; // Ends the Thread to reclaim resources
                  }
                }
                try { // Checks EVERY 1 second
                  // TODO Discuss if a check every second is okay or we need faster ones
                  Thread.sleep(1000);
                } catch (InterruptedException e) {
                  LOG.info("Exception Occured in timeLimitedHandler thread");
                }
              }
            });
    timeLimitedThread.run();
  }

  /**
   * @author rsyed
   * @return -1 if no move time limit set, 0 if over, > 0 if seconds remain
   */
  @Override
  public int getRemainingMoveTimeInSeconds() {
    if (copyOfTemplate.getMoveTimeLimitInSeconds() == -1) {
      return -1;
    }
    if (isGameOver()) {
      return 0;
    } else {
      return Math.toIntExact(
          Duration.between(currentTime.instant(), turnEndsBy.instant()).getSeconds());
    }
  }

  /**
   * Handler which should be called incase the moves are time limited in the game While the trigger
   * is active AND Teams are full, Checks if current time is after the time the turn should end. If
   * so..Switches Current Team to the next available team and resets the time
   *
   * @author rsyed
   */
  public void moveTimeLimitedHander() {
    Thread moveLimitedThread =
        new Thread(
            () -> {
              boolean setOnceTrigger = true;
              while (moveTimeLimitedGameTrigger) {
                if (TeamsAreFull) { // If teams are full
                  if (setOnceTrigger) {
                    increaseTurnTimer();      // Block for increasing the timer ONCE for the first turn
                    setOnceTrigger = false;
                  }
                  if (currentTime.instant().isAfter(turnEndsBy.instant())) {
                    // TODO ASK SIMON FOR CLARIFICATION ON HOW TO DO BEST DO THIS
                    // TODO SWTICH THE CURRENT TEAM TO THE NEXT TEAM

                  
                    increaseTurnTimer();  // UPDATES when the next turn should end
                  }
                  if (isGameOver()) { // Checks if game is over
                    moveTimeLimitedGameTrigger = false; // Ends the thread if game is over
                  }
                }
              }
              try { // Checks EVERY quater second
                // TODO Discuss if this is okay or we need faster ones
                Thread.sleep(250);
              } catch (InterruptedException e) {
                LOG.info("Exception Occured in moveTimeLimitedHander thread");
              }
            });
    moveLimitedThread.run();
  }

  private void increaseTurnTimer() {
    this.turnEndsBy =
        Clock.fixed(Clock.offset(currentTime, turnTime).instant(), ZoneId.systemDefault());
  }

  private void setWhenGameShouldEnd() {
    this.gameShouldEndBy =
        Clock.fixed(Clock.offset(currentTime, totalGameTime).instant(), ZoneId.systemDefault());
  }

  // **************************************************
  // End of Alt Game Mode Methods
  // **************************************************

  // **************************************************
  // Private Internal Methods
  // **************************************************

  /**
   * Method which will take care of ending the game for ALT MODES
   *
   * @author rsyed
   */
  // TODO Write Handler
  private void gameOverHandler() {
    // CASE CALLED FROM LIMITED GAME TIME GAMEOVER HANDLER

    // CASE CALLED FROM LIMITED MOVE TIME HANDLER
  }

  // **************************************************
  // End of Private Internal Methods
  // **************************************************
}
