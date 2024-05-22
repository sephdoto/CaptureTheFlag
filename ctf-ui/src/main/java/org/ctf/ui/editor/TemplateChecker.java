package org.ctf.ui.editor;

import org.ctf.shared.state.data.map.MapTemplate;
import org.ctf.shared.state.data.map.PieceDescription;

/**
 * Provides methods on checking validity of templates.
 * 
 * @author aniemesc
 * 
 */
public class TemplateChecker {
  /**
   * method that that checks wheter a template provides enough space
   * 
   * @author aniemesc
   * @param MapTemplate template
   * @return boolean isValid
   * 
   */
  public static boolean checkTemplate(MapTemplate template) {
    int space = template.getGridSize()[0] * template.getGridSize()[1];
    int figurecount = 0;
    for (PieceDescription piece : template.getPieces()) {
      figurecount += piece.getCount();
    }
    if (figurecount * template.getTeams() + template.getBlocks() + template.getTeams() > space) {
      return false;
    }
    return true;
  }

  public static boolean checkEnoughPieces(MapTemplate template) {
    if (template.getPieces().length == 0) {
      return false;
    }
    return true;
  }
}
