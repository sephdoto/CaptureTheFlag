package org.ctf.model;

import org.ctf.shared.state.data.map.PieceDescription;
import org.ctf.shared.state.data.map.PlacementType;

public record MapTemplate(int[] gridSize,int teams,int flags,PieceDescription[] pieces,int blocks,PlacementType placement,int totalTimeLimitInSeconds,int moveTimeLimitInSeconds) {

}
