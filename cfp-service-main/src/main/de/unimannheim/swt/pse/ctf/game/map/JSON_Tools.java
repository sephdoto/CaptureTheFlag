package de.unimannheim.swt.pse.ctf.game.map;

import org.json.JSONObject;
import org.json.JSONArray;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;


/**
 * using external JSON library, have to mention it in README; https://github.com/stleary/JSON-java/tree/master
 *@author sistumpf
 */
public class JSON_Tools {
	static String mapTemplateFolder = "."+File.separator+"cfp-service-main"+File.separator+"src"+File.separator+"main"+File.separator+"resources"+File.separator+"maptemplates"+File.separator;
	
	/**
	 * Saves a MapTemplate as a file in mapTemplateFolder.
	 * The file Name must be given as mapName.
	 * @param mapName
	 * @param mapTemplate
	 */
	public static void saveMapTemplateAsFile(String mapName, MapTemplate mapTemplate) {
		byte[] contentBytes = mapTemplate.toJSONString().getBytes();
		try {
			File file = new File(mapTemplateFolder+mapName+".json");
			Files.write(file.toPath(), contentBytes);
			System.out.println(file.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns a MapTemplate from a given mapName. The mapName must exist in resources.maptemplates
	 * @param mapName
	 * @return mapTemplate
	 * @throws MapNotFoundException
	 */
	public static MapTemplate readMapTemplate(String mapName) throws MapNotFoundException {
		if(!Files.exists(Paths.get(mapTemplateFolder+mapName+".json")))
			throw new MapNotFoundException(mapName);
		
		return MapFromJson(fileToString(mapTemplateFolder+mapName+".json"));
	}
		
	/**
	 * Creates a MapTemplate instance from a valid JSON String
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
	 * Returns a files content as String.
	 * Source must be a Path.
	 * @param source
	 */
	private static String fileToString(String source) {
		String content = null; 
		try {
			content = Files.readString(Paths.get(source), StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return content;
	}
	
	/**
	 * Gets thrown if you access a map that doesn't exist in mapTemplateFolder
	 */
	static class MapNotFoundException extends Exception {
		MapNotFoundException(String mapName){
			super("There is no MapTemplate named " + mapName + " in " + mapTemplateFolder);
		}
	}
}
