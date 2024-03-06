package org.ctf.Client;

import org.ctf.TL.data.map.MapTemplate;
import org.ctf.TL.data.wrappers.GameSessionResponse;
import org.ctf.TL.data.wrappers.JoinGameResponse;
import org.ctf.TL.exceptions.NoMoreTeamSlots;
import org.ctf.TL.exceptions.SessionNotFound;
import org.ctf.TL.exceptions.URLError;
import org.ctf.TL.layer.CommLayer;
import org.ctf.TL.state.GameState;
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
    private GameState currentState;
    private Gson gson; // Gson object for conversions
    private CommLayer comm; //Layer instance which is used for communication

    private String URL; //Saves the URL to connect to
    

    //Block to store Data from Game Session
    private String gameSessionID; // Sets the Session ID for the Layer
    private String urlWithID = URL + "/" + gameSessionID; // Creates URL with Session ID for use later

    //Block for Team Data
    private String teamSecret;
    private String teamID;
    private String teamColor;

    /* 
     * Constructor which inits some objects on creation 
    */
    public TestClient(){
        this.gson = new Gson(); // creates a gson Object on creation to conserve memory
        currentState = new GameState();
    }

    /* 
     * Connect method to establish connection to the server
     * @param URL 
     * @param Map
     * @throws UnknownError 500
     * @throws URLError 404
    */
    public void connect(String URL, MapTemplate Map){
        this.URL = URL + "/api/gamesession";
        this.comm = new CommLayer(URL);
        GameSessionResponse response = new GameSessionResponse();
        try {
        response = comm.createGameSession(Map);
        } catch (UnknownError e) {
            System.out.println("Something wong");
        } catch (URLError e){
            System.out.println("Bruh check the URL");
        }

        this.gameSessionID = response.getId();   //Saves SessionID
        this.urlWithID = URL + "/" + gameSessionID; // Creates URL with Session ID for use later
        
        //TODO You can also init the map here for game session if you need

    }

    /* 
     * Connect method to establish connection to the server
     * @param URL 
     * @param Map
     * @throws SessionNotFound
     * @throws NoMoreTeamSlots
     * @throws UnknownError
    */
    public void joinGame(String teamName){
        JoinGameResponse response = new JoinGameResponse();
        
        try {
            response = comm.joinGame(teamName);
            } catch (SessionNotFound e) {
                System.out.println("SessionID is wrong / Server is not there");
            } catch (NoMoreTeamSlots e){
                System.out.println("Slots are full!");
            } catch (UnknownError e){
                System.out.println("Something wong");
            }

        this.teamSecret = response.getTeamSecret();
		this.teamID = response.getTeamId();
		this.teamColor = response.getTeamColor();
        //update();
    }

    public void giveUp(){
        this.comm.giveUp(teamID,teamSecret);
        update();
        refreshState();
    }

    public void update(){

    }

    public void refreshState(){

    }

    public void deleteSession(){

    }

}
