package org.ctf.ui;

import java.io.IOException;

import org.ctf.ui.customobjects.*;

import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * @author mkrakows
 * @author rsyed (Bug fixer) startpoint for the Gui Application Opens HomeScreen
 */
public class App extends Application {
	static Stage mainStage;
	static Scene startScene;
	HomeSceneController ssc = new HomeSceneController();
	FadeTransition startTransition;

	public void start(Stage stage) {
		mainStage = stage;
		Scene lockscreen = new Scene(createLockScreen(), 1000, 500);
		startScene = new Scene(createParent());
		lockscreen.setOnKeyPressed(e -> {
			mainStage.setScene(startScene);
			startTransition.stop();
		});
		lockscreen.setOnMousePressed(e -> {
			mainStage.setScene(startScene);
			startTransition.stop();
		});
		stage.setTitle("Capture The Flag Team 14");
		stage.setScene(lockscreen);
		stage.show();
	}

	/**
	 * @author mkrakows creates a new stage and scene with a stackpane as root
	 *         container. Sets a background image which is bound to the size of the
	 *         window and 3 custom buttons. {@link costumObjects.HomeScreenButton }
	 * @return Parent
	 */
	private Parent createParent() {
		StackPane root = new StackPane();
		root.setPrefSize(600, 600);
		Image bImage = new Image(getClass().getResourceAsStream("output.jpg"));
		ImageView vw = new ImageView(bImage);
		vw.fitWidthProperty().bind(root.widthProperty());
		vw.fitHeightProperty().bind(root.heightProperty());
		HomeScreenButton i1 = new HomeScreenButton("CREATE MAP", () -> {
			ssc.switchToMapEditorScene(mainStage);
		});
		HomeScreenButton i2 = new HomeScreenButton("CREATE GAME", () -> {
			CreateGameScreen.initCreateGameScreen(mainStage);
		});
		HomeScreenButton i3 = new HomeScreenButton("JOIN GAME", () -> {
			new HomeSceneController().switchToJoinScene(mainStage);
		});
		VBox vbox = new VBox(11, i1, i2, i3);
		vbox.setAlignment(Pos.CENTER);
		vbox.setMaxWidth(50);
		root.getChildren().addAll(vw, vbox);
		return root;
	}

	private Parent createLockScreen() {
		VBox root = new VBox();
		root.setStyle("-fx-background-color: black;");
		root.setAlignment(Pos.CENTER);
		Image ctf = new Image(getClass().getResourceAsStream("ctf2.png"));
		ImageView ctfv = new ImageView(ctf);
		ctfv.fitWidthProperty().bind(mainStage.widthProperty().multiply(0.8));
		ctfv.setPreserveRatio(true);
		FadeTransition ft = new FadeTransition(Duration.millis(5000), ctfv);
		ft.setFromValue(0.0);
		ft.setToValue(1.0);
		ft.play();
		root.getChildren().add(ctfv);

		Text text = new Text("Press any Key to Start");
		text.setStyle("-fx-font-family: Arial; -fx-font-size: 18px; -fx-fill: white ;");
		text.setOpacity(0);
		root.getChildren().add(text);

		VBox.setMargin(text, new Insets(150));
		startTransition = new FadeTransition(Duration.millis(1500), text);
		startTransition.setFromValue(0.1);
		startTransition.setToValue(1.0);
		startTransition.setDelay(Duration.millis(2000));
		startTransition.setAutoReverse(true); //
		startTransition.setCycleCount(Timeline.INDEFINITE);
		startTransition.play();
		text.fontProperty().bind(
				Bindings.createObjectBinding(() -> Font.font(mainStage.getWidth() / 50), mainStage.widthProperty()));
		return root;
	}

	public static void main(String[] args) {
		launch(args);
	}

	public static Stage getStage() {
		return mainStage;
	}

	public static Scene getScene() {
		return startScene;
	}
}