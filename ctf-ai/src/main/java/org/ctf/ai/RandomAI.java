package org.ctf.ai;

import org.ctf.client.state.GameState;
import org.ctf.client.state.Piece;
import org.ctf.client.state.Move;
import org.ctf.client.state.data.map.Directions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


/**
 * @author sistumpf
 * Everything needed for choosing a random move
 */
public class RandomAI {

	/**
	 * Given a GameState, the pickMove method is able to randomly choose the next move.
	 * From all pieces a random piece is chosen, if it is able to move its move is randomly chosen.
	 * If the piece is not able to move a new piece is chosen from the remaining pieces.
	 * If no move is possible a NoMovesLeftException is thrown.
	 * @param gameState
	 * @return a random Move
	 * @throws NoMovesLeftException
	 */
	public static Move pickMove(GameState gameState) throws NoMovesLeftException {
		ArrayList<Piece> pieceList = new ArrayList<Piece>();
		Move move = new Move();
		
		for(Piece piece : gameState.getTeams()[gameState.getCurrentTeam()].getPieces())
			pieceList.add(piece);
		
		while(pieceList.size() > 0) {		
			int random = (int)(Math.random() * pieceList.size());
			Piece picked = pieceList.get(random);
			
			if(picked.getDescription().getMovement().getDirections() != null) {		//move if Directions
				HashMap<Integer,Integer> dirMap = new HashMap<Integer,Integer>();
				for(int i=0; i<8; i++) {
					int reach = getReach(picked.getDescription().getMovement().getDirections(), i);
					if(reach > 0) {
						if(validDirection(gameState, pieceList.get(random), i)) {
							dirMap.put(i, reach);
						} else {
							continue;
						}
					}
				}
				if(dirMap.size() > 0) {
					return getDirectionMove(dirMap, picked, gameState);
				} else {
					pieceList.remove(random);
					continue;
				}
				
			} else {	//Move if Shape
				// shape
			}
			
		}
		
		if(pieceList.size() == 0)
			throw new NoMovesLeftException(gameState.getTeams()[gameState.getCurrentTeam()].getId());

		
		return move;
	}
	
	/**
	 * 
	 * @param dirMap
	 * @param piece
	 * @param gameState
	 * @return
	 */
	static Move getDirectionMove(HashMap<Integer,Integer> dirMap, Piece piece, GameState gameState) {
		int randomKey = (int)dirMap.keySet().toArray()[(int)(dirMap.size() * Math.random())];
		int reach;
		
		while(true) {
			reach = (int)(Math.random() * dirMap.get(randomKey) +1);
			Move newPos = checkMoveValidity(gameState, piece, randomKey, reach);
			if(newPos != null)
				return newPos;
			continue;
		}
	}
	
	/**
	 * Checks if a piece can walk any fields into a given direction.
	 * The direction is given as a number (0-7).
	 * @param gameState
	 * @param piece
	 * @param direction
	 * @return false if there are no possible moves in this direction, true otherwise.
	 */
	static boolean validDirection(GameState gameState, Piece piece, int direction) {
		return checkMoveValidity(gameState, piece, direction, 1) != null;
	}
	
	/**
	 * Returns the Move if a piece can walk onto a specific position.
	 * The direction is given as a number (0-7) and and int that specifies how many fields into that direction.
	 * @param gameState
	 * @param piece
	 * @param direction
	 * @param reach
	 * @return a Move instance with the piece and its new position
	 * @return null if the piece can't occupy the position or the position is not in the grid
	 */
	static Move checkMoveValidity(GameState gameState, Piece piece, int direction, int reach) {
		int[] pos = new int[] {piece.getPosition()[0],piece.getPosition()[1]};
		switch(direction) {
			case 0: pos[1] -= reach; break;						//left
			case 1: pos[1] += reach; break;						//right
			case 2: pos[0] -= reach; break;						//up
			case 3: pos[0] += reach; break;						//down
			case 4: pos[1] -= reach; pos[0] -= reach; break;	//left Up
			case 5: pos[1] += reach; pos[0] -= reach; break;	//right Up
			case 6: pos[1] -= reach; pos[0] += reach; break;	//left Down
			case 7: pos[1] += reach; pos[0] += reach; break;	//right Down
		}
		
		if(!validPos(pos, piece, gameState)) {
			return null;
		} else {
			Move move = new Move();
			move.setPieceId(piece.getId());
			move.setNewPosition(pos);
			return move;
		}
	}
	
	/**
	 * Checks if a piece can occupy a given position.
	 * @param pos
	 * @param piece
	 * @param gameState
	 * @return true if the position can be occupied.
	 */
	static boolean validPos(int[] pos, Piece piece, GameState gameState) {
		//out of bounds check
		if(pos[0] < 0 || 
				pos[1] < 0 ||
				pos[0] >= gameState.getGrid()[0].length || 
				pos[1] >= gameState.getGrid()[1].length) {
			return false;
		}

		String occupant = gameState.getGrid()[pos[0]][pos[1]];
		//free position check
		if(occupant.equals("")) {
			return true;
		}
		
		//occupied by block check
		if(occupant.equals("b")) {
			return false;
		}
			
		//same team occupant check
		int occupantTeam = Integer.parseInt(occupant.split(":")[1].split("_")[0]);
		if(occupantTeam == gameState.getCurrentTeam()) {
			return false;
		}
		
		//weaker opponent check
		for(Piece p : gameState.getTeams()[occupantTeam].getPieces()) {
			if(p.getId().equals(occupant.split(":")[1].split("_")[1])) {
				if(p.getDescription().getAttackPower() <= piece.getDescription().getAttackPower()) {
					return true;
				}
			}
		}
		
		//if opponent is stronger or something unforeseen happens
		return false;
	}
	
	
	/**
	 * Returns a pieces maximum reach into a certain direction,
	 * @param directions
	 * @param dir
	 * @return int reach
	 */
	static int getReach(Directions directions, int dir) {
		switch(dir) {
			case 0: return directions.getLeft();
			case 1: return directions.getRight();
			case 2: return directions.getUp();
			case 3: return directions.getDown();
			case 4: return directions.getUpLeft();
			case 5: return directions.getUpRight();
			case 6: return directions.getDownLeft();
			case 7: return directions.getDownRight();
			default: return -1;
		}
	}
	
	/**
	 * Gets thrown if the current team cannot move
	 */
	public static class NoMovesLeftException extends Exception {
		NoMovesLeftException(String team){
			super("Team " + team + " can not move.");
		}
	}
}
