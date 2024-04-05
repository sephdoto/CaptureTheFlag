package de.unimannheim.swt.pse.ctf.game;

import static org.junit.jupiter.api.Assertions.*;
import org.ctf.shared.state.data.exceptions.TooManyPiecesException;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.unimannheim.swt.pse.ctf.game.map.MapTemplate;
import de.unimannheim.swt.pse.ctf.game.state.GameState;
import de.unimannheim.swt.pse.ctf.game.state.Team;

class BoardSetUpTest {
  
    /**
     * @author yannicksiebenhaar
     * 
     */
    @Test
    void testInitializeTeam() {
      MapTemplate template = TestValues.getTestTemplate();
      Team team = BoardSetUp.initializeTeam(0, template);
      assertNotNull(team.getPieces());
      assertNotNull(team.getBase());
      assertNotNull(team.getClass());
      assertNotNull(team.getColor());
      assertEquals(team.getFlags(),template.getFlags());
      assertNotNull(team.getId());     
    }
    
    /**
     * @author yannicksiebenhaar
     * 
     */
    @Test
    void testInitPieces() {
      
      MapTemplate[] templates = TestValues.getDummyTeplates();
      
      for (int o = 0; o < templates.length; o++) {
        GameState gs = new GameState();
        Team[] teams = new Team[templates[o].getTeams()];

        String[][] grid = new String[templates[o].getGridSize()[0]][templates[o].getGridSize()[1]];
        for (int i = 0; i < grid.length; i++) {
          for (int j = 0; j < grid[i].length; j++) {
            grid[i][j] = "";
          }
        }
        gs.setGrid(grid);

        for (int i = 0; i < templates[o].getTeams(); i++) {
          teams[i] = BoardSetUp.initializeTeam(i, templates[o]);
        }
        gs.setTeams(teams);
        BoardSetUp.placeBases(gs, templates[o]);

        try {
          BoardSetUp.initPieces(gs, templates[o]);
        } catch (TooManyPiecesException e) {
          //System.out.println("too many pieces");
          //System.out.println("-----------------------------------");
          continue;
        }
        

        EngineTools.updateGrid(gs);

        printGrid(gs.getGrid());
        System.out.println("-----------------------------------");
      }
    }
    
    /**
     * @author yannicksiebenhaar
     * 
     */
    @Test
    void testInitGrid() {
      fail("not yet implimented");

    }
    
    /**
     * @author yannicksiebenhaar
     * 
     */
    @Test
    void testPlaceBases() {

      MapTemplate[] templates = TestValues.getDummyTeplates();

      for (int o = 0; o < templates.length; o++) {
        GameState gs = new GameState();
        Team[] teams = new Team[templates[o].getTeams()];

        String[][] grid = new String[templates[o].getGridSize()[0]][templates[o].getGridSize()[1]];
        for (int i = 0; i < grid.length; i++) {
          for (int j = 0; j < grid[i].length; j++) {
            grid[i][j] = "";
          }
        }
        gs.setGrid(grid);

        for (int i = 0; i < templates[o].getTeams(); i++) {
          teams[i] = BoardSetUp.initializeTeam(i, templates[o]);
        }
        gs.setTeams(teams);
        BoardSetUp.placeBases(gs, templates[o]);
        
        EngineTools.updateGrid(gs);

        printGrid(gs.getGrid());
        System.out.println("-----------------------------------");
      }
     

    }

  /**
   * @author sistumpf
   */
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

	  /**
	   * @author sistumpf
	   */
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
	

	  /**
	   * @author sistumpf
	   */	
	static void printGrid(String[][] grid) {
		for(String[] s : grid) {
			for(String ss : s) {
				System.out.print(ss.equals("") ? "x " : ss + " ");
			} 
			System.out.println();
		}
	}
}
