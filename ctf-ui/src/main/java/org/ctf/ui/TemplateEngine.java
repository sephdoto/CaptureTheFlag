package org.ctf.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Consumer;

import org.ctf.shared.constants.Constants;
import org.ctf.shared.state.GameState;
import org.ctf.shared.state.data.map.Directions;
import org.ctf.shared.state.data.map.MapTemplate;
import org.ctf.shared.state.data.map.Movement;
import org.ctf.shared.state.data.map.PieceDescription;
import org.ctf.shared.state.data.map.PlacementType;
import org.ctf.shared.state.data.map.Shape;
import org.ctf.shared.tools.JsonTools;
import org.ctf.shared.tools.JsonTools.IncompleteMapTemplateException;
import org.ctf.ui.controllers.MapPreview;
import org.ctf.ui.controllers.MapPreviewThread;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;

/**
 * Covers all tasks concerning the management of map template data required in
 * the map editor.
 * 
 * @author aniemesc
 */
public class TemplateEngine {
	ArrayList<String> names = new ArrayList<String>(Arrays.asList("Pawn", "Knight", "Queen", "Bishop", "Rook", "King"));
	EditorScene editorscene;
	MapTemplate tmpTemplate;
	Movement tmpMovement = new Movement();
	HashMap<String, PieceDescription> pieces = new HashMap<String, PieceDescription>();

	/**
	 * Initializes the TemplateEngine by loading a default template and connecting
	 * to an EditorScene.
	 * 
	 * @author aniemesc
	 * @param editorscene - EditorScene object
	 */
	public TemplateEngine(EditorScene editorscene) {
		this.editorscene = editorscene;
		initializeCustomBox(this.editorscene.getCustomFigureBox());
		loadTemplate("test");
		tmpMovement.setDirections(new Directions());
		initializePieces();
	}

	/**
	 * Loads a map template and updates the tmpTemplate attribute.
	 * 
	 * @author aniemesc
	 * @param name - name of the map template
	 */
	public void loadTemplate(String name) {
		File map = new File(Constants.mapTemplateFolder + name + ".json");
		try {
			if (map.exists()) {
				tmpTemplate = JsonTools.readMapTemplate(map);
			} else {
				System.out.println("Fehler: Default Template konnte nicht geladen werden!");
			}

		} catch (IncompleteMapTemplateException | IOException e) {
			System.out.println("fail");
		}

	}

