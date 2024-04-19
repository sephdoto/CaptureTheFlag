package de.unimannheim.swt.pse.ctf.game;

import com.google.gson.Gson;

import de.unimannheim.swt.pse.ctf.game.exceptions.TooManyPiecesException;
import de.unimannheim.swt.pse.ctf.game.map.MapTemplate;
import de.unimannheim.swt.pse.ctf.game.map.PieceDescription;
import de.unimannheim.swt.pse.ctf.game.state.GameState;
import de.unimannheim.swt.pse.ctf.game.state.Piece;
import de.unimannheim.swt.pse.ctf.game.state.Team;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * Stores all static helper methods for the {@link GameEngine#create(MapTemplate) create} method
 *
 * @author rsyed & ysiebenh & sistumpf
 */
public class BoardSetUp {

  /**
   * Helper method to initialize the teams
   *
   * @author ysiebenh
   * @param int teamID, MapTemplate template
   * @return initialized team
   */
  public static Team initializeTeam(int teamID, MapTemplate template) {

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

    // initializing the team
    Team team = new Team();
    team.setId(Integer.toString(teamID));
    team.setColor(GameEngine.getRandColor());
    team.setFlags(template.getFlags());

    Piece[] pieces = new Piece[indPieces.size()];
    int iterator = 0;
    for (Piece p : indPieces) {
      pieces[iterator++] = p;
    }
    team.setPieces(pieces);
    return team;
  }

  /**
   * Decides what method to call for placing pieces.
   *
   * @author sistumpf
   * @param GameState gameState
   * @param MapTemplate template
   * @throws TooManyPiecesException
   */
  static void initPieces(GameState gameState, MapTemplate template) throws TooManyPiecesException {

    //Exception Calculator
    if (gameState.getTeams()[0].getPieces().length
        > (gameState.getGrid().length / 2) * (gameState.getGrid()[0].length - 2)) {
      throw new TooManyPiecesException("Too many Pieces! Make the board bigger! :)");
    }
    
    //Base teamID assigner
    for (Team team : gameState.getTeams()) {
      gameState.getGrid()[team.getBase()[0]][team.getBase()[1]] = "b:" + team.getId();
    }
    
    //Switch for placement strat
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
      // grid[team.getBase()[0]][team.getBase()[1]] = "b:" + team.getId(); //Moved to InitPieces so
      // the pieces will be placed around the base
      for (Piece piece : team.getPieces()) {
        grid[piece.getPosition()[0]][piece.getPosition()[1]] = piece.getId();
      }
    }
  }

  /**
   * Places the Pieces on the Board using a standard hill-climbing algorithm to ensure that every
   * piece has the maximum amount of possible moves
   *
   * @author ysiebenh
   * @param GameState gameState
   */
  static void placePiecesSpaced(GameState gameState) {
    randomPlacement(gameState);
    EngineTools.updateGrid(gameState);
    for (Team t : gameState.getTeams()) {
      GameState current = gameState;
      while (true) {
        LinkedList<GameState> neighbors =
            EngineTools.getNeighbors(current, Integer.decode(t.getId()));
        if (neighbors.size() == 0) {
          break;
        }
        GameState bestNeighbor = EngineTools.getBestState(neighbors, Integer.decode(t.getId()));
        if (EngineTools.valueOf(current, Integer.decode(t.getId()))
            >= EngineTools.valueOf(bestNeighbor, Integer.decode(t.getId()))) {
          gameState.setGrid(current.getGrid());
          gameState.getTeams()[Integer.decode(t.getId())].setPieces(
              current.getTeams()[Integer.decode(t.getId())].getPieces());

          break;
        } else current = bestNeighbor;
      }
    }
  }

  /**
   * Places the Pieces on the Board by placing the strongest pieces around the base and placing the
   * rest via the {@link BoardSetUp#placePiecesSpaced(GameState) placePiecesSpaced} method
   *
   * @author ysiebenh
   * @param GameState gameState
   */
  static void placePiecesDefensive(GameState gs) {
    // TODO implement position out of bounds
    placePiecesSpaced(gs);
    for (int i = 0; i < gs.getTeams().length; i++) {
      LinkedList<Piece> pieces = EngineTools.getStrongest(gs.getTeams()[i].getPieces());
      for (int y = -1; y <= 1; y++) {
        for (int x = -1; x <= 1; x++) {
          
          //checks if there already is a piece on that position:
          Piece toDelete = null;
          for (Piece p : pieces) {
            if (p.getPosition()[0] == gs.getTeams()[i].getBase()[0] + y
                && p.getPosition()[1] == gs.getTeams()[i].getBase()[1] + x) {
              toDelete = p;
            }
          }
          pieces.remove(toDelete);

          if (gs.getGrid()[gs.getTeams()[i].getBase()[0] + y][gs.getTeams()[i].getBase()[1] + x]
              .equals("")) {
            Piece piece = pieces.pop();
            gs.getGrid()[piece.getPosition()[0]][piece.getPosition()[1]] = "";
            piece.setPosition(
                new int[] {gs.getTeams()[i].getBase()[0] + y, gs.getTeams()[i].getBase()[1] + x});
            gs.getGrid()[gs.getTeams()[i].getBase()[0] + y][gs.getTeams()[i].getBase()[1] + x] =
                piece.getId();
          }
        }
      }
    }
  }

  /**
   * Places the pieces on the board randomly using the {@link EngineTools#seededRandom(String[][],
   * int, int, int) seededRandom} method
   *
   * @author ysiebenh
   * @param GameState gameState
   */
  private static void randomPlacement(GameState gs) {
    int i = 0;
    int[][] boundaries = EngineTools.cutUpGrid(gs);
    for (Team t : gs.getTeams()) {
      for (Piece p : t.getPieces()) {
        int newY = 0;
        int newX = 0;
        int m = 0;
        do {
          newY = EngineTools.seededRandom(gs.getGrid(), m++, boundaries[i][1], boundaries[i][0]);
          newX =
              EngineTools.seededRandom(gs.getGrid(), 1 - m++, boundaries[i][3], boundaries[i][2]);
        } while (!gs.getGrid()[newY][newX].equals(""));
        p.setPosition(new int[] {newY, newX});
        gs.getGrid()[newY][newX] = p.getId();
      }
      i++;
    }
  }

  /**
   * Places the pieces on the Board symmetrically around the base in a way that is pleasing to the
   * eye
   *
   * @author ysiebenh
   * @param GameState gameState
   */
  static void placePiecesSymmetrical(GameState gs) {
    int[][] boundaries = EngineTools.cutUpGrid(gs);
    for (int t = 0; t < gs.getTeams().length; t++) {
      int y;

      // checking the orientation and then putting the starting point 'under' the base
      if (boundaries[t][4] == 0) {
        y = gs.getTeams()[t].getBase()[0] - 1;
      } else {
        y = gs.getTeams()[t].getBase()[0] + 1;
      }
      int x = boundaries[t][2] + 1;
      
      for (int j = 0; j < gs.getTeams()[0].getPieces().length; j++) {
        
        if (x == boundaries[t][3] - 1 || gs.getTeams()[t].getPieces().length < gs.getGrid()[0].length - 2) {
          y = (boundaries[t][4] == 0) ? y + 1 : y - 1;
          x = boundaries[t][2] + 1;

          if (gs.getTeams()[t].getPieces().length - j < (boundaries[t][3] - boundaries[t][2]) / 2) {
            x =
                (boundaries[t][3] - boundaries[t][2]) / 2
                    - (gs.getTeams()[t].getPieces().length - j) / 2
                    + boundaries[t][2];
          }
        }

        if (!gs.getGrid()[y][x].equals("")) {
          x++;
          j--;
        } else {
          Piece piece = gs.getTeams()[t].getPieces()[j];
          piece.setPosition(new int[] {y, x});
          gs.getGrid()[y][x] = piece.getId();
          x++;
        }
      }
    }
  }

  

  /**
   * Places the bases on the grid and assigns them to a team.
   *
   * @author sistumpf
   * @param GameState gameState
   * @param MapTemplate template
   */
  public static void placeBases(GameState gameState, MapTemplate template) {
    String[][] grid = gameState.getGrid();
    int bases = template.getTeams();
    int yCuts = 1;
    int xCuts = 0;
    for(boolean cutX = true; (xCuts+1) * (yCuts+1) < bases; cutX = !cutX) {
      if(cutX) {
        xCuts++;
      } else {
        yCuts++;
      }
    }
    double yPartitionSize = grid.length / (double)(yCuts+1);
    double xPartitionSize = grid[0].length / (double)(xCuts+1);
//    System.out.println("xPartitions: " + (xCuts+1)+ " with size " + xPartitionSize + ", yPartitions: " + (yCuts+1)+ " with size " + yPartitionSize);
    for(int y=0, yc=0, team=0; yc*yPartitionSize<grid.length; y+=yPartitionSize, yc++)
      for(int x=0, xc=0; bases>0 && xc*xPartitionSize<grid[y].length; x+=xPartitionSize, bases--, xc++) {
        grid[(int)(y + yPartitionSize/2)][(int)(x + xPartitionSize/2)] = "b:" + gameState.getTeams()[team].getId();
        gameState.getTeams()[team].setBase(new int[] {(int)(y + yPartitionSize/2), (int)(x + xPartitionSize/2)});
        team++;
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

  /**
   * Helper method to initialize the grid and teams
   *
   * @author rsyed
   * @param GameState the object to to operations on
   * @param MapTemplate to read data from
   * @return GameState initialized with
   */
  static GameState makeGridandTeams(GameState gs, MapTemplate template) {
    gs.setGrid(
        initEmptyGrid(
            template.getGridSize()[0], template.getGridSize()[1])); // INIT Size and make the board
    gs.setTeams(initTeams(template));
    return gs;
  }

  /**
   * Helper Method for initializing the Grid with Empty spaces
   *
   * @author rsyed
   * @param int x
   * @param int y
   * @return String[][] with empty boxes
   */
  private static String[][] initEmptyGrid(int x, int y) {
    String[][] grid = new String[x][y];
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[i].length; j++) {
        grid[i][j] = "";
      }
    }
    return grid;
  }

  /**
   * Helper Method for initializing Teams
   *
   * @author rsyed
   * @param template Uses it to read data
   * @return Team[] with empty boxes
   */
  private static Team[] initTeams(MapTemplate template) {
    Team[] teams = new Team[template.getTeams()];
    for (int i = 0; i < teams.length; i++) {
      teams[i] = initializeTeam(i, template);
    }
    return teams;
  }
}

