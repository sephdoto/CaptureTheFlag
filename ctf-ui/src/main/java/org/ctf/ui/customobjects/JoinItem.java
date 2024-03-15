package org.ctf.ui.customobjects;


import javafx.animation.FadeTransition;
import javafx.animation.FillTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
public class JoinItem extends StackPane{
	String id;
	String host;
	String map = "custom";

	public JoinItem(String id,String name) {
		this.id = id;
		this.host = name;

		this.setPrefSize(50, 50);
		this.setMaxWidth(500);
		this.setStyle("-fx-border-color: #000000; -fx-border-width: 2px;-fx-background-color: rgba(53,89,119,0.6);");
		this.setAlignment(Pos.CENTER);
		this.setOnMouseEntered(e -> this.setStyle("-fx-border-color: white; -fx-border-width: 2px;-fx-background-color: rgba(53,89,119,0.6);"));
		this.setOnMouseExited(e -> this.setStyle("-fx-border-color: black; -fx-border-width: 2px;-fx-background-color: rgba(53,89,119,0.6);"));
		generateComponents();

	}

	private void generateComponents() {
		GridPane gp = new GridPane();
		gp.setHgap(10);
		gp.setVgap(10);
		gp.setAlignment(Pos.TOP_LEFT);
		Text name = new Text("Host: " + this.host);
		Text id = new Text("#"+ this.id);
		Text map = new Text("Map: " + this.map);
		Button join = new Button("Join");
		join.setOnAction(e -> {});

		name.setFill(Color.WHITE);
		id.setFill(Color.WHITE);
		map.setFill(Color.WHITE);

		name.setFont(Font.font(18));
		id.setFont(Font.font(18));
		map.setFont(Font.font(18));

		GridPane.setMargin(name, new Insets(0, 0, 0, 10));
		GridPane.setMargin(id, new Insets(0, 0, 0, 10));
		GridPane.setMargin(map, new Insets(0, 0, 0, 10));
		GridPane.setMargin(join, new Insets(0, 0, 10, 10));

		FadeTransition fadeTransition = new FadeTransition(Duration.millis(700), join);
		fadeTransition.setFromValue(1.0);
		fadeTransition.setToValue(0.5);
		fadeTransition.setCycleCount(FadeTransition.INDEFINITE);
		fadeTransition.setAutoReverse(true);

		join.setStyle("-fx-text-fill: white;" + "-fx-background-color: rgba(53,89,119,0.0);"
				+ "-fx-border-color: white; -fx-border-width: 1px;" + " -fx-background-radius: 20;"
				+ "-fx-border-radius: 20");

		join.setOnMouseEntered(e -> {
			join.setStyle("-fx-text-fill: white;" + "-fx-background-color: rgba(60,159,19);"
					+ "-fx-border-color: white; -fx-border-width: 1px;" + "-fx-border-radius: 20;"
					+ "-fx-background-radius: 20;");
			fadeTransition.play();
		});

		join.setOnMouseExited(e -> {
			fadeTransition.stop();
			join.setStyle("-fx-text-fill: rgba(255,255,255,1.0);" + "-fx-background-color: rgba(53,89,119,0.0);"
					+ "-fx-border-color: white; -fx-border-width: 1px;" + "-fx-border-radius: 20;"
					+ "-fx-background-radius: 20;");
			
		});

		gp.add(name, 0, 0);
		gp.add(id, 1, 0);
		gp.add(map, 0, 1);
		gp.add(join, 1, 1);

		this.getChildren().add(gp);
	}
}
