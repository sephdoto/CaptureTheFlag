package org.ctf.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;

import org.ctf.shared.constants.Constants;
import org.ctf.shared.state.data.map.Directions;
import org.ctf.shared.state.data.map.MapTemplate;
import org.ctf.shared.state.data.map.Movement;
import org.ctf.shared.state.data.map.PieceDescription;
import org.ctf.shared.state.data.map.PlacementType;
import org.ctf.shared.tools.JSON_Tools;
import org.ctf.shared.tools.JSON_Tools.IncompleteMapTemplateException;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

public class TemplateEngine {
	EditorScene editorscene;
	MapTemplate tmpTemplate;
	Movement tmpMovement = new Movement();
	HashMap<String, PieceDescription> pieces = new HashMap<String, PieceDescription>();

	public TemplateEngine(EditorScene editorscene) {
		this.editorscene = editorscene;
		loadTemplate("10x10_2teams_example");
		tmpMovement.setDirections(new Directions());
		initializePieces();
	}

	public void loadTemplate(String name) {
		File map = new File(Constants.mapTemplateFolder + name +".json");
		try {
			if (map.exists()) {
				tmpTemplate = JSON_Tools.readMapTemplate(map);
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
				+ "Teams: " + tmpTemplate.getTeams() + " Falgs: " + tmpTemplate.getFlags() + " Blocks: "
				+ tmpTemplate.getBlocks() + "\n" + "placement" + tmpTemplate.getPlacement().toString() + "\n"
				+ "Turn Time " + tmpTemplate.getMoveTimeLimitInSeconds() + " Game Time "
				+ tmpTemplate.getTotalTimeLimitInSeconds() + "\n" + "Pieces: \n" + buf.toString());
	}

	public boolean handleSpinnerEvent(String event, Spinner<Integer> spinner, int old, int newV) {
		switch (event) {
		case "Flags":
			tmpTemplate.setFlags(newV);
			return true;
		case "TurnTime":
			tmpTemplate.setMoveTimeLimitInSeconds(newV);
			return false;
		case "GameTime":
			tmpTemplate.setTotalTimeLimitInSeconds(newV * 60);
			return false;
		case "Blocks":
			return setandCheckTemplate(spinner, tmpTemplate::setBlocks, old, newV);
		case "Teams":
			return setandCheckTemplate(spinner, tmpTemplate::setTeams, old, newV);
		case "Rows":
			return setandCheckTemplate(spinner, this::setRows, old, newV);
		case "Cols":
			return setandCheckTemplate(spinner, this::setCols, old, newV);
		case "Rook":
			return updatePiece(spinner, "Rook", old, newV);
		case "Pawn":
			return updatePiece(spinner, "Pawn", old, newV);
		case "Knight":
			return updatePiece(spinner, "Knight", old, newV);
		case "Bishop":
			return updatePiece(spinner, "Bishop", old, newV);
		case "Queen":
			return updatePiece(spinner, "Queen", old, newV);
		case "King":
			return updatePiece(spinner, "King", old, newV);
		case "custom":
			return updatePiece(spinner, editorscene.getCustomFigureBox().getValue(), old, newV);
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
	public void initializePieces() {
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

	public boolean setandCheckTemplate(Spinner<Integer> spinner, Consumer<Integer> setter, int old, int newV) {
		setter.accept(newV);
		boolean isvalid = TemplateChecker.checkTemplate(tmpTemplate);
		if (!TemplateChecker.checkTemplate(tmpTemplate)) {
			setter.accept(old);
			editorscene.setSpinnerChange(true);
			spinner.getValueFactory().setValue(old);
			editorscene.inform("There is not enough space!");
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
		// TemplateEngine engine = new TemplateEngine(new Edi);
		// engine.printTemplate();
	}

	public void setPlacement(String value) {
		switch (value) {
		case "Symmetric":
			tmpTemplate.setPlacement(PlacementType.symmetrical);
			break;
		case "Spaced Out":
			tmpTemplate.setPlacement(PlacementType.spaced_out);
			break;
		case "Defensive":
			tmpTemplate.setPlacement(PlacementType.defensive);
			break;

		default:
		}
	}

	public void saveTemplate(String name) {
		try {
			JSON_Tools.saveMapTemplateAsFile(name, tmpTemplate);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public boolean updatePiece(Spinner<Integer> spinner, String type, int old, int newV) {
		
		
			if (newV==0&&tmpTemplate.getPieces().length == 1) {
				editorscene.setSpinnerChange(true);
				spinner.getValueFactory().setValue(old);
				editorscene.inform("You need at least one Figure!");
				return false;
			
			}
			
			betterUpdateCount(type, newV);
			
			boolean isvalid = TemplateChecker.checkTemplate(tmpTemplate);
			System.out.println(isvalid);
			if (!isvalid) {
				betterUpdateCount(type, old);
				editorscene.setSpinnerChange(true);
				spinner.getValueFactory().setValue(old);
				 
			editorscene.inform(("There is not enough space for that many figures!"));
		}
		return true;
	}

	public void betterUpdateCount(String type, int newValue) {
		pieces.get(type).setCount(newValue);
		int number = (int) pieces.values().stream().filter(p -> p.getCount() > 0).count();
		PieceDescription[] updated = new PieceDescription[number];
		int i = 0;
		for (PieceDescription p : pieces.values()) {
			if (p.getCount() > 0) {
				updated[i] = p;
				i++;
			}
		}
		tmpTemplate.setPieces(updated);

	}
	
	public int getPieceCount(String type) {
		return pieces.get(type).getCount();
	}
	
	public void fillCustomBox(ComboBox<String> customBox) {
		String[] names = { "Pawn", "Knight", "Queen", "Bishop", "Rook","King" };
		ArrayList<String> defaultPieces = new ArrayList<String>(Arrays.asList(names));
		for (String type : pieces.keySet()) {
			if (!defaultPieces.contains(type)) {
				customBox.getItems().add(type);
			}
		}
	}
	public Directions genrateMovementCopy() {
		Directions result = new Directions();
		result.setLeft(this.tmpMovement.getDirections().getLeft());
		result.setUp(this.tmpMovement.getDirections().getUp());
		result.setRight(this.tmpMovement.getDirections().getRight());
		result.setDown(this.tmpMovement.getDirections().getDown());
		result.setUpLeft(this.tmpMovement.getDirections().getUpLeft());
		result.setUpRight(this.tmpMovement.getDirections().getUpRight());
		result.setDownLeft(this.tmpMovement.getDirections().getDownLeft());
		result.setDownRight(this.tmpMovement.getDirections().getDownRight());
		return result;
	}
	public void addpiece(TextField nameField,Spinner<Integer> strengthSpinner) {
		if(pieces.keySet().contains(nameField.getText())) {
			editorscene.inform(nameField.getText()+ " already exists!");
			return;
		}
		Directions directions = genrateMovementCopy();
		PieceDescription result = new PieceDescription();
		Movement movement = new Movement();
		movement.setDirections(directions);
		result.setMovement(movement);
		result.setType(nameField.getText());
		result.setAttackPower(strengthSpinner.getValueFactory().getValue());
		pieces.put(result.getType(), result);
		editorscene.getCustomFigureBox().getItems().add(result.getType());
		editorscene.inform(nameField.getText() +" was added succesfully!");
	}
	
	public void handleDirection(String value,Spinner<Integer> vaSpinner) {
		switch (value) {
		case "Left":
			vaSpinner.getValueFactory().setValue(tmpMovement.getDirections().getLeft());
			break;
		case "Right":
			System.out.println("Test");
			vaSpinner.getValueFactory().setValue(tmpMovement.getDirections().getRight());
			break;
		case "Up":
			vaSpinner.getValueFactory().setValue(tmpMovement.getDirections().getUp());
			break;
		case "Down":
			vaSpinner.getValueFactory().setValue(tmpMovement.getDirections().getDown());
			break;
		case "Up-Left":
			vaSpinner.getValueFactory().setValue(tmpMovement.getDirections().getUpLeft());
			break;
		case "Up-Right":
			vaSpinner.getValueFactory().setValue(tmpMovement.getDirections().getUpRight());
			break;
		case "Down-Left":
			vaSpinner.getValueFactory().setValue(tmpMovement.getDirections().getDownLeft());
			break;
		case "Down-Right":
			vaSpinner.getValueFactory().setValue(tmpMovement.getDirections().getDownRight());
			break;
		default:
			System.out.println("Unknown");
			break;
		}
	}
	public void handleDirectionValue(ComboBox<String> directionsBox,int newv) {
		switch (directionsBox.getValue()) {
		case "Left":
			tmpMovement.getDirections().setLeft(newv);
			break;
		case "Right":
			System.out.println("Test");
			tmpMovement.getDirections().setRight(newv);
			break;
		case "Up":
			tmpMovement.getDirections().setUp(newv);
			break;
		case "Down":
			tmpMovement.getDirections().setDown(newv);
			break;
		case "Up-Left":
			tmpMovement.getDirections().setUpLeft(newv);
			break;
		case "Up-Right":
			tmpMovement.getDirections().setUpRight(newv);
			break;
		case "Down-Left":
			tmpMovement.getDirections().setDownLeft(newv);
			break;
		case "Down-Right":
			tmpMovement.getDirections().setDownRight(newv);
			break;
		default:
			System.out.println("Unknown");
			break;
		}
	}
	
	public ArrayList<String> getTemplateNames(){
		File templateFolder = new File(Constants.mapTemplateFolder);
		if(templateFolder.isDirectory()) {
			String[] names = templateFolder.list();
			for(int i=0;i<names.length;i++) {
				names[i] =   names[i].substring(0, names[i].length()-5);
			}
			ArrayList<String> result = new ArrayList<String>();
			result.addAll(Arrays.asList(names));
			return result;
			}		
		return new ArrayList<String>();
	}
	public Movement getTmpMovement() {
		return this.tmpMovement;
	}
}
