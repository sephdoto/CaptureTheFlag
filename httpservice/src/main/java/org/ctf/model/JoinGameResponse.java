package org.ctf.model;

public record JoinGameResponse(String gameSessionId, String teamSecret, String teamId, String teamColor) {

}
