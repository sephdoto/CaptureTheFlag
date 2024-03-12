package de.unimannheim.swt.pse.ctf.game;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.unimannheim.swt.pse.ctf.game.map.MapTemplate;

class BoardSetUpTest {

	@Test
	void testPlaceBlocks() {
		String[][] grid = new String[][]
				{{"","!","","!"},{"!","!",""},{"!","!"},{""}};
		String[][] gridMuster = new String[][] {{"b","!","b","!"},{"!","!","b"},{"!","!"},{"b"}};
		grid = BoardSetUp.placeBlocks(testTemplate(), grid, 4);
		
		printGrid(grid);
		System.out.println("----------");
		assertArrayEquals(gridMuster, grid);									//Belegung mit Pieces
		
		grid = new String[][] {{"","",""},{"","",""},{"","",""}};
		gridMuster = new String[][] {{"","b","b"},{"b","b",""},{"","b",""}};
		grid = BoardSetUp.placeBlocks(testTemplate(), grid, 5);
		
		printGrid(grid);
		System.out.println("----------");
		assertArrayEquals(gridMuster, grid);									//Belegung ohne Pieces
		
		grid = new String[][] {{"","",""},{"","",""},{"","",""}};
		gridMuster = new String[][] {{"b","b","b"},{"","b",""},{"","b","b"}};
		grid = BoardSetUp.placeBlocks(testTemplate(), grid, 6);
		
		printGrid(grid);
		System.out.println("----------");
		assertArrayEquals(gridMuster, grid);									//Belegung wie vorher mit 6 Blöcken
	
	}
	
	@Test
	void testSeedRandom() {
		int mult0 = BoardSetUp.seededRandom(testTemplate(), 0, 10);
		int mult1 = BoardSetUp.seededRandom(testTemplate(), 1, 10);
		int mult2 = BoardSetUp.seededRandom(testTemplate(), 2, 10);
		int bound9 = BoardSetUp.seededRandom(testTemplate(), 0, 9);
		int bound3 = BoardSetUp.seededRandom(testTemplate(), 0, 3);
		
		assertEquals(1, mult0);
		assertEquals(6, mult1);
		assertEquals(4, mult2);
		assertEquals(2, bound3);
		assertEquals(8, bound9);
	}

	
	
	
	
	
	static void printGrid(String[][] grid) {
		for(String[] s : grid) {
			for(String ss : s) {
				System.out.print(ss.equals("") ? "x " : ss + " ");
			} 
			System.out.println();
		}
	}
	static MapTemplate testTemplate() {
		String mapString = "{\"gridSize\":[10,10],\"teams\":2,\"flags\":1,\"pieces\":[{\"type\":\"Pawn\",\"attackPower\":1,\"count\":10,\"movement\":{\"directions\":{\"left\":0,\"right\":0,\"up\":1,\"down\":0,\"upLeft\":1,\"upRight\":1,\"downLeft\":0,\"downRight\":0}}},{\"type\":\"Rook\",\"attackPower\":5,\"count\":2,\"movement\":{\"directions\":{\"left\":2,\"right\":2,\"up\":2,\"down\":2,\"upLeft\":0,\"upRight\":0,\"downLeft\":0,\"downRight\":0}}},{\"type\":\"Knight\",\"attackPower\":3,\"count\":2,\"movement\":{\"shape\":{\"type\":\"lshape\"}}},{\"type\":\"Bishop\",\"attackPower\":3,\"count\":2,\"movement\":{\"directions\":{\"left\":0,\"right\":0,\"up\":0,\"down\":0,\"upLeft\":2,\"upRight\":2,\"downLeft\":2,\"downRight\":2}}},{\"type\":\"Queen\",\"attackPower\":5,\"count\":1,\"movement\":{\"directions\":{\"left\":2,\"right\":2,\"up\":2,\"down\":2,\"upLeft\":2,\"upRight\":2,\"downLeft\":2,\"downRight\":2}}},{\"type\":\"King\",\"attackPower\":1,\"count\":1,\"movement\":{\"directions\":{\"left\":1,\"right\":1,\"up\":1,\"down\":1,\"upLeft\":1,\"upRight\":1,\"downLeft\":1,\"downRight\":1}}}],\"blocks\":0,\"placement\":\"symmetrical\",\"totalTimeLimitInSeconds\":-1,\"moveTimeLimitInSeconds\":-1}\r\n";
		Gson gson = new Gson();
		new TypeToken<>() {}.getType(); 
		return gson.fromJson(mapString, MapTemplate.class);
	}
}
