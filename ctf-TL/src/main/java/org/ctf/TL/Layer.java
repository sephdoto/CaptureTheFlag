package org.ctf.TL;

import de.unimannheim.swt.pse.ctf.controller.data.GameSessionRequest;
import de.unimannheim.swt.pse.ctf.controller.data.GameSessionResponse;
import de.unimannheim.swt.pse.ctf.controller.data.GiveupRequest;
import de.unimannheim.swt.pse.ctf.controller.data.JoinGameRequest;
import de.unimannheim.swt.pse.ctf.controller.data.JoinGameResponse;
import de.unimannheim.swt.pse.ctf.controller.data.MoveRequest;
import de.unimannheim.swt.pse.ctf.game.map.MapTemplate;
import de.unimannheim.swt.pse.ctf.game.state.GameState;
import de.unimannheim.swt.pse.ctf.game.state.Move;
import de.unimannheim.swt.pse.ctf.game.state.Piece;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import com.google.gson.Gson;



/**
 * Layer file which is going to contain commands sent to the Rest API running on the game server
 *@author rsyed
 */
public class Layer {
	private String url;				//Stores the URL to connect to
	private Gson gson;				//Gson object to convert classes to Json
	private String gameSessionURL;
	
	//Data Blocks for the Layer
	private GameSessionResponse gsRes;
	private String gameSessionID;	
	
	private JoinGameResponse jgRes;
	private String teamSecret;
	private String teamID;
	private String teamColor;
	
	
	
	
	/**
	 * Creates a Layer Object which can then be used to communicate with the Server 
	 * The URL and the port the layer binds to are given on object creation
	 * @param url
	 * @param po
	 */
	public Layer(String url) {
		this.url = url;
		gson = new Gson();	//creates a gson Object on creation to conserve memory
	}
	
	
	/**
	 * Creates a Game Session if object is not connected to any game session
	 * Receives a template object which it uses to create the body for the API request
	 * Returns CODE
	 * 200 Game session created
	 * 500 Unknown error occurred
	 * @param template
	 * 
	 */
	public int createGameSession(MapTemplate template) throws URISyntaxException, InterruptedException, IOException {
		int code = 500;
		if( gameSessionID == null ) {												
			GameSessionRequest gametemp = new GameSessionRequest();
			gametemp.setTemplate(template);
			
			String sessionString = gson.toJson(gametemp);
			
			HttpRequest postRequest = HttpRequest.newBuilder()
						.uri(new URI(url))
						.header("Content-Type", "application/json")
						.POST(BodyPublishers.ofString(sessionString))
						.build();
			
			HttpClient httpClient = HttpClient.newHttpClient();
			
			HttpResponse<String> postResponse = httpClient.send(postRequest, BodyHandlers.ofString());
			
			gsRes = gson.fromJson(postResponse.body(), GameSessionResponse.class);
			gameSessionID = gsRes.getId();
			
			//Getting Response code
			code = postResponse.statusCode();
			if ( code == 200 ) {
				gameSessionURL = url + "/" + gameSessionID + "/";
			}
		}
		
		return code;
	}
	
	/**
	 * Makes a move on the board if object is connected to a game session and a game is joined
	 * Takes a Move Object as an input 
	 * Returns
	 * 200 Valid move
	 * 403 Move is forbidden for given team (anti-cheat)
	 * 404 Game session not found
	 * 409 Invalid move
	 * 410 Game is over
	 * 500 Unknown error occurred
	 * @param mov
	 */
	public int makeMove(Move mov) throws URISyntaxException, InterruptedException, IOException {
		int code = 500;
		if( gameSessionID!=null && teamID!=null ) {
			
			MoveRequest moveReq = new MoveRequest();
			moveReq.setTeamId(teamID);
			moveReq.setTeamSecret(teamSecret);
			moveReq.setPieceId(mov.getPieceId());
			moveReq.setNewPosition(mov.getNewPosition());
			
			String sessionString = gson.toJson(moveReq);
			
			HttpRequest postRequest = HttpRequest.newBuilder()
						.uri(new URI(gameSessionURL + "move"))
						.header("Content-Type", "application/json")
						.POST(BodyPublishers.ofString(sessionString))
						.build();
			
			HttpClient httpClient = HttpClient.newHttpClient();
			
			HttpResponse<String> postResponse = httpClient.send(postRequest, BodyHandlers.ofString());
			
			code = postResponse.statusCode();
		}
		return code;
	}
	
