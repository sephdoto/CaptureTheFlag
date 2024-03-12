package org.ctf.UI;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class SceneCreator {
	public  Scene createJoinScene() {
		StackPane root = new StackPane();
		Image background;
		try {
			background = new Image(getClass().getResourceAsStream("dark.jpg"));
			ImageView backgroundView = new ImageView(background);
			backgroundView.fitWidthProperty().bind(root.widthProperty());
			backgroundView.fitHeightProperty().bind(root.heightProperty());
			root.getChildren().add(backgroundView);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		root.setAlignment(Pos.CENTER);

		VBox vbox = new VBox();
		vbox.setAlignment(Pos.TOP_CENTER);
		Image mp = new Image(getClass().getResourceAsStream("MP.png"));
		ImageView mpv = new ImageView(mp);
		mpv.fitWidthProperty().bind(root.widthProperty().multiply(0.8));
		mpv.setPreserveRatio(true);
		root.widthProperty().addListener(e -> {
			 if (root.getWidth() > 800) {
			        mpv.fitWidthProperty().unbind(); 
			        mpv.setFitWidth(640); 
			    } else if (root.getWidth() <= 800) {
			    	 mpv.fitWidthProperty().unbind(); 
			    	mpv.fitWidthProperty().bind(root.widthProperty().multiply(0.8)); 
			    }
		});
		
		vbox.getChildren().add(mpv);
		
		TextField searchField = new TextField();
		searchField.setMaxWidth(250);
		searchField.setPromptText("Search for Game ID");
		searchField.setStyle(" -fx-prompt-text-fill: white;" + "-fx-text-fill: white;"
				+ "-fx-background-color: rgba(53,89,119,0.4);" + "-fx-border-color: #000000; -fx-border-width: 2px;");
		
		searchField.setOnKeyPressed(e -> {			
		});
		
		Button exit = new Button("Leave");
		exit.setPrefSize(100, 25);
		exit.setStyle("-fx-text-fill: white;" + "-fx-background-color: rgba(53,89,119,0.4);"
				+ "-fx-border-color: #000000; -fx-border-width: 2px;");
		exit.setOnMouseEntered(e -> {
			exit.setStyle("-fx-text-fill: white;" + "-fx-background-color: rgba(53,89,119,0.4);"
					+ "-fx-border-color: white; -fx-border-width: 2px;");
		});
		exit.setOnMouseExited(e -> {
			exit.setStyle("-fx-text-fill: white;" + "-fx-background-color: rgba(53,89,119,0.4);"
					+ "-fx-border-color: black; -fx-border-width: 2px;");
		});

		exit.setFont(Font.font("System", FontWeight.BOLD, 14));
		Button refresh = new Button("Refresh");
		refresh.setStyle("-fx-text-fill: white;" + "-fx-background-color: rgba(53,89,119,0.4);"
				+ "-fx-border-color: #000000; -fx-border-width: 2px;");
		refresh.setOnMouseEntered(e -> {
			refresh.setStyle("-fx-text-fill: white;" + "-fx-background-color: rgba(53,89,119,0.4);"
					+ "-fx-border-color: white; -fx-border-width: 2px;");
		});
		exit.setOnMouseClicked(e -> {
			System.out.println(root.getWidth());
		});
		
		
		
		refresh.setOnMouseExited(e -> {
			refresh.setStyle("-fx-text-fill: white;" + "-fx-background-color: rgba(53,89,119,0.4);"
					+ "-fx-border-color: black; -fx-border-width: 2px;");
		});
		

		refresh.setFont(Font.font("System", FontWeight.BOLD, 14));
		refresh.setPrefSize(100, 25);
		HBox butBox = new HBox();
		butBox.getChildren().add(exit);
		butBox.setAlignment(Pos.CENTER);
		butBox.getChildren().add(refresh);
		HBox.setMargin(exit, new Insets(25));
		vbox.setMargin(searchField, new Insets(25));
		vbox.setMargin(butBox, new Insets(25));
		vbox.getChildren().add(searchField);		
		VBox glist = new VBox();
		glist.setAlignment(Pos.TOP_CENTER);
		
//		for(DummySession d : getDummyGames() ) {
//			JoinItem ji = new JoinItem(d);
//			glist.getChildren().add(ji);
//			VBox.setMargin(ji, new Insets(15));
//		}
		vbox.getChildren().add(glist);
		vbox.getChildren().add(butBox);
		root.getChildren().add(vbox);

		return new Scene(root);
	}
}
