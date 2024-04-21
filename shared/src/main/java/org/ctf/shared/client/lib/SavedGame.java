package org.ctf.shared.client.lib;

import java.util.HashMap;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Move;

public class SavedGame implements java.io.Serializable {
  private GameState initialState;
  private HashMap<String, Move> lastmoves = new HashMap<>();
  private static int counter = 1;

  public SavedGame() {}

  public void setInitialGameState(GameState initialState){
        this.initialState = initialState;
  }

  public void addMove(Move move) {
    if(lastmoves.isEmpty()){
        lastmoves.put(Integer.toString(counter), move);
        counter++;
    } else if (move.hashCode() != lastmoves.get(Integer.toString(counter-1)).hashCode()){
        this.lastmoves.put(Integer.toString(counter), move);
        counter++;
    }  
  }

  public GameState getInitialState(){
    return this.initialState;
  }

  public HashMap<String, Move> getMoves(){
    return this.lastmoves;
  }
}
