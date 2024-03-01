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
		String testJson = " {\r\n"
				+ "  \"gridSize\": [10, 10],\r\n"
				+ "  \"teams\": 2,\r\n"
				+ "  \"flags\": 1,\r\n"
				+ "  \"blocks\": 0,\r\n"
				+ "  \"pieces\": [\r\n"
				+ "    {\r\n"
				+ "      \"type\": \"Pawn\",\r\n"
				+ "      \"attackPower\": 1,\r\n"
				+ "      \"count\": 10,\r\n"
				+ "      \"movement\": {\r\n"
				+ "        \"directions\": {\r\n"
				+ "          \"left\": 0,\r\n"
				+ "          \"right\": 0,\r\n"
				+ "          \"up\": 1,\r\n"
				+ "          \"down\": 0,\r\n"
				+ "          \"upLeft\": 1,\r\n"
				+ "          \"upRight\": 1,\r\n"
				+ "          \"downLeft\": 0,\r\n"
				+ "          \"downRight\": 0\r\n"
				+ "        }\r\n"
				+ "      }\r\n"
				+ "    },\r\n"
				+ "    {\r\n"
				+ "      \"type\": \"Rook\",\r\n"
				+ "      \"attackPower\": 5,\r\n"
				+ "      \"count\": 2,\r\n"
				+ "      \"movement\": {\r\n"
				+ "        \"directions\": {\r\n"
				+ "          \"left\": 2,\r\n"
				+ "          \"right\": 2,\r\n"
				+ "          \"up\": 2,\r\n"
				+ "          \"down\": 2,\r\n"
				+ "          \"upLeft\": 0,\r\n"
				+ "          \"upRight\": 0,\r\n"
				+ "          \"downLeft\": 0,\r\n"
				+ "          \"downRight\": 0\r\n"
				+ "        }\r\n"
				+ "      }\r\n"
				+ "    },\r\n"
				+ "    {\r\n"
				+ "      \"type\": \"Knight\",\r\n"
				+ "      \"attackPower\": 3,\r\n"
				+ "      \"count\": 2,\r\n"
				+ "      \"movement\": {\r\n"
				+ "        \"shape\": {\r\n"
				+ "          \"type\": \"lshape\"\r\n"
				+ "        }\r\n"
				+ "      }\r\n"
				+ "    },\r\n"
				+ "    {\r\n"
				+ "      \"type\": \"Bishop\",\r\n"
				+ "      \"attackPower\": 3,\r\n"
				+ "      \"count\": 2,\r\n"
				+ "      \"movement\": {\r\n"
				+ "        \"directions\": {\r\n"
				+ "          \"left\": 0,\r\n"
				+ "          \"right\": 0,\r\n"
				+ "          \"up\": 0,\r\n"
				+ "          \"down\": 0,\r\n"
				+ "          \"upLeft\": 2,\r\n"
				+ "          \"upRight\": 2,\r\n"
				+ "          \"downLeft\": 2,\r\n"
				+ "          \"downRight\": 2\r\n"
				+ "        }\r\n"
				+ "      }\r\n"
				+ "    },\r\n"
				+ "    {\r\n"
				+ "      \"type\": \"Queen\",\r\n"
				+ "      \"attackPower\": 5,\r\n"
				+ "      \"count\": 1,\r\n"
				+ "      \"movement\": {\r\n"
				+ "        \"directions\": {\r\n"
				+ "          \"left\": 2,\r\n"
				+ "          \"right\": 2,\r\n"
				+ "          \"up\": 2,\r\n"
				+ "          \"down\": 2,\r\n"
				+ "          \"upLeft\": 2,\r\n"
				+ "          \"upRight\": 2,\r\n"
				+ "          \"downLeft\": 2,\r\n"
				+ "          \"downRight\": 2\r\n"
				+ "        }\r\n"
				+ "      }\r\n"
				+ "    },\r\n"
				+ "    {\r\n"
				+ "      \"type\": \"King\",\r\n"
				+ "      \"attackPower\": 1,\r\n"
				+ "      \"count\": 1,\r\n"
				+ "      \"movement\": {\r\n"
				+ "        \"directions\": {\r\n"
				+ "          \"left\": 1,\r\n"
				+ "          \"right\": 1,\r\n"
				+ "          \"up\": 1,\r\n"
				+ "          \"down\": 1,\r\n"
				+ "          \"upLeft\": 1,\r\n"
				+ "          \"upRight\": 1,\r\n"
				+ "          \"downLeft\": 1,\r\n"
				+ "          \"downRight\": 1\r\n"
				+ "        }\r\n"
				+ "      }\r\n"
				+ "    }\r\n"
				+ "  ],\r\n"
				+ "  \"placement\": \"symmetrical\",\r\n"
				+ "  \"totalTimeLimitInSeconds\": -1,\r\n"
				+ "  \"moveTimeLimitInSeconds\": -1\r\n"
				+ "}\r\n";
		Layer ne = new Layer("http://localhost:8080/api/gamesesion");
		Gson gson1 = new Gson();
		MapTemplate template = gson1.fromJson(testJson, MapTemplate.class);
		System.out.println(template.getTeams());
		System.out.println(ne.createGameSession(template));
//		ne.createGameSession(template);
		
		
	}
	

	
}
