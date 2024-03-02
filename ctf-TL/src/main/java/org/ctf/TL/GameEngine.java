//TODO Add license if applicable
package org.ctf.TL;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;

import de.unimannheim.swt.pse.ctf.game.Game;
import de.unimannheim.swt.pse.ctf.game.map.MapTemplate;
import de.unimannheim.swt.pse.ctf.game.state.GameState;
import de.unimannheim.swt.pse.ctf.game.state.Move;
import de.unimannheim.swt.pse.ctf.game.state.Team;

/**
* Game Engine Implementation 
*@author rsyed
*/
public class GameEngine implements Game {

    Layer tLayer;


    /**
    * Constructor which gets a URL to connect to
    *@param url
    */
    public GameEngine(String url){
        this.tLayer = new Layer(url);
    }

    @Override
    public GameState create(MapTemplate template) {
  
        return null;
    }

    @Override
    public GameState getCurrentGameState() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getEndDate() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getRemainingGameTimeInSeconds() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getRemainingMoveTimeInSeconds() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getRemainingTeamSlots() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Date getStartedDate() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String[] getWinner() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void giveUp(String teamId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isGameOver() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isStarted() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isValidMove(Move move) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Team joinGame(String teamId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void makeMove(Move move) {
        // TODO Auto-generated method stub
        
    }

}
