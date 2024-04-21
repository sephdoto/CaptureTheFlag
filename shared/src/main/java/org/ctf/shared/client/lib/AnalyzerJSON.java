package org.ctf.shared.client.lib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import javax.swing.JFileChooser;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.state.GameState;

public class AnalyzerJSON {
  HashMap<String, GameState> hashMap = null;

  HashMap<String, GameState> gameStates = new HashMap<>();
  public static int key = 0;
  LocalDateTime localDateTime;

  public AnalyzerJSON() {
    this.gameStates = new HashMap<String, GameState>();
  }

  public AnalyzerJSON(HashMap<String, GameState> gameStates) {
    this.gameStates = gameStates;
  }

  public boolean writeOut() {
    Type type = new TypeToken<HashMap<String, GameState>>() {}.getType();
    localDateTime = LocalDateTime.now();
    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd-hhmmss");
    String fileName = localDateTime.format(df);
    File file = new File(Constants.dataBankPath + fileName + ".json");
    Gson gson = new Gson();
    try {
      gson.toJson(this.gameStates, HashMap.class, new FileWriter(file));
    } catch (JsonIOException e) {
      System.out.println("IO Exception in Serializer class");
      e.printStackTrace();
      return false;
    } catch (IOException e) {
      System.out.println("IO Exception in Serializer class");
      e.printStackTrace();
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

  public String toJson() {
    Gson gson = new Gson();
    return gson.toJson(this.gameStates);
  }

  public HashMap<String, GameState> simpleReader(){
    final JFileChooser fc = new JFileChooser(Constants.dataBankPath);
    fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
    fc.showOpenDialog(null);
    HashMap<String, GameState> data = null;
    JsonReader reader;
    try {
      reader = new JsonReader(new FileReader(fc.getSelectedFile()));
      Type type = new TypeToken<HashMap<String, GameState>>() {}.getType();
      Gson gson = new Gson();
      data = gson.fromJson(reader, HashMap.class);
    } catch (FileNotFoundException e) {
      System.out.println("Couldnt Find the File");
      e.printStackTrace();
    }
   

    return data;
  }
  
}
