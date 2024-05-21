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
   * Move evaluations to represent how good or bad a move is. TODO add colors
   *
   * @author sistumpf
   */
  public enum MoveEvaluation {
    GREAT,
    BEST,
    EXCELLENT,
    GOOD,
    OK,
    INACCURACY,
    MISTAKE,
    MISS,
    BLUNDER,
    SUPERBLUNDER;
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

  public enum ImageType {
    HOME("homescreen"),
    START("startscreen"),
    PIECE("pieces"),
    MISC("misc"),
    WAVE("WaveFunctionTiles");

    private final String folderName;

    ImageType(final String folderName) {
      this.folderName = folderName + File.separator;
    }

    public String getFolderName() {
      return this.folderName;
    }
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

    /**
     * MISC is used for sounds like Button clicks, a user should not change that.
     *
     * @return An Array Containing the SoundTypes a user is allowed to change
     */
    public SoundType[] userChangeValues() {
      return new SoundType[] {MOVE, KILL, CAPTURE, SELECT, DESELECT};
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

    @Override
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
    IMPROVED("MCTS-IMPROVED"),
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

    @Override
    public String toString() {
      return this.name;
    }
  }
}
