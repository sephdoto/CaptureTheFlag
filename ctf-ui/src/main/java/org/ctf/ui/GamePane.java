package org.ctf.ui;

import java.util.ArrayList;
import java.util.HashMap;

import javax.management.BadAttributeValueExpException;

import org.ctf.ui.customobjects.BackgroundCell;
import org.ctf.ui.customobjects.BackgroundCellV2;
import org.ctf.ui.customobjects.BlockRepV3;
import org.ctf.ui.customobjects.CostumFigurePain;
import org.ctf.ui.customobjects.DameRep;

import de.unimannheim.swt.pse.ctf.game.state.GameState;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * @author mkrakows
 * This class represents the GameBoard on which the figures are placed
 * it is realized y using a GridPane and resizable for any kind of map
 */
public class GamePane extends HBox {
	
	String[][] map;
	GameState state;
	int rows;
	int cols;
	final VBox vBox;
	int anzTeams;
	ArrayList<HashMap<String, CostumFigurePain>> teams;
	HashMap<String, CostumFigurePain> team1 = new HashMap<String, CostumFigurePain>();
	HashMap<String, CostumFigurePain> team2 = new HashMap<String, CostumFigurePain>();
	ArrayList<CostumFigurePain> allFigures = new ArrayList<CostumFigurePain>();
	//ArrayList<BackgroundCell> cells = new ArrayList<BackgroundCell>();
	HashMap<Integer, BackgroundCellV2> cells = new HashMap<Integer, BackgroundCellV2>();
	 GridPane gridPane;
	
	
	public GamePane(GameState state) {
		this.state = state;
		this.map = state.getGrid();
		teams = new ArrayList<HashMap<String,CostumFigurePain>>();
		anzTeams = state.getTeams().length;
		for(int i=0; i<anzTeams;i++) {
			teams.add(i, new HashMap<String, CostumFigurePain>());
		}
		rows = map.length;
		cols = map[0].length;
		vBox = new VBox();
		vBox.alignmentProperty().set(Pos.CENTER);
		alignmentProperty().set(Pos.BOTTOM_CENTER);
		paddingProperty().set(new Insets(10));
		 gridPane = new GridPane();

		//gridPane.setGridLinesVisible(true);
		//gridPane.setAlignment(Pos.CENTER);

		 NumberBinding binding = Bindings.min(widthProperty().divide(cols), heightProperty().divide(rows));
		// gridPane.setMinSize(300, 300);
		vBox.prefWidthProperty().bind(binding.multiply(cols));
		vBox.prefHeightProperty().bind(binding.multiply(rows));
		vBox.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
		vBox.setFillWidth(true);
		VBox.setVgrow(gridPane, Priority.ALWAYS);

		for (int i = 0; i < cols; i++) {
			final ColumnConstraints columnConstraints = new ColumnConstraints(Control.USE_PREF_SIZE,
					Control.USE_COMPUTED_SIZE, Double.MAX_VALUE);
			columnConstraints.setHgrow(Priority.SOMETIMES);
			// columnConstraints.setHalignment(HPos.CENTER);
			gridPane.getColumnConstraints().add(columnConstraints);
		}
		for (int j = 0; j < rows; j++) {
			final RowConstraints rowConstraints = new RowConstraints(Control.USE_PREF_SIZE, Control.USE_COMPUTED_SIZE,
					Double.MAX_VALUE);
			rowConstraints.setVgrow(Priority.SOMETIMES);
			rowConstraints.setValignment(VPos.CENTER);
			gridPane.getRowConstraints().add(rowConstraints);
		}
		//this.addMouseListener(gridPane);
		this.fillGridPane2();
		

		getChildren().add(vBox);

		HBox.setHgrow(this, Priority.ALWAYS);

	}

//	public void addMouseListener(GridPane gridPane) {
//		gridPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
//
//			@Override
//			public void handle(MouseEvent e) {
//				EventTarget target = e.getTarget();
//				if(target.toString().equals("Square")) {
//					System.out.println("Square");
//				} 
////				if (target.toString().equals("Queen")) {
////					System.out.println("Queen");
////				}
//				
//			}
//		});
//	}
	public void moveFigure(int x, int y, CostumFigurePain cuPain) {
		cells.get(generateKey(x, y)).addFigure(cuPain);
		
	}
	
	
	public int generateKey(int x, int y) {
		return x* 31 + y;
	}
	
