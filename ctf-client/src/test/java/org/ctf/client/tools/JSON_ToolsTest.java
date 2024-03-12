package org.ctf.client.tools;

import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.ctf.client.constants.Constants;
import org.ctf.client.state.data.map.MapTemplate;
import org.ctf.client.tools.JSON_Tools.IncompleteMapTemplateException;
import org.ctf.client.tools.JSON_Tools.MapNotFoundException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JSON_ToolsTest {
  String mapTemplateFolder;
  String mapString;
  MapTemplate mapTemplate;
  
  @AfterAll
  void tearDownAfterClass() throws Exception {
    Constants.mapTemplateFolder = mapTemplateFolder;
  }

  @Test
  void testSaveMapTemplateAsFile() {
    try {
      JSON_Tools.saveMapTemplateAsFile("test", mapTemplate);
    } catch (IOException e) {
      fail("MapTemplate konnte nicht gespeichert werden");
    }

    try {
      assertTrue(Files.deleteIfExists(Paths.get(Constants.mapTemplateFolder + "test.json")));
    } catch (IOException e) {
      fail("MapTemplate konnte nicht gelöscht werden");
    }
  }
  
  @Test
  void testStringFromMap() {
    
    assertEquals(this.mapString, JSON_Tools.stringFromMap(mapTemplate));
  }

  @Test
  void testReadMapTemplateFile() {
    MapTemplate testTemplate = null;
    try {
      testTemplate = JSON_Tools.readMapTemplate(new File(Constants.mapTemplateFolder + "10x10_2teams_example.json"));
   } catch (IncompleteMapTemplateException e) {
     fail(e.getMessage());
   } catch (IOException e) {
     fail(e.getMessage());
   }
    
    assertEquals(this.mapTemplate, testTemplate);               //Muster und eingelesenes Template stimmen überein
  }

  @Test
  void testReadMapTemplateString() {
    MapTemplate testTemplate = null;
    try {
      testTemplate = JSON_Tools.readMapTemplate("10x10_2teams_example");
    } catch (MapNotFoundException e) {
      fail("einzulesendes MapTemplate existiert nicht");
    }
    
    assertEquals(this.mapTemplate, testTemplate);               //Muster und eingelesenes Template stimmen überein
  }

  @Test
  void testMapFromJson() {
    fail("Not yet implemented");
  }

  
  @BeforeAll
  void create() {
    mapString = """
{
  "gridSize": [
    10,
    10
  ],
  "teams": 2,
  "flags": 1,
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
  "blocks": 0,
  "placement": "symmetrical",
  "totalTimeLimitInSeconds": -1,
  "moveTimeLimitInSeconds": -1
}""";
    Gson gson = new Gson();
    new TypeToken<>() {}.getType(); 
    this.mapTemplate = gson.fromJson(mapString, MapTemplate.class);
    this.mapTemplateFolder = Constants.mapTemplateFolder;
    Constants.mapTemplateFolder = Paths.get("target" + File.separator + "test-classes" + File.separator + "maptemplates").toAbsolutePath().toString() + File.separator;
  }
}
