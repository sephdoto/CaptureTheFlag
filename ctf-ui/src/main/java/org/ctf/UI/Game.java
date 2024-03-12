package org.ctf.UI;

import org.ctf.UI.customObjects.CostumFigurePain;

import javafx.scene.layout.GridPane;

public class Game {
	public CostumFigurePain current;
    public String playerColor;
    public GamePane cb;
    private boolean game;

    public Game(GamePane pane){
        cb = pane;
        current = null;
        playerColor = "red";
        this.game = true;
        //addEventHandlers(cb.chessBoard);
    }

	public  CostumFigurePain getCurrent() {
		return current;
	}

	public  void setCurrent(CostumFigurePain current) {
		this.current = current;
	}
    
    
}
