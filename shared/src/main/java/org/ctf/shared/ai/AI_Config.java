package org.ctf.shared.ai;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.tools.JSON_Tools;
import org.json.JSONObject;

/**
 * Every AI got its own AI_Config that contains important constants.
 * AI_Config either contains default values (default constructor)
 * or fine tuned values, which are loaded in as a json String.
 * 
 * @author sistumpf
 */
public class AI_Config {
  public double C;
  // used to calculate UCT
  public int MAX_STEPS;
  // maximum of possible simulation steps the algorithm is allowed to take
  public int numThreads;
  // how many Threads are used in multithreadding

  public int attackPowerMultiplier;
  // how much the pieces attack power is valued
  public int pieceMultiplier;
  // how much having one piece is valued
  public int flagMultiplier;
  // how much having a flag is valued
  public int directionMultiplier;
  // how much a pieces reach into a certain direction is valued
  public int shapeReachMultiplier;
  // for valuing a shape Similar to a Direction movement. Calculated as 8 * this value (instead of 8 directions)
  public int distanceBaseMultiplier;
  // how much a pieces distance to the enemies base is weighted (higher = near enemies base is better)
  
  
  /**
   * Default constructor to set default values.
   */
  public AI_Config() {
    //use default values
    this.C = Math.E / 2;
    this.MAX_STEPS = 100;
    this.numThreads = Runtime.getRuntime().availableProcessors() / 2;
    
    this.attackPowerMultiplier = 3;
    this.pieceMultiplier = 10;
    this.flagMultiplier = 1000;
    this.directionMultiplier = 2;
    this.shapeReachMultiplier = 1;
    this.distanceBaseMultiplier = 10;
  }
  
  /**
   * Config-loading constructor to load values from a config json String.
   * First loads the default values, uses them if any errors show up in the config String.
   * 
   * @param configName configs file name without .json
   */
  public AI_Config(String configName) {
    this();
    
    String jsonConfig;
    try {
      jsonConfig = Files.readString(Paths.get(Constants.aiConfigFolder + configName + ".json"));
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    
    JSONObject settings = new JSONObject(jsonConfig);
    this.C = settings.getDouble("C");
    this.MAX_STEPS = settings.getInt("MAX_STEPS");
    this.numThreads = settings.getInt("numThreads");
    this.attackPowerMultiplier = settings.getInt("attackPowerMultiplier");
    this.pieceMultiplier = settings.getInt("pieceMultiplier");
    this.flagMultiplier = settings.getInt("flagMultiplier");
    this.directionMultiplier = settings.getInt("directionMultiplier");
    this.shapeReachMultiplier = settings.getInt("shapeReachMultiplier");
    this.distanceBaseMultiplier = settings.getInt("distanceBaseMultiplier");
  }
  
  public static void main(String[] args) {
    new AI_Config("TestConfig").saveConfigAs("TestConfigCopy");
  }
  
  /**
   * Saves a config in the ai_configs folder with the name configName.
   * 
   * @param configName the configs name
   */
  public void saveConfigAs(String configName) {
    JSONObject settings = new JSONObject();
    settings.put("C", this.C);
    settings.put("MAX_STEPS", this.MAX_STEPS);
    settings.put("numThreads", this.numThreads);
    settings.put("attackPowerMultiplier", this.attackPowerMultiplier);
    settings.put("pieceMultiplier", this.pieceMultiplier);
    settings.put("flagMultiplier", this.flagMultiplier);
    settings.put("directionMultiplier", this.directionMultiplier);
    settings.put("shapeReachMultiplier", this.shapeReachMultiplier);
    settings.put("distanceBaseMultiplier", this.distanceBaseMultiplier);
    
    try {
      JSON_Tools.saveObjectAsJSON(Constants.aiConfigFolder + configName + ".json", settings, true);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}