package de.unimannheim.swt.pse.ctf.game;

import java.util.LinkedList;

import de.unimannheim.swt.pse.ctf.game.map.MapTemplate;
import de.unimannheim.swt.pse.ctf.game.map.PieceDescription;
import de.unimannheim.swt.pse.ctf.game.state.GameState;
import de.unimannheim.swt.pse.ctf.game.state.Piece;
import de.unimannheim.swt.pse.ctf.game.state.Team;

public class BoardController {

  /**
   * Helper Method for initializing the Grid with Empty spaces
   *
   * @author rsyed
   * @param int x
   * @param int y
   * @return String[][] with empty boxes
   */
  public String[][] initEmptyGrid(int x, int y) {
    String[][] grid = new String[x][y];
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[i].length; j++) {
        grid[i][j] = "";
      }
    }
    return grid;
  }

   /**
   * Helper method to initialize the teams
   *
   * @author ysiebenh
   * @param int teamID, MapTemplate template
   * @return initialized team
   */
  public Team initializeTeam(int teamID, MapTemplate template) {

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
   * Assigns positions to the Bases
   *
   * @author ysiebenh
   * @param GameState gameState, MapTemplate template
   */
  static void placeBases(GameState gs, MapTemplate mt) {
    int teams = mt.getTeams();
    int[][] boundaries = EngineTools.cutUpGrid(gs);
    if (teams % 2 != 0) {
      teams++;
    }

    for (int i = 0; i < mt.getTeams(); i++) {
      if (boundaries[i][4] == 0) {
        gs.getTeams()[i].setBase(
            new int[] {
              boundaries[i][1] / 2, (boundaries[i][3] - boundaries[i][2]) / 2 + boundaries[i][2]
            });
        gs.getGrid()[gs.getTeams()[i].getBase()[0]][gs.getTeams()[i].getBase()[1]] =
            "b:" + gs.getTeams()[i].getId();
      } else if (boundaries[i][4] == 1) {
        gs.getTeams()[i].setBase(
            new int[] {
              (boundaries[i][1] - boundaries[i][0]) / 2 + (boundaries[i][0]),
              (boundaries[i][3] - boundaries[i][2]) / 2 + boundaries[i][2]
            });
        gs.getGrid()[gs.getTeams()[i].getBase()[0]][gs.getTeams()[i].getBase()[1]] =
            "b:" + gs.getTeams()[i].getId();
      }
    }
  }
}
