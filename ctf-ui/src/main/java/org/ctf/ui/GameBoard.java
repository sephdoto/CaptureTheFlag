package org.ctf.ui;

import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class GameBoard {
	private static AnchorPane anchor;

	   private GridPane gPane;

	   private Label winLabel;

	   // this stackPane will contain the grid and the winLabel
	   private StackPane stack;

	   // number of columns and rows, from which we calculate the binding cellSize
	   private IntegerProperty nbC;

	   private IntegerProperty nbR;
	   private Scene scene;
	   private static final int WINDOW_SIZE = 720;
	   private NumberBinding cellSize;
}
