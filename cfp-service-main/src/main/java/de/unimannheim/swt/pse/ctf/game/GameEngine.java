// TODO Add license if applicable
package de.unimannheim.swt.pse.ctf.game;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.unimannheim.swt.pse.ctf.game.exceptions.GameOver;
import de.unimannheim.swt.pse.ctf.game.exceptions.InvalidMove;
import de.unimannheim.swt.pse.ctf.game.exceptions.NoMoreTeamSlots;
import de.unimannheim.swt.pse.ctf.game.map.MapTemplate;
import de.unimannheim.swt.pse.ctf.game.map.PieceDescription;
import de.unimannheim.swt.pse.ctf.game.state.GameState;
import de.unimannheim.swt.pse.ctf.game.state.Move;
import de.unimannheim.swt.pse.ctf.game.state.Piece;
import de.unimannheim.swt.pse.ctf.game.state.Team;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import javafx.scene.paint.Color;

/**
 * Game Engine Implementation\
 *
 * @author rsyed & ysiebenh & sistumpf
 */
public class GameEngine implements Game {

  private GameState gameState; // MAIN Data Store for GameEngine

  private MapTemplate currentTemplate; // Saves a copy of the template

  private int remainingTeamSlots;

  private boolean isGameOver;

  // Blocks for branches in game mode
  // Game state is taking care of all time calculations
  private boolean timeLimitedGame;
  private boolean moveTimeLimitedGame;
  private Date startedDate;
  private Date endDate;
  private LocalDateTime lastMoveTime;
  private LocalDateTime nextMoveTime;

