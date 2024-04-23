package org.ctf.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

import org.ctf.shared.constants.Constants;
import org.ctf.shared.state.data.map.MapTemplate;
import org.ctf.shared.state.data.map.Movement;
import org.ctf.shared.state.data.map.PieceDescription;
import org.ctf.shared.tools.JSON_Tools;
import org.ctf.shared.tools.JSON_Tools.IncompleteMapTemplateException;

import javafx.scene.control.Spinner;

public class TemplateEngine {
	EditorScene editorscene;
	MapTemplate tmpTemplate;
	Movement movement;
	HashMap<String, PieceDescription> pieces = new HashMap<String, PieceDescription>();
	
	public TemplateEngine(EditorScene editorscene) {
		this.editorscene = editorscene;
		loadStartingTemplate();
		initializePieces();
	}
	
	public void loadStartingTemplate() {
		File defaultMap = new File(Constants.mapTemplateFolder+"10x10_2teams_example.json");
		try {
			if (defaultMap.exists()) {
				tmpTemplate = JSON_Tools.readMapTemplate(defaultMap);				
			} else {
				System.out.println("Fehler: Default Template konnte nicht geladen werden!"
						+ "Es wurde ein alternatives Template geladen.");
			}

		} catch (IncompleteMapTemplateException | IOException e) {
			System.out.println("fail");
		}

	}
	/**
	 * method that generates a text representation of a template
	 * 
	 * @author aniemesc
	 * 
	 * 
	 */
	public void printTemplate() {
		StringBuffer buf = new StringBuffer();
		for (PieceDescription p : tmpTemplate.getPieces()) {
			buf.append(p.getType() + " " + p.getCount() + "\n");
		}
		System.out.println("Rows:" + tmpTemplate.getGridSize()[0] + "  Collums:" + tmpTemplate.getGridSize()[1] + "\n"
				+ "Teams: " + tmpTemplate.getTeams() +" Falgs: "+ tmpTemplate.getFlags() + " Blocks: "+tmpTemplate.getBlocks()
				+ "\n" + "placement" + tmpTemplate.getPlacement().toString() + "\n" + "Turn Time "
				+ tmpTemplate.getMoveTimeLimitInSeconds()+" Game Time " + tmpTemplate.getTotalTimeLimitInSeconds()+"\n" 
				+ "Pieces: \n" +buf.toString());
	}
	
	
	public boolean handleSpinnerEvent(String event ,Spinner<Integer> spinner,int old,int newV) {
		switch (event) {
		case "Flags": 
			tmpTemplate.setFlags(newV);
			return true;
		case "TurnTime":
			tmpTemplate.setMoveTimeLimitInSeconds(newV);
			return false;
		case "GameTime":
			tmpTemplate.setTotalTimeLimitInSeconds(newV*60);
			return false;
		case "Blocks":
			return setandCheckTemplate(spinner, tmpTemplate::setBlocks, old, newV);
		case "Teams":
			return setandCheckTemplate(spinner, tmpTemplate::setTeams, old, newV);
		case "Rows":
			return setandCheckTemplate(spinner, this::setRows, old, newV);
		case "Cols":
			return setandCheckTemplate(spinner, this::setCols, old, newV);
			
		}
		return false;	
			
	}
	
	/**
	 * method that updates the custompieces hash map when a new template is loaded
	 * 
	 * @author aniemesc
	 * 
	 * 
	 */
	private void initializePieces() {
		ArrayList<String> usedTypes = new ArrayList<String>();
		for (PieceDescription piece : tmpTemplate.getPieces()) {
			pieces.put(piece.getType(), piece);
			usedTypes.add(piece.getType());
		}
		for (PieceDescription piece : pieces.values()) {
			if (!usedTypes.contains(piece.getType())) {
				piece.setCount(0);
			}
		}

	}
	
	public boolean setandCheckTemplate(Spinner<Integer> spinner,Consumer<Integer> setter,int old, int newV) {
		setter.accept(newV);
		boolean isvalid = TemplateChecker.checkTemplate(tmpTemplate);
		if (!TemplateChecker.checkTemplate(tmpTemplate)) {
			setter.accept(old);
			editorscene.setSpinnerChange(true);
			spinner.getValueFactory().setValue(old);
			System.out.println("Zu wenig Platz");
		}
		return isvalid;
	}
	
	public void setRows(int rows) {
		tmpTemplate.getGridSize()[0] = rows;
	}
	public void setCols(int cols) {
		tmpTemplate.getGridSize()[1] = cols;
	}
	public static void main(String[] args) {
		//TemplateEngine engine = new TemplateEngine(new Edi);
		//engine.printTemplate();
	}
}
