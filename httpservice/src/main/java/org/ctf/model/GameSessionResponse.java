package org.ctf.model;

import java.util.Date;

public record GameSessionResponse(
    String id, Date gameStarted, Date gameEnded, boolean gameOver, String[] winner) {
}
