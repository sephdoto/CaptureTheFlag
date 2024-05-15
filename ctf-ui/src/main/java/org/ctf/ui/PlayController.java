package org.ctf.ui;

import java.util.HashMap;

import org.ctf.ui.customobjects.CostumFigurePain;

public class PlayController {
private static HashMap<String, CostumFigurePain> figures = new HashMap<String, CostumFigurePain>();


public static HashMap<String, CostumFigurePain> getFigures() {
	return figures;
}

public static void setFigures(HashMap<String, CostumFigurePain> figures) {
	PlayController.figures = figures;
}



}
