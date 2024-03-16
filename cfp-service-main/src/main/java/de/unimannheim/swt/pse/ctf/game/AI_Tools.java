package de.unimannheim.swt.pse.ctf.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import de.unimannheim.swt.pse.ctf.game.map.Directions;
import de.unimannheim.swt.pse.ctf.game.map.ShapeType;
import de.unimannheim.swt.pse.ctf.game.state.GameState;
import de.unimannheim.swt.pse.ctf.game.state.Move;
import de.unimannheim.swt.pse.ctf.game.state.Piece;


/**
 * @author sistumpf
 * This class is a copy of AI_Tools from shared class but it uses and returns classes from the server.
 * As the map or state package exists in the shared and ctf module, here the ctf versions are used.
 */
public class AI_Tools {
		/**
		 * Given a Piece and a GameState containing the Piece, an ArrayList with all valid locations the Piece can walk on is returned.
		 * The ArrayList contains int[2] values, representing a (y,x) location on the grid.
		 * @param GameState gameState
		 * @param String pieceID
		 * @return ArrayList<int[]> that contains all valid positions a piece could move to
		 */
	  	public static ArrayList<int[]> getPossibleMoves(GameState gameState, String pieceID) {
			Piece piece = Arrays.stream(gameState.getTeams()[Integer.parseInt(pieceID.split(":")[1].split("_")[0])].getPieces()).filter(p -> p.getId().equals(pieceID)).findFirst().get();
			ArrayList<int[]> possibleMoves = new ArrayList<int[]>();
			
			if(piece.getDescription().getMovement().getDirections() == null) {
				try {
					AI_Tools.createShapeMoveList(gameState, piece).stream().forEach(m -> possibleMoves.add(m.getNewPosition()));
				} catch (InvalidShapeException e) {e.printStackTrace();}
				
			} else {
				HashMap<Integer, Integer> dirMap = AI_Tools.createDirectionMap(gameState, piece);
				for(Integer direction : dirMap.keySet()) {
					for(int reach = dirMap.get(direction); reach>0; reach--) {
						Move move = AI_Tools.checkMoveValidity(gameState, piece, direction, reach);
						if(move != null)
							possibleMoves.add(move.getNewPosition());
					}
				}
			}
			return possibleMoves;
		}
	
	  /**
	   * Selects and returns a random Move from an ArrayList which only contains valid Moves.
	   * @param moveArrayList
	   * @return
	   */
	  static Move getRandomShapeMove(ArrayList<Move> moveArrayList) {
	    return moveArrayList.get((int)(moveArrayList.size() * Math.random()));
	  }

	  /**
	   * Checks if a Shape (currently only l-shape) move is valid.
	   * The Shape-Direction is given as a number (0-7).
	   * @param gameState
	   * @param piece
	   * @param direction
	   * @return false if the move is invalid.
	   * @throws InvalidShapeException if the Shape is not yet implemented here
	   */
	  static Move validShapeDirection(GameState gameState, Piece piece, int direction) throws InvalidShapeException {
	    int[] pos = new int[] {piece.getPosition()[0],piece.getPosition()[1]};

	    if(piece.getDescription().getMovement().getShape().getType() == ShapeType.lshape) {
	      switch(direction) {
	        case 0: pos[0] -= 2; pos[1] -= 1; break;	//2up1left
	        case 1: pos[0] -= 2; pos[1] += 1; break;	//2up1right
	        case 2: pos[1] += 2; pos[0] -= 1; break;	//2right1up
	        case 3: pos[1] += 2; pos[0] += 1; break;	//2right1down
	        case 4: pos[0] += 2; pos[1] -= 1; break;	//2down1left
	        case 5: pos[0] += 2; pos[1] += 1; break;	//2down1right
	        case 6: pos[1] -= 2; pos[0] -= 1; break;	//2left1up
	        case 7: pos[1] -= 2; pos[0] += 1; break;	//2left1down
	      }
	    } else {
	      throw new InvalidShapeException(piece.getDescription().getMovement().getShape().getType().toString());
	    }

	    if(validPos(pos, piece, gameState)) {
	      Move move = new Move();
	      move.setPieceId(piece.getId());
	      move.setNewPosition(pos);
	      return move;
	    } else {
	      return null;
	    }
	  }
	  
