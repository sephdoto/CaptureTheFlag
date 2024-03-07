package org.ctf.TL.layer;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.ctf.client.data.map.MapTemplate;
import org.ctf.client.data.wrappers.GameSessionRequest;
import org.ctf.client.data.wrappers.GameSessionResponse;
import org.ctf.client.data.wrappers.GiveupRequest;
import org.ctf.client.data.wrappers.JoinGameRequest;
import org.ctf.client.data.wrappers.JoinGameResponse;
import org.ctf.client.data.wrappers.MoveRequest;
import org.ctf.client.exceptions.Accepted;
import org.ctf.client.exceptions.ForbiddenMove;
import org.ctf.client.exceptions.GameOver;
import org.ctf.client.exceptions.InvalidMove;
import org.ctf.client.exceptions.NoMoreTeamSlots;
import org.ctf.client.exceptions.SessionNotFound;
import org.ctf.client.exceptions.URLError;
import org.ctf.client.exceptions.UnknownError;
import org.ctf.client.state.GameState;
import org.ctf.client.state.Move;

import com.google.gson.Gson;

/**
 * Layer file which is going to contain commands sent to the Rest API running on
 * the game server
 * 
 * @author rsyed
 * @return Layer Object
 */
public class CommLayer implements CommLayerInterface {

	// Data Blocks for the Layer
	private Gson gson; // Gson object to convert classes to Json

	/**
	 * Creates a Layer Object which can then be used to communicate with the Server
	 * The URL and the port the layer binds to are given on object creation
	 * 
	 * @param url
	 *            Example URL http://localhost:8080
	 */
	public CommLayer() {
		gson = new Gson(); // creates a gson Object on creation to conserve memory
	}

	/**
	 * Creates a Game Session if object is not connected to any game session
	 * Receives a template object which it uses to create the body for the API
	 * request
	 * Returns GameSessionResponse containing a session ID, start and end date,
	 * Gameover flag, and winner flag
	 * 200 Game session created
	 * 500 Unknown error occurred
	 * 
	 * @param URL
	 * @param map
	 * 
	 * @returns GameSessionResponse
	 * @throws UnknownError
	 * @throws URLError
	 * @throws Accepted
	 */
	@Override
	public GameSessionResponse createGameSession(String URL, MapTemplate map) {
		GameSessionRequest gsr = new GameSessionRequest();
		gsr.setTemplate(map);

		String jsonPayload = gson.toJson(gsr);

		// Performs the POST request
		HttpResponse<String> serverResponse = POSTRequest(URL, jsonPayload);

		// Parses Server Response to expected class
		GameSessionResponse gameSessionResponse = gson.fromJson(serverResponse.body(), GameSessionResponse.class);

		// Saves the code of the server response
		int returnedCode = serverResponse.statusCode();

		if (returnedCode == 500) {
			throw new UnknownError();
		} else if (returnedCode == 404) {
			throw new URLError("URL Error");
		}
		return gameSessionResponse;
	}

