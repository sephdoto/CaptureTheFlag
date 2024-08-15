package org.ctf.shared.tools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.data.map.Directions;
import org.ctf.shared.state.data.map.MapTemplate;
import org.ctf.shared.state.data.map.Movement;
import org.ctf.shared.state.data.map.PieceDescription;
import org.ctf.shared.state.data.map.PlacementType;
import org.ctf.shared.state.data.map.Shape;
import org.ctf.shared.state.data.map.ShapeType;
import org.json.JSONArray;
import org.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Using the  external json and gson library, MapTemplates can be saved as and created from a JSON String.
 * @author sistumpf
 */
public class JsonTools {
  public static String mapTemplates = Constants.aboveMapTemplateFolder + "templates" + File.separator;
  static String gameStates = Constants.aboveMapTemplateFolder + "gamestates" + File.separator;
  
  /**
   * Returns a MapTemplate from a given File. 
   * The File should be choosen in the GameEngine via a FileChooser, so it surely exists.
   * 
   * @param mapName
   * @return mapTemplate
   * @throws IOException, IncompleteMapTemplateException
   */
  public static MapTemplate readMapTemplate(File mapTemplate) throws IOException, IncompleteMapTemplateException {
    try {
      return MapFromJson(fileToString(mapTemplate.getAbsolutePath()));
    } catch(org.json.JSONException jsone) {
      Files.delete(mapTemplate.toPath());
      throw new IncompleteMapTemplateException(mapTemplate.getName());
    }
  }

  /**
   * Returns a gameState and mapTemplate which were saved with the given name.
   * This method only gets called with existing file names, so using the deprecated method is ok here.
   * Searches in {@link mapTemplates} and {@link gameStates}
   * 
   * @param name the mapTemplate and gameStates name
   * @return 
   */
  public static HashMap<MapTemplate, GameState> getTemplateAndGameState(String name){
    HashMap<MapTemplate, GameState> mapMap = new HashMap<MapTemplate, GameState> ();
    try {
      MapTemplate mapTemplate = readMapTemplate(name);
      GameState gameState = readGameState(name);
      mapMap.put(mapTemplate, gameState);
    } catch(MapNotFoundException e) {
      e.printStackTrace();
    }
    return mapMap;
  }
  
  /**
   * Saves a MapTemplate and GameState as a file with the given name
   * 
   * @param mapName the filename for the mapTemplate and gameState
   * @param mapTemplate
   * @param gameState
   * @throws IOException
   */
  public static void saveTemplateWithGameState(String mapName, MapTemplate mapTemplate, GameState gameState) throws IOException {
    saveObjectAsJSON(mapTemplates +mapName+".json", mapTemplate, false);
    saveObjectAsJSON(gameStates +mapName+".json", gameState, false);
  }
  
  /**
   * Saves a MapTemplate as a file in mapTemplateFolder.
   * The file Name must be given as mapName, without an ending.
   * Example: save mapTemplate as template.json:   saveMapTemplateAsFile("template", mapTemplate)
   * 
   * @param mapName
   * @param mapTemplate
   * @throws IOException 
   */
  public static void saveMapTemplateAsFile(String mapName, MapTemplate mapTemplate) throws IOException {
    saveObjectAsJSON(mapTemplates+mapName+".json", mapTemplate, false);
  }
  
  
  /**
   * Saves any given object as a .json file at a given location.
   * If any other class than Object should be stringified, add another instanceof check and create a tailored contentBytes.
   * 
   * @param location
   * @param object
   * @throws IOException
   */
  public static void saveObjectAsJSON(String location, Object object, boolean prettyprinting) throws IOException {
    Gson gson;
    if(prettyprinting)
      gson = new GsonBuilder().setPrettyPrinting().create();
    else
      gson = new GsonBuilder().create();
    
    byte[] contentBytes = gson.toJson(object).getBytes();
    
    if(object instanceof JSONObject)
      contentBytes = ((JSONObject)object).toString(2).getBytes();
    
    File file = new File(location);
    Files.write(file.toPath(), contentBytes);
  }

  /**
   * Given a files name (without .json), a gameState is created from the JSON and gets returned.
   * 
   * @param name file name without .json
   * @return a new created GameState 
   * @throws MapNotFoundException
   */
  public static GameState readGameState(String name) throws MapNotFoundException {
    Path path = Paths.get(gameStates+name+".json");
    if(!Files.exists(path))
      throw new MapNotFoundException(name);
    try {
      return new GsonBuilder().create().fromJson(fileToString(gameStates+name+".json"), GameState.class);
    } catch (IOException e) {e.printStackTrace();
    }
    return null;
  }
  
