package de.unimannheim.swt.pse.ctf.game.map;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONWriter;
import java.io.StringWriter;


/**
 * using external JSON library, have to mention it in README; https://github.com/stleary/JSON-java/tree/master
 *@author sistumpf
 */
public class JSON_Tools {
	
	//Test method, remove later
	public static void main(String args[]) {
		MapFromJson(jsonString);
	}
	
	/**
	 * Creates a MapTemplate instance from a valid JSON String
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
		System.out.println(mt.toJSONString());
		return mt;
	}
	
	
	// Test String, remove later
	static String jsonString = """ 
			{
  "gridSize": [10, 10],
  "teams": 2,
  "flags": 1,
  "blocks": 0,
  "pieces": [
    {
      "type": "Pawn",
      "attackPower": 1,
      "count": 10,
      "movement": {
        "directions": {
          "left": 0,
          "right": 0,
          "up": 1,
          "down": 0,
          "upLeft": 1,
          "upRight": 1,
          "downLeft": 0,
          "downRight": 0
        }
      }
    },
    {
      "type": "Rook",
      "attackPower": 5,
      "count": 2,
      "movement": {
        "directions": {
          "left": 2,
          "right": 2,
          "up": 2,
          "down": 2,
          "upLeft": 0,
          "upRight": 0,
          "downLeft": 0,
          "downRight": 0
        }
      }
    },
    {
      "type": "Knight",
      "attackPower": 3,
      "count": 2,
      "movement": {
        "shape": {
          "type": "lshape"
        }
      }
    },
    {
      "type": "Bishop",
      "attackPower": 3,
      "count": 2,
      "movement": {
        "directions": {
          "left": 0,
          "right": 0,
          "up": 0,
          "down": 0,
          "upLeft": 2,
          "upRight": 2,
          "downLeft": 2,
          "downRight": 2
        }
      }
    },
    {
      "type": "Queen",
      "attackPower": 5,
      "count": 1,
      "movement": {
        "directions": {
          "left": 2,
          "right": 2,
          "up": 2,
          "down": 2,
          "upLeft": 2,
          "upRight": 2,
          "downLeft": 2,
          "downRight": 2
        }
      }
    },
    {
      "type": "King",
      "attackPower": 1,
      "count": 1,
      "movement": {
        "directions": {
          "left": 1,
          "right": 1,
          "up": 1,
          "down": 1,
          "upLeft": 1,
          "upRight": 1,
          "downLeft": 1,
          "downRight": 1
        }
      }
    }
  ],
  "placement": "symmetrical",
  "totalTimeLimitInSeconds": -1,
  "moveTimeLimitInSeconds": -1
}

			""";
}
