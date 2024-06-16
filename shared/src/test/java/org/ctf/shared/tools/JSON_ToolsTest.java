package org.ctf.shared.tools;

import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import org.ctf.shared.ai.TestValues;
import org.ctf.shared.constants.Constants;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.data.map.MapTemplate;
import org.ctf.shared.tools.JsonTools.IncompleteMapTemplateException;
import org.ctf.shared.tools.JsonTools.MapNotFoundException;

import com.google.gson.reflect.TypeToken;
import com.google.gson.Gson;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/**
 * @author sistumpf
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JSON_ToolsTest {
  String mapTemplateFolder;
  String mapString;
  MapTemplate mapTemplate;

  @Test
  void getTemplateAndGameState() {
    try {
      JsonTools.saveTemplateWithGameState("test", mapTemplate, TestValues.getTestState());
    } catch (IOException e) {
      fail(e.getMessage());
    }
    HashMap<MapTemplate, GameState> map = JsonTools.getTemplateAndGameState("test");
    assertTrue(map.keySet().size() > 0);
    File file = new File(JsonTools.gameStates + "test.json");
    assertTrue(file.delete());
  }
  
  @Test
  void saveTemplateWithGameState() {
    try {
      JsonTools.saveTemplateWithGameState("test", mapTemplate, TestValues.getTestState());
    } catch (Exception e) {
      fail("saving template with gamestate should be possible");
    }
    File file = new File(JsonTools.gameStates + "test.json");
    assertTrue(file.delete());
  }
  
  @Test
  void testSaveMapTemplateAsFile() {
    try {
      JsonTools.saveMapTemplateAsFile("testxxx", mapTemplate);
    } catch (IOException e) {
      fail("MapTemplate konnte nicht gespeichert werden");
    }

    try {
      assertTrue(Files.deleteIfExists(Paths.get(mapTemplateFolder + "testxxx.json")));
    } catch (IOException e) {
      fail("MapTemplate konnte nicht gelöscht werden");
    }
  }

  @Test
  void testStringFromMap() {
    assertEquals(this.mapString, JsonTools.stringFromMap(mapTemplate).replaceAll("\\s", ""));
  }

  @Test
  void testReadMapTemplateFile() {
    MapTemplate testTemplate = null;
    File file = new File(this.mapTemplateFolder + "10x10_2teams_example.json");

    try {
      JsonTools.saveMapTemplateAsFile("incomplete", testTemplate);
    } catch (IOException e) {e.printStackTrace();}
    File incomplete = new File(mapTemplateFolder + "incomplete.json");

    try {
      testTemplate = JsonTools.readMapTemplate(file);
    } catch (IncompleteMapTemplateException e) {
      fail(e.getMessage());
    } catch (IOException e) {
      fail(e.getMessage());
    }
    assertEquals(JsonTools.stringFromMap(this.mapTemplate), JsonTools.stringFromMap(testTemplate));	//Muster und eingelesenes Template stimmen überein

    Exception imte = assertThrows(IncompleteMapTemplateException.class, () -> JsonTools.readMapTemplate(incomplete));
    String expectedMessage = "The MapTemplate incomplete.json is incomplete and got deleted.";
    String actualMessage = imte.getMessage();

    assertEquals(expectedMessage, actualMessage);				// Ist das File kein/ein unvollständiges MapTemplate wird es gelöscht
  }

  @SuppressWarnings("deprecation")
  @Test
  void testReadMapTemplateString() {
    MapTemplate testTemplate = null;
    try {
      testTemplate = JsonTools.readMapTemplate("10x10_2teams_example");
    } catch (MapNotFoundException e) {
      fail("einzulesendes MapTemplate existiert nicht");
    }

    assertEquals(JsonTools.stringFromMap(this.mapTemplate), JsonTools.stringFromMap(testTemplate));	//Muster und eingelesenes Template stimmen überein

    Exception mnf = assertThrows(MapNotFoundException.class, () -> JsonTools.readMapTemplate("DAD"));
    String expectedMessage = "There is no MapTemplate named DAD in " + Constants.mapTemplateFolder;
    String actualMessage = mnf.getMessage();

    assertEquals(expectedMessage, actualMessage);				// Ist das File kein/ein unvollständiges MapTemplate wird es gelöscht

  }

  @Test
  void testMapFromJson() {
    MapTemplate mt = JsonTools.MapFromJson(mapString);

    assertEquals(JsonTools.stringFromMap(mt), JsonTools.stringFromMap(this.mapTemplate));
  }


  @BeforeAll
  void create() {
    mapString = "{\"gridSize\":[10,10],\"teams\":2,\"flags\":1,\"pieces\":[{\"type\":\"Pawn\",\"attackPower\":1,\"count\":10,\"movement\":{\"directions\":{\"left\":0,\"right\":0,\"up\":1,\"down\":0,\"upLeft\":1,\"upRight\":1,\"downLeft\":0,\"downRight\":0}}},{\"type\":\"Rook\",\"attackPower\":5,\"count\":2,\"movement\":{\"directions\":{\"left\":2,\"right\":2,\"up\":2,\"down\":2,\"upLeft\":0,\"upRight\":0,\"downLeft\":0,\"downRight\":0}}},{\"type\":\"Knight\",\"attackPower\":3,\"count\":2,\"movement\":{\"shape\":{\"type\":\"lshape\"}}},{\"type\":\"Bishop\",\"attackPower\":3,\"count\":2,\"movement\":{\"directions\":{\"left\":0,\"right\":0,\"up\":0,\"down\":0,\"upLeft\":2,\"upRight\":2,\"downLeft\":2,\"downRight\":2}}},{\"type\":\"Queen\",\"attackPower\":5,\"count\":1,\"movement\":{\"directions\":{\"left\":2,\"right\":2,\"up\":2,\"down\":2,\"upLeft\":2,\"upRight\":2,\"downLeft\":2,\"downRight\":2}}},{\"type\":\"King\",\"attackPower\":1,\"count\":1,\"movement\":{\"directions\":{\"left\":1,\"right\":1,\"up\":1,\"down\":1,\"upLeft\":1,\"upRight\":1,\"downLeft\":1,\"downRight\":1}}}],\"blocks\":0,\"placement\":\"symmetrical\",\"totalTimeLimitInSeconds\":-1,\"moveTimeLimitInSeconds\":-1}";
    Gson gson = new Gson();
    new TypeToken<>() {}.getType(); 
    this.mapTemplate = gson.fromJson(mapString, MapTemplate.class);
    this.mapTemplateFolder = Paths.get("src" + File.separator + "test" + File.separator +"java" + File.separator + "org" + File.separator + "ctf" + 
    File.separator +"shared" + File.separator +"tools" + File.separator + "maptemplates" + File.separator + "templates").toAbsolutePath().toString() + File.separator;
//    JsonTools.gameStates = Constants.mapTemplateFolder + "gamestates" + File.separator;
    JsonTools.mapTemplates = this.mapTemplateFolder;
  }

  @AfterAll
  void tearDownAfterClass() throws Exception {
//    Constants.mapTemplateFolder = mapTemplateFolder;
    JsonTools.gameStates = Constants.aboveMapTemplateFolder + "gamestates" + File.separator;
    JsonTools.mapTemplates = Constants.aboveMapTemplateFolder + "templates" + File.separator;
  }
}
