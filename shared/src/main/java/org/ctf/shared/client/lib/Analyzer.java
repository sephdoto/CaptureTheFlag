package org.ctf.shared.client.lib;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import javax.swing.JFileChooser;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.state.GameState;

import com.google.gson.Gson;

public class Analyzer {

  HashMap<String, GameState> gameStates = new HashMap<>();
  public static int key = 0;
  LocalDateTime localDateTime;

  public Analyzer() {
    this.gameStates = new HashMap<String, GameState>();
  }

  public Analyzer(HashMap<String, GameState> gameStates) {
    this.gameStates = gameStates;
  }

  public boolean writeOut() {
    try {
      localDateTime = LocalDateTime.now();
      DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd-hhmmss");
      String fileName = localDateTime.format(df);
      FileOutputStream fileOutStream =
          new FileOutputStream(Constants.dataBankPath + fileName + ".txt");

      ObjectOutputStream objectOutStream = new ObjectOutputStream(fileOutStream);

      objectOutStream.writeObject(gameStates);
      objectOutStream.close();
      fileOutStream.close();
    } catch (IOException e) {
      System.out.println("IO Exception in Serializer class");
      return false;
    }
    return true;
  }

  public void addToMap(GameState gameState) {
    this.gameStates.put(String.valueOf(key), gameState);
    key++;
  }

  @SuppressWarnings("unchecked")
  public HashMap<String, GameState> readFile() {
    HashMap<String, GameState> returnMap = null;
    try {
      final JFileChooser fc = new JFileChooser(Constants.dataBankPath);
      fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
      fc.showOpenDialog(null);
      FileInputStream fileInput = new FileInputStream(fc.getSelectedFile());
      ObjectInputStream objectInput = new ObjectInputStream(fileInput);

      returnMap = (HashMap) objectInput.readObject();

      objectInput.close();
      fileInput.close();

    } catch (IOException obj1) {
      obj1.printStackTrace();

    } catch (ClassNotFoundException obj2) {
      System.out.println("Class not found");
      obj2.printStackTrace();
    }
    return returnMap;
  }

  @SuppressWarnings("unchecked")
  public boolean readFile(String name) {
    try {
      FileInputStream fileInput = new FileInputStream(Constants.dataBankPath + name);
      ObjectInputStream objectInput = new ObjectInputStream(fileInput);

      this.gameStates = (HashMap) objectInput.readObject();

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

  public HashMap<String, GameState> getMap() {
    return this.gameStates;
  }

  public String toJson(){
    Gson gson = new Gson();
    return gson.toJson(this.gameStates);
  }
}