  /**
   * Returns a MapTemplate from a given mapName. 
   * The mapName must exist in resources.maptemplates.
   * Example: read template.json from resources.maptemplates:   readMapTemplate("template")
   * 
   * @param mapName
   * @return mapTemplate
   * @throws MapNotFoundException
   * @deprecated use {@link #readMapTemplate(File mapTemplate)} instead.
   */
  @Deprecated
  public static MapTemplate readMapTemplate(String mapName) throws MapNotFoundException {
    Path path = Paths.get(mapTemplates+mapName+".json");
    if(!Files.exists(path))
      throw new MapNotFoundException(mapName);

    try {
      return MapFromJson(fileToString(mapTemplates+mapName+".json"));
    } catch (IOException e) {e.printStackTrace();}

    return null;
  }

  /**
   * Creates a MapTemplate instance from a valid JSON String.
   * 
   * @param jsonString
   * @return MapTemplate
   */
  public static MapTemplate MapFromJson(String jsonString) {
    MapTemplate mt = new MapTemplate();
    JSONObject jo = new JSONObject(jsonString);

    mt.setGridSize(new int[] {jo.getJSONArray("gridSize").getInt(0), jo.getJSONArray("gridSize").getInt(1)});
    mt.setTeams(jo.getInt("teams"));
    mt.setFlags(jo.getInt("flags"));
    mt.setBlocks(jo.getInt("blocks"));
    mt.setPlacement(PlacementType.valueOf(jo.getString("placement")));
    mt.setTotalTimeLimitInSeconds(jo.getInt("totalTimeLimitInSeconds"));
    mt.setMoveTimeLimitInSeconds(jo.getInt("moveTimeLimitInSeconds"));

    JSONArray ja = jo.getJSONArray("pieces");
    PieceDescription[] pieces = new PieceDescription[ja.length()];

    for(int i=0; i<ja.length(); i++) {
      pieces[i] = new PieceDescription();
      pieces[i].setAttackPower(ja.getJSONObject(i).getInt("attackPower"));
      pieces[i].setCount(ja.getJSONObject(i).getInt("count"));
      pieces[i].setType(ja.getJSONObject(i).getString("type"));
      Movement movement = new Movement();
      if(ja.getJSONObject(i).getJSONObject("movement").has("shape")) {
        Shape shape = new Shape();
        shape.setType(ShapeType.valueOf(ja.getJSONObject(i).getJSONObject("movement").getJSONObject("shape").getString("type")));
        movement.setShape(shape);
      } else {
        Directions directions = new Directions();
        JSONObject jsonDir = ja.getJSONObject(i).getJSONObject("movement").getJSONObject("directions");
        directions.setLeft(jsonDir.getInt("left"));
        directions.setRight(jsonDir.getInt("right"));
        directions.setUp(jsonDir.getInt("up"));
        directions.setDown(jsonDir.getInt("down"));
        directions.setUpLeft(jsonDir.getInt("upLeft"));
        directions.setUpRight(jsonDir.getInt("upRight"));
        directions.setDownLeft(jsonDir.getInt("downLeft"));
        directions.setDownRight(jsonDir.getInt("downRight"));
        movement.setDirections(directions);
      }
      pieces[i].setMovement(movement);
    }
    mt.setPieces(pieces);
    return mt;
  }

  /**
   * Creates a JSON String from an Object
   * 
   * @param Object
   * @return JSON String
   */
  public static String stringFromMap(Object object) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create(); 
    return gson.toJson(object);
  }


  /**
   * Returns a files content as String.
   * 
   * @param path
   * @throws IOException 
   */
  private static String fileToString(String path) throws IOException {
    return Files.readString(Paths.get(path), StandardCharsets.UTF_8);
  }

  /**
   * Gets thrown if a MapTemplate that doesn't exist in mapTemplateFolder is accessed.
   */
  public static class MapNotFoundException extends Exception {
    private static final long serialVersionUID = -6475981227866234664L;

    MapNotFoundException(String mapName){
      super("There is no MapTemplate named " + mapName + " in " + Constants.mapTemplateFolder);
    }
  }

  /**    
   * Gets thrown if a MapTemplate doesn't contain all information needed to build a GameState.
   */
  public static class IncompleteMapTemplateException extends org.json.JSONException {
    private static final long serialVersionUID = -6398646324585262889L;

    IncompleteMapTemplateException(String mapName){
      super("The MapTemplate " + mapName + " is incomplete and got deleted.");
    }
  }
}
