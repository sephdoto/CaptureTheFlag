package org.ctf.ui.highscore;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import org.ctf.shared.constants.Constants;

public class LeaderBoardController {
  private static Gson gson;
  private static LeaderBoard board;

  static {
    gson = new GsonBuilder().setPrettyPrinting().create();
    if (checkIfFileExists()) {
      loadBoard();
    } else {
      board = new LeaderBoard();
    }
  }

  public static boolean clearBoard() {
    board = new LeaderBoard();
    return saveCurrentBoard();
  }

  public static LeaderBoard getBoard() {
    return board;
  }

  public static boolean addEntry(Score score) {
    return getBoard().addScore(score);
  }

  public static Score[] getEntries(int length) {

    throw new UnsupportedOperationException("Unimplemented method 'getEntries'");
  }

  // FILE Operation Methods
  public static boolean saveCurrentBoard() {
    try (Writer writer = new FileWriter(Constants.TOLEADERBOARD + "leaderboard" + ".json")) {
      gson.toJson(board, writer);
    } catch (IOException io) {
      return false;
    }
    return true;
  }

  public static boolean loadBoard() {
    try (Reader reader = new FileReader(Constants.TOLEADERBOARD + "leaderboard" + ".json")) {
      board = gson.fromJson(reader, LeaderBoard.class);
    } catch (IOException io) {
      return false;
    }
    return true;
  }

  public static boolean checkIfFileExists() {
    File f = new File(Constants.TOLEADERBOARD + "leaderboard" + ".json");
    return (f.exists() && !f.isDirectory());
  }
  // END of File Operation Methods
}
