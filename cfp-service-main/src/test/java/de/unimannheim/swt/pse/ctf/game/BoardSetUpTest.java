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
		BoardSetUp.placeBlocks(TestValues.getTestTemplate(), grid, 4);
		
		printGrid(grid);
		System.out.println("----------");
		assertArrayEquals(gridMuster, grid);									//Belegung mit Pieces
		
		grid = new String[][] {{"","",""},{"","",""},{"","",""}};
		gridMuster = new String[][] {{"","b","b"},{"b","b",""},{"","b",""}};
		BoardSetUp.placeBlocks(TestValues.getTestTemplate(), grid, 5);
		
		printGrid(grid);
		System.out.println("----------");
		assertArrayEquals(gridMuster, grid);									//Belegung ohne Pieces
		
		grid = new String[][] {{"","",""},{"","",""},{"","",""}};
		gridMuster = new String[][] {{"b","b","b"},{"","b",""},{"","b","b"}};
		BoardSetUp.placeBlocks(TestValues.getTestTemplate(), grid, 6);
		
		printGrid(grid);
		System.out.println("----------");
		assertArrayEquals(gridMuster, grid);									//Belegung wie vorher mit 6 Blöcken
	
	}
	
	@Test
	void testSeedRandom() {
		int mult0 = BoardSetUp.seededRandom(TestValues.getTestTemplate(), 0, 10);
		int mult1 = BoardSetUp.seededRandom(TestValues.getTestTemplate(), 1, 10);
		int mult2 = BoardSetUp.seededRandom(TestValues.getTestTemplate(), 2, 10);
		int bound9 = BoardSetUp.seededRandom(TestValues.getTestTemplate(), 0, 9);
		int bound3 = BoardSetUp.seededRandom(TestValues.getTestTemplate(), 0, 3);
		
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
}