	/**
	 * Generates a text representation the current template that is printed in the
	 * console used for testing.
	 * 
	 * @author aniemesc
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

	/**
	 * Updates the tmpTemplate attribute based on an event.
	 * 
	 * @author aniemesc
	 * @param event   - String representation of an event
	 * @param spinner - Spinner that caused the event
	 * @param old     - old int value of the spinner
	 * @param newV    - new int value of the spinner
	 * @return boolean that indicates whether the tmpTemplate was updated
	 */
	public boolean handleSpinnerEvent(String event, Spinner<Integer> spinner, int old, int newV) {
		switch (event) {
		case "Flags":
			tmpTemplate.setFlags(newV);
			editorscene.updateVisualRoot();
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
	 * Updates the piece amount in the pieces hash map when a new template is
	 * loaded.
	 * 
	 * @author aniemesc
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

	/**
	 * Updates the tmpTemplate attribute. If this results in an invalid template the
	 * change gets reversed.
	 * 
	 * @author aniemesc
	 * @param spinner - Spinner that caused the event
	 * @param setter  - Consumer that sets an attribute of the tmpTemplate
	 * @param old     - old int value
	 * @param newV    - new int value
	 * @return boolean that indicates whether the tmpTemplate was updated
	 */
	public boolean setandCheckTemplate(Spinner<Integer> spinner, Consumer<Integer> setter, int old, int newV) {
		setter.accept(newV);
		boolean isvalid = TemplateChecker.checkTemplate(tmpTemplate);
		if (!TemplateChecker.checkTemplate(tmpTemplate)) {
			setter.accept(old);
			editorscene.setSpinnerChange(true);
			spinner.getValueFactory().setValue(old);
			editorscene.inform("There is not enough space!");
			return isvalid;
		}
		editorscene.setValidTemplate(true);
		MapPreviewThread thread = new MapPreviewThread(editorscene);
		thread.start();

		return isvalid;
	}

	/**
	 * Sets the attribute for row number in the tmpTemplate.
	 * 
	 * @author aniemesc
	 * @param rows - number of rows
	 */
	public void setRows(int rows) {
		tmpTemplate.getGridSize()[0] = rows;
	}

	/**
	 * Sets the attribute for column number in the tmpTemplate.
	 * 
	 * @author aniemesc
	 * @param rows - number of columns
	 */
	public void setCols(int cols) {
		tmpTemplate.getGridSize()[1] = cols;
	}

	public static void main(String[] args) {
		// TemplateEngine engine = new TemplateEngine(new Edi);
		// engine.printTemplate();
	}

	/**
	 * Updates the Placement attribute of the tmpTemplate according to String value.
	 * 
	 * @author aniemesc
	 * @param value - String for placement value
	 */
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

	/**
	 * Saves the current template in the map template folder.
	 * 
	 * @author aniemesc
	 * @param name - String value for the name of the template
	 */
	public void saveTemplate(String name) {
		try {
			JsonTools.saveTemplateWithGameState(name, tmpTemplate, new MapPreview(tmpTemplate).getGameState());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/**
	 * Updates the amout of figures in the current template in response to the
	 * change of a Spinner. If the updated map template is invalid the changes get
	 * reversed. It Also checks if there is at least 1 piece remaining.
	 * 
	 * @author aniemesc
	 * @param spinner - Spinner that was changed
	 * @param type    - String value for the type of the piece whose amount was
	 *                changed
	 * @param old     - int value for old amount
	 * @param newV    - int value for new amount
	 * @return boolean that indicates whether new template was valid
	 */
	public boolean updatePiece(Spinner<Integer> spinner, String type, int old, int newV) {
		if (newV == 0 && tmpTemplate.getPieces().length == 1) {
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
		editorscene.setValidTemplate(true);
		MapPreviewThread thread = new MapPreviewThread(editorscene);
		thread.start();
		return true;
	}

	/**
	 * Updates the pieces attribute and removes pieces from the template if their
	 * amount is 0. Makes sure that pieces get stored properly even though they were
	 * removed from the template.
	 * 
	 * @author aniemesc
	 * @param type
	 * @param newValue
	 */
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

	/**
	 * Returns the amount of pieces of a certain type.
	 * 
	 * @author aniemesc
	 * @param type - String value stating the type of the piece
	 * @return int value stating the amount
	 */
	public int getPieceCount(String type) {
		return pieces.get(type).getCount();
	}

	/**
	 * Fills the comboBox for custom pieces with all existing pieces that are not
	 * part of the default set.
	 * 
	 * @author aniemesc
	 * @param customBox - ComboBox for selecting custom pieces
	 */
	public void fillCustomBox(ComboBox<String> customBox) {
		String[] names = { "Pawn", "Knight", "Queen", "Bishop", "Rook", "King" };
		ArrayList<String> defaultPieces = new ArrayList<String>(Arrays.asList(names));
		for (String type : pieces.keySet()) {
			if (!defaultPieces.contains(type)) {
				customBox.getItems().add(type);
			}
		}
	}

	public void initializeCustomBox(ComboBox<String> customBox) {
//    String[] names = {"Pawn", "Knight", "Queen", "Bishop", "Rook", "King"};
//    ArrayList<String> defaultPieces = new ArrayList<String>(Arrays.asList(names));
		for (String name : TemplateEngine.getTemplateNames()) {
			this.loadTemplate(name);
//      System.out.println(name);
//      initializePieces();
			for (PieceDescription p : tmpTemplate.getPieces()) {
				if (!names.contains(p.getType())) {
					pieces.put(p.getType(), p);
					names.add(p.getType());
					customBox.getItems().add(p.getType());

				}
			}

		}

	}

	/**
	 * Generates a Directions object which is a copy of tmpMovement attribute.
	 * 
	 * @author aniemesc
	 * @return Directions object
	 */
	public Movement genrateMovementCopy() {
		Movement movement = new Movement();
		if (tmpMovement.getShape() != null) {
			Shape shape = new Shape();
			shape.setType(tmpMovement.getShape().getType());
			movement.setShape(shape);
		}
		Directions result = new Directions();
		result.setLeft(this.tmpMovement.getDirections().getLeft());
		result.setUp(this.tmpMovement.getDirections().getUp());
		result.setRight(this.tmpMovement.getDirections().getRight());
		result.setDown(this.tmpMovement.getDirections().getDown());
		result.setUpLeft(this.tmpMovement.getDirections().getUpLeft());
		result.setUpRight(this.tmpMovement.getDirections().getUpRight());
		result.setDownLeft(this.tmpMovement.getDirections().getDownLeft());
		result.setDownRight(this.tmpMovement.getDirections().getDownRight());
		movement.setDirections(result);
		return movement;
	}

	/**
	 * Adds the current custom piece in the EditorScene to the pieces attribute.
	 * 
	 * @author aniemesc
	 * @param nameField       - TextField that contains the type
	 * @param strengthSpinner - Spinner that contains the attackPower
	 */
	public void addpiece(TextField nameField, Spinner<Integer> strengthSpinner) {
		if (pieces.keySet().contains(nameField.getText())) {
			editorscene.inform(nameField.getText() + " already exists!");
			return;
		}
		PieceDescription result = new PieceDescription();
		Movement movement = genrateMovementCopy();
		result.setMovement(movement);
		result.setType(nameField.getText());
		result.setAttackPower(strengthSpinner.getValueFactory().getValue());
		pieces.put(result.getType(), result);
		editorscene.getCustomFigureBox().getItems().add(result.getType());
		editorscene.inform(nameField.getText() + " was added succesfully!");
	}

	/**
	 * Sets a Spinner on the value of the current direction.
	 * 
	 * @author aniemesc
	 * @param value     - String stating the direction
	 * @param vaSpinner - Spinner thats need to be set
	 */
	public void handleDirection(String value, Spinner<Integer> vaSpinner) {
		switch (value) {
		case "Left":
			vaSpinner.getValueFactory().setValue(tmpMovement.getDirections().getLeft());
			break;
		case "Right":
			// System.out.println("Test");
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

	/**
	 * Updates the Directions attribute of the tmpTemplate in response to a Spinner
	 * change.
	 * 
	 * @author aniemesc
	 * @param directionsBox - ComboBox containing the current direction
	 * @param newv          - int value received from a spinner
	 */
	public void handleDirectionValue(ComboBox<String> directionsBox, int newv) {
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

	/**
	 * Returns all names of saved map templates.
	 * 
	 * @author aniemesc
	 * @return ArrayList containing the names of all saved map templates
	 */
	public static ArrayList<String> getTemplateNames() {
		File templateFolder = new File(Constants.mapTemplateFolder);
		if (templateFolder.isDirectory()) {
			String[] names = templateFolder.list();
			for (int i = 0; i < names.length; i++) {
				names[i] = names[i].substring(0, names[i].length() - 5);
			}
			ArrayList<String> result = new ArrayList<String>();
			result.addAll(Arrays.asList(names));
			return result;
		}
		return new ArrayList<String>();
	}

	/**
	 * Returns the Movement object of the current map template.
	 * 
	 * @author aniemesc
	 * @return Movement object
	 */
	public Movement getTmpMovement() {
		return this.tmpMovement;
	}

	public MapTemplate getTmpTemplate() {
		return this.tmpTemplate;
	}

	public void setTmpShape(org.ctf.shared.state.data.map.Shape shape) {
		this.tmpMovement.setShape(shape);
	}
}
