package org.ctf.ui;

import org.ctf.shared.client.lib.ServerManager;
import org.ctf.shared.state.GameState;
import org.ctf.ui.customobjects.BackgroundCellV2;
import org.ctf.ui.customobjects.PopUpPane;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class TestScene  {
	HomeSceneController hsc;
	String selected;
	static GameState state;
	StackPane root2;
	StackPane left;
	StackPane right;
	TextField serverIPText;
	TextField portText;
	String serverIP;
	String port;
	HBox sep;
	PopUpPane pop;
	StackPane showMapBox;
	ServerManager serverManager;
	GamePane gm;
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
	public TestScene(HomeSceneController hsc, Stage primaryStage) {
		this.hsc = hsc;
		 primaryStage.setMinWidth(720.0);
	      primaryStage.setMinHeight(720.0);

	      // main panes
	      StackPane king = new StackPane();
	      king.setAlignment(Pos.CENTER_LEFT);
	      AnchorPane anchor2 = new AnchorPane();
	      anchor = new AnchorPane();
	      anchor.setStyle("-fx-background-color: red");
	      scene = new Scene(king, WINDOW_SIZE, WINDOW_SIZE, Color.LIGHTBLUE);

	      nbC = new SimpleIntegerProperty(10);
	      nbR = new SimpleIntegerProperty(12);

	      createPanes();
	      //addWinLabel();
	      //king.setPadding(new Insets(20));
	      anchor.maxWidthProperty().bind(king.widthProperty().multiply(0.7));
	      king.getChildren().add(anchor);

	     
	}

		   // will be the root of our scene
		 

		  

		   public Scene getScene() {
		return scene;
	}

	public void setScene(Scene scene) {
		this.scene = scene;
	}

		public void addWinLabel() {
		      SimpleDoubleProperty fontSize = new SimpleDoubleProperty();
		      fontSize.bind(cellSize.multiply(nbC.getValue()).divide(15));

		      winLabel = new Label("Congratulations, you won ! :)");
		      winLabel.prefWidthProperty().bind(stack.widthProperty());
		      winLabel.prefHeightProperty().bind(stack.heightProperty());

		      winLabel.setTextFill(Color.web("#3099AA"));
		      winLabel.setAlignment(Pos.CENTER);

		      winLabel.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString(), ";",
		            "-fx-background-color: rgba(200, 200, 200, 0.85);"));

		      anchor.getChildren().add(winLabel);
		      AnchorPane.setTopAnchor(winLabel, 75.0);
		      AnchorPane.setLeftAnchor(winLabel, 75.0);
		   }

		   public void createPanes() {
		      anchor.getChildren().remove(stack);
		      stack = new StackPane();
		      stack.setStyle("-fx-background-color: green");
		      gPane = new GridPane();
		      cellSize = Bindings.min(scene.widthProperty().subtract(50).divide(nbC.getValue()),
		            scene.heightProperty().subtract(50).divide(nbR.getValue()));

		      for(int c = 0; c < nbC.getValue(); c++) {
		         for(int r = 0; r < nbR.getValue(); r++) {
		            // Comment these lines, as you set the size thanks to panes you add.
		            // if(c == 0) {
		            // RowConstraints row = new RowConstraints();
		            // row.setPercentHeight(100 / nbR.getValue());
		            // gPane.getRowConstraints().add(row);
		            // }

		            BackgroundCellV2 pane = new BackgroundCellV2(1,1);
		            pane.addBlock();

		            // Weird to me to bind min and max width and height, prefer constant value and prefHeight / prefWidth binding with the cellSize
		            pane.minWidthProperty().bind(cellSize);
		            pane.minHeightProperty().bind(cellSize);
		            pane.maxWidthProperty().bind(cellSize);
		            pane.maxHeightProperty().bind(cellSize);
		            

		            //pane.setStyle("-fx-background-color: #FFFFFF");
		            gPane.add(pane, c, r);
		         }
		      }
		      

		      gPane.setGridLinesVisible(true);
		      stack.getChildren().addAll(gPane);
		      anchor.getChildren().add(stack);
		      AnchorPane.setTopAnchor(stack, 20.0);
		      AnchorPane.setLeftAnchor(stack, 20.0);
		   }

		   
}
	
	
	


	
	