	  /**
	   * Creates an ArrayList with all valid Moves a piece with shape movement can do.
	   * @param gameState
	   * @param picked
	   * @return ArrayList<Move>
	   * @throws InvalidShapeException
	   */
	  public static ArrayList<Move> createShapeMoveList(GameState gameState, Piece picked) throws InvalidShapeException {
		  ArrayList<Move> shapeMoves = new ArrayList<Move>();
	      for(int i=0; i<8; i++) {
	        Move shapeMove = validShapeDirection(gameState, picked, i);
	        if(shapeMove != null) {
	          shapeMoves.add(shapeMove);
	        }
	      }
	      return shapeMoves;
	  }
	  
	  /**
	   * Creates a key-value map where a direction is the key and a value is the pieces maximum reach into that direction.
	   * This map only applies for the Piece picked.
	   * The reach value is directly from MapTemplate, this method only checks if the positions adjacent to a piece are occupied.
	   * @param gameState
	   * @param picked
	   * @return HashMap<Integer,Integer>
	   */
	  public static HashMap<Integer,Integer> createDirectionMap(GameState gameState, Piece picked) {
		  HashMap<Integer,Integer> dirMap = new HashMap<Integer,Integer>();
	      for(int i=0; i<8; i++) {
	        int reach = getReach(picked.getDescription().getMovement().getDirections(), i);
	        if(reach > 0) {
	          if(validDirection(gameState, picked, i)) {
	            dirMap.put(i, reach);
	          } else {
	            continue;
	          }
	        }
	      }
		  return dirMap;
	  }

	  /**
	   * Returns a Move from a given HashMap of possible directions and and their reach to move in.
	   * This method picks a random dirction-reach pair and returns a Move to this position using {@link #checkMoveValidity(GameState gameState, Piece piece, int direction, int reach)}.
	   * If the position is invalid this process is tried again till a valid move is generated.
	   * If a random position is invalid the HashMap reach value is lowered to ensure the same position is not picked again.
	   * This method assumes the HashMap contains elements and all directions contain at least 1 valid position.
	   * @param dirMap
	   * @param piece
	   * @param gameState
	   * @return a valid move
	   */
	  static Move getDirectionMove(HashMap<Integer,Integer> dirMap, Piece piece, GameState gameState) {
	    int randomKey = (int)dirMap.keySet().toArray()[(int)(dirMap.size() * Math.random())];
	    int reach;

	    while(true) {
	      reach = (int)(Math.random() * dirMap.get(randomKey) +1);
	      Move newPos = checkMoveValidity(gameState, piece, randomKey, reach);
	      if(newPos != null)
	        return newPos;
	      dirMap.replace(randomKey, reach - 1);
	      continue;
	    }
	  }

	  /**
	   * This method tests if a piece could walk into a given direction.
	   * It does not test if a pieces reach in a direction is >0.
	   * The direction is given as an int (0-7).
	   * @param gameState
	   * @param piece
	   * @param direction
	   * @return false if there are no possible moves in this direction, true otherwise.
	   */
	  static boolean validDirection(GameState gameState, Piece piece, int direction) {
	    return checkMoveValidity(gameState, piece, direction, 1) != null;
	  }