	/**
	 * Joins a Game Session if object is connected to a game session
	 * Receives a teamName which is used as teamID
	 * Return Codes
	 * 200 Team joined
	 * 404 Game session not found
	 * 429 No more team slots available
	 * 500 Unknown error occurred
	 * @param teamName
	 * 
	 */
	public int joinGame(String teamName) throws URISyntaxException, InterruptedException, IOException {
		int code = 500;
		if( gameSessionID!=null ) {												
			JoinGameRequest joinGameRequest = new JoinGameRequest();
			joinGameRequest.setTeamId(teamName);
			
			String sessionString = gson.toJson(joinGameRequest);
			
			HttpRequest postRequest = HttpRequest.newBuilder()
						.uri(new URI(gameSessionURL + "join"))
						.header("Content-Type", "application/json")
						.POST(BodyPublishers.ofString(sessionString))
						.build();
			
			HttpClient httpClient = HttpClient.newHttpClient();
			
			HttpResponse<String> postResponse = httpClient.send(postRequest, BodyHandlers.ofString());
			
			jgRes = gson.fromJson(postResponse.body(), JoinGameResponse.class);
			
			code = postResponse.statusCode();
			
			if( code==200 ) {
				teamSecret = jgRes.getTeamSecret();
				teamID = jgRes.getTeamId();
				
				//TODO Team Color coming up empty in testing
				teamColor = jgRes.getTeamColor();
				
//				DebugCODE to check Data
//				System.out.println(teamSecret);
//				System.out.println(teamID);
//				System.out.println(teamColor);
			}
			return code;
		}
			

		return code;
	}
	
	/**
	 * Gives up in Game Session if object is connected to a game session and a game is joined
	 * Return Codes
	 * 200 Request completed
	 * 403 Give up is forbidden for given team (anti-cheat)
	 * 404 Game session not found
	 * 410 Game is over
	 * 500 Unknown error occurred
	 */
	public int giveUp() throws URISyntaxException, InterruptedException, IOException {
		int code = 500;
		if( gameSessionID!=null && teamID!=null ) {												
			GiveupRequest giveUpRequest = new GiveupRequest();
			giveUpRequest.setTeamId(teamID);
			giveUpRequest.setTeamSecret(teamSecret);
			
			String sessionString = gson.toJson(giveUpRequest);
			
			HttpRequest postRequest = HttpRequest.newBuilder()
						.uri(new URI(gameSessionURL + "giveup"))
						.header("Content-Type", "application/json")
						.POST(BodyPublishers.ofString(sessionString))
						.build();
			
			HttpClient httpClient = HttpClient.newHttpClient();
			
			HttpResponse<String> postResponse = httpClient.send(postRequest, BodyHandlers.ofString());
			
			code = postResponse.statusCode();
			return code;
		}
			return code;  //DUMMY RETURN: Can be modified to a custom code so that we can use it for error handling later
	}	
	
	/**
	 * Gets the current Session
	 * Return Codes
	 * 200 Game session response returned
	 * 404 Game session not found
	 * 500 Unknown error occurred
	 */
	public int getCurrentSession() throws URISyntaxException, InterruptedException, IOException {
		int code = 500;
		if ( gameSessionID != null ) {
			
			HttpRequest postRequest = HttpRequest.newBuilder()
						.uri(new URI(url + "/" + gameSessionID))
						.header("Content-Type", "application/json")
						.build();
			
			HttpClient httpClient = HttpClient.newHttpClient();
			
			HttpResponse<String> postResponse = httpClient.send(postRequest, BodyHandlers.ofString());
			
			gsRes = gson.fromJson(postResponse.body(), GameSessionResponse.class);
			code = postResponse.statusCode();
			return code;
		}
		return code;
	}
	
	/**
	 * Gets the current Session
	 * Return Codes
	 * 200 Game session response returned
	 * 404 Game session not found
	 * 500 Unknown error occurred
	 */
	public int deleteCurrentSession() throws URISyntaxException, InterruptedException, IOException {
		int code = 500;
		if ( gameSessionID != null ) {
			
			HttpRequest postRequest = HttpRequest.newBuilder()
						.uri(new URI(url + "/" + gameSessionID))
						.header("Content-Type", "application/json")
						.DELETE()
						.build();
			
			HttpClient httpClient = HttpClient.newHttpClient();
			
			HttpResponse<String> postResponse = httpClient.send(postRequest, BodyHandlers.ofString());
			
			code = postResponse.statusCode();
			reset();
			return code;
		}
		return code;
	}
	
	/**
	 * Method to reset the current layer to its default state
	 */
	private String reset() {
		gameSessionURL = null;
		gsRes = null;
		gameSessionID = null;	
		jgRes = null;
		teamSecret = null;
		teamID = null;
		teamColor = null;
		return "Layer Reset";
	}
	
	/**
	 * Gets the current Session
	 * Returns Game State Object
	 * Codes internally thrown
	 * 200 Game state returned
	 * 404 Game session not found
	 * 500 Unknown error occurred
	 */
	public GameState getCurrentState() throws URISyntaxException, InterruptedException, IOException {

			HttpRequest postRequest = HttpRequest.newBuilder()
					.uri(new URI(gameSessionURL + "state"))
					.header("Content-Type", "application/json")
					.build();
		
		HttpClient httpClient = HttpClient.newHttpClient();
		
		HttpResponse<String> postResponse = httpClient.send(postRequest, BodyHandlers.ofString());
		
		return gson.fromJson(postResponse.body(), GameState.class);
	}

	
	
	//Testing
	public static void main(String[] args) throws Exception {
		String testJson = """
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
		Layer ne = new Layer("http://localhost:8080/api/gamesession");
		Gson gson1 = new Gson();
		MapTemplate template = gson1.fromJson(testJson, MapTemplate.class);
		System.out.println(ne.createGameSession(template));
		System.out.println(ne.joinGame("Seph"));
		System.out.println(ne.getCurrentSession());
		System.out.println(ne.deleteCurrentSession());
	}
	

	
}
