package org.ctf.shared.client.lib;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JFileChooser;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;

/**
 * An service that can be used to save games as serialized object of {@link SavedGame} with
 * .savedgame extension for use in AI training. Saves files in
 * \shared\src\main\java\org\ctf\shared\resources\savegames with time stamps as file names. Also has
 * a loader function "readFile()" which can either open a file picker to select and read a file or
 * takes a file name as string. Incase of picker the {@link SavedGame} object is returned but if a
 * String file name is provided the analyzer holds it in its internal savedGame attribute. The
 * SavedGame object can then be fetched using the getSavedGame() command.
 *
 * @author rsyed
 */
public class Analyzer {

  LocalDateTime localDateTime;
  public SavedGame savedGame;

  /**
   * Main constructor just initialzes the {@link SavedGame} object this Analyzer will use to save
   * data into.
   *
   * @author rsyed
   */
  public Analyzer() {
    savedGame = new SavedGame();
  }

  /**
   * Writes out the currently held {@link SavedGame} object into a file, with its properties as
   * described in the main javadocs of this class. Essentially provides a Serialization function
   *
   * @author rsyed
   */
  public boolean writeOut() {
    try {
      localDateTime = LocalDateTime.now();
      DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd-hhmmss");
      String fileName = localDateTime.format(df);
      FileOutputStream fileOutStream =
          new FileOutputStream(Constants.saveGameFolder + fileName + ".savedgame");

      ObjectOutputStream objectOutStream = new ObjectOutputStream(fileOutStream);

      objectOutStream.writeObject(this.savedGame);
      objectOutStream.close();
      fileOutStream.close();
    } catch (IOException e) {
      System.out.println("IO Exception in Serializer class");
      return false;
    }
    return true;
  }

  /**
   * Reads a .savegame {@link SavedGame} file and returns a Save Essentially provides a
   * Deserialization function
   *
   * @author rsyed
   * @return {@link SavedGame} object
   */
  @SuppressWarnings("unchecked")
  public SavedGame readFile() {
    SavedGame returnObject = new SavedGame();
    try {
      final JFileChooser fc = new JFileChooser(Constants.saveGameFolder);
      fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
      fc.showOpenDialog(null);
      FileInputStream fileInput = new FileInputStream(fc.getSelectedFile());
      ObjectInputStream objectInput = new ObjectInputStream(fileInput);

      returnObject = (SavedGame) objectInput.readObject();

      objectInput.close();
      fileInput.close();

    } catch (IOException obj1) {
      obj1.printStackTrace();

    } catch (ClassNotFoundException obj2) {
      System.out.println("Class not found");
      obj2.printStackTrace();
    }

    return returnObject;
  }

  /**
   * Reads a .savegame {@link SavedGame} file and returns a Save Essentially provides a
   * Deserialization function
   *
   * @author rsyed
   * @param name of the file you want to read into this analyzer (without its extension)
   */
  @SuppressWarnings("unchecked")
  public boolean readFile(String name) {
    try {
      FileInputStream fileInput =
          new FileInputStream(Constants.saveGameFolder + name + ".savedgame");
      ObjectInputStream objectInput = new ObjectInputStream(fileInput);

      this.savedGame = (SavedGame) objectInput.readObject();

      objectInput.close();
      fileInput.close();

    } catch (IOException obj1) {
      obj1.printStackTrace();
      return false;
    } catch (ClassNotFoundException obj2) {
      System.out.println("Class not found");
      obj2.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * Returns the {@link SavedGame} attribute held by this class
   *
   * @author rsyed
   * @return {@link SavedGame}
   */
  public SavedGame getSavedGame() {
    return this.savedGame;
  }

  /**
   * Method to add a move to the internal {@link SavedGame} object
   *
   * @author rsyed
   * @return {@link SavedGame}
   */
  public void addMove(Move move) {
    this.savedGame.addMove(move);
  }

  /**
   * Method to add a {@link GameState} (preferrabily the initial game state) to the {@link
   * SavedGame} object
   *
   * @author rsyed
   * @return {@link SavedGame}
   */
  public void addGameState(GameState gameState) {
    this.savedGame.setInitialGameState(gameState);
  }
}