	  /**
	   * Returns the Move if a piece can occupy specific position.	 
	   * This method does not test if a pieces reach in a direction is >0.
	   * The direction is given as an int (0-7) and reach as an int that specifies how many fields into that direction.
	   * @param gameState
	   * @param piece
	   * @param direction
	   * @param reach
	   * @return a Move instance with the piece and its new position
	   * @return null if the piece can't occupy the position or the position is not in the grid
	   */
	  static Move checkMoveValidity(GameState gameState, Piece piece, int direction, int reach) {
	    int[] pos = new int[] {piece.getPosition()[0],piece.getPosition()[1]};
	    updatePos(pos, direction, reach);

	    if(!validPos(pos, piece, gameState)) {
	      return null;
	    } else if(!sightLine(gameState, new int[] {pos[0], pos[1]}, direction, reach)) {
	      return null;
	    } else {
	      Move move = new Move();
	      move.setPieceId(piece.getId());
	      move.setNewPosition(pos);
	      return move;
	    }
	  }

	  /**
	   * Checks if two positions have a direct line of sight.
	   * The line of sight can be disrupted by pieces, blocks or bases in between them.
	   * The old position is calculated from the new Position minus the reach in the negative direction the piece took to get to newPos.
	   * In simpler words, if a piece went from 2,2 to 2,0 (2 to left) newPos would be [2,0], reach would be 2 and the direction 0 (left)
	   * @param gameState
	   * @param newPos
	   * @param direction
	   * @param reach
	   * @return true if there is no obstacle in between
	   * @return false if any obstacle is in between or the target position is not on the grid
	   */
	  //TODO bis jetzt blockieren Bases noch die Sicht, ich weiß nicht ob das so korrekt ist
	  static boolean sightLine(GameState gameState, int[] newPos, int direction, int reach) {
	    String[][] grid = gameState.getGrid();
	    --reach;
	    for(; reach > 0; reach--) {
	      newPos = updatePos(newPos, direction, -1);
	      try {
	        if(grid[newPos[0]][newPos[1]].equals("")) {
	          continue;
	        } else {
	          return false;
	        }
	      } catch (IndexOutOfBoundsException ioe) {
	        return false;
	      }
	    }
	    return true;
	  }

	  /**
	   * Updates the y,x position of a piece.
	   * A given int[2] positional Array is altered by going a given amount of steps (reach) into a given direction.
	   * @param pos
	   * @param direction
	   * @param reach
	   * @return updated position
	   */
	  static int[] updatePos(int[] pos, int direction, int reach) {
	    switch(direction) {
	      case 0: pos[1] -= reach; break;                   //left
	      case 1: pos[1] += reach; break;                   //right
	      case 2: pos[0] -= reach; break;                   //up
	      case 3: pos[0] += reach; break;                   //down
	      case 4: pos[1] -= reach; pos[0] -= reach; break;  //left Up
	      case 5: pos[1] += reach; pos[0] -= reach; break;  //right Up
	      case 6: pos[1] -= reach; pos[0] += reach; break;  //left Down
	      case 7: pos[1] += reach; pos[0] += reach; break;  //right Down
	    }
	    return pos;
	  }

	  /**
	   * Checks if a piece can occupy a given position.
	   * @param pos
	   * @param piece
	   * @param gameState
	   * @return true if the position can be occupied.
	   */
	  //TODO: Bases werden noch nicht berücksichtigt, ich weiß noch nicht wie man damit umgehen muss.
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
	      if(p.getId().equals(occupant)) {
	        if(p.getDescription().getAttackPower() <= piece.getDescription().getAttackPower()) {
	          return true;
	        }
	      }
	    }

	    //if opponent is stronger or something unforeseen happens
	    return false;
	  }


	  /**
	   * Returns a pieces maximum reach into a certain direction.
	   * Assumes the direction is valid, doesn't catch Null Pointer Exceptions.
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
	   * Gets thrown if the current team cannot move.
	   */
	  public static class NoMovesLeftException extends Exception {
		private static final long serialVersionUID = -5045376294141974451L;

		NoMovesLeftException(String team){
	      super("Team " + team + " can not move.");
	    }
	  }

	  /**
	   * Gets thrown if a Shape is not yet implemented in RandomAI.
	   */
	  public static class InvalidShapeException extends Exception {
		private static final long serialVersionUID = -574558731715073847L;

		InvalidShapeException(String shape){
	      super("Unknown shape: " + shape);
	    }
	  }
}
