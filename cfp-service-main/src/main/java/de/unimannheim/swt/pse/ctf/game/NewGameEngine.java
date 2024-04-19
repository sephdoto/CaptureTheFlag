package de.unimannheim.swt.pse.ctf.game;

import de.unimannheim.swt.pse.ctf.game.exceptions.GameOver;
import de.unimannheim.swt.pse.ctf.game.exceptions.InvalidMove;
import de.unimannheim.swt.pse.ctf.game.exceptions.NoMoreTeamSlots;
import de.unimannheim.swt.pse.ctf.game.exceptions.TooManyPiecesException;
import de.unimannheim.swt.pse.ctf.game.map.MapTemplate;
import de.unimannheim.swt.pse.ctf.game.state.GameState;
import de.unimannheim.swt.pse.ctf.game.state.Move;
import de.unimannheim.swt.pse.ctf.game.state.Team;
import java.time.Clock;
import java.time.Duration;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javafx.scene.paint.Color;
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
  private Date startedDate = null;
  private Date endDate; // Setting this Ends the game
  private Clock currentTime;
  private Map<Integer, String> integerToTeam;
  private Map<String, Integer> teamToInteger;

  // **************************************************
  // End of Required by GameState
  // **************************************************

  // **************************************************
  // Nice to haves
  // **************************************************
  private MapTemplate copyOfTemplate; // Saves a copy of the template
  private static final Logger LOG = LoggerFactory.getLogger(NewGameEngine.class);

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
  private int graceTime = 1; // Time added to be fair for processing delays

  // **************************************************
  // END of Alt Mode Data
  // **************************************************

  @Override
  public GameState create(MapTemplate template) {
    this.copyOfTemplate = template; // Template Copy Box
    this.integerToTeam = Collections.synchronizedMap(new HashMap<>());
    this.teamToInteger = Collections.synchronizedMap(new HashMap<>());
    gameState = new GameState();
    BoardController boardController = new BoardController();

    gameState.setGrid(
        boardController.initEmptyGrid(
            template.getGridSize()[0], template.getGridSize()[1])); // Makes an Empty Grid

    gameState.setTeams(new Team[template.getTeams()]);
    initAltGameModeLogic(template); // Inits Alt Game mode support

    return gameState;
  }

  /**
   * Updates a game and its state based on team join request (add team).
   *
   * <ul>
   *   <li>adds team if a slot is free (array element is null)
   *   <li>if all team slots are finally assigned, implicitly starts the game by picking a starting
   *       team at random
   * </ul>
   *
   * @param teamId Team ID
   * @return Team
   * @throws NoMoreTeamSlots No more team slots available
   */
  @Override
  public Team joinGame(String teamId) {
    if (getRemainingTeamSlots() == 0) {
      throw new NoMoreTeamSlots();
    }
    BoardController boardController = new BoardController();
    int slot = EngineTools.getNextEmptyTeamSlot(this.gameState);
    Team tempTeam = boardController.initializeTeam(slot, copyOfTemplate);
    // Method above Sets Flags, Pieces in the Team object

    teamToInteger.put(
        teamId, slot); // initial save in two dictionaries.I know its stupid but we can remove one
    integerToTeam.put(slot, teamId);

    // **************************************************
    // Init the Game State here with the Team
    // **************************************************
    // TODO Base, Pieces, Flag
    // test code

    // **************************************************
    // End of Init the Game State
    // **************************************************

    this.gameState.getTeams()[slot] = tempTeam; // places the team in the slot its supposed to go in

    // **************************************************
    // Slot Full Check
    // **************************************************
    // TODO this method below ALSO has to set the map in the game state with all the pieces, blocks,
    // bases etc on the map! Code needs to be finished
    canWeStartTheGameUwU(); // Method also checks for alt game logic

    // **************************************************
    // End of is Slot Full Check
    // **************************************************

    return tempTeam;
  }

  /**
   * Here a move, if valid, updates the grid, a pieces position, a teams flags and the time.
   *
   * @author sistumpf
   * @param move {@link Move}
   * @throws InvalidMove Requested move is invalid
   * @throws GameOver Game is over
   */
  @Override
  public void makeMove(Move move) {
    if (!movePreconditionsMet(move)) return;
    EngineTools.computeMove(this.gameState, move);
    afterMoveCleanup();
  }

  /**
   * A team has to option to give up a game (i.e., losing the game as a result). Assume that a team
   * can only give up if it is its move (turn). If the next teams got no moves left they also get
   * removed. If only one team is left the game ends.
   *
   * @author sistumpf
   * @param teamId Team ID
   */
  @Override
  public void giveUp(String teamId) {
    if (teamToInteger.get(teamId)
        == this.gameState
            .getCurrentTeam()) { // test is also in controller but doppelt gemoppelt hält besser
      EngineTools.removeTeam(gameState, teamToInteger.get(teamId)); // removed and set to null
      this.gameState.setCurrentTeam(EngineTools.getNextTeam(gameState));
    }
    if (EngineTools.removeMovelessTeams(this.gameState)) setGameOver();
  }

  /**
   * Checks whether a move is valid based on the current game state.
   *
   * @author sistumpf
   * @param move {@link Move}
   * @return true if move is valid based on current game state, false otherwise
   */
  @Override
  public boolean isValidMove(Move move) {
    if (isStarted()) {
      return EngineTools.getPossibleMoves(this.gameState, move.getPieceId()).stream()
          .anyMatch(i -> i[0] == move.getNewPosition()[0] && i[1] == move.getNewPosition()[1]);
    }
    return false;
  }

  /**
   * Checks whether the Game SESSION is started based on the current {@link GameState}.
   *
   * @author rsyed
   * @return true if game is started, false is over
   */
  @Override
  public boolean isStarted() {
    return (!isGameOver() && (getStartedDate() != null));
  }

  /**
   * Checks whether the game is over based on the current {@link GameState}.
   *
   * @author rsyed
   * @return true if game is over, false if game is still running.
   */
  @Override
  public boolean isGameOver() {
    return (getEndDate() != null);
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
    LOG.info("get Winner was called");
    ArrayList<String> winners = new ArrayList<String>();
    if (this.isGameOver()) {
      for (Team team : this.gameState.getTeams()) {
        if (team != null) {
          winners.add(integerToTeam.get(Integer.parseInt(team.getId())));
          LOG.info("Winner was " + integerToTeam.get(Integer.parseInt(team.getId())));
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
   * Method to call from create method while parsing {@link GameTemplate} and inits some prereqs for
   * the Alt Game Mode.
   *
   * @author rsyed
   */
  private void initAltGameModeLogic(MapTemplate template) {
    if ((template.getMoveTimeLimitInSeconds() != -1)
        || (template.getTotalTimeLimitInSeconds() != -1)) {
      this.currentTime = Clock.systemDefaultZone(); // Start BaseClock
      if (template.getMoveTimeLimitInSeconds() != -1) {
        this.moveTimeLimitedGameTrigger = true;
      }
      if (template.getTotalTimeLimitInSeconds() != -1) {
        this.timeLimitedGameTrigger = true;
      }
    } else {
      this.timeLimitedGameTrigger = false;
      this.moveTimeLimitedGameTrigger = false;
    }
  }

  /**
   * Main CONTROLLER for Alt Game Modes Call when Game Starts (teams are full)
   *
   * @author rsyed
   */
  private void startAltGameController() {
    if (moveTimeLimitedGameTrigger) {
      startMoveTimeLimitedGame();
    }
    if (timeLimitedGameTrigger) {
      startTimeLimitedGame();
    }
  }

  /**
   * Helper method to start logic for TimeLimitedGame.
   *
   * @author rsyed
   */
  private void startTimeLimitedGame() {
    this.totalGameTime =
        Duration.ofSeconds(copyOfTemplate.getTotalTimeLimitInSeconds() + graceTime);
    timeLimitedHandler();
  }

  /**
   * Helper method to start logic for MoveTimeLimitedGame. Do not call
   *
   * @author rsyed
   */
  private void startMoveTimeLimitedGame() {
    this.turnTime = Duration.ofSeconds(copyOfTemplate.getMoveTimeLimitInSeconds() + graceTime);
    moveTimeLimitedHander();
  }

  /**
   * Checks how much time is left for the game
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
                if (isStarted()) {
                  if (setOnceTrigger) {
                    setWhenGameShouldEnd();
                    setOnceTrigger = false;
                  }
                  // Checks if Clock says its past game end time
                  if (currentTime.instant().isAfter(gameShouldEndBy.instant())) {
                    altGameModeGameOverHandler(); // Calls the Handler incase game has to end
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
    timeLimitedThread.start();
  }

  /**
   * Method returns how many seconds are left in the turn
   *
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
                if (isStarted()) { // If teams are full
                  if (setOnceTrigger) {
                    increaseTurnTimer(); // Block for increasing the timer ONCE for the first turn
                    setOnceTrigger = false;
                  }
                  if (currentTime.instant().isAfter(turnEndsBy.instant())) {
                    this.gameState.setCurrentTeam(EngineTools.getNextTeam(this.gameState));
                    increaseTurnTimer(); // UPDATES when the next turn should end
                  }
                  if (isGameOver()) { // Checks if game is over
                    moveTimeLimitedGameTrigger = false; // Ends the thread if game is over
                  }
                  try {
                    Thread.sleep(160);
                  } catch (InterruptedException e) {
                    LOG.info("Exception Occured in moveTimeLimitedHander thread");
                  }
                }
              }
            });
    moveLimitedThread.start();
  }

  /**
   * Part of turn Time limited Game logic Method to increase the timer. Can be used when a turn has
   * been made. Also called when turn timer expires to set timer for next move Call when turn time
   * expires (already done) TODO this method when flag is set and a move is made
   *
   * @author rsyed
   */
  private void increaseTurnTimer() {
    this.turnEndsBy =
        Clock.fixed(Clock.offset(currentTime, turnTime).instant(), ZoneId.systemDefault());
  }

  /**
   * Part of time limited game Logic. Method called to set the time when the game should end. Auto
   * Set by the handler. Call when handler inits (already done)
   *
   * @author rsyed
   */
  private void setWhenGameShouldEnd() {
    this.gameShouldEndBy =
        Clock.fixed(Clock.offset(currentTime, totalGameTime).instant(), ZoneId.systemDefault());
  }

  /**
   * Method which will take care of ending the game for Time limited Alt Mode
   *
   * @author sistumpf
   */
  private void altGameModeGameOverHandler() {
    ArrayList<Team> teamList = new ArrayList<Team>();
    Stream.of(this.gameState.getTeams())
        .forEach(
            team -> {
              if (team != null) teamList.add(team);
            });
    teamList.sort(
        new Comparator<Team>() {
          public int compare(Team team1, Team team2) {
            return team2.getPieces().length - team1.getPieces().length;
          }
        });
    teamList.removeIf(
        new Predicate<Team>() {
          public boolean test(Team team) {
            return teamList.get(0).getPieces().length > team.getPieces().length;
          }
        });
    this.gameState.setTeams(teamList.toArray(new Team[teamList.size()]));
    setGameOver();
  }

  // **************************************************
  // End of Alt Game Mode Methods
  // **************************************************

  // **************************************************
  // Private Internal Methods
  // **************************************************

  /**
   * Cleans up the GameState after a move was made. Updates the timer if the game uses time limits,
   * Updates the current team and removes it if it got no moves left. Repeats the second step till
   * only 1 team is left or the current team got moves.
   *
   * @author sistumpf
   */
  private void afterMoveCleanup() {
    if (this.moveTimeLimitedGameTrigger) increaseTurnTimer();
    gameState.setCurrentTeam(EngineTools.getNextTeam(gameState));
    if (EngineTools.removeMovelessTeams(gameState)) setGameOver();
  }

  /**
   * Returns true if a move is valid. The move is valid if * the game is not over * the piece
   * belongs to the current team * the move complies with the rules
   *
   * @author sistumpf
   * @param move
   * @return true if the move is valid
   */
  private boolean movePreconditionsMet(Move move) {
    if (isGameOver()) {
      throw new GameOver();
    } else if (gameState.getCurrentTeam()
        != Integer.parseInt(move.getPieceId().split(":")[1].split("_")[0])) {
      throw new InvalidMove();
    } else if (!isValidMove(move)) {
      throw new InvalidMove();
    } else {
      return true;
    }
  }

  /**
   * Ends the game INTERNALLY by setting the endDate Variable
   *
   * @author rsyed
   */
  private void setGameOver() {
    this.gameState.setCurrentTeam(-1);  //Sets current team to -1 to indicate game has ended
    this.endDate = new Date();
  }

  /**
   * Checks how many teams are still standing in the Team Array 0 if no teams left standing
   *
   * @author rsyed
   * @return number of remaining team slots
   */
  private int teamsLeft() {
    int counter = copyOfTemplate.getTeams();
    for (Team t : gameState.getTeams()) {
      if (t != null) {
        counter--;
      }
    }
    return counter;
  }

  /**
   * Checks if we can start the game. Call this after adding a team to the game
   *
   * @author rsyed
   */
  private void canWeStartTheGameUwU() {
    if (getRemainingTeamSlots() == 0) {

      // **************************************************
      // Make the game Grid
      // **************************************************

      BoardController boardController = new BoardController();
      boardController.placeBases(
          this.gameState, copyOfTemplate); // Places and inits teams properly on the map
      BoardSetUp.placeBlocks(
          copyOfTemplate,
          this.gameState.getGrid(),
          copyOfTemplate.getBlocks()); // Places blocks....tested
      try {
        BoardSetUp.initPieces(this.gameState, copyOfTemplate); // Inits pieces on the grid
      } catch (TooManyPiecesException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      // **************************************************
      // End of making the game Grid
      // **************************************************

      setRandomStartingTeam();
      startAltGameController();
      this.startedDate = new Date();
    }
  }

  /**
   * Sets a random team as starting team. From 0 to n
   *
   * @author rsyed
   */
  private void setRandomStartingTeam() {
    this.gameState.setCurrentTeam((int) (Math.random() * teamsLeft()));
  }

  /**
   * Helper method returns HEX Codes for colors
   *
   * @author rsyed
   * @return
   */
  static String getRandColor() {
    Random rand = new Random();
    int r = rand.nextInt(255);
    int g = rand.nextInt(255);
    int b = rand.nextInt(255);
    Color testColor = Color.rgb(r, g, b);
    return testColor.toString();
  }
  // **************************************************
  // End of Private Internal Methods
  // **************************************************
}