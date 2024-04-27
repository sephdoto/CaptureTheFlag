package org.ctf.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.ctf.shared.state.GameState;
import org.ctf.shared.state.Piece;
import org.ctf.shared.state.Team;
import org.ctf.ui.customobjects.BackgroundCellV2;
import org.ctf.ui.customobjects.BaseRep;
import org.ctf.ui.customobjects.BlockRepV3;
import org.ctf.ui.customobjects.CostumFigurePain;

import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;


/**
 * @author mkrakows
 * This class represents the GameBoard on which the figures are placed
 * it is realized y using a GridPane and resizable for any kind of map
 */
public class GamePane extends HBox{
	
	String[][] map;
	final GameState state;
	Team[] teams;
	int rows;
	int cols;
	final VBox vBox;
	int anzTeams;
	HashMap<String, CostumFigurePain> figures = new HashMap<String, CostumFigurePain>();
	HashMap<Integer, BaseRep> bases = new HashMap<Integer, BaseRep>();
	HashMap<Integer, BackgroundCellV2> cells = new HashMap<Integer, BackgroundCellV2>();
	 GridPane gridPane;
	
	public GamePane(GameState state) {
		this.state = state;
		this.map = state.getGrid();
		rows = map.length;
		cols = map[0].length;
		vBox = new VBox();
		vBox.alignmentProperty().set(Pos.CENTER);
		alignmentProperty().set(Pos.CENTER);
		paddingProperty().set(new Insets(20));
		//setFillHeight(true);
//		 double paddingValue = 0.05;
//	      paddingProperty().bind(Bindings.createObjectBinding(
//	            () -> new Insets(getWidth() * paddingValue, 
//	                             getHeight() * paddingValue, 
//	                             getWidth() * paddingValue, 
//	                             getHeight() * paddingValue),
//	            widthProperty(), heightProperty()));
		 gridPane = new GridPane();
		// gridPane.setFillWidth(true);


		 NumberBinding binding = Bindings.min(widthProperty().divide(cols), heightProperty().divide(rows));
		//gridPane.setMinSize(300, 300);
		vBox.prefWidthProperty().bind(binding.multiply(cols));
		vBox.prefHeightProperty().bind(binding.multiply(rows));
		vBox.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
		vBox.setFillWidth(true);
		VBox.setVgrow(gridPane, Priority.ALWAYS);

		for (int i = 0; i < cols; i++) {
			final ColumnConstraints columnConstraints = new ColumnConstraints(Control.USE_PREF_SIZE,
					Control.USE_COMPUTED_SIZE, Double.MAX_VALUE);
			columnConstraints.setHgrow(Priority.SOMETIMES);
			gridPane.getColumnConstraints().add(columnConstraints);
		}
		for (int j = 0; j < rows; j++) {
			final RowConstraints rowConstraints = new RowConstraints(Control.USE_PREF_SIZE, Control.USE_COMPUTED_SIZE,
					Double.MAX_VALUE);
			rowConstraints.setVgrow(Priority.SOMETIMES);
			gridPane.getRowConstraints().add(rowConstraints);
		}
		this.fillGrid();
		getChildren().add(vBox);
		HBox.setHgrow(this, Priority.ALWAYS);

	}


	public void moveFigure(int x, int y, CostumFigurePain mover) {
		mover.getParentCell().removeFigure();
		//BackgroundCellV2 oldField = mover.getParentCell();
		BackgroundCellV2 newField = cells.get(generateKey(x, y));
		//showTransition(mover, oldField, newField);
		if(newField.isOccupied()) {
			CostumFigurePain figureToDelete = newField.getChild();
			System.out.println("XXX" + figureToDelete.getPiece().getId());
			boolean delted = figures.remove(figureToDelete.getPiece().getId(), figureToDelete); 
			System.out.println(delted);
			newField.removeFigure();
		}
		newField.addFigure(mover);
	}
	
	public void enableBaseColors(WaitingScene waitingscene) {
		for(BaseRep b: bases.values()) {
			b.setScene(waitingscene);
		}
	}

	
	public int generateKey(int x, int y) {
		return (x+y)*(x+y+1) / 2 + x;
	}
	
	public HashMap<String, CostumFigurePain> getFigures() {
		return figures;
	}


	public void setFigures(HashMap<String, CostumFigurePain> figures) {
		this.figures = figures;
	}


	public HashMap<Integer, BackgroundCellV2> getCells() {
		return cells;
	}


	public void setCells(HashMap<Integer, BackgroundCellV2> cells) {
		this.cells = cells;
	}


	public void fillGrid() {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				BackgroundCellV2 child = new BackgroundCellV2(i, j);
				cells.put(generateKey(i, j),child);
				String objectOnMap = map[i][j];
				if(objectOnMap.equals("b")) {
					child.addBlock();
				} else if (objectOnMap.startsWith("b:")) {
					
				}else if (objectOnMap.startsWith("b:2")) {
					//Add base of team 2 here
				}
				GridPane.setRowIndex(child, i);
				GridPane.setColumnIndex(child, j);
				gridPane.getChildren().add(child);
			}
		}
		teams = state.getTeams();
		for(int i=0;i<teams.length;i++) {
			Team currenTeam = teams[i];
			int baseX = currenTeam.getBase()[0];
			int baseY = currenTeam.getBase()[1];
			String teamColor = currenTeam.getColor();
			BaseRep b = new BaseRep(currenTeam.getFlags(),teamColor, currenTeam.getId());
			bases.put(i, b);
			cells.get(generateKey(baseX, baseY)).addBasis(b);
			Piece[] pieces = currenTeam.getPieces();
			for(Piece piece: pieces) {
				CostumFigurePain pieceRep = new CostumFigurePain(piece);
				pieceRep.showTeamColor("red");//
				figures.put(piece.getId(), pieceRep);
				//allFigures.add(pieceRep);
				int x = piece.getPosition()[0];
				int y = piece.getPosition()[1];
				cells.get(generateKey(x, y)).addFigure(pieceRep);
			}
		}
		vBox.getChildren().add(gridPane);
	}
	
	
	
	


	
	
	

	
}
