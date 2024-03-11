//TODO Add license if applicable
package de.unimannheim.swt.pse.ctf.game;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


import de.unimannheim.swt.pse.ctf.game.exceptions.GameOver;
import de.unimannheim.swt.pse.ctf.game.exceptions.InvalidMove;
import de.unimannheim.swt.pse.ctf.game.exceptions.NoMoreTeamSlots;
import de.unimannheim.swt.pse.ctf.game.map.MapTemplate;
import de.unimannheim.swt.pse.ctf.game.map.PieceDescription;
import de.unimannheim.swt.pse.ctf.game.state.GameState;
import de.unimannheim.swt.pse.ctf.game.state.Move;
import de.unimannheim.swt.pse.ctf.game.state.Piece;
import de.unimannheim.swt.pse.ctf.game.state.Team;
import javafx.scene.paint.Color; 

/**
 * Game Engine Implementation\
 * 
 * @author rsyed & ysiebenh & sistumpf
 */
public class GameEngine implements Game {

    private GameState gameState; //MAIN Data Store for GameEngine

    private MapTemplate currentTemplate; // Saves a copy of the template

    private int remainingTeamSlots;
    private int maxFlags;
    private boolean isStarted; // Initial value
    private boolean isGameOver;
    private boolean timeLimit;
    private Date startedDate;
    private Date endDate;
    private List<String> colorList;
    
    /**
     * Creates a game session with the corresponding Map passed onto as the Template
     * 
     * @param template
     * @return GameState
     */
    @Override
    public GameState create(MapTemplate template) {
        this.currentTemplate = template; // Saves a copy of the initial template
        colorList =  new LinkedList<>(Arrays.asList(new String[]{"red" , "green", "yellow" , "white" , "black" , "blue" })); //Inits a String LL with predefined colors
        
        GameState gameState = new GameState();
        gameState.setTeams(new Team[template.getTeams()]);        
        String[][] newGrid = new String[template.getGridSize()[0]][template.getGridSize()[1]];
             
        //this for loop initializes the grid with empty Strings
        for(int i = 0; i < newGrid.length; i++) {
        	for(int j = 0; j < newGrid[i].length; j++) {
        		newGrid[i][j] = "";
        	}
        }
        
        //placing the bases/flags 
        //TODO multiple flags in a base??????
        newGrid = BoardSetUp.placeFlags(template, newGrid);
       
        //initializing teams
       	Team[] teams = new Team[2];
       	gameState.setTeams(teams);
   		teams[0] = BoardSetUp.initializeTeam(1, template);
   		teams[1] = BoardSetUp.initializeTeam(2, template);
   		
   		//placing the pieces 
        newGrid = BoardSetUp.placePieces(teams, newGrid);
          
        //placing blocks   TODO odd numbers?(only divisible by 2)
        newGrid = BoardSetUp.placeBlocks(newGrid, template.getBlocks());
       
        // selecting starting team, here or in joinGame?
        gameState.setCurrentTeam((int)(Math.random()*2)+1);
        
        // Setting Flags
        this.isStarted = false;
        this.isGameOver = false;

        // Setting State
        gameState.setGrid(newGrid);
        this.gameState = gameState;
        return this.gameState;
    }

    /**
     * Get current state of the game
     *
     * @return GameState
     */
    @Override
    public GameState getCurrentGameState() {
        return gameState;
    }

    /**
     * @return End date of game
     */
    @Override
    public Date getEndDate() {
        return this.endDate;
    }

    /**
     *
     * @return -1 if no total game time limit set, 0 if over, > 0 if seconds remain
     */
    @Override
    public int getRemainingGameTimeInSeconds() {
        if (!timeLimit) { // If no limit is set returns -1
            return -1;
        } else if (isGameOver) { // if GameOver flag is set returns 0
            return 0;
        }
        return 1; // Otherwise returns 1
    }

    /**
     *
     * @return -1 if no move time limit set, 0 if over, > 0 if seconds remain
     */
    @Override
    public int getRemainingMoveTimeInSeconds() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * @return number of remaining team slots
     */
    @Override
    public int getRemainingTeamSlots() {
        return remainingTeamSlots;
    }

