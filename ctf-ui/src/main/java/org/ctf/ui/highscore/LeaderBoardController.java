package org.ctf.ui.highscore;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import org.ctf.shared.constants.Constants;

public class LeaderBoardController {
  private Gson gson;
  private LeaderBoard board;

  public LeaderBoardController() {
    gson = new Gson();
    init();
  }

  /** Checks if board already exists, loads it if possible, otherwise creates a new empty board */
  public void init() {
    if (checkIfFileExists()) {
      loadBoard();
    } else {
      board = new LeaderBoard();
    }
  }

  public boolean saveCurrentBoard() {
    try (Writer writer = new FileWriter(Constants.TOLEADERBOARD + "leaderboard" + ".json")) {
      gson.toJson(board, writer);
    } catch (IOException io) {
      return false;
    }
    return true;
  }

  public boolean loadBoard() {
    try (Reader reader = new FileReader("person.json")) {
      board = gson.fromJson(reader, LeaderBoard.class);
    } catch (IOException io) {
      return false;
    }
    return true;
  }

  public boolean checkIfFileExists() {
    File f = new File(Constants.TOLEADERBOARD + "leaderboard" + ".json");
    return (f.exists() && !f.isDirectory());
  }

  public LeaderBoard getBoard() {
    return board;
  }
}