	public void setGame(Game game) {
		for(CostumFigurePain cm : allFigures) {
			cm.game = game;
		}
		for(BackgroundCellV2 cl: cells.values()) {
			cl.game = game;
		}
	}
	
	public void setGameState(GameState state) {
		this.state = state;
		this.map = state.getGrid();
	}
	public void fillGridPane() {
		for(int i=0;i<anzTeams;i++ ) {
			if(!teams.get(i).isEmpty()) {
				teams.get(i).clear();
			}
		}
		if(!cells.isEmpty()) {
			cells.clear();
		}
		if(!vBox.getChildren().isEmpty()) {
			vBox.getChildren().clear();
		}
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				String objectRep = map[i][j];
				BackgroundCellV2 child = new BackgroundCellV2(i, j);
				cells.put(generateKey(i, j),child);
				
				if (objectRep.startsWith("p:1")) {
					DameRep d1 = new DameRep(objectRep);
					//team1.put(objectRep, d1);
					
					allFigures.add(d1);
					child.addFigure(d1);					
				} else if (objectRep.startsWith("p:2")) {
					
					DameRep d2 = new DameRep(objectRep);
					team2.put(objectRep, d2);
					allFigures.add(d2);
					child.addFigure(d2);
					
				} else if (objectRep.equals("b")) {
					child.addBlock(new BlockRepV3());
				} else if (objectRep.startsWith("b:1")) {

				} else {
					// child = new BackgroundCell(i, j);
				}
				GridPane.setRowIndex(child, i);
				GridPane.setColumnIndex(child, j);
				gridPane.getChildren().add(child);
				
			}
		}
		vBox.getChildren().add(gridPane);
	}
	
	public void fillGridPane2() {
		for(int i=0;i<anzTeams;i++ ) {
			if(!teams.get(i).isEmpty()) {
				teams.get(i).clear();
			}
		}
		if(!allFigures.isEmpty()) {
			allFigures.clear();
		}
		if(!cells.isEmpty()) {
			cells.clear();
		}
		if(!vBox.getChildren().isEmpty()) {
			vBox.getChildren().clear();
		}
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				String objectRep = map[i][j];
				BackgroundCellV2 child = new BackgroundCellV2(i, j);
				cells.put(generateKey(i, j),child);
				if (objectRep.startsWith("p")) {
					char teamNc = objectRep.charAt(2);
					int teamN = Character.getNumericValue(teamNc);
					System.out.println("Team: " + teamN);
					DameRep d2 = new DameRep(objectRep);
					teams.get(teamN-1).put(objectRep, d2);
					allFigures.add(d2);
					child.addFigure(d2);
					
				} else if (objectRep.equals("b")) {
					child.addBlock(new BlockRepV3());
				} else if (objectRep.startsWith("b:1")) {

				} else {
					// child = new BackgroundCell(i, j);
				}
				GridPane.setRowIndex(child, i);
				GridPane.setColumnIndex(child, j);
				gridPane.getChildren().add(child);
				
			}
		}
		vBox.getChildren().add(gridPane);
	}
	
	public void setTeamActive(int i, boolean active){
		HashMap<String, CostumFigurePain> currentTeam = teams.get(i-1);
		for(CostumFigurePain p: currentTeam.values()) {
			if(active) {
				p.setActive();
			} else {
				p.setUnactive();
			}
			System.out.println("ich bin aktiv");
		}
	}
//	public void setTeamActive2(int i) {
//		for(CostumFigurePain p: allFigures) {
//			if(p.team = i) {
//				p.setActive();
//			}else {
//				p.setUnactive();
//			}
//		}
//	}
	
	
	

	
}
