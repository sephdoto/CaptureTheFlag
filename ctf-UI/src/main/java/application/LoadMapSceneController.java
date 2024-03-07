package application;

import java.net.URL;
import customObjects.*;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
/**
 * @author mkrakows
 * Generates the code for the Scene in that one can choose a Map a show it on the screen
 */
public class LoadMapSceneController implements Initializable {
	String[][] exm = { { "", "p:1_2", "" }, { "", "p:2_3", "b" },{ "", "", "" } };
	int rows = exm.length;
	int columns = exm[0].length;

	Main main;
	@FXML
	private HBox topPane;

	@FXML
	private VBox vBox;

	/**
	 * @author mkrakows
	 * The Map is placed in a vBox on the Left side of the screen. For each row a HBox is generated in which is placed a Stackpane for each Column.
	 * in every Stackpane is placed a custom Rectangle
	 * {@link customObjects.BackgroundRepV1} 
	 * if a field in the array isn't emtpy but contains a object for a block a black custom Rectangle, for a figure of team a one a red
	 * custom rectangle a for a figure of team two a blue custom Rectangle is generated by Using the same custom Rectangle Class for now
	 * {@link customObjects.FigureRepV1 }
	 *
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		//vBox.prefWidthProperty().bind(Main.getStage().widthProperty().divide(2));
		this.controlVboxSize();
		
		for (int i = 0; i < rows; i++) {
			HBox h = this.createHBox(rows);
			for (int j = 0; j < columns; j++) {
				StackPane s = this.creStackPane(h, columns);
				BackgroundRepV1 b = new BackgroundRepV1(s);
				String objectRep = exm[i][j];

				if (objectRep.startsWith("p:1")) {
					FigureRepV1 rc = new FigureRepV1(b, Color.RED);
					b.addNode(rc);
				} else if (objectRep.startsWith("p:2")) {
					FigureRepV1 rc = new FigureRepV1(b, Color.BLUE);
					b.addNode(rc);
				} else if (objectRep.equals("b")) {
					FigureRepV1 rc = new FigureRepV1(b,Color.BLACK);
					b.addNode(rc);
				}
				s.getChildren().add(b);
				h.getChildren().add(s);
			}
			vBox.getChildren().add(h);
		}

	}
	/**
	 * @author mkrakows
	 * this Methods creates a hBox which represents a row. Its width is bound to the VBOX and its hight is selected such that all rows fi perfectly 
	 * into the vBox being equally high
	 * @param int 
	 * @return HBox
	 */
	public HBox createHBox(int rows) {
		HBox h = new HBox();
		h.setStyle("-fx-background-color:green ");
		h.prefWidthProperty().bind(vBox.widthProperty());
		h.prefHeightProperty().bind(vBox.heightProperty().divide(rows));
		return h;
	}
	/**
	 * @author mkrakows
	 * this method creates a Stackpane which represents a grid. Is height is bound to the Hbox and the width is selected such that all Stackpanes fit
	 * perefectly to the width of the HBox (row they are placed in) being equally width
	 * @param HBox
	 * @param int
	 * @return StackPane
	 */
	public StackPane creStackPane(HBox h1, int columns) {
		StackPane sc = new StackPane();
		sc.prefWidthProperty().bind(h1.widthProperty().divide(columns));
		sc.prefHeightProperty().bind(sc.heightProperty());
		//sc.prefHeightProperty().bind(h1.heightProperty());
		return sc;
	}

	public Button createButton(StackPane sc) {
		Button b = new Button();
		b.prefWidthProperty().bind(sc.widthProperty());
		b.prefHeightProperty().bind(sc.heightProperty());
		return b;
	}
	
	/**
	 * @author mkrakows
	 * The size of the vBox is set quadratic by setting the hight and the width to the minimum of both
	 */
	public void controlVboxSize() {
		Stage stage = Main.getStage();
		ChangeListener<Number> cl = new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				// TODO Auto-generated method stub
				double w = stage.getWidth();
				double h = stage.getHeight();
				double min = Math.min(w, h);
				vBox.setPrefHeight(min);
				vBox.setPrefWidth(min);
			}
		};
		stage.widthProperty().addListener(cl);
		stage.heightProperty().addListener(cl);
	}

}
