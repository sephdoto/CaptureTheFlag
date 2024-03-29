package org.ctf.model;

public record MoveRequest(String teamId, String teamSecret, String pieceId, int[] newPosition) {

}
