package org.ctf.ui;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class MapEditorScene extends Scene {
	public MapEditorScene() {
		super(new VBox(),1000,500);
		this.createLayout();
	}
	private void createLayout() {
		VBox root = (VBox) this.getRoot();
		root.setStyle("-fx-background-color: black;" + "-fx-padding: 25px;" + "-fx-spacing: 50px;"
				+ "-fx-alignment: top-center ;");
		Image meI = new Image(getClass().getResourceAsStream("EditorImage.png"));
		ImageView meIv = new ImageView(meI);
		meIv.setPreserveRatio(true);
		root.getChildren().add(meIv);
	}
}
