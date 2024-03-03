package org.ctf.TL.GameEngine;

import de.unimannheim.swt.pse.ctf.controller.data.GameSessionRequest;
import de.unimannheim.swt.pse.ctf.controller.data.GameSessionResponse;
import de.unimannheim.swt.pse.ctf.controller.data.GiveupRequest;
import de.unimannheim.swt.pse.ctf.controller.data.JoinGameRequest;
import de.unimannheim.swt.pse.ctf.controller.data.JoinGameResponse;
import de.unimannheim.swt.pse.ctf.controller.data.MoveRequest;
import de.unimannheim.swt.pse.ctf.game.map.MapTemplate;
import de.unimannheim.swt.pse.ctf.game.state.GameState;
import de.unimannheim.swt.pse.ctf.game.state.Move;
import de.unimannheim.swt.pse.ctf.game.state.Team;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.ctf.TL.Exceptions.ApiError;

import de.unimannheim.swt.pse.ctf.game.exceptions.Accepted;
import de.unimannheim.swt.pse.ctf.game.exceptions.ForbiddenMove;
import de.unimannheim.swt.pse.ctf.game.exceptions.GameOver;
import de.unimannheim.swt.pse.ctf.game.exceptions.InvalidMove;
import de.unimannheim.swt.pse.ctf.game.exceptions.NoMoreTeamSlots;
import de.unimannheim.swt.pse.ctf.game.exceptions.SessionNotFound;
import de.unimannheim.swt.pse.ctf.game.exceptions.UnknownError;

import com.google.gson.Gson;

/**
 * Layer file which is going to contain commands sent to the Rest API running on
 * the game server
 * 
 * @author rsyed
 * @return Layer Object
 */
public class Layer {
	private String url; // Stores the URL to connect to
	private Gson gson; // Gson object to convert classes to Json
	private String urlWithID;

	// Data Blocks for the Layer
	private GameSessionResponse gameSessionResponse;
	private String gameSessionID; // Stores the sessionId

	private JoinGameResponse joinGameResponse;
	private String teamSecret; //Only set when a valid join is done
	private String teamID;
	private String teamColor;

	/**
	 * Creates a Layer Object which can then be used to communicate with the Server
	 * The URL and the port the layer binds to are given on object creation
	 * 
	 * @param url
	 */
	public Layer(String url) {
		this.url = url;
		gson = new Gson(); // creates a gson Object on creation to conserve memory
	}

	/**
	 * Creates a Game Session if object is not connected to any game session
	 * Receives a template object which it uses to create the body for the API
	 * request
	 * Returns GameSessionResponse and HTTP Codes
	 * 200 Game session created
	 * 500 Unknown error occurred
	 * 
	 * @param template
	 */
	public void createGameSession(MapTemplate template) {
		// TODO decide the return type of the Method. The Response alone isnt a
		// GameSession Object
		GameSessionRequest gsr = new GameSessionRequest();
		gsr.setTemplate(template);

		String jsonPayload = gson.toJson(gsr);
		// Performs the POST request

		HttpResponse<String> serverResponse = POSTRequest(url, jsonPayload);

		// Parses Server Response to expected class
		gameSessionResponse = gson.fromJson(serverResponse.body(), GameSessionResponse.class);

		int returnedCode = serverResponse.statusCode();

			if (returnedCode == 200) {
				gameSessionID = gameSessionResponse.getId(); // Sets the Session ID for the Layer
				urlWithID = url + "/" + gameSessionID; // Creates URL with Session ID for use later
				throw new Accepted();
			} else if (returnedCode == 500) {
				throw new UnknownError();
			}
	}

	/**
	 * Makes a move on the board if object is connected to a game session and a game
	 * is joined
	 * Takes a Move Object as an input
	 * Returns
	 * 200 Valid move
	 * 403 Move is forbidden for given team (anti-cheat)
	 * 404 Game session not found
	 * 409 Invalid move
	 * 410 Game is over
	 * 500 Unknown error occurred
	 * 
	 * @param mov
	 */
	public void makeMove(Move mov) {
		MoveRequest moveReq = new MoveRequest();
		moveReq.setTeamId(teamID);
		moveReq.setTeamSecret(teamSecret);
		moveReq.setPieceId(mov.getPieceId());
		moveReq.setNewPosition(mov.getNewPosition());

		HttpResponse<String> postResponse = POSTRequest(urlWithID + "/move", gson.toJson(moveReq));

		int returnedCode = postResponse.statusCode();

			if (returnedCode == 200) {
				throw new Accepted();
			} else if (returnedCode == 403) {
				throw new ForbiddenMove();
			} else if (returnedCode == 404) {
				throw new SessionNotFound();
			} else if (returnedCode == 409) {
				throw new InvalidMove();
			} else if (returnedCode == 410) {
				throw new GameOver();
			} else if (returnedCode == 500) {
				throw new UnknownError();
			}
	}