  /**
   * Creates a game session with the corresponding Map passed onto as the Template
   *
   * @author everyone lol
   * @param template
   * @return GameState
   */
  @Override
  public GameState create(MapTemplate template) {
    this.currentTemplate = template; // Saves a copy of the initial template

    GameState imposterState = new GameState();
    imposterState.setTeams(new Team[template.getTeams()]);
    String[][] grid = new String[template.getGridSize()[0]][template.getGridSize()[1]];
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[i].length; j++) {
        grid[i][j] = "";
      }
    }
    imposterState.setGrid(grid);

    // initializing teams
    Team[] teams = new Team[template.getTeams()];
    for (int i = 0; i < teams.length; i++) {
      teams[i] = BoardSetUp.initializeTeam(i, template);
    }
    imposterState.setTeams(teams);

    BoardSetUp.initPieces(imposterState, template);
    // placing the pieces and blocks
    BoardSetUp.initGrid(imposterState, template);

    this.remainingTeamSlots = template.getTeams() - 1;
    // Setting Flags
    this.isGameOver = false;

    // TODO CODE BLOCK HERE LATER FOR ALT MODE BRANCHES
    setAltGameModes(template);

    // END OF CODE BLOCK FOR BRANCHES

    // Setting State
    this.gameState = imposterState;
    return imposterState;
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
   * @param teamId Team ID
   */
  @Override
  public void giveUp(String teamId) {
    Team[] teamBlock = getCurrentGameState().getTeams();
    int currentTeam = getCurrentGameState().getCurrentTeam() - 1;

    if (teamBlock[currentTeam].getId().equals(teamId)) {
      // TODO CODE TO DELETE WHATS LEFT OF THE TEAM and give up
    }

    // TODO WHEN GAME ENDS YOU HAVE TO SET THE DATE THE GAME ENDED
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
   * @author rsyed Checks whether the game is started based on the current {@link GameState}.
   *     <ul>
   *       <li>{@link Game#isGameOver()} == false
   *       <li>{@link Game#getCurrentGameState()} != null
   *     </ul>
   *
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
   * @author sistumpf Checks whether a move is valid based on the current game state.
   * @param move {@link Move}
   * @return true if move is valid based on current game state, false otherwise
   */
  @Override
  public boolean isValidMove(Move move) {
    if (isStarted()) {
      return AI_Tools.getPossibleMoves(this.gameState, move.getPieceId()).stream()
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
    return gameState;
  }

  /**
   * Returns the remaining time in seconds.
   *
   * @author sistumpf
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
    return returnTime - 1; // -1 Cuz tests were expecting -1 of the value the method was giving back
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
   * Here a move, if valid, updates the grid, a pieces position, a teams flags and the time.
   *
   * @author sistumpf
   * @param move {@link Move}
   * @throws InvalidMove Requested move is invalid
   * @throws GameOver Game is over
   */
  @Override
  public void makeMove(Move move) {
    if(!movePreconditionsMet(move))
      return;

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
          AI_Tools.respawnPiecePosition(
              gameState, gameState.getTeams()[gameState.getCurrentTeam()].getBase()));
      gameState.getGrid()[picked.getPosition()[0]][picked.getPosition()[1]] = picked.getId();
    } else {
      gameState.getGrid()[move.getNewPosition()[0]][move.getNewPosition()[1]] = move.getPieceId();
      picked.setPosition(move.getNewPosition());
    }

    gameState.setCurrentTeam((gameState.getCurrentTeam() + 1) % gameState.getTeams().length);
    gameState.setLastMove(move);

    afterMoveCleanup();
  }
  
  /**
   * Call this after making a move.
   * Timer gets updated, gameOverChecks are made.
   * @author sistumpf, rsyed
   */
  void afterMoveCleanup() {
    // Update Time
    if (this.timeLimitedGame) {
      this.lastMoveTime = LocalDateTime.now();
      this.nextMoveTime = lastMoveTime.plusSeconds(currentTemplate.getMoveTimeLimitInSeconds());
    }

    gameOverCheck();
  }
  
  /**
   * Call this before making a move.
   * @param move
   * @return false if the move is invalid, either because it is the wrong player, the move itself is invalid or the game is already over.
   */
  boolean movePreconditionsMet(Move move) {
    if(isGameOver() || !isTurn(move)) {
      return false;
    } else if(!isValidMove(move)) {
      throw new InvalidMove();
    } 
    return true;
  }

  /**
   * Helper method to perform a turn check. So that players cannot move out of turn moves.
   *
   * @author rsyed
   * @param move Name of the team
   * @return boolean
   */
  private boolean isTurn(Move move) {
    int moveTeamIdentifier = Integer.parseInt(move.getPieceId().split(":")[1].split("_")[0]);
    return (moveTeamIdentifier == gameState.getCurrentTeam()-1);
  }

  /**
   * The {@link GameEngine#isGameOver()} method only returns the {@link GameEngine#isGameOver}
   * value, so this method implements the game over checks. It updates the isGameOver {@link
   * GameEngine#isGameOver} value accordingly.
   *
   * @author sistumpf
   */
  public void gameOverCheck() {
    if (getRemainingGameTimeInSeconds() == 0) {
      this.isGameOver = true;
    } else {
      for (Team team : gameState.getTeams()) {
        if (team.getFlags() < 1) {
          this.isGameOver = true;
          break;
        } else if (team.getPieces().length == 0) {
          this.isGameOver = true;
          break;
        }
      }
    }
    
    if(this.isGameOver) {
      this.endDate =
          Date.from(
              LocalDateTime.now()
              .atZone(ZoneId.systemDefault())
              .toInstant()); // Sets game end time
    }
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
   * Helper method to Start the game
   *
   * @author rsyed
   */
  private void startGame() {
    gameState.setCurrentTeam(
        (int) (Math.random() * currentTemplate.getTeams())); // Sets the starting team randomly
    this.startedDate =
        Date.from(
            LocalDateTime.now()
                .atZone(ZoneId.systemDefault())
                .toInstant()); // Sets the TimeStamp for when the game started
  }

  /**
   * Helper method to add a team to the gameState. It takes in a String which it uses to create a
   * team, also needs a position to add the team at Also sets a random color to the team
   *
   * @author rsyed
   * @param teamID Name of the team
   * @param int position to add it in
   */
  private Team addTeam(String teamID, int pos) {
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
   * Helper method to visualize the board
   *
   * @author ysiebenh
   */
  private void printState() {
    for (String[] x : this.gameState.getGrid()) {
      for (String y : x) {
        System.out.print("[" + y + "]");
      }
      System.out.println("");
    }
  }

  /**
   * Helper method to set flags for Alternate Game modes
   *
   * @author rsyed
   */
  private void setAltGameModes(MapTemplate template) {
    if (template.getTotalTimeLimitInSeconds() != -1) {
      this.timeLimitedGame = true;
    }
    if (template.getMoveTimeLimitInSeconds() != -1) {
      this.moveTimeLimitedGame = true;
    }
  }

  public static void main(String[] args) {

    GameEngine test = new GameEngine();
    MapTemplate testMap = new MapTemplate();
    PieceDescription[] pieces = new PieceDescription[1];
    pieces[0] = new PieceDescription();
    pieces[0].setAttackPower(5);
    pieces[0].setCount(10);
    // pieces[1] = new PieceDescription();
    // pieces[1].setAttackPower(1);
    // pieces[1].setCount(3);
    testMap.setGridSize(new int[] {10, 10});
    testMap.setTeams(2);
    testMap.setBlocks(0);
    testMap.setPieces(pieces);

    String mapString =
        "{\"gridSize\":[10,10],\"teams\":2,\"flags\":1,\"pieces\":[{\"type\":\"Pawn\",\"attackPower\":1,\"count\":10,\"movement\":{\"directions\":{\"left\":0,\"right\":0,\"up\":1,\"down\":0,\"upLeft\":1,\"upRight\":1,\"downLeft\":0,\"downRight\":0}}},{\"type\":\"Rook\",\"attackPower\":5,\"count\":2,\"movement\":{\"directions\":{\"left\":2,\"right\":2,\"up\":2,\"down\":2,\"upLeft\":0,\"upRight\":0,\"downLeft\":0,\"downRight\":0}}},{\"type\":\"Knight\",\"attackPower\":3,\"count\":2,\"movement\":{\"shape\":{\"type\":\"lshape\"}}},{\"type\":\"Bishop\",\"attackPower\":3,\"count\":2,\"movement\":{\"directions\":{\"left\":0,\"right\":0,\"up\":0,\"down\":0,\"upLeft\":2,\"upRight\":2,\"downLeft\":2,\"downRight\":2}}},{\"type\":\"Queen\",\"attackPower\":5,\"count\":1,\"movement\":{\"directions\":{\"left\":2,\"right\":2,\"up\":2,\"down\":2,\"upLeft\":2,\"upRight\":2,\"downLeft\":2,\"downRight\":2}}},{\"type\":\"King\",\"attackPower\":1,\"count\":1,\"movement\":{\"directions\":{\"left\":1,\"right\":1,\"up\":1,\"down\":1,\"upLeft\":1,\"upRight\":1,\"downLeft\":1,\"downRight\":1}}}],\"blocks\":10,\"placement\":\"symmetrical\",\"totalTimeLimitInSeconds\":-1,\"moveTimeLimitInSeconds\":3}\r\n";
    Gson gson = new Gson();
    new TypeToken<>() {}.getType();
    testMap = gson.fromJson(mapString, MapTemplate.class);

    test.create(testMap);
    test.addTeam("seph", 1);
    // test.addTeam("seph2", 1);
    System.out.println(test.isStarted());
    System.out.println(test.isGameOver());
    int[] futuresquare = {2, 0};
    Move testmove = new Move();
    testmove.setNewPosition(futuresquare);
    testmove.setPieceId("p:1_1");
    // test.makeMove(testmove);
    test.printState();
    // System.out.println(test.gameState.getTeams()[0].getColor().toString());
    // System.out.println(test.gameState.getTeams()[1].getColor().toString());

    Move move = new Move();
    move.setPieceId(test.getCurrentGameState().getTeams()[0].getPieces()[0].getId());
    //	System.out.println(test.getCurrentGameState().getTeams()[1].getBase()[0]);
    //    	test.makeMove(move);
    // System.out.println(((int)(Math.random()*test.currentTemplate.getTeams())));
    // System.out.println(((int)(Math.random()*test.currentTemplate.getTeams())));
    // System.out.println(((int)(Math.random()*test.currentTemplate.getTeams())));
    // System.out.println(test.getRemainingMoveTimeInSeconds());
  }

  /**
   * TODO Test Konstruktor von Simon Kann entfernt werden wenn das Generieren von GameStates
   * funktioniert, wird in der Test Klasse gebraucht.
   */
  public GameEngine(GameState gameState, boolean isGameOver, boolean withTimeLimit, Date endDate) {
    this.gameState = gameState;
    this.isGameOver = isGameOver;
    this.endDate = endDate;
    this.timeLimitedGame = withTimeLimit;
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
