package org.ctf.TL;

import de.unimannheim.swt.pse.ctf.controller.data.GameSessionRequest;
import de.unimannheim.swt.pse.ctf.controller.data.GameSessionResponse;
import de.unimannheim.swt.pse.ctf.game.map.MapTemplate;

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
	public String url;	//Stores the URL to connect to
	Gson gson;			//Gson object to convert classes to Json
	
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
	 * Creates a Game Session
	 * Receives a template object which it uses to create the body for the API request
	 * @param template
	 * 
	 */
	public int createGameSession(MapTemplate template) throws URISyntaxException, InterruptedException, IOException {
		GameSessionRequest gametemp = new GameSessionRequest();
		gametemp.setTemplate(template);
		
		String sessionString = gson.toJson(gametemp);
		
		HttpRequest postRequest = HttpRequest.newBuilder()
					.uri(new URI(url))
					.POST(BodyPublishers.ofString(sessionString))
					.build();
		
		HttpClient httpClient = HttpClient.newHttpClient();
		
		HttpResponse<String> postResponse = httpClient.send(postRequest, BodyHandlers.ofString());
		
		GameSessionResponse gsRes = gson.fromJson(postResponse.body(), GameSessionResponse.class);
		
		
		//Getting Response code
		int code = postResponse.statusCode();
		return code;
		//Code for 200
		
		
		//Code for 500
	}
	
	
	//Testing
	public static void main(String[] args) throws Exception{
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
		System.out.println(template.getTeams());
		System.out.println(ne.createGameSession(template));
//		ne.createGameSession(template);
		
	}
	

	
}
