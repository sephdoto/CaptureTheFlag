// TODO Add license if applicable
package de.unimannheim.swt.pse.ctf.game;

import de.unimannheim.swt.pse.ctf.game.exceptions.GameOver;
import de.unimannheim.swt.pse.ctf.game.exceptions.InvalidMove;
import de.unimannheim.swt.pse.ctf.game.exceptions.NoMoreTeamSlots;
import de.unimannheim.swt.pse.ctf.game.map.MapTemplate;
import de.unimannheim.swt.pse.ctf.game.state.GameState;
import de.unimannheim.swt.pse.ctf.game.state.Move;
import de.unimannheim.swt.pse.ctf.game.state.Piece;
import de.unimannheim.swt.pse.ctf.game.state.Team;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javafx.scene.paint.Color;
import org.ctf.shared.state.data.exceptions.TooManyPiecesException;

/**
 * Game Engine Implementation
 *
 * @author ysiebenh & sistumpf & rsyed Mainly bug fixing and code consistency checks by rsyed
 */
public class GameEngine implements Game {

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

  private MapTemplate currentTemplate; // Saves a copy of the template
  private int teamPos = 0;
  private Map<String, Integer> teamIDtoInteger;

  // Blocks for branches in game mode
  // Game state is taking care of all time calculations
  private boolean timeLimitedGame;
  private LocalDateTime gameEndsAt;
  private boolean moveTimeLimitedGame;
  
  
  private LocalDateTime lastMoveTime;
  private LocalDateTime nextMoveTime;

  // **************************************************
  // Public Methods
  // **************************************************

