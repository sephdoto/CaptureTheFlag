package org.ctf.ui;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.geometry.Pos;
import javafx.scene.control.Control;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;


	public class GamePane2 extends HBox {
	    public GamePane2() {
	        final VBox vBox = new VBox();

	        vBox.alignmentProperty().set(Pos.CENTER);
	        alignmentProperty().set(Pos.CENTER);

	        final FlowGridPane gridPane = new FlowGridPane(8,8);
	        gridPane.setStyle("-fx-background-color: green");

	        final NumberBinding binding = Bindings.min(widthProperty(), heightProperty());

	        gridPane.setMinSize(200, 200);
	        vBox.prefWidthProperty().bind(binding);
	        vBox.prefHeightProperty().bind(binding);
	        vBox.setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);

	        vBox.setFillWidth(true);
	        VBox.setVgrow(gridPane, Priority.ALWAYS);

	        final int sideLength = 8;
	        for (int i = 0; i < sideLength; i++) {
	            final ColumnConstraints columnConstraints = new ColumnConstraints(Control.USE_PREF_SIZE, Control.USE_COMPUTED_SIZE, Double.MAX_VALUE);
	            columnConstraints.setHgrow(Priority.SOMETIMES);
	            gridPane.getColumnConstraints().add(columnConstraints);

	            final RowConstraints rowConstraints = new RowConstraints(Control.USE_PREF_SIZE, Control.USE_COMPUTED_SIZE, Double.MAX_VALUE);
	            rowConstraints.setVgrow(Priority.SOMETIMES);
	            gridPane.getRowConstraints().add(rowConstraints);
	        }

	        vBox.getChildren().add(gridPane);

	        getChildren().add(vBox);

	        HBox.setHgrow(this, Priority.ALWAYS);

	        for (int i = 0; i < sideLength; i++) {
	            for (int j = 0; j < sideLength; j++) {
	                final Pane child = new GameCell();

	                GridPane.setRowIndex(child, i);
	                GridPane.setColumnIndex(child, j);
	                gridPane.getChildren().add(child);
	            }
	        }
	    }
	}

