package org.ctf.shared.tools;

import org.ctf.shared.constants.Constants;
import org.ctf.shared.state.data.map.Directions;
import org.ctf.shared.state.data.map.MapTemplate;
import org.ctf.shared.state.data.map.Movement;
import org.ctf.shared.state.data.map.PieceDescription;
import org.ctf.shared.state.data.map.PlacementType;
import org.ctf.shared.state.data.map.Shape;
import org.ctf.shared.state.data.map.ShapeType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;



/**
 * Using the  external json and gson library, MapTemplates can be saved as and created from a JSON String.
 * @author sistumpf
 */
public class JSON_Tools {
  
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
   * Saves a MapTemplate as a file in mapTemplateFolder.
   * The file Name must be given as mapName, without an ending.
   * Example: save mapTemplate as template.json:   saveMapTemplateAsFile("template", mapTemplate)
   * 
   * @param mapName
   * @param mapTemplate
   * @throws IOException 
   */
  public static void saveMapTemplateAsFile(String mapName, MapTemplate mapTemplate) throws IOException {
    saveObjectAsJSON(Constants.mapTemplateFolder+mapName+".json", mapTemplate);
  }
  
  
  /**
   * Saves any given object as a .json file at a given location.
   * If any other class than Object should be stringified, add another instanceof check and create a tailored contentBytes.
   * 
   * @param location
   * @param object
   * @throws IOException
   */
  public static void saveObjectAsJSON(String location, Object object) throws IOException {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    byte[] contentBytes = gson.toJson(object).getBytes();
    
    if(object instanceof JSONObject)
      contentBytes = ((JSONObject)object).toString(2).getBytes();
    
    File file = new File(location);
    Files.write(file.toPath(), contentBytes);
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
    Path path = Paths.get(Constants.mapTemplateFolder+mapName+".json");
    if(!Files.exists(path))
      throw new MapNotFoundException(mapName);

    try {
      return MapFromJson(fileToString(Constants.mapTemplateFolder+mapName+".json"));
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
    MapNotFoundException(String mapName){
      super("There is no MapTemplate named " + mapName + " in " + Constants.mapTemplateFolder);
    }
  }

  /**    
   * Gets thrown if a MapTemplate doesn't contain all information needed to build a GameState.
   */
  public static class IncompleteMapTemplateException extends org.json.JSONException {
    IncompleteMapTemplateException(String mapName){
      super("The MapTemplate " + mapName + " is incomplete and got deleted.");
    }
  }
}