  /**
   * Creates a game session with the corresponding Map passed onto as the Template
   *
   * @author rsyed & ysiebenh & sistumpf
   * @param template
   * @return GameState
   */
  @Override
  public GameState create(MapTemplate template) {
    this.currentTemplate = template; // Saves a copy of the initial template

    this.gameState = new GameState();
    // Inits Grid + Assigns pieces to teams
    BoardSetUp.makeGridandTeams(this.gameState, template);
    // Sets Bases on the Grid
    BoardSetUp.placeBases(this.gameState, template);

    // placing the pieces and blocks

    try {
      BoardSetUp.initPieces(this.gameState, template);
    } catch (
        TooManyPiecesException
            e) { // If too many pieces. Catches the exception and throws UnknownError at the client.
      throw new UnknownError();
    }

    BoardSetUp.initGrid(this.gameState, template);

    BoardSetUp.placeBlocks(template, this.gameState.getGrid(), template.getBlocks());

    this.remainingTeamSlots = template.getTeams() - 1; // Sets counter for remaining teams
    // Setting Flags
    this.isGameOver = false;

    // TODO CODE BLOCK HERE LATER FOR ALT MODE BRANCHES
    setAltGameModes(template);

    // END OF CODE BLOCK FOR BRANCHES
    this.teamIDtoInteger = Collections.synchronizedMap(new HashMap<>());
    // Setting State
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
   * @author rsyed
   * @param teamId Team ID
   * @return Team
   * @throws NoMoreTeamSlots No more team slots available
   */
  @Override
  public Team joinGame(String teamId) {
    // Initial check if Slots are even available
    Team retu = new Team();
    try {
      if (this.getRemainingTeamSlots() < 0) {
        throw new NoMoreTeamSlots();
      } else {
        retu = addTeam(teamId, getRemainingTeamSlots());
        this.remainingTeamSlots = getRemainingTeamSlots() - 1;
        if (getRemainingTeamSlots() < 0) {
          // START THE GAME
          startGame();
        }
      }
    } catch (NoMoreTeamSlots e) {
      throw new NoMoreTeamSlots(); // Throws it further up to the method
    }
    return retu;
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
   * @author rsyed
   * @param teamId Team ID
   * @return Team
   * @throws NoMoreTeamSlots No more team slots available
   */
  /*
  public Team joinGameNew(String teamId) {
    // Initial check if Slots are even available
    Team randomStartTeam = new Team();
    if(getRemainingTeamSlots() < 1){
      throw new NoMoreTeamSlots();
    } else {
      addTeam(teamId, teamPos);  //ADD TEAM TO THE GAME

      this.remainingTeamSlots--; //DECREASE THE NUMBER OF TEAMS
      this.teamPos++;            //Increase the Pos tracker

      if(getRemainingTeamSlots() < 1){     //CHECK SLOTS LEFT
        //CHECK FLAGS FOR ALT MODE

        //SET DATA FOR ALT MODE

        //START THE GAME

        //RETURN RANDOM TEAM

      }
    }
    return randomStartTeam;
  } */

  /**
   * @return number of remaining team slots
   */
  @Override
  public int getRemainingTeamSlots() {
    return remainingTeamSlots;
  }

  /**
   * A team has to option to give up a game (i.e., losing the game as a result). Assume that a team
   * can only give up if it is its move (turn).
   *
   * @author sistumpf
   * @param teamId Team ID
   */
  @Override
  public void giveUp(String teamId) {
    EngineTools.removeTeam(gameState, Integer.valueOf(teamIDtoInteger.get(teamId)));
    // Logic for switching Current Team
    if (gameState.getTeams()[gameState.getCurrentTeam()] == null)
      gameState.setCurrentTeam(EngineTools.getNextTeam(gameState));
    this.gameOverCheck();
  }

  /**
   * Checks whether the game is over based on the current {@link GameState}.
   *
   * @return true if game is over, false if game is still running.
   */
  @Override
  public boolean isGameOver() {
    return this.isGameOver;
  }

  /**
   * Checks whether the game is started based on the current {@link GameState}.
   *
   * <ul>
   *   <li>{@link Game#isGameOver()} == false
   *   <li>{@link Game#getCurrentGameState()} != null
   * </ul>
   *
   * @author rsyed
   * @return boolean
   */
  @Override
  public boolean isStarted() {
    if ((!isGameOver) && (getCurrentGameState() != null)) {
      return true;
    } else {
      return false;
    }
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
   * Get current state of the game
   *
   * @return GameState
   */
  @Override
  public GameState getCurrentGameState() {
    // turnTimeLimitedChecks();

    gameOverCheck();
    if (gameState.getTeams()[gameState.getCurrentTeam()] == null)
      gameState.setCurrentTeam(EngineTools.getNextTeam(gameState));

    return gameState;
  }

  /**
   * Returns the remaining time in seconds.
   *
   * @author sistumpf, rsyed
   * @return -1 if no total game time limit set, 0 if over, > 0 if seconds remain
   */
  @Override
  public int getRemainingGameTimeInSeconds() {
    if (!timeLimitedGame) {
      return -1;
    } else if (isGameOver) {
      return 0;
    }

    Calendar now = Calendar.getInstance();
    Calendar end = Calendar.getInstance();
    end.setTime(endDate);

    if (now.after(end)) return 0;

    int returnTime;
    try {
      returnTime = Math.toIntExact((end.getTimeInMillis() - now.getTimeInMillis()) / 1000);
    } catch (ArithmeticException ae) {
      returnTime = Integer.MAX_VALUE;
    }
    return returnTime;
  }

  /**
   * @author rsyed
   * @return -1 if no move time limit set, 0 if over, > 0 if seconds remain
   */
  @Override
  public int getRemainingMoveTimeInSeconds() {
    if (!timeLimitedGame) {
      return -1;
    } else if (isGameOver) {
      return 0;
    } else {
      LocalDateTime currentTime = LocalDateTime.now();
      return (int) currentTime.until(nextMoveTime, ChronoUnit.SECONDS);
    }
  }

  /**
   * Here a move, if valid, updates the grid, a pieces position, a teams flags and the time. The
   * currentTeam also gets changed here (with afterMoveCleanUp())
   *
   * @author sistumpf
   * @param move {@link Move}
   * @throws InvalidMove Requested move is invalid
   * @throws GameOver Game is over
   */
  @Override
  public void makeMove(Move move) {
    if (!movePreconditionsMet(move)) return;
    String occupant = gameState.getGrid()[move.getNewPosition()[0]][move.getNewPosition()[1]];
    Piece picked =
        Arrays.asList(gameState.getTeams()[gameState.getCurrentTeam()].getPieces()).stream()
            .filter(p -> p.getId().equals(move.getPieceId()))
            .findFirst()
            .get();
    int[] oldPos = picked.getPosition();

    gameState.getGrid()[oldPos[0]][oldPos[1]] = "";

    if (occupant.contains("p:")) {
      int occupantTeam = Integer.parseInt(occupant.split(":")[1].split("_")[0]);
      gameState.getTeams()[occupantTeam].setPieces(
          Arrays.asList(gameState.getTeams()[occupantTeam].getPieces()).stream()
              .filter(p -> !p.getId().equals(occupant))
              .toArray(Piece[]::new));
      gameState.getGrid()[move.getNewPosition()[0]][move.getNewPosition()[1]] = move.getPieceId();
      picked.setPosition(move.getNewPosition());
    } else if (occupant.contains("b:")) {
      int occupantTeam = Integer.parseInt(occupant.split(":")[1].split("_")[0]);
      gameState.getTeams()[occupantTeam].setFlags(
          gameState.getTeams()[occupantTeam].getFlags() - 1);
      picked.setPosition(
          EngineTools.respawnPiecePosition(
              gameState, gameState.getTeams()[gameState.getCurrentTeam()].getBase()));
      gameState.getGrid()[picked.getPosition()[0]][picked.getPosition()[1]] = picked.getId();
    } else {
      gameState.getGrid()[move.getNewPosition()[0]][move.getNewPosition()[1]] = move.getPieceId();
      picked.setPosition(move.getNewPosition());
    }

    gameState.setLastMove(move);

    // Update Time
    if (this.moveTimeLimitedGame) {
      this.lastMoveTime = LocalDateTime.now();
      this.nextMoveTime = lastMoveTime.plusSeconds(currentTemplate.getMoveTimeLimitInSeconds());
    }

    afterMoveCleanup();
  }

  /**
   * The {@link GameEngine#isGameOver()} method only returns the {@link GameEngine#isGameOver}
   * value, so this method implements the game over checks. It updates the isGameOver {@link
   * GameEngine#isGameOver} value accordingly. TODO needs further tests
   *
   * @author sistumpf
   */
  public void gameOverCheck() {
    if (getRemainingGameTimeInSeconds() == 0) { // Time Limited Game Check
      this.isGameOver = true;
      // TODO Calculate the winner of a time limited game

    } else {
      ArrayList<Integer> teamsLeft = new ArrayList<Integer>();
      for (int i = 0; i < gameState.getTeams().length; i++) {
        if (gameState.getTeams()[i] != null) {
          teamsLeft.add(i);
        }
      }

      for (int i = 0; i < teamsLeft.size(); i++) {
        if (teamsLeft.size() == 1) {
          this.isGameOver = true;
          break;
        }
        Team team = gameState.getTeams()[teamsLeft.get(i)];
        if (team.getFlags() < 1) {
          EngineTools.removeTeam(gameState, i--);
          teamsLeft.remove(i--);
        } else if (team.getPieces().length == 0) {
          EngineTools.removeTeam(gameState, i--);
          teamsLeft.remove(i--);
        }
      }

      removeNoMoveTeams(gameState);
    }
    if (this.isGameOver) {
      this.endDate =
          Date.from(
              LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()); // Sets game end time
    }
  }

  /**
   * If the game is over a String Array containing all winner IDs is returned. This method relies on
   * the fact that loser teams get set to null in the gameState.teams Array.
   *
   * @author sistumpf
   * @return {@link Team#getId()} if there is a winner
   */
  @Override
  public String[] getWinner() {
    ArrayList<String> winners = new ArrayList<String>();
    if (this.isGameOver) {
      for (Team team : this.gameState.getTeams()) {
        if (team != null) {
          winners.add(team.getId());
        }
      }
    }
    return winners.toArray(new String[winners.size()]);
  }

  /**
   * @return Start {@link Date} of game
   */
  @Override
  public Date getStartedDate() {
    return startedDate;
  }

  /**
   * @return End date of game
   */
  @Override
  public Date getEndDate() {
    return this.endDate;
  }

  // **************************************************
  // Private Methods
  // **************************************************

  /**
   * Call this after making a move. Timer gets updated, gameOverChecks are made.
   *
   * @author sistumpf, rsyed
   */
  private void afterMoveCleanup() {
    // Update Time
    if (this.moveTimeLimitedGame) {
      this.lastMoveTime = LocalDateTime.now();
    }

    gameState.setCurrentTeam(EngineTools.getNextTeam(gameState));
    gameOverCheck();
    if (gameState.getTeams()[gameState.getCurrentTeam()] == null)
      gameState.setCurrentTeam(EngineTools.getNextTeam(gameState));
  }
  
  private boolean movePreconditionsMet(Move move) {
    if(isGameOver()){
      throw new GameOver();
    }
    if (isGameOver() || !isTurn(move)) {
      return false;
    } else if (!isValidMove(move)){
      throw new InvalidMove();
    }
    return true;
  }


  /**
   * Helper method to perform a turn check. So that players cannot make out of turn moves.
   *
   * @author rsyed
   * @param move To extract the team from the piece being moved
   * @return boolean
   */
  private boolean isTurn(Move move) {
    int moveTeamIdentifier = Integer.parseInt(move.getPieceId().split(":")[1].split("_")[0]);
    return (moveTeamIdentifier == gameState.getCurrentTeam());
  }

  /**
   * This method checks if the current team got moves left, if thats not the case the team get
   * removed. After a team got removed the next team gets checked, until only 1 team is left or a
   * team got moves. TODO currently just tested in MCTS, no guarantee that it works
   *
   * @author sistumpf
   * @param gameState
   */
  void removeNoMoveTeams(GameState gameState) {
    int teamsLeft = 0;
    for (int i = 0; i < gameState.getTeams().length; i++) {
      if (gameState.getTeams()[i] != null) {
        teamsLeft++;
      }
    }

    for (int i = gameState.getCurrentTeam();
        teamsLeft > 1;
        i = EngineTools.getNextTeam(gameState)) {
      boolean canMove = false;
      for (int j = 0; !canMove && j < gameState.getTeams()[i].getPieces().length; j++) {
        // if there are possible moves
        if (EngineTools.getPossibleMoves(gameState, gameState.getTeams()[i].getPieces()[j].getId())
                .size()
            > 0) {
          canMove = true;
        }
      }
      if (canMove) {
        return;
      } else if (!canMove) {
        EngineTools.removeTeam(gameState, i);
        teamsLeft--;
      }
    }

    if (teamsLeft <= 1) isGameOver = true;
  }

  /**
   * Helper method to Start the game
   *
   * @author rsyed
   */
  private void startGame() {
    gameState.setCurrentTeam(0);
    // TODO Fix the problem with randomly selecting a starting team
    // gameState.setCurrentTeam(
    // (int) (Math.random() * currentTemplate.getTeams())); // Sets the starting team randomly

    // Sets the TimeStamp for when the game started
    if (this.timeLimitedGame) {
      this.startedDate = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());

      // Sets the TimeStamp for when the game should end
      this.gameEndsAt =
          LocalDateTime.now().plusSeconds(currentTemplate.getTotalTimeLimitInSeconds());
    }
    if (this.moveTimeLimitedGame) {
      this.lastMoveTime = LocalDateTime.now();
      this.nextMoveTime = lastMoveTime.plusSeconds(currentTemplate.getMoveTimeLimitInSeconds());
    }
  }

  /**
   * Helper method to add a team to the gameState. It takes in a String which it uses to create a
   * team, also needs a position to add the team at. Also sets a random color to the team
   *
   * @author rsyed
   * @param teamID Name of the team
   * @param int position to add it in
   */
  private Team addTeam(String teamID, int pos) {
    this.teamIDtoInteger.put(teamID, pos);
    Team[] team = gameState.getTeams();
    team[pos].setId(teamID);
    team[pos].setColor(getRandColor());
    gameState.setTeams(team);
    return team[pos];
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

  /**
   * Helper method to set flags for Alternate Game modes
   *
   * @author rsyed
   */
  private void setAltGameModes(MapTemplate template) {
    if (template.getTotalTimeLimitInSeconds() != -1) {
      this.timeLimitedGame = true;
    } else {
      this.timeLimitedGame = false;
    }
    if (template.getMoveTimeLimitInSeconds() != -1) {
      this.moveTimeLimitedGame = true;
    } else {
      this.moveTimeLimitedGame = false;
    }
  }

  /**
   * Helper method to change whose turn it is if time runs out
   *
   * @author rsyed
   */
  private void turnTimeLimitedChecks() {
    if (getRemainingMoveTimeInSeconds() < 0) {
      gameState.setCurrentTeam(EngineTools.getNextTeam(gameState));
    }
  }

  // **************************************************
  // Simons Special Methods
  // **************************************************

  /**
   * TODO Test Konstruktor von Simon Kann entfernt werden wenn das Generieren von GameStates
   * funktioniert, wird in der Test Klasse gebraucht.
   */
  public GameEngine(GameState gameState, boolean isGameOver, boolean withTimeLimit, Date endDate) {
    this.gameState = gameState;
    this.isGameOver = isGameOver;
    this.startedDate = new Date(System.currentTimeMillis());
    this.endDate = endDate;
    this.timeLimitedGame = withTimeLimit;
    if (withTimeLimit) {
      this.lastMoveTime = LocalDateTime.now();
      this.nextMoveTime = lastMoveTime.plusSeconds(currentTemplate.getMoveTimeLimitInSeconds());
    }
  }

  /**
   * TODO Test Konstruktor von Simon Kann entfernt werden wenn das Generieren von GameStates
   * funktioniert, wird in der Test Klasse gebraucht.
   */
  public GameEngine(
      GameState gameState,
      MapTemplate mt,
      boolean isGameOver,
      boolean withTimeLimit,
      Date endDate) {
    this.gameState = gameState;
    this.currentTemplate = mt;
    this.isGameOver = isGameOver;
    this.startedDate = new Date(System.currentTimeMillis());
    this.endDate = endDate;
    this.timeLimitedGame = withTimeLimit;
    if (withTimeLimit) {
      this.lastMoveTime = LocalDateTime.now();
      this.nextMoveTime = lastMoveTime.plusSeconds(currentTemplate.getMoveTimeLimitInSeconds());
    }
  }

  /**
   * TODO Default Konstruktor von Simon Kann entfernt werden wenn das Generieren von GameStates
   * funktioniert, wird in der Test Klasse gebraucht.
   */
  public GameEngine() {
    this.startedDate = new Date(System.currentTimeMillis());
    this.timeLimitedGame = false;
  }
}
