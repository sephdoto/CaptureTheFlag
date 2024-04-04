package org.ctf.ui;

import org.ctf.shared.state.data.map.MapTemplate;
import org.ctf.shared.state.data.map.PieceDescription;

public class TemplateChecker {
	public static boolean checkTemplate(MapTemplate template) {
		int space = template.getGridSize()[0]*template.getGridSize()[1];
		int figurecount = 0;
		for(PieceDescription piece : template.getPieces()) {
			figurecount += piece.getCount();
		}
		if(figurecount*template.getTeams()+template.getBlocks()+template.getTeams()>space) {
			return false;
		}
		return true;
	}
	
	public static boolean checkEnoughPieces(MapTemplate template) {
		if(template.getPieces().length==0) {
			return false;
		}
		return true;
	}
}