    /**
     * @return Start {@link Date} of game
     */
    @Override
    public Date getStartedDate() {
        return startedDate;
    }

    /**
     * Get winner(s) (if any)
     *
     * @return {@link Team#getId()} if there is a winner
     */
    @Override
    public String[] getWinner() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * A team has to option to give up a game (i.e., losing the game as a result).
     *
     * Assume that a team can only give up if it is its move (turn).
     *
     * @param teamId Team ID
     */
    @Override
    public void giveUp(String teamId) {
        Team[] teamBlock = getCurrentGameState().getTeams();
        int currentTeam = getCurrentGameState().getCurrentTeam() - 1;

        if (teamBlock[currentTeam].getId().equals(teamId)) {
            // TODO CODE TO DELETE WHATS LEFT OF THE TEAM and give up
        }
    }

    /**
     * Checks whether the game is over based on the current {@link GameState}.
     *
     * @return true if game is over, false if game is still running.
     */
    @Override
    public boolean isGameOver() {
        if (isGameOver) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks whether the game is started based on the current {@link GameState}.
     *
     * <ul>
     * <li>{@link Game#isGameOver()} == false</li>
     * <li>{@link Game#getCurrentGameState()} != null</li>
     * </ul>
     *
     * @return boolean
     */
    @Override
    public boolean isStarted() {
        if (isStarted && !isGameOver && getCurrentGameState() != null) {
            return true;
        }
        return false;
    }

    /**
     * @author sistumpf
     * Checks whether a move is valid based on the current game state.
     * @param move {@link Move}
     * @return true if move is valid based on current game state, false otherwise
     */
    @Override
    public boolean isValidMove(Move move) {
      Piece piece = (Piece)(Arrays.asList(gameState.getTeams()[gameState.getCurrentTeam()].getPieces()).stream().filter(p -> p.getId().equals(move.getPieceId())));
      return AI_Tools.validPos(move.getNewPosition(), piece, gameState);      
    }

    /**
     * Updates a game and its state based on team join request (add team).
     *
     * <ul>
     * <li>adds team if a slot is free (array element is null)</li>
     * <li>if all team slots are finally assigned, implicitly starts the game by
     * picking a starting team at random</li>
     * </ul>
     *
     * @param teamId Team ID
     * @return Team
     * @throws NoMoreTeamSlots No more team slots available
     */
    // TODO do proper joinGameLogic
    // TODO Ask tutor how they mean to start a game.....through the exception ? or filling team slots and calling joinGame one more time to start it?
    @Override
    public Team joinGame(String teamId) {
        // Initial check if Slots are even available
        // Check if .....Team with same name exists
        try {
            if (this.getRemainingTeamSlots() < 0) {
                throw new NoMoreTeamSlots();
            } else {
                makeNAddTeam(teamId);
                //TODO Method here to load and arrange pieces
            }
        } catch (NoMoreTeamSlots e) {
            startGame();
        }
        
        

        // TODO Extra Method to place pieces on the Grid when joining


        // Code for Random starting team selection return
        Team[] currentTeams = getCurrentGameState().getTeams();
        int teamSelector = randomGen(currentTemplate.getTeams() - 1, 5); // updates team selector with a random Number
        return currentTeams[teamSelector];
    }

    /**
     * Make a move
     *
     * @param move {@link Move}
     * @throws InvalidMove Requested move is invalid
     * @throws GameOver Game is over
     */
    @Override
    public void makeMove(Move move) {
        if(!isValidMove(move))
          throw new InvalidMove();
        
        String occupant = gameState.getGrid()[move.getNewPosition()[0]][move.getNewPosition()[1]];
        int[] oldPos = ((Piece)(Arrays.asList(gameState.getTeams()[gameState.getCurrentTeam()].getPieces()).stream().filter(p->p.getId().equals(move.getPieceId())).toArray()[0])).getPosition();

        gameState.getGrid()[oldPos[0]][oldPos[1]] = "";
        gameState.getGrid()[move.getNewPosition()[0]][move.getNewPosition()[1]] = move.getPieceId();

        if(occupant.contains("p:")) {
          int occupantTeam = Integer.parseInt(occupant.split(":")[1].split("_")[0]);
          gameState.getTeams()[occupantTeam].setPieces((Piece[])Arrays.asList(gameState.getTeams()[occupantTeam].getPieces()).stream().filter(p->!p.getId().equals(occupant)).toArray());
        }
        
        gameState.setCurrentTeam((gameState.getCurrentTeam()+1) % gameState.getTeams().length);
        gameState.setLastMove(move);

        //TODO Flagge/Base Logik, GameOver check, 
    }

    /**
     * Returns a random number between 0 and max
     * Does specified number of iterations
     *
     * @param max
     * @param iterations
     * 
     */
    public int randomGen(int max, int iterations) {
        Random rand = new Random();
        int ret = 0;
        for (int i = 0; i <= iterations; i++) {
            ret = rand.nextInt(max);
        }
        return ret;
    }

    /**
     * Helper method to Start the game
     * 
     */
    private void startGame() {
        this.isStarted = true; // Starts the game because all teams have joined

        LocalDateTime localDateTime = LocalDateTime.now(); // Gets a localDateTime Obj
        this.startedDate = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        Long seconds = Integer.toUnsignedLong(currentTemplate.getTotalTimeLimitInSeconds());

        if (seconds > 0) { // Checks if its a timed Game
            LocalDateTime modifiedDateTime = localDateTime.plusMinutes(seconds); // Adds seconds to a new Object
            this.endDate = Date.from(modifiedDateTime.atZone(ZoneId.systemDefault()).toInstant()); // Converts and saves
                                                                                                   // it to endDate
        } else if (seconds == 0) {
            this.isGameOver = true;
        } else {
            this.timeLimit = false;
        }
    }

    /**
     * Helper method to add a team to the gameState
     * Takes in a String which it uses to create a team
     * @param teamID
     */

     //TODO this method is fucky
    private void makeNAddTeam(String teamID){
        Team[] currentTeams = getCurrentGameState().getTeams(); // Gets array of teams from currentGameState

        for (int i = 0 ; i < currentTeams.length; i++) { // Loops over it to
            if (currentTeams[i] == null) { // find the first empty team
                currentTeams[i] = new Team();
                currentTeams[i].setId(teamID); // sets the team ID
                //Fix Color assignment:
                currentTeams[i].setColor(getRandColor());  //gets a randomly selected color from the list

                remainingTeamSlots--;
                
                getCurrentGameState().setTeams(currentTeams);     //Returns the updated Team array to the Game State
                
                if(remainingTeamSlots<=0){ 
                    startGame();
                    break;
                }
                break;
            }
        }
    }
    
    /**
     * Helper method returns HEX Codes for colors
     * 
     * @return 
     */
    static String getRandColor(){
        Random rand = new Random();
           int r = rand.nextInt(255); // [0,255]
           int g = rand.nextInt(255); // [0,255]
           int b = rand.nextInt(255); // [0,255]
           Color testColor = Color.rgb(r, g, b);
           return testColor.toString();
    }
    
    /**
     * Helper method to visualize the board
     * @author ysiebenh
     */
    private void printState() {
    	for( String[]  x : this.gameState.getGrid()) {
    		for( String y : x) {
    			System.out.print("[" + y + "]");
    		}
    		System.out.println("");
    	}
    }
    
    public static void main(String[] args) {
    	GameEngine test = new GameEngine();
    	MapTemplate testMap = new MapTemplate();
    	PieceDescription[] pieces = new PieceDescription[2];
    	pieces[0] = new PieceDescription();
    	pieces[0].setAttackPower(5);
    	pieces[0].setCount(10);
    	pieces[1] = new PieceDescription();
    	pieces[1].setAttackPower(1);
    	pieces[1].setCount(3);
    	testMap.setGridSize(new int[]{10,10});
    	testMap.setTeams(2);
    	testMap.setBlocks(4);
    	testMap.setPieces(pieces);
    	test.create(testMap);
    	
    	int[] futuresquare = {2,0};
    	Move testmove = new Move();
    	testmove.setNewPosition(futuresquare);
    	testmove.setPieceId("p:1_1");
    	//test.makeMove(testmove);
    	test.printState();
    	System.out.println(test.gameState.getTeams()[0].getColor().toString());
    	System.out.println(test.gameState.getTeams()[1].getColor().toString());
    	
    }
    
    
}