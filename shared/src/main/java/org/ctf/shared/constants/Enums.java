package org.ctf.shared.constants;

import java.io.File;

public class Enums {

  /**
   * Contains all different Themes to select from
   *
   * @author sistumpf
   */
  public enum Themes {
    STARWARS,
    LOTR,
    BAYERN;

  }

  /**
   * Contains the above mentioned variables. It safes them with their name to put in the
   * settings.json file.
   *
   * @author sistumpf
   */
  public enum UserChangeable {
    SOUNDVOLUME("soundVolume"),
    MUSICVOLUME("musicVolume"),
    THEME("theme");

    private final String name;

    private UserChangeable(final String name) {
      this.name = name;
    }

    public String getString() {
      return this.name;
    }
    ;
  }

  /**
   * Contains the different Sound Types and their locations in the project.
   *
   * @author sistumpf
   */
  public enum SoundType {
    MOVE("move"),
    KILL("kill"),
    CAPTURE("capture"),
    SELECT("select"),
    DESELECT("deselect"),
    MISC("misc");

    private final String location;

    SoundType(final String location) {
      this.location = location + File.separator;
    }

    public String getLocation() {
      return this.location;
    }
  }

  /**
   * Enums for AI Client
   *
   * @author rsyed
   */
  public enum AI {
    HUMAN,
    RANDOM,
    MCTS,
    IMPROVED,
    EXPERIMENTAL;
  }

  /**
   * Optional enums for Port Selection in Client
   *
   * @author rsyed
   */
  public enum Port {
    DEFAULT("8888"),
    AICLIENTTEST("9992");

    public final String label;

    private Port(String label) {
      this.label = label;
    }

    public boolean equalsName(String otherEnum) {
      return label.equals(otherEnum);
    }

    public String toString() {
      return this.label;
    }
  }
  
  
  	/**
  	 * Enum for the UI to show COnfig Labels and their Description {@link Descriptions}
  	 * 
  	 * @author Manuel Krakowski
  	 */
	public enum AIConfigs {
	  RANDOM("RANDOM"),
	  MCTS("MCTS"),
	  MCTS_IMPROVED("MCTS-IMPROVED"),
	  EXPERIMENTAL("EXPERIMENTAL"),
	  
	  C("UCT-C"), 
	  MAX_STEPS("Max-Steps"),
	  NUM_THREADS("Number of Threads "),
	  
	  ATTACK_POWER_MUL("Attack-Power"),
	  PIECE_MUL("Pieces"),
	  FLAG_MUL("Flags"),
	  DIRECTION_MUL("Directions"),
	  SHAPE_REACH_MUL("Shape-Reach"),
	  BASE_DISTANCE_MUL("Base-Distance");
	 
	  private final String name;

	  private AIConfigs(final String name) {
		  this.name = name;
	  }

	  public String toString() {
		  return this.name;
	  }
	}
}
