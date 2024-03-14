package org.ctf.ui;

import org.ctf.ui.customobjects.CostumFigurePain;

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
