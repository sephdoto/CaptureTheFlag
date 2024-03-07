package org.ctf.client;

import org.ctf.TL.data.map.MapTemplate;
import org.ctf.TL.data.wrappers.GameSessionResponse;
import org.ctf.TL.data.wrappers.JoinGameResponse;
import org.ctf.TL.exceptions.Accepted;
import org.ctf.TL.exceptions.ForbiddenMove;
import org.ctf.TL.exceptions.GameOver;
import org.ctf.TL.exceptions.InvalidMove;
import org.ctf.TL.exceptions.NoMoreTeamSlots;
import org.ctf.TL.exceptions.SessionNotFound;
import org.ctf.TL.exceptions.URLError;
import org.ctf.TL.layer.CommLayer;
import org.ctf.TL.state.GameState;
import org.ctf.TL.state.Move;
import org.ctf.TL.state.Team;

import com.google.gson.Gson;

/**
 * Base Client file which is going to use the Translation Layer to talk to
 * the game server
 * 
 * Also saves data critical for the GameState
 * 
 * @author rsyed
 * 
 */
public class TestClient {

    private GameState currentState; //Main DataStore
    private Gson gson; // Gson object for conversions
    private CommLayer comm; // Layer instance which is used for communication

    //private String URL; // Saves the URL to connect to

    // Block to store Data from Game Session
    private String gameSessionID; // Sets the Session ID for the Layer
    private String urlWithID; // Creates URL with Session ID for use later

    // Block for Team Data
    private String teamSecret;
    private String teamID;
    private String teamColor;


    /**
     * Constructor which inits some objects on creation
     */
    public TestClient() {
        this.gson = new Gson(); // creates a gson Object on creation to conserve memory
        this.currentState = new GameState();
    }

    /**
     * Connect method to establish connection to the server
     * 
     * @param URL "http://localhost:8080"
     * @param Map
     * 
     * @throws URLError     404
     * @throws UnknownError 500
     */
    public void connect(String URL, MapTemplate Map) {
        this.comm = new CommLayer();
        URL = URL + "/api/gamesession";
        GameSessionResponse response = new GameSessionResponse();

        try {
            response = comm.createGameSession(URL, Map);
        } catch (UnknownError e) {
            System.out.println("Something wong");
        } catch (URLError e) {
            System.out.println("Bruh check the URL");
        } catch (Accepted e) {
            System.out.println("We Gucci");
        }

        this.gameSessionID = response.getId(); // Saves SessionID
        this.urlWithID = URL + "/" + gameSessionID; // Creates URL with Session ID for use later
    }

    /**
     * Method joins the requested game session
     * 
     * @param teamName
     * 
     * @throws SessionNotFound
     * @throws NoMoreTeamSlots
     * @throws UnknownError
     */
    public void joinGame(String teamName) {
        JoinGameResponse response = new JoinGameResponse();

        try {
            response = comm.joinGame(urlWithID, teamName);
        } catch (SessionNotFound e) {
            System.out.println("SessionID is wrong / Server is not there");
        } catch (NoMoreTeamSlots e) {
            System.out.println("Slots are full!");
        } catch (UnknownError e) {
            System.out.println("Something wong");
        }

        //Sets Data for the Layer from the response
        this.teamSecret = response.getTeamSecret();
        this.teamID = response.getTeamId();
        this.teamColor = response.getTeamColor();
        
        //Additional Tags for ALT game Modes


        // update();
    }

    /**
     * Method makes a move in the game
     * 
     * @param teamID
     * @param secret
     * @param Move
     * 
     * @throws Accepted        (200)
     * @throws SessionNotFound (404)
     * @throws ForbiddenMove   (403)
     * @throws InvalidMove     (409)
     * @throws GameOver        (410)
     * @throws UnknownError    (500)
     */
    public void makeMove(String teamID, String secret, Move move) {

        try {
            comm.makeMove(urlWithID, teamID, teamSecret, move);
        } catch (Accepted e) {
            System.out.println("We Gucci");
        } catch (SessionNotFound e) {
            System.out.println("SessionID is wrong / Server is not there");
        } catch (ForbiddenMove e) {
            System.out.println("Not turn/secret is borked");
        } catch (InvalidMove e) {
            System.out.println("Canne make this move mate");
        } catch (GameOver e) {
            System.out.println("Games Ova");
        } catch (UnknownError e) {
            System.out.println("Something wong");
        }

        // update();
    }

    public void giveUp() {
        comm.giveUp(urlWithID, teamID, teamSecret);
        update();
        refreshState();
    }

    public void update() {

    }

    public void refreshState() {

    }

    public void changeSession() {

    }

    public void deleteSession() {
        comm.deleteCurrentSession(urlWithID);
    }

    //Methods for Testing Returns
    public String getSessionID(){
        return this.gameSessionID;
    }
    public String getSecretID(){
        return this.teamSecret;
    }
}
