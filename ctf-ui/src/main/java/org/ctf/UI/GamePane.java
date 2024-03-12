package org.ctf.UI;

import javax.management.BadAttributeValueExpException;

import org.ctf.UI.customObjects.BackgroundCell;
import org.ctf.UI.customObjects.BlockRepV2;
import org.ctf.UI.customObjects.BlueFlagRepV1;
import org.ctf.UI.customObjects.FigureRepV2;

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

public class GamePane extends HBox {
	String[][] map;
	int rows;
	int cols;

	public GamePane(String[][] map) {
		this.map = map;
		rows = map.length;
		cols = map[0].length;
		final VBox vBox = new VBox();

		vBox.alignmentProperty().set(Pos.CENTER);
		alignmentProperty().set(Pos.BOTTOM_CENTER);
		paddingProperty().set(new Insets(10));
		final GridPane gridPane = new GridPane();

		gridPane.setGridLinesVisible(true);
		gridPane.setAlignment(Pos.CENTER);

		final NumberBinding binding = Bindings.min(widthProperty().divide(cols), heightProperty().divide(rows));
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
		this.addMouseListener(gridPane);
		this.fillGridPane(gridPane);
		vBox.getChildren().add(gridPane);

		getChildren().add(vBox);

		HBox.setHgrow(this, Priority.ALWAYS);

	}
	public void addMouseListener(GridPane gridPane) {
		gridPane.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent e) {
				EventTarget target = e.getTarget();
				if(target.toString().equals("Square")) {
					System.out.println("Square");
				} 
//				if (target.toString().equals("Queen")) {
//					System.out.println("Queen");
//				}
				
			}
		});
	}

	public void fillGridPane(GridPane gridPane) {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				String objectRep = map[i][j];
				BackgroundCell child = new BackgroundCell(i, j);
				;
				if (objectRep.startsWith("p:1")) {
					child.getChildren().add(new BlueFlagRepV1(10, child));

				} else if (objectRep.startsWith("p:2")) {
					// child = new FigureRepV2(10, Color.BLUE);
					child.getChildren().add(new FigureRepV2(10, Color.BLUE));
				} else if (objectRep.equals("b")) {
					// child = new BlockRepV2(10);
					child.getChildren().add(new BlockRepV2());
				} else if (objectRep.startsWith("b:1")) {

				} else {
					// child = new BackgroundCell(i, j);
				}
				GridPane.setRowIndex(child, i);
				GridPane.setColumnIndex(child, j);
				gridPane.getChildren().add(child);
			}
		}
	}
}
