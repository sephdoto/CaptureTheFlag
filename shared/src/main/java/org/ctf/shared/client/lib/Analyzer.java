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
 * @author rsyed
 */
public class Analyzer {

  LocalDateTime localDateTime;
  public SavedGame savedGame;

  public Analyzer() {
    savedGame = new SavedGame();
  }

  public boolean writeOut() {
    try {
      localDateTime = LocalDateTime.now();
      DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd-hhmmss");
      String fileName = localDateTime.format(df);
      FileOutputStream fileOutStream =
          new FileOutputStream(Constants.saveGameFolder + fileName + ".txt");

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

  @SuppressWarnings("unchecked")
  public boolean readFile(String name) {
    try {
      FileInputStream fileInput = new FileInputStream(Constants.saveGameFolder + name);
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

  public SavedGame getSavedGame() {
    return this.savedGame;
  }

  public void addMove(Move move) {
    this.savedGame.addMove(move);
  }

  public void addGameState(GameState gameState) {
    this.savedGame.setInitialGameState(gameState);
  }
}
