package org.ctf.shared.ai.mcts3;

import java.util.ArrayList;
import java.util.Arrays;
import org.ctf.shared.state.Piece;
/**
 * This class represents a team together with all its pieces.
 * It is slightly modified, to safe pieces differently.
 * 
 * @author Marcus Kessel, sistumpf
 */
public class Team {
    
    private String id;
    private String color;
    private int[] base;
    private int flags;
    private ArrayList<Piece> pieces;

    public Team() {
        this.id = "";
        this.color = "";
        this.base = new int[2];
        this.flags = 1;
        this.pieces = new ArrayList<Piece>();
    }
    
    public Team(org.ctf.shared.state.Team team) {
      this.id = team.getId();
      this.color = team.getColor();
      this.base = team.getBase();
      this.flags = team.getFlags();
      this.pieces = new ArrayList<Piece>(Arrays.asList(team.getPieces()));
    }
    
    /**
     * Converts an Array of Teams to instances from this Team class.
     * 
     * @param teams original Team array
     * @return instances of this Team class
     */
    public static Team[] toNewTeams(org.ctf.shared.state.Team[] teams) {
      Team newTeams[] = new Team[teams.length];
      for(int i=0; i<teams.length; i++)
        if(teams[i] != null)
          newTeams[i] = new Team(teams[i]);
        else 
          newTeams[i] = null;
      return newTeams;
    }
    
    /**
     * Converts an Array of Teams to instances from this Team class.
     * @param teams
     * @return instances of this Team
     */
    public static org.ctf.shared.state.Team[] toOldTeams(Team[] teams) {
      org.ctf.shared.state.Team oldTeams[] = new org.ctf.shared.state.Team[teams.length];
      for(int i=0; i<teams.length; i++) {
        oldTeams[i] = new org.ctf.shared.state.Team();
        oldTeams[i].setBase(teams[i].getBase());
        oldTeams[i].setColor(teams[i].getColor());
        oldTeams[i].setFlags(teams[i].getFlags());
        oldTeams[i].setId(teams[i].getId());
        oldTeams[i].setPieces(teams[i].getPieces().toArray(Piece[]::new));
      }
      return oldTeams;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int[] getBase() {
        return base;
    }

    public void setBase(int[] base) {
        this.base = base;
    }

    public ArrayList<Piece> getPieces() {
        return pieces;
    }

    public void setPieces(ArrayList<Piece> pieces) {
        this.pieces = pieces;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }
}