	/**
	 * Joins a Game Session if object is connected to a game session
	 * Receives a
	 * String URL to use while making the request
	 * String for proposed teamID
	 * Return Codes
	 * 200 Team joined
	 * 404 Game session not found
	 * 429 No more team slots available
	 * 500 Unknown error occurred
	 * 
	 * The Return is data needed for your team to communicate with
	 * 
	 * @param URL
	 * @param teamName
	 * 
	 * @return String JSON
	 * 
	 * @throws SessionNotFound
	 * @throws NoMoreTeamSlots
	 * @throws UnknownError
	 * @throws Accepted
	 */
	@Override
	public JoinGameResponse joinGame(String URL, String teamName) {

		JoinGameRequest joinGameRequest = new JoinGameRequest();
		joinGameRequest.setTeamId(teamName);

		HttpResponse<String> postResponse = POSTRequest(URL + "/join", gson.toJson(joinGameRequest));

		JoinGameResponse joinGameResponse = gson.fromJson(postResponse.body(), JoinGameResponse.class);

		int returnedCode = postResponse.statusCode();

		if (returnedCode == 404) {
			throw new SessionNotFound();
		} else if (returnedCode == 429) {
			throw new NoMoreTeamSlots();
		} else if (returnedCode == 500) {
			throw new UnknownError();
		}

		return joinGameResponse;
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
	 * @param URL
	 * @param teamID
	 * @param teamSecret
	 * @param move
	 * 
	 * @throws Accepted
	 * @throws ForbiddenMove
	 * @throws SessionNotFound
	 * @throws InvalidMove
	 * @throws GameOver
	 * @throws UnknownError
	 */
	@Override
	public void makeMove(String URL, String teamID, String teamSecret, Move move) {
		MoveRequest moveReq = new MoveRequest();
		moveReq.setTeamId(teamID);
		moveReq.setTeamSecret(teamSecret);
		moveReq.setPieceId(move.getPieceId());
		moveReq.setNewPosition(move.getNewPosition());

		HttpResponse<String> postResponse = POSTRequest(URL + "/move", gson.toJson(moveReq));

		int returnedCode = postResponse.statusCode();

		if (returnedCode == 200) {
			throw new Accepted(200);
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
	 * Gives up in Game Session if object is connected to a game session and a game
	 * is joined
	 * Return Codes
	 * 200 Request completed
	 * 403 Give up is forbidden for given team (anti-cheat)
	 * 404 Game session not found
	 * 410 Game is over
	 * 500 Unknown error occurred
	 * 
	 * @param URL
	 * @param teamID
	 * @param teamSecret
	 * 
	 * @throws Accepted
	 * @throws SessionNotFound
	 * @throws ForbiddenMove
	 * @throws GameOver
	 * @throws UnknownError
	 */
	@Override
	public void giveUp(String URL, String teamID, String teamSecret) {

		GiveupRequest giveUpRequest = new GiveupRequest();
		giveUpRequest.setTeamId(teamID);
		giveUpRequest.setTeamSecret(teamSecret);

		HttpResponse<String> postResponse = POSTRequest(URL + "/giveup", gson.toJson(giveUpRequest));

		int returnedCode = postResponse.statusCode();
		if (returnedCode == 200) {
			throw new Accepted(200);
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
	 * Gets the State of the current Session
	 * Return Codes
	 * 200 Game session response returned
	 * 404 Game session not found
	 * 500 Unknown error occurred
	 * 
	 * @param URL
	 * @return GameSessionResponse
	 * @throws Accepted
	 * @throws SessionNotFound
	 * @throws UnknownError
	 */
	@Override
	public GameSessionResponse getCurrentSessionState(String URL) {
		HttpResponse<String> getResponse = GETRequest(URL);
		GameSessionResponse gameSessionResponse = gson.fromJson(getResponse.body(), GameSessionResponse.class);

		int returnedCode = getResponse.statusCode();

		if (returnedCode == 404) {
			throw new SessionNotFound();
		} else if (returnedCode == 500) {
			throw new UnknownError();
		}

		return gameSessionResponse;
	}

	/**
	 * Deletes the current Session
	 * Return Codes
	 * 200 Game session response returned
	 * 404 Game session not found
	 * 500 Unknown error occurred
	 * 
	 * @param URL
	 * @throws Accepted
	 * @throws SessionNotFound
	 * @throws UnknownError
	 * 
	 */
	@Override
	public void deleteCurrentSession(String URL) {
		HttpResponse<String> deleteResponse = DELETERequest(URL);

		int returnedCode = deleteResponse.statusCode();

		if (returnedCode == 404) {
			throw new SessionNotFound();
		} else if (returnedCode == 500) {
			throw new UnknownError();
		}
	}

	/**
	 * Gets the current Session
	 * Returns GameState Object
	 * Codes internally thrown
	 * 200 Game state returned
	 * 404 Game session not found
	 * 500 Unknown error occurred
	 * 
	 * @param URL
	 * @return GameState
	 * @throws Accepted
	 * @throws SessionNotFound
	 * @throws UnknownError
	 */
	@Override
	public GameState getCurrentGameState(String URL) {
		HttpResponse<String> getResponse = GETRequest(URL + "/state");

		GameState returnedState = gson.fromJson(getResponse.body(), GameState.class);

		int returnedCode = getResponse.statusCode();

		if (returnedCode == 404) {
			throw new SessionNotFound();
		} else if (returnedCode == 500) {
			throw new UnknownError();
		}
		return returnedState;
	}

	/**
	 * Method to perform a POST HTTP Request and
	 * 
	 * @param URL
	 * @param jsonPayload String Representation of a json
	 * @return HttpResponse<String>
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws NullPointerException
	 * 
	 */
	private HttpResponse<String> POSTRequest(String URL, String jsonPayload) {

		HttpResponse<String> ret = null;
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(new URI(URL))
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
	 * @param URL
	 * @return HttpResponse<String>
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws NullPointerException
	 */
	private HttpResponse<String> GETRequest(String URL) {
		HttpResponse<String> ret = null;
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(new URI(URL))
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
	 * @param URL
	 * @return HttpResponse<String>
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws NullPointerException
	 */
	private HttpResponse<String> DELETERequest(String URL) {
		HttpResponse<String> ret = null;
		try {
			HttpRequest request = HttpRequest.newBuilder()
					.uri(new URI(URL))
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