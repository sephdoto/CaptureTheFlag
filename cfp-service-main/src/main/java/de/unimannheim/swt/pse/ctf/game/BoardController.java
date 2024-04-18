package de.unimannheim.swt.pse.ctf.game;

import java.util.LinkedList;

import de.unimannheim.swt.pse.ctf.game.map.MapTemplate;
import de.unimannheim.swt.pse.ctf.game.map.PieceDescription;
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

}