	/**
	 * Joins a Game Session if object is connected to a game session
	 * Receives a teamName which is used as teamID
	 * Return Codes
	 * 200 Team joined
	 * 404 Game session not found
	 * 429 No more team slots available
	 * 500 Unknown error occurred
	 * 
	 * @param teamName
	 * 
	 * @return String JSON
	 */
	public Team joinGame(String teamName) {

		JoinGameRequest joinGameRequest = new JoinGameRequest();
		joinGameRequest.setTeamId(teamName);

		HttpResponse<String> postResponse = POSTRequest(urlWithID + "/join", gson.toJson(joinGameRequest));

		joinGameResponse = gson.fromJson(postResponse.body(), JoinGameResponse.class);

		int returnedCode = postResponse.statusCode();

		// TODO Team Color coming up empty in testing
		if (returnedCode == 404) {
			throw new SessionNotFound();
		} else if (returnedCode == 429) {
			throw new NoMoreTeamSlots();
		} else if (returnedCode == 500) {
			throw new UnknownError();
		} else {
		teamSecret = joinGameResponse.getTeamSecret();
		teamID = joinGameResponse.getTeamId();
		teamColor = joinGameResponse.getTeamColor();
		Team returnObject = new Team();
		returnObject.setId(teamID);
		returnObject.setColor(teamColor);
		return returnObject;
		}
		
	}

	/**
	 * Gives up in Game Session if object is connected to a game session and a game
	 * is joined
	 * Return Codes
	 * 200 Request completed
	 * 403 Give up is forbidden for given team (anti-cheat)
	 * 404 Game session not found
	 * 410 Game is over
	 * 500 Unknown error occurred
	 */
	public void giveUp() {

		GiveupRequest giveUpRequest = new GiveupRequest();
		giveUpRequest.setTeamId(teamID);
		giveUpRequest.setTeamSecret(teamSecret);

		HttpResponse<String> postResponse = POSTRequest(urlWithID + "/giveup", gson.toJson(giveUpRequest));

		int returnedCode = postResponse.statusCode();
			if (returnedCode == 200) {
				throw new Accepted();
			} else if (returnedCode == 403) {
				throw new ForbiddenMove();
			} else if (returnedCode == 404) {
				throw new SessionNotFound();
			} else if (returnedCode == 410) {
				throw new GameOver();
			} else {
				throw new UnknownError();
			}
	}

	/**
	 * Gets the current Session
	 * Return Codes
	 * 200 Game session response returned
	 * 404 Game session not found
	 * 500 Unknown error occurred
	 */
	public void getCurrentSession() {
		// TODO decide the return type of the Method.
		HttpResponse<String> getResponse = GETRequest(urlWithID);
		// TODO Object returned from Server. Decide what to do with it
		gameSessionResponse = gson.fromJson(getResponse.body(), GameSessionResponse.class);

		int returnedCode = getResponse.statusCode();

			if (returnedCode == 200) {
				throw new Accepted();
			} else if (returnedCode == 404) {
				throw new SessionNotFound();
			} else if (returnedCode == 500) {
				throw new UnknownError();
			}
	}

	/**
	 * Delets the current Session
	 * Return Codes
	 * 200 Game session response returned
	 * 404 Game session not found
	 * 500 Unknown error occurred
	 */
	public void deleteCurrentSession() {
		HttpResponse<String> deleteResponse = DELETERequest(urlWithID);

		int returnedCode = deleteResponse.statusCode();

			 if (returnedCode == 404) {
				throw new SessionNotFound();
			} else if (returnedCode == 500) {
				throw new UnknownError();
			} else {
				throw new Accepted();
			}
	}

	/**
	 * Gets the current Session
	 * Returns Game State Object
	 * Codes internally thrown
	 * 200 Game state returned
	 * 404 Game session not found
	 * 500 Unknown error occurred
	 * 
	 * @return GameState
	 */
	public GameState getCurrentState() {
		HttpResponse<String> getResponse = GETRequest(urlWithID + "/state");

		GameState returnedState = gson.fromJson(getResponse.body(), GameState.class);

		int returnedCode = getResponse.statusCode();

			if (returnedCode == 404) {
				throw new ApiError.ApiErrorBuilder(404).message("Game session not found").build();
			} else if (returnedCode == 500) {
				throw new ApiError.ApiErrorBuilder(500).message("Unknown error occurred").build();
			} else {
				return returnedState;
			}
	}

	/**
	 * Method to perform a POST HTTP Request and
	 * 
	 * @param urlInput    baseURL
	 * @param jsonPayload String Representation of a json
	 * @return HttpResponse<String>
	 */
	private HttpResponse<String> POSTRequest(String urlInput, String jsonPayload) {

		HttpResponse<String> ret = null;
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(new URI(urlInput))
					.header("Content-Type", "application/json")
					.POST(BodyPublishers.ofString(jsonPayload))
					.build();
			ret = HttpClient.newHttpClient().send(request, BodyHandlers.ofString());
			return ret;
		} catch (URISyntaxException | IOException | InterruptedException | NullPointerException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * Method which takes a URL and performs a GET HTTP request and returns the
	 * response
	 * 
	 * @param urlInput
	 * @return HttpResponse<String>
	 */
	private HttpResponse<String> GETRequest(String urlInput) {
		HttpResponse<String> ret = null;
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(new URI(urlInput))
					.header("Content-Type", "application/json")
					.GET()
					.build();
			ret = HttpClient.newHttpClient().send(request, BodyHandlers.ofString());
		} catch (URISyntaxException | IOException | InterruptedException | NullPointerException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * Method which takes a URL and performs a DELETE HTTP request and returns the
	 * response
	 * 
	 * @param urlInput
	 * @return HttpResponse<String>
	 */
	private HttpResponse<String> DELETERequest(String urlInput) {
		HttpResponse<String> ret = null;
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(new URI(urlInput))
					.header("Content-Type", "application/json")
					.DELETE()
					.build();
			ret = HttpClient.newHttpClient().send(request, BodyHandlers.ofString());
		} catch (URISyntaxException | IOException | InterruptedException | NullPointerException e) {
			e.printStackTrace();
		}
		return ret;
	}
}