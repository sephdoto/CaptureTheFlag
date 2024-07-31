package org.ctf.shared.gameanalyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import javax.swing.JFileChooser;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;

/**
 * An service that can be used to save games as serialized object of {@link SavedGame} with
 * .savedgame extension for use in AI training. Saves files in Save Game folder with time stamps as
 * file names. Also has a loader function "readFile()" which can either open a file picker to select
 * and read a file or takes a file name as String. In case of picker the {@link SavedGame} object is
 * returned but if a String file name is provided the GameSaveHandler holds it in its internal
 * savedGame attribute. The SavedGame object can then be fetched using the getSavedGame() command.
 *
 * @author rsyed
 */
public class GameSaveHandler {

  LocalDateTime localDateTime;
  public SavedGame savedGame;
  public String lastFileName;

  /**
   * Main constructor just initialzes the {@link SavedGame} object this GameSaveHandler will use to
   * save data into.
   *
   * @author rsyed
   */
  public GameSaveHandler() {
    savedGame = new SavedGame();
  }

  /**
   * Writes out the currently held {@link SavedGame} object into a file, with its properties as
   * described in the main javadocs of this class. The file name is saved into a lastFileName
   * attribute for ready availibility. Essentially provides a Serialization function
   *
   * @author rsyed
   */
  public boolean writeOut() {
    try {
      localDateTime = LocalDateTime.now();
      DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy'_'MM'_'dd' 'HH'-'mm'_'ss");
      String fileName = localDateTime.format(df) /*+ "_" + this.hashCode()*/;
      this.lastFileName = fileName;
      FileOutputStream fileOutStream =
          new FileOutputStream(Constants.saveGameFolder + fileName + ".savedgame");

      ObjectOutputStream objectOutStream = new ObjectOutputStream(fileOutStream);

      objectOutStream.writeObject(this.savedGame);
      objectOutStream.close();
      fileOutStream.close();
    } catch (IOException e) {
//      System.out.println("IO Exception in Serializer class");
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * Reads a .savegame {@link SavedGame} file and returns a SaveGame. Also saves it into the local
   * attribute. Essentially provides a Deserialization function
   *
   * @author rsyed
   * @return {@link SavedGame} object
   */
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
    this.savedGame = returnObject;
    return returnObject;
  }

  /**
   * Reads a .savegame {@link SavedGame} file and returns a Save Essentially provides a
   * Deserialization function
   *
   * @author rsyed
   * @param name of the file you want to read into this analyzer (without its extension)
   */
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
   * Method to add a move to the internal {@link SavedGame} object.
   *
   * @author rsyed, sistumpf
   * @param move the Move to save
   * @param teams a String containing the Teams who gave up after a Move, seperated by ","
   * @return {@link SavedGame}
   */
  public synchronized void addMove(Move move, String teams) {
    this.savedGame.addMove(move, teams);
  }

  /**
   * Method to add a {@link GameState} (preferrabily the initial game state) to the {@link
   * SavedGame} object
   *
   * @author rsyed
   * @return {@link SavedGame}
   */
  public synchronized void addGameState(GameState gameState) {
    this.savedGame.setInitialGameState(gameState);
  }

  /**
   * Getter for the last file name this object used while writing out.
   *
   * @author rsyed
   * @return fileName used when writing out the file
   */
  public String getLastFileName() {
    return lastFileName;
  }

  /**
   * Deletes the last file it wrote out
   *
   * @author rsyed
   * @return true if the delete is successful, false if not
   */
  public boolean deleteLastSavedFile() {
    try {
      new File(Constants.saveGameFolder + this.lastFileName + ".savedgame").delete();
    } catch (Exception e) {
      return false;
    }
    return true;
  }
}
