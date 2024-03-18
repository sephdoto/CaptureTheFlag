package de.unimannheim.swt.pse.ctf.game;

import com.google.gson.Gson;
import de.unimannheim.swt.pse.ctf.game.map.MapTemplate;
import de.unimannheim.swt.pse.ctf.game.map.PieceDescription;
import de.unimannheim.swt.pse.ctf.game.state.GameState;
import de.unimannheim.swt.pse.ctf.game.state.Piece;
import de.unimannheim.swt.pse.ctf.game.state.Team;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class BoardSetUp {

  /**
   * This is a helper method to initialize the teams in create
   *
   * @author ysiebenh
   * @param int teamID
   * @return Team thats initialized
   */
  static Team initializeTeam(int teamID, MapTemplate template) {
    // TODO different placement types
    // Creating the Pieces for the team
    int count = 1;
    LinkedList<Piece> indPieces = new LinkedList<Piece>();
    for (PieceDescription piece : template.getPieces()) {
      for (int i = 0; i < piece.getCount(); i++) {
        Piece x = new Piece();
        x.setId("p:" + teamID + "_" + Integer.toString(count++));
        x.setDescription(piece);
        x.setTeamId(Integer.toString(teamID));
        indPieces.add(x);
      }
    }

    // initializing team
    Team team = new Team();
    team.setId(Integer.toString(teamID));
    team.setColor(GameEngine.getRandColor());

    // TODO die Bases müssen anders gesetzt werden.
    if (teamID == 0) {
      team.setBase(new int[] {0, 0});
    } else if (teamID == 1) {
      team.setBase(new int[] {template.getGridSize()[1] - 1, template.getGridSize()[0] - 1});
    }

    team.setFlag(new int[] {template.getFlags()});

    Piece[] pieces = new Piece[indPieces.size()]; // putting the pieces in an array
    int iterator = 0;
    for (Piece p : indPieces) {
      pieces[iterator++] = p;
    }
    team.setPieces(pieces);

    return team;
  }

  /**
   * This method decides what method to call for placing pieces.
   *
   * @author sistumpf
   * @param gameState
   * @param template
   */
  static void initPieces(GameState gameState, MapTemplate template) {
    switch (template.getPlacement()) {
      case symmetrical:
        placePiecesSymmetrical(gameState);
        break;
      case spaced_out:
        placePiecesSpaced(gameState);
        break;
      case defensive:
        placePiecesDefensive(gameState);
        break;
    }
  }

  /**
   * This Method creates a GameStates grid. Teams, Pieces and Bases must already be initialized in
   * the GameState. Pieces and Bases are placed right on their in gameState.getTeams()[x] specified
   * position. Blocks get placed based on the PlacementType from the MapTemplate.
   *
   * @author sistumpf
   * @param MapTemplate template
   * @param Team[] teams
   * @param String[][] grid
   * @param int blocks
   * @return grid with placed pieces and blocks
   */
  static void initGrid(GameState gameState, MapTemplate template) {
    String[][] grid = gameState.getGrid();

    for (Team team : gameState.getTeams()) {
      grid[team.getBase()[0]][team.getBase()[1]] = "b:" + team.getId();
      for (Piece piece : team.getPieces()) {
        grid[piece.getPosition()[0]][piece.getPosition()[1]] = piece.getId();
      }
    }

    placeBlocks(template, grid, template.getBlocks());
  }

  /**
   * DUMMY TODO implement this method
   *
   * @param teams
   * @param grid
   * @return
   */
  static void placePiecesSpaced(GameState gameState) {
    return;
  }

  /**
   * DUMMY TODO implement this method
   *
   * @param teams
   * @param grid
   * @return
   */
  static void placePiecesDefensive(GameState gameState) {
    return;
  }

  /**
   * This is a helper method to place the pieces on the board in the create method
   *
   * @author ysiebenh
   * @param Team[] teams to be placed, String[][] grid upon which they are supposed to be placed
   * @return String[][] the finished board
   */
  static void placePiecesSymmetrical(GameState gameState) {
    // TODO more than two teams
    // putting the pieces on the board (team1)
    int row = 1;
    int column = 0;
    for (int i = 0; i < gameState.getTeams()[0].getPieces().length; i++) {
      if (column == gameState.getGrid()[0].length) {
        row++;
        column = 0;
      }
      if (!gameState.getGrid()[row][column].equals("")) {
        column++;
        i--;
      } else {
        Piece piece = gameState.getTeams()[0].getPieces()[i];
        piece.setPosition(new int[] {row, column});
        gameState.getGrid()[row][column] = "p:" + piece.getTeamId() + "_" + piece.getId();
        column++;
      }
    }

    // putting pieces on the board (team2)
    row = gameState.getGrid().length - 2;
    column = gameState.getGrid()[0].length - 1;
    for (int i = 0; i < gameState.getTeams()[0].getPieces().length; i++) {
      if (column == -1) {
        row--;
        column = gameState.getGrid()[0].length - 1;
      }
      if (!gameState.getGrid()[row][column].equals("")) {
        column--;
        i--;
      } else {
        Piece piece = gameState.getTeams()[1].getPieces()[i];
        piece.setPosition(new int[] {row, column});
        gameState.getGrid()[row][column] = "p:" + piece.getTeamId() + "_" + piece.getId();
        column--;
      }
    }
  }

  /**
   * This is a helper method to place the blocks on the board in the create method
   *
   * @author sistumpf
   * @param MapTemplate mt, used as a seed for pseudo random number generating
   * @param String[][] grid
   * @param int blocks, number of blocks to be placed
   * @return String[][] grid with blocks placed on it
   */
  static void placeBlocks(MapTemplate mt, String[][] grid, int blocks) {
    ArrayList<Integer[]> freeList = new ArrayList<Integer[]>();
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[i].length; j++) {
        if (grid[i][j].equals("")) {
          freeList.add(new Integer[] {i, j});
        }
      }
    }

    for (; blocks > 0; blocks--) {
      int x = seededRandom(mt, blocks, freeList.size());
      grid[freeList.get(x)[0]][freeList.get(x)[1]] = "b";
      freeList.remove(x);
    }
  }

  /**
   * This method should be used instead of Math.random() to generate deterministic positive pseudo
   * random values. Changing modifier changes the resulting output for the same seed.
   *
   * @author sistumpf
   * @param MapTemplate mt, gets converted to a random seed
   * @param int modifier, to get different random values with the same seed
   * @param upperBound, upper bound for returned random values, upperBound = 3 -> values 0 to 2
   * @return
   */
  static int seededRandom(MapTemplate mt, int modifier, int upperBound) {
    int seed = (new Gson().toJson(mt) + String.valueOf(modifier)).hashCode();
    return new Random(seed).nextInt(upperBound);
  }
}
